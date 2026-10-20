package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.OldustryConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Oldustry.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH = sound("wrench");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREETAP = sound("treetap");
    public static final DeferredHolder<SoundEvent, SoundEvent> PAINTER = sound("painter");
    public static final DeferredHolder<SoundEvent, SoundEvent> CUTTER = sound("cutter");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCANNER = sound("od_scanner");
    public static final DeferredHolder<SoundEvent, SoundEvent> JETPACK = sound("jetpack");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHAINSAW_USE = sound("chainsaw_use");
    public static final DeferredHolder<SoundEvent, SoundEvent> DRILL_USE = sound("drill_use");
    public static final DeferredHolder<SoundEvent, SoundEvent> MACERATOR = sound("macerator");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXTRACTOR = sound("extractor");
    public static final DeferredHolder<SoundEvent, SoundEvent> COMPRESSOR = sound("compressor");
    public static final DeferredHolder<SoundEvent, SoundEvent> RECYCLER = sound("recycler");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELECTRIC_FURNACE = sound("electric_furnace");
    public static final DeferredHolder<SoundEvent, SoundEvent> GENERATOR = sound("generator");
    public static final DeferredHolder<SoundEvent, SoundEvent> GEO = sound("geo");
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER = sound("water_mill");
    public static final DeferredHolder<SoundEvent, SoundEvent> WIND = sound("wind_mill");
    public static final DeferredHolder<SoundEvent, SoundEvent> INTERRUPT = sound("interrupt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINER = sound("miner");
    public static final DeferredHolder<SoundEvent, SoundEvent> PUMP = sound("pump");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELECTROLYZER = sound("electrolyzer");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAGNETIZER = sound("magnetizer");
    public static final DeferredHolder<SoundEvent, SoundEvent> INDUCTION = sound("induction");

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static boolean enabled() {
        return OldustryConfig.SOUNDS_ENABLED.get() && OldustryConfig.SOUND_SOURCE_LIMIT.get() > 6;
    }

    public static void play(Level level, Player except, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        // shortcut: sourceLimit caps our tracked loops, not vanilla one-shots; hook the sound engine only if a hard cap is needed.
        if (enabled()) {
            level.playSound(except, pos, sound, source, volume, pitch);
        }
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }

    private ModSounds() {}
}
