/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ZombieVillagerEntity.class)
public class ZombieVillager_OnCure {

    @Redirect(method = "finishConversion",at = @At(value = "INVOKE",target = "Lnet/minecraft/advancement/criterion/CuredZombieVillagerCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/mob/ZombieEntity;Lnet/minecraft/entity/passive/VillagerEntity;)V"))
    private void onCure(CuredZombieVillagerCriterion curedZombieVillagerCriterion, ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
        Objectives.CURE_ZOMBIE.trigger(player,1);
        curedZombieVillagerCriterion.trigger(player, zombie, villager);
    }

}
