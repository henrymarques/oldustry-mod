package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class OldustryElectricFurnaceScreen extends OldustryProcessMachineScreen {
    public OldustryElectricFurnaceScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
