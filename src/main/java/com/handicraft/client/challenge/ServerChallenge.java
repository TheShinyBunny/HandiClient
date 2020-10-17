/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.challenge.objectives.ObjectiveType;
import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ServerChallenge<I extends ObjectiveInstance> extends Challenge<I> {

    private I data;

    public ServerChallenge(int id, ObjectiveType<I> objective, I data, int minCount) {
        super(id,objective,minCount);
        this.data = data;
    }

    public I getData() {
        return data;
    }

    @Override
    public Text getText() {
        return data.getText(minCount);
    }

    public void writePacket(PacketByteBuf buf) {
        buf.writeVarInt(getId());
        buf.writeVarInt(minCount);
        buf.writeVarInt(Objectives.REGISTRY.getRawId(objective));
        buf.writeText(getText());
        buf.writeItemStack(data.getIcon());
    }

    @Override
    public ChallengeInstance createNewInstance() {
        return new ChallengeInstance(this,0);
    }

}
