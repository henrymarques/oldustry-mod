package com.oldustry.api;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.item.ElectricItem;

import net.minecraft.world.item.ItemStack;

/** Thin charge API for other mods. Not an ic2.api clone — Oldustry-native. */
public final class ElectricChargeAccess {
    private ElectricChargeAccess() {}

    public static boolean isElectric(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem;
    }

    public static int energy(ItemStack stack) {
        return isElectric(stack) ? ElectricItem.energy(stack) : 0;
    }

    public static int maxEnergy(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem electric ? electric.spec().max() : 0;
    }

    public static int transferLimit(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem electric ? electric.spec().transfer() : 0;
    }

    public static int tier(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem electric ? electric.spec().tier() : 0;
    }

    /** @return EU actually added */
    public static int charge(ItemStack stack, int amount) {
        return stack.getItem() instanceof ElectricItem electric ? electric.charge(stack, amount) : 0;
    }

    /** @return EU actually removed */
    public static int discharge(ItemStack stack, int amount, int sinkTier) {
        return stack.getItem() instanceof ElectricItem electric ? electric.discharge(stack, amount, sinkTier) : 0;
    }

    public static ElectricCharge.Spec specOrNull(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem electric ? electric.spec() : null;
    }
}
