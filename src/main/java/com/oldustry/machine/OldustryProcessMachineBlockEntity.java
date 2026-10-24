package com.oldustry.machine;

import com.oldustry.energy.ProcessLogic;
import com.oldustry.energy.UpgradeLogic;
import com.oldustry.registry.OldustrySounds;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.item.UpgradeItem;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Shared tick for the five IC2 standard processing machines. */
public abstract class OldustryProcessMachineBlockEntity extends OldustryMachineBlockEntity {
    protected OldustryProcessMachineBlockEntity(OldustryMachineKind kind, BlockPos pos, BlockState state) {
        super(kind, pos, state);
    }

    protected final boolean tickProcess(ServerLevel level) {
        ItemStack in = items.get(0);
        ItemStack out = items.get(1);
        ItemStack result = processResult(level, in);
        int consume = OldustryProcessingRecipes.consume(kind.type, in.getItem());
        boolean work = in.getCount() >= consume && !result.isEmpty()
            && canOutput(out, result) && acceptsInput(in);
        UpgradeLogic.Stats s = stats();
        int was = progress;
        ProcessLogic.Tick tick = ProcessLogic.tick(energy, progress, s.euPerTick(), s.duration(), work);
        energy = tick.energy();
        progress = tick.progress();
        // JAR InterruptOne plays when an operation aborts, not when it finishes.
        if (was > 0 && progress == 0 && !tick.crafted() && playsInterruptSound()) {
            OldustrySounds.play(level, null, worldPosition, OldustrySounds.INTERRUPT.get(), SoundSource.BLOCKS, 1f, 1f);
        }
        if (tick.crafted()) {
            in.shrink(consume);
            craftResult(level, result);
        }
        return work && energy >= s.euPerTick();
    }

    final void tickEjector(ServerLevel level, BlockPos pos) {
        ItemStack out = items.get(1);
        if (out.isEmpty() || energy < 20) {
            return;
        }
        int ejectors = upgrades(UpgradeItem.Kind.EJECTOR);
        if (ejectors <= 0) {
            return;
        }
        int dir = 0;
        for (int i = 2; i < SIZE; i++) {
            ItemStack stack = items.get(i);
            if (stack.getItem() instanceof UpgradeItem upgrade && upgrade.kind() == UpgradeItem.Kind.EJECTOR) {
                dir = stack.getOrDefault(OldustryDataComponents.EJECTOR_DIR.get(), 0);
                break;
            }
        }
        int amount = Math.min(out.getCount(), energy / 20);
        int moved = 0;
        Direction face = ejectorFace(dir);
        if (face == null) {
            for (Direction d : Direction.values()) {
                moved += OldustryMachineTransfer.insert(level, pos.relative(d), d.getOpposite(), out, amount - moved);
                if (moved >= amount) {
                    break;
                }
            }
        } else {
            moved = OldustryMachineTransfer.insert(level, pos.relative(face), face.getOpposite(), out, amount);
        }
        if (moved > 0) {
            out.shrink(moved);
            energy -= 20 * moved;
        }
    }

    private static Direction ejectorFace(int dir) {
        return switch (dir) {
            case 1 -> Direction.WEST;
            case 2 -> Direction.EAST;
            case 3 -> Direction.DOWN;
            case 4 -> Direction.UP;
            case 5 -> Direction.NORTH;
            case 6 -> Direction.SOUTH;
            default -> null;
        };
    }

    protected abstract ItemStack processResult(ServerLevel level, ItemStack in);

    protected boolean acceptsInput(ItemStack in) {
        return true;
    }

    protected void craftResult(ServerLevel level, ItemStack result) {
        mergeOut(1, result);
    }

    protected boolean playsInterruptSound() {
        return true;
    }
}
