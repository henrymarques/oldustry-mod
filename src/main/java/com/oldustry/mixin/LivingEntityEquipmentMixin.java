package com.oldustry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oldustry.item.ChargeComponents;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.NanoArmor;
import com.oldustry.item.NanoSuitItem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * ENERGY/FUEL/walk-anchor updates must not count as re-equip (sound, LivingEquipmentChangeEvent,
 * SetEquipmentPacket). {@link ItemStack#matches} includes those components.
 */
@Mixin(LivingEntity.class)
public class LivingEntityEquipmentMixin {
    @Inject(method = "equipmentHasChanged", at = @At("HEAD"), cancellable = true)
    private void oldustry$ignoreChargeComponents(ItemStack previous, ItemStack current, CallbackInfoReturnable<Boolean> cir) {
        if (ChargeComponents.onlyChargeDiffers(previous, current) && !nanoIconsCrossed(previous, current)) {
            cir.setReturnValue(false);
        }
    }

    /** The armor bar is the ARMOR modifier, applied only while charge covers one point. */
    private static boolean nanoIconsCrossed(ItemStack previous, ItemStack current) {
        if (!(previous.getItem() instanceof NanoSuitItem) || !(current.getItem() instanceof NanoSuitItem)) {
            return false;
        }
        boolean was = ElectricItem.energy(previous) >= NanoArmor.EU_PER_DAMAGE;
        boolean now = ElectricItem.energy(current) >= NanoArmor.EU_PER_DAMAGE;
        return was != now;
    }

    /** Creative/container sync can call setItemSlot with a copy that only differs in ENERGY. */
    @Inject(method = "onEquipItem", at = @At("HEAD"), cancellable = true)
    private void oldustry$skipChargeOnlyEquipSound(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
        if (ChargeComponents.onlyChargeDiffers(oldStack, newStack)) {
            ci.cancel();
        }
    }
}
