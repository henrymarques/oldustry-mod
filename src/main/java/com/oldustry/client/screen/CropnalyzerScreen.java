package com.oldustry.client.screen;

import com.oldustry.Oldustry;
import com.oldustry.crop.CropSeed;
import com.oldustry.crop.OldustryCrop;
import com.oldustry.menu.CropnalyzerMenu;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** JAR {@code GuiCropnalyzer}. */
public class CropnalyzerScreen extends AbstractContainerScreen<CropnalyzerMenu> {
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/gui/container/cropnalyzer.png");
    private static final int LABEL = 0xFF404040;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GROWTH = 0xFFAEFF2F;
    private static final int GAIN = 0xFFEEFF00;
    private static final int RES = 0xFF00CE91;

    public CropnalyzerScreen(CropnalyzerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 74;
        titleLabelY = 11;
        inventoryLabelY = 10000;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, LABEL, false);
        int level = menu.scannedLevel();
        if (level < 0) {
            return;
        }
        ItemStack out = menu.outputSeed();
        CropSeed seed = out.get(OldustryDataComponents.CROP_SEED.get());
        if (seed == null) {
            return;
        }
        OldustryCrop crop = OldustryCrop.byId(seed.id());
        if (level == 0) {
            graphics.text(font, "UNKNOWN", 8, 37, WHITE, false);
            return;
        }
        graphics.text(font, crop.getSerializedName(), 8, 37, WHITE, false);
        if (level >= 2) {
            graphics.text(font, "Tier: " + crop.tierRoman(), 8, 50, WHITE, false);
            graphics.text(font, "Discovered by:", 8, 73, WHITE, false);
            graphics.text(font, crop.discoveredBy(), 8, 86, WHITE, false);
        }
        if (level >= 3) {
            graphics.text(font, crop.desc(0), 8, 109, WHITE, false);
            graphics.text(font, crop.desc(1), 8, 122, WHITE, false);
        }
        if (level >= 4) {
            graphics.text(font, "Growth:", 118, 37, GROWTH, false);
            graphics.text(font, Integer.toString(seed.growth()), 118, 50, GROWTH, false);
            graphics.text(font, "Gain:", 118, 73, GAIN, false);
            graphics.text(font, Integer.toString(seed.gain()), 118, 86, GAIN, false);
            graphics.text(font, "Resis.:", 118, 109, RES, false);
            graphics.text(font, Integer.toString(seed.resistance()), 118, 122, RES, false);
        }
    }
}
