/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.challenge.PlayerChallenges;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManager_SavePlayer {

    @Inject(method = "savePlayerData",at = @At("TAIL"))
    private void save(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerChallenges challenges = PlayerPersistentData.of(player).challenges;
        challenges.saveToFile(player.server);
    }

}
