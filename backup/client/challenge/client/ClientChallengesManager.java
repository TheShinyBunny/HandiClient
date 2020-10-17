/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.client;

import com.handicraft.client.challenge.Challenge;
import com.handicraft.client.challenge.ChallengeInstance;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientChallengesManager {

    private static boolean isDefaultLoaded;
    private static List<ChallengeInstance> challenges = new ArrayList<>();

    public static void setChallenges(List<ChallengeInstance> newChallenges, boolean isDefault) {
        isDefaultLoaded = isDefault;
        challenges = newChallenges;
    }

    public static List<ChallengeInstance> getChallenges() {
        return challenges;
    }

    private static ChallengeInstance getInstance(UUID id) {
        return challenges.stream().filter(c->c.getChallenge().getId().equals(id)).findFirst().orElse(null);
    }

    public static ChallengeInstance updateProgress(PacketByteBuf buf) {
        int completes = buf.readVarInt();
        UUID id = buf.readUuid();
        ChallengeInstance i = getInstance(id);
        if (i != null) {
            System.out.println("updated progress: " + completes + "/" + i.getChallenge().getMinCount() + " of " + i.getChallenge());
            i.setCompleteCount(completes);
        }
        return i;
    }

    public static void completed(PacketByteBuf buf) {
        System.out.println("RECEIVED COMPLETED");
        ChallengeInstance i = updateProgress(buf);
        if (i == null) return;
        if (i.isCompleted()) {
            System.out.println("completed");
            ChallengeToastHud.INSTANCE.add(i.getChallenge());
        } else {
            System.err.println("NOT ACTUALLY COMPLETED CHALLENGE!!");
        }
    }

    private static File getSaveFile() {
        MinecraftClient client = MinecraftClient.getInstance();
        return new File(client.runDirectory,"challenges.dat");
    }

    public static boolean loadDefault() {
        if (isDefaultLoaded) return true;
        File f = getSaveFile();
        if (!f.exists()) return false;
        try {
            CompoundTag tag = NbtIo.read(f);
            ListTag list = tag.getList("challenges", NbtType.COMPOUND);
            challenges.clear();
            for (Tag t : list) {
                if (t instanceof CompoundTag) {
                    challenges.add(ChallengeInstance.fromNBT((CompoundTag) t,true));
                }
            }
            isDefaultLoaded = true;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveDefault() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (ChallengeInstance i : challenges) {
            list.add(i.toNBT());
        }
        tag.put("challenges",list);
        try {
            NbtIo.write(tag,getSaveFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        challenges.clear();
    }

    public static void update(List<Challenge<?>> challenges) {
        for (Challenge<?> c : challenges) {
            if (getInstance(c.getId()) == null) {
                ClientChallengesManager.challenges.add(new ChallengeInstance(c,0));
            }
        }
        ClientChallengesManager.challenges.removeIf(c->challenges.stream().noneMatch(ch->ch.getId().equals(c.getChallenge().getId())));
        saveDefault();
    }
}
