package com.oldustry.menu;

import java.util.ArrayList;
import java.util.List;

import com.oldustry.crop.CropSeed;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.tool.CropnalyzerItem;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryMenus;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** JAR {@code ContainerCropnalyzer}: seed in, seed out, battery. Hotbar only. */
public class CropnalyzerMenu extends AbstractContainerMenu {
    private final InteractionHand hand;
    private final SimpleContainer container = new SimpleContainer(CropnalyzerItem.SLOTS);
    private final Player player;

    public static CropnalyzerMenu fromClient(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        InteractionHand hand = data != null && data.readBoolean() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        return new CropnalyzerMenu(id, inventory, hand);
    }

    public CropnalyzerMenu(int id, Inventory inventory, InteractionHand hand) {
        super(OldustryMenus.CROPANALYZER.get(), id);
        this.hand = hand;
        this.player = inventory.player;
        loadFromItem();
        addSlot(new Slot(container, 0, 8, 7) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(OldustryItems.CROP_SEED.get());
            }
        });
        addSlot(new Slot(container, 1, 41, 7) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new Slot(container, 2, 152, 7) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ElectricItem;
            }
        });
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void slotsChanged(net.minecraft.world.Container changed) {
        super.slotsChanged(changed);
        if (changed == container) {
            saveToItem();
        }
    }

    public ItemStack analyzer() {
        return player.getItemInHand(hand);
    }

    public int scannedLevel() {
        ItemStack out = container.getItem(1);
        CropSeed seed = out.get(OldustryDataComponents.CROP_SEED.get());
        return seed == null ? -1 : seed.scan();
    }

    public ItemStack outputSeed() {
        return container.getItem(1);
    }

    private void loadFromItem() {
        List<ItemStack> stored = CropnalyzerItem.contents(analyzer());
        for (int i = 0; i < CropnalyzerItem.SLOTS; i++) {
            container.setItem(i, i < stored.size() ? stored.get(i).copy() : ItemStack.EMPTY);
        }
    }

    private void saveToItem() {
        ItemStack tool = analyzer();
        if (!(tool.getItem() instanceof CropnalyzerItem)) {
            return;
        }
        List<ItemStack> next = new ArrayList<>(CropnalyzerItem.SLOTS);
        for (int i = 0; i < CropnalyzerItem.SLOTS; i++) {
            next.add(container.getItem(i).copy());
        }
        CropnalyzerItem.setContents(tool, next);
    }

    @Override
    public void broadcastChanges() {
        tryScan();
        saveToItem();
        super.broadcastChanges();
    }

    /** JAR {@code HandHeldCropnalyzer.tickCallback}. */
    private void tryScan() {
        if (container.getItem(1).isEmpty() && container.getItem(0).is(OldustryItems.CROP_SEED.get())) {
            ItemStack seedStack = container.getItem(0);
            CropSeed seed = seedStack.get(OldustryDataComponents.CROP_SEED.get());
            if (seed == null) {
                return;
            }
            if (seed.scan() >= 4) {
                container.setItem(1, seedStack.copy());
                container.setItem(0, ItemStack.EMPTY);
                return;
            }
            ItemStack battery = container.getItem(2);
            if (!(battery.getItem() instanceof ElectricItem electric)) {
                return;
            }
            int need = CropnalyzerItem.energyForLevel(seed.scan());
            int got = electric.dischargeIgnoreTransfer(battery, need, 2);
            if (got < need) {
                return;
            }
            CropSeed next = seed.withScan(seed.scan() + 1);
            ItemStack out = seedStack.copy();
            out.set(OldustryDataComponents.CROP_SEED.get(), next);
            container.setItem(1, out);
            container.setItem(0, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack stack = player.getItemInHand(hand);
        return stack.getItem() instanceof CropnalyzerItem;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index < CropnalyzerItem.SLOTS) {
                if (!moveItemStackTo(stack, CropnalyzerItem.SLOTS, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, CropnalyzerItem.SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public void removed(Player player) {
        saveToItem();
        super.removed(player);
    }
}
