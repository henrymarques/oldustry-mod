package com.oldustry.item;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.oldustry.Oldustry;
import com.oldustry.compat.CuriosWearables;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * JAR {@code ItemArmorNightvisionGoggles}: 20000 EU, transfer 200, tier 1, does not provide energy.
 * Desvio: no Blindness. Night vision and 1 EU/t only when eye-block light is {@code <= 7}.
 * Taking the goggles off clears that effect immediately. JAR left the 250-tick potion running.
 * Recipe waits on the diamond heat switch (D5) and the nano helmet.
 */
@EventBusSubscriber(modid = Oldustry.MOD_ID)
public class NightvisionGogglesItem extends ElectricItem {
    /** Players whose current night vision came from these goggles. */
    private static final Set<UUID> ACTIVE = ConcurrentHashMap.newKeySet();

    public NightvisionGogglesItem(Properties properties) {
        super(Nightvision.SPEC, properties);
    }

    public static void tick(ServerLevel level, Player player, ItemStack stack) {
        BlockPos eyes = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        if (!Nightvision.darkEnough(level.getMaxLocalRawBrightness(eyes))) {
            player.removeEffect(MobEffects.NIGHT_VISION);
            ACTIVE.remove(player.getUUID());
            return;
        }
        if (!(stack.getItem() instanceof ElectricItem electric) || !electric.pay(stack, 1, player)) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Nightvision.DURATION, 0, true, false));
        ACTIVE.add(player.getUUID());
    }

    /** Head slot or Curios head. Runs after the armor tick, so a removed pair is already gone. */
    @SubscribeEvent
    static void clearWhenRemoved(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || wearing(player) || !ACTIVE.remove(player.getUUID())) {
            return;
        }
        MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
        if (effect != null && Nightvision.fromGoggles(effect.getDuration(), effect.getAmplifier())) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    private static boolean wearing(Player player) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof NightvisionGogglesItem) {
            return true;
        }
        return CuriosWearables.any(player, stack -> stack.getItem() instanceof NightvisionGogglesItem);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player player && slot == EquipmentSlot.HEAD) {
            tick(level, player, stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        if (ModList.get().isLoaded("curios")) {
            builder.accept(Component.translatable("info.oldustry.curios.head").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
