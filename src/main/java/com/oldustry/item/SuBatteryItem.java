package com.oldustry.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SuBatteryItem extends Item {
    public SuBatteryItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        int left = 1000;
        for (ItemStack inv : player.getInventory().getNonEquipmentItems()) {
            if (inv.getItem() instanceof ElectricItem electric) {
                int moved = electric.charge(inv, left);
                left -= moved;
                if (left <= 0) {
                    break;
                }
            }
        }
        if (left < 1000) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
