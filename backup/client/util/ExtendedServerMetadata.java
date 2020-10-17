/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import net.minecraft.server.ServerMetadata;

import java.util.UUID;

public class ExtendedServerMetadata extends ServerMetadata {

    private UUID player;

    public ExtendedServerMetadata(UUID player, ServerMetadata metadata) {
        this.player = player;
        this.setDescription(metadata.getDescription());
        this.setFavicon(metadata.getFavicon());
        this.setPlayers(metadata.getPlayers());
        this.setPlayers(metadata.getPlayers());
    }

    public UUID getPlayer() {
        return player;
    }
}
