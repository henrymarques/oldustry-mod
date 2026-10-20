package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.explosion.DynamiteProjectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(Oldustry.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<DynamiteProjectile>> DYNAMITE = ENTITIES.registerEntityType(
        "dynamite", DynamiteProjectile::new, MobCategory.MISC,
        builder -> builder.sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(5)
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    private ModEntities() {}
}
