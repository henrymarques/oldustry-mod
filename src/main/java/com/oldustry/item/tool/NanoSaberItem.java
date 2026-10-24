package com.oldustry.item.tool;

import com.oldustry.item.ElectricItem;
import com.oldustry.item.equipment.NanoSuitItem;

import org.jspecify.annotations.Nullable;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustrySounds;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * JAR {@code ItemNanoSaber}. Idle sword damage is 4; active adds {@link NanoSaber#ACTIVE_BONUS}
 * so the hit is 20. Right-click toggles it. Hit drain is {@code 20 × 8}.
 */
@EventBusSubscriber(modid = com.oldustry.Oldustry.MOD_ID)
public class NanoSaberItem extends ElectricItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(NanoSaber.MAX, NanoSaber.TRANSFER, NanoSaber.TIER, false);
    /** JAR {@code ItemNanoSaber.ticker}, once per server tick before inventories run. */
    private static int ticker;

    @SubscribeEvent
    static void tick(ServerTickEvent.Pre event) {
        ticker++;
    }

    public NanoSaberItem(Properties properties) {
        super(SPEC, properties);
    }

    public static boolean active(ItemStack stack) {
        return stack.has(OldustryDataComponents.SABER_ACTIVE.get());
    }

    public static void setActive(ItemStack stack, boolean on, @Nullable Player holder) {
        if (on) {
            stack.set(OldustryDataComponents.SABER_ACTIVE.get(), true);
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributes(3f + NanoSaber.ACTIVE_BONUS));
        } else {
            stack.remove(OldustryDataComponents.SABER_ACTIVE.get());
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributes(3f));
        }
        if (holder != null) {
            syncHeldDamage(holder, stack);
        }
    }

    /**
     * The held attack modifier is applied when the stack changes. Turning the blade off rewrites that
     * component in place, and the player keeps the active amount. Write it here.
     */
    private static void syncHeldDamage(Player player, ItemStack stack) {
        if (player.getMainHandItem() != stack) {
            return;
        }
        AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack == null) {
            return;
        }
        double want = active(stack) ? 3.0 + NanoSaber.ACTIVE_BONUS : 3.0;
        for (AttributeModifier modifier : attack.getModifiers()) {
            if (modifier.is(Item.BASE_ATTACK_DAMAGE_ID) && modifier.amount() == want) {
                return;
            }
        }
        attack.addOrUpdateTransientModifier(new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, want, AttributeModifier.Operation.ADD_VALUE));
    }

    /** A swing that cannot pay {@link NanoSaber#HIT_EU} is the idle hit. Checked before damage is rolled. */
    @SubscribeEvent
    static void beforeHit(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (!active(stack) || !(stack.getItem() instanceof ElectricItem saber)) {
            return;
        }
        if (!saber.canPay(stack, NanoSaber.HIT_EU, player)) {
            setActive(stack, false, player);
        }
    }

    /** Idle sword is +3 (hit 4). Active replaces it with +19 (hit 20). */
    private static ItemAttributeModifiers attributes(float damage) {
        return ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .build();
    }

    /** JAR {@code onItemRightClick}. Needs {@link NanoSaber#ARM_EU} available; the toggle itself is free. */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            toggle(player, player.getItemInHand(hand));
        }
        return InteractionResult.CONSUME;
    }

    public static void toggle(Player player, ItemStack stack) {
        if (active(stack)) {
            setActive(stack, false, player);
            return;
        }
        if (!(stack.getItem() instanceof ElectricItem saber) || !saber.canPay(stack, NanoSaber.ARM_EU, player)) {
            return;
        }
        setActive(stack, true, player);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), OldustrySounds.NANO_SABER_POWER.get(), SoundSource.PLAYERS, 1f, 1f);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.level().isClientSide() || !active(stack)) {
            return;
        }
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }
            if (!(target.getItemBySlot(slot).getItem() instanceof NanoSuitItem)) {
                continue;
            }
            NanoSuitItem.shred(target, slot);
            if (!pay(stack, NanoSaber.SHRED_EU, attacker)) {
                setActive(stack, false, attacker instanceof Player player ? player : null);
            }
        }
        if (!pay(stack, NanoSaber.HIT_EU, attacker)) {
            setActive(stack, false, attacker instanceof Player player ? player : null);
        }
        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), OldustrySounds.NANO_SABER_SWING.get(), SoundSource.PLAYERS, 1f, 1f);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide() && active(stack) && !pay(stack, NanoSaber.BREAK_EU, entity)) {
            setActive(stack, false, entity instanceof Player player ? player : null);
        }
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return active(stack) ? 4f : 1f;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player player)) {
            return;
        }
        syncHeldDamage(player, stack);
        if (!active(stack)) {
            return;
        }
        int cost = NanoSaber.upkeep(hotbar(player, stack), ticker);
        if (cost <= 0) {
            return;
        }
        if (!pay(stack, cost, player)) {
            setActive(stack, false, player);
        }
    }

    private static boolean hotbar(Player player, ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i) == stack) {
                return true;
            }
        }
        return false;
    }
}
