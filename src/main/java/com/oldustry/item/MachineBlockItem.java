package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.block.MachineBlock;
import com.oldustry.machine.MachineKind;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/** Generator block items: show nominal EU/t on tooltip. */
public class MachineBlockItem extends BlockItem {
    public MachineBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        if (getBlock() instanceof MachineBlock machine) {
            Component line = generatorTooltip(machine.kind);
            if (line != null) {
                builder.accept(line);
            }
        }
        super.appendHoverText(stack, context, display, builder, flag);
    }

    private static Component generatorTooltip(MachineKind kind) {
        return switch (kind.type) {
            case GENERATOR -> Component.translatable("info.oldustry.generator.output", 10).withStyle(ChatFormatting.GRAY);
            case GEO -> Component.translatable("info.oldustry.generator.output", 20).withStyle(ChatFormatting.GRAY);
            case SOLAR -> Component.translatable("info.oldustry.generator.output", 1).withStyle(ChatFormatting.GRAY);
            case WATER -> Component.translatable("info.oldustry.generator.water").withStyle(ChatFormatting.GRAY);
            case WIND -> Component.translatable("info.oldustry.generator.wind").withStyle(ChatFormatting.GRAY);
            default -> null;
        };
    }
}
