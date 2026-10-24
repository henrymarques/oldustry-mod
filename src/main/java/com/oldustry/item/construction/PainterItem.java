package com.oldustry.item.construction;

import com.oldustry.registry.OldustrySounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PainterItem extends Item {
    private final DyeColor color;

    public PainterItem(DyeColor color, Item.Properties properties) {
        super(properties);
        this.color = color;
    }

    public DyeColor color() {
        return color;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        BlockState painted = paint(state);
        if (painted == null || painted == state) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            level.setBlock(pos, painted, Block.UPDATE_ALL);
            hurt(context);
            OldustrySounds.play(level, null, pos, OldustrySounds.PAINTER.get(), SoundSource.BLOCKS, 1f, 1f);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, net.minecraft.world.InteractionHand hand) {
        if (target instanceof Sheep sheep && sheep.getColor() != color) {
            if (!player.level().isClientSide()) {
                sheep.setColor(color);
                stack.hurtAndBreak(1, player, hand);
                OldustrySounds.play(player.level(), null, target.blockPosition(), OldustrySounds.PAINTER.get(), SoundSource.PLAYERS, 1f, 1f);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private BlockState paint(BlockState state) {
        if (state.is(BlockTags.WOOL)) {
            return Blocks.WOOL.pick(color).defaultBlockState();
        }
        if (state.is(BlockTags.CONCRETE)) {
            return Blocks.CONCRETE.pick(color).defaultBlockState();
        }
        return null;
    }

    private void hurt(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            context.getItemInHand().hurtAndBreak(1, player, context.getHand());
        }
    }
}
