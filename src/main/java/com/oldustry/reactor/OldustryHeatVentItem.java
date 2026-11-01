package com.oldustry.reactor;

import net.minecraft.world.item.ItemStack;

/** IC2 {@code ItemReactorVent}. Pulls reactor heat first; if that does not fit, it skips the self-cool. */
public final class OldustryHeatVentItem extends OldustryHeatStorageItem {
    private final int selfVent;
    private final int reactorVent;

    public OldustryHeatVentItem(int capacity, int selfVent, int reactorVent, Properties properties) {
        super(capacity, properties);
        this.selfVent = selfVent;
        this.reactorVent = reactorVent;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        if (reactorVent > 0) {
            int pull = OldustryReactor.ventPull(reactor.heat(), reactorVent);
            int left = reactor.heat() - pull;
            if (alterHeat(reactor, stack, x, y, pull) > 0) {
                return;
            }
            reactor.setHeat(left);
        }
        alterHeat(reactor, stack, x, y, -selfVent);
    }
}
