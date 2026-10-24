package com.oldustry.data.recipe;

import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

final class OldustryMaterialRecipes extends RecipeProvider {
    OldustryMaterialRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        nineBlockStorageRecipes(RecipeCategory.MISC, OldustryItems.TIN_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, OldustryItems.TIN_BLOCK.get());
        nineBlockStorageRecipes(RecipeCategory.MISC, OldustryItems.BRONZE_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, OldustryItems.BRONZE_BLOCK.get());
        nineBlockStorageRecipes(RecipeCategory.MISC, OldustryItems.URANIUM_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, OldustryItems.URANIUM_BLOCK.get());

        shapeless(RecipeCategory.MISC, OldustryItems.BRONZE_DUST.get(), 2)
            .requires(OldustryTags.Items.DUSTS_COPPER)
            .requires(OldustryTags.Items.DUSTS_COPPER)
            .requires(OldustryTags.Items.DUSTS_COPPER)
            .requires(OldustryTags.Items.DUSTS_TIN)
            .unlockedBy("has_copper_dust", has(OldustryTags.Items.DUSTS_COPPER))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.IRON_DUST.get())
            .requires(OldustryItems.SMALL_IRON_DUST.get())
            .requires(OldustryItems.SMALL_IRON_DUST.get())
            .unlockedBy("has_small_iron_dust", has(OldustryItems.SMALL_IRON_DUST.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.SMALL_IRON_DUST.get())
            .define('C', OldustryTags.Items.DUSTS_COPPER)
            .define('T', OldustryTags.Items.DUSTS_TIN)
            .pattern("CTC")
            .pattern("TCT")
            .pattern("CTC")
            .unlockedBy("has_copper_dust", has(OldustryTags.Items.DUSTS_COPPER))
            .save(output, OldustryRecipeProvider.id("small_iron_dust_from_copper_tin"));
        shaped(RecipeCategory.MISC, OldustryItems.SMALL_IRON_DUST.get())
            .define('C', OldustryTags.Items.DUSTS_COPPER)
            .define('T', OldustryTags.Items.DUSTS_TIN)
            .pattern("TCT")
            .pattern("CTC")
            .pattern("TCT")
            .unlockedBy("has_copper_dust", has(OldustryTags.Items.DUSTS_COPPER))
            .save(output, OldustryRecipeProvider.id("small_iron_dust_from_tin_copper"));

        shaped(RecipeCategory.MISC, OldustryItems.CARBON_FIBER.get())
            .define('C', OldustryTags.Items.DUSTS_COAL)
            .pattern("CC")
            .pattern("CC")
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.CARBON_MESH.get())
            .requires(OldustryItems.CARBON_FIBER.get())
            .requires(OldustryItems.CARBON_FIBER.get())
            .unlockedBy("has_carbon_fiber", has(OldustryItems.CARBON_FIBER.get()))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.COAL_BALL.get())
            .define('C', OldustryTags.Items.DUSTS_COAL)
            .define('F', Items.FLINT)
            .pattern("CCC")
            .pattern("CFC")
            .pattern("CCC")
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.COAL_CHUNK.get())
            .define('#', OldustryItems.COMPRESSED_COAL_BALL.get())
            .define('O', Items.OBSIDIAN)
            .pattern("###")
            .pattern("#O#")
            .pattern("###")
            .unlockedBy("has_compressed_coal_ball", has(OldustryItems.COMPRESSED_COAL_BALL.get()))
            .save(output);

        plantball(Tags.Items.SEEDS, "seeds");
        plantball(Items.SUGAR_CANE, "reeds");
        plantball(Items.WHEAT, "wheat");
        plantball(Items.CACTUS, "cactus");
        plantball(ItemTags.LEAVES, "leaves");
        plantball(ItemTags.SAPLINGS, "saplings", 2);

        shaped(RecipeCategory.MISC, OldustryItems.MIXED_METAL_INGOT.get(), 2)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('B', OldustryTags.Items.INGOTS_BRONZE)
            .define('T', OldustryTags.Items.INGOTS_TIN)
            .pattern("III")
            .pattern("BBB")
            .pattern("TTT")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.HYDRATED_COAL_DUST.get(), 8)
            .define('C', OldustryTags.Items.DUSTS_COAL)
            .define('W', Items.WATER_BUCKET)
            .pattern("CCC")
            .pattern("CWC")
            .pattern("CCC")
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output);
        shaped(RecipeCategory.MISC, OldustryItems.HYDRATED_COAL_DUST.get(), 8)
            .define('C', OldustryTags.Items.DUSTS_COAL)
            .define('W', OldustryItems.WATER_CELL.get())
            .pattern("CCC")
            .pattern("CWC")
            .pattern("CCC")
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output, OldustryRecipeProvider.id("hydrated_coal_dust_from_cell"));
        shapeless(RecipeCategory.MISC, OldustryItems.HYDRATED_COAL_DUST.get())
            .requires(OldustryTags.Items.DUSTS_COAL)
            .requires(Items.WATER_BUCKET)
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output, OldustryRecipeProvider.id("hydrated_coal_dust_single"));
        shapeless(RecipeCategory.MISC, OldustryItems.HYDRATED_COAL_DUST.get())
            .requires(OldustryTags.Items.DUSTS_COAL)
            .requires(OldustryItems.WATER_CELL.get())
            .unlockedBy("has_coal_dust", has(OldustryTags.Items.DUSTS_COAL))
            .save(output, OldustryRecipeProvider.id("hydrated_coal_dust_single_from_cell"));

        shaped(RecipeCategory.MISC, OldustryItems.INDUSTRIAL_CREDIT.get(), 16)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("II")
            .pattern("II")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);

        shaped(RecipeCategory.MISC, OldustryItems.REFINED_IRON_INGOT.get())
            .define('I', OldustryItems.INDUSTRIAL_CREDIT.get())
            .pattern("III")
            .pattern("III")
            .pattern("III")
            .unlockedBy("has_credit", has(OldustryItems.INDUSTRIAL_CREDIT.get()))
            .save(output, OldustryRecipeProvider.id("refined_iron_ingot_from_credits"));

        shaped(RecipeCategory.MISC, OldustryItems.SCRAP_BOX.get())
            .define('S', OldustryItems.SCRAP.get())
            .pattern("SSS")
            .pattern("SSS")
            .pattern("SSS")
            .unlockedBy("has_scrap", has(OldustryItems.SCRAP.get()))
            .save(output);

        blastAndSmeltTag(OldustryTags.Items.ORES_TIN, OldustryItems.TIN_INGOT.get(), 0.5f, "tin_ore");
        blastAndSmeltTag(OldustryTags.Items.RAW_MATERIALS_TIN, OldustryItems.TIN_INGOT.get(), 0.5f, "raw_tin");
        blastAndSmeltTag(OldustryTags.Items.DUSTS_TIN, OldustryItems.TIN_INGOT.get(), 0.0f, "tin_dust");
        blastAndSmeltTag(OldustryTags.Items.DUSTS_BRONZE, OldustryItems.BRONZE_INGOT.get(), 0.0f, "bronze_dust");
        blastAndSmeltTag(OldustryTags.Items.DUSTS_COPPER, Items.COPPER_INGOT, 0.0f, "copper_dust");
        blastAndSmeltTag(OldustryTags.Items.DUSTS_IRON, Items.IRON_INGOT, 0.0f, "iron_dust");
        blastAndSmeltTag(OldustryTags.Items.DUSTS_GOLD, Items.GOLD_INGOT, 0.0f, "gold_dust");
        smelt(OldustryItems.HYDRATED_COAL_DUST.get(), OldustryItems.COAL_DUST.get(), 0.0f, "hydrated_coal_dust");
        // 1 ancient scrap dust → netherite scrap (macerator yields 2 from debris).
        smelt(OldustryItems.ANCIENT_SCRAP_DUST.get(), Items.NETHERITE_SCRAP, 0.7f, "ancient_scrap_dust");
        blastTag(Tags.Items.INGOTS_IRON, OldustryItems.REFINED_IRON_INGOT.get(), 0.2f, "iron_ingot");
        smeltTag(Tags.Items.INGOTS_IRON, OldustryItems.REFINED_IRON_INGOT.get(), 0.2f, "iron_ingot");
        smelt(OldustryItems.STICKY_RESIN.get(), OldustryItems.RUBBER.get(), 0.3f, "sticky_resin");
        smelt(OldustryItems.RUBBER_LOG.get(), Items.JUNGLE_LOG, 0.1f, "rubber_log");
        smelt(OldustryItems.COFFEE_MUG_COLD.get(), OldustryItems.COFFEE_MUG_DARK.get(), 0.1f, "coffee_mug_cold");
    }

    private void plantball(TagKey<Item> tag, String name) {
        plantball(tag, name, 1);
    }

    private void plantball(TagKey<Item> tag, String name, int count) {
        shaped(RecipeCategory.MISC, OldustryItems.PLANTBALL.get(), count)
            .define('P', tag)
            .pattern("PPP")
            .pattern("P P")
            .pattern("PPP")
            .group("plantball")
            .unlockedBy("has_" + name, has(tag))
            .save(output, OldustryRecipeProvider.id("plantball_from_" + name));
    }

    private void plantball(ItemLike ingredient, String name) {
        shaped(RecipeCategory.MISC, OldustryItems.PLANTBALL.get())
            .define('P', ingredient)
            .pattern("PPP")
            .pattern("P P")
            .pattern("PPP")
            .group("plantball")
            .unlockedBy("has_" + name, has(ingredient))
            .save(output, OldustryRecipeProvider.id("plantball_from_" + name));
    }

    private void blastTag(TagKey<Item> tag, ItemLike outputItem, float xp, String name) {
        SimpleCookingRecipeBuilder.blasting(this.tag(tag), RecipeCategory.MISC, CookingBookCategory.MISC, outputItem, xp, 100)
            .unlockedBy("has_" + name, has(tag))
            .save(output, OldustryRecipeProvider.id(name + "_blasting"));
    }

    private void smelt(ItemLike input, ItemLike outputItem, float xp, String name) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, CookingBookCategory.MISC, outputItem, xp, 200)
            .unlockedBy("has_" + name, has(input))
            .save(output, OldustryRecipeProvider.id(name + "_smelting"));
    }

    private void smeltTag(TagKey<Item> tag, ItemLike outputItem, float xp, String name) {
        SimpleCookingRecipeBuilder.smelting(this.tag(tag), RecipeCategory.MISC, CookingBookCategory.MISC, outputItem, xp, 200)
            .unlockedBy("has_" + name, has(tag))
            .save(output, OldustryRecipeProvider.id(name + "_smelting"));
    }

    private void blastAndSmeltTag(TagKey<Item> tag, ItemLike outputItem, float xp, String name) {
        blastTag(tag, outputItem, xp, name);
        smeltTag(tag, outputItem, xp, name);
    }
}

