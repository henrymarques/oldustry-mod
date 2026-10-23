package com.oldustry.data;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class OldustryBlockTags extends BlockTagsProvider {
    public OldustryBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, Oldustry.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.TIN_ORE.getKey());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.TIN_ORE.getKey());
        tag(OldustryTags.Blocks.ORES_TIN).add(ModBlocks.TIN_ORE.getKey());
        tag(Tags.Blocks.ORES).add(ModBlocks.TIN_ORE.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.URANIUM_ORE.getKey());
        tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.URANIUM_ORE.getKey());
        tag(OldustryTags.Blocks.ORES_URANIUM).add(ModBlocks.URANIUM_ORE.getKey());
        tag(Tags.Blocks.ORES).add(ModBlocks.URANIUM_ORE.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.TIN_BLOCK.getKey());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.TIN_BLOCK.getKey());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_TIN).add(ModBlocks.TIN_BLOCK.getKey());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(ModBlocks.TIN_BLOCK.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.BRONZE_BLOCK.getKey());
        tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.BRONZE_BLOCK.getKey());
        tag(BlockTags.BEACON_BASE_BLOCKS).add(ModBlocks.BRONZE_BLOCK.getKey());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_BRONZE).add(ModBlocks.BRONZE_BLOCK.getKey());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(ModBlocks.BRONZE_BLOCK.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.URANIUM_BLOCK.getKey());
        tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.URANIUM_BLOCK.getKey());
        tag(BlockTags.BEACON_BASE_BLOCKS).add(ModBlocks.URANIUM_BLOCK.getKey());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_URANIUM).add(ModBlocks.URANIUM_BLOCK.getKey());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(ModBlocks.URANIUM_BLOCK.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.REINFORCED_STONE.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.REINFORCED_GLASS.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.REINFORCED_DOOR.getKey());
        tag(BlockTags.DOORS).add(ModBlocks.REINFORCED_DOOR.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.IRON_FENCE.getKey());
        // Block tag only: oak fences share the wood class and draw their rail. Item is not fuel.
        tag(BlockTags.WOODEN_FENCES).add(ModBlocks.IRON_FENCE.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.IRON_SCAFFOLD.getKey());
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.WOODEN_SCAFFOLD.getKey());
        // Climb is JAR entityInside (forward boost), not ladder CLIMBABLE.

        tag(BlockTags.LOGS).add(ModBlocks.RUBBER_LOG.getKey());
        tag(BlockItemTags.LOGS_THAT_BURN.block()).add(ModBlocks.RUBBER_LOG.getKey());
        tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(ModBlocks.RUBBER_LOG.getKey());
        tag(BlockTags.LEAVES).add(ModBlocks.RUBBER_LEAVES.getKey());
        tag(BlockItemTags.SAPLINGS.block()).add(ModBlocks.RUBBER_SAPLING.getKey());
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.RUBBER_LOG.getKey());
        tag(BlockTags.MINEABLE_WITH_HOE).add(ModBlocks.RUBBER_LEAVES.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.IMPROVED_FURNACE.getKey());
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES).add(ModBlocks.IMPROVED_FURNACE.getKey());
        tag(OldustryTags.Blocks.FURNACES_IRON).add(ModBlocks.IMPROVED_FURNACE.getKey());
        tag(OldustryTags.Blocks.FURNACES).addTag(OldustryTags.Blocks.FURNACES_IRON);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PERSONAL_SAFE.getKey());
        tag(BlockTags.DRAGON_IMMUNE).add(ModBlocks.PERSONAL_SAFE.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.MACHINE_CASING.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ADVANCED_MACHINE.getKey());
        for (var holder : ModBlocks.BLOCKS.getEntries()) {
            if (holder.value() instanceof com.oldustry.block.MachineBlock || holder.value() instanceof com.oldustry.block.CableBlock) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(holder.getKey());
            }
        }
    }
}
