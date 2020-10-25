/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientNetworkHandler_OnAnimation {

    @Shadow private ClientWorld world;

    @Shadow private MinecraftClient client;

    @Inject(method = "onEntityAnimation",at = @At("TAIL"))
    private void onAnimation(EntityAnimationS2CPacket packet, CallbackInfo ci) {
        Entity e = world.getEntityById(packet.getId());
        if (e != null) {
            if (packet.getAnimationId() == 6) {
                //client.particleManager.addEmitter(e, ParticleTypes.WITCH);
            }
        }
    }

}
