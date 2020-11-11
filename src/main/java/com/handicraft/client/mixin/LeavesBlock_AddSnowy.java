/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlock_AddSnowy extends Block {

    public LeavesBlock_AddSnowy(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    private void init(AbstractBlock.Settings settings, CallbackInfo ci) {
        setDefaultState(stateManager.getDefaultState().with(DISTANCE, 7).with(PERSISTENT, false).with(Properties.SNOWY,false));
    }

    @Shadow
    private static int getDistanceFromLog(BlockState state) {
        return 0;
    }

    @Shadow @Final public static IntProperty DISTANCE;

    @Shadow @Final public static BooleanProperty PERSISTENT;

    @Shadow
    private static BlockState updateDistanceFromLogs(BlockState state, WorldAccess world, BlockPos pos) {
        return null;
    }

    @Overwrite
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        int i = getDistanceFromLog(newState) + 1;
        if (i != 1 || state.get(DISTANCE) != i) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }

        if (direction == Direction.UP) {
            return state.with(Properties.SNOWY, newState.isOf(Blocks.SNOW_BLOCK) || newState.isOf(Blocks.SNOW));
        }

        return state;
    }

    @Overwrite
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState above = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
        return updateDistanceFromLogs(getDefaultState().with(PERSISTENT, true).with(Properties.SNOWY,above.isOf(Blocks.SNOW_BLOCK) || above.isOf(Blocks.SNOW)), ctx.getWorld(), ctx.getBlockPos());
    }

    @Overwrite
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.SNOWY,DISTANCE,PERSISTENT);
    }
}
