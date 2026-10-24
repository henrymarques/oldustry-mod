package com.oldustry.client.render;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.oldustry.Oldustry;
import com.oldustry.block.PersonalSafeBlock;
import com.oldustry.block.PersonalSafeBlockEntity;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/** Door yaw from {@code TileEntityPersonalChestRenderer}. North 180°, west 90°, east −90°, south 0°. */
public class PersonalSafeRenderer implements BlockEntityRenderer<PersonalSafeBlockEntity, PersonalSafeRenderer.State> {
    static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/entity/personal_safe.png");
    private final PersonalSafeModel model;

    public PersonalSafeRenderer(BlockEntityRendererProvider.Context context) {
        this.model = PersonalSafeModel.create();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
        PersonalSafeBlockEntity safe,
        State state,
        float partialTick,
        Vec3 camera,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(safe, state, partialTick, camera, breakProgress);
        state.facing = safe.getBlockState().getValue(PersonalSafeBlock.FACING);
        state.openness = safe.lid(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        pose(poseStack, facingDegrees(state.facing));
        collector.submitModel(
            model, eased(state.openness), poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
        );
        poseStack.popPose();
    }

    static float facingDegrees(Direction facing) {
        return switch (facing) {
            case NORTH -> 180.0F;
            case WEST -> 90.0F;
            case EAST -> -90.0F;
            default -> 0.0F;
        };
    }

    static float eased(float lid) {
        float closed = 1.0F - lid;
        return 1.0F - closed * closed * closed;
    }

    /** GL from the block corner: translate (0,1,1), flip Y/Z, rotate Y, back to the corner. */
    static void pose(PoseStack pose, float degrees) {
        pose.translate(0.0F, 1.0F, 1.0F);
        pose.scale(1.0F, -1.0F, -1.0F);
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(degrees));
        pose.translate(-0.5F, -0.5F, -0.5F);
    }

    public static class State extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public float openness;
    }

    public static final class Item implements NoDataSpecialModelRenderer {
        private final PersonalSafeModel model;

        public Item(PersonalSafeModel model) {
            this.model = model;
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean foil, int outline) {
            poseStack.pushPose();
            pose(poseStack, 0.0F);
            collector.submitModel(model, 0.0F, poseStack, TEXTURE, light, overlay, outline, null);
            poseStack.popPose();
        }

        @Override
        public void getExtents(Consumer<Vector3fc> output) {
            PoseStack poseStack = new PoseStack();
            pose(poseStack, 0.0F);
            model.setupAnim(0.0F);
            model.root().getExtentsForGui(poseStack, output);
        }
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<Void> bake(SpecialModelRenderer.BakingContext context) {
            return new Item(PersonalSafeModel.create());
        }
    }
}
