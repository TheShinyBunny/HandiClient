/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import com.handicraft.client.challenge.client.ClientChallengesManager;
import com.handicraft.client.collectibles.ClientCollectibleCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClient_Disconnect {

    @Shadow public abstract boolean isInSingleplayer();

    @Shadow public abstract @Nullable ClientPlayNetworkHandler getNetworkHandler();

    @Shadow @Nullable public ClientWorld world;

    @Shadow private @Nullable ServerInfo currentServerEntry;

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",at = @At("HEAD"))
    private void disconnecting(Screen screen, CallbackInfo ci) {
        if (world != null && isInSingleplayer()) {
            ClientChallengesManager.clear();
            ClientCollectibleCache.clear();
        }
    }

}
