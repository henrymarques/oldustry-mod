package com.oldustry.energy;

/** Charge/discharge with transfer limit and tier gate. */
public final class ElectricCharge {
    public record Spec(int max, int transfer, int tier, boolean canProvide) {}

    public static int charge(int energy, int amount, Spec spec) {
        if (amount <= 0) {
            return 0;
        }
        int room = spec.max - energy;
        int moved = Math.min(Math.min(amount, spec.transfer), Math.max(0, room));
        return moved;
    }

    /** JAR {@code ignoreTransferLimit=true}: only capacity room caps the move. */
    public static int chargeIgnoreTransfer(int energy, int amount, Spec spec) {
        if (amount <= 0) {
            return 0;
        }
        return Math.min(amount, Math.max(0, spec.max - energy));
    }

    public static int discharge(int energy, int amount, Spec spec, int sinkTier) {
        if (!spec.canProvide || sinkTier < spec.tier || amount <= 0 || energy <= 0) {
            return 0;
        }
        return Math.min(Math.min(amount, spec.transfer), energy);
    }

    /** JAR {@code ignoreTransferLimit=true}: only stored EU caps the move (tier gate still applies). */
    public static int dischargeIgnoreTransfer(int energy, int amount, Spec spec, int sinkTier) {
        if (!spec.canProvide || sinkTier < spec.tier || amount <= 0 || energy <= 0) {
            return 0;
        }
        return Math.min(amount, energy);
    }

    /**
     * One-shot discharge (SU battery / redstone): only if {@code room} fits the full value.
     * Returns EU to add, or 0 → caller must not shrink the stack.
     */
    public static int consumeOnce(int room, int energyValue) {
        if (energyValue <= 0 || room < energyValue) {
            return 0;
        }
        return energyValue;
    }

    private ElectricCharge() {}
}
