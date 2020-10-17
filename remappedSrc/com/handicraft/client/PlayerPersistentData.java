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
    private boolean capeLoaded;
    public Identifier cape;
    public PlayerChallenges challenges;
    public PlayerCollectibles collectibles;

    private PlayerPersistentData(PlayerEntity player) {
        this.player = player;
        this.challenges = new PlayerChallenges((ServerPlayerEntity) player);
        this.collectibles = new PlayerCollectibles();
    }

    public static PlayerPersistentData of(PlayerEntity player) {
        return data.computeIfAbsent(player,PlayerPersistentData::new);
    }

    @Override
    public void read(CompoundTag tag) {
        try {
            Dynamic<?> res = CommonMod.DATA_FIXER.update(TypeReferences.PLAYER, new Dynamic<>(NbtOps.INSTANCE, tag), 0, CommonMod.DATA_VERSION);
            CommonMod.updateNBT(tag, (CompoundTag) res.getValue());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (tag.contains("cape")) {
            cape = Identifier.CODEC.decode(NbtOps.INSTANCE,tag.get("cape")).result().map(Pair::getFirst).orElse(null);
        }
        cape = new Identifier("hcclient:textures/capes/ruby.png");
        storedXP = tag.getInt("storedXP");

        //challenges.read(tag.getList("challenges", NbtType.COMPOUND));
    }

    @Override
    public void write(CompoundTag tag) {
        if (cape != null) {
            tag.putString("cape", cape.toString());
        }
        tag.putInt("storedXP",storedXP);
        /*ListTag list = new ListTag();
        challenges.write(list);
        tag.put("challenges",list);*/
    }

    public Identifier getCape() {
        if (!capeLoaded && player.world.isClient) {
            ClientMod.requestCape(player.getUuid());
        }
        return cape;
    }

    public void setCape(Identifier cape) {
        this.cape = cape;
        this.capeLoaded = true;
    }

    public void onSpawn() {
        challenges.init();
    }
}
