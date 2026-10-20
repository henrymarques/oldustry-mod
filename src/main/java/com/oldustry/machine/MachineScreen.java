package com.oldustry.machine;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends AbstractContainerScreen<MachineMenu> {
    // ARGB — GuiGraphicsExtractor skips text when alpha == 0 (0x404040 alone is invisible).
    private static final int LABEL = 0xFF404040;
    private String lastMinerStatus;

    public MachineScreen(MachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, MachineGui.WIDTH, MachineGui.height(menu.kind()));
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (MachineGui.overlay(menu.kind()) != MachineGui.Overlay.MINER) {
            return;
        }
        String status = minerStatus();
        if (!status.equals(lastMinerStatus)) {
            lastMinerStatus = status;
            if (status.startsWith("no_") && minecraft.player != null) {
                minecraft.player.sendOverlayMessage(Component.translatable("info.oldustry.miner." + status));
            }
        }
    }

    private String minerStatus() {
        return MachineGui.minerStatus(menu.data().get(6) != 0,
            menu.slots.get(0).hasItem(), menu.slots.get(2).hasItem(),
            menu.data().get(0), menu.data().get(5));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        Identifier texture = MachineGui.texture(menu.kind());
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
        int energy = menu.data().get(0);
        int capacity = menu.data().get(1);
        int progress = menu.data().get(2);
        int duration = menu.data().get(3);
        int fuel = menu.data().get(4);
        int fuelMax = menu.data().get(5);
        int extra = menu.data().get(6);
        switch (MachineGui.overlay(menu.kind())) {
            case PROCESS -> {
                int charge = MachineGui.scaled(energy, capacity, 14);
                if (charge > 0) {
                    blit(graphics, texture, 56, 36 + 14 - charge, 176, 14 - charge, 14, charge);
                }
                int arrow = MachineGui.scaled(progress, duration, 24);
                if (arrow > 0) {
                    blit(graphics, texture, 79, 34, 176, 14, arrow + 1, 16);
                }
            }
            case GENERATOR -> {
                if (fuel > 0) {
                    int flame = MachineGui.scaled(fuel, fuelMax, 12);
                    blit(graphics, texture, 66, 36 + 12 - flame, 176, 12 - flame, 14, flame + 2);
                }
                int bar = MachineGui.scaled(energy, capacity, 24);
                blit(graphics, texture, 94, 35, 176, 14, bar, 17);
            }
            case GEO -> {
                if (fuel > 0) {
                    int lava = MachineGui.scaled(fuel, fuelMax, 14);
                    blit(graphics, texture, 66, 36 + 14 - lava, 176, 14 - lava, 14, lava);
                }
                if (energy > 0) {
                    blit(graphics, texture, 89, 39, 177, 19, MachineGui.scaled(energy, capacity, 22), 6);
                }
            }
            case SOLAR -> {
                if (extra > 0) {
                    blit(graphics, texture, 80, 45, 176, 0, 14, 14);
                }
            }
            case WATER -> {
                if (fuel > 0) {
                    int drop = MachineGui.scaled(fuel, fuelMax, 14);
                    blit(graphics, texture, 80, 36 + 14 - drop, 176, 14 - drop, 14, drop);
                }
            }
            case WIND -> {
                int wind = MachineGui.scaled(fuel, fuelMax, 14);
                blit(graphics, texture, 80, 45 + 14 - wind, 176, 14 - wind, 14, wind);
            }
            case STORAGE -> {
                if (energy > 0) {
                    blit(graphics, texture, 79, 34, 176, 14, MachineGui.scaled(energy, capacity, 24) + 1, 16);
                }
            }
            case MINER -> {
                // EU bar top-left; progress arrow center
                int charge = MachineGui.scaled(energy, capacity, 14);
                if (charge > 0) {
                    blit(graphics, texture, 8, 17 + 14 - charge, 176, 14 - charge, 14, charge);
                }
                int arrow = MachineGui.scaled(progress, duration, 24);
                if (arrow > 0) {
                    blit(graphics, texture, 79, 34, 176, 14, arrow + 1, 16);
                }
            }
            case PUMP -> {
                int charge = MachineGui.scaled(energy, capacity, 14);
                if (charge > 0) {
                    blit(graphics, texture, 8, 17 + 14 - charge, 176, 14 - charge, 14, charge);
                }
                // pump charge bar reuse fuel slots: data[4] = pumpCharge, data[5] = 200
                int pumpBar = MachineGui.scaled(fuel, fuelMax, 14);
                if (pumpBar > 0) {
                    blit(graphics, texture, 80, 36 + 14 - pumpBar, 176, 14 - pumpBar, 14, pumpBar);
                }
            }
            case CANNER -> {
                // JAR GuiCanner: energy (31,27) 14px; progress (74,36) 34×13
                int charge = MachineGui.scaled(energy, capacity, 14);
                if (charge > 0) {
                    blit(graphics, texture, 31, 27 + 14 - charge, 176, 14 - charge, 14, charge);
                }
                int arrow = MachineGui.scaled(progress, duration, 34);
                if (arrow > 0) {
                    blit(graphics, texture, 74, 36, 176, 15, arrow, 13);
                }
            }
            case ELECTROLYZER -> {
                if (energy > 0) {
                    blit(graphics, texture, 79, 34, 176, 14, MachineGui.scaled(energy, capacity, 24) + 1, 16);
                }
            }
            case INDUCTION -> {
                int charge = MachineGui.scaled(energy, capacity, 14);
                if (charge > 0) {
                    blit(graphics, texture, 56, 36 + 14 - charge, 176, 14 - charge, 14, charge);
                }
                int arrow = MachineGui.scaled(progress, duration, 24);
                blit(graphics, texture, 79, 34, 176, 14, arrow + 1, 16);
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        MachineGui.Overlay overlay = MachineGui.overlay(menu.kind());
        if (overlay == MachineGui.Overlay.INDUCTION) {
            graphics.text(font, Component.translatable("container.oldustry.induction.heat"), 10, 36, LABEL, false);
            int heatPct = menu.data().get(5) > 0 ? menu.data().get(4) * 100 / menu.data().get(5) : 0;
            graphics.text(font, heatPct + "%", 10, 44, LABEL, false);
            return;
        }
        if (overlay == MachineGui.Overlay.MINER) {
            graphics.text(font, Component.translatable("info.oldustry.miner." + minerStatus()), 8, 72, LABEL, false);
            return;
        }
        if (overlay != MachineGui.Overlay.STORAGE) {
            return;
        }
        graphics.text(font, Component.translatable("container.oldustry.armor"), 8, imageHeight - 123, LABEL, false);
        graphics.text(font, Component.translatable("container.oldustry.electric.level"), 79, 25, LABEL, false);
        int energy = Math.min(menu.data().get(0), menu.data().get(1));
        graphics.text(font, " " + energy, 110, 35, LABEL, false);
        graphics.text(font, "/" + menu.data().get(1), 110, 45, LABEL, false);
        graphics.text(font, Component.translatable("container.oldustry.electric.output", menu.kind().packet), 85, 60, LABEL, false);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (hoveredSlot != null && hoveredSlot.hasItem()) {
            return;
        }
        int lx = mouseX - leftPos;
        int ly = mouseY - topPos;
        MachineGui.Overlay overlay = MachineGui.overlay(menu.kind());
        if (overlay == MachineGui.Overlay.GEO && MachineGui.inside(MachineGui.lavaGauge(), lx, ly)) {
            graphics.setTooltipForNextFrame(
                font,
                Component.translatable("container.oldustry.lava", menu.data().get(4), menu.data().get(5)),
                mouseX,
                mouseY
            );
            return;
        }
        int[] gauge = MachineGui.energyGauge(overlay);
        if (!MachineGui.inside(gauge, lx, ly)) {
            return;
        }
        int energy = Math.min(menu.data().get(0), menu.data().get(1));
        graphics.setTooltipForNextFrame(
            font,
            Component.translatable("container.oldustry.eu", energy, menu.data().get(1)),
            mouseX,
            mouseY
        );
    }

    private void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int u, int v, int w, int h) {
        if (w <= 0 || h <= 0) {
            return;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos + x, topPos + y, u, v, w, h, 256, 256);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (menu.kind().type == MachineKind.Type.STORAGE) {
            double x = event.x() - (leftPos + 152);
            double y = event.y() - (topPos + 4);
            if (x >= 0 && y >= 0 && x < 20 && y < 20 && menu.clickMenuButton(minecraft.player, 0)) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
