package com.oldustry.item;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Consumer;

import com.oldustry.block.FoamBlock;
import com.oldustry.compat.CuriosWearables;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModDataComponents;
import com.oldustry.registry.ModItems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SprayerItem extends Item {
    public static final int MAX = CfCharge.SPRAYER_MAX;
    /** Crafted empty. */
    public static final int CRAFTED = CfCharge.SPRAYER_CRAFTED;
    public static final int SPRAY_COST = CfCharge.SPRAY_COST;
    public static final int SPRAY_MASS = 13;

    public static int charge(ItemStack stack) {
        return Math.clamp(stack.getOrDefault(ModDataComponents.FOAM.get(), 0), 0, MAX);
    }

    public static void setCharge(ItemStack stack, int value) {
        int next = Math.clamp(value, 0, MAX);
        if (charge(stack) == next) {
            return;
        }
        stack.set(ModDataComponents.FOAM.get(), next);
    }

    /** JAR {@code RecipeGradual}: each pellet slot adds 100, clamped to max. */
    public static int addPellets(int charge, int pellets) {
        return CfCharge.addPellets(charge, pellets);
    }

    /** A spray from the tool itself needs at least 100 units. The pack bypasses this. */
    public static boolean canSprayFromSelf(int charge) {
        return CfCharge.canSprayFromSelf(charge);
    }

    public static int afterSpray(int charge) {
        return CfCharge.afterSpray(charge);
    }

    public SprayerItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) {
            return false;
        }
        ItemStack other = slot.getItem();
        if (!other.is(ModItems.CF_PELLET.get()) || charge(stack) >= MAX) {
            return false;
        }
        setCharge(stack, addPellets(charge(stack), 1));
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
        int held = charge(stack);
        if (!fromPack && !canSprayFromSelf(held)) {
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
            setCharge(stack, afterSpray(held));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * charge(stack) / (float) MAX);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fill = charge(stack) / (float) MAX;
        return fill < 0.15f ? 0xFF5555 : (fill < 0.5f ? 0xFFAA00 : 0x55FF55);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("info.oldustry.foam.stored", charge(stack), MAX).withStyle(ChatFormatting.GRAY));
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
