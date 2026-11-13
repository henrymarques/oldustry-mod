package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryItems;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/** JAR {@code ItemBooze}. Quality lives in {@code BOOZE}. */
public class BoozeMugItem extends Item {
    private static final String[] SOLID = { "Watery ", "Clear ", "Lite ", "", "Strong ", "Thick ", "Stodge ", "X" };
    private static final String[] HOPS = { "Soup ", "Alcfree ", "White ", "", "Dark ", "Full ", "Black ", "X" };
    private static final String[] TIME = { "Brew", "Youngster", "Beer", "Ale", "Dragonblood", "Black Stuff" };
    private static final int[] BASE_DURATION = { 300, 600, 900, 1200, 1600, 2000, 2400 };
    private static final float[] BASE_INTENSITY = { 0.4F, 0.75F, 1.0F, 1.5F, 2.0F };
    private static final float RUM_STACK = 2.0F;
    private static final int RUM_DURATION = 600;

    public BoozeMugItem(Properties props) {
        super(props);
    }

    public static BoozeContents contents(ItemStack stack) {
        return stack.getOrDefault(OldustryDataComponents.BOOZE.get(), BoozeContents.EMPTY);
    }

    @Override
    public Component getName(ItemStack stack) {
        BoozeContents booze = contents(stack);
        if (booze.type() == 1) {
            int time = Math.min(booze.timeRatio(), TIME.length - 1);
            if (time == TIME.length - 1) {
                return Component.literal(TIME[time]);
            }
            return Component.literal(SOLID[Math.clamp(booze.solidRatio(), 0, SOLID.length - 1)]
                + HOPS[Math.clamp(booze.hopsRatio(), 0, HOPS.length - 1)]
                + TIME[time]);
        }
        if (booze.type() == 2) {
            return Component.literal("Rum");
        }
        return Component.literal("Zero");
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return stack;
        }
        BoozeContents booze = contents(stack);
        if (booze.type() == 0) {
            return new ItemStack(OldustryItems.STONE_MUG.get());
        }
        if (booze.type() == 1) {
            if (booze.timeRatio() == 5) {
                drinkBlackStuff(player);
            } else {
                int solid = Math.clamp(booze.solidRatio(), 0, BASE_DURATION.length - 1);
                int alc = booze.hopsRatio();
                int duration = BASE_DURATION[solid];
                float intensity = BASE_INTENSITY[Math.clamp(booze.timeRatio(), 0, BASE_INTENSITY.length - 1)];
                player.getFoodData().eat(6 - alc, solid * 0.15F);
                int max = (int) (intensity * alc * 0.5F);
                MobEffectInstance slow = player.getEffect(MobEffects.SLOWNESS);
                int levelBefore = slow == null ? -1 : slow.getAmplifier();
                amplify(player, MobEffects.SLOWNESS, max, intensity, duration);
                if (levelBefore > -1) {
                    amplify(player, MobEffects.HASTE, max, intensity, duration);
                    if (levelBefore > 0) {
                        amplify(player, MobEffects.MINING_FATIGUE, max / 2, intensity, duration);
                        if (levelBefore > 1) {
                            amplify(player, MobEffects.RESISTANCE, max - 1, intensity, duration);
                            if (levelBefore > 2) {
                                amplify(player, MobEffects.NAUSEA, 0, intensity, duration);
                                if (levelBefore > 3) {
                                    player.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, level.getRandom().nextInt(3)));
                                }
                            }
                        }
                    }
                }
            }
        } else if (booze.type() == 2) {
            if (booze.rumProgress() < 100) {
                drinkBlackStuff(player);
            } else {
                amplify(player, MobEffects.FIRE_RESISTANCE, 0, RUM_STACK, RUM_DURATION);
                MobEffectInstance def = player.getEffect(MobEffects.RESISTANCE);
                int levelBefore = def == null ? -1 : def.getAmplifier();
                amplify(player, MobEffects.RESISTANCE, 2, RUM_STACK, RUM_DURATION);
                if (levelBefore >= 0) {
                    amplify(player, MobEffects.BLINDNESS, 0, RUM_STACK, RUM_DURATION);
                }
                if (levelBefore >= 1) {
                    amplify(player, MobEffects.NAUSEA, 0, RUM_STACK, RUM_DURATION);
                }
            }
        }
        return new ItemStack(OldustryItems.STONE_MUG.get());
    }

    private static void amplify(Player player, net.minecraft.core.Holder<MobEffect> potion, int max, float intensity, int duration) {
        MobEffectInstance eff = player.getEffect(potion);
        if (eff == null) {
            player.addEffect(new MobEffectInstance(potion, duration, 0));
            return;
        }
        int newDur = eff.getDuration();
        int maxNew = (int) (duration * (1.0F + intensity * 2.0F) - newDur) / 2;
        if (maxNew < 0) {
            maxNew = 0;
        }
        if (maxNew < duration) {
            duration = maxNew;
        }
        newDur += duration;
        int amp = eff.getAmplifier();
        if (amp < max) {
            amp++;
        }
        player.addEffect(new MobEffectInstance(potion, newDur, amp));
    }

    private static void drinkBlackStuff(Player player) {
        switch (player.level().getRandom().nextInt(6)) {
            case 1 -> player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 1200, 0));
            case 2 -> player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 2400, 0));
            case 3 -> player.addEffect(new MobEffectInstance(MobEffects.POISON, 2400, 0));
            case 4 -> player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
            case 5 -> player.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, player.level().getRandom().nextInt(4)));
            default -> {
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        BoozeContents booze = contents(stack);
        if (!booze.isEmpty()) {
            tooltip.accept(Component.translatable("tooltip.oldustry.booze_amount", booze.amount()));
        }
    }
}
