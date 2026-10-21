package com.oldustry.compat;

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.oldustry.Oldustry;
import com.oldustry.item.ElectricArmorItem;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.FuelJetpackItem;
import com.oldustry.item.SolarHelmetItem;
import com.oldustry.item.StaticBootsItem;
import com.oldustry.network.KeysPayload;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Soft Curios: packs/solar/static keep working in accessory slots.
 * ponytail: IItemHandler deprecated in 26.2; Curios still exposes {@code curios:item_handler}.
 */
@SuppressWarnings("removal")
@EventBusSubscriber(modid = Oldustry.MOD_ID)
public final class CuriosWearables {
    private static final @NonNull EntityCapability<@NonNull IItemHandler, @Nullable Void> CURIOS_ITEMS = EntityCapability.createVoid(
        Identifier.fromNamespaceAndPath("curios", "item_handler"),
        IItemHandler.class
    );

    private CuriosWearables() {}

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !ModList.get().isLoaded("curios")) {
            return;
        }
        IItemHandler curios = player.getCapability(CURIOS_ITEMS);
        if (curios == null) {
            return;
        }
        for (int i = 0; i < curios.getSlots(); i++) {
            ItemStack stack = curios.getStackInSlot(i);
            if (stack.getItem() instanceof SolarHelmetItem && player.level() instanceof ServerLevel server) {
                SolarHelmetItem.tickCharge(server, player);
            } else if (stack.getItem() instanceof StaticBootsItem) {
                StaticBootsItem.chargeFromWalk(player, stack);
            } else if (stack.getItem() instanceof FuelJetpackItem
                && !(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof FuelJetpackItem)) {
                FuelJetpackItem.tickFlight(player, stack, KeysPayload.jump(player), KeysPayload.forward(player), true);
            }
        }
    }

    /** Armor feet, then any Curios stack. Magnetizer accepts static boots in the feet accessory. */
    public static boolean wearingFeet(Player player, TagKey<Item> tag) {
        if (player.getItemBySlot(EquipmentSlot.FEET).is(tag)) {
            return true;
        }
        if (!ModList.get().isLoaded("curios")) {
            return false;
        }
        IItemHandler curios = player.getCapability(CURIOS_ITEMS);
        if (curios == null) {
            return false;
        }
        for (int i = 0; i < curios.getSlots(); i++) {
            if (curios.getStackInSlot(i).is(tag)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack chestOrCurios(Player player, Item item) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(item)) {
            return chest;
        }
        if (!ModList.get().isLoaded("curios")) {
            return ItemStack.EMPTY;
        }
        IItemHandler curios = player.getCapability(CURIOS_ITEMS);
        if (curios != null) {
            for (int i = 0; i < curios.getSlots(); i++) {
                ItemStack stack = curios.getStackInSlot(i);
                if (stack.is(item)) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    /** Worn Curios packs. Tools pull from these in {@code ElectricItem#pay}; this is not a tick. */
    public static void forEachPack(Player player, Consumer<ItemStack> packs) {
        if (!ModList.get().isLoaded("curios")) {
            return;
        }
        IItemHandler curios = player.getCapability(CURIOS_ITEMS);
        if (curios == null) {
            return;
        }
        for (int i = 0; i < curios.getSlots(); i++) {
            ItemStack stack = curios.getStackInSlot(i);
            if (stack.getItem() instanceof ElectricArmorItem) {
                packs.accept(stack);
            }
        }
    }

    /** Chest pack first, then Curios electric items (batpack on back, etc.). */
    public static void chargeChestPack(Player player, int eu) {
        chargePacks(player, eu, false);
    }

    /** Solar: JAR ignoreTransfer into chest, then Curios back packs. */
    public static void chargePacksIgnoreTransfer(Player player, int eu) {
        chargePacks(player, eu, true);
    }

    private static void chargePacks(Player player, int eu, boolean ignoreTransfer) {
        // Same as ElectricArmorItem: skip ENERGY writes in creative (sync duplicates).
        if (eu <= 0 || player.getAbilities().instabuild) {
            return;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ElectricItem electric) {
            int moved = ignoreTransfer ? electric.chargeIgnoreTransfer(chest, eu) : electric.charge(chest, eu);
            if (moved >= eu) {
                return;
            }
            eu -= moved;
        }
        if (!ModList.get().isLoaded("curios") || eu <= 0) {
            return;
        }
        IItemHandler curios = player.getCapability(CURIOS_ITEMS);
        if (curios == null) {
            return;
        }
        for (int i = 0; i < curios.getSlots() && eu > 0; i++) {
            ItemStack stack = curios.getStackInSlot(i);
            if (stack.getItem() instanceof ElectricItem electric) {
                eu -= ignoreTransfer ? electric.chargeIgnoreTransfer(stack, eu) : electric.charge(stack, eu);
            }
        }
    }
}
