/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

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

    @Shadow private boolean responseSent;
    @Shadow @Final private ClientConnection connection;
    @Shadow @Final private static Text REQUEST_HANDLED;
    @Shadow @Final private MinecraftServer server;

    @Overwrite
    public void onRequest(QueryRequestC2SPacket packet) {
        if (this.responseSent) {
            this.connection.disconnect(REQUEST_HANDLED);
        } else {
            this.responseSent = true;
            if (!(packet instanceof UUIDHolder)) {
                System.out.println("NO UUID IN PACKET HUH");
            } else {
                UUID id = ((UUIDHolder) packet).getUUID();
                this.connection.send(new QueryResponseS2CPacket(new ExtendedServerMetadata(id,this.server.getServerMetadata())));
            }
        }

    }
}
