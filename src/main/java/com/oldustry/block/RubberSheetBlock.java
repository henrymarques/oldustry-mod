package com.oldustry.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RubberSheetBlock extends Block {
    public static final MapCodec<RubberSheetBlock> CODEC = simpleCodec(RubberSheetBlock::new);
    public static final BooleanProperty SUPPORT = BooleanProperty.create("support");
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    public RubberSheetBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SUPPORT, false));
    }

    @Override
    public MapCodec<RubberSheetBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SUPPORT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos())
            ? defaultBlockState()
            : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
            return true;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighbor);
            if (neighborState.isFaceSturdy(level, neighbor, dir.getOpposite())
                || neighborState.getBlock() instanceof RubberSheetBlock) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, net.minecraft.util.RandomSource random) {
        if (state.getValue(SUPPORT)) {
            state = state.setValue(SUPPORT, false);
        }
        if (!canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        entity.causeFallDamage(fallDistance, 0, level.damageSources().fall());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
            return;
        }
        if (entity instanceof LivingEntity && !canSupportWeight(level, pos)) {
            if (!level.isClientSide()) {
                level.destroyBlock(pos, true);
            }
            return;
        }
        if (entity.getDeltaMovement().y <= -0.4) {
            bounce(entity);
        }
    }

    private boolean canSupportWeight(Level level, BlockPos pos) {
        if (level.getBlockState(pos).getValue(SUPPORT)) {
            return true;
        }
        BlockPos below = pos.below();
        if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
            return true;
        }
        if (bridged(level, pos, Direction.EAST) && bridged(level, pos, Direction.WEST)) {
            level.setBlock(pos, level.getBlockState(pos).setValue(SUPPORT, true), 3);
            return true;
        }
        if (bridged(level, pos, Direction.NORTH) && bridged(level, pos, Direction.SOUTH)) {
            level.setBlock(pos, level.getBlockState(pos).setValue(SUPPORT, true), 3);
            return true;
        }
        return false;
    }

    private static boolean bridged(Level level, BlockPos start, Direction dir) {
        BlockPos.MutableBlockPos cursor = start.mutable();
        while (true) {
            cursor.move(dir);
            BlockState state = level.getBlockState(cursor);
            if (state.isFaceSturdy(level, cursor, dir.getOpposite())) {
                return true;
            }
            if (!(state.getBlock() instanceof RubberSheetBlock)) {
                return false;
            }
            BlockPos below = cursor.below();
            if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                return true;
            }
        }
    }

    private static void bounce(Entity entity) {
        entity.resetFallDistance();
        Vec3 m = entity.getDeltaMovement();
        double y;
        if (entity instanceof LivingEntity living && living.isJumping()) {
            y = m.y * -1.3;
        } else if (entity instanceof Player player && player.isShiftKeyDown()) {
            y = m.y * -0.1;
        } else {
            y = m.y * -0.8;
        }
        entity.setDeltaMovement(m.x * 1.1, y, m.z * 1.1);
    }
}
