package com.oldustry.crop;

/**
 * JAR {@code TileEntityCrop} growth, crossing and drop counts. The switch in
 * {@code calculateRatioFor} falls through: a difference of 0 scores 3, 1 scores 1, 2 scores 0, and anything else scores 2.
 */
public final class OldustryCropMath {
    /** {@code tickRate} is the char 256. The crop step runs on that modulus. */
    public static final int TICK_RATE = 256;

    private OldustryCropMath() {}

    public static int statDiffScore(int diff) {
        int distance = Math.abs(diff);
        if (distance == 2) {
            return 0;
        }
        if (distance == 1) {
            return 1;
        }
        if (distance == 0) {
            return 3;
        }
        return 2;
    }

    public static int ratio(OldustryCrop a, OldustryCrop b) {
        if (a == b) {
            return 500;
        }
        int value = 0;
        for (int i = 0; i < 5; i++) {
            value += statDiffScore(a.stat(i) - b.stat(i));
        }
        for (String left : a.attributes()) {
            for (String right : b.attributes()) {
                if (left.equalsIgnoreCase(right)) {
                    value += 5;
                }
            }
        }
        if (b.tier() < a.tier() - 1) {
            value -= 2 * (a.tier() - b.tier());
        }
        if (b.tier() - 3 > a.tier()) {
            value -= b.tier() - a.tier();
        }
        return Math.max(value, 0);
    }

    /** JAR {@code calcGrowthRate}: {@code have = weightInfluences(...) * 5}. */
    public static int envHave(int weight) {
        return weight * 5;
    }

    /**
     * @param have already {@link #envHave(int)}
     * @return growth points to add, or {@code -1} when the plant dies from a bad environment
     */
    public static int growthRate(OldustryCrop crop, int growth, int gain, int resistance, int have, int roll7, int roll32) {
        int base = 3 + roll7 + growth;
        int need = (crop.tier() - 1) * 4 + growth + gain + resistance;
        if (need < 0) {
            need = 0;
        }
        if (have >= need) {
            return base * (100 + have - need) / 100;
        }
        int neg = (need - have) * 4;
        if (neg > 100 && roll32 > resistance) {
            return -1;
        }
        base = base * (100 - neg) / 100;
        return Math.max(base, 0);
    }

    public static int humidity(int biome, int farmlandMoisture, int waterStorage) {
        int value = biome;
        if (farmlandMoisture >= 7) {
            value += 2;
        }
        if (waterStorage >= 5) {
            value += 2;
        }
        return value + (waterStorage + 24) / 25;
    }

    public static int nutrients(int biome, int dirtLayers, int storage) {
        return biome + dirtLayers + (storage + 19) / 20;
    }

    /** Y above 64 adds up to 4. {@code fresh} is what is left of the 9 after the neighbour scan. */
    public static int air(int y, int fresh, boolean sky) {
        int height = (y - 64) / 15;
        if (height > 4) {
            height = 4;
        }
        if (height < 0) {
            height = 0;
        }
        return height + fresh / 2 + (sky ? 2 : 0);
    }

    public static int clampStat(int value) {
        return Math.max(0, Math.min(31, value));
    }

    /** Average of the parents, then {@code nextInt(1 + 2 * count) - count}. */
    public static int crossedStat(int sum, int count, int roll) {
        return clampStat(sum / count + roll - count);
    }

    public static float dropGainChance(OldustryCrop crop) {
        float base = crop.gainChance();
        return base;
    }

    public static float seedChance(int size, int tier) {
        if (size == 1) {
            return 0.0f;
        }
        float base = 0.5f;
        if (size == 2) {
            base /= 2.0f;
        }
        for (int i = 0; i < tier; i++) {
            base *= 0.8f;
        }
        return base;
    }

    /**
     * {@code chance *= 1.03} per gain point, then subtract random floats until it is not positive.
     * {@code next} is {@code nextFloat}, in order.
     */
    public static int gainDrops(float chance, int gain, java.util.function.DoubleSupplier next) {
        for (int i = 0; i < gain; i++) {
            chance *= 1.03f;
        }
        chance -= (float) next.getAsDouble();
        int drop = 0;
        while (chance > 0.0f) {
            drop++;
            chance -= (float) next.getAsDouble();
        }
        return drop;
    }

    /** JAR weed switch has no breaks until the last case, so only west and north actually move. */
    public static int[] weedStep(int roll) {
        int x = 0;
        int z = 0;
        switch (roll) {
            case 0:
                x++;
            case 1:
                x--;
            case 2:
                z++;
            case 3:
                z--;
                break;
            default:
                break;
        }
        return new int[] {x, z};
    }
}
