/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class SmeltObjective implements ObjectiveType<SmeltObjective.Instance> {

    public static class Instance implements ObjectiveInstance {

        private ItemStack ingredient;
        private ItemStack result;
        private String text;

        public Instance(ItemStack ingredient, ItemStack result, String text) {
            this.ingredient = ingredient;
            this.result = result;
            this.text = text;
        }

        @Override
        public Text getText(int count) {
            return new LiteralText(text);
        }

        @Override
        public ItemStack getIcon() {
            return ingredient;
        }

        @Override
        public ObjectiveType<?> getType() {
            return Objectives.SMELT;
        }

        public boolean test(ItemStack stack) {
            return result.isItemEqual(stack);
        }
    }

}
