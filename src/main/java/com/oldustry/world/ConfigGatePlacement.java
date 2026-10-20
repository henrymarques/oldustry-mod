package com.oldustry.world;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.oldustry.OldustryConfig;
import com.oldustry.registry.ModFeatures;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/** Runtime oreDensityFactor + enableWorldGen* flags. Ceiling: extra copies when density>1. */
public class ConfigGatePlacement extends PlacementModifier {
    public static final MapCodec<ConfigGatePlacement> CODEC = Codec.STRING.fieldOf("flag").xmap(ConfigGatePlacement::new, p -> p.flag);
    private final String flag;

    public ConfigGatePlacement(String flag) {
        this.flag = flag;
    }

    public static ConfigGatePlacement tin() {
        return new ConfigGatePlacement("tin");
    }

    public static ConfigGatePlacement uranium() {
        return new ConfigGatePlacement("uranium");
    }

    public static ConfigGatePlacement rubber() {
        return new ConfigGatePlacement("rubber");
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos origin) {
        boolean on = switch (flag) {
            case "tin" -> OldustryConfig.WORLDGEN_TIN.get();
            case "uranium" -> OldustryConfig.WORLDGEN_URANIUM.get();
            case "rubber" -> OldustryConfig.WORLDGEN_RUBBER.get();
            default -> true;
        };
        if (!on) {
            return Stream.empty();
        }
        if ("rubber".equals(flag)) {
            return Stream.of(origin);
        }
        double density = OldustryConfig.ORE_DENSITY.get();
        if (density <= 0) {
            return Stream.empty();
        }
        if (density >= 1) {
            int extra = (int) Math.round(density) - 1;
            Stream<BlockPos> base = Stream.of(origin);
            return extra <= 0 ? base : Stream.concat(base, Stream.generate(() -> origin).limit(extra));
        }
        return random.nextDouble() < density ? Stream.of(origin) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModFeatures.CONFIG_GATE.get();
    }
}
