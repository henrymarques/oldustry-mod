package com.oldustry.item;

import java.util.function.Consumer;

import com.oldustry.registry.ModDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;

public class UpgradeItem extends Item {
    public enum Kind {
        OVERCLOCKER,
        TRANSFORMER,
        STORAGE,
        EJECTOR
    }

    private final Kind kind;

    public UpgradeItem(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (kind != Kind.EJECTOR) {
            return InteractionResult.PASS;
        }
        int dir = 1 + (context.getClickedFace().get3DDataValue() + 2) % 6;
        ItemStack stack = context.getItemInHand();
        if (stack.getOrDefault(ModDataComponents.EJECTOR_DIR.get(), 0) != dir) {
            stack.set(ModDataComponents.EJECTOR_DIR.get(), dir);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        int n = stack.getCount();
        switch (kind) {
            case OVERCLOCKER -> {
                builder.accept(Component.translatable("oldustry.tooltip.upgrade.overclocker.time", String.format("%.2f", 100 * Math.pow(0.7, n))));
                builder.accept(Component.translatable("oldustry.tooltip.upgrade.overclocker.power", String.format("%.2f", 100 * Math.pow(1.6, n))));
            }
            case TRANSFORMER -> builder.accept(Component.translatable("oldustry.tooltip.upgrade.transformer", n));
            case STORAGE -> builder.accept(Component.translatable("oldustry.tooltip.upgrade.storage", 10000 * n));
            case EJECTOR -> {
                int dir = stack.getOrDefault(ModDataComponents.EJECTOR_DIR.get(), 0);
                String side = switch (dir) {
                    case 1 -> "oldustry.dir.west";
                    case 2 -> "oldustry.dir.east";
                    case 3 -> "oldustry.dir.bottom";
                    case 4 -> "oldustry.dir.top";
                    case 5 -> "oldustry.dir.north";
                    case 6 -> "oldustry.dir.south";
                    default -> "oldustry.tooltip.upgrade.ejector.anyside";
                };
                builder.accept(Component.translatable("oldustry.tooltip.upgrade.ejector", Component.translatable(side)));
            }
        }
    }
}
