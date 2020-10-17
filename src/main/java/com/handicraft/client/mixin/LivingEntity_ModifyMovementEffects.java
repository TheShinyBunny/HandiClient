/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.enchantments.FarmingFeetEnchantment;
import com.handicraft.client.enchantments.HeatWalkerEnchantment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_ModifyMovementEffects extends Entity {

    private static final UUID PATH_BLOCK_BOOST_ID = UUID.fromString("249-3248d-24425-2589a-545");

    @Shadow protected abstract boolean method_29500(BlockState blockState);

    @Shadow public @Nullable abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    public LivingEntity_ModifyMovementEffects(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "baseTick",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/LivingEntity;getBlockPos()Lnet/minecraft/util/math/BlockPos;",shift = At.Shift.BEFORE))
    private void activateEnchantments(CallbackInfo ci) {
        int i = EnchantmentHelper.getEquipmentLevel(CommonMod.HEAT_WALKER,(LivingEntity)(Object)this);
        if (i > 0) {
            HeatWalkerEnchantment.heatLava((LivingEntity)(Object)this,this.world,getBlockPos(),i);
        }
        int j = EnchantmentHelper.getEquipmentLevel(CommonMod.FARMING_FEET,(LivingEntity)(Object)this);
        if (j > 0) {
            FarmingFeetEnchantment.farm((LivingEntity)(Object)this);
        }
    }

    @Inject(method = "applyMovementEffects",at = @At("HEAD"))
    private void movementEffects(BlockPos pos, CallbackInfo ci) {

        if (method_29500(getLandingBlockState())) {
            EntityAttributeInstance entityAttributeInstance = getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (entityAttributeInstance != null) {
                if (entityAttributeInstance.getModifier(PATH_BLOCK_BOOST_ID) != null) {
                    entityAttributeInstance.removeModifier(PATH_BLOCK_BOOST_ID);
                }
            }
        }

        if (!getLandingBlockState().isAir()) {
            if (world.getBlockState(getVelocityAffectingPos()).isOf(Blocks.GRASS_PATH)) {
                EntityAttributeInstance entityAttributeInstance = getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (entityAttributeInstance == null) {
                    return;
                }
                entityAttributeInstance.addTemporaryModifier(new EntityAttributeModifier(PATH_BLOCK_BOOST_ID, "Path block speed boost", 0.035f, EntityAttributeModifier.Operation.ADDITION));
            }
        }
    }

}
