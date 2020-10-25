/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.util.HandiVersionHolder;
import net.minecraft.SharedConstants;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeHandler_Extend {

    @Shadow @Final private ClientConnection connection;

    @Inject(method = "onHandshake",at = @At(value = "INVOKE",target = "Lnet/minecraft/network/ClientConnection;setPacketListener(Lnet/minecraft/network/listener/PacketListener;)V",shift = At.Shift.BEFORE),cancellable = true)
    private void extendHandshake(HandshakeC2SPacket packet, CallbackInfo ci) {
        if (packet instanceof HandiVersionHolder) {
            int ver = ((HandiVersionHolder) packet).getHandiVersion();
            Text text;
            if (ver == -1) {
                text = new LiteralText("You are not using the HandiCraft Client!");
                connection.send(new LoginDisconnectS2CPacket(text));
                connection.disconnect(text);
                ci.cancel();
            } else if (ver < CommonMod.VERSION) {
                text = new LiteralText("You are using an outdated version of HandiClient!");
                connection.send(new LoginDisconnectS2CPacket(text));
                connection.disconnect(text);
                ci.cancel();
            }
        }
    }

}
