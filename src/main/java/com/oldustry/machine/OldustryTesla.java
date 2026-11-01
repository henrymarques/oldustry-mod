package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

/** JAR {@code TileEntityTesla}. Numbers only; the block entity applies the hit. */
public final class OldustryTesla {
    public static final int CAPACITY = 10000;
    public static final int MAX_INPUT = 128;
    public static final int COST = 400;
    /** {@code ticker++ % 32 == 0}. */
    public static final int SHOCK_PERIOD = 32;
    /** AABB from {@code (x-4,y-4,z-4)} to {@code (x+5,y+5,z+5)}. */
    public static final int RANGE = 4;
    /** 1.5.2 reddust reads the speed args as RGB. The coil passes {@code (0, 0, 1)}. */
    public static final int PARTICLE_RGB = 0x0000FF;

    public record Tick(int energy, int ticker, int shockDamage) {}

    /**
     * One server tick. No redstone, or energy under {@link #COST}, leaves the ticker alone.
     * Shock damage is {@code energy / 400} from before the 1 EU drain.
     */
    public static Tick step(int energy, int ticker, boolean redstone) {
        if (!redstone || energy < COST) {
            return new Tick(energy, ticker, 0);
        }
        int damage = energy / COST;
        int shock = ticker % SHOCK_PERIOD == 0 ? damage : 0;
        return new Tick(energy - 1, ticker + 1, shock);
    }

    public static AABB box(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return new AABB(x - RANGE, y - RANGE, z - RANGE, x + RANGE + 1, y + RANGE + 1, z + RANGE + 1);
    }

    private OldustryTesla() {}
}
