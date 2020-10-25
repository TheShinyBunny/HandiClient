/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.block.ModBlocks;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlock_AddCustomFire {

    @Inject(method = "getState",at = @At("HEAD"),cancellable = true)
    private static void getCustomFire(BlockView world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (ModBlocks.GREEN_DARK_FIRE.canPlaceAt(world.getBlockState(pos),(WorldView)world,pos)) {
            cir.setReturnValue(ModBlocks.GREEN_DARK_FIRE.getDefaultState());
        }
        if (ModBlocks.PURPLE_DARK_FIRE.canPlaceAt(world.getBlockState(pos), (WorldView) world,pos)) {
            cir.setReturnValue(ModBlocks.PURPLE_DARK_FIRE.getDefaultState());
        }
    }

}
