package com.oldustry.item.equipment;

import com.oldustry.item.ElectricArmorItem;

import java.util.function.Consumer;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.network.KeysPayload;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * JAR {@code ItemArmorJetpackElectric}: 30000 EU, transfer 60, tier 1, does not provide energy.
 * Flight is {@link JetpackFlight#ELECTRIC}. {@code use} discharges 7 (hover 4), partial if the tank is lower.
 */
public class ElectricJetpackItem extends ElectricArmorItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(30000, 60, 1, false);

    public ElectricJetpackItem(Properties properties) {
        super(SPEC, properties);
    }

    /**
     * @param consume server only
     * @return true if thrust applied
     */
    public static boolean tickFlight(Player player, ItemStack stack, boolean jump, boolean forward, boolean consume) {
        if (consume) {
            JetpackFlight.tickHover(player, stack);
        }
        if (player.getAbilities().flying) {
            return false;
        }
        int charge = energy(stack);
        boolean flew = JetpackFlight.thrust(player, stack, JetpackFlight.ELECTRIC, charge, SPEC.max(), jump, forward);
        if (flew && consume) {
            boolean hover = stack.getOrDefault(OldustryDataComponents.HOVER.get(), false);
            setEnergy(stack, charge - JetpackFlight.electricDrain(charge, hover));
        }
        return flew;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player player) || slot != EquipmentSlot.CHEST) {
            return;
        }
        tickFlight(player, stack, KeysPayload.jump(player), KeysPayload.forward(player), true);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        if (stack.getOrDefault(OldustryDataComponents.HOVER.get(), false)) {
            builder.accept(Component.translatable("info.oldustry.jetpack.hover.electric").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
