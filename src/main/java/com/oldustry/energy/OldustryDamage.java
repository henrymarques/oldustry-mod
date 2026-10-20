package com.oldustry.energy;

import com.oldustry.Oldustry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public final class OldustryDamage {
    public static final ResourceKey<DamageType> ELECTRICITY = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "electricity")
    );

    public static DamageSource electricity(Level level) {
        return level.damageSources().source(ELECTRICITY);
    }

    private OldustryDamage() {}
}
