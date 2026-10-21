package com.oldustry.machine;

import com.oldustry.item.ElectricItem;
import com.oldustry.item.ScannerItem;
import com.oldustry.item.UpgradeItem;
import com.oldustry.registry.ModItems;
import com.oldustry.registry.ModMenus;

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

public class MachineMenu extends AbstractContainerMenu {
    private static final EquipmentSlot[] ARMOR = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private final Container container;
    private final ContainerData data;
    private final MachineKind kind;
    private final int machineSlots;
    private final net.minecraft.world.level.Level level;

    public static MachineMenu fromClient(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        MachineKind kind = data != null ? data.readEnum(MachineKind.class) : MachineKind.GENERATOR;
        return new MachineMenu(id, inventory, new SimpleContainer(MachineBlockEntity.SIZE), new SimpleContainerData(7), kind);
    }

    public MachineMenu(int id, Inventory inventory, Container container, ContainerData data, MachineKind kind) {
        super(ModMenus.MACHINE.get(), id);
        checkContainerSize(container, MachineBlockEntity.SIZE);
        this.container = container;
        this.data = data;
        this.kind = kind;
        this.level = inventory.player.level();
        for (int i = 0; i < MachineBlockEntity.SIZE; i++) {
            int[] xy = MachineGui.slot(kind, i);
            boolean shown = xy[0] >= 0;
            int slot = i;
            addSlot(new Slot(container, i, shown ? xy[0] : 0, shown ? xy[1] : 0) {
                @Override
                public boolean isActive() {
                    return shown;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return shown && MachineMenu.this.mayPlace(slot, stack);
                }
            });
        }
        machineSlots = MachineBlockEntity.SIZE;
        if (kind.type == MachineKind.Type.STORAGE) {
            Player player = inventory.player;
            for (int i = 0; i < ARMOR.length; i++) {
                EquipmentSlot eq = ARMOR[i];
                addSlot(new ArmorSlot(inventory, player, eq, 39 - i, 8 + i * 18, 84, emptyArmor(eq)));
            }
        }
        addStandardInventorySlots(inventory, 8, MachineGui.inventoryTop(kind));
        addDataSlots(data);
    }

    public MachineKind kind() {
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

    private boolean mayPlace(int slot, ItemStack stack) {
        return switch (MachineGui.role(kind, slot)) {
            case FUEL -> mayPlaceFuel(stack);
            case OUTPUT -> false;
            case UPGRADE -> stack.getItem() instanceof UpgradeItem;
            case CHARGE -> stack.getItem() instanceof ElectricItem;
            case DISCHARGE -> stack.getItem() instanceof ElectricItem
                || stack.is(ModItems.SU_BATTERY.get())
                || stack.is(Items.REDSTONE);
            case HIDDEN -> false;
            case INPUT -> mayPlaceInput(slot, stack);
        };
    }

    private boolean mayPlaceFuel(ItemStack stack) {
        return switch (kind.type) {
            case GEO -> stack.is(ModItems.LAVA_CELL.get()) || stack.is(Items.LAVA_BUCKET);
            case WATER -> stack.is(ModItems.WATER_CELL.get()) || stack.is(Items.WATER_BUCKET);
            case GENERATOR -> !stack.is(Items.LAVA_BUCKET); // JAR rejects lava buckets here
            default -> true;
        };
    }

    private boolean mayPlaceInput(int slot, ItemStack stack) {
        return switch (kind.type) {
            case MINING -> switch (slot) {
                case 0 -> stack.is(ModItems.MINING_DRILL.get()) || stack.is(ModItems.DIAMOND_DRILL.get());
                case 1 -> stack.getItem() instanceof ScannerItem;
                case 2 -> stack.is(ModItems.MINING_PIPE.get());
                default -> false;
            };
            case PUMPING -> stack.is(ModItems.EMPTY_CELL.get()) || stack.is(Items.BUCKET);
            case CANNING -> switch (slot) {
                case 0 -> stack.has(net.minecraft.core.component.DataComponents.FOOD)
                    || CannerFuelValues.isFuel(stack)
                    || stack.is(ModItems.CF_PELLET.get());
                case 1 -> stack.is(ModItems.TIN_CAN.get())
                    || stack.is(ModItems.FUEL_CAN.get())
                    || stack.is(ModItems.FUEL_JETPACK.get())
                    || stack.is(ModItems.CF_PACK.get());
                default -> false;
            };
            case ELECTROLYZER -> switch (slot) {
                case 0 -> stack.is(ModItems.WATER_CELL.get());
                case 1 -> stack.is(ModItems.ELECTROLYZED_WATER_CELL.get());
                default -> false;
            };
            case MACERATOR -> !MachineRecipes.macerator(stack.getItem()).isEmpty();
            case EXTRACTOR -> !MachineRecipes.extractor(stack.getItem()).isEmpty();
            case COMPRESSOR -> !MachineRecipes.compressor(stack.getItem()).isEmpty();
            case RECYCLER -> !MachineRecipes.recyclerBlacklist(stack.getItem());
            case FURNACE, INDUCTION -> canSmelt(stack);
            default -> true;
        };
    }

    private boolean canSmelt(ItemStack stack) {
        return level.recipeAccess()
            .propertySet(net.minecraft.world.item.crafting.RecipePropertySet.FURNACE_INPUT)
            .test(stack);
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
        if (id == 0 && kind.type == MachineKind.Type.STORAGE && container instanceof MachineBlockEntity machine) {
            if (!player.level().isClientSide()) {
                machine.cycleMode(player);
            }
            return true;
        }
        return false;
    }
}
