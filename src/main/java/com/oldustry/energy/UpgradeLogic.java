package com.oldustry.energy;

/** IC2 overclocker 0.7 time / 1.6 EU; transformer +tier; storage +10000; capacity += maxInput−1. */
public final class UpgradeLogic {
    public record Stats(int euPerTick, int duration, int capacity, int tier) {}

    public static Stats apply(Stats base, int overclockers, int transformers, int storage) {
        double time = base.duration;
        double eu = base.euPerTick;
        for (int i = 0; i < overclockers; i++) {
            time *= 0.7;
            eu *= 1.6;
        }
        // Keep 0 duration for non-process machines (storage/transformer/gens) — Math.max(1,0) was lying to Jade.
        int duration = base.duration <= 0 ? 0 : Math.max(1, (int) Math.round(time));
        int euPerTick = base.euPerTick <= 0 ? 0 : Math.max(1, (int) Math.round(eu));
        int tier = base.tier + transformers;
        int capacity = base.capacity + storage * 10000;
        // JAR TileEntityStandardMachine only: maxEnergy += maxInput − 1 (not storage/gens).
        if (base.duration > 0) {
            capacity += maxSafeInput(tier) - 1;
        }
        return new Stats(euPerTick, duration, capacity, tier);
    }

    /** JAR {@code TileEntityElectricMachine.setTier} — not a bare bit-shift (overflows for tier≥15). */
    public static int maxSafeInput(int tier) {
        return switch (tier) {
            case 1 -> 32;
            case 2 -> 128;
            case 3 -> 512;
            default -> tier <= 0 ? 32 : 2048; // JAR default branch is 2048 (tier≥4); treat ≤0 as LV
        };
    }

    /** JAR: keep progress ratio when operationLength changes (overclocker install/remove). */
    public static int rescaleProgress(int progress, int oldDuration, int newDuration) {
        if (progress <= 0 || oldDuration <= 0 || newDuration <= 0 || oldDuration == newDuration) {
            return progress;
        }
        return (int) Math.floor(progress / (double) oldDuration * newDuration + 0.1D);
    }

    private UpgradeLogic() {}
}
