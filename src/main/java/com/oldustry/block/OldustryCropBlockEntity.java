package com.oldustry.block;

import java.util.ArrayList;
import java.util.List;

import com.oldustry.crop.CropSeed;
import com.oldustry.crop.OldustryCrop;
import com.oldustry.crop.OldustryCropMath;
import com.oldustry.item.HydratingCellItem;
import com.oldustry.item.WeedExItem;
import com.oldustry.item.tool.CropnalyzerItem;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** JAR {@code TileEntityCrop}. One crop step every 256 ticks. */
public class OldustryCropBlockEntity extends BlockEntity {
    private static final TagKey<net.minecraft.world.level.biome.Biome> SWAMP = biome("c", "is_swamp");
    private static final TagKey<net.minecraft.world.level.biome.Biome> FOREST = biome("c", "is_forest");
    private static final TagKey<net.minecraft.world.level.biome.Biome> JUNGLE = biome("c", "is_jungle");
    private static final TagKey<net.minecraft.world.level.biome.Biome> DESERT = biome("c", "is_desert");

    private int ticker = -1;
    private int cropId = -1;
    private int size;
    private int growth;
    private int gain;
    private int resistance;
    private int scan;
    private int points;
    private int nutrientsStored;
    private int waterStored;
    private int weedEx;
    private boolean upgraded;
    private int humidity = -1;
    private int nutrients = -1;
    private int air = -1;

    public OldustryCropBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryBlockEntities.CROP.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OldustryCropBlockEntity crop) {
        if (crop.ticker < 0) {
            crop.ticker = level.getRandom().nextInt(OldustryCropMath.TICK_RATE);
        }
        crop.ticker = (crop.ticker + 1) & 0xFFFF;
        int rate = OldustryCropMath.TICK_RATE;
        if (crop.ticker % rate == 0) {
            crop.step();
        }
    }

    private void step() {
        Level level = this.level;
        if (level == null) {
            return;
        }
        int rate = OldustryCropMath.TICK_RATE;
        if (ticker % (rate << 2) == 0) {
            humidity = measureHumidity();
        }
        if ((ticker + rate) % (rate << 2) == 0) {
            nutrients = measureNutrients();
        }
        if ((ticker + rate * 2) % (rate << 2) == 0) {
            air = measureAir();
        }
        if (cropId < 0) {
            if (!upgraded || !cross()) {
                if (level.getRandom().nextInt(100) == 0 && !spendWeedEx(5)) {
                    reset();
                    plant(OldustryCrop.WEED, 1, 0, 0, 0, 0);
                } else {
                    if (weedEx > 0 && level.getRandom().nextInt(10) == 0) {
                        weedEx--;
                        setChanged();
                    }
                    return;
                }
            }
        }
        OldustryCrop crop = card();
        if (crop.canGrow(size, light(), nutrients(), ironOrGold(Blocks.IRON_BLOCK), ironOrGold(Blocks.GOLD_BLOCK))) {
            int added = OldustryCropMath.growthRate(
                crop, growth, gain, resistance,
                OldustryCropMath.envHave(crop.weight(humidity(), nutrients(), air())),
                level.getRandom().nextInt(7), level.getRandom().nextInt(32)
            );
            if (added < 0) {
                reset();
                return;
            }
            points += added;
            if (cropId >= 0 && points >= crop.duration(size)) {
                points = 0;
                size++;
                sync();
            }
        }
        if (nutrientsStored > 0) {
            nutrientsStored--;
        }
        if (waterStored > 0) {
            waterStored--;
        }
        setChanged();
        if (crop.weed(size, growth) && level.getRandom().nextInt(50) - growth <= 2) {
            spreadWeed();
        }
    }

    public boolean use(Player player, ItemStack stack) {
        Level level = this.level;
        if (level == null) {
            return false;
        }
        if (level.isClientSide()) {
            return !stack.isEmpty() || card().canHarvest(size);
        }
        if (cropId < 0) {
            if (stack.is(OldustryItems.CROP.get()) && !upgraded) {
                take(player, stack, 1);
                upgraded = true;
                sync();
                return true;
            }
            return plantStack(player, stack);
        }
        if (stack.is(OldustryItems.FERTILIZER.get()) && applyFertilizer(true)) {
            take(player, stack, 1);
            return true;
        }
        if (stack.getItem() instanceof WeedExItem && WeedExItem.uses(stack) > 0 && applyWeedEx(true)) {
            WeedExItem.spend(stack);
            if (WeedExItem.uses(stack) <= 0) {
                take(player, stack, 1);
            }
            return true;
        }
        if (stack.getItem() instanceof HydratingCellItem && applyHydration(true, stack)) {
            if (HydratingCellItem.remaining(stack) <= 0) {
                take(player, stack, 1);
            }
            return true;
        }
        if ((stack.is(Items.WATER_BUCKET) || stack.is(OldustryItems.WATER_CELL.get())) && waterStored < 10) {
            waterStored = 10;
            setChanged();
            return true;
        }
        if (stack.getItem() instanceof CropnalyzerItem) {
            String desc = scannedDescription();
            player.sendSystemMessage(Component.literal(desc == null ? "Unknown Crop" : desc));
            return true;
        }
        if (card() == OldustryCrop.VENOMILIA && !player.isShiftKeyDown()) {
            poison(player);
        }
        return harvest(true);
    }

    /** JAR {@code TileEntityCrop.applyFertilizer}. Manual +100, matron +90. */
    public boolean applyFertilizer(boolean manual) {
        if (nutrientsStored >= 100) {
            return false;
        }
        nutrientsStored += manual ? 100 : 90;
        setChanged();
        return true;
    }

    /**
     * JAR {@code applyHydration}. Manual fills to 200; matron to 180.
     * Spends hydration units from the cell equal to the fill amount.
     */
    public boolean applyHydration(boolean manual, ItemStack cell) {
        if ((!manual && waterStored >= 180) || waterStored >= 200) {
            return false;
        }
        int want = manual ? (200 - waterStored) : (180 - waterStored);
        int taken = HydratingCellItem.consume(cell, want);
        if (taken <= 0) {
            return false;
        }
        waterStored += taken;
        setChanged();
        return true;
    }

    /** Matron path: pull hydration across fertilizer-adjacent cell stacks via a consumer. */
    public boolean applyHydration(boolean manual, java.util.function.IntUnaryOperator drain) {
        if ((!manual && waterStored >= 180) || waterStored >= 200) {
            return false;
        }
        int want = manual ? (200 - waterStored) : (180 - waterStored);
        int taken = drain.applyAsInt(want);
        if (taken <= 0) {
            return false;
        }
        waterStored += taken;
        setChanged();
        return true;
    }

    /** JAR {@code applyWeedEx}. Adds 50; may nibble stats when ex ≥ 75. */
    public boolean applyWeedEx(boolean manual) {
        if ((weedEx >= 100 && manual) || weedEx >= 150) {
            return false;
        }
        weedEx += 50;
        Level level = this.level;
        if (level != null) {
            boolean trigger = level.getRandom().nextInt(manual ? 5 : 3) == 0;
            if (cropId > 0 && weedEx >= 75 && trigger) {
                // JAR switch fall-through: case 0 also hits case 1.
                int roll = level.getRandom().nextInt(5);
                if (roll == 0 && growth > 0) {
                    growth--;
                }
                if (roll <= 1 && gain > 0) {
                    gain--;
                }
                if (resistance > 0) {
                    resistance--;
                }
            }
        }
        setChanged();
        return true;
    }

    /** JAR {@code getScanned}. */
    public String scannedDescription() {
        if (scan <= 0 || cropId < 0) {
            return null;
        }
        OldustryCrop crop = card();
        if (scan >= 4) {
            return crop.getSerializedName() + " - Gr: " + growth + " Ga: " + gain + " Re: " + resistance;
        }
        return crop.getSerializedName();
    }

    public void leftClick(Player player) {
        if (cropId < 0) {
            if (upgraded && level != null) {
                upgraded = false;
                Block.popResource(level, worldPosition, new ItemStack(OldustryItems.CROP.get()));
                sync();
            }
            return;
        }
        if (card() == OldustryCrop.WEED) {
            reset();
            return;
        }
        if (card() == OldustryCrop.VENOMILIA && !player.isShiftKeyDown()) {
            poison(player);
        }
        pick(true);
    }

    public void bumped(Entity entity) {
        if (!(entity instanceof LivingEntity living) || level == null || cropId < 0) {
            return;
        }
        if (card() == OldustryCrop.VENOMILIA && size == 5) {
            boolean careful = entity instanceof Player player && player.isShiftKeyDown() && level.getRandom().nextInt(50) != 0;
            if (!careful) {
                poison(living);
                size = 4;
                sync();
                return;
            }
        }
        if (card() == OldustryCrop.WEED || card() == OldustryCrop.REED || card() == OldustryCrop.STICKREED) {
            return;
        }
        if (living.isSprinting() && level.getRandom().nextInt(100) == 0 && level.getRandom().nextInt(40) > resistance) {
            reset();
            level.setBlock(worldPosition.below(), Blocks.DIRT.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    public void onNeighbor() {
    }

    private boolean plantStack(Player player, ItemStack stack) {
        CropSeed bred = stack.get(OldustryDataComponents.CROP_SEED.get());
        if (bred != null && bred.id() > 0) {
            take(player, stack, 1);
            plant(OldustryCrop.byId(bred.id()), 1, bred.growth(), bred.gain(), bred.resistance(), bred.scan());
            return true;
        }
        Base base = baseSeed(stack);
        if (base == null || stack.getCount() < base.count || upgraded) {
            return false;
        }
        take(player, stack, base.count);
        plant(base.crop, base.size, base.growth, base.gain, base.resistance, 1);
        return true;
    }

    private void plant(OldustryCrop crop, int nextSize, int nextGrowth, int nextGain, int nextResistance, int nextScan) {
        cropId = crop.id();
        size = nextSize;
        growth = nextGrowth;
        gain = nextGain;
        resistance = nextResistance;
        scan = nextScan;
        points = 0;
        sync();
    }

    private boolean harvest(boolean manual) {
        OldustryCrop crop = card();
        if (cropId < 0 || !crop.canHarvest(size) || level == null) {
            return false;
        }
        int drops = OldustryCropMath.gainDrops(crop.gainChance(), gain, () -> level.getRandom().nextFloat());
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < drops; i++) {
            ItemStack gainStack = gainStack();
            if (gainStack.isEmpty()) {
                continue;
            }
            if (level.getRandom().nextInt(100) <= gain) {
                gainStack.setCount(gainStack.getCount() + 1);
            }
            stacks.add(gainStack);
        }
        size = crop.sizeAfterHarvest(size, level.getRandom().nextInt(3));
        sync();
        for (ItemStack stack : stacks) {
            Block.popResource(level, worldPosition, stack);
        }
        return true;
    }

    private void pick(boolean manual) {
        if (cropId < 0 || level == null) {
            return;
        }
        boolean bonus = harvest(false);
        float first = OldustryCropMath.seedChance(size, card().tier());
        for (int i = 0; i < resistance; i++) {
            first *= 1.1f;
        }
        int drop = 0;
        if (bonus) {
            if (level.getRandom().nextFloat() <= (first + 1.0f) * 0.8f) {
                drop++;
            }
            float chance = OldustryCropMath.seedChance(size, card().tier()) + growth / 100.0f;
            if (!manual) {
                chance *= 0.8f;
            }
            for (int k = 23; k < gain; k++) {
                chance *= 0.95f;
            }
            if (level.getRandom().nextFloat() <= chance) {
                drop++;
            }
        } else if (level.getRandom().nextFloat() <= first * 1.5f) {
            drop++;
        }
        OldustryCrop crop = card();
        int seedGrowth = growth;
        int seedGain = gain;
        int seedResistance = resistance;
        int seedScan = scan;
        reset();
        for (int i = 0; i < drop; i++) {
            Block.popResource(level, worldPosition, seedStack(crop, seedGrowth, seedGain, seedResistance, seedScan));
        }
    }

    private boolean cross() {
        Level level = this.level;
        if (level == null || level.getRandom().nextInt(3) != 0) {
            return false;
        }
        List<OldustryCropBlockEntity> parents = new ArrayList<>();
        consider(parents, worldPosition.north());
        consider(parents, worldPosition.south());
        consider(parents, worldPosition.east());
        consider(parents, worldPosition.west());
        if (parents.size() < 2) {
            return false;
        }
        int[] ratios = new int[22];
        int total = 0;
        for (int id = 1; id < ratios.length; id++) {
            OldustryCrop candidate = OldustryCrop.byId(id);
            if (!candidate.canGrow(size, light(), nutrients(), ironOrGold(Blocks.IRON_BLOCK), ironOrGold(Blocks.GOLD_BLOCK))) {
                continue;
            }
            for (OldustryCropBlockEntity parent : parents) {
                ratios[id] += OldustryCropMath.ratio(candidate, parent.card());
            }
            total += ratios[id];
        }
        if (total <= 0) {
            return false;
        }
        int roll = level.getRandom().nextInt(total);
        int chosen = 1;
        for (int id = 0; id < ratios.length; id++) {
            if (ratios[id] > 0 && ratios[id] > roll) {
                chosen = id;
                break;
            }
            roll -= ratios[id];
        }
        int sumGrowth = 0;
        int sumGain = 0;
        int sumResistance = 0;
        for (OldustryCropBlockEntity parent : parents) {
            sumGrowth += parent.growth;
            sumGain += parent.gain;
            sumResistance += parent.resistance;
        }
        int count = parents.size();
        int span = 1 + 2 * count;
        upgraded = false;
        plant(
            OldustryCrop.byId(chosen),
            1,
            OldustryCropMath.crossedStat(sumGrowth, count, level.getRandom().nextInt(span)),
            OldustryCropMath.crossedStat(sumGain, count, level.getRandom().nextInt(span)),
            OldustryCropMath.crossedStat(sumResistance, count, level.getRandom().nextInt(span)),
            0
        );
        return true;
    }

    private void consider(List<OldustryCropBlockEntity> parents, BlockPos pos) {
        if (level != null && level.getBlockEntity(pos) instanceof OldustryCropBlockEntity parent && parent.cropId > 0) {
            OldustryCrop crop = parent.card();
            if (crop.canGrow(size, light(), nutrients(), ironOrGold(Blocks.IRON_BLOCK), ironOrGold(Blocks.GOLD_BLOCK)) && crop.canCross(parent.size)) {
                parents.add(parent);
            }
        }
    }

    private void spreadWeed() {
        if (level == null) {
            return;
        }
        int[] step = OldustryCropMath.weedStep(level.getRandom().nextInt(4));
        BlockPos side = worldPosition.offset(step[0], 0, step[1]);
        if (level.getBlockEntity(side) instanceof OldustryCropBlockEntity neighbor) {
            if (neighbor.cropId < 0 || (!neighbor.card().weed(neighbor.size, neighbor.growth) && level.getRandom().nextInt(32) >= neighbor.resistance && !neighbor.spendWeedEx(5))) {
                int nextGrowth = Math.max(growth, neighbor.growth);
                if (nextGrowth < 31 && level.getRandom().nextBoolean()) {
                    nextGrowth++;
                }
                neighbor.reset();
                neighbor.plant(OldustryCrop.WEED, 1, nextGrowth, 0, 0, 0);
            }
            return;
        }
        if (!level.getBlockState(side).isAir()) {
            return;
        }
        BlockState ground = level.getBlockState(side.below());
        if (ground.is(Blocks.DIRT) || ground.is(Blocks.GRASS_BLOCK) || ground.is(Blocks.FARMLAND)) {
            level.setBlock(side.below(), Blocks.DIRT.defaultBlockState(), Block.UPDATE_ALL);
            level.setBlock(side, Blocks.FERN.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private boolean spendWeedEx(int cost) {
        if (weedEx <= 0) {
            return false;
        }
        weedEx -= cost;
        if (weedEx < 0) {
            weedEx = 0;
        }
        setChanged();
        return true;
    }

    private void reset() {
        cropId = -1;
        size = 0;
        growth = 0;
        gain = 0;
        resistance = 0;
        scan = 0;
        points = 0;
        sync();
    }

    private void poison(LivingEntity living) {
        if (level == null) {
            return;
        }
        int seconds = level.getRandom().nextInt(10) + 5;
        living.addEffect(new MobEffectInstance(MobEffects.POISON, seconds * 20, 0));
    }

    private ItemStack gainStack() {
        Level level = this.level;
        OldustryCrop crop = card();
        return switch (crop) {
            case WHEAT -> new ItemStack(Items.WHEAT);
            case PUMPKIN -> new ItemStack(Items.PUMPKIN);
            case MELON -> level != null && level.getRandom().nextInt(3) == 0
                ? new ItemStack(Items.MELON)
                : new ItemStack(Items.MELON_SLICE, level == null ? 2 : level.getRandom().nextInt(4) + 2);
            case DANDELION -> dye(DyeColor.YELLOW);
            case ROSE -> dye(DyeColor.RED);
            case BLACKTHORN -> dye(DyeColor.BLACK);
            case TULIP -> dye(DyeColor.PURPLE);
            case CYAZINT -> dye(DyeColor.CYAN);
            case VENOMILIA -> size == 5 ? new ItemStack(OldustryItems.GRIN_POWDER.get()) : size >= 4 ? dye(DyeColor.PURPLE) : ItemStack.EMPTY;
            case REED -> new ItemStack(Items.SUGAR_CANE, Math.max(1, size - 1));
            case STICKREED -> size <= 3 ? new ItemStack(Items.SUGAR_CANE, Math.max(1, size - 1)) : new ItemStack(OldustryItems.STICKY_RESIN.get());
            case COCOA -> new ItemStack(Items.COCOA_BEANS);
            case FERRU -> new ItemStack(OldustryItems.SMALL_IRON_DUST.get());
            case AURELIA -> new ItemStack(Items.GOLD_NUGGET);
            case REDWHEAT -> level != null && (level.hasNeighborSignal(worldPosition) || level.getRandom().nextBoolean())
                ? new ItemStack(Items.REDSTONE)
                : new ItemStack(Items.WHEAT);
            case NETHER_WART -> new ItemStack(Items.NETHER_WART);
            case TERRA_WART -> new ItemStack(OldustryItems.TERRA_WART.get());
            case COFFEE -> size == 4 ? ItemStack.EMPTY : new ItemStack(OldustryItems.COFFEE_BEANS.get());
            case HOPS -> new ItemStack(OldustryItems.HOPS.get());
            case CARROTS -> new ItemStack(Items.CARROT);
            case POTATO -> level != null && level.getRandom().nextInt(50) == 0 ? new ItemStack(Items.POISONOUS_POTATO) : new ItemStack(Items.POTATO);
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack dye(DyeColor color) {
        return new ItemStack(com.oldustry.OldustryDyes.dye(color));
    }

    private ItemStack seedStack(OldustryCrop crop, int seedGrowth, int seedGain, int seedResistance, int seedScan) {
        if (crop.plainSeed(seedGrowth, seedGain, seedResistance)) {
            Item item = switch (crop) {
                case PUMPKIN -> Items.PUMPKIN_SEEDS;
                case MELON -> Items.MELON_SEEDS;
                default -> Items.WHEAT_SEEDS;
            };
            return new ItemStack(item);
        }
        ItemStack stack = new ItemStack(OldustryItems.CROP_SEED.get());
        stack.set(OldustryDataComponents.CROP_SEED.get(), new CropSeed(crop.id(), seedGrowth, seedGain, seedResistance, seedScan));
        return stack;
    }

    private void sync() {
        setChanged();
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        BlockState next = state
            .setValue(OldustryCropBlock.CROP, card())
            .setValue(OldustryCropBlock.SIZE, Math.max(0, Math.min(7, size)))
            .setValue(OldustryCropBlock.UPGRADED, upgraded);
        if (next != state) {
            level.setBlock(worldPosition, next, Block.UPDATE_CLIENTS);
        }
    }

    private OldustryCrop card() {
        return OldustryCrop.byId(cropId);
    }

    private int humidity() {
        if (humidity < 0) {
            humidity = measureHumidity();
        }
        return humidity;
    }

    private int nutrients() {
        if (nutrients < 0) {
            nutrients = measureNutrients();
        }
        return nutrients;
    }

    private int air() {
        if (air < 0) {
            air = measureAir();
        }
        return air;
    }

    private int measureHumidity() {
        if (level == null) {
            return 0;
        }
        return OldustryCropMath.humidity(biomeHumidity(), OldustryCropBlock.farmlandMoisture(level, worldPosition), waterStored);
    }

    private int measureNutrients() {
        if (level == null) {
            return 0;
        }
        int dirt = 0;
        for (int i = 2; i < 5; i++) {
            if (level.getBlockState(worldPosition.below(i)).is(Blocks.DIRT)) {
                dirt++;
            }
        }
        return OldustryCropMath.nutrients(biomeNutrients(), dirt, nutrientsStored);
    }

    private int measureAir() {
        if (level == null) {
            return 0;
        }
        int fresh = 9;
        for (int x = worldPosition.getX() - 1; x < worldPosition.getX() + 1 && fresh > 0; x++) {
            for (int z = worldPosition.getZ() - 1; z < worldPosition.getZ() + 1 && fresh > 0; z++) {
                BlockPos side = new BlockPos(x, worldPosition.getY(), z);
                BlockState state = level.getBlockState(side);
                if (state.isSolidRender() || level.getBlockEntity(side) instanceof OldustryCropBlockEntity) {
                    fresh--;
                }
            }
        }
        return OldustryCropMath.air(worldPosition.getY(), fresh, level.canSeeSky(worldPosition.above()));
    }

    private int light() {
        return level == null ? 0 : level.getMaxLocalRawBrightness(worldPosition);
    }

    private boolean ironOrGold(Block block) {
        if (level == null) {
            return false;
        }
        for (int i = 1; i < 4; i++) {
            BlockState state = level.getBlockState(worldPosition.below(i));
            if (state.isAir()) {
                return false;
            }
            if (state.is(block)) {
                return true;
            }
        }
        return false;
    }

    private int biomeHumidity() {
        var biome = level.getBiome(worldPosition);
        if (biome.is(net.minecraft.world.level.biome.Biomes.RIVER) || biome.is(SWAMP)) {
            return 2;
        }
        if (biome.is(FOREST) || biome.is(JUNGLE)) {
            return 1;
        }
        if (biome.is(DESERT)) {
            return -1;
        }
        return 0;
    }

    private int biomeNutrients() {
        var biome = level.getBiome(worldPosition);
        if (biome.is(SWAMP) || biome.is(JUNGLE) || biome.is(net.minecraft.world.level.biome.Biomes.MUSHROOM_FIELDS)) {
            return 2;
        }
        if (biome.is(FOREST)) {
            return 1;
        }
        return 0;
    }

    private static TagKey<net.minecraft.world.level.biome.Biome> biome(String namespace, String path) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(namespace, path));
    }

    private static void take(Player player, ItemStack stack, int count) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(count);
        }
    }

    private static Base baseSeed(ItemStack stack) {
        if (stack.is(Items.WHEAT_SEEDS)) return new Base(OldustryCrop.WHEAT, 1, 1, 1, 1, 1);
        if (stack.is(Items.PUMPKIN_SEEDS)) return new Base(OldustryCrop.PUMPKIN, 1, 1, 1, 1, 1);
        if (stack.is(Items.MELON_SEEDS)) return new Base(OldustryCrop.MELON, 1, 1, 1, 1, 1);
        if (stack.is(Items.NETHER_WART)) return new Base(OldustryCrop.NETHER_WART, 1, 1, 1, 1, 1);
        if (stack.is(OldustryItems.TERRA_WART.get())) return new Base(OldustryCrop.TERRA_WART, 1, 1, 1, 1, 1);
        if (stack.is(OldustryItems.COFFEE_BEANS.get())) return new Base(OldustryCrop.COFFEE, 1, 1, 1, 1, 1);
        if (stack.is(Items.SUGAR_CANE)) return new Base(OldustryCrop.REED, 1, 3, 0, 2, 1);
        if (stack.is(Items.COCOA_BEANS)) return new Base(OldustryCrop.COCOA, 1, 0, 0, 0, 1);
        if (stack.is(Items.POPPY)) return new Base(OldustryCrop.ROSE, 4, 1, 1, 1, 4);
        if (stack.is(Items.DANDELION)) return new Base(OldustryCrop.DANDELION, 4, 1, 1, 1, 4);
        if (stack.is(Items.CARROT)) return new Base(OldustryCrop.CARROTS, 1, 1, 1, 1, 1);
        if (stack.is(Items.POTATO)) return new Base(OldustryCrop.POTATO, 1, 1, 1, 1, 1);
        return null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("cropid", cropId);
        output.putInt("size", size);
        output.putInt("statGrowth", growth);
        output.putInt("statGain", gain);
        output.putInt("statResistance", resistance);
        output.putInt("scanLevel", scan);
        output.putInt("growthPoints", points);
        output.putInt("nutrientStorage", nutrientsStored);
        output.putInt("waterStorage", waterStored);
        output.putInt("exStorage", weedEx);
        output.putBoolean("upgraded", upgraded);
        output.putInt("ticker", ticker);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        cropId = input.getIntOr("cropid", -1);
        size = input.getIntOr("size", 0);
        growth = input.getIntOr("statGrowth", 0);
        gain = input.getIntOr("statGain", 0);
        resistance = input.getIntOr("statResistance", 0);
        scan = input.getIntOr("scanLevel", 0);
        points = input.getIntOr("growthPoints", 0);
        nutrientsStored = input.getIntOr("nutrientStorage", 0);
        waterStored = input.getIntOr("waterStorage", 0);
        weedEx = input.getIntOr("exStorage", 0);
        upgraded = input.getBooleanOr("upgraded", false);
        ticker = input.getIntOr("ticker", -1);
    }

    private record Base(OldustryCrop crop, int size, int growth, int gain, int resistance, int count) {}
}
