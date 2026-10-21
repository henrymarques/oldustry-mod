package com.oldustry.compat.jei;

import org.jspecify.annotations.NonNull;

import com.oldustry.machine.MachineRecipes;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

final class ProcessRecipeCategory implements IRecipeCategory<MachineRecipes.@NonNull Listed> {
    private static final int WIDTH = 82;
    private static final int HEIGHT = 34;

    private final @NonNull IRecipeType<MachineRecipes.@NonNull Listed> type;
    private final @NonNull Component title;
    private final IDrawable icon;

    ProcessRecipeCategory(IGuiHelper gui, @NonNull IRecipeType<MachineRecipes.@NonNull Listed> type, @NonNull ItemLike catalyst, @NonNull String titleKey) {
        this.type = type;
        this.title = Component.translatable(titleKey);
        this.icon = gui.createDrawableItemStack(new ItemStack(catalyst));
    }

    @Override
    public @NonNull IRecipeType<MachineRecipes.@NonNull Listed> getRecipeType() {
        return type;
    }

    @Override
    public @NonNull Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NonNull IRecipeLayoutBuilder builder, MachineRecipes.@NonNull Listed recipe, @NonNull IFocusGroup focuses) {
        builder.addInputSlot(1, 9).setStandardSlotBackground().add(recipe.input());
        builder.addOutputSlot(61, 9).setOutputSlotBackground().add(recipe.output());
    }

    @Override
    public void createRecipeExtras(@NonNull IRecipeExtrasBuilder builder, MachineRecipes.@NonNull Listed recipe, @NonNull IFocusGroup focuses) {
        builder.addAnimatedRecipeArrowWidget(40).setPosition(26, 9);
    }
}
