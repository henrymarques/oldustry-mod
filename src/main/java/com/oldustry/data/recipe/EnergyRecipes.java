package com.oldustry.data.recipe;

import com.oldustry.registry.ModItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

final class EnergyRecipes extends RecipeProvider {
    EnergyRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ModItems.MACHINE_CASING.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("III")
            .pattern("I I")
            .pattern("III")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.REFINED_IRON_INGOT.get(), 8)
            .requires(ModItems.MACHINE_CASING.get())
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output, OldustryRecipeProvider.id("refined_iron_from_machine_casing"));

        shaped(RecipeCategory.DECORATIONS, ModItems.ADVANCED_MACHINE.get())
            .define('A', ModItems.ADVANCED_ALLOY.get())
            .define('C', ModItems.CARBON_PLATE.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .pattern(" A ")
            .pattern("CMC")
            .pattern(" A ")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.ADVANCED_MACHINE.get())
            .define('A', ModItems.ADVANCED_ALLOY.get())
            .define('C', ModItems.CARBON_PLATE.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .pattern(" C ")
            .pattern("AMA")
            .pattern(" C ")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output, OldustryRecipeProvider.id("advanced_machine_alt"));

        // JAR reactorCoolantSimple: tin + water → 10k Coolant Cell (not fluid itemCoolant).
        shaped(RecipeCategory.MISC, ModItems.COOLANT_CELL.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('W', Items.WATER_BUCKET)
            .pattern(" T ")
            .pattern("TWT")
            .pattern(" T ")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.WRENCH.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("B B")
            .pattern("BBB")
            .pattern(" B ")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.COPPER_CABLE.get(), 6)
            .define('C', Items.COPPER_INGOT)
            .pattern("CCC")
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.INSULATED_COPPER_CABLE.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.COPPER_CABLE.get())
            .unlockedBy("has_copper_cable", has(ModItems.COPPER_CABLE.get()))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.INSULATED_COPPER_CABLE.get(), 6)
            .define('C', Items.COPPER_INGOT)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern("RRR")
            .pattern("CCC")
            .pattern("RRR")
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(output, OldustryRecipeProvider.id("insulated_copper_cable_from_ingots"));
        shaped(RecipeCategory.MISC, ModItems.INSULATED_COPPER_CABLE.get(), 6)
            .define('C', Items.COPPER_INGOT)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern("RCR")
            .pattern("RCR")
            .pattern("RCR")
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(output, OldustryRecipeProvider.id("insulated_copper_cable_from_ingots_alt"));

        shaped(RecipeCategory.MISC, ModItems.ELECTRONIC_CIRCUIT.get())
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .define('R', Items.REDSTONE)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("CCC")
            .pattern("RIR")
            .pattern("CCC")
            .unlockedBy("has_insulated_copper", has(ModItems.INSULATED_COPPER_CABLE.get()))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.RE_BATTERY.get())
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('R', Items.REDSTONE)
            .pattern(" C ")
            .pattern("TRT")
            .pattern("TRT")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.BATBOX.get())
            .define('P', ItemTags.PLANKS)
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .define('B', ModItems.RE_BATTERY.get())
            .pattern("PCP")
            .pattern("BBB")
            .pattern("PPP")
            .unlockedBy("has_re_battery", has(ModItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.GENERATOR.get())
            .define('B', ModItems.RE_BATTERY.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .define('F', Items.FURNACE)
            .pattern("B")
            .pattern("M")
            .pattern("F")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.MACERATOR.get())
            .define('F', Items.FLINT)
            .define('S', Items.COBBLESTONE)
            .define('M', ModItems.MACHINE_CASING.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("FFF")
            .pattern("SMS")
            .pattern(" C ")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.EXTRACTOR.get())
            .define('T', ModItems.TREETAP.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("TMT")
            .pattern("TCT")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.COMPRESSOR.get())
            .define('S', Items.STONE)
            .define('M', ModItems.MACHINE_CASING.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("S S")
            .pattern("SMS")
            .pattern("SCS")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.ELECTRIC_FURNACE.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('R', Items.REDSTONE)
            .define('F', ModItems.IMPROVED_FURNACE.get())
            .pattern(" C ")
            .pattern("RFR")
            .unlockedBy("has_improved_furnace", has(ModItems.IMPROVED_FURNACE.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.CUTTER.get())
            .define('A', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('I', Items.IRON_INGOT)
            .pattern("A A")
            .pattern(" A ")
            .pattern("I I")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.EU_READER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .define('E', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern(" G ")
            .pattern("CEC")
            .pattern("C C")
            .unlockedBy("has_electronic_circuit", has(ModItems.ELECTRONIC_CIRCUIT.get()))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.OVERCLOCKER_UPGRADE.get())
            .define('C', ModItems.COOLANT_CELL.get())
            .define('W', ModItems.INSULATED_COPPER_CABLE.get())
            .define('E', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("CCC")
            .pattern("WEW")
            .unlockedBy("has_coolant_cell", has(ModItems.COOLANT_CELL.get()))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.TIN_CABLE.get(), 9)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("TTT")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.BATPACK.get())
            .define('B', ModItems.RE_BATTERY.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("BCB")
            .pattern("BTB")
            .pattern("B B")
            .unlockedBy("has_re_battery", has(ModItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.GOLD_CABLE.get(), 12)
            .define('G', Items.GOLD_INGOT)
            .pattern("GGG")
            .unlockedBy("has_gold", has(Items.GOLD_INGOT))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.INSULATED_GOLD_CABLE.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.GOLD_CABLE.get())
            .unlockedBy("has_gold_cable", has(ModItems.GOLD_CABLE.get()))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.GOLD_CABLE_2X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.INSULATED_GOLD_CABLE.get())
            .unlockedBy("has_insulated_gold", has(ModItems.INSULATED_GOLD_CABLE.get()))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.GOLD_CABLE_2X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.GOLD_CABLE.get())
            .unlockedBy("has_gold_cable", has(ModItems.GOLD_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("2x_insulated_gold_cable_from_bare"));
        shaped(RecipeCategory.MISC, ModItems.INSULATED_GOLD_CABLE.get(), 4)
            .define('G', Items.GOLD_INGOT)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern(" R ")
            .pattern("RGR")
            .pattern(" R ")
            .unlockedBy("has_gold", has(Items.GOLD_INGOT))
            .save(output, OldustryRecipeProvider.id("insulated_gold_cable_from_ingot"));

        shaped(RecipeCategory.MISC, ModItems.HV_CABLE.get(), 12)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("III")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.INSULATED_HV_CABLE.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.HV_CABLE.get())
            .unlockedBy("has_hv_cable", has(ModItems.HV_CABLE.get()))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.HV_CABLE_2X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.INSULATED_HV_CABLE.get())
            .unlockedBy("has_insulated_hv", has(ModItems.INSULATED_HV_CABLE.get()))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.HV_CABLE_3X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.HV_CABLE_2X.get())
            .unlockedBy("has_2x_hv", has(ModItems.HV_CABLE_2X.get()))
            .save(output);
        shapeless(RecipeCategory.MISC, ModItems.HV_CABLE_2X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.HV_CABLE.get())
            .unlockedBy("has_hv_cable", has(ModItems.HV_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("2x_insulated_hv_cable_from_bare"));
        shapeless(RecipeCategory.MISC, ModItems.HV_CABLE_3X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.INSULATED_HV_CABLE.get())
            .unlockedBy("has_insulated_hv", has(ModItems.INSULATED_HV_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("3x_insulated_hv_cable_from_1x"));
        shapeless(RecipeCategory.MISC, ModItems.HV_CABLE_3X.get())
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(OldustryTags.Items.RUBBER)
            .requires(ModItems.HV_CABLE.get())
            .unlockedBy("has_hv_cable", has(ModItems.HV_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("3x_insulated_hv_cable_from_bare"));
        shaped(RecipeCategory.MISC, ModItems.INSULATED_HV_CABLE.get(), 4)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('R', OldustryTags.Items.RUBBER)
            .pattern(" R ")
            .pattern("RIR")
            .pattern(" R ")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output, OldustryRecipeProvider.id("insulated_hv_cable_from_ingot"));

        shaped(RecipeCategory.MISC, ModItems.GLASS_FIBRE_CABLE.get(), 4)
            .define('G', Items.GLASS)
            .define('R', Items.REDSTONE)
            .define('D', Items.DIAMOND)
            .pattern("GGG")
            .pattern("RDR")
            .pattern("GGG")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.GLASS_FIBRE_CABLE.get(), 4)
            .define('G', Items.GLASS)
            .define('R', Items.REDSTONE)
            .define('D', ModItems.INDUSTRIAL_DIAMOND.get())
            .pattern("GGG")
            .pattern("RDR")
            .pattern("GGG")
            .unlockedBy("has_industrial_diamond", has(ModItems.INDUSTRIAL_DIAMOND.get()))
            .save(output, OldustryRecipeProvider.id("glass_fibre_cable_from_industrial_diamond"));

        shaped(RecipeCategory.REDSTONE, ModItems.DETECTOR_CABLE.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.HV_CABLE_3X.get())
            .pattern(" C ")
            .pattern("RIR")
            .pattern(" R ")
            .unlockedBy("has_3x_hv", has(ModItems.HV_CABLE_3X.get()))
            .save(output);
        shaped(RecipeCategory.REDSTONE, ModItems.SPLITTER_CABLE.get())
            .define('R', Items.REDSTONE)
            .define('I', ModItems.HV_CABLE_3X.get())
            .define('L', Items.LEVER)
            .pattern(" R ")
            .pattern("ILI")
            .pattern(" R ")
            .unlockedBy("has_3x_hv", has(ModItems.HV_CABLE_3X.get()))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.SU_BATTERY.get(), 5)
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .define('R', Items.REDSTONE)
            .define('D', OldustryTags.Items.DUSTS_COAL)
            .pattern("C")
            .pattern("R")
            .pattern("D")
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.ENERGY_CRYSTAL.get())
            .define('R', Items.REDSTONE)
            .define('D', Items.DIAMOND)
            .pattern("RRR")
            .pattern("RDR")
            .pattern("RRR")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.LAPOTRON_CRYSTAL.get())
            .define('L', Items.LAPIS_LAZULI)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('D', ModItems.ENERGY_CRYSTAL.get())
            .pattern("LCL")
            .pattern("LDL")
            .pattern("LCL")
            .unlockedBy("has_energy_crystal", has(ModItems.ENERGY_CRYSTAL.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.RECYCLER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('D', Items.DIRT)
            .define('M', ModItems.COMPRESSOR.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern(" G ")
            .pattern("DMD")
            .pattern("IDI")
            .unlockedBy("has_compressor", has(ModItems.COMPRESSOR.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.GEO_GENERATOR.get())
            .define('G', Items.GLASS)
            .define('C', ModItems.EMPTY_CELL.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('E', ModItems.GENERATOR.get())
            .pattern("GCG")
            .pattern("GCG")
            .pattern("IEI")
            .unlockedBy("has_generator", has(ModItems.GENERATOR.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.SOLAR_PANEL.get())
            .define('C', OldustryTags.Items.DUSTS_COAL)
            .define('G', Items.GLASS)
            .define('E', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('N', ModItems.GENERATOR.get())
            .pattern("CGC")
            .pattern("GCG")
            .pattern("ENE")
            .unlockedBy("has_generator", has(ModItems.GENERATOR.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.WATER_MILL.get(), 2)
            .define('S', Items.STICK)
            .define('P', ItemTags.PLANKS)
            .define('G', ModItems.GENERATOR.get())
            .pattern("SPS")
            .pattern("PGP")
            .pattern("SPS")
            .unlockedBy("has_generator", has(ModItems.GENERATOR.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.WIND_MILL.get())
            .define('I', Items.IRON_INGOT)
            .define('G', ModItems.GENERATOR.get())
            .pattern("I I")
            .pattern(" G ")
            .pattern("I I")
            .unlockedBy("has_generator", has(ModItems.GENERATOR.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.MFE.get())
            .define('C', ModItems.GOLD_CABLE_2X.get())
            .define('E', ModItems.ENERGY_CRYSTAL.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .pattern("CEC")
            .pattern("EME")
            .pattern("CEC")
            .unlockedBy("has_energy_crystal", has(ModItems.ENERGY_CRYSTAL.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.MFSU.get())
            .define('L', ModItems.LAPOTRON_CRYSTAL.get())
            .define('C', ModItems.ADVANCED_CIRCUIT.get())
            .define('M', ModItems.MFE.get())
            .define('A', ModItems.ADVANCED_MACHINE.get())
            .pattern("LCL")
            .pattern("LML")
            .pattern("LAL")
            .unlockedBy("has_mfe", has(ModItems.MFE.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.LV_TRANSFORMER.get())
            .define('P', ItemTags.PLANKS)
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .define('I', Items.COPPER_INGOT)
            .pattern("PCP")
            .pattern("III")
            .pattern("PCP")
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.MV_TRANSFORMER.get())
            .define('C', ModItems.GOLD_CABLE_2X.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .pattern(" C ")
            .pattern(" M ")
            .pattern(" C ")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.HV_TRANSFORMER.get())
            .define('C', ModItems.HV_CABLE_3X.get())
            .define('E', ModItems.ENERGY_CRYSTAL.get())
            .define('D', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('M', ModItems.MV_TRANSFORMER.get())
            .pattern(" C ")
            .pattern("DEM")
            .pattern(" C ")
            .unlockedBy("has_mv_transformer", has(ModItems.MV_TRANSFORMER.get()))
            .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.LUMINATOR.get(), 8)
            .define('G', Items.GLASS)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('T', ModItems.TIN_CABLE.get())
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .pattern("ICI")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_tin_cable", has(ModItems.TIN_CABLE.get()))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.TRANSFORMER_UPGRADE.get())
            .define('G', Items.GLASS)
            .define('W', ModItems.GOLD_CABLE_2X.get())
            .define('T', ModItems.MV_TRANSFORMER.get())
            .define('E', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("GGG")
            .pattern("WTW")
            .pattern("GEG")
            .unlockedBy("has_mv_transformer", has(ModItems.MV_TRANSFORMER.get()))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.ENERGY_STORAGE_UPGRADE.get())
            .define('W', ItemTags.PLANKS)
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .define('B', ModItems.RE_BATTERY.get())
            .define('E', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("WWW")
            .pattern("CBC")
            .pattern("WEW")
            .unlockedBy("has_re_battery", has(ModItems.RE_BATTERY.get()))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.EJECTOR_UPGRADE.get())
            .define('P', Items.PISTON)
            .define('H', Items.HOPPER)
            .define('W', ModItems.INSULATED_COPPER_CABLE.get())
            .define('E', ModItems.ELECTRONIC_CIRCUIT.get())
            .pattern("PHP")
            .pattern("WEW")
            .unlockedBy("has_piston", has(Items.PISTON))
            .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.MINING_DRILL.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('B', ModItems.RE_BATTERY.get())
            .pattern(" I ")
            .pattern("ICI")
            .pattern("IBI")
            .unlockedBy("has_re_battery", has(ModItems.RE_BATTERY.get()))
            .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.CHAINSAW.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('B', ModItems.RE_BATTERY.get())
            .pattern(" II")
            .pattern("ICI")
            .pattern("BI ")
            .unlockedBy("has_re_battery", has(ModItems.RE_BATTERY.get()))
            .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.DIAMOND_DRILL.get())
            .define('D', Items.DIAMOND)
            .define('d', ModItems.MINING_DRILL.get())
            .pattern(" D ")
            .pattern("DdD")
            .unlockedBy("has_mining_drill", has(ModItems.MINING_DRILL.get()))
            .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.OD_SCANNER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('B', ModItems.RE_BATTERY.get())
            .define('c', ModItems.INSULATED_COPPER_CABLE.get())
            .pattern(" G ")
            .pattern("CBC")
            .pattern("ccc")
            .unlockedBy("has_re_battery", has(ModItems.RE_BATTERY.get()))
            .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.OV_SCANNER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('C', ModItems.ADVANCED_CIRCUIT.get())
            .define('S', ModItems.OD_SCANNER.get())
            .define('c', ModItems.GOLD_CABLE_2X.get())
            .pattern(" G ")
            .pattern("GCG")
            .pattern("cSc")
            .unlockedBy("has_od_scanner", has(ModItems.OD_SCANNER.get()))
            .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.ELECTRIC_WRENCH.get())
            .define('W', ModItems.WRENCH.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('B', ModItems.RE_BATTERY.get())
            .pattern("  W")
            .pattern(" C ")
            .pattern("B  ")
            .unlockedBy("has_wrench", has(ModItems.WRENCH.get()))
            .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.ELECTRIC_TREETAP.get())
            .define('W', ModItems.TREETAP.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('B', ModItems.RE_BATTERY.get())
            .pattern("  W")
            .pattern(" C ")
            .pattern("B  ")
            .unlockedBy("has_treetap", has(ModItems.TREETAP.get()))
            .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.ELECTRIC_HOE.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('B', ModItems.RE_BATTERY.get())
            .pattern("II ")
            .pattern(" C ")
            .pattern(" B ")
            .unlockedBy("has_re_battery", has(ModItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.LAPPACK.get())
            .define('L', Items.LAPIS_BLOCK)
            .define('A', ModItems.ADVANCED_CIRCUIT.get())
            .define('B', ModItems.BATPACK.get())
            .pattern("LAL")
            .pattern("LBL")
            .pattern("L L")
            .unlockedBy("has_batpack", has(ModItems.BATPACK.get()))
            .save(output);
        // JAR Recipies: iron frame + panel + cable; alt with iron helmet.
        shaped(RecipeCategory.COMBAT, ModItems.SOLAR_HELMET.get())
            .define('I', Items.IRON_INGOT)
            .define('S', ModItems.SOLAR_PANEL.get())
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .pattern("III")
            .pattern("ISI")
            .pattern("CCC")
            .unlockedBy("has_solar_panel", has(ModItems.SOLAR_PANEL.get()))
            .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.SOLAR_HELMET.get())
            .define('H', Items.IRON_HELMET)
            .define('S', ModItems.SOLAR_PANEL.get())
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .pattern(" H ")
            .pattern(" S ")
            .pattern("CCC")
            .unlockedBy("has_solar_panel", has(ModItems.SOLAR_PANEL.get()))
            .save(output, OldustryRecipeProvider.id("solar_helmet_from_iron_helmet"));
        shaped(RecipeCategory.COMBAT, ModItems.STATIC_BOOTS.get())
            .define('I', Items.IRON_INGOT)
            .define('S', ItemTags.WOOL)
            .define('C', ModItems.INSULATED_COPPER_CABLE.get())
            .pattern("I I")
            .pattern("ISI")
            .pattern("CCC")
            .unlockedBy("has_wool", has(ItemTags.WOOL))
            .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.FUEL_JETPACK.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('F', ModItems.FUEL_CAN.get())
            .define('R', Items.REDSTONE)
            .pattern("ICI")
            .pattern("IFI")
            .pattern("R R")
            .unlockedBy("has_fuel_can", has(ModItems.FUEL_CAN.get()))
            .save(output);

        // ── Mining pipe + tip ──
        shaped(RecipeCategory.MISC, ModItems.MINING_PIPE.get(), 6)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("T T")
            .pattern("T T")
            .pattern("T T")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.MINING_PIPE_TIP.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('D', Items.DIAMOND)
            .pattern("TDT")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);

        // ── Miner ──
        shaped(RecipeCategory.DECORATIONS, ModItems.MINER.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .define('P', ModItems.MINING_PIPE_TIP.get())
            .pattern("ICI")
            .pattern("PMP")
            .pattern("ICI")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);

        // ── Pump ──
        shaped(RecipeCategory.DECORATIONS, ModItems.PUMP.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .define('E', ModItems.EMPTY_CELL.get())
            .pattern("ECE")
            .pattern("IMI")
            .pattern("ECE")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);

        // ── Canner ──
        shaped(RecipeCategory.DECORATIONS, ModItems.CANNER.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .pattern("TCT")
            .pattern("TMT")
            .pattern("TCT")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);

        // ── Electrolyzer (JAR: c c / cCc / EME) ──
        shaped(RecipeCategory.DECORATIONS, ModItems.ELECTROLYZER.get())
            .define('c', ModItems.INSULATED_COPPER_CABLE.get())
            .define('C', ModItems.ELECTRONIC_CIRCUIT.get())
            .define('E', ModItems.EMPTY_CELL.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .pattern("c c")
            .pattern("cCc")
            .pattern("EME")
            .unlockedBy("has_machine_casing", has(ModItems.MACHINE_CASING.get()))
            .save(output);

        // ── Magnetizer (JAR: RFR / RMR / RFR) ──
        shaped(RecipeCategory.DECORATIONS, ModItems.MAGNETIZER.get())
            .define('R', Items.REDSTONE)
            .define('F', ModItems.IRON_FENCE.get())
            .define('M', ModItems.MACHINE_CASING.get())
            .pattern("RFR")
            .pattern("RMR")
            .pattern("RFR")
            .unlockedBy("has_iron_fence", has(ModItems.IRON_FENCE.get()))
            .save(output);

        // ── Induction (JAR: CCC / CFC / CMC) ──
        shaped(RecipeCategory.DECORATIONS, ModItems.INDUCTION.get())
            .define('C', Items.COPPER_INGOT)
            .define('F', ModItems.ELECTRIC_FURNACE.get())
            .define('M', ModItems.ADVANCED_MACHINE.get())
            .pattern("CCC")
            .pattern("CFC")
            .pattern("CMC")
            .unlockedBy("has_advanced_machine", has(ModItems.ADVANCED_MACHINE.get()))
            .save(output);

        // ── CF Pack ──
        shaped(RecipeCategory.MISC, ModItems.CF_PACK.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('P', ModItems.CF_PELLET.get())
            .define('C', ModItems.COMPRESSED_PLANTS.get())
            .pattern("TPT")
            .pattern("PCP")
            .pattern("TPT")
            .unlockedBy("has_cf_pellet", has(ModItems.CF_PELLET.get()))
            .save(output);
    }
}
