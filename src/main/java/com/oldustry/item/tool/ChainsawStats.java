package com.oldustry.item.tool;

import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * JAR ItemElectricToolChainsaw: iron tool class, efficiency 12 on axe set + leaves/wool/vines
 * ({@code Material.leaves} harvest + mineableBlocks), EU 100 / 10k / transfer 100.
 * Desvio speed 15. Behaviour = axe + shears ({@code IShearable} / {@code SHEARS_DIG}).
 */
public final class ChainsawStats {
    public static final float SPEED = 15f;
    /** JAR operationEnergyCost. */
    public static final int EU = 100;
    public static final int ENCHANT = 14;
    /** Iron axe baselines — JAR hit for 9 (iron-axe class). */
    public static final float ATTACK_DAMAGE = 6f;
    public static final float ATTACK_SPEED = -3.1f;

    public static Item.Properties properties(Item.Properties props, ToolMaterial material) {
        HolderGetter<Block> blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return props
            .axe(material, ATTACK_DAMAGE, ATTACK_SPEED)
            .component(
                DataComponents.TOOL,
                new Tool(
                    List.of(
                        Tool.Rule.deniesDrops(blocks.getOrThrow(material.incorrectBlocksForDrops())),
                        Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_AXE), material.speed()),
                        // JAR flat efficiency on leaves/wool/vines (IShearable + Material.leaves).
                        Tool.Rule.minesAndDrops(HolderSet.direct(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.COBWEB)), SPEED),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SHEARS_EXTREME_BREAKING_SPEED), SPEED),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SHEARS_MAJOR_BREAKING_SPEED), SPEED),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SHEARS_MINOR_BREAKING_SPEED), SPEED)
                    ),
                    1.0F,
                    0,
                    true
                )
            )
            .enchantable(ENCHANT);
    }

    private ChainsawStats() {}
}
