/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "net.minecraft.enchantment.EnchantmentTarget$11")
public class BowEnchantTarget_Crossbow {

    @Overwrite
    public boolean isAcceptableItem(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem;
    }
}
