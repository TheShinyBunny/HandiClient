/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.rewards;

import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.collectibles.Collectible;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    public static Reward getByLevel(int level) {
        return REGISTRY.stream().filter(r->r.level == level).findAny().orElse(null);
    }

    public static void register(Identifier id, Reward reward) {
        Registry.register(REGISTRY,id,reward);
    }

    public static Reward getByCollectible(Collectible collectible) {
        return REGISTRY.stream().filter(r->r instanceof CollectibleReward && ((CollectibleReward<?>) r).getCollectible() == collectible).findAny().orElse(null);
    }

    @Environment(EnvType.CLIENT)
    public void onSelect(HandiPassScreen screen) {

    }

    @Environment(EnvType.CLIENT)
    public void selectTick(HandiPassScreen screen, int ticksHovered) {

    }

    @Environment(EnvType.CLIENT)
    public void onDeselect(HandiPassScreen screen) {

    }

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

    public boolean isObtainable(int passLevel) {
        return this.level > 0 && passLevel >= this.level;
    }
}
