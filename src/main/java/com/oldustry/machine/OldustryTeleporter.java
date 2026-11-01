package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

/** JAR {@code TileEntityTeleporter} cost, range and storage drain. */
public final class OldustryTeleporter {
    public static final int FAR = 1000;

    public static double distance(int x, int y, int z, int tx, int ty, int tz) {
        double dx = x - tx;
        double dy = y - ty;
        double dz = z - tz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static boolean far(double distance) {
        return distance >= FAR;
    }

    /** {@code (int)(weight * (distance + 10) ^ 0.7 * 5)}. */
    public static int cost(int weight, double distance) {
        return (int) (weight * Math.pow(distance + 10.0, 0.7) * 5.0);
    }

    /** Item entities and carried stacks: {@code 100 * count / maxStack}. */
    public static int stackWeight(int count, int maxStack) {
        return maxStack <= 0 ? 0 : 100 * count / maxStack;
    }

    /**
     * One body, before carried stacks. Mount is animal, boat or minecart.
     * Creature is the 1.5.2 {@code EntityCreature} bucket (not ghast, dragon, slime).
     */
    public static int bodyWeight(boolean mount, boolean player, boolean ghast, boolean dragon, boolean creature) {
        if (mount) {
            return 100;
        }
        if (player) {
            return 1000;
        }
        if (ghast) {
            return 2500;
        }
        if (dragon) {
            return 10000;
        }
        return creature ? 500 : 0;
    }

    /** Column on top of the block. JAR also covered the four side blocks. */
    public static AABB pickupBox(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return new AABB(x, y + 1, z, x + 1, y + 3, z + 1);
    }

    /** {@code targetY + 1.5 + getYOffset()}. Player offset is 0 ({@code yOffset - 1.62}). */
    public static double arriveY(int blockY) {
        return blockY + 1.5;
    }

    /**
     * JAR {@code consumeEnergy}: ceil-share across storages that still hold EU.
     * Returns false without writing when the sum is short.
     */
    public static boolean draw(int cost, int[] stored) {
        if (cost <= 0) {
            return true;
        }
        int energy = cost;
        int[] index = new int[stored.length];
        int n = 0;
        int sum = 0;
        for (int i = 0; i < stored.length; i++) {
            if (stored[i] > 0) {
                index[n++] = i;
                sum += stored[i];
            }
        }
        if (sum < energy) {
            return false;
        }
        int guard = n * energy + 4;
        while (energy > 0 && n > 0 && guard-- > 0) {
            int drain = (energy + n - 1) / n;
            int k = 0;
            while (k < n) {
                if (drain > energy) {
                    drain = energy;
                }
                int at = index[k];
                if (stored[at] <= drain) {
                    energy -= stored[at];
                    stored[at] = 0;
                    index[k] = index[--n];
                } else {
                    energy -= drain;
                    stored[at] -= drain;
                    k++;
                }
            }
        }
        return energy <= 0;
    }

    private OldustryTeleporter() {}
}
