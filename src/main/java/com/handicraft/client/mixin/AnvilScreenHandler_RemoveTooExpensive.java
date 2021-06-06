/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandler_RemoveTooExpensive {

    @Shadow @Final private Property levelCost;

    @Inject(method = "updateResult",at = @At(value = "INVOKE",target = "Lnet/minecraft/screen/Property;get()I",ordinal = 1,shift = At.Shift.BEFORE))
    private void modifyCost(CallbackInfo ci) {
        if (levelCost.get() >= 40) {
            levelCost.set(39);
        }
    }

    @Overwrite
    public static int getNextCost(int cost) {
        if (cost > 40) return cost;
        return cost * 2 + 1;
    }
}
