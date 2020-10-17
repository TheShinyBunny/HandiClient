/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.stateprovider.ForestFlowerBlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.Random;

@Mixin(ForestFlowerBlockStateProvider.class)
public class FlowerForest_AddCustomFlowers {

    @Shadow @Final private static BlockState[] FLOWERS;

    @Overwrite
    public BlockState getBlockState(Random random, BlockPos pos) {
        BlockState[] flowers = Arrays.copyOf(FLOWERS,FLOWERS.length+1);
        flowers[FLOWERS.length] = ModBlocks.PEONY.getDefaultState();
        double d = MathHelper.clamp((1.0D + Biome.FOLIAGE_NOISE.sample((double) pos.getX() / 48.0D, (double) pos.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
        return flowers[(int) (d * (double) flowers.length)];
    }
}
