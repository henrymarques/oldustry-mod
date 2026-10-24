package com.oldustry.machine;

import java.util.function.IntPredicate;
import com.oldustry.item.tool.ValuableOre;
import com.oldustry.registry.OldustryTags;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.Tags;

public final class OldustryMinerCheck {
    public static void main(String[] args) {
        // JAR getOperationHeight: first cell that is not a pipe. A broken pipe is that cell.
        assert OldustryMinerColumn.head(10, 0, y -> y == 5 ? OldustryMinerColumn.TIP : OldustryMinerColumn.PIPE) == 5;
        assert OldustryMinerColumn.head(10, 0, y -> switch (y) {
            case 5 -> OldustryMinerColumn.TIP;
            case 7 -> OldustryMinerColumn.AIR;
            default -> y > 5 && y < 10 ? OldustryMinerColumn.PIPE : OldustryMinerColumn.OTHER;
        }) == 7;
        assert OldustryMinerColumn.head(10, 0, y -> y == 9 ? OldustryMinerColumn.AIR : OldustryMinerColumn.OTHER) == 9;
        assert ValuableOre.valueOf(Tags.Blocks.ORES_COPPER) == 2;
        assert ValuableOre.valueOf(OldustryTags.Blocks.ORES_TIN) == 2;
        assert ValuableOre.valueOf(Tags.Blocks.ORES_GOLD) == 3;
        assert ValuableOre.valueOf(Tags.Blocks.ORES_LAPIS) == 3;
        assert ValuableOre.valueOf(Tags.Blocks.ORES_REDSTONE) == 3;
        assert ValuableOre.valueOf(OldustryTags.Blocks.ORES_SILVER) == 3;
        assert ValuableOre.valueOf(Tags.Blocks.ORES_DIAMOND) == 4;
        assert ValuableOre.valueOf(OldustryTags.Blocks.ORES_URANIUM) == 4;
        assert ValuableOre.valueOf(OldustryTags.Blocks.ORES_RUBY) == 4;
        assert ValuableOre.valueOf(OldustryTags.Blocks.ORES_SAPPHIRE) == 4;
        assert ValuableOre.valueOf(OldustryTags.Blocks.ORES_GREEN_SAPPHIRE) == 4;
        assert ValuableOre.valueOf(Tags.Blocks.ORES_EMERALD) == 5;
        assert ValuableOre.valueOf(Tags.Blocks.ORES_QUARTZ) == 5;
        assert ValuableOre.valueOf(OldustryTags.Blocks.ORES_TUNGSTEN) == 5;
        assert ValuableOre.valueOf(Tags.Blocks.ORES) == 1;
        assert ValuableOre.valueOf(Tags.Blocks.STORAGE_BLOCKS) == 0;
        assert ValuableOre.specificBeforeCatchAll();
        IntPredicate block = slot -> slot == OldustryMinerAccess.PIPE;
        IntPredicate drill = slot -> slot == OldustryMinerAccess.DRILL || slot == OldustryMinerAccess.BATTERY;
        IntPredicate battery = slot -> slot == OldustryMinerAccess.BATTERY;
        assert OldustryMinerAccess.insert(OldustryMinerAccess.PIPE, Direction.NORTH, block);
        assert OldustryMinerAccess.insert(OldustryMinerAccess.PIPE, Direction.UP, block);
        assert !OldustryMinerAccess.insert(OldustryMinerAccess.DRILL, Direction.UP, block);
        assert OldustryMinerAccess.insert(OldustryMinerAccess.DRILL, Direction.UP, drill);
        assert OldustryMinerAccess.insert(OldustryMinerAccess.DRILL, Direction.NORTH, drill);
        assert !OldustryMinerAccess.insert(OldustryMinerAccess.BATTERY, Direction.UP, drill);
        assert OldustryMinerAccess.insert(OldustryMinerAccess.BATTERY, Direction.NORTH, drill);
        assert OldustryMinerAccess.insert(OldustryMinerAccess.BATTERY, Direction.UP, battery);
        assert OldustryMinerAccess.insert(OldustryMinerAccess.BATTERY, Direction.DOWN, battery);
        assert OldustryMinerAccess.canExtract(OldustryMinerAccess.DRILL, Direction.UP);
        assert !OldustryMinerAccess.canExtract(OldustryMinerAccess.DRILL, Direction.DOWN);
        assert OldustryMinerAccess.canExtract(OldustryMinerAccess.SCANNER, Direction.DOWN);
        assert !OldustryMinerAccess.canExtract(OldustryMinerAccess.SCANNER, Direction.NORTH);
        assert OldustryMinerAccess.canExtract(OldustryMinerAccess.PIPE, Direction.WEST);
        assert !OldustryMinerAccess.canExtract(OldustryMinerAccess.PIPE, Direction.UP);
        assert !OldustryMinerAccess.canExtract(OldustryMinerAccess.BATTERY, Direction.UP);
        assert !OldustryMinerAccess.canExtract(OldustryMinerAccess.BATTERY, Direction.DOWN);
        assert !OldustryMinerAccess.canExtract(OldustryMinerAccess.BATTERY, Direction.SOUTH);
        System.out.println("OldustryMinerCheck ok");
    }

    private OldustryMinerCheck() {}
}
