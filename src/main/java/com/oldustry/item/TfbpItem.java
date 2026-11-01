package com.oldustry.item;

import com.oldustry.machine.OldustryTerraformerBlockEntity;
import com.oldustry.machine.OldustryTfbp;
import com.oldustry.registry.OldustryBlocks;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** Programmed blueprint. The empty TFBP is a plain item and does not insert. */
public final class TfbpItem extends Item {
    private final OldustryTfbp kind;

    public TfbpItem(OldustryTfbp kind, Properties properties) {
        super(properties.stacksTo(1));
        this.kind = kind;
    }

    public OldustryTfbp kind() {
        return kind;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.getBlockState(context.getClickedPos()).is(OldustryBlocks.TERRAFORMER.get())) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(context.getClickedPos()) instanceof OldustryTerraformerBlockEntity terra && context.getPlayer() != null) {
            terra.insert(context.getPlayer(), context.getItemInHand());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
