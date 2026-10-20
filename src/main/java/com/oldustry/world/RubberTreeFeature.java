package com.oldustry.world;

import com.mojang.serialization.MapCodec;
import com.oldustry.block.RubberLogBlock;
import com.oldustry.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** IC2 WorldGenRubTree: trunk up to 8, irregular 5x5 canopy. */
public class RubberTreeFeature extends Feature<NoneFeatureConfiguration> {
    public static final MapCodec<RubberTreeFeature> CODEC = MapCodec.unit(RubberTreeFeature::new);
    public static final int MAX_HEIGHT = 8;

    public RubberTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return grow(context.level(), context.origin(), context.random());
    }

    public static boolean grow(WorldGenLevel level, BlockPos origin, RandomSource random) {
        int space = growHeight(level, origin);
        if (space < 2) {
            return false;
        }
        int height = space / 2;
        height += random.nextInt(space - height + 1);
        int[] holeChance = { 25 };
        BlockState log = ModBlocks.RUBBER_LOG.get().defaultBlockState();
        BlockState leaves = ModBlocks.RUBBER_LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
        for (int i = 0; i < height; i++) {
            BlockPos trunk = origin.above(i);
            setBlock(level, trunk, RubberLogBlock.withRandomTap(log, random, holeChance));
            if (height < 4 || (height < 7 && i > 1) || i > 2) {
                int c = i + 4 - height;
                if (c < 1) {
                    c = 1;
                }
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        boolean gen = (dx > -2 && dx < 2 && dz > -2 && dz < 2)
                            || (dx > -2 && dx < 2 && random.nextInt(c) == 0)
                            || (dz > -2 && dz < 2 && random.nextInt(c) == 0);
                        if (dx == 0 && dz == 0) {
                            continue;
                        }
                        BlockPos leafPos = origin.offset(dx, i, dz);
                        if (gen && level.getBlockState(leafPos).isAir()) {
                            setBlock(level, leafPos, leaves);
                        }
                    }
                }
            }
        }
        int top = height / 4 + random.nextInt(2);
        for (int i = 0; i <= top; i++) {
            BlockPos leafPos = origin.above(height + i);
            if (level.getBlockState(leafPos).isAir()) {
                setBlock(level, leafPos, leaves);
            }
        }
        return true;
    }

    private static void setBlock(WorldGenLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 3);
    }

    private static int growHeight(WorldGenLevel level, BlockPos origin) {
        BlockState soil = level.getBlockState(origin.below());
        // 26.2 #minecraft:dirt is only dirt/coarse/rooted — grass lives in
        // #minecraft:supports_vegetation (same tag VegetationBlock uses to place the sapling).
        if (!soil.is(BlockTags.SUPPORTS_VEGETATION) && !soil.is(BlockTags.SAND)) {
            return 0;
        }
        BlockState here = level.getBlockState(origin);
        if (!here.isAir() && !here.is(ModBlocks.RUBBER_SAPLING.get())) {
            return 0;
        }
        int height = 1;
        BlockPos.MutableBlockPos cursor = origin.mutable();
        while (height < MAX_HEIGHT) {
            cursor.move(Direction.UP);
            if (!level.getBlockState(cursor).isAir()) {
                break;
            }
            height++;
        }
        return height;
    }
}
