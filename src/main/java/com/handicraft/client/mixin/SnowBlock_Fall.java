/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(SnowBlock.class)
public class SnowBlock_Fall extends Block {

    @Shadow @Final public static IntProperty LAYERS;

    public SnowBlock_Fall(Settings settings) {
        super(settings);
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.getBlockTickScheduler().schedule(pos, this, 2);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        world.getBlockTickScheduler().schedule(pos, this, 2);
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState down = world.getBlockState(pos.down());
        if (down.isOf(this)) {
            int myLayer = state.get(LAYERS);
            int l = down.get(LAYERS);
            if (l < 8) {
                myLayer -= 8 - l;
                l = Math.min(8,l + myLayer);
                world.setBlockState(pos.down(),down.with(LAYERS,l));
                if (myLayer > 0 && myLayer < state.get(LAYERS)) {
                    world.setBlockState(pos,state.with(LAYERS,myLayer));
                }
            }
        } else {
            if (FallingBlock.canFallThrough(down) && pos.getY() >= 0) {
                FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, world.getBlockState(pos));
                world.spawnEntity(fallingBlockEntity);
            }
        }
    }

    @Overwrite
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11) {
            decreaseLayer(world,state,pos);
        } else if (!world.isRaining() && world.getBiome(pos).getPrecipitation() != Biome.Precipitation.SNOW && world.getLightLevel(pos) > 11) {
            decreaseLayer(world,state,pos);
        }
    }

    private void decreaseLayer(ServerWorld world, BlockState state, BlockPos pos) {
        int layer = state.get(LAYERS);
        if (layer > 1) {
            world.setBlockState(pos,state.with(LAYERS,layer - 1));
        } else {
            world.removeBlock(pos,false);
        }
    }

    @Overwrite
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        int i = state.get(LAYERS);
        if (context.getStack().getItem() == this.asItem() && i < 8) {
            if (context.canReplaceExisting()) {
             return context.getSide() == Direction.UP;
            } else {
             return true;
            }
        } else {
            return context.getStack().isEmpty() || i == 1;
        }
    }
}
