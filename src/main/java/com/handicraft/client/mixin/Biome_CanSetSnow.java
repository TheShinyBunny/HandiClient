/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.client.ClientMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class Biome_CanSetSnow {

    @Shadow public abstract Biome.Precipitation getPrecipitation();

    @Redirect(method = "canSetSnow", at = @At(value = "INVOKE",target = "Lnet/minecraft/block/BlockState;isAir()Z"))
    private boolean isAir(BlockState state) {
        return true;
    }

    @Inject(method = "getTemperature(Lnet/minecraft/util/math/BlockPos;)F",at = @At("HEAD"),cancellable = true)
    private void modifyTemperature(BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
        if (this.getPrecipitation() != Biome.Precipitation.NONE) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                if (ClientMod.isAlwaysSnowing) {
                    cir.setReturnValue(0f);
                }
            } else {
                if (CommonMod.SERVER.get().getGameRules().getBoolean(CommonMod.DO_ALWAYS_SNOW)) {
                    cir.setReturnValue(0f);
                }
            }
        }
    }


}
