package com.oldustry.item.equipment;

import com.oldustry.Oldustry;
import com.oldustry.energy.OldustryDamage;
import com.oldustry.item.ElectricItem;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * JAR {@code ItemArmorHazmat}. Four pieces, durability 64, display 1.
 * The helmet refills air; the full set blocks the environmental sources and nuke fallout.
 */
@EventBusSubscriber(modid = Oldustry.MOD_ID)
public class OldustryHazmatItem extends Item {
    /** 1.5.2 {@code armorType}: 0 helmet, 1 chest, 2 legs, 3 boots. */
    public static final int HELMET = 0;
    public static final int CHEST = 1;
    public static final int LEGS = 2;
    public static final int BOOTS = 3;

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    private final int armorType;

    public OldustryHazmatItem(Properties properties, int armorType) {
        super(properties);
        this.armorType = armorType;
    }

    public int armorType() {
        return armorType;
    }

    public static boolean wearing(LivingEntity entity) {
        for (EquipmentSlot slot : SLOTS) {
            if (entity.getItemBySlot(slot).getItem() instanceof OldustryHazmatItem) {
                return true;
            }
        }
        return false;
    }

    /** JAR {@code hasCompleteHazmat}: a player with a hazmat piece in every armor slot. */
    public static boolean blocksFallout(LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        for (EquipmentSlot slot : SLOTS) {
            if (!(entity.getItemBySlot(slot).getItem() instanceof OldustryHazmatItem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (armorType != HELMET || slot != EquipmentSlot.HEAD || !(entity instanceof Player player)) {
            return;
        }
        if (player.isOnFire() && blocksFallout(player)) {
            if (inLavaOrFire(player)) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, OldustryHazmatArmor.LAVA_RESIST_TICKS, 0));
            }
            player.clearFire();
        }
        int supply = player.getAirSupply();
        // Room is decided after the cell leaves: a stack of 1 frees its own slot.
        OldustryHazmatArmor.AirTick air = OldustryHazmatArmor.air(supply, hasAirCell(player), true);
        if (!air.consume()) {
            return;
        }
        takeAirCell(player);
        ItemStack empty = new ItemStack(OldustryItems.EMPTY_CELL.get());
        if (!player.addItem(empty)) {
            player.drop(empty, false);
        }
        player.setAirSupply(air.air());
    }

    @SubscribeEvent
    static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!(boots.getItem() instanceof OldustryHazmatItem hazmat) || hazmat.armorType != BOOTS) {
            return;
        }
        int fall = OldustryHazmatArmor.fallDamage(event.getDistance());
        if (!OldustryHazmatArmor.cancelsFall(fall, boots.getDamageValue())) {
            return;
        }
        int cost = OldustryHazmatArmor.fallWear(fall);
        if (cost > 0) {
            boots.hurtAndBreak(cost, player, EquipmentSlot.FEET);
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    static void onHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide() || !wearing(entity)) {
            return;
        }
        float damage = event.getContainer().getNewDamage();
        if (damage <= 0) {
            return;
        }
        OldustryHazmatArmor.Piece[] pieces = pieces(entity);
        OldustryHazmatArmor.Hit hit = OldustryHazmatArmor.absorb(damage, pieces, attack(event.getSource()));
        for (int i = 0; i < 4; i++) {
            ItemStack stack = entity.getItemBySlot(SLOTS[i]);
            int worn = hit.hazmat(i) + hit.vanillaWear()[i];
            if (worn > 0) {
                stack.hurtAndBreak(worn, entity, SLOTS[i]);
            }
            int eu = hit.nano(i);
            if (eu > 0 && stack.getItem() instanceof NanoSuitItem) {
                ElectricItem.setEnergy(stack, Math.max(0, ElectricItem.energy(stack) - eu));
            }
        }
        if (hit.fireResist() && entity instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, OldustryHazmatArmor.RESIST_TICKS, 1));
        }
        event.getContainer().setNewDamage(hit.remaining());
        // The pass already applied hazmat, nano and vanilla ratios. The +1 display must not reduce again.
        event.getContainer().addModifier(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.ARMOR, (container, vanilla) -> 0f);
    }

    @SubscribeEvent
    static void attributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof OldustryHazmatItem)) {
            return;
        }
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return;
        }
        event.addModifier(
            Attributes.ARMOR,
            new AttributeModifier(
                Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "hazmat_armor_" + equippable.slot().getName()),
                OldustryHazmatArmor.DISPLAY,
                AttributeModifier.Operation.ADD_VALUE
            ),
            EquipmentSlotGroup.bySlot(equippable.slot())
        );
    }

    static OldustryHazmatArmor.Attack attack(DamageSource source) {
        OldustryHazmatArmor.Source kind;
        if (source.is(DamageTypes.IN_FIRE)) {
            kind = OldustryHazmatArmor.Source.IN_FIRE;
        } else if (source.is(DamageTypes.ON_FIRE)) {
            kind = OldustryHazmatArmor.Source.ON_FIRE;
        } else if (source.is(DamageTypes.LAVA)) {
            kind = OldustryHazmatArmor.Source.LAVA;
        } else if (source.is(DamageTypes.IN_WALL)) {
            kind = OldustryHazmatArmor.Source.IN_WALL;
        } else if (source.is(DamageTypes.FALL)) {
            kind = OldustryHazmatArmor.Source.FALL;
        } else if (source.is(OldustryDamage.ELECTRICITY)) {
            kind = OldustryHazmatArmor.Source.ELECTRICITY;
        } else if (source.is(OldustryDamage.RADIATION)) {
            kind = OldustryHazmatArmor.Source.RADIATION;
        } else {
            kind = OldustryHazmatArmor.Source.OTHER;
        }
        return new OldustryHazmatArmor.Attack(kind, source.is(DamageTypeTags.BYPASSES_ARMOR));
    }

    private static OldustryHazmatArmor.Piece[] pieces(LivingEntity entity) {
        OldustryHazmatArmor.Piece[] pieces = new OldustryHazmatArmor.Piece[4];
        for (int slot = 0; slot < 4; slot++) {
            ItemStack stack = entity.getItemBySlot(SLOTS[slot]);
            if (stack.getItem() instanceof OldustryHazmatItem hazmat) {
                pieces[slot] = new OldustryHazmatArmor.Piece(
                    OldustryHazmatArmor.Kind.HAZMAT, hazmat.armorType, stack.getDamageValue(), 0, OldustryHazmatArmor.MAX_DAMAGE
                );
            } else if (stack.getItem() instanceof NanoSuitItem) {
                pieces[slot] = new OldustryHazmatArmor.Piece(
                    OldustryHazmatArmor.Kind.NANO, 3 - slot, ElectricItem.energy(stack), 0, 0
                );
            } else if (!stack.isEmpty() && stack.isDamageableItem()) {
                int points = armorPoints(stack, SLOTS[slot]);
                if (points > 0) {
                    pieces[slot] = new OldustryHazmatArmor.Piece(
                        OldustryHazmatArmor.Kind.VANILLA, 3 - slot, stack.getDamageValue(), points, stack.getMaxDamage()
                    );
                }
            }
        }
        return pieces;
    }

    private static int armorPoints(ItemStack stack, EquipmentSlot slot) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(slot);
        int points = 0;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.slot() != group || !entry.attribute().is(Attributes.ARMOR)) {
                continue;
            }
            if (entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                points += (int) entry.modifier().amount();
            }
        }
        return points;
    }

    /** JAR samples the block at the feet, and only lava or fire material. */
    private static boolean inLavaOrFire(Player player) {
        double sample = player.getY() + 0.02;
        int x = Mth.floor(player.getX());
        int y = Mth.floor(sample);
        int z = Mth.floor(player.getZ());
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = player.level().getBlockState(pos);
        FluidState fluid = state.getFluidState();
        int meta;
        if (fluid.is(net.minecraft.tags.FluidTags.LAVA)) {
            meta = OldustryHazmatArmor.lavaMeta(fluid.isSource(), fluid.getAmount());
        } else if (state.is(Blocks.FIRE)) {
            meta = state.getValue(net.minecraft.world.level.block.FireBlock.AGE);
        } else {
            return false;
        }
        return OldustryHazmatArmor.insideMaterial(player.getY(), y, meta);
    }

    /** JAR {@code consumeInventoryItem} walks the 36-slot main inventory. */
    private static boolean hasAirCell(Player player) {
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getItem(i).is(OldustryItems.AIR_CELL.get())) {
                return true;
            }
        }
        return false;
    }

    private static void takeAirCell(Player player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(OldustryItems.AIR_CELL.get())) {
                stack.shrink(1);
                return;
            }
        }
    }

}
