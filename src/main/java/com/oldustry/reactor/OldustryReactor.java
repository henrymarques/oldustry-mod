package com.oldustry.reactor;

import net.minecraft.world.item.ItemStack;

/**
 * IC2 1.117 {@code TileEntityNuclearReactor} numbers.
 * A lone cell's pulse is {@code +1} EU per self-accept, not {@code 2 * cells}
 * ({@code 2 * cells} is {@code influenceExplosion}). The electric tile then
 * sends {@code output * energyGeneratorNuclear}.
 */
public final class OldustryReactor {
    public static final int TICK_RATE = 20;
    public static final int BASE_HEAT = 10_000;
    public static final int BASE_COLUMNS = 3;
    public static final int ROWS = 6;
    public static final int WIDTH = 9;
    public static final int SLOTS = WIDTH * ROWS;
    public static final int FUEL_LIFE = 10_000;
    /** Crafted isotope sits one short of breaking, same as damage 9999. */
    public static final int ISOTOPE_CRAFT_DAMAGE = FUEL_LIFE - 1;
    public static final int HEAT_EFFECT = 4_000;
    public static final int LIT_HEAT = 1_000;
    public static final int OUTPUT_SCALE = 5;
    /** Path helper on the reactor tile. Not a clamp on the packet actually sent. */
    public static final int REACTOR_MAX_PACKET = 1_512;
    public static final int CHAMBER_MAX_PACKET = 240;
    public static final float BLAST_BASE = 10f;
    public static final float BLAST_CAP = 45f;
    public static final float BLAST_DROP = 0.01f;
    public static final float WRENCH_RATE = 0.8f;
    public static final int GUI_WIDTH = 176;
    public static final int GUI_HEIGHT = 222;

    public static int columns(int chambers) {
        if (chambers < 0) {
            chambers = 0;
        }
        return Math.min(WIDTH, BASE_COLUMNS + chambers);
    }

    public static int slotX(int index) {
        return index % WIDTH;
    }

    /** JAR {@code 1 + numberOfCells / 2} self pulses, each adding 1 EU. */
    public static int selfPulses(int cells) {
        return 1 + cells / 2;
    }

    public static int triangular(int pulses) {
        int sum = 0;
        for (int i = 1; i <= pulses; i++) {
            sum += i;
        }
        return sum;
    }

    /** Output added by one fuel item with no neighbors, one reactor cycle. */
    public static int soloOutput(int cells) {
        return selfPulses(cells) * cells;
    }

    /** Heat dumped into the reactor when nothing next to the fuel stores heat. */
    public static int soloHeat(int cells) {
        return triangular(selfPulses(cells)) * 4 * cells;
    }

    public static int sent(int output, int multiplier) {
        return output * multiplier;
    }

    public static boolean producing(boolean redstone, int multiplier) {
        return redstone && multiplier != 0;
    }

    public static boolean lit(int heat, float output) {
        return heat >= LIT_HEAT || output > 0f;
    }

    /** 0 silent, 1 low, 2 med, 3 high. Thresholds are the unscaled output. */
    public static int geiger(float output) {
        if (output <= 0f) {
            return 0;
        }
        if (output < 40f) {
            return 1;
        }
        if (output < 80f) {
            return 2;
        }
        return 3;
    }

    public static boolean fuelSpent(int damage) {
        return damage >= FUEL_LIFE - 1;
    }

    /** {@code IC2.random.nextInt(3) == 0} keeps a near-depleted stack. */
    public static boolean keepNearDepleted(int roll3) {
        return roll3 == 0;
    }

    /** Damage after one neutron pulse. {@code <= 0} becomes a re-enriched cell. */
    public static int isotopeDamage(int damage, int heat) {
        return damage - 1 - heat / 3000;
    }

    public static boolean heatEffects(int heat, float cap) {
        return heat >= HEAT_EFFECT && cap > 0f;
    }

    public static float ratio(int heat, int maxHeat) {
        return maxHeat <= 0 ? 0f : heat / (float) maxHeat;
    }

    public static boolean melts(float ratio) {
        return ratio >= 1f;
    }

    public static boolean lavaRoll(float ratio, float hem, float roll) {
        return ratio >= 0.85f && roll <= 0.2f * hem;
    }

    public static boolean hurts(float ratio) {
        return ratio >= 0.7f;
    }

    public static int hurt(int roll4, float hem) {
        return (int) (roll4 * hem);
    }

    public static boolean evaporates(float ratio, float hem, float roll) {
        return ratio >= 0.5f && roll <= hem;
    }

    public static boolean ignites(float ratio, float hem, float roll) {
        return ratio >= 0.4f && roll <= hem;
    }

    public static float blast(float hem, float[] influence, float cap) {
        float power = BLAST_BASE;
        float mod = 1f;
        for (float part : influence) {
            if (part > 0f && part < 1f) {
                mod *= part;
            } else {
                power += part;
            }
        }
        power *= hem * mod;
        return power > cap ? cap : power;
    }

    public static boolean useful(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getItem() instanceof OldustryReactorComponent
            || stack.is(com.oldustry.registry.OldustryItems.NEAR_DEPLETED_URANIUM_CELL.get())
            || stack.is(com.oldustry.registry.OldustryItems.RE_ENRICHED_URANIUM_CELL.get());
    }

    /**
     * IC2 {@code ItemReactorHeatStorage.alterHeat}. Overflow removes the component.
     * The returned leftover is what the caller still has to place.
     */
    public static int storedHeat(int current, int added, int capacity) {
        int next = current + added;
        if (next > capacity || next < 0) {
            return 0;
        }
        return next;
    }

    public static int heatLeftover(int current, int added, int capacity) {
        int next = current + added;
        if (next > capacity) {
            return capacity - next + 1;
        }
        if (next < 0) {
            return next;
        }
        return 0;
    }

    public static boolean heatMelts(int current, int added, int capacity) {
        return current + added > capacity;
    }

    /** IC2 {@code ItemReactorCondensator.alterHeat}. Negative heat restores wear. */
    public static int condensatorWear(int wear, int maxDamage, int heat) {
        int can = maxDamage - wear + 1;
        if (can > heat) {
            can = heat;
        }
        return wear + can;
    }

    public static int condensatorLeftover(int wear, int maxDamage, int heat) {
        int can = maxDamage - wear + 1;
        if (can > heat) {
            can = heat;
        }
        return heat - can;
    }

    public static boolean condensatorOpen(int wear, int maxDamage) {
        return wear + 1 < maxDamage;
    }

    /** IC2 {@code RecipeGradual}: subtract, then clamp to {@code [0, max]}. */
    public static int repairWear(int wear, int restored, int maxDamage) {
        int damage = wear - restored;
        if (damage > maxDamage) {
            return maxDamage;
        }
        if (damage < 0) {
            return 0;
        }
        return damage;
    }

    public static boolean reflectorBreaks(int wear, int maxDamage) {
        return wear + 1 >= maxDamage;
    }

    public static int ventPull(int reactorHeat, int limit) {
        return reactorHeat > limit ? limit : reactorHeat;
    }

    public static int clampTransfer(int add, int limit) {
        if (add > limit) {
            return limit;
        }
        if (add < -limit) {
            return -limit;
        }
        return add;
    }

    /** IC2 heat switch: self ratio, plus the reactor when it participates, plus neighbor ratios. */
    public static double heatMedian(double selfRatio, boolean withReactor, double reactorRatio, double neighborSum, int neighbors) {
        double med = selfRatio;
        int parts = 1;
        if (withReactor) {
            parts++;
            med += reactorRatio;
        }
        med += neighborSum;
        return med / (parts + neighbors);
    }

    /** Heating cell. {@code stack / 10} is integer division, so nine cells add nothing to the blast. */
    public static int heatpackAdd(int stack, int heatPer, int maxPer, int reactorHeat, int neighborHeat) {
        int want = maxPer * stack;
        if (reactorHeat >= want) {
            return 0;
        }
        int add = heatPer * stack;
        int room = want - neighborHeat;
        if (add > room) {
            add = room;
        }
        return add > 0 ? add : 0;
    }

    public static float heatpackBlast(int stack) {
        return stack / 10;
    }

    /** GUI slot origin. JAR {@code startX = 89 - 9 * size}, {@code startY = 18}. */
    public static int slotLeft(int columns) {
        return 89 - 9 * columns;
    }

    private OldustryReactor() {}
}
