package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

final class OldustryInductionBlockEntity extends OldustryMachineBlockEntity {
    private int heat;

    int heat() {
        return heat;
    }

    OldustryInductionBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.INDUCTION, pos, state);
    }

    boolean tickInduction(ServerLevel level, BlockPos pos) {
        boolean canOp = inductionCanOperate(level);
        boolean redstone = level.hasNeighborSignal(pos);
        // Powered draw (recipe or RS) — lit/sound follow this, not residual heat.
        boolean heating = energy > 0 && (canOp || redstone);
        boolean working = heat > 0;

        if (progress >= 4000) {
            inductionOperate(level);
            progress = 0;
            working = false;
            setChanged();
        }

        if (heating) {
            energy--;
            if (heat < 10000) {
                heat++;
            }
            working = true;
        } else {
            // JAR heat decay while unpowered (RS off / no recipe / no EU).
            heat = Math.max(0, heat - 4);
        }

        if (!working || progress == 0) {
            if (canOp) {
                if (energy >= 15) {
                    working = true;
                }
            } else {
                progress = 0;
            }
        } else if (!canOp || energy < 15) {
            if (!canOp) {
                progress = 0;
            }
            working = false;
        }

        if (working && canOp) {
            progress += heat / 30;
            energy -= 15;
            setChanged();
        }
        // Inactive when lever off (and no recipe): stop sound; heat still decays above.
        return heating;
    }

    private boolean inductionCanOperate(ServerLevel level) {
        return inductionCanPair(level, 0, 3) || inductionCanPair(level, 1, 4);
    }

    private boolean inductionCanPair(ServerLevel level, int inSlot, int outSlot) {
        ItemStack in = items.get(inSlot);
        if (in.isEmpty()) {
            return false;
        }
        ItemStack result = smelt(level, in);
        return !result.isEmpty() && canOutput(items.get(outSlot), result);
    }

    private void inductionOperate(ServerLevel level) {
        inductionOperatePair(level, 0, 3);
        inductionOperatePair(level, 1, 4);
    }

    private void inductionOperatePair(ServerLevel level, int inSlot, int outSlot) {
        if (!inductionCanPair(level, inSlot, outSlot)) {
            return;
        }
        ItemStack in = items.get(inSlot);
        ItemStack result = smelt(level, in);
        in.shrink(1);
        mergeOut(outSlot, result);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("burn", heat);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        heat = input.getIntOr("burn", 0);
    }
}
