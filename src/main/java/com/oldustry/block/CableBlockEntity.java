package com.oldustry.block;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oldustry.energy.CableKind;
import com.oldustry.energy.EnergyNet;
import com.oldustry.energy.EnergyNetLogic;
import com.oldustry.energy.EnergyTile;
import com.oldustry.item.tool.ObscuratorSample;
import com.oldustry.item.tool.ObscuratorStats;
import com.oldustry.registry.OldustryBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
    /** JAR {@code retextureRef*} on hardened foam. Index is {@link Direction#get3DDataValue()}. */
    private final ObscuratorSample[] paint = new ObscuratorSample[6];

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
        List<PaintFace> stored = storedPaint();
        if (!stored.isEmpty()) {
            output.store("obscurator", PaintFace.CODEC.listOf(), stored);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        open = input.getBooleanOr("open", true);
        totalFlow = input.getLongOr("flow", 0);
        powered = input.getBooleanOr("active", false);
        java.util.Arrays.fill(paint, null);
        input.read("obscurator", PaintFace.CODEC.listOf()).ifPresent(list -> {
            for (PaintFace face : list) {
                if (face.side >= 0 && face.side < paint.length) {
                    paint[face.side] = face.sample;
                }
            }
        });
    }

    public ObscuratorSample face(Direction side) {
        return paint[side.get3DDataValue()];
    }

    public boolean hasPaint() {
        for (ObscuratorSample sample : paint) {
            if (sample != null) {
                return true;
            }
        }
        return false;
    }

    /** JAR {@code retexture}. False when the face already shows this sample, so EU is not spent. */
    public boolean paint(Direction side, ObscuratorSample sample) {
        BlockState state = getBlockState();
        int foam = state.hasProperty(CableBlock.FOAM) ? state.getValue(CableBlock.FOAM) : 0;
        if (!ObscuratorStats.canPrint(foam)) {
            return false;
        }
        int index = side.get3DDataValue();
        ObscuratorSample old = paint[index];
        if (!ObscuratorStats.paintChanges(old != null, sample.sameBlock(old), sample.sameState(old), sample.sameFace(old))) {
            return false;
        }
        paint[index] = sample;
        syncPaint();
        return true;
    }

    /** JAR clears the arrays when foam drops below 2 or the painter recolours foam. */
    public void clearPaint() {
        if (!hasPaint()) {
            return;
        }
        java.util.Arrays.fill(paint, null);
        syncPaint();
    }

    private void syncPaint() {
        setChanged();
        if (level instanceof ServerLevel server) {
            server.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private List<PaintFace> storedPaint() {
        List<PaintFace> stored = new ArrayList<>();
        for (int i = 0; i < paint.length; i++) {
            if (paint[i] != null) {
                stored.add(new PaintFace(i, paint[i]));
            }
        }
        return stored;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    private record PaintFace(int side, ObscuratorSample sample) {
        static final Codec<PaintFace> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("side").forGetter(PaintFace::side),
            ObscuratorSample.CODEC.fieldOf("sample").forGetter(PaintFace::sample)
        ).apply(instance, PaintFace::new));
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
