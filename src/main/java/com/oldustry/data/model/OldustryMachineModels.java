package com.oldustry.data.model;

import java.util.HashMap;
import java.util.Map;

import com.oldustry.Oldustry;
import com.oldustry.block.MachineBlock;
import com.oldustry.block.OldustryLuminatorBlock;

import net.minecraft.core.Direction;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public final class OldustryMachineModels {
    public static void casing(BlockModelGenerators gens, Block block) {
        Identifier model = ModelTemplates.CUBE.create(block, cubeMap(block, ""), gens.modelOutput);
        gens.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
        gens.registerSimpleItemModel(block, model);
    }

    public static void machine(BlockModelGenerators gens, MachineBlock block) {
        Identifier off = ModelTemplates.CUBE.create(block, cubeMap(block, ""), gens.modelOutput);
        Identifier on = ModelTemplates.CUBE.createWithSuffix(block, "_on", cubeMap(block, "_on"), gens.modelOutput);
        gens.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block)
                .with(BlockModelGenerators.createBooleanModelDispatch(
                    MachineBlock.ACTIVE,
                    BlockModelGenerators.plainVariant(on),
                    BlockModelGenerators.plainVariant(off)
                ))
                .with(BlockModelGenerators.ROTATION_FACING)
        );
        gens.registerSimpleItemModel(block, off);
    }

    public static TextureMapping cubeMap(Block block, String suffix) {
        // IC2 atlas tile 3 (south/front) maps to model NORTH so furnace Y-rot matches FACING.
        return new TextureMapping()
            .put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block, suffix + "_down"))
            .put(TextureSlot.UP, TextureMapping.getBlockTexture(block, suffix + "_up"))
            .put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, suffix + "_south"))
            .put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block, suffix + "_north"))
            .put(TextureSlot.WEST, TextureMapping.getBlockTexture(block, suffix + "_east"))
            .put(TextureSlot.EAST, TextureMapping.getBlockTexture(block, suffix + "_west"))
            .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, suffix + "_south"));
    }

    public static void luminator(BlockModelGenerators gens, OldustryLuminatorBlock block) {
        Map<String, Identifier> models = new HashMap<>();
        PropertyDispatch<net.minecraft.client.data.models.MultiVariant> facing = PropertyDispatch.initial(MachineBlock.FACING, MachineBlock.ACTIVE, OldustryLuminatorBlock.COVER)
            .generate((dir, active, cover) -> {
                // Floor plate is always the full sheet. JAR ignores fullCover when facing up,
                // so cover true/false share one model and must be created only once.
                boolean sheet = cover && dir != Direction.UP;
                String shape = dir.getSerializedName() + (sheet ? "_cover" : "");
                String suffix = "_" + shape + (active ? "_on" : "_off");
                Identifier model = models.computeIfAbsent(suffix, key -> {
                    Identifier parent = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/luminator_" + shape);
                    TextureMapping map = new TextureMapping().put(
                        TextureSlot.ALL,
                        active ? TextureMapping.getBlockTexture(block) : TextureMapping.getBlockTexture(block, "_dark")
                    );
                    ModelTemplate template = new ModelTemplate(
                        java.util.Optional.of(parent),
                        java.util.Optional.of(suffix),
                        TextureSlot.ALL
                    );
                    return template.create(block, map, gens.modelOutput);
                });
                return BlockModelGenerators.plainVariant(model);
            });
        gens.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(facing));
        // JAR RenderBlockLuminator.renderInventoryBlock: column x/z 0.375–0.625, full height,
        // dark texture (Ic2Items.luminator is blockLuminatorDark).
        gens.registerSimpleItemModel(block, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/luminator_column"));
    }

    public static void miningPipe(BlockModelGenerators gens, Block block, boolean item) {
        ModelTemplate column = new ModelTemplate(
            java.util.Optional.of(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/mining_pipe_column")),
            java.util.Optional.empty(),
            TextureSlot.ALL
        );
        Identifier model = column.create(block, new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block)), gens.modelOutput);
        gens.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
        // JAR RenderBlockMiningPipe.renderInventoryBlock is the same 4×16 column.
        if (item) {
            gens.registerSimpleItemModel(block, model);
        }
    }

    /** JAR BlockMiningTip uses the default cube renderer (blockMiningTip), not the pipe column. */
    public static void miningTip(BlockModelGenerators gens, Block block) {
        Identifier model = ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(block), gens.modelOutput);
        gens.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
    }

}
