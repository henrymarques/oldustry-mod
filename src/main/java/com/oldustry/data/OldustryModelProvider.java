package com.oldustry.data;

import com.oldustry.Oldustry;
import com.oldustry.block.CableBlock;
import com.oldustry.block.MachineBlock;
import com.oldustry.block.MachineCasingBlock;
import com.oldustry.block.RubberLogBlock;
import com.oldustry.block.ScaffoldBlock;
import com.oldustry.energy.CableKind;
import com.oldustry.machine.MachineKind;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModItems;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;

public class OldustryModelProvider extends ModelProvider {
    public OldustryModelProvider(PackOutput output) {
        super(output, Oldustry.MOD_ID);
    }

    @Override
    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Block>> getKnownBlocks() {
        return super.getKnownBlocks().filter(holder -> holder.value() != ModBlocks.REINFORCED_GLASS.get());
    }

    @Override
    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() != ModItems.REINFORCED_GLASS.get());
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        cube(blockModels, ModBlocks.TIN_ORE.get());
        cube(blockModels, ModBlocks.URANIUM_ORE.get());
        cube(blockModels, ModBlocks.TIN_BLOCK.get());
        cube(blockModels, ModBlocks.BRONZE_BLOCK.get());
        cube(blockModels, ModBlocks.URANIUM_BLOCK.get());
        cube(blockModels, ModBlocks.REINFORCED_STONE.get());
        scaffold(blockModels, ModBlocks.WOODEN_SCAFFOLD.get());
        scaffold(blockModels, ModBlocks.IRON_SCAFFOLD.get());
        blockModels.createTrivialBlock(
            ModBlocks.RUBBER_SHEET.get(),
            TexturedModel.createDefault(
                TextureMapping::defaultTexture,
                ModelTemplates.create("snow_height2", TextureSlot.TEXTURE, TextureSlot.PARTICLE)
            )
        );
        blockModels.createTrivialBlock(
            ModBlocks.RESIN_SHEET.get(),
            TexturedModel.createDefault(
                TextureMapping::defaultTexture,
                ModelTemplates.create("snow_height2", TextureSlot.TEXTURE, TextureSlot.PARTICLE)
            )
        );
        blockModels.createDoor(ModBlocks.REINFORCED_DOOR.get());
        blockModels.createBarsAndItem(ModBlocks.IRON_FENCE.get());

        rubberLog(blockModels, ModBlocks.RUBBER_LOG.get());
        // Same inventory tint as oak leaves (1.5.2 foliage green).
        blockModels.createTintedLeaves(ModBlocks.RUBBER_LEAVES.get(), TexturedModel.LEAVES, -12012264);
        blockModels.createCrossBlockWithDefaultItem(ModBlocks.RUBBER_SAPLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);
        blockModels.createFurnace(ModBlocks.IMPROVED_FURNACE.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        cube(blockModels, ModBlocks.CONSTRUCTION_FOAM.get());
        casing(blockModels, ModBlocks.MACHINE_CASING.get());
        casing(blockModels, ModBlocks.ADVANCED_MACHINE.get());
        miningPipe(blockModels, itemModels, ModBlocks.MINING_PIPE.get());
        miningPipe(blockModels, itemModels, ModBlocks.MINING_PIPE_TIP.get());

        for (var holder : ModBlocks.BLOCKS.getEntries()) {
            Block block = holder.value();
            if (block instanceof MachineBlock machine) {
                if (machine.kind.type == MachineKind.Type.LUMINATOR) {
                    luminator(blockModels, itemModels, machine);
                } else {
                    machine(blockModels, machine);
                }
            } else if (block instanceof CableBlock cable) {
                cable(blockModels, itemModels, cable);
            }
        }

        for (DeferredHolder<Item, ? extends Item> item : ModItems.ITEMS.getEntries()) {
            if (item.get() instanceof BlockItem) {
                continue;
            }
            String path = item.getId().getPath();
            if (handheldItem(path)) {
                itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
            } else {
                itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
            }
        }
    }

    private static void cube(BlockModelGenerators blockModels, Block block) {
        blockModels.createTrivialCube(block);
    }

    private static void casing(BlockModelGenerators gens, Block block) {
        Identifier model = ModelTemplates.CUBE.create(block, cubeMap(block, ""), gens.modelOutput);
        gens.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
        gens.registerSimpleItemModel(block, model);
    }

    private static void machine(BlockModelGenerators gens, MachineBlock block) {
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

    private static TextureMapping cubeMap(Block block, String suffix) {
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

    private static void luminator(BlockModelGenerators gens, ItemModelGenerators itemModels, MachineBlock block) {
        PropertyDispatch<net.minecraft.client.data.models.MultiVariant> facing = PropertyDispatch.initial(MachineBlock.FACING, MachineBlock.ACTIVE)
            .generate((dir, active) -> {
                Identifier parent = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/luminator_" + dir.getSerializedName());
                TextureMapping map = new TextureMapping().put(
                    TextureSlot.ALL,
                    active ? TextureMapping.getBlockTexture(block) : TextureMapping.getBlockTexture(block, "_dark")
                );
                ModelTemplate template = new ModelTemplate(
                    java.util.Optional.of(parent),
                    java.util.Optional.of("_" + dir.getSerializedName() + (active ? "_on" : "_off")),
                    TextureSlot.ALL
                );
                return BlockModelGenerators.plainVariant(template.create(block, map, gens.modelOutput));
            });
        gens.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(facing));
        // IC2 inventory icon (items/luminator.png), not the 3D plate mesh.
        itemModels.generateFlatItem(block.asItem(), ModelTemplates.FLAT_ITEM);
    }

    private static void miningPipe(BlockModelGenerators gens, ItemModelGenerators itemModels, Block block) {
        ModelTemplate column = new ModelTemplate(
            java.util.Optional.of(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "block/mining_pipe_column")),
            java.util.Optional.empty(),
            TextureSlot.ALL
        );
        Identifier model = column.create(block, new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block)), gens.modelOutput);
        gens.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
        // Flat item icon (textures/item/mining_pipe*.png), not the thin 3D column.
        itemModels.generateFlatItem(block.asItem(), ModelTemplates.FLAT_ITEM);
    }

    private static void cable(BlockModelGenerators gens, ItemModelGenerators itemModels, CableBlock block) {
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
            TextureMapping.cube(ModBlocks.CONSTRUCTION_FOAM.get()),
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

    private static void scaffold(BlockModelGenerators gens, ScaffoldBlock block) {
        Identifier normal = ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(block), gens.modelOutput);
        Identifier reinforced = ModelTemplates.CUBE_ALL.createWithSuffix(
            block,
            "_reinforced",
            new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block, "_reinforced")),
            gens.modelOutput
        );
        int high = block.reinforcedStrength();
        gens.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(ScaffoldBlock.STRENGTH).generate(s ->
                BlockModelGenerators.plainVariant(s >= high ? reinforced : normal)
            ))
        );
        gens.registerSimpleItemModel(block, normal);
    }

    private static void rubberLog(BlockModelGenerators gens, Block block) {
        Identifier column = TexturedModel.COLUMN.create(block, gens.modelOutput);
        TextureMapping wet = new TextureMapping()
            .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
            .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_wet"))
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
        TextureMapping dry = new TextureMapping()
            .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
            .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_dry"))
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
        Identifier wetModel = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_wet", wet, gens.modelOutput);
        Identifier dryModel = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_dry", dry, gens.modelOutput);
        gens.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(RubberLogBlock.TAP_FACE, RubberLogBlock.WET).generate((face, isWet) -> {
                    if (face == RubberLogBlock.TapFace.NONE) {
                        return BlockModelGenerators.plainVariant(column);
                    }
                    var model = BlockModelGenerators.plainVariant(isWet ? wetModel : dryModel);
                    return switch (face) {
                        case EAST -> model.with(BlockModelGenerators.Y_ROT_90);
                        case SOUTH -> model.with(BlockModelGenerators.Y_ROT_180);
                        case WEST -> model.with(BlockModelGenerators.Y_ROT_270);
                        default -> model;
                    };
                }))
                .with(BlockModelGenerators.createRotatedPillar())
        );
    }

    /** Tools held like vanilla pickaxe/sword (not inventory-flat `generated`). */
    private static boolean handheldItem(String path) {
        return path.endsWith("_pickaxe")
            || path.endsWith("_axe")
            || path.endsWith("_sword")
            || path.endsWith("_shovel")
            || path.endsWith("_hoe")
            || path.endsWith("_drill")
            || path.equals("chainsaw")
            || path.equals("wrench")
            || path.equals("electric_wrench")
            || path.equals("cutter")
            || path.equals("treetap")
            || path.equals("electric_treetap")
            || path.equals("cf_sprayer")
            || path.equals("od_scanner")
            || path.equals("ov_scanner");
    }
}
