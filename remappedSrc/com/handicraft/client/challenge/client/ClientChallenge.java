/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.client;

import com.handicraft.client.challenge.Challenge;
import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.challenge.objectives.ObjectiveType;
import com.handicraft.client.challenge.objectives.Objectives;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class ClientChallenge<I extends ObjectiveInstance> extends Challenge<I> {

    private Text text;
    private ItemConvertible[] icons;

    public ClientChallenge(UUID id, ObjectiveType<I> objective, int minCount, Text text, ItemConvertible[] icons) {
        super(id,objective, minCount);
        this.text = text;
        this.icons = icons;
    }

    public static <T extends ObjectiveInstance> ClientChallenge<T> readPacket(PacketByteBuf buf) {
        UUID id = buf.readUuid();
        ObjectiveType<T> type = Objectives.REGISTRY.get(buf.readVarInt());
        int count = buf.readVarInt();
        Text text = buf.readText();
        ItemConvertible[] icons = new ItemConvertible[buf.readVarInt()];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = Registry.ITEM.get(buf.readVarInt());
        }
        return new ClientChallenge<>(id,type,count,text,icons);
    }

    public static <T extends ObjectiveInstance> Challenge<T> fromNBT(CompoundTag tag) {
        UUID id = tag.getUuid("id");
        ObjectiveType<T> type = Objectives.REGISTRY.get(new Identifier(tag.getString("type")));
        int count = tag.getInt("count");
        Text text = Text.Serializer.fromJson(tag.getString("text"));
        ListTag iconsTag = tag.getList("icons", NbtType.STRING);
        ItemConvertible[] icons = new ItemConvertible[iconsTag.size()];
        int i = 0;
        for (Tag t : iconsTag) {
            if (t instanceof StringTag) {
                icons[i++] = Registry.ITEM.get(new Identifier(t.asString()));
            }
        }
        return new ClientChallenge<>(id,type,count,text,icons);
    }

    public Text getText() {
        return text;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUuid("id",getId());
        tag.putString("type",Objectives.REGISTRY.getId(objective).toString());
        tag.putInt("count",minCount);
        tag.putString("text",Text.Serializer.toJson(text));
        ListTag list = new ListTag();
        for (ItemConvertible i : icons) {
            list.add(StringTag.of(Registry.ITEM.getId(i.asItem()).toString()));
        }
        tag.put("icons",list);
        return tag;
    }

    public ItemConvertible[] getIcons() {
        return icons;
    }
}
