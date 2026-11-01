package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.machine.OldustryFrequency;
import com.oldustry.machine.OldustryTeleporterBlockEntity;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** JAR {@code ItemFrequencyTransmitter}. The damage flag is {@code frequency_latched}. */
public final class FrequencyTransmitterItem extends Item {
    public FrequencyTransmitterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        OldustryFrequency.Link before = read(stack);
        OldustryFrequency.Link after = OldustryFrequency.air(before);
        if (after.equals(before)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            write(stack, after);
            if (before.set() && !after.set()) {
                player.sendSystemMessage(Component.translatable("info.oldustry.frequency.unlinked"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(OldustryBlocks.TELEPORTER.get())) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof OldustryTeleporterBlockEntity here)) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        OldustryFrequency.Link link = read(stack);
        boolean storedExists = false;
        boolean storedOpen = false;
        if (link.set()) {
            BlockPos stored = new BlockPos(link.x(), link.y(), link.z());
            storedExists = level.getBlockState(stored).is(OldustryBlocks.TELEPORTER.get());
            if (storedExists && level.getBlockEntity(stored) instanceof OldustryTeleporterBlockEntity other) {
                storedOpen = !other.linked();
            }
        }
        OldustryFrequency.ClickResult result = OldustryFrequency.click(
            link, pos.getX(), pos.getY(), pos.getZ(), storedExists,
            here.linked(), here.targetX(), here.targetY(), here.targetZ(),
            storedOpen
        );
        write(stack, result.transmitter());
        if (result.pointHere()) {
            here.setTarget(link.x(), link.y(), link.z());
        }
        if (result.pointBack()
            && level.getBlockEntity(new BlockPos(link.x(), link.y(), link.z())) instanceof OldustryTeleporterBlockEntity other) {
            other.setTarget(pos.getX(), pos.getY(), pos.getZ());
        }
        Player player = context.getPlayer();
        if (player != null) {
            String key = switch (result.click()) {
                case STORED -> "info.oldustry.frequency.stored";
                case SELF -> "info.oldustry.frequency.self";
                case UNCHANGED -> "info.oldustry.frequency.unchanged";
                case LINKED -> "info.oldustry.frequency.linked";
            };
            player.sendSystemMessage(Component.translatable(key));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        OldustryFrequency.Link link = read(stack);
        if (link.set()) {
            builder.accept(Component.translatable("info.oldustry.frequency.at", link.x(), link.y(), link.z()));
        }
    }

    private static OldustryFrequency.Link read(ItemStack stack) {
        BlockPos pos = stack.get(OldustryDataComponents.FREQUENCY_TARGET.get());
        boolean latched = Boolean.TRUE.equals(stack.get(OldustryDataComponents.FREQUENCY_LATCHED.get()));
        if (pos == null) {
            return new OldustryFrequency.Link(false, 0, 0, 0, latched);
        }
        return new OldustryFrequency.Link(true, pos.getX(), pos.getY(), pos.getZ(), latched);
    }

    private static void write(ItemStack stack, OldustryFrequency.Link link) {
        if (link.set()) {
            stack.set(OldustryDataComponents.FREQUENCY_TARGET.get(), new BlockPos(link.x(), link.y(), link.z()));
        } else {
            stack.remove(OldustryDataComponents.FREQUENCY_TARGET.get());
        }
        if (link.latched()) {
            stack.set(OldustryDataComponents.FREQUENCY_LATCHED.get(), true);
        } else {
            stack.remove(OldustryDataComponents.FREQUENCY_LATCHED.get());
        }
    }
}
