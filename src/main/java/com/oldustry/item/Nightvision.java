package com.oldustry.item;

import com.oldustry.energy.ElectricCharge;

/** JAR night-vision numbers plus the light gate. Kept off {@code Item} so energyCheck can load it. */
public final class Nightvision {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(20000, 200, 1, false);
    /** Desvio: {@code getMaxLocalRawBrightness} at the eye block. */
    public static final int DARK = 7;
    /** JAR night-vision duration, refreshed while the goggles pay. */
    public static final int DURATION = 250;

    private Nightvision() {}

    public static boolean darkEnough(int light) {
        return light <= DARK;
    }

    /** Effect this item just applied. A longer potion (duration above {@link #DURATION}) stays. */
    public static boolean fromGoggles(int duration, int amplifier) {
        return amplifier == 0 && duration > 0 && duration <= DURATION;
    }
}
