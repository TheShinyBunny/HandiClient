/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.minecraft.util.Identifier;

public class Cape extends Collectible {
    private String id;

    public Cape(String id) {
        super(CollectibleType.CAPE);
        this.id = id;
    }

    public Identifier getTextureId() {
        return new Identifier("hcclient:textures/capes/" + id + ".png");
    }
}
