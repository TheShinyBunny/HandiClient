/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.HandiUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Random;

public class FishObjective implements ObjectiveType<FishObjective.Instance> {

    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return null;
    }

    @Override
    public Instance fromNBT(NbtCompound tag) {
        return null;
    }

    public static class Instance implements ObjectiveInstance {

        private ItemStack stack;
        private String name;

        public Instance(ItemStack stack, String name) {
            this.stack = stack;
            this.name = name;
        }

        @Override
        public Text getText(int count) {
            return new LiteralText("Fish " + count + " " + HandiUtils.pluralize(count,name));
        }

        @Override
        public ItemStack getIcon() {
            return stack;
        }

        @Override
        public ObjectiveType<?> getType() {
            return Objectives.FISH;
        }

        @Override
        public void toNBT(NbtCompound tag) {

        }

        public boolean test(ItemStack stack) {
            return stack.isItemEqual(this.stack);
        }
    }

}
