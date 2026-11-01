package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.oldustry.reactor.OldustryReactor;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.wrench.Wrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * Places only while exactly one reactor touches the spot ({@code canPlaceBlockAt}).
 * A chamber already in the world pops if that stops being true. A normal break drops a machine casing;
 * the wrench keeps the chamber (80%). Right-click opens that reactor.
 * Not a Container: the wrench would spill the reactor if this block owned that inventory.
 */
public final class OldustryReactorChamberBlock extends BaseEntityBlock implements Wrenchable {
    public static final MapCodec<OldustryReactorChamberBlock> CODEC = simpleCodec(OldustryReactorChamberBlock::new);

    public OldustryReactorChamberBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OldustryReactorChamberBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel server) {
            return createTickerHelper(type, OldustryBlockEntities.REACTOR_CHAMBER.get(), (l, pos, s, be) -> OldustryReactorChamberBlockEntity.serverTick(server, pos, s, be));
        }
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return reactors(context.getLevel(), context.getClickedPos()) == 1 ? defaultBlockState() : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return reactors(level, pos) == 1;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moved) {
        super.onPlace(state, level, pos, old, moved);
        sustain(level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, @Nullable Orientation orientation, boolean movedByPiston) {
        sustain(level, pos);
    }

    private void sustain(Level level, BlockPos pos) {
        if (level.isClientSide() || !level.getBlockState(pos).is(this) || reactors(level, pos) == 1) {
            return;
        }
        popResource(level, pos, new ItemStack(this));
        level.removeBlock(pos, false);
    }

    static int reactors(BlockGetter level, BlockPos pos) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(OldustryBlocks.NUCLEAR_REACTOR.get())) {
                count++;
            }
        }
        return count;
    }

    static @Nullable OldustryReactorBlockEntity reactor(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(pos.relative(dir)) instanceof OldustryReactorBlockEntity reactor) {
                return reactor;
            }
        }
        return null;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        return useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        OldustryReactorBlockEntity reactor = reactor(level, pos);
        if (reactor == null) {
            if (!level.isClientSide()) {
                sustain(level, pos);
            }
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide()) {
            player.openMenu(reactor);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        OldustryReactorBlockEntity reactor = reactor(level, pos);
        if (reactor != null) {
            OldustryReactorBlock.puffs(level, pos, random, reactor.heat());
        }
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
        return new ItemStack(OldustryBlocks.MACHINE_CASING.get());
    }
}
