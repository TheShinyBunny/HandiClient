/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class Entity_TeleportToDarkness {

    @Shadow public World world;

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public float yaw;

    @Shadow public float pitch;

    @Inject(method = "getTeleportTarget",at = @At("HEAD"),cancellable = true)
    private void tpToDarkness(ServerWorld destination, CallbackInfoReturnable<@Nullable TeleportTarget> cir) {
        if (world.getRegistryKey() == World.OVERWORLD && destination.getRegistryKey() == CommonMod.DARKNESS_KEY) {
            destination.getChunk(BlockPos.ORIGIN);
            BlockPos pos = destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,BlockPos.ORIGIN);
            Vec3d vec = new Vec3d(pos.getX() + 0.5,pos.getY(),pos.getZ() + 0.5);
            cir.setReturnValue(new TeleportTarget(vec,getVelocity(),yaw,pitch));
        } else if (world.getRegistryKey() == CommonMod.DARKNESS_KEY && destination.getRegistryKey() == World.OVERWORLD) {
            BlockPos pos = destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());
            cir.setReturnValue(new TeleportTarget(Vec3d.ofBottomCenter(pos),getVelocity(),yaw,pitch));
        }
    }



}
