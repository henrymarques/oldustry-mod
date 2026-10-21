package com.oldustry.data;

import java.util.Set;

import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

public class OldustryBlockLoot extends BlockLootSubProvider {
    public OldustryBlockLoot(HolderLookup.Provider lookup) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookup);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(e -> (Block) e.value()).toList();
    }

    @Override
    protected void generate() {
        add(ModBlocks.TIN_ORE.get(), block -> createOreDrop(block, ModItems.RAW_TIN.get()));
        add(ModBlocks.URANIUM_ORE.get(), block -> createOreDrop(block, ModItems.URANIUM_DROP.get()));
        dropSelf(ModBlocks.TIN_BLOCK.get());
        dropSelf(ModBlocks.BRONZE_BLOCK.get());
        dropSelf(ModBlocks.URANIUM_BLOCK.get());
        dropSelf(ModBlocks.REINFORCED_STONE.get());
        dropSelf(ModBlocks.REINFORCED_GLASS.get());
        add(ModBlocks.REINFORCED_DOOR.get(), this::createDoorTable);
        dropSelf(ModBlocks.IRON_FENCE.get());
        dropSelf(ModBlocks.WOODEN_SCAFFOLD.get());
        dropSelf(ModBlocks.IRON_SCAFFOLD.get());
        dropSelf(ModBlocks.RUBBER_SHEET.get());
        dropSelf(ModBlocks.RESIN_SHEET.get());
        dropSelf(ModBlocks.RUBBER_LOG.get());
        add(ModBlocks.RUBBER_LEAVES.get(), block -> createLeavesDrops(block, ModBlocks.RUBBER_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(ModBlocks.RUBBER_SAPLING.get());
        dropSelf(ModBlocks.IMPROVED_FURNACE.get());
        add(ModBlocks.CONSTRUCTION_FOAM.get(), noDrop());
        add(ModBlocks.ITNT.get(), noDrop());
        dropSelf(ModBlocks.MACHINE_CASING.get());
        dropSelf(ModBlocks.ADVANCED_MACHINE.get());
        dropSelf(ModBlocks.MINING_PIPE.get());
        // Tip always drops as regular mining pipe (JAR).
        add(ModBlocks.MINING_PIPE_TIP.get(), block -> createSingleItemTable(ModItems.MINING_PIPE.get()));
        for (var block : ModBlocks.machines()) {
            dropSelf(block);
        }
        for (var block : ModBlocks.cables()) {
            dropSelf(block);
        }
    }
}
