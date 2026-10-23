package com.oldustry.block;

import java.util.UUID;

import com.oldustry.registry.ModBlockEntities;
import com.oldustry.registry.ModBlocks;
import com.oldustry.wrench.Wrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jspecify.annotations.Nullable;

/**
 * 54 slots, no hopper face. The block entity is not a {@link Container}: breaking it must not spill the contents,
 * and hoppers must not see the slots ({@code InvSlot.Access.NONE}).
 */
public class PersonalSafeBlockEntity extends BlockEntity implements MenuProvider, Wrenchable {
    public static final int SLOTS = 54;
    private static final double REACH = 64.0;

    private final Contents contents = new Contents();
    private int ticksSinceSync;
    private int openCount;
    private float lid;
    private float prevLid;
    private @Nullable UUID owner;
    private String ownerName = "";

    public PersonalSafeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PERSONAL_SAFE.get(), pos, state);
    }

    public Container contents() {
        return contents;
    }

    public float lid(float partialTick) {
        return prevLid + (lid - prevLid) * partialTick;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PersonalSafeBlockEntity safe) {
        if (PersonalSafeAccess.syncViewers(++safe.ticksSinceSync) && !level.isClientSide()) {
            level.blockEvent(pos, state.getBlock(), 1, safe.openCount);
        }
        safe.prevLid = safe.lid;
        if (safe.openCount > 0 && safe.lid == 0.0F && !level.isClientSide()) {
            play(level, pos, true);
        }
        if (safe.openCount == 0 && safe.lid > 0.0F || safe.openCount > 0 && safe.lid < 1.0F) {
            float before = safe.lid;
            safe.lid += safe.openCount > 0 ? 0.1F : -0.1F;
            if (safe.lid > 1.0F) {
                safe.lid = 1.0F;
            }
            if (safe.lid < 0.0F) {
                safe.lid = 0.0F;
            }
            if (before >= 0.5F && safe.lid < 0.5F && !level.isClientSide()) {
                play(level, pos, false);
            }
        }
    }

    private static void play(Level level, BlockPos pos, boolean open) {
        float pitch = level.getRandom().nextFloat() * 0.1F + 0.9F;
        level.playSound(null, pos, open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, pitch);
    }

    /** The first open claims the safe. Anyone else is told who owns it. */
    public boolean tryOpen(Player player) {
        PersonalSafeAccess.Verdict verdict = verdict(player);
        if (verdict == PersonalSafeAccess.Verdict.DENY) {
            player.sendSystemMessage(Component.translatable("info.oldustry.safe.owned", ownerLabel()));
            return false;
        }
        if (verdict == PersonalSafeAccess.Verdict.CLAIM) {
            claim(player);
        }
        return true;
    }

    @Override
    public boolean canRemove() {
        return false;
    }

    @Override
    public boolean canRemove(Player player) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }
        PersonalSafeAccess.Verdict verdict = verdict(player);
        if (verdict == PersonalSafeAccess.Verdict.DENY) {
            return false;
        }
        if (verdict == PersonalSafeAccess.Verdict.CLAIM) {
            claim(player);
        }
        if (!contents.isEmpty()) {
            player.sendSystemMessage(Component.translatable("info.oldustry.safe.wrench_full"));
            return false;
        }
        return true;
    }

    @Override
    public ItemStack wrenchKeepDrop(BlockState state) {
        return new ItemStack(ModBlocks.PERSONAL_SAFE.get());
    }

    @Override
    public ItemStack wrenchFailDrop(BlockState state) {
        return wrenchKeepDrop(state);
    }

    private PersonalSafeAccess.Verdict verdict(Player player) {
        return PersonalSafeAccess.open(owner, player.getUUID(), privileged(player));
    }

    private static boolean privileged(Player player) {
        var server = player.level().getServer();
        if (server == null) {
            return false;
        }
        var id = player.nameAndId();
        return server.getPlayerList().isOp(id) || server.isSingleplayerOwner(id);
    }

    private void claim(Player player) {
        owner = player.getUUID();
        ownerName = player.getPlainTextName();
        setChanged();
    }

    private String ownerLabel() {
        if (!ownerName.isEmpty()) {
            return ownerName;
        }
        return owner == null ? "" : owner.toString();
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            openCount = type;
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.PERSONAL_SAFE.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new PersonalSafeMenu(id, inventory, contents);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, net.minecraft.network.RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, contents.getItems());
        output.storeNullable("owner", UUIDUtil.CODEC, owner);
        output.putString("ownerName", ownerName);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        contents.clearContent();
        ContainerHelper.loadAllItems(input, contents.getItems());
        owner = input.read("owner", UUIDUtil.CODEC).orElse(null);
        ownerName = input.getStringOr("ownerName", "");
    }

    private void onOpen(ContainerUser user) {
        if (level == null || level.isClientSide()) {
            return;
        }
        openCount++;
        level.blockEvent(worldPosition, getBlockState().getBlock(), 1, openCount);
    }

    private void onClose(ContainerUser user) {
        if (level == null || level.isClientSide()) {
            return;
        }
        openCount = Math.max(0, openCount - 1);
        level.blockEvent(worldPosition, getBlockState().getBlock(), 1, openCount);
    }

    private boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= REACH * REACH;
    }

    private final class Contents extends SimpleContainer {
        private Contents() {
            super(SLOTS);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            PersonalSafeBlockEntity.this.setChanged();
        }

        @Override
        public void startOpen(ContainerUser user) {
            onOpen(user);
        }

        @Override
        public void stopOpen(ContainerUser user) {
            onClose(user);
        }

        @Override
        public boolean stillValid(Player player) {
            return PersonalSafeBlockEntity.this.stillValid(player);
        }
    }
}
