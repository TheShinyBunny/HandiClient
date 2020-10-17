/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.challenge.client.ClientChallenge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class ChallengeInstance {

    private Challenge<?> challenge;
    private int completeCount;

    public ChallengeInstance(Challenge<?> challenge, int completeCount) {
        this.challenge = challenge;
        this.completeCount = completeCount;
    }

    public static ChallengeInstance fromNBT(CompoundTag tag, boolean client) {
        Challenge<?> c = client ? ClientChallenge.fromNBT(tag.getCompound("challenge")) : ServerChallenge.fromNBT(tag.getCompound("challenge"));
        int count = tag.getInt("completeCount");
        return new ChallengeInstance(c,count);
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("challenge",challenge.toNBT());
        tag.putInt("completeCount",completeCount);
        return tag;
    }

    public void trigger() {
        completeCount++;
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
        buf.writeUuid(challenge.getId());
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

    public void setCompleteCount(int completes) {
        this.completeCount = completes;
    }
}
