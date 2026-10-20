package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.network.KeysPayload;
import com.oldustry.registry.ModDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;

/**
 * JAR {@code ItemArmorJetpack} (fuel). Tank is {@link ModDataComponents#FUEL} (~18000), not durability.
 * Thrust must run on the <em>client</em> (player movement is client-auth); server consumes fuel.
 */
public class FuelJetpackItem extends Item {
    /** JAR maxCharge = maxDamage − 2 with maxDamage 18002. */
    public static final int MAX_FUEL = 18000;

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
     * JAR {@code useJetpack}: power 1.0, drop 0.2, consume 9 / hover 6.
     * Fuel hover: does <em>not</em> climb — clamps motionY to −0.1 (slow sink). Electric uses +0.1 with jump.
     * @param consumeFuel server only
     * @return true if thrust applied
     */
    public static boolean tickFlight(Player player, ItemStack stack, boolean jump, boolean forward, boolean consumeFuel) {
        if (player.getAbilities().flying) {
            return false;
        }
        boolean hover = stack.getOrDefault(ModDataComponents.HOVER.get(), false);
        double motionY = player.getDeltaMovement().y;
        // JAR −0.35; −0.15 = less gap before catch. Jump still thrusts (then hover clamps sink).
        boolean thrust = jump || (hover && !player.onGround() && motionY < -0.15);
        int charge = fuel(stack);
        // Creative: fly without filling (survival still needs FUEL > 0).
        if (!thrust || (charge <= 0 && !player.getAbilities().instabuild)) {
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
            // JAR fuel maxHoverY = −0.1 always (no climb). Hard clamp while jump so sink stays steady.
            float maxHoverY = -0.1f;
            if (nextY > maxHoverY) {
                nextY = maxHoverY;
                // JAR restores prevmotion when it was above clamp — skips that while jump (keeps −0.1 sink).
                if (!jump && prevY > nextY) {
                    nextY = prevY;
                }
            }
        }
        player.setDeltaMovement(m.x, nextY, m.z);
        player.resetFallDistance();

        if (consumeFuel && !player.getAbilities().instabuild) {
            setFuel(stack, charge - (hover ? 6 : 9));
        }
        return true;
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
