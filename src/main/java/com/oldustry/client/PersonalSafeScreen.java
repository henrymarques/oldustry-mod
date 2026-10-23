package com.oldustry.client;

import com.oldustry.Oldustry;
import com.oldustry.block.PersonalSafeMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** {@code GuiPersonalChest}: 176×222, title at y 6, inventory label at y 128. */
public class PersonalSafeScreen extends AbstractContainerScreen<PersonalSafeMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/gui/container/personal_safe.png");

    public PersonalSafeScreen(PersonalSafeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 222);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
    }
}
