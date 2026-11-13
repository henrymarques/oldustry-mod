package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineLayout;
import com.oldustry.machine.OldustryMachineMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** JAR {@code GuiCropmatron}: vertical energy bolt at 26,38. */
public final class OldustryCropMatronScreen extends OldustryMachineScreen {
    public OldustryCropMatronScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        int capacity = menu.data().get(OldustryMachineMenu.DATA_CAPACITY);
        int charge = OldustryMachineLayout.scaled(energy, capacity, 14);
        if (charge > 0) {
            blit(graphics, texture, 26, 38 + 14 - charge, 176, 14 - charge, 14, charge);
        }
    }
}
