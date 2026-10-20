package com.oldustry.compat.jei;

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

final class ProcessRecipeCategory implements IRecipeCategory<MachineRecipes.Listed> {
    private static final int WIDTH = 82;
    private static final int HEIGHT = 34;

    private final IRecipeType<MachineRecipes.Listed> type;
    private final Component title;
    private final IDrawable icon;

    ProcessRecipeCategory(IGuiHelper gui, IRecipeType<MachineRecipes.Listed> type, ItemLike catalyst, String titleKey) {
        this.type = type;
        this.title = Component.translatable(titleKey);
        this.icon = gui.createDrawableItemStack(new ItemStack(catalyst));
    }

    @Override
    public IRecipeType<MachineRecipes.Listed> getRecipeType() {
        return type;
    }

    @Override
    public Component getTitle() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipes.Listed recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 9).setStandardSlotBackground().add(recipe.input());
        builder.addOutputSlot(61, 9).setOutputSlotBackground().add(recipe.output());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MachineRecipes.Listed recipe, IFocusGroup focuses) {
        builder.addAnimatedRecipeArrowWidget(40).setPosition(26, 9);
    }
}
