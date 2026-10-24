package com.oldustry.data;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;
import com.oldustry.registry.OldustryBlocks;
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
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.TIN_ORE.getKey());
        tag(BlockTags.NEEDS_STONE_TOOL).add(OldustryBlocks.TIN_ORE.getKey());
        tag(OldustryTags.Blocks.ORES_TIN).add(OldustryBlocks.TIN_ORE.getKey());
        tag(Tags.Blocks.ORES).add(OldustryBlocks.TIN_ORE.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.URANIUM_ORE.getKey());
        tag(BlockTags.NEEDS_IRON_TOOL).add(OldustryBlocks.URANIUM_ORE.getKey());
        tag(OldustryTags.Blocks.ORES_URANIUM).add(OldustryBlocks.URANIUM_ORE.getKey());
        tag(Tags.Blocks.ORES).add(OldustryBlocks.URANIUM_ORE.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.TIN_BLOCK.getKey());
        tag(BlockTags.NEEDS_STONE_TOOL).add(OldustryBlocks.TIN_BLOCK.getKey());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_TIN).add(OldustryBlocks.TIN_BLOCK.getKey());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(OldustryBlocks.TIN_BLOCK.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.BRONZE_BLOCK.getKey());
        tag(BlockTags.NEEDS_IRON_TOOL).add(OldustryBlocks.BRONZE_BLOCK.getKey());
        tag(BlockTags.BEACON_BASE_BLOCKS).add(OldustryBlocks.BRONZE_BLOCK.getKey());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_BRONZE).add(OldustryBlocks.BRONZE_BLOCK.getKey());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(OldustryBlocks.BRONZE_BLOCK.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.URANIUM_BLOCK.getKey());
        tag(BlockTags.NEEDS_IRON_TOOL).add(OldustryBlocks.URANIUM_BLOCK.getKey());
        tag(BlockTags.BEACON_BASE_BLOCKS).add(OldustryBlocks.URANIUM_BLOCK.getKey());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_URANIUM).add(OldustryBlocks.URANIUM_BLOCK.getKey());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(OldustryBlocks.URANIUM_BLOCK.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.REINFORCED_STONE.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.REINFORCED_GLASS.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.REINFORCED_DOOR.getKey());
        tag(BlockTags.DOORS).add(OldustryBlocks.REINFORCED_DOOR.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.IRON_FENCE.getKey());
        // Block tag only: oak fences share the wood class and draw their rail. Item is not fuel.
        tag(BlockTags.WOODEN_FENCES).add(OldustryBlocks.IRON_FENCE.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.IRON_SCAFFOLD.getKey());
        tag(BlockTags.MINEABLE_WITH_AXE).add(OldustryBlocks.WOODEN_SCAFFOLD.getKey());
        // Climb is JAR entityInside (forward boost), not ladder CLIMBABLE.

        tag(BlockTags.LOGS).add(OldustryBlocks.RUBBER_LOG.getKey());
        tag(BlockItemTags.LOGS_THAT_BURN.block()).add(OldustryBlocks.RUBBER_LOG.getKey());
        tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(OldustryBlocks.RUBBER_LOG.getKey());
        tag(BlockTags.LEAVES).add(OldustryBlocks.RUBBER_LEAVES.getKey());
        tag(BlockItemTags.SAPLINGS.block()).add(OldustryBlocks.RUBBER_SAPLING.getKey());
        tag(BlockTags.MINEABLE_WITH_AXE).add(OldustryBlocks.RUBBER_LOG.getKey());
        tag(BlockTags.MINEABLE_WITH_HOE).add(OldustryBlocks.RUBBER_LEAVES.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.IMPROVED_FURNACE.getKey());
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES).add(OldustryBlocks.IMPROVED_FURNACE.getKey());
        tag(OldustryTags.Blocks.FURNACES_IRON).add(OldustryBlocks.IMPROVED_FURNACE.getKey());
        tag(OldustryTags.Blocks.FURNACES).addTag(OldustryTags.Blocks.FURNACES_IRON);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.PERSONAL_SAFE.getKey());
        tag(BlockTags.DRAGON_IMMUNE).add(OldustryBlocks.PERSONAL_SAFE.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.MACHINE_CASING.getKey());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.ADVANCED_MACHINE.getKey());
        for (var holder : OldustryBlocks.BLOCKS.getEntries()) {
            if (holder.value() instanceof com.oldustry.block.MachineBlock || holder.value() instanceof com.oldustry.block.CableBlock) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(holder.getKey());
            }
        }
    }
}
