package com.oldustry.explosion;

/**
 * Ray decisions from IC2 1.117 {@code ExplosionIC2.shootRay}.
 * Entity damage in the JAR is {@code (int) (32 * power / 8)}, not the constructor's damage argument.
 */
public final class OldustryRay {
    public static final double DROP_POWER_LIMIT = 8.0;
    public static final double PASS_THROUGH = 1000.0;

    public record Hit(double absorption, boolean passThrough, boolean stop) {}

    private OldustryRay() {}

    public static Hit hit(boolean solid, double resistance, boolean whitelisted, double power) {
        double absorption = 0.5;
        if (solid) {
            absorption += (resistance + 4.0) * 0.3;
        }
        if (absorption > PASS_THROUGH && !whitelisted) {
            return new Hit(0.5, true, false);
        }
        if (absorption > power) {
            return new Hit(absorption, false, true);
        }
        return new Hit(absorption, false, false);
    }

    /** {@code true} drops, {@code false} destroys without drops, {@code null} keeps a previous mark. */
    public static Boolean drop(double power, boolean alreadyMarked) {
        if (power > DROP_POWER_LIMIT) {
            return Boolean.FALSE;
        }
        if (alreadyMarked) {
            return null;
        }
        return Boolean.TRUE;
    }

    public static int entityDamage(double power) {
        return (int) (32.0 * power / DROP_POWER_LIMIT);
    }

    public static int raySteps(double power) {
        double maxDistance = power / 0.4;
        return (int) Math.ceil(Math.PI / Math.atan(1.0 / maxDistance));
    }

    /** JAR {@code Util.roundToNegInf}. */
    public static int floor(double value) {
        int ret = (int) value;
        if (ret > value) {
            ret--;
        }
        return ret;
    }

    public static int anchorIndex(int step, int[] distanceKeys) {
        if (step == 4 || distanceKeys.length == 0) {
            return 0;
        }
        int distanceMin = step * step - 25;
        int indexStart = 0;
        int indexEnd = distanceKeys.length - 1;
        int index;
        do {
            index = (indexStart + indexEnd) / 2;
            int distance = distanceKeys[index];
            if (distance < distanceMin) {
                indexStart = index + 1;
            } else if (distance > distanceMin) {
                indexEnd = index - 1;
            } else {
                indexEnd = index;
            }
        } while (indexStart < indexEnd);
        return index;
    }

    /**
     * The 1.117 bytecode reads {@code entities.get(anchor)} on every pass of the loop variable.
     * With nobody dying, that repeats {@code size - anchor} times while the anchor key is inside range.
     */
    public static int anchorRepeats(int size, int anchor, int anchorKey, int step) {
        int distanceMax = step * step + 25;
        if (anchor >= size || anchorKey >= distanceMax) {
            return 0;
        }
        return size - anchor;
    }

    public static double[] direction(double phi, double theta) {
        return new double[] {
            Math.sin(theta) * Math.cos(phi),
            Math.cos(theta),
            Math.sin(theta) * Math.sin(phi)
        };
    }
}
