package com.oldustry.data.recipe;

import com.oldustry.registry.ModItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ColorCollection;
import net.neoforged.neoforge.common.Tags;

final class EquipmentRecipes extends RecipeProvider {
    EquipmentRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        tool("BBB", " S ", " S ", ModItems.BRONZE_PICKAXE.get());
        tool("BB", "SB", "S ", ModItems.BRONZE_AXE.get());
        tool("BB", "S ", "S ", ModItems.BRONZE_HOE.get());
        shaped(RecipeCategory.COMBAT, ModItems.BRONZE_SWORD.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .define('S', Items.STICK)
            .pattern("B")
            .pattern("B")
            .pattern("S")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);
        tool(" B ", " S ", " S ", ModItems.BRONZE_SHOVEL.get());

        shaped(RecipeCategory.COMBAT, ModItems.BRONZE_HELMET.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("BBB")
            .pattern("B B")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.BRONZE_CHESTPLATE.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("B B")
            .pattern("BBB")
            .pattern("BBB")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.BRONZE_LEGGINGS.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("BBB")
            .pattern("B B")
            .pattern("B B")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.BRONZE_BOOTS.get())
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .pattern("B B")
            .pattern("B B")
            .unlockedBy("has_bronze", has(OldustryTags.Items.INGOTS_BRONZE))
            .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.COMPOSITE_VEST.get())
            .define('A', ModItems.ADVANCED_ALLOY.get())
            .define('L', Items.LEATHER_CHESTPLATE)
            .define('I', Items.IRON_CHESTPLATE)
            .pattern("A A")
            .pattern("ALA")
            .pattern("AIA")
            .unlockedBy("has_alloy", has(ModItems.ADVANCED_ALLOY.get()))
            .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.PAINTER.get())
            .define('C', ItemTags.WOOL)
            .define('I', Tags.Items.INGOTS_IRON)
            .pattern(" CC")
            .pattern(" IC")
            .pattern("I  ")
            .unlockedBy("has_wool", has(ItemTags.WOOL))
            .save(output);

        ColorCollection.zipApply(Items.DYE, ModItems.PAINTERS.map(item -> item.get()), (dye, painter) -> shapeless(RecipeCategory.MISC, painter)
            .requires(ModItems.PAINTER.get())
            .requires(dye)
            .group("painter")
            .unlockedBy("has_painter", has(ModItems.PAINTER.get()))
            .save(output));

        shaped(RecipeCategory.TOOLS, ModItems.TOOLBOX.get())
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
