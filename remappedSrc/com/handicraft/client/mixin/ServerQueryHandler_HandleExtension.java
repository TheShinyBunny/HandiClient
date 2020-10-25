/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.util.ExtendedQueryRequest;
import com.handicraft.client.util.ExtendedServerMetadata;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerQueryNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryHandler_HandleExtension {

    @Shadow @Final private ClientConnection connection;
    @Shadow @Final private MinecraftServer server;

    @Overwrite
    public void onRequest(QueryRequestC2SPacket packet) {
        if (!(packet instanceof ExtendedQueryRequest)) {
            System.out.println("VANILLA PING PACKET RECEIVED");
        } else {
            ExtendedQueryRequest req = (ExtendedQueryRequest)packet;
            UUID id = req.getUUID();
            if (req.getNewClaim() != null) {
                PlayerCollectibles.claimOffline(id, req.getNewClaim());
            }
            if (req.getNewSelectedType() != null) {
                PlayerCollectibles.selectOffline(id,req.getNewSelectedType(),req.getNewSelected());
            }
            this.connection.send(new QueryResponseS2CPacket(new ExtendedServerMetadata(id,this.server.getServerMetadata())));
        }

    }
}
