package com.oldustry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oldustry.item.equipment.QuantumSuitItem;

import net.minecraft.client.player.LocalPlayer;

/**
 * Shallow water blocks sprint before the eyes go under. Quantum legs in the JAR still sprinted there.
 * Swim sprint ({@code isSprintingPossible(true)}) is left to vanilla.
 */
@Mixin(LocalPlayer.class)
public class LocalPlayerSprintMixin {
    @Inject(method = "isSprintingPossible", at = @At("HEAD"), cancellable = true)
    private void oldustry$sprintInShallowWater(boolean allowedInShallowWater, CallbackInfoReturnable<Boolean> cir) {
        if (allowedInShallowWater) {
            return;
        }
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (!player.isInShallowWater() || player.isPassenger() || player.isMobilityRestricted()) {
            return;
        }
        if ((!player.getFoodData().hasEnoughFood() && !player.mayFly()) || !QuantumSuitItem.allowsShallowSprint(player)) {
            return;
        }
        cir.setReturnValue(true);
    }
}
