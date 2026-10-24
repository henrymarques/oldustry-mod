package com.oldustry.machine;

import com.oldustry.item.equipment.CfPackItem;
import com.oldustry.item.equipment.FuelJetpackItem;
import com.oldustry.item.equipment.JetpackFuel;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

final class OldustryCannerBlockEntity extends OldustryMachineBlockEntity {
    private int fuelQuality;

    OldustryCannerBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.CANNER, pos, state);
    }

    boolean tickCanner(ServerLevel level) {
        dischargeSlot(3);
        ItemStack resource = items.get(0); // food/fuel_cell/pellets
        ItemStack vessel = items.get(1); // tin_can/fuel_can/cf_pack
        ItemStack out = items.get(2);
        int mode = cannerMode(vessel);
        boolean canOp = cannerCanOperate(mode, resource, vessel, out);

        if (canOp && cannerReadyToOperate(mode, resource)) {
            if (mode == 1 || mode == 3 || progress >= 600) {
                cannerOperate(mode, false, resource, vessel, out);
                fuelQuality = 0;
                progress = 0;
                // JAR starts the next charge on this same tick when the machine can still run.
                resource = items.get(0);
                vessel = items.get(1);
                out = items.get(2);
                mode = cannerMode(vessel);
                canOp = cannerCanOperate(mode, resource, vessel, out);
            } else {
                cannerOperate(mode, true, resource, vessel, out);
            }
            setChanged();
        }

        if (!canOp) {
            if (mode != 2) {
                fuelQuality = 0;
                progress = 0;
            }
            return false;
        }
        if (energy < kind.euPerTick()) {
            return false;
        }
        progress++;
        energy -= kind.euPerTick();
        return true;
    }

    /** Mode: 0=none, 1=food→tin_can, 2=fuel→fuel_can, 3=pellet→cf_pack */
    private static int cannerMode(ItemStack vessel) {
        if (vessel.isEmpty()) {
            return 0;
        }
        if (vessel.is(OldustryItems.TIN_CAN.get())) {
            return 1;
        }
        if (vessel.is(OldustryItems.FUEL_CAN.get()) || vessel.is(OldustryItems.FUEL_JETPACK.get())) {
            return 2;
        }
        if (vessel.is(OldustryItems.CF_PACK.get())) {
            return 3;
        }
        return 0;
    }

    /** JAR gaugeProgressScaled length (not the fuel-cell 100-tick step). */
    int cannerGaugeDuration() {
        int mode = cannerMode(items.get(1));
        return switch (mode) {
            case 1 -> {
                int food = cannerFoodValue(items.get(0));
                yield food > 0 ? 50 * food : 600;
            }
            case 3 -> 50;
            default -> 600;
        };
    }

    private boolean cannerReadyToOperate(int mode, ItemStack resource) {
        return switch (mode) {
            case 1 -> progress >= cannerFoodValue(resource) * 50;
            case 2 -> progress > 0 && progress % 100 == 0;
            case 3 -> progress >= 50;
            default -> false;
        };
    }

    private boolean cannerCanOperate(int mode, ItemStack resource, ItemStack vessel, ItemStack out) {
        if (resource.isEmpty() || mode == 0) {
            return false;
        }
        return switch (mode) {
            case 1 -> {
                int food = cannerFoodValue(resource);
                ItemStack result = new ItemStack(OldustryItems.FILLED_TIN_CAN.get(), food);
                yield food > 0 && vessel.getCount() >= food && canOutput(out, result);
            }
            case 2 -> {
                int fuel = cannerFuelValue(resource);
                if (fuel <= 0) {
                    yield false;
                }
                if (vessel.is(OldustryItems.FUEL_JETPACK.get())) {
                    yield canOutput(out, vessel);
                }
                yield canOutput(out, new ItemStack(OldustryItems.FILLED_FUEL_CAN.get()));
            }
            case 3 -> CfPackItem.acceptsPellet(CfPackItem.charge(vessel))
                && resource.is(OldustryItems.CF_PELLET.get())
                && canOutput(out, vessel);
            default -> false;
        };
    }

    private void cannerOperate(int mode, boolean incremental, ItemStack resource, ItemStack vessel, ItemStack out) {
        switch (mode) {
            case 1 -> {
                int food = cannerFoodValue(resource);
                resource.shrink(1);
                vessel.shrink(food);
                mergeOut(2, new ItemStack(OldustryItems.FILLED_TIN_CAN.get(), food));
            }
            case 2 -> {
                fuelQuality += cannerFuelValue(resource);
                resource.shrink(1);
                if (!incremental) {
                    if (vessel.is(OldustryItems.FUEL_JETPACK.get())) {
                        ItemStack result = vessel.copy();
                        result.setCount(1);
                        FuelJetpackItem.setFuel(result, JetpackFuel.afterCanner(FuelJetpackItem.fuel(result), fuelQuality));
                        fuelQuality = 0;
                        vessel.shrink(1);
                        mergeOut(2, result);
                    } else {
                        ItemStack result = new ItemStack(OldustryItems.FILLED_FUEL_CAN.get());
                        result.set(OldustryDataComponents.FUEL.get(), fuelQuality);
                        vessel.shrink(1);
                        mergeOut(2, result);
                    }
                }
            }
            case 3 -> {
                // One pellet adds 2. A full run from empty ends at 260.
                resource.shrink(1);
                int charge = CfPackItem.afterPellet(CfPackItem.charge(vessel));
                CfPackItem.setCharge(vessel, charge);
                if (CfPackItem.leaveCanner(charge, !resource.isEmpty())) {
                    mergeOut(2, vessel.copy());
                    vessel.setCount(0);
                }
            }
            default -> {}
        }
    }

    /**
     * Cans produced from one food item.
     * Desvio (IC2 experimental wiki): 1 can per nutrition point (steak→8, apple→4…).
     * JAR 1.117 was {@code ceil(nutrition / 2)}.
     */
    private static int cannerFoodValue(ItemStack resource) {
        if (resource.is(OldustryItems.FILLED_TIN_CAN.get())) {
            return 0;
        }
        FoodProperties food = resource.get(DataComponents.FOOD);
        if (food != null && food.nutrition() > 0) {
            return food.nutrition();
        }
        return 0;
    }

    /** Fuel units this input adds. Boosters read the quality already stored this cycle. */
    int cannerFuelValue(ItemStack resource) {
        return OldustryCannerFuelValues.value(resource, fuelQuality);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("lava", fuelQuality);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fuelQuality = input.getIntOr("lava", 0);
    }
}
