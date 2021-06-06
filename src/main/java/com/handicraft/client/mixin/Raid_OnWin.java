/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Raid.class)
public class Raid_OnWin {

    @Shadow private int badOmenLevel;

    @Redirect(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/advancement/criterion/LocationArrivalCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void onWin(LocationArrivalCriterion locationArrivalCriterion, ServerPlayerEntity player) {
        Objectives.WIN_RAID.trigger(player,i->i.test(badOmenLevel),1);
        locationArrivalCriterion.trigger(player);
    }

}
