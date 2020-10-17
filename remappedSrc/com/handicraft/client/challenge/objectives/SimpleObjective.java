/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Random;

public class SimpleObjective extends ObjectiveType<SimpleObjective.Instance> {

    private String textKey;
    private ItemConvertible icon;

    public SimpleObjective(String textKey, ItemConvertible icon) {
        this.textKey = textKey;
        this.icon = icon;
    }

    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return new Instance(PlayerPredicate.ANY,textKey,icon);
    }

    @Override
    public Instance fromNBT(CompoundTag tag, PlayerPredicate player) {
        return new Instance(player,textKey,icon);
    }

    public static class Instance extends ObjectiveInstance {

        private String textKey;
        private ItemConvertible icon;

        public Instance(PlayerPredicate player, String textKey, ItemConvertible icon) {
            super(player);
            this.textKey = textKey;
            this.icon = icon;
        }

        @Override
        public Text getText(int count) {
            return new TranslatableText(textKey,count);
        }

        @Override
        public ItemConvertible[] getIcons() {
            if (icon == null) return new ItemConvertible[0];
            return new ItemConvertible[]{icon};
        }

        @Override
        public void toNBT(CompoundTag tag) {

        }
    }
}
