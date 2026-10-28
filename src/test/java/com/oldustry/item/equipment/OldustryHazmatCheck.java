package com.oldustry.item.equipment;

import com.oldustry.item.equipment.OldustryHazmatArmor.Attack;
import com.oldustry.item.equipment.OldustryHazmatArmor.Hit;
import com.oldustry.item.equipment.OldustryHazmatArmor.Kind;
import com.oldustry.item.equipment.OldustryHazmatArmor.Piece;
import com.oldustry.item.equipment.OldustryHazmatArmor.Source;

public final class OldustryHazmatCheck {
    public static void main(String[] args) {
        assert OldustryHazmatArmor.MAX_DAMAGE == 64;
        assert OldustryHazmatArmor.DISPLAY == 1;
        assert OldustryHazmatArmor.complete(true, true, true, true);
        assert !OldustryHazmatArmor.complete(true, true, true, false);
        assert !OldustryHazmatArmor.complete(false, true, true, true);
        assert !OldustryHazmatArmor.complete(true, false, true, true);
        assert !OldustryHazmatArmor.complete(true, true, false, true);
        // A curios or inventory copy does not fill the missing slot.
        assert !OldustryHazmatArmor.complete(true, true, true, false);

        Piece[] set = freshSet();
        Hit immune = OldustryHazmatArmor.absorb(20f, set, attack(Source.IN_FIRE, false));
        assert immune.remaining() == 0f : immune.remaining();
        assert wear(immune) == 0;
        assert immune.fireResist();
        Hit burning = OldustryHazmatArmor.absorb(20f, freshSet(), attack(Source.ON_FIRE, true));
        assert burning.remaining() == 0f && wear(burning) == 0 && !burning.fireResist();
        Hit lava = OldustryHazmatArmor.absorb(8f, freshSet(), attack(Source.LAVA, false));
        assert lava.remaining() == 0f && wear(lava) == 0 && lava.fireResist();
        Hit wall = OldustryHazmatArmor.absorb(6f, freshSet(), attack(Source.IN_WALL, true));
        assert wall.remaining() == 0f && wear(wall) == 0 && !wall.fireResist();
        Hit shock = OldustryHazmatArmor.absorb(4f, freshSet(), attack(Source.ELECTRICITY, false));
        assert shock.remaining() == 0f && wear(shock) == 0;
        Hit rad = OldustryHazmatArmor.absorb(4f, freshSet(), attack(Source.RADIATION, true));
        assert rad.remaining() == 0f && wear(rad) == 0;

        // Four rubber boots still count as the set for wear, but there is no helmet to grant fire resistance.
        Piece[] bootsOnly = new Piece[4];
        for (int i = 0; i < 4; i++) {
            bootsOnly[i] = new Piece(Kind.HAZMAT, OldustryHazmatItem.BOOTS, 0, 0, 64);
        }
        assert OldustryHazmatArmor.complete(bootsOnly);
        Hit wrong = OldustryHazmatArmor.absorb(10f, bootsOnly, attack(Source.IN_FIRE, false));
        assert wrong.remaining() == 0f && wear(wrong) == 0 && !wrong.fireResist();

        Piece[] chest = new Piece[4];
        chest[2] = fresh(OldustryHazmatItem.CHEST);
        Hit one = OldustryHazmatArmor.absorb(10f, chest, attack(Source.OTHER, false));
        assert one.remaining() == 0f : one.remaining();
        assert one.hazmat(2) == 20;

        Hit capped = OldustryHazmatArmor.absorb(40f, chest, attack(Source.OTHER, false));
        assert capped.hazmat(2) == 64;
        assert Math.abs(capped.remaining() - 8f) < 0.001f : capped.remaining();

        Piece[] worn = new Piece[4];
        worn[2] = new Piece(Kind.HAZMAT, OldustryHazmatItem.CHEST, 63, 0, 64);
        Hit almost = OldustryHazmatArmor.absorb(10f, worn, attack(Source.OTHER, false));
        assert almost.hazmat(2) == 2;
        assert Math.abs(almost.remaining() - 9f) < 0.001f : almost.remaining();

        Piece[] broken = new Piece[4];
        broken[2] = new Piece(Kind.HAZMAT, OldustryHazmatItem.CHEST, 64, 0, 64);
        Hit dead = OldustryHazmatArmor.absorb(10f, broken, attack(Source.OTHER, false));
        assert dead.hazmat(2) == 0;
        assert Math.abs(dead.remaining() - 10f) < 0.001f : dead.remaining();

        Hit shared = OldustryHazmatArmor.absorb(10f, freshSet(), attack(Source.OTHER, false));
        assert shared.remaining() == 0f;
        assert shared.hazmat(0) == 4 && shared.hazmat(1) == 4 && shared.hazmat(2) == 4 && shared.hazmat(3) == 4;

        Piece[] three = freshSet();
        three[3] = null;
        Hit open = OldustryHazmatArmor.absorb(10f, three, attack(Source.IN_FIRE, false));
        assert open.hazmat(0) == 6 && open.hazmat(1) == 6 && open.hazmat(2) == 6 && open.hazmat(3) == 0;
        assert !open.fireResist();

        assert OldustryHazmatArmor.fallDamage(10) == 7;
        assert OldustryHazmatArmor.fallDamage(11) == 8;
        assert OldustryHazmatArmor.fallWear(7) == 4;
        assert OldustryHazmatArmor.cancelsFall(7, 0);
        assert OldustryHazmatArmor.cancelsFall(7, 60);
        assert !OldustryHazmatArmor.cancelsFall(7, 61);
        assert !OldustryHazmatArmor.cancelsFall(8, 0);

        Piece[] fallBoots = new Piece[4];
        fallBoots[0] = fresh(OldustryHazmatItem.BOOTS);
        Hit steep = OldustryHazmatArmor.absorb(10f, fallBoots, attack(Source.FALL, true));
        assert steep.hazmat(0) == 4;
        assert Math.abs(steep.remaining() - 1.25f) < 0.001f : steep.remaining();

        Piece[] mixed = freshSet();
        mixed[2] = new Piece(Kind.NANO, OldustryHazmatItem.CHEST, 100_000, 0, 0);
        Hit mix = OldustryHazmatArmor.absorb(10f, mixed, attack(Source.OTHER, false));
        assert mix.nano(2) > 0 && mix.hazmat(0) > 0;
        assert mix.remaining() < 10f;
        Hit mixedShock = OldustryHazmatArmor.absorb(10f, mixed, attack(Source.ELECTRICITY, false));
        assert mixedShock.nano(2) > 0 && mixedShock.hazmat(0) > 0;
        assert !mixedShock.fireResist();

        Piece[] vanilla = new Piece[4];
        vanilla[2] = new Piece(Kind.VANILLA, OldustryHazmatItem.CHEST, 0, 8, 528);
        vanilla[0] = fresh(OldustryHazmatItem.BOOTS);
        Hit withDiamond = OldustryHazmatArmor.absorb(10f, vanilla, attack(Source.OTHER, false));
        assert withDiamond.hazmat(0) > 0 && withDiamond.vanillaWear()[2] > 0;
        assert withDiamond.vanillaWear()[2] < 10;

        var airFull = OldustryHazmatArmor.air(100, true, false);
        assert airFull.consume() && airFull.air() == 250 && airFull.dropEmpty();
        var airRoom = OldustryHazmatArmor.air(100, true, true);
        assert airRoom.consume() && !airRoom.dropEmpty();
        assert !OldustryHazmatArmor.air(101, true, true).consume();
        assert !OldustryHazmatArmor.air(100, false, true).consume();
        assert OldustryHazmatArmor.air(0, true, true).air() == 150;

        assert OldustryHazmatArmor.insideMaterial(64.0, 64, 0);
        assert !OldustryHazmatArmor.insideMaterial(64.5, 64, 7);
        assert OldustryHazmatArmor.lavaMeta(true, 8) == 0;
        assert OldustryHazmatArmor.insideMaterial(64.0, 64, OldustryHazmatArmor.lavaMeta(false, 8));
        assert OldustryHazmatArmor.absorbMax(0, false) == 800;
        assert OldustryHazmatArmor.absorbMax(1, false) == 800;
        assert OldustryHazmatArmor.absorbMax(63, false) == 25;
        assert OldustryHazmatArmor.absorbMax(64, false) == 0;
        assert OldustryHazmatArmor.absorbMax(0, true) == 3250;

        System.out.println("OldustryHazmatCheck ok");
    }

    private static Piece[] freshSet() {
        return new Piece[] {
            fresh(OldustryHazmatItem.BOOTS),
            fresh(OldustryHazmatItem.LEGS),
            fresh(OldustryHazmatItem.CHEST),
            fresh(OldustryHazmatItem.HELMET)
        };
    }

    private static Piece fresh(int armorType) {
        return new Piece(Kind.HAZMAT, armorType, 0, 0, OldustryHazmatArmor.MAX_DAMAGE);
    }

    private static Attack attack(Source source, boolean bypass) {
        return new Attack(source, bypass);
    }

    private static int wear(Hit hit) {
        int sum = 0;
        for (int slot = 0; slot < 4; slot++) {
            sum += hit.hazmat(slot) + hit.nano(slot) + hit.vanillaWear()[slot];
        }
        return sum;
    }

    private OldustryHazmatCheck() {}
}
