package com.oldustry.machine;




public final class MachineGuiCheck {
    public static void main(String[] args) {
        assert !OldustryMachineKind.GENERATOR.verticalFacing();
        assert !OldustryMachineKind.MACERATOR.verticalFacing();
        assert !OldustryMachineKind.SOLAR_PANEL.verticalFacing();
        assert OldustryMachineKind.BATBOX.verticalFacing();
        assert OldustryMachineKind.HV_TRANSFORMER.verticalFacing();
        assert !OldustryMachineKind.LUMINATOR.verticalFacing();

        assert OldustryMachineLayout.hasGui(OldustryMachineKind.MACERATOR);
        assert OldustryMachineLayout.hasGui(OldustryMachineKind.SOLAR_PANEL);
        assert OldustryMachineLayout.hasGui(OldustryMachineKind.WIND_MILL);
        assert !OldustryMachineLayout.hasGui(OldustryMachineKind.LV_TRANSFORMER);
        assert !OldustryMachineLayout.hasGui(OldustryMachineKind.LUMINATOR);
        assert OldustryMachineLayout.minerStatus(false, false, true, 100, 0).equals("no_drill");
        assert OldustryMachineLayout.minerStatus(false, true, false, 100, 0).equals("no_pipe");
        assert OldustryMachineLayout.minerStatus(false, true, true, 0, 0).equals("no_eu");
        assert OldustryMachineLayout.minerStatus(false, true, true, 5, 3).equals("no_eu");
        assert OldustryMachineLayout.minerStatus(false, true, true, 199, 4).equals("no_eu");
        assert OldustryMachineLayout.minerStatus(true, true, true, 100, 3).equals("working");
        assert OldustryMachineLayout.minerStatus(true, false, true, 100, 1).equals("withdrawing");
        assert OldustryMachineLayout.height(OldustryMachineKind.MACERATOR) == 166;
        assert OldustryMachineLayout.height(OldustryMachineKind.BATBOX) == 196;
        assert OldustryMachineLayout.inventoryTop(OldustryMachineKind.GENERATOR) == 84;
        assert OldustryMachineLayout.inventoryTop(OldustryMachineKind.MFE) == 114;

        int[] in = OldustryMachineLayout.slot(OldustryMachineKind.MACERATOR, 0);
        assert in[0] == 56 && in[1] == 17 : java.util.Arrays.toString(in);
        int[] out = OldustryMachineLayout.slot(OldustryMachineKind.EXTRACTOR, 1);
        assert out[0] == 116 && out[1] == 35;
        int[] bat = OldustryMachineLayout.slot(OldustryMachineKind.COMPRESSOR, 6);
        assert bat[0] == 56 && bat[1] == 53;
        int[] up = OldustryMachineLayout.slot(OldustryMachineKind.RECYCLER, 2);
        assert up[0] == 152 && up[1] == 8;
        assert OldustryMachineLayout.slot(OldustryMachineKind.GENERATOR, 0)[0] == 65;
        assert OldustryMachineLayout.slot(OldustryMachineKind.GENERATOR, 1)[1] == 17;
        assert OldustryMachineLayout.slot(OldustryMachineKind.SOLAR_PANEL, 1)[0] == 80;
        assert OldustryMachineLayout.slot(OldustryMachineKind.SOLAR_PANEL, 0)[0] < 0;
        assert OldustryMachineLayout.slot(OldustryMachineKind.BATBOX, 1)[1] == 53;

        assert OldustryMachineSlots.role(OldustryMachineKind.MACERATOR, 2) == OldustryMachineSlots.Role.UPGRADE;
        assert OldustryMachineSlots.role(OldustryMachineKind.GENERATOR, 0) == OldustryMachineSlots.Role.FUEL;
        assert OldustryMachineLayout.overlay(OldustryMachineKind.ELECTRIC_FURNACE) == OldustryMachineLayout.Overlay.PROCESS;
        assert OldustryMachineLayout.overlay(OldustryMachineKind.MFSU) == OldustryMachineLayout.Overlay.STORAGE;

        assert OldustryMachineLayout.scaled(0, 100, 24) == 0;
        assert OldustryMachineLayout.scaled(50, 100, 24) == 12;
        assert OldustryMachineLayout.scaled(100, 100, 14) == 14;
        assert OldustryMachineLayout.scaled(1, 0, 24) == 0;

        int[] storage = OldustryMachineLayout.energyGauge(OldustryMachineLayout.Overlay.STORAGE);
        assert storage[0] == 79 && storage[2] == 25;
        assert OldustryMachineLayout.inside(storage, 79, 34);
        assert OldustryMachineLayout.inside(storage, 103, 49);
        assert !OldustryMachineLayout.inside(storage, 78, 34);
        assert !OldustryMachineLayout.inside(storage, 79, 50);
        assert OldustryMachineLayout.energyGauge(OldustryMachineLayout.Overlay.SOLAR) == null;
        assert OldustryMachineLayout.inside(OldustryMachineLayout.energyGauge(OldustryMachineLayout.Overlay.PROCESS), 56, 36);
        int[] lava = OldustryMachineLayout.lavaGauge();
        assert lava[0] == 66 && lava[1] == 36 && lava[2] == 14 && lava[3] == 14;
        assert OldustryMachineLayout.inside(lava, 66, 36);
        assert !OldustryMachineLayout.inside(lava, 65, 36);

        // JAR TileEntityElectric*: (tier, output, maxStorage)
        assert OldustryMachineKind.BATBOX.packet == 32 && OldustryMachineKind.BATBOX.capacity == 40000;
        assert OldustryMachineKind.MFE.packet == 128 && OldustryMachineKind.MFE.capacity == 600000;
        assert OldustryMachineKind.MFSU.packet == 512 && OldustryMachineKind.MFSU.capacity == 10000000;
        // JAR Geo: production=energyGeneratorGeo(20), maxStorage=max(20,32)=32, maxLava=24000
        assert OldustryMachineKind.GEO_GENERATOR.packet == 20 && OldustryMachineKind.GEO_GENERATOR.buffer() == 32;
        assert OldustryMachineKind.GEO_GENERATOR.capacity == 20;

        // JAR ContainerCanner / GuiCanner
        assert OldustryMachineLayout.slot(OldustryMachineKind.CANNER, 0)[0] == 69 && OldustryMachineLayout.slot(OldustryMachineKind.CANNER, 0)[1] == 17;
        assert OldustryMachineLayout.slot(OldustryMachineKind.CANNER, 1)[0] == 69 && OldustryMachineLayout.slot(OldustryMachineKind.CANNER, 1)[1] == 53;
        assert OldustryMachineLayout.slot(OldustryMachineKind.CANNER, 2)[0] == 119 && OldustryMachineLayout.slot(OldustryMachineKind.CANNER, 2)[1] == 35;
        assert OldustryMachineLayout.slot(OldustryMachineKind.CANNER, 3)[0] == 30 && OldustryMachineLayout.slot(OldustryMachineKind.CANNER, 3)[1] == 45;
        // JAR ContainerMiner / GuiMiner
        assert OldustryMachineLayout.slot(OldustryMachineKind.MINER, 0)[0] == 45 && OldustryMachineLayout.slot(OldustryMachineKind.MINER, 0)[1] == 22;
        assert OldustryMachineLayout.slot(OldustryMachineKind.MINER, 2)[0] == 81 && OldustryMachineLayout.slot(OldustryMachineKind.MINER, 2)[1] == 22;
        assert OldustryMachineLayout.slot(OldustryMachineKind.MINER, 1)[0] == 117 && OldustryMachineLayout.slot(OldustryMachineKind.MINER, 1)[1] == 22;
        assert OldustryMachineLayout.slot(OldustryMachineKind.MINER, 3)[0] == 81 && OldustryMachineLayout.slot(OldustryMachineKind.MINER, 3)[1] == 59;
        assert OldustryMachineLayout.slot(OldustryMachineKind.MINER, 4)[0] < 0;
        assert OldustryMachineSlots.role(OldustryMachineKind.MINER, 4) == OldustryMachineSlots.Role.HIDDEN;
        int[] minerEu = OldustryMachineLayout.energyGauge(OldustryMachineLayout.Overlay.MINER);
        assert minerEu[0] == 81 && minerEu[1] == 41 && minerEu[2] == 14 && minerEu[3] == 14;
        // JAR ContainerPump / GuiPump
        assert OldustryMachineLayout.slot(OldustryMachineKind.PUMP, 0)[0] == 62 && OldustryMachineLayout.slot(OldustryMachineKind.PUMP, 0)[1] == 17;
        assert OldustryMachineLayout.slot(OldustryMachineKind.PUMP, 2)[0] == 62 && OldustryMachineLayout.slot(OldustryMachineKind.PUMP, 2)[1] == 53;
        assert OldustryMachineLayout.slot(OldustryMachineKind.PUMP, 1)[0] < 0;
        int[] pumpEu = OldustryMachineLayout.energyGauge(OldustryMachineLayout.Overlay.PUMP);
        assert pumpEu[0] == 62 && pumpEu[1] == 36 && pumpEu[2] == 14 && pumpEu[3] == 14;
        int[] cannerEu = OldustryMachineLayout.energyGauge(OldustryMachineLayout.Overlay.CANNER);
        assert cannerEu[0] == 31 && cannerEu[1] == 27 && cannerEu[2] == 14;

        System.out.println("MachineGuiCheck ok");
    }

    private MachineGuiCheck() {}
}
