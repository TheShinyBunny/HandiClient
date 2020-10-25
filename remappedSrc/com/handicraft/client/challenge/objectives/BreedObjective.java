/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BreedObjective implements ObjectiveType<BreedObjective.Instance> {

    public static class Instance implements ObjectiveInstance {

        private EntityType<?> type;
        private ItemStack icon;
        private String text;

        public Instance(EntityType<?> type, ItemStack icon, String text) {
            this.type = type;
            this.icon = icon;
            this.text = text;
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
            return Objectives.BREED;
        }

        public boolean test(AnimalEntity entity) {
            return entity.getType() == type;
        }
    }

}
