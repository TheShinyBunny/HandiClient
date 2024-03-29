/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.PlayerPersistentData;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntity_PersistentData extends PlayerEntity {

    public PlayerEntity_PersistentData(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "readCustomDataFromTag",at = @At("TAIL"))
    private void read(CompoundTag tag, CallbackInfo ci) {
        PlayerPersistentData.of(this).read(tag);
    }

    @Inject(method = "writeCustomDataToTag",at = @At("TAIL"))
    private void write(CompoundTag tag, CallbackInfo ci) {
        PlayerPersistentData.of(this).write(tag);
    }

    @Inject(method = "onSpawn",at = @At("HEAD"))
    private void spawn(CallbackInfo ci) {
        PlayerPersistentData.of(this).onSpawn();
    }

}
