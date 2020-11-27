/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManager_SendAlwaysSnow {

    @Inject(method = "onPlayerConnect",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/PlayerManager;sendCommandTree(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void sendAlwaysSnow(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        boolean b = player.world.getGameRules().getBoolean(CommonMod.DO_ALWAYS_SNOW);
        player.networkHandler.sendPacket(new GameStateChangeS2CPacket(CommonMod.ALWAYS_SNOW_CHANGED,b ? 1 : 0));
    }

}
