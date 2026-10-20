package com.oldustry.item;

import com.oldustry.registry.ModItems;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.Level;

public class FilledTinCanItem extends Item {
    /** JAR ItemTinCan.getMaxItemUseDuration = 20 ticks (vanilla food default is 32 / 1.6s). */
    private static final float EAT_SECONDS = 1.0f;

    public FilledTinCanItem(Properties properties) {
        // IC2 experimental wiki: 1 hunger + 3.2 saturation (JAR was 2 / 0.95).
        super(properties.food(
            new FoodProperties.Builder().nutrition(1).saturationModifier(1.6f).alwaysEdible().build(),
            Consumables.defaultFood().consumeSeconds(EAT_SECONDS).build()
        ));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack leftover = super.finishUsingItem(stack, level, entity);
        ItemStack empty = new ItemStack(ModItems.TIN_CAN.get());
        if (leftover.isEmpty()) {
            return empty;
        }
        if (entity instanceof Player player && !player.addItem(empty)) {
            player.drop(empty, false);
        }
        return leftover;
    }
}
