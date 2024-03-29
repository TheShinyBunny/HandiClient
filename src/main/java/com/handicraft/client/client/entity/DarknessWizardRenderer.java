/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.IllusionerEntityRenderer;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;

public class DarknessWizardRenderer extends IllusionerEntityRenderer {

    private static final Identifier TEXTURE = new Identifier("textures/entity/darkness_wizard.png");

    public DarknessWizardRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public Identifier getTexture(IllusionerEntity illusionerEntity) {
        return TEXTURE;
    }
}
