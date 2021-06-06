/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import com.handicraft.client.client.ClientMod;
import com.handicraft.client.util.CapeHolder;
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
public abstract class AbstractClientPlayerEntity_ModifyCape extends PlayerEntity implements CapeHolder {

    public AbstractClientPlayerEntity_ModifyCape(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    private Identifier cape;
    private boolean requestedCape;

    @Overwrite
    @Nullable
    public Identifier getCapeTexture() {
        if (!requestedCape) {
            ClientMod.requestCape(getUuid());
        }
        return cape;
    }

    @Override
    public void setCape(Identifier cape) {
        this.cape = cape;
        requestedCape = true;
    }

    @Override
    public void resetCape() {
        requestedCape = false;
    }
}
