package com.oldustry.data;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;
import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

public class OldustryItemTags extends ItemTagsProvider {
    public OldustryItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, Oldustry.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(OldustryTags.Items.ORES_TIN).add(OldustryItems.TIN_ORE.get());
        tag(OldustryTags.Items.ORES_URANIUM).add(OldustryItems.URANIUM_ORE.get());
        tag(OldustryTags.Items.RAW_MATERIALS_TIN).add(OldustryItems.RAW_TIN.get());
        tag(Tags.Items.RAW_MATERIALS).add(OldustryItems.RAW_TIN.get());
        tag(OldustryTags.Items.STORAGE_BLOCKS_TIN).add(OldustryItems.TIN_BLOCK.get());
        tag(OldustryTags.Items.STORAGE_BLOCKS_BRONZE).add(OldustryItems.BRONZE_BLOCK.get());
        tag(OldustryTags.Items.STORAGE_BLOCKS_URANIUM).add(OldustryItems.URANIUM_BLOCK.get());
        tag(Tags.Items.ORES).add(OldustryItems.TIN_ORE.get());
        tag(Tags.Items.ORES).add(OldustryItems.URANIUM_ORE.get());
        tag(Tags.Items.STORAGE_BLOCKS).add(OldustryItems.TIN_BLOCK.get());
        tag(Tags.Items.STORAGE_BLOCKS).add(OldustryItems.BRONZE_BLOCK.get());
        tag(Tags.Items.STORAGE_BLOCKS).add(OldustryItems.URANIUM_BLOCK.get());

        tag(OldustryTags.Items.INGOTS_TIN).add(OldustryItems.TIN_INGOT.get());
        tag(OldustryTags.Items.INGOTS_BRONZE).add(OldustryItems.BRONZE_INGOT.get());
        tag(OldustryTags.Items.INGOTS_URANIUM).add(OldustryItems.URANIUM_INGOT.get());
        tag(OldustryTags.Items.INGOTS_REFINED_IRON).add(OldustryItems.REFINED_IRON_INGOT.get());
        tag(Tags.Items.INGOTS).add(OldustryItems.TIN_INGOT.get());
        tag(Tags.Items.INGOTS).add(OldustryItems.BRONZE_INGOT.get());
        tag(Tags.Items.INGOTS).add(OldustryItems.URANIUM_INGOT.get());
        tag(Tags.Items.INGOTS).add(OldustryItems.REFINED_IRON_INGOT.get());
        tag(Tags.Items.INGOTS).add(OldustryItems.MIXED_METAL_INGOT.get());

        tag(OldustryTags.Items.DUSTS_COAL).add(OldustryItems.COAL_DUST.get());
        tag(OldustryTags.Items.DUSTS_IRON).add(OldustryItems.IRON_DUST.get());
        tag(OldustryTags.Items.DUSTS_GOLD).add(OldustryItems.GOLD_DUST.get());
        tag(OldustryTags.Items.DUSTS_COPPER).add(OldustryItems.COPPER_DUST.get());
        tag(OldustryTags.Items.DUSTS_TIN).add(OldustryItems.TIN_DUST.get());
        tag(OldustryTags.Items.DUSTS_BRONZE).add(OldustryItems.BRONZE_DUST.get());
        tag(OldustryTags.Items.DUSTS_CLAY).add(OldustryItems.CLAY_DUST.get());
        tag(OldustryTags.Items.DUSTS_SILVER).add(OldustryItems.SILVER_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.COAL_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.IRON_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.GOLD_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.COPPER_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.TIN_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.BRONZE_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.CLAY_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.SILVER_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.SMALL_IRON_DUST.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.HYDRATED_COAL_DUST.get());

        tag(Tags.Items.GEMS).add(OldustryItems.INDUSTRIAL_DIAMOND.get());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(OldustryItems.BRONZE_INGOT.get());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(OldustryItems.URANIUM_INGOT.get());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(OldustryItems.REFINED_IRON_INGOT.get());

        tag(ItemTags.PICKAXES).add(OldustryItems.BRONZE_PICKAXE.get());
        tag(ItemTags.PICKAXES).add(OldustryItems.MINING_DRILL.get());
        tag(ItemTags.PICKAXES).add(OldustryItems.DIAMOND_DRILL.get());
        tag(ItemTags.AXES).add(OldustryItems.BRONZE_AXE.get());
        tag(ItemTags.AXES).add(OldustryItems.CHAINSAW.get());
        // JAR IShearable — NeoForge loot rewrites shears match_tool → SHEARS_DIG ability.
        tag(Tags.Items.TOOLS_SHEAR).add(OldustryItems.CHAINSAW.get());
        tag(Tags.Items.DUSTS).add(OldustryItems.ANCIENT_SCRAP_DUST.get());
        tag(ItemTags.SWORDS).add(OldustryItems.BRONZE_SWORD.get());
        tag(ItemTags.SHOVELS).add(OldustryItems.BRONZE_SHOVEL.get());
        // JAR drill also digs dirt/sand/gravel (shovel set).
        tag(ItemTags.SHOVELS).add(OldustryItems.MINING_DRILL.get());
        tag(ItemTags.SHOVELS).add(OldustryItems.DIAMOND_DRILL.get());
        tag(ItemTags.HOES).add(OldustryItems.BRONZE_HOE.get());
        tag(ItemTags.HOES).add(OldustryItems.ELECTRIC_HOE.get());
        tag(ItemTags.HEAD_ARMOR).add(OldustryItems.BRONZE_HELMET.get());
        tag(ItemTags.CHEST_ARMOR).add(OldustryItems.BRONZE_CHESTPLATE.get());
        tag(ItemTags.CHEST_ARMOR).add(OldustryItems.COMPOSITE_VEST.get());
        tag(ItemTags.LEG_ARMOR).add(OldustryItems.BRONZE_LEGGINGS.get());
        tag(ItemTags.FOOT_ARMOR).add(OldustryItems.BRONZE_BOOTS.get());
        tag(OldustryTags.Items.ADVANCED_ALLOY).add(OldustryItems.ADVANCED_ALLOY.get());
        // Magnetizer lift: JAR iron/gold/chain + isMetalArmor (static boots). Bronze and copper are the deviation.
        tag(OldustryTags.Items.METAL_SHOES)
            .add(Items.IRON_BOOTS)
            .add(Items.GOLDEN_BOOTS)
            .add(Items.CHAINMAIL_BOOTS)
            .add(Items.COPPER_BOOTS)
            .add(OldustryItems.BRONZE_BOOTS.get())
            .add(OldustryItems.STATIC_BOOTS.get());

        tag(ItemTags.LOGS).add(OldustryItems.RUBBER_LOG.get());
        tag(ItemTags.LOGS_THAT_BURN).add(OldustryItems.RUBBER_LOG.get());
        tag(ItemTags.LEAVES).add(OldustryItems.RUBBER_LEAVES.get());
        tag(ItemTags.SAPLINGS).add(OldustryItems.RUBBER_SAPLING.get());
        tag(OldustryTags.Items.RUBBER).add(OldustryItems.RUBBER.get());

        tag(Tags.Items.PLAYER_WORKSTATIONS_FURNACES).add(OldustryItems.IMPROVED_FURNACE.get());
        tag(OldustryTags.Items.FURNACES_IRON).add(OldustryItems.IMPROVED_FURNACE.get());
        tag(OldustryTags.Items.FURNACES).addTag(OldustryTags.Items.FURNACES_IRON);
    }
}
