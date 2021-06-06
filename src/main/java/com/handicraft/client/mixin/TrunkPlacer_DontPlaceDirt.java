/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TrunkPlacer.class)
public class TrunkPlacer_DontPlaceDirt {

    @Overwrite
    private static boolean canGenerate(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, (blockState) -> {
            if (blockState.isIn(ModBlocks.Tags.DARK_STONES)) return true;
            return Feature.isSoil(blockState) && !blockState.isOf(Blocks.GRASS_BLOCK) && !blockState.isOf(Blocks.MYCELIUM);
        });
    }
}
