package com.oldustry.compat.jei;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.oldustry.Oldustry;
import com.oldustry.machine.MachineKind;
import com.oldustry.machine.MachineRecipes;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModItems;

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
    public static final @NonNull IRecipeType<MachineRecipes.@NonNull Listed> MACERATOR =
        IRecipeType.create(Oldustry.MOD_ID, "macerator", MachineRecipes.Listed.class);
    public static final @NonNull IRecipeType<MachineRecipes.@NonNull Listed> EXTRACTOR =
        IRecipeType.create(Oldustry.MOD_ID, "extractor", MachineRecipes.Listed.class);
    public static final @NonNull IRecipeType<MachineRecipes.@NonNull Listed> COMPRESSOR =
        IRecipeType.create(Oldustry.MOD_ID, "compressor", MachineRecipes.Listed.class);
    public static final @NonNull IRecipeType<MachineRecipes.@NonNull Listed> RECYCLER =
        IRecipeType.create(Oldustry.MOD_ID, "recycler", MachineRecipes.Listed.class);

    @Override
    public @NonNull Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "jei");
    }

    @Override
    public void registerCategories(@NonNull IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new ProcessRecipeCategory(gui, MACERATOR, ModBlocks.MACERATOR.get(), "gui.oldustry.jei.macerator"),
            new ProcessRecipeCategory(gui, EXTRACTOR, ModBlocks.EXTRACTOR.get(), "gui.oldustry.jei.extractor"),
            new ProcessRecipeCategory(gui, COMPRESSOR, ModBlocks.COMPRESSOR.get(), "gui.oldustry.jei.compressor"),
            new ProcessRecipeCategory(gui, RECYCLER, ModBlocks.RECYCLER.get(), "gui.oldustry.jei.recycler")
        );
    }

    @Override
    public void registerRecipes(@NonNull IRecipeRegistration registration) {
        registration.addRecipes(MACERATOR, MachineRecipes.list(MachineKind.Type.MACERATOR));
        registration.addRecipes(EXTRACTOR, MachineRecipes.list(MachineKind.Type.EXTRACTOR));
        registration.addRecipes(COMPRESSOR, MachineRecipes.list(MachineKind.Type.COMPRESSOR));
        @SuppressWarnings("null")
        @NonNull List<MachineRecipes.@NonNull Listed> scrap = List.of(
            new MachineRecipes.Listed(new ItemStack(Items.DIRT), new ItemStack(ModItems.SCRAP.get()))
        );
        registration.addRecipes(RECYCLER, scrap);
        registration.addIngredientInfo(
            ModBlocks.RECYCLER.get(),
            Component.translatable("gui.oldustry.jei.recycler.info")
        );
    }

    @Override
    public void registerRecipeCatalysts(@NonNull IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(MACERATOR, ModBlocks.MACERATOR.get());
        registration.addCraftingStation(EXTRACTOR, ModBlocks.EXTRACTOR.get());
        registration.addCraftingStation(COMPRESSOR, ModBlocks.COMPRESSOR.get());
        registration.addCraftingStation(RECYCLER, ModBlocks.RECYCLER.get());
        registration.addCraftingStation(RecipeTypes.SMELTING, ModBlocks.ELECTRIC_FURNACE.get());
    }
}
