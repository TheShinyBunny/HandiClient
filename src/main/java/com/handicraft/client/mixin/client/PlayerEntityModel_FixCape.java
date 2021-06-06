/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import com.handicraft.client.client.FakePlayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModel_FixCape {

    @Redirect(method = "setAngles",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean isRenderingCape(ItemStack itemStack, LivingEntity entity) {
        if (entity.isFallFlying()) return itemStack.isEmpty();
        if (entity instanceof FakePlayer) return false;
        return itemStack.isEmpty() || itemStack.getItem() == Items.ELYTRA;
    }

}
