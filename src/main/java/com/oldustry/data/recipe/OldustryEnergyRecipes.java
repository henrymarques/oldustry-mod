package com.oldustry.data.recipe;

import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

final class OldustryEnergyRecipes extends RecipeProvider {
    OldustryEnergyRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shapeless(RecipeCategory.MISC, OldustryItems.REFINED_IRON_INGOT.get(), 8)
            .requires(OldustryItems.MACHINE_CASING.get())
            .unlockedBy("has_machine_casing", has(OldustryItems.MACHINE_CASING.get()))
            .save(output, OldustryRecipeProvider.id("refined_iron_from_machine_casing"));

        // JAR reactorCoolantSimple: tin + water → 10k Coolant Cell (not fluid itemCoolant).
        shaped(RecipeCategory.MISC, OldustryItems.COOLANT_CELL.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('W', Items.WATER_BUCKET)
            .pattern(" T ")
            .pattern("TWT")
            .pattern(" T ")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.COOLANT_CELL.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('W', OldustryItems.WATER_CELL.get())
            .pattern(" T ")
            .pattern("TWT")
            .pattern(" T ")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output, OldustryRecipeProvider.id("coolant_cell_from_cell"));

        shaped(RecipeCategory.MISC, OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('R', Items.REDSTONE)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("CCC")
            .pattern("RIR")
            .pattern("CCC")
            .unlockedBy("has_insulated_copper", has(OldustryItems.INSULATED_COPPER_CABLE.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('R', Items.REDSTONE)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("CRC")
            .pattern("CIC")
            .pattern("CRC")
            .unlockedBy("has_insulated_copper", has(OldustryItems.INSULATED_COPPER_CABLE.get()))
            .save(output, OldustryRecipeProvider.id("electronic_circuit_alt"));

        shaped(RecipeCategory.MISC, OldustryItems.RE_BATTERY.get())
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .define('R', Items.REDSTONE)
            .pattern(" C ")
            .pattern("TRT")
            .pattern("TRT")
            .unlockedBy("has_tin", has(OldustryTags.Items.INGOTS_TIN))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.OVERCLOCKER_UPGRADE.get())
            .define('C', OldustryItems.COOLANT_CELL.get())
            .define('W', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('E', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("CCC")
            .pattern("WEW")
            .unlockedBy("has_coolant_cell", has(OldustryItems.COOLANT_CELL.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.SU_BATTERY.get(), 5)
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('R', Items.REDSTONE)
            .define('D', OldustryTags.Items.DUSTS_COAL)
            .pattern("C")
            .pattern("R")
            .pattern("D")
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.SU_BATTERY.get(), 5)
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('R', Items.REDSTONE)
            .define('D', OldustryTags.Items.DUSTS_COAL)
            .pattern("C")
            .pattern("D")
            .pattern("R")
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output, OldustryRecipeProvider.id("su_battery_swapped"));

        shaped(RecipeCategory.MISC, OldustryItems.SU_BATTERY.get(), 8)
            .define('c', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('C', OldustryItems.HYDRATED_COAL_DUST.get())
            .define('R', Items.REDSTONE)
            .pattern("c")
            .pattern("C")
            .pattern("R")
            .unlockedBy("has_hydrated_coal_dust", has(OldustryItems.HYDRATED_COAL_DUST.get()))
            .save(output, OldustryRecipeProvider.id("su_battery_from_hydrated_coal"));

        shaped(RecipeCategory.MISC, OldustryItems.SU_BATTERY.get(), 8)
            .define('c', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('C', OldustryItems.HYDRATED_COAL_DUST.get())
            .define('R', Items.REDSTONE)
            .pattern("c")
            .pattern("R")
            .pattern("C")
            .unlockedBy("has_hydrated_coal_dust", has(OldustryItems.HYDRATED_COAL_DUST.get()))
            .save(output, OldustryRecipeProvider.id("su_battery_from_hydrated_coal_swapped"));

        shaped(RecipeCategory.MISC, OldustryItems.ENERGY_CRYSTAL.get())
            .define('R', Items.REDSTONE)
            .define('D', Items.DIAMOND)
            .pattern("RRR")
            .pattern("RDR")
            .pattern("RRR")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.ENERGY_CRYSTAL.get())
            .define('R', Items.REDSTONE)
            .define('D', OldustryItems.INDUSTRIAL_DIAMOND.get())
            .pattern("RRR")
            .pattern("RDR")
            .pattern("RRR")
            .unlockedBy("has_industrial_diamond", has(OldustryItems.INDUSTRIAL_DIAMOND.get()))
            .save(output, OldustryRecipeProvider.id("energy_crystal_from_industrial_diamond"));

        shaped(RecipeCategory.MISC, OldustryItems.LAPOTRON_CRYSTAL.get())
            .define('L', Items.LAPIS_LAZULI)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('D', OldustryItems.ENERGY_CRYSTAL.get())
            .pattern("LCL")
            .pattern("LDL")
            .pattern("LCL")
            .unlockedBy("has_energy_crystal", has(OldustryItems.ENERGY_CRYSTAL.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.TRANSFORMER_UPGRADE.get())
            .define('G', Items.GLASS)
            .define('W', OldustryItems.GOLD_CABLE_2X.get())
            .define('T', OldustryItems.MV_TRANSFORMER.get())
            .define('E', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("GGG")
            .pattern("WTW")
            .pattern("GEG")
            .unlockedBy("has_mv_transformer", has(OldustryItems.MV_TRANSFORMER.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.ENERGY_STORAGE_UPGRADE.get())
            .define('W', ItemTags.PLANKS)
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .define('E', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("WWW")
            .pattern("CBC")
            .pattern("WEW")
            .unlockedBy("has_re_battery", has(OldustryItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.EJECTOR_UPGRADE.get())
            .define('P', Items.PISTON)
            .define('H', Items.HOPPER)
            .define('W', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('E', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("PHP")
            .pattern("WEW")
            .unlockedBy("has_piston", has(Items.PISTON))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.ADVANCED_CIRCUIT.get())
            .define('R', Items.REDSTONE)
            .define('G', Items.GLOWSTONE_DUST)
            .define('L', Items.LAPIS_LAZULI)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("RGR")
            .pattern("LCL")
            .pattern("RGR")
            .unlockedBy("has_electronic_circuit", has(OldustryItems.ELECTRONIC_CIRCUIT.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.ADVANCED_CIRCUIT.get())
            .define('R', Items.REDSTONE)
            .define('G', Items.GLOWSTONE_DUST)
            .define('L', Items.LAPIS_LAZULI)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern("RLR")
            .pattern("GCG")
            .pattern("RLR")
            .unlockedBy("has_electronic_circuit", has(OldustryItems.ELECTRONIC_CIRCUIT.get()))
            .save(output, OldustryRecipeProvider.id("advanced_circuit_alt"));
    }
}
