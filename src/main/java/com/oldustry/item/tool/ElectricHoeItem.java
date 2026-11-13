package com.oldustry.item.tool;

import com.oldustry.energy.ElectricCharge;
import com.oldustry.item.ElectricToolItem;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * JAR {@code ItemElectricToolHoe}: till grass/dirt → farmland for {@code operationEnergyCost} (50).
 * Till uses modern {@link HoeItem} / {@code HOE_TILL} (paths, coarse, rooted dirt included).
 */
public class ElectricHoeItem extends ElectricToolItem {
    public static final ElectricCharge.Spec SPEC = new ElectricCharge.Spec(10000, 100, 1, true);
    public static final int EU = 50;

    public ElectricHoeItem(Properties properties) {
        super(SPEC, EU, properties);
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(ability);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState tilled = level.getBlockState(pos).getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
        if (tilled == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        boolean creative = player != null && player.getAbilities().instabuild;
        // JAR: canUse before till; empty tool does nothing.
        if (!creative && !canPay(stack, euPerUse(), player)) {
            return InteractionResult.FAIL;
        }
        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            HoeItem.changeIntoState(tilled).accept(context);
            if (!creative) {
                pay(stack, euPerUse(), player);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
