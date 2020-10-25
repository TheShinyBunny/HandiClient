/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.client.screen.HandiPassScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ItemReward extends Reward {
    private ItemStack stack;

    public ItemReward(String name, int level, int textureHeight, ItemStack stack) {
        super(name, level, textureHeight);
        this.stack = stack;
    }

    @Override
    public void onSelect(HandiPassScreen screen) {
        screen.player.setStackInHand(Hand.MAIN_HAND,stack);
    }

    @Override
    public void onDeselect(HandiPassScreen screen) {
        screen.player.setStackInHand(Hand.MAIN_HAND,ItemStack.EMPTY);
    }

    public ItemStack getStack() {
        return stack.copy();
    }
}
