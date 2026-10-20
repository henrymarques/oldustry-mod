package com.oldustry.energy;

/** IC2 TileEntityWindGenerator.gainEnergy (1.117.385-lf). */
public final class WindLogic {
    public static final int SEA = 64;
    public static final int TICK_RATE = 128;
    public static final int MAX_OUTPUT = 10;
    public static final int GENERATOR_PERCENT = 100;

    public static double production(int wind, int y, int obscured, boolean thunder, boolean rain) {
        double sub = wind * (y - SEA - obscured) / 750.0;
        if (sub <= 0) {
            return 0;
        }
        if (thunder) {
            sub *= 1.5;
        } else if (rain) {
            sub *= 1.2;
        }
        return sub * GENERATOR_PERCENT / 100.0;
    }

    public static int gaugeFuel(double subproduction, int pixels) {
        int re = (int) (subproduction / 3.0 * pixels);
        if (re < 0) {
            return 0;
        }
        return Math.min(pixels, re);
    }

    private WindLogic() {}
}
