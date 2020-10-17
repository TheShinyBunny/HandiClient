/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.minecraft.particle.ParticleEffect;

public class ParticleTrail extends Collectible {
    private ParticleEffect effect;

    public ParticleTrail(ParticleEffect effect) {
        super(CollectibleType.PARTICLE);
        this.effect = effect;
    }

    public ParticleEffect getEffect() {
        return effect;
    }
}
