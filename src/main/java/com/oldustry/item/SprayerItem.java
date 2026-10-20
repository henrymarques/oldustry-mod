package com.oldustry.item;

import java.util.ArrayList;
import java.util.List;

import com.oldustry.block.FoamBlock;
import com.oldustry.compat.CuriosWearables;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SprayerItem extends Item {
    public static final int MAX_DAMAGE = 1602;
    public static final int SPRAY_COST = 100;
    public static final int SPRAY_MASS = 13;

    public SprayerItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) {
            return false;
        }
        ItemStack other = slot.getItem();
        if (!other.is(ModItems.CF_PELLET.get()) || stack.getDamageValue() <= 0) {
            return false;
        }
        stack.setDamageValue(Math.max(0, stack.getDamageValue() - SPRAY_COST));
        other.shrink(1);
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        boolean fromPack = player != null && CfPackItem.consumePellet(
            CuriosWearables.chestOrCurios(player, ModItems.CF_PACK.get()));
        if (!fromPack && stack.getDamageValue() > MAX_DAMAGE - SPRAY_COST - 1) {
            return InteractionResult.FAIL;
        }
        BlockPos pos = context.getClickedPos();
        boolean[] dirs = directions(player);
        boolean scaffold = level.getBlockState(pos).is(ModBlocks.WOODEN_SCAFFOLD.get());
        if (!scaffold) {
            pos = pos.relative(context.getClickedFace());
        }
        if (!spray(level, pos, dirs, scaffold)) {
            return InteractionResult.FAIL;
        }
        if (!fromPack && player != null) {
            stack.hurtAndBreak(SPRAY_COST, player, context.getHand());
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean spray(Level level, BlockPos origin, boolean[] dirs, boolean scaffold) {
        if (scaffold && !level.getBlockState(origin).is(ModBlocks.WOODEN_SCAFFOLD.get())) {
            return false;
        }
        if (!scaffold && !FoamBlock.canFoamAt(level, origin)) {
            return false;
        }
        List<BlockPos> check = new ArrayList<>();
        List<BlockPos> place = new ArrayList<>();
        check.add(origin);
        int foam = SPRAY_MASS;
        for (int i = 0; i < check.size() && foam > 0; i++) {
            BlockPos at = check.get(i);
            BlockState state = level.getBlockState(at);
            boolean ok = scaffold
                ? state.is(ModBlocks.WOODEN_SCAFFOLD.get())
                : FoamBlock.canFoamAt(level, at);
            if (!ok) {
                continue;
            }
            if (!place.contains(at)) {
                place.add(at);
            }
            addAdjacent(at, check, dirs, scaffold);
            foam--;
        }
        if (!place.isEmpty()) {
            BlockState foamState = ModBlocks.CONSTRUCTION_FOAM.get().defaultBlockState();
            List<BlockPos> dropAt = new ArrayList<>();
            List<BlockState> dropState = new ArrayList<>();
            for (BlockPos at : place) {
                BlockState current = level.getBlockState(at);
                if (current.is(ModBlocks.WOODEN_SCAFFOLD.get())) {
                    dropAt.add(at);
                    dropState.add(current);
                }
            }
            for (BlockPos at : place) {
                level.setBlock(at, foamState, Block.UPDATE_CLIENTS);
            }
            for (int i = 0; i < dropAt.size(); i++) {
                Block.dropResources(dropState.get(i), level, dropAt.get(i), null);
            }
            for (BlockPos at : place) {
                level.updateNeighborsAt(at, foamState.getBlock());
            }
        }
        return !place.isEmpty();
    }

    private static void addAdjacent(BlockPos at, List<BlockPos> check, boolean[] dirs, boolean ignoreDirs) {
        for (Direction dir : shuffled()) {
            if (ignoreDirs || dirs[dir.get3DDataValue()]) {
                BlockPos next = at.relative(dir);
                if (!check.contains(next)) {
                    check.add(next);
                }
            }
        }
    }

    private static Direction[] shuffled() {
        Direction[] order = Direction.values();
        // ponytail: fixed order is enough; IC2 shuffled 16 times. Upgrade: RandomSource shuffle.
        return order;
    }

    static boolean[] directions(Player player) {
        boolean[] r = { true, true, true, true, true, true };
        if (player == null) {
            return r;
        }
        float yaw = Mth.wrapDegrees(player.getYRot());
        float pitch = player.getXRot();
        if (pitch >= -65 && pitch <= 65) {
            if (yaw >= -60 && yaw <= 60) {
                r[Direction.NORTH.get3DDataValue()] = false;
            }
            if (yaw >= 30 && yaw <= 150) {
                r[Direction.EAST.get3DDataValue()] = false;
            }
            if (yaw <= -120 || yaw >= 120) {
                r[Direction.SOUTH.get3DDataValue()] = false;
            }
            if (yaw >= -150 && yaw <= -30) {
                r[Direction.WEST.get3DDataValue()] = false;
            }
        }
        if (pitch <= -40) {
            r[Direction.DOWN.get3DDataValue()] = false;
        }
        if (pitch >= 40) {
            r[Direction.UP.get3DDataValue()] = false;
        }
        return r;
    }
}
