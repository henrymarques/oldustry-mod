package com.oldustry.data.recipe;

import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ColorCollection;
import net.neoforged.neoforge.common.Tags;

final class OldustryEquipmentRecipes extends RecipeProvider {
    OldustryEquipmentRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        tool("BBB", " S ", " S ", OldustryItems.BRONZE_PICKAXE.get());
        tool("BB", "SB", "S ", OldustryItems.BRONZE_AXE.get());
        tool("BB", "S ", "S ", OldustryItems.BRONZE_HOE.get());
        shaped(RecipeCategory.COMBAT, OldustryItems.BRONZE_SWORD.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .define('S', Items.STICK)
            .pattern("B")
            .pattern("B")
            .pattern("S")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);
        tool(" B ", " S ", " S ", OldustryItems.BRONZE_SHOVEL.get());

        shaped(RecipeCategory.COMBAT, OldustryItems.BRONZE_HELMET.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("BBB")
            .pattern("B B")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.BRONZE_CHESTPLATE.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("B B")
            .pattern("BBB")
            .pattern("BBB")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.BRONZE_LEGGINGS.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("BBB")
            .pattern("B B")
            .pattern("B B")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.BRONZE_BOOTS.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("B B")
            .pattern("B B")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);

        shaped(RecipeCategory.COMBAT, OldustryItems.COMPOSITE_VEST.get())
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .define('L', Items.LEATHER_CHESTPLATE)
            .define('I', Items.IRON_CHESTPLATE)
            .pattern("A A")
            .pattern("ALA")
            .pattern("AIA")
            .unlockedBy("has_alloy", has(OldustryItems.ADVANCED_ALLOY.get()))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.COMPOSITE_VEST.get())
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .define('L', Items.LEATHER_CHESTPLATE)
            .define('I', Items.IRON_CHESTPLATE)
            .pattern("A A")
            .pattern("AIA")
            .pattern("ALA")
            .unlockedBy("has_alloy", has(OldustryItems.ADVANCED_ALLOY.get()))
            .save(output, OldustryRecipeProvider.id("composite_vest_swapped"));

        shaped(RecipeCategory.TOOLS, OldustryItems.PAINTER.get())
            .define('C', ItemTags.WOOL)
            .define('I', Tags.Items.INGOTS_IRON)
            .pattern(" CC")
            .pattern(" IC")
            .pattern("I  ")
            .unlockedBy("has_wool", has(ItemTags.WOOL))
            .save(output);

        ColorCollection.zipApply(Items.DYE, OldustryItems.PAINTERS.map(item -> item.get()), (dye, painter) -> shapeless(RecipeCategory.MISC, painter)
            .requires(OldustryItems.PAINTER.get())
            .requires(dye)
            .group("painter")
            .unlockedBy("has_painter", has(OldustryItems.PAINTER.get()))
            .save(output));
        OldustryItems.PAINTERS.forEach(painter -> shapeless(RecipeCategory.MISC, OldustryItems.PAINTER.get())
            .requires(painter.get())
            .group("painter")
            .unlockedBy("has_" + painter.getId().getPath(), has(painter.get()))
            .save(output, OldustryRecipeProvider.id("painter_from_" + painter.getId().getPath())));

        shaped(RecipeCategory.COMBAT, OldustryItems.NANO_HELMET.get())
            .define('C', OldustryItems.CARBON_PLATE.get())
            .define('c', OldustryItems.ENERGY_CRYSTAL.get())
            .define('G', Items.GLASS)
            .pattern("CcC")
            .pattern("CGC")
            .unlockedBy("has_carbon_plate", has(OldustryItems.CARBON_PLATE.get()))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.NANO_CHESTPLATE.get())
            .define('C', OldustryItems.CARBON_PLATE.get())
            .define('c', OldustryItems.ENERGY_CRYSTAL.get())
            .pattern("C C")
            .pattern("CcC")
            .pattern("CCC")
            .unlockedBy("has_carbon_plate", has(OldustryItems.CARBON_PLATE.get()))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.NANO_LEGGINGS.get())
            .define('C', OldustryItems.CARBON_PLATE.get())
            .define('c', OldustryItems.ENERGY_CRYSTAL.get())
            .pattern("CcC")
            .pattern("C C")
            .pattern("C C")
            .unlockedBy("has_carbon_plate", has(OldustryItems.CARBON_PLATE.get()))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.NANO_BOOTS.get())
            .define('C', OldustryItems.CARBON_PLATE.get())
            .define('c', OldustryItems.ENERGY_CRYSTAL.get())
            .pattern("C C")
            .pattern("CcC")
            .unlockedBy("has_carbon_plate", has(OldustryItems.CARBON_PLATE.get()))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.NANO_SABER.get())
            .define('G', Items.GLOWSTONE_DUST)
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .define('C', OldustryItems.CARBON_PLATE.get())
            .define('c', OldustryItems.ENERGY_CRYSTAL.get())
            .pattern("GA ")
            .pattern("GA ")
            .pattern("CcC")
            .unlockedBy("has_carbon_plate", has(OldustryItems.CARBON_PLATE.get()))
            .save(output);

        // Recipies 310–313. O = orange dye (meta 14). G and # are blocks.
        // AdvRecipe turns a Block into meta 32767, so W is any wool, not only white.
        shaped(RecipeCategory.COMBAT, OldustryItems.HAZMAT_HELMET.get())
            .define('R', OldustryTags.Items.RUBBER)
            .define('O', Items.DYE.pick(DyeColor.ORANGE))
            .define('G', Items.GLASS)
            .define('#', Items.IRON_BARS)
            .pattern(" O ")
            .pattern("RGR")
            .pattern("R#R")
            .unlockedBy("has_rubber", has(OldustryTags.Items.RUBBER))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.HAZMAT_CHESTPLATE.get())
            .define('R', OldustryTags.Items.RUBBER)
            .define('O', Items.DYE.pick(DyeColor.ORANGE))
            .pattern("R R")
            .pattern("ROR")
            .pattern("ROR")
            .unlockedBy("has_rubber", has(OldustryTags.Items.RUBBER))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.HAZMAT_LEGGINGS.get())
            .define('R', OldustryTags.Items.RUBBER)
            .define('O', Items.DYE.pick(DyeColor.ORANGE))
            .pattern("ROR")
            .pattern("R R")
            .pattern("R R")
            .unlockedBy("has_rubber", has(OldustryTags.Items.RUBBER))
            .save(output);
        shaped(RecipeCategory.COMBAT, OldustryItems.HAZMAT_BOOTS.get())
            .define('R', OldustryTags.Items.RUBBER)
            .define('W', ItemTags.WOOL)
            .pattern("R R")
            .pattern("R R")
            .pattern("RWR")
            .unlockedBy("has_wool", has(ItemTags.WOOL))
            .save(output);

        shaped(RecipeCategory.TOOLS, OldustryItems.TOOLBOX.get())
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('C', Tags.Items.CHESTS_WOODEN)
            .pattern("ICI")
            .pattern("III")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);
    }

    private void tool(String a, String b, String c, ItemLike result) {
        var builder = shaped(RecipeCategory.TOOLS, result)
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .define('S', Items.STICK)
            .pattern(a);
        if (!b.isEmpty()) {
            builder.pattern(b);
        }
        if (!c.isEmpty()) {
            builder.pattern(c);
        }
        builder.unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE)).save(output);
    }
}

