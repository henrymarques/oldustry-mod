package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oldustry.machine.MachineGui;
import com.oldustry.machine.MachineKind;
import com.oldustry.machine.MachineBlockEntity;
import com.oldustry.registry.ModBlockEntities;
import com.oldustry.registry.ModBlocks;
import com.oldustry.wrench.Wrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class MachineBlock extends BaseEntityBlock implements Wrenchable {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    /** JAR {@code fullCover}: glass, fence, iron fence or cable behind the plate. */
    public static final BooleanProperty COVER = BooleanProperty.create("cover");
    /** Set only for the {@code super()} call. {@code createBlockStateDefinition} runs before {@code kind} is assigned. */
    private static MachineKind defining;
    public static final MapCodec<MachineBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        StringRepresentable.fromEnum(MachineKind::values).fieldOf("kind").forGetter(b -> b.kind),
        propertiesCodec()
    ).apply(i, MachineBlock::new));

    private static final VoxelShape LUM_UP = Block.box(0, 0, 0, 16, 1, 16);
    private static final VoxelShape LUM_DOWN = Block.box(4, 13, 4, 12, 16, 12);
    private static final VoxelShape LUM_DOWN_COVER = Block.box(0, 15, 0, 16, 16, 16);
    private static final VoxelShape LUM_NORTH = Block.box(6, 3, 14, 10, 13, 16);
    private static final VoxelShape LUM_NORTH_COVER = Block.box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape LUM_SOUTH = Block.box(6, 3, 0, 10, 13, 2);
    private static final VoxelShape LUM_SOUTH_COVER = Block.box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape LUM_WEST = Block.box(14, 3, 6, 16, 13, 10);
    private static final VoxelShape LUM_WEST_COVER = Block.box(15, 0, 0, 16, 16, 16);
    private static final VoxelShape LUM_EAST = Block.box(0, 3, 6, 2, 13, 10);
    private static final VoxelShape LUM_EAST_COVER = Block.box(0, 0, 0, 1, 16, 16);

    public final MachineKind kind;

    public MachineBlock(MachineKind kind, BlockBehaviour.Properties properties) {
        super(mark(kind, properties));
        defining = null;
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    private static BlockBehaviour.Properties mark(MachineKind kind, BlockBehaviour.Properties properties) {
        defining = kind;
        return properties;
    }

    @Override
    public MapCodec<MachineBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
        if (defining != null && defining.type == MachineKind.Type.LUMINATOR) {
            builder.add(COVER);
        }
    }

    /** JAR {@code isSpecialSupporter}: fence, iron fence, cable, reinforced glass, vanilla glass. */
    static boolean luminatorCover(BlockState support) {
        Block block = support.getBlock();
        return block instanceof FenceBlock
            || block instanceof IronFenceBlock
            || block instanceof CableBlock
            || block == Blocks.GLASS
            || block == ModBlocks.REINFORCED_GLASS.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (kind.type == MachineKind.Type.LUMINATOR) {
            Direction facing = context.getClickedFace();
            BlockPos pos = context.getClickedPos();
            BlockState support = context.getLevel().getBlockState(pos.relative(facing.getOpposite()));
            BlockState state = defaultBlockState().setValue(FACING, facing).setValue(COVER, luminatorCover(support));
            return canSurvive(state, context.getLevel(), pos) ? state : null;
        }
        Direction facing = kind.verticalFacing()
            ? context.getNearestLookingDirection().getOpposite()
            : context.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (kind.type != MachineKind.Type.LUMINATOR) {
            return super.canSurvive(state, level, pos);
        }
        BlockPos support = pos.relative(state.getValue(FACING).getOpposite());
        BlockState other = level.getBlockState(support);
        return other.isFaceSturdy(level, support, state.getValue(FACING)) || luminatorCover(other);
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
        if (kind.type == MachineKind.Type.LUMINATOR) {
            if (!canSurvive(state, level, pos)) {
                return Blocks.AIR.defaultBlockState();
            }
            boolean cover = luminatorCover(level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())));
            if (state.getValue(COVER) != cover) {
                return state.setValue(COVER, cover);
            }
        }
        return state;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (kind.type != MachineKind.Type.LUMINATOR) {
            return super.getShape(state, level, pos, context);
        }
        boolean cover = state.getValue(COVER);
        return switch (state.getValue(FACING)) {
            case UP -> LUM_UP;
            case DOWN -> cover ? LUM_DOWN_COVER : LUM_DOWN;
            case NORTH -> cover ? LUM_NORTH_COVER : LUM_NORTH;
            case SOUTH -> cover ? LUM_SOUTH_COVER : LUM_SOUTH;
            case WEST -> cover ? LUM_WEST_COVER : LUM_WEST;
            case EAST -> cover ? LUM_EAST_COVER : LUM_EAST;
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MachineBlockEntity(kind, pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel server) {
            return createTickerHelper(type, ModBlockEntities.MACHINE.get(), (l, pos, s, be) -> MachineBlockEntity.serverTick(server, pos, s, be));
        }
        return createTickerHelper(type, ModBlockEntities.MACHINE.get(), MachineBlockEntity::clientTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // JAR transformers/luminator/magnetizer: no GUI — PASS so adjacent place works without sneak.
        if (!MachineGui.hasGui(kind)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MachineBlockEntity machine) {
            player.openMenu(machine);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canSetFacing(BlockState state, Direction face) {
        return kind.verticalFacing() && state.getValue(FACING) != face;
    }

    @Override
    public BlockState setFacing(BlockState state, Direction face) {
        return state.setValue(FACING, face);
    }

    @Override
    public float wrenchDropRate() {
        return kind.wrenchRate;
    }

    @Override
    public ItemStack wrenchKeepDrop(BlockState state) {
        return new ItemStack(this);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return kind.type == MachineKind.Type.STORAGE;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof MachineBlockEntity machine ? machine.redstonePower() : 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        // Modernization: all electric machines expose comparator (progress or buffer fill).
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof MachineBlockEntity machine ? machine.comparator() : 0;
    }

    @Override
    public ItemStack wrenchFailDrop(BlockState state) {
        return kind.type == MachineKind.Type.MACERATOR
            || kind.type == MachineKind.Type.EXTRACTOR
            || kind.type == MachineKind.Type.COMPRESSOR
            || kind.type == MachineKind.Type.RECYCLER
            ? new ItemStack(com.oldustry.registry.ModBlocks.MACHINE_CASING.get())
            : new ItemStack(this);
    }
}
