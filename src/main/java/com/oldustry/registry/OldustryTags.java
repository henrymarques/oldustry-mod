package com.oldustry.registry;

import com.oldustry.Oldustry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public final class OldustryTags {
    public static final class Blocks {
        public static final TagKey<Block> ORES_TIN = block("ores/tin");
        public static final TagKey<Block> ORES_URANIUM = block("ores/uranium");
        public static final TagKey<Block> ORES_SILVER = block("ores/silver");
        public static final TagKey<Block> ORES_RUBY = block("ores/ruby");
        public static final TagKey<Block> ORES_SAPPHIRE = block("ores/sapphire");
        public static final TagKey<Block> ORES_GREEN_SAPPHIRE = block("ores/green_sapphire");
        public static final TagKey<Block> ORES_TUNGSTEN = block("ores/tungsten");
        public static final TagKey<Block> STORAGE_BLOCKS_TIN = block("storage_blocks/tin");
        public static final TagKey<Block> STORAGE_BLOCKS_BRONZE = block("storage_blocks/bronze");
        public static final TagKey<Block> STORAGE_BLOCKS_URANIUM = block("storage_blocks/uranium");
        public static final TagKey<Block> FURNACES = block("furnaces");
        public static final TagKey<Block> FURNACES_IRON = block("furnaces/iron");

        private static TagKey<Block> block(String path) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", path));
        }

        private Blocks() {}
    }

    public static final class Items {
        public static final TagKey<Item> ORES_TIN = item("ores/tin");
        public static final TagKey<Item> ORES_URANIUM = item("ores/uranium");
        public static final TagKey<Item> RAW_MATERIALS_TIN = item("raw_materials/tin");
        public static final TagKey<Item> STORAGE_BLOCKS_TIN = item("storage_blocks/tin");
        public static final TagKey<Item> STORAGE_BLOCKS_BRONZE = item("storage_blocks/bronze");
        public static final TagKey<Item> STORAGE_BLOCKS_URANIUM = item("storage_blocks/uranium");
        public static final TagKey<Item> INGOTS_TIN = item("ingots/tin");
        public static final TagKey<Item> INGOTS_BRONZE = item("ingots/bronze");
        public static final TagKey<Item> INGOTS_URANIUM = item("ingots/uranium");
        public static final TagKey<Item> INGOTS_REFINED_IRON = item("ingots/refined_iron");
        public static final TagKey<Item> DUSTS_COAL = item("dusts/coal");
        public static final TagKey<Item> DUSTS_IRON = item("dusts/iron");
        public static final TagKey<Item> DUSTS_GOLD = item("dusts/gold");
        public static final TagKey<Item> DUSTS_COPPER = item("dusts/copper");
        public static final TagKey<Item> DUSTS_TIN = item("dusts/tin");
        public static final TagKey<Item> DUSTS_BRONZE = item("dusts/bronze");
        public static final TagKey<Item> DUSTS_CLAY = item("dusts/clay");
        public static final TagKey<Item> DUSTS_SILVER = item("dusts/silver");
        public static final TagKey<Item> RUBBER = item("rubber");
        public static final TagKey<Item> FURNACES = item("furnaces");
        public static final TagKey<Item> FURNACES_IRON = item("furnaces/iron");
        public static final TagKey<Item> ADVANCED_ALLOY = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "advanced_alloy")
        );
        /** Magnetizer lift boots: iron/gold/chain + copper and bronze (deviation). */
        public static final TagKey<Item> METAL_SHOES = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "metal_shoes")
        );

        private static TagKey<Item> item(String path) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
        }

        private Items() {}
    }

    public static final class Biomes {
        public static final TagKey<Biome> RUBBER_FOREST_OR_JUNGLE = TagKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "rubber_forest_or_jungle")
        );

        private Biomes() {}
    }

    private OldustryTags() {}
}
