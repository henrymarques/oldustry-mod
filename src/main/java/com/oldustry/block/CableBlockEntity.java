package com.oldustry.block;

import com.oldustry.energy.CableKind;
import com.oldustry.energy.EnergyNet;
import com.oldustry.energy.EnergyNetLogic;
import com.oldustry.energy.EnergyTile;
import com.oldustry.registry.OldustryBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CableBlockEntity extends BlockEntity implements EnergyTile {
    private final CableKind kind;
    /** Splitter: conducting / in EnergyNet (JAR addedToEnergyNet). */
    private boolean open = true;
    private long totalFlow;
    private long lastSample = -1;
    private int ticker;
    /** Splitter: JAR ticksUntilNextUpdate (fires on first tick when 0). */
    private int ticksUntilNextUpdate;
    private boolean powered;
    private boolean netted;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        this(kindOf(state), pos, state);
    }

    public CableBlockEntity(CableKind kind, BlockPos pos, BlockState state) {
        super(OldustryBlockEntities.CABLE.get(), pos, state);
        this.kind = kind;
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, CableBlockEntity be) {
        EnergyNet.of(level).tick(level);
        if (be.kind == CableKind.SPLITTER) {
            be.tickSplitter(level, pos, state);
            return;
        }
        be.ensureNet(level);
        if (be.kind != CableKind.DETECTOR) {
            return;
        }
        // JAR TileEntityCableDetector: ticker++ % 20 == 0 (fires on first tick).
        if (be.ticker++ % 20 != 0) {
            return;
        }
        boolean next = be.lastSample >= 0 && be.totalFlow > be.lastSample;
        be.lastSample = be.totalFlow;
        if (next != be.powered) {
            be.powered = next;
            be.setActive(level, pos, next);
            level.updateNeighborsAt(pos, state.getBlock());
        }
    }

    /** JAR TileEntityCableSplitter: every 20t toggle EnergyNet membership vs RS, setActive(inNet). */
    private void tickSplitter(ServerLevel level, BlockPos pos, BlockState state) {
        // JAR onLoaded always loads into the net once; do not re-add after an RS unload.
        if (!netted && open) {
            ensureNet(level);
        }
        if (ticksUntilNextUpdate == 0) {
            ticksUntilNextUpdate = 20;
            boolean signal = level.hasNeighborSignal(pos);
            if (signal == netted) {
                if (netted) {
                    EnergyNet.of(level).remove(pos);
                    netted = false;
                    open = false;
                } else {
                    EnergyNet.of(level).add(this);
                    netted = true;
                    open = true;
                }
                setChanged();
            }
            setActive(level, pos, netted);
        }
        ticksUntilNextUpdate--;
    }

    private void setActive(ServerLevel level, BlockPos pos, boolean active) {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(CableBlock.ACTIVE) && state.getValue(CableBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(CableBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }

    public int redstone() {
        return powered ? 15 : 0;
    }

    private void ensureNet(ServerLevel level) {
        if (!netted) {
            EnergyNet.of(level).add(this);
            netted = true;
            open = true;
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("open", open);
        output.putLong("flow", totalFlow);
        output.putBoolean("active", powered);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        open = input.getBooleanOr("open", true);
        totalFlow = input.getLongOr("flow", 0);
        powered = input.getBooleanOr("active", false);
    }

    @Override
    public BlockPos energyPos() {
        return worldPosition;
    }

    @Override
    public EnergyNetLogic.Role energyRole() {
        return EnergyNetLogic.Role.WIRE;
    }

    @Override
    public boolean conducts() {
        return true;
    }

    @Override
    public double cableLoss() {
        return kind.loss;
    }

    @Override
    public int meltAt() {
        return kind.meltAt;
    }

    @Override
    public int color() {
        return getBlockState().hasProperty(CableBlock.COLOR) ? getBlockState().getValue(CableBlock.COLOR) : 0;
    }

    @Override
    public boolean foamed() {
        return getBlockState().hasProperty(CableBlock.FOAM) && getBlockState().getValue(CableBlock.FOAM) > 0;
    }

    @Override
    public boolean open() {
        return kind != CableKind.SPLITTER || open;
    }

    @Override
    public int insulation() {
        return foamed() ? 9001 : kind.insulation;
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

    @Override
    public long totalEmitted() {
        return totalFlow;
    }

    @Override
    public long totalSunken() {
        return totalFlow;
    }

    @Override
    public void onFlow(int amount) {
        totalFlow += amount;
        setChanged();
    }

    @Override
    public void melt(ServerLevel level) {
        level.removeBlock(worldPosition, false);
    }

    private static CableKind kindOf(BlockState state) {
        return state.getBlock() instanceof CableBlock cable ? cable.kind : CableKind.COPPER;
    }
}
