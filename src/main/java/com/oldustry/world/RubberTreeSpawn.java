package com.oldustry.world;

import java.util.function.IntUnaryOperator;

/** JAR {@code IC2.generate} rubber weight and gate. Each tag rolls on its own. */
public final class RubberTreeSpawn {
    private RubberTreeSpawn() {}

    /** How many positions this roll emits. A closed gate emits none. Weight 0 still consumes the gate roll. */
    public static int tries(boolean taiga, boolean forestOrJungle, boolean swamp, IntUnaryOperator nextInt) {
        int weight = weight(taiga, forestOrJungle, swamp, nextInt);
        return opens(weight, nextInt) ? weight : 0;
    }

    /** Taiga 0–2, forest/jungle 1–5, swamp 5–14. */
    public static int weight(boolean taiga, boolean forestOrJungle, boolean swamp, IntUnaryOperator nextInt) {
        int weight = 0;
        if (taiga) {
            weight += nextInt.applyAsInt(3);
        }
        if (forestOrJungle) {
            weight += nextInt.applyAsInt(5) + 1;
        }
        if (swamp) {
            weight += nextInt.applyAsInt(10) + 5;
        }
        return weight;
    }

    /** {@code nextInt(100) + 1 <= weight * 2}. Weight 0 never opens and still consumes the roll. */
    public static boolean opens(int weight, IntUnaryOperator nextInt) {
        return nextInt.applyAsInt(100) + 1 <= weight * 2;
    }
}
