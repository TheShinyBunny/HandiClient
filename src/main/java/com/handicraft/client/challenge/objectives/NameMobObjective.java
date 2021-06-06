/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Random;

public class NameMobObjective implements ObjectiveType<NameMobObjective.Instance> {

    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return null;
    }

    @Override
    public Instance fromNBT(NbtCompound tag) {
        return null;
    }

    public static class Instance implements ObjectiveInstance {
        private EntityType<?> type;
        private String name;
        private String text;

        public Instance(EntityType<?> type, String name, String text) {
            this.type = type;
            this.name = name;
            this.text = text;
        }

        @Override
        public Text getText(int count) {
            return new LiteralText(text);
        }

        @Override
        public ItemStack getIcon() {
            return new ItemStack(Items.NAME_TAG);
        }

        @Override
        public ObjectiveType<?> getType() {
            return Objectives.NAME_MOB;
        }

        @Override
        public void toNBT(NbtCompound tag) {

        }

        public boolean test(LivingEntity entity, ItemStack stack) {
            return type == entity.getType() && stack.getName().asString().equalsIgnoreCase(name);
        }
    }

}
