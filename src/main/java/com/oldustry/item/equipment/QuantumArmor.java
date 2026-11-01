package com.oldustry.item.equipment;

/**
 * IC2 {@code ItemArmorQuantumSuit}. Base slot ratios times 1.0, chest times 1.1.
 * A full set sums past 1 and is scaled back to a full absorb, the way Forge did.
 */
public final class QuantumArmor {
    public static final int MAX = 1_000_000;
    public static final int TRANSFER = 1000;
    public static final int TIER = 3;
    public static final int EU_PER_DAMAGE = 900;
    public static final int AIR_EU = 1000;
    public static final int FOOD_EU = 1000;
    public static final int LEG_EU = 1000;
    public static final int JUMP_EU = 4000;
    /** Poison rate. Every harmful effect except wither uses this. */
    public static final int POISON_EU = 10_000;
    public static final int WITHER_EU = 25_000;
    /** Nano saber discharge per quantum piece. */
    public static final int SHRED = 30_000;
    public static final double JUMP_XZ = 3.5;
    /** Boots, legs, chest, helmet. Chest is {@code 0.4 * 1.1}. */
    private static final double[] RATIO = { 0.15, 0.30, 0.4 * 1.1, 0.15 };

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

    private QuantumArmor() {}

    /** Slot order matches {@link NanoArmor}: boots, legs, chest, helmet. */
    public static double ratio(int slot) {
        return slot >= 0 && slot < RATIO.length ? RATIO[slot] : 0;
    }

    public static int display(int slot, int charge) {
        if (charge < EU_PER_DAMAGE) {
            return 0;
        }
        return (int) Math.round(20.0 * ratio(slot));
    }

    /**
     * {@code max((int) distance - 3 - 7, 0) * 900}. Zero still cancels the fall.
     * The caller cancels when this is {@code <=} the charge.
     */
    public static int fallCancelEu(double distance) {
        return Math.max((int) distance - 3 - 7, 0) * EU_PER_DAMAGE;
    }

    /** Wither keeps its rate. Any other harmful effect uses {@link #POISON_EU}. Beneficial and neutral return 0. */
    public static int cleanseBase(boolean harmful, boolean wither) {
        if (!harmful) {
            return 0;
        }
        return wither ? WITHER_EU : POISON_EU;
    }

    public static int cleanseCost(int base, int amplifier) {
        return base * (amplifier + 1);
    }

    public static int refilledAir(int air) {
        return air + 200;
    }

    public static boolean legPays(int ticker) {
        return ticker + 1 >= 10;
    }

    public static int afterLegTick(int ticker) {
        int next = ticker + 1;
        return next >= 10 ? 0 : next;
    }

    public static float legSpeed(boolean water) {
        return water ? 0.1f : 0.22f;
    }

    public static boolean firstHop(float charge) {
        return charge == 1f;
    }

    public static double lift(float charge) {
        return charge * 0.3f;
    }

    public static float nextJump(float charge) {
        return (float) (charge * 0.75);
    }

    /**
     * @param charge boots, legs, chest, helmet. Fall uses only the boots, at ratio 1.
     */
    public static Hit absorb(float damage, int boots, int legs, int chest, int helmet, boolean fall) {
        double[] ratio = fall
            ? new double[] { 1, 0, 0, 0 }
            : new double[] { ratio(0), ratio(1), ratio(2), ratio(3) };
        return share(
            damage,
            new int[] { boots, legs, chest, helmet },
            ratio,
            new int[] { EU_PER_DAMAGE, EU_PER_DAMAGE, EU_PER_DAMAGE, EU_PER_DAMAGE }
        );
    }

    /**
     * Forge {@code ApplyArmor} hands {@code damageArmor} an int: anything under 1 point still bills 1,
     * and the rest is truncated. Lava is 4, so a full quantum set bills 1 point on every piece.
     */
    public static int billedPoints(double absorbed) {
        if (absorbed <= 0) {
            return 0;
        }
        return (int) (absorbed < 1 ? 1 : absorbed);
    }

    /** One Forge priority group. Ratios that sum past 1 are scaled; a piece that cannot pay its share is pinned. */
    public static Hit share(float damage, int[] charge, double[] ratio, int[] euPer) {
        int[] eu = new int[4];
        if (damage <= 0f) {
            return new Hit(0f, 0, 0, 0, 0);
        }
        double[] base = new double[4];
        for (int i = 0; i < 4; i++) {
            if (charge[i] > 0 && ratio[i] > 0 && euPer[i] > 0) {
                base[i] = ratio[i];
            }
        }
        double[] out = new double[4];
        boolean[] pinned = new boolean[4];
        for (int pass = 0; pass < 4; pass++) {
            double sum = 0;
            double room = 1;
            for (int i = 0; i < 4; i++) {
                if (pinned[i]) {
                    room -= out[i];
                } else {
                    sum += base[i];
                }
            }
            if (room < 0) {
                room = 0;
            }
            if (sum <= 0) {
                break;
            }
            double scale = sum > room ? room / sum : 1;
            boolean pinnedNow = false;
            for (int i = 0; i < 4; i++) {
                if (pinned[i] || base[i] == 0) {
                    continue;
                }
                double part = base[i] * scale;
                double cap = (charge[i] / (double) euPer[i]) / damage;
                if (part > cap) {
                    out[i] = cap;
                    pinned[i] = true;
                    pinnedNow = true;
                } else {
                    out[i] = part;
                }
            }
            if (!pinnedNow) {
                break;
            }
        }
        double left = damage;
        for (int i = 0; i < 4; i++) {
            if (euPer[i] <= 0) {
                continue;
            }
            double take = Math.min(damage * out[i], charge[i] / (double) euPer[i]);
            take = Math.min(take, Math.max(0, left));
            if (take <= 0) {
                continue;
            }
            eu[i] = Math.min(billedPoints(take) * euPer[i], charge[i]);
            left -= take;
        }
        if (left < 1.0e-4) {
            left = 0;
        }
        return new Hit((float) left, eu[0], eu[1], eu[2], eu[3]);
    }
}
