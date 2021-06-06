/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcher_RenderFakePlayer {


    @Overwrite
    public double getSquaredDistanceToCamera(Entity entity) {
        return 0;
    }

    @Overwrite
    public double getSquaredDistanceToCamera(double x, double y, double z) {
        return 0;
    }
}
