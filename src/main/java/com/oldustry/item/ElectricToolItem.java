package com.oldustry.item;

import com.oldustry.energy.ElectricCharge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricToolItem extends ElectricItem {
    private final int euPerUse;
    /** When &gt; 0 and TOOL says the block is efficient, force this speed (mining vs diamond drill). */
    private final float poweredMiningSpeed;

    public ElectricToolItem(ElectricCharge.Spec spec, int euPerUse, Properties properties) {
        this(spec, euPerUse, 0f, properties);
    }

    public ElectricToolItem(ElectricCharge.Spec spec, int euPerUse, float poweredMiningSpeed, Properties properties) {
        super(spec, properties);
        this.euPerUse = euPerUse;
        this.poweredMiningSpeed = poweredMiningSpeed;
    }

    public int euPerUse() {
        return euPerUse;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (energy(stack) < euPerUse) {
            return 1f;
        }
        float toolSpeed = super.getDestroySpeed(stack, state);
        // TOOL matched pickaxe/shovel (efficient) — apply explicit drill speed so diamond ≠ mining.
        if (poweredMiningSpeed > 0f && toolSpeed > 1f) {
            return poweredMiningSpeed;
        }
        return toolSpeed;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        // JAR ElectricItemManager.use: only drain when the full operation cost is available (no partial).
        if (!level.isClientSide()
            && state.getDestroySpeed(level, pos) != 0
            && energy(stack) >= euPerUse) {
            discharge(stack, euPerUse, 99);
        }
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Same full-cost gate as mining (JAR hitEntity does not drain; keep cost if we do).
        if (energy(stack) >= euPerUse) {
            discharge(stack, euPerUse, 99);
        }
    }
}
