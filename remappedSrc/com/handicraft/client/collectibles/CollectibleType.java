/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

public class CollectibleType<T extends Collectible> {

    public static final CollectibleType<Cape> CAPE = new CollectibleType<>();
    public static final CollectibleType<ParticleTrail> PARTICLE = new CollectibleType<>();
    public static final CollectibleType<Emote> EMOTE = new CollectibleType<>();

    public CollectibleType() {
    }
}
