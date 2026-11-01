package com.oldustry.item.tool;

import java.util.function.Consumer;

import com.oldustry.block.CableBlock;
import com.oldustry.block.CableBlockEntity;
import com.oldustry.energy.ElectricCharge;
import com.oldustry.item.ElectricItem;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * IC2 1.117 {@code ItemObscurator}. Sneak copies a full-cube face from any mod. A normal click paints
 * that face onto hardened foam ({@code foam == 2}) on a cable. No CF wall in this port.
 */
public class ObscuratorItem extends ElectricItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(
        ObscuratorStats.CAPACITY, ObscuratorStats.TRANSFER, ObscuratorStats.TIER, false
    );

    public ObscuratorItem(Properties properties) {
        super(SPEC, properties);
    }

    public static ObscuratorSample sample(ItemStack stack) {
        return stack.get(OldustryDataComponents.OBSCURATOR.get());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            return scan(stack, context, player);
        }
        return print(stack, context, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        ObscuratorSample copied = sample(stack);
        if (copied != null) {
            builder.accept(Component.translatable("info.oldustry.obscurator.sample", copied.state().getBlock().getName())
                .withStyle(ChatFormatting.AQUA));
        }
        builder.accept(Component.translatable("info.oldustry.obscurator.scan").withStyle(ChatFormatting.DARK_GRAY));
        builder.accept(Component.translatable("info.oldustry.obscurator.print").withStyle(ChatFormatting.DARK_GRAY));
    }

    private InteractionResult scan(ItemStack stack, UseOnContext context, Player player) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (!ObscuratorStats.canScan(
            state.isAir(),
            state.isCollisionShapeFullBlock(context.getLevel(), context.getClickedPos()),
            state.getRenderShape() == RenderShape.MODEL,
            state.getBlock() instanceof CableBlock
        )) {
            return InteractionResult.PASS;
        }
        if (!holds(stack, ObscuratorStats.SCAN_COST)) {
            return InteractionResult.PASS;
        }
        ObscuratorSample next = new ObscuratorSample(state, context.getClickedFace());
        ObscuratorSample old = sample(stack);
        if (old != null && old.sameState(next) && old.sameFace(next)) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide() && !pay(stack, ObscuratorStats.SCAN_COST, player)) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide()) {
            stack.set(OldustryDataComponents.OBSCURATOR.get(), next);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult print(ItemStack stack, UseOnContext context, Player player) {
        ObscuratorSample copied = sample(stack);
        if (copied == null || !holds(stack, ObscuratorStats.PRINT_COST)) {
            return InteractionResult.PASS;
        }
        if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CableBlockEntity cable)) {
            return InteractionResult.PASS;
        }
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        int foam = state.hasProperty(CableBlock.FOAM) ? state.getValue(CableBlock.FOAM) : 0;
        if (!ObscuratorStats.canPrint(foam)) {
            return InteractionResult.PASS;
        }
        ObscuratorSample painted = cable.face(context.getClickedFace());
        if (!ObscuratorStats.paintChanges(
            painted != null,
            copied.sameBlock(painted),
            copied.sameState(painted),
            copied.sameFace(painted)
        )) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide()) {
            if (!cable.paint(context.getClickedFace(), copied) || !pay(stack, ObscuratorStats.PRINT_COST, player)) {
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    /** JAR {@code canUse}: pack EU does not count. {@code pay} still refills a tool that already holds the cost. */
    private static boolean holds(ItemStack stack, int cost) {
        return ObscuratorStats.afford(ElectricItem.energy(stack), cost);
    }
}
