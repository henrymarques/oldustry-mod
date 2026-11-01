package com.oldustry.machine;

/** TileEntityMatter inject and the 1 UU threshold. The check calls this without booting the game. */
public final class OldustryMatter {
    public static final int UU = 1_000_000;
    public static final int MAX_INPUT = 512;
    /** Slot is read only while the stored amplifier is under this. */
    public static final int PULL_AT = 1000;
    public static final int SCRAP = 5000;
    public static final int SCRAP_BOX = 45000;
    public static final float EXPLOSION = 15.0f;

    /**
     * JAR {@code injectEnergy}: bonus is {@code max(packet, scrap)}, then scrap loses that whole bonus.
     * Returns energy, scrap, leftover. Leftover is the packet when the buffer is already full or redstone is on.
     */
    public static int[] inject(int energy, int scrap, int amount, boolean redstone) {
        if (energy >= UU || redstone) {
            return new int[] { energy, scrap, amount };
        }
        int bonus = Math.max(amount, scrap);
        return new int[] { energy + amount + 5 * bonus, scrap - bonus, 0 };
    }

    public static int progress(int energy) {
        int percent = energy / 10_000;
        return percent > 100 ? 100 : percent;
    }

    /** One UU per call, and only when the output can take it. */
    public static int produce(int energy, boolean room) {
        return energy >= UU && room ? energy - UU : energy;
    }

    public static boolean running(int energy, boolean redstone) {
        return energy > 0 && !redstone;
    }

    private OldustryMatter() {}
}
