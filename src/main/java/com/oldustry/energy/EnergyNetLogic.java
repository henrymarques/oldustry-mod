package com.oldustry.energy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * IC2 1.117 {@code EnergyNet.emitEnergyFrom}: min-loss paths, shuffle, split ∝ 1/loss
 * (path loss clamped to ≥0.1), deduct {@code floor(loss)}, overvolt on injected share.
 * Melt/shock/flow on conductors of paths that carried energy.
 */
public final class EnergyNetLogic {
    public static final double MIN_LOSS = 1.0E-4;
    /** JAR discover: {@code EnergyPath.loss = max(actual, 0.1)}. */
    public static final double PATH_LOSS_FLOOR = 0.1;

    public enum Role {
        SOURCE,
        SINK,
        BOTH,
        WIRE
    }

    public record Node(Role role, double loss, int meltAt, int demand, int maxSafeInput, int insulation) {
        public Node(Role role, double loss, int meltAt, int demand) {
            this(role, loss, meltAt, demand, Integer.MAX_VALUE, 9001);
        }

        public Node(Role role, double loss, int meltAt, int demand, int maxSafeInput) {
            this(role, loss, meltAt, demand, maxSafeInput, 9001);
        }
    }

    public record Edge(int from, int to) {}

    public record Result(
        int[] delivered,
        boolean[] melted,
        boolean[] exploded,
        double[] lossTo,
        int consumed,
        int[] conductorFlow,
        int[] shockExtra,
        /** JAR injectEnergy amount (share−loss) offered to each sink — full packet share, not min(room). */
        int[] offered
    ) {}

    public static Result emit(List<Node> nodes, List<Edge> edges, int source, int packet) {
        return emit(nodes, edges, source, packet, null);
    }

    public static Result emit(List<Node> nodes, List<Edge> edges, int source, int packet, Random rng) {
        int n = nodes.size();
        int[] delivered = new int[n];
        boolean[] melted = new boolean[n];
        boolean[] exploded = new boolean[n];
        double[] lossTo = new double[n];
        int[] parent = new int[n];
        int[] conductorFlow = new int[n];
        int[] shockExtra = new int[n];
        int[] offered = new int[n];
        Arrays.fill(lossTo, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        if (packet <= 0 || source < 0 || source >= n) {
            return new Result(delivered, melted, exploded, lossTo, 0, conductorFlow, shockExtra, offered);
        }

        List<List<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (Edge e : edges) {
            adj.get(e.from).add(e.to);
        }

        lossTo[source] = 0;
        ArrayDeque<Integer> q = new ArrayDeque<>();
        q.add(source);
        while (!q.isEmpty()) {
            int cur = q.remove();
            for (int nxt : adj.get(cur)) {
                Node hop = nodes.get(nxt);
                // JAR: do not prune conductors during discover. Melting is applied after
                // inject via energyInjected >= getConductorBreakdownEnergy (markPath).
                double extra = hop.role == Role.WIRE ? Math.max(hop.loss, MIN_LOSS) : 0;
                double nextLoss = lossTo[cur] + extra;
                if (nextLoss < lossTo[nxt]) {
                    lossTo[nxt] = nextLoss;
                    parent[nxt] = cur;
                    q.add(nxt);
                }
            }
        }

        record SinkPath(int node, double pathLoss) {}
        List<SinkPath> sinks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i == source || !(lossTo[i] < Double.POSITIVE_INFINITY)) {
                continue;
            }
            Node node = nodes.get(i);
            if (node.role != Role.SINK && node.role != Role.BOTH) {
                continue;
            }
            double pathLoss = Math.max(lossTo[i], PATH_LOSS_FLOOR);
            // JAR: only sinks with demandsEnergy()>0 enter emit → injectEnergy (overvolt there).
            if (node.demand <= 0 || pathLoss >= packet) {
                continue;
            }
            sinks.add(new SinkPath(i, pathLoss));
        }

        Random random = rng != null ? rng : new Random();
        Collections.shuffle(sinks, random);
        while (sinks.size() > packet) {
            sinks.remove(sinks.size() - 1);
        }

        int remaining = packet;
        int consumed = 0;
        int guard = 64;
        while (remaining > 0 && !sinks.isEmpty() && guard-- > 0) {
            double weight = 0;
            for (SinkPath s : sinks) {
                weight += 1.0 / s.pathLoss;
            }
            if (weight <= 0) {
                break;
            }
            List<SinkPath> again = new ArrayList<>();
            int moved = 0;
            for (SinkPath s : List.copyOf(sinks)) {
                if (remaining <= 0) {
                    break;
                }
                Node node = nodes.get(s.node);
                int room = node.demand - delivered[s.node];
                if (room <= 0 || exploded[s.node]) {
                    continue;
                }
                int share = (int) Math.floor(Math.round(remaining / weight / s.pathLoss * 100_000.0) / 100_000.0);
                int lossEu = (int) Math.floor(s.pathLoss);
                if (share <= lossEu) {
                    again.add(s);
                    continue;
                }
                int inject = share - lossEu;
                // JAR always calls injectEnergy(share−loss); overvolt sees the full offer, not min(room).
                offered[s.node] = Math.max(offered[s.node], inject);
                if (inject > node.maxSafeInput) {
                    exploded[s.node] = true;
                    // JAR path accounting / melt / shock use energyInjected, not source packet.
                    markPath(s.node, parent, source, nodes, conductorFlow, shockExtra, inject, melted);
                    continue;
                }
                int give = Math.min(room, inject);
                if (give <= 0) {
                    continue;
                }
                delivered[s.node] += give;
                int paid = lossEu + give;
                consumed += paid;
                remaining -= paid;
                moved += paid;
                // JAR: energyPath.totalEnergyConducted += energyInjected (give).
                markPath(s.node, parent, source, nodes, conductorFlow, shockExtra, give, melted);
                if (node.demand - delivered[s.node] > 0) {
                    again.add(s);
                }
            }
            if (moved == 0) {
                if (again.isEmpty()) {
                    break;
                }
                // JAR: drop one sink when a round accepts nothing.
                again.remove(again.size() - 1);
            }
            sinks = again;
        }

        return new Result(
            delivered, melted, exploded, lossTo, Math.min(Math.max(0, consumed), packet), conductorFlow, shockExtra, offered
        );
    }

    /** @param injected JAR energyInjected for this path delivery (not the source packet). */
    private static void markPath(
        int sink,
        int[] parent,
        int source,
        List<Node> nodes,
        int[] conductorFlow,
        int[] shockExtra,
        int injected,
        boolean[] melted
    ) {
        int cur = parent[sink];
        while (cur != source && cur >= 0) {
            if (nodes.get(cur).role == Role.WIRE) {
                conductorFlow[cur] += injected;
                int absorb = nodes.get(cur).insulation();
                if (injected > absorb) {
                    shockExtra[cur] = Math.max(shockExtra[cur], injected - absorb);
                }
                if (injected >= nodes.get(cur).meltAt()) {
                    melted[cur] = true;
                }
            }
            cur = parent[cur];
        }
    }

    private EnergyNetLogic() {}
}
