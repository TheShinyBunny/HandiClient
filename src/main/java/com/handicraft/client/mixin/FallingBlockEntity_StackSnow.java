/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntity_StackSnow extends Entity {

    @Shadow private BlockState block;

    public FallingBlockEntity_StackSnow(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/block/FallingBlock;canFallThrough(Lnet/minecraft/block/BlockState;)Z"))
    private boolean canFallThrough(BlockState state) {
        if (this.block.isOf(Blocks.SNOW) && state.isOf(Blocks.SNOW)) {
            return false;
        }
        return FallingBlock.canFallThrough(state);
    }

    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/block/BlockState;contains(Lnet/minecraft/state/property/Property;)Z",shift = At.Shift.BEFORE))
    private void stackSnow(CallbackInfo ci) {
        BlockPos pos = getBlockPos();
        BlockState existing = world.getBlockState(pos);
        if (this.block.isOf(Blocks.SNOW) && existing.isOf(Blocks.SNOW)) {
            int existingLayer = existing.get(SnowBlock.LAYERS);
            if (existingLayer == 8) {
                return;
            }
            int down = existingLayer + this.block.get(SnowBlock.LAYERS);
            if (down > 8) {
                int extra = down - 8;
                down = 8;
                world.setBlockState(pos.up(),this.block.with(SnowBlock.LAYERS,extra));
            }
            this.block = this.block.with(SnowBlock.LAYERS,down);
        }
    }

}
