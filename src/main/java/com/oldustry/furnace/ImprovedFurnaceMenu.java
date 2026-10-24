package com.oldustry.furnace;

import com.oldustry.registry.OldustryMenus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipePropertySet;

public class ImprovedFurnaceMenu extends AbstractFurnaceMenu {
    public ImprovedFurnaceMenu(int id, Inventory inventory) {
        super(OldustryMenus.IMPROVED_FURNACE.get(), RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, id, inventory);
    }

    public ImprovedFurnaceMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(OldustryMenus.IMPROVED_FURNACE.get(), RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, id, inventory, container, data);
    }
}
