package com.oldustry.reactor;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * IC2 {@code ItemReactorCondensator}. {@code maxDamage} is the crafted capacity plus one.
 * Current heat reported to exchangers stays 0, so they keep pushing heat in.
 */
public final class OldustryCondensatorItem extends Item implements OldustryReactorComponent {
    private final int maxDamage;

    public OldustryCondensatorItem(int capacity, Properties properties) {
        super(properties);
        this.maxDamage = capacity + 1;
    }

    public int maxDamage() {
        return maxDamage;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {}

    @Override
    public boolean acceptUraniumPulse(OldustryReactorGrid reactor, ItemStack stack, ItemStack pulser, int x, int y, int pulseX, int pulseY) {
        return false;
    }

    @Override
    public boolean canStoreHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return OldustryReactor.condensatorOpen(OldustryReactorStore.wear(stack), maxDamage);
    }

    @Override
    public int maxHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return maxDamage;
    }

    @Override
    public int currentHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return 0;
    }

    @Override
    public int alterHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y, int heat) {
        int wear = OldustryReactorStore.wear(stack);
        OldustryReactorStore.setWear(stack, OldustryReactor.condensatorWear(wear, maxDamage, heat));
        return OldustryReactor.condensatorLeftover(wear, maxDamage, heat);
    }

    @Override
    public float influenceExplosion(OldustryReactorGrid reactor, ItemStack stack) {
        return 0f;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return OldustryReactorStore.wear(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return OldustryReactorStore.bar(OldustryReactorStore.wear(stack), maxDamage);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF5555;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        int stored = Math.max(0, OldustryReactorStore.wear(stack));
        builder.accept(Component.translatable("info.oldustry.reactor.heat", stored, maxDamage - 1).withStyle(ChatFormatting.GRAY));
    }
}
