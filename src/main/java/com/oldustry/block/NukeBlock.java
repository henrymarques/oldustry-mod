package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.oldustry.Oldustry;
import com.oldustry.explosion.PrimedNuke;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/** Nuke. Redstone and flint prime it. A survival break puts the block back in the world. */
public class NukeBlock extends Block {
    public static final MapCodec<NukeBlock> CODEC = simpleCodec(NukeBlock::new);

    public NukeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<NukeBlock> codec() {
        return CODEC;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack stack) {
        if (by instanceof Player player) {
            Oldustry.LOGGER.info("Player {} placed a nuke at {} {}", player.getName().getString(), level.dimension().identifier(), pos.toShortString());
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && level.hasNeighborSignal(pos) && prime(level, pos, null, PrimedNuke.FUSE)) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos) && prime(level, pos, null, PrimedNuke.FUSE)) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !player.getAbilities().instabuild) {
            popResource(level, pos, new ItemStack(this));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
        if (!level.getGameRules().get(GameRules.TNT_EXPLODES)) {
            return;
        }
        int fuse = level.getRandom().nextInt(PrimedNuke.FUSE / 4) + PrimedNuke.FUSE / 8;
        level.addFreshEntity(new PrimedNuke(level, pos, explosion.getIndirectSourceEntity(), fuse));
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    ) {
        if (!stack.is(Items.FLINT_AND_STEEL)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        if (prime(level, pos, player, PrimedNuke.FUSE)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        } else if (level instanceof ServerLevel server && !server.getGameRules().get(GameRules.TNT_EXPLODES)) {
            player.sendOverlayMessage(Component.translatable("block.minecraft.tnt.disabled"));
            return InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean prime(Level level, BlockPos pos, @Nullable LivingEntity igniter, int fuse) {
        if (!(level instanceof ServerLevel server) || !server.getGameRules().get(GameRules.TNT_EXPLODES)) {
            return false;
        }
        Oldustry.LOGGER.info(
            "Nuke at {} {} was ignited {}",
            level.dimension().identifier(),
            pos.toShortString(),
            igniter instanceof Player player ? "by " + player.getName().getString() : "indirectly"
        );
        PrimedNuke primed = new PrimedNuke(server, pos, igniter, fuse);
        if (!server.addFreshEntity(primed)) {
            return false;
        }
        server.playSound(null, primed.getX(), primed.getY(), primed.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        server.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        return true;
    }
}
