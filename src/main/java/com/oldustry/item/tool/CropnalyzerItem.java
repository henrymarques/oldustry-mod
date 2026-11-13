package com.oldustry.item.tool;

import com.oldustry.menu.CropnalyzerMenu;
import com.oldustry.registry.OldustryDataComponents;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** JAR {@code ItemCropnalyzer} + {@code HandHeldCropnalyzer}. Inventory lives in {@code BOX}. */
public class CropnalyzerItem extends Item {
    public static final int SLOTS = 3;

    public CropnalyzerItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.openMenu(new Provider(hand, stack.getHoverName()));
        }
        return InteractionResult.SUCCESS;
    }

    public static List<ItemStack> contents(ItemStack analyzer) {
        return analyzer.getOrDefault(OldustryDataComponents.BOX.get(), List.of());
    }

    public static void setContents(ItemStack analyzer, List<ItemStack> items) {
        analyzer.set(OldustryDataComponents.BOX.get(), List.copyOf(items));
    }

    /** EU to raise scan from {@code level} to {@code level+1}. JAR {@code energyForLevel}. */
    public static int energyForLevel(int level) {
        return switch (level) {
            case 1 -> 90;
            case 2 -> 900;
            case 3 -> 9000;
            default -> 10;
        };
    }

    private record Provider(InteractionHand hand, Component title) implements MenuProvider {
        @Override
        public Component getDisplayName() {
            return title;
        }

        @Override
        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            return new CropnalyzerMenu(id, inventory, hand);
        }

        @Override
        public void writeClientSideData(AbstractContainerMenu menu, net.minecraft.network.RegistryFriendlyByteBuf buffer) {
            buffer.writeBoolean(hand == InteractionHand.OFF_HAND);
        }
    }
}
