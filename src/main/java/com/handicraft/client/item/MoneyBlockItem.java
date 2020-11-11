/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class MoneyBlockItem extends BlockItem implements MoneyLike {
    private MoneyLike base;

    public MoneyBlockItem(Block block, MoneyLike base, Settings settings) {
        super(block, settings);
        this.base = base;
    }

    @Override
    public int getRubyValue() {
        return base.getRubyValue() * 9;
    }
}
