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

    public CapeReward(String name, int level, int textureHeight, Cape cape) {
        super(name, level, textureHeight, cape);
    }

    @Override
    public void onSelect(HandiPassScreen screen) {
        screen.player.setCape(collectible.getTextureId());
    }

    @Override
    public void onDeselect(HandiPassScreen screen) {
        screen.player.setCape(null);
    }
}
