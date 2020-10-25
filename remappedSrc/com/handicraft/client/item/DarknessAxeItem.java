/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemGroup;

public class DarknessAxeItem extends AxeItem {
    protected DarknessAxeItem() {
        super(DarknessMaterial.INSTANCE,6,-2.4f,new Settings().group(ItemGroup.TOOLS).maxCount(1).fireproof());
    }
}
