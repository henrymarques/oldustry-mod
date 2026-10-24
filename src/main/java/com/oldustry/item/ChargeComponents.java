package com.oldustry.item;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

/**
 * ENERGY/FUEL/walk-anchor diffs must not look like a different item (mining reset, re-equip anim,
 * LivingEntity equipment sync).
 */
public final class ChargeComponents {
    private ChargeComponents() {}

    /** Same item/count; any component difference is only charge-related (or identical). */
    public static boolean onlyChargeDiffers(ItemStack a, ItemStack b) {
        if (a == b) {
            return true;
        }
        if (a.isEmpty() || b.isEmpty() || a.getCount() != b.getCount() || !ItemStack.isSameItem(a, b)) {
            return false;
        }
        Set<DataComponentType<?>> keys = new HashSet<>();
        keys.addAll(a.getComponents().keySet());
        keys.addAll(b.getComponents().keySet());
        for (DataComponentType<?> type : keys) {
            if (isCharge(type)) {
                continue;
            }
            if (!Objects.equals(a.get(type), b.get(type))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCharge(DataComponentType<?> type) {
        return type == OldustryDataComponents.ENERGY.get()
            || type == OldustryDataComponents.FUEL.get()
            || type == OldustryDataComponents.HOVER.get()
            || type == OldustryDataComponents.WALK_X.get()
            || type == OldustryDataComponents.WALK_Z.get();
    }
}
