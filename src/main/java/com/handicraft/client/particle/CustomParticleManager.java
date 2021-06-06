/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.particle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;

import java.util.ArrayList;
import java.util.List;

public class CustomParticleManager {

    public static final CustomParticleManager INSTANCE = new CustomParticleManager();

    private Multimap<ParticleTextureSheet,Particle> particles = ArrayListMultimap.create();

    public void addParticle(Particle p) {
        particles.put(p.getType(),p);
    }

    public void tick() {
        List<Particle> dead = new ArrayList<>();
        particles.forEach((particleTextureSheet, particle) -> {
            particle.tick();
            if (!particle.isAlive()) dead.add(particle);
        });

        for (Particle p : dead) {
            particles.remove(p.getType(),p);
        }
    }

    public void render() {
        for (ParticleTextureSheet p : particles.keys()) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            p.begin(bufferBuilder, MinecraftClient.getInstance().getTextureManager());
            for (Particle particle : particles.get(p)) {
                particle.buildGeometry(bufferBuilder,MinecraftClient.getInstance().gameRenderer.getCamera(),1);
            }
            p.draw(tessellator);
        }
    }

}
