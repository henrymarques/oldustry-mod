package com.oldustry.machine;

import com.oldustry.Oldustry;
import com.oldustry.OldustryConfig;
import com.oldustry.block.IronFenceBlock;
import com.oldustry.block.MachineBlock;
import com.oldustry.energy.ElectricCharge;
import com.oldustry.energy.EnergyNet;
import com.oldustry.energy.EnergyNetLogic;
import com.oldustry.energy.EnergyTile;
import com.oldustry.energy.FuelLogic;
import com.oldustry.energy.ProcessLogic;
import com.oldustry.energy.UpgradeLogic;
import com.oldustry.energy.Wind;
import com.oldustry.energy.WindLogic;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.FuelJetpackItem;
import com.oldustry.item.ScannerItem;
import com.oldustry.item.SolarHelmetItem;
import com.oldustry.item.UpgradeItem;
import com.oldustry.registry.ModBlockEntities;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModDataComponents;
import com.oldustry.registry.ModItems;
import com.oldustry.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class MachineBlockEntity extends BlockEntity implements EnergyTile, WorldlyContainer, MenuProvider {
    public static final int SIZE = 7;
    private static final Component[] MODE_MSG = {
        Component.translatable("info.oldustry.storage.mode.nothing"),
        Component.translatable("info.oldustry.storage.mode.full"),
        Component.translatable("info.oldustry.storage.mode.partial"),
        Component.translatable("info.oldustry.storage.mode.empty"),
        Component.translatable("info.oldustry.storage.mode.no_output"),
        Component.translatable("info.oldustry.storage.mode.output_full")
    };

    private final MachineKind kind;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int energy;
    private int progress;
    private int burnTime;
    private int burnMax;
    private int lava;
    private int lastGen;
    private int sun;
    private int mode;
    private int waterFuel;
    private int waterProd = 2;
    private int microStorage;
    private int waterCount;
    private int waterTicker;
    private double substorage;
    private double subproduction;
    private int obscured = -1;
    private int windTicker;
    private long totalEmitted;
    private long totalSunken;
    private boolean netted;
    /** Miner → adjacent Pump liquid handoff (IC2 canProvideLiquid). */
    private boolean canProvideLiquid;
    private int liquidX;
    private int liquidY;
    private int liquidZ;
    private int scannedLevel = Integer.MIN_VALUE;
    private int scanRange;
    private int lastMineX;
    private int lastMineZ;
    private int lastMineMode;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> capacity();
                case 2 -> progress;
                case 3 -> processDuration();
                case 4 -> fuelGauge();
                case 5 -> kind.type == MachineKind.Type.MINING ? lastMineMode : fuelGaugeMax();
                case 6 -> kind.type == MachineKind.Type.MINING
                    ? (getBlockState().getValue(MachineBlock.ACTIVE) ? 1 : 0)
                    : sun;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                energy = value;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public MachineBlockEntity(BlockPos pos, BlockState state) {
        this(kindOf(state), pos, state);
    }

    public MachineBlockEntity(MachineKind kind, BlockPos pos, BlockState state) {
        super(ModBlockEntities.MACHINE.get(), pos, state);
        this.kind = kind;
    }

    public MachineKind kind() {
        return kind;
    }

    public ContainerData data() {
        return data;
    }

    public int energy() {
        return energy;
    }

    public int energyCapacity() {
        return capacity();
    }

    public int processProgress() {
        return progress;
    }

    /** Duration for progress %; 0 if this machine has no process bar (transformers/storage/gens). */
    public int processDuration() {
        return switch (kind.type) {
            case MACERATOR, EXTRACTOR, COMPRESSOR, RECYCLER, FURNACE, INDUCTION -> stats().duration();
            // JAR GuiCanner.gaugeProgressScaled: food=50×cans, CF=50, fuel=600.
            case CANNING -> cannerGaugeDuration();
            case MINING -> lastMineMode == MODE_DDRILL ? MINER_DDRILL_TICKS : MINER_AIR_TICKS;
            default -> 0;
        };
    }

    public void cycleMode(Player player) {
        mode = (mode + 1) % 6;
        if (player != null) {
            player.sendSystemMessage(MODE_MSG[mode]);
        }
        setChanged();
    }

    public int redstonePower() {
        int cap = capacity();
        int packet = kind.packet;
        return switch (mode) {
            case 1 -> energy >= cap && cap > 0 ? 15 : 0;
            case 2 -> energy > packet && energy < cap ? 15 : 0;
            case 3 -> energy < packet ? 15 : 0;
            default -> 0;
        };
    }

    public int comparator() {
        int duration = kind.duration();
        if (duration > 0) {
            return Math.min(15, progress * 15 / duration);
        }
        int cap = capacity();
        return cap <= 0 ? 0 : Math.min(15, energy * 15 / cap);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, MachineBlockEntity be) {
        // Process before EnergyNet so this tick's EU spend opens demand for overvolt inject (JAR-like).
        be.ensureNet(level);
        be.tickMachine(level, pos);
        EnergyNet.of(level).tick(level);
    }

    /** Client ticker: keep loop sound while ACTIVE; stop immediately when cleared/broken. */
    public static void clientTick(Level level, BlockPos pos, BlockState state, MachineBlockEntity be) {
        if (state.getValue(MachineBlock.ACTIVE)) {
            Oldustry.clientMachineLoop.accept(pos, be.kind());
        } else {
            Oldustry.clientMachineLoopStop.accept(pos);
        }
    }

    private void tickMachine(ServerLevel level, BlockPos pos) {
        boolean active = switch (kind.type) {
            case GENERATOR -> tickGenerator(level);
            case GEO -> tickGeo();
            case SOLAR -> tickSolar(level, pos);
            case WATER -> tickWater(level, pos);
            case WIND -> tickWind(level, pos);
            case STORAGE -> {
                chargeSlot(0);
                dischargeSlot(1);
                yield redstonePower() > 0;
            }
            case TRANSFORMER -> level.hasNeighborSignal(pos);
            case LUMINATOR -> tickLuminator(level, pos);
            case MINING -> tickMiner(level, pos);
            case PUMPING -> tickPump(level, pos);
            case CANNING -> tickCanner(level);
            case ELECTROLYZER -> tickElectrolyzer(level, pos);
            case MAGNETIZER -> tickMagnetizer(level, pos);
            case INDUCTION -> {
                dischargeSlot(2);
                yield tickInduction(level, pos);
            }
            default -> {
                dischargeSlot(6);
                boolean work = tickProcess(level);
                tickEjector(level, pos);
                yield work;
            }
        };
        switch (kind.type) {
            case GENERATOR, GEO, SOLAR, WATER, WIND -> chargeSlot(1);
            default -> {}
        }
        if (kind.type == MachineKind.Type.STORAGE) {
            level.updateNeighborsAt(pos, getBlockState().getBlock());
        }
        setActive(level, pos, active);
        // Loop sound is client-side (MachineLoopSound) so interrupt can stop it — JAR AudioSource.stop().
        setChanged();
    }

    private boolean tickGenerator(ServerLevel level) {
        if (burnTime <= 0 && energy + kind.packet <= capacity()) {
            ItemStack fuel = items.get(0);
            if (!fuel.isEmpty()) {
                int ticks = burnOf(level, fuel);
                if (ticks > 0) {
                    burnTime = ticks;
                    burnMax = ticks;
                    if (fuel.is(ModItems.FILLED_FUEL_CAN.get())) {
                        items.set(0, FuelLogic.afterConsumingOne(fuel, new ItemStack(ModItems.FUEL_CAN.get())));
                    } else if (fuel.is(ModItems.LAVA_CELL.get())) {
                        items.set(0, FuelLogic.afterConsumingOne(fuel, new ItemStack(ModItems.EMPTY_CELL.get())));
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
        if (fuel.is(ModItems.FILLED_FUEL_CAN.get())) {
            int value = fuel.getOrDefault(ModDataComponents.FUEL.get(), 0);
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
        int furnace = extra > 0 ? extra : level.fuelValues().burnDuration(fuel);
        return FuelLogic.generatorBurnTicks(furnace, false);
    }

    private boolean tickGeo() {
        // JAR GeoGenerator.gainFuel: top up whenever fuel+1000 <= maxLava (not only when empty).
        // Lava cell = plain ItemIC2 (no container) → consume destroys it; bucket leaves empty bucket.
        ItemStack fuel = items.get(0);
        if (FuelLogic.geoCanAccept(lava) && !fuel.isEmpty()) {
            if (fuel.is(ModItems.LAVA_CELL.get())) {
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
            if (fuel.is(ModItems.WATER_CELL.get())) {
                waterFuel += FuelLogic.WATER_CELL;
                waterProd = 2;
                items.set(0, FuelLogic.afterConsumingOne(fuel, new ItemStack(ModItems.EMPTY_CELL.get())));
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
                level.setBlock(pos, ModBlocks.GENERATOR.get().defaultBlockState(), Block.UPDATE_ALL);
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

    private boolean tickLuminator(ServerLevel level, BlockPos pos) {
        // JAR TileEntityLuminator: buf 10k, drain 1 EU every 4 ticks.
        if (energy <= 0) {
            return false;
        }
        if (level.getGameTime() % 4 == 0) {
            energy--;
        }
        AABB box = new AABB(pos);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (entity.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD)) {
                entity.igniteForSeconds(20);
            } else if (entity instanceof net.minecraft.world.entity.monster.Enemy) {
                entity.igniteForSeconds(10);
            }
        }
        return true;
    }

    // ──────────────── MINER (IC2 TileEntityMiner) ────────────────
    // EU: air 3/t×20; mining drill 6/t×20 +50 from drill; diamond 200/t×50 +80 from drill; hoist 2×Δy

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

    private boolean tickMiner(ServerLevel level, BlockPos pos) {
        dischargeSlot(3);
        chargeSlot(0);
        chargeSlot(1);

        ItemStack drill = items.get(0);
        boolean hasDrill = drill.is(ModItems.MINING_DRILL.get()) || drill.is(ModItems.DIAMOND_DRILL.get());
        int opY = minerOperationHeight(level, pos);

        if (!hasDrill) {
            return minerWithdraw(level, pos, opY);
        }
        if (opY < level.getMinY()) {
            return false;
        }

        BlockState atOp = level.getBlockState(new BlockPos(pos.getX(), opY, pos.getZ()));
        if (!atOp.is(ModBlocks.MINING_PIPE_TIP.get())) {
            return opY > level.getMinY() && minerDigDown(level, pos, opY, false);
        }

        int result = minerMineLevel(level, pos, opY);
        if (result == MINE_DONE) {
            return minerDigDown(level, pos, opY - 1, true);
        }
        return result == MINE_WORKING;
    }

    /** First non-pipe Y below the miner (tip or solid/air). */
    private int minerOperationHeight(ServerLevel level, BlockPos pos) {
        for (int y = pos.getY() - 1; y >= level.getMinY(); y--) {
            BlockState state = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
            if (!state.is(ModBlocks.MINING_PIPE.get())) {
                return y;
            }
        }
        return level.getMinY() - 1;
    }

    private boolean minerWithdraw(ServerLevel level, BlockPos pos, int y) {
        if (lastMineMode != MODE_WITHDRAW) {
            lastMineMode = MODE_WITHDRAW;
            progress = 0;
        }
        if (y < level.getMinY()
            || !level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).is(ModBlocks.MINING_PIPE_TIP.get())) {
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
        if (at.is(ModBlocks.MINING_PIPE_TIP.get()) || at.is(ModBlocks.MINING_PIPE.get())) {
            level.removeBlock(tipPos, false);
            if (!addToMinerOutput(new ItemStack(ModItems.MINING_PIPE.get()))) {
                Block.popResource(level, pos, new ItemStack(ModItems.MINING_PIPE.get()));
            }
        }
        return true;
    }

    private boolean minerDigDown(ServerLevel level, BlockPos pos, int y, boolean removeTipAbove) {
        ItemStack pipeSupply = items.get(2);
        if (pipeSupply.isEmpty() || !pipeSupply.is(ModItems.MINING_PIPE.get())) {
            return false;
        }
        if (y < level.getMinY()) {
            if (removeTipAbove) {
                level.setBlock(
                    new BlockPos(pos.getX(), y + 1, pos.getZ()),
                    ModBlocks.MINING_PIPE.get().defaultBlockState(),
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
                    ModBlocks.MINING_PIPE.get().defaultBlockState(),
                    Block.UPDATE_CLIENTS
                );
            }
            return false;
        }
        if (result == MINE_DONE) {
            if (removeTipAbove) {
                level.setBlock(
                    new BlockPos(pos.getX(), y + 1, pos.getZ()),
                    ModBlocks.MINING_PIPE.get().defaultBlockState(),
                    Block.UPDATE_CLIENTS
                );
            }
            pipeSupply.shrink(1);
            level.setBlock(
                new BlockPos(pos.getX(), y, pos.getZ()),
                ModBlocks.MINING_PIPE_TIP.get().defaultBlockState(),
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
        } else if (drill.is(ModItems.MINING_DRILL.get())) {
            mode = MODE_DRILL;
            euPerTick = MINER_DRILL_EU;
            duration = MINER_DRILL_TICKS;
        } else if (drill.is(ModItems.DIAMOND_DRILL.get())) {
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
        int use = drill.is(ModItems.DIAMOND_DRILL.get()) ? MINER_DDRILL_USE : MINER_DRILL_USE;
        if (!(drill.getItem() instanceof ElectricItem electric) || ElectricItem.energy(drill) < use) {
            return false;
        }
        int hoist = 2 * (minerPos.getY() - target.getY());
        if (energy < hoist) {
            return false;
        }
        electric.discharge(drill, use, 99);
        energy -= hoist;
        java.util.List<ItemStack> drops = Block.getDrops(state, level, target, level.getBlockEntity(target));
        level.removeBlock(target, false);
        for (ItemStack drop : drops) {
            if (!addToMinerOutput(drop)) {
                Block.popResource(level, minerPos, drop);
            }
        }
        return true;
    }

    private boolean minerCanMine(ServerLevel level, BlockPos minerPos, BlockPos target, BlockState state) {
        if (state.isAir()) {
            return true;
        }
        if (state.is(ModBlocks.MINING_PIPE.get()) || state.is(ModBlocks.MINING_PIPE_TIP.get())) {
            return false;
        }
        float hardness = state.getDestroySpeed(level, target);
        if (hardness < 0) {
            return false;
        }
        ItemStack drill = items.get(0);
        return !drill.isEmpty() && drill.isCorrectToolForDrops(state);
    }

    /** Try adding to miner output slots 4-6. */
    private boolean addToMinerOutput(ItemStack stack) {
        for (int i = 4; i <= 6; i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty()) {
                items.set(i, stack.copy());
                stack.setCount(0);
                return true;
            }
            if (ItemStack.isSameItemSameComponents(slot, stack) && slot.getCount() + stack.getCount() <= slot.getMaxStackSize()) {
                slot.grow(stack.getCount());
                stack.setCount(0);
                return true;
            }
        }
        return false;
    }

    public boolean canProvideLiquid() {
        return canProvideLiquid;
    }

    public BlockPos liquidPos() {
        return new BlockPos(liquidX, liquidY, liquidZ);
    }

    // ──────────────── PUMP (IC2 TileEntityPump) ────────────────
    // lava = pumpCharge (0..200). Prefer adjacent miner's canProvideLiquid, else adjacent sources.

    private static final int PUMP_CHARGE_TARGET = 200;

    private boolean tickPump(ServerLevel level, BlockPos pos) {
        dischargeSlot(2);
        int pumpCharge = lava;

        if (pumpCharge < PUMP_CHARGE_TARGET) {
            if (energy <= 0) {
                return false;
            }
            int add = Math.min(energy, 128);
            energy -= add;
            lava = pumpCharge + add;
            return true;
        }

        ItemStack cellSlot = items.get(0);
        if (cellSlot.isEmpty() || !cellSlot.is(ModItems.EMPTY_CELL.get())) {
            return false;
        }

        BlockPos fluidPos = null;
        MachineBlockEntity linkedMiner = findAdjacentMiner(level, pos);
        if (linkedMiner != null && linkedMiner.canProvideLiquid()) {
            fluidPos = linkedMiner.liquidPos();
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
        ItemStack filled;
        if (fluid.is(net.minecraft.tags.FluidTags.WATER)) {
            filled = new ItemStack(ModItems.WATER_CELL.get());
        } else if (fluid.is(net.minecraft.tags.FluidTags.LAVA)) {
            filled = new ItemStack(ModItems.LAVA_CELL.get());
        } else {
            return false;
        }

        ItemStack out = items.get(1);
        if (!canOutput(out, filled)) {
            return false;
        }

        level.removeBlock(fluidPos, false);
        if (linkedMiner != null) {
            linkedMiner.canProvideLiquid = false;
        }
        cellSlot.shrink(1);
        mergeOut(1, filled);
        lava = pumpCharge - PUMP_CHARGE_TARGET;
        return true;
    }

    private static boolean isPumpableSource(ServerLevel level, BlockPos pos) {
        var fluid = level.getBlockState(pos).getFluidState();
        return !fluid.isEmpty() && fluid.isSource()
            && (fluid.is(net.minecraft.tags.FluidTags.WATER) || fluid.is(net.minecraft.tags.FluidTags.LAVA));
    }

    private static MachineBlockEntity findAdjacentMiner(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) {
                continue; // IC2 skips YP when searching
            }
            BlockEntity te = level.getBlockEntity(pos.relative(dir));
            if (te instanceof MachineBlockEntity machine && machine.kind.type == MachineKind.Type.MINING) {
                return machine;
            }
        }
        return null;
    }

    // ──────────────── CANNER ────────────────
    // lava = fuelQuality (accumulated EU for fuel_can mode)
    // progress = operation ticks — JAR TileEntityCanner order: complete → then ++

    private boolean tickCanner(ServerLevel level) {
        dischargeSlot(3);
        ItemStack resource = items.get(0); // food/fuel_cell/pellets
        ItemStack vessel = items.get(1); // tin_can/fuel_can/cf_pack
        ItemStack out = items.get(2);
        int mode = cannerMode(vessel);
        boolean canOp = cannerCanOperate(mode, resource, vessel, out);

        if (canOp && cannerReadyToOperate(mode, resource)) {
            if (mode == 1 || mode == 3 || progress >= 600) {
                cannerOperate(mode, false, resource, vessel, out);
                lava = 0;
                progress = 0;
                setChanged();
                return false;
            } else {
                cannerOperate(mode, true, resource, vessel, out);
            }
            setChanged();
        }

        if (!canOp) {
            if (mode != 2) {
                lava = 0;
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
        if (vessel.is(ModItems.TIN_CAN.get())) {
            return 1;
        }
        if (vessel.is(ModItems.FUEL_CAN.get()) || vessel.is(ModItems.FUEL_JETPACK.get())) {
            return 2;
        }
        if (vessel.is(ModItems.CF_PACK.get())) {
            return 3;
        }
        return 0;
    }

    /** JAR gaugeProgressScaled length (not the fuel-cell 100-tick step). */
    private int cannerGaugeDuration() {
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
                ItemStack result = new ItemStack(ModItems.FILLED_TIN_CAN.get(), food);
                yield food > 0 && vessel.getCount() >= food && canOutput(out, result);
            }
            case 2 -> {
                int fuel = cannerFuelValue(resource);
                if (fuel <= 0) {
                    yield false;
                }
                if (vessel.is(ModItems.FUEL_JETPACK.get())) {
                    yield FuelJetpackItem.fuel(vessel) < FuelJetpackItem.MAX_FUEL && canOutput(out, vessel);
                }
                yield canOutput(out, new ItemStack(ModItems.FILLED_FUEL_CAN.get()));
            }
            case 3 -> vessel.getDamageValue() > 2
                && resource.is(ModItems.CF_PELLET.get())
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
                mergeOut(2, new ItemStack(ModItems.FILLED_TIN_CAN.get(), food));
            }
            case 2 -> {
                lava += cannerFuelValue(resource);
                resource.shrink(1);
                if (!incremental) {
                    if (vessel.is(ModItems.FUEL_JETPACK.get())) {
                        ItemStack result = vessel.copy();
                        result.setCount(1);
                        FuelJetpackItem.setFuel(result, FuelJetpackItem.fuel(result) + lava);
                        lava = 0;
                        vessel.shrink(1);
                        mergeOut(2, result);
                    } else {
                        ItemStack result = new ItemStack(ModItems.FILLED_FUEL_CAN.get());
                        result.set(ModDataComponents.FUEL.get(), lava);
                        vessel.shrink(1);
                        mergeOut(2, result);
                    }
                }
            }
            case 3 -> {
                resource.shrink(1);
                vessel.setDamageValue(vessel.getDamageValue() - 2);
                if (resource.isEmpty() || vessel.getDamageValue() <= 1) {
                    ItemStack result = vessel.copy();
                    if (result.getDamageValue() <= 1) {
                        result.setDamageValue(0);
                    }
                    mergeOut(2, result);
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
        if (resource.is(ModItems.FILLED_TIN_CAN.get())) {
            return 0;
        }
        FoodProperties food = resource.get(DataComponents.FOOD);
        if (food != null && food.nutrition() > 0) {
            return food.nutrition();
        }
        return 0;
    }

    /** EU value stored per fuel cell consumed. Matches IC2 JAR (see CannerFuelValues). */
    static int cannerFuelValue(ItemStack resource) {
        if (resource.is(ModItems.COALFUEL_CELL.get())) {
            return CannerFuelValues.COALFUEL;
        }
        if (resource.is(ModItems.BIOFUEL_CELL.get())) {
            return CannerFuelValues.BIOFUEL;
        }
        return 0;
    }

    // ── Electrolyzer (JAR TileEntityElectrolyzer) ──
    // Not EnergyNet sink. Adjacent BatBox/MFE/MFSU only. Internal buf 20k.
    // ≥70% drain water→electrolyzed (−processRate from storage); ≤30% power electrolyzed→water.

    private boolean tickElectrolyzer(ServerLevel level, BlockPos pos) {
        if (++burnTime % 16 == 0) {
            // burnMax reused as cache marker; storage looked up each cycle below
        }
        MachineBlockEntity storage = lookForStorage(level, pos);
        if (storage == null) {
            return false;
        }
        boolean active = false;
        int rate = electrolyzerRate(storage.kind.tier);
        int storCap = storage.capacity();
        int storEu = storage.energy;
        if (storCap > 0 && storEu * 10L >= storCap * 7L && electrolyzerCanDrain()) {
            storage.energy = Math.max(0, storEu - rate);
            energy = Math.min(20000, energy + rate);
            if (energy >= 20000) {
                energy -= 20000;
                ItemStack water = items.get(0);
                water.shrink(1);
                ItemStack hydro = items.get(1);
                if (hydro.isEmpty()) {
                    items.set(1, new ItemStack(ModItems.ELECTROLYZED_WATER_CELL.get()));
                } else {
                    hydro.grow(1);
                }
                setChanged();
            }
            storage.setChanged();
            active = true;
        }
        storEu = storage.energy;
        if (storCap > 0 && storEu * 10L <= storCap * 3L && (electrolyzerCanPower() || energy > 0)) {
            if (energy > 0) {
                int out = Math.min(energy, rate);
                energy -= out;
                storage.energy = Math.min(storCap, storage.energy + out);
                storage.setChanged();
            } else {
                energy += 12000 + 2000 * storage.kind.tier;
                ItemStack hydro = items.get(1);
                hydro.shrink(1);
                ItemStack water = items.get(0);
                if (water.isEmpty()) {
                    items.set(0, new ItemStack(ModItems.WATER_CELL.get()));
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
        return water.is(ModItems.WATER_CELL.get())
            && (hydro.isEmpty()
                || (hydro.is(ModItems.ELECTROLYZED_WATER_CELL.get())
                    && hydro.getCount() < hydro.getMaxStackSize()));
    }

    private boolean electrolyzerCanPower() {
        ItemStack hydro = items.get(1);
        ItemStack water = items.get(0);
        return hydro.is(ModItems.ELECTROLYZED_WATER_CELL.get())
            && (water.isEmpty()
                || (water.is(ModItems.WATER_CELL.get()) && water.getCount() < water.getMaxStackSize()));
    }

    private static int electrolyzerRate(int tier) {
        return switch (tier) {
            case 2 -> 8;
            case 3 -> 32;
            default -> 2;
        };
    }

    private MachineBlockEntity lookForStorage(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(pos.relative(dir)) instanceof MachineBlockEntity be
                && be.kind.type == MachineKind.Type.STORAGE) {
                return be;
            }
        }
        return null;
    }

    // ── Magnetizer (JAR TileEntityMagnetizer) ──
    // Buf 100 / maxInput 32. Charge iron_fence CHARGE 0–15 ±20 continuous.

    private boolean tickMagnetizer(ServerLevel level, BlockPos pos) {
        if (burnTime-- > 0) {
            return energy > 0;
        }
        boolean change = false;
        change |= magnetizeColumn(level, pos, -1);
        change |= magnetizeColumn(level, pos, 1);
        if (!change) {
            burnTime = 10;
        }
        return change || energy > 0;
    }

    private boolean magnetizeColumn(ServerLevel level, BlockPos origin, int dyStep) {
        boolean change = false;
        int y = origin.getY() + dyStep;
        int limit = dyStep < 0 ? Math.max(level.getMinY(), origin.getY() - 20) : Math.min(level.getMaxY(), origin.getY() + 20);
        while ((dyStep < 0 ? y >= limit : y <= limit) && energy > 0) {
            BlockPos at = new BlockPos(origin.getX(), y, origin.getZ());
            BlockState state = level.getBlockState(at);
            if (!(state.getBlock() instanceof IronFenceBlock) || !state.hasProperty(IronFenceBlock.CHARGE)) {
                break;
            }
            int need = 15 - state.getValue(IronFenceBlock.CHARGE);
            if (need > 0) {
                change = true;
                need = Math.min(need, energy);
                level.setBlock(at, state.setValue(IronFenceBlock.CHARGE, state.getValue(IronFenceBlock.CHARGE) + need), Block.UPDATE_CLIENTS);
                energy -= need;
                setChanged();
            }
            y += dyStep;
        }
        return change;
    }

    // ── Induction (JAR TileEntityInduction) ──
    // Buf 10k T2; heat 0–10k; progress to 4000 at heat/30; 15 EU/t while processing.
    // burnTime = heat. Redstone keeps heat without recipe.

    private boolean tickInduction(ServerLevel level, BlockPos pos) {
        boolean canOp = inductionCanOperate(level);
        boolean redstone = level.hasNeighborSignal(pos);
        // Powered draw (recipe or RS) — lit/sound follow this, not residual heat.
        boolean heating = energy > 0 && (canOp || redstone);
        boolean working = burnTime > 0;

        if (progress >= 4000) {
            inductionOperate(level);
            progress = 0;
            working = false;
            setChanged();
        }

        if (heating) {
            energy--;
            if (burnTime < 10000) {
                burnTime++;
            }
            working = true;
        } else {
            // JAR heat decay while unpowered (RS off / no recipe / no EU).
            burnTime = Math.max(0, burnTime - 4);
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
            progress += burnTime / 30;
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

    private boolean tickProcess(ServerLevel level) {
        ItemStack in = items.get(0);
        ItemStack out = items.get(1);
        ItemStack result = resultOf(level, in);
        int consume = MachineRecipes.consume(kind.type, in.getItem());
        boolean work = in.getCount() >= consume && !result.isEmpty() && canOutput(out, result)
            && !(kind.type == MachineKind.Type.RECYCLER && MachineRecipes.recyclerBlacklist(in.getItem()));
        UpgradeLogic.Stats s = stats();
        int was = progress;
        ProcessLogic.Tick tick = ProcessLogic.tick(energy, progress, s.euPerTick(), s.duration(), work);
        energy = tick.energy();
        progress = tick.progress();
        // JAR: InterruptOne when running process aborts (input gone / blocked), not on craft finish.
        if (was > 0 && progress == 0 && !tick.crafted() && hasInterruptSound()) {
            ModSounds.play(level, null, worldPosition, ModSounds.INTERRUPT.get(), SoundSource.BLOCKS, 1f, 1f);
        }
        if (tick.crafted()) {
            in.shrink(consume);
            if (kind.type == MachineKind.Type.RECYCLER) {
                if (ProcessLogic.recyclerScrap(level.getRandom().nextInt(8), 8)) {
                    ItemStack scrap = new ItemStack(ModItems.SCRAP.get());
                    if (canOutput(items.get(1), scrap)) {
                        mergeOut(1, scrap);
                    }
                }
            } else {
                mergeOut(1, result);
            }
        }
        return work && energy >= s.euPerTick();
    }

    private boolean hasInterruptSound() {
        return switch (kind.type) {
            case MACERATOR, EXTRACTOR, COMPRESSOR, RECYCLER -> true;
            default -> false;
        };
    }

    private void tickEjector(ServerLevel level, BlockPos pos) {
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
                dir = stack.getOrDefault(ModDataComponents.EJECTOR_DIR.get(), 0);
                break;
            }
        }
        int amount = Math.min(out.getCount(), energy / 20);
        int moved = 0;
        Direction face = ejectorFace(dir);
        if (face == null) {
            for (Direction d : Direction.values()) {
                moved += insert(level, pos.relative(d), d.getOpposite(), out, amount - moved);
                if (moved >= amount) {
                    break;
                }
            }
        } else {
            moved = insert(level, pos.relative(face), face.getOpposite(), out, amount);
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

    /** Insert via NeoForge item ResourceHandler; Container fallback. */
    private static int insert(ServerLevel level, BlockPos pos, Direction side, ItemStack stack, int amount) {
        if (amount <= 0) {
            return 0;
        }
        var handler = level.getCapability(Capabilities.Item.BLOCK, pos, side);
        if (handler != null) {
            ItemStack toMove = stack.copyWithCount(amount);
            try (Transaction tx = Transaction.openRoot()) {
                ItemStack left = ItemUtil.insertItemReturnRemaining(handler, toMove, false, tx);
                tx.commit();
                return amount - left.getCount();
            }
        }
        if (!(level.getBlockEntity(pos) instanceof Container container)) {
            return 0;
        }
        int left = amount;
        for (int i = 0; i < container.getContainerSize() && left > 0; i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                int n = Math.min(left, stack.getMaxStackSize());
                container.setItem(i, stack.copyWithCount(n));
                left -= n;
            } else if (ItemStack.isSameItemSameComponents(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                int n = Math.min(left, slot.getMaxStackSize() - slot.getCount());
                slot.grow(n);
                left -= n;
            }
        }
        if (left < amount) {
            container.setChanged();
        }
        return amount - left;
    }

    private void setActive(ServerLevel level, BlockPos pos, boolean active) {
        BlockState state = getBlockState();
        if (state.hasProperty(MachineBlock.ACTIVE) && state.getValue(MachineBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(MachineBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }

    private ItemStack resultOf(ServerLevel level, ItemStack in) {
        if (in.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return switch (kind.type) {
            case MACERATOR -> MachineRecipes.macerator(in.getItem());
            case EXTRACTOR -> MachineRecipes.extractor(in.getItem());
            case COMPRESSOR -> MachineRecipes.compressor(in.getItem());
            case RECYCLER -> new ItemStack(ModItems.SCRAP.get());
            case FURNACE -> smelt(level, in);
            default -> ItemStack.EMPTY;
        };
    }

    private ItemStack smelt(ServerLevel level, ItemStack in) {
        SingleRecipeInput input = new SingleRecipeInput(in);
        return level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level)
            .map(holder -> holder.value().assemble(input))
            .orElse(ItemStack.EMPTY);
    }

    private static boolean canOutput(ItemStack slot, ItemStack result) {
        return slot.isEmpty()
            || ItemStack.isSameItemSameComponents(slot, result) && slot.getCount() + result.getCount() <= slot.getMaxStackSize();
    }

    private void mergeOut(int slotIndex, ItemStack result) {
        ItemStack slot = items.get(slotIndex);
        if (slot.isEmpty()) {
            items.set(slotIndex, result.copy());
        } else {
            slot.grow(result.getCount());
        }
    }

    private void chargeSlot(int slot) {
        ItemStack stack = items.get(slot);
        if (stack.getItem() instanceof ElectricItem electric && energy > 0) {
            energy -= electric.charge(stack, Math.min(energy, kind.packet));
        }
    }

    private void dischargeSlot(int slot) {
        ItemStack stack = items.get(slot);
        int room = capacity() - energy;
        if (room <= 0 || stack.isEmpty()) {
            return;
        }
        if (stack.getItem() instanceof ElectricItem electric) {
            energy += electric.discharge(stack, Math.min(room, kind.packet), stats().tier());
            return;
        }
        if (stack.is(ModItems.SU_BATTERY.get())) {
            // JAR ItemInfo.getEnergyValue(suBattery) = 1200 in discharge slot (hand dump stays 1000).
            // Don't eat the cell unless the buffer can take the full dump.
            int gained = ElectricCharge.consumeOnce(room, 1200);
            if (gained > 0) {
                energy += gained;
                stack.shrink(1);
            }
            return;
        }
        if (stack.is(Items.REDSTONE)) {
            int gained = ElectricCharge.consumeOnce(room, 800);
            if (gained > 0) {
                energy += gained;
                stack.shrink(1);
            }
        }
    }

    /** Last applied process duration — rescale progress when OC changes length (JAR setOverclockRates). */
    private int lastDuration = -1;

    private UpgradeLogic.Stats stats() {
        UpgradeLogic.Stats s = UpgradeLogic.apply(
            new UpgradeLogic.Stats(kind.euPerTick(), kind.duration(), kind.buffer(), kind.tier),
            upgrades(UpgradeItem.Kind.OVERCLOCKER),
            upgrades(UpgradeItem.Kind.TRANSFORMER),
            upgrades(UpgradeItem.Kind.STORAGE)
        );
        if (lastDuration > 0 && s.duration() > 0 && lastDuration != s.duration()) {
            progress = UpgradeLogic.rescaleProgress(progress, lastDuration, s.duration());
        }
        lastDuration = s.duration();
        return s;
    }

    private int upgrades(UpgradeItem.Kind upgrade) {
        int n = 0;
        // Only real upgrade slots (JAR InvSlotUpgrade) — skip discharge/input.
        for (int i = 0; i < SIZE; i++) {
            if (MachineGui.role(kind, i) != MachineGui.Role.UPGRADE) {
                continue;
            }
            if (items.get(i).getItem() instanceof UpgradeItem item && item.kind() == upgrade) {
                n += items.get(i).getCount();
            }
        }
        return n;
    }

    private int capacity() {
        return stats().capacity();
    }

    /** Storage upgrades shrink live capacity; clamp so EU above the new max is discarded. */
    private void clampEnergyToCapacity() {
        int cap = capacity();
        if (energy > cap) {
            energy = cap;
        }
    }

    private boolean allowOutput() {
        // JAR TileEntityElectricBlock: modes 4/5 gate on redstone input.
        boolean powered = level != null && level.hasNeighborSignal(worldPosition);
        return switch (mode) {
            case 4 -> !powered;
            case 5 -> !powered || energy >= capacity();
            default -> true;
        };
    }

    private void ensureNet(ServerLevel level) {
        if (!netted) {
            EnergyNet.of(level).add(this);
            netted = true;
        }
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel server) {
            EnergyNet.of(server).remove(worldPosition);
        }
        super.setRemoved();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, this);
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("energy", energy);
        output.putInt("progress", progress);
        output.putInt("burn", burnTime);
        output.putInt("burnMax", burnMax);
        output.putInt("lava", lava);
        output.putInt("mode", mode);
        output.putInt("waterFuel", waterFuel);
        output.putInt("micro", microStorage);
        output.putLong("emitted", totalEmitted);
        output.putLong("sunken", totalSunken);
        output.putBoolean("canProvideLiquid", canProvideLiquid);
        output.putInt("liquidX", liquidX);
        output.putInt("liquidY", liquidY);
        output.putInt("liquidZ", liquidZ);
        output.putInt("lastMineMode", lastMineMode);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        energy = input.getIntOr("energy", 0);
        clampEnergyToCapacity();
        progress = input.getIntOr("progress", 0);
        burnTime = input.getIntOr("burn", 0);
        burnMax = input.getIntOr("burnMax", burnTime);
        lava = input.getIntOr("lava", 0);
        mode = input.getIntOr("mode", 0);
        waterFuel = input.getIntOr("waterFuel", 0);
        microStorage = input.getIntOr("micro", 0);
        totalEmitted = input.getLongOr("emitted", 0);
        totalSunken = input.getLongOr("sunken", 0);
        canProvideLiquid = input.getBooleanOr("canProvideLiquid", false);
        liquidX = input.getIntOr("liquidX", 0);
        liquidY = input.getIntOr("liquidY", 0);
        liquidZ = input.getIntOr("liquidZ", 0);
        lastMineMode = input.getIntOr("lastMineMode", 0);
    }

    @Override
    public BlockPos energyPos() {
        return worldPosition;
    }

    @Override
    public EnergyNetLogic.Role energyRole() {
        return switch (kind.type) {
            case GENERATOR, GEO, SOLAR, WATER, WIND -> EnergyNetLogic.Role.SOURCE;
            case STORAGE, TRANSFORMER -> EnergyNetLogic.Role.BOTH;
            default -> EnergyNetLogic.Role.SINK;
        };
    }

    /** Tier for the miner: 3 if lapotron config enabled, else 1. */
    private int minerTier() {
        return OldustryConfig.MINER_LAPOTRON.get() ? 3 : 1;
    }

    @Override
    public boolean emits(Direction face) {
        Direction facing = getBlockState().getValue(MachineBlock.FACING);
        boolean powered = level != null && level.hasNeighborSignal(worldPosition);
        return switch (kind.type) {
            case GENERATOR, GEO, SOLAR, WATER, WIND -> true;
            case STORAGE -> allowOutput() && face == facing;
            case TRANSFORMER -> powered ? face == facing : face != facing;
            default -> false;
        };
    }

    @Override
    public boolean accepts(Direction face) {
        Direction facing = getBlockState().getValue(MachineBlock.FACING);
        boolean powered = level != null && level.hasNeighborSignal(worldPosition);
        return switch (kind.type) {
            case STORAGE -> face != facing;
            case TRANSFORMER -> powered ? face != facing : face == facing;
            case MACERATOR, EXTRACTOR, COMPRESSOR, RECYCLER, FURNACE, LUMINATOR, MINING, PUMPING, CANNING, MAGNETIZER, INDUCTION -> true;
            // Electrolyzer is NOT an EnergyNet sink — only drains adjacent storage.
            default -> false;
        };
    }

    @Override
    public int demand() {
        if (kind.type == MachineKind.Type.ELECTROLYZER) {
            return 0; // not an EnergyNet sink
        }
        return Math.max(0, capacity() - energy);
    }

    @Override
    public int acceptEnergy(int amount) {
        // JAR TileEntityElectricMachine.injectEnergy: overvolt before buffer fill.
        if (amount > maxSafeInput()) {
            if (level instanceof ServerLevel server) {
                explode(server);
            }
            return 0;
        }
        int take = Math.min(amount, demand());
        energy += take;
        setChanged();
        return amount - take;
    }

    @Override
    public int offerEnergy() {
        if (kind.type == MachineKind.Type.STORAGE && !allowOutput()) {
            return 0;
        }
        int packet = kind.type == MachineKind.Type.TRANSFORMER && level != null && level.hasNeighborSignal(worldPosition)
            ? kind.highPacket()
            : kind.packet;
        if (kind.type == MachineKind.Type.WIND) {
            return energy > 0 ? Math.min(energy, WindLogic.MAX_OUTPUT) : 0;
        }
        return energy >= packet ? packet : 0;
    }

    @Override
    public int emitBursts() {
        // JAR TileEntityTransformer: redstone→1×high; else up to 4×low per tick.
        if (kind.type == MachineKind.Type.TRANSFORMER
            && (level == null || !level.hasNeighborSignal(worldPosition))) {
            return 4;
        }
        return 1;
    }

    @Override
    public void takeEnergy(int amount) {
        energy = Math.max(0, energy - amount);
        setChanged();
    }

    @Override
    public int maxSafeInput() {
        // JAR TileEntityTransformer.getMaxSafeInput: redstone→low; else high (HV high=2048 → ∞).
        if (kind.type == MachineKind.Type.TRANSFORMER) {
            boolean powered = level != null && level.hasNeighborSignal(worldPosition);
            if (powered) {
                return kind.packet;
            }
            int high = kind.highPacket();
            return high == 2048 ? Integer.MAX_VALUE : high;
        }
        int tier = kind.type == MachineKind.Type.MINING ? minerTier() : stats().tier();
        return UpgradeLogic.maxSafeInput(tier);
    }

    @Override
    public long totalEmitted() {
        return totalEmitted;
    }

    @Override
    public long totalSunken() {
        return totalSunken;
    }

    @Override
    public void addEmitted(long amount) {
        totalEmitted += amount;
    }

    @Override
    public void addSunken(long amount) {
        totalSunken += amount;
    }

    @Override
    public void explode(ServerLevel level) {
        // JAR explodeMachineAt: wipe block + ExplosionIC2 — no machine casing refund.
        BlockPos pos = worldPosition;
        level.removeBlock(pos, false);
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2.5f, false, Level.ExplosionInteraction.NONE);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.oldustry." + kind.getSerializedName());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MachineMenu(id, inventory, this, data, kind);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, net.minecraft.network.RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(kind);
    }

    private int fuelGauge() {
        return switch (kind.type) {
            case GENERATOR -> burnTime;
            case GEO -> lava;
            case WATER -> waterFuel > 0 ? waterFuel : lastGen;
            case WIND -> WindLogic.gaugeFuel(subproduction, 14);
            case PUMPING -> lava;  // pumpCharge
            case INDUCTION -> burnTime; // heat 0..10000
            default -> 0;
        };
    }

    private int fuelGaugeMax() {
        return switch (kind.type) {
            case GENERATOR -> Math.max(1, burnMax);
            case GEO -> 24000;
            case WATER -> 2000;
            case WIND -> 14;
            case PUMPING -> PUMP_CHARGE_TARGET;
            case INDUCTION -> 10000;
            default -> 1;
        };
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack out = ContainerHelper.removeItem(items, slot, amount);
        clampEnergyToCapacity();
        setChanged();
        return out;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack out = ContainerHelper.takeItem(items, slot);
        clampEnergyToCapacity();
        return out;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        clampEnergyToCapacity();
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        clampEnergyToCapacity();
    }

    @Override
    public int[] getSlotsForFace(Direction face) {
        return switch (kind.type) {
            case MINING -> new int[] { 0, 1, 2, 4, 5, 6 };
            case PUMPING -> new int[] { 0, 1 };
            case CANNING -> new int[] { 0, 1, 2 };
            case ELECTROLYZER -> new int[] { 0, 1 };
            case INDUCTION -> new int[] { 0, 1, 3, 4 };
            default -> new int[] { 0, 1 };
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
        MachineGui.Role role = MachineGui.role(kind, slot);
        if (role == MachineGui.Role.FUEL) {
            return true;
        }
        if (role != MachineGui.Role.INPUT) {
            return false;
        }
        // Match MachineMenu.mayPlaceInput — hoppers must not dump junk into process slot.
        return switch (kind.type) {
            case MACERATOR -> !MachineRecipes.macerator(stack.getItem()).isEmpty();
            case EXTRACTOR -> !MachineRecipes.extractor(stack.getItem()).isEmpty();
            case COMPRESSOR -> !MachineRecipes.compressor(stack.getItem()).isEmpty();
            case RECYCLER -> !MachineRecipes.recyclerBlacklist(stack.getItem());
            case FURNACE, INDUCTION -> level != null
                && level.recipeAccess().propertySet(net.minecraft.world.item.crafting.RecipePropertySet.FURNACE_INPUT).test(stack);
            case MINING -> switch (slot) {
                case 0 -> stack.is(ModItems.MINING_DRILL.get()) || stack.is(ModItems.DIAMOND_DRILL.get());
                case 1 -> stack.getItem() instanceof ScannerItem;
                case 2 -> stack.is(ModItems.MINING_PIPE.get());
                default -> false;
            };
            case PUMPING -> stack.is(ModItems.EMPTY_CELL.get());
            case CANNING -> switch (slot) {
                case 0 -> stack.has(DataComponents.FOOD)
                    || stack.is(ModItems.COALFUEL_CELL.get())
                    || stack.is(ModItems.BIOFUEL_CELL.get())
                    || stack.is(ModItems.CF_PELLET.get());
                case 1 -> stack.is(ModItems.TIN_CAN.get())
                    || stack.is(ModItems.FUEL_CAN.get())
                    || stack.is(ModItems.FUEL_JETPACK.get())
                    || stack.is(ModItems.CF_PACK.get());
                default -> false;
            };
            case ELECTROLYZER -> switch (slot) {
                case 0 -> stack.is(ModItems.WATER_CELL.get());
                case 1 -> stack.is(ModItems.ELECTROLYZED_WATER_CELL.get());
                default -> false;
            };
            default -> true;
        };
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        // Electrolyzer both slots are IO (JAR InvSlot.Access.IO).
        if (kind.type == MachineKind.Type.ELECTROLYZER) {
            return slot == 0 || slot == 1;
        }
        return MachineGui.role(kind, slot) == MachineGui.Role.OUTPUT;
    }

    private static MachineKind kindOf(BlockState state) {
        return state.getBlock() instanceof MachineBlock machine ? machine.kind : MachineKind.GENERATOR;
    }
}
