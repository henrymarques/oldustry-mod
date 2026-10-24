package com.oldustry.compat.jei;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.oldustry.Oldustry;
import com.oldustry.machine.OldustryMachineKind;
import com.oldustry.machine.OldustryProcessingRecipes;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryItems;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@JeiPlugin
public final class OldustryJeiPlugin implements IModPlugin {
    public static final @NonNull IRecipeType<OldustryProcessingRecipes.@NonNull Listed> MACERATOR =
        IRecipeType.create(Oldustry.MOD_ID, "macerator", OldustryProcessingRecipes.Listed.class);
    public static final @NonNull IRecipeType<OldustryProcessingRecipes.@NonNull Listed> EXTRACTOR =
        IRecipeType.create(Oldustry.MOD_ID, "extractor", OldustryProcessingRecipes.Listed.class);
    public static final @NonNull IRecipeType<OldustryProcessingRecipes.@NonNull Listed> COMPRESSOR =
        IRecipeType.create(Oldustry.MOD_ID, "compressor", OldustryProcessingRecipes.Listed.class);
    public static final @NonNull IRecipeType<OldustryProcessingRecipes.@NonNull Listed> RECYCLER =
        IRecipeType.create(Oldustry.MOD_ID, "recycler", OldustryProcessingRecipes.Listed.class);

    @Override
    public @NonNull Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "jei");
    }

    @Override
    public void registerCategories(@NonNull IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new ProcessRecipeCategory(gui, MACERATOR, OldustryBlocks.MACERATOR.get(), "gui.oldustry.jei.macerator"),
            new ProcessRecipeCategory(gui, EXTRACTOR, OldustryBlocks.EXTRACTOR.get(), "gui.oldustry.jei.extractor"),
            new ProcessRecipeCategory(gui, COMPRESSOR, OldustryBlocks.COMPRESSOR.get(), "gui.oldustry.jei.compressor"),
            new ProcessRecipeCategory(gui, RECYCLER, OldustryBlocks.RECYCLER.get(), "gui.oldustry.jei.recycler")
        );
    }

    @Override
    public void registerRecipes(@NonNull IRecipeRegistration registration) {
        registration.addRecipes(MACERATOR, OldustryProcessingRecipes.list(OldustryMachineKind.Type.MACERATOR));
        registration.addRecipes(EXTRACTOR, OldustryProcessingRecipes.list(OldustryMachineKind.Type.EXTRACTOR));
        registration.addRecipes(COMPRESSOR, OldustryProcessingRecipes.list(OldustryMachineKind.Type.COMPRESSOR));
        @SuppressWarnings("null")
        @NonNull List<OldustryProcessingRecipes.@NonNull Listed> scrap = List.of(
            new OldustryProcessingRecipes.Listed(new ItemStack(Items.DIRT), new ItemStack(OldustryItems.SCRAP.get()))
        );
        registration.addRecipes(RECYCLER, scrap);
        registration.addIngredientInfo(
            OldustryBlocks.RECYCLER.get(),
            Component.translatable("gui.oldustry.jei.recycler.info")
        );
    }

    @Override
    public void registerRecipeCatalysts(@NonNull IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(MACERATOR, OldustryBlocks.MACERATOR.get());
        registration.addCraftingStation(EXTRACTOR, OldustryBlocks.EXTRACTOR.get());
        registration.addCraftingStation(COMPRESSOR, OldustryBlocks.COMPRESSOR.get());
        registration.addCraftingStation(RECYCLER, OldustryBlocks.RECYCLER.get());
        registration.addCraftingStation(RecipeTypes.SMELTING, OldustryBlocks.ELECTRIC_FURNACE.get());
    }
}
