package com.oldustry.data.recipe;

import com.oldustry.item.SprayerItem;
import com.oldustry.registry.ModDataComponents;
import com.oldustry.registry.ModItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

final class MiscRecipes extends RecipeProvider {
    MiscRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ModItems.STONE_MUG.get())
            .define('S', Items.COBBLESTONE)
            .pattern("SS ")
            .pattern("SSS")
            .pattern("SS ")
            .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
            .save(output);

        shapeless(RecipeCategory.MISC, ModItems.COFFEE_POWDER.get())
            .requires(ModItems.COFFEE_BEANS.get())
            .unlockedBy("has_coffee_beans", has(ModItems.COFFEE_BEANS.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, ModItems.COFFEE_MUG_COLD.get())
            .requires(ModItems.STONE_MUG.get())
            .requires(ModItems.COFFEE_POWDER.get())
            .requires(Items.WATER_BUCKET)
            .unlockedBy("has_coffee_powder", has(ModItems.COFFEE_POWDER.get()))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.COFFEE_MUG_COLD.get())
            .requires(ModItems.STONE_MUG.get())
            .requires(ModItems.COFFEE_POWDER.get())
            .requires(ModItems.WATER_CELL.get())
            .unlockedBy("has_coffee_powder", has(ModItems.COFFEE_POWDER.get()))
            .save(output, OldustryRecipeProvider.id("coffee_mug_cold_from_cell"));

        shapeless(RecipeCategory.MISC, ModItems.COFFEE_MUG.get())
            .requires(ModItems.COFFEE_MUG_DARK.get())
            .requires(Items.SUGAR)
            .requires(Items.MILK_BUCKET)
            .unlockedBy("has_coffee_mug_dark", has(ModItems.COFFEE_MUG_DARK.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, ModItems.FERTILIZER.get(), 2)
            .requires(ModItems.SCRAP.get())
            .requires(Items.BONE_MEAL)
            .unlockedBy("has_scrap", has(ModItems.SCRAP.get()))
            .save(output, OldustryRecipeProvider.id("fertilizer_from_scrap"));

        shapeless(RecipeCategory.MISC, ModItems.FERTILIZER.get(), 2)
            .requires(ModItems.SCRAP.get())
            .requires(ModItems.SCRAP.get())
            .requires(ModItems.FERTILIZER.get())
            .unlockedBy("has_fertilizer", has(ModItems.FERTILIZER.get()))
            .save(output, OldustryRecipeProvider.id("fertilizer_from_fertilizer"));

        shaped(RecipeCategory.MISC, ModItems.WEED_EX.get())
            .define('R', Items.REDSTONE)
            .define('G', ModItems.GRIN_POWDER.get())
            .define('C', ModItems.EMPTY_CELL.get())
            .pattern("R")
            .pattern("G")
            .pattern("C")
            .unlockedBy("has_grin_powder", has(ModItems.GRIN_POWDER.get()))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.BARREL.get())
            .define('P', ItemTags.PLANKS)
            .define('W', ModItems.RUBBER_LOG.get())
            .pattern("P")
            .pattern("W")
            .pattern("P")
            .unlockedBy("has_rubber_log", has(ModItems.RUBBER_LOG.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.TREETAP.get())
            .define('P', ItemTags.PLANKS)
            .pattern(" P ")
            .pattern("PPP")
            .pattern("P  ")
            .unlockedBy("has_planks", has(ItemTags.PLANKS))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.RUBBER_SHEET.get(), 3)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern("RRR")
            .pattern("RRR")
            .unlockedBy("has_rubber", has(OldustryTags.Items.RUBBER))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.IMPROVED_FURNACE.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .pattern("III")
            .pattern("I I")
            .pattern("III")
            .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.IMPROVED_FURNACE.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('F', Items.FURNACE)
            .pattern(" I ")
            .pattern("I I")
            .pattern("IFI")
            .unlockedBy("has_furnace", has(Items.FURNACE))
            .save(output, OldustryRecipeProvider.id("improved_furnace_from_furnace"));

        shapeless(RecipeCategory.MISC, ModItems.CONSTRUCTION_FOAM.get(), 3)
            .requires(OldustryTags.Items.DUSTS_CLAY)
            .requires(Items.WATER_BUCKET)
            .requires(Items.REDSTONE)
            .requires(OldustryTags.Items.DUSTS_COAL)
            .unlockedBy("has_clay_dust", has(OldustryTags.Items.DUSTS_CLAY))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.CONSTRUCTION_FOAM.get(), 3)
            .requires(OldustryTags.Items.DUSTS_CLAY)
            .requires(ModItems.WATER_CELL.get())
            .requires(Items.REDSTONE)
            .requires(OldustryTags.Items.DUSTS_COAL)
            .unlockedBy("has_clay_dust", has(OldustryTags.Items.DUSTS_CLAY))
            .save(output, OldustryRecipeProvider.id("construction_foam_from_cell"));

        // JAR: cobble/cobble/empty, cobble/stick/empty, empty/empty/cobble. Result is an empty tank.
        shaped(RecipeCategory.TOOLS, new ItemStackTemplate(
                ModItems.CF_SPRAYER.get(),
                DataComponentPatch.builder().set(ModDataComponents.FOAM.get(), SprayerItem.CRAFTED).build()))
            .define('S', Items.COBBLESTONE)
            .define('s', Items.STICK)
            .pattern("SS ")
            .pattern("Ss ")
            .pattern("  S")
            .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
            .save(output);

        shaped(RecipeCategory.REDSTONE, ModItems.REINFORCED_DOOR.get())
            .define('S', ModItems.REINFORCED_STONE.get())
            .pattern("SS")
            .pattern("SS")
            .pattern("SS")
            .unlockedBy("has_reinforced_stone", has(ModItems.REINFORCED_STONE.get()))
            .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.REINFORCED_STONE.get(), 8)
            .define('S', Items.STONE)
            .define('A', ModItems.ADVANCED_ALLOY.get())
            .pattern("SSS")
            .pattern("SAS")
            .pattern("SSS")
            .unlockedBy("has_alloy", has(ModItems.ADVANCED_ALLOY.get()))
            .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.REINFORCED_GLASS.get(), 7)
            .define('G', Items.GLASS)
            .define('A', ModItems.ADVANCED_ALLOY.get())
            .pattern("GAG")
            .pattern("GGG")
            .pattern("GAG")
            .unlockedBy("has_alloy", has(ModItems.ADVANCED_ALLOY.get()))
            .save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.REINFORCED_GLASS.get(), 7)
            .define('G', Items.GLASS)
            .define('A', ModItems.ADVANCED_ALLOY.get())
            .pattern("GGG")
            .pattern("AGA")
            .pattern("GGG")
            .unlockedBy("has_alloy", has(ModItems.ADVANCED_ALLOY.get()))
            .save(output, OldustryRecipeProvider.id("reinforced_glass_rows"));

        shaped(RecipeCategory.DECORATIONS, ModItems.WOODEN_SCAFFOLD.get(), 16)
            .define('P', ItemTags.PLANKS)
            .define('s', Items.STICK)
            .pattern("PPP")
            .pattern(" s ")
            .pattern("s s")
            .unlockedBy("has_planks", has(ItemTags.PLANKS))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.IRON_SCAFFOLD.get(), 16)
            .define('P', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('s', ModItems.IRON_FENCE.get())
            .pattern("PPP")
            .pattern(" s ")
            .pattern("s s")
            .unlockedBy("has_iron_fence", has(ModItems.IRON_FENCE.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.IRON_FENCE.get(), 12)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("III")
            .pattern("III")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.EMPTY_CELL.get(), 16)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern(" T ")
            .pattern("T T")
            .pattern(" T ")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.FUEL_CAN.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern(" TT")
            .pattern("T T")
            .pattern("TTT")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.TIN_CAN.get(), 4)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("T T")
            .pattern("TTT")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shapeless(RecipeCategory.MISC, ModItems.WATER_CELL.get())
            .requires(ModItems.EMPTY_CELL.get())
            .requires(Items.WATER_BUCKET)
            .unlockedBy("has_empty_cell", has(ModItems.EMPTY_CELL.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, ModItems.LAVA_CELL.get())
            .requires(ModItems.EMPTY_CELL.get())
            .requires(Items.LAVA_BUCKET)
            .unlockedBy("has_empty_cell", has(ModItems.EMPTY_CELL.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, ModItems.COAL_CELL.get())
            .requires(ModItems.EMPTY_CELL.get())
            .requires(ModItems.HYDRATED_COAL_CLUMP.get())
            .unlockedBy("has_hydrated_coal_clump", has(ModItems.HYDRATED_COAL_CLUMP.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, ModItems.BIO_CELL.get())
            .requires(ModItems.EMPTY_CELL.get())
            .requires(ModItems.COMPRESSED_PLANTS.get())
            .unlockedBy("has_compressed_plants", has(ModItems.COMPRESSED_PLANTS.get()))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.ADVANCED_CIRCUIT.get())
            .define('R', Items.REDSTONE)
            .define('G', Items.GLOWSTONE_DUST)
            .define('L', Items.LAPIS_LAZULI)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("RGR")
            .pattern("LCL")
            .pattern("RGR")
            .unlockedBy("has_electronic_circuit", has(ModItems.ELECTRONIC_CIRCUIT.get()))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.ADVANCED_CIRCUIT.get())
            .define('R', Items.REDSTONE)
            .define('G', Items.GLOWSTONE_DUST)
            .define('L', Items.LAPIS_LAZULI)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("RLR")
            .pattern("GCG")
            .pattern("RLR")
            .unlockedBy("has_electronic_circuit", has(ModItems.ELECTRONIC_CIRCUIT.get()))
            .save(output, OldustryRecipeProvider.id("advanced_circuit_alt"));

        shaped(RecipeCategory.MISC, ModItems.IRIDIUM_PLATE.get())
            .define('I', ModItems.IRIDIUM_ORE.get())
            .define('A', ModItems.ADVANCED_ALLOY.get())
            .define('D', Items.DIAMOND)
            .pattern("IAI")
            .pattern("ADA")
            .pattern("IAI")
            .unlockedBy("has_iridium_ore", has(ModItems.IRIDIUM_ORE.get()))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.IRIDIUM_PLATE.get())
            .define('I', ModItems.IRIDIUM_ORE.get())
            .define('A', ModItems.ADVANCED_ALLOY.get())
            .define('D', ModItems.INDUSTRIAL_DIAMOND.get())
            .pattern("IAI")
            .pattern("ADA")
            .pattern("IAI")
            .unlockedBy("has_iridium_ore", has(ModItems.IRIDIUM_ORE.get()))
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

        shaped(RecipeCategory.MISC, ModItems.ITNT.get(), 4)
            .define('F', Items.FLINT)
            .define('T', Items.TNT)
            .pattern("FFF")
            .pattern("TTT")
            .pattern("FFF")
            .unlockedBy("has_tnt", has(Items.TNT))
            .save(output, OldustryRecipeProvider.id("itnt_rows"));
        shaped(RecipeCategory.MISC, ModItems.ITNT.get(), 4)
            .define('F', Items.FLINT)
            .define('T', Items.TNT)
            .pattern("FTF")
            .pattern("FTF")
            .pattern("FTF")
            .unlockedBy("has_tnt", has(Items.TNT))
            .save(output, OldustryRecipeProvider.id("itnt_columns"));
        shapeless(RecipeCategory.MISC, ModItems.DYNAMITE.get(), 8)
            .requires(ModItems.ITNT.get())
            .requires(Items.STRING)
            .unlockedBy("has_itnt", has(ModItems.ITNT.get()))
            .save(output, OldustryRecipeProvider.id("dynamite_from_itnt"));
        shaped(RecipeCategory.MISC, ModItems.STICKY_DYNAMITE.get(), 8)
            .define('D', ModItems.DYNAMITE.get())
            .define('R', ModItems.STICKY_RESIN.get())
            .pattern("DDD")
            .pattern("DRD")
            .pattern("DDD")
            .unlockedBy("has_dynamite", has(ModItems.DYNAMITE.get()))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.REMOTE.get())
            .define('c', ModItems.INSULATED_COPPER_CABLE.get())
            .define('G', Items.GOLD_INGOT)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('T', Items.TNT)
            .pattern(" c ")
            .pattern("GCG")
            .pattern("TTT")
            .unlockedBy("has_electronic_circuit", has(ModItems.ELECTRONIC_CIRCUIT.get()))
            .save(output, OldustryRecipeProvider.id("remote_from_tnt"));
    }
}
