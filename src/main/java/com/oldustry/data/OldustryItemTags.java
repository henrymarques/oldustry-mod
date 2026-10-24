package com.oldustry.data;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;
import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class OldustryItemTags extends TagsProvider<Item> {
    public OldustryItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Registries.ITEM, lookup, Oldustry.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(OldustryTags.Items.ORES_TIN).add(OldustryItems.TIN_ORE.getKey());
        tag(OldustryTags.Items.ORES_URANIUM).add(OldustryItems.URANIUM_ORE.getKey());
        tag(OldustryTags.Items.RAW_MATERIALS_TIN).add(OldustryItems.RAW_TIN.getKey());
        tag(Tags.Items.RAW_MATERIALS).add(OldustryItems.RAW_TIN.getKey());
        tag(OldustryTags.Items.STORAGE_BLOCKS_TIN).add(OldustryItems.TIN_BLOCK.getKey());
        tag(OldustryTags.Items.STORAGE_BLOCKS_BRONZE).add(OldustryItems.BRONZE_BLOCK.getKey());
        tag(OldustryTags.Items.STORAGE_BLOCKS_URANIUM).add(OldustryItems.URANIUM_BLOCK.getKey());
        tag(Tags.Items.ORES).add(OldustryItems.TIN_ORE.getKey());
        tag(Tags.Items.ORES).add(OldustryItems.URANIUM_ORE.getKey());
        tag(Tags.Items.STORAGE_BLOCKS).add(OldustryItems.TIN_BLOCK.getKey());
        tag(Tags.Items.STORAGE_BLOCKS).add(OldustryItems.BRONZE_BLOCK.getKey());
        tag(Tags.Items.STORAGE_BLOCKS).add(OldustryItems.URANIUM_BLOCK.getKey());

        tag(OldustryTags.Items.INGOTS_TIN).add(OldustryItems.TIN_INGOT.getKey());
        tag(OldustryTags.Items.INGOTS_BRONZE).add(OldustryItems.BRONZE_INGOT.getKey());
        tag(OldustryTags.Items.INGOTS_URANIUM).add(OldustryItems.URANIUM_INGOT.getKey());
        tag(OldustryTags.Items.INGOTS_REFINED_IRON).add(OldustryItems.REFINED_IRON_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(OldustryItems.TIN_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(OldustryItems.BRONZE_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(OldustryItems.URANIUM_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(OldustryItems.REFINED_IRON_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(OldustryItems.MIXED_METAL_INGOT.getKey());

        tag(OldustryTags.Items.DUSTS_COAL).add(OldustryItems.COAL_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_IRON).add(OldustryItems.IRON_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_GOLD).add(OldustryItems.GOLD_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_COPPER).add(OldustryItems.COPPER_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_TIN).add(OldustryItems.TIN_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_BRONZE).add(OldustryItems.BRONZE_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_CLAY).add(OldustryItems.CLAY_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_SILVER).add(OldustryItems.SILVER_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.COAL_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.IRON_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.GOLD_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.COPPER_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.TIN_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.BRONZE_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.CLAY_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.SILVER_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.SMALL_IRON_DUST.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.HYDRATED_COAL_DUST.getKey());

        tag(Tags.Items.GEMS).add(OldustryItems.INDUSTRIAL_DIAMOND.getKey());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(OldustryItems.BRONZE_INGOT.getKey());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(OldustryItems.URANIUM_INGOT.getKey());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(OldustryItems.REFINED_IRON_INGOT.getKey());

        tag(ItemTags.PICKAXES).add(OldustryItems.BRONZE_PICKAXE.getKey());
        tag(ItemTags.PICKAXES).add(OldustryItems.MINING_DRILL.getKey());
        tag(ItemTags.PICKAXES).add(OldustryItems.DIAMOND_DRILL.getKey());
        tag(ItemTags.AXES).add(OldustryItems.BRONZE_AXE.getKey());
        tag(ItemTags.AXES).add(OldustryItems.CHAINSAW.getKey());
        // JAR IShearable — NeoForge loot rewrites shears match_tool → SHEARS_DIG ability.
        tag(Tags.Items.TOOLS_SHEAR).add(OldustryItems.CHAINSAW.getKey());
        tag(Tags.Items.DUSTS).add(OldustryItems.ANCIENT_SCRAP_DUST.getKey());
        tag(ItemTags.SWORDS).add(OldustryItems.BRONZE_SWORD.getKey());
        tag(ItemTags.SHOVELS).add(OldustryItems.BRONZE_SHOVEL.getKey());
        // JAR drill also digs dirt/sand/gravel (shovel set).
        tag(ItemTags.SHOVELS).add(OldustryItems.MINING_DRILL.getKey());
        tag(ItemTags.SHOVELS).add(OldustryItems.DIAMOND_DRILL.getKey());
        tag(ItemTags.HOES).add(OldustryItems.BRONZE_HOE.getKey());
        tag(ItemTags.HEAD_ARMOR).add(OldustryItems.BRONZE_HELMET.getKey());
        tag(ItemTags.CHEST_ARMOR).add(OldustryItems.BRONZE_CHESTPLATE.getKey());
        tag(ItemTags.CHEST_ARMOR).add(OldustryItems.COMPOSITE_VEST.getKey());
        tag(ItemTags.LEG_ARMOR).add(OldustryItems.BRONZE_LEGGINGS.getKey());
        tag(ItemTags.FOOT_ARMOR).add(OldustryItems.BRONZE_BOOTS.getKey());
        tag(OldustryTags.Items.ADVANCED_ALLOY).add(OldustryItems.ADVANCED_ALLOY.getKey());
        // Magnetizer lift: JAR iron/gold/chain + isMetalArmor (static boots). Bronze and copper are the deviation.
        tag(OldustryTags.Items.METAL_SHOES)
            .add(Items.IRON_BOOTS.builtInRegistryHolder().key())
            .add(Items.GOLDEN_BOOTS.builtInRegistryHolder().key())
            .add(Items.CHAINMAIL_BOOTS.builtInRegistryHolder().key())
            .add(Items.COPPER_BOOTS.builtInRegistryHolder().key())
            .add(OldustryItems.BRONZE_BOOTS.getKey())
            .add(OldustryItems.STATIC_BOOTS.getKey());

        tag(ItemTags.LOGS).add(OldustryItems.RUBBER_LOG.getKey());
        tag(ItemTags.LOGS_THAT_BURN).add(OldustryItems.RUBBER_LOG.getKey());
        tag(ItemTags.LEAVES).add(OldustryItems.RUBBER_LEAVES.getKey());
        tag(ItemTags.SAPLINGS).add(OldustryItems.RUBBER_SAPLING.getKey());
        tag(OldustryTags.Items.RUBBER).add(OldustryItems.RUBBER.getKey());

        tag(Tags.Items.PLAYER_WORKSTATIONS_FURNACES).add(OldustryItems.IMPROVED_FURNACE.getKey());
        tag(OldustryTags.Items.FURNACES_IRON).add(OldustryItems.IMPROVED_FURNACE.getKey());
        tag(OldustryTags.Items.FURNACES).addTag(OldustryTags.Items.FURNACES_IRON);
    }
}
