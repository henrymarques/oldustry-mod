package com.oldustry.entity;

/**
 * JAR {@code EntityIC2Boat} / rubber / carbon / electric.
 * Crash is horizontal speed above {@code getBreakMotion}. Rubber above 0.26 drops the damaged dinghy.
 * Electric pays 4 EU from one worn provider while moving faster than 0.1, then uses the 1.5 factor and 0.7 cap.
 */
public final class OldustryBoatStats {
    public static final double RUBBER_BREAK = 0.23;
    public static final double RUBBER_SPLIT = 0.26;
    public static final double CARBON_BREAK = 0.4;
    public static final double ELECTRIC_BREAK = 0.5;
    /** JAR {@code getTopSpeed} for rubber and carbon. */
    public static final double PLAIN_TOP = 0.35;
    public static final double ELECTRIC_TOP = 0.7;
    /**
     * Unpowered electric equilibrium of rider speed 0.1 × 0.07 × factor 0.25, water drag 0.99.
     * Holds the paddle boat under the crash line when no armor pays.
     */
    public static final double ELECTRIC_COAST = 0.175;
    /**
     * ponytail: vanilla water paddle settles near 0.36 ({@code (v+0.04)*0.9}).
     * Adding this along the current heading, then the same drag, settles near {@link #ELECTRIC_TOP}.
     */
    public static final double ELECTRIC_BOOST = 0.038;
    public static final double EU_SPEED = 0.1;
    public static final int EU = 4;

    public enum Kind {
        RUBBER(RUBBER_BREAK, false),
        CARBON(CARBON_BREAK, false),
        ELECTRIC(ELECTRIC_BREAK, true);

        public final double breakMotion;
        public final boolean lava;

        Kind(double breakMotion, boolean lava) {
            this.breakMotion = breakMotion;
            this.lava = lava;
        }
    }

    public static boolean crashes(double horizontalSpeed, double breakMotion) {
        return horizontalSpeed > breakMotion;
    }

    /** JAR rubber {@code breakBoat}: motion &gt; 0.26 drops the damaged dinghy. */
    public static boolean rubberSplits(double horizontalSpeed) {
        return horizontalSpeed > RUBBER_SPLIT;
    }

    public static boolean paysEu(double horizontalSpeed) {
        return horizontalSpeed > EU_SPEED;
    }

    private OldustryBoatStats() {}
}
