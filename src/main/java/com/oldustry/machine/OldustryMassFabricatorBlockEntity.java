package com.oldustry.machine;

import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class OldustryMassFabricatorBlockEntity extends OldustryMachineBlockEntity {
    private static final int AMPLIFIER = 0;
    private static final int OUTPUT = 1;
    private static final int BATTERY = 2;

    private int scrap;
    private boolean scrapOn;

    public OldustryMassFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.MASS_FABRICATOR, pos, state);
    }

    public int scrap() {
        return scrap;
    }

    public boolean tickMass(ServerLevel level, BlockPos pos) {
        if (energy < capacity()) {
            dischargeSlot(BATTERY);
        }
        boolean redstone = level.hasNeighborSignal(pos);
        if (!OldustryMatter.running(energy, redstone)) {
            noteScrap();
            return false;
        }
        pullAmplifier();
        ItemStack matter = new ItemStack(OldustryItems.UU_MATTER.get());
        int next = OldustryMatter.produce(energy, canOutput(items.get(OUTPUT), matter));
        if (next != energy) {
            energy = next;
            mergeOut(OUTPUT, matter);
        }
        noteScrap();
        return true;
    }

    private void pullAmplifier() {
        if (scrap >= OldustryMatter.PULL_AT) {
            return;
        }
        ItemStack stack = items.get(AMPLIFIER);
        int gain = stack.is(OldustryItems.SCRAP_BOX.get()) ? OldustryMatter.SCRAP_BOX
            : stack.is(OldustryItems.SCRAP.get()) ? OldustryMatter.SCRAP : 0;
        if (gain <= 0) {
            return;
        }
        scrap += gain;
        stack.shrink(1);
        if (stack.isEmpty()) {
            items.set(AMPLIFIER, ItemStack.EMPTY);
        }
    }

    @Override
    public int demand() {
        if (level != null && level.hasNeighborSignal(worldPosition)) {
            return 0;
        }
        return Math.max(0, capacity() - energy);
    }

    @Override
    public int acceptEnergy(int amount) {
        if (amount > maxSafeInput()) {
            if (level instanceof ServerLevel server) {
                explode(server);
            }
            return 0;
        }
        boolean redstone = level != null && level.hasNeighborSignal(worldPosition);
        int[] next = OldustryMatter.inject(energy, scrap, amount, redstone);
        energy = next[0];
        scrap = next[1];
        noteScrap();
        setChanged();
        return next[2];
    }

    @Override
    public void explode(ServerLevel level) {
        BlockPos pos = worldPosition;
        level.removeBlock(pos, false);
        level.explode(
            null,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            OldustryMatter.EXPLOSION,
            false,
            Level.ExplosionInteraction.NONE
        );
    }

    private void noteScrap() {
        boolean on = scrap > 0;
        if (on == scrapOn) {
            return;
        }
        scrapOn = on;
        setChanged();
        if (level instanceof ServerLevel server) {
            server.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("scrap", scrap);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        scrap = input.getIntOr("scrap", 0);
        scrapOn = scrap > 0;
    }
}
