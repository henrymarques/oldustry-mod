package com.oldustry.client.screen;

import com.oldustry.machine.OldustryMachineMenu;
import com.oldustry.machine.OldustryMachineLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class OldustryStorageScreen extends OldustryMachineScreen {
    public OldustryStorageScreen(OldustryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture) {
        int energy = menu.data().get(OldustryMachineMenu.DATA_ENERGY);
        int capacity = menu.data().get(OldustryMachineMenu.DATA_CAPACITY);
        if (energy > 0) {
            blit(graphics, texture, 79, 34, 176, 14, OldustryMachineLayout.scaled(energy, capacity, 24) + 1, 16);
        }
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new RedstoneButton(leftPos + 152, topPos + 4, button -> {
            if (minecraft.player != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
        }));
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(font, Component.translatable("container.oldustry.armor"), 8, imageHeight - 123, LABEL, false);
        graphics.text(font, Component.translatable("container.oldustry.electric.level"), 79, 25, LABEL, false);
        int energy = Math.min(menu.data().get(OldustryMachineMenu.DATA_ENERGY), menu.data().get(OldustryMachineMenu.DATA_CAPACITY));
        graphics.text(font, " " + energy, 110, 35, LABEL, false);
        graphics.text(font, "/" + menu.data().get(OldustryMachineMenu.DATA_CAPACITY), 110, 45, LABEL, false);
        graphics.text(font, Component.translatable("container.oldustry.electric.output", menu.kind().packet), 85, 60, LABEL, false);
    }

    /** JAR GuiIconButton: widget chrome plus a redstone dust icon. */
    private static final class RedstoneButton extends Button.Plain {
        private static final ItemStack ICON = new ItemStack(Items.REDSTONE);

        private RedstoneButton(int x, int y, OnPress onPress) {
            super(x, y, 20, 20, Component.empty(), onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            extractDefaultSprite(graphics);
            graphics.item(ICON, getX() + 2, getY() + 1);
        }
    }
}
