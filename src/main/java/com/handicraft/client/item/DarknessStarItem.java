/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class DarknessStarItem extends Item {
    public DarknessStarItem() {
        super(new Settings().rarity(Rarity.EPIC).group(ItemGroup.MISC).fireproof());
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
