package com.oldustry.item;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.oldustry.network.KeysPayload;
import com.oldustry.registry.ModDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;

/**
 * JAR {@code ItemArmorJetpack} (fuel). Tank is {@link ModDataComponents#FUEL}
 * holding the readable charge ({@link JetpackFuel#MAX}), not item damage.
 * Thrust must run on the client (player movement is client-auth); server consumes fuel.
 */
public class FuelJetpackItem extends Item {
    public static final int MAX_FUEL = JetpackFuel.MAX;
    /** JAR sets this to 10, then decrements on the same tick. */
    private static final Map<UUID, Integer> HOVER_LOCK = new ConcurrentHashMap<>();

    public FuelJetpackItem(Properties properties) {
        super(properties);
    }

    public static int fuel(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FUEL.get(), 0);
    }

    public static void setFuel(ItemStack stack, int value) {
        int next = Math.max(0, Math.min(MAX_FUEL, value));
        if (fuel(stack) == next) {
            return;
        }
        stack.set(ModDataComponents.FUEL.get(), next);
    }

    /**
     * JAR {@code useJetpack}: power 1.0, drop 0.2. {@code use} asks for 9 / hover 6 and the
     * damage write reads back 4 less, so the tank drops 13 / 10.
     * Fuel hover clamps motionY to −0.1 and keeps a faster previous rise. Electric uses +0.1 with jump.
     * @param consumeFuel server only
     * @return true if thrust applied
     */
    public static boolean tickFlight(Player player, ItemStack stack, boolean jump, boolean forward, boolean consumeFuel) {
        if (consumeFuel) {
            tickHoverToggle(player, stack);
        }
        if (player.getAbilities().flying) {
            return false;
        }
        boolean hover = stack.getOrDefault(ModDataComponents.HOVER.get(), false);
        double motionY = player.getDeltaMovement().y;
        boolean thrust = jump || (hover && motionY < -0.35);
        int charge = fuel(stack);
        if (!thrust || charge <= 0) {
            return false;
        }

        float power = 1f;
        float dropPercentage = 0.2f;
        if (charge > 0 && (float) charge / MAX_FUEL <= dropPercentage) {
            power *= charge / (MAX_FUEL * dropPercentage);
        }

        if (forward) {
            float retruster = hover ? 0.5f : 0.15f;
            float forwardPower = power * retruster * 2f;
            if (forwardPower > 0f) {
                // JAR Entity.moveFlying(0, 0.4 * forwardpower, 0.02)
                player.moveRelative(0.02f, new Vec3(0, 0, 0.4f * forwardPower));
            }
        }

        int worldHeight = player.level().getMaxY();
        double y = player.getY();
        if (y > worldHeight - 25) {
            if (y > worldHeight) {
                y = worldHeight;
            }
            power = (float) (power * (worldHeight - y) / 25.0);
        }

        Vec3 m = player.getDeltaMovement();
        double prevY = m.y;
        // JAR: motionY = min(motionY + power * 0.2, 0.6)
        double nextY = Math.min(prevY + power * 0.2f, 0.6);
        if (hover) {
            float maxHoverY = -0.1f;
            if (nextY > maxHoverY) {
                nextY = maxHoverY;
                if (prevY > nextY) {
                    nextY = prevY;
                }
            }
        }
        player.setDeltaMovement(m.x, nextY, m.z);
        player.resetFallDistance();

        if (consumeFuel) {
            setFuel(stack, JetpackFuel.afterUse(charge, hover ? JetpackFuel.USE_HOVER : JetpackFuel.USE_NORMAL));
        }
        return true;
    }

    /** JAR: jump and mode-switch together, then 10 ticks before it can flip again. */
    private static void tickHoverToggle(Player player, ItemStack stack) {
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

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player player) || slot != EquipmentSlot.CHEST) {
            return;
        }
        // Sound: client JetpackLoopSound (one looping instance — not playSound spam).
        tickFlight(player, stack, KeysPayload.jump(player), KeysPayload.forward(player), true);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * fuel(stack) / (float) MAX_FUEL);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fill = fuel(stack) / (float) MAX_FUEL;
        return fill < 0.15f ? 0xFF5555 : (fill < 0.5f ? 0xFFAA00 : 0x55FF55);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("info.oldustry.fuel.stored", fuel(stack), MAX_FUEL).withStyle(ChatFormatting.GRAY));
        if (stack.getOrDefault(ModDataComponents.HOVER.get(), false)) {
            builder.accept(Component.translatable("info.oldustry.jetpack.hover.hint").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
