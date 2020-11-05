/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class ServerWorld_SnowStacking {

    @Redirect(method = "tickChunk",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",ordinal = 1))
    private boolean setSnow(ServerWorld serverWorld, BlockPos pos, BlockState state) {
        BlockState prev = serverWorld.getBlockState(pos);
        if (prev.isOf(Blocks.SNOW)) {
            int layers = prev.get(SnowBlock.LAYERS);
            if (layers < 8) {
                serverWorld.setBlockState(pos, prev.with(SnowBlock.LAYERS, prev.get(SnowBlock.LAYERS) + 1));
            }
        } else {
            serverWorld.setBlockState(pos, state);
        }
        return true;
    }

}
