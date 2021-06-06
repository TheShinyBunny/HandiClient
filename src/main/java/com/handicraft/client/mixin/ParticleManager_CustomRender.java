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

import java.util.*;

@Mixin(ParticleManager.class)
public class ParticleManager_CustomRender {

    @Shadow @Final private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;
    @Shadow @Final private Map<ParticleTextureSheet, Queue<Particle>> particles;
    @Shadow @Final private TextureManager textureManager;

    @Overwrite
    public void renderParticles(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, LightmapTextureManager lightmapTextureManager, Camera camera, float f) {
        if (MinecraftClient.getInstance().world != null) {
            lightmapTextureManager.enable();
            RenderSystem.enableDepthTest();
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.method_34425(matrices.peek().getModel());
            RenderSystem.applyModelViewMatrix();
        }
        Iterator var7 = PARTICLE_TEXTURE_SHEETS.iterator();

        while(true) {
            ParticleTextureSheet particleTextureSheet;
            Iterable iterable;
            do {
                if (!var7.hasNext()) {
                    matrices.pop();
                    if (MinecraftClient.getInstance().world != null) {
                        RenderSystem.applyModelViewMatrix();
                        RenderSystem.depthMask(true);
                        RenderSystem.disableBlend();
                        lightmapTextureManager.disable();
                    }
                    return;
                }

                particleTextureSheet = (ParticleTextureSheet)var7.next();
                iterable = (Iterable)this.particles.get(particleTextureSheet);
            } while(iterable == null);

            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            particleTextureSheet.begin(bufferBuilder, this.textureManager);
            Iterator var12 = iterable.iterator();

            while(var12.hasNext()) {
                Particle particle = (Particle)var12.next();

                try {
                    particle.buildGeometry(bufferBuilder, camera, f);
                } catch (Throwable var17) {
                    CrashReport crashReport = CrashReport.create(var17, "Rendering Particle");
                    CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
                    Objects.requireNonNull(particle);
                    crashReportSection.add("Particle", particle::toString);
                    Objects.requireNonNull(particleTextureSheet);
                    crashReportSection.add("Particle Type", particleTextureSheet::toString);
                    throw new CrashException(crashReport);
                }
            }

            particleTextureSheet.draw(tessellator);
        }
    }
}
