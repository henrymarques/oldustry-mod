package com.oldustry.item;

import com.oldustry.block.DynamiteAnchor;
import com.oldustry.block.DynamiteBlock;
import com.oldustry.explosion.DynamiteProjectile;
import com.oldustry.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class DynamiteItem extends Item implements ProjectileItem {
    private final boolean sticky;

    public DynamiteItem(Properties properties, boolean sticky) {
        super(properties.stacksTo(16));
        this.sticky = sticky;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (sticky) {
            return throwStick(context.getLevel(), context.getPlayer(), context.getHand());
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        if (!level.getBlockState(pos).isAir()) {
            return InteractionResult.SUCCESS;
        }
        DynamiteAnchor anchor = DynamiteBlock.anchorAt(level, pos);
        if (anchor == null) {
            return InteractionResult.SUCCESS;
        }
        BlockState state = ModBlocks.DYNAMITE.get().defaultBlockState().setValue(DynamiteBlock.ANCHOR, anchor);
        if (!level.setBlock(pos, state, 3)) {
            return InteractionResult.SUCCESS;
        }
        Player player = context.getPlayer();
        context.getItemInHand().consume(1, player);
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return throwStick(level, player, hand);
    }

    private InteractionResult throwStick(Level level, Player player, InteractionHand hand) {
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(
            null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (level instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileFromRotation(DynamiteProjectile::new, serverLevel, stack, player, 0, 1.0F, 1.0F);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack stack, Direction direction) {
        return new DynamiteProjectile(level, position, stack);
    }
}
