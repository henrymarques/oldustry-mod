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

    /**
     * JAR {@code AdvRecipe}: electric inputs dump into an empty electric result.
     * Transfer and tier do not apply. Anything past {@code resultMax} is lost.
     */
    public static int carriedCharge(int resultMax, long inputCharge) {
        if (inputCharge <= 0 || resultMax <= 0) {
            return 0;
        }
        int amount = inputCharge > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) inputCharge;
        return chargeIgnoreTransfer(0, amount, new Spec(resultMax, 0, 0, false));
    }

    /** JAR {@code ignoreTransferLimit=true}: only capacity room caps the move. */
    public static int chargeIgnoreTransfer(int energy, int amount, Spec spec) {
        if (amount <= 0) {
            return 0;
        }
        return Math.min(amount, Math.max(0, spec.max - energy));
    }

    /**
     * JAR {@code ElectricItemManager.charge}: nothing moves when the target tier is above the charger.
     */
    public static int chargeAtTier(int energy, int amount, Spec spec, int sourceTier, boolean ignoreTransfer) {
        if (spec.tier() > sourceTier) {
            return 0;
        }
        return ignoreTransfer ? chargeIgnoreTransfer(energy, amount, spec) : charge(energy, amount, spec);
    }

    /**
     * JAR {@code chargeFromArmor} for one worn pack: dump into the tool (ignore transfer).
     * Armor tier must reach the tool tier. Returns EU moved.
     */
    public static int chargeFromArmor(int toolEnergy, Spec tool, int armorEnergy, Spec armor) {
        if (!armor.canProvide() || armor.tier() < tool.tier()) {
            return 0;
        }
        int room = chargeIgnoreTransfer(toolEnergy, Integer.MAX_VALUE, tool);
        return dischargeIgnoreTransfer(armorEnergy, room, armor, Integer.MAX_VALUE);
    }

    /**
     * JAR {@code ElectricItemManager.use}: pay {@code amount} in full, or nothing.
     * Transfer limit and tier do not apply — the item is spending its own EU.
     */
    public static int spend(int energy, int amount) {
        if (amount <= 0 || energy < amount) {
            return 0;
        }
        return amount;
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

    /**
     * Single-use on a process machine also waits for an energy storage upgrade.
     * Transformers can push the buffer past 1200; that still does not eat the cell.
     */
    public static int consumeOnce(int room, int energyValue, boolean storageGate, int storageUpgrades) {
        if (storageGate && storageUpgrades <= 0) {
            return 0;
        }
        return consumeOnce(room, energyValue);
    }

    private ElectricCharge() {}
}
