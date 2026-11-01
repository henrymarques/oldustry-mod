package com.oldustry.client.sound;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.oldustry.OldustryConfig;
import com.oldustry.block.MachineBlock;
import com.oldustry.block.OldustryReactorBlockEntity;
import com.oldustry.machine.OldustryMachineKind;
import com.oldustry.reactor.OldustryReactor;
import com.oldustry.registry.OldustrySounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * JAR AudioSource loop: one instance per machine.
 * Do not recreate while a map entry exists — {@link #isStopped()} is true for a few ticks after
 * {@code play()}, which used to spawn stacked loops (esp. induction).
 * Client-only via {@link com.oldustry.OldustryClient} (never referenced from common).
 */
public final class MachineLoopSound extends AbstractTickableSoundInstance {
    private static final Map<BlockPos, MachineLoopSound> ACTIVE = new HashMap<>();
    private static final Map<BlockPos, MachineLoopSound> SCRAP = new HashMap<>();
    private static final Map<BlockPos, MachineLoopSound> REACTOR = new HashMap<>();
    private static final Map<BlockPos, MachineLoopSound> GEIGER = new HashMap<>();

    private final BlockPos pos;
    private final OldustryMachineKind kind;
    private final boolean scrap;
    /** 0 is the reactor hum. 1–3 are Geiger bands. */
    private final int geigerBand;

    private MachineLoopSound(SoundEvent sound, BlockPos pos, OldustryMachineKind kind, boolean scrap, int geigerBand) {
        super(sound, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.pos = pos.immutable();
        this.kind = kind;
        this.scrap = scrap;
        this.geigerBand = geigerBand;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.15f;
        this.pitch = 1f;
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
        this.attenuation = Attenuation.LINEAR;
    }

    @Override
    public void tick() {
        Level level = Minecraft.getInstance().level;
        if (level == null || !stillActive(level)) {
            stop();
        }
    }

    private boolean stillActive(Level level) {
        if (geigerBand > 0 || kind == null) {
            if (!(level.getBlockEntity(pos) instanceof OldustryReactorBlockEntity reactor)) {
                return false;
            }
            int output = reactor.output();
            return geigerBand > 0 ? OldustryReactor.geiger(output) == geigerBand : output > 0;
        }
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof MachineBlock machine
            && machine.kind == kind
            && state.getValue(MachineBlock.ACTIVE)
            && (!scrap || scrapPlaying(level));
    }

    private boolean scrapPlaying(Level level) {
        return level.getBlockEntity(pos) instanceof com.oldustry.machine.OldustryMassFabricatorBlockEntity mass && mass.scrap() > 0;
    }

    /** Call each client tick for an ACTIVE machine that has a loop sound. */
    public static void sync(BlockPos pos, OldustryMachineKind kind) {
        if (kind.type == OldustryMachineKind.Type.MASS) {
            syncScrap(pos, kind);
        }
        if (!OldustrySounds.enabled()) {
            stopAt(pos);
            return;
        }
        SoundEvent sound = loopOf(kind);
        if (sound == null) {
            return;
        }
        BlockPos key = pos.immutable();
        // Keep a single map entry until tickCleanup removes a stopped instance.
        if (ACTIVE.containsKey(key) || !room()) {
            return;
        }
        MachineLoopSound next = new MachineLoopSound(sound, key, kind, false, 0);
        ACTIVE.put(key, next);
        Minecraft.getInstance().getSoundManager().play(next);
    }

    /** Force-stop when block is gone (break) or ACTIVE cleared mid-tick. */
    public static void stopAt(BlockPos pos) {
        MachineLoopSound existing = ACTIVE.remove(pos.immutable());
        if (existing != null) {
            existing.stop();
        }
        stopScrap(pos);
    }

    private static void syncScrap(BlockPos pos, OldustryMachineKind kind) {
        BlockPos key = pos.immutable();
        Level level = Minecraft.getInstance().level;
        boolean on = OldustrySounds.enabled()
            && level != null
            && level.getBlockEntity(key) instanceof com.oldustry.machine.OldustryMassFabricatorBlockEntity mass
            && mass.scrap() > 0;
        if (!on) {
            stopScrap(key);
            return;
        }
        if (SCRAP.containsKey(key) || !room()) {
            return;
        }
        MachineLoopSound next = new MachineLoopSound(OldustrySounds.MASS_FABRICATOR_SCRAP.get(), key, kind, true, 0);
        SCRAP.put(key, next);
        Minecraft.getInstance().getSoundManager().play(next);
    }

    private static void stopScrap(BlockPos pos) {
        MachineLoopSound existing = SCRAP.remove(pos.immutable());
        if (existing != null) {
            existing.stop();
        }
    }

    /** Reactor hum while it is producing. Geiger follows the unscaled output band. A hot idle core is silent. */
    public static void syncReactor(BlockPos pos) {
        BlockPos key = pos.immutable();
        Level level = Minecraft.getInstance().level;
        int output = level != null && level.getBlockEntity(key) instanceof OldustryReactorBlockEntity reactor ? reactor.output() : 0;
        if (!OldustrySounds.enabled() || output <= 0) {
            stopReactor(key);
        } else if (!REACTOR.containsKey(key) && room()) {
            MachineLoopSound next = new MachineLoopSound(OldustrySounds.NUCLEAR_REACTOR.get(), key, null, false, 0);
            REACTOR.put(key, next);
            Minecraft.getInstance().getSoundManager().play(next);
        }
        int band = OldustryReactor.geiger(output);
        MachineLoopSound geiger = GEIGER.get(key);
        if (!OldustrySounds.enabled() || band == 0) {
            stopGeiger(key);
            return;
        }
        if (geiger != null && geiger.geigerBand == band) {
            return;
        }
        stopGeiger(key);
        if (!room()) {
            return;
        }
        SoundEvent sound = switch (band) {
            case 1 -> OldustrySounds.GEIGER_LOW.get();
            case 2 -> OldustrySounds.GEIGER_MED.get();
            default -> OldustrySounds.GEIGER_HIGH.get();
        };
        MachineLoopSound next = new MachineLoopSound(sound, key, null, false, band);
        GEIGER.put(key, next);
        Minecraft.getInstance().getSoundManager().play(next);
    }

    private static void stopReactor(BlockPos pos) {
        MachineLoopSound existing = REACTOR.remove(pos.immutable());
        if (existing != null) {
            existing.stop();
        }
    }

    private static void stopGeiger(BlockPos pos) {
        MachineLoopSound existing = GEIGER.remove(pos.immutable());
        if (existing != null) {
            existing.stop();
        }
    }

    private static boolean room() {
        return ACTIVE.size() + SCRAP.size() + REACTOR.size() + GEIGER.size() + JetpackLoopSound.activeCount()
            < OldustryConfig.SOUND_SOURCE_LIMIT.get();
    }

    public static void tickCleanup() {
        Iterator<Map.Entry<BlockPos, MachineLoopSound>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, MachineLoopSound> entry = it.next();
            if (entry.getValue().isStopped()) {
                it.remove();
            }
        }
        Iterator<Map.Entry<BlockPos, MachineLoopSound>> scrap = SCRAP.entrySet().iterator();
        while (scrap.hasNext()) {
            if (scrap.next().getValue().isStopped()) {
                scrap.remove();
            }
        }
        clearStopped(REACTOR);
        clearStopped(GEIGER);
    }

    private static void clearStopped(Map<BlockPos, MachineLoopSound> map) {
        Iterator<Map.Entry<BlockPos, MachineLoopSound>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().isStopped()) {
                it.remove();
            }
        }
    }

    public static int activeCount() {
        return ACTIVE.size();
    }

    private static SoundEvent loopOf(OldustryMachineKind kind) {
        return switch (kind.type) {
            case MACERATOR -> OldustrySounds.MACERATOR.get();
            case EXTRACTOR -> OldustrySounds.EXTRACTOR.get();
            case COMPRESSOR -> OldustrySounds.COMPRESSOR.get();
            case RECYCLER -> OldustrySounds.RECYCLER.get();
            case FURNACE -> OldustrySounds.ELECTRIC_FURNACE.get();
            case GENERATOR -> OldustrySounds.GENERATOR.get();
            case GEO -> OldustrySounds.GEO.get();
            case WATER -> OldustrySounds.WATER.get();
            case WIND -> OldustrySounds.WIND.get();
            case MINING -> OldustrySounds.MINER.get();
            case PUMPING -> OldustrySounds.PUMP.get();
            case ELECTROLYZER -> OldustrySounds.ELECTROLYZER.get();
            case MAGNETIZER -> OldustrySounds.MAGNETIZER.get();
            case INDUCTION -> OldustrySounds.INDUCTION.get();
            case TELEPORTER -> OldustrySounds.TELEPORTER_LOOP.get();
            case TERRAFORMER -> OldustrySounds.TERRAFORMER.get();
            case MASS -> OldustrySounds.MASS_FABRICATOR.get();
            default -> null;
        };
    }
}
