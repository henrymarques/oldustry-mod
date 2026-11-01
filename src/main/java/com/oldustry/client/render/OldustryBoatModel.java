package com.oldustry.client.render;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.state.BoatRenderState;

/** 26.2 boat mesh. The electric boat has no paddles. */
public final class OldustryBoatModel extends BoatModel {
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;
    private final boolean hidePaddles;

    public OldustryBoatModel(ModelPart root, boolean hidePaddles) {
        super(root);
        this.leftPaddle = root.getChild("left_paddle");
        this.rightPaddle = root.getChild("right_paddle");
        this.hidePaddles = hidePaddles;
    }

    @Override
    public void setupAnim(BoatRenderState state) {
        super.setupAnim(state);
        if (hidePaddles) {
            leftPaddle.visible = false;
            rightPaddle.visible = false;
        }
    }
}
