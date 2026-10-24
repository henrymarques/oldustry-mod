package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OldustryGeneratorScreen extends OldustryMachineScreen {
    public OldustryGeneratorScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        int capacity = menu.data().get(OldustryMachineMenu.DATA_CAPACITY);
        int fuel = menu.data().get(OldustryMachineMenu.DATA_GAUGE);
        int fuelMax = menu.data().get(OldustryMachineMenu.DATA_GAUGE_MAX_OR_MINER_MODE);
        if (fuel > 0) {
            int flame = OldustryMachineLayout.scaled(fuel, fuelMax, 12);
            blit(graphics, texture, 66, 36 + 12 - flame, 176, 12 - flame, 14, flame + 2);
        }
        int bar = OldustryMachineLayout.scaled(energy, capacity, 24);
        blit(graphics, texture, 94, 35, 176, 14, bar, 17);
    }
}
