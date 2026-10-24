package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OldustryWaterMillScreen extends OldustryMachineScreen {
    public OldustryWaterMillScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int fuel = menu.data().get(OldustryMachineMenu.DATA_GAUGE);
        int fuelMax = menu.data().get(OldustryMachineMenu.DATA_GAUGE_MAX_OR_MINER_MODE);
        if (fuel > 0) {
            int drop = OldustryMachineLayout.scaled(fuel, fuelMax, 14);
            blit(graphics, texture, 80, 36 + 14 - drop, 176, 14 - drop, 14, drop);
        }
    }
}
