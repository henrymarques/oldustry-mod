package com.oldustry.machine;

import com.oldustry.registry.OldustryItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** IC2 JAR fuel units added per input by the Canning Machine (mode 2). */
public final class OldustryCannerFuelValues {
    /** Coalfuel Cell. */
    public static final int COALFUEL = 2548;
    /** Biofuel Cell. */
    public static final int BIOFUEL = 868;

    private OldustryCannerFuelValues() {}

    public static boolean isFuel(ItemStack stack) {
        return stack.is(OldustryItems.COALFUEL_CELL.get())
            || stack.is(OldustryItems.BIOFUEL_CELL.get())
            || stack.is(Items.REDSTONE)
            || stack.is(Items.GLOWSTONE_DUST)
            || stack.is(Items.GUNPOWDER);
    }

    /**
     * Coalfuel 2548, biofuel 868. Once {@code stored} is already above 0, redstone / glowstone /
     * gunpowder add 20% / 30% / 40% of that total, truncated.
     */
    public static int value(ItemStack stack, int stored) {
        if (stack.is(OldustryItems.COALFUEL_CELL.get())) {
            return COALFUEL;
        }
        if (stack.is(OldustryItems.BIOFUEL_CELL.get())) {
            return BIOFUEL;
        }
        if (stored <= 0) {
            return 0;
        }
        if (stack.is(Items.REDSTONE)) {
            return (int) (stored * 0.2);
        }
        if (stack.is(Items.GLOWSTONE_DUST)) {
            return (int) (stored * 0.3);
        }
        if (stack.is(Items.GUNPOWDER)) {
            return (int) (stored * 0.4);
        }
        return 0;
    }
}
