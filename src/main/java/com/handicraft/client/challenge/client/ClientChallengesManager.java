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
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientChallengesManager {


    private static List<ChallengeInstance> challenges = new ArrayList<>();

    public static void setChallenges(List<ChallengeInstance> newChallenges) {
        challenges = newChallenges;
    }

    public static List<ChallengeInstance> getChallenges() {
        return challenges;
    }

    private static ChallengeInstance getInstance(int id) {
        return challenges.stream().filter(c->c.getChallenge().getId() == id).findFirst().orElse(null);
    }

    public static ChallengeInstance updateProgress(PacketByteBuf buf) {
        int completes = buf.readVarInt();
        int id = buf.readVarInt();
        ChallengeInstance i = getInstance(id);
        if (i != null) {
            //System.out.println("updated progress: " + completes + "/" + i.getChallenge().getMinCount() + " of " + i.getChallenge());
            i.setCompleteCount(completes);
            MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.GAME_INFO,new TranslatableText("challenges.progress_update",i.getChallenge().getText(),completes,i.getChallenge().getMinCount()).formatted(Formatting.GOLD),MinecraftClient.getInstance().player.getUuid());
        }
        return i;
    }

    public static void completed(PacketByteBuf buf) {
        ChallengeInstance i = updateProgress(buf);
        if (i == null) return;
        if (i.isCompleted()) {
            ChallengeToastHud.INSTANCE.add(i.getChallenge());
        }
    }

    public static void clear() {
        challenges.clear();
    }

    public static void update(List<Challenge<?>> challenges) {
        for (Challenge<?> c : challenges) {
            if (getInstance(c.getId()) == null) {
                ClientChallengesManager.challenges.add(c.createNewInstance());
            }
        }
        ClientChallengesManager.challenges.removeIf(c->challenges.stream().noneMatch(ch->ch.getId() == c.getChallenge().getId()));
    }
}
