package com.oldustry.data;

import java.util.concurrent.CompletableFuture;

import com.oldustry.Oldustry;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class OldustryBlockTags extends BlockTagsProvider {
    public OldustryBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, Oldustry.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.TIN_ORE.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(OldustryBlocks.TIN_ORE.get());
        tag(OldustryTags.Blocks.ORES_TIN).add(OldustryBlocks.TIN_ORE.get());
        tag(Tags.Blocks.ORES).add(OldustryBlocks.TIN_ORE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.URANIUM_ORE.get());
        tag(BlockTags.NEEDS_IRON_TOOL).add(OldustryBlocks.URANIUM_ORE.get());
        tag(OldustryTags.Blocks.ORES_URANIUM).add(OldustryBlocks.URANIUM_ORE.get());
        tag(Tags.Blocks.ORES).add(OldustryBlocks.URANIUM_ORE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.TIN_BLOCK.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(OldustryBlocks.TIN_BLOCK.get());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_TIN).add(OldustryBlocks.TIN_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(OldustryBlocks.TIN_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.BRONZE_BLOCK.get());
        tag(BlockTags.NEEDS_IRON_TOOL).add(OldustryBlocks.BRONZE_BLOCK.get());
        tag(BlockTags.BEACON_BASE_BLOCKS).add(OldustryBlocks.BRONZE_BLOCK.get());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_BRONZE).add(OldustryBlocks.BRONZE_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(OldustryBlocks.BRONZE_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.URANIUM_BLOCK.get());
        tag(BlockTags.NEEDS_IRON_TOOL).add(OldustryBlocks.URANIUM_BLOCK.get());
        tag(BlockTags.BEACON_BASE_BLOCKS).add(OldustryBlocks.URANIUM_BLOCK.get());
        tag(OldustryTags.Blocks.STORAGE_BLOCKS_URANIUM).add(OldustryBlocks.URANIUM_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(OldustryBlocks.URANIUM_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.REINFORCED_STONE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.REINFORCED_GLASS.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.REINFORCED_DOOR.get());
        tag(BlockTags.DOORS).add(OldustryBlocks.REINFORCED_DOOR.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.IRON_FENCE.get());
        // Block tag only: oak fences share the wood class and draw their rail. Item is not fuel.
        tag(BlockTags.WOODEN_FENCES).add(OldustryBlocks.IRON_FENCE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.IRON_SCAFFOLD.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(OldustryBlocks.WOODEN_SCAFFOLD.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(OldustryBlocks.BARREL.get());
        // Climb is JAR entityInside (forward boost), not ladder CLIMBABLE.

        tag(BlockTags.LOGS).add(OldustryBlocks.RUBBER_LOG.get());
        tag(BlockTags.LOGS_THAT_BURN).add(OldustryBlocks.RUBBER_LOG.get());
        tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(OldustryBlocks.RUBBER_LOG.get());
        tag(BlockTags.LEAVES).add(OldustryBlocks.RUBBER_LEAVES.get());
        tag(BlockTags.SAPLINGS).add(OldustryBlocks.RUBBER_SAPLING.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(OldustryBlocks.RUBBER_LOG.get());
        tag(BlockTags.MINEABLE_WITH_HOE).add(OldustryBlocks.RUBBER_LEAVES.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.IMPROVED_FURNACE.get());
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES).add(OldustryBlocks.IMPROVED_FURNACE.get());
        tag(OldustryTags.Blocks.FURNACES_IRON).add(OldustryBlocks.IMPROVED_FURNACE.get());
        tag(OldustryTags.Blocks.FURNACES).addTag(OldustryTags.Blocks.FURNACES_IRON);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.PERSONAL_SAFE.get());
        tag(BlockTags.DRAGON_IMMUNE).add(OldustryBlocks.PERSONAL_SAFE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.NUCLEAR_REACTOR.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.REACTOR_CHAMBER.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.MACHINE_CASING.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OldustryBlocks.ADVANCED_MACHINE.get());
        for (var holder : OldustryBlocks.BLOCKS.getEntries()) {
            if (holder.value() instanceof com.oldustry.block.MachineBlock || holder.value() instanceof com.oldustry.block.CableBlock) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(holder.value());
            }
        }
    }
}
