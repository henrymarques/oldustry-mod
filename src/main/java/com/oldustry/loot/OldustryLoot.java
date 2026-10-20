package com.oldustry.loot;

import com.oldustry.Oldustry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class OldustryLoot {
    public static final ResourceKey<LootTable> INGOTS = table("chests/ingots");
    public static final ResourceKey<LootTable> BRONZE_GEAR = table("chests/bronze_gear");
    public static final ResourceKey<LootTable> MINE_SPECIAL = table("chests/mine_special");
    public static final ResourceKey<LootTable> UU_RARE = table("chests/uu_rare");
    public static final ResourceKey<LootTable> IRIDIUM_UNIQUE = table("chests/iridium_unique");
    public static final ResourceKey<LootTable> SAPLING = table("chests/sapling");
    public static final ResourceKey<LootTable> TREETAP = table("chests/treetap");
    public static final ResourceKey<LootTable> CANS = table("chests/cans");
    public static final ResourceKey<LootTable> BRONZE_INGOT = table("chests/bronze_ingot");
    public static final ResourceKey<LootTable> TIN_ONE = table("chests/tin_one");
    public static final ResourceKey<LootTable> TIN_POT = table("chests/tin_pot");
    public static final ResourceKey<LootTable> SAPLING_ONE = table("chests/sapling_one");
    public static final ResourceKey<LootTable> STRONGHOLD_PICK = table("chests/stronghold_pick");

    private static ResourceKey<LootTable> table(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, path));
    }

    private OldustryLoot() {}
}
