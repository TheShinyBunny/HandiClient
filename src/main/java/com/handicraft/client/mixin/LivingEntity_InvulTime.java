/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntity_InvulTime {

    @ModifyConstant(method = "damage",constant = @Constant(intValue = 20,ordinal = 0))
    private int invulTime(int v, DamageSource source, float amount) {
        return source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).isHolding(ModItems.DARKNESS_SWORD) ? CommonMod.invulTime() : v;
    }

}
