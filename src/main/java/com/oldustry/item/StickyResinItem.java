package com.oldustry.item;

import com.oldustry.registry.ModBlocks;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StickyResinItem extends Item {
    public StickyResinItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPlaceContext place = new BlockPlaceContext(context);
        BlockState state = ModBlocks.RESIN_SHEET.get().getStateForPlacement(place);
        if (state == null || !place.canPlace()) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide()) {
            context.getLevel().setBlock(place.getClickedPos(), state, Block.UPDATE_ALL);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
