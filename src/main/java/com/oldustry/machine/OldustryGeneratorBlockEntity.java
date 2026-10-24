package com.oldustry.machine;

import com.oldustry.OldustryConfig;
import com.oldustry.energy.FuelLogic;
import com.oldustry.energy.Wind;
import com.oldustry.energy.WindLogic;
import com.oldustry.item.equipment.SolarHelmetItem;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

final class OldustryGeneratorBlockEntity extends OldustryMachineBlockEntity {
    private int burnTime;
    private int burnMax;
    private int lava;
    private int lastGen;
    private int sun;
    private int waterFuel;
    private int waterProd = 2;
    private int microStorage;
    private int waterCount;
    private int waterTicker;
    private double substorage;
    private double subproduction;
    private int obscured = -1;
    private int windTicker;

    OldustryGeneratorBlockEntity(OldustryMachineKind kind, BlockPos pos, BlockState state) {
        super(kind, pos, state);
    }

    boolean tickGeneratorFamily(ServerLevel level, BlockPos pos) {
        boolean active = switch (kind.type) {
            case GENERATOR -> tickGenerator(level);
            case GEO -> tickGeo();
            case SOLAR -> tickSolar(level, pos);
            case WATER -> tickWater(level, pos);
            case WIND -> tickWind(level, pos);
            default -> false;
        };
        chargeSlot(1);
        return active;
    }

    int sun() { return sun; }
    int gauge() {
        return switch (kind.type) {
            case GENERATOR -> burnTime;
            case GEO -> lava;
            case WATER -> waterFuel > 0 ? waterFuel : lastGen;
            case WIND -> WindLogic.gaugeFuel(subproduction, 14);
            default -> 0;
        };
    }

    int gaugeMax() {
        return switch (kind.type) {
            case GENERATOR -> Math.max(1, burnMax);
            case GEO -> 24000;
            case WATER -> 2000;
            case WIND -> 14;
            default -> 1;
        };
    }

    private boolean tickGenerator(ServerLevel level) {
        if (burnTime <= 0 && energy + kind.packet <= capacity()) {
            ItemStack fuel = items.get(0);
            if (!fuel.isEmpty()) {
                int ticks = burnOf(level, fuel);
                if (ticks > 0) {
                    burnTime = ticks;
                    burnMax = ticks;
                    if (fuel.is(OldustryItems.FILLED_FUEL_CAN.get())) {
                        items.set(0, FuelLogic.afterConsumingOne(fuel, new ItemStack(OldustryItems.FUEL_CAN.get())));
                    } else if (fuel.is(OldustryItems.LAVA_CELL.get())) {
                        items.set(0, FuelLogic.afterConsumingOne(fuel, new ItemStack(OldustryItems.EMPTY_CELL.get())));
                    } else {
                        fuel.shrink(1);
                    }
                }
            }
        }
        FuelLogic.BurnTick tick = FuelLogic.tickBurn(burnTime, energy, kind.packet, capacity());
        burnTime = tick.burnTime();
        energy = tick.energy();
        return tick.converting();
    }

    private int burnOf(ServerLevel level, ItemStack fuel) {
        if (fuel.is(OldustryItems.FILLED_FUEL_CAN.get())) {
            int value = fuel.getOrDefault(OldustryDataComponents.FUEL.get(), 0);
            return FuelLogic.generatorBurnTicks(Math.min(32767, value * 2), false);
        }
        String id = BuiltInRegistries.ITEM.getKey(fuel.getItem()).getPath();
        if (FuelLogic.lavaBucket(id)) {
            return 0;
        }
        if (("scrap".equals(id) || "scrap_box".equals(id)) && !OldustryConfig.BURN_SCRAP.get()) {
            return 0;
        }
        int extra = FuelLogic.extraFurnaceBurn(id);
        int furnace = extra > 0 ? extra : fuel.getBurnTime(RecipeType.SMELTING, level.fuelValues());
        return FuelLogic.generatorBurnTicks(furnace, false);
    }

    private boolean tickGeo() {
        // JAR GeoGenerator.gainFuel: top up whenever fuel+1000 <= maxLava (not only when empty).
        // Lava cell = plain ItemIC2 (no container) → consume destroys it; bucket leaves empty bucket.
        ItemStack fuel = items.get(0);
        if (FuelLogic.geoCanAccept(lava) && !fuel.isEmpty()) {
            if (fuel.is(OldustryItems.LAVA_CELL.get())) {
                lava += FuelLogic.GEO_CELL;
                fuel.shrink(1);
            } else if (fuel.is(Items.LAVA_BUCKET)) {
                lava += FuelLogic.GEO_CELL;
                items.set(0, FuelLogic.afterConsumingOne(fuel, new ItemStack(Items.BUCKET)));
            }
        }
        FuelLogic.BurnTick tick = FuelLogic.tickIfFits(lava, energy, kind.packet, capacity());
        lava = tick.burnTime();
        energy = tick.energy();
        return tick.converting();
    }

    private boolean tickSolar(ServerLevel level, BlockPos pos) {
        // JAR TileEntitySolarGenerator.isSunVisible (shared with solar helmet).
        sun = SolarHelmetItem.isSunVisible(level, pos.above()) ? 1 : 0;
        if (sun > 0 && energy < capacity()) {
            energy++;
            return true;
        }
        return false;
    }

    private boolean tickWater(ServerLevel level, BlockPos pos) {
        ItemStack fuel = items.get(0);
        if (FuelLogic.waterCanAccept(waterFuel) && !fuel.isEmpty()) {
            if (fuel.is(OldustryItems.WATER_CELL.get())) {
                waterFuel += FuelLogic.WATER_CELL;
                waterProd = 2;
                items.set(0, FuelLogic.afterConsumingOne(fuel, new ItemStack(OldustryItems.EMPTY_CELL.get())));
            } else if (fuel.is(Items.WATER_BUCKET)) {
                waterFuel += FuelLogic.WATER_CELL;
                waterProd = 1;
                items.set(0, FuelLogic.afterConsumingOne(fuel, new ItemStack(Items.BUCKET)));
            }
        }
        if (waterFuel > 0) {
            lastGen = waterProd;
            FuelLogic.BurnTick tick = FuelLogic.tickIfFits(waterFuel, energy, lastGen, capacity());
            waterFuel = tick.burnTime();
            energy = tick.energy();
            return tick.converting();
        }
        if (++waterTicker % 128 == 0) {
            waterCount = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (level.getFluidState(pos.offset(dx, dy, dz)).is(Fluids.WATER)) {
                            waterCount++;
                        }
                    }
                }
            }
        }
        if (waterCount > 0) {
            microStorage += waterCount;
        }
        lastGen = microStorage / 100;
        microStorage -= lastGen * 100;
        if (lastGen > 0 && energy + lastGen <= capacity()) {
            energy += lastGen;
            return true;
        }
        return false;
    }

    private boolean tickWind(ServerLevel level, BlockPos pos) {
        if (++windTicker % WindLogic.TICK_RATE == 0) {
            obscured = -1;
            for (int x = -4; x < 5; x++) {
                for (int y = -2; y < 5; y++) {
                    for (int z = -4; z < 5; z++) {
                        if (!level.getBlockState(pos.offset(x, y, z)).isAir()) {
                            obscured++;
                        }
                    }
                }
            }
            subproduction = WindLogic.production(
                Wind.strength(), pos.getY(), obscured, level.isThundering(), level.isRaining()
            );
            if (subproduction <= 0) {
                return false;
            }
            if (subproduction > 5 && level.getRandom().nextInt(5000) <= subproduction - 5) {
                ItemStack iron = new ItemStack(Items.IRON_INGOT, 1 + level.getRandom().nextInt(5));
                level.setBlock(pos, OldustryBlocks.GENERATOR.get().defaultBlockState(), Block.UPDATE_ALL);
                Block.popResource(level, pos, iron);
                return false;
            }
        }
        if (subproduction <= 0) {
            return false;
        }
        substorage += subproduction;
        lastGen = (int) substorage;
        if (energy + lastGen >= capacity()) {
            substorage = 0;
            return false;
        }
        energy += lastGen;
        substorage -= lastGen;
        return lastGen > 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (kind.type == OldustryMachineKind.Type.GENERATOR) {
            output.putInt("burn", burnTime);
            output.putInt("burnMax", burnMax);
        }
        if (kind.type == OldustryMachineKind.Type.GEO) output.putInt("lava", lava);
        output.putInt("waterFuel", waterFuel);
        output.putInt("micro", microStorage);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (kind.type == OldustryMachineKind.Type.GENERATOR) {
            burnTime = input.getIntOr("burn", 0);
            burnMax = input.getIntOr("burnMax", burnTime);
        }
        if (kind.type == OldustryMachineKind.Type.GEO) lava = input.getIntOr("lava", 0);
        waterFuel = input.getIntOr("waterFuel", 0);
        microStorage = input.getIntOr("micro", 0);
    }
}
