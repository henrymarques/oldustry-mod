package com.oldustry.energy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class EnergyNet {
    private static final Map<ServerLevel, EnergyNet> NETS = new WeakHashMap<>();

    private final Map<BlockPos, EnergyTile> tiles = new HashMap<>();
    private long lastTick = Long.MIN_VALUE;

    public static EnergyNet of(ServerLevel level) {
        return NETS.computeIfAbsent(level, l -> new EnergyNet());
    }

    public void add(EnergyTile tile) {
        tiles.put(tile.energyPos().immutable(), tile);
    }

    public void remove(BlockPos pos) {
        tiles.remove(pos);
    }

    public EnergyTile get(BlockPos pos) {
        return tiles.get(pos);
    }

    public void tick(ServerLevel level) {
        if (level.getGameTime() == lastTick) {
            return;
        }
        lastTick = level.getGameTime();
        Wind.tick(level.getGameTime(), level.getRandom(), level.dimension() == Level.OVERWORLD);
        List<EnergyTile> sources = new ArrayList<>();
        for (EnergyTile tile : tiles.values()) {
            if (tile.offerEnergy() > 0 && (tile.energyRole() == EnergyNetLogic.Role.SOURCE || tile.energyRole() == EnergyNetLogic.Role.BOTH)) {
                sources.add(tile);
            }
        }
        for (EnergyTile source : sources) {
            int bursts = Math.max(1, source.emitBursts());
            for (int b = 0; b < bursts; b++) {
                if (source.offerEnergy() <= 0) {
                    break;
                }
                emit(level, source);
            }
        }
    }

    private void emit(ServerLevel level, EnergyTile source) {
        int packet = source.offerEnergy();
        if (packet <= 0) {
            return;
        }
        List<EnergyTile> order = new ArrayList<>();
        Map<BlockPos, Integer> index = new HashMap<>();
        List<EnergyNetLogic.Node> nodes = new ArrayList<>();
        List<EnergyNetLogic.Edge> edges = new ArrayList<>();
        addNode(source, order, index, nodes);
        walk(source, packet, order, index, nodes, edges);
        EnergyNetLogic.Result result = EnergyNetLogic.emit(nodes, edges, 0, packet);
        for (int i = 0; i < order.size(); i++) {
            EnergyTile tile = order.get(i);
            // Explode before melt — same tick can flag both on a path; sink must go boom.
            if (result.exploded()[i]) {
                tile.explode(level);
                continue;
            }
            if (result.melted()[i]) {
                tile.melt(level);
                continue;
            }
            // JAR injectEnergy(share−loss): overvolt checks the full offer, not min(room, inject).
            int offer = result.offered()[i] > 0 ? result.offered()[i] : result.delivered()[i];
            if (offer > 0) {
                int leftover = tile.acceptEnergy(offer);
                int took = offer - leftover;
                if (took > 0) {
                    tile.addSunken(took);
                    tile.onFlow(took);
                }
            }
            int flow = result.conductorFlow()[i];
            if (flow > 0 && tile.conducts()) {
                tile.onFlow(flow);
            }
            int shock = result.shockExtra()[i];
            if (shock > 0 && tile.conducts()) {
                shock(level, tile.energyPos(), shock);
            }
        }
        int take = Math.min(result.consumed(), packet);
        if (take > 0) {
            source.takeEnergy(take);
        }
        // JAR getTotalEnergyEmitted(source): sum path.totalEnergyConducted (= energyInjected), not buffer drain.
        int injected = 0;
        for (int d : result.delivered()) {
            injected += d;
        }
        if (injected > 0) {
            source.addEmitted(injected);
        }
    }

    private void walk(
        EnergyTile start,
        int packet,
        List<EnergyTile> order,
        Map<BlockPos, Integer> index,
        List<EnergyNetLogic.Node> nodes,
        List<EnergyNetLogic.Edge> edges
    ) {
        List<EnergyTile> queue = new ArrayList<>();
        queue.add(start);
        for (int q = 0; q < queue.size(); q++) {
            EnergyTile cur = queue.get(q);
            for (Direction dir : Direction.values()) {
                if (!emitsToward(cur, dir)) {
                    continue;
                }
                EnergyTile nxt = tiles.get(cur.energyPos().relative(dir));
                if (nxt == null || !acceptsFrom(nxt, dir.getOpposite()) || !colorOk(cur, nxt) || !nxt.open()) {
                    continue;
                }
                int from = index.get(cur.energyPos());
                int to = addNode(nxt, order, index, nodes);
                edges.add(new EnergyNetLogic.Edge(from, to));
                // JAR EnergyNet: only cables conduct. Transformers/storage/machines are
                // sink+source endpoints — never path a HV packet "through" an MV/LV xfmr.
                if (to == order.size() - 1 && nxt != start && nxt.conducts()) {
                    queue.add(nxt);
                }
            }
        }
    }

    private static int addNode(EnergyTile tile, List<EnergyTile> order, Map<BlockPos, Integer> index, List<EnergyNetLogic.Node> nodes) {
        Integer existing = index.get(tile.energyPos());
        if (existing != null) {
            return existing;
        }
        int i = order.size();
        order.add(tile);
        index.put(tile.energyPos().immutable(), i);
        nodes.add(new EnergyNetLogic.Node(
            tile.energyRole(),
            tile.cableLoss(),
            tile.meltAt(),
            tile.demand(),
            tile.maxSafeInput(),
            tile.insulation()
        ));
        return i;
    }

    private static boolean emitsToward(EnergyTile tile, Direction dir) {
        return tile.conducts() || tile.emits(dir);
    }

    private static boolean acceptsFrom(EnergyTile tile, Direction dir) {
        return tile.conducts() || tile.accepts(dir);
    }

    private static boolean colorOk(EnergyTile a, EnergyTile b) {
        return a.color() == 0 || b.color() == 0 || a.color() == b.color();
    }

    private static void shock(ServerLevel level, BlockPos pos, int extra) {
        int hearts = Math.max(1, (extra + 63) / 64);
        AABB box = new AABB(pos).inflate(0.25);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
            entity.hurtServer(level, OldustryDamage.electricity(level), hearts);
        }
    }

    private EnergyNet() {}
}
