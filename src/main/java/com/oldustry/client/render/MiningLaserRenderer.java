package com.oldustry.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import com.oldustry.Oldustry;
import com.oldustry.entity.MiningLaser;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/** IC2 {@code RenderCrossed} on {@code laser.png}. Tail cap plus the arrow shaft, spun four times. */
public final class MiningLaserRenderer extends EntityRenderer<MiningLaser, MiningLaserRenderer.State> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/entity/laser.png");

    public MiningLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(MiningLaser entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getYRot(partialTick);
        state.xRot = entity.getXRot(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(45f));
        poseStack.scale(0.05625f, 0.05625f, 0.05625f);
        poseStack.translate(-4f, 0f, 0f);
        int light = state.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            vertex(buffer, pose, -7f, -2f, -2f, 0f, 5f / 32f, light, 1f, 0f, 0f);
            vertex(buffer, pose, -7f, -2f, 2f, 0.15625f, 5f / 32f, light, 1f, 0f, 0f);
            vertex(buffer, pose, -7f, 2f, 2f, 0.15625f, 10f / 32f, light, 1f, 0f, 0f);
            vertex(buffer, pose, -7f, 2f, -2f, 0f, 10f / 32f, light, 1f, 0f, 0f);
            vertex(buffer, pose, -7f, 2f, -2f, 0f, 5f / 32f, light, -1f, 0f, 0f);
            vertex(buffer, pose, -7f, 2f, 2f, 0.15625f, 5f / 32f, light, -1f, 0f, 0f);
            vertex(buffer, pose, -7f, -2f, 2f, 0.15625f, 10f / 32f, light, -1f, 0f, 0f);
            vertex(buffer, pose, -7f, -2f, -2f, 0f, 10f / 32f, light, -1f, 0f, 0f);
        });
        for (int turn = 0; turn < 4; turn++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
                vertex(buffer, pose, -8f, -2f, 0f, 0f, 0f, light, 0f, 0f, 1f);
                vertex(buffer, pose, 8f, -2f, 0f, 0.5f, 0f, light, 0f, 0f, 1f);
                vertex(buffer, pose, 8f, 2f, 0f, 0.5f, 5f / 32f, light, 0f, 0f, 1f);
                vertex(buffer, pose, -8f, 2f, 0f, 0f, 5f / 32f, light, 0f, 0f, 1f);
            });
        }
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    /** {@code DefaultVertexFormat.ENTITY} also wants overlay and a normal. Leaving them out crashes the frame. */
    private static void vertex(
        VertexConsumer buffer, PoseStack.Pose pose,
        float x, float y, float z, float u, float v, int light, float nx, float ny, float nz
    ) {
        buffer.addVertex(pose, x, y, z)
            .setColor(-1)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, nx, ny, nz);
    }

    public static final class State extends EntityRenderState {
        public float yRot;
        public float xRot;
    }
}
