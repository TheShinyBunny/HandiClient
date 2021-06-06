/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntity.class)
public class Creeper_OnIgnite {

    @Inject(method = "interactMob",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/mob/CreeperEntity;ignite()V"))
    private void onIgnite(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Objectives.IGNITE_CREEPER.trigger(player,1);
    }

}
