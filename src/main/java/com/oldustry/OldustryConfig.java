package com.oldustry;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class OldustryConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue ORE_DENSITY;
    public static final ModConfigSpec.BooleanValue WORLDGEN_TIN;
    public static final ModConfigSpec.BooleanValue WORLDGEN_URANIUM;
    public static final ModConfigSpec.BooleanValue WORLDGEN_RUBBER;
    public static final ModConfigSpec.BooleanValue BURN_SCRAP;
    public static final ModConfigSpec.BooleanValue CRAFT_BUCKET;
    public static final ModConfigSpec.BooleanValue CRAFT_COIN;
    public static final ModConfigSpec.BooleanValue CRAFT_GLOWSTONE;
    public static final ModConfigSpec.BooleanValue CRAFT_GUNPOWDER;
    public static final ModConfigSpec.BooleanValue CRAFT_RAIL;
    public static final ModConfigSpec.BooleanValue MINER_LAPOTRON;
    public static final ModConfigSpec.BooleanValue SOUNDS_ENABLED;
    public static final ModConfigSpec.IntValue SOUND_SOURCE_LIMIT;
    public static final ModConfigSpec.BooleanValue CRAFT_ITNT;
    public static final ModConfigSpec.BooleanValue CRAFT_NUKE;
    public static final ModConfigSpec.DoubleValue NUKE_POWER;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        ORE_DENSITY = b.defineInRange("oreDensityFactor", 1.0, 0.0, 10.0);
        WORLDGEN_TIN = b.define("enableWorldGenOreTin", true);
        WORLDGEN_URANIUM = b.define("enableWorldGenOreUranium", true);
        WORLDGEN_RUBBER = b.define("enableWorldGenTreeRubber", true);
        BURN_SCRAP = b.define("enableBurningScrap", true);
        CRAFT_BUCKET = b.define("enableCraftingBucket", true);
        CRAFT_COIN = b.define("enableCraftingCoin", true);
        CRAFT_GLOWSTONE = b.define("enableCraftingGlowstoneDust", true);
        CRAFT_GUNPOWDER = b.define("enableCraftingGunpowder", true);
        CRAFT_RAIL = b.define("enableCraftingRail", true);
        MINER_LAPOTRON = b.comment("Allow tier-3 (lapotron) input on the Miner (default false = tier-1 RE battery)").define("enableMinerLapotron", false);
        SOUNDS_ENABLED = b.define("soundsEnabled", true);
        SOUND_SOURCE_LIMIT = b.defineInRange("soundSourceLimit", 32, 0, 256);
        CRAFT_ITNT = b.comment("Industrial TNT recipes (flint + TNT). Default on, same as the other enableCrafting* flags.").define("enableCraftingITnt", true);
        CRAFT_NUKE = b.comment("Enable crafting of nukes. The re-enriched uranium cell is nuclear content, so the recipe stays unregistered until that item exists.").define("enableCraftingNuke", true);
        NUKE_POWER = b.comment("Explosion power of a nuke, where TNT is 4").defineInRange("explosionPowerNuke", 35.0, 0.0, 100.0);
        SPEC = b.build();
    }

    private OldustryConfig() {}
}
