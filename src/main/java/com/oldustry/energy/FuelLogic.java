package com.oldustry.energy;

import net.minecraft.world.item.ItemStack;

/** Generator fuel: furnace burn / 4. Lava buckets rejected; lava cells burn. */
public final class FuelLogic {
    public static final int RUBBER_SAPLING = 80;
    public static final int SCRAP = 350;
    public static final int SCRAP_BOX = 3150;
    public static final int GEO_MAX_LAVA = 24000;
    public static final int GEO_CELL = 1000;
    public static final int WATER_MAX = 2000;
    public static final int WATER_CELL = 500;

    public static int generatorBurnTicks(int furnaceBurnTime, boolean lavaBucket) {
        if (lavaBucket || furnaceBurnTime <= 0) {
            return 0;
        }
        return furnaceBurnTime / 4;
    }

    public static boolean lavaBucket(String item) {
        return "lava_bucket".equals(item);
    }

    public static int extraFurnaceBurn(String item) {
        return switch (item) {
            case "rubber_sapling" -> RUBBER_SAPLING;
            case "scrap" -> SCRAP;
            case "scrap_box" -> SCRAP_BOX;
            case "lava_cell" -> 20000;
            default -> 0;
        };
    }

    /** JAR consume(1): only the last unit leaves a container in-slot. */
    public static boolean leaveContainer(int stackCount) {
        return stackCount <= 1;
    }

    /**
     * JAR {@code InvSlotConsumable.consume(1)} with container item: shrink one;
     * only the last item becomes the empty container (stack of N does not wipe to 1 empty).
     */
    public static ItemStack afterConsumingOne(ItemStack fuel, ItemStack container) {
        if (fuel.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (leaveContainer(fuel.getCount())) {
            return container.copy();
        }
        ItemStack left = fuel.copy();
        left.shrink(1);
        return left;
    }

    public static boolean geoCanAccept(int lava) {
        return lava + GEO_CELL <= GEO_MAX_LAVA;
    }

    public static boolean waterCanAccept(int waterFuel) {
        return waterFuel + WATER_CELL <= WATER_MAX;
    }

    public record BurnTick(int burnTime, int energy, boolean converting) {}

    /** TileEntityGenerator.isConverting: fuel > 0. Adds then clamps like BaseGenerator. */
    public static BurnTick tickBurn(int burnTime, int energy, int packet, int capacity) {
        if (burnTime <= 0) {
            return new BurnTick(0, energy, false);
        }
        return new BurnTick(burnTime - 1, Math.min(capacity, energy + packet), true);
    }

    /** TileEntityBaseGenerator.isConverting: pause unless the whole packet fits. */
    public static BurnTick tickIfFits(int burnTime, int energy, int packet, int capacity) {
        if (burnTime <= 0 || energy + packet > capacity) {
            return new BurnTick(burnTime, energy, false);
        }
        return new BurnTick(burnTime - 1, energy + packet, true);
    }

    private FuelLogic() {}
}
