/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.minecraft.util.Identifier;

public class Cape extends Collectible {
    private Identifier id;

    public Cape(Identifier id) {
        super(CollectibleType.CAPE);
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}
