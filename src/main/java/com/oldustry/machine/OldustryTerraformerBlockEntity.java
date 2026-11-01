package com.oldustry.machine;

import com.oldustry.Oldustry;
import com.oldustry.item.TfbpItem;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** JAR {@code TileEntityTerra}. One blueprint slot, no GUI. HV input, 100k buffer. */
public final class OldustryTerraformerBlockEntity extends OldustryMachineBlockEntity {
    private OldustryTerraformer.Cursor cursor = OldustryTerraformer.Cursor.fresh();
    private int inactiveTicks;

    OldustryTerraformerBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.TERRAFORMER, pos, state);
    }

    public boolean hasBlueprint() {
        return !getItem(0).isEmpty();
    }

    /** Right-click. Drops the blueprint already in the slot, then takes this one. */
    public void insert(Player player, ItemStack stack) {
        if (level == null || level.isClientSide() || !(stack.getItem() instanceof TfbpItem tfbp)) {
            return;
        }
        ejectBlueprint();
        ItemStack one = stack.copyWithCount(1);
        setItem(0, one);
        stack.shrink(1);
        sync();
        if (tfbp.kind() == OldustryTfbp.CULTIVATION && player instanceof ServerPlayer server && level.dimension() == Level.END) {
            var holder = server.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "terraform_end_cultivation"));
            if (holder != null) {
                server.getAdvancements().award(holder, "end");
            }
        }
    }

    /** Wrench. True when a blueprint was dropped. */
    public boolean ejectBlueprint() {
        ItemStack stack = getItem(0);
        if (stack.isEmpty() || level == null) {
            return false;
        }
        Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack.copy());
        setItem(0, ItemStack.EMPTY);
        sync();
        return true;
    }

    boolean tickTerraformer(ServerLevel level, BlockPos pos) {
        boolean working = false;
        if (getItem(0).getItem() instanceof TfbpItem tfbp && energy >= tfbp.kind().consume) {
            working = true;
            OldustryTfbp blueprint = tfbp.kind();
            cursor = OldustryTerraformer.clampFailed(cursor);
            int range = OldustryTerraformer.scanRange(blueprint.range, cursor);
            int originX = cursor.lastY() > -1 ? cursor.lastX() : pos.getX();
            int originZ = cursor.lastY() > -1 ? cursor.lastZ() : pos.getZ();
            var random = level.getRandom();
            int x = OldustryTerraformer.roll(originX, range, random.nextInt(range + 1), random.nextInt(range + 1));
            int z = OldustryTerraformer.roll(originZ, range, random.nextInt(range + 1), random.nextInt(range + 1));
            boolean success = blueprint.apply(level, x, z, pos.getY());
            energy = OldustryTerraformer.spent(energy, blueprint.consume, success);
            cursor = OldustryTerraformer.after(cursor, success, x, z, pos.getY());
            setChanged();
        }
        int[] grace = OldustryTerraformer.grace(working, isActive(), inactiveTicks);
        inactiveTicks = grace[1];
        return grace[0] == 1;
    }

    private boolean isActive() {
        return getBlockState().getValue(com.oldustry.block.MachineBlock.ACTIVE);
    }

    private void sync() {
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
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("failedAttempts", cursor.failed());
        output.putInt("lastX", cursor.lastX());
        output.putInt("lastY", cursor.lastY());
        output.putInt("lastZ", cursor.lastZ());
        output.putInt("inactiveTicks", inactiveTicks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        cursor = new OldustryTerraformer.Cursor(
            input.getIntOr("failedAttempts", 0),
            input.getIntOr("lastX", -1),
            input.getIntOr("lastY", -1),
            input.getIntOr("lastZ", -1)
        );
        inactiveTicks = input.getIntOr("inactiveTicks", 0);
    }
}
