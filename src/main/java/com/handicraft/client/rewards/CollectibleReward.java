/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.collectibles.Collectible;

public abstract class CollectibleReward<C extends Collectible> extends Reward {

    protected C collectible;

    public CollectibleReward(String name, int level, int textureHeight, C collectible) {
        super(name, level, textureHeight);
        this.collectible = collectible;
    }

    public C getCollectible() {
        return collectible;
    }
}
