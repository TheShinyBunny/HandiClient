/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTracker_Disable {

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion",at = @At("HEAD"),cancellable = true)
    private void disable(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!owner.server.getOverworld().getGameRules().getBoolean(CommonMod.ADVANCEMENT_GAME_RULE)) {
            cir.setReturnValue(false);
        }
    }

}
