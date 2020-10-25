/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.emotes;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientEmoteManager {

    private static Map<AbstractClientPlayerEntity, EmoteInstance> currentEmotes = new HashMap<>();

    public static void displayEmote(AbstractClientPlayerEntity player, Identifier id, int time) {
        currentEmotes.put(player,new EmoteInstance(new Identifier("hcclient:emotes/" + id.getPath() + ".png"),time));
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
