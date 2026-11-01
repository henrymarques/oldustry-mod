package com.oldustry.reactor;

import com.oldustry.registry.OldustryItems;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** IC2 {@code ItemReactorDepletedUranium}. Pulses lower the damage until it becomes re-enriched. */
public final class OldustryIsotopeCellItem extends Item implements OldustryReactorComponent {
    public OldustryIsotopeCellItem(Properties properties) {
        super(properties);
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {}

    @Override
    public boolean acceptUraniumPulse(OldustryReactorGrid reactor, ItemStack stack, ItemStack pulser, int x, int y, int pulseX, int pulseY) {
        int level = OldustryReactor.isotopeDamage(stack.getDamageValue(), reactor.heat());
        if (level <= 0) {
            reactor.setItemAt(x, y, new ItemStack(OldustryItems.RE_ENRICHED_URANIUM_CELL.get()));
        } else {
            stack.setDamageValue(level);
        }
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
        return 0f;
    }
}
