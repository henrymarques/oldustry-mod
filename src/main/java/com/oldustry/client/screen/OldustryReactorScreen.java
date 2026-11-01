package com.oldustry.client.screen;

import com.oldustry.Oldustry;
import com.oldustry.OldustryConfig;
import com.oldustry.block.OldustryReactorMenu;
import com.oldustry.reactor.OldustryReactor;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** JAR {@code GUI6byN}. The name band shows heat and EU/t; both don't fit next to the title. */
public final class OldustryReactorScreen extends AbstractContainerScreen<OldustryReactorMenu> {
    private static final int LABEL = 0xFF404040;

    public OldustryReactorScreen(OldustryReactorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, OldustryReactor.GUI_WIDTH, OldustryReactor.GUI_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelY = -1000;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int columns = menu.columns();
        Identifier texture = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/gui/container/reactor_" + columns + ".png");
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        int heat = menu.data().get(0);
        int max = menu.data().get(1);
        int eu = OldustryReactor.sent(menu.data().get(2), OldustryConfig.REACTOR_OUTPUT.get());
        Component heatText = Component.translatable("container.oldustry.reactor.heat", heat, max);
        Component euText = Component.translatable("container.oldustry.reactor.output", eu);
        graphics.text(font, heatText, 8, 6, LABEL, false);
        graphics.text(font, euText, imageWidth - 8 - font.width(euText), 6, LABEL, false);
    }
}
