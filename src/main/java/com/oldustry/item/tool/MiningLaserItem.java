package com.oldustry.item.tool;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.entity.MiningLaser;
import com.oldustry.item.ElectricToolItem;
import com.oldustry.network.KeysPayload;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustrySounds;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** IC2 {@code ItemToolMiningLaser}. Mode key cycles 0–6. Horizontal fires only on a block. */
public final class MiningLaserItem extends ElectricToolItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(
        OldustryLaser.CAPACITY, OldustryLaser.TRANSFER, OldustryLaser.TIER, false
    );

    public MiningLaserItem(Properties properties) {
        super(SPEC, OldustryLaser.BREAK_COST, properties);
    }

    public static int mode(ItemStack stack) {
        return Math.floorMod(stack.getOrDefault(OldustryDataComponents.LASER_MODE.get(), 0), OldustryLaser.MODES);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (KeysPayload.mode(player)) {
            return InteractionResult.SUCCESS;
        }
        int setting = mode(stack);
        if (setting == OldustryLaser.HORIZONTAL || !(level instanceof ServerLevel server)) {
            return setting == OldustryLaser.HORIZONTAL ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }
        if (!fire(server, player, stack, setting, player.getYRot(), player.getXRot(), OldustryLaser.eyeLaunch(player.getY(), player.getEyeHeight()))) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || KeysPayload.mode(player)) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        if (mode(stack) != OldustryLaser.HORIZONTAL) {
            return InteractionResult.PASS;
        }
        if (!(context.getLevel() instanceof ServerLevel server)) {
            return InteractionResult.PASS;
        }
        int y = context.getClickedPos().getY();
        if (!OldustryLaser.horizontalAim(player.getY() + player.getEyeHeight(), y)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.translatable("info.oldustry.laser.steep"));
            }
            return InteractionResult.PASS;
        }
        if (!pay(stack, OldustryLaser.HORIZONTAL_COST, player)) {
            return InteractionResult.PASS;
        }
        MiningLaser.shoot(server, player, Float.POSITIVE_INFINITY, 5f, Integer.MAX_VALUE, false, false, player.getYRot(), 0f, y + 0.5);
        play(server, player, OldustryLaser.HORIZONTAL);
        return InteractionResult.PASS;
    }

    private boolean fire(ServerLevel level, Player player, ItemStack stack, int setting, float yaw, float pitch, double y) {
        if (!pay(stack, OldustryLaser.cost(setting), player)) {
            return false;
        }
        switch (setting) {
            case 0 -> MiningLaser.shootLook(level, player, Float.POSITIVE_INFINITY, 5f, Integer.MAX_VALUE, false, false);
            case 1 -> MiningLaser.shootLook(level, player, 4f, 5f, 1, false, false);
            case 2 -> MiningLaser.shootLook(level, player, Float.POSITIVE_INFINITY, 20f, Integer.MAX_VALUE, false, false);
            case 4 -> MiningLaser.shootLook(level, player, Float.POSITIVE_INFINITY, 8f, Integer.MAX_VALUE, false, true);
            case 5 -> {
                for (int column = -2; column <= 2; column++) {
                    for (int row = -2; row <= 2; row++) {
                        MiningLaser.shoot(
                            level, player, Float.POSITIVE_INFINITY, 12f, Integer.MAX_VALUE, false, false,
                            OldustryLaser.scatterYaw(yaw, column), OldustryLaser.scatterPitch(pitch, row), y
                        );
                    }
                }
            }
            case 6 -> MiningLaser.shootLook(level, player, Float.POSITIVE_INFINITY, 12f, Integer.MAX_VALUE, true, false);
            default -> {
                return true;
            }
        }
        play(level, player, setting);
        return true;
    }

    private static void play(ServerLevel level, Player player, int setting) {
        var sound = switch (setting) {
            case 1 -> OldustrySounds.MINING_LASER_LOW.get();
            case 2 -> OldustrySounds.MINING_LASER_LONG.get();
            case 5 -> OldustrySounds.MINING_LASER_SCATTER.get();
            case 6 -> OldustrySounds.MINING_LASER_EXPLOSIVE.get();
            default -> OldustrySounds.MINING_LASER.get();
        };
        OldustrySounds.play(level, null, player.blockPosition(), sound, SoundSource.PLAYERS, 1f, 1f);
    }
}
