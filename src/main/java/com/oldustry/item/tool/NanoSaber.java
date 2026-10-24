package com.oldustry.item.tool;

/** JAR {@code ItemNanoSaber} numbers. Hit drain is the documented {@code damage × 8}. */
public final class NanoSaber {
    /** JAR 40000. Desvio: energy crystal carrega 100000. */
    public static final int MAX = 100_000;
    public static final int TRANSFER = 128;
    public static final int TIER = 2;
    public static final int IDLE_DAMAGE = 4;
    public static final int ACTIVE_DAMAGE = 20;
    public static final int ENCHANT = 14;
    /** Added on top of the idle sword so a full-charge hit totals {@link #ACTIVE_DAMAGE}. */
    public static final int ACTIVE_BONUS = ACTIVE_DAMAGE - IDLE_DAMAGE;
    public static final int HIT_EU = ACTIVE_DAMAGE * 8;
    /** {@code drainSaber(2)} per nano piece shredded. */
    public static final int SHRED_EU = 2 * 8;
    /** {@code onBlockStartBreak} passes 10. */
    public static final int BREAK_EU = 10 * 8;
    /** Hotbar, every 16 ticks: {@code drainSaber(64)}. */
    public static final int HOTBAR_EU = 64 * 8;
    /** Rest of the inventory, every 64 ticks: {@code drainSaber(16)}. */
    public static final int STORED_EU = 16 * 8;
    /** {@code canUse(16)} required to turn it on. Not spent by the toggle. */
    public static final int ARM_EU = 16 * 8;

    private NanoSaber() {}

    /** 0 when this tick is free. Hotbar wins on ticks that are multiples of both. */
    public static int upkeep(boolean hotbar, int tick) {
        if (tick % 16 == 0 && hotbar) {
            return HOTBAR_EU;
        }
        if (tick % 64 == 0) {
            return STORED_EU;
        }
        return 0;
    }
}
