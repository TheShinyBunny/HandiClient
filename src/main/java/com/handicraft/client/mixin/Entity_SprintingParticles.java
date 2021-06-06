/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.collectibles.CollectibleType;
import com.handicraft.client.collectibles.ParticleTrail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class Entity_SprintingParticles {

    @Shadow public World world;

    @Redirect(method = "spawnSprintingParticles",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void addParticles(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if ((Object)this instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            ParticleTrail selected = (ParticleTrail) PlayerPersistentData.of(player).collectibles.getSelected(CollectibleType.PARTICLE);
            if (selected != null) {
                ((ServerWorld)world).spawnParticles(selected.getEffect(),x,y,z,1,velocityX * 0.1,velocityY * 0.05,velocityZ * 0.2,0.05);
            } else {
                ((ServerWorld) world).spawnParticles(parameters, x, y, z, 1, velocityX, velocityY * 0.1, velocityZ, 0.05);
            }
        } else if ((Object)this instanceof PlayerEntity) {
            return;
        }
        world.addParticle(parameters,x,y,z,velocityX,velocityY,velocityZ);
    }

}
