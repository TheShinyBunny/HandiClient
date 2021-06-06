/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.PlayerPersistentData;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntity_ModifyCape extends PlayerEntity {

    public AbstractClientPlayerEntity_ModifyCape(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }


    @Overwrite
    @Nullable
    public Identifier getCapeTexture() {
        return PlayerPersistentData.of(this).getCape();
    }
}
