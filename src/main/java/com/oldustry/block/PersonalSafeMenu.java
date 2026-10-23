package com.oldustry.block;

import com.oldustry.registry.ModMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** 6×9 at y 18, player inventory at y 140. Same layout as {@code ContainerPersonalChest} (height 222). */
public class PersonalSafeMenu extends AbstractContainerMenu {
    private static final int ROWS = 6;
    private final Container contents;

    public static PersonalSafeMenu fromClient(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Container contents = inventory.player.level().getBlockEntity(pos) instanceof PersonalSafeBlockEntity safe
            ? safe.contents()
            : new SimpleContainer(PersonalSafeBlockEntity.SLOTS);
        return new PersonalSafeMenu(id, inventory, contents);
    }

    public PersonalSafeMenu(int id, Inventory inventory, Container contents) {
        super(ModMenus.PERSONAL_SAFE.get(), id);
        this.contents = contents;
        checkContainerSize(contents, PersonalSafeBlockEntity.SLOTS);
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(contents, column + row * 9, 8 + column * 18, 18 + row * 18));
            }
        }
        addStandardInventorySlots(inventory, 8, 140);
        contents.startOpen(inventory.player);
    }

    @Override
    public boolean stillValid(Player player) {
        return contents.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < PersonalSafeBlockEntity.SLOTS) {
                if (!moveItemStackTo(stack, PersonalSafeBlockEntity.SLOTS, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, PersonalSafeBlockEntity.SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return clicked;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        contents.stopOpen(player);
    }
}
