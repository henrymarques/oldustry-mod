package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class OldustryMaceratorBlockEntity extends OldustryProcessMachineBlockEntity {
    public OldustryMaceratorBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.MACERATOR, pos, state);
    }

    @Override
    protected ItemStack processResult(ServerLevel level, ItemStack in) {
        return in.isEmpty() ? ItemStack.EMPTY : OldustryProcessingRecipes.macerator(in.getItem());
    }
}
