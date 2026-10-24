package com.oldustry.item.tool;

import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

/**
 * Desvio speed 10 / 20 (JAR was 8 / 16). EU cost stays JAR: 50 / 80; buf 10k transfer 100.
 * JAR ItemElectricToolDrill: pickaxe + shovel mineable — both tags here.
 */
public final class DrillStats {
    public static final float MINING_SPEED = 10f;
    public static final float DIAMOND_SPEED = 20f;
    /** JAR ItemElectricToolDrill operationEnergyCost. */
    public static final int MINING_EU = 50;
    /** JAR ItemElectricToolDDrill operationEnergyCost. */
    public static final int DIAMOND_EU = 80;
    public static final int MINING_ENCHANT = 14;
    public static final int DIAMOND_ENCHANT = 10;

    /** Pickaxe baseline attrs + TOOL rules for pickaxe and shovel (JAR dual-tool). */
    public static Item.Properties properties(Item.Properties props, ToolMaterial material, float attackDamage, float attackSpeed) {
        HolderGetter<Block> blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return props
            .pickaxe(material, attackDamage, attackSpeed)
            .component(
                DataComponents.TOOL,
                new Tool(
                    List.of(
                        Tool.Rule.deniesDrops(blocks.getOrThrow(material.incorrectBlocksForDrops())),
                        Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_PICKAXE), material.speed()),
                        Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_SHOVEL), material.speed())
                    ),
                    1.0F,
                    0,
                    true
                )
            );
    }

    private DrillStats() {}
}
