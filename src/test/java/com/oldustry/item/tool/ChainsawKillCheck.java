package com.oldustry.item.tool;

public final class ChainsawKillCheck {
    public static void main(String[] args) {
        assert ChainsawStats.killedCreeper(true, true, 0f);
        assert ChainsawStats.killedCreeper(true, true, -1f);
        assert !ChainsawStats.killedCreeper(true, true, 1f);
        assert !ChainsawStats.killedCreeper(false, true, 0f);
        assert !ChainsawStats.killedCreeper(true, false, 0f);
        System.out.println("ChainsawKillCheck ok");
    }
}
