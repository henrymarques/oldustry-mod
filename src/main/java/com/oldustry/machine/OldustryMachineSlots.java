package com.oldustry.machine;

import com.oldustry.item.ElectricItem;
import com.oldustry.item.UpgradeItem;
import com.oldustry.registry.OldustryItems;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Inventory slot roles shared by menu and block entity. */
public final class OldustryMachineSlots {
    public enum Role {
        HIDDEN,
        INPUT,
        OUTPUT,
        FUEL,
        CHARGE,
        DISCHARGE,
        UPGRADE
    }

    public static Role role(OldustryMachineKind kind, int slot) {
        return switch (kind.type) {
            case MACERATOR, EXTRACTOR, COMPRESSOR, RECYCLER, FURNACE -> switch (slot) {
                case 0 -> Role.INPUT;
                case 1 -> Role.OUTPUT;
                case 2, 3, 4, 5 -> Role.UPGRADE;
                case 6 -> Role.DISCHARGE;
                default -> Role.HIDDEN;
            };
            case GENERATOR, GEO -> slot == 0 ? Role.FUEL : slot == 1 ? Role.CHARGE : Role.HIDDEN;
            case SOLAR, WIND -> slot == 1 ? Role.CHARGE : Role.HIDDEN;
            case WATER -> slot == 0 ? Role.FUEL : slot == 1 ? Role.CHARGE : Role.HIDDEN;
            case STORAGE -> slot == 0 ? Role.CHARGE : slot == 1 ? Role.DISCHARGE : Role.HIDDEN;
            // Miner: drill=0, scanner=1, pipe=2, discharge=3. Drops are ejected, not stored.
            case MINING -> switch (slot) {
                case 0 -> Role.INPUT;   // drill
                case 1 -> Role.INPUT;   // scanner
                case 2 -> Role.INPUT;   // mining pipe, or a block placed when the column retracts
                case 3 -> Role.DISCHARGE;
                default -> Role.HIDDEN;
            };
            // Pump: empty_cell=0, output=1, discharge=2
            case PUMPING -> switch (slot) {
                case 0 -> Role.INPUT;
                case 1 -> Role.OUTPUT;
                case 2 -> Role.DISCHARGE;
                default -> Role.HIDDEN;
            };
            // Canner: resource=0, can=1, output=2, discharge=3
            case CANNING -> switch (slot) {
                case 0 -> Role.INPUT;
                case 1 -> Role.INPUT;
                case 2 -> Role.OUTPUT;
                case 3 -> Role.DISCHARGE;
                default -> Role.HIDDEN;
            };
            // Electrolyzer: water=0, electrolyzed=1
            case ELECTROLYZER -> switch (slot) {
                case 0, 1 -> Role.INPUT;
                default -> Role.HIDDEN;
            };
            // Induction: inputA=0, inputB=1, discharge=2, outA=3, outB=4
            case INDUCTION -> switch (slot) {
                case 0, 1 -> Role.INPUT;
                case 2 -> Role.DISCHARGE;
                case 3, 4 -> Role.OUTPUT;
                default -> Role.HIDDEN;
            };
            default -> Role.HIDDEN;
        };
    }

    private OldustryMachineSlots() {}

    public static boolean mayPlaceManual(OldustryMachineKind kind, net.minecraft.world.level.Level level, int slot, ItemStack stack) {
        return switch (role(kind, slot)) {
            case FUEL -> mayPlaceFuel(kind, stack);
            case OUTPUT -> false;
            case UPGRADE -> stack.getItem() instanceof UpgradeItem;
            case CHARGE -> stack.getItem() instanceof ElectricItem;
            case DISCHARGE -> OldustryMinerAccess.acceptsDischarge(stack);
            case HIDDEN -> false;
            case INPUT -> mayPlaceInput(kind, level, slot, stack);
        };
    }

    private static boolean mayPlaceFuel(OldustryMachineKind kind, ItemStack stack) {
        return switch (kind.type) {
            case GEO -> stack.is(OldustryItems.LAVA_CELL.get()) || stack.is(Items.LAVA_BUCKET);
            case WATER -> stack.is(OldustryItems.WATER_CELL.get()) || stack.is(Items.WATER_BUCKET);
            case GENERATOR -> !stack.is(Items.LAVA_BUCKET); // JAR rejects lava buckets here
            default -> true;
        };
    }

    private static boolean mayPlaceInput(OldustryMachineKind kind, net.minecraft.world.level.Level level, int slot, ItemStack stack) {
        return switch (kind.type) {
            case MINING -> OldustryMinerAccess.accepts(slot, stack);
            case PUMPING -> stack.is(OldustryItems.EMPTY_CELL.get()) || stack.is(Items.BUCKET);
            case CANNING -> switch (slot) {
                case 0 -> stack.has(net.minecraft.core.component.DataComponents.FOOD)
                    || OldustryCannerFuelValues.isFuel(stack)
                    || stack.is(OldustryItems.CF_PELLET.get());
                case 1 -> stack.is(OldustryItems.TIN_CAN.get())
                    || stack.is(OldustryItems.FUEL_CAN.get())
                    || stack.is(OldustryItems.FUEL_JETPACK.get())
                    || stack.is(OldustryItems.CF_PACK.get());
                default -> false;
            };
            case ELECTROLYZER -> switch (slot) {
                case 0 -> stack.is(OldustryItems.WATER_CELL.get());
                case 1 -> stack.is(OldustryItems.ELECTROLYZED_WATER_CELL.get());
                default -> false;
            };
            case MACERATOR -> !OldustryProcessingRecipes.macerator(stack.getItem()).isEmpty();
            case EXTRACTOR -> !OldustryProcessingRecipes.extractor(stack.getItem()).isEmpty();
            case COMPRESSOR -> !OldustryProcessingRecipes.compressor(stack.getItem()).isEmpty();
            case RECYCLER -> !OldustryProcessingRecipes.recyclerBlacklist(stack.getItem());
            case FURNACE, INDUCTION -> level != null && canSmelt(level, stack);
            default -> true;
        };
    }

    private static boolean canSmelt(net.minecraft.world.level.Level level, ItemStack stack) {
        return level.recipeAccess()
            .propertySet(net.minecraft.world.item.crafting.RecipePropertySet.FURNACE_INPUT)
            .test(stack);
    }

    public static int[] slotsForFace(OldustryMachineKind kind) {
        return switch (kind.type) {
            case MINING -> OldustryMinerAccess.FACES;
            case PUMPING -> new int[] { 0, 1, 2 };
            case CANNING -> new int[] { 0, 1, 2, 3 };
            case ELECTROLYZER -> new int[] { 0, 1 };
            case INDUCTION -> new int[] { 0, 1, 2, 3, 4 };
            default -> new int[] { 0, 1, 6 };
        };
    }

    public static boolean mayPlaceAutomation(OldustryMachineKind kind, net.minecraft.world.level.Level level,
                                              int slot, ItemStack stack, Direction face) {
        if (kind.type == OldustryMachineKind.Type.MINING) return OldustryMinerAccess.canInsert(slot, stack, face);
        Role role = role(kind, slot);
        if (role == Role.FUEL) return true;
        if (role == Role.DISCHARGE) return OldustryMinerAccess.acceptsDischarge(stack);
        if (role != Role.INPUT) return false;
        // Pump automation accepts cells only; the menu also accepts buckets.
        if (kind.type == OldustryMachineKind.Type.PUMPING) return stack.is(OldustryItems.EMPTY_CELL.get());
        return mayPlaceInput(kind, level, slot, stack);
    }

    public static boolean mayTakeAutomation(OldustryMachineKind kind, int slot, Direction face) {
        if (kind.type == OldustryMachineKind.Type.MINING) return OldustryMinerAccess.canExtract(slot, face);
        if (kind.type == OldustryMachineKind.Type.ELECTROLYZER) return slot == 0 || slot == 1;
        return role(kind, slot) == Role.OUTPUT;
    }

}
