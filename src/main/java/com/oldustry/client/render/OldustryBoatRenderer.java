package com.oldustry.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/** Vanilla boat mesh with a texture laid out on the 26.2 UV (128×64). */
public final class OldustryBoatRenderer extends AbstractBoatRenderer {
    private final Model.Simple waterPatchModel;
    private final EntityModel<BoatRenderState> model;

    public OldustryBoatRenderer(EntityRendererProvider.Context context, Identifier texture, boolean hidePaddles) {
        super(context, texture);
        this.waterPatchModel = new Model.Simple(context.bakeLayer(ModelLayers.BOAT_WATER_PATCH), ignored -> RenderTypes.waterMask());
        this.model = new OldustryBoatModel(context.bakeLayer(ModelLayers.OAK_BOAT), hidePaddles);
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return model;
    }

    @Override
    protected void submitTypeAdditions(BoatRenderState state, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords) {
        if (!state.isUnderWater) {
            collector.submitModel(
                waterPatchModel, Unit.INSTANCE, poseStack, texture, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null
            );
        }
    }
}
