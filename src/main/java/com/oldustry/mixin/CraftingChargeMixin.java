package com.oldustry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.item.ElectricItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/** JAR {@code AdvRecipe} / {@code AdvShapelessRecipe}: electric ingredients fill the electric result. */
@Mixin({ShapedRecipe.class, ShapelessRecipe.class})
public class CraftingChargeMixin {
    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void oldustry$carryCharge(CraftingInput input, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result.isEmpty() || !(result.getItem() instanceof ElectricItem electric)) {
            return;
        }
        long sum = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack offer = input.getItem(i);
            if (offer.getItem() instanceof ElectricItem) {
                sum += ElectricItem.energy(offer);
            }
        }
        int carried = ElectricCharge.carriedCharge(electric.spec().max(), sum);
        if (carried > 0) {
            ElectricItem.setEnergy(result, carried);
        }
    }
}
