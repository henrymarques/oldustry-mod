package com.oldustry.wrench;

/** IC2: keep machine when {@code rng <= dropRate}. Rate 1.0 always keeps. */
public final class WrenchLogic {
    public enum Drop {
        KEEP,
        HULL
    }

    public static Drop dismantle(float dropRate, float roll) {
        return roll <= dropRate ? Drop.KEEP : Drop.HULL;
    }

    private WrenchLogic() {}
}
