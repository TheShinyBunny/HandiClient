/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.collectibles.Emote;
import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.emotes.EmoteManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class EmoteReward extends Reward {

    private Identifier emote;

    public EmoteReward(String name, int level, int textureHeight, Identifier emote) {
        super(name, level, textureHeight);
        this.emote = emote;
    }

    @Override
    public void clicked(HandiPassScreen screen) {
        super.clicked(screen);
        EmoteManager.displayEmote(screen.player,emote,100);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        PlayerCollectibles.give(player,new Emote(emote));
    }
}
