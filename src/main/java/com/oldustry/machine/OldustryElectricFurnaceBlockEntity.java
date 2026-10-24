package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class OldustryElectricFurnaceBlockEntity extends OldustryProcessMachineBlockEntity {
    public OldustryElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.ELECTRIC_FURNACE, pos, state);
    }

    @Override
    protected ItemStack processResult(ServerLevel level, ItemStack in) {
        return in.isEmpty() ? ItemStack.EMPTY : smelt(level, in);
    }

    @Override
    protected boolean playsInterruptSound() {
        return false;
    }
}
