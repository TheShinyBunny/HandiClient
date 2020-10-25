/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.challenge.client.ClientChallenge;
import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.util.HandiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ChallengeInstance {

    private Challenge<?> challenge;
    private int completeCount;

    public ChallengeInstance(Challenge<?> challenge, int completeCount) {
        this.challenge = challenge;
        this.completeCount = completeCount;
    }

    public static ChallengeInstance fromNBT(CompoundTag tag, Function<Integer,Challenge<?>> challengeGetter) {
        Challenge<?> c = challengeGetter.apply(tag.getInt("challenge"));
        int count = tag.getInt("completeCount");
        return new ChallengeInstance(c,count);
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("challenge",challenge.getId());
        tag.putInt("completeCount",completeCount);
        return tag;
    }

    public void trigger(int times) {
        completeCount+=times;
        if(completeCount > challenge.getMinCount()) {
            completeCount = challenge.getMinCount();
        }
    }

    public Challenge<?> getChallenge() {
        return challenge;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public boolean isCompleted() {
        return completeCount >= challenge.getMinCount();
    }

    public void writePacket(PacketByteBuf buf) {
        buf.writeVarInt(completeCount);
        buf.writeVarInt(challenge.getId());
    }

    public void writeFullPacket(PacketByteBuf buf) {
        buf.writeVarInt(completeCount);
        challenge.writePacket(buf);
    }

    public static ChallengeInstance readFullPacket(PacketByteBuf buf) {
        int count = buf.readVarInt();
        Challenge<?> challenge = ClientChallenge.readPacket(buf);
        return new ChallengeInstance(challenge,count);
    }

    @Override
    public String toString() {
        return "ChallengeInstance{" +
                "challenge=" + challenge +
                ", completeCount=" + completeCount +
                '}';
    }

    public void setCompleteCount(int completes) {
        this.completeCount = completes;
    }
}
