package com.oldustry.block;

import com.oldustry.Oldustry;
import com.oldustry.OldustryConfig;
import com.oldustry.energy.EnergyNet;
import com.oldustry.energy.EnergyNetLogic;
import com.oldustry.energy.EnergyTile;
import com.oldustry.energy.OldustryDamage;
import com.oldustry.explosion.OldustryExplosion;
import com.oldustry.reactor.OldustryReactor;
import com.oldustry.reactor.OldustryReactorComponent;
import com.oldustry.reactor.OldustryReactorGrid;
import com.oldustry.registry.OldustryBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * IC2 {@code TileEntityNuclearReactorElectric}. Grid is 6×9; chambers open columns from the left.
 * EU is pushed every tick from the last cycle's output, then the remainder through adjacent chambers.
 */
public final class OldustryReactorBlockEntity extends BlockEntity implements OldustryReactorGrid, EnergyTile, WorldlyContainer, MenuProvider {
    private final NonNullList<ItemStack> items = NonNullList.withSize(OldustryReactor.SLOTS, ItemStack.EMPTY);
    private int heat;
    private int maxHeat = OldustryReactor.BASE_HEAT;
    private float hem = 1f;
    private float output;
    private int ticker = -1;
    private boolean exploding;
    private boolean netted;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> heat;
                case 1 -> maxHeat;
                case 2 -> (int) output;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (level != null && !level.isClientSide()) {
                return;
            }
            switch (index) {
                case 0 -> heat = value;
                case 1 -> maxHeat = value;
                case 2 -> output = value;
                default -> {}
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public OldustryReactorBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryBlockEntities.NUCLEAR_REACTOR.get(), pos, state);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, OldustryReactorBlockEntity reactor) {
        reactor.ensureNet(level);
        reactor.tick(level, pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, OldustryReactorBlockEntity reactor) {
        com.oldustry.client.sound.MachineLoopSound.syncReactor(pos);
    }

    private void tick(ServerLevel level, BlockPos pos, BlockState state) {
        if (ticker < 0) {
            ticker = level.getRandom().nextInt(OldustryReactor.TICK_RATE);
        }
        int multiplier = OldustryConfig.REACTOR_OUTPUT.get();
        int left = EnergyNet.of(level).emitAmount(level, this, OldustryReactor.sent((int) output, multiplier));
        for (Direction dir : Direction.values()) {
            if (left <= 0) {
                break;
            }
            BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
            if (neighbor instanceof OldustryReactorChamberBlockEntity chamber) {
                left = EnergyNet.of(level).emitAmount(level, chamber, left);
            }
        }
        if (++ticker % OldustryReactor.TICK_RATE != 0) {
            return;
        }
        if (!level.hasChunksAt(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
            output = 0f;
        } else {
            dropUnfitting(level, pos);
            output = 0f;
            maxHeat = OldustryReactor.BASE_HEAT;
            hem = 1f;
            int size = columns();
            for (int y = 0; y < OldustryReactor.ROWS; y++) {
                for (int x = 0; x < size; x++) {
                    ItemStack stack = itemAt(x, y);
                    if (!stack.isEmpty() && stack.getItem() instanceof OldustryReactorComponent component) {
                        component.processChamber(this, stack, x, y);
                    }
                }
            }
            if (heatEffects(level, pos)) {
                return;
            }
            boolean on = OldustryReactor.lit(heat, output);
            if (state.getValue(MachineBlock.ACTIVE) != on) {
                level.setBlock(pos, state.setValue(MachineBlock.ACTIVE, on), Block.UPDATE_CLIENTS);
            }
            setChanged();
        }
        sync(level);
    }

    private boolean heatEffects(ServerLevel level, BlockPos pos) {
        float cap = OldustryConfig.REACTOR_POWER.get().floatValue();
        if (!OldustryReactor.heatEffects(heat, cap)) {
            return false;
        }
        float ratio = OldustryReactor.ratio(heat, maxHeat);
        if (OldustryReactor.melts(ratio)) {
            explode(level, pos);
            return true;
        }
        if (OldustryReactor.lavaRoll(ratio, hem, level.getRandom().nextFloat())) {
            BlockPos at = randomAround(level, pos, 2);
            if (at != null) {
                BlockState state = level.getBlockState(at);
                if (state.isAir()) {
                    level.setBlock(at, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
                } else if (state.getDestroySpeed(level, at) <= -1f) {
                    level.setBlock(at, melts(state) ? Blocks.LAVA.defaultBlockState() : Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
        if (OldustryReactor.hurts(ratio)) {
            AABB box = new AABB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4);
            int amount = OldustryReactor.hurt(level.getRandom().nextInt(4), hem);
            if (amount > 0) {
                for (var entity : level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, box)) {
                    entity.hurtServer(level, OldustryDamage.radiation(level), amount);
                }
            }
        }
        if (OldustryReactor.evaporates(ratio, hem, level.getRandom().nextFloat())) {
            BlockPos at = randomAround(level, pos, 2);
            if (at != null && level.getBlockState(at).is(Blocks.WATER)) {
                level.setBlock(at, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        if (OldustryReactor.ignites(ratio, hem, level.getRandom().nextFloat())) {
            BlockPos at = randomAround(level, pos, 2);
            if (at != null && catches(level.getBlockState(at))) {
                level.setBlock(at, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        return false;
    }

    /** Unbreakable rock, metal, lava, dirt or sand becomes lava. Anything else unbreakable becomes fire. */
    private static boolean melts(BlockState state) {
        return state.is(Blocks.BEDROCK)
            || state.is(net.minecraft.tags.BlockTags.BASE_STONE_OVERWORLD)
            || state.is(net.minecraft.tags.BlockTags.BASE_STONE_NETHER)
            || state.is(net.minecraft.tags.BlockTags.DIRT)
            || state.is(net.minecraft.tags.BlockTags.SAND)
            || state.is(Blocks.GRAVEL)
            || state.is(Blocks.IRON_BLOCK)
            || state.is(Blocks.GOLD_BLOCK)
            || state.is(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS_COPPER)
            || state.is(Blocks.ANVIL)
            || state.getFluidState().is(net.minecraft.world.level.material.Fluids.LAVA);
    }

    /** JAR materials wood, leaves and cloth. */
    private static boolean catches(BlockState state) {
        return state.is(net.minecraft.tags.BlockTags.LOGS)
            || state.is(net.minecraft.tags.BlockTags.PLANKS)
            || state.is(net.minecraft.tags.BlockTags.LEAVES)
            || state.is(net.minecraft.tags.BlockTags.WOOL)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_STAIRS)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_SLABS)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_FENCES)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_DOORS)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_TRAPDOORS);
    }

    private BlockPos randomAround(ServerLevel level, BlockPos pos, int radius) {
        int x = pos.getX() + level.getRandom().nextInt(2 * radius + 1) - radius;
        int y = pos.getY() + level.getRandom().nextInt(2 * radius + 1) - radius;
        int z = pos.getZ() + level.getRandom().nextInt(2 * radius + 1) - radius;
        if (x == pos.getX() && y == pos.getY() && z == pos.getZ()) {
            return null;
        }
        return new BlockPos(x, y, z);
    }

    private void explode(ServerLevel level, BlockPos pos) {
        float[] influence = new float[OldustryReactor.SLOTS];
        int n = 0;
        int size = columns();
        for (int y = 0; y < OldustryReactor.ROWS; y++) {
            for (int x = 0; x < size; x++) {
                ItemStack stack = itemAt(x, y);
                if (!stack.isEmpty() && stack.getItem() instanceof OldustryReactorComponent component) {
                    influence[n++] = component.influenceExplosion(this, stack);
                }
                setItemAt(x, y, ItemStack.EMPTY);
            }
        }
        float[] used = new float[n];
        System.arraycopy(influence, 0, used, 0, n);
        float power = OldustryReactor.blast(hem, used, OldustryConfig.REACTOR_POWER.get().floatValue());
        Oldustry.LOGGER.info(
            "Nuclear Reactor at {}:({},{},{}) melted (explosion power {})",
            level.dimension().identifier(),
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            power
        );
        exploding = true;
        for (Direction dir : Direction.values()) {
            BlockPos side = pos.relative(dir);
            if (level.getBlockEntity(side) instanceof OldustryReactorChamberBlockEntity) {
                level.setBlock(side, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        level.removeBlock(pos, false);
        new OldustryExplosion(level, null, Vec3.atCenterOf(pos), power, OldustryReactor.BLAST_DROP, true).detonate();
    }

    /**
     * Hidden columns are not aliased onto the last visible one. A hopper writing that alias
     * would clobber the column the player can see, so anything outside the open grid is ejected.
     */
    private void dropUnfitting(ServerLevel level, BlockPos pos) {
        int size = columns();
        for (int x = 0; x < OldustryReactor.WIDTH; x++) {
            for (int y = 0; y < OldustryReactor.ROWS; y++) {
                ItemStack stack = itemAt(x, y);
                if (stack.isEmpty()) {
                    continue;
                }
                if (stack.getCount() <= 0 || x >= size || !OldustryReactor.useful(stack)) {
                    eject(level, pos, stack);
                    setItemAt(x, y, ItemStack.EMPTY);
                }
            }
        }
    }

    private void eject(ServerLevel level, BlockPos pos, ItemStack drop) {
        float span = 0.7f;
        double dx = level.getRandom().nextFloat() * span + (1f - span) * 0.5;
        double dy = level.getRandom().nextFloat() * span + (1f - span) * 0.5;
        double dz = level.getRandom().nextFloat() * span + (1f - span) * 0.5;
        ItemEntity entity = new ItemEntity(level, pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz, drop);
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
    }

    public int columns() {
        if (level == null) {
            return OldustryReactor.WIDTH;
        }
        int count = OldustryReactor.BASE_COLUMNS;
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(worldPosition.relative(dir)) instanceof OldustryReactorChamberBlockEntity) {
                count++;
            }
        }
        return OldustryReactor.columns(count - OldustryReactor.BASE_COLUMNS);
    }

    public int heat() {
        return heat;
    }

    public int maxHeat() {
        return maxHeat;
    }

    public int output() {
        return (int) output;
    }

    public ContainerData data() {
        return data;
    }

    private void sync(ServerLevel level) {
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    private void ensureNet(ServerLevel level) {
        if (!netted) {
            EnergyNet.of(level).add(this);
            netted = true;
        }
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel server && netted) {
            EnergyNet.of(server).remove(worldPosition);
            netted = false;
        }
        super.setRemoved();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && !exploding) {
            Containers.dropContents(level, pos, this);
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public boolean produceEnergy() {
        return level != null && level.hasNeighborSignal(worldPosition) && OldustryConfig.REACTOR_OUTPUT.get() != 0;
    }

    @Override
    public ItemStack itemAt(int x, int y) {
        if (x < 0 || x >= OldustryReactor.WIDTH || y < 0 || y >= OldustryReactor.ROWS) {
            return ItemStack.EMPTY;
        }
        return items.get(x + y * OldustryReactor.WIDTH);
    }

    @Override
    public void setItemAt(int x, int y, ItemStack stack) {
        if (x < 0 || x >= OldustryReactor.WIDTH || y < 0 || y >= OldustryReactor.ROWS) {
            return;
        }
        items.set(x + y * OldustryReactor.WIDTH, stack);
        setChanged();
    }

    @Override
    public float addOutput(float energy) {
        return output += energy;
    }

    @Override
    public int addHeat(int amount) {
        heat += amount;
        return heat;
    }

    @Override
    public void setHeat(int heat) {
        this.heat = heat;
    }

    @Override
    public void setMaxHeat(int maxHeat) {
        this.maxHeat = maxHeat;
    }

    @Override
    public float heatEffect() {
        return hem;
    }

    @Override
    public void setHeatEffect(float modifier) {
        hem = modifier;
    }

    @Override
    public RandomSource random() {
        return level == null ? RandomSource.create() : level.getRandom();
    }

    @Override
    public BlockPos energyPos() {
        return worldPosition;
    }

    @Override
    public EnergyNetLogic.Role energyRole() {
        return EnergyNetLogic.Role.SOURCE;
    }

    @Override
    public int demand() {
        return 0;
    }

    @Override
    public int acceptEnergy(int amount) {
        return amount;
    }

    @Override
    public int offerEnergy() {
        return 0;
    }

    @Override
    public void takeEnergy(int amount) {}

    @Override
    public Component getDisplayName() {
        return Component.translatable(com.oldustry.registry.OldustryBlocks.NUCLEAR_REACTOR.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new OldustryReactorMenu(id, inventory, this, data, columns());
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, net.minecraft.network.RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
        buffer.writeByte(columns());
    }

    @Override
    public int getContainerSize() {
        return OldustryReactor.SLOTS;
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
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack out = ContainerHelper.removeItem(items, slot, amount);
        setChanged();
        return out;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < items.size()) {
            items.set(slot, stack);
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return OldustryReactor.slotX(slot) < columns() && OldustryReactor.useful(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction face) {
        int size = columns();
        int[] slots = new int[size * OldustryReactor.ROWS];
        int n = 0;
        for (int y = 0; y < OldustryReactor.ROWS; y++) {
            for (int x = 0; x < size; x++) {
                slots[n++] = x + y * OldustryReactor.WIDTH;
            }
        }
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        return OldustryReactor.slotX(slot) < columns();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("heat", heat);
        output.putInt("output", (int) this.output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items.clear();
        ContainerHelper.loadAllItems(input, items);
        heat = input.getIntOr("heat", 0);
        output = input.getIntOr("output", 0);
    }
}
