package com.oldustry.data.model;

import com.oldustry.Oldustry;
import com.oldustry.block.CableBlock;
import com.oldustry.energy.CableKind;
import com.oldustry.registry.OldustryBlocks;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

public final class OldustryCableModels {
    public static void cable(BlockModelGenerators gens, ItemModelGenerators itemModels, CableBlock block) {
        int t = block.kind.thickness;
        boolean stateful = block.kind == CableKind.DETECTOR || block.kind == CableKind.SPLITTER;
        ModelTemplate coreT = new ModelTemplate(
            java.util.Optional.of(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/cable_core_" + t)),
            java.util.Optional.of("_core"),
            TextureSlot.ALL
        );
        ModelTemplate armT = new ModelTemplate(
            java.util.Optional.of(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/cable_arm_" + t)),
            java.util.Optional.of("_arm"),
            TextureSlot.ALL
        );
        ModelTemplate coreOnT = new ModelTemplate(
            java.util.Optional.of(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/cable_core_" + t)),
            java.util.Optional.of("_core_on"),
            TextureSlot.ALL
        );
        ModelTemplate armOnT = new ModelTemplate(
            java.util.Optional.of(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/cable_arm_" + t)),
            java.util.Optional.of("_arm_on"),
            TextureSlot.ALL
        );
        TextureMapping map = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block));
        Identifier core = coreT.create(block, map, gens.modelOutput);
        Identifier arm = armT.create(block, map, gens.modelOutput);
        Identifier foam = ModelTemplates.CUBE_ALL.createWithSuffix(
            block,
            "_foam",
            TextureMapping.cube(OldustryBlocks.CONSTRUCTION_FOAM.get()),
            gens.modelOutput
        );
        Identifier hardened = ModelTemplates.CUBE_ALL.createWithSuffix(
            block,
            "_hard",
            TextureMapping.cube(Blocks.CONCRETE.pick(net.minecraft.world.item.DyeColor.LIGHT_GRAY)),
            gens.modelOutput
        );
        var coreV = BlockModelGenerators.plainVariant(core);
        var armV = BlockModelGenerators.plainVariant(arm);
        MultiPartGenerator multipart = MultiPartGenerator.multiPart(block);
        if (stateful) {
            TextureMapping onMap = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block, "_on"));
            Identifier coreOn = coreOnT.create(block, onMap, gens.modelOutput);
            Identifier armOn = armOnT.create(block, onMap, gens.modelOutput);
            var coreOnV = BlockModelGenerators.plainVariant(coreOn);
            var armOnV = BlockModelGenerators.plainVariant(armOn);
            // JAR 32x16 sheet: left=inactive, right=active (BlockTextureStitched count==2).
            multipart
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, false), coreV)
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, true), coreOnV)
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, false).term(CableBlock.NORTH, true), armV)
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, true).term(CableBlock.NORTH, true), armOnV)
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, false).term(CableBlock.SOUTH, true), armV.with(BlockModelGenerators.Y_ROT_180))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, true).term(CableBlock.SOUTH, true), armOnV.with(BlockModelGenerators.Y_ROT_180))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, false).term(CableBlock.EAST, true), armV.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, true).term(CableBlock.EAST, true), armOnV.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, false).term(CableBlock.WEST, true), armV.with(BlockModelGenerators.Y_ROT_270))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, true).term(CableBlock.WEST, true), armOnV.with(BlockModelGenerators.Y_ROT_270))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, false).term(CableBlock.UP, true), armV.with(BlockModelGenerators.X_ROT_270))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, true).term(CableBlock.UP, true), armOnV.with(BlockModelGenerators.X_ROT_270))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, false).term(CableBlock.DOWN, true), armV.with(BlockModelGenerators.X_ROT_90))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.ACTIVE, true).term(CableBlock.DOWN, true), armOnV.with(BlockModelGenerators.X_ROT_90));
        } else {
            multipart
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0), coreV)
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.NORTH, true), armV)
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.SOUTH, true), armV.with(BlockModelGenerators.Y_ROT_180))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.EAST, true), armV.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.WEST, true), armV.with(BlockModelGenerators.Y_ROT_270))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.UP, true), armV.with(BlockModelGenerators.X_ROT_270))
                .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 0).term(CableBlock.DOWN, true), armV.with(BlockModelGenerators.X_ROT_90));
        }
        multipart
            .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 1), BlockModelGenerators.plainVariant(foam))
            .with(BlockModelGenerators.condition().term(CableBlock.FOAM, 2), BlockModelGenerators.plainVariant(hardened));
        gens.blockStateOutput.accept(multipart);
        // 1.5.2 used flat itemCable* icons in inventory, not the thin 3D core.
        itemModels.generateFlatItem(block.asItem(), ModelTemplates.FLAT_ITEM);
    }

}
