package com.oldustry.data;

import java.util.List;
import java.util.Set;

import com.oldustry.data.recipe.OldustryRecipeProvider;
import com.oldustry.loot.OldustryChestLoot;
import com.oldustry.loot.OldustryGlms;
import com.oldustry.world.OldustryWorldgen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class OldustryData {
    public static void gatherClient(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, OldustryWorldgen::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, OldustryWorldgen::bootstrapPlaced)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, OldustryWorldgen::bootstrapBiomeModifiers)
        );
        event.createProvider(OldustryModelProvider::new);
        event.createProvider(OldustryRecipeProvider.Runner::new);
        event.createProvider((output, lookup) -> new LootTableProvider(
            output,
            Set.of(),
            List.of(
                new LootTableProvider.SubProviderEntry(OldustryBlockLoot::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(registries -> new OldustryChestLoot(), LootContextParamSets.CHEST)
            ),
            lookup
        ));
        event.createProvider(OldustryBlockTags::new);
        event.createProvider(OldustryItemTags::new);
        event.createProvider(OldustryBiomeTags::new);
        event.createProvider(OldustryGlms::new);
        event.createProvider((output, lookup) -> new AdvancementProvider(output, lookup, List.of(new OldustryAdvancements())));
    }

    private OldustryData() {}
}
