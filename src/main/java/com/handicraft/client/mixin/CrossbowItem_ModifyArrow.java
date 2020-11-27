/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrossbowItem.class)
public class CrossbowItem_ModifyArrow {

    @Redirect(method = "createArrow",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private static PersistentProjectileEntity makeArrow(ArrowItem arrowItem, World world, ItemStack stack, LivingEntity shooter) {
        PersistentProjectileEntity arrow = arrowItem.createArrow(world, stack, shooter);
        int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
        if (j > 0) {
            arrow.setDamage(arrow.getDamage() + (double)j * 0.5D + 0.5D);
        }

        int k = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
        if (k > 0) {
            arrow.setPunch(k);
        }

        if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
            arrow.setOnFireFor(100);
        }
        return arrow;
    }

}
