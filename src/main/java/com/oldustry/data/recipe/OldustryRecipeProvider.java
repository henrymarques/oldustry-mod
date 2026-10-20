package com.oldustry.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

public class OldustryRecipeProvider extends RecipeProvider {
    public OldustryRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        new MaterialRecipes(registries, output).buildRecipes();
        new EquipmentRecipes(registries, output).buildRecipes();
        new MiscRecipes(registries, output).buildRecipes();
        new EnergyRecipes(registries, output).buildRecipes();
    }

    static String id(String path) {
        return Oldustry.MOD_ID + ":" + path;
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new OldustryRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Oldustry recipes";
        }
    }
}
