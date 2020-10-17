/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FluidRenderer.class)
public class FluidRenderer_MatchWaters {

    @Redirect(method = "isSameFluid",at = @At(value = "INVOKE",target = "Lnet/minecraft/fluid/Fluid;matchesType(Lnet/minecraft/fluid/Fluid;)Z"))
    private static boolean matchesWater(Fluid fluid1, Fluid fluid2) {
        if (fluid1.isIn(FluidTags.WATER) && fluid2.isIn(FluidTags.WATER)) return true;
        return fluid1.matchesType(fluid2);
    }

    @Redirect(method = "getNorthWestCornerFluidHeight",at = @At(value = "INVOKE",target = "Lnet/minecraft/fluid/Fluid;matchesType(Lnet/minecraft/fluid/Fluid;)Z"))
    private boolean matchesWater2(Fluid fluid1, Fluid fluid2) {
        if (fluid1.isIn(FluidTags.WATER) && fluid2.isIn(FluidTags.WATER)) return true;
        return fluid1.matchesType(fluid2);
    }

}
