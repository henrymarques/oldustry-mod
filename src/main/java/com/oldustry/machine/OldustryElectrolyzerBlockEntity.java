package com.oldustry.machine;

import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

final class OldustryElectrolyzerBlockEntity extends OldustryMachineBlockEntity {
    private int cacheTicker;
    OldustryElectrolyzerBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.ELECTROLYZER, pos, state);
    }

    boolean tickElectrolyzer(ServerLevel level, BlockPos pos) {
        cacheTicker++; // Existing saved counter; the adjacent storage is checked every tick.
        OldustryMachineBlockEntity storage = lookForStorage(level, pos);
        if (storage == null) {
            return false;
        }
        boolean active = false;
        int rate = electrolyzerRate(storage.kind().tier);
        int storCap = storage.energyCapacity();
        int storEu = storage.energy();
        if (storCap > 0 && storEu * 10L >= storCap * 7L && electrolyzerCanDrain()) {
            storage.drainForElectrolyzer(rate);
            energy = Math.min(20000, energy + rate);
            if (energy >= 20000) {
                energy -= 20000;
                ItemStack water = items.get(0);
                water.shrink(1);
                ItemStack hydro = items.get(1);
                if (hydro.isEmpty()) {
                    items.set(1, new ItemStack(OldustryItems.ELECTROLYZED_WATER_CELL.get()));
                } else {
                    hydro.grow(1);
                }
                setChanged();
            }
            
            active = true;
        }
        storEu = storage.energy();
        if (storCap > 0 && storEu * 10L <= storCap * 3L && (electrolyzerCanPower() || energy > 0)) {
            if (energy > 0) {
                int out = Math.min(energy, rate);
                energy -= out;
                storage.fillFromElectrolyzer(out);
                
            } else {
                energy += 12000 + 2000 * storage.kind().tier;
                ItemStack hydro = items.get(1);
                hydro.shrink(1);
                ItemStack water = items.get(0);
                if (water.isEmpty()) {
                    items.set(0, new ItemStack(OldustryItems.WATER_CELL.get()));
                } else {
                    water.grow(1);
                }
                setChanged();
            }
            active = true;
        }
        return active;
    }

    private boolean electrolyzerCanDrain() {
        ItemStack water = items.get(0);
        ItemStack hydro = items.get(1);
        return water.is(OldustryItems.WATER_CELL.get())
            && (hydro.isEmpty()
                || (hydro.is(OldustryItems.ELECTROLYZED_WATER_CELL.get())
                    && hydro.getCount() < hydro.getMaxStackSize()));
    }

    private boolean electrolyzerCanPower() {
        ItemStack hydro = items.get(1);
        ItemStack water = items.get(0);
        return hydro.is(OldustryItems.ELECTROLYZED_WATER_CELL.get())
            && (water.isEmpty()
                || (water.is(OldustryItems.WATER_CELL.get()) && water.getCount() < water.getMaxStackSize()));
    }

    private static int electrolyzerRate(int tier) {
        return switch (tier) {
            case 2 -> 8;
            case 3 -> 32;
            default -> 2;
        };
    }

    private OldustryMachineBlockEntity lookForStorage(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(pos.relative(dir)) instanceof OldustryMachineBlockEntity be
                && be.kind.type == OldustryMachineKind.Type.STORAGE) {
                return be;
            }
        }
        return null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("burn", cacheTicker);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        cacheTicker = input.getIntOr("burn", 0);
    }
}
