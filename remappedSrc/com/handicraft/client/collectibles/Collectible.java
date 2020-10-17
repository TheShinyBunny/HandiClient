/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Collectible {

    //public static final Registry<Collectible> REGISTRY = FabricRegistryBuilder.createSimple(Collectible.class,new Identifier("hcclient:collectibles")).buildAndRegister();

    private CollectibleType<?> type;

    public Collectible(CollectibleType<?> type) {
        this.type = type;
    }

    public CollectibleType<?> getType() {
        return type;
    }
}
