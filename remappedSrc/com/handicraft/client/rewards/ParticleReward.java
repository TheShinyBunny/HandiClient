/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.CommonMod;
import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.collectibles.ParticleTrail;
import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.particle.CustomParticleManager;
import com.handicraft.client.particle.CustomSpriteProvider;
import com.handicraft.client.particle.JackOContrailParticle;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Matrix4f;

public class ParticleReward extends Reward {

    private ParticleEffect particleType;

    public ParticleReward(String name, int level, int textureHeight, ParticleEffect particleType) {
        super(name, level, textureHeight);
        this.particleType = particleType;
    }

    @Override
    public void startedHover(HandiPassScreen screen) {
        super.startedHover(screen);
        screen.player.visible = false;
        MinecraftClient.getInstance().particleManager.setWorld((ClientWorld)screen.player.world);
    }

    @Override
    public void hoveredTick(HandiPassScreen screen, int ticksHovered) {
        super.hoveredTick(screen, ticksHovered);
        MinecraftClient client = MinecraftClient.getInstance();
        screen.addRenderJob(matrices->{
            RenderSystem.pushMatrix();
            float w = (client.getWindow().getWidth() / (float)screen.width) * -170f;
            float h = (client.getWindow().getHeight() / (float)screen.height) * -55f;
            RenderSystem.rotatef(180,0,0,1f);
            RenderSystem.translatef(w,h,0);
            client.particleManager.renderParticles(matrices,null,MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager(), MinecraftClient.getInstance().gameRenderer.getCamera(),1);
            RenderSystem.popMatrix();
        });
        screen.addTickJob(()->{
            if (ticksHovered % 10 == 0) {
                client.particleManager.addParticle(particleType,0, 0, 0, (Math.random() - 0.5) * 4, Math.random() * 8, (Math.random() - 0.5) * 4);
            }
            client.particleManager.tick();
        });
    }

    @Override
    public void stoppedHover(HandiPassScreen screen) {
        super.stoppedHover(screen);
        screen.player.visible = true;
    }

    @Override
    public void giveReward(PlayerEntity player) {
        PlayerCollectibles.give(player,new ParticleTrail(particleType));
    }
}
