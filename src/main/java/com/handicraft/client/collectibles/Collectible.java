/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

/**
 * The base class of a collectible. Each collectible has its own instance extending this class.
 */
public abstract class Collectible {

    /**
     * The type of the collectible. May be null for collectibles that do not need to be selected in the locker.
     */
    private CollectibleType type;

    public Collectible(CollectibleType type) {
        this.type = type;
    }

    public CollectibleType getType() {
        return type;
    }

    public Identifier getId() {
        return Collectibles.REGISTRY.getId(this);
    }

    public Identifier getDisplayTexture() {
        return new Identifier("hcclient:textures/collectibles/" + getId().getPath() + ".png");
    }

    public String getTranslationKey() {
        return Util.createTranslationKey("collectible",getId());
    }
}
