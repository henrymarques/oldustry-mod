package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.world.ConfigGatePlacement;
import com.oldustry.world.RubberTreeFeature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Oldustry.MOD_ID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENTS =
        DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, Oldustry.MOD_ID);

    public static final DeferredHolder<Feature<?>, RubberTreeFeature> RUBBER_TREE =
        FEATURES.register("rubber_tree", RubberTreeFeature::new);
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<ConfigGatePlacement>> CONFIG_GATE =
        PLACEMENTS.register("config_gate", () -> () -> ConfigGatePlacement.CODEC);

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
        PLACEMENTS.register(bus);
    }

    private ModFeatures() {}
}
