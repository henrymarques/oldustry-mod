package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public abstract class OldustryProcessMachineScreen extends OldustryMachineScreen {
    public OldustryProcessMachineScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        int capacity = menu.data().get(OldustryMachineMenu.DATA_CAPACITY);
        int progress = menu.data().get(OldustryMachineMenu.DATA_PROGRESS);
        int duration = menu.data().get(OldustryMachineMenu.DATA_DURATION);
        int charge = OldustryMachineLayout.scaled(energy, capacity, 14);
        if (charge > 0) {
            blit(graphics, texture, 56, 36 + 14 - charge, 176, 14 - charge, 14, charge);
        }
        int arrow = OldustryMachineLayout.scaled(progress, duration, 24);
        if (arrow > 0) {
            blit(graphics, texture, 79, 34, 176, 14, arrow + 1, 16);
        }
    }
}
