/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntity_BlockDamage {

    @Inject(method = "damage",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private void onBlock(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof ServerPlayerEntity) {
            Objectives.BLOCK_DAMAGE.trigger((ServerPlayerEntity)(Object)this,i->i.test(source),1);
        }
    }

}
