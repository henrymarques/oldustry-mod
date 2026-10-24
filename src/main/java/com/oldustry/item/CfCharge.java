package com.oldustry.item;

/**
 * Remaining construction-foam units. JAR stored the inverse in item damage
 * (sprayer max 1602, crafted at 1601; pack max 260, crafted at 259).
 * Both tanks are crafted empty at 0. The sprayer holds 1600 (16 sprays).
 * The pack holds 260.
 */
public final class CfCharge {
    public static final int SPRAYER_MAX = 1600;
    public static final int SPRAYER_CRAFTED = 0;
    public static final int SPRAY_COST = 100;
    public static final int PACK_MAX = 260;
    public static final int PACK_CRAFTED = 0;

    private CfCharge() {}

    /** {@code RecipeGradual}: 100 units per pellet slot, clamped to the sprayer max. */
    public static int addPellets(int charge, int pellets) {
        long next = (long) charge + (long) SPRAY_COST * pellets;
        if (next > SPRAYER_MAX) {
            return SPRAYER_MAX;
        }
        return (int) Math.max(0, next);
    }

    /** A spray from the tool itself needs a full 100 units. The pack bypasses this. */
    public static boolean canSprayFromSelf(int charge) {
        return charge >= SPRAY_COST;
    }

    public static int afterSpray(int charge) {
        return Math.max(0, charge - SPRAY_COST);
    }

    public static boolean canFeed(int charge) {
        return charge >= 1;
    }

    public static int afterFeed(int charge) {
        return Math.max(0, charge - 1);
    }

    /** Canner mode 3: one pellet adds 2 units. */
    public static int afterPellet(int charge) {
        return Math.min(PACK_MAX, Math.max(0, charge) + 2);
    }

    /** Room for another pellet that lands on the cap: 2 units, up to 260. */
    public static boolean acceptsPellet(int charge) {
        return charge <= PACK_MAX - 2;
    }

    /** Eject when the tank is full, or when the pellets run out. */
    public static boolean leaveCanner(int charge, boolean pelletsRemain) {
        return !pelletsRemain || charge >= PACK_MAX;
    }
}
