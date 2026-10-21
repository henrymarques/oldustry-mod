package com.oldustry.item;

import com.oldustry.OldustryConfig;
import com.oldustry.energy.ElectricCharge;
import com.oldustry.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

public class ScannerItem extends ElectricItem {
    private final boolean valuable;

    public ScannerItem(ElectricCharge.Spec spec, boolean valuable, Properties properties) {
        super(spec, properties);
        this.valuable = valuable;
    }

    /** OD=2 / OV=4 on success; 0 if not enough EU. Drains 50 / 250. IC2 startLayerScan. */
    public int startLayerScan(ItemStack stack) {
        int cost = valuable ? 250 : 50;
        // Machine slot: the scanner pays its own EU. No player pack.
        if (energy(stack) < cost) {
            return 0;
        }
        spend(stack, cost);
        return valuable ? 4 : 2;
    }

    public static boolean isValuable(BlockState state) {
        return value(state) > 0;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int cost = valuable ? 250 : 50;
        if (!canPay(stack, cost, player)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            if (!pay(stack, cost, player)) {
                return InteractionResult.FAIL;
            }
            int range = valuable ? 4 : 2;
            int score = 0;
            int scanned = 0;
            BlockPos origin = player.blockPosition();
            for (int y = origin.getY(); y > level.getMinY(); y--) {
                for (int x = origin.getX() - range; x <= origin.getX() + range; x++) {
                    for (int z = origin.getZ() - range; z <= origin.getZ() + range; z++) {
                        int v = value(level.getBlockState(new BlockPos(x, y, z)));
                        score += valuable ? v : (v > 0 ? 1 : 0);
                        scanned++;
                    }
                }
            }
            int result = scanned > 0 ? (int) (1000.0 * score / scanned) : 0;
            player.sendSystemMessage(Component.translatable(
                valuable ? "info.oldustry.scan.value" : "info.oldustry.scan.density", result));
        } else {
            ModSounds.play(level, player, player.blockPosition(), ModSounds.SCANNER.get(), SoundSource.PLAYERS, 1f, 1f);
        }
        return InteractionResult.SUCCESS;
    }

    static int value(BlockState state) {
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        String cfg = OldustryConfig.VALUABLE_ORES.get();
        for (String part : cfg.split(",")) {
            String[] kv = part.split("=");
            if (kv.length == 2 && kv[0].trim().equals(id)) {
                return Integer.parseInt(kv[1].trim());
            }
        }
        int byPath = ValuableOre.pathValue(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath());
        if (byPath > 0) {
            return byPath;
        }
        // JAR registerOre: any other ore dictionary name starting with "ore" is value 1.
        return state.is(Tags.Blocks.ORES) ? 1 : 0;
    }
}
