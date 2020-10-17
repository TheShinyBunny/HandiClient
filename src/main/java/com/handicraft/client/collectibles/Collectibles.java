/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import com.handicraft.client.CommonMod;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Collectibles {

    public static final Registry<Collectible> REGISTRY = FabricRegistryBuilder.createSimple(Collectible.class,new Identifier("hcclient:collectibles")).buildAndRegister();

    public static <C extends Collectible> C register(String id, C collectible) {
        return Registry.register(REGISTRY,new Identifier("hcclient",id),collectible);
    }

    public static final Cape BAT_CAPE = register("bat_cape",new Cape("bat"));
    public static final Cape PUMPKIN_CAPE = register("pumpkin_cape",new Cape("pumpkin"));
    public static final Cape RUBY_CAPE = register("ruby_cape",new Cape("ruby"));
    public static final ParticleTrail RUBY_TRAIL = register("ruby_trail",new ParticleTrail(CommonMod.RUBY_CONTRAIL));
    public static final ParticleTrail PUMPKIN_TRAIL = register("pumpkin_trail",new ParticleTrail(CommonMod.JACK_O_CONTRAIL_PARTICLE));
    public static final ParticleTrail HEROBRINE_TRAIL = register("herobrine_trail",new ParticleTrail(CommonMod.HEROBRINE_TRAIL));

}
