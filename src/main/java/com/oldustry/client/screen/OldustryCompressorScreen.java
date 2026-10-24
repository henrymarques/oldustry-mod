package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class OldustryCompressorScreen extends OldustryProcessMachineScreen {
    public OldustryCompressorScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
