package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.furnace.ImprovedFurnaceMenu;
import com.oldustry.machine.MachineMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Oldustry.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ImprovedFurnaceMenu>> IMPROVED_FURNACE =
        MENUS.register("improved_furnace", () -> new MenuType<>(ImprovedFurnaceMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<MachineMenu>> MACHINE =
        MENUS.register("machine", () -> IMenuTypeExtension.create(MachineMenu::fromClient));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }

    private ModMenus() {}
}
