package com.oldustry.machine;

import com.oldustry.block.IronFenceBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

final class OldustryMagnetizerBlockEntity extends OldustryMachineBlockEntity {
    private int idleTicks;
    OldustryMagnetizerBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.MAGNETIZER, pos, state);
    }

    boolean tickMagnetizer(ServerLevel level, BlockPos pos) {
        if (idleTicks-- > 0) {
            return energy > 0;
        }
        boolean change = false;
        change |= magnetizeColumn(level, pos, -1);
        change |= magnetizeColumn(level, pos, 1);
        if (!change) {
            idleTicks = 10;
        }
        return change || energy > 0;
    }

    private boolean magnetizeColumn(ServerLevel level, BlockPos origin, int dyStep) {
        boolean change = false;
        int y = origin.getY() + dyStep;
        int limit = dyStep < 0 ? Math.max(level.getMinY(), origin.getY() - 20) : Math.min(level.getMaxY(), origin.getY() + 20);
        while ((dyStep < 0 ? y >= limit : y <= limit) && energy > 0) {
            BlockPos at = new BlockPos(origin.getX(), y, origin.getZ());
            BlockState state = level.getBlockState(at);
            if (!(state.getBlock() instanceof IronFenceBlock) || !state.hasProperty(IronFenceBlock.CHARGE)) {
                break;
            }
            int need = 15 - state.getValue(IronFenceBlock.CHARGE);
            if (need > 0) {
                change = true;
                need = Math.min(need, energy);
                level.setBlock(at, state.setValue(IronFenceBlock.CHARGE, state.getValue(IronFenceBlock.CHARGE) + need), Block.UPDATE_CLIENTS);
                energy -= need;
                setChanged();
            }
            y += dyStep;
        }
        return change;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("burn", idleTicks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        idleTicks = input.getIntOr("burn", 0);
    }
}
