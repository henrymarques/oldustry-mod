package com.oldustry.block;

import com.oldustry.item.BoozeContents;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** JAR {@code TileEntityBarrel}. Beer / rum aging; no GUI. */
public class OldustryBarrelBlockEntity extends BlockEntity {
    private int type;
    private int boozeAmount;
    private int age;
    private boolean detailed = true;
    private Direction treetapSide = Direction.DOWN;
    private int hopsCount;
    private int wheatCount;
    private int solidRatio;
    private int hopsRatio;
    private int timeRatio;

    public OldustryBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryBlockEntities.BARREL.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OldustryBarrelBlockEntity barrel) {
        if (level.isClientSide() || barrel.isEmpty()) {
            return;
        }
        // JAR: age while treetapSide < 2 (no horizontal tap). Tap installed → aging stops.
        if (barrel.treetapSide.get3DDataValue() >= 2) {
            return;
        }
        barrel.age++;
        if (barrel.type == 1 && barrel.timeRatio < 5) {
            int x = barrel.timeRatio;
            if (x == 4) {
                x += 2;
            }
            if (barrel.age >= 24000.0D * Math.pow(3.0D, x)) {
                barrel.age = 0;
                barrel.timeRatio++;
            }
        }
        barrel.setChanged();
    }

    public void loadFrom(BoozeContents contents) {
        type = contents.type();
        if (type > 0) {
            boozeAmount = contents.amount();
        }
        if (type == 1) {
            detailed = false;
            hopsRatio = contents.hopsRatio();
            solidRatio = contents.solidRatio();
            timeRatio = contents.timeRatio();
        }
        if (type == 2) {
            detailed = true;
            age = timeNedForRum(boozeAmount) * contents.rumProgress() / 100;
        }
        setChanged();
    }

    public boolean isEmpty() {
        return type == 0 || boozeAmount <= 0;
    }

    public Direction treetapSide() {
        return treetapSide;
    }

    public boolean rightClick(Player player, InteractionHand hand) {
        ItemStack cur = player.getItemInHand(hand);
        if (cur.isEmpty()) {
            return false;
        }
        if (cur.is(Items.WATER_BUCKET)) {
            if (!detailed || boozeAmount + 1 > 32 || type > 1) {
                return false;
            }
            type = 1;
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, new ItemStack(Items.BUCKET));
            }
            boozeAmount++;
            setChanged();
            return true;
        }
        if (cur.is(OldustryItems.WATER_CELL.get())) {
            if (!detailed || type > 1) {
                return false;
            }
            type = 1;
            int want = player.isShiftKeyDown() ? 1 : cur.getCount();
            if (boozeAmount + want > 32) {
                want = 32 - boozeAmount;
            }
            if (want <= 0) {
                return false;
            }
            boozeAmount += want;
            if (!player.getAbilities().instabuild) {
                cur.shrink(want);
            }
            setChanged();
            return true;
        }
        if (cur.is(Items.WHEAT)) {
            if (!detailed || type > 1) {
                return false;
            }
            type = 1;
            int want = player.isShiftKeyDown() ? 1 : cur.getCount();
            if (want > 64 - wheatCount) {
                want = 64 - wheatCount;
            }
            if (want <= 0) {
                return false;
            }
            wheatCount += want;
            if (!player.getAbilities().instabuild) {
                cur.shrink(want);
            }
            alterComposition();
            setChanged();
            return true;
        }
        if (cur.is(OldustryItems.HOPS.get())) {
            if (!detailed || type > 1) {
                return false;
            }
            type = 1;
            int want = player.isShiftKeyDown() ? 1 : cur.getCount();
            if (want > 64 - hopsCount) {
                want = 64 - hopsCount;
            }
            if (want <= 0) {
                return false;
            }
            hopsCount += want;
            if (!player.getAbilities().instabuild) {
                cur.shrink(want);
            }
            alterComposition();
            setChanged();
            return true;
        }
        if (cur.is(Items.SUGAR_CANE)) {
            if (age > 600 || (type > 0 && type != 2)) {
                return false;
            }
            type = 2;
            int want = player.isShiftKeyDown() ? 1 : cur.getCount();
            if (boozeAmount + want > 32) {
                want = 32 - boozeAmount;
            }
            if (want <= 0) {
                return false;
            }
            boozeAmount += want;
            if (!player.getAbilities().instabuild) {
                cur.shrink(want);
            }
            setChanged();
            return true;
        }
        return false;
    }

    public boolean useTreetap(Player player, Direction side, ItemStack tap) {
        if (!tap.is(OldustryItems.TREETAP.get()) || tap.getDamageValue() != 0 || !side.getAxis().isHorizontal()) {
            return false;
        }
        treetapSide = side;
        syncTap();
        if (!player.getAbilities().instabuild) {
            tap.shrink(1);
        }
        setChanged();
        return true;
    }

    public boolean drainLiquid(int amount) {
        if (isEmpty() || amount > boozeAmount) {
            return false;
        }
        enforceUndetailed();
        if (type == 2) {
            int progress = age * 100 / Math.max(1, timeNedForRum(boozeAmount));
            boozeAmount -= amount;
            age = progress / 100 * timeNedForRum(boozeAmount);
        } else {
            boozeAmount -= amount;
        }
        if (boozeAmount <= 0) {
            if (type == 1) {
                hopsCount = 0;
                wheatCount = 0;
                hopsRatio = 0;
                solidRatio = 0;
                timeRatio = 0;
            }
            type = 0;
            detailed = true;
            boozeAmount = 0;
        }
        setChanged();
        return true;
    }

    public BoozeContents snapshot() {
        if (isEmpty()) {
            return BoozeContents.EMPTY;
        }
        enforceUndetailed();
        if (type == 1) {
            return BoozeContents.beer(boozeAmount, solidRatio, hopsRatio, timeRatio);
        }
        if (type == 2) {
            int progress = Math.min(100, age * 100 / Math.max(1, timeNedForRum(boozeAmount)));
            return BoozeContents.rum(boozeAmount, progress);
        }
        return BoozeContents.EMPTY;
    }

    public void clearTap() {
        treetapSide = Direction.DOWN;
        syncTap();
        setChanged();
    }

    private void alterComposition() {
        if (timeRatio == 0) {
            age = 0;
        }
        Level level = this.level;
        if (level == null) {
            return;
        }
        if (timeRatio == 1) {
            if (level.getRandom().nextBoolean()) {
                timeRatio = 0;
            } else if (level.getRandom().nextBoolean()) {
                timeRatio = 5;
            }
        }
        if (timeRatio == 2 && level.getRandom().nextBoolean()) {
            timeRatio = 5;
        }
        if (timeRatio > 2) {
            timeRatio = 5;
        }
    }

    private void enforceUndetailed() {
        if (!detailed) {
            return;
        }
        detailed = false;
        if (type != 1) {
            return;
        }
        float hops = wheatCount > 0 ? (float) hopsCount / wheatCount : 10.0F;
        if (hopsCount <= 0 && wheatCount <= 0) {
            hops = 0.0F;
        }
        float solid = boozeAmount > 0 ? (float) (hopsCount + wheatCount) / boozeAmount : 10.0F;
        if (hops <= 0.25F) {
            hopsRatio = 0;
        } else if (hops <= 0.33333334F) {
            hopsRatio = 1;
        } else if (hops <= 0.5F) {
            hopsRatio = 2;
        } else if (hops < 2.0F) {
            hopsRatio = 3;
        } else if (hops < 3.0F) {
            hopsRatio = 4;
        } else if (hops < 4.0F) {
            hopsRatio = 5;
        } else if (hops < 5.0F) {
            hopsRatio = 6;
        } else {
            timeRatio = 5;
        }
        // JAR: first `if (solid <= 0.416...)` has no body; thresholds below match the decompile.
        if (solid > 0.41666666F && solid <= 0.5F) {
            solidRatio = 1;
        } else if (solid > 0.5F && solid < 1.0F) {
            solidRatio = 2;
        } else if (solid == 1.0F) {
            solidRatio = 3;
        } else if (solid > 1.0F && solid < 2.0F) {
            solidRatio = 4;
        } else if (solid >= 2.0F && solid < 2.4F) {
            solidRatio = 5;
        } else if (solid >= 2.4F && solid < 4.0F) {
            solidRatio = 6;
        } else if (solid >= 4.0F) {
            timeRatio = 5;
        }
    }

    private void syncTap() {
        if (level != null) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof OldustryBarrelBlock && state.hasProperty(OldustryBarrelBlock.TAP)) {
                int tap = treetapSide.getAxis().isHorizontal() ? treetapSide.get3DDataValue() : 0;
                level.setBlock(worldPosition, state.setValue(OldustryBarrelBlock.TAP, tap), Block.UPDATE_CLIENTS);
            }
        }
    }

    public static int timeNedForRum(int amount) {
        return (int) ((1200 * amount) * Math.pow(0.95D, amount - 1));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("type", type);
        output.putInt("waterCount", boozeAmount);
        output.putInt("age", age);
        output.putInt("treetapSide", treetapSide.get3DDataValue());
        output.putBoolean("detailed", detailed);
        if (type == 1) {
            if (detailed) {
                output.putInt("hopsCount", hopsCount);
                output.putInt("wheatCount", wheatCount);
            }
            output.putInt("solidRatio", solidRatio);
            output.putInt("hopsRatio", hopsRatio);
            output.putInt("timeRatio", timeRatio);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        type = input.getIntOr("type", 0);
        boozeAmount = input.getIntOr("waterCount", 0);
        age = input.getIntOr("age", 0);
        treetapSide = Direction.from3DDataValue(input.getIntOr("treetapSide", 0));
        detailed = input.getBooleanOr("detailed", true);
        if (type == 1) {
            if (detailed) {
                hopsCount = input.getIntOr("hopsCount", 0);
                wheatCount = input.getIntOr("wheatCount", 0);
            }
            solidRatio = input.getIntOr("solidRatio", 0);
            hopsRatio = input.getIntOr("hopsRatio", 0);
            timeRatio = input.getIntOr("timeRatio", 0);
        }
    }
}
