/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.item.Item;

public class MoneyItem extends Item implements MoneyLike {
    private int value;

    public MoneyItem(Settings settings, int value) {
        super(settings);
        this.value = value;
    }

    @Override
    public int getRubyValue() {
        return value;
    }
}
