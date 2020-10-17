/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.function.Predicate;

public class BlockDamageObjective implements ObjectiveType<BlockDamageObjective.Instance> {

    public static class Instance implements ObjectiveInstance {

        private Predicate<DamageSource> predicate;
        private String text;
        private ItemStack icon;

        public Instance(Predicate<DamageSource> predicate, String text, ItemStack icon) {
            this.predicate = predicate;
            this.text = text;
            this.icon = icon;
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
            return Objectives.BLOCK_DAMAGE;
        }

        public boolean test(DamageSource source) {
            return predicate.test(source);
        }
    }
}
