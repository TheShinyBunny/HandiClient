/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.collectibles.Emote;
import com.handicraft.client.emotes.ClientEmoteManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EmoteReward extends CollectibleReward<Emote> {

    public EmoteReward(String name, int level, int textureHeight, Emote emote) {
        super(name, level, textureHeight,emote);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void selectTick(HandiPassScreen screen, int ticksHovered) {
        super.selectTick(screen, ticksHovered);
        if (ticksHovered % 200 == 0) {
            ClientEmoteManager.displayEmote(screen.player,collectible.getEmote(),200);
        }
    }
}
