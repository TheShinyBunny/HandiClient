/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.function.Supplier;

public class DarkFireBlock extends AbstractFireBlock {
    private final Supplier<Tag<Block>> baseTag;

    public DarkFireBlock(Settings settings, Supplier<Tag<Block>> baseTag) {
        super(settings, 2f);
        this.baseTag = baseTag;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return this.canPlaceAt(state, world, pos) ? this.getDefaultState() : Blocks.AIR.getDefaultState();
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isDarkFireBase(world.getBlockState(pos.down()).getBlock());
    }

    public boolean isDarkFireBase(Block block) {
        return baseTag.get().contains(block);
    }

    @Override
    protected boolean isFlammable(BlockState state) {
        return true;
    }
}
