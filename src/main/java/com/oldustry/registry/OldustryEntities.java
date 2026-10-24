package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.explosion.DynamiteProjectile;
import com.oldustry.explosion.PrimedItnt;
import com.oldustry.explosion.PrimedNuke;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OldustryEntities {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(Oldustry.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<DynamiteProjectile>> DYNAMITE = ENTITIES.registerEntityType(
        "dynamite", DynamiteProjectile::new, MobCategory.MISC,
        builder -> builder.sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(5)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<PrimedNuke>> PRIMED_NUKE = ENTITIES.registerEntityType(
        "primed_nuke", PrimedNuke::new, MobCategory.MISC,
        builder -> builder.sized(0.98F, 0.98F).clientTrackingRange(160).updateInterval(5).fireImmune()
    );
    public static final DeferredHolder<EntityType<?>, EntityType<PrimedItnt>> PRIMED_ITNT = ENTITIES.registerEntityType(
        "primed_itnt", PrimedItnt::new, MobCategory.MISC,
        builder -> builder.sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10).fireImmune()
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    private OldustryEntities() {}
}
