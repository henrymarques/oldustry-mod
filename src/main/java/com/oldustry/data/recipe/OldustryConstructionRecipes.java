package com.oldustry.data.recipe;

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

final class OldustryConstructionRecipes extends RecipeProvider {
    OldustryConstructionRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shapeless(RecipeCategory.MISC, OldustryItems.CONSTRUCTION_FOAM.get(), 3)
            .requires(OldustryTags.Items.DUSTS_CLAY)
            .requires(Items.WATER_BUCKET)
            .requires(Items.REDSTONE)
            .requires(OldustryTags.Items.DUSTS_COAL)
            .unlockedBy("has_clay_dust", has(OldustryTags.Items.DUSTS_CLAY))
            .save(output);

        shapeless(RecipeCategory.MISC, OldustryItems.CONSTRUCTION_FOAM.get(), 3)
            .requires(OldustryTags.Items.DUSTS_CLAY)
            .requires(OldustryItems.WATER_CELL.get())
            .requires(Items.REDSTONE)
            .requires(OldustryTags.Items.DUSTS_COAL)
            .unlockedBy("has_clay_dust", has(OldustryTags.Items.DUSTS_CLAY))
            .save(output, OldustryRecipeProvider.id("construction_foam_from_cell"));

        // JAR: cobble/cobble/empty, cobble/stick/empty, empty/empty/cobble. Result is an empty tank.
        shaped(RecipeCategory.TOOLS, new ItemStackTemplate(
                OldustryItems.CF_SPRAYER.get(),
                DataComponentPatch.builder().set(OldustryDataComponents.FOAM.get(), SprayerItem.CRAFTED).build()))
            .define('S', Items.COBBLESTONE)
            .define('s', Items.STICK)
            .pattern("SS ")
            .pattern("Ss ")
            .pattern("  S")
            .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
            .save(output);

        shaped(RecipeCategory.REDSTONE, OldustryItems.REINFORCED_DOOR.get())
            .define('S', OldustryItems.REINFORCED_STONE.get())
            .pattern("SS")
            .pattern("SS")
            .pattern("SS")
            .unlockedBy("has_reinforced_stone", has(OldustryItems.REINFORCED_STONE.get()))
            .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, OldustryItems.REINFORCED_STONE.get(), 8)
            .define('S', Items.STONE)
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .pattern("SSS")
            .pattern("SAS")
            .pattern("SSS")
            .unlockedBy("has_alloy", has(OldustryItems.ADVANCED_ALLOY.get()))
            .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, OldustryItems.REINFORCED_GLASS.get(), 7)
            .define('G', Items.GLASS)
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .pattern("GAG")
            .pattern("GGG")
            .pattern("GAG")
            .unlockedBy("has_alloy", has(OldustryItems.ADVANCED_ALLOY.get()))
            .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, OldustryItems.REINFORCED_GLASS.get(), 7)
            .define('G', Items.GLASS)
            .define('A', OldustryItems.ADVANCED_ALLOY.get())
            .pattern("GGG")
            .pattern("AGA")
            .pattern("GGG")
            .unlockedBy("has_alloy", has(OldustryItems.ADVANCED_ALLOY.get()))
            .save(output, OldustryRecipeProvider.id("reinforced_glass_rows"));

        shaped(RecipeCategory.DECORATIONS, OldustryItems.WOODEN_SCAFFOLD.get(), 16)
            .define('P', ItemTags.PLANKS)
            .define('s', Items.STICK)
            .pattern("PPP")
            .pattern(" s ")
            .pattern("s s")
            .unlockedBy("has_planks", has(ItemTags.PLANKS))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.IRON_SCAFFOLD.get(), 16)
            .define('P', OldustryTags.Items.INGOTS_REFINED_IRON)
            .define('s', OldustryItems.IRON_FENCE.get())
            .pattern("PPP")
            .pattern(" s ")
            .pattern("s s")
            .unlockedBy("has_iron_fence", has(OldustryItems.IRON_FENCE.get()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, OldustryItems.IRON_FENCE.get(), 12)
            .define('I', OldustryTags.Items.INGOTS_REFINED_IRON)
            .pattern("III")
            .pattern("III")
            .unlockedBy("has_refined_iron", has(OldustryTags.Items.INGOTS_REFINED_IRON))
            .save(output);
    }
}
