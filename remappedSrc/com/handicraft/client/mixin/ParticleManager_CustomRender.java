/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public class ParticleManager_CustomRender {

    @Shadow @Final private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;
    @Shadow @Final private Map<ParticleTextureSheet, Queue<Particle>> particles;
    @Shadow @Final private TextureManager textureManager;

    @Overwrite
    public void renderParticles(MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, LightmapTextureManager lightmapTextureManager, Camera camera, float f) {
        if (MinecraftClient.getInstance().world != null) {
            lightmapTextureManager.enable();
            RenderSystem.enableAlphaTest();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.enableFog();
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.peek().getModel());
        }
        Iterator<ParticleTextureSheet> var6 = PARTICLE_TEXTURE_SHEETS.iterator();

        while (true) {
            ParticleTextureSheet particleTextureSheet;
            Iterable<Particle> iterable;
            do {
             if (!var6.hasNext()) {
                 if (MinecraftClient.getInstance().world != null) {
                     RenderSystem.popMatrix();
                     RenderSystem.depthMask(true);
                     RenderSystem.depthFunc(515);
                     RenderSystem.disableBlend();
                     RenderSystem.defaultAlphaFunc();
                     lightmapTextureManager.disable();
                     RenderSystem.disableFog();
                 }
                 return;
             }

             particleTextureSheet = var6.next();
             iterable = this.particles.get(particleTextureSheet);
            } while (iterable == null);

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            particleTextureSheet.begin(bufferBuilder, this.textureManager);

            for (Particle particle : iterable) {

                try {
                    particle.buildGeometry(bufferBuilder, camera, f);
                } catch (Throwable var16) {
                    CrashReport crashReport = CrashReport.create(var16, "Rendering Particle");
                    CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
                    crashReportSection.add("Particle", particle::toString);
                    crashReportSection.add("Particle Type", particleTextureSheet::toString);
                    throw new CrashException(crashReport);
                }
            }

            particleTextureSheet.draw(tessellator);
        }
    }
}
