/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.function.Predicate;

public class StripLogObjective implements ObjectiveType<StripLogObjective.Instance> {

    public static class Instance implements ObjectiveInstance {

        private Predicate<Block> predicate;
        private ItemStack icon;
        private String text;

        public Instance(Predicate<Block> predicate, ItemStack icon, String text) {
            this.predicate = predicate;
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
            return Objectives.STRIP_WOOD;
        }

        public boolean test(Block block) {
            return predicate.test(block);
        }
    }

}
