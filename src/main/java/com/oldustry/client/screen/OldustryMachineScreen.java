package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public abstract class OldustryMachineScreen extends AbstractContainerScreen<OldustryMachineMenu> {
    // ARGB — GuiGraphicsExtractor skips text when alpha == 0 (0x404040 alone is invisible).
    protected static final int LABEL = 0xFF404040;

    public static OldustryMachineScreen create(OldustryMachineMenu menu, Inventory inventory, Component title) {
        return switch (menu.kind().type) {
            case MACERATOR -> new OldustryMaceratorScreen(menu, inventory, title);
            case EXTRACTOR -> new OldustryExtractorScreen(menu, inventory, title);
            case COMPRESSOR -> new OldustryCompressorScreen(menu, inventory, title);
            case RECYCLER -> new OldustryRecyclerScreen(menu, inventory, title);
            case FURNACE -> new OldustryElectricFurnaceScreen(menu, inventory, title);
            case GENERATOR -> new OldustryGeneratorScreen(menu, inventory, title);
            case GEO -> new OldustryGeoGeneratorScreen(menu, inventory, title);
            case SOLAR -> new OldustrySolarPanelScreen(menu, inventory, title);
            case WATER -> new OldustryWaterMillScreen(menu, inventory, title);
            case WIND -> new OldustryWindMillScreen(menu, inventory, title);
            case STORAGE -> new OldustryStorageScreen(menu, inventory, title);
            case MINING -> new OldustryMinerScreen(menu, inventory, title);
            case PUMPING -> new OldustryPumpScreen(menu, inventory, title);
            case CANNING -> new OldustryCannerScreen(menu, inventory, title);
            case ELECTROLYZER -> new OldustryElectrolyzerScreen(menu, inventory, title);
            case INDUCTION -> new OldustryInductionScreen(menu, inventory, title);
            default -> throw new IllegalArgumentException("Machine has no GUI: " + menu.kind());
        };
    }

    protected OldustryMachineScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, OldustryMachineLayout.WIDTH, OldustryMachineLayout.height(menu.kind()));
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        Identifier texture = OldustryMachineLayout.texture(menu.kind());
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
        drawOverlay(graphics, texture);
    }

    protected abstract void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture);

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (hoveredSlot != null && hoveredSlot.hasItem()) {
            return;
        }
        int lx = mouseX - leftPos;
        int ly = mouseY - topPos;
        OldustryMachineLayout.Overlay overlay = OldustryMachineLayout.overlay(menu.kind());
        int[] gauge = OldustryMachineLayout.energyGauge(overlay);
        if (!OldustryMachineLayout.inside(gauge, lx, ly)) {
            return;
        }
        int energy = Math.min(menu.data().get(OldustryMachineMenu.DATA_ENERGY), menu.data().get(OldustryMachineMenu.DATA_CAPACITY));
        graphics.setTooltipForNextFrame(
            font,
            Component.translatable("container.oldustry.eu", energy, menu.data().get(OldustryMachineMenu.DATA_CAPACITY)),
            mouseX,
            mouseY
        );
    }

    protected final void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int u, int v, int w, int h) {
        if (w <= 0 || h <= 0) {
            return;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos + x, topPos + y, u, v, w, h, 256, 256);
    }

}
