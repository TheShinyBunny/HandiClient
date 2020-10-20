/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.entity.mob.BlazeEntity$ShootFireballGoal")
public class BlazeFireballGoal_DarkBlaze {

    @ModifyConstant(method = "tick",constant = @Constant(intValue = 4))
    private int modifyFireballCount(int c) {
        return 6;
    }

}
