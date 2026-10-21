package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.registry.ModDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * CF pack. Foam lives in {@code oldustry:foam} (max 260, crafted at 0).
 * The canner adds 2 per pellet. One spray spends 1 and does not touch the sprayer.
 */
public class CfPackItem extends Item {
    public static final int MAX = CfCharge.PACK_MAX;
    /** Crafted empty. */
    public static final int CRAFTED = CfCharge.PACK_CRAFTED;

    public CfPackItem(Properties props) {
        super(props);
    }

    public static int charge(ItemStack stack) {
        return Math.clamp(stack.getOrDefault(ModDataComponents.FOAM.get(), 0), 0, MAX);
    }

    public static void setCharge(ItemStack stack, int value) {
        int next = Math.clamp(value, 0, MAX);
        if (charge(stack) == next) {
            return;
        }
        stack.set(ModDataComponents.FOAM.get(), next);
    }

    /** One spray. The last unit is spent too. */
    public static boolean canFeed(int charge) {
        return CfCharge.canFeed(charge);
    }

    public static int afterFeed(int charge) {
        return CfCharge.afterFeed(charge);
    }

    /** Canner mode 3: one pellet adds 2. */
    public static int afterPellet(int charge) {
        return CfCharge.afterPellet(charge);
    }

    /** Room for a pellet that adds 2 without passing 260. */
    public static boolean acceptsPellet(int charge) {
        return CfCharge.acceptsPellet(charge);
    }

    /** Eject when the tank is full, or when the pellets run out. */
    public static boolean leaveCanner(int charge, boolean pelletsRemain) {
        return CfCharge.leaveCanner(charge, pelletsRemain);
    }

    public static boolean consumePellet(ItemStack pack) {
        if (!(pack.getItem() instanceof CfPackItem) || !canFeed(charge(pack))) {
            return false;
        }
        setCharge(pack, afterFeed(charge(pack)));
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * charge(stack) / (float) MAX);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fill = charge(stack) / (float) MAX;
        return fill < 0.15f ? 0xFF5555 : (fill < 0.5f ? 0xFFAA00 : 0x55FF55);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("info.oldustry.foam.stored", charge(stack), MAX).withStyle(ChatFormatting.GRAY));
    }
}
