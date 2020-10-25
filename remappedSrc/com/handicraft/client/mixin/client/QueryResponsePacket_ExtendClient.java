/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import com.handicraft.client.challenge.ChallengeInstance;
import com.handicraft.client.challenge.client.ClientChallengesManager;
import com.handicraft.client.collectibles.ClientCollectibleCache;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(QueryResponseS2CPacket.class)
public class QueryResponsePacket_ExtendClient {

    @Inject(method = "read",at = @At("TAIL"))
    private void read(PacketByteBuf buf, CallbackInfo ci) {
        if (buf.readBoolean()) {
            int size = buf.readVarInt();
            List<ChallengeInstance> challenges = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                ChallengeInstance chi = ChallengeInstance.readFullPacket(buf);
                challenges.add(chi);
            }
            ClientChallengesManager.setChallenges(challenges);
        } else {
            ClientChallengesManager.setChallenges(new ArrayList<>());
        }
        ClientCollectibleCache.readResponse(buf);
    }

}
