package com.oldustry.item.tool;

public final class ObscuratorCheck {
    public static void main(String[] args) {
        assert ObscuratorStats.CAPACITY == 100000;
        assert ObscuratorStats.TRANSFER == 250;
        assert ObscuratorStats.TIER == 2;
        assert ObscuratorStats.SCAN_COST == 20000;
        assert ObscuratorStats.PRINT_COST == 5000;
        assert ObscuratorStats.afford(20000, ObscuratorStats.SCAN_COST);
        assert !ObscuratorStats.afford(19999, ObscuratorStats.SCAN_COST);
        assert ObscuratorStats.afford(5000, ObscuratorStats.PRINT_COST);
        assert !ObscuratorStats.afford(4999, ObscuratorStats.PRINT_COST);
        assert !ObscuratorStats.afford(0, ObscuratorStats.PRINT_COST);

        assert ObscuratorStats.canScan(false, true, true, false);
        assert !ObscuratorStats.canScan(true, true, true, false);
        assert !ObscuratorStats.canScan(false, false, true, false);
        assert !ObscuratorStats.canScan(false, true, false, false);
        assert !ObscuratorStats.canScan(false, true, true, true);

        assert !ObscuratorStats.canPrint(0);
        assert !ObscuratorStats.canPrint(1);
        assert ObscuratorStats.canPrint(2);

        assert ObscuratorStats.paintChanges(false, true, true, true);
        assert !ObscuratorStats.paintChanges(true, true, true, true);
        assert ObscuratorStats.paintChanges(true, false, true, true);
        assert ObscuratorStats.paintChanges(true, true, false, true);
        assert ObscuratorStats.paintChanges(true, true, true, false);

        System.out.println("ObscuratorCheck ok");
    }
}
