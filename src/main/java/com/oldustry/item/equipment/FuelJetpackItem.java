package com.oldustry.item.equipment;

import com.oldustry.client.sound.JetpackLoopSound;

import java.util.function.Consumer;

import com.oldustry.network.KeysPayload;
import com.oldustry.registry.OldustryDataComponents;

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

/**
 * JAR {@code ItemArmorJetpack} (fuel). Tank is {@link OldustryDataComponents#FUEL}
 * holding the readable charge ({@link JetpackFuel#MAX}), not item damage.
 * Thrust must run on the client (player movement is client-auth); server consumes fuel.
 */
public class FuelJetpackItem extends Item {
    public static final int MAX_FUEL = JetpackFuel.MAX;

    public FuelJetpackItem(Properties properties) {
        super(properties);
    }

    public static int fuel(ItemStack stack) {
        return stack.getOrDefault(OldustryDataComponents.FUEL.get(), 0);
    }

    public static void setFuel(ItemStack stack, int value) {
        int next = Math.max(0, Math.min(MAX_FUEL, value));
        if (fuel(stack) == next) {
            return;
        }
        stack.set(OldustryDataComponents.FUEL.get(), next);
    }

    /**
     * JAR {@code useJetpack}: power 1.0, drop 0.2. {@code use} asks for 9 / hover 6 and the
     * damage write reads back 4 less, so the tank drops 13 / 10.
     * @param consumeFuel server only
     * @return true if thrust applied
     */
    public static boolean tickFlight(Player player, ItemStack stack, boolean jump, boolean forward, boolean consumeFuel) {
        if (consumeFuel) {
            JetpackFlight.tickHover(player, stack);
        }
        if (player.getAbilities().flying) {
            return false;
        }
        int charge = fuel(stack);
        boolean flew = JetpackFlight.thrust(player, stack, JetpackFlight.FUEL, charge, MAX_FUEL, jump, forward);
        if (flew && consumeFuel) {
            boolean hover = stack.getOrDefault(OldustryDataComponents.HOVER.get(), false);
            setFuel(stack, JetpackFuel.afterUse(charge, hover ? JetpackFuel.USE_HOVER : JetpackFuel.USE_NORMAL));
        }
        return flew;
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
        if (stack.getOrDefault(OldustryDataComponents.HOVER.get(), false)) {
            builder.accept(Component.translatable("info.oldustry.jetpack.hover.hint").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
