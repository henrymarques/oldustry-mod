package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.oldustry.item.BoozeContents;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import org.jspecify.annotations.Nullable;

/** JAR {@code BlockBarrel}. Sits on wooden scaffold; punch without tap pops the barrel back. */
public class OldustryBarrelBlock extends BaseEntityBlock {
    public static final MapCodec<OldustryBarrelBlock> CODEC = simpleCodec(OldustryBarrelBlock::new);
    /** 0 = no tap; 2–5 = horizontal Direction 3D data value. */
    public static final IntegerProperty TAP = IntegerProperty.create("tap", 0, 5);

    public OldustryBarrelBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TAP, 0));
    }

    @Override
    public MapCodec<OldustryBarrelBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TAP);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OldustryBarrelBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, OldustryBlockEntities.BARREL.get(), OldustryBarrelBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof OldustryBarrelBlockEntity barrel)) {
            return InteractionResult.PASS;
        }
        Direction face = hit.getDirection();
        if (barrel.useTreetap(player, face, stack)) {
            return InteractionResult.SUCCESS;
        }
        if (stack.is(OldustryItems.STONE_MUG.get())) {
            if (barrel.treetapSide().getAxis().isVertical() || barrel.treetapSide() != face) {
                return InteractionResult.PASS;
            }
            BoozeContents snap = barrel.snapshot();
            if (!barrel.drainLiquid(1) || level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            ItemStack filled = new ItemStack(OldustryItems.BOOZE_MUG.get());
            filled.set(OldustryDataComponents.BOOZE.get(), snap);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (stack.isEmpty()) {
                player.setItemInHand(hand, filled);
            } else if (!player.getInventory().add(filled)) {
                player.drop(filled, false);
            }
            return InteractionResult.SUCCESS;
        }
        if (barrel.rightClick(player, hand)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof OldustryBarrelBlockEntity barrel)) {
            return;
        }
        if (barrel.treetapSide().getAxis().isHorizontal()) {
            if (!level.isClientSide()) {
                Block.popResource(level, pos, new ItemStack(OldustryItems.TREETAP.get()));
                barrel.drainLiquid(1);
                barrel.clearTap();
            }
            return;
        }
        if (!level.isClientSide()) {
            ItemStack drop = new ItemStack(OldustryItems.BARREL.get());
            BoozeContents snap = barrel.snapshot();
            if (!snap.isEmpty()) {
                drop.set(OldustryDataComponents.BOOZE.get(), snap);
            }
            Block.popResource(level, pos, drop);
            level.setBlock(pos, OldustryBlocks.WOODEN_SCAFFOLD.get().defaultBlockState(), Block.UPDATE_ALL);
        }
    }
}
