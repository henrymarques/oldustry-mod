package com.oldustry.item.equipment;

import com.oldustry.compat.CuriosWearables;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** JAR ItemArmorStaticBoots: charge chest pack every 5 blocks walked, up to 3 EU. */
public class StaticBootsItem extends Item {
    public StaticBootsItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player player) || slot != EquipmentSlot.FEET) {
            return;
        }
        chargeFromWalk(player, stack);
    }

    /** JAR walk-distance charge; {@code boots} may be armor feet or Curios feet stack. */
    public static void chargeFromWalk(Player player, ItemStack boots) {
        if (player.getAbilities().instabuild) {
            return;
        }
        int px = player.blockPosition().getX();
        int pz = player.blockPosition().getZ();
        // JAR: riding or water → reset anchor to current pos (no charge).
        boolean isNotWalking = player.getVehicle() != null || player.isInWater();
        if (!boots.has(OldustryDataComponents.WALK_X.get()) || !boots.has(OldustryDataComponents.WALK_Z.get()) || isNotWalking) {
            boots.set(OldustryDataComponents.WALK_X.get(), px);
            boots.set(OldustryDataComponents.WALK_Z.get(), pz);
            return;
        }
        int ox = boots.getOrDefault(OldustryDataComponents.WALK_X.get(), px);
        int oz = boots.getOrDefault(OldustryDataComponents.WALK_Z.get(), pz);
        double distance = Math.sqrt((double) (ox - px) * (ox - px) + (double) (oz - pz) * (oz - pz));
        if (distance < 5.0) {
            return;
        }
        boots.set(OldustryDataComponents.WALK_X.get(), px);
        boots.set(OldustryDataComponents.WALK_Z.get(), pz);
        int eu = Math.min(3, (int) distance / 5);
        CuriosWearables.chargeChestPack(player, eu);
    }
}
