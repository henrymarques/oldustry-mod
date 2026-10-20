package com.oldustry.item;

import java.util.ArrayList;
import java.util.List;

import com.oldustry.registry.ModItems;

import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class ScrapBoxItem extends Item {
    private record Drop(ItemStack stack, float weight) {}

    private static final List<Drop> DROPS = new ArrayList<>();

    public ScrapBoxItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            ItemStack drop = roll(level.getRandom());
            if (!drop.isEmpty() && !player.addItem(drop)) {
                player.drop(drop, false);
            }
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    public static ItemStack roll(RandomSource random) {
        ensure();
        float total = 0;
        for (Drop drop : DROPS) {
            total += drop.weight;
        }
        float pick = random.nextFloat() * total;
        for (Drop drop : DROPS) {
            pick -= drop.weight;
            if (pick <= 0) {
                return drop.stack.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    private static void ensure() {
        if (!DROPS.isEmpty()) {
            return;
        }
        add(new ItemStack(Blocks.DIRT), 5f);
        add(new ItemStack(Items.STICK), 4f);
        add(new ItemStack(Blocks.GRASS_BLOCK), 3f);
        add(new ItemStack(Blocks.GRAVEL), 3f);
        add(new ItemStack(Blocks.SNOW_BLOCK), 2f);
        add(new ItemStack(Items.ROTTEN_FLESH), 2f);
        add(new ItemStack(Items.APPLE), 1.5f);
        add(new ItemStack(Items.BREAD), 1.5f);
        add(new ItemStack(ModItems.FILLED_TIN_CAN.get()), 1.5f);
        add(new ItemStack(Items.WOODEN_HOE), 1f);
        add(new ItemStack(Items.WOODEN_SHOVEL), 1f);
        add(new ItemStack(Items.WOODEN_SWORD), 1f);
        add(new ItemStack(Blocks.SOUL_SAND), 1f);
        add(new ItemStack(Items.OAK_SIGN), 1f);
        add(new ItemStack(Items.LEATHER), 1f);
        add(new ItemStack(Items.FEATHER), 1f);
        add(new ItemStack(Items.BONE), 1f);
        add(new ItemStack(Items.PORKCHOP), 0.9f);
        add(new ItemStack(Items.BEEF), 0.9f);
        add(new ItemStack(Blocks.NETHERRACK), 0.9f);
        add(new ItemStack(Items.CHICKEN), 0.9f);
        add(new ItemStack(Items.SLIME_BALL), 0.9f);
        add(new ItemStack(Items.REDSTONE), 0.9f);
        add(new ItemStack(ModItems.RUBBER.get()), 0.8f);
        add(new ItemStack(Items.GLOWSTONE_DUST), 0.8f);
        add(new ItemStack(ModItems.COAL_DUST.get()), 0.8f);
        add(new ItemStack(ModItems.COPPER_DUST.get()), 0.8f);
        add(new ItemStack(ModItems.TIN_DUST.get()), 0.8f);
        add(new ItemStack(ModItems.PLANTBALL.get()), 0.7f);
        add(new ItemStack(ModItems.SU_BATTERY.get()), 0.7f);
        add(new ItemStack(ModItems.IRON_DUST.get()), 0.7f);
        add(new ItemStack(ModItems.GOLD_DUST.get()), 0.7f);
        add(new ItemStack(Items.CAKE), 0.6f);
        add(new ItemStack(Blocks.IRON_ORE), 0.5f);
        add(new ItemStack(Items.GOLDEN_HELMET), 0.5f);
        add(new ItemStack(Blocks.GOLD_ORE), 0.5f);
        add(new ItemStack(Items.GHAST_TEAR), 0.5f);
        add(new ItemStack(Items.DIAMOND), 0.1f);
        add(new ItemStack(Items.EMERALD), 0.05f);
        add(new ItemStack(ModItems.TIN_ORE.get()), 0.7f);
        add(new ItemStack(Items.COPPER_ORE), 0.7f);
    }

    private static void add(ItemStack stack, float weight) {
        DROPS.add(new Drop(stack, weight));
    }
}
