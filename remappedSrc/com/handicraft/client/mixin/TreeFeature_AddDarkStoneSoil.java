/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeFeature.class)
public class TreeFeature_AddDarkStoneSoil {

    @Inject(method = "isDirtOrGrass",at = @At("HEAD"), cancellable = true)
    private static void isDarkStone(TestableWorld world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world.testBlockState(pos,b->b.isIn(ModBlocks.Tags.DARK_STONES))) cir.setReturnValue(true);
    }

}
