/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.collectibles.ParticleTrail;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class ParticleReward extends CollectibleReward<ParticleTrail> {

    public ParticleReward(String name, int level, int textureHeight, ParticleTrail particleType) {
        super(name, level, textureHeight,particleType);
    }

    @Override
    public void onSelect(HandiPassScreen screen) {
        super.onSelect(screen);
        screen.player.visible = false;
        if (MinecraftClient.getInstance().world == null) {
            MinecraftClient.getInstance().particleManager.setWorld((ClientWorld) screen.player.world);
        } else {
            screen.backgroundVisible = false;
        }
    }

    @Override
    public void selectTick(HandiPassScreen screen, int ticksHovered) {
        super.selectTick(screen, ticksHovered);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            screen.addRenderJob(matrices -> {
                RenderSystem.pushMatrix();
                float w = (screen.width / 1920f) * -1560;
                float h = (screen.height / 720f) * -400;
                RenderSystem.rotatef(180, 0, 0, 1f);
                RenderSystem.translatef(w, h, 0);
                client.particleManager.renderParticles(matrices, null, MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager(), MinecraftClient.getInstance().gameRenderer.getCamera(), 1);
                RenderSystem.popMatrix();
            });
            screen.addTickJob(() -> {
                if (ticksHovered % 10 == 0) {
                    client.particleManager.addParticle(collectible.getEffect(), 0, 0, 0, (Math.random() - 0.5) * 4, Math.random() * 8, (Math.random() - 0.5) * 4);
                }
                try {
                    client.particleManager.tick();
                } catch (Exception ignored) {

                }
            });
        } else if (ticksHovered % 40 == 0) {
            Vec3d camPos = client.gameRenderer.getCamera().getPos();
            float f = client.gameRenderer.getCamera().getPitch() * 0.017453292F;
            float g = -(client.gameRenderer.getCamera().getYaw() + 55) * 0.017453292F;
            float h = MathHelper.cos(g);
            float i = MathHelper.sin(g);
            float j = MathHelper.cos(f);
            float k = MathHelper.sin(f);
            Vec3d rot = new Vec3d(i * j, -k, h * j);
            camPos = camPos.add(rot.multiply(2));
            client.player.world.addParticle(collectible.getEffect(), camPos.getX(), camPos.getY(), camPos.getZ(), (Math.random() - 0.5) * 0.05, Math.random() * 0.2, (Math.random() - 0.5) * 0.05);
        }
    }

    @Override
    public void onDeselect(HandiPassScreen screen) {
        super.onDeselect(screen);
        screen.player.visible = true;
        if (MinecraftClient.getInstance().world != null) {
            screen.backgroundVisible = true;
        }
    }
}
