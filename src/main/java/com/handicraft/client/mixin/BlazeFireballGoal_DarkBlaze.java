/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.entity.DarkBlazeEntity;
import net.minecraft.entity.mob.BlazeEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.entity.mob.BlazeEntity$ShootFireballGoal")
public class BlazeFireballGoal_DarkBlaze {

    @Shadow @Final private BlazeEntity blaze;

    @ModifyConstant(method = "tick()V",constant = @Constant(intValue = 4))
    private int modifyFireballCount(int c) {
        return blaze instanceof DarkBlazeEntity ? 6 : c;
    }

}
