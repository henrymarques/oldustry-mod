package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.oldustry.crop.OldustryCrop;
import com.oldustry.registry.OldustryBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jspecify.annotations.Nullable;

/** JAR {@code BlockCrop}. Sticks only stay on farmland. The plant lives in the block entity. */
public class OldustryCropBlock extends BaseEntityBlock {
    public static final MapCodec<OldustryCropBlock> CODEC = simpleCodec(OldustryCropBlock::new);
    public static final EnumProperty<OldustryCrop> CROP = EnumProperty.create("crop", OldustryCrop.class);
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 7);
    public static final BooleanProperty UPGRADED = BooleanProperty.create("upgraded");
    private static final VoxelShape SHAPE = Shapes.box(0.2, 0.0, 0.2, 0.8, 0.7, 0.8);

    public OldustryCropBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(CROP, OldustryCrop.NONE)
            .setValue(SIZE, 0)
            .setValue(UPGRADED, false));
    }

    @Override
    public MapCodec<OldustryCropBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CROP, SIZE, UPGRADED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos()) ? defaultBlockState() : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(Blocks.FARMLAND);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean movedByPiston) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        if (level.getBlockEntity(pos) instanceof OldustryCropBlockEntity crop) {
            crop.onNeighbor();
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /** JAR sticks were walk-through; the outline stays for click and look. */
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return state.getValue(CROP).redstone(state.getValue(SIZE)) > 0;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(CROP).redstone(state.getValue(SIZE));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OldustryCropBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, OldustryBlockEntities.CROP.get(), OldustryCropBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    ) {
        if (level.getBlockEntity(pos) instanceof OldustryCropBlockEntity crop && crop.use(player, stack)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof OldustryCropBlockEntity crop && crop.use(player, ItemStack.EMPTY)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OldustryCropBlockEntity crop) {
            crop.leftClick(player);
        }
    }

    @Override
    protected void entityInside(
        BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effect, boolean precise
    ) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OldustryCropBlockEntity crop) {
            crop.bumped(entity);
        }
    }

    static int farmlandMoisture(LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.FARMLAND) ? below.getValue(FarmlandBlock.MOISTURE) : 0;
    }
}
