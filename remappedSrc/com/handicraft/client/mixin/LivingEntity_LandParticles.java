/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.collectibles.CollectibleType;
import com.handicraft.client.collectibles.ParticleTrail;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntity_LandParticles {

    @Redirect(method = "fall",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private int spawnFallParticles(ServerWorld serverWorld, ParticleEffect particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        if ((Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            ParticleTrail selected = PlayerPersistentData.of(player).collectibles.getSelected(CollectibleType.PARTICLE);
            if (selected != null) {
                particle = selected.getEffect();
                deltaX *= 0.1;
                deltaY *= 0.1;
                deltaZ *= 0.1;
                speed = 0.05;
                count *= 0.3;
            }
        }
        return serverWorld.spawnParticles(particle, x, y, z, count, deltaX, deltaY, deltaZ, speed);
    }

}
