/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BredAnimalsCriterion.class)
public class BredAnimalsTrigger_OnBreed {

    @Inject(method = "trigger",at = @At("HEAD"))
    private void onBreed(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity, CallbackInfo ci) {
        Objectives.BREED.trigger(serverPlayerEntity,i->i.test(animalEntity),1);
    }

}
