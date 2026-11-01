package com.oldustry.machine;

import com.oldustry.registry.OldustryBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

/** The six programmed blueprints. Empty TFBP is a crafting item and is not one of these. */
public enum OldustryTfbp {
    CULTIVATION(4000, 40, 10),
    IRRIGATION(3000, 60, 10),
    CHILLING(2000, 50, 10),
    DESERTIFICATION(2500, 40, 10),
    FLATIFICATION(4000, 40, 20),
    MUSHROOM(8000, 25, 20);

    public final int consume;
    public final int range;
    private final int scanAbove;

    OldustryTfbp(int consume, int range, int scanAbove) {
        this.consume = consume;
        this.range = range;
        this.scanAbove = scanAbove;
    }

    public boolean apply(ServerLevel level, int x, int z, int machineY) {
        return switch (this) {
            case CULTIVATION -> cultivate(level, x, z, machineY);
            case IRRIGATION -> irrigate(level, x, z, machineY);
            case CHILLING -> chill(level, x, z, machineY);
            case DESERTIFICATION -> desert(level, x, z, machineY);
            case FLATIFICATION -> flatten(level, x, z, machineY);
            case MUSHROOM -> mushroom(level, x, z, machineY);
        };
    }

    /** JAR {@code pickRandomPlantId}: each entry has a 2-in-5 chance, then tall grass. */
    public static int pickPlant(RandomSource random, int count) {
        for (int i = 0; i < count; i++) {
            if (random.nextInt(5) <= 1) {
                return i;
            }
        }
        return 0;
    }

    private static boolean cultivate(ServerLevel level, int x, int z, int machineY) {
        int y = firstSolid(level, x, z, machineY + CULTIVATION.scanAbove);
        if (y == -1) {
            return false;
        }
        if (switchGround(level, x, y, z, Blocks.SAND, Blocks.DIRT, true)) {
            return true;
        }
        BlockState state = at(level, x, y, z);
        if (state.is(Blocks.DIRT)) {
            set(level, x, y, z, Blocks.GRASS_BLOCK.defaultBlockState());
            return true;
        }
        if (state.is(Blocks.GRASS_BLOCK)) {
            return growPlant(level, x, y + 1, z);
        }
        return false;
    }

    private static Block[] plants() {
        return new Block[] {
            Blocks.SHORT_GRASS,
            Blocks.DANDELION,
            Blocks.POPPY,
            Blocks.OAK_SAPLING,
            Blocks.SUGAR_CANE,
            Blocks.RED_MUSHROOM,
            Blocks.BROWN_MUSHROOM,
            Blocks.PUMPKIN
        };
    }

    private static boolean growPlant(ServerLevel level, int x, int y, int z) {
        BlockState state = at(level, x, y, z);
        boolean grass = state.is(Blocks.SHORT_GRASS);
        if (!state.isAir() && !(grass && level.getRandom().nextInt(4) == 0)) {
            return false;
        }
        Block[] plants = plants();
        int index = pickPlant(level.getRandom(), plants.length + 1);
        Block plant = index < plants.length ? plants[index] : OldustryBlocks.RUBBER_SAPLING.get();
        if (plant == Blocks.SUGAR_CANE) {
            set(level, x, y - 1, z, Blocks.FARMLAND.defaultBlockState());
        }
        set(level, x, y, z, plant.defaultBlockState());
        return true;
    }

    private static boolean irrigate(ServerLevel level, int x, int z, int machineY) {
        if (level.getRandom().nextInt(48000) == 0) {
            level.getWeatherData().setRaining(true);
            return true;
        }
        int y = firstBlock(level, x, z, machineY + IRRIGATION.scanAbove);
        if (y == -1) {
            return false;
        }
        if (switchGround(level, x, y, z, Blocks.SAND, Blocks.DIRT, true)) {
            switchGround(level, x, y, z, Blocks.SAND, Blocks.DIRT, true);
            return true;
        }
        BlockState state = at(level, x, y, z);
        if (state.is(Blocks.SHORT_GRASS)) {
            return spreadGrass(level, x + 1, y, z)
                || spreadGrass(level, x - 1, y, z)
                || spreadGrass(level, x, y, z + 1)
                || spreadGrass(level, x, y, z - 1);
        }
        if (state.getBlock() instanceof SaplingBlock sapling && (state.is(Blocks.OAK_SAPLING) || state.is(OldustryBlocks.RUBBER_SAPLING.get()))) {
            sapling.advanceTree(level, new BlockPos(x, y, z), state, level.getRandom());
            return true;
        }
        if (state.is(BlockTags.LOGS) || state.is(OldustryBlocks.RUBBER_LOG.get())) {
            set(level, x, y + 1, z, state);
            BlockState leaves = leavesFor(state);
            leaf(level, x, y + 2, z, leaves);
            leaf(level, x + 1, y + 1, z, leaves);
            leaf(level, x - 1, y + 1, z, leaves);
            leaf(level, x, y + 1, z + 1, leaves);
            leaf(level, x, y + 1, z - 1, leaves);
            return true;
        }
        if (state.is(Blocks.SUGAR_CANE)) {
            set(level, x, y, z, state.setValue(SugarCaneBlock.AGE, 7));
            return true;
        }
        if (state.is(Blocks.SNOW)) {
            set(level, x, y, z, Blocks.AIR.defaultBlockState());
            return true;
        }
        return false;
    }

    private static boolean spreadGrass(ServerLevel level, int x, int y, int z) {
        if (level.getRandom().nextBoolean()) {
            return false;
        }
        y = firstBlock(level, x, z, y);
        if (y == -1) {
            return false;
        }
        BlockState state = at(level, x, y, z);
        if (state.is(Blocks.DIRT)) {
            set(level, x, y, z, Blocks.GRASS_BLOCK.defaultBlockState());
            return true;
        }
        if (state.is(Blocks.GRASS_BLOCK)) {
            set(level, x, y + 1, z, Blocks.SHORT_GRASS.defaultBlockState());
            return true;
        }
        return false;
    }

    private static boolean chill(ServerLevel level, int x, int z, int machineY) {
        int y = firstBlock(level, x, z, machineY + CHILLING.scanAbove);
        if (y == -1) {
            return false;
        }
        BlockState state = at(level, x, y, z);
        if (water(state)) {
            set(level, x, y, z, Blocks.ICE.defaultBlockState());
            return true;
        }
        if (state.is(Blocks.ICE) && water(at(level, x, y - 1, z))) {
            set(level, x, y - 1, z, Blocks.ICE.defaultBlockState());
            return true;
        }
        if (state.is(Blocks.SNOW) && surroundedBySnow(level, x, y, z)) {
            set(level, x, y, z, Blocks.SNOW_BLOCK.defaultBlockState());
            return true;
        }
        BlockPos above = new BlockPos(x, y + 1, z);
        if (Blocks.SNOW.defaultBlockState().canSurvive(level, above) || state.is(Blocks.ICE)) {
            set(level, x, y + 1, z, Blocks.SNOW.defaultBlockState());
        }
        return false;
    }

    private static boolean surroundedBySnow(ServerLevel level, int x, int y, int z) {
        return snowHere(level, x + 1, y, z)
            && snowHere(level, x - 1, y, z)
            && snowHere(level, x, y, z + 1)
            && snowHere(level, x, y, z - 1);
    }

    private static boolean snowHere(ServerLevel level, int x, int y, int z) {
        int save = y;
        y = firstBlock(level, x, z, y + 16);
        if (y == -1 || save > y) {
            return false;
        }
        BlockState state = at(level, x, y, z);
        if (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)) {
            return true;
        }
        BlockPos above = new BlockPos(x, y + 1, z);
        if (Blocks.SNOW.defaultBlockState().canSurvive(level, above) || state.is(Blocks.ICE)) {
            set(level, x, y + 1, z, Blocks.SNOW.defaultBlockState());
        }
        return false;
    }

    private static boolean desert(ServerLevel level, int x, int z, int machineY) {
        int y = firstBlock(level, x, z, machineY + DESERTIFICATION.scanAbove);
        if (y == -1) {
            return false;
        }
        if (switchGround(level, x, y, z, Blocks.DIRT, Blocks.SAND, false)
            || switchGround(level, x, y, z, Blocks.GRASS_BLOCK, Blocks.SAND, false)
            || switchGround(level, x, y, z, Blocks.FARMLAND, Blocks.SAND, false)) {
            switchGround(level, x, y, z, Blocks.DIRT, Blocks.SAND, false);
            return true;
        }
        BlockState state = at(level, x, y, z);
        if (water(state) || state.is(Blocks.SNOW) || state.is(BlockTags.LEAVES) || state.is(OldustryBlocks.RUBBER_LEAVES.get()) || plant(state)) {
            set(level, x, y, z, Blocks.AIR.defaultBlockState());
            return true;
        }
        if (state.is(Blocks.ICE) || state.is(Blocks.SNOW_BLOCK)) {
            set(level, x, y, z, Blocks.WATER.defaultBlockState());
            return true;
        }
        if ((state.is(BlockTags.PLANKS) || state.is(BlockTags.LOGS) || state.is(OldustryBlocks.RUBBER_LOG.get()))
            && level.getRandom().nextInt(15) == 0) {
            set(level, x, y, z, Blocks.FIRE.defaultBlockState());
            return true;
        }
        return false;
    }

    private static boolean plant(BlockState state) {
        for (Block plant : plants()) {
            if (state.is(plant)) {
                return true;
            }
        }
        return state.is(OldustryBlocks.RUBBER_SAPLING.get());
    }

    private static boolean flatten(ServerLevel level, int x, int z, int machineY) {
        int y = firstBlock(level, x, z, machineY + FLATIFICATION.scanAbove);
        if (y == -1) {
            return false;
        }
        if (at(level, x, y, z).is(Blocks.SNOW)) {
            y--;
        }
        if (y == machineY) {
            return false;
        }
        if (y < machineY) {
            set(level, x, y + 1, z, Blocks.DIRT.defaultBlockState());
            return true;
        }
        if (removable(at(level, x, y, z))) {
            set(level, x, y, z, Blocks.AIR.defaultBlockState());
            return true;
        }
        return false;
    }

    private static boolean removable(BlockState state) {
        return state.is(Blocks.SNOW)
            || state.is(Blocks.ICE)
            || state.is(Blocks.GRASS_BLOCK)
            || state.is(Blocks.STONE)
            || state.is(Blocks.GRAVEL)
            || state.is(Blocks.SAND)
            || state.is(Blocks.DIRT)
            || state.is(BlockTags.LEAVES)
            || state.is(BlockTags.LOGS)
            || state.is(OldustryBlocks.RUBBER_LEAVES.get())
            || state.is(OldustryBlocks.RUBBER_LOG.get())
            || state.is(OldustryBlocks.RUBBER_SAPLING.get())
            || plant(state);
    }

    private static boolean mushroom(ServerLevel level, int x, int z, int machineY) {
        int y = firstSolid(level, x, z, machineY + MUSHROOM.scanAbove);
        if (y == -1) {
            return false;
        }
        return growDependency(level, x, y, z, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM);
    }

    private static boolean growDependency(ServerLevel level, int x, int y, int z, Block id, Block dependency) {
        for (int xm = x - 1; xm < x + 1; xm++) {
            for (int zm = z - 1; zm < z + 1; zm++) {
                for (int ym = y + 5; ym > y - 2; ym--) {
                    BlockState block = at(level, xm, ym, zm);
                    if (dependency == Blocks.MYCELIUM) {
                        if (block.is(Blocks.MYCELIUM) || block.is(Blocks.BROWN_MUSHROOM_BLOCK) || block.is(Blocks.RED_MUSHROOM_BLOCK)) {
                            break;
                        }
                        if (block.isAir()) {
                            continue;
                        }
                        if (block.is(Blocks.DIRT) || block.is(Blocks.GRASS_BLOCK)) {
                            set(level, xm, ym, zm, Blocks.MYCELIUM.defaultBlockState());
                            mushroomBiome(level, x, ym, z);
                            return true;
                        }
                    }
                    if (dependency == Blocks.BROWN_MUSHROOM) {
                        if (block.is(Blocks.BROWN_MUSHROOM) || block.is(Blocks.RED_MUSHROOM)) {
                            break;
                        }
                        if (!block.isAir() && growDependency(level, xm, ym, zm, Blocks.BROWN_MUSHROOM, Blocks.MYCELIUM)) {
                            return true;
                        }
                    }
                }
            }
        }
        if (id == Blocks.BROWN_MUSHROOM) {
            BlockState base = at(level, x, y, z);
            if (!base.is(Blocks.MYCELIUM)) {
                if (base.is(Blocks.BROWN_MUSHROOM_BLOCK) || base.is(Blocks.RED_MUSHROOM_BLOCK)) {
                    set(level, x, y, z, Blocks.MYCELIUM.defaultBlockState());
                } else {
                    return false;
                }
            }
            BlockState above = at(level, x, y + 1, z);
            if (!above.isAir() && !above.is(Blocks.SHORT_GRASS)) {
                return false;
            }
            Block shroom = level.getRandom().nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
            set(level, x, y + 1, z, shroom.defaultBlockState());
            return true;
        }
        if (id == Blocks.BROWN_MUSHROOM_BLOCK) {
            BlockState base = at(level, x, y + 1, z);
            if (!(base.getBlock() instanceof MushroomBlock mushroom)) {
                return false;
            }
            if (!base.is(Blocks.BROWN_MUSHROOM) && !base.is(Blocks.RED_MUSHROOM)) {
                return false;
            }
            if (mushroom.growMushroom(level, new BlockPos(x, y + 1, z), base, level.getRandom())) {
                for (int i = x - 1; i < x + 1; i++) {
                    for (int zm = z - 1; zm < z + 1; zm++) {
                        BlockState block = at(level, i, y + 1, zm);
                        if (block.is(Blocks.BROWN_MUSHROOM) || block.is(Blocks.RED_MUSHROOM)) {
                            set(level, i, y + 1, zm, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static void mushroomBiome(ServerLevel level, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        var biome = level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.MUSHROOM_FIELDS);
        FillBiomeCommand.fill(level, pos, pos, biome);
    }

    private static boolean switchGround(ServerLevel level, int x, int y, int z, Block from, Block to, boolean upwards) {
        if (upwards) {
            int save = ++y;
            while (at(level, x, y - 1, z).is(from)) {
                y--;
            }
            if (save == y) {
                return false;
            }
            set(level, x, y, z, to.defaultBlockState());
            return true;
        }
        while (at(level, x, y, z).is(to)) {
            y--;
        }
        if (!at(level, x, y, z).is(from)) {
            return false;
        }
        set(level, x, y, z, to.defaultBlockState());
        return true;
    }

    private static BlockState leavesFor(BlockState log) {
        if (log.is(Blocks.SPRUCE_LOG)) return Blocks.SPRUCE_LEAVES.defaultBlockState();
        if (log.is(Blocks.BIRCH_LOG)) return Blocks.BIRCH_LEAVES.defaultBlockState();
        if (log.is(Blocks.JUNGLE_LOG)) return Blocks.JUNGLE_LEAVES.defaultBlockState();
        if (log.is(Blocks.ACACIA_LOG)) return Blocks.ACACIA_LEAVES.defaultBlockState();
        if (log.is(Blocks.DARK_OAK_LOG)) return Blocks.DARK_OAK_LEAVES.defaultBlockState();
        if (log.is(Blocks.CHERRY_LOG)) return Blocks.CHERRY_LEAVES.defaultBlockState();
        if (log.is(Blocks.MANGROVE_LOG)) return Blocks.MANGROVE_LEAVES.defaultBlockState();
        if (log.is(OldustryBlocks.RUBBER_LOG.get())) return OldustryBlocks.RUBBER_LEAVES.get().defaultBlockState();
        return Blocks.OAK_LEAVES.defaultBlockState();
    }

    private static void leaf(ServerLevel level, int x, int y, int z, BlockState leaves) {
        if (at(level, x, y, z).isAir()) {
            set(level, x, y, z, leaves);
        }
    }

    private static int firstSolid(ServerLevel level, int x, int z, int y) {
        int min = level.getMinY();
        y = Math.min(y, level.getMaxY() - 1);
        for (; y > min; y--) {
            if (at(level, x, y, z).isSolidRender()) {
                return y;
            }
        }
        return -1;
    }

    private static int firstBlock(ServerLevel level, int x, int z, int y) {
        int min = level.getMinY();
        y = Math.min(y, level.getMaxY() - 1);
        for (; y > min; y--) {
            if (!at(level, x, y, z).isAir()) {
                return y;
            }
        }
        return -1;
    }

    private static boolean water(BlockState state) {
        return state.getFluidState().is(Fluids.WATER);
    }

    private static BlockState at(ServerLevel level, int x, int y, int z) {
        return level.getBlockState(new BlockPos(x, y, z));
    }

    private static void set(ServerLevel level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, Block.UPDATE_ALL);
    }
}
