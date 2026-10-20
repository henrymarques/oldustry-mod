package com.oldustry.wrench;

import com.oldustry.network.KeysPayload;
import com.oldustry.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class WrenchHandler {
    public enum Result {
        PASS,
        ROTATE,
        REMOVE
    }

    public static InteractionResult use(UseOnContext context, boolean lossless) {
        return switch (apply(context, lossless)) {
            case PASS -> InteractionResult.PASS;
            case ROTATE, REMOVE -> InteractionResult.SUCCESS;
        };
    }

    /** What the wrench would do (no world mutation). */
    public static Result intend(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        Direction face = context.getClickedFace();
        if (player != null && KeysPayload.alt(player)) {
            face = nextFace(state, resolve(state, level.getBlockEntity(pos)));
        } else if (player != null && player.isShiftKeyDown()) {
            face = face.getOpposite();
        }
        Wrenchable wrenchable = resolve(state, level.getBlockEntity(pos));
        if (wrenchable != null && wrenchable.canSetFacing(state, face)) {
            return Result.ROTATE;
        }
        if (wrenchable != null && wrenchable.canRemove()) {
            return Result.REMOVE;
        }
        BlockState rotated = state.rotate(level, pos, Rotation.CLOCKWISE_90);
        return rotated != state ? Result.ROTATE : Result.PASS;
    }

    public static Result apply(UseOnContext context, boolean lossless) {
        Result intended = intend(context);
        if (intended == Result.PASS) {
            return Result.PASS;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        Direction face = context.getClickedFace();
        if (player != null && KeysPayload.alt(player)) {
            face = nextFace(state, resolve(state, level.getBlockEntity(pos)));
        } else if (player != null && player.isShiftKeyDown()) {
            face = face.getOpposite();
        }
        Wrenchable wrenchable = resolve(state, level.getBlockEntity(pos));
        if (intended == Result.ROTATE && wrenchable != null && wrenchable.canSetFacing(state, face)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, wrenchable.setFacing(state, face), Block.UPDATE_ALL);
                play(level, pos);
            }
            return Result.ROTATE;
        }
        if (intended == Result.REMOVE && wrenchable != null && wrenchable.canRemove()) {
            if (!level.isClientSide()) {
                float roll = lossless ? 0f : level.getRandom().nextFloat();
                boolean keep = lossless || WrenchLogic.dismantle(wrenchable.wrenchDropRate(), roll) == WrenchLogic.Drop.KEEP;
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof net.minecraft.world.Container container) {
                    Containers.dropContents(level, pos, container);
                    container.clearContent();
                }
                ItemStack drop = keep ? wrenchable.wrenchKeepDrop(state) : wrenchable.wrenchFailDrop(state);
                level.removeBlock(pos, false);
                Block.popResource(level, pos, drop);
                play(level, pos);
            }
            return Result.REMOVE;
        }
        if (intended == Result.ROTATE) {
            BlockState rotated = state.rotate(level, pos, Rotation.CLOCKWISE_90);
            if (rotated != state) {
                if (!level.isClientSide()) {
                    level.setBlock(pos, rotated, Block.UPDATE_ALL);
                    play(level, pos);
                }
                return Result.ROTATE;
            }
        }
        return Result.PASS;
    }

    private static Direction nextFace(BlockState state, Wrenchable wrenchable) {
        Direction current = state.hasProperty(com.oldustry.block.MachineBlock.FACING)
            ? state.getValue(com.oldustry.block.MachineBlock.FACING)
            : Direction.NORTH;
        Direction next = current;
        for (int i = 0; i < Direction.values().length; i++) {
            next = Direction.from3DDataValue((next.get3DDataValue() + 1) % 6);
            if (wrenchable == null || wrenchable.canSetFacing(state, next)) {
                return next;
            }
        }
        return current;
    }

    private static void play(Level level, BlockPos pos) {
        ModSounds.play(level, null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1f, 1f);
    }

    private static Wrenchable resolve(BlockState state, BlockEntity be) {
        if (be instanceof Wrenchable w) {
            return w;
        }
        if (state.getBlock() instanceof Wrenchable w) {
            return w;
        }
        return null;
    }

    private WrenchHandler() {}
}
