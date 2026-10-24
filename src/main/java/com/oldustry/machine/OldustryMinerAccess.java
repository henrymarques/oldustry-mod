package com.oldustry.machine;

import java.util.function.IntPredicate;

import com.oldustry.item.ElectricItem;
import com.oldustry.item.tool.ScannerItem;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * JAR {@code TileEntityMiner} slots plus {@code TileEntityInventory} side reservation.
 * Drill owns the top, the pipe slot owns the sides, the scanner owns the bottom.
 * The battery ({@code InvSlotDischarge}, side ANY) can be inserted wherever no owner accepts the stack, and never extracted.
 */
public final class OldustryMinerAccess {
    public static final int DRILL = 0;
    public static final int SCANNER = 1;
    public static final int PIPE = 2;
    public static final int BATTERY = 3;
    static final int[] FACES = {DRILL, SCANNER, PIPE, BATTERY};

    private enum Pref {
        TOP, BOTTOM, SIDE, ANY
    }

    public static boolean accepts(int slot, ItemStack stack) {
        return switch (slot) {
            case DRILL -> stack.is(OldustryItems.MINING_DRILL.get()) || stack.is(OldustryItems.DIAMOND_DRILL.get());
            case SCANNER -> stack.getItem() instanceof ScannerItem;
            case PIPE -> stack.getItem() instanceof BlockItem;
            case BATTERY -> acceptsDischarge(stack);
            default -> false;
        };
    }

    /** JAR {@code InvSlotDischarge.accepts}: electric item, single-use battery, or redstone. */
    public static boolean acceptsDischarge(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ElectricItem || item == OldustryItems.SU_BATTERY.get() || item == Items.REDSTONE;
    }

    public static boolean canInsert(int slot, ItemStack stack, Direction face) {
        return insert(slot, face, s -> accepts(s, stack));
    }

    /** Side matrix. {@code accepts} is which slots would take this stack. */
    static boolean insert(int slot, Direction face, IntPredicate accepts) {
        if (!known(slot) || !accepts.test(slot)) {
            return false;
        }
        Pref pref = pref(slot);
        if (pref != Pref.ANY && matches(pref, face)) {
            return true;
        }
        for (int other : FACES) {
            if (other != slot && pref(other) != Pref.ANY && matches(pref(other), face) && accepts.test(other)) {
                return false;
            }
        }
        return true;
    }

    public static boolean canExtract(int slot, Direction face) {
        if (!known(slot)) {
            return false;
        }
        Pref pref = pref(slot);
        if (pref != Pref.ANY && matches(pref, face)) {
            return true;
        }
        for (int other : FACES) {
            if (other != slot && pref(other) != Pref.ANY && matches(pref(other), face)) {
                return false;
            }
        }
        return true;
    }

    private static boolean known(int slot) {
        return slot >= DRILL && slot <= BATTERY;
    }

    private static Pref pref(int slot) {
        return switch (slot) {
            case DRILL -> Pref.TOP;
            case SCANNER -> Pref.BOTTOM;
            case PIPE -> Pref.SIDE;
            default -> Pref.ANY;
        };
    }

    private static boolean matches(Pref pref, Direction face) {
        if (face == null) {
            return false;
        }
        return switch (pref) {
            case TOP -> face == Direction.UP;
            case BOTTOM -> face == Direction.DOWN;
            case SIDE -> face.getAxis().isHorizontal();
            case ANY -> false;
        };
    }

    private OldustryMinerAccess() {}
}
