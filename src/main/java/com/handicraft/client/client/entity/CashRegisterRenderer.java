/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.entity;

import com.handicraft.client.block.CashRegisterBlock;
import com.handicraft.client.block.entity.CashRegisterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class CashRegisterRenderer implements BlockEntityRenderer<CashRegisterBlockEntity> {

    @Override
    public void render(CashRegisterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getDisplayStack() != null) {
            matrices.push();
            Direction facing = entity.getCachedState().get(CashRegisterBlock.FACING);
            matrices.translate(0.5 + (0.1 * facing.getOffsetZ()),0.5,0.5 - (0.1 * facing.getOffsetX()));
            matrices.translate(-0.1 * facing.getOffsetX(),0,-0.1 * facing.getOffsetZ());
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation()));
            matrices.scale(-0.35f,0.35f,-0.35f);

            MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getDisplayStack(), ModelTransformation.Mode.FIXED,light,overlay,matrices, vertexConsumers, 0);
            matrices.pop();
        }
    }
}
