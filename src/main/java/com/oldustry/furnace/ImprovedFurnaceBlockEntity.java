package com.oldustry.furnace;

import com.oldustry.registry.OldustryBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ImprovedFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public static final int COOK_TIME = 160;

    public ImprovedFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryBlockEntities.IMPROVED_FURNACE.get(), pos, state, RecipeType.SMELTING);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, ImprovedFurnaceBlockEntity furnace) {
        AbstractFurnaceBlockEntity.serverTick(level, pos, state, furnace);
        scaleCookTime(furnace);
    }

    @Override
    public void setItem(int slot, ItemStack stack, boolean insideTransaction) {
        super.setItem(slot, stack, insideTransaction);
        scaleCookTime(this);
    }

    private static void scaleCookTime(ImprovedFurnaceBlockEntity furnace) {
        // Vanilla smelting recipes are 200 ticks; IC2 iron furnace is 160.
        if (furnace.dataAccess.get(DATA_COOKING_TOTAL_TIME) == 200) {
            furnace.dataAccess.set(DATA_COOKING_TOTAL_TIME, COOK_TIME);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.oldustry.improved_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ImprovedFurnaceMenu(id, inventory, this, dataAccess);
    }
}
