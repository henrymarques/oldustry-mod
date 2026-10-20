package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.energy.ElectricCharge;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.fml.ModList;

public class ElectricArmorItem extends ElectricItem {
    public ElectricArmorItem(ElectricCharge.Spec spec, Properties properties) {
        super(spec, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        if (ModList.get().isLoaded("curios")) {
            builder.accept(Component.translatable("info.oldustry.curios.back").withStyle(ChatFormatting.DARK_AQUA));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @javax.annotation.Nullable EquipmentSlot slot) {
        // Chestplate slot, or Curios (slot null but still worn — CuriosPackCharge ticks those).
        if (!(entity instanceof Player player) || slot == null) {
            return;
        }
        chargePlayer(stack, player);
    }

    /**
     * Charge hotbar/inventory (+ offhand) from this pack.
     * {@code getNonEquipmentItems()} includes the selected (main-hand) slot — Curios back uses the same path.
     */
    public void chargePlayer(ItemStack pack, Player player) {
        // Creative inventory sync + ENERGY mutations → re-equip spam / stack duplication.
        if (player.getAbilities().instabuild) {
            return;
        }
        int available = energy(pack);
        if (available <= 0) {
            return;
        }
        // Prefer held item first (common “charge what’s in hand” expectation).
        available = chargeAll(pack, java.util.List.of(player.getMainHandItem()), available);
        if (available > 0) {
            available = chargeAll(pack, player.getInventory().getNonEquipmentItems(), available);
        }
        if (available > 0) {
            chargeAll(pack, java.util.List.of(player.getOffhandItem()), available);
        }
    }

    private int chargeAll(ItemStack pack, Iterable<ItemStack> targets, int available) {
        for (ItemStack inv : targets) {
            if (!(inv.getItem() instanceof ElectricItem electric) || inv == pack) {
                continue;
            }
            int moved = electric.charge(inv, Math.min(available, spec().transfer()));
            if (moved > 0) {
                discharge(pack, moved, 99);
                available -= moved;
                if (available <= 0) {
                    return 0;
                }
            }
        }
        return available;
    }
}
