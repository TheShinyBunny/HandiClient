/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.google.gson.JsonElement;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.text.Text;

public abstract class ObjectiveInstance {
    private PlayerPredicate player;

    public ObjectiveInstance(PlayerPredicate player) {
        this.player = player;
    }

    public boolean test(PlayerEntity p) {
        return player.test(p);
    }

    public abstract Text getText(int count);

    public abstract ItemConvertible[] getIcons();

    public final void write(CompoundTag tag) {
        if (player != PlayerPredicate.ANY) {
            JsonElement p = player.toJson();
            Tag res = Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, p);
            tag.put("player", res);
        }
        toNBT(tag);
    }

    public abstract void toNBT(CompoundTag tag);

}
