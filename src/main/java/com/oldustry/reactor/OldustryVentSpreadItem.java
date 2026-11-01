package com.oldustry.reactor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** IC2 {@code ItemReactorVentSpread}. Cools the four orthogonal components by a fixed amount. */
public final class OldustryVentSpreadItem extends Item implements OldustryReactorComponent {
    private final int side;

    public OldustryVentSpreadItem(int side, Properties properties) {
        super(properties);
        this.side = side;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        cool(reactor, x - 1, y);
        cool(reactor, x + 1, y);
        cool(reactor, x, y - 1);
        cool(reactor, x, y + 1);
    }

    private void cool(OldustryReactorGrid reactor, int x, int y) {
        ItemStack other = reactor.itemAt(x, y);
        if (!other.isEmpty() && other.getItem() instanceof OldustryReactorComponent component
            && component.canStoreHeat(reactor, other, x, y)) {
            component.alterHeat(reactor, other, x, y, -side);
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
        return heat;
    }

    @Override
    public float influenceExplosion(OldustryReactorGrid reactor, ItemStack stack) {
        return 0f;
    }
}
