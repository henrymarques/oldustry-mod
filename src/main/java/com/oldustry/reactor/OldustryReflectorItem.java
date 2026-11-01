package com.oldustry.reactor;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** IC2 {@code ItemReactorReflector}. Each pulse adds 1 EU and 1 wear. At the cap the reflector is gone. */
public final class OldustryReflectorItem extends Item implements OldustryReactorComponent {
    private final int life;

    public OldustryReflectorItem(int life, Properties properties) {
        super(properties);
        this.life = life;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {}

    @Override
    public boolean acceptUraniumPulse(OldustryReactorGrid reactor, ItemStack stack, ItemStack pulser, int x, int y, int pulseX, int pulseY) {
        reactor.addOutput(1f);
        if (OldustryReactor.reflectorBreaks(OldustryReactorStore.wear(stack), life)) {
            reactor.setItemAt(x, y, ItemStack.EMPTY);
        } else {
            OldustryReactorStore.setWear(stack, OldustryReactorStore.wear(stack) + 1);
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
        return -1f;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return OldustryReactorStore.wear(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return OldustryReactorStore.bar(OldustryReactorStore.wear(stack), life);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x55FF55;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        int left = Math.max(0, life - OldustryReactorStore.wear(stack));
        builder.accept(Component.translatable("info.oldustry.reactor.pulses", left, life).withStyle(ChatFormatting.GRAY));
    }
}
