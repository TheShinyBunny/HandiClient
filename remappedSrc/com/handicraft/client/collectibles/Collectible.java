/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.minecraft.util.Identifier;

public abstract class Collectible {


    private CollectibleType<?> type;

    public Collectible(CollectibleType<?> type) {
        this.type = type;
    }

    public CollectibleType<?> getType() {
        return type;
    }

    public Identifier getId() {
        return Collectibles.REGISTRY.getId(this);
    }
}
