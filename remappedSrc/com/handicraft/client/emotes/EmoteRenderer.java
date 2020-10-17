/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.emotes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class EmoteRenderer {
    public static void render(AbstractClientPlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean considerLabel) {
        EmoteManager.EmoteInstance m = EmoteManager.getEmoteFor(player);
        if (m != null) {
            int time = m.getTime();
            int life = m.getLifespan();
            float alpha = MathHelper.sqrt(MathHelper.clamp(1 - (time / (float)life),0,1));

            matrices.push();
            matrices.scale(-0.25F, 0.25F, 0.25F);
            matrices.translate(0, player.getHeight() + (considerLabel ? 8 : 6) + (Math.sqrt(time) / 15f), 0);

            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MinecraftClient.getInstance().gameRenderer.getCamera().getYaw()));
            MatrixStack.Entry entry = matrices.peek();
            Matrix4f matrix4f = entry.getModel();
            Matrix3f matrix3f = entry.getNormal();

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(m.getTexture()));
            produceVertex(vertexConsumer, matrix4f, matrix3f, light, 0, 0, 0, 1,alpha);
            produceVertex(vertexConsumer, matrix4f, matrix3f, light, 1, 0, 1, 1,alpha);
            produceVertex(vertexConsumer, matrix4f, matrix3f, light, 1, 1, 1, 0,alpha);
            produceVertex(vertexConsumer, matrix4f, matrix3f, light, 0, 1, 0, 0,alpha);
            matrices.pop();
        }
    }

    private static void produceVertex(VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, int light, int x, int y, int textureU, int textureV, float alpha) {
        vertexConsumer.vertex(modelMatrix, x - 0.5F, (float)y - 0.25F, 0.0F).color(1,1,1,alpha).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }
}
