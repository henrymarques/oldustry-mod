package com.oldustry.reactor;

import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.world.item.ItemStack;

/** Heat and wear live on the stack. The JAR kept them in NBT and mirrored a damage bar. */
public final class OldustryReactorStore {
    public static int heat(ItemStack stack) {
        return stack.getOrDefault(OldustryDataComponents.REACTOR_HEAT.get(), 0);
    }

    public static void setHeat(ItemStack stack, int heat) {
        if (heat <= 0) {
            stack.remove(OldustryDataComponents.REACTOR_HEAT.get());
        } else {
            stack.set(OldustryDataComponents.REACTOR_HEAT.get(), heat);
        }
    }

    public static int wear(ItemStack stack) {
        return stack.getOrDefault(OldustryDataComponents.REACTOR_WEAR.get(), 0);
    }

    public static void setWear(ItemStack stack, int wear) {
        if (wear <= 0) {
            stack.remove(OldustryDataComponents.REACTOR_WEAR.get());
        } else {
            stack.set(OldustryDataComponents.REACTOR_WEAR.get(), wear);
        }
    }

    /** Remaining room, same direction as the vanilla damage bar. */
    public static int bar(int used, int max) {
        int free = Math.max(0, max - used);
        return Math.round(13f * free / max);
    }

    private OldustryReactorStore() {}
}
