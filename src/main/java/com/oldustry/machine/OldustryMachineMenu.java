package com.oldustry.machine;

import com.oldustry.item.ElectricItem;
import com.oldustry.item.UpgradeItem;
import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryMenus;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class OldustryMachineMenu extends AbstractContainerMenu {
    public static final int DATA_ENERGY = 0;
    public static final int DATA_CAPACITY = 1;
    public static final int DATA_PROGRESS = 2;
    public static final int DATA_DURATION = 3;
    public static final int DATA_GAUGE = 4;
    public static final int DATA_GAUGE_MAX_OR_MINER_MODE = 5;
    public static final int DATA_EXTRA_OR_MINER_ACTIVE = 6;
    public static final int DATA_COUNT = 7;

    private static final EquipmentSlot[] ARMOR = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private final Container container;
    private final ContainerData data;
    private final OldustryMachineKind kind;
    private final int machineSlots;
    private final net.minecraft.world.level.Level level;

    public static OldustryMachineMenu fromClient(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        OldustryMachineKind kind = data != null ? data.readEnum(OldustryMachineKind.class) : OldustryMachineKind.GENERATOR;
        return new OldustryMachineMenu(id, inventory, new SimpleContainer(OldustryMachineBlockEntity.SIZE), new SimpleContainerData(DATA_COUNT), kind);
    }

    public OldustryMachineMenu(int id, Inventory inventory, Container container, ContainerData data, OldustryMachineKind kind) {
        super(OldustryMenus.MACHINE.get(), id);
        checkContainerSize(container, OldustryMachineBlockEntity.SIZE);
        this.container = container;
        this.data = data;
        this.kind = kind;
        this.level = inventory.player.level();
        for (int i = 0; i < OldustryMachineBlockEntity.SIZE; i++) {
            int[] xy = OldustryMachineLayout.slot(kind, i);
            boolean shown = xy[0] >= 0;
            int slot = i;
            addSlot(new Slot(container, i, shown ? xy[0] : 0, shown ? xy[1] : 0) {
                @Override
                public boolean isActive() {
                    return shown;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return shown && OldustryMachineSlots.mayPlaceManual(kind, level, slot, stack);
                }
            });
        }
        machineSlots = OldustryMachineBlockEntity.SIZE;
        if (kind.type == OldustryMachineKind.Type.STORAGE) {
            Player player = inventory.player;
            for (int i = 0; i < ARMOR.length; i++) {
                EquipmentSlot eq = ARMOR[i];
                addSlot(new ArmorSlot(inventory, player, eq, 39 - i, 8 + i * 18, 84, emptyArmor(eq)));
            }
        }
        addStandardInventorySlots(inventory, 8, OldustryMachineLayout.inventoryTop(kind));
        addDataSlots(data);
    }

    public OldustryMachineKind kind() {
        return kind;
    }

    public ContainerData data() {
        return data;
    }

    private static net.minecraft.resources.Identifier emptyArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> InventoryMenu.EMPTY_ARMOR_SLOT_HELMET;
            case CHEST -> InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE;
            case LEGS -> InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS;
            case FEET -> InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS;
            default -> null;
        };
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index < machineSlots) {
                if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, machineSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && kind.type == OldustryMachineKind.Type.STORAGE && container instanceof OldustryMachineBlockEntity machine) {
            if (!player.level().isClientSide()) {
                machine.cycleMode(player);
            }
            return true;
        }
        return false;
    }
}
