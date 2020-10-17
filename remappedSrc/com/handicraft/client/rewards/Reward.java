/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.client.screen.HandiPassScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Reward {

    public static final Registry<Reward> REGISTRY = FabricRegistryBuilder.createSimple(Reward.class,new Identifier("hcclient:rewards")).attribute(RegistryAttribute.MODDED).buildAndRegister();

    private String name;
    private int level;
    private int textureHeight;

    public Reward(String name, int level, int textureHeight) {
        this.name = name;
        this.level = level;
        this.textureHeight = textureHeight;
    }

    public static List<Reward> getByLevel(int level) {
        return REGISTRY.stream().filter(r->r.level == level).collect(Collectors.toList());
    }

    @Environment(EnvType.CLIENT)
    public void startedHover(HandiPassScreen screen) {

    }

    @Environment(EnvType.CLIENT)
    public void hoveredTick(HandiPassScreen screen, int ticksHovered) {

    }

    @Environment(EnvType.CLIENT)
    public void stoppedHover(HandiPassScreen screen) {

    }

    @Environment(EnvType.CLIENT)
    public void clicked(HandiPassScreen screen) {

    }

    public abstract void giveReward(PlayerEntity player);

    public String getName() {
        return name;
    }

    @Environment(EnvType.CLIENT)
    public Identifier getTexture() {
        Identifier id = REGISTRY.getId(this);
        return new Identifier(id.getNamespace(),"textures/rewards/" + id.getPath() + ".png");
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public int getLevel() {
        return level;
    }
}
