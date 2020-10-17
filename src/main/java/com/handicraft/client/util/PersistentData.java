/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import net.minecraft.nbt.CompoundTag;

public interface PersistentData {

    void read(CompoundTag tag);

    void write(CompoundTag tag);

}
