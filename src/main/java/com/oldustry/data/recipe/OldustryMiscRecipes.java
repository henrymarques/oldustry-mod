package com.oldustry.data.recipe;

import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

final class OldustryMiscRecipes extends RecipeProvider {
    OldustryMiscRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, OldustryItems.STONE_MUG.get())
            .define('S', Items.COBBLESTONE)
            .pattern("SS ")
            .pattern("SSS")
            .pattern("SS ")
            .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.COFFEE_POWDER.get())
            .requires(OldustryItems.COFFEE_BEANS.get())
            .unlockedBy("has_coffee_beans", has(OldustryItems.COFFEE_BEANS.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.COFFEE_MUG_COLD.get())
            .requires(OldustryItems.STONE_MUG.get())
            .requires(OldustryItems.COFFEE_POWDER.get())
            .requires(Items.WATER_BUCKET)
            .unlockedBy("has_coffee_powder", has(OldustryItems.COFFEE_POWDER.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.COFFEE_MUG_COLD.get())
            .requires(OldustryItems.STONE_MUG.get())
            .requires(OldustryItems.COFFEE_POWDER.get())
            .requires(OldustryItems.WATER_CELL.get())
            .unlockedBy("has_coffee_powder", has(OldustryItems.COFFEE_POWDER.get()))
            .save(output, OldustryRecipeProvider.id("coffee_mug_cold_from_cell"));

        shapeless(RecipeCategory.MISC, OldustryItems.COFFEE_MUG.get())
            .requires(OldustryItems.COFFEE_MUG_DARK.get())
            .requires(Items.SUGAR)
            .requires(Items.MILK_BUCKET)
            .unlockedBy("has_coffee_mug_dark", has(OldustryItems.COFFEE_MUG_DARK.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.FERTILIZER.get(), 2)
            .requires(OldustryItems.SCRAP.get())
            .requires(Items.BONE_MEAL)
            .unlockedBy("has_scrap", has(OldustryItems.SCRAP.get()))
            .save(output, OldustryRecipeProvider.id("fertilizer_from_scrap"));

        shapeless(RecipeCategory.MISC, OldustryItems.FERTILIZER.get(), 2)
            .requires(OldustryItems.SCRAP.get())
            .requires(OldustryItems.SCRAP.get())
            .requires(OldustryItems.FERTILIZER.get())
            .unlockedBy("has_fertilizer", has(OldustryItems.FERTILIZER.get()))
            .save(output, OldustryRecipeProvider.id("fertilizer_from_fertilizer"));

        shaped(RecipeCategory.MISC, OldustryItems.WEED_EX.get())
            .define('R', Items.REDSTONE)
            .define('G', OldustryItems.GRIN_POWDER.get())
            .define('C', OldustryItems.EMPTY_CELL.get())
            .pattern("R")
            .pattern("G")
            .pattern("C")
            .unlockedBy("has_grin_powder", has(OldustryItems.GRIN_POWDER.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.BARREL.get())
            .define('P', ItemTags.PLANKS)
            .define('W', OldustryItems.RUBBER_LOG.get())
            .pattern("P")
            .pattern("W")
            .pattern("P")
            .unlockedBy("has_rubber_log", has(OldustryItems.RUBBER_LOG.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.TREETAP.get())
            .define('P', ItemTags.PLANKS)
            .pattern(" P ")
            .pattern("PPP")
            .pattern("P  ")
            .unlockedBy("has_planks", has(ItemTags.PLANKS))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.RUBBER_SHEET.get(), 3)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern("RRR")
            .pattern("RRR")
            .unlockedBy("has_rubber", has(OldustryTags.Items.RUBBER))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.IMPROVED_FURNACE.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .pattern("III")
            .pattern("I I")
            .pattern("III")
            .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.IMPROVED_FURNACE.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('F', Items.FURNACE)
            .pattern(" I ")
            .pattern("I I")
            .pattern("IFI")
            .unlockedBy("has_furnace", has(Items.FURNACE))
            .save(output, OldustryRecipeProvider.id("improved_furnace_from_furnace"));

        shaped(RecipeCategory.MISC, OldustryItems.EMPTY_CELL.get(), 16)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern(" T ")
            .pattern("T T")
            .pattern(" T ")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.FUEL_CAN.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern(" TT")
            .pattern("T T")
            .pattern("TTT")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.TIN_CAN.get(), 4)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("T T")
            .pattern("TTT")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.WATER_CELL.get())
            .requires(OldustryItems.EMPTY_CELL.get())
            .requires(Items.WATER_BUCKET)
            .unlockedBy("has_empty_cell", has(OldustryItems.EMPTY_CELL.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.LAVA_CELL.get())
            .requires(OldustryItems.EMPTY_CELL.get())
            .requires(Items.LAVA_BUCKET)
            .unlockedBy("has_empty_cell", has(OldustryItems.EMPTY_CELL.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.COAL_CELL.get())
            .requires(OldustryItems.EMPTY_CELL.get())
            .requires(OldustryItems.HYDRATED_COAL_CLUMP.get())
            .unlockedBy("has_hydrated_coal_clump", has(OldustryItems.HYDRATED_COAL_CLUMP.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.BIO_CELL.get())
            .requires(OldustryItems.EMPTY_CELL.get())
            .requires(OldustryItems.COMPRESSED_PLANTS.get())
            .unlockedBy("has_compressed_plants", has(OldustryItems.COMPRESSED_PLANTS.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.IRIDIUM_PLATE.get())
            .define('I', OldustryItems.IRIDIUM_ORE.get())
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .define('D', Items.DIAMOND)
            .pattern("IAI")
            .pattern("ADA")
            .pattern("IAI")
            .unlockedBy("has_iridium_ore", has(OldustryItems.IRIDIUM_ORE.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.IRIDIUM_PLATE.get())
            .define('I', OldustryItems.IRIDIUM_ORE.get())
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .define('D', OldustryItems.INDUSTRIAL_DIAMOND.get())
            .pattern("IAI")
            .pattern("ADA")
            .pattern("IAI")
            .unlockedBy("has_iridium_ore", has(OldustryItems.IRIDIUM_ORE.get()))
            .save(output, OldustryRecipeProvider.id("iridium_plate_from_industrial_diamond"));

        shaped(RecipeCategory.MISC, Items.BUCKET)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("T T")
            .pattern(" T ")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output, OldustryRecipeProvider.id("bucket_from_tin"));

        shaped(RecipeCategory.MISC, Items.GLOWSTONE_DUST)
            .define('R', Items.REDSTONE)
            .define('G', OldustryTags.Items.DUSTS_GOLD)
            .pattern("RGR")
            .pattern("GRG")
            .pattern("RGR")
            .unlockedBy("has_gold_dust", has(OldustryTags.Items.DUSTS_GOLD))
            .save(output, OldustryRecipeProvider.id("glowstone_from_gold_dust"));

        shaped(RecipeCategory.MISC, Items.GUNPOWDER, 3)
            .define('R', Items.REDSTONE)
            .define('C', OldustryTags.Items.DUSTS_COAL)
            .pattern("RCR")
            .pattern("CRC")
            .pattern("RCR")
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output, OldustryRecipeProvider.id("gunpowder_from_coal_dust"));

        shaped(RecipeCategory.MISC, Items.RAIL, 8)
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .define('s', Items.STICK)
            .pattern("B B")
            .pattern("BsB")
            .pattern("B B")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output, OldustryRecipeProvider.id("rail_from_bronze"));

        shaped(RecipeCategory.MISC, OldustryItems.ITNT.get(), 4)
            .define('F', Items.FLINT)
            .define('T', Items.TNT)
            .pattern("FFF")
            .pattern("TTT")
            .pattern("FFF")
            .unlockedBy("has_tnt", has(Items.TNT))
            .save(output, OldustryRecipeProvider.id("itnt_rows"));

        shaped(RecipeCategory.MISC, OldustryItems.ITNT.get(), 4)
            .define('F', Items.FLINT)
            .define('T', Items.TNT)
            .pattern("FTF")
            .pattern("FTF")
            .pattern("FTF")
            .unlockedBy("has_tnt", has(Items.TNT))
            .save(output, OldustryRecipeProvider.id("itnt_columns"));

        shapeless(RecipeCategory.MISC, OldustryItems.DYNAMITE.get(), 8)
            .requires(OldustryItems.ITNT.get())
            .requires(Items.STRING)
            .unlockedBy("has_itnt", has(OldustryItems.ITNT.get()))
            .save(output, OldustryRecipeProvider.id("dynamite_from_itnt"));

        shaped(RecipeCategory.MISC, OldustryItems.STICKY_DYNAMITE.get(), 8)
            .define('D', OldustryItems.DYNAMITE.get())
            .define('R', OldustryItems.STICKY_RESIN.get())
            .pattern("DDD")
            .pattern("DRD")
            .pattern("DDD")
            .unlockedBy("has_dynamite", has(OldustryItems.DYNAMITE.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.REMOTE.get())
            .define('c', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('G', Items.GOLD_INGOT)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('T', Items.TNT)
            .pattern(" c ")
            .pattern("GCG")
            .pattern("TTT")
            .unlockedBy("has_electronic_circuit", has(OldustryItems.ELECTRONIC_CIRCUIT.get()))
            .save(output, OldustryRecipeProvider.id("remote_from_tnt"));
    }
}
