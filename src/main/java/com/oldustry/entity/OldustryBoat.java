package com.oldustry.entity;

import java.util.function.Supplier;

import com.oldustry.item.ElectricItem;
import com.oldustry.registry.OldustryItems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Modern paddle boat with JAR crash, drop and electric rules.
 * Placement is {@code BoatItem}. The 1.5.2 rider-velocity copy is the paddle input;
 * caps stay {@link OldustryBoatStats#PLAIN_TOP} (vanilla water) and {@link OldustryBoatStats#ELECTRIC_TOP}.
 */
public class OldustryBoat extends net.minecraft.world.entity.vehicle.boat.Boat {
    private final OldustryBoatStats.Kind kind;
    private double movedHorizontally;

    public OldustryBoat(EntityType<? extends OldustryBoat> type, Level level, OldustryBoatStats.Kind kind, Supplier<Item> drop) {
        super(type, level, drop);
        this.kind = kind;
    }

    public OldustryBoatStats.Kind kind() {
        return kind;
    }

    @Override
    public void tick() {
        double speed = movedHorizontally;
        movedHorizontally = 0;
        if (kind == OldustryBoatStats.Kind.ELECTRIC) {
            clearFire();
            if (getControllingPassenger() instanceof Player player) {
                player.clearFire();
                boolean pay = OldustryBoatStats.paysEu(speed);
                boolean powered = level().isClientSide() ? pay && armorCanPay(player) : pay && drainArmor(player);
                if (isLocalInstanceAuthoritative()) {
                    shapeElectric(powered);
                }
            } else if (isLocalInstanceAuthoritative()) {
                shapeElectric(false);
            }
        }
        super.tick();
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        double speed = Math.hypot(movement.x, movement.z);
        movedHorizontally = Math.max(movedHorizontally, speed);
        super.move(type, movement);
        if (!level().isClientSide() && horizontalCollision && OldustryBoatStats.crashes(speed, kind.breakMotion) && !isRemoved()) {
            crash(speed);
        }
    }

    @Override
    public boolean canBoatInFluid(FluidState state) {
        if (state.is(FluidTags.WATER)) {
            return true;
        }
        return kind.lava && state.is(FluidTags.LAVA);
    }

    @Override
    public boolean canBoatInFluid(FluidType type) {
        if (type == net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value()) {
            return true;
        }
        return kind.lava && type == net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value();
    }

    private void shapeElectric(boolean powered) {
        Vec3 motion = getDeltaMovement();
        double horizontal = Math.hypot(motion.x, motion.z);
        double cap = powered ? OldustryBoatStats.ELECTRIC_TOP : OldustryBoatStats.ELECTRIC_COAST;
        if (powered && horizontal > 0.05) {
            double scale = OldustryBoatStats.ELECTRIC_BOOST / horizontal;
            motion = new Vec3(motion.x + motion.x * scale, motion.y, motion.z + motion.z * scale);
            horizontal = Math.hypot(motion.x, motion.z);
        }
        if (horizontal > cap) {
            double scale = cap / horizontal;
            motion = new Vec3(motion.x * scale, motion.y, motion.z * scale);
        }
        setDeltaMovement(motion);
    }

    private void crash(double speed) {
        ejectPassengers();
        if (level() instanceof ServerLevel server) {
            Item item = crashItem(speed);
            if (kind == OldustryBoatStats.Kind.RUBBER) {
                server.playSound(null, blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 16.0F, 8.0F);
            }
            spawnAtLocation(server, new ItemStack(item));
        }
        discard();
    }

    private Item crashItem(double speed) {
        if (kind == OldustryBoatStats.Kind.RUBBER && OldustryBoatStats.rubberSplits(speed)) {
            return OldustryItems.DAMAGED_RUBBER_DINGHY.get();
        }
        return getDropItem();
    }

    private static boolean drainArmor(Player player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }
            ItemStack stack = player.getItemBySlot(slot);
            if (!(stack.getItem() instanceof ElectricItem electric) || !electric.spec().canProvide()) {
                continue;
            }
            if (ElectricItem.energy(stack) < OldustryBoatStats.EU) {
                continue;
            }
            if (electric.dischargeIgnoreTransfer(stack, OldustryBoatStats.EU, Integer.MAX_VALUE) == OldustryBoatStats.EU) {
                return true;
            }
        }
        return false;
    }

    private static boolean armorCanPay(Player player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof ElectricItem electric
                && electric.spec().canProvide()
                && ElectricItem.energy(stack) >= OldustryBoatStats.EU) {
                return true;
            }
        }
        return false;
    }
}
