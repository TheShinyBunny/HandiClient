/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Random;

public class SimpleObjective implements ObjectiveType<SimpleObjective.Instance> {

    public Instance create(String text,ItemStack icon) {
        return new Instance(this,text,icon);
    }

    public void trigger(PlayerEntity player, int count) {
        trigger(player,i->true,count);
    }

    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return null;
    }

    @Override
    public Instance fromNBT(NbtCompound tag) {
        return null;
    }

    public static class Instance implements ObjectiveInstance {

        private ObjectiveType<?> type;
        private final String text;
        private final ItemStack icon;

        public Instance(ObjectiveType<?> type, String text, ItemStack icon) {
            this.type = type;
            this.text = text;
            this.icon = icon;
        }

        @Override
        public Text getText(int count) {
            return new LiteralText(text);
        }

        @Override
        public ItemStack getIcon() {
            return icon;
        }

        @Override
        public ObjectiveType<?> getType() {
            return type;
        }

        @Override
        public void toNBT(NbtCompound tag) {

        }
    }
}
