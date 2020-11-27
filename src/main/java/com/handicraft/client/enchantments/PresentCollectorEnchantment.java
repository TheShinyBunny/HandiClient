/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;

public class PresentCollectorEnchantment extends Enchantment {
    public PresentCollectorEnchantment() {
        super(Rarity.RARE,EnchantmentTarget.DIGGER,new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) && stack.getItem() instanceof HoeItem;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

}
