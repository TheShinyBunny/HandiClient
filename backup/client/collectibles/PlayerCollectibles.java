/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import com.handicraft.client.PlayerPersistentData;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerCollectibles {

    private Map<CollectibleType,Collectible> selected;
    private List<Collectible> owned;

    public PlayerCollectibles() {
        selected = new HashMap<>();
        owned = new ArrayList<>();
    }

    public static void give(PlayerEntity player, Collectible c) {
        PlayerPersistentData.of(player).collectibles.add(c);
    }

    private void add(Collectible collectible) {

    }

    public List<Collectible> getOwned() {
        return owned;
    }

    public List<Collectible> getOwned(CollectibleType type) {
        return owned.stream().filter(c->c.getType() == type).collect(Collectors.toList());
    }

    public Collectible getSelected(CollectibleType type) {
        return selected.get(type);
    }

}
