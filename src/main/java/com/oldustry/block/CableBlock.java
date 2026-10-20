package com.oldustry.block;

import java.util.Map;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oldustry.energy.CableKind;
import com.oldustry.item.CutterItem;
import com.oldustry.item.CfPackItem;
import com.oldustry.item.PainterItem;
import com.oldustry.item.SprayerItem;
import com.oldustry.compat.CuriosWearables;
import com.oldustry.registry.ModBlockEntities;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModItems;
import com.oldustry.registry.ModSounds;
import com.oldustry.wrench.Wrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class CableBlock extends BaseEntityBlock implements Wrenchable {
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 16);
    public static final IntegerProperty FOAM = IntegerProperty.create("foam", 0, 2);
    /** Detector: EU flow this sample; splitter: in EnergyNet (JAR setActive). */
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final Map<Direction, BooleanProperty> PROPERTY = Map.of(
        Direction.NORTH, NORTH,
        Direction.EAST, EAST,
        Direction.SOUTH, SOUTH,
        Direction.WEST, WEST,
        Direction.UP, UP,
        Direction.DOWN, DOWN
    );
    public static final MapCodec<CableBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        StringRepresentable.fromEnum(CableKind::values).fieldOf("kind").forGetter(b -> b.kind),
        propertiesCodec()
    ).apply(i, CableBlock::new));

    public final CableKind kind;
    private final VoxelShape[] shapes;

    public CableBlock(CableKind kind, BlockBehaviour.Properties properties) {
        super(properties);
        this.kind = kind;
        this.shapes = buildShapes(kind.thickness);
        registerDefaultState(stateDefinition.any()
            .setValue(COLOR, 0).setValue(FOAM, 0).setValue(ACTIVE, false)
            .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
            .setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    private static VoxelShape[] buildShapes(int t) {
        double min = 8 - t / 2.0;
        double max = 8 + t / 2.0;
        VoxelShape core = Block.box(min, min, min, max, max, max);
        VoxelShape[] out = new VoxelShape[64];
        for (int mask = 0; mask < 64; mask++) {
            VoxelShape s = core;
            if ((mask & 1) != 0) {
                s = Shapes.or(s, Block.box(min, min, 0, max, max, min));
            }
            if ((mask & 2) != 0) {
                s = Shapes.or(s, Block.box(max, min, min, 16, max, max));
            }
            if ((mask & 4) != 0) {
                s = Shapes.or(s, Block.box(min, min, max, max, max, 16));
            }
            if ((mask & 8) != 0) {
                s = Shapes.or(s, Block.box(0, min, min, min, max, max));
            }
            if ((mask & 16) != 0) {
                s = Shapes.or(s, Block.box(min, max, min, max, 16, max));
            }
            if ((mask & 32) != 0) {
                s = Shapes.or(s, Block.box(min, 0, min, max, min, max));
            }
            out[mask] = s;
        }
        return out;
    }

    private static int mask(BlockState state) {
        int m = 0;
        if (state.getValue(NORTH)) {
            m |= 1;
        }
        if (state.getValue(EAST)) {
            m |= 2;
        }
        if (state.getValue(SOUTH)) {
            m |= 4;
        }
        if (state.getValue(WEST)) {
            m |= 8;
        }
        if (state.getValue(UP)) {
            m |= 16;
        }
        if (state.getValue(DOWN)) {
            m |= 32;
        }
        return m;
    }

    @Override
    public MapCodec<CableBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR, FOAM, ACTIVE, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return connections(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction direction,
        BlockPos neighborPos,
        BlockState neighborState,
        RandomSource random
    ) {
        return connections(state, level, pos);
    }

    private BlockState connections(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(FOAM) > 0) {
            return state;
        }
        BlockState next = state;
        for (Direction dir : Direction.values()) {
            next = next.setValue(PROPERTY.get(dir), connects(state, level, pos, dir));
        }
        return next;
    }

    public static boolean connects(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        BlockState other = level.getBlockState(pos.relative(dir));
        if (other.getBlock() instanceof CableBlock) {
            int a = state.getValue(COLOR);
            int b = other.getValue(COLOR);
            return a == 0 || b == 0 || a == b;
        }
        return other.getBlock() instanceof MachineBlock;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FOAM) > 0 ? Shapes.block() : shapes[mask(state)];
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(kind, pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level instanceof ServerLevel server
            ? createTickerHelper(type, ModBlockEntities.CABLE.get(), (l, pos, s, be) -> CableBlockEntity.serverTick(server, pos, s, be))
            : null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit
    ) {
        // Modernization: scrape foam (cutter, or sneak + empty hand) before insulation strip.
        if (state.getValue(FOAM) > 0 && (stack.getItem() instanceof CutterItem || (stack.isEmpty() && player.isShiftKeyDown()))) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(FOAM, 0), Block.UPDATE_ALL);
                if (stack.getItem() instanceof CutterItem) {
                    stack.hurtAndBreak(1, player, hand);
                    ModSounds.play(level, null, pos, ModSounds.CUTTER.get(), SoundSource.BLOCKS, 1f, 1f);
                }
            }
            return InteractionResult.SUCCESS;
        }
        // JAR ItemToolCutter.cutInsulationFrom: strip 1 layer + drop rubber + damage 3.
        if (stack.getItem() instanceof CutterItem && kind.strip() != kind) {
            if (!level.isClientSide()) {
                level.setBlock(pos, copyVisual(ModBlocks.cableBlock(kind.strip()).defaultBlockState(), state), Block.UPDATE_ALL);
                Block.popResource(level, pos, new ItemStack(ModItems.RUBBER.get()));
                stack.hurtAndBreak(3, player, hand);
                ModSounds.play(level, null, pos, ModSounds.CUTTER.get(), SoundSource.BLOCKS, 1f, 1f);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(ModItems.RUBBER.get()) && kind.insulate() != kind) {
            if (!level.isClientSide()) {
                level.setBlock(pos, copyVisual(ModBlocks.cableBlock(kind.insulate()).defaultBlockState(), state), Block.UPDATE_ALL);
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.getItem() instanceof PainterItem painter) {
            int color = painter.color().getId() + 1;
            if (state.getValue(COLOR) != color) {
                if (!level.isClientSide()) {
                    level.setBlock(pos, state.setValue(COLOR, color), Block.UPDATE_ALL);
                    stack.hurtAndBreak(1, player, hand);
                    ModSounds.play(level, null, pos, ModSounds.PAINTER.get(), SoundSource.BLOCKS, 1f, 1f);
                }
                return InteractionResult.SUCCESS;
            }
        }
        if (stack.getItem() instanceof SprayerItem && state.getValue(FOAM) == 0) {
            if (!level.isClientSide()) {
                boolean fromPack = CfPackItem.consumePellet(
                    CuriosWearables.chestOrCurios(player, ModItems.CF_PACK.get()));
                if (!fromPack && stack.getDamageValue() > SprayerItem.MAX_DAMAGE - SprayerItem.SPRAY_COST - 1) {
                    return InteractionResult.FAIL;
                }
                level.setBlock(pos, state.setValue(FOAM, 1), Block.UPDATE_ALL);
                if (!fromPack) {
                    stack.hurtAndBreak(SprayerItem.SPRAY_COST, player, hand);
                }
                level.scheduleTick(pos, this, 500);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static BlockState copyVisual(BlockState next, BlockState from) {
        BlockState s = next.setValue(COLOR, from.getValue(COLOR)).setValue(FOAM, from.getValue(FOAM));
        if (next.hasProperty(ACTIVE) && from.hasProperty(ACTIVE)) {
            s = s.setValue(ACTIVE, from.getValue(ACTIVE));
        }
        for (BooleanProperty prop : PROPERTY.values()) {
            s = s.setValue(prop, from.getValue(prop));
        }
        return s;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(FOAM) == 1) {
            level.setBlock(pos, state.setValue(FOAM, 2), Block.UPDATE_ALL);
        }
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return kind == CableKind.DETECTOR;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (kind == CableKind.DETECTOR && level.getBlockEntity(pos) instanceof CableBlockEntity cable) {
            return cable.redstone();
        }
        return 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston) {
        // Splitter open/close is polled every 20t in CableBlockEntity (JAR TileEntityCableSplitter).
        if (!level.isClientSide()) {
            BlockState next = connections(state, level, pos);
            if (next != state) {
                level.setBlock(pos, next, Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public ItemStack wrenchKeepDrop(BlockState state) {
        return new ItemStack(this);
    }

    @Override
    public ItemStack wrenchFailDrop(BlockState state) {
        return new ItemStack(this);
    }
}
