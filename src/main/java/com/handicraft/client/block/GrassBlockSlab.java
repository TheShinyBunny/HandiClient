/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

import java.util.Random;

public class GrassBlockSlab extends SlabBlock {
    public GrassBlockSlab(Settings settings) {
        super(settings);
    }

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

    private static boolean canSpread(BlockState state, WorldView worldView, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return canSurvive(state, worldView, pos) && !worldView.getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!canSurvive(state, world, pos) && state.get(TYPE) != SlabType.BOTTOM) {
            world.setBlockState(pos, ModBlocks.DIRT_SLAB.getDefaultState().with(TYPE,state.get(TYPE)));
        } else {
            if (world.getLightLevel(pos.up()) >= 9) {
                BlockState blockState = this.getDefaultState();

                for(int i = 0; i < 4; ++i) {
                    BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (canSpread(blockState, world, blockPos)) {
                        BlockState targetState = world.getBlockState(blockPos);
                        if (targetState.isOf(Blocks.DIRT)) {
                            world.setBlockState(blockPos, Blocks.GRASS_BLOCK.getDefaultState().with(GrassBlock.SNOWY, world.getBlockState(blockPos.up()).isOf(Blocks.SNOW)));
                        } else if (targetState.isOf(ModBlocks.DIRT_SLAB)) {
                            world.setBlockState(blockPos, ModBlocks.GRASS_BLOCK_SLAB.getDefaultState().with(SlabBlock.TYPE,targetState.get(SlabBlock.TYPE)));
                        }
                    }
                }
            }
        }
    }

}
