package com.oldustry.item;

/**
 * JAR {@code IC2} static ores plus {@code OreDictionary} {@code ore*} = 1.
 * Copper is the {@code oreCopper} value 2. Iron is the {@code ore*} catch-all (1).
 */
public final class ValuableOre {
    public static int pathValue(String path) {
        if (path.contains("diamond_ore")) {
            return 4;
        }
        if (path.contains("emerald_ore") || path.contains("quartz_ore")) {
            return 5;
        }
        if (path.contains("gold_ore") || path.contains("lapis_ore") || path.contains("redstone_ore")) {
            return 3;
        }
        if (path.contains("copper_ore") || path.contains("tin_ore")) {
            return 2;
        }
        if (path.contains("uranium_ore")) {
            return 4;
        }
        if (path.contains("coal_ore") || path.contains("iron_ore")) {
            return 1;
        }
        return 0;
    }

    private ValuableOre() {}
}
