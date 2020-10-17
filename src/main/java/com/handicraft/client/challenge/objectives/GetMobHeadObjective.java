/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.UUID;

public class GetMobHeadObjective implements ObjectiveType<GetMobHeadObjective.Instance> {

    public static class Instance implements ObjectiveInstance {
        private final ItemStack icon;
        private UUID skullId;
        private String name;

        public Instance(UUID skullId, String name, String texture) {
            this.skullId = skullId;
            this.name = name;
            this.icon = new ItemStack(Items.PLAYER_HEAD);
            CompoundTag owner = new CompoundTag();
            owner.putUuid("Id",skullId);
            owner.putString("Name",name);
            CompoundTag props = new CompoundTag();
            CompoundTag tex = new CompoundTag();
            tex.putString("Value",texture);
            ListTag texs = new ListTag();
            texs.add(tex);
            props.put("textures",texs);
            owner.put("Properties",props);
            icon.putSubTag("SkullOwner",owner);
        }

        public boolean test(ItemStack stack) {
            CompoundTag owner = stack.getOrCreateSubTag("SkullOwner");
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
    }

}
