package com.oldustry.data.recipe;

import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

final class OldustryCableRecipes extends RecipeProvider {
    OldustryCableRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, OldustryItems.COPPER_CABLE.get(), 6)
            .define('C', Items.COPPER_INGOT)
            .pattern("CCC")
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.INSULATED_COPPER_CABLE.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.COPPER_CABLE.get())
            .unlockedBy("has_copper_cable", has(OldustryItems.COPPER_CABLE.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.INSULATED_COPPER_CABLE.get(), 6)
            .define('C', Items.COPPER_INGOT)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern("RRR")
            .pattern("CCC")
            .pattern("RRR")
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(output, OldustryRecipeProvider.id("insulated_copper_cable_from_ingots"));

        shaped(RecipeCategory.MISC, OldustryItems.INSULATED_COPPER_CABLE.get(), 6)
            .define('C', Items.COPPER_INGOT)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern("RCR")
            .pattern("RCR")
            .pattern("RCR")
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(output, OldustryRecipeProvider.id("insulated_copper_cable_from_ingots_alt"));

        shaped(RecipeCategory.MISC, OldustryItems.TIN_CABLE.get(), 9)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("TTT")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.GOLD_CABLE.get(), 12)
            .define('G', Items.GOLD_INGOT)
            .pattern("GGG")
            .unlockedBy("has_gold", has(Items.GOLD_INGOT))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.INSULATED_GOLD_CABLE.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.GOLD_CABLE.get())
            .unlockedBy("has_gold_cable", has(OldustryItems.GOLD_CABLE.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.GOLD_CABLE_2X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.INSULATED_GOLD_CABLE.get())
            .unlockedBy("has_insulated_gold", has(OldustryItems.INSULATED_GOLD_CABLE.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.GOLD_CABLE_2X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.GOLD_CABLE.get())
            .unlockedBy("has_gold_cable", has(OldustryItems.GOLD_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("2x_insulated_gold_cable_from_bare"));

        shaped(RecipeCategory.MISC, OldustryItems.INSULATED_GOLD_CABLE.get(), 4)
            .define('G', Items.GOLD_INGOT)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern(" R ")
            .pattern("RGR")
            .pattern(" R ")
            .unlockedBy("has_gold", has(Items.GOLD_INGOT))
            .save(output, OldustryRecipeProvider.id("insulated_gold_cable_from_ingot"));

        shaped(RecipeCategory.MISC, OldustryItems.HV_CABLE.get(), 12)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("III")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.INSULATED_HV_CABLE.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.HV_CABLE.get())
            .unlockedBy("has_hv_cable", has(OldustryItems.HV_CABLE.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.HV_CABLE_2X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.INSULATED_HV_CABLE.get())
            .unlockedBy("has_insulated_hv", has(OldustryItems.INSULATED_HV_CABLE.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.HV_CABLE_3X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.HV_CABLE_2X.get())
            .unlockedBy("has_2x_hv", has(OldustryItems.HV_CABLE_2X.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.HV_CABLE_2X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.HV_CABLE.get())
            .unlockedBy("has_hv_cable", has(OldustryItems.HV_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("2x_insulated_hv_cable_from_bare"));

        shapeless(RecipeCategory.MISC, OldustryItems.HV_CABLE_3X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.INSULATED_HV_CABLE.get())
            .unlockedBy("has_insulated_hv", has(OldustryItems.INSULATED_HV_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("3x_insulated_hv_cable_from_1x"));

        shapeless(RecipeCategory.MISC, OldustryItems.HV_CABLE_3X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryItems.HV_CABLE.get())
            .unlockedBy("has_hv_cable", has(OldustryItems.HV_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("3x_insulated_hv_cable_from_bare"));

        shaped(RecipeCategory.MISC, OldustryItems.INSULATED_HV_CABLE.get(), 4)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern(" R ")
            .pattern("RIR")
            .pattern(" R ")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output, OldustryRecipeProvider.id("insulated_hv_cable_from_ingot"));

        shaped(RecipeCategory.MISC, OldustryItems.GLASS_FIBRE_CABLE.get(), 4)
            .define('G', Items.GLASS)
            .define('R', Items.REDSTONE)
            .define('D', Items.DIAMOND)
            .pattern("GGG")
            .pattern("RDR")
            .pattern("GGG")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.GLASS_FIBRE_CABLE.get(), 4)
            .define('G', Items.GLASS)
            .define('R', Items.REDSTONE)
            .define('D', OldustryItems.INDUSTRIAL_DIAMOND.get())
            .pattern("GGG")
            .pattern("RDR")
            .pattern("GGG")
            .unlockedBy("has_industrial_diamond", has(OldustryItems.INDUSTRIAL_DIAMOND.get()))
            .save(output, OldustryRecipeProvider.id("glass_fibre_cable_from_industrial_diamond"));

        shaped(RecipeCategory.REDSTONE, OldustryItems.DETECTOR_CABLE.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('R', Items.REDSTONE)
            .define('I', OldustryItems.HV_CABLE_3X.get())
            .pattern(" C ")
            .pattern("RIR")
            .pattern(" R ")
            .unlockedBy("has_3x_hv", has(OldustryItems.HV_CABLE_3X.get()))
            .save(output);

        shaped(RecipeCategory.REDSTONE, OldustryItems.SPLITTER_CABLE.get())
            .define('R', Items.REDSTONE)
            .define('I', OldustryItems.HV_CABLE_3X.get())
            .define('L', Items.LEVER)
            .pattern(" R ")
            .pattern("ILI")
            .pattern(" R ")
            .unlockedBy("has_3x_hv", has(OldustryItems.HV_CABLE_3X.get()))
            .save(output);
    }
}
