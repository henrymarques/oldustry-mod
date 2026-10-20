package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.registry.ModDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class ElectricItem extends Item {
    private final ElectricCharge.Spec spec;

    public ElectricItem(ElectricCharge.Spec spec, Properties properties) {
        super(properties.stacksTo(1).component(ModDataComponents.ENERGY.get(), 0));
        this.spec = spec;
    }

    public ElectricCharge.Spec spec() {
        return spec;
    }

    public static int energy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    public static void setEnergy(ItemStack stack, int energy) {
        int next = Math.max(0, energy);
        // Skip no-op writes (LivingEntityEquipmentMixin ignores ENERGY diffs; still avoid churn).
        if (energy(stack) == next) {
            return;
        }
        stack.set(ModDataComponents.ENERGY.get(), next);
    }

    public int charge(ItemStack stack, int amount) {
        int energy = energy(stack);
        int moved = ElectricCharge.charge(energy, amount, spec);
        setEnergy(stack, energy + moved);
        return moved;
    }

    /** JAR {@code charge(..., ignoreTransferLimit=true)} — room only, no transfer cap. */
    public int chargeIgnoreTransfer(ItemStack stack, int amount) {
        int energy = energy(stack);
        int moved = ElectricCharge.chargeIgnoreTransfer(energy, amount, spec);
        setEnergy(stack, energy + moved);
        return moved;
    }

    public int discharge(ItemStack stack, int amount, int sinkTier) {
        int energy = energy(stack);
        int moved = ElectricCharge.discharge(energy, amount, spec, sinkTier);
        setEnergy(stack, energy - moved);
        return moved;
    }

    /** JAR {@code discharge(..., ignoreTransferLimit=true)}. */
    public int dischargeIgnoreTransfer(ItemStack stack, int amount, int sinkTier) {
        int energy = energy(stack);
        int moved = ElectricCharge.dischargeIgnoreTransfer(energy, amount, spec, sinkTier);
        setEnergy(stack, energy - moved);
        return moved;
    }

    /**
     * JAR {@code instanceof ItemBattery} — RE / energy crystal / lapotron cells.
     * Subclasses (tools, packs, scanners) are not cells.
     */
    public boolean isBatteryCell() {
        Class<?> c = getClass();
        return c == ElectricItem.class || c == ReBatteryItem.class;
    }

    /**
     * Batpack (and solar/static) rewrite ENERGY every tick. Default NeoForge path treats any
     * component change on non-damageable items as a new tool → mining progress resets.
     */
    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        if (ChargeComponents.onlyChargeDiffers(oldStack, newStack)) {
            return false;
        }
        return super.shouldCauseBlockBreakReset(oldStack, newStack);
    }

    /** Same ENERGY churn was bobbing the hand like a re-equip. */
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!slotChanged && ChargeComponents.onlyChargeDiffers(oldStack, newStack)) {
            return false;
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * energy(stack) / (float) spec.max());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fill = energy(stack) / (float) spec.max();
        return fill < 0.15f ? 0xFF5555 : (fill < 0.5f ? 0xFFAA00 : 0x55FF55);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("info.oldustry.eu.stored", energy(stack), spec.max()).withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("info.oldustry.eu.transfer", spec.transfer()).withStyle(ChatFormatting.DARK_GRAY));
        builder.accept(Component.translatable("info.oldustry.eu.tier", spec.tier()).withStyle(ChatFormatting.DARK_GRAY));
    }
}
