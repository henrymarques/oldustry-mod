package com.oldustry.data.recipe;

import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

/** Recipies.java 86–106. The 10k cell is already in {@link OldustryEnergyRecipes}. */
final class OldustryReactorRecipes extends RecipeProvider {
    OldustryReactorRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, OldustryItems.COOLANT_CELL_30K.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('C', OldustryItems.COOLANT_CELL.get())
            .pattern("TTT")
            .pattern("CCC")
            .pattern("TTT")
            .unlockedBy("has_coolant", has(OldustryItems.COOLANT_CELL.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.COOLANT_CELL_60K.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('C', OldustryItems.COOLANT_CELL_30K.get())
            .define('c', OldustryItems.DENSE_COPPER_PLATE.get())
            .pattern("TCT")
            .pattern("TcT")
            .pattern("TCT")
            .unlockedBy("has_coolant_30k", has(OldustryItems.COOLANT_CELL_30K.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.REACTOR_PLATING.get())
            .requires(OldustryItems.DENSE_COPPER_PLATE.get())
            .requires(OldustryItems.ADVANCED_ALLOY.get())
            .unlockedBy("has_advanced_alloy", has(OldustryItems.ADVANCED_ALLOY.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.HEAT_REACTOR_PLATING.get())
            .requires(OldustryItems.REACTOR_PLATING.get())
            .requires(OldustryItems.DENSE_COPPER_PLATE.get())
            .requires(OldustryItems.DENSE_COPPER_PLATE.get())
            .unlockedBy("has_plating", has(OldustryItems.REACTOR_PLATING.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.CONTAINMENT_REACTOR_PLATING.get())
            .requires(OldustryItems.REACTOR_PLATING.get())
            .requires(OldustryItems.ADVANCED_ALLOY.get())
            .requires(OldustryItems.ADVANCED_ALLOY.get())
            .unlockedBy("has_plating", has(OldustryItems.REACTOR_PLATING.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.HEAT_EXCHANGER.get())
            .define('c', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('C', OldustryItems.DENSE_COPPER_PLATE.get())
            .pattern(" c ")
            .pattern("TCT")
            .pattern(" T ")
            .unlockedBy("has_circuit", has(OldustryItems.ELECTRONIC_CIRCUIT.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.REACTOR_HEAT_EXCHANGER.get())
            .define('C', OldustryItems.DENSE_COPPER_PLATE.get())
            .define('S', OldustryItems.HEAT_EXCHANGER.get())
            .pattern("C")
            .pattern("S")
            .pattern("C")
            .unlockedBy("has_exchanger", has(OldustryItems.HEAT_EXCHANGER.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.COMPONENT_HEAT_EXCHANGER.get())
            .define('G', Items.GOLD_INGOT)
            .define('S', OldustryItems.HEAT_EXCHANGER.get())
            .pattern(" G ")
            .pattern("GSG")
            .pattern(" G ")
            .unlockedBy("has_exchanger", has(OldustryItems.HEAT_EXCHANGER.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.ADVANCED_HEAT_EXCHANGER.get())
            .define('G', OldustryItems.GLASS_FIBRE_CABLE.get())
            .define('c', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('S', OldustryItems.HEAT_EXCHANGER.get())
            .define('C', OldustryItems.DENSE_COPPER_PLATE.get())
            .pattern("GcG")
            .pattern("SCS")
            .pattern("GcG")
            .unlockedBy("has_exchanger", has(OldustryItems.HEAT_EXCHANGER.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.HEAT_VENT.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('#', Items.IRON_BARS)
            .pattern("I#I")
            .pattern("# #")
            .pattern("I#I")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.REACTOR_HEAT_VENT.get())
            .define('C', OldustryItems.DENSE_COPPER_PLATE.get())
            .define('V', OldustryItems.HEAT_VENT.get())
            .pattern("C")
            .pattern("V")
            .pattern("C")
            .unlockedBy("has_vent", has(OldustryItems.HEAT_VENT.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.OVERCLOCKED_HEAT_VENT.get())
            .define('G', Items.GOLD_INGOT)
            .define('V', OldustryItems.REACTOR_HEAT_VENT.get())
            .pattern("G")
            .pattern("V")
            .pattern("G")
            .unlockedBy("has_reactor_vent", has(OldustryItems.REACTOR_HEAT_VENT.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.COMPONENT_HEAT_VENT.get())
            .define('#', Items.IRON_BARS)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('V', OldustryItems.HEAT_VENT.get())
            .pattern("#T#")
            .pattern("TVT")
            .pattern("#T#")
            .unlockedBy("has_vent", has(OldustryItems.HEAT_VENT.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.ADVANCED_HEAT_VENT.get())
            .define('#', Items.IRON_BARS)
            .define('V', OldustryItems.HEAT_VENT.get())
            .define('D', Items.DIAMOND)
            .pattern("#V#")
            .pattern("#D#")
            .pattern("#V#")
            .unlockedBy("has_vent", has(OldustryItems.HEAT_VENT.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.HEATING_CELL.get())
            .define('c', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('L', OldustryItems.LAVA_CELL.get())
            .define('C', OldustryItems.DENSE_COPPER_PLATE.get())
            .pattern("c")
            .pattern("L")
            .pattern("C")
            .unlockedBy("has_lava_cell", has(OldustryItems.LAVA_CELL.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.NEUTRON_REFLECTOR.get())
            .define('T', OldustryItems.TIN_DUST.get())
            .define('c', OldustryItems.COAL_DUST.get())
            .define('C', OldustryItems.DENSE_COPPER_PLATE.get())
            .pattern("TcT")
            .pattern("cCc")
            .pattern("TcT")
            .unlockedBy("has_coal_dust", has(OldustryItems.COAL_DUST.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.THICK_NEUTRON_REFLECTOR.get())
            .define('R', OldustryItems.NEUTRON_REFLECTOR.get())
            .define('C', OldustryItems.DENSE_COPPER_PLATE.get())
            .pattern(" R ")
            .pattern("RCR")
            .pattern(" R ")
            .unlockedBy("has_reflector", has(OldustryItems.NEUTRON_REFLECTOR.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.RSH_CONDENSATOR.get())
            .define('R', Items.REDSTONE)
            .define('V', OldustryItems.HEAT_VENT.get())
            .define('S', OldustryItems.HEAT_EXCHANGER.get())
            .pattern("RRR")
            .pattern("RVR")
            .pattern("RSR")
            .unlockedBy("has_vent", has(OldustryItems.HEAT_VENT.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.LZH_CONDENSATOR.get())
            .define('R', Items.REDSTONE)
            .define('V', OldustryItems.REACTOR_HEAT_VENT.get())
            .define('C', OldustryItems.RSH_CONDENSATOR.get())
            .define('L', Items.LAPIS_BLOCK)
            .define('S', OldustryItems.REACTOR_HEAT_EXCHANGER.get())
            .pattern("RVR")
            .pattern("CLC")
            .pattern("RSR")
            .unlockedBy("has_rsh", has(OldustryItems.RSH_CONDENSATOR.get()))
            .save(output);
    }
}
