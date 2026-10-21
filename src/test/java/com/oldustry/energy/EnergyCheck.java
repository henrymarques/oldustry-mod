package com.oldustry.energy;

import com.oldustry.item.BronzeStats;
import com.oldustry.item.ChainsawStats;
import com.oldustry.item.DrillStats;
import com.oldustry.item.WrenchEu;
import com.oldustry.machine.MachineGui;
import com.oldustry.machine.MachineKind;
import com.oldustry.wrench.WrenchHandler;
import com.oldustry.wrench.WrenchLogic;

public final class EnergyCheck {
    public static void main(String[] args) {
        assert WrenchLogic.dismantle(0.85f, 0.1f) == WrenchLogic.Drop.KEEP;
        assert WrenchLogic.dismantle(0.85f, 0.9f) == WrenchLogic.Drop.HULL;
        assert WrenchLogic.dismantle(1f, 0.99f) == WrenchLogic.Drop.KEEP;

        EnergyNetLogic.Node source = new EnergyNetLogic.Node(EnergyNetLogic.Role.SOURCE, 0, Integer.MAX_VALUE, 0);
        EnergyNetLogic.Node wire = new EnergyNetLogic.Node(
            EnergyNetLogic.Role.WIRE, CableKind.COPPER.loss, CableKind.COPPER.meltAt, 0, Integer.MAX_VALUE, CableKind.COPPER.insulation
        );
        EnergyNetLogic.Node sink = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 32);
        EnergyNetLogic.Result result = EnergyNetLogic.emit(
            java.util.List.of(source, wire, sink),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            32
        );
        // JAR floor(0.2)=0 loss on short copper → full 32 delivered.
        assert result.delivered()[2] == 32 : result.delivered()[2];
        assert !result.melted()[1];
        // JAR totalEnergyConducted += energyInjected (delivered), not source packet.
        assert result.conductorFlow()[1] == 32 : result.conductorFlow()[1];
        // Meter source "out" = Σ inject (= delivered), not consumed (lost EU not counted as emitted).
        int deliveredSum = 0;
        for (int d : result.delivered()) {
            deliveredSum += d;
        }
        assert deliveredSum == 32 && result.consumed() >= deliveredSum;

        EnergyNetLogic.Node tin = new EnergyNetLogic.Node(
            EnergyNetLogic.Role.WIRE, CableKind.TIN.loss, CableKind.TIN.meltAt, 0, Integer.MAX_VALUE, CableKind.TIN.insulation
        );
        EnergyNetLogic.Result melt = EnergyNetLogic.emit(
            java.util.List.of(source, tin, sink),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            32
        );
        // JAR: melt when energyInjected >= meltAt (tin=6).
        assert melt.melted()[1] : "tin must melt when inject 32 ≥ 6";

        // Shared hub wire: two sinks → flow = sum of injects ≤ packet (not packet×sinks).
        EnergyNetLogic.Node hub = new EnergyNetLogic.Node(
            EnergyNetLogic.Role.WIRE, CableKind.COPPER.loss, CableKind.COPPER.meltAt, 0, Integer.MAX_VALUE, CableKind.COPPER.insulation
        );
        EnergyNetLogic.Node hubA = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 32);
        EnergyNetLogic.Node hubB = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 32);
        EnergyNetLogic.Result hubFlow = EnergyNetLogic.emit(
            java.util.List.of(source, hub, hubA, hubB),
            java.util.List.of(
                new EnergyNetLogic.Edge(0, 1),
                new EnergyNetLogic.Edge(1, 2),
                new EnergyNetLogic.Edge(1, 3)
            ),
            0,
            32,
            new java.util.Random(1)
        );
        assert hubFlow.conductorFlow()[1] == hubFlow.delivered()[2] + hubFlow.delivered()[3]
            : "hub flow must equal sum of injects, got " + hubFlow.conductorFlow()[1]
                + " vs " + hubFlow.delivered()[2] + "+" + hubFlow.delivered()[3];
        assert hubFlow.conductorFlow()[1] <= 32 : hubFlow.conductorFlow()[1];

        // JAR split ∝ 1/loss: both sinks get EU; shorter path gets the larger share.
        EnergyNetLogic.Node shortWire = new EnergyNetLogic.Node(
            EnergyNetLogic.Role.WIRE, CableKind.COPPER.loss, CableKind.COPPER.meltAt, 0, Integer.MAX_VALUE, CableKind.COPPER.insulation
        );
        EnergyNetLogic.Node sinkA = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 32);
        EnergyNetLogic.Node long1 = new EnergyNetLogic.Node(
            EnergyNetLogic.Role.WIRE, CableKind.COPPER.loss, CableKind.COPPER.meltAt, 0, Integer.MAX_VALUE, CableKind.COPPER.insulation
        );
        EnergyNetLogic.Node long2 = new EnergyNetLogic.Node(
            EnergyNetLogic.Role.WIRE, CableKind.COPPER.loss, CableKind.COPPER.meltAt, 0, Integer.MAX_VALUE, CableKind.COPPER.insulation
        );
        EnergyNetLogic.Node sinkB = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 32);
        EnergyNetLogic.Result twoPath = EnergyNetLogic.emit(
            java.util.List.of(source, shortWire, sinkA, long1, long2, sinkB),
            java.util.List.of(
                new EnergyNetLogic.Edge(0, 1),
                new EnergyNetLogic.Edge(1, 2),
                new EnergyNetLogic.Edge(0, 3),
                new EnergyNetLogic.Edge(3, 4),
                new EnergyNetLogic.Edge(4, 5)
            ),
            0,
            32,
            new java.util.Random(1)
        );
        assert twoPath.delivered()[2] > 0 : "short path must receive";
        assert twoPath.delivered()[5] > 0 : "long path must receive (JAR split)";
        assert twoPath.delivered()[2] > twoPath.delivered()[5] : "shorter loss → larger share";
        assert twoPath.delivered()[2] + twoPath.delivered()[5] <= 32;

        EnergyNetLogic.Node hv = new EnergyNetLogic.Node(EnergyNetLogic.Role.WIRE, CableKind.HV_BARE.loss, CableKind.HV_BARE.meltAt, 0);
        EnergyNetLogic.Node lvSink = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 32, 32);
        EnergyNetLogic.Result overvolt = EnergyNetLogic.emit(
            java.util.List.of(source, hv, lvSink),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            128
        );
        assert overvolt.exploded()[2] : "packet 128 must explode LV 32 (active demand)";
        assert overvolt.delivered()[2] == 0;
        assert overvolt.offered()[2] == 128 || overvolt.offered()[2] > 32
            : "JAR offer is full inject share, not min(demand)";
        // demand=2 (post-process hole): still offers full share → overvolt, not a 2 EU drip.
        EnergyNetLogic.Node tinyDemand = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 2, 32);
        EnergyNetLogic.Result tinyOv = EnergyNetLogic.emit(
            java.util.List.of(source, hv, tinyDemand),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            128
        );
        assert tinyOv.exploded()[2] : "demand 2 must still overvolt on 128";
        assert tinyOv.offered()[2] > 32 : "offer must be full share for acceptEnergy overvolt";
        // JAR: full buffer (demand 0) is skipped by emit — no inject → no explode.
        EnergyNetLogic.Node fullLv = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 0, 32);
        EnergyNetLogic.Result fullOvervolt = EnergyNetLogic.emit(
            java.util.List.of(source, hv, fullLv),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            128
        );
        assert !fullOvervolt.exploded()[2] : "JAR: demand 0 must not overvolt";

        // HV→MV→LV: xfmr is BOTH endpoint (JAR) — packet must not traverse BOTH.
        // Use HV cable (meltAt 2049) so 512 does not melt the wire before the xfmr.
        EnergyNetLogic.Node hvWire = new EnergyNetLogic.Node(EnergyNetLogic.Role.WIRE, CableKind.HV.loss, CableKind.HV.meltAt, 0);
        EnergyNetLogic.Node mvXfmr = new EnergyNetLogic.Node(EnergyNetLogic.Role.BOTH, 0, Integer.MAX_VALUE, 256, 512);
        EnergyNetLogic.Result stepDown = EnergyNetLogic.emit(
            java.util.List.of(source, hvWire, mvXfmr),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            512
        );
        assert !stepDown.exploded()[2] : "MV accepts 512 when stepping down";
        assert stepDown.delivered()[2] > 0 : "MV must receive EU";
        EnergyNetLogic.Node lvMachine = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 32, 32);
        EnergyNetLogic.Result badThrough = EnergyNetLogic.emit(
            java.util.List.of(source, hvWire, mvXfmr, lvMachine),
            java.util.List.of(
                new EnergyNetLogic.Edge(0, 1),
                new EnergyNetLogic.Edge(1, 2),
                new EnergyNetLogic.Edge(2, 3) // illegally through BOTH
            ),
            0,
            512
        );
        assert badThrough.exploded()[3] : "512 through xfmr must explode LV machine";
        EnergyNetLogic.Result afterLv = EnergyNetLogic.emit(
            java.util.List.of(
                new EnergyNetLogic.Node(EnergyNetLogic.Role.SOURCE, 0, Integer.MAX_VALUE, 0),
                wire,
                lvMachine
            ),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            32
        );
        assert !afterLv.exploded()[2] && afterLv.delivered()[2] > 0 : "LV 32 packet safe for LV machine";

        assert FuelLogic.generatorBurnTicks(1600, false) == 400;
        assert FuelLogic.generatorBurnTicks(20000, true) == 0;
        assert FuelLogic.lavaBucket("lava_bucket");
        assert !FuelLogic.lavaBucket("lava_cell");
        assert FuelLogic.extraFurnaceBurn("lava_cell") == 20000;
        assert FuelLogic.generatorBurnTicks(20000, false) == 5000;
        assert FuelLogic.generatorBurnTicks(6 * com.oldustry.machine.CannerFuelValues.COALFUEL * 2, false) * MachineKind.GENERATOR.packet == 76440;
        assert FuelLogic.generatorBurnTicks(6 * com.oldustry.machine.CannerFuelValues.BIOFUEL * 2, false) * MachineKind.GENERATOR.packet == 26040;
        // JAR consume-with-container: stack of N shrinks; only last becomes empty.
        assert !FuelLogic.leaveContainer(8) : "mid-stack must not leave container";
        assert FuelLogic.leaveContainer(1) : "last unit leaves container";
        assert FuelLogic.geoCanAccept(0) && FuelLogic.geoCanAccept(23000) && !FuelLogic.geoCanAccept(23001);
        assert FuelLogic.waterCanAccept(0) && FuelLogic.waterCanAccept(1500) && !FuelLogic.waterCanAccept(1501);
        FuelLogic.BurnTick full = FuelLogic.tickBurn(10, 4000, 10, 4000);
        assert full.burnTime() == 9 && full.energy() == 4000 && full.converting();
        FuelLogic.BurnTick near = FuelLogic.tickBurn(10, 3995, 10, 4000);
        assert near.burnTime() == 9 && near.energy() == 4000 && near.converting();
        FuelLogic.BurnTick room = FuelLogic.tickBurn(10, 0, 10, 4000);
        assert room.burnTime() == 9 && room.energy() == 10 && room.converting();
        FuelLogic.BurnTick idle = FuelLogic.tickBurn(0, 4000, 10, 4000);
        assert idle.burnTime() == 0 && idle.energy() == 4000 && !idle.converting();
        FuelLogic.BurnTick geoFull = FuelLogic.tickIfFits(10, 32, 20, 32);
        assert geoFull.burnTime() == 10 && geoFull.energy() == 32 && !geoFull.converting();
        FuelLogic.BurnTick geoRoom = FuelLogic.tickIfFits(10, 0, 20, 32);
        assert geoRoom.burnTime() == 9 && geoRoom.energy() == 20 && geoRoom.converting();

        ElectricCharge.Spec re = new ElectricCharge.Spec(10000, 100, 1, true);
        assert ElectricCharge.charge(0, 50, re) == 50;
        assert ElectricCharge.discharge(50, 50, re, 0) == 0;
        assert ElectricCharge.discharge(50, 50, re, 1) == 50;
        // JAR RE right-click burst: 2×transfer, ignoreTransferLimit
        assert ElectricCharge.discharge(10000, 200, re, 1) == 100;
        assert ElectricCharge.dischargeIgnoreTransfer(10000, 200, re, 1) == 200;
        assert ElectricCharge.dischargeIgnoreTransfer(50, 200, re, 1) == 50;
        assert ElectricCharge.dischargeIgnoreTransfer(10000, 200, re, 0) == 0;
        assert ElectricCharge.chargeIgnoreTransfer(9900, 200, re) == 100;
        // JAR charge tier: RE (tier 1) fills a tier-1 tool and refuses a tier-2 tool.
        ElectricCharge.Spec tier2 = new ElectricCharge.Spec(10000, 50, 2, true);
        assert ElectricCharge.chargeAtTier(0, 200, re, 1, true) == 200;
        assert ElectricCharge.chargeAtTier(0, 200, tier2, 1, true) == 0;
        assert ElectricCharge.chargeAtTier(0, 200, tier2, 2, false) == 50;
        // JAR chargeFromArmor: one use dumps the tool full (transfer 100 does not cap it). Tier still gates.
        ElectricCharge.Spec pack = new ElectricCharge.Spec(60000, 100, 1, true);
        ElectricCharge.Spec drill = new ElectricCharge.Spec(10000, 100, 1, true);
        assert ElectricCharge.chargeFromArmor(0, drill, 60000, pack) == 10000;
        assert ElectricCharge.chargeFromArmor(10000, drill, 60000, pack) == 0;
        assert ElectricCharge.chargeFromArmor(0, tier2, 60000, pack) == 0 : "batpack tier 1 must refuse tier 2";
        ElectricCharge.Spec lap = new ElectricCharge.Spec(300000, 250, 2, true);
        assert ElectricCharge.chargeFromArmor(0, tier2, 300000, lap) == 10000;
        // use(): fill, pay 50, fill again. Pack loses the empty room, tool ends full.
        int toolE = 0;
        int packE = 60000;
        int moved = ElectricCharge.chargeFromArmor(toolE, drill, packE, pack);
        toolE += moved;
        packE -= moved;
        assert ElectricCharge.spend(toolE, 50) == 50;
        toolE -= 50;
        moved = ElectricCharge.chargeFromArmor(toolE, drill, packE, pack);
        toolE += moved;
        packE -= moved;
        assert toolE == 10000 && packE == 49950 : toolE + "/" + packE;
        // Pack shorter than the operation: first pull moves what it has, spend pays nothing.
        assert ElectricCharge.chargeFromArmor(0, drill, 30, pack) == 30;
        assert ElectricCharge.spend(30, 50) == 0;
        // JAR use() spends the full operation cost. discharge() would cap OV (250) and lossless wrench (10000) at transfer.
        assert ElectricCharge.spend(10000, 250) == 250;
        assert ElectricCharge.spend(249, 250) == 0;
        assert ElectricCharge.spend(12000, WrenchEu.eu(WrenchHandler.Result.REMOVE, true)) == 10000;
        assert ElectricCharge.consumeOnce(1200, 1200) == 1200;
        assert ElectricCharge.consumeOnce(1199, 1200) == 0 : "one-shot needs full room";
        assert ElectricCharge.consumeOnce(800, 800) == 800;

        UpgradeLogic.Stats stats = UpgradeLogic.apply(new UpgradeLogic.Stats(2, 400, 800, 1), 1, 0, 1);
        assert stats.duration() == 280 : stats.duration();
        assert stats.euPerTick() == 3 : stats.euPerTick();
        // JAR: capacity = base + storage×10k + maxInput − 1 (tier1 → 32).
        assert stats.capacity() == 10831 : stats.capacity();
        assert UpgradeLogic.maxSafeInput(1) == 32;
        assert UpgradeLogic.maxSafeInput(2) == 128;
        UpgradeLogic.Stats withTf = UpgradeLogic.apply(new UpgradeLogic.Stats(2, 400, 800, 1), 0, 1, 0);
        assert withTf.tier() == 2 && withTf.capacity() == 800 + 128 - 1 : withTf;
        // JAR TF upgrade: +1 tier each → LV32 / MV128 / HV512 / EV2048
        assert UpgradeLogic.maxSafeInput(1) == 32;
        assert UpgradeLogic.maxSafeInput(2) == 128;
        assert UpgradeLogic.maxSafeInput(3) == 512;
        assert UpgradeLogic.maxSafeInput(4) == 2048;
        assert UpgradeLogic.maxSafeInput(UpgradeLogic.apply(new UpgradeLogic.Stats(2, 400, 800, 1), 0, 1, 0).tier()) == 128;
        assert UpgradeLogic.maxSafeInput(UpgradeLogic.apply(new UpgradeLogic.Stats(2, 400, 800, 1), 0, 2, 0).tier()) == 512;
        assert UpgradeLogic.maxSafeInput(UpgradeLogic.apply(new UpgradeLogic.Stats(2, 400, 800, 1), 0, 3, 0).tier()) == 2048;
        // 1 TF (max 128) must survive MV packet; must explode on HV packet
        EnergyNetLogic.Node lvMachineTf = new EnergyNetLogic.Node(EnergyNetLogic.Role.SINK, 0, Integer.MAX_VALUE, 927, 128);
        EnergyNetLogic.Result mvOk = EnergyNetLogic.emit(
            java.util.List.of(source, wire, lvMachineTf),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            128
        );
        assert !mvOk.exploded()[2] && mvOk.delivered()[2] > 0 : "1×TF must accept 128";
        EnergyNetLogic.Result hvBoom = EnergyNetLogic.emit(
            java.util.List.of(source, wire, lvMachineTf),
            java.util.List.of(new EnergyNetLogic.Edge(0, 1), new EnergyNetLogic.Edge(1, 2)),
            0,
            512
        );
        assert hvBoom.exploded()[2] : "1×TF must still explode on 512 (need 2×TF)";
        assert UpgradeLogic.rescaleProgress(200, 400, 280) == 140 : "OC keeps progress ratio";
        UpgradeLogic.Stats noProcess = UpgradeLogic.apply(new UpgradeLogic.Stats(32, 0, 256, 1), 0, 0, 0);
        assert noProcess.duration() == 0 : "non-process machines must keep duration 0";

        assert ProcessLogic.recyclerScrap(0, 8);
        assert !ProcessLogic.recyclerScrap(1, 8);

        double y120 = WindLogic.production(15, 120, 0, false, false);
        assert y120 > 1.0 && y120 < 1.2 : y120;
        assert WindLogic.production(15, 120, 56, false, false) == 0;
        assert WindLogic.production(15, 64, 0, false, false) == 0;
        assert WindLogic.gaugeFuel(3, 14) == 14;
        assert WindLogic.gaugeFuel(1.12, 14) > 0;

        assert WrenchEu.eu(WrenchHandler.Result.ROTATE, false) == 50;
        assert WrenchEu.eu(WrenchHandler.Result.REMOVE, false) == 500;
        assert WrenchEu.eu(WrenchHandler.Result.REMOVE, true) == 10000;
        assert new ElectricCharge.Spec(12000, 250, 1, true).max() == 12000;
        assert new ElectricCharge.Spec(10000, 100, 1, true).transfer() == 100;
        assert pin(BronzeStats.DURABILITY) == 350 && BronzeStats.DURABILITY > 250;
        assert pin(BronzeStats.SPEED) == 6.5f && BronzeStats.SPEED > 6f;
        assert pin(BronzeStats.ENCHANT) == 14;
        assert pin(DrillStats.MINING_SPEED) == 10f;
        assert pin(DrillStats.DIAMOND_SPEED) == 20f;
        assert pin(DrillStats.DIAMOND_SPEED) == 2f * DrillStats.MINING_SPEED;
        // JAR EU costs unchanged despite speed desvio
        assert pin(DrillStats.MINING_EU) == 50;
        assert pin(DrillStats.DIAMOND_EU) == 80;
        assert pin(com.oldustry.item.JetpackFuel.MAX) == 17998;
        assert com.oldustry.item.JetpackFuel.afterUse(17998, 9) == 17985;
        assert com.oldustry.item.JetpackFuel.afterUse(20, 6) == 10;
        assert com.oldustry.item.JetpackFuel.afterUse(10, 9) == 0;
        assert com.oldustry.item.JetpackFuel.afterCanner(0, 6 * 2548) == 15286;
        assert com.oldustry.item.JetpackFuel.afterCanner(15286, 6 * 2548) == 17998;
        assert pin(DrillStats.MINING_ENCHANT) == 14;
        assert pin(DrillStats.DIAMOND_ENCHANT) == 10;
        // Chainsaw: JAR efficiency 12 / EU 100; desvio speed 15
        assert pin(ChainsawStats.SPEED) == 15f && ChainsawStats.SPEED > 12f;
        assert pin(ChainsawStats.EU) == 100;
        // Generator / geo JAR numbers
        assert MachineKind.GENERATOR.capacity == 4000 && MachineKind.GENERATOR.packet == 10;
        assert MachineKind.GEO_GENERATOR.buffer() == 32 && MachineKind.GEO_GENERATOR.packet == 20;
        assert pin(FuelLogic.GEO_MAX_LAVA) == 24000 && pin(FuelLogic.GEO_CELL) == 1000;

        // ── Canner EU numbers (JAR spec) ──
        assert MachineKind.CANNER.capacity == 600 : "canner buf must be 600";
        assert MachineKind.CANNER.euPerTick() == 1 : "canner must use 1 EU/t";
        // coalfuel=2548, biofuel=868 (IC2 JAR spec)
        assert pin(com.oldustry.machine.CannerFuelValues.COALFUEL) == 2548 : "coalfuel EU";
        assert pin(com.oldustry.machine.CannerFuelValues.BIOFUEL) == 868 : "biofuel EU";
        // Filled tin can desvio (experimental wiki): 1 hunger → sat 3.2 via modifier 1.6
        assert net.minecraft.world.food.FoodConstants.saturationByModifier(1, 1.6f) == 3.2f;
        // miner buf=1000 (JAR spec); pump charge target 200
        assert MachineKind.MINER.capacity == 1000 : "miner buf";
        assert MachineKind.MINER.packet == 32 : "miner packet";
        assert MachineKind.PUMP.capacity == 200 : "pump buf";
        assert MachineKind.PUMP.packet == 128 : "pump packet";
        // luminator JAR: 10k buf / maxInput 32
        assert MachineKind.LUMINATOR.capacity == 10000 : "luminator buf";
        assert MachineKind.LUMINATOR.packet == 32 : "luminator maxInput";
        // D4 electro / magnet / induction
        assert MachineKind.ELECTROLYZER.capacity == 20000 : "electrolyzer buf";
        assert MachineKind.ELECTROLYZER.packet == 0 : "electrolyzer not EnergyNet sink";
        assert MachineKind.MAGNETIZER.capacity == 100 && MachineKind.MAGNETIZER.packet == 32;
        assert MachineKind.INDUCTION.capacity == 10000 && MachineKind.INDUCTION.packet == 128;
        assert MachineKind.INDUCTION.duration() == 4000 && MachineKind.INDUCTION.euPerTick() == 15;
        assert !MachineGui.hasGui(MachineKind.MAGNETIZER);

        System.out.println("EnergyCheck ok");
    }

    /** ECJ folds {@code static final == literal} into "comparing identical expressions". A call stays a real spec lock. */
    private static int pin(int value) {
        return value;
    }

    private static float pin(float value) {
        return value;
    }

    private EnergyCheck() {}
}
