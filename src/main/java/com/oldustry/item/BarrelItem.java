package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.block.OldustryBarrelBlockEntity;
import com.oldustry.block.ScaffoldBlock;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

/** JAR {@code ItemBarrel}. Places on wooden scaffold with strength &lt; 5. */
public class BarrelItem extends Item {
    public BarrelItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        // JAR: wooden scaffold meta < 5 (not reinforced, not iron).
        if (!(state.getBlock() instanceof ScaffoldBlock) || state.getValue(ScaffoldBlock.STRENGTH) >= 5) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        context.getLevel().setBlock(context.getClickedPos(), OldustryBlocks.BARREL.get().defaultBlockState(), 3);
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof OldustryBarrelBlockEntity barrel) {
            BoozeContents booze = context.getItemInHand().getOrDefault(OldustryDataComponents.BOOZE.get(), BoozeContents.EMPTY);
            if (!booze.isEmpty()) {
                barrel.loadFrom(booze);
            }
        }
        if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getName(ItemStack stack) {
        BoozeContents booze = stack.getOrDefault(OldustryDataComponents.BOOZE.get(), BoozeContents.EMPTY);
        if (booze.amount() > 0) {
            return Component.translatable("item.oldustry.barrel.filled", booze.amount());
        }
        return Component.translatable("item.oldustry.barrel");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        BoozeContents booze = stack.getOrDefault(OldustryDataComponents.BOOZE.get(), BoozeContents.EMPTY);
        if (!booze.isEmpty()) {
            tooltip.accept(Component.translatable("tooltip.oldustry.booze_amount", booze.amount()));
        }
    }
}
