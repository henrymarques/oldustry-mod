package com.oldustry.reactor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** IC2 {@code ItemReactorPlating}. Adds max heat and multiplies the heat-effect modifier. */
public final class OldustryPlatingItem extends Item implements OldustryReactorComponent {
    private final int extra;
    private final float modifier;

    public OldustryPlatingItem(int extra, float modifier, Properties properties) {
        super(properties);
        this.extra = extra;
        this.modifier = modifier;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        reactor.setMaxHeat(reactor.maxHeat() + extra);
        reactor.setHeatEffect(reactor.heatEffect() * modifier);
    }

    @Override
    public boolean acceptUraniumPulse(OldustryReactorGrid reactor, ItemStack stack, ItemStack pulser, int x, int y, int pulseX, int pulseY) {
        return false;
    }

    @Override
    public boolean canStoreHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return false;
    }

    @Override
    public int alterHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y, int heat) {
        return heat;
    }

    @Override
    public float influenceExplosion(OldustryReactorGrid reactor, ItemStack stack) {
        return modifier >= 1f ? 0f : modifier;
    }
}
