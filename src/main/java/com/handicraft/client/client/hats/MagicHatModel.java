/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.hats;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class MagicHatModel extends EntityModel<AbstractClientPlayerEntity> {

    private final ModelPart main;
    private float leaningPitch;

    public MagicHatModel() {
        textureWidth = 64;
        textureHeight = 64;
        main = new ModelPart(this);
        main.setPivot(0.0F, 0F, 0.0F);
        main.setTextureOffset(0, 0).addCuboid(-4.0F, -18.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);
        main.setTextureOffset(0, 0).addCuboid(-5.0F, -10.0F, 4.0F, 10.0F, 2.0F, 2.0F, 0.0F, false);
        main.setTextureOffset(0, 0).addCuboid(4.0F, -10.0F, -5.0F, 2.0F, 2.0F, 11.0F, 0.0F, false);
        main.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -6.0F, 10.0F, 2.0F, 2.0F, 0.0F, false);
        main.setTextureOffset(0, 0).addCuboid(-6.0F, -10.0F, -6.0F, 2.0F, 2.0F, 12.0F, 0.0F, false);
    }

    @Override
    public void animateModel(AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
        leaningPitch = entity.getLeaningPitch(tickDelta);
    }

    @Override
    public void setAngles(AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        if (entity.getRoll() > 4) {
            main.pitch = -0.7853982F;
        } else if (leaningPitch > 0) {
            if (entity.isInSwimmingPose()) {
                this.main.pitch = this.lerpAngle(this.leaningPitch, this.main.pitch, -0.7853982F);
            } else {
                this.main.pitch = this.lerpAngle(this.leaningPitch, this.main.pitch, headPitch * 0.017453292F);
            }
        } else {
            main.pitch = (float) Math.toRadians(headPitch);
        }
        main.yaw = (float) Math.toRadians(headYaw);
        if (entity.isInSneakingPose()) {
            main.pivotY = 5;
        } else {
            main.pivotY = 0;
        }
    }

    protected float lerpAngle(float f, float g, float h) {
        float i = (h - g) % 6.2831855F;
        if (i < -3.1415927F) {
            i += 6.2831855F;
        }

        if (i >= 3.1415927F) {
            i -= 6.2831855F;
        }

        return g + f * i;
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
