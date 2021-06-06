/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public interface ObjectiveInstance {

    Text getText(int count);

    ItemStack getIcon();

    ObjectiveType<?> getType();

    void toNBT(NbtCompound tag);
}
