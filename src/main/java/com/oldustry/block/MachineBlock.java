package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oldustry.machine.OldustryMachineLayout;
import com.oldustry.machine.OldustryMachineKind;
import com.oldustry.machine.OldustryMachineBlockEntity;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.wrench.Wrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class MachineBlock extends BaseEntityBlock implements Wrenchable {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final MapCodec<MachineBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        StringRepresentable.fromEnum(OldustryMachineKind::values).fieldOf("kind").forGetter(b -> b.kind),
        propertiesCodec()
    ).apply(i, MachineBlock::new));

    public final OldustryMachineKind kind;

    public MachineBlock(OldustryMachineKind kind, BlockBehaviour.Properties properties) {
        super(properties);
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    @Override
    public MapCodec<? extends MachineBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = kind.verticalFacing()
            ? context.getNearestLookingDirection().getOpposite()
            : context.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return OldustryMachineBlockEntity.create(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel server) {
            return createTickerHelper(type, OldustryBlockEntities.MACHINE.get(), (l, pos, s, be) -> OldustryMachineBlockEntity.serverTick(server, pos, s, be));
        }
        return createTickerHelper(type, OldustryBlockEntities.MACHINE.get(), OldustryMachineBlockEntity::clientTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // JAR transformers/luminator/magnetizer: no GUI — PASS so adjacent place works without sneak.
        if (!OldustryMachineLayout.hasGui(kind)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OldustryMachineBlockEntity machine) {
            player.openMenu(machine);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canSetFacing(BlockState state, Direction face) {
        return kind.verticalFacing() && state.getValue(FACING) != face;
    }

    @Override
    public BlockState setFacing(BlockState state, Direction face) {
        return state.setValue(FACING, face);
    }

    @Override
    public float wrenchDropRate() {
        return kind.wrenchRate;
    }

    @Override
    public ItemStack wrenchKeepDrop(BlockState state) {
        return new ItemStack(this);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return kind.type == OldustryMachineKind.Type.STORAGE;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof OldustryMachineBlockEntity machine ? machine.redstonePower() : 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        // Modernization: all electric machines expose comparator (progress or buffer fill).
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof OldustryMachineBlockEntity machine ? machine.comparator() : 0;
    }

    @Override
    public ItemStack wrenchFailDrop(BlockState state) {
        return kind.type == OldustryMachineKind.Type.MACERATOR
            || kind.type == OldustryMachineKind.Type.EXTRACTOR
            || kind.type == OldustryMachineKind.Type.COMPRESSOR
            || kind.type == OldustryMachineKind.Type.RECYCLER
            ? new ItemStack(com.oldustry.registry.OldustryBlocks.MACHINE_CASING.get())
            : new ItemStack(this);
    }
}
