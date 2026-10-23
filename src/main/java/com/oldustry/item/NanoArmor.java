package com.oldustry.item;

/**
 * JAR {@code ItemArmorElectric} ratios times {@code ItemArmorNanoSuit} 0.9.
 * Forge's {@code * 25} on the damage and on {@code AbsorbMax} cancel, so one
 * damage point costs {@link #EU_PER_DAMAGE} and a full set keeps 10%.
 */
public final class NanoArmor {
    public static final int MAX = 100_000;
    public static final int TRANSFER = 160;
    public static final int TIER = 2;
    public static final int EU_PER_DAMAGE = 800;
    /** Boots cancel the fall when {@code (int) distance - 3} is below this. */
    public static final int FALL_CANCEL_BELOW = 8;
    /** JAR hit with an active nano saber empties this much from each nano piece. */
    public static final int SHRED_EU = 4800;

    private static final double HELMET = 0.15 * 0.9;
    private static final double CHEST = 0.4 * 0.9;
    private static final double LEGS = 0.3 * 0.9;
    private static final double BOOTS = 0.15 * 0.9;
    /** Fall, boots, damage already at least {@link #FALL_CANCEL_BELOW}. */
    private static final double BOOTS_FALL = 0.875;

    public record Hit(float remaining, int boots, int legs, int chest, int helmet) {
        public int eu(int slot) {
            return switch (slot) {
                case 0 -> boots;
                case 1 -> legs;
                case 2 -> chest;
                case 3 -> helmet;
                default -> 0;
            };
        }
    }

    private NanoArmor() {}

    /**
     * @param fall boots go first at 100% (damage &lt; 8) or 87.5%, then the other pieces
     *             take their normal share of what is left
     */
    public static Hit absorb(float damage, int boots, int legs, int chest, int helmet, boolean fall) {
        int[] charge = { boots, legs, chest, helmet };
        double[] ratio = { BOOTS, LEGS, CHEST, HELMET };
        if (fall) {
            ratio[0] = damage < FALL_CANCEL_BELOW ? 1.0 : BOOTS_FALL;
        }
        int[] eu = new int[4];
        double left = damage;
        for (int i = 0; i < 4; i++) {
            double basis = fall && i != 0 ? left : damage;
            double cap = charge[i] / (double) EU_PER_DAMAGE;
            double take = Math.min(basis * ratio[i], cap);
            take = Math.min(take, left);
            if (take <= 0) {
                continue;
            }
            eu[i] = Math.min((int) Math.round(take * EU_PER_DAMAGE), charge[i]);
            left -= take;
        }
        return new Hit((float) left, eu[0], eu[1], eu[2], eu[3]);
    }

    /**
     * JAR {@code getArmorDisplay}: {@code round(20 × ratio)} while charge covers one point, else 0.
     * Slot order matches {@link #absorb}: boots, legs, chest, helmet. These icons are not the damage math.
     */
    public static int display(int slot, int charge) {
        if (charge < EU_PER_DAMAGE) {
            return 0;
        }
        double ratio = switch (slot) {
            case 0 -> BOOTS;
            case 1 -> LEGS;
            case 2 -> CHEST;
            case 3 -> HELMET;
            default -> 0;
        };
        return (int) Math.round(20.0 * ratio);
    }

    /** EU the boots must hold to cancel this many points of fall damage. {@code -1} means the fall lands. */
    public static int fallCancelCost(int dealt) {
        if (dealt <= 0 || dealt >= FALL_CANCEL_BELOW) {
            return -1;
        }
        return dealt * EU_PER_DAMAGE;
    }

    /** JAR {@code (int) distance - 3}. */
    public static int fallCancelEu(double distance) {
        return fallCancelCost((int) distance - 3);
    }
}
