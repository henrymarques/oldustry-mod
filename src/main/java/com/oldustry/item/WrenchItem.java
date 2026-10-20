package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.wrench.WrenchHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("info.oldustry.wrench.hint.rotate").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("info.oldustry.wrench.hint.sneak").withStyle(ChatFormatting.DARK_GRAY));
        builder.accept(Component.translatable("info.oldustry.wrench.hint.alt").withStyle(ChatFormatting.DARK_GRAY));
        builder.accept(Component.translatable("info.oldustry.wrench.hint.remove").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        WrenchHandler.Result result = WrenchHandler.apply(context, false);
        if (result != WrenchHandler.Result.PASS && !context.getLevel().isClientSide()) {
            Player player = context.getPlayer();
            if (player != null) {
                int damage = result == WrenchHandler.Result.REMOVE ? 10 : 1;
                context.getItemInHand().hurtAndBreak(damage, player, context.getHand());
            }
        }
        return result == WrenchHandler.Result.PASS ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }
}
