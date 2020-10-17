/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.challenge.*;
import com.handicraft.client.challenge.client.ClientChallenge;
import com.handicraft.client.challenge.client.ClientChallengesManager;
import com.handicraft.client.util.ExtendedServerMetadata;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(QueryResponseS2CPacket.class)
public class QueryResponsePacket_Extend {

    @Shadow private ServerMetadata metadata;

    @Inject(method = "write",at = @At("TAIL"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        if (metadata instanceof ExtendedServerMetadata) {
            System.out.println("writing extra data to client");
            MinecraftServer server = CommonMod.SERVER.get();
            List<ChallengeInstance> challenges = PlayerChallenges.getChallengesFor(((ExtendedServerMetadata) metadata).getPlayer(),server);
            if (challenges == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeVarInt(challenges.size());
                for (ChallengeInstance c : challenges) {
                    c.writePacket(buf);
                }
            }
        }
    }

    @Inject(method = "read",at = @At("TAIL"))
    private void read(PacketByteBuf buf, CallbackInfo ci) {
        if (buf.readBoolean()) {
            int size = buf.readVarInt();
            List<ChallengeInstance> challenges = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                challenges.add(ChallengeInstance.readFullPacket(buf));
            }
            ClientChallengesManager.setChallenges(challenges, true);
        }
    }

}
