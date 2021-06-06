/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.entity;

import net.minecraft.client.render.entity.BlazeEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;

public class DarkBlazeRenderer extends BlazeEntityRenderer {

    private static final Identifier TEXTURE = new Identifier("textures/entity/dark_blaze.png");

    public DarkBlazeRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(BlazeEntity blazeEntity) {
        return TEXTURE;
    }
}
