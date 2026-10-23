package com.oldustry.item;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.oldustry.network.KeysPayload;
import com.oldustry.registry.ModDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Shared {@code ItemArmorJetpack.useJetpack}. Fuel and electric differ only by {@link Kind}.
 * The low-charge scale is {@code charge / (max * drop)} — the decompile drops the parentheses
 * and integer-divides the charge away.
 */
public final class JetpackFlight {
    public record Kind(float power, float drop, float retrusterBonus, boolean electricCeiling, boolean hoverClimb) {}

    public static final Kind FUEL = new Kind(1f, 0.2f, 0f, false, false);
    /** JAR electric: power 0.7, drop 5%, retruster +0.15, ceiling worldHeight/1.28, hover jump climbs. */
    public static final Kind ELECTRIC = new Kind(0.7f, 0.05f, 0.15f, true, true);

    /** JAR {@code consume -= 2} on the fuel 9 / 6 ask. Discharge is partial, not the fuel damage readback. */
    public static final int ELECTRIC_USE_NORMAL = 7;
    public static final int ELECTRIC_USE_HOVER = 4;

    private static final Map<UUID, Integer> HOVER_LOCK = new ConcurrentHashMap<>();

    private JetpackFlight() {}

    public static int ceiling(int worldHeight, boolean electric) {
        return electric ? (int) (worldHeight / 1.28f) : worldHeight;
    }

    public static float scaledPower(float base, int charge, int max, float drop) {
        if (charge > 0 && max > 0 && (float) charge / max <= drop) {
            base *= charge / (max * drop);
        }
        return base;
    }

    public static int electricDrain(int charge, boolean hover) {
        return Math.min(hover ? ELECTRIC_USE_HOVER : ELECTRIC_USE_NORMAL, Math.max(0, charge));
    }

    /** JAR: jump and mode-switch together, then 10 ticks before it can flip again. Server only. */
    public static void tickHover(Player player, ItemStack stack) {
        int lock = HOVER_LOCK.getOrDefault(player.getUUID(), 0);
        if (lock == 0 && KeysPayload.jump(player) && KeysPayload.mode(player)) {
            boolean next = !stack.getOrDefault(ModDataComponents.HOVER.get(), false);
            stack.set(ModDataComponents.HOVER.get(), next);
            lock = 10;
            if (player instanceof ServerPlayer server) {
                server.sendSystemMessage(Component.translatable(next ? "info.oldustry.jetpack.hover.on" : "info.oldustry.jetpack.hover.off"), true);
            }
        }
        if (lock > 0) {
            lock--;
            if (lock == 0) {
                HOVER_LOCK.remove(player.getUUID());
            } else {
                HOVER_LOCK.put(player.getUUID(), lock);
            }
        }
    }

    /** @return true when thrust was applied */
    public static boolean thrust(Player player, ItemStack stack, Kind kind, int charge, int max, boolean jump, boolean forward) {
        boolean hover = stack.getOrDefault(ModDataComponents.HOVER.get(), false);
        double motionY = player.getDeltaMovement().y;
        if (!(jump || (hover && motionY < -0.35)) || charge <= 0) {
            return false;
        }

        float power = scaledPower(kind.power, charge, max, kind.drop);
        if (forward) {
            float retruster = (hover ? 0.5f : 0.15f) + kind.retrusterBonus;
            float forwardPower = power * retruster * 2f;
            if (forwardPower > 0f) {
                // JAR Entity.moveFlying(0, 0.4 * forwardpower, 0.02)
                player.moveRelative(0.02f, new Vec3(0, 0, 0.4f * forwardPower));
            }
        }

        int limit = ceiling(player.level().getMaxY(), kind.electricCeiling);
        double y = player.getY();
        if (y > limit - 25) {
            if (y > limit) {
                y = limit;
            }
            power = (float) (power * (limit - y) / 25.0);
        }

        Vec3 m = player.getDeltaMovement();
        double prevY = m.y;
        double nextY = Math.min(prevY + power * 0.2f, 0.6);
        if (hover) {
            float maxHoverY = kind.hoverClimb && jump ? 0.1f : -0.1f;
            if (nextY > maxHoverY) {
                nextY = maxHoverY;
                if (prevY > nextY) {
                    nextY = prevY;
                }
            }
        }
        player.setDeltaMovement(m.x, nextY, m.z);
        player.resetFallDistance();
        return true;
    }
}
