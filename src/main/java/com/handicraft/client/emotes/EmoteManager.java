/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.emotes;

import com.handicraft.client.collectibles.Collectibles;
import com.handicraft.client.collectibles.Emote;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class EmoteManager {

    public static final List<Emote> EMOTES = new ArrayList<>();

    public static final Emote BARVAZY;
    public static final Emote DARKVAZY;
    public static final Emote SKELETON;
    public static final Emote HEROBRINE;

    static {
        BARVAZY = add("barvazy");
        DARKVAZY = add("darkvazy");
        SKELETON = add("skeleton");
        HEROBRINE = add("herobrine");
    }

    private static Emote add(String id) {
        Emote i = new Emote(new Identifier(id));
        EMOTES.add(i);
        Collectibles.register(id,i);
        return i;
    }

    public static final Identifier SEND_EMOTE_PACKET = new Identifier("hcclient:show_emote");

    public static void sendEmote(ServerPlayerEntity player, Identifier emoteId, int time) {
        for (ServerPlayerEntity e : player.server.getPlayerManager().getPlayerList()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(player.getUuid());
            buf.writeIdentifier(emoteId);
            buf.writeVarInt(time);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(e,SEND_EMOTE_PACKET,buf);
        }
    }

    public static Emote getEmote(String id) {
        return EMOTES.stream().filter(e->e.getId().getPath().equalsIgnoreCase(id)).findAny().orElse(null);
    }
}
