/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.challenge.objectives.ObjectiveType;
import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ServerChallenge<I extends ObjectiveInstance> extends Challenge<I> implements ObjectiveType.Listener<I> {

    private I data;

    public ServerChallenge(UUID id, ObjectiveType<I> objective, I data, int minCount) {
        super(id,objective,minCount);
        this.data = data;
        this.objective.addListener(this);
    }

    @Override
    public Text getText() {
        return data.getText(minCount);
    }

    @Override
    public void triggered(PlayerEntity player) {
        PlayerPersistentData.of(player).challenges.trigger(this);
    }

    @Override
    public I getInstance() {
        return data;
    }

    public void unregister() {
        this.objective.removeListener(this);
    }

    public void writePacket(PacketByteBuf buf) {
        buf.writeUuid(getId());
        buf.writeVarInt(Objectives.REGISTRY.getRawId(objective));
        buf.writeVarInt(minCount);
        buf.writeText(getText());
        ItemConvertible[] icons = data.getIcons();
        buf.writeVarInt(icons.length);
        for (ItemConvertible i : icons) {
            buf.writeVarInt(Item.getRawId(i.asItem()));
        }
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUuid("id",getId());
        tag.putString("type",Objectives.REGISTRY.getId(objective).toString());
        CompoundTag data = new CompoundTag();
        this.data.write(data);
        tag.put("data",data);
        tag.putInt("count",minCount);
        return tag;
    }

    public static <T extends ObjectiveInstance> ServerChallenge<T> fromNBT(CompoundTag tag) {
        UUID id = tag.getUuid("id");
        ObjectiveType<T> type = Objectives.REGISTRY.get(new Identifier(tag.getString("type")));
        if (type == null) {
            throw new RuntimeException("Invalid objective type " + tag.getString("type"));
        }
        T i = type.read(tag.getCompound("data"));
        int count = tag.getInt("count");
        return new ServerChallenge<>(id,type,i,count);
    }
}
