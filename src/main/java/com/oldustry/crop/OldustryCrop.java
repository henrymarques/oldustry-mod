package com.oldustry.crop;

import net.minecraft.util.StringRepresentable;

/**
 * The 22 cards from {@code IC2Crops.registerCrops}, ids 0–21. {@link #NONE} is an empty stick.
 */
public enum OldustryCrop implements StringRepresentable {
    NONE(-1, "none", 0, new int[5], new String[0], 0),
    WEED(0, "weed", 0, stats(0, 0, 1, 0, 5), attrs("Weed", "Bad"), 3),
    WHEAT(1, "wheat", 1, stats(0, 4, 0, 0, 2), attrs("Yellow", "Food", "Wheat"), 7),
    PUMPKIN(2, "pumpkin", 1, stats(0, 1, 0, 3, 1), attrs("Orange", "Decoration", "Stem"), 4),
    MELON(3, "melon", 2, stats(0, 4, 0, 2, 0), attrs("Green", "Food", "Stem"), 4),
    DANDELION(4, "dandelion", 2, stats(1, 1, 0, 5, 1), attrs("Yellow", "Flower"), 4),
    ROSE(5, "rose", 2, stats(1, 1, 0, 5, 1), attrs("Red", "Flower", "Rose"), 4),
    BLACKTHORN(6, "blackthorn", 2, stats(1, 1, 0, 5, 1), attrs("Black", "Flower", "Rose"), 4),
    TULIP(7, "tulip", 2, stats(1, 1, 0, 5, 1), attrs("Purple", "Flower", "Tulip"), 4),
    CYAZINT(8, "cyazint", 2, stats(1, 1, 0, 5, 1), attrs("Blue", "Flower"), 4),
    VENOMILIA(9, "venomilia", 3, stats(3, 1, 3, 3, 3), attrs("Purple", "Flower", "Tulip", "Poison"), 6),
    REED(10, "reed", 2, stats(0, 0, 1, 0, 2), attrs("Reed"), 3),
    STICKREED(11, "stickreed", 4, stats(2, 0, 1, 0, 1), attrs("Reed", "Resin"), 4),
    COCOA(12, "cocoa", 3, stats(1, 3, 0, 4, 0), attrs("Brown", "Food", "Stem"), 4),
    FERRU(13, "ferru", 6, stats(2, 0, 0, 1, 0), attrs("Gray", "Leaves", "Metal"), 4),
    AURELIA(14, "aurelia", 8, stats(2, 0, 0, 2, 0), attrs("Gold", "Leaves", "Metal"), 5),
    REDWHEAT(15, "redwheat", 6, stats(3, 0, 0, 2, 0), attrs("Red", "Redstone", "Wheat"), 7),
    NETHER_WART(16, "nether_wart", 5, stats(4, 2, 0, 2, 1), attrs("Red", "Nether", "Ingredient", "Soulsand"), 3),
    TERRA_WART(17, "terra_wart", 5, stats(2, 4, 0, 3, 0), attrs("Blue", "Aether", "Consumable", "Snow"), 3),
    COFFEE(18, "coffee", 7, stats(1, 4, 1, 2, 0), attrs("Leaves", "Ingrident", "Beans"), 5),
    HOPS(19, "hops", 5, stats(2, 2, 0, 1, 1), attrs("Green", "Ingredient", "Wheat"), 7),
    CARROTS(20, "carrots", 2, stats(0, 4, 0, 0, 2), attrs("Orange", "Food", "Carrots"), 3),
    POTATO(21, "potato", 2, stats(0, 4, 0, 0, 2), attrs("Yellow", "Food", "Potato"), 3);

    private static final OldustryCrop[] BY_ID = new OldustryCrop[22];

    static {
        for (OldustryCrop crop : values()) {
            if (crop.id >= 0) {
                BY_ID[crop.id] = crop;
            }
        }
    }

    private final int id;
    private final String name;
    private final int tier;
    private final int[] stats;
    private final String[] attributes;
    private final int maxSize;

    OldustryCrop(int id, String name, int tier, int[] stats, String[] attributes, int maxSize) {
        this.id = id;
        this.name = name;
        this.tier = tier;
        this.stats = stats;
        this.attributes = attributes;
        this.maxSize = maxSize;
    }

    public static OldustryCrop byId(int id) {
        if (id < 0 || id >= BY_ID.length || BY_ID[id] == null) {
            return NONE;
        }
        return BY_ID[id];
    }

    public int id() {
        return id;
    }

    public int tier() {
        return tier;
    }

    public int stat(int index) {
        return index >= 0 && index < stats.length ? stats[index] : 0;
    }

    public String[] attributes() {
        return attributes;
    }

    /** JAR {@code CropCard.discoveredBy} default. */
    public String discoveredBy() {
        return "Alblaka";
    }

    /** JAR {@code CropCard.desc(i)} — attribute pairs for the cropnalyzer. */
    public String desc(int line) {
        if (attributes.length == 0) {
            return "";
        }
        if (line == 0) {
            String str = attributes[0];
            if (attributes.length >= 2) {
                str = str + ", " + attributes[1];
                if (attributes.length >= 3) {
                    str = str + ",";
                }
            }
            return str;
        }
        if (attributes.length < 3) {
            return "";
        }
        String s = attributes[2];
        if (attributes.length >= 4) {
            s = s + ", " + attributes[3];
        }
        return s;
    }

    public String tierRoman() {
        return switch (Math.clamp(tier, 0, 16)) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            case 11 -> "XI";
            case 12 -> "XII";
            case 13 -> "XIII";
            case 14 -> "XIV";
            case 15 -> "XV";
            case 16 -> "XVI";
            default -> "0";
        };
    }

    public int maxSize() {
        return maxSize;
    }

    /** Dye damage from {@code CropColorFlower}, or purple (5) for a harvested Venomilia that is not yet grinning. */
    public int dyeColor() {
        return switch (this) {
            case DANDELION -> 11;
            case ROSE -> 1;
            case BLACKTHORN -> 0;
            case TULIP, VENOMILIA -> 5;
            case CYAZINT -> 6;
            default -> -1;
        };
    }

    public int duration(int size) {
        return switch (this) {
            case WEED -> 300;
            case FERRU -> size == 3 ? 2000 : 800;
            case AURELIA -> size == 4 ? 2200 : 750;
            case COCOA -> size == 3 ? 900 : 400;
            case COFFEE -> size == 3 ? (int) (tier * 200 * 0.5) : size == 4 ? (int) (tier * 200 * 1.5) : tier * 200;
            case HOPS, REDWHEAT -> 600;
            case DANDELION, ROSE, BLACKTHORN, TULIP, CYAZINT -> size == 3 ? 600 : 400;
            case MELON -> size == 3 ? 700 : 250;
            case PUMPKIN -> size == 3 ? 600 : 200;
            case REED -> 200;
            case STICKREED -> size == 4 ? 400 : 100;
            case VENOMILIA -> size >= 3 ? 600 : 400;
            default -> tier * 200;
        };
    }

    public boolean canGrow(int size, int light, int nutrients, boolean ironBelow, boolean goldBelow) {
        return switch (this) {
            case NONE -> false;
            case WEED -> size < 3;
            case WHEAT, HOPS, CARROTS, POTATO, COFFEE -> size < maxSize && light >= 9;
            case REDWHEAT -> size < 7 && light <= 10 && light >= 5;
            case DANDELION, ROSE, BLACKTHORN, TULIP, CYAZINT -> size <= 3 && light >= 12;
            case VENOMILIA -> (size <= 4 && light >= 12) || size == 5;
            case COCOA -> size <= 3 && nutrients >= 3;
            case FERRU -> size < 3 || (size == 3 && ironBelow);
            case AURELIA -> size < 4 || (size == 4 && goldBelow);
            case PUMPKIN, MELON -> size <= 3;
            default -> size < maxSize;
        };
    }

    public boolean canHarvest(int size) {
        return switch (this) {
            case NONE, WEED -> false;
            case COFFEE, VENOMILIA -> size >= 4;
            case REED, STICKREED -> size > 1;
            default -> size == maxSize;
        };
    }

    public boolean canCross(int size) {
        return size >= 3;
    }

    public int sizeAfterHarvest(int size, int roll3) {
        if (this == STICKREED && size == 4) {
            return 3 - roll3;
        }
        return switch (this) {
            case WHEAT, FERRU, AURELIA, REDWHEAT -> 2;
            case PUMPKIN, MELON, DANDELION, ROSE, BLACKTHORN, TULIP, CYAZINT, VENOMILIA, COCOA, COFFEE, HOPS -> 3;
            default -> 1;
        };
    }

    public float gainChance() {
        return switch (this) {
            case FERRU -> tierChance() / 2.0f;
            case NETHER_WART -> 2.0f;
            case TERRA_WART -> 0.8f;
            case REDWHEAT -> 0.5f;
            default -> tierChance();
        };
    }

    private float tierChance() {
        float base = 1.0f;
        for (int i = 0; i < tier; i++) {
            base *= 0.95f;
        }
        return base;
    }

    public int weight(int humidity, int nutrients, int air) {
        return switch (this) {
            case COFFEE -> (int) (0.4 * humidity + 1.4 * nutrients + 1.2 * air);
            case COCOA -> (int) (humidity * 0.8 + nutrients * 1.3 + air * 0.9);
            case MELON, PUMPKIN -> (int) (humidity * 1.1 + nutrients * 0.9 + air);
            case REED, STICKREED -> (int) (humidity * 1.2 + nutrients + air * 0.8);
            default -> humidity + nutrients + air;
        };
    }

    public boolean weed(int size, int growth) {
        if (this == VENOMILIA) {
            return size == 5 && growth >= 8;
        }
        return size >= 2 && (id == 0 || growth >= 24);
    }

    public int emittedLight(int size) {
        return this == REDWHEAT && size == 7 ? 7 : 0;
    }

    public int redstone(int size) {
        return this == REDWHEAT && size == 7 ? 15 : 0;
    }

    /** Vanilla seed when every breeding stat is at most 1. Anything bred past that is a crop seed. */
    public boolean plainSeed(int growth, int gain, int resistance) {
        if (growth > 1 || gain > 1 || resistance > 1) {
            return false;
        }
        return this == WHEAT || this == PUMPKIN || this == MELON;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    private static int[] stats(int a, int b, int c, int d, int e) {
        return new int[] {a, b, c, d, e};
    }

    private static String[] attrs(String... names) {
        return names;
    }
}
