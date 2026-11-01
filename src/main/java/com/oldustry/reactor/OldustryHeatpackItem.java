package com.oldustry.reactor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** IC2 {@code ItemReactorHeatpack}. Stack size scales the heat it pushes into neighbors. */
public final class OldustryHeatpackItem extends Item implements OldustryReactorComponent {
    private final int maxPer;
    private final int heatPer;

    public OldustryHeatpackItem(int maxPer, int heatPer, Properties properties) {
        super(properties);
        this.maxPer = maxPer;
        this.heatPer = heatPer;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        int count = stack.getCount();
        heat(reactor, count, x + 1, y);
        heat(reactor, count, x - 1, y);
        heat(reactor, count, x, y + 1);
        heat(reactor, count, x, y - 1);
    }

    private void heat(OldustryReactorGrid reactor, int count, int x, int y) {
        ItemStack other = reactor.itemAt(x, y);
        if (other.isEmpty() || !(other.getItem() instanceof OldustryReactorComponent component)) {
            return;
        }
        if (!component.canStoreHeat(reactor, other, x, y)) {
            return;
        }
        int add = OldustryReactor.heatpackAdd(
            count,
            heatPer,
            maxPer,
            reactor.heat(),
            component.currentHeat(reactor, other, x, y)
        );
        if (add > 0) {
            component.alterHeat(reactor, other, x, y, add);
        }
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
        return 0;
    }

    @Override
    public float influenceExplosion(OldustryReactorGrid reactor, ItemStack stack) {
        return OldustryReactor.heatpackBlast(stack.getCount());
    }
}
