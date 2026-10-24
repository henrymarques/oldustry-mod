package com.oldustry.data.recipe;

import com.oldustry.item.equipment.CfPackItem;
import com.oldustry.item.construction.SprayerItem;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

final class OldustryElectricEquipmentRecipes extends RecipeProvider {
    OldustryElectricEquipmentRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.TOOLS, OldustryItems.WRENCH.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("B B")
            .pattern("BBB")
            .pattern(" B ")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.CUTTER.get())
            .define('A', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('I', Items.IRON_INGOT)
            .pattern("A A")
            .pattern(" A ")
            .pattern("I I")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.EU_READER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .define('E', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .pattern(" G ")
            .pattern("CEC")
            .pattern("C C")
            .unlockedBy("has_electronic_circuit", has(OldustryItems.ELECTRONIC_CIRCUIT.get()))
            .save(output);

        shaped(RecipeCategory.COMBAT, OldustryItems.BATPACK.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("BCB")
            .pattern("BTB")
            .pattern("B B")
            .unlockedBy("has_re_battery", has(OldustryItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.MINING_DRILL.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .pattern(" I ")
            .pattern("ICI")
            .pattern("IBI")
            .unlockedBy("has_re_battery", has(OldustryItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.CHAINSAW.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .pattern(" II")
            .pattern("ICI")
            .pattern("BI ")
            .unlockedBy("has_re_battery", has(OldustryItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.DIAMOND_DRILL.get())
            .define('D', Items.DIAMOND)
            .define('d', OldustryItems.MINING_DRILL.get())
            .pattern(" D ")
            .pattern("DdD")
            .unlockedBy("has_mining_drill", has(OldustryItems.MINING_DRILL.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.OD_SCANNER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .define('c', OldustryItems.INSULATED_COPPER_CABLE.get())
            .pattern(" G ")
            .pattern("CBC")
            .pattern("ccc")
            .unlockedBy("has_re_battery", has(OldustryItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.OV_SCANNER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('C', OldustryItems.ADVANCED_CIRCUIT.get())
            .define('S', OldustryItems.OD_SCANNER.get())
            .define('c', OldustryItems.GOLD_CABLE_2X.get())
            .pattern(" G ")
            .pattern("GCG")
            .pattern("cSc")
            .unlockedBy("has_od_scanner", has(OldustryItems.OD_SCANNER.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.OV_SCANNER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('C', OldustryItems.ADVANCED_CIRCUIT.get())
            .define('S', DataComponentIngredient.of(false, OldustryDataComponents.ENERGY.get(), 10000, OldustryItems.RE_BATTERY.get()))
            .define('c', OldustryItems.GOLD_CABLE_2X.get())
            .pattern(" G ")
            .pattern("GCG")
            .pattern("cSc")
            .unlockedBy("has_re_battery", has(OldustryItems.RE_BATTERY.get()))
            .save(output, OldustryRecipeProvider.id("ov_scanner_from_charged_battery"));

        shaped(RecipeCategory.TOOLS, OldustryItems.ELECTRIC_WRENCH.get())
            .define('W', OldustryItems.WRENCH.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .pattern("  W")
            .pattern(" C ")
            .pattern("B  ")
            .unlockedBy("has_wrench", has(OldustryItems.WRENCH.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.ELECTRIC_TREETAP.get())
            .define('W', OldustryItems.TREETAP.get())
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .pattern("  W")
            .pattern(" C ")
            .pattern("B  ")
            .unlockedBy("has_treetap", has(OldustryItems.TREETAP.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.ELECTRIC_HOE.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('B', OldustryItems.RE_BATTERY.get())
            .pattern("II ")
            .pattern(" C ")
            .pattern(" B ")
            .unlockedBy("has_re_battery", has(OldustryItems.RE_BATTERY.get()))
            .save(output);

        shaped(RecipeCategory.COMBAT, OldustryItems.LAPPACK.get())
            .define('L', Items.LAPIS_BLOCK)
            .define('A', OldustryItems.ADVANCED_CIRCUIT.get())
            .define('B', OldustryItems.BATPACK.get())
            .pattern("LAL")
            .pattern("LBL")
            .pattern("L L")
            .unlockedBy("has_batpack", has(OldustryItems.BATPACK.get()))
            .save(output);

        // JAR Recipies: iron frame + panel + cable; alt with iron helmet.
        shaped(RecipeCategory.COMBAT, OldustryItems.SOLAR_HELMET.get())
            .define('I', Items.IRON_INGOT)
            .define('S', OldustryItems.SOLAR_PANEL.get())
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .pattern("III")
            .pattern("ISI")
            .pattern("CCC")
            .unlockedBy("has_solar_panel", has(OldustryItems.SOLAR_PANEL.get()))
            .save(output);

        shaped(RecipeCategory.COMBAT, OldustryItems.SOLAR_HELMET.get())
            .define('H', Items.IRON_HELMET)
            .define('S', OldustryItems.SOLAR_PANEL.get())
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .pattern(" H ")
            .pattern(" S ")
            .pattern("CCC")
            .unlockedBy("has_solar_panel", has(OldustryItems.SOLAR_PANEL.get()))
            .save(output, OldustryRecipeProvider.id("solar_helmet_from_iron_helmet"));

        shaped(RecipeCategory.COMBAT, OldustryItems.STATIC_BOOTS.get())
            .define('I', Items.IRON_INGOT)
            .define('S', ItemTags.WOOL)
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .pattern("I I")
            .pattern("ISI")
            .pattern("CCC")
            .unlockedBy("has_wool", has(ItemTags.WOOL))
            .save(output);

        shaped(RecipeCategory.COMBAT, OldustryItems.STATIC_BOOTS.get())
            .define('H', Items.IRON_BOOTS)
            .define('S', ItemTags.WOOL)
            .define('C', OldustryItems.INSULATED_COPPER_CABLE.get())
            .pattern(" H ")
            .pattern(" S ")
            .pattern("CCC")
            .unlockedBy("has_wool", has(ItemTags.WOOL))
            .save(output, OldustryRecipeProvider.id("static_boots_from_iron_boots"));

        shaped(RecipeCategory.COMBAT, OldustryItems.FUEL_JETPACK.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('F', OldustryItems.FUEL_CAN.get())
            .define('R', Items.REDSTONE)
            .pattern("ICI")
            .pattern("IFI")
            .pattern("R R")
            .unlockedBy("has_fuel_can", has(OldustryItems.FUEL_CAN.get()))
            .save(output);

        shaped(RecipeCategory.COMBAT, OldustryItems.ELECTRIC_JETPACK.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', OldustryItems.ADVANCED_CIRCUIT.get())
            .define('B', OldustryItems.BATBOX.get())
            .define('G', Items.GLOWSTONE_DUST)
            .pattern("ICI")
            .pattern("IBI")
            .pattern("G G")
            .unlockedBy("has_batbox", has(OldustryItems.BATBOX.get()))
            .save(output);

        // JAR: empty sprayer / circuit / empty sprayer, fuel can / tin / fuel can, fuel can / empty / fuel can.
        // Result is an empty pack. Only the crafted-empty sprayer is accepted.
        shaped(RecipeCategory.MISC, new ItemStackTemplate(
                OldustryItems.CF_PACK.get(),
                DataComponentPatch.builder().set(OldustryDataComponents.FOAM.get(), CfPackItem.CRAFTED).build()))
            .define('S', DataComponentIngredient.of(false, OldustryDataComponents.FOAM.get(), SprayerItem.CRAFTED, OldustryItems.CF_SPRAYER.get()))
            .define('C', OldustryItems.ELECTRONIC_CIRCUIT.get())
            .define('F', OldustryItems.FUEL_CAN.get())
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("SCS")
            .pattern("FTF")
            .pattern("F F")
            .unlockedBy("has_fuel_can", has(OldustryItems.FUEL_CAN.get()))
            .save(output);
    }
}
