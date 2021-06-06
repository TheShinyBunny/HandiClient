/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelper_HasFrostOrHeatWalker {

    @Shadow
    public static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
        return 0;
    }

    @Inject(method = "hasFrostWalker",at = @At("HEAD"),cancellable = true)
    private static void hasHeatWalker(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (getEquipmentLevel(CommonMod.HEAT_WALKER,entity) > 0) cir.setReturnValue(true);
    }

}
