/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class Item_ModifyMaxCounts {

    @Inject(method = "getMaxCount",at = @At("HEAD"),cancellable = true)
    private void maxCount(CallbackInfoReturnable<Integer> cir) {
        if ((Object)this instanceof MinecartItem) cir.setReturnValue(16);
        if ((Object)this instanceof BoatItem) cir.setReturnValue(16);
    }

}
