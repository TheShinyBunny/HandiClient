/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerEntity_KillEntity {

    @Inject(method = "updateKilledAdvancementCriterion",at = @At("HEAD"))
    private void killedEntity(Entity killer, int score, DamageSource damageSource, CallbackInfo ci) {
        if (killer != (Object)this) {
            Objectives.KILL_ENTITY.trigger((PlayerEntity)(Object)this,killer,damageSource);
        }
    }


}
