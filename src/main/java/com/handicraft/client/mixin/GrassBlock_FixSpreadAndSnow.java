/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.block.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(SpreadableBlock.class)
public abstract class GrassBlock_FixSpreadAndSnow extends SnowyBlock {

    protected GrassBlock_FixSpreadAndSnow(Settings settings) {
        super(settings);
    }

    @Shadow
    protected static boolean canSpread(BlockState state, WorldView worldView, BlockPos pos) {
        return false;
    }

    @Overwrite
    private static boolean canSurvive(BlockState state, WorldView worldView, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = worldView.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SNOW)) {
            return true;
        } else if (blockState.getFluidState().getLevel() == 8) {
            return false;
        } else {
            int i = ChunkLightProvider.getRealisticOpacity(worldView, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(worldView, blockPos));
            return i < worldView.getMaxLightLevel();
        }
    }

    @Overwrite
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!canSurvive(state, world, pos)) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        } else {
            if (world.getLightLevel(pos.up()) >= 9) {
                BlockState blockState = this.getDefaultState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    BlockState targetState = world.getBlockState(blockPos);
                    if (canSpread(blockState, world, blockPos)) {
                        if (targetState.isOf(Blocks.DIRT)) {
                            world.setBlockState(blockPos, blockState.with(SNOWY, world.getBlockState(blockPos.up()).isOf(Blocks.SNOW)));
                        } else if (targetState.isOf(ModBlocks.DIRT_SLAB)) {
                            world.setBlockState(blockPos, ModBlocks.GRASS_BLOCK_SLAB.getDefaultState().with(SlabBlock.TYPE,targetState.get(SlabBlock.TYPE)));
                        }
                    }
                }
            }

        }
    }
}
