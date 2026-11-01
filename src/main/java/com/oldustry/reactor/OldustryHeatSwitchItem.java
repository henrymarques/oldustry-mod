package com.oldustry.reactor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;

/** IC2 {@code ItemReactorHeatSwitch}. Moves heat toward the average of itself, the reactor and its sides. */
public final class OldustryHeatSwitchItem extends OldustryHeatStorageItem {
    private final int side;
    private final int reactorRate;

    public OldustryHeatSwitchItem(int capacity, int side, int reactorRate, Properties properties) {
        super(capacity, properties);
        this.side = side;
        this.reactorRate = reactorRate;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        List<Neighbor> acceptors = new ArrayList<>(4);
        double med = currentHeat(reactor, stack, x, y) / (double) maxHeat(reactor, stack, x, y);
        double neighborSum = 0;
        if (side > 0) {
            neighborSum += consider(reactor, x - 1, y, acceptors);
            neighborSum += consider(reactor, x + 1, y, acceptors);
            neighborSum += consider(reactor, x, y - 1, acceptors);
            neighborSum += consider(reactor, x, y + 1, acceptors);
        }
        double reactorRatio = reactor.maxHeat() <= 0 ? 0 : reactor.heat() / (double) reactor.maxHeat();
        med = OldustryReactor.heatMedian(med, reactorRate > 0, reactorRatio, neighborSum, acceptors.size());
        int mine = 0;
        if (side > 0) {
            for (Neighbor neighbor : acceptors) {
                OldustryReactorComponent component = (OldustryReactorComponent) neighbor.stack.getItem();
                int add = (int) (med * component.maxHeat(reactor, neighbor.stack, neighbor.x, neighbor.y))
                    - component.currentHeat(reactor, neighbor.stack, neighbor.x, neighbor.y);
                add = OldustryReactor.clampTransfer(add, side);
                mine -= add;
                mine += component.alterHeat(reactor, neighbor.stack, neighbor.x, neighbor.y, add);
            }
        }
        if (reactorRate > 0) {
            int add = (int) (med * reactor.maxHeat()) - reactor.heat();
            add = OldustryReactor.clampTransfer(add, reactorRate);
            mine -= add;
            reactor.setHeat(reactor.heat() + add);
        }
        alterHeat(reactor, stack, x, y, mine);
    }

    private static double consider(OldustryReactorGrid reactor, int x, int y, List<Neighbor> acceptors) {
        ItemStack other = reactor.itemAt(x, y);
        if (other.isEmpty() || !(other.getItem() instanceof OldustryReactorComponent component)) {
            return 0;
        }
        if (!component.canStoreHeat(reactor, other, x, y)) {
            return 0;
        }
        acceptors.add(new Neighbor(other, x, y));
        int max = component.maxHeat(reactor, other, x, y);
        if (max <= 0) {
            return 0;
        }
        return component.currentHeat(reactor, other, x, y) / (double) max;
    }

    private record Neighbor(ItemStack stack, int x, int y) {}
}
