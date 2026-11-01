package com.oldustry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oldustry.item.ChargeComponents;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.equipment.NanoArmor;
import com.oldustry.item.equipment.NanoSuitItem;
import com.oldustry.item.equipment.QuantumArmor;
import com.oldustry.item.equipment.QuantumSuitItem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * ENERGY/FUEL/walk-anchor updates must not count as re-equip (sound, LivingEquipmentChangeEvent,
 * SetEquipmentPacket). {@link ItemStack#matches} includes those components.
 */
@Mixin(LivingEntity.class)
public class LivingEntityEquipmentMixin {
    @Inject(method = "equipmentHasChanged", at = @At("HEAD"), cancellable = true)
    private void oldustry$ignoreChargeComponents(ItemStack previous, ItemStack current, CallbackInfoReturnable<Boolean> cir) {
        if (ChargeComponents.onlyChargeDiffers(previous, current) && !armorIconsCrossed(previous, current)) {
            cir.setReturnValue(false);
        }
    }

    /** The armor bar is the ARMOR modifier, applied only while charge covers one point. */
    private static boolean armorIconsCrossed(ItemStack previous, ItemStack current) {
        int line = iconLine(previous, current);
        if (line < 0) {
            return false;
        }
        boolean was = ElectricItem.energy(previous) >= line;
        boolean now = ElectricItem.energy(current) >= line;
        return was != now;
    }

    private static int iconLine(ItemStack previous, ItemStack current) {
        if (previous.getItem() instanceof NanoSuitItem && current.getItem() instanceof NanoSuitItem) {
            return NanoArmor.EU_PER_DAMAGE;
        }
        if (previous.getItem() instanceof QuantumSuitItem && current.getItem() instanceof QuantumSuitItem) {
            return QuantumArmor.EU_PER_DAMAGE;
        }
        return -1;
    }

    /**
     * 26.2 skips {@code getFluidFallingAdjustedMovement} while sprinting. Quantum legs keep that sink.
     */
    @Redirect(
        method = "getFluidFallingAdjustedMovement",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z")
    )
    private boolean oldustry$waterSinkWhileQuantumSprint(LivingEntity entity) {
        if (entity instanceof Player player && player.isInWater() && QuantumSuitItem.allowsShallowSprint(player)) {
            return false;
        }
        return entity.isSprinting();
    }

    /** Creative/container sync can call setItemSlot with a copy that only differs in ENERGY. */
    @Inject(method = "onEquipItem", at = @At("HEAD"), cancellable = true)
    private void oldustry$skipChargeOnlyEquipSound(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
        if (ChargeComponents.onlyChargeDiffers(oldStack, newStack)) {
            ci.cancel();
        }
    }
}
