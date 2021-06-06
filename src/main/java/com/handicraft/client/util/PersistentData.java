/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import net.minecraft.nbt.NbtCompound;

public interface PersistentData {

    void read(NbtCompound tag);

    void write(NbtCompound tag);

}
