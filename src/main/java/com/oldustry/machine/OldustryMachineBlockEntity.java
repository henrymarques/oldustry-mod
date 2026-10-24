package com.oldustry.machine;

import com.oldustry.client.sound.MachineLoopSound;

import com.oldustry.Oldustry;
import com.oldustry.block.MachineBlock;
import com.oldustry.energy.ElectricCharge;
import com.oldustry.energy.EnergyNet;
import com.oldustry.energy.EnergyNetLogic;
import com.oldustry.energy.EnergyTile;
import com.oldustry.energy.UpgradeLogic;
import com.oldustry.energy.WindLogic;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.UpgradeItem;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class OldustryMachineBlockEntity extends BlockEntity implements EnergyTile, WorldlyContainer, MenuProvider {
    public static final int SIZE = 7;
    protected final OldustryMachineKind kind;
    protected final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    protected int energy;
    protected int progress;
    private long totalEmitted;
    private long totalSunken;
    private boolean netted;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case OldustryMachineMenu.DATA_ENERGY -> energy;
                case OldustryMachineMenu.DATA_CAPACITY -> capacity();
                case OldustryMachineMenu.DATA_PROGRESS -> progress;
                case OldustryMachineMenu.DATA_DURATION -> processDuration();
                case OldustryMachineMenu.DATA_GAUGE -> fuelGauge();
                case OldustryMachineMenu.DATA_GAUGE_MAX_OR_MINER_MODE -> kind.type == OldustryMachineKind.Type.MINING ? ((OldustryMinerBlockEntity) OldustryMachineBlockEntity.this).mineMode() : fuelGaugeMax();
                case OldustryMachineMenu.DATA_EXTRA_OR_MINER_ACTIVE -> kind.type == OldustryMachineKind.Type.MINING
                    ? (getBlockState().getValue(MachineBlock.ACTIVE) ? 1 : 0)
                    : kind.type == OldustryMachineKind.Type.SOLAR ? ((OldustryGeneratorBlockEntity) OldustryMachineBlockEntity.this).sun() : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == OldustryMachineMenu.DATA_ENERGY) {
                energy = value;
            }
        }

        @Override
        public int getCount() {
            return OldustryMachineMenu.DATA_COUNT;
        }
    };

    public static OldustryMachineBlockEntity create(BlockPos pos, BlockState state) {
        return switch (kindOf(state)) {
            case MACERATOR -> new OldustryMaceratorBlockEntity(pos, state);
            case EXTRACTOR -> new OldustryExtractorBlockEntity(pos, state);
            case COMPRESSOR -> new OldustryCompressorBlockEntity(pos, state);
            case RECYCLER -> new OldustryRecyclerBlockEntity(pos, state);
            case ELECTRIC_FURNACE -> new OldustryElectricFurnaceBlockEntity(pos, state);
            case MINER -> new OldustryMinerBlockEntity(pos, state);
            case PUMP -> new OldustryPumpBlockEntity(pos, state);
            case CANNER -> new OldustryCannerBlockEntity(pos, state);
            case ELECTROLYZER -> new OldustryElectrolyzerBlockEntity(pos, state);
            case MAGNETIZER -> new OldustryMagnetizerBlockEntity(pos, state);
            case INDUCTION -> new OldustryInductionBlockEntity(pos, state);
            case BATBOX, MFE, MFSU -> new OldustryStorageBlockEntity(kindOf(state), pos, state);
            case GENERATOR, GEO_GENERATOR, SOLAR_PANEL, WATER_MILL, WIND_MILL -> new OldustryGeneratorBlockEntity(kindOf(state), pos, state);
            case LUMINATOR -> new OldustryLuminatorBlockEntity(pos, state);
            case LV_TRANSFORMER, MV_TRANSFORMER, HV_TRANSFORMER -> new OldustryTransformerBlockEntity(kindOf(state), pos, state);
        };
    }

    protected OldustryMachineBlockEntity(OldustryMachineKind kind, BlockPos pos, BlockState state) {
        super(OldustryBlockEntities.MACHINE.get(), pos, state);
        this.kind = kind;
    }

    public OldustryMachineKind kind() {
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
            case CANNING -> ((OldustryCannerBlockEntity) this).cannerGaugeDuration();
            case MINING -> ((OldustryMinerBlockEntity) this).mineDuration();
            default -> 0;
        };
    }

    public void cycleMode(Player player) {
        if (this instanceof OldustryStorageBlockEntity storage) storage.cycleMode(player);
    }

    public int redstonePower() {
        return this instanceof OldustryStorageBlockEntity storage ? storage.redstonePower() : 0;
    }

    public int comparator() {
        int duration = kind.duration();
        if (duration > 0) {
            return Math.min(15, progress * 15 / duration);
        }
        int cap = capacity();
        return cap <= 0 ? 0 : Math.min(15, energy * 15 / cap);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, OldustryMachineBlockEntity be) {
        // Process before EnergyNet so this tick's EU spend opens demand for overvolt inject (JAR-like).
        be.ensureNet(level);
        be.tickMachine(level, pos);
        EnergyNet.of(level).tick(level);
    }

    /** Client ticker: keep loop sound while ACTIVE; stop immediately when cleared/broken. */
    public static void clientTick(Level level, BlockPos pos, BlockState state, OldustryMachineBlockEntity be) {
        if (state.getValue(MachineBlock.ACTIVE)) {
            Oldustry.clientMachineLoop.accept(pos, be.kind());
        } else {
            Oldustry.clientMachineLoopStop.accept(pos);
        }
    }

    private void tickMachine(ServerLevel level, BlockPos pos) {
        boolean active = switch (kind.type) {
            case GENERATOR, GEO, SOLAR, WATER, WIND -> ((OldustryGeneratorBlockEntity) this).tickGeneratorFamily(level, pos);
            case STORAGE -> ((OldustryStorageBlockEntity) this).tickStorage();
            case TRANSFORMER -> level.hasNeighborSignal(pos);
            case LUMINATOR -> ((OldustryLuminatorBlockEntity) this).tickLuminator(level, pos);
            case MINING -> ((OldustryMinerBlockEntity) this).tickMiner(level, pos);
            case PUMPING -> ((OldustryPumpBlockEntity) this).tickPump(level, pos);
            case CANNING -> ((OldustryCannerBlockEntity) this).tickCanner(level);
            case ELECTROLYZER -> ((OldustryElectrolyzerBlockEntity) this).tickElectrolyzer(level, pos);
            case MAGNETIZER -> ((OldustryMagnetizerBlockEntity) this).tickMagnetizer(level, pos);
            case INDUCTION -> {
                dischargeSlot(2);
                yield ((OldustryInductionBlockEntity) this).tickInduction(level, pos);
            }
            default -> {
                dischargeSlot(6);
                boolean work = ((OldustryProcessMachineBlockEntity) this).tickProcess(level);
                ((OldustryProcessMachineBlockEntity) this).tickEjector(level, pos);
                yield work;
            }
        };
        if (kind.type == OldustryMachineKind.Type.STORAGE) {
            level.updateNeighborsAt(pos, getBlockState().getBlock());
        }
        setActive(level, pos, active);
        // Loop sound is client-side (MachineLoopSound) so interrupt can stop it — JAR AudioSource.stop().
        setChanged();
    }

    private void setActive(ServerLevel level, BlockPos pos, boolean active) {
        BlockState state = getBlockState();
        if (state.hasProperty(MachineBlock.ACTIVE) && state.getValue(MachineBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(MachineBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }

    protected final ItemStack smelt(ServerLevel level, ItemStack in) {
        SingleRecipeInput input = new SingleRecipeInput(in);
        return level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level)
            .map(holder -> holder.value().assemble(input))
            .orElse(ItemStack.EMPTY);
    }

    protected static boolean canOutput(ItemStack slot, ItemStack result) {
        return slot.isEmpty()
            || ItemStack.isSameItemSameComponents(slot, result) && slot.getCount() + result.getCount() <= slot.getMaxStackSize();
    }

    protected final void mergeOut(int slotIndex, ItemStack result) {
        ItemStack slot = items.get(slotIndex);
        if (slot.isEmpty()) {
            items.set(slotIndex, result.copy());
        } else {
            slot.grow(result.getCount());
        }
    }

    protected final void chargeSlot(int slot) {
        ItemStack stack = items.get(slot);
        if (stack.getItem() instanceof ElectricItem electric && energy > 0) {
            energy -= electric.charge(stack, Math.min(energy, kind.packet));
        }
    }

    protected final void dischargeSlot(int slot) {
        ItemStack stack = items.get(slot);
        int room = capacity() - energy;
        if (room <= 0 || stack.isEmpty()) {
            return;
        }
        if (stack.getItem() instanceof ElectricItem electric) {
            energy += electric.discharge(stack, Math.min(room, kind.packet), stats().tier());
            return;
        }
        // SU battery is 1200, redstone is 800. Eat one only when the free buffer holds that whole value.
        // On macerator-family machines the single-use also waits for a storage upgrade.
        int once = stack.getItem() == OldustryItems.SU_BATTERY.get() ? 1200 : stack.getItem() == Items.REDSTONE ? 800 : 0;
        boolean storageGate = once == 1200 && kind.suNeedsStorageUpgrade();
        int gained = ElectricCharge.consumeOnce(room, once, storageGate, upgrades(UpgradeItem.Kind.STORAGE));
        if (gained <= 0) {
            return;
        }
        energy += gained;
        stack.shrink(1);
        if (stack.isEmpty()) {
            items.set(slot, ItemStack.EMPTY);
        }
    }

    /** Last applied process duration — rescale progress when OC changes length (JAR setOverclockRates). */
    private int lastDuration = -1;

    protected final UpgradeLogic.Stats stats() {
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

    protected final int upgrades(UpgradeItem.Kind upgrade) {
        int n = 0;
        // Only real upgrade slots (JAR InvSlotUpgrade) — skip discharge/input.
        for (int i = 0; i < SIZE; i++) {
            if (OldustryMachineSlots.role(kind, i) != OldustryMachineSlots.Role.UPGRADE) {
                continue;
            }
            if (items.get(i).getItem() instanceof UpgradeItem item && item.kind() == upgrade) {
                n += items.get(i).getCount();
            }
        }
        return n;
    }

    protected final int capacity() {
        return stats().capacity();
    }

    /** Storage upgrades shrink live capacity; clamp so EU above the new max is discarded. */
    private void clampEnergyToCapacity() {
        int cap = capacity();
        if (energy > cap) {
            energy = cap;
        }
    }

    void drainForElectrolyzer(int amount) {
        energy = Math.max(0, energy - amount);
        setChanged();
    }

    void fillFromElectrolyzer(int amount) {
        energy = Math.min(capacity(), energy + amount);
        setChanged();
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
        output.putLong("emitted", totalEmitted);
        output.putLong("sunken", totalSunken);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        energy = input.getIntOr("energy", 0);
        clampEnergyToCapacity();
        progress = input.getIntOr("progress", 0);
        totalEmitted = input.getLongOr("emitted", 0);
        totalSunken = input.getLongOr("sunken", 0);
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

    @Override
    public boolean emits(Direction face) {
        Direction facing = getBlockState().getValue(MachineBlock.FACING);
        boolean powered = level != null && level.hasNeighborSignal(worldPosition);
        return switch (kind.type) {
            case GENERATOR, GEO, SOLAR, WATER, WIND -> true;
            case STORAGE -> ((OldustryStorageBlockEntity) this).allowOutput() && face == facing;
            default -> false;
        };
    }

    @Override
    public boolean accepts(Direction face) {
        Direction facing = getBlockState().getValue(MachineBlock.FACING);
        boolean powered = level != null && level.hasNeighborSignal(worldPosition);
        return switch (kind.type) {
            case STORAGE -> face != facing;
            case MACERATOR, EXTRACTOR, COMPRESSOR, RECYCLER, FURNACE, LUMINATOR, MINING, PUMPING, CANNING, MAGNETIZER, INDUCTION -> true;
            // Electrolyzer is NOT an EnergyNet sink — only drains adjacent storage.
            default -> false;
        };
    }

    @Override
    public int demand() {
        if (kind.type == OldustryMachineKind.Type.ELECTROLYZER) {
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
        if (kind.type == OldustryMachineKind.Type.STORAGE && !((OldustryStorageBlockEntity) this).allowOutput()) {
            return 0;
        }
        int packet = kind.packet;
        if (kind.type == OldustryMachineKind.Type.WIND) {
            return energy > 0 ? Math.min(energy, WindLogic.MAX_OUTPUT) : 0;
        }
        // Storage/transformer stay full-packet (JAR energy >= output). Generators emit the short remainder.
        if (kind.type == OldustryMachineKind.Type.GENERATOR
            || kind.type == OldustryMachineKind.Type.GEO
            || kind.type == OldustryMachineKind.Type.SOLAR
            || kind.type == OldustryMachineKind.Type.WATER) {
            return EnergyNetLogic.generatorPacket(energy, packet);
        }
        return energy >= packet ? packet : 0;
    }

    @Override
    public int emitBursts() {
        return 1;
    }

    @Override
    public void takeEnergy(int amount) {
        energy = Math.max(0, energy - amount);
        setChanged();
    }

    protected int inputTier() {
        return stats().tier();
    }

    @Override
    public int maxSafeInput() {
        int tier = inputTier();
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
        return new OldustryMachineMenu(id, inventory, this, data, kind);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, net.minecraft.network.RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(kind);
    }

    private int fuelGauge() {
        return switch (kind.type) {
            case GENERATOR, GEO, SOLAR, WATER, WIND -> ((OldustryGeneratorBlockEntity) this).gauge();
            case PUMPING -> ((OldustryPumpBlockEntity) this).pumpCharge();
            case INDUCTION -> ((OldustryInductionBlockEntity) this).heat(); // heat 0..10000
            default -> 0;
        };
    }

    private int fuelGaugeMax() {
        return switch (kind.type) {
            case GENERATOR, GEO, SOLAR, WATER, WIND -> ((OldustryGeneratorBlockEntity) this).gaugeMax();
            case PUMPING -> OldustryPumpBlockEntity.CHARGE_TARGET;
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
        return OldustryMachineSlots.slotsForFace(kind);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
        return OldustryMachineSlots.mayPlaceAutomation(kind, level, slot, stack, face);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        return OldustryMachineSlots.mayTakeAutomation(kind, slot, face);
    }

    private static OldustryMachineKind kindOf(BlockState state) {
        return state.getBlock() instanceof MachineBlock machine ? machine.kind : OldustryMachineKind.GENERATOR;
    }
}
