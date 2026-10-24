package com.oldustry.item.equipment;

import com.oldustry.compat.CuriosWearables;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

/**
 * JAR {@code ItemArmorSolarHelmet}: no EU storage; 1 EU/t into chest {@code IElectricItem}
 * when {@link #isSunVisible}. Curios back packs (batpack/lappack) = soft extension.
 */
public class SolarHelmetItem extends Item {
    public SolarHelmetItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player player) || slot != EquipmentSlot.HEAD) {
            return;
        }
        tickCharge(level, player);
    }

    /** Worn in armor head or Curios head. */
    public static void tickCharge(ServerLevel level, Player player) {
        if (player.getAbilities().instabuild) {
            return;
        }
        if (!isSunVisible(level, player.blockPosition().above())) {
            return;
        }
        // JAR: chest IElectricItem +1 EU ignoreTransfer. Curios back = soft QoL.
        CuriosWearables.chargePacksIgnoreTransfer(player, 1);
    }

    /**
     * JAR {@code TileEntitySolarGenerator.isSunVisible}:
     * day && !hasNoSky && canSeeSky && (desert || (!rain && !thunder)).
     */
    public static boolean isSunVisible(Level level, BlockPos pos) {
        if (!level.isBrightOutside() || level.dimensionType().hasCeiling()) {
            return false;
        }
        if (!level.canSeeSky(pos)) {
            return false;
        }
        if (level.getBiome(pos).is(Tags.Biomes.IS_DESERT)) {
            return true;
        }
        return !level.isRaining() && !level.isThundering();
    }
}
