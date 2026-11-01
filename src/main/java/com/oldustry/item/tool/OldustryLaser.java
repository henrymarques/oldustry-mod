package com.oldustry.item.tool;

/**
 * Numbers from IC2 {@code ItemToolMiningLaser} and {@code EntityMiningLaser}.
 * The beam steps one block per tick. Air with no hit costs {@code 0.5} power.
 */
public final class OldustryLaser {
    public static final int CAPACITY = 200_000;
    public static final int TRANSFER = 120;
    public static final int TIER = 2;
    /** Inherited {@code ItemElectricTool} cost when a left-click actually breaks a block. */
    public static final int BREAK_COST = 100;
    public static final int MODES = 7;
    public static final int HORIZONTAL = 3;
    public static final int HORIZONTAL_COST = 3000;
    public static final float SPEED = 1f;
    public static final float EXPLOSION_POWER = 5f;
    public static final float EXPLOSION_DROP = 0.85f;
    /** Right-click costs. Horizontal is 0 here; the block click spends {@link #HORIZONTAL_COST}. */
    private static final int[] COST = {1250, 100, 5000, 0, 2500, 10000, 5000};

    public static int next(int mode) {
        return Math.floorMod(mode + 1, MODES);
    }

    public static int cost(int mode) {
        return COST[Math.floorMod(mode, MODES)];
    }

    /**
     * {@code onItemUseFirst} subtracts the block center: {@code abs((eyeY - 0.1) - (blockY + 0.5)) < 1.5}.
     * The decompile writes {@code - blockY + 0.5} and drops the parentheses, which shifts the window up one block.
     */
    public static boolean horizontalAim(double eyeY, int blockY) {
        return Math.abs(eyeY - 0.1 - (blockY + 0.5)) < 1.5;
    }

    public static double muzzleX(double x, double yawDeg) {
        return x - Math.cos(Math.toRadians(yawDeg)) * 0.16;
    }

    public static double muzzleZ(double z, double yawDeg) {
        return z - Math.sin(Math.toRadians(yawDeg)) * 0.16;
    }

    public static double eyeLaunch(double y, double eyeHeight) {
        return y + eyeHeight - 0.1;
    }

    /** Unit look vector. Yaw and pitch are degrees, same axes as the player. */
    public static double[] heading(double yawDeg, double pitchDeg) {
        double yaw = Math.toRadians(yawDeg);
        double pitch = Math.toRadians(pitchDeg);
        double x = -Math.sin(yaw) * Math.cos(pitch);
        double y = -Math.sin(pitch);
        double z = Math.cos(yaw) * Math.cos(pitch);
        double length = Math.sqrt(x * x + y * y + z * z);
        return new double[] {x / length, y / length, z / length};
    }

    public static float scatterYaw(float yaw, int column) {
        return yaw + 20f * column;
    }

    public static float scatterPitch(float pitch, int row) {
        return pitch + 20f * row;
    }

    public static boolean spent(float range, float power, int breaks) {
        return range < 1f || power <= 0f || breaks <= 0;
    }

    public static float absorb(float power, float resistance) {
        return power - (resistance + 0.3f) / 10f;
    }

    public static float airDrain(float power) {
        return power - 0.5f;
    }

    public static float stepRange(float range) {
        return range - SPEED;
    }

    /** {@code setFire} takes seconds. Super-Heat doubles them. */
    public static float burnSeconds(int damage, boolean smelt) {
        return damage * (smelt ? 2f : 1f);
    }

    private OldustryLaser() {}
}
