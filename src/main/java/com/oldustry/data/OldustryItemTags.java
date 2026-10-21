package com.oldustry.data;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;
import com.oldustry.registry.ModItems;
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
        tag(OldustryTags.Items.ORES_TIN).add(ModItems.TIN_ORE.getKey());
        tag(OldustryTags.Items.ORES_URANIUM).add(ModItems.URANIUM_ORE.getKey());
        tag(OldustryTags.Items.RAW_MATERIALS_TIN).add(ModItems.RAW_TIN.getKey());
        tag(Tags.Items.RAW_MATERIALS).add(ModItems.RAW_TIN.getKey());
        tag(OldustryTags.Items.STORAGE_BLOCKS_TIN).add(ModItems.TIN_BLOCK.getKey());
        tag(OldustryTags.Items.STORAGE_BLOCKS_BRONZE).add(ModItems.BRONZE_BLOCK.getKey());
        tag(OldustryTags.Items.STORAGE_BLOCKS_URANIUM).add(ModItems.URANIUM_BLOCK.getKey());
        tag(Tags.Items.ORES).add(ModItems.TIN_ORE.getKey());
        tag(Tags.Items.ORES).add(ModItems.URANIUM_ORE.getKey());
        tag(Tags.Items.STORAGE_BLOCKS).add(ModItems.TIN_BLOCK.getKey());
        tag(Tags.Items.STORAGE_BLOCKS).add(ModItems.BRONZE_BLOCK.getKey());
        tag(Tags.Items.STORAGE_BLOCKS).add(ModItems.URANIUM_BLOCK.getKey());

        tag(OldustryTags.Items.INGOTS_TIN).add(ModItems.TIN_INGOT.getKey());
        tag(OldustryTags.Items.INGOTS_BRONZE).add(ModItems.BRONZE_INGOT.getKey());
        tag(OldustryTags.Items.INGOTS_URANIUM).add(ModItems.URANIUM_INGOT.getKey());
        tag(OldustryTags.Items.INGOTS_REFINED_IRON).add(ModItems.REFINED_IRON_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(ModItems.TIN_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(ModItems.BRONZE_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(ModItems.URANIUM_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(ModItems.REFINED_IRON_INGOT.getKey());
        tag(Tags.Items.INGOTS).add(ModItems.MIXED_METAL_INGOT.getKey());

        tag(OldustryTags.Items.DUSTS_COAL).add(ModItems.COAL_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_IRON).add(ModItems.IRON_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_GOLD).add(ModItems.GOLD_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_COPPER).add(ModItems.COPPER_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_TIN).add(ModItems.TIN_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_BRONZE).add(ModItems.BRONZE_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_CLAY).add(ModItems.CLAY_DUST.getKey());
        tag(OldustryTags.Items.DUSTS_SILVER).add(ModItems.SILVER_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.COAL_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.IRON_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.GOLD_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.COPPER_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.TIN_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.BRONZE_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.CLAY_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.SILVER_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.SMALL_IRON_DUST.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.HYDRATED_COAL_DUST.getKey());

        tag(Tags.Items.GEMS).add(ModItems.INDUSTRIAL_DIAMOND.getKey());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(ModItems.BRONZE_INGOT.getKey());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(ModItems.URANIUM_INGOT.getKey());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(ModItems.REFINED_IRON_INGOT.getKey());

        tag(ItemTags.PICKAXES).add(ModItems.BRONZE_PICKAXE.getKey());
        tag(ItemTags.PICKAXES).add(ModItems.MINING_DRILL.getKey());
        tag(ItemTags.PICKAXES).add(ModItems.DIAMOND_DRILL.getKey());
        tag(ItemTags.AXES).add(ModItems.BRONZE_AXE.getKey());
        tag(ItemTags.AXES).add(ModItems.CHAINSAW.getKey());
        // JAR IShearable — NeoForge loot rewrites shears match_tool → SHEARS_DIG ability.
        tag(Tags.Items.TOOLS_SHEAR).add(ModItems.CHAINSAW.getKey());
        tag(Tags.Items.DUSTS).add(ModItems.ANCIENT_SCRAP_DUST.getKey());
        tag(ItemTags.SWORDS).add(ModItems.BRONZE_SWORD.getKey());
        tag(ItemTags.SHOVELS).add(ModItems.BRONZE_SHOVEL.getKey());
        // JAR drill also digs dirt/sand/gravel (shovel set).
        tag(ItemTags.SHOVELS).add(ModItems.MINING_DRILL.getKey());
        tag(ItemTags.SHOVELS).add(ModItems.DIAMOND_DRILL.getKey());
        tag(ItemTags.HOES).add(ModItems.BRONZE_HOE.getKey());
        tag(ItemTags.HEAD_ARMOR).add(ModItems.BRONZE_HELMET.getKey());
        tag(ItemTags.CHEST_ARMOR).add(ModItems.BRONZE_CHESTPLATE.getKey());
        tag(ItemTags.CHEST_ARMOR).add(ModItems.COMPOSITE_VEST.getKey());
        tag(ItemTags.LEG_ARMOR).add(ModItems.BRONZE_LEGGINGS.getKey());
        tag(ItemTags.FOOT_ARMOR).add(ModItems.BRONZE_BOOTS.getKey());
        tag(OldustryTags.Items.ADVANCED_ALLOY).add(ModItems.ADVANCED_ALLOY.getKey());
        // Magnetizer lift: JAR iron/gold/chain + isMetalArmor (static boots). Bronze is the deviation.
        tag(OldustryTags.Items.METAL_SHOES)
            .add(Items.IRON_BOOTS.builtInRegistryHolder().key())
            .add(Items.GOLDEN_BOOTS.builtInRegistryHolder().key())
            .add(Items.CHAINMAIL_BOOTS.builtInRegistryHolder().key())
            .add(ModItems.BRONZE_BOOTS.getKey())
            .add(ModItems.STATIC_BOOTS.getKey());

        tag(ItemTags.LOGS).add(ModItems.RUBBER_LOG.getKey());
        tag(ItemTags.LOGS_THAT_BURN).add(ModItems.RUBBER_LOG.getKey());
        tag(ItemTags.LEAVES).add(ModItems.RUBBER_LEAVES.getKey());
        tag(ItemTags.SAPLINGS).add(ModItems.RUBBER_SAPLING.getKey());
        tag(OldustryTags.Items.RUBBER).add(ModItems.RUBBER.getKey());

        tag(Tags.Items.PLAYER_WORKSTATIONS_FURNACES).add(ModItems.IMPROVED_FURNACE.getKey());
        tag(OldustryTags.Items.FURNACES_IRON).add(ModItems.IMPROVED_FURNACE.getKey());
        tag(OldustryTags.Items.FURNACES).addTag(OldustryTags.Items.FURNACES_IRON);
    }
}
