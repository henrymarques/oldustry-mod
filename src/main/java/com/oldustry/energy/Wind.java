package com.oldustry.energy;

import net.minecraft.util.RandomSource;

/** IC2.windStrength: starts 10–20, random-walk every 128 ticks. */
public final class Wind {
    private static int strength = 15;
    private static long lastTick = Long.MIN_VALUE;

    public static int strength() {
        return strength;
    }

    public static void tick(long gameTime, RandomSource random, boolean overworld) {
        if (!overworld || gameTime == lastTick || gameTime % 128 != 0) {
            return;
        }
        lastTick = gameTime;
        if (strength == 0) {
            strength = 10 + random.nextInt(10);
            return;
        }
        int up = 10;
        int down = 10;
        if (strength > 20) {
            up -= strength - 20;
        }
        if (strength < 10) {
            down -= 10 - strength;
        }
        int roll = random.nextInt(up + down);
        if (roll < up) {
            strength++;
        } else {
            strength--;
        }
        strength = Math.max(1, strength);
    }

    private Wind() {}
}
