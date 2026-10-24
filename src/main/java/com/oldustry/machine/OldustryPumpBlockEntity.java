package com.oldustry.machine;

import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class OldustryPumpBlockEntity extends OldustryMachineBlockEntity {
    private int pumpCharge;

    public OldustryPumpBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.PUMP, pos, state);
    }

    // ──────────────── PUMP (IC2 TileEntityPump) ────────────────
    // Charge 0..200. Prefer the adjacent miner's pending source, then adjacent sources.

    static final int CHARGE_TARGET = 200;

    boolean tickPump(ServerLevel level, BlockPos pos) {
        dischargeSlot(2);
        // Older builds parked the filled cell in a slot the GUI does not draw.
        ItemStack stuck = items.get(1);
        if (!stuck.isEmpty()) {
            ejectPumpItem(level, pos, stuck);
            items.set(1, ItemStack.EMPTY);
        }
        if (pumpCharge < CHARGE_TARGET) {
            if (energy <= 0) {
                return false;
            }
            int add = Math.min(energy, 128);
            energy -= add;
            pumpCharge += add;
            return true;
        }

        BlockPos fluidPos = null;
        OldustryMinerBlockEntity linkedMiner = findAdjacentMiner(level, pos);
        if (linkedMiner != null && linkedMiner.pendingLiquidSource() != null) {
            fluidPos = linkedMiner.pendingLiquidSource();
        } else if (linkedMiner == null) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (isPumpableSource(level, neighbor)) {
                    fluidPos = neighbor;
                    break;
                }
            }
        }
        if (fluidPos == null || !isPumpableSource(level, fluidPos)) {
            return false;
        }

        var fluid = level.getBlockState(fluidPos).getFluidState();
        ItemStack container = items.get(0);
        ItemStack filled = filledContainer(container, fluid);
        if (!filled.isEmpty()) {
            ejectPumpItem(level, pos, filled);
            container.shrink(1);
            if (container.isEmpty()) {
                items.set(0, ItemStack.EMPTY);
            }
        } else if (!container.isEmpty() || !takeAdjacentBucket(level, pos, fluid)) {
            return false;
        }

        // JAR clearLastBlock is setBlockToAir. Level.removeBlock puts the fluid's own block back.
        level.setBlock(fluidPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        if (linkedMiner != null) {
            linkedMiner.consumeLiquidSource(fluidPos);
        }
        pumpCharge -= CHARGE_TARGET;
        return true;
    }

    /** JAR {@code LiquidContainerRegistry.fillLiquidContainer}: cell or bucket in the pump slot. */
    private static ItemStack filledContainer(ItemStack container, net.minecraft.world.level.material.FluidState fluid) {
        boolean water = fluid.is(net.minecraft.tags.FluidTags.WATER);
        boolean lava = fluid.is(net.minecraft.tags.FluidTags.LAVA);
        if (container.is(OldustryItems.EMPTY_CELL.get())) {
            if (water) {
                return new ItemStack(OldustryItems.WATER_CELL.get());
            }
            if (lava) {
                return new ItemStack(OldustryItems.LAVA_CELL.get());
            }
        }
        if (container.is(Items.BUCKET)) {
            if (water) {
                return new ItemStack(Items.WATER_BUCKET);
            }
            if (lava) {
                return new ItemStack(Items.LAVA_BUCKET);
            }
        }
        return ItemStack.EMPTY;
    }

    /** JAR {@code putInChestBucket}: one empty bucket in an adjacent chest. */
    private boolean takeAdjacentBucket(ServerLevel level, BlockPos pos, net.minecraft.world.level.material.FluidState fluid) {
        ItemStack filled = filledContainer(new ItemStack(Items.BUCKET), fluid);
        if (filled.isEmpty()) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
            if (!(neighbor instanceof Container container) || neighbor instanceof OldustryMachineBlockEntity) {
                continue;
            }
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.is(Items.BUCKET)) {
                    continue;
                }
                stack.shrink(1);
                if (stack.isEmpty()) {
                    container.setItem(i, filled.copy());
                } else if (!mergeInto(container, filled)) {
                    Block.popResource(level, pos, filled);
                }
                container.setChanged();
                return true;
            }
        }
        return false;
    }

    private static boolean mergeInto(Container container, ItemStack filled) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                container.setItem(i, filled.copy());
                return true;
            }
            if (ItemStack.isSameItemSameComponents(slot, filled) && slot.getCount() < slot.getMaxStackSize()) {
                slot.grow(1);
                return true;
            }
        }
        return false;
    }

    /** JAR {@code distributeDrop}: adjacent inventories, then the ground. Skips our own machines. */
    private void ejectPumpItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        ItemStack left = stack.copy();
        for (Direction dir : Direction.values()) {
            if (left.isEmpty()) {
                return;
            }
            BlockPos neighbor = pos.relative(dir);
            if (level.getBlockEntity(neighbor) instanceof OldustryMachineBlockEntity) {
                continue;
            }
            int moved = OldustryMachineTransfer.insert(level, neighbor, dir.getOpposite(), left, left.getCount());
            left.shrink(moved);
        }
        if (!left.isEmpty()) {
            Block.popResource(level, pos, left);
        }
    }

    private static boolean isPumpableSource(ServerLevel level, BlockPos pos) {
        var fluid = level.getBlockState(pos).getFluidState();
        return !fluid.isEmpty() && fluid.isSource()
            && (fluid.is(net.minecraft.tags.FluidTags.WATER) || fluid.is(net.minecraft.tags.FluidTags.LAVA));
    }

    private static OldustryMinerBlockEntity findAdjacentMiner(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) {
                continue; // IC2 skips YP when searching
            }
            BlockEntity te = level.getBlockEntity(pos.relative(dir));
            if (te instanceof OldustryMinerBlockEntity miner) {
                return miner;
            }
        }
        return null;
    }

    int pumpCharge() {
        return pumpCharge;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("lava", pumpCharge);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        pumpCharge = input.getIntOr("lava", 0);
    }

}
