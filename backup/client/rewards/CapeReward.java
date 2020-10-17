/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.collectibles.Cape;
import com.handicraft.client.collectibles.PlayerCollectibles;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class CapeReward extends Reward {
    private Identifier cape;

    public CapeReward(String name, int level, int textureHeight, Identifier cape) {
        super(name, level, textureHeight);
        this.cape = cape;
    }

    @Override
    public void startedHover(HandiPassScreen screen) {
        screen.player.setCape(cape);
    }

    @Override
    public void hoveredTick(HandiPassScreen screen, int ticksHovered) {
        if (ticksHovered == 300) {
            screen.player.equipStack(EquipmentSlot.CHEST,new ItemStack(Items.ELYTRA));
        }
    }

    @Override
    public void stoppedHover(HandiPassScreen screen) {
        screen.player.setCape(null);
        screen.player.equipStack(EquipmentSlot.CHEST,ItemStack.EMPTY);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        PlayerCollectibles.give(player,new Cape(cape));
    }
}
