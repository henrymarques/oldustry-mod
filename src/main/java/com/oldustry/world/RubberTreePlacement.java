package com.oldustry.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.oldustry.registry.ModFeatures;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

/**
 * One tag, one roll. JAR {@code IC2.generate} weight and gate; positions stay in the chunk.
 * A missed plant costs one try: {@code WorldGenRubTree} used to spend four.
 */
public class RubberTreePlacement extends RepeatingPlacement {
    public static final MapCodec<RubberTreePlacement> CODEC = Codec.STRING.fieldOf("group").xmap(RubberTreePlacement::new, p -> p.group);
    private final String group;

    public RubberTreePlacement(String group) {
        this.group = group;
    }

    public static RubberTreePlacement taiga() {
        return new RubberTreePlacement("taiga");
    }

    public static RubberTreePlacement forest() {
        return new RubberTreePlacement("forest");
    }

    public static RubberTreePlacement swamp() {
        return new RubberTreePlacement("swamp");
    }

    @Override
    protected int count(RandomSource random, BlockPos origin) {
        return RubberTreeSpawn.tries("taiga".equals(group), "forest".equals(group), "swamp".equals(group), random::nextInt);
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModFeatures.RUBBER_COUNT.get();
    }
}
