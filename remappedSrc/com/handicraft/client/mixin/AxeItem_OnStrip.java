/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItem_OnStrip {

    @Inject(method = "useOnBlock",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private void onStrip(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        Objectives.STRIP_WOOD.trigger(context.getPlayer(),i->i.test(context.getWorld().getBlockState(context.getBlockPos()).getBlock()),1);
    }

}
