package com.oldustry.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.oldustry.OldustryConfig;
import com.oldustry.block.MachineBlock;
import com.oldustry.machine.MachineKind;
import com.oldustry.registry.ModSounds;

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

    private final BlockPos pos;
    private final MachineKind kind;

    private MachineLoopSound(SoundEvent sound, BlockPos pos, MachineKind kind) {
        super(sound, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.pos = pos.immutable();
        this.kind = kind;
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
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof MachineBlock machine
            && machine.kind == kind
            && state.getValue(MachineBlock.ACTIVE);
    }

    /** Call each client tick for an ACTIVE machine that has a loop sound. */
    public static void sync(BlockPos pos, MachineKind kind) {
        if (!ModSounds.enabled()) {
            stopAt(pos);
            return;
        }
        SoundEvent sound = loopOf(kind);
        if (sound == null) {
            return;
        }
        BlockPos key = pos.immutable();
        // Keep a single map entry until tickCleanup removes a stopped instance.
        if (ACTIVE.containsKey(key)
            || ACTIVE.size() + JetpackLoopSound.activeCount() >= OldustryConfig.SOUND_SOURCE_LIMIT.get()) {
            return;
        }
        MachineLoopSound next = new MachineLoopSound(sound, key, kind);
        ACTIVE.put(key, next);
        Minecraft.getInstance().getSoundManager().play(next);
    }

    /** Force-stop when block is gone (break) or ACTIVE cleared mid-tick. */
    public static void stopAt(BlockPos pos) {
        MachineLoopSound existing = ACTIVE.remove(pos.immutable());
        if (existing != null) {
            existing.stop();
        }
    }

    public static void tickCleanup() {
        Iterator<Map.Entry<BlockPos, MachineLoopSound>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, MachineLoopSound> entry = it.next();
            if (entry.getValue().isStopped()) {
                it.remove();
            }
        }
    }

    public static int activeCount() {
        return ACTIVE.size();
    }

    private static SoundEvent loopOf(MachineKind kind) {
        return switch (kind.type) {
            case MACERATOR -> ModSounds.MACERATOR.get();
            case EXTRACTOR -> ModSounds.EXTRACTOR.get();
            case COMPRESSOR -> ModSounds.COMPRESSOR.get();
            case RECYCLER -> ModSounds.RECYCLER.get();
            case FURNACE -> ModSounds.ELECTRIC_FURNACE.get();
            case GENERATOR -> ModSounds.GENERATOR.get();
            case GEO -> ModSounds.GEO.get();
            case WATER -> ModSounds.WATER.get();
            case WIND -> ModSounds.WIND.get();
            case MINING -> ModSounds.MINER.get();
            case PUMPING -> ModSounds.PUMP.get();
            case ELECTROLYZER -> ModSounds.ELECTROLYZER.get();
            case MAGNETIZER -> ModSounds.MAGNETIZER.get();
            case INDUCTION -> ModSounds.INDUCTION.get();
            default -> null;
        };
    }
}
