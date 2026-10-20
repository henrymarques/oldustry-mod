package com.oldustry.item;

import com.oldustry.energy.ElectricCharge;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * JAR {@code ItemBattery} right-click on charged RE battery: push EU into hotbar
 * electric items (not other battery cells), up to {@code 2 × transfer} per target.
 */
public class ReBatteryItem extends ElectricItem {
    public ReBatteryItem(ElectricCharge.Spec spec, Properties properties) {
        super(spec, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack battery = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // JAR: discharge(2 * transferLimit, ignoreTransfer=true) → charge → real discharge.
        int burst = 2 * spec().transfer();
        var inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack target = inv.getItem(i);
            if (!(target.getItem() instanceof ElectricItem electric) || electric.isBatteryCell()) {
                continue;
            }
            int offer = ElectricCharge.dischargeIgnoreTransfer(energy(battery), burst, spec(), electric.spec().tier());
            if (offer <= 0) {
                continue;
            }
            int moved = electric.chargeIgnoreTransfer(target, offer);
            if (moved > 0) {
                dischargeIgnoreTransfer(battery, moved, electric.spec().tier());
            }
        }
        return InteractionResult.SUCCESS;
    }
}
