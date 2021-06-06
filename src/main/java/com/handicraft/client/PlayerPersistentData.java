/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client;

import com.handicraft.client.challenge.PlayerChallenges;
import com.handicraft.client.collectibles.Cape;
import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.CollectibleType;
import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.util.PersistentData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PlayerPersistentData implements PersistentData {

    private static final Map<PlayerEntity,PlayerPersistentData> data = new HashMap<>();

    private PlayerEntity player;

    public int storedXP;
    public PlayerChallenges challenges;
    public PlayerCollectibles collectibles;

    private PlayerPersistentData(PlayerEntity player) {
        this.player = player;
        this.challenges = new PlayerChallenges((ServerPlayerEntity) player);
        this.collectibles = PlayerCollectibles.load(player.getServer(),player.getUuid());
    }

    public static PlayerPersistentData of(PlayerEntity player) {
        return data.computeIfAbsent(player,PlayerPersistentData::new);
    }

    @Override
    public void read(NbtCompound tag) {
        if (tag.contains("BukkitValues")) {
            storedXP = tag.getCompound("BukkitValues").getInt("handicraft:enderchest/stored_xp");
            tag.remove("BukkitValues");
        } else {
            storedXP = tag.getInt("storedXP");
        }
    }

    @Override
    public void write(NbtCompound tag) {
        tag.putInt("storedXP",storedXP);
    }

    public void onSpawn() {
        challenges.init();
        collectibles.sendUpdate(player);
    }

    public void saveExtraData() {
        challenges.saveToFile(player.getServer());
        collectibles.save(player.getServer(),player.getUuid());
    }

    public Identifier getCape() {
        Collectible selected = collectibles.getSelected(CollectibleType.CAPE);
        return selected instanceof Cape ? selected.getDisplayTexture() : null;
    }
}
