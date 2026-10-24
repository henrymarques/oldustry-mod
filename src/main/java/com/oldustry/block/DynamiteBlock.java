package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.oldustry.explosion.DynamiteProjectile;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

/** Placed dynamite stick. Render is a torch. Sticky dynamite never becomes this block. */
public class DynamiteBlock extends Block {
    public static final MapCodec<DynamiteBlock> CODEC = simpleCodec(DynamiteBlock::new);
    public static final EnumProperty<DynamiteAnchor> ANCHOR = EnumProperty.create("anchor", DynamiteAnchor.class);
    /** JAR {@code blockDynamiteRemote}: same stick, other block id, so a remote can tell it apart. */
    public static final BooleanProperty LINKED = BooleanProperty.create("linked");

    private static final VoxelShape FLOOR = Block.box(6.4, 0.0, 6.4, 9.6, 9.6, 9.6);
    private static final VoxelShape WEST = Block.box(0.0, 3.2, 5.6, 4.8, 12.8, 10.4);
    private static final VoxelShape EAST = Block.box(11.2, 3.2, 5.6, 16.0, 12.8, 10.4);
    private static final VoxelShape NORTH = Block.box(5.6, 3.2, 0.0, 10.4, 12.8, 4.8);
    private static final VoxelShape SOUTH = Block.box(5.6, 3.2, 11.2, 10.4, 12.8, 16.0);

    public DynamiteBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ANCHOR, DynamiteAnchor.FLOOR).setValue(LINKED, false));
    }

    @Override
    public MapCodec<DynamiteBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANCHOR, LINKED);
    }

    public static @Nullable DynamiteAnchor anchorAt(LevelReader level, BlockPos pos) {
        return DynamiteAnchor.choose(
            sturdy(level, pos.below(), Direction.UP),
            sturdy(level, pos.west(), Direction.EAST),
            sturdy(level, pos.east(), Direction.WEST),
            sturdy(level, pos.north(), Direction.SOUTH),
            sturdy(level, pos.south(), Direction.NORTH)
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(ANCHOR)) {
            case FLOOR -> FLOOR;
            case WEST -> WEST;
            case EAST -> EAST;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
        };
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this) && level.hasNeighborSignal(pos)) {
            prime(level, pos, null);
        }
    }

    @Override
    protected void neighborChanged(
        BlockState state, Level level, BlockPos pos, Block neighbor, @Nullable Orientation orientation, boolean movedByPiston
    ) {
        if (level.getBlockState(pos).is(this) && level.hasNeighborSignal(pos)) {
            prime(level, pos, null);
        }
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction directionToNeighbour,
        BlockPos neighbourPos,
        BlockState neighbourState,
        RandomSource random
    ) {
        boolean down = sturdy(level, pos.below(), Direction.UP);
        boolean west = sturdy(level, pos.west(), Direction.EAST);
        boolean east = sturdy(level, pos.east(), Direction.WEST);
        boolean north = sturdy(level, pos.north(), Direction.SOUTH);
        boolean south = sturdy(level, pos.south(), Direction.NORTH);
        DynamiteAnchor anchor = state.getValue(ANCHOR);
        if (DynamiteAnchor.choose(down, west, east, north, south) == null) {
            if (!(level instanceof ServerLevel server) || DynamiteProjectile.spawnPrimed(server, pos, null, DynamiteProjectile.UNSEATED_FUSE)) {
                return Blocks.AIR.defaultBlockState();
            }
            return state;
        }
        // The attached face is gone, but another cube remains. JAR quantityDropped is 0, so the stick vanishes.
        if (anchor.detached(down, west, east, north, south)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !prime(level, pos, player) && !player.getAbilities().instabuild) {
            popResource(level, pos, new ItemStack(OldustryItems.DYNAMITE.get()));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
        DynamiteProjectile.spawnPrimed(level, pos, explosion.getIndirectSourceEntity(), DynamiteProjectile.UNSEATED_FUSE);
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    /** Redstone, breaking, or the remote: fuse 40, then the block is gone. */
    public static boolean prime(Level level, BlockPos pos, @Nullable LivingEntity owner) {
        if (!(level instanceof ServerLevel server) || !(server.getBlockState(pos).getBlock() instanceof DynamiteBlock)) {
            return false;
        }
        if (!DynamiteProjectile.spawnPrimed(server, pos, owner, DynamiteProjectile.LIT_FUSE)) {
            return false;
        }
        server.removeBlock(pos, false);
        return true;
    }

    private static boolean sturdy(LevelReader level, BlockPos pos, Direction face) {
        return level.getBlockState(pos).isFaceSturdy(level, pos, face);
    }
}
