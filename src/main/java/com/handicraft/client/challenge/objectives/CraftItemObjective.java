/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.HandiUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Random;
import java.util.function.Predicate;

public class CraftItemObjective implements ObjectiveType<CraftItemObjective.Instance> {


    public void trigger(PlayerEntity player, ItemStack stack) {
        trigger(player, i -> i.test(stack),stack.getCount());
    }

    public Instance create(ItemStack stack, String name) {
        return new Instance(i->i.isItemEqual(stack), stack, name);
    }

    public Instance create(Predicate<ItemStack> stack, ItemStack icon, String name) {
        return new Instance(stack, icon, name);
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
        private final Predicate<ItemStack> stack;
        private final ItemStack icon;
        private final String name;

        public Instance(Predicate<ItemStack> stack, ItemStack icon, String name) {
            this.stack = stack;
            this.icon = icon;
            this.name = name;
        }

        public Instance(ItemStack stack, String name) {
            this(i->i.isItemEqual(stack),stack,name);
        }

        @Override
        public Text getText(int count) {
            return new LiteralText("Craft " + count + " " + HandiUtils.pluralize(count,name));
        }

        public boolean test(ItemStack stack) {
            return this.stack.test(stack);
        }

        @Override
        public ItemStack getIcon() {
            return icon;
        }

        @Override
        public ObjectiveType<?> getType() {
            return Objectives.CRAFT_ITEM;
        }

        @Override
        public void toNBT(NbtCompound tag) {

        }
    }

}
