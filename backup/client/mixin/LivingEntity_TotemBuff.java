/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_TotemBuff extends Entity {

    public LivingEntity_TotemBuff(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "tryUseTotem",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/damage/DamageSource;isOutOfWorld()Z"))
    private boolean outOfWorld(DamageSource damageSource) {
        return false;
    }

    @ModifyVariable(method = "tryUseTotem",at = @At("STORE"),ordinal = 0)
    private ItemStack modifyItem(ItemStack stack) {
        if ((Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object)this;
            for (ItemStack s : player.inventory.main) {
                if (s.getItem() == Items.TOTEM_OF_UNDYING) {
                    return s.split(1);
                }
            }
        }
        return stack;
    }

    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void tpOutOfVoid(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source.isOutOfWorld()) {
            BlockPos dest = null;
            for (BlockPos pos : BlockPos.iterateOutwards(this.getBlockPos(),50,1,50)) {
                BlockPos top = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,pos);
                if (isSafe(top)) {
                    dest = top;
                    break;
                }
            }
            if (dest != null) {
                teleport(dest.getX() + 0.5,dest.getY(),dest.getZ() + 0.5);
                fallDistance = 0;
            }
        }
    }

    private boolean isSafe(BlockPos pos) {
        System.out.println("checking safety of: " + world.getBlockState(pos) + " at " + pos);
        BlockState ground = world.getBlockState(pos.down());
        boolean b = !ground.isAir() && ground.getMaterial().isSolid() && world.isAir(pos);
        if (b) {
            System.out.println("that was safe! block below is " + ground);
        }
        return b;
    }

}
