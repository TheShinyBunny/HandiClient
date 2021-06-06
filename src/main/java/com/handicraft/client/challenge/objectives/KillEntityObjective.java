/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Random;
import java.util.function.Predicate;

public class KillEntityObjective implements ObjectiveType<KillEntityObjective.Instance> {

    public void trigger(PlayerEntity player, Entity entity, DamageSource source) {
        trigger(player,i->i.type.test(entity) && i.source.test(source),1);
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

        private Predicate<Entity> type;
        private String text;
        private ItemStack icon;
        private Predicate<DamageSource> source;

        public Instance(EntityType<?> type, String text, ItemStack icon) {
            this(e->e.getType() == type,text,icon,s->true);
        }

        public Instance(Predicate<Entity> type, String text, ItemStack icon, Predicate<DamageSource> source) {
            this.type = type;
            this.text = text;
            this.icon = icon;
            this.source = source;
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
            return Objectives.KILL_ENTITY;
        }

        @Override
        public void toNBT(NbtCompound tag) {

        }
    }

}
