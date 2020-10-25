/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceOutputSlot.class)
public class FurnaceOutput_OnSmelt {

    @Shadow @Final private PlayerEntity player;

    @Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V",at = @At(value = "INVOKE",target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;dropExperience(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void onSmelt(ItemStack stack, CallbackInfo ci) {
        Objectives.SMELT.trigger(player,i->i.test(stack),stack.getCount());
    }

}
