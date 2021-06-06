/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntity_ComeBackFromDarkness extends PlayerEntity {

    public ServerPlayerEntity_ComeBackFromDarkness(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Shadow public abstract ServerWorld getServerWorld();

    @Shadow public boolean notInAnyWorld;

    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Inject(method = "moveToWorld",at = @At("HEAD"),cancellable = true)
    private void moveToOverworld(ServerWorld destination, CallbackInfoReturnable<@Nullable Entity> cir) {
        if (getServerWorld().getRegistryKey() == CommonMod.DARKNESS_KEY && destination.getRegistryKey() == World.OVERWORLD) {
            detach();
            getServerWorld().removePlayer((ServerPlayerEntity)(Object)this,RemovalReason.CHANGED_DIMENSION);
            if (!this.notInAnyWorld) {
                this.notInAnyWorld = true;
                this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_WON, 0.0F));
            }
            cir.setReturnValue(this);
        }
    }

}
