package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OldustryGeoGeneratorScreen extends OldustryMachineScreen {
    public OldustryGeoGeneratorScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        int capacity = menu.data().get(OldustryMachineMenu.DATA_CAPACITY);
        int fuel = menu.data().get(OldustryMachineMenu.DATA_GAUGE);
        int fuelMax = menu.data().get(OldustryMachineMenu.DATA_GAUGE_MAX_OR_MINER_MODE);
        if (fuel > 0) {
            int lava = OldustryMachineLayout.scaled(fuel, fuelMax, 14);
            blit(graphics, texture, 66, 36 + 14 - lava, 176, 14 - lava, 14, lava);
        }
        if (energy > 0) {
            blit(graphics, texture, 89, 39, 177, 19, OldustryMachineLayout.scaled(energy, capacity, 22), 6);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if ((hoveredSlot == null || !hoveredSlot.hasItem())
            && OldustryMachineLayout.inside(OldustryMachineLayout.lavaGauge(), mouseX - leftPos, mouseY - topPos)) {
            graphics.setTooltipForNextFrame(font,
                Component.translatable("container.oldustry.lava", menu.data().get(OldustryMachineMenu.DATA_GAUGE), menu.data().get(OldustryMachineMenu.DATA_GAUGE_MAX_OR_MINER_MODE)),
                mouseX, mouseY);
        }
    }
}
