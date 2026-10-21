package com.oldustry.explosion;

/** Distances from {@code ExplosionIC2.doExplosion} when the blast is nuclear. */
final class NukeFallout {
    private NukeFallout() {}

    /** Hunger ticks. Negative means the entity is outside the 100-block radius. */
    static int hungerTicks(double distance) {
        return (int) (120.0 * (100.0 - distance));
    }

    /** Radiation ticks. Negative means the entity is outside the 30-block radius. */
    static int radiationTicks(double distance) {
        return (int) (80.0 * (30.0 - distance));
    }

    /** JAR {@code IC2Potion.radiation}: 1 damage every {@code 25 >> amplifier} ticks. */
    static boolean radiationTick(int tickCount, int amplifier) {
        int rate = 25 >> amplifier;
        return rate > 0 ? tickCount % rate == 0 : true;
    }
}
