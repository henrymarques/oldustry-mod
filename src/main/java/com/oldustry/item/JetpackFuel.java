package com.oldustry.item;

/**
 * Readable fuel from JAR {@code ItemArmorJetpack}. {@code maxDamage} is 18002.
 * {@code getCharge} is {@code max(0, maxDamage - 2 - damage - 1)}. Damage never
 * goes below 1, so the fullest stack (creative and canner) reads 17998.
 * {@code use(n)} then reads back {@code n + 4} lower, until the remainder clips to 0.
 */
public final class JetpackFuel {
    public static final int MAX_DAMAGE = 18002;
    public static final int MAX = 17998;
    public static final int USE_NORMAL = 9;
    public static final int USE_HOVER = 6;
    /** Crafted recipe meta. {@code getCharge} is 0. */
    private static final int EMPTY_DAMAGE = 18001;

    private JetpackFuel() {}

    public static int chargeOfDamage(int damage) {
        int charge = (MAX_DAMAGE - 2) - damage - 1;
        return Math.max(0, charge);
    }

    /** Inverse of {@link #chargeOfDamage} for a charged stack. Empty maps to the crafted meta. */
    public static int damageOfCharge(int charge) {
        if (charge <= 0) {
            return EMPTY_DAMAGE;
        }
        return (MAX_DAMAGE - 2) - 1 - charge;
    }

    public static int afterUse(int charge, int amount) {
        int next = Math.max(0, charge - amount);
        return chargeOfDamage(1 + MAX_DAMAGE - next);
    }

    /** Canner subtracts {@code fuelQuality} from damage and refuses to go below 1. */
    public static int afterCanner(int charge, int fuelQuality) {
        int damage = Math.max(1, damageOfCharge(charge) - fuelQuality);
        return chargeOfDamage(damage);
    }
}
