/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;

public class DarknessSword extends SwordItem {
    public DarknessSword() {
        super(DarknessToolMaterial.INSTANCE, 3, -3, new Settings().group(ItemGroup.COMBAT));
    }
}
