/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.CommonMod;
import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.challenge.objectives.CountModifier;
import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.challenge.objectives.ObjectiveType;
import com.handicraft.client.challenge.objectives.Objectives;
import com.handicraft.client.util.HandiUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class ChallengesManager extends PersistentState {


    private List<ServerChallenge<?>> challenges;
    private long lastRestockTime;

    private ChallengesManager() {
        super("challenges");
        challenges = new ArrayList<>();
        registerListeners();
    }

    public static void registerListeners() {
        PlayerBlockBreakEvents.AFTER.register((world, playerEntity, blockPos, blockState, blockEntity) -> {
            if (!world.isClient) {
                Objectives.BREAK_BLOCK.trigger(playerEntity, blockState.getBlock(), playerEntity.getMainHandStack());
            }
        });

    }

    public void init() {
        System.out.println("INITIALIZED CHALLENGE MANAGER");
    }

    public static ChallengesManager get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(ChallengesManager::new,"challenges");
    }

    public void tick(MinecraftServer server) {
        Calendar now = Calendar.getInstance(CommonMod.TIME_ZONE);
        int today = now.get(Calendar.DAY_OF_WEEK);
        if (lastRestockTime > 0) {
            Calendar c = Calendar.getInstance(CommonMod.TIME_ZONE);
            c.setTimeInMillis(lastRestockTime);
            int last = c.get(Calendar.DAY_OF_WEEK);
            if (today == Calendar.SUNDAY)
            if (today < Calendar.FRIDAY && last != today && (today - last == 1 || today == Calendar.SUNDAY)) {
                restock(server);
            }
        } else if (today == Calendar.SUNDAY) {
            restock(server);
        }
    }

    public void restock(MinecraftServer server) {
        if (challenges.size() >= 15) {
            challenges.forEach(ServerChallenge::unregister);
            challenges.clear();
            markDirty();
            return;
        }
        ServerChallenge<?> challenge = generateChallenge(server.getOverworld().random);
        challenges.add(challenge);


        for (PlayerEntity p : server.getPlayerManager().getPlayerList()) {
            PlayerPersistentData.of(p).challenges.update(challenges);
        }
        lastRestockTime = System.currentTimeMillis();
        markDirty();
    }

    public <T extends ObjectiveInstance> ServerChallenge<T> generateChallenge(Random r) {
        ObjectiveType<T> obj = HandiUtils.getWeightedRandom(r,Objectives.REGISTRY.stream().collect(Collectors.toList()));
        CountModifier modifier = CountModifier.create(obj.getRandomBaseCount(r));
        T i = obj.generate(r,modifier);
        return new ServerChallenge<>(UUID.randomUUID(),obj,i,modifier.get());
    }

    @Override
    public void fromTag(CompoundTag tag) {
        System.out.println("LOADING CHALLENGES");
        lastRestockTime = tag.getLong("LastRestockTime");
        if (tag.contains("Challenges", NbtType.LIST)) {
            challenges.clear();
            ListTag list = tag.getList("Challenges",NbtType.COMPOUND);
            for (Tag t : list) {
                if (t instanceof CompoundTag) {
                    challenges.add(ServerChallenge.fromNBT((CompoundTag)t));
                }
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        System.out.println("SERIALIZING CHALLENGES");
        tag.putLong("LastRestockTime",lastRestockTime);
        ListTag list = new ListTag();
        for (ServerChallenge<?> c : challenges) {
            list.add(c.toNBT());
        }
        tag.put("Challenges",list);
        return tag;
    }

    public List<ServerChallenge<?>> getChallenges() {
        return challenges;
    }

    public ServerChallenge<?> get(UUID id) {
        return challenges.stream().filter(c->c.getId().equals(id)).findFirst().orElse(null);
    }

    public void reset() {
        challenges.forEach(ServerChallenge::unregister);
        challenges.clear();
        lastRestockTime = 0;
        markDirty();
    }
}
