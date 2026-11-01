package com.oldustry.reactor;

import net.minecraft.world.item.ItemStack;

/** IC2 {@code IReactorComponent}. */
public interface OldustryReactorComponent {
    void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y);

    boolean acceptUraniumPulse(OldustryReactorGrid reactor, ItemStack stack, ItemStack pulser, int x, int y, int pulseX, int pulseY);

    boolean canStoreHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y);

    default int maxHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return 0;
    }

    default int currentHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return 0;
    }

    int alterHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y, int heat);

    float influenceExplosion(OldustryReactorGrid reactor, ItemStack stack);
}
