package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OldustryPumpScreen extends OldustryMachineScreen {
    public OldustryPumpScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        int capacity = menu.data().get(OldustryMachineMenu.DATA_CAPACITY);
        int fuel = menu.data().get(OldustryMachineMenu.DATA_GAUGE);
        int fuelMax = menu.data().get(OldustryMachineMenu.DATA_GAUGE_MAX_OR_MINER_MODE);
        // JAR GuiPump: EU bolt at 62,36; charge column at 99 (41px) with the frame on top.
        int charge = OldustryMachineLayout.scaled(energy, capacity, 14);
        if (charge > 0) {
            blit(graphics, texture, 62, 36 + 14 - charge, 176, 14 - charge, 14, charge);
        }
        int fill = OldustryMachineLayout.scaled(fuel, fuelMax, 41);
        blit(graphics, texture, 99, 61 - fill, 176, 55, 12, 5);
        if (fill > 0) {
            blit(graphics, texture, 99, 25 + 41 - fill, 176, 14, 12, fill);
        }
        blit(graphics, texture, 98, 19, 188, 14, 13, 47);
    }
}
