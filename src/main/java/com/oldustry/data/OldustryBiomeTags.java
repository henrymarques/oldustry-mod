package com.oldustry.data;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.neoforged.neoforge.common.Tags;

public class OldustryBiomeTags extends BiomeTagsProvider {
    public OldustryBiomeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, Oldustry.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(OldustryTags.Biomes.RUBBER_FOREST_OR_JUNGLE)
            .addTag(Tags.Biomes.IS_FOREST)
            .addTag(Tags.Biomes.IS_JUNGLE);
    }
}
