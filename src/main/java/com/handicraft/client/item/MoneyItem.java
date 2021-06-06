/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.item.Item;

public class MoneyItem extends Item implements MoneyLike {
    private int value;
    private boolean canPurchase;

    public MoneyItem(Settings settings, int value, boolean canPurchase) {
        super(settings);
        this.value = value;
        this.canPurchase = canPurchase;
    }

    @Override
    public int getRubyValue() {
        return value;
    }

    @Override
    public boolean canPurchase() {
        return canPurchase;
    }
}
