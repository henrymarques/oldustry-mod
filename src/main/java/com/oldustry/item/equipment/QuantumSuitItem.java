package com.oldustry.item.equipment;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.oldustry.Oldustry;
import com.oldustry.OldustryConfig;
import com.oldustry.energy.ElectricCharge;
import com.oldustry.item.ElectricItem;
import com.oldustry.network.KeysPayload;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * JAR {@code ItemArmorQuantumSuit}. 1000000 / 1000 / T3. No vanilla armor points.
 * Helmet air and tin cans, chest puts the fire out, legs run, boots jump and cancel falls.
 */
@EventBusSubscriber(modid = Oldustry.MOD_ID)
public final class QuantumSuitItem extends ElectricItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(
        QuantumArmor.MAX, QuantumArmor.TRANSFER, QuantumArmor.TIER, false
    );
    /** 1.5.2 {@code armorType}. */
    public static final int HELMET = 0;
    public static final int CHEST = 1;
    public static final int LEGS = 2;
    public static final int BOOTS = 3;

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    private static final Map<UUID, Integer> LEG_TICKER = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> WAS_ON_GROUND = new ConcurrentHashMap<>();
    /** Per player object. Client and server copies of the same person must not share the charge. */
    private static final Map<Player, Float> JUMP = new IdentityHashMap<>();

    private final int armorType;

    public QuantumSuitItem(Properties properties, int armorType) {
        super(SPEC, properties);
        this.armorType = armorType;
    }

    public static boolean wearing(LivingEntity entity) {
        for (EquipmentSlot slot : SLOTS) {
            if (entity.getItemBySlot(slot).getItem() instanceof QuantumSuitItem) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player player)) {
            return;
        }
        switch (armorType) {
            case HELMET -> {
                if (slot == EquipmentSlot.HEAD) {
                    helmet(level, player, stack);
                }
            }
            case CHEST -> {
                if (slot == EquipmentSlot.CHEST) {
                    player.clearFire();
                }
            }
            case LEGS -> {
                if (slot == EquipmentSlot.LEGS) {
                    legs(player, stack);
                }
            }
            case BOOTS -> {
                if (slot == EquipmentSlot.FEET) {
                    boots(player, stack);
                }
            }
            default -> {
            }
        }
    }

    private static void helmet(ServerLevel level, Player player, ItemStack stack) {
        int air = player.getAirSupply();
        if (energy(stack) >= QuantumArmor.AIR_EU && air < 100) {
            player.setAirSupply(QuantumArmor.refilledAir(air));
            take(stack, QuantumArmor.AIR_EU);
        } else if (air <= 0 && player instanceof ServerPlayer server) {
            forgot(server);
        }
        FoodData food = player.getFoodData();
        if (energy(stack) >= QuantumArmor.FOOD_EU && food.needsFood()) {
            if (eatCan(level, player)) {
                take(stack, QuantumArmor.FOOD_EU);
            }
        } else if (food.getFoodLevel() <= 0 && player instanceof ServerPlayer server) {
            forgot(server);
        }
        for (MobEffectInstance effect : java.util.List.copyOf(player.getActiveEffects())) {
            int base = cleanseBase(effect.getEffect());
            if (base == 0) {
                continue;
            }
            int cost = QuantumArmor.cleanseCost(base, effect.getAmplifier());
            if (take(stack, cost)) {
                player.removeEffect(effect.getEffect());
            }
        }
    }

    private static int cleanseBase(Holder<MobEffect> effect) {
        return QuantumArmor.cleanseBase(effect.value().getCategory() == MobEffectCategory.HARMFUL, effect.is(MobEffects.WITHER));
    }

    private static boolean eatCan(ServerLevel level, Player player) {
        var items = player.getInventory().getNonEquipmentItems();
        for (int i = 0; i < items.size(); i++) {
            ItemStack can = items.get(i);
            if (!can.is(OldustryItems.FILLED_TIN_CAN.get())) {
                continue;
            }
            items.set(i, can.getItem().finishUsingItem(can, level, player));
            return true;
        }
        return false;
    }

    private static void forgot(ServerPlayer player) {
        var holder = player.level().getServer().getAdvancements().get(
            Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "starve_with_q_helmet"));
        if (holder != null) {
            player.getAdvancements().award(holder, "starved");
        }
    }

    private static void legs(Player player, ItemStack stack) {
        if (!boost(player, stack, KeysPayload.forward(player), KeysPayload.jump(player), KeysPayload.boost(player))) {
            return;
        }
        UUID id = player.getUUID();
        int ticker = LEG_TICKER.getOrDefault(id, 0);
        if (QuantumArmor.legPays(ticker)) {
            take(stack, QuantumArmor.LEG_EU);
        }
        LEG_TICKER.put(id, QuantumArmor.afterLegTick(ticker));
    }

    /**
     * JAR {@code moveRelative(0, 0, 1, speed)} on the side that simulates the walk.
     * EU stays in {@link #legs}. The client calls this before travel; a server-only push is overwritten by the position packet.
     */
    /** 26.2 refuses sprint in shallow water. The JAR legs sprinted whenever {@code isInWater}. */
    public static boolean allowsShallowSprint(Player player) {
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        return legs.getItem() instanceof QuantumSuitItem && energy(legs) >= QuantumArmor.LEG_EU;
    }

    public static boolean boost(Player player, ItemStack stack, boolean forward, boolean jump, boolean boostKey) {
        boolean trigger = OldustryConfig.QUANTUM_SPRINT.get() ? player.isSprinting() : boostKey;
        if (energy(stack) < QuantumArmor.LEG_EU || !(player.onGround() || player.isInWater()) || !forward || !trigger) {
            return false;
        }
        boolean water = player.isInWater();
        if (water && jump) {
            player.setDeltaMovement(player.getDeltaMovement().add(0, 0.1, 0));
        }
        player.moveRelative(QuantumArmor.legSpeed(water), new Vec3(0, 0, 1));
        return true;
    }

    private static void boots(Player player, ItemStack stack) {
        UUID id = player.getUUID();
        boolean was = WAS_ON_GROUND.getOrDefault(id, true);
        boolean on = player.onGround();
        if (was && !on && KeysPayload.jump(player) && KeysPayload.boost(player)) {
            take(stack, QuantumArmor.JUMP_EU);
        }
        WAS_ON_GROUND.put(id, on);
        hop(player, stack, KeysPayload.jump(player), KeysPayload.boost(player), !on);
    }

    @SubscribeEvent
    static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof QuantumSuitItem) {
            hop(player, boots, KeysPayload.jump(player), KeysPayload.boost(player), true);
        }
    }

    /**
     * JAR client half of the boots. {@code jumpFromGround} already set the vanilla hop when {@code push} is the launch.
     * EU stays in {@link #boots}.
     */
    public static void hop(Player player, ItemStack stack, boolean jump, boolean boostKey, boolean push) {
        float charge = JUMP.getOrDefault(player, 0f);
        if (player.onGround() && energy(stack) >= QuantumArmor.JUMP_EU) {
            charge = 1f;
        }
        Vec3 motion = player.getDeltaMovement();
        if (motion.y >= 0 && charge > 0f && !player.isInWater()) {
            if (push && jump && boostKey) {
                double xz = QuantumArmor.firstHop(charge) ? QuantumArmor.JUMP_XZ : 1;
                player.setDeltaMovement(motion.x * xz, motion.y + QuantumArmor.lift(charge), motion.z * xz);
                charge = QuantumArmor.nextJump(charge);
            } else if (charge < 1f) {
                charge = 0f;
            }
        }
        if (charge == 0f) {
            JUMP.remove(player);
        } else {
            JUMP.put(player, charge);
        }
    }

    private static boolean take(ItemStack stack, int cost) {
        return stack.getItem() instanceof ElectricItem item && item.spend(stack, cost) == cost;
    }

    @SubscribeEvent
    static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) {
            return;
        }
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!(boots.getItem() instanceof QuantumSuitItem)) {
            return;
        }
        int cost = QuantumArmor.fallCancelEu(event.getDistance());
        if (energy(boots) < cost) {
            return;
        }
        if (cost > 0) {
            setEnergy(boots, energy(boots) - cost);
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    static void onHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) {
            return;
        }
        if (OldustryHazmatItem.wearing(entity) || !wearing(entity)) {
            return;
        }
        var container = event.getContainer();
        boolean fall = container.getSource().is(DamageTypes.FALL);
        if (!fall && container.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }
        if (container.getNewDamage() <= 0) {
            return;
        }
        // JAR damageArmor runs inside damageEntity, after hurtResistantTime. This event is earlier:
        // lava calls hurt every tick, and the cooldown throws away 9 of each 10. Paying here billed all of them.
        // setNewDamage would also store the post-armor amount in lastHurt, so the cooldown never closed.
        // The armor reduction runs from actuallyHurt, which is the call the cooldown skips.
        container.addModifier(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.ARMOR, (c, vanilla) -> {
            float base = c.getNewDamage();
            if (base <= 0f) {
                return vanilla;
            }
            ItemStack[] stacks = new ItemStack[4];
            int[] charge = new int[4];
            double[] ratio = new double[4];
            int[] euPer = new int[4];
            int shown = 0;
            for (int i = 0; i < 4; i++) {
                stacks[i] = entity.getItemBySlot(SLOTS[i]);
                if (stacks[i].getItem() instanceof QuantumSuitItem) {
                    charge[i] = energy(stacks[i]);
                    euPer[i] = QuantumArmor.EU_PER_DAMAGE;
                    ratio[i] = fall ? (i == 0 ? 1 : 0) : QuantumArmor.ratio(i);
                    shown += QuantumArmor.display(i, charge[i]);
                } else if (stacks[i].getItem() instanceof NanoSuitItem) {
                    charge[i] = energy(stacks[i]);
                    euPer[i] = NanoArmor.EU_PER_DAMAGE;
                    ratio[i] = fall ? (i == 0 ? NanoArmor.bootsFallRatio(base) : 0) : NanoArmor.typedRatio(3 - i);
                    shown += NanoArmor.display(i, charge[i]);
                }
            }
            QuantumArmor.Hit hit = QuantumArmor.share(base, charge, ratio, euPer);
            int paid = 0;
            for (int i = 0; i < 4; i++) {
                int eu = hit.eu(i);
                paid += eu;
                if (eu > 0) {
                    setEnergy(stacks[i], charge[i] - eu);
                }
            }
            if (paid == 0 && shown == 0) {
                return vanilla;
            }
            float armor = shown > 0 ? Math.max(0, entity.getArmorValue() - shown) : entity.getArmorValue();
            float toughness = (float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
            float wanted = CombatRules.getDamageAfterAbsorb(entity, hit.remaining(), c.getSource(), armor, toughness);
            return Math.max(0f, base - wanted);
        });
    }

    @SubscribeEvent
    static void attributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof QuantumSuitItem)) {
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
        int points = QuantumArmor.display(slot, energy(stack));
        if (points <= 0) {
            return;
        }
        event.addModifier(
            Attributes.ARMOR,
            new AttributeModifier(
                Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "quantum_armor_" + equippable.slot().getName()),
                points,
                AttributeModifier.Operation.ADD_VALUE
            ),
            EquipmentSlotGroup.bySlot(equippable.slot())
        );
    }

    /** Active nano saber. Removes the piece when the discharge leaves it empty. */
    public static void shred(LivingEntity target, EquipmentSlot slot) {
        ItemStack stack = target.getItemBySlot(slot);
        if (!(stack.getItem() instanceof QuantumSuitItem)) {
            return;
        }
        int left = Math.max(0, energy(stack) - QuantumArmor.SHRED);
        if (left < 1) {
            target.setItemSlot(slot, ItemStack.EMPTY);
        } else {
            setEnergy(stack, left);
        }
    }
}
