package com.oldustry.item.tool;

/** Numbers from IC2 1.117 {@code ItemObscurator} / cable {@code retexture} when {@code foamed == 2}. */
public final class ObscuratorStats {
    public static final int CAPACITY = 100000;
    public static final int TRANSFER = 250;
    public static final int TIER = 2;
    public static final int SCAN_COST = 20000;
    public static final int PRINT_COST = 5000;

    private ObscuratorStats() {}

    /**
     * Full collision cube with a normal model, from any namespace.
     * Cables are not a source. JAR {@code renderAsNormalBlock} was the opaque cube.
     */
    public static boolean canScan(boolean air, boolean fullCube, boolean modelRenderer, boolean cable) {
        return !air && fullCube && modelRenderer && !cable;
    }

    /** JAR {@code TileEntityCable.retexture}: only hardened foam. */
    public static boolean canPrint(int foam) {
        return foam == 2;
    }

    /**
     * JAR {@code ElectricItemManager.canUse}: the obscurator's own charge.
     * A worn pack does not unlock the click. {@code use} may still top the tool up after that.
     */
    public static boolean afford(int energy, int cost) {
        return energy >= cost;
    }

    /**
     * First paint always applies. A later click applies when block, state or copied face differs.
     * Same triple does not spend EU.
     */
    public static boolean paintChanges(boolean hadPaint, boolean sameBlock, boolean sameState, boolean sameFace) {
        if (!hadPaint) {
            return true;
        }
        return !sameBlock || !sameState || !sameFace;
    }
}
