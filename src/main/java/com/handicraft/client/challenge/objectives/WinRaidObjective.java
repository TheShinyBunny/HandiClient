/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class WinRaidObjective implements ObjectiveType<WinRaidObjective.Instance> {

    public static class Instance implements ObjectiveInstance {

        private int minLevel;

        public Instance(int minLevel) {
            this.minLevel = minLevel;
        }

        @Override
        public Text getText(int count) {
            return new LiteralText("Win a Bad Omen " + minLevel + "+ raid");
        }

        @Override
        public ItemStack getIcon() {
            return new ItemStack(Items.TOTEM_OF_UNDYING);
        }

        @Override
        public ObjectiveType<?> getType() {
            return Objectives.WIN_RAID;
        }

        public boolean test(int badOmenLevel) {
            return badOmenLevel >= minLevel;
        }
    }
}
