package com.oldustry.registry;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.oldustry.Oldustry;
import com.oldustry.block.CableBlock;
import com.oldustry.block.FoamBlock;
import com.oldustry.block.IronFenceBlock;
import com.oldustry.block.MachineBlock;
import com.oldustry.block.MachineCasingBlock;
import com.oldustry.block.MiningPipeBlock;
import com.oldustry.block.ResinSheetBlock;
import com.oldustry.block.RubberLogBlock;
import com.oldustry.block.RubberSheetBlock;
import com.oldustry.block.ScaffoldBlock;
import com.oldustry.energy.CableKind;
import com.oldustry.furnace.ImprovedFurnaceBlock;
import com.oldustry.machine.MachineKind;
import com.oldustry.world.OldustryWorldgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Oldustry.MOD_ID);

    public static final DeferredBlock<Block> TIN_ORE = cube("tin_ore", p -> p.mapColor(MapColor.STONE).strength(3, 5).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> URANIUM_ORE = cube("uranium_ore", p -> p.mapColor(MapColor.COLOR_LIGHT_GREEN).strength(3, 5).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> TIN_BLOCK = cube("tin_block", p -> p.mapColor(MapColor.METAL).strength(5, 6).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> BRONZE_BLOCK = cube("bronze_block", p -> p.mapColor(MapColor.COLOR_ORANGE).strength(5, 6).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> URANIUM_BLOCK = cube("uranium_block", p -> p.mapColor(MapColor.COLOR_LIGHT_GREEN).strength(5, 6).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<Block> REINFORCED_STONE = cube("reinforced_stone", p -> p.mapColor(MapColor.STONE).strength(80, 150).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> REINFORCED_GLASS = typed("reinforced_glass", TransparentBlock::new, p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5, 150).sound(SoundType.GLASS).noOcclusion().isViewBlocking((s, l, pos) -> false).isSuffocating((s, l, pos) -> false));
    public static final DeferredBlock<DoorBlock> REINFORCED_DOOR = BLOCKS.registerBlock(
        "reinforced_door",
        props -> new DoorBlock(BlockSetType.IRON, props),
        p -> p.mapColor(MapColor.STONE).strength(80, 150).noOcclusion().pushReaction(PushReaction.DESTROY)
    );

    public static final DeferredBlock<IronFenceBlock> IRON_FENCE = BLOCKS.registerBlock(
        "iron_fence",
        IronFenceBlock::new,
        p -> p.mapColor(MapColor.METAL).strength(5, 10).sound(SoundType.METAL).noOcclusion()
    );

    public static final DeferredBlock<ScaffoldBlock> WOODEN_SCAFFOLD = BLOCKS.registerBlock(
        "wooden_scaffold",
        props -> new ScaffoldBlock(false, props),
        p -> p.mapColor(MapColor.WOOD).strength(0.5f).sound(SoundType.WOOD).noOcclusion()
    );
    public static final DeferredBlock<ScaffoldBlock> IRON_SCAFFOLD = BLOCKS.registerBlock(
        "iron_scaffold",
        props -> new ScaffoldBlock(true, props),
        p -> p.mapColor(MapColor.METAL).strength(0.8f).sound(SoundType.METAL).noOcclusion()
    );
    public static final DeferredBlock<RubberSheetBlock> RUBBER_SHEET = BLOCKS.registerBlock(
        "rubber_sheet",
        RubberSheetBlock::new,
        p -> p.mapColor(MapColor.COLOR_BLACK).strength(0.8f).sound(SoundType.WOOL).noOcclusion().bounceRestitution(0.8f)
    );
    public static final DeferredBlock<ResinSheetBlock> RESIN_SHEET = BLOCKS.registerBlock(
        "resin_sheet",
        ResinSheetBlock::new,
        p -> p.mapColor(MapColor.COLOR_ORANGE).strength(0.5f).sound(SoundType.SLIME_BLOCK).noOcclusion()
    );

    public static final DeferredBlock<RubberLogBlock> RUBBER_LOG = BLOCKS.registerBlock(
        "rubber_log",
        RubberLogBlock::new,
        p -> p.mapColor(MapColor.WOOD).strength(1).sound(SoundType.WOOD).ignitedByLava()
    );
    public static final DeferredBlock<TintedParticleLeavesBlock> RUBBER_LEAVES = BLOCKS.registerBlock(
        "rubber_leaves",
        props -> new TintedParticleLeavesBlock(0.01f, props),
        p -> p.mapColor(MapColor.PLANT).strength(0.2f).sound(SoundType.GRASS).noOcclusion().isSuffocating((s, l, pos) -> false).isViewBlocking((s, l, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor((s, l, pos) -> false)
    );
    public static final DeferredBlock<SaplingBlock> RUBBER_SAPLING = BLOCKS.registerBlock(
        "rubber_sapling",
        props -> new SaplingBlock(OldustryWorldgen.RUBBER_GROWER, props),
        p -> p.mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY)
    );

    public static final DeferredBlock<ImprovedFurnaceBlock> IMPROVED_FURNACE = BLOCKS.registerBlock(
        "improved_furnace",
        ImprovedFurnaceBlock::new,
        p -> p.mapColor(MapColor.STONE).strength(3.5f).requiresCorrectToolForDrops().lightLevel(s -> s.getValue(net.minecraft.world.level.block.AbstractFurnaceBlock.LIT) ? 13 : 0)
    );

    public static final DeferredBlock<FoamBlock> CONSTRUCTION_FOAM = BLOCKS.registerBlock(
        "construction_foam",
        FoamBlock::new,
        p -> p.mapColor(MapColor.WOOL).strength(0.01f, 10).sound(SoundType.WOOL).noOcclusion().noCollision().forceSolidOn()
    );

    public static final DeferredBlock<MachineCasingBlock> MACHINE_CASING = typed(
        "machine_casing",
        MachineCasingBlock::new,
        p -> p.mapColor(MapColor.METAL).strength(3, 10).requiresCorrectToolForDrops().sound(SoundType.METAL)
    );
    public static final DeferredBlock<MachineCasingBlock> ADVANCED_MACHINE = typed(
        "advanced_machine",
        MachineCasingBlock::new,
        p -> p.mapColor(MapColor.METAL).strength(3, 10).requiresCorrectToolForDrops().sound(SoundType.METAL)
    );

    public static final DeferredBlock<MachineBlock> GENERATOR = machine(MachineKind.GENERATOR);
    public static final DeferredBlock<MachineBlock> GEO_GENERATOR = machine(MachineKind.GEO_GENERATOR);
    public static final DeferredBlock<MachineBlock> SOLAR_PANEL = machine(MachineKind.SOLAR_PANEL);
    public static final DeferredBlock<MachineBlock> WATER_MILL = machine(MachineKind.WATER_MILL);
    public static final DeferredBlock<MachineBlock> WIND_MILL = machine(MachineKind.WIND_MILL);
    public static final DeferredBlock<MachineBlock> BATBOX = machine(MachineKind.BATBOX);
    public static final DeferredBlock<MachineBlock> MFE = machine(MachineKind.MFE);
    public static final DeferredBlock<MachineBlock> MFSU = machine(MachineKind.MFSU);
    public static final DeferredBlock<MachineBlock> LV_TRANSFORMER = machine(MachineKind.LV_TRANSFORMER);
    public static final DeferredBlock<MachineBlock> MV_TRANSFORMER = machine(MachineKind.MV_TRANSFORMER);
    public static final DeferredBlock<MachineBlock> HV_TRANSFORMER = machine(MachineKind.HV_TRANSFORMER);
    public static final DeferredBlock<MachineBlock> MACERATOR = machine(MachineKind.MACERATOR);
    public static final DeferredBlock<MachineBlock> EXTRACTOR = machine(MachineKind.EXTRACTOR);
    public static final DeferredBlock<MachineBlock> COMPRESSOR = machine(MachineKind.COMPRESSOR);
    public static final DeferredBlock<MachineBlock> RECYCLER = machine(MachineKind.RECYCLER);
    public static final DeferredBlock<MachineBlock> ELECTRIC_FURNACE = machine(MachineKind.ELECTRIC_FURNACE);
    public static final DeferredBlock<MachineBlock> LUMINATOR = machine(MachineKind.LUMINATOR);
    public static final DeferredBlock<MachineBlock> MINER = machine(MachineKind.MINER);
    public static final DeferredBlock<MachineBlock> PUMP = machine(MachineKind.PUMP);
    public static final DeferredBlock<MachineBlock> CANNER = machine(MachineKind.CANNER);
    public static final DeferredBlock<MachineBlock> ELECTROLYZER = machine(MachineKind.ELECTROLYZER);
    public static final DeferredBlock<MachineBlock> MAGNETIZER = machine(MachineKind.MAGNETIZER);
    public static final DeferredBlock<MachineBlock> INDUCTION = machine(MachineKind.INDUCTION);

    public static final DeferredBlock<MiningPipeBlock> MINING_PIPE = BLOCKS.registerBlock(
        "mining_pipe",
        props -> new MiningPipeBlock(false, props),
        p -> p.mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(0.5f).sound(SoundType.METAL).noOcclusion().noCollision()
    );
    public static final DeferredBlock<MiningPipeBlock> MINING_PIPE_TIP = BLOCKS.registerBlock(
        "mining_pipe_tip",
        props -> new MiningPipeBlock(true, props),
        p -> p.mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(0.5f).sound(SoundType.METAL).noOcclusion().noCollision()
    );

    public static final DeferredBlock<CableBlock> INSULATED_COPPER_CABLE = cable(CableKind.COPPER);
    public static final DeferredBlock<CableBlock> COPPER_CABLE = cable(CableKind.COPPER_BARE);
    public static final DeferredBlock<CableBlock> GOLD_CABLE = cable(CableKind.GOLD_BARE);
    public static final DeferredBlock<CableBlock> INSULATED_GOLD_CABLE = cable(CableKind.GOLD);
    public static final DeferredBlock<CableBlock> GOLD_CABLE_2X = cable(CableKind.GOLD_2X);
    public static final DeferredBlock<CableBlock> HV_CABLE = cable(CableKind.HV_BARE);
    public static final DeferredBlock<CableBlock> INSULATED_HV_CABLE = cable(CableKind.HV);
    public static final DeferredBlock<CableBlock> HV_CABLE_2X = cable(CableKind.HV_2X);
    public static final DeferredBlock<CableBlock> HV_CABLE_3X = cable(CableKind.HV_3X);
    public static final DeferredBlock<CableBlock> GLASS_FIBRE_CABLE = cable(CableKind.GLASS);
    public static final DeferredBlock<CableBlock> TIN_CABLE = cable(CableKind.TIN);
    public static final DeferredBlock<CableBlock> DETECTOR_CABLE = cable(CableKind.DETECTOR);
    public static final DeferredBlock<CableBlock> SPLITTER_CABLE = cable(CableKind.SPLITTER);

    private static DeferredBlock<Block> cube(String name, UnaryOperator<BlockBehaviour.Properties> props) {
        return BLOCKS.registerSimpleBlock(name, props);
    }

    private static <T extends Block> DeferredBlock<T> typed(String name, Function<BlockBehaviour.Properties, T> factory, UnaryOperator<BlockBehaviour.Properties> props) {
        return BLOCKS.registerBlock(name, factory, props);
    }

    private static DeferredBlock<MachineBlock> machine(MachineKind kind) {
        return BLOCKS.registerBlock(
            kind.getSerializedName(),
            props -> new MachineBlock(kind, props),
            p -> {
                var props = p.mapColor(MapColor.METAL).strength(3, 10).requiresCorrectToolForDrops().sound(SoundType.METAL);
                if (kind == MachineKind.LUMINATOR) {
                    props = props.noOcclusion().lightLevel(s -> s.getValue(MachineBlock.ACTIVE) ? 15 : 0);
                }
                return props;
            }
        );
    }

    private static DeferredBlock<CableBlock> cable(CableKind kind) {
        return BLOCKS.registerBlock(
            kind.id,
            props -> new CableBlock(kind, props),
            p -> p.mapColor(MapColor.COLOR_ORANGE).strength(0.2f).sound(SoundType.WOOL).noOcclusion()
        );
    }

    public static CableBlock cableBlock(CableKind kind) {
        return switch (kind) {
            case COPPER -> INSULATED_COPPER_CABLE.get();
            case COPPER_BARE -> COPPER_CABLE.get();
            case GOLD_BARE -> GOLD_CABLE.get();
            case GOLD -> INSULATED_GOLD_CABLE.get();
            case GOLD_2X -> GOLD_CABLE_2X.get();
            case HV_BARE -> HV_CABLE.get();
            case HV -> INSULATED_HV_CABLE.get();
            case HV_2X -> HV_CABLE_2X.get();
            case HV_3X -> HV_CABLE_3X.get();
            case GLASS -> GLASS_FIBRE_CABLE.get();
            case TIN -> TIN_CABLE.get();
            case DETECTOR -> DETECTOR_CABLE.get();
            case SPLITTER -> SPLITTER_CABLE.get();
        };
    }

    public static java.util.Set<Block> machines() {
        return java.util.Set.of(
            GENERATOR.get(), GEO_GENERATOR.get(), SOLAR_PANEL.get(), WATER_MILL.get(), WIND_MILL.get(),
            BATBOX.get(), MFE.get(), MFSU.get(), LV_TRANSFORMER.get(), MV_TRANSFORMER.get(), HV_TRANSFORMER.get(),
            MACERATOR.get(), EXTRACTOR.get(), COMPRESSOR.get(), RECYCLER.get(), ELECTRIC_FURNACE.get(), LUMINATOR.get(),
            MINER.get(), PUMP.get(), CANNER.get(),
            ELECTROLYZER.get(), MAGNETIZER.get(), INDUCTION.get()
        );
    }

    public static java.util.Set<Block> cables() {
        return java.util.Set.of(
            INSULATED_COPPER_CABLE.get(), COPPER_CABLE.get(), GOLD_CABLE.get(), INSULATED_GOLD_CABLE.get(), GOLD_CABLE_2X.get(),
            HV_CABLE.get(), INSULATED_HV_CABLE.get(), HV_CABLE_2X.get(), HV_CABLE_3X.get(),
            GLASS_FIBRE_CABLE.get(), TIN_CABLE.get(), DETECTOR_CABLE.get(), SPLITTER_CABLE.get()
        );
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    private ModBlocks() {}
}
