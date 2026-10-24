package com.oldustry.block;

import java.util.Random;

import com.mojang.serialization.MapCodec;
import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustrySounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

public class RubberLogBlock extends RotatedPillarBlock {
    public static final MapCodec<RubberLogBlock> CODEC = simpleCodec(RubberLogBlock::new);
    public static final EnumProperty<TapFace> TAP_FACE = EnumProperty.create("tap_face", TapFace.class);
    public static final BooleanProperty WET = BooleanProperty.create("wet");

    public static final int REFILL_INTERVAL = 100;

    public RubberLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TAP_FACE, TapFace.NONE).setValue(WET, false));
    }

    @Override
    public MapCodec<? extends RubberLogBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TAP_FACE, WET);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? defaultBlockState() : state.setValue(TAP_FACE, TapFace.NONE).setValue(WET, false);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation).setValue(TAP_FACE, state.getValue(TAP_FACE).rotate(rotation));
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    ) {
        if (!stack.is(OldustryItems.TREETAP.get())) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        Direction face = hit.getDirection();
        if (!matchesFace(state, face)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            RubberTapLogic.Result result = extract(state, new Random(level.getRandom().nextLong()));
            if (result.resin() > 0) {
                eject(level, pos, face, result.resin());
            }
            BlockState next = apply(state, result);
            level.setBlock(pos, next, Block.UPDATE_ALL_IMMEDIATE);
            scheduleRefill(level, pos, next);
            stack.hurtAndBreak(1, player, hand);
            OldustrySounds.play(level, null, pos, OldustrySounds.TREETAP.get(), SoundSource.BLOCKS, 1f, 1f);
        }
        return InteractionResult.SUCCESS;
    }

    private static void eject(Level level, BlockPos pos, Direction face, int quantity) {
        double x = pos.getX() + 0.5 + face.getStepX() * 0.3;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5 + face.getStepZ() * 0.3;
        for (int i = 0; i < quantity; i++) {
            ItemEntity entity = new ItemEntity(level, x, y, z, new ItemStack(OldustryItems.STICKY_RESIN.get()));
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
    }

    public static BlockState withRandomTap(BlockState state, RandomSource random, int[] holeChance) {
        if (random.nextInt(100) <= holeChance[0]) {
            holeChance[0] -= 10;
            return state.setValue(TAP_FACE, TapFace.HORIZONTAL[random.nextInt(4)]).setValue(WET, true);
        }
        return state.setValue(TAP_FACE, TapFace.NONE).setValue(WET, false);
    }

    public static RubberTapLogic.Result extract(BlockState state, Random random) {
        TapFace face = state.getValue(TAP_FACE);
        return RubberTapLogic.extract(face != TapFace.NONE, state.getValue(WET) && face != TapFace.NONE, random);
    }

    public static BlockState apply(BlockState state, RubberTapLogic.Result result) {
        if (!result.hole()) {
            return state.setValue(TAP_FACE, TapFace.NONE).setValue(WET, false);
        }
        return state.setValue(WET, result.wet());
    }

    public static boolean matchesFace(BlockState state, Direction clicked) {
        return state.getValue(TAP_FACE).direction == clicked;
    }

    public static void scheduleRefill(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(TAP_FACE) != TapFace.NONE && !state.getValue(WET)) {
            level.scheduleTick(pos, state.getBlock(), REFILL_INTERVAL);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(TAP_FACE) == TapFace.NONE || state.getValue(WET)) {
            return;
        }
        if (RubberTapLogic.refillDry(new Random(random.nextLong()))) {
            level.setBlock(pos, state.setValue(WET, true), Block.UPDATE_ALL);
            return;
        }
        scheduleRefill(level, pos, state);
    }

    @Override
    protected java.util.List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        var drops = new java.util.ArrayList<>(super.getDrops(state, builder));
        RandomSource random = builder.getLevel().getRandom();
        if (RubberTapLogic.extraResinOnBreak(new Random(random.nextLong()))) {
            drops.add(new ItemStack(OldustryItems.STICKY_RESIN.get()));
        }
        return drops;
    }

    public enum TapFace implements StringRepresentable {
        NONE("none", null),
        NORTH("north", Direction.NORTH),
        SOUTH("south", Direction.SOUTH),
        WEST("west", Direction.WEST),
        EAST("east", Direction.EAST);

        public static final TapFace[] HORIZONTAL = { NORTH, SOUTH, WEST, EAST };

        private final String name;
        public final Direction direction;

        TapFace(String name, Direction direction) {
            this.name = name;
            this.direction = direction;
        }

        public TapFace rotate(Rotation rotation) {
            if (direction == null) {
                return this;
            }
            return from(rotation.rotate(direction));
        }

        public static TapFace from(Direction direction) {
            return switch (direction) {
                case NORTH -> NORTH;
                case SOUTH -> SOUTH;
                case WEST -> WEST;
                case EAST -> EAST;
                default -> NONE;
            };
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
