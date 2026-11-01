package com.oldustry.block;

import com.oldustry.energy.EnergyNet;
import com.oldustry.energy.EnergyNetLogic;
import com.oldustry.energy.EnergyTile;
import com.oldustry.registry.OldustryBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * IC2 {@code TileEntityReactorChamberElectric}. No inventory of its own.
 * The reactor pushes whatever EU it could not send, and this face tries that packet.
 */
public final class OldustryReactorChamberBlockEntity extends BlockEntity implements EnergyTile {
    private boolean netted;

    public OldustryReactorChamberBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryBlockEntities.REACTOR_CHAMBER.get(), pos, state);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, OldustryReactorChamberBlockEntity chamber) {
        if (!chamber.netted) {
            EnergyNet.of(level).add(chamber);
            chamber.netted = true;
        }
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel server && netted) {
            EnergyNet.of(server).remove(worldPosition);
            netted = false;
        }
        super.setRemoved();
    }

    @Override
    public BlockPos energyPos() {
        return worldPosition;
    }

    @Override
    public EnergyNetLogic.Role energyRole() {
        return EnergyNetLogic.Role.SOURCE;
    }

    @Override
    public boolean emits(Direction face) {
        return true;
    }

    @Override
    public boolean accepts(Direction face) {
        return false;
    }

    @Override
    public int demand() {
        return 0;
    }

    @Override
    public int acceptEnergy(int amount) {
        return amount;
    }

    @Override
    public int offerEnergy() {
        return 0;
    }

    @Override
    public void takeEnergy(int amount) {}
}
