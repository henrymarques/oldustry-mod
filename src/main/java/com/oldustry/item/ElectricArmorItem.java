package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.energy.ElectricCharge;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.fml.ModList;

public class ElectricArmorItem extends ElectricItem {
    public ElectricArmorItem(ElectricCharge.Spec spec, Properties properties) {
        super(spec, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        if (ModList.get().isLoaded("curios")) {
            builder.accept(Component.translatable("info.oldustry.curios.back").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
