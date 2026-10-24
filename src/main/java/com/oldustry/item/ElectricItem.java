package com.oldustry.item;

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import com.oldustry.compat.CuriosWearables;
import com.oldustry.energy.ElectricCharge;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class ElectricItem extends Item {
    private final ElectricCharge.Spec spec;

    public ElectricItem(ElectricCharge.Spec spec, Properties properties) {
        super(properties.stacksTo(1).component(OldustryDataComponents.ENERGY.get(), 0));
        this.spec = spec;
    }

    public ElectricCharge.Spec spec() {
        return spec;
    }

    public static int energy(ItemStack stack) {
        return stack.getOrDefault(OldustryDataComponents.ENERGY.get(), 0);
    }

    public static void setEnergy(ItemStack stack, int energy) {
        int next = Math.max(0, energy);
        // Skip no-op writes (LivingEntityEquipmentMixin ignores ENERGY diffs; still avoid churn).
        if (energy(stack) == next) {
            return;
        }
        stack.set(OldustryDataComponents.ENERGY.get(), next);
    }

    public int charge(ItemStack stack, int amount) {
        int energy = energy(stack);
        int moved = ElectricCharge.charge(energy, amount, spec);
        setEnergy(stack, energy + moved);
        return moved;
    }

    /** JAR {@code charge(..., ignoreTransferLimit=true)} — room only, no transfer cap. */
    public int chargeIgnoreTransfer(ItemStack stack, int amount) {
        return chargeAtTier(stack, amount, Integer.MAX_VALUE, true);
    }

    /** JAR {@code charge} tier gate: target tier must be &lt;= {@code sourceTier}. */
    public int chargeAtTier(ItemStack stack, int amount, int sourceTier, boolean ignoreTransfer) {
        int energy = energy(stack);
        int moved = ElectricCharge.chargeAtTier(energy, amount, spec, sourceTier, ignoreTransfer);
        if (moved > 0) {
            setEnergy(stack, energy + moved);
        }
        return moved;
    }

    /** JAR {@code ElectricItemManager.use}: full cost, no transfer cap. Does not touch armor. */
    public int spend(ItemStack stack, int amount) {
        int energy = energy(stack);
        int moved = ElectricCharge.spend(energy, amount);
        if (moved > 0) {
            setEnergy(stack, energy - moved);
        }
        return moved;
    }

    /**
     * JAR {@code ElectricItemManager.use}: fill this stack from worn packs, pay {@code amount} in full, fill again.
     * Idle stacks are not charged. {@code entity} null (miner drill, scanner in a machine) pays only what the stack holds.
     */
    public boolean pay(ItemStack stack, int amount, @Nullable LivingEntity entity) {
        if (amount <= 0) {
            return true;
        }
        chargeFromArmor(stack, entity);
        if (spend(stack, amount) != amount) {
            return false;
        }
        chargeFromArmor(stack, entity);
        return true;
    }

    /** True when {@link #pay} would succeed. Does not move EU. */
    public boolean canPay(ItemStack stack, int amount, @Nullable LivingEntity entity) {
        if (amount <= 0) {
            return true;
        }
        int have = energy(stack);
        if (have >= amount || entity == null) {
            return have >= amount;
        }
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return false;
        }
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }
            have += offerFrom(stack, entity.getItemBySlot(slot), have);
            if (have >= amount) {
                return true;
            }
        }
        if (entity instanceof Player player) {
            int running = have;
            int[] boxed = { running };
            CuriosWearables.forEachPack(player, armor -> {
                boxed[0] += offerFrom(stack, armor, boxed[0]);
            });
            have = boxed[0];
        }
        return have >= amount;
    }

    /** JAR {@code chargeFromArmor}: worn packs (then Curios back) dump into this stack. Not a tick. */
    public void chargeFromArmor(ItemStack stack, @Nullable LivingEntity entity) {
        if (entity == null || (entity instanceof Player player && player.getAbilities().instabuild)) {
            return;
        }
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                pullFromArmor(stack, entity.getItemBySlot(slot));
            }
        }
        if (entity instanceof Player player) {
            CuriosWearables.forEachPack(player, armor -> pullFromArmor(stack, armor));
        }
    }

    /** One worn pack → this stack. Offhand is a target when that hand calls {@link #pay}, not a second source. */
    public void pullFromArmor(ItemStack tool, ItemStack armor) {
        if (armor == tool || !(armor.getItem() instanceof ElectricArmorItem pack)) {
            return;
        }
        int want = ElectricCharge.chargeFromArmor(energy(tool), spec(), energy(armor), pack.spec());
        if (want <= 0) {
            return;
        }
        int moved = pack.dischargeIgnoreTransfer(armor, want, Integer.MAX_VALUE);
        if (moved > 0) {
            chargeIgnoreTransfer(tool, moved);
        }
    }

    private int offerFrom(ItemStack tool, ItemStack armor, int toolEnergy) {
        if (armor == tool || !(armor.getItem() instanceof ElectricArmorItem pack)) {
            return 0;
        }
        return ElectricCharge.chargeFromArmor(toolEnergy, spec(), energy(armor), pack.spec());
    }

    public int discharge(ItemStack stack, int amount, int sinkTier) {
        int energy = energy(stack);
        int moved = ElectricCharge.discharge(energy, amount, spec, sinkTier);
        setEnergy(stack, energy - moved);
        return moved;
    }

    /** JAR {@code discharge(..., ignoreTransferLimit=true)}. */
    public int dischargeIgnoreTransfer(ItemStack stack, int amount, int sinkTier) {
        int energy = energy(stack);
        int moved = ElectricCharge.dischargeIgnoreTransfer(energy, amount, spec, sinkTier);
        setEnergy(stack, energy - moved);
        return moved;
    }

    /**
     * JAR {@code instanceof ItemBattery} — RE / energy crystal / lapotron cells.
     * Subclasses (tools, packs, scanners) are not cells.
     */
    public boolean isBatteryCell() {
        Class<?> c = getClass();
        return c == ElectricItem.class || c == ReBatteryItem.class;
    }

    /**
     * Solar/static and a pack dump on use rewrite ENERGY. Default NeoForge path treats any
     * component change on non-damageable items as a new tool → mining progress resets.
     */
    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        if (ChargeComponents.onlyChargeDiffers(oldStack, newStack)) {
            return false;
        }
        return super.shouldCauseBlockBreakReset(oldStack, newStack);
    }

    /** Same ENERGY churn was bobbing the hand like a re-equip. */
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!slotChanged && ChargeComponents.onlyChargeDiffers(oldStack, newStack)) {
            return false;
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * energy(stack) / (float) spec.max());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fill = energy(stack) / (float) spec.max();
        return fill < 0.15f ? 0xFF5555 : (fill < 0.5f ? 0xFFAA00 : 0x55FF55);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("info.oldustry.eu.stored", energy(stack), spec.max()).withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("info.oldustry.eu.transfer", spec.transfer()).withStyle(ChatFormatting.DARK_GRAY));
        builder.accept(Component.translatable("info.oldustry.eu.tier", spec.tier()).withStyle(ChatFormatting.DARK_GRAY));
    }
}
