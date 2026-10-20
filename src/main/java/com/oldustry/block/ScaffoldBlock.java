package com.oldustry.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oldustry.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** IC2 BlockScaffold: climb, strength, collapse. */
public class ScaffoldBlock extends Block {
    public static final MapCodec<ScaffoldBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.BOOL.fieldOf("iron").forGetter(block -> block.iron),
        propertiesCodec()
    ).apply(instance, (iron, properties) -> new ScaffoldBlock(Boolean.TRUE.equals(iron), properties)));
    public static final IntegerProperty STRENGTH = IntegerProperty.create("strength", 0, 12);
    /** JAR collision inset; selection box stays full so targeting matches 1.5.2. */
    private static final VoxelShape COLLISION = Block.box(1, 0, 1, 15, 16, 15);

    private final boolean iron;

    public ScaffoldBlock(boolean iron, BlockBehaviour.Properties properties) {
        super(properties);
        this.iron = iron;
        registerDefaultState(stateDefinition.any().setValue(STRENGTH, standardStrength()));
    }

    @Override
    public MapCodec<? extends ScaffoldBlock> codec() {
        return CODEC;
    }

    public int standardStrength() {
        return iron ? 5 : 2;
    }

    public int reinforcedStrength() {
        return iron ? 12 : 5;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STRENGTH);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION;
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(STRENGTH, standardStrength());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof Player player) {
            player.resetFallDistance();
            Vec3 movement = player.getDeltaMovement();
            if (movement.y < -0.15) {
                player.setDeltaMovement(movement.x, -0.15, movement.z);
            }
            if (player.zza > 0 && player.getDeltaMovement().y < 0.2) {
                Vec3 m = player.getDeltaMovement();
                player.setDeltaMovement(m.x, 0.2, m.z);
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (stack.is(asItem())) {
            if (!level.isClientSide()) {
                stackUp(level, pos, player, stack);
            }
            return InteractionResult.SUCCESS;
        }
        boolean woodReinforce = !iron && stack.is(Items.STICK) && stack.getCount() >= 2;
        boolean ironReinforce = iron && stack.is(ModItems.IRON_FENCE.get());
        if (!woodReinforce && !ironReinforce) {
            return InteractionResult.PASS;
        }
        if (state.getValue(STRENGTH) == reinforcedStrength() || !isPillar(level, pos)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            stack.shrink(iron ? 1 : 2);
            level.setBlock(pos, state.setValue(STRENGTH, reinforcedStrength()), Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    private void stackUp(Level level, BlockPos pos, Player player, ItemStack held) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (level.getBlockState(cursor).is(this)) {
            cursor.move(Direction.UP);
        }
        if (cursor.getY() < level.getMaxY() && canSurvive(defaultBlockState(), level, cursor) && level.getBlockState(cursor).canBeReplaced()) {
            level.setBlock(cursor, defaultBlockState().setValue(STRENGTH, standardStrength()), Block.UPDATE_ALL);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, net.minecraft.world.level.redstone.Orientation orientation, boolean movedByPiston) {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int own = state.getValue(STRENGTH);
        if (own >= reinforcedStrength()) {
            if (!isPillar(level, pos)) {
                own = strengthFrom(level, pos);
                ItemStack extra = iron ? new ItemStack(ModItems.IRON_FENCE.get()) : new ItemStack(Items.STICK, 2);
                popResource(level, pos, extra);
            }
        } else {
            own = strengthFrom(level, pos);
        }
        if (own <= -1) {
            popResource(level, pos, new ItemStack(this));
            level.removeBlock(pos, false);
        } else if (own != state.getValue(STRENGTH)) {
            level.setBlock(pos, state.setValue(STRENGTH, Math.max(0, own)), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        var drops = new java.util.ArrayList<>(super.getDrops(state, builder));
        if (state.getValue(STRENGTH) >= reinforcedStrength()) {
            drops.add(iron ? new ItemStack(ModItems.IRON_FENCE.get()) : new ItemStack(Items.STICK, 2));
        }
        return drops;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return strengthFrom(level, pos) > -1;
    }

    private boolean isPillar(LevelReader level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (level.getBlockState(cursor).is(this)) {
            cursor.move(Direction.DOWN);
        }
        BlockState base = level.getBlockState(cursor);
        return base.getBlock() instanceof FoamBlock || base.isFaceSturdy(level, cursor, Direction.UP);
    }

    private int strengthFrom(LevelReader level, BlockPos pos) {
        int strength = 0;
        if (isPillar(level, pos.below())) {
            strength = standardStrength() + 1;
        }
        strength = compare(level, pos.below(), strength);
        strength = compare(level, pos.east(), strength);
        strength = compare(level, pos.west(), strength);
        strength = compare(level, pos.south(), strength);
        strength = compare(level, pos.north(), strength);
        return strength - 1;
    }

    private int compare(LevelReader level, BlockPos pos, int strength) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(this)) {
            return strength;
        }
        int s = Math.min(state.getValue(STRENGTH), reinforcedStrength());
        return Math.max(s, strength);
    }
}
