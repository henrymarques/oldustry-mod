package com.oldustry.item;

import java.util.List;
import java.util.function.Consumer;

import com.oldustry.block.DynamiteBlock;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModDataComponents;
import com.oldustry.registry.ModSounds;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Dynamite-O-Mote. Air detonates the current dimension. A stick links or unlinks. */
public final class RemoteItem extends Item {
    private static final int TOOLTIP_LINES = 8;

    public RemoteItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.DYNAMITE.get())) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        RemoteLink here = linkAt(level, pos);
        List<RemoteLink> links = links(stack);
        int index = RemoteLinks.indexOf(links, here);
        if (!state.getValue(DynamiteBlock.LINKED)) {
            store(stack, RemoteLinks.add(links, here));
            level.setBlock(pos, state.setValue(DynamiteBlock.LINKED, true), 3);
            return InteractionResult.SUCCESS;
        }
        if (index < 0) {
            player.sendSystemMessage(Component.translatable("info.oldustry.remote.not_linked"));
            return InteractionResult.SUCCESS;
        }
        store(stack, RemoteLinks.removeAt(links, index));
        level.setBlock(pos, state.setValue(DynamiteBlock.LINKED, false), 3);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return detonate(level, player, player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        List<RemoteLink> links = links(stack);
        if (links.isEmpty()) {
            return;
        }
        builder.accept(Component.translatable("info.oldustry.remote.linked", links.size()));
        int shown = Math.min(links.size(), TOOLTIP_LINES);
        for (int i = 0; i < shown; i++) {
            RemoteLink link = links.get(i);
            builder.accept(Component.translatable(
                "info.oldustry.remote.entry",
                Component.translatable(Identifier.parse(link.dimension()).toLanguageKey("dimension")),
                link.x(), link.y(), link.z()
            ).withStyle(ChatFormatting.GRAY));
        }
        if (links.size() > TOOLTIP_LINES) {
            builder.accept(Component.translatable("info.oldustry.remote.more", links.size() - TOOLTIP_LINES).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static InteractionResult detonate(Level level, Player player, ItemStack stack) {
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            ModSounds.play(level, null, player.blockPosition(), ModSounds.DYNAMITE_REMOTE.get(), SoundSource.PLAYERS, 1.2F, 1.0F);
            for (RemoteLink link : links(stack)) {
                if (!link.dimension().equals(level.dimension().identifier().toString())) {
                    continue;
                }
                BlockPos pos = new BlockPos(link.x(), link.y(), link.z());
                BlockState state = level.getBlockState(pos);
                if (state.is(ModBlocks.DYNAMITE.get()) && state.getValue(DynamiteBlock.LINKED)) {
                    DynamiteBlock.prime(level, pos, player);
                }
            }
            store(stack, RemoteLinks.exceptDimension(links(stack), level.dimension().identifier().toString()));
        }
        return InteractionResult.SUCCESS;
    }

    private static RemoteLink linkAt(Level level, BlockPos pos) {
        return new RemoteLink(level.dimension().identifier().toString(), pos.getX(), pos.getY(), pos.getZ());
    }

    private static List<RemoteLink> links(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.REMOTE_LINKS.get(), List.of());
    }

    private static void store(ItemStack stack, List<RemoteLink> links) {
        if (links.isEmpty()) {
            stack.remove(ModDataComponents.REMOTE_LINKS.get());
        } else {
            stack.set(ModDataComponents.REMOTE_LINKS.get(), links);
        }
    }
}
