package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.energy.OldustryDamage;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OldustryEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Oldustry.MOD_ID);

    /** JAR liquid color 5149489. */
    public static final DeferredHolder<MobEffect, RadiationEffect> RADIATION =
        EFFECTS.register("radiation", () -> new RadiationEffect(MobEffectCategory.HARMFUL, 5149489));

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }

    public static final class RadiationEffect extends MobEffect {
        public RadiationEffect(MobEffectCategory category, int color) {
            super(category, color);
        }

        @Override
        public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
            entity.hurtServer(level, OldustryDamage.radiation(level), amplifier + 1);
            return true;
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
            int rate = 25 >> amplifier;
            return rate > 0 ? tickCount % rate == 0 : true;
        }
    }

    private OldustryEffects() {}
}
