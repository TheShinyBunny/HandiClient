/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;

@Mixin(FishingBobberEntity.class)
public abstract class FishingRod_OnFish {

    @Shadow @Nullable public abstract PlayerEntity getPlayerOwner();

    @Redirect(method = "use",at = @At(value = "INVOKE",target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private Object onFish(Iterator<ItemStack> iterator) {
        ItemStack stack = iterator.next();
        Objectives.FISH.trigger(getPlayerOwner(),i->i.test(stack),1);
        return stack;
    }

}
