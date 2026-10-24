package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class OldustryMaceratorScreen extends OldustryProcessMachineScreen {
    public OldustryMaceratorScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
