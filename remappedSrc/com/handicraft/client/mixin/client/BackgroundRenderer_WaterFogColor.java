/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import com.handicraft.client.block.ColoredWaterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BackgroundRenderer.class)
public class BackgroundRenderer_WaterFogColor {

    @Redirect(method = "render",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/biome/Biome;getWaterFogColor()I"))
    private static int modifyFog(Biome biome, Camera camera, float tickDelta, ClientWorld world){
        BlockPos pos = new BlockPos(camera.getPos());
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof ColoredWaterBlock) {
            return ((ColoredWaterBlock) state.getBlock()).getColor().getSignColor();
        }
        return biome.getWaterFogColor();
    }

}
