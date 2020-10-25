/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PillagerEntityRenderer;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

public class DarkPillagerRenderer extends PillagerEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("textures/entity/dark_pillager.png");

    public DarkPillagerRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public Identifier getTexture(PillagerEntity pillagerEntity) {
        return TEXTURE;
    }
}
