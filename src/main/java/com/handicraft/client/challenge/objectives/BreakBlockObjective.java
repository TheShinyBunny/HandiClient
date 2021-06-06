/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.HandiUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.Random;

public class BreakBlockObjective implements ObjectiveType<BreakBlockObjective.Instance> {

    public void trigger(PlayerEntity player, Block block) {
        trigger(player,i->i.block == block,1);
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

        private Block block;
        private ItemStack icon;
        private String name;

        public Instance(Block block, ItemStack icon, String name) {
            this.block = block;
            this.icon = icon;
            this.name = name;
        }

        @Override
        public Text getText(int count) {
            return new LiteralText("Mine " + count + " " + HandiUtils.pluralize(count,name));
        }

        @Override
        public ItemStack getIcon() {
            return icon;
        }

        @Override
        public ObjectiveType<?> getType() {
            return Objectives.BREAK_BLOCK;
        }

        @Override
        public void toNBT(NbtCompound tag) {
            tag.putString("block", Registry.BLOCK.getId(block).toString());
        }
    }

}
