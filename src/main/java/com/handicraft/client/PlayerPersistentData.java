/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client;

import com.handicraft.client.challenge.PlayerChallenges;
import com.handicraft.client.challenge.client.ChallengeToastHud;
import com.handicraft.client.client.ClientMod;
import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.util.PersistentData;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
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
    public void read(CompoundTag tag) {
        if (tag.contains("BukkitValues")) {
            storedXP = tag.getCompound("BukkitValues").getInt("handicraft:enderchest/stored_xp");
            tag.remove("BukkitValues");
        } else {
            storedXP = tag.getInt("storedXP");
        }
    }

    @Override
    public void write(CompoundTag tag) {
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
}
