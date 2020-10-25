/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.HandiUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.function.Predicate;

public class BreakBlockObjective implements ObjectiveType<BreakBlockObjective.Instance> {

    public void trigger(PlayerEntity player, Block block) {
        trigger(player,i->i.predicate.test(block),1);
    }

    public static class Instance implements ObjectiveInstance {

        private Predicate<Block> predicate;
        private ItemConvertible icon;
        private String name;

        public Instance(Predicate<Block> predicate, ItemConvertible icon, String name) {
            this.predicate = predicate;
            this.icon = icon;
            this.name = name;
        }

        public Instance(Block block, String name) {
            this(b->b == block,block,name);
        }

        @Override
        public Text getText(int count) {
            return new LiteralText("Mine " + count + " " + HandiUtils.pluralize(count,name));
        }

        @Override
        public ItemStack getIcon() {
            return new ItemStack(icon);
        }

        @Override
        public ObjectiveType<?> getType() {
            return Objectives.BREAK_BLOCK;
        }
    }

}
