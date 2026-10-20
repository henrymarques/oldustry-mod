package com.oldustry.loot;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public class OldustryGlms extends GlobalLootModifierProvider {
    public OldustryGlms(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Oldustry.MOD_ID);
    }

    @Override
    protected void start() {
        inject("abandoned_mineshaft", BuiltInLootTables.ABANDONED_MINESHAFT, OldustryLoot.MINE_SPECIAL, OldustryLoot.INGOTS);
        inject("simple_dungeon", BuiltInLootTables.SIMPLE_DUNGEON, OldustryLoot.INGOTS);
        inject("desert_pyramid", BuiltInLootTables.DESERT_PYRAMID, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("jungle_temple", BuiltInLootTables.JUNGLE_TEMPLE, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("stronghold_corridor", BuiltInLootTables.STRONGHOLD_CORRIDOR, OldustryLoot.UU_RARE, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("stronghold_crossing", BuiltInLootTables.STRONGHOLD_CROSSING, OldustryLoot.STRONGHOLD_PICK, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("village_weaponsmith", BuiltInLootTables.VILLAGE_WEAPONSMITH, OldustryLoot.BRONZE_INGOT, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS, OldustryLoot.SAPLING);
        inject("spawn_bonus_chest", BuiltInLootTables.SPAWN_BONUS_CHEST, OldustryLoot.TREETAP);
        inject("stronghold_library", BuiltInLootTables.STRONGHOLD_LIBRARY, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("village_armorer", BuiltInLootTables.VILLAGE_ARMORER, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("village_toolsmith", BuiltInLootTables.VILLAGE_TOOLSMITH, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("village_plains_house", BuiltInLootTables.VILLAGE_PLAINS_HOUSE, OldustryLoot.SAPLING, OldustryLoot.INGOTS);
        inject("village_desert_house", BuiltInLootTables.VILLAGE_DESERT_HOUSE, OldustryLoot.SAPLING, OldustryLoot.INGOTS);
        inject("village_savanna_house", BuiltInLootTables.VILLAGE_SAVANNA_HOUSE, OldustryLoot.SAPLING, OldustryLoot.INGOTS);
        inject("village_snowy_house", BuiltInLootTables.VILLAGE_SNOWY_HOUSE, OldustryLoot.SAPLING, OldustryLoot.INGOTS);
        inject("village_taiga_house", BuiltInLootTables.VILLAGE_TAIGA_HOUSE, OldustryLoot.SAPLING, OldustryLoot.INGOTS);
        inject("igloo_chest", BuiltInLootTables.IGLOO_CHEST, OldustryLoot.CANS);
        inject("pillager_outpost", BuiltInLootTables.PILLAGER_OUTPOST, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS, OldustryLoot.UU_RARE);
        inject("woodland_mansion", BuiltInLootTables.WOODLAND_MANSION, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS, OldustryLoot.UU_RARE);
        inject("buried_treasure", BuiltInLootTables.BURIED_TREASURE, OldustryLoot.INGOTS, OldustryLoot.IRIDIUM_UNIQUE);
        inject("shipwreck_treasure", BuiltInLootTables.SHIPWRECK_TREASURE, OldustryLoot.INGOTS, OldustryLoot.UU_RARE, OldustryLoot.IRIDIUM_UNIQUE);
        inject("shipwreck_supply", BuiltInLootTables.SHIPWRECK_SUPPLY, OldustryLoot.CANS);
        inject("shipwreck_map", BuiltInLootTables.SHIPWRECK_MAP, OldustryLoot.SAPLING_ONE);
        inject("underwater_ruin_big", BuiltInLootTables.UNDERWATER_RUIN_BIG, OldustryLoot.INGOTS);
        inject("underwater_ruin_small", BuiltInLootTables.UNDERWATER_RUIN_SMALL, OldustryLoot.INGOTS);
        inject("nether_bridge", BuiltInLootTables.NETHER_BRIDGE, OldustryLoot.MINE_SPECIAL, OldustryLoot.INGOTS);
        inject("ruined_portal", BuiltInLootTables.RUINED_PORTAL, OldustryLoot.INGOTS);
        inject("bastion_bridge", BuiltInLootTables.BASTION_BRIDGE, OldustryLoot.INGOTS);
        inject("bastion_hoglin_stable", BuiltInLootTables.BASTION_HOGLIN_STABLE, OldustryLoot.INGOTS);
        inject("bastion_other", BuiltInLootTables.BASTION_OTHER, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("bastion_treasure", BuiltInLootTables.BASTION_TREASURE, OldustryLoot.UU_RARE, OldustryLoot.IRIDIUM_UNIQUE);
        inject("ancient_city", BuiltInLootTables.ANCIENT_CITY, OldustryLoot.UU_RARE, OldustryLoot.INGOTS, OldustryLoot.IRIDIUM_UNIQUE);
        inject("ancient_city_ice_box", BuiltInLootTables.ANCIENT_CITY_ICE_BOX, OldustryLoot.CANS);
        inject("end_city_treasure", BuiltInLootTables.END_CITY_TREASURE, OldustryLoot.UU_RARE, OldustryLoot.BRONZE_GEAR);
        inject("trail_ruins_common", BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON, OldustryLoot.TIN_ONE);
        inject("trail_ruins_rare", BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE, OldustryLoot.IRIDIUM_UNIQUE);
        inject("desert_pyramid_archaeology", BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, OldustryLoot.TIN_ONE);
        inject("desert_well_archaeology", BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, OldustryLoot.TIN_ONE);
        inject("ocean_ruin_cold_archaeology", BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY, OldustryLoot.TIN_ONE);
        inject("ocean_ruin_warm_archaeology", BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY, OldustryLoot.TIN_ONE);
        inject("trial_reward", BuiltInLootTables.TRIAL_CHAMBERS_REWARD, OldustryLoot.UU_RARE);
        inject("trial_reward_ominous", BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS, OldustryLoot.IRIDIUM_UNIQUE);
        inject("trial_corridor", BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR, OldustryLoot.INGOTS);
        inject("trial_intersection", BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION, OldustryLoot.BRONZE_GEAR, OldustryLoot.INGOTS);
        inject("trial_intersection_barrel", BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL, OldustryLoot.CANS);
        inject("trial_entrance", BuiltInLootTables.TRIAL_CHAMBERS_ENTRANCE, OldustryLoot.TREETAP);
        inject("trial_supply", BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY, OldustryLoot.CANS, OldustryLoot.INGOTS);
        inject("trial_corridor_pot", BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR_POT, OldustryLoot.TIN_POT);
    }

    @SafeVarargs
    private void inject(String name, ResourceKey<LootTable> vanilla, ResourceKey<LootTable>... tables) {
        for (ResourceKey<LootTable> table : tables) {
            add(name + "_" + table.identifier().getPath().replace('/', '_'), new AddTableLootModifier(
                new LootItemCondition[] { LootTableIdCondition.builder(vanilla.identifier()).build() },
                0,
                table
            ));
        }
    }
}
