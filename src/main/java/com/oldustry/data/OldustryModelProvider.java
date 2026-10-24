package com.oldustry.data;

import com.oldustry.client.render.SaberFlicker;

import java.util.HashMap;
import java.util.Map;

import com.oldustry.Oldustry;
import com.oldustry.data.model.OldustryCableModels;
import com.oldustry.data.model.OldustryMachineModels;
import com.oldustry.block.CableBlock;
import com.oldustry.block.PersonalSafeBlock;
import com.oldustry.client.render.PersonalSafeRenderer;
import com.oldustry.block.DynamiteBlock;
import com.oldustry.block.MachineBlock;
import com.oldustry.block.OldustryLuminatorBlock;
import com.oldustry.block.MachineCasingBlock;
import com.oldustry.block.RubberLogBlock;
import com.oldustry.block.ScaffoldBlock;
import com.oldustry.energy.CableKind;
import com.oldustry.machine.OldustryMachineKind;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.Direction;
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
import net.minecraft.client.data.models.model.ItemModelUtils;
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
        return super.getKnownBlocks().filter(holder -> holder.value() != OldustryBlocks.REINFORCED_GLASS.get());
    }

    @Override
    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() != OldustryItems.REINFORCED_GLASS.get());
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        cube(blockModels, OldustryBlocks.TIN_ORE.get());
        cube(blockModels, OldustryBlocks.URANIUM_ORE.get());
        cube(blockModels, OldustryBlocks.TIN_BLOCK.get());
        cube(blockModels, OldustryBlocks.BRONZE_BLOCK.get());
        cube(blockModels, OldustryBlocks.URANIUM_BLOCK.get());
        cube(blockModels, OldustryBlocks.REINFORCED_STONE.get());
        scaffold(blockModels, OldustryBlocks.WOODEN_SCAFFOLD.get());
        scaffold(blockModels, OldustryBlocks.IRON_SCAFFOLD.get());
        blockModels.createTrivialBlock(
            OldustryBlocks.RUBBER_SHEET.get(),
            TexturedModel.createDefault(
                TextureMapping::defaultTexture,
                ModelTemplates.create("snow_height2", TextureSlot.TEXTURE, TextureSlot.PARTICLE)
            )
        );
        blockModels.createTrivialBlock(
            OldustryBlocks.RESIN_SHEET.get(),
            TexturedModel.createDefault(
                TextureMapping::defaultTexture,
                ModelTemplates.create("snow_height2", TextureSlot.TEXTURE, TextureSlot.PARTICLE)
            )
        );
        blockModels.createDoor(OldustryBlocks.REINFORCED_DOOR.get());
        ironFence(blockModels);

        rubberLog(blockModels, OldustryBlocks.RUBBER_LOG.get());
        // Same inventory tint as oak leaves (1.5.2 foliage green).
        blockModels.createTintedLeaves(OldustryBlocks.RUBBER_LEAVES.get(), TexturedModel.LEAVES, -12012264);
        blockModels.createCrossBlockWithDefaultItem(OldustryBlocks.RUBBER_SAPLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);
        blockModels.createFurnace(OldustryBlocks.IMPROVED_FURNACE.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        cube(blockModels, OldustryBlocks.CONSTRUCTION_FOAM.get());
        OldustryMachineModels.casing(blockModels, OldustryBlocks.MACHINE_CASING.get());
        OldustryMachineModels.casing(blockModels, OldustryBlocks.ADVANCED_MACHINE.get());
        OldustryMachineModels.miningPipe(blockModels, OldustryBlocks.MINING_PIPE.get(), true);
        OldustryMachineModels.miningTip(blockModels, OldustryBlocks.MINING_PIPE_TIP.get());
        personalSafe(blockModels);
        itnt(blockModels, OldustryBlocks.ITNT.get());
        itnt(blockModels, OldustryBlocks.NUKE.get());
        dynamite(blockModels, OldustryBlocks.DYNAMITE.get());

        for (var holder : OldustryBlocks.BLOCKS.getEntries()) {
            Block block = holder.value();
            if (block instanceof OldustryLuminatorBlock luminator) {
                OldustryMachineModels.luminator(blockModels, luminator);
            } else if (block instanceof MachineBlock machine) {
                OldustryMachineModels.machine(blockModels, machine);
            } else if (block instanceof CableBlock cable) {
                OldustryCableModels.cable(blockModels, itemModels, cable);
            }
        }

        for (DeferredHolder<Item, ? extends Item> item : OldustryItems.ITEMS.getEntries()) {
            if (item.get() instanceof BlockItem) {
                continue;
            }
            String path = item.getId().getPath();
            if (path.equals("nano_saber")) {
                nanoSaber(itemModels, item.get());
                continue;
            }
            if (handheldItem(path)) {
                itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
            } else {
                itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
            }
        }
    }

    /** Particle blockstate per facing. The world mesh is the block-entity renderer; the item uses the same closed model. */
    private static void personalSafe(BlockModelGenerators blockModels) {
        var block = OldustryBlocks.PERSONAL_SAFE.get();
        var particle = blockModels.createParticleOnlyBlockModel(block, block);
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(PersonalSafeBlock.FACING).generate(facing -> particle))
        );
        Identifier base = ModelTemplates.CHEST_INVENTORY.create(
            block.asItem(), TextureMapping.particle(block), blockModels.modelOutput
        );
        blockModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.specialModel(base, new PersonalSafeRenderer.Unbaked()));
    }

    private static void cube(BlockModelGenerators blockModels, Block block) {
        blockModels.createTrivialCube(block);
    }

    /** JAR cuboids: 4px post, rails at y 6–9 and 12–15. Same geometry as {@code fence_post} / {@code fence_side}. */
    private static void ironFence(BlockModelGenerators blockModels) {
        Block block = OldustryBlocks.IRON_FENCE.get();
        TextureMapping mapping = TextureMapping.defaultTexture(block);
        var post = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_POST.create(block, mapping, blockModels.modelOutput));
        var side = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_SIDE.create(block, mapping, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createFence(block, post, side));
        Identifier inventory = ModelTemplates.FENCE_INVENTORY.create(block, mapping, blockModels.modelOutput);
        blockModels.registerSimpleItemModel(block, inventory);
    }

    /** JAR {@code blockITNT.png} is 96×16. Facing south uses cells 0 bottom, 1 top, 2–5 the same side. */
    private static void itnt(BlockModelGenerators gens, Block block) {
        TextureMapping map = new TextureMapping()
            .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"))
            .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
        Identifier model = ModelTemplates.CUBE_BOTTOM_TOP.create(block, map, gens.modelOutput);
        gens.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
        gens.registerSimpleItemModel(block, model);
    }

    /** JAR render type 2 (torch). Wall model faces east when the support is west. Linked sticks use blockDynamiteRemote. */
    private static void dynamite(BlockModelGenerators gens, DynamiteBlock block) {
        TextureMapping plain = TextureMapping.torch(block);
        TextureMapping linkedTex = new TextureMapping().put(TextureSlot.TORCH, TextureMapping.getBlockTexture(block, "_remote"));
        var standing = BlockModelGenerators.plainVariant(ModelTemplates.TORCH.create(block, plain, gens.modelOutput));
        Identifier wallId = ModelTemplates.WALL_TORCH.createWithSuffix(block, "_wall", plain, gens.modelOutput);
        var standingLinked = BlockModelGenerators.plainVariant(ModelTemplates.TORCH.createWithSuffix(block, "_linked", linkedTex, gens.modelOutput));
        Identifier wallLinked = ModelTemplates.WALL_TORCH.createWithSuffix(block, "_linked_wall", linkedTex, gens.modelOutput);
        gens.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(
            PropertyDispatch.initial(DynamiteBlock.ANCHOR, DynamiteBlock.LINKED).generate((anchor, isLinked) -> {
                Identifier wall = isLinked ? wallLinked : wallId;
                return switch (anchor) {
                    case FLOOR -> isLinked ? standingLinked : standing;
                    case WEST -> BlockModelGenerators.plainVariant(wall);
                    case NORTH -> BlockModelGenerators.plainVariant(wall).with(BlockModelGenerators.Y_ROT_90);
                    case EAST -> BlockModelGenerators.plainVariant(wall).with(BlockModelGenerators.Y_ROT_180);
                    case SOUTH -> BlockModelGenerators.plainVariant(wall).with(BlockModelGenerators.Y_ROT_270);
                };
            })
        ));
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

    /** Off model. Active picks the cyan or magenta blade each draw, like the JAR icon. */
    private static void nanoSaber(ItemModelGenerators itemModels, Item item) {
        var off = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, ModelTemplates.FLAT_HANDHELD_ITEM));
        var blue = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_active", ModelTemplates.FLAT_HANDHELD_ITEM));
        var red = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_active_alt", ModelTemplates.FLAT_HANDHELD_ITEM));
        var lit = ItemModelUtils.rangeSelect(new com.oldustry.client.render.SaberFlicker(), blue, ItemModelUtils.override(red, 0.5F));
        itemModels.generateBooleanDispatch(item, ItemModelUtils.hasComponent(com.oldustry.registry.OldustryDataComponents.SABER_ACTIVE.get()), lit, off);
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
            || path.equals("ov_scanner")
            || path.equals("remote");
    }
}
