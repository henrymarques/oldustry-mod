package com.oldustry.machine;

/** JAR {@code TileEntityTerra} scan cursor and EU spend. Block edits live in {@link OldustryTfbp}. */
public final class OldustryTerraformer {
    public static final int CAPACITY = 100000;
    public static final int MAX_INPUT = 512;
    /** Idle ticks that stay lit after the last paid column. The compare is post-increment, so this is 31 ticks. */
    public static final int ACTIVE_GRACE = 30;

    public record Cursor(int failed, int lastX, int lastY, int lastZ) {
        public static Cursor fresh() {
            return new Cursor(0, -1, -1, -1);
        }
    }

    /** Follow-up sweeps use {@code range/10}. A cold start uses {@code range * (failed + 1) / 5} with failed capped at 4. */
    public static int scanRange(int tfbpRange, Cursor cursor) {
        if (cursor.lastY() > -1) {
            return tfbpRange / 10;
        }
        int failed = Math.min(cursor.failed(), 4);
        return tfbpRange * (failed + 1) / 5;
    }

    public static int roll(int origin, int range, int first, int second) {
        return origin - first + second;
    }

    public static Cursor clampFailed(Cursor cursor) {
        if (cursor.lastY() > -1 || cursor.failed() <= 4) {
            return cursor;
        }
        return new Cursor(4, cursor.lastX(), cursor.lastY(), cursor.lastZ());
    }

    public static int spent(int energy, int consume, boolean success) {
        return energy - (success ? consume : consume / 10);
    }

    public static Cursor after(Cursor cursor, boolean success, int x, int z, int machineY) {
        if (success) {
            return new Cursor(0, x, machineY, z);
        }
        return new Cursor(cursor.failed() + 1, cursor.lastX(), -1, cursor.lastZ());
    }

    /** @return {@code [active 0/1, inactiveTicks]} */
    public static int[] grace(boolean working, boolean active, int inactive) {
        if (working) {
            return new int[] {1, 0};
        }
        if (active && inactive++ > ACTIVE_GRACE) {
            return new int[] {0, inactive};
        }
        return new int[] {active ? 1 : 0, inactive};
    }

    /**
     * Same walk as {@code TileEntityTerra.switchGround}. {@code column[y]} is the block code.
     * Upwards starts one above {@code y} and replaces the first cell whose underside is no longer {@code from}.
     */
    public static boolean switchColumn(int[] column, int y, int from, int to, boolean upwards) {
        if (upwards) {
            int save = ++y;
            while (y - 1 >= 0 && y - 1 < column.length && column[y - 1] == from) {
                y--;
            }
            if (save == y || y < 0 || y >= column.length) {
                return false;
            }
            column[y] = to;
            return true;
        }
        while (y >= 0 && y < column.length && column[y] == to) {
            y--;
        }
        if (y < 0 || y >= column.length || column[y] != from) {
            return false;
        }
        column[y] = to;
        return true;
    }

    private OldustryTerraformer() {}
}
