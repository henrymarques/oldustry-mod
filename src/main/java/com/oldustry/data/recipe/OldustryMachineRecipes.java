package com.oldustry.data.recipe;

import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

final class OldustryMachineRecipes extends RecipeProvider {
    OldustryMachineRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, OldustryItems.MACHINE_CASING.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("III")
            .pattern("I I")
            .pattern("III")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.ADVANCED_MACHINE.get())
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .define('C', OldustryItems.CARBON_PLATE.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .pattern(" A ")
            .pattern("CMC")
            .pattern(" A ")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.ADVANCED_MACHINE.get())
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .define('C', OldustryItems.CARBON_PLATE.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .pattern(" C ")
            .pattern("AMA")
            .pattern(" C ")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output, OldustryRecipeProvider.id("advanced_machine_alt"));

        shaped(RecipeCategory.DECORATIONS, OldustryItems.BATBOX.get())
            .define('P', ItemTags.PLANKS)
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .pattern("PCP")
            .pattern("BBB")
            .pattern("PPP")
            .unlockedBy("has_re_battery", has(OldustryItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.GENERATOR.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .define('F', Items.FURNACE)
            .pattern("B")
            .pattern("M")
            .pattern("F")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.GENERATOR.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('F', OldustryItems.IMPROVED_FURNACE.get())
            .pattern(" B ")
            .pattern("III")
            .pattern(" F ")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output, OldustryRecipeProvider.id("generator_from_improved_furnace"));

        shaped(RecipeCategory.DECORATIONS, OldustryItems.MACERATOR.get())
            .define('F', Items.FLINT)
            .define('S', Items.COBBLESTONE)
            .define('M', OldustryItems.MACHINE_CASING.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("FFF")
            .pattern("SMS")
            .pattern(" C ")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.EXTRACTOR.get())
            .define('T', OldustryItems.TREETAP.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("TMT")
            .pattern("TCT")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.COMPRESSOR.get())
            .define('S', Items.STONE)
            .define('M', OldustryItems.MACHINE_CASING.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("S S")
            .pattern("SMS")
            .pattern("SCS")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.ELECTRIC_FURNACE.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('R', Items.REDSTONE)
            .define('F', OldustryItems.IMPROVED_FURNACE.get())
            .pattern(" C ")
            .pattern("RFR")
            .unlockedBy("has_improved_furnace", has(OldustryItems.IMPROVED_FURNACE.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.RECYCLER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('D', Items.DIRT)
            .define('M', OldustryItems.COMPRESSOR.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern(" G ")
            .pattern("DMD")
            .pattern("IDI")
            .unlockedBy("has_compressor", has(OldustryItems.COMPRESSOR.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.GEO_GENERATOR.get())
            .define('G', Items.GLASS)
            .define('C', OldustryItems.EMPTY_CELL.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('E', OldustryItems.GENERATOR.get())
            .pattern("GCG")
            .pattern("GCG")
            .pattern("IEI")
            .unlockedBy("has_generator", has(OldustryItems.GENERATOR.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.SOLAR_PANEL.get())
            .define('C', OldustryTags.Items.DUSTS_COAL)
            .define('G', Items.GLASS)
            .define('E', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('N', OldustryItems.GENERATOR.get())
            .pattern("CGC")
            .pattern("GCG")
            .pattern("ENE")
            .unlockedBy("has_generator", has(OldustryItems.GENERATOR.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.WATER_MILL.get(), 2)
            .define('S', Items.STICK)
            .define('P', ItemTags.PLANKS)
            .define('G', OldustryItems.GENERATOR.get())
            .pattern("SPS")
            .pattern("PGP")
            .pattern("SPS")
            .unlockedBy("has_generator", has(OldustryItems.GENERATOR.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.WIND_MILL.get())
            .define('I', Items.IRON_INGOT)
            .define('G', OldustryItems.GENERATOR.get())
            .pattern("I I")
            .pattern(" G ")
            .pattern("I I")
            .unlockedBy("has_generator", has(OldustryItems.GENERATOR.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.MFE.get())
            .define('C', OldustryItems.GOLD_CABLE_2X.get())
            .define('E', OldustryItems.ENERGY_CRYSTAL.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .pattern("CEC")
            .pattern("EME")
            .pattern("CEC")
            .unlockedBy("has_energy_crystal", has(OldustryItems.ENERGY_CRYSTAL.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.MFSU.get())
            .define('L', OldustryItems.LAPOTRON_CRYSTAL.get())
            .define('C', OldustryItems.ADVANCED_CIRCUIT.get())
            .define('M', OldustryItems.MFE.get())
            .define('A', OldustryItems.ADVANCED_MACHINE.get())
            .pattern("LCL")
            .pattern("LML")
            .pattern("LAL")
            .unlockedBy("has_mfe", has(OldustryItems.MFE.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.LV_TRANSFORMER.get())
            .define('P', ItemTags.PLANKS)
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('I', Items.COPPER_INGOT)
            .pattern("PCP")
            .pattern("III")
            .pattern("PCP")
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.MV_TRANSFORMER.get())
            .define('C', OldustryItems.GOLD_CABLE_2X.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .pattern(" C ")
            .pattern(" M ")
            .pattern(" C ")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.HV_TRANSFORMER.get())
            .define('C', OldustryItems.HV_CABLE_3X.get())
            .define('E', OldustryItems.ENERGY_CRYSTAL.get())
            .define('D', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('M', OldustryItems.MV_TRANSFORMER.get())
            .pattern(" C ")
            .pattern("DME")
            .pattern(" C ")
            .unlockedBy("has_mv_transformer", has(OldustryItems.MV_TRANSFORMER.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.LUMINATOR.get(), 8)
            .define('G', Items.GLASS)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('T', OldustryItems.TIN_CABLE.get())
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .pattern("ICI")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_tin_cable", has(OldustryItems.TIN_CABLE.get()))
            .save(output);

        // ── Mining pipe (JAR: tip is not a craftable item) ──
        shaped(RecipeCategory.MISC, OldustryItems.MINING_PIPE.get(), 8)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('T', OldustryItems.TREETAP.get())
            .pattern("I I")
            .pattern("I I")
            .pattern("ITI")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        // ── Miner ──
        shaped(RecipeCategory.DECORATIONS, OldustryItems.MINER.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .define('P', OldustryItems.MINING_PIPE.get())
            .pattern("CMC")
            .pattern(" P ")
            .pattern(" P ")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        // ── Pump ──
        shaped(RecipeCategory.DECORATIONS, OldustryItems.PUMP.get())
            .define('c', OldustryItems.EMPTY_CELL.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .define('P', OldustryItems.MINING_PIPE.get())
            .define('T', OldustryItems.TREETAP.get())
            .pattern("cCc")
            .pattern("cMc")
            .pattern("PTP")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        // ── Canner ──
        shaped(RecipeCategory.DECORATIONS, OldustryItems.CANNER.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .pattern("TCT")
            .pattern("TMT")
            .pattern("TTT")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        // ── Electrolyzer (JAR: c c / cCc / EME) ──
        shaped(RecipeCategory.DECORATIONS, OldustryItems.ELECTROLYZER.get())
            .define('c', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('E', OldustryItems.EMPTY_CELL.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .pattern("c c")
            .pattern("cCc")
            .pattern("EME")
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);

        // ── Magnetizer (JAR: RFR / RMR / RFR) ──
        shaped(RecipeCategory.DECORATIONS, OldustryItems.MAGNETIZER.get())
            .define('R', Items.REDSTONE)
            .define('F', OldustryItems.IRON_FENCE.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .pattern("RFR")
            .pattern("RMR")
            .pattern("RFR")
            .unlockedBy("has_iron_fence", has(OldustryItems.IRON_FENCE.get()))
            .save(output);

        // ── Induction (JAR: CCC / CFC / CMC) ──
        shaped(RecipeCategory.DECORATIONS, OldustryItems.INDUCTION.get())
            .define('C', Items.COPPER_INGOT)
            .define('F', OldustryItems.ELECTRIC_FURNACE.get())
            .define('M', OldustryItems.ADVANCED_MACHINE.get())
            .pattern("CCC")
            .pattern("CFC")
            .pattern("CMC")
            .unlockedBy("has_advanced_machine", has(OldustryItems.ADVANCED_MACHINE.get()))
            .save(output);

        // Personal Safe (JAR column: circuit / machine / chest). The chest is the vanilla block, same field as the toolbox.
        shaped(RecipeCategory.DECORATIONS, OldustryItems.PERSONAL_SAFE.get())
            .define('c', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('M', OldustryItems.MACHINE_CASING.get())
            .define('C', Items.CHEST)
            .pattern("c")
            .pattern("M")
            .pattern("C")
            .unlockedBy("has_machine", has(OldustryItems.MACHINE_CASING.get()))
            .save(output);
    }
}
