/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.client;

import com.handicraft.client.challenge.Challenge;
import com.handicraft.client.challenge.ChallengeInstance;
import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.challenge.objectives.ObjectiveType;
import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class ClientChallenge<I extends ObjectiveInstance> extends Challenge<I> {

    private Text text;
    private ItemStack icon;

    public ClientChallenge(int id, ObjectiveType<I> objective, int minCount, Text text, ItemStack icon) {
        super(id,objective, minCount);
        this.text = text;
        this.icon = icon;
    }

    public static <T extends ObjectiveInstance> ClientChallenge<T> readPacket(PacketByteBuf buf) {
        int id = buf.readVarInt();
        int count = buf.readVarInt();
        ObjectiveType<T> type = Objectives.REGISTRY.get(buf.readVarInt());
        Text text = buf.readText();
        ItemStack icon = buf.readItemStack();
        return new ClientChallenge<>(id,type,count,text,icon);
    }

    public Text getText() {
        return text;
    }

    @Override
    public ChallengeInstance createNewInstance() {
        return new ChallengeInstance(this,0);
    }

    public ItemStack getIcon() {
        return icon;
    }
}
