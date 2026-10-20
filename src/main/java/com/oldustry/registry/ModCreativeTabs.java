package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.FuelJetpackItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Oldustry.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("oldustry", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.oldustry"))
        .icon(() -> new ItemStack(ModItems.REFINED_IRON_INGOT.get()))
        .displayItems((params, output) -> {
            for (var entry : ModItems.ITEMS.getEntries()) {
                Item item = entry.get();
                if (item == ModItems.DEBUG_ITEM.get()) {
                    continue;
                }
                if (item == ModItems.FILLED_FUEL_CAN.get()) {
                    ItemStack filled = new ItemStack(item);
                    // shortcut: JAR has no fixed can capacity; one coalfuel cell (2548) makes this creative sample usable.
                    filled.set(ModDataComponents.FUEL.get(), 2548);
                    assert filled.get(ModDataComponents.FUEL.get()) == 2548;
                    output.accept(filled);
                    continue;
                }
                output.accept(item);
                if (item instanceof ElectricItem electric) {
                    ItemStack full = new ItemStack(item);
                    ElectricItem.setEnergy(full, electric.spec().max());
                    assert ElectricItem.energy(full) == electric.spec().max();
                    output.accept(full);
                } else if (item instanceof FuelJetpackItem) {
                    ItemStack full = new ItemStack(item);
                    FuelJetpackItem.setFuel(full, FuelJetpackItem.MAX_FUEL);
                    assert FuelJetpackItem.fuel(full) == FuelJetpackItem.MAX_FUEL;
                    output.accept(full);
                }
            }
        })
        .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }

    private ModCreativeTabs() {}
}
