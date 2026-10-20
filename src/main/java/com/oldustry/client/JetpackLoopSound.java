package com.oldustry.client;

import com.oldustry.OldustryConfig;
import com.oldustry.registry.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * JAR jetpack AudioSource: one looping instance while thrusting.
 * Same rule as {@link MachineLoopSound} — do not recreate while an entry exists
 * ({@link #isStopped()} is true for a few ticks after {@code play()}).
 */
public final class JetpackLoopSound extends AbstractTickableSoundInstance {
    private static JetpackLoopSound active;

    private final Player player;

    private JetpackLoopSound(Player player) {
        super(ModSounds.JETPACK.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.4f;
        this.pitch = 1f;
        this.attenuation = Attenuation.LINEAR;
        follow();
    }

    private void follow() {
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public void tick() {
        if (player.isRemoved() || Minecraft.getInstance().player != player) {
            stop();
            return;
        }
        follow();
    }

    /** Call each client tick: start once while thrusting, stop when idle. */
    public static void sync(boolean thrusting) {
        if (!thrusting || !ModSounds.enabled()) {
            stopNow();
            return;
        }
        // Keep a single entry until tickCleanup clears a stopped instance.
        if (active != null || MachineLoopSound.activeCount() >= OldustryConfig.SOUND_SOURCE_LIMIT.get()) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        active = new JetpackLoopSound(player);
        Minecraft.getInstance().getSoundManager().play(active);
    }

    private static void stopNow() {
        if (active != null) {
            active.stop();
            active = null;
        }
    }

    public static void tickCleanup() {
        if (active != null && active.isStopped()) {
            active = null;
        }
    }

    public static int activeCount() {
        return active == null ? 0 : 1;
    }
}
