package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMatter;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OldustryMassFabricatorScreen extends OldustryMachineScreen {
    public OldustryMassFabricatorScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        graphics.text(font, Component.translatable("container.oldustry.matter.progress"), 16, 20, LABEL, false);
        graphics.text(font, OldustryMatter.progress(energy) + "%", 16, 28, LABEL, false);
        int scrap = menu.data().get(OldustryMachineMenu.DATA_EXTRA_OR_MINER_ACTIVE);
        if (scrap > 0) {
            graphics.text(font, Component.translatable("container.oldustry.matter.amplifier"), 16, 44, LABEL, false);
            graphics.text(font, Integer.toString(scrap), 16, 56, LABEL, false);
        }
    }
}
