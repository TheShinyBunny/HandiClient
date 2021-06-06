/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import com.handicraft.client.collectibles.ClientCollectibleCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QueryRequestC2SPacket.class)
public class QueryRequestPacket_ExtendClient {

    @Inject(method = "write",at = @At("HEAD"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeUuid(MinecraftClient.getInstance().getSession().getProfile().getId());
        ClientCollectibleCache.fillPacket(buf);
    }

}
