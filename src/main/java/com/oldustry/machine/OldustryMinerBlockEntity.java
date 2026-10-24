package com.oldustry.machine;

import com.oldustry.OldustryConfig;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.tool.ScannerItem;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class OldustryMinerBlockEntity extends OldustryMachineBlockEntity {
    private boolean canProvideLiquid;
    private int liquidX;
    private int liquidY;
    private int liquidZ;
    private int scannedLevel = Integer.MIN_VALUE;
    private int scanRange;
    private int lastMineX;
    private int lastMineZ;
    private int lastMineMode;

    public OldustryMinerBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.MINER, pos, state);
    }

    private static final int MINER_AIR_EU = 3;
    private static final int MINER_AIR_TICKS = 20;
    private static final int MINER_DRILL_EU = 6;
    private static final int MINER_DRILL_TICKS = 20;
    private static final int MINER_DRILL_USE = 50;
    private static final int MINER_DDRILL_EU = 200;
    private static final int MINER_DDRILL_TICKS = 50;
    private static final int MINER_DDRILL_USE = 80;
    private static final int MODE_NONE = 0;
    private static final int MODE_WITHDRAW = 1;
    private static final int MODE_AIR = 2;
    private static final int MODE_DRILL = 3;
    private static final int MODE_DDRILL = 4;
    private static final int MINE_WORKING = 0;
    private static final int MINE_DONE = 1;
    private static final int MINE_FAIL_TEMP = 2;
    private static final int MINE_FAIL_PERM = 3;

    boolean tickMiner(ServerLevel level, BlockPos pos) {
        flushMinerBuffer(level, pos);
        feedMinerPower();

        ItemStack drill = items.get(0);
        boolean hasDrill = drill.is(OldustryItems.MINING_DRILL.get()) || drill.is(OldustryItems.DIAMOND_DRILL.get());
        int opY = minerOperationHeight(level, pos);

        if (!hasDrill) {
            return minerWithdraw(level, pos, opY);
        }
        if (opY < level.getMinY()) {
            return false;
        }

        BlockState atOp = level.getBlockState(new BlockPos(pos.getX(), opY, pos.getZ()));
        if (!atOp.is(OldustryBlocks.MINING_PIPE_TIP.get())) {
            return opY > level.getMinY() && minerDigDown(level, pos, opY, false);
        }

        int result = minerMineLevel(level, pos, opY);
        if (result == MINE_DONE) {
            return minerDigDown(level, pos, opY - 1, true);
        }
        return result == MINE_WORKING;
    }

    private int minerOperationHeight(ServerLevel level, BlockPos pos) {
        return OldustryMinerColumn.head(pos.getY(), level.getMinY(), y -> minerColumnCell(level, pos, y));
    }

    private static int minerColumnCell(ServerLevel level, BlockPos pos, int y) {
        BlockState state = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
        if (state.is(OldustryBlocks.MINING_PIPE.get())) {
            return OldustryMinerColumn.PIPE;
        }
        if (state.is(OldustryBlocks.MINING_PIPE_TIP.get())) {
            return OldustryMinerColumn.TIP;
        }
        return state.isAir() ? OldustryMinerColumn.AIR : OldustryMinerColumn.OTHER;
    }

    private boolean minerWithdraw(ServerLevel level, BlockPos pos, int y) {
        if (lastMineMode != MODE_WITHDRAW) {
            lastMineMode = MODE_WITHDRAW;
            progress = 0;
        }
        if (y < level.getMinY()
            || !level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).is(OldustryBlocks.MINING_PIPE_TIP.get())) {
            y++;
        }
        if (y >= pos.getY() || energy < MINER_AIR_EU) {
            return false;
        }
        if (progress < MINER_AIR_TICKS) {
            energy -= MINER_AIR_EU;
            progress++;
            return true;
        }
        progress = 0;
        BlockPos tipPos = new BlockPos(pos.getX(), y, pos.getZ());
        BlockState at = level.getBlockState(tipPos);
        if (at.is(OldustryBlocks.MINING_PIPE_TIP.get()) || at.is(OldustryBlocks.MINING_PIPE.get())) {
            level.removeBlock(tipPos, false);
            ejectDrop(level, pos, new ItemStack(OldustryItems.MINING_PIPE.get()));
            // JAR removePipe: a non-pipe block in the pipe slot is placed in the hole.
            placeMinerFiller(level, tipPos);
        }
        return true;
    }

    private boolean minerDigDown(ServerLevel level, BlockPos pos, int y, boolean removeTipAbove) {
        ItemStack pipeSupply = items.get(2);
        if (pipeSupply.isEmpty() || !pipeSupply.is(OldustryItems.MINING_PIPE.get())) {
            return false;
        }
        if (y < level.getMinY()) {
            if (removeTipAbove) {
                level.setBlock(
                    new BlockPos(pos.getX(), y + 1, pos.getZ()),
                    OldustryBlocks.MINING_PIPE.get().defaultBlockState(),
                    Block.UPDATE_CLIENTS
                );
            }
            return false;
        }
        int result = minerMineBlock(level, pos, pos.getX(), y, pos.getZ());
        if (result == MINE_FAIL_TEMP || result == MINE_FAIL_PERM) {
            if (removeTipAbove) {
                level.setBlock(
                    new BlockPos(pos.getX(), y + 1, pos.getZ()),
                    OldustryBlocks.MINING_PIPE.get().defaultBlockState(),
                    Block.UPDATE_CLIENTS
                );
            }
            return false;
        }
        if (result == MINE_DONE) {
            if (removeTipAbove) {
                level.setBlock(
                    new BlockPos(pos.getX(), y + 1, pos.getZ()),
                    OldustryBlocks.MINING_PIPE.get().defaultBlockState(),
                    Block.UPDATE_CLIENTS
                );
            }
            pipeSupply.shrink(1);
            level.setBlock(
                new BlockPos(pos.getX(), y, pos.getZ()),
                OldustryBlocks.MINING_PIPE_TIP.get().defaultBlockState(),
                Block.UPDATE_CLIENTS
            );
        }
        return true;
    }

    /** Lateral valuable-ore pass (IC2 mineLevel). No scanner → Done immediately. */
    private int minerMineLevel(ServerLevel level, BlockPos pos, int y) {
        ItemStack scanner = items.get(1);
        if (scanner.isEmpty() || !(scanner.getItem() instanceof ScannerItem scannerItem)) {
            return MINE_DONE;
        }
        if (scannedLevel != y) {
            scanRange = scannerItem.startLayerScan(scanner);
        }
        if (scanRange <= 0) {
            return MINE_FAIL_TEMP;
        }
        scannedLevel = y;
        for (int x = pos.getX() - scanRange; x <= pos.getX() + scanRange; x++) {
            for (int z = pos.getZ() - scanRange; z <= pos.getZ() + scanRange; z++) {
                BlockPos target = new BlockPos(x, y, z);
                BlockState state = level.getBlockState(target);
                if (!ScannerItem.isValuable(state) || !minerCanMine(level, pos, target, state)) {
                    continue;
                }
                int result = minerMineTowards(level, pos, x, y, z);
                if (result == MINE_DONE) {
                    return MINE_WORKING;
                }
                if (result != MINE_FAIL_PERM) {
                    return result;
                }
            }
        }
        return MINE_DONE;
    }

    private int minerMineTowards(ServerLevel level, BlockPos pos, int x, int y, int z) {
        int cx = pos.getX();
        int cz = pos.getZ();
        while (cx != x || cz != z) {
            boolean isCurrent = cx == lastMineX && cz == lastMineZ;
            if (Math.abs(x - cx) >= Math.abs(z - cz)) {
                cx += x > cx ? 1 : -1;
            } else {
                cz += z > cz ? 1 : -1;
            }
            BlockPos step = new BlockPos(cx, y, cz);
            BlockState state = level.getBlockState(step);
            boolean blocking = isCurrent;
            if (!blocking && !state.isAir()) {
                var fluid = state.getFluidState();
                if (fluid.isEmpty() || fluid.isSource()) {
                    blocking = true;
                }
            }
            if (blocking) {
                int result = minerMineBlock(level, pos, cx, y, cz);
                if (result == MINE_DONE) {
                    lastMineX = cx;
                    lastMineZ = cz;
                }
                return result;
            }
        }
        lastMineX = pos.getX();
        lastMineZ = pos.getZ();
        return MINE_DONE;
    }

    private int minerMineBlock(ServerLevel level, BlockPos minerPos, int x, int y, int z) {
        BlockPos target = new BlockPos(x, y, z);
        BlockState state = level.getBlockState(target);
        boolean isAir = state.isAir() || (state.getDestroySpeed(level, target) == 0 && state.getFluidState().isEmpty());

        if (!isAir) {
            var fluid = state.getFluidState();
            if (!fluid.isEmpty() && fluid.isSource()) {
                liquidX = x;
                liquidY = y;
                liquidZ = z;
                canProvideLiquid = true;
                return MINE_FAIL_PERM;
            }
            if (!minerCanMine(level, minerPos, target, state)) {
                return MINE_FAIL_PERM;
            }
        }

        canProvideLiquid = false;
        ItemStack drill = items.get(0);
        int mode;
        int euPerTick;
        int duration;
        if (isAir) {
            mode = MODE_AIR;
            euPerTick = MINER_AIR_EU;
            duration = MINER_AIR_TICKS;
        } else if (drill.is(OldustryItems.MINING_DRILL.get())) {
            mode = MODE_DRILL;
            euPerTick = MINER_DRILL_EU;
            duration = MINER_DRILL_TICKS;
        } else if (drill.is(OldustryItems.DIAMOND_DRILL.get())) {
            mode = MODE_DDRILL;
            euPerTick = MINER_DDRILL_EU;
            duration = MINER_DDRILL_TICKS;
        } else {
            return MINE_FAIL_TEMP;
        }

        if (lastMineMode != mode) {
            lastMineMode = mode;
            progress = 0;
        }

        if (progress < duration) {
            if (energy < euPerTick) {
                return MINE_FAIL_TEMP;
            }
            energy -= euPerTick;
            progress++;
            return MINE_WORKING;
        }

        if (isAir || minerHarvest(level, minerPos, target, state, drill)) {
            progress = 0;
            return MINE_DONE;
        }
        return MINE_FAIL_TEMP;
    }

    private boolean minerHarvest(ServerLevel level, BlockPos minerPos, BlockPos target, BlockState state, ItemStack drill) {
        int use = drill.is(OldustryItems.DIAMOND_DRILL.get()) ? MINER_DDRILL_USE : MINER_DRILL_USE;
        if (!(drill.getItem() instanceof ElectricItem electric) || ElectricItem.energy(drill) < use) {
            return false;
        }
        int hoist = 2 * (minerPos.getY() - target.getY());
        if (energy < hoist) {
            return false;
        }
        electric.spend(drill, use);
        energy -= hoist;
        java.util.List<ItemStack> drops = Block.getDrops(state, level, target, level.getBlockEntity(target));
        level.removeBlock(target, false);
        for (ItemStack drop : drops) {
            ejectDrop(level, minerPos, drop);
        }
        return true;
    }

    private boolean minerCanMine(ServerLevel level, BlockPos minerPos, BlockPos target, BlockState state) {
        if (state.isAir()) {
            return true;
        }
        if (state.is(OldustryBlocks.MINING_PIPE.get()) || state.is(OldustryBlocks.MINING_PIPE_TIP.get())) {
            return false;
        }
        float hardness = state.getDestroySpeed(level, target);
        if (hardness < 0) {
            return false;
        }
        ItemStack drill = items.get(0);
        return !drill.isEmpty() && drill.isCorrectToolForDrops(state);
    }

    /**
     * JAR {@code chargeTools} after {@code InvSlotDischarge.discharge(room, ignoreLimit=true)}.
     * Scanner first (tier 2, transfer 50), then drill (tier 1, transfer 100). The machine packet is not the cap.
     */
    private void feedMinerPower() {
        ItemStack battery = items.get(3);
        if (battery.getItem() instanceof ElectricItem electric) {
            int room = capacity() - energy;
            if (room > 0) {
                energy += electric.dischargeIgnoreTransfer(battery, room, inputTier());
            }
        } else {
            dischargeSlot(3);
        }
        chargeMinerTool(1, 2);
        chargeMinerTool(0, 1);
    }

    private void chargeMinerTool(int slot, int sourceTier) {
        ItemStack stack = items.get(slot);
        if (energy > 0 && stack.getItem() instanceof ElectricItem electric) {
            energy -= electric.chargeAtTier(stack, energy, sourceTier, false);
        }
    }

    /** Items left in the old hidden buffer (slots 4–6) from before drops were ejected. */
    private void flushMinerBuffer(ServerLevel level, BlockPos pos) {
        for (int slot = 4; slot <= 6; slot++) {
            ItemStack stuck = items.get(slot);
            if (!stuck.isEmpty()) {
                ejectDrop(level, pos, stuck);
                items.set(slot, ItemStack.EMPTY);
            }
        }
    }

    /** JAR {@code removePipe}: consume one filler block and place it where the pipe was. */
    private void placeMinerFiller(ServerLevel level, BlockPos hole) {
        ItemStack filler = items.get(OldustryMinerAccess.PIPE);
        if (filler.isEmpty() || filler.is(OldustryItems.MINING_PIPE.get()) || !(filler.getItem() instanceof net.minecraft.world.item.BlockItem blockItem)) {
            return;
        }
        ItemStack one = filler.copyWithCount(1);
        filler.shrink(1);
        if (filler.isEmpty()) {
            items.set(OldustryMinerAccess.PIPE, ItemStack.EMPTY);
        }
        blockItem.place(new net.minecraft.world.item.context.DirectionalPlaceContext(level, hole, Direction.DOWN, one, Direction.DOWN));
    }

    /** JAR {@code StackUtil.distributeDrop}: adjacent inventories, then an item entity at this block. */
    private void ejectDrop(ServerLevel level, BlockPos pos, ItemStack stack) {
        ItemStack left = stack.copy();
        for (Direction dir : Direction.values()) {
            if (left.isEmpty()) {
                return;
            }
            int moved = OldustryMachineTransfer.insert(level, pos.relative(dir), dir.getOpposite(), left, left.getCount());
            left.shrink(moved);
        }
        if (!left.isEmpty()) {
            Block.popResource(level, pos, left);
        }
    }

    BlockPos pendingLiquidSource() {
        return canProvideLiquid ? new BlockPos(liquidX, liquidY, liquidZ) : null;
    }

    void consumeLiquidSource(BlockPos source) {
        if (canProvideLiquid && source.equals(new BlockPos(liquidX, liquidY, liquidZ))) {
            canProvideLiquid = false;
            setChanged();
        }
    }

    int mineMode() {
        return lastMineMode;
    }

    int mineDuration() {
        return lastMineMode == MODE_DDRILL ? MINER_DDRILL_TICKS : MINER_AIR_TICKS;
    }

    @Override
    protected int inputTier() {
        return OldustryConfig.MINER_LAPOTRON.get() ? 3 : 1;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("canProvideLiquid", canProvideLiquid);
        output.putInt("liquidX", liquidX);
        output.putInt("liquidY", liquidY);
        output.putInt("liquidZ", liquidZ);
        output.putInt("lastMineMode", lastMineMode);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        canProvideLiquid = input.getBooleanOr("canProvideLiquid", false);
        liquidX = input.getIntOr("liquidX", 0);
        liquidY = input.getIntOr("liquidY", 0);
        liquidZ = input.getIntOr("liquidZ", 0);
        lastMineMode = input.getIntOr("lastMineMode", MODE_NONE);
    }

}
