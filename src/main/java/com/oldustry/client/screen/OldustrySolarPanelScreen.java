package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OldustrySolarPanelScreen extends OldustryMachineScreen {
    public OldustrySolarPanelScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int extra = menu.data().get(OldustryMachineMenu.DATA_EXTRA_OR_MINER_ACTIVE);
        if (extra > 0) {
            blit(graphics, texture, 80, 45, 176, 0, 14, 14);
        }
    }
}
