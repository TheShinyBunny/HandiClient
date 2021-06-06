/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.minecraft.util.Identifier;

public class Emote extends Collectible {
    private final Identifier emote;

    public Emote(Identifier emote) {
        super(null);
        this.emote = emote;
    }

    public Identifier getEmote() {
        return emote;
    }
}
