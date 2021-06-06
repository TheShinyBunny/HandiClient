/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.datafix;

import net.minecraft.nbt.NbtCompound;

public class RubyFix {

    public static void fix(NbtCompound tag) {
        String id = tag.getString("id");
        if (id.equalsIgnoreCase("minecraft:phantom_membrane")) {
            tag.putString("id","minecraft:ruby");
        }
        if (id.equalsIgnoreCase("minecraft:item_frame")) {
            int model = tag.getCompound("tag").getInt("CustomModelData");
            if (model == 1) {
                tag.getCompound("tag").remove("CustomModelData");
                if (tag.getCompound("tag").isEmpty()) {
                    tag.remove("tag");
                }
                tag.putString("id","minecraft:ruby_block");
            }
        }
    }
}
