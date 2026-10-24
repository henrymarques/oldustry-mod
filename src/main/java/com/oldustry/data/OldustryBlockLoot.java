package com.oldustry.data;

import java.util.Set;

import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryItems;

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
        return OldustryBlocks.BLOCKS.getEntries().stream().map(e -> (Block) e.value()).toList();
    }

    @Override
    protected void generate() {
        add(OldustryBlocks.TIN_ORE.get(), block -> createOreDrop(block, OldustryItems.RAW_TIN.get()));
        add(OldustryBlocks.URANIUM_ORE.get(), block -> createOreDrop(block, OldustryItems.URANIUM_DROP.get()));
        dropSelf(OldustryBlocks.TIN_BLOCK.get());
        dropSelf(OldustryBlocks.BRONZE_BLOCK.get());
        dropSelf(OldustryBlocks.URANIUM_BLOCK.get());
        dropSelf(OldustryBlocks.REINFORCED_STONE.get());
        dropSelf(OldustryBlocks.REINFORCED_GLASS.get());
        add(OldustryBlocks.REINFORCED_DOOR.get(), this::createDoorTable);
        dropSelf(OldustryBlocks.IRON_FENCE.get());
        dropSelf(OldustryBlocks.WOODEN_SCAFFOLD.get());
        dropSelf(OldustryBlocks.IRON_SCAFFOLD.get());
        dropSelf(OldustryBlocks.RUBBER_SHEET.get());
        dropSelf(OldustryBlocks.RESIN_SHEET.get());
        dropSelf(OldustryBlocks.RUBBER_LOG.get());
        add(OldustryBlocks.RUBBER_LEAVES.get(), block -> createLeavesDrops(block, OldustryBlocks.RUBBER_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(OldustryBlocks.RUBBER_SAPLING.get());
        dropSelf(OldustryBlocks.IMPROVED_FURNACE.get());
        add(OldustryBlocks.CONSTRUCTION_FOAM.get(), noDrop());
        add(OldustryBlocks.ITNT.get(), noDrop());
        add(OldustryBlocks.NUKE.get(), noDrop());
        add(OldustryBlocks.DYNAMITE.get(), noDrop());
        dropSelf(OldustryBlocks.PERSONAL_SAFE.get());
        dropSelf(OldustryBlocks.MACHINE_CASING.get());
        dropSelf(OldustryBlocks.ADVANCED_MACHINE.get());
        dropSelf(OldustryBlocks.MINING_PIPE.get());
        // Tip always drops as regular mining pipe (JAR).
        add(OldustryBlocks.MINING_PIPE_TIP.get(), block -> createSingleItemTable(OldustryItems.MINING_PIPE.get()));
        for (var block : OldustryBlocks.machines()) {
            dropSelf(block);
        }
        for (var block : OldustryBlocks.cables()) {
            dropSelf(block);
        }
    }
}
