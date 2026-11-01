package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.oldustry.reactor.OldustryReactor;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.wrench.Wrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
import org.jspecify.annotations.Nullable;

/** Generator meta 5. Clicking with a chamber in hand does not open the GUI, so the chamber can be attached. */
public final class OldustryReactorBlock extends BaseEntityBlock implements Wrenchable {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = MachineBlock.ACTIVE;
    public static final MapCodec<OldustryReactorBlock> CODEC = simpleCodec(OldustryReactorBlock::new);

    public OldustryReactorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        return new OldustryReactorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel server) {
            return createTickerHelper(type, OldustryBlockEntities.NUCLEAR_REACTOR.get(), (l, pos, s, be) -> OldustryReactorBlockEntity.serverTick(server, pos, s, be));
        }
        return createTickerHelper(type, OldustryBlockEntities.NUCLEAR_REACTOR.get(), OldustryReactorBlockEntity::clientTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        open(level, pos, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(com.oldustry.registry.OldustryBlocks.REACTOR_CHAMBER.get().asItem())
            || stack.getItem() instanceof com.oldustry.item.tool.WrenchItem
            || stack.getItem() instanceof com.oldustry.item.tool.ElectricWrenchItem) {
            return InteractionResult.PASS;
        }
        open(level, pos, player);
        return InteractionResult.SUCCESS;
    }

    private static void open(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OldustryReactorBlockEntity reactor) {
            player.openMenu(reactor);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof OldustryReactorBlockEntity reactor) {
            puffs(level, pos, random, reactor.heat());
        }
    }

    static void puffs(Level level, BlockPos pos, RandomSource random, int heat) {
        int puffs = heat / 1000;
        if (puffs <= 0) {
            return;
        }
        puffs = random.nextInt(puffs);
        for (int n = 0; n < puffs; n++) {
            level.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextFloat(), pos.getY() + 0.95, pos.getZ() + random.nextFloat(), 0, 0, 0);
        }
        puffs -= random.nextInt(4) + 3;
        for (int n = 0; n < puffs; n++) {
            level.addParticle(ParticleTypes.FLAME, pos.getX() + random.nextFloat(), pos.getY() + 1.0, pos.getZ() + random.nextFloat(), 0, 0, 0);
        }
    }

    @Override
    public boolean canSetFacing(BlockState state, Direction face) {
        return face.getAxis().isHorizontal() && state.getValue(FACING) != face;
    }

    @Override
    public BlockState setFacing(BlockState state, Direction face) {
        return state.setValue(FACING, face);
    }

    @Override
    public float wrenchDropRate() {
        return OldustryReactor.WRENCH_RATE;
    }

    @Override
    public ItemStack wrenchKeepDrop(BlockState state) {
        return new ItemStack(this);
    }

    @Override
    public ItemStack wrenchFailDrop(BlockState state) {
        return new ItemStack(this);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof OldustryReactorBlockEntity reactor) {
            int max = Math.max(1, reactor.maxHeat());
            return Math.min(15, reactor.heat() * 15 / max);
        }
        return 0;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return false;
    }
}
