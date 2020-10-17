/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.handicraft.client.util.WeightedItem;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public abstract class ObjectiveType<I extends ObjectiveInstance> implements WeightedItem {

    private List<Listener<I>> listenerList;

    private String translationKey;

    public ObjectiveType() {
        this.listenerList = new ArrayList<>();
    }

    public abstract I generate(Random random, CountModifier modifier);

    public I read(CompoundTag tag) {
        PlayerPredicate p;
        if (tag.contains("player")) {
            JsonElement e = Dynamic.convert(NbtOps.INSTANCE,JsonOps.INSTANCE,tag.get("player"));
            p = PlayerPredicate.fromJson(e);
        } else {
            p = PlayerPredicate.ANY;
        }
        return fromNBT(tag,p);
    }

    public abstract I fromNBT(CompoundTag tag, PlayerPredicate player);

    public void addListener(Listener<I> listener) {
        this.listenerList.add(listener);
    }

    public void removeListener(Listener<I> listener) {
        this.listenerList.remove(listener);
    }

    public String getTranslationKey() {
        if (translationKey == null) {
            translationKey = Util.createTranslationKey("objective",Objectives.REGISTRY.getId(this));
        }
        return translationKey;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + Objectives.REGISTRY.getId(this) + "}";
    }

    @Override
    public int getWeight() {
        return 5;
    }

    public int getBaseCount() {
        return 10;
    }

    public int getMaxCount() {
        return 20;
    }

    public void triggerListeners(PlayerEntity player, Predicate<I> predicate) {
        listenerList.forEach(l->{
            if (predicate.test(l.getInstance())) {
                System.out.println("triggering listener " + l);
                l.triggered(player);
            }
        });
    }

    public int getRandomBaseCount(Random r) {
        return MathHelper.nextInt(r,getBaseCount(),getMaxCount());
    }

    public interface Listener<I> {

        void triggered(PlayerEntity player);

        I getInstance();

    }




}
