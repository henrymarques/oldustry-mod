package com.oldustry.block;

import com.oldustry.reactor.OldustryReactor;
import com.oldustry.registry.OldustryMenus;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Columns follow the chamber count at the moment the GUI opens. Player inventory sits at y 140. */
public final class OldustryReactorMenu extends AbstractContainerMenu {
    private final Container contents;
    private final ContainerData data;
    private final int columns;
    private final int gridSlots;

    public static OldustryReactorMenu fromClient(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        var pos = buf.readBlockPos();
        int columns = buf.readByte();
        Container contents = inventory.player.level().getBlockEntity(pos) instanceof OldustryReactorBlockEntity reactor
            ? reactor
            : new SimpleContainer(OldustryReactor.SLOTS);
        ContainerData data = contents instanceof OldustryReactorBlockEntity reactor
            ? reactor.data()
            : new SimpleContainerData(3);
        return new OldustryReactorMenu(id, inventory, contents, data, columns);
    }

    public OldustryReactorMenu(int id, Inventory inventory, Container contents, ContainerData data, int columns) {
        super(OldustryMenus.NUCLEAR_REACTOR.get(), id);
        this.contents = contents;
        this.data = data;
        this.columns = columns;
        checkContainerSize(contents, OldustryReactor.SLOTS);
        int left = OldustryReactor.slotLeft(columns);
        int shown = 0;
        for (int index = 0; index < OldustryReactor.SLOTS; index++) {
            int x = OldustryReactor.slotX(index);
            int y = index / OldustryReactor.WIDTH;
            if (x >= columns) {
                continue;
            }
            int slot = index;
            addSlot(new Slot(contents, slot, left + 18 * x, 18 + 18 * y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return OldustryReactor.useful(stack);
                }
            });
            shown++;
        }
        gridSlots = shown;
        addStandardInventorySlots(inventory, 8, 140);
        addDataSlots(data);
    }

    public int columns() {
        return columns;
    }

    public ContainerData data() {
        return data;
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
            if (slotIndex < gridSlots) {
                if (!moveItemStackTo(stack, gridSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, gridSlots, false)) {
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
}
