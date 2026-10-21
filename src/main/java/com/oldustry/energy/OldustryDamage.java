package com.oldustry.energy;

import com.oldustry.Oldustry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class OldustryDamage {
    public static final ResourceKey<DamageType> ELECTRICITY = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "electricity")
    );
    public static final ResourceKey<DamageType> NUKE = key("nuke");
    public static final ResourceKey<DamageType> RADIATION = key("radiation");

    public static DamageSource electricity(Level level) {
        return level.damageSources().source(ELECTRICITY);
    }

    public static DamageSource radiation(Level level) {
        return level.damageSources().source(RADIATION);
    }

    /** {@code nuke.player} when someone lit it. The blast itself bypasses armor. */
    public static DamageSource nuke(Level level, @Nullable Entity direct, @Nullable Entity igniter) {
        return igniter == null ? level.damageSources().source(NUKE) : level.damageSources().source(NUKE, direct, igniter);
    }

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, name));
    }

    private OldustryDamage() {}
}
