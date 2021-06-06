/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Fluid.class)
public class Fluid_FixTag {

    @Overwrite
    public boolean isIn(Tag<Fluid> tag) {
        try {
            return tag.contains((Fluid)(Object)this);
        } catch (Exception e) {
            return false;
        }
    }
}
