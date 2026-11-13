package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** JAR {@code ItemGradual} hydrating cell. Tank is {@code hydration}, full = 10000. */
public class HydratingCellItem extends Item {
    public static final int MAX = 10000;

    public HydratingCellItem(Properties props) {
        super(props);
    }

    public static int remaining(ItemStack stack) {
        return Math.clamp(stack.getOrDefault(OldustryDataComponents.HYDRATION.get(), MAX), 0, MAX);
    }

    public static void setRemaining(ItemStack stack, int value) {
        stack.set(OldustryDataComponents.HYDRATION.get(), Math.clamp(value, 0, MAX));
    }

    /** Spend up to {@code want} units. Returns how many were taken. Empty cell is cleared by the caller. */
    public static int consume(ItemStack stack, int want) {
        if (want <= 0 || !(stack.getItem() instanceof HydratingCellItem)) {
            return 0;
        }
        int have = remaining(stack);
        int take = Math.min(want, have);
        setRemaining(stack, have - take);
        return take;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return remaining(stack) < MAX;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * remaining(stack) / (float) MAX);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF3A8CFF;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.oldustry.hydration", remaining(stack), MAX).withStyle(ChatFormatting.GRAY));
    }
}
