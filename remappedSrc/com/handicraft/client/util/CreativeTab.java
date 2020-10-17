/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import net.minecraft.item.ItemGroup;

public enum CreativeTab {
    BLOCKS(ItemGroup.BUILDING_BLOCKS),
    DECORATION(ItemGroup.DECORATIONS),
    REDSTONE(ItemGroup.REDSTONE),
    TRANSPORTATION(ItemGroup.TRANSPORTATION),
    MISC(ItemGroup.MISC),
    FOOD(ItemGroup.FOOD),
    TOOLS(ItemGroup.TOOLS),
    COMBAT(ItemGroup.COMBAT),
    BREWING(ItemGroup.BREWING),
    NONE(null);

    private ItemGroup group;

    CreativeTab(ItemGroup group) {
        this.group = group;
    }

    public ItemGroup getGroup() {
        return group;
    }
}
