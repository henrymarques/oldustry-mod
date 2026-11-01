package com.oldustry.reactor;

import java.util.ArrayList;
import java.util.List;

import com.oldustry.registry.OldustryItems;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** IC2 {@code ItemReactorUranium}. {@code cells} is 1, 2 or 4. */
public final class OldustryUraniumCellItem extends Item implements OldustryReactorComponent {
    private final int cells;

    public OldustryUraniumCellItem(int cells, Properties properties) {
        super(properties);
        this.cells = cells;
    }

    public int cells() {
        return cells;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        if (!reactor.produceEnergy()) {
            return;
        }
        for (int iteration = 0; iteration < cells; iteration++) {
            int pulses = OldustryReactor.selfPulses(cells);
            for (int i = 0; i < pulses; i++) {
                acceptUraniumPulse(reactor, stack, stack, x, y, x, y);
            }
            pulses += pulseAt(reactor, x - 1, y, stack, x, y);
            pulses += pulseAt(reactor, x + 1, y, stack, x, y);
            pulses += pulseAt(reactor, x, y - 1, stack, x, y);
            pulses += pulseAt(reactor, x, y + 1, stack, x, y);
            int heat = OldustryReactor.triangular(pulses) * 4;
            List<Neighbor> acceptors = new ArrayList<>(4);
            consider(reactor, x - 1, y, acceptors);
            consider(reactor, x + 1, y, acceptors);
            consider(reactor, x, y - 1, acceptors);
            consider(reactor, x, y + 1, acceptors);
            while (!acceptors.isEmpty() && heat > 0) {
                int share = heat / acceptors.size();
                heat -= share;
                Neighbor neighbor = acceptors.remove(0);
                OldustryReactorComponent component = (OldustryReactorComponent) neighbor.stack.getItem();
                heat += component.alterHeat(reactor, neighbor.stack, neighbor.x, neighbor.y, share);
            }
            if (heat > 0) {
                reactor.addHeat(heat);
            }
        }
        if (OldustryReactor.fuelSpent(stack.getDamageValue())) {
            if (OldustryReactor.keepNearDepleted(reactor.random().nextInt(3))) {
                reactor.setItemAt(x, y, new ItemStack(OldustryItems.NEAR_DEPLETED_URANIUM_CELL.get(), cells));
            } else {
                reactor.setItemAt(x, y, ItemStack.EMPTY);
            }
        } else {
            stack.setDamageValue(stack.getDamageValue() + 1);
        }
    }

    @Override
    public boolean acceptUraniumPulse(OldustryReactorGrid reactor, ItemStack stack, ItemStack pulser, int x, int y, int pulseX, int pulseY) {
        reactor.addOutput(1f);
        return true;
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
        return 2f * cells;
    }

    private static int pulseAt(OldustryReactorGrid reactor, int x, int y, ItemStack me, int meX, int meY) {
        ItemStack other = reactor.itemAt(x, y);
        if (!other.isEmpty() && other.getItem() instanceof OldustryReactorComponent component
            && component.acceptUraniumPulse(reactor, other, me, x, y, meX, meY)) {
            return 1;
        }
        return 0;
    }

    private static void consider(OldustryReactorGrid reactor, int x, int y, List<Neighbor> acceptors) {
        ItemStack other = reactor.itemAt(x, y);
        if (!other.isEmpty() && other.getItem() instanceof OldustryReactorComponent component
            && component.canStoreHeat(reactor, other, x, y)) {
            acceptors.add(new Neighbor(other, x, y));
        }
    }

    private record Neighbor(ItemStack stack, int x, int y) {}
}
