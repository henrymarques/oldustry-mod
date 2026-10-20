package com.oldustry.item;

import com.oldustry.registry.ModItems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class CoffeeMugItem extends Item {
    public enum Strength {
        COLD,
        DARK,
        SWEET
    }

    private final Strength strength;

    public CoffeeMugItem(Strength strength, Properties properties) {
        super(properties.stacksTo(1));
        this.strength = strength;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && level instanceof ServerLevel server) {
            int speed = amplify(player, MobEffects.SPEED);
            int haste = amplify(player, MobEffects.HASTE);
            int highest = Math.max(speed, haste);
            if (strength == Strength.SWEET) {
                highest -= 2;
            }
            if (highest >= 3) {
                player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, (highest - 2) * 200, 0));
                if (highest >= 4) {
                    player.hurtServer(server, server.damageSources().magic(), highest - 3);
                }
            }
        }
        return new ItemStack(ModItems.STONE_MUG.get());
    }

    private int amplify(Player player, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect) {
        MobEffectInstance current = player.getEffect(effect);
        int max = switch (strength) {
            case COLD -> 1;
            case DARK -> 5;
            case SWEET -> 6;
        };
        if (current == null) {
            player.addEffect(new MobEffectInstance(effect, 300, 0));
            return 1;
        }
        int amp = current.getAmplifier();
        int dur = current.getDuration();
        if (amp < max) {
            amp++;
        }
        dur += strength == Strength.COLD ? 600 : 1200;
        player.addEffect(new MobEffectInstance(effect, dur, amp));
        return amp;
    }
}
