/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import com.handicraft.client.client.FakePlayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CapeFeatureRenderer.class)
public abstract class CapeFeatureRenderer_FixForFakePlayers extends FeatureRenderer<AbstractClientPlayerEntity,PlayerEntityModel<AbstractClientPlayerEntity>> {

    public CapeFeatureRenderer_FixForFakePlayers(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Overwrite
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
        if (abstractClientPlayerEntity.canRenderCapeTexture() && !abstractClientPlayerEntity.isInvisible() && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE) && abstractClientPlayerEntity.getCapeTexture() != null) {
             matrixStack.push();

             double d = MathHelper.lerp((double) h, abstractClientPlayerEntity.prevCapeX, abstractClientPlayerEntity.capeX) - MathHelper.lerp((double) h, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.getX());
             double e = MathHelper.lerp((double) h, abstractClientPlayerEntity.prevCapeY, abstractClientPlayerEntity.capeY) - MathHelper.lerp((double) h, abstractClientPlayerEntity.prevY, abstractClientPlayerEntity.getY());
             double m = MathHelper.lerp((double) h, abstractClientPlayerEntity.prevCapeZ, abstractClientPlayerEntity.capeZ) - MathHelper.lerp((double) h, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.getZ());
             float n;
             if (abstractClientPlayerEntity instanceof FakePlayer || (abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && abstractClientPlayerEntity.isFallFlying())) {
                 n = 0;
                 d = 0;
                 e = 0;
                 m = 0;
             } else {
                 n = abstractClientPlayerEntity.prevBodyYaw + (abstractClientPlayerEntity.bodyYaw - abstractClientPlayerEntity.prevBodyYaw);
                 matrixStack.translate(0.0D, 0.0D, 0.125D);
             }
             double o = (double) MathHelper.sin(n * 0.017453292F);
             double p = (double) (-MathHelper.cos(n * 0.017453292F));
             float q = (float) e * 10.0F;
             q = MathHelper.clamp(q, -6.0F, 32.0F);
             float r = (float) (d * o + m * p) * 100.0F;
             r = MathHelper.clamp(r, 0.0F, 150.0F);
             float s = (float) (d * p - m * o) * 100.0F;
             s = MathHelper.clamp(s, -20.0F, 20.0F);
             if (r < 0.0F) {
                 r = 0.0F;
             }

             float t = MathHelper.lerp(h, abstractClientPlayerEntity.prevStrideDistance, abstractClientPlayerEntity.strideDistance);
             q += MathHelper.sin(MathHelper.lerp(h, abstractClientPlayerEntity.prevHorizontalSpeed, abstractClientPlayerEntity.horizontalSpeed) * 6.0F) * 32.0F * t;
             if (abstractClientPlayerEntity.isInSneakingPose()) {
                 q += 25.0F;
             }

             matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(6.0F + r / 2.0F + q));
             matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(s / 2.0F));
             matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - s / 2.0F));
             VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getCapeTexture()));
             this.getContextModel().renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
             matrixStack.pop();
        }
    }
}