/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.util.HandiVersionHolder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandshakeC2SPacket.class)
public class HandshakePacket_Extend implements HandiVersionHolder {

    @Inject(method = "write",at = @At("TAIL"))
    private void extendWrite(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeVarInt(CommonMod.VERSION);
    }

    @Unique
    private int handiVersion;

    @Inject(method = "read",at = @At("TAIL"))
    private void extendRead(PacketByteBuf buf, CallbackInfo ci) {
        if (buf.isReadable()) {
            handiVersion = buf.readVarInt();
        } else {
            handiVersion = -1;
        }
    }


    @Override
    public int getHandiVersion() {
        return handiVersion;
    }
}
