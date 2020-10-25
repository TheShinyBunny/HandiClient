/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.TradeOffer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantEntity.class)
public class Villager_OnTrade {

    @Shadow private @Nullable PlayerEntity customer;

    @Inject(method = "trade",at = @At(value = "INVOKE",target = "Lnet/minecraft/advancement/criterion/VillagerTradeCriterion;handle(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/passive/MerchantEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void onTrade(TradeOffer offer, CallbackInfo ci) {
        Objectives.TRADE.trigger(customer,1);
    }

}
