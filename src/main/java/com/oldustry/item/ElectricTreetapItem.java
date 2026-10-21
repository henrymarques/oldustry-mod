package com.oldustry.item;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.registry.ModBlocks;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricTreetapItem extends ElectricItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(10000, 100, 1, true);

    public ElectricTreetapItem(Properties properties) {
        super(SPEC, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (!canPay(stack, 50, context.getPlayer())) {
            return InteractionResult.FAIL;
        }
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (!state.is(ModBlocks.RUBBER_LOG.get())) {
            return InteractionResult.PASS;
        }
        InteractionResult result = state.useItemOn(new net.minecraft.world.item.ItemStack(com.oldustry.registry.ModItems.TREETAP.get()), context.getLevel(), context.getPlayer(), context.getHand(), new net.minecraft.world.phys.BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false));
        if (result.consumesAction() && !context.getLevel().isClientSide()) {
            pay(stack, 50, context.getPlayer());
        }
        return result;
    }
}
