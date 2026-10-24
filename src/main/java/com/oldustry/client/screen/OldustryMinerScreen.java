package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OldustryMinerScreen extends OldustryMachineScreen {
    private String lastMinerStatus;

    public OldustryMinerScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        int capacity = menu.data().get(OldustryMachineMenu.DATA_CAPACITY);
        int progress = menu.data().get(OldustryMachineMenu.DATA_PROGRESS);
        // JAR GuiMiner: 14px bolt under the pipe. No progress arrow on this texture.
        int charge = OldustryMachineLayout.scaled(energy, capacity, 14);
        if (charge > 0) {
            blit(graphics, texture, 81, 41 + 14 - charge, 176, 14 - charge, 14, charge);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        String status = minerStatus();
        if (!status.equals(lastMinerStatus)) {
            lastMinerStatus = status;
            if (status.startsWith("no_") && minecraft.player != null) {
                minecraft.player.sendOverlayMessage(Component.translatable("info.oldustry.miner." + status));
            }
        }
    }

    private String minerStatus() {
        return OldustryMachineLayout.minerStatus(menu.data().get(OldustryMachineMenu.DATA_EXTRA_OR_MINER_ACTIVE) != 0,
            menu.slots.get(0).hasItem(), menu.slots.get(2).hasItem(),
            menu.data().get(OldustryMachineMenu.DATA_ENERGY), menu.data().get(OldustryMachineMenu.DATA_GAUGE_MAX_OR_MINER_MODE));
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(font, Component.translatable("info.oldustry.miner." + minerStatus()),
            8, inventoryLabelY - OldustryMachineLayout.MINER_STATUS_GAP, LABEL, false);
    }
}
