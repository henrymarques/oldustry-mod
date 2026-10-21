package com.oldustry.item;

import java.util.List;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.registry.ModSounds;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * JAR {@code ItemElectricToolChainsaw}: axe + {@code IShearable} (entities / leaf drops via {@code SHEARS_DIG}).
 */
public class ChainsawItem extends ElectricToolItem {
    public ChainsawItem(ElectricCharge.Spec spec, Properties properties) {
        super(spec, ChainsawStats.EU, ChainsawStats.SPEED, properties);
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability)
            || ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(ability);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!(entity instanceof IShearable target)) {
            return InteractionResult.PASS;
        }
        BlockPos pos = entity.blockPosition();
        if (!target.isShearable(player, stack, entity.level(), pos)) {
            return InteractionResult.PASS;
        }
        boolean creative = player.getAbilities().instabuild;
        if (!creative && !canPay(stack, euPerUse(), player)) {
            return InteractionResult.FAIL;
        }
        List<ItemStack> drops = target.onSheared(player, stack, entity.level(), pos);
        if (entity.level() instanceof ServerLevel serverLevel) {
            for (ItemStack drop : drops) {
                target.spawnShearedDrop(serverLevel, pos, drop);
            }
            if (!creative) {
                pay(stack, euPerUse(), player);
            }
        }
        entity.gameEvent(GameEvent.SHEAR, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        BlockState trimmed = state.getToolModifiedState(context, ItemAbilities.SHEARS_TRIM, false);
        if (trimmed == null) {
            return super.useOn(context);
        }
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        boolean creative = player != null && player.getAbilities().instabuild;
        if (!creative && !canPay(stack, euPerUse(), player)) {
            return InteractionResult.FAIL;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }
        level.setBlockAndUpdate(pos, trimmed);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, trimmed));
        if (!level.isClientSide() && !creative) {
            pay(stack, euPerUse(), player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        boolean result = super.mineBlock(stack, level, state, pos, entity);
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0) {
            ModSounds.play(level, null, pos, ModSounds.CHAINSAW_USE.get(), SoundSource.PLAYERS, 1f, 1f);
        }
        return result;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (!attacker.level().isClientSide()) {
            ModSounds.play(attacker.level(), null, target.blockPosition(), ModSounds.CHAINSAW_USE.get(), SoundSource.PLAYERS, 1f, 1f);
        }
    }
}
