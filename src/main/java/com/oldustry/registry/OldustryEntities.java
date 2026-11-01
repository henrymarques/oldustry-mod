package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.entity.OldustryBoat;
import com.oldustry.entity.OldustryBoatStats;
import com.oldustry.entity.MiningLaser;
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
    public static final DeferredHolder<EntityType<?>, EntityType<MiningLaser>> MINING_LASER = ENTITIES.registerEntityType(
        "mining_laser", MiningLaser::new, MobCategory.MISC,
        builder -> builder.sized(0.8F, 0.8F).clientTrackingRange(160).updateInterval(1)
    );
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
    public static final DeferredHolder<EntityType<?>, EntityType<OldustryBoat>> RUBBER_DINGHY = boat(
        "rubber_dinghy", OldustryBoatStats.Kind.RUBBER, false
    );
    public static final DeferredHolder<EntityType<?>, EntityType<OldustryBoat>> CARBON_BOAT = boat(
        "carbon_boat", OldustryBoatStats.Kind.CARBON, false
    );
    public static final DeferredHolder<EntityType<?>, EntityType<OldustryBoat>> ELECTRIC_BOAT = boat(
        "electric_boat", OldustryBoatStats.Kind.ELECTRIC, true
    );

    private static DeferredHolder<EntityType<?>, EntityType<OldustryBoat>> boat(String name, OldustryBoatStats.Kind kind, boolean fireImmune) {
        return ENTITIES.registerEntityType(name, (type, level) -> new OldustryBoat(type, level, kind, () -> switch (kind) {
            case RUBBER -> OldustryItems.RUBBER_DINGHY.get();
            case CARBON -> OldustryItems.CARBON_BOAT.get();
            case ELECTRIC -> OldustryItems.ELECTRIC_BOAT.get();
        }), MobCategory.MISC, builder -> {
            builder.sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10).noLootTable();
            if (fireImmune) {
                builder.fireImmune();
            }
            return builder;
        });
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    private OldustryEntities() {}
}
