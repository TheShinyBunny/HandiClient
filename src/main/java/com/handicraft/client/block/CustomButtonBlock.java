/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class CustomButtonBlock extends AbstractButtonBlock {

    private final boolean wooden;

    public CustomButtonBlock(boolean wooden, Settings settings) {
        super(wooden,settings);
        this.wooden = wooden;
    }

    @Override
    protected SoundEvent getClickSound(boolean powered) {
        if (wooden) {
            return powered ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
        } else {
            return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
        }
    }
}
