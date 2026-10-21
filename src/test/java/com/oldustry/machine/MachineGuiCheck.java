package com.oldustry.machine;

public final class MachineGuiCheck {
    public static void main(String[] args) {
        assert !MachineKind.GENERATOR.verticalFacing();
        assert !MachineKind.MACERATOR.verticalFacing();
        assert !MachineKind.SOLAR_PANEL.verticalFacing();
        assert MachineKind.BATBOX.verticalFacing();
        assert MachineKind.HV_TRANSFORMER.verticalFacing();
        assert !MachineKind.LUMINATOR.verticalFacing();

        assert MachineGui.hasGui(MachineKind.MACERATOR);
        assert MachineGui.hasGui(MachineKind.SOLAR_PANEL);
        assert MachineGui.hasGui(MachineKind.WIND_MILL);
        assert !MachineGui.hasGui(MachineKind.LV_TRANSFORMER);
        assert !MachineGui.hasGui(MachineKind.LUMINATOR);
        assert MachineGui.minerStatus(false, false, true, 100, 0).equals("no_drill");
        assert MachineGui.minerStatus(false, true, false, 100, 0).equals("no_pipe");
        assert MachineGui.minerStatus(false, true, true, 0, 0).equals("no_eu");
        assert MachineGui.minerStatus(false, true, true, 5, 3).equals("no_eu");
        assert MachineGui.minerStatus(false, true, true, 199, 4).equals("no_eu");
        assert MachineGui.minerStatus(true, true, true, 100, 3).equals("working");
        assert MachineGui.minerStatus(true, false, true, 100, 1).equals("withdrawing");
        assert MachineGui.height(MachineKind.MACERATOR) == 166;
        assert MachineGui.height(MachineKind.BATBOX) == 196;
        assert MachineGui.inventoryTop(MachineKind.GENERATOR) == 84;
        assert MachineGui.inventoryTop(MachineKind.MFE) == 114;

        int[] in = MachineGui.slot(MachineKind.MACERATOR, 0);
        assert in[0] == 56 && in[1] == 17 : java.util.Arrays.toString(in);
        int[] out = MachineGui.slot(MachineKind.EXTRACTOR, 1);
        assert out[0] == 116 && out[1] == 35;
        int[] bat = MachineGui.slot(MachineKind.COMPRESSOR, 6);
        assert bat[0] == 56 && bat[1] == 53;
        int[] up = MachineGui.slot(MachineKind.RECYCLER, 2);
        assert up[0] == 152 && up[1] == 8;
        assert MachineGui.slot(MachineKind.GENERATOR, 0)[0] == 65;
        assert MachineGui.slot(MachineKind.GENERATOR, 1)[1] == 17;
        assert MachineGui.slot(MachineKind.SOLAR_PANEL, 1)[0] == 80;
        assert MachineGui.slot(MachineKind.SOLAR_PANEL, 0)[0] < 0;
        assert MachineGui.slot(MachineKind.BATBOX, 1)[1] == 53;

        assert MachineGui.role(MachineKind.MACERATOR, 2) == MachineGui.Role.UPGRADE;
        assert MachineGui.role(MachineKind.GENERATOR, 0) == MachineGui.Role.FUEL;
        assert MachineGui.overlay(MachineKind.ELECTRIC_FURNACE) == MachineGui.Overlay.PROCESS;
        assert MachineGui.overlay(MachineKind.MFSU) == MachineGui.Overlay.STORAGE;

        assert MachineGui.scaled(0, 100, 24) == 0;
        assert MachineGui.scaled(50, 100, 24) == 12;
        assert MachineGui.scaled(100, 100, 14) == 14;
        assert MachineGui.scaled(1, 0, 24) == 0;

        int[] storage = MachineGui.energyGauge(MachineGui.Overlay.STORAGE);
        assert storage[0] == 79 && storage[2] == 25;
        assert MachineGui.inside(storage, 79, 34);
        assert MachineGui.inside(storage, 103, 49);
        assert !MachineGui.inside(storage, 78, 34);
        assert !MachineGui.inside(storage, 79, 50);
        assert MachineGui.energyGauge(MachineGui.Overlay.SOLAR) == null;
        assert MachineGui.inside(MachineGui.energyGauge(MachineGui.Overlay.PROCESS), 56, 36);
        int[] lava = MachineGui.lavaGauge();
        assert lava[0] == 66 && lava[1] == 36 && lava[2] == 14 && lava[3] == 14;
        assert MachineGui.inside(lava, 66, 36);
        assert !MachineGui.inside(lava, 65, 36);

        // JAR TileEntityElectric*: (tier, output, maxStorage)
        assert MachineKind.BATBOX.packet == 32 && MachineKind.BATBOX.capacity == 40000;
        assert MachineKind.MFE.packet == 128 && MachineKind.MFE.capacity == 600000;
        assert MachineKind.MFSU.packet == 512 && MachineKind.MFSU.capacity == 10000000;
        // JAR Geo: production=energyGeneratorGeo(20), maxStorage=max(20,32)=32, maxLava=24000
        assert MachineKind.GEO_GENERATOR.packet == 20 && MachineKind.GEO_GENERATOR.buffer() == 32;
        assert MachineKind.GEO_GENERATOR.capacity == 20;

        // JAR ContainerCanner / GuiCanner
        assert MachineGui.slot(MachineKind.CANNER, 0)[0] == 69 && MachineGui.slot(MachineKind.CANNER, 0)[1] == 17;
        assert MachineGui.slot(MachineKind.CANNER, 1)[0] == 69 && MachineGui.slot(MachineKind.CANNER, 1)[1] == 53;
        assert MachineGui.slot(MachineKind.CANNER, 2)[0] == 119 && MachineGui.slot(MachineKind.CANNER, 2)[1] == 35;
        assert MachineGui.slot(MachineKind.CANNER, 3)[0] == 30 && MachineGui.slot(MachineKind.CANNER, 3)[1] == 45;
        // JAR ContainerMiner / GuiMiner
        assert MachineGui.slot(MachineKind.MINER, 0)[0] == 45 && MachineGui.slot(MachineKind.MINER, 0)[1] == 22;
        assert MachineGui.slot(MachineKind.MINER, 2)[0] == 81 && MachineGui.slot(MachineKind.MINER, 2)[1] == 22;
        assert MachineGui.slot(MachineKind.MINER, 1)[0] == 117 && MachineGui.slot(MachineKind.MINER, 1)[1] == 22;
        assert MachineGui.slot(MachineKind.MINER, 3)[0] == 81 && MachineGui.slot(MachineKind.MINER, 3)[1] == 59;
        assert MachineGui.slot(MachineKind.MINER, 4)[0] < 0;
        assert MachineGui.role(MachineKind.MINER, 4) == MachineGui.Role.HIDDEN;
        int[] minerEu = MachineGui.energyGauge(MachineGui.Overlay.MINER);
        assert minerEu[0] == 81 && minerEu[1] == 41 && minerEu[2] == 14 && minerEu[3] == 14;
        // JAR ContainerPump / GuiPump
        assert MachineGui.slot(MachineKind.PUMP, 0)[0] == 62 && MachineGui.slot(MachineKind.PUMP, 0)[1] == 17;
        assert MachineGui.slot(MachineKind.PUMP, 2)[0] == 62 && MachineGui.slot(MachineKind.PUMP, 2)[1] == 53;
        assert MachineGui.slot(MachineKind.PUMP, 1)[0] < 0;
        int[] pumpEu = MachineGui.energyGauge(MachineGui.Overlay.PUMP);
        assert pumpEu[0] == 62 && pumpEu[1] == 36 && pumpEu[2] == 14 && pumpEu[3] == 14;
        int[] cannerEu = MachineGui.energyGauge(MachineGui.Overlay.CANNER);
        assert cannerEu[0] == 31 && cannerEu[1] == 27 && cannerEu[2] == 14;

        // JAR getOperationHeight: first cell that is not a pipe. A broken pipe is that cell.
        assert MinerColumn.head(10, 0, y -> y == 5 ? MinerColumn.TIP : MinerColumn.PIPE) == 5;
        assert MinerColumn.head(10, 0, y -> switch (y) {
            case 5 -> MinerColumn.TIP;
            case 7 -> MinerColumn.AIR;
            default -> y > 5 && y < 10 ? MinerColumn.PIPE : MinerColumn.OTHER;
        }) == 7;
        assert MinerColumn.head(10, 0, y -> y == 9 ? MinerColumn.AIR : MinerColumn.OTHER) == 9;
        assert com.oldustry.item.ValuableOre.pathValue("iron_ore") == 1;
        assert com.oldustry.item.ValuableOre.pathValue("deepslate_iron_ore") == 1;
        assert com.oldustry.item.ValuableOre.pathValue("copper_ore") == 2;
        assert com.oldustry.item.ValuableOre.pathValue("deepslate_copper_ore") == 2;
        assert com.oldustry.item.ValuableOre.pathValue("coal_ore") == 1;
        assert com.oldustry.item.ValuableOre.pathValue("diamond_ore") == 4;
        assert com.oldustry.item.ValuableOre.pathValue("stone") == 0;
        System.out.println("MachineGuiCheck ok");
    }

    private MachineGuiCheck() {}
}
