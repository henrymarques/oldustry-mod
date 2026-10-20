package com.oldustry.world;

import java.util.List;
import java.util.Optional;

import com.oldustry.Oldustry;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModFeatures;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;

public final class OldustryWorldgen {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TIN = configured("ore_tin");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_URANIUM = configured("ore_uranium");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE = configured("rubber_tree");

    public static final ResourceKey<PlacedFeature> ORE_TIN_PLACED = placed("ore_tin");
    public static final ResourceKey<PlacedFeature> ORE_URANIUM_PLACED = placed("ore_uranium");
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_SWAMP = placed("rubber_tree_swamp");
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_FOREST = placed("rubber_tree_forest");
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_TAIGA = placed("rubber_tree_taiga");

    public static final TreeGrower RUBBER_GROWER = new TreeGrower(
        "rubber",
        Optional.empty(),
        Optional.of(RUBBER_TREE),
        Optional.empty()
    );

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        var stone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        var deepslate = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        context.register(ORE_TIN, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
            OreConfiguration.target(stone, ModBlocks.TIN_ORE.get().defaultBlockState()),
            OreConfiguration.target(deepslate, ModBlocks.TIN_ORE.get().defaultBlockState())
        ), 6)));
        context.register(ORE_URANIUM, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
            OreConfiguration.target(stone, ModBlocks.URANIUM_ORE.get().defaultBlockState()),
            OreConfiguration.target(deepslate, ModBlocks.URANIUM_ORE.get().defaultBlockState())
        ), 3)));
        context.register(RUBBER_TREE, new ConfiguredFeature<>(ModFeatures.RUBBER_TREE.get(), NoneFeatureConfiguration.INSTANCE));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(ORE_TIN_PLACED, new PlacedFeature(features.getOrThrow(ORE_TIN), orePlacement("tin", 10, -16, 48)));
        context.register(ORE_URANIUM_PLACED, new PlacedFeature(features.getOrThrow(ORE_URANIUM), orePlacement("uranium", 5, -64, 32)));
        context.register(RUBBER_TREE_SWAMP, new PlacedFeature(features.getOrThrow(RUBBER_TREE), treePlacement(8, 5)));
        context.register(RUBBER_TREE_FOREST, new PlacedFeature(features.getOrThrow(RUBBER_TREE), treePlacement(3, 16)));
        context.register(RUBBER_TREE_TAIGA, new PlacedFeature(features.getOrThrow(RUBBER_TREE), treePlacement(1, 50)));
    }

    public static void bootstrapBiomeModifiers(net.minecraft.data.worldgen.BootstrapContext<net.neoforged.neoforge.common.world.BiomeModifier> context) {
        var biomes = context.lookup(net.minecraft.core.registries.Registries.BIOME);
        var placed = context.lookup(net.minecraft.core.registries.Registries.PLACED_FEATURE);
        var overworld = biomes.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_OVERWORLD);
        context.register(biomeModifier("ore_tin"), new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
            overworld,
            net.minecraft.core.HolderSet.direct(placed.getOrThrow(ORE_TIN_PLACED)),
            net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(biomeModifier("ore_uranium"), new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
            overworld,
            net.minecraft.core.HolderSet.direct(placed.getOrThrow(ORE_URANIUM_PLACED)),
            net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        // 26.2: tags de bioma (mais biomas que 1.5.2). Não voltar ao match por nome do IC2.java ~1135.
        context.register(biomeModifier("rubber_tree_swamp"), new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
            new net.neoforged.neoforge.registries.holdersets.AndHolderSet<>(overworld, biomes.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_SWAMP)),
            net.minecraft.core.HolderSet.direct(placed.getOrThrow(RUBBER_TREE_SWAMP)),
            net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION
        ));
        context.register(biomeModifier("rubber_tree_forest"), new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
            new net.neoforged.neoforge.registries.holdersets.AndHolderSet<>(overworld, biomes.getOrThrow(com.oldustry.registry.OldustryTags.Biomes.RUBBER_FOREST_OR_JUNGLE)),
            net.minecraft.core.HolderSet.direct(placed.getOrThrow(RUBBER_TREE_FOREST)),
            net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION
        ));
        context.register(biomeModifier("rubber_tree_taiga"), new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
            new net.neoforged.neoforge.registries.holdersets.AndHolderSet<>(overworld, biomes.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_TAIGA)),
            net.minecraft.core.HolderSet.direct(placed.getOrThrow(RUBBER_TREE_TAIGA)),
            net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION
        ));
    }

    private static List<PlacementModifier> orePlacement(String flag, int count, int minY, int maxY) {
        return List.of(
            new ConfigGatePlacement(flag),
            CountPlacement.of(count),
            InSquarePlacement.spread(),
            HeightRangePlacement.uniform(VerticalAnchor.absolute(minY), VerticalAnchor.absolute(maxY)),
            BiomeFilter.biome()
        );
    }

    private static List<PlacementModifier> treePlacement(int count, int rarity) {
        return List.of(
            ConfigGatePlacement.rubber(),
            RarityFilter.onAverageOnceEvery(rarity),
            CountPlacement.of(count),
            InSquarePlacement.spread(),
            HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),
            BiomeFilter.biome()
        );
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configured(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placed(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, name));
    }

    private static ResourceKey<net.neoforged.neoforge.common.world.BiomeModifier> biomeModifier(String name) {
        return ResourceKey.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, name));
    }

    private OldustryWorldgen() {}
}
