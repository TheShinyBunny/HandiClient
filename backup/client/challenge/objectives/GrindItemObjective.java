/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.HandiUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Random;
import java.util.function.Predicate;

public class GrindItemObjective extends ObjectiveType<GrindItemObjective.Instance> {

    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return new Instance(PlayerPredicate.ANY,modifier.modify(HandiUtils.randomEnum(random,Grindable.class)));
    }

    @Override
    public Instance fromNBT(CompoundTag tag, PlayerPredicate player) {
        Grindable g = Grindable.valueOf(tag.getString("grindable"));
        return new Instance(player,g);
    }

    public static class Instance extends ObjectiveInstance {

        private Grindable grindable;

        public Instance(PlayerPredicate player, Grindable grindable) {
            super(player);
            this.grindable = grindable;
        }

        @Override
        public Text getText(int count) {
            return new TranslatableText("objective.grind_item",count, HandiUtils.pluralize(count,grindable.name));
        }

        @Override
        public ItemConvertible[] getIcons() {
            if (grindable == Grindable.ANY) return new ItemConvertible[]{Blocks.GRINDSTONE};
            return new ItemConvertible[]{Blocks.GRINDSTONE,grindable.icon};
        }

        @Override
        public void toNBT(CompoundTag tag) {
            tag.putString("grindable",grindable.name());
        }
    }

    public enum Grindable implements ObjectiveParameter<ItemStack> {
        SWORD(3,1,i->i instanceof SwordItem, Items.IRON_SWORD,"Sword(s) of any type"),
        BOW(2,1.5f,i->i instanceof BowItem,Items.BOW,"Bow(s)"),
        ARMOR(1,0.6F,i->i instanceof ArmorItem,Items.DIAMOND_CHESTPLATE,"Armor Piece(s)"),
        BOOK(1,0.8f,i->i instanceof BookItem || i instanceof EnchantedBookItem,Items.ENCHANTED_BOOK,"Book(s)"),
        ANY(2,2f,i->true,null,"Item(s)");

        private final int weight;
        private final float countModifier;
        private final Predicate<Item> item;
        private final ItemConvertible icon;
        private final String name;

        Grindable(int weight, float countModifier, Predicate<Item> item, ItemConvertible icon, String name) {
            this.weight = weight;
            this.countModifier = countModifier;
            this.item = item;
            this.icon = icon;
            this.name = name;
        }

        @Override
        public float getCountModifier() {
            return countModifier;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean test(ItemStack input) {
            return item.test(input.getItem());
        }


    }

}
