/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.HandiUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Random;
import java.util.function.Predicate;

import static com.handicraft.client.challenge.objectives.CraftItemObjective.*;

public class CraftItemObjective extends ObjectiveType<Instance> {


    public void trigger(PlayerEntity player, ItemStack stack) {
        triggerListeners(player,i->i.test(player,stack));
    }

    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return new Instance(PlayerPredicate.ANY,modifier.modify(HandiUtils.randomEnum(random,Craftable.class)));
    }

    @Override
    public Instance fromNBT(CompoundTag tag, PlayerPredicate player) {
        Craftable c = Craftable.valueOf(tag.getString("craftable"));
        return new Instance(player,c);
    }

    public static class Instance extends ObjectiveInstance {
        private Craftable craftable;

        public Instance(PlayerPredicate player, Craftable craftable) {
            super(player);
            this.craftable = craftable;
        }

        public boolean test(PlayerEntity player, ItemStack result) {
            return super.test(player) && craftable.test(result);
        }

        @Override
        public Text getText(int count) {
            return new TranslatableText("objective.craft", count, HandiUtils.pluralize(count * craftable.count,craftable.getName()));
        }

        @Override
        public ItemConvertible[] getIcons() {
            return new ItemConvertible[]{craftable.icon};
        }

        @Override
        public void toNBT(CompoundTag tag) {
            tag.putString("craftable",craftable.name());
        }
    }

    public enum Craftable implements ObjectiveParameter<ItemStack> {
        STICK(4,1, Items.STICK,4,"Stick(s)"),
        FURNACE(2,0.2F, Blocks.FURNACE, 1,"Furnace(s)"),
        SLAB(2,0.5F,i->i instanceof BlockItem && ((BlockItem)i).getBlock() instanceof SlabBlock,Blocks.SMOOTH_STONE_SLAB,6,"Slab(s) of any type"),
        REPEATER(1,0.2F,Blocks.REPEATER,1,"Repeater(s)");

        private int weight;
        private float countModifier;
        private Predicate<Item> result;
        private int count;
        private String name;
        private ItemConvertible icon;

        Craftable(int weight, float countModifier, Predicate<Item> result, ItemConvertible icon, int count, String name) {
            this.weight = weight;
            this.countModifier = countModifier;
            this.result = result;
            this.count = count;
            this.name = name;
            this.icon = icon;
        }

        Craftable(int weight, float countModifier, ItemConvertible result, int count, String name) {
            this(weight,countModifier,i->i == result.asItem(),result,count, name);
        }

        @Override
        public float getCountModifier() {
            return countModifier * count;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean test(ItemStack input) {
            return result.test(input.getItem());
        }

        @Override
        public int getWeight() {
            return weight;
        }
    }

}
