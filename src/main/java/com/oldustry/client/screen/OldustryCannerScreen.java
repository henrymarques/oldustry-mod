package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OldustryCannerScreen extends OldustryMachineScreen {
    public OldustryCannerScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        int capacity = menu.data().get(OldustryMachineMenu.DATA_CAPACITY);
        int progress = menu.data().get(OldustryMachineMenu.DATA_PROGRESS);
        int duration = menu.data().get(OldustryMachineMenu.DATA_DURATION);
        // JAR GuiCanner: energy (31,27) 14px; progress (74,36) 34×13
        int charge = OldustryMachineLayout.scaled(energy, capacity, 14);
        if (charge > 0) {
            blit(graphics, texture, 31, 27 + 14 - charge, 176, 14 - charge, 14, charge);
        }
        int arrow = OldustryMachineLayout.scaled(progress, duration, 34);
        if (arrow > 0) {
            blit(graphics, texture, 74, 36, 176, 15, arrow, 13);
        }
    }
}
