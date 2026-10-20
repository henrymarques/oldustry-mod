package com.oldustry.item;

import net.minecraft.world.item.Item;

/**
 * CF Construction Foam Pack – durability 260 (0 = fully charged, 260 = empty).
 * Charged by Canner (CF-pellets reduce damage toward 0).
 * Used like a CF Sprayer once charged.
 */
public class CfPackItem extends Item {
    public static final int MAX_DAMAGE = 260;

    public CfPackItem(Properties props) {
        super(props);
    }

    public static boolean consumePellet(net.minecraft.world.item.ItemStack pack) {
        if (!(pack.getItem() instanceof CfPackItem) || pack.getDamageValue() >= MAX_DAMAGE - 1) {
            return false;
        }
        pack.setDamageValue(pack.getDamageValue() + 1);
        return true;
    }
}
