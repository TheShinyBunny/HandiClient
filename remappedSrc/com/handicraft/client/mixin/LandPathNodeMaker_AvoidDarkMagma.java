/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LandPathNodeMaker.class)
public class LandPathNodeMaker_AvoidDarkMagma {

    @Inject(method = "method_27138",at = @At("HEAD"),cancellable = true)
    private static void isFireBlock(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.isOf(ModBlocks.DARK_MAGMA_BLOCK)) cir.setReturnValue(true);
    }

}
