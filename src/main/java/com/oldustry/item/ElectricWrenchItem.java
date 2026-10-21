package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.registry.ModDataComponents;
import com.oldustry.wrench.WrenchHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;

public class ElectricWrenchItem extends ElectricItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(12000, 250, 1, true);

    public ElectricWrenchItem(Properties properties) {
        super(SPEC, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        boolean lossless = stack.getOrDefault(ModDataComponents.LOSSLESS.get(), false);
        builder.accept(Component.translatable(lossless ? "info.oldustry.wrench.lossless.on" : "info.oldustry.wrench.lossless.off")
            .withStyle(lossless ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        builder.accept(Component.translatable("info.oldustry.wrench.hint.rotate").withStyle(ChatFormatting.DARK_GRAY));
        builder.accept(Component.translatable("info.oldustry.wrench.hint.mode").withStyle(ChatFormatting.DARK_GRAY));
        builder.accept(Component.translatable("info.oldustry.wrench.hint.remove").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        boolean lossless = context.getItemInHand().getOrDefault(ModDataComponents.LOSSLESS.get(), false);
        WrenchHandler.Result intended = WrenchHandler.intend(context);
        if (intended == WrenchHandler.Result.PASS) {
            return InteractionResult.PASS;
        }
        int cost = WrenchEu.eu(intended, lossless);
        ItemStack stack = context.getItemInHand();
        if (!canPay(stack, cost, context.getPlayer())) {
            return InteractionResult.FAIL;
        }
        WrenchHandler.Result result = WrenchHandler.apply(context, lossless);
        if (result != WrenchHandler.Result.PASS && !context.getLevel().isClientSide()) {
            pay(stack, cost, context.getPlayer());
        }
        return result == WrenchHandler.Result.PASS ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }
}
