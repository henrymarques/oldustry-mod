package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class OldustryCompressorBlockEntity extends OldustryProcessMachineBlockEntity {
    public OldustryCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.COMPRESSOR, pos, state);
    }

    @Override
    protected ItemStack processResult(ServerLevel level, ItemStack in) {
        return in.isEmpty() ? ItemStack.EMPTY : OldustryProcessingRecipes.compressor(in.getItem());
    }
}
