/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.emotes;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmoteManager {

    public static final List<Identifier> EMOTES = new ArrayList<>();

    public static final Identifier BARVAZY;
    public static final Identifier DARKVAZY;

    static {
        BARVAZY = add("hcclient:barvazy");
        DARKVAZY = add("hcclient:darkvazy");
    }

    private static Identifier add(String id) {
        Identifier i = new Identifier(id);
        EMOTES.add(i);
        return i;
    }

    private static Map<AbstractClientPlayerEntity, EmoteInstance> currentEmotes = new HashMap<>();

    public static void displayEmote(AbstractClientPlayerEntity player, Identifier id, int time) {
        currentEmotes.put(player,new EmoteInstance(new Identifier(id.getNamespace(),"emotes/" + id.getPath() + ".png"),time));
    }

    public static EmoteInstance getEmoteFor(AbstractClientPlayerEntity player) {
        EmoteInstance e = currentEmotes.get(player);
        if (e == null) return null;
        e.time++;
        if (e.time > e.lifespan) {
            currentEmotes.put(player,null);
        }
        return e;
    }

    public static final Identifier SEND_EMOTE_PACKET = new Identifier("hcclient:show_emote");

    public static void sendEmote(ServerPlayerEntity player, Identifier emote, int time) {
        for (ServerPlayerEntity e : player.server.getPlayerManager().getPlayerList()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(player.getUuid());
            buf.writeIdentifier(emote);
            buf.writeVarInt(time);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(e,SEND_EMOTE_PACKET,buf);
        }
    }

    public static class EmoteInstance {
        private Identifier texture;
        private int time;
        private int lifespan;

        public EmoteInstance(Identifier texture, int lifespan) {
            this.texture = texture;
            this.lifespan = lifespan;
            this.time = 0;
        }

        public Identifier getTexture() {
            return texture;
        }

        public int getTime() {
            return time;
        }

        public int getLifespan() {
            return lifespan;
        }
    }

}
