package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.oldustry.machine.OldustryMachineKind;
import com.oldustry.registry.OldustryBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class OldustryLuminatorBlock extends MachineBlock {
    public static final BooleanProperty COVER = BooleanProperty.create("cover");
    public static final MapCodec<OldustryLuminatorBlock> CODEC = simpleCodec(OldustryLuminatorBlock::new);
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

    public OldustryLuminatorBlock(BlockBehaviour.Properties properties) {
        super(OldustryMachineKind.LUMINATOR, properties);
    }

    @Override
    public MapCodec<OldustryLuminatorBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COVER);
    }

    /** JAR {@code isSpecialSupporter}: fence, iron fence, cable, reinforced glass, vanilla glass. */
    static boolean luminatorCover(BlockState support) {
        Block block = support.getBlock();
        return block instanceof FenceBlock
            || block instanceof IronFenceBlock
            || block instanceof CableBlock
            || block == Blocks.GLASS
            || block == OldustryBlocks.REINFORCED_GLASS.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        BlockState support = context.getLevel().getBlockState(pos.relative(facing.getOpposite()));
        BlockState state = defaultBlockState().setValue(FACING, facing).setValue(COVER, luminatorCover(support));
        return canSurvive(state, context.getLevel(), pos) ? state : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
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
        if (!canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        boolean cover = luminatorCover(level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())));
        if (state.getValue(COVER) != cover) {
            return state.setValue(COVER, cover);
        }
        return state;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
}
