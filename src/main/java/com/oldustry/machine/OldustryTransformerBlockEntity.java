package com.oldustry.machine;

import com.oldustry.block.MachineBlock;
import com.oldustry.energy.EnergyNetLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

final class OldustryTransformerBlockEntity extends OldustryMachineBlockEntity {
    OldustryTransformerBlockEntity(OldustryMachineKind kind, BlockPos pos, BlockState state) {
        super(kind, pos, state);
    }

    private boolean powered() {
        return level != null && level.hasNeighborSignal(worldPosition);
    }

    @Override
    public EnergyNetLogic.Role energyRole() {
        return EnergyNetLogic.Role.BOTH;
    }

    @Override
    public boolean emits(Direction face) {
        Direction facing = getBlockState().getValue(MachineBlock.FACING);
        return powered() ? face == facing : face != facing;
    }

    @Override
    public boolean accepts(Direction face) {
        Direction facing = getBlockState().getValue(MachineBlock.FACING);
        return powered() ? face != facing : face == facing;
    }

    @Override
    public int offerEnergy() {
        int packet = powered() ? kind.highPacket() : kind.packet;
        return energy >= packet ? packet : 0;
    }

    @Override
    public int emitBursts() {
        return level == null || !powered() ? 4 : 1;
    }

    @Override
    public int maxSafeInput() {
        if (powered()) return kind.packet;
        int high = kind.highPacket();
        return high == 2048 ? Integer.MAX_VALUE : high;
    }
}
