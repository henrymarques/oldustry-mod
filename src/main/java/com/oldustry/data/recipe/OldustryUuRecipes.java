package com.oldustry.data.recipe;

import com.oldustry.registry.OldustryItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

/** Recipies.java 141–183. Hidden from NEI in the JAR; they stay normal crafting recipes so JEI lists them. */
final class OldustryUuRecipes extends RecipeProvider {
    OldustryUuRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        uu("uu_stone", Items.STONE, 16, "   ", " M ", "   ");
        uu("uu_glass", Items.GLASS, 32, " M ", "M M", " M ");
        uu("uu_grass", Items.GRASS_BLOCK, 16, "   ", "M  ", "M  ");
        uu("uu_mossy_cobblestone", Items.MOSSY_COBBLESTONE, 16, "   ", " M ", "M M");
        uu("uu_sandstone", Items.SANDSTONE, 16, "   ", "  M", " M ");
        uu("uu_snow", Items.SNOW_BLOCK, 4, "M M", "   ", "   ");
        // Water and lava have no block item in 26.2. A bucket is the source the player can hold.
        uu("uu_water", Items.WATER_BUCKET, 1, "   ", " M ", " M ");
        uu("uu_lava", Items.LAVA_BUCKET, 1, " M ", " M ", " M ");
        uu("uu_iron_ore", Items.IRON_ORE, 2, "M M", " M ", "M M");
        uu("uu_gold_ore", Items.GOLD_ORE, 2, " M ", "MMM", " M ");
        uu("uu_obsidian", Items.OBSIDIAN, 12, "M M", "M M", "   ");
        uu("uu_netherrack", Items.NETHERRACK, 16, "  M", " M ", "M  ");
        uu("uu_glowstone", Items.GLOWSTONE, 8, " M ", "M M", "MMM");
        uu("uu_oak_log", Items.OAK_LOG, 8, " M ", "   ", "   ");
        uu("uu_cactus", Items.CACTUS, 48, " M ", "MMM", "M M");
        uu("uu_vine", Items.VINE, 24, "M  ", "M  ", "M  ");
        uu("uu_wool", Items.WOOL.pick(DyeColor.WHITE), 12, "M M", "   ", " M ");
        uu("uu_coal", Items.COAL, 20, "  M", "M  ", "  M");
        uu("uu_diamond", Items.DIAMOND, 1, "MMM", "MMM", "MMM");
        uu("uu_redstone", Items.REDSTONE, 24, "   ", " M ", "MMM");
        uu("uu_lapis", Items.LAPIS_LAZULI, 9, " M ", " M ", " MM");
        uu("uu_feather", Items.FEATHER, 32, " M ", " M ", "M M");
        uu("uu_snowball", Items.SNOWBALL, 16, "   ", "   ", "MMM");
        uu("uu_gunpowder", Items.GUNPOWDER, 15, "MMM", "M  ", "MMM");
        uu("uu_clay", Items.CLAY_BALL, 48, "MM ", "M  ", "MM ");
        uu("uu_cocoa", Items.COCOA_BEANS, 32, "MM ", "  M", "MM ");
        uu("uu_ink", Items.INK_SAC, 48, " MM", " MM", " M ");
        uu("uu_sugar_cane", Items.SUGAR_CANE, 48, "M M", "M M", "M M");
        uu("uu_flint", Items.FLINT, 32, " M ", "MM ", "MM ");
        uu("uu_seeds", Items.WHEAT_SEEDS, 32, "M  ", "MM ", "M  ");
        uu("uu_resin", OldustryItems.STICKY_RESIN.get(), 21, "M M", "   ", "M M");
        uu("uu_iridium", OldustryItems.IRIDIUM_ORE.get(), 1, "MMM", " M ", "MMM");
        uu("uu_mycelium", Items.MYCELIUM, 24, "   ", "M M", "MMM");
        uu("uu_jungle_planks", Items.JUNGLE_PLANKS, 48, "MM ", "MM ", "M  ");
        uu("uu_copper_ore", Items.COPPER_ORE, 5, "  M", "M M", "   ");
        uu("uu_tin_ore", OldustryItems.TIN_ORE.get(), 5, "   ", "M M", "  M");
    }

    private void uu(String id, ItemLike result, int count, String top, String mid, String bottom) {
        shaped(RecipeCategory.MISC, result, count)
            .define('M', OldustryItems.UU_MATTER.get())
            .pattern(top)
            .pattern(mid)
            .pattern(bottom)
            .unlockedBy("has_uu_matter", has(OldustryItems.UU_MATTER.get()))
            .save(output, OldustryRecipeProvider.id(id));
    }
}
