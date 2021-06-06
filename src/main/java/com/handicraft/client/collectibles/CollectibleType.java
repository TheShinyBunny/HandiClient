/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.List;

public enum CollectibleType {
    CAPE,
    PARTICLE;


    public static CollectibleType get(String id) {
        try {
            return valueOf(id.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }

    public static CollectibleType byIndex(int i) {
        return values()[i];
    }

    public static List<CollectibleType> all() {
        return Arrays.asList(values());
    }

    public String getId() {
        return name().toLowerCase();
    }

    public Text getName() {
        return new TranslatableText("collectible.type." + getId());
    }
}
