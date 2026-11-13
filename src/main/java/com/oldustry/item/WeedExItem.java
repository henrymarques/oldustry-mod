package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** JAR Weed-EX: durability 64. Uses live in {@code weed_ex_uses}. */
public class WeedExItem extends Item {
    public static final int MAX_USES = 64;

    public WeedExItem(Properties props) {
        super(props);
    }

    public static int uses(ItemStack stack) {
        return Math.clamp(stack.getOrDefault(OldustryDataComponents.WEED_EX_USES.get(), MAX_USES), 0, MAX_USES);
    }

    public static void setUses(ItemStack stack, int value) {
        stack.set(OldustryDataComponents.WEED_EX_USES.get(), Math.clamp(value, 0, MAX_USES));
    }

    /** One JAR {@code InvSlot.damage(1)}. Returns false when empty. */
    public static boolean spend(ItemStack stack) {
        if (!(stack.getItem() instanceof WeedExItem)) {
            return false;
        }
        int left = uses(stack);
        if (left <= 0) {
            return false;
        }
        setUses(stack, left - 1);
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return uses(stack) < MAX_USES;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * uses(stack) / (float) MAX_USES);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF55FF55;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.oldustry.weed_ex_uses", uses(stack), MAX_USES).withStyle(ChatFormatting.GRAY));
    }
}
