/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Random;
import java.util.UUID;

public class GetMobHeadObjective implements ObjectiveType<GetMobHeadObjective.Instance> {

    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return null;
    }

    @Override
    public Instance fromNBT(NbtCompound tag) {
        return null;
    }

    public static class Instance implements ObjectiveInstance {
        private final ItemStack icon;
        private UUID skullId;
        private String name;

        public Instance(UUID skullId, String name, String texture) {
            this.skullId = skullId;
            this.name = name;
            this.icon = new ItemStack(Items.PLAYER_HEAD);
            NbtCompound owner = new NbtCompound();
            owner.putUuid("Id",skullId);
            owner.putString("Name",name);
            NbtCompound props = new NbtCompound();
            NbtCompound tex = new NbtCompound();
            tex.putString("Value",texture);
            NbtList texs = new NbtList();
            texs.add(tex);
            props.put("textures",texs);
            owner.put("Properties",props);
            icon.putSubTag("SkullOwner",owner);
        }

        public boolean test(ItemStack stack) {
            NbtCompound owner = stack.getOrCreateSubTag("SkullOwner");
            if (owner.containsUuid("Id")) {
                UUID id = owner.getUuid("Id");
                return id.equals(skullId);
            }
            return false;
        }

        @Override
        public Text getText(int count) {
            return new LiteralText("Kill a " + name + " and get his head");
        }

        @Override
        public ItemStack getIcon() {
            return icon;
        }

        @Override
        public ObjectiveType<?> getType() {
            return Objectives.GET_MOB_HEAD;
        }

        @Override
        public void toNBT(NbtCompound tag) {

        }
    }

}
