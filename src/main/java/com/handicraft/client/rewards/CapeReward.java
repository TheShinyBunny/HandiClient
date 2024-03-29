/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.collectibles.Cape;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CapeReward extends CollectibleReward<Cape> {

    private boolean elytra;

    public CapeReward(String name, int level, int textureHeight, Cape cape) {
        super(name, level, textureHeight, cape);
    }

    @Override
    public void onSelect(HandiPassScreen screen) {
        screen.player.setCape(collectible.getTextureId());
        elytra = true;
    }

    @Override
    public void selectTick(HandiPassScreen screen, int ticksHovered) {
        if (ticksHovered % 300 == 0) {
            if (elytra) {
                screen.player.equipStack(EquipmentSlot.CHEST,ItemStack.EMPTY);
            } else {
                screen.player.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
            }
            elytra = !elytra;
        }
    }

    @Override
    public void onDeselect(HandiPassScreen screen) {
        screen.player.setCape(null);
        screen.player.equipStack(EquipmentSlot.CHEST,ItemStack.EMPTY);
    }
}
