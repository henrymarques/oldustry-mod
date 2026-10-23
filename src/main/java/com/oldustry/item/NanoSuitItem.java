package com.oldustry.item;

import com.oldustry.Oldustry;
import com.oldustry.energy.ElectricCharge;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * JAR {@code ItemArmorNanoSuit}: 100000 / 160 / T2, 90% across the four pieces, 800 EU per point.
 * No vanilla armor points — the ratio replaces them, the way {@code ISpecialArmor} did.
 */
@EventBusSubscriber(modid = Oldustry.MOD_ID)
public class NanoSuitItem extends ElectricItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(NanoArmor.MAX, NanoArmor.TRANSFER, NanoArmor.TIER, false);

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    public NanoSuitItem(Properties properties) {
        super(SPEC, properties);
    }

    @SubscribeEvent
    static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) {
            return;
        }
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!(boots.getItem() instanceof NanoSuitItem)) {
            return;
        }
        int dealt = Mth.floor(
            (event.getDistance() + 1.0E-6 - entity.getAttributeValue(Attributes.SAFE_FALL_DISTANCE))
                * event.getDamageMultiplier()
                * entity.getAttributeValue(Attributes.FALL_DAMAGE_MULTIPLIER)
        );
        int cost = NanoArmor.fallCancelCost(dealt);
        if (cost < 0 || energy(boots) < cost) {
            return;
        }
        setEnergy(boots, energy(boots) - cost);
        event.setCanceled(true);
    }

    @SubscribeEvent
    static void onHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) {
            return;
        }
        var container = event.getContainer();
        // 26.2 tags minecraft:fall as bypassing armor. The JAR still absorbs that hit on the boots.
        boolean fall = container.getSource().is(DamageTypes.FALL);
        if (!fall && container.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }
        float damage = container.getNewDamage();
        if (damage <= 0) {
            return;
        }
        ItemStack[] stacks = new ItemStack[4];
        int[] charge = new int[4];
        boolean any = false;
        for (int i = 0; i < 4; i++) {
            stacks[i] = entity.getItemBySlot(SLOTS[i]);
            if (stacks[i].getItem() instanceof NanoSuitItem) {
                charge[i] = energy(stacks[i]);
                any = true;
            }
        }
        if (!any) {
            return;
        }
        NanoArmor.Hit hit = NanoArmor.absorb(damage, charge[0], charge[1], charge[2], charge[3], fall);
        int shown = 0;
        for (int i = 0; i < 4; i++) {
            int eu = hit.eu(i);
            if (eu > 0) {
                setEnergy(stacks[i], charge[i] - eu);
            }
            shown += NanoArmor.display(i, charge[i]);
        }
        container.setNewDamage(hit.remaining());
        // The bar reads ARMOR. Put that reduction back to every piece that is not this display.
        if (shown > 0) {
            int icons = shown;
            container.addModifier(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.ARMOR, (c, vanilla) -> {
                float realArmor = Math.max(0, entity.getArmorValue() - icons);
                float toughness = (float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
                float wanted = CombatRules.getDamageAfterAbsorb(entity, c.getNewDamage(), c.getSource(), realArmor, toughness);
                return Math.max(0f, c.getNewDamage() - wanted);
            });
        }
    }

    /** HUD icons and the vanilla "+N Armor" line. Absent below {@link NanoArmor#EU_PER_DAMAGE}. */
    @SubscribeEvent
    static void attributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof NanoSuitItem)) {
            return;
        }
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return;
        }
        int slot = switch (equippable.slot()) {
            case FEET -> 0;
            case LEGS -> 1;
            case CHEST -> 2;
            case HEAD -> 3;
            default -> -1;
        };
        int points = NanoArmor.display(slot, energy(stack));
        if (points <= 0) {
            return;
        }
        event.addModifier(
            Attributes.ARMOR,
            new AttributeModifier(
                Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "nano_armor_" + equippable.slot().getName()),
                points,
                AttributeModifier.Operation.ADD_VALUE
            ),
            EquipmentSlotGroup.bySlot(equippable.slot())
        );
    }

    /** Active nano saber. Removes the piece when the discharge leaves it empty. Quantum waits for D5. */
    public static void shred(LivingEntity target, EquipmentSlot slot) {
        ItemStack stack = target.getItemBySlot(slot);
        if (!(stack.getItem() instanceof NanoSuitItem)) {
            return;
        }
        int left = Math.max(0, energy(stack) - NanoArmor.SHRED_EU);
        if (left < 1) {
            target.setItemSlot(slot, ItemStack.EMPTY);
        } else {
            setEnergy(stack, left);
        }
    }
}
