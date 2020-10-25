/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectibleType<T extends Collectible> {

    private static final Map<String,CollectibleType<?>> types = new HashMap<>();
    private static final List<CollectibleType<?>> byIndex = new ArrayList<>();

    public static final CollectibleType<Cape> CAPE = new CollectibleType<>("cape");
    public static final CollectibleType<ParticleTrail> PARTICLE = new CollectibleType<>("particle");
    private final String id;

    public CollectibleType(String id) {
        this.id = id;
        types.put(id,this);
        byIndex.add(this);
    }

    public static CollectibleType<?> get(String k) {
        return types.get(k);
    }

    public static CollectibleType<?> byIndex(int i) {
        return byIndex.get(i);
    }

    public static List<CollectibleType<?>> all() {
        return new ArrayList<>(types.values());
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return byIndex.indexOf(this);
    }

    public Text getName() {
        return new TranslatableText("collectible.type." + id);
    }
}
