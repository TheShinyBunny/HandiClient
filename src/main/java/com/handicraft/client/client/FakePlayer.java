/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client;

import com.handicraft.client.collectibles.Cape;
import com.handicraft.client.collectibles.ClientCollectibleCache;
import com.handicraft.client.collectibles.CollectibleType;
import com.handicraft.client.mixin.DimensionTypeAccessor;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FakePlayer extends ClientPlayerEntity {

    private boolean texturesLoaded;
    private Map<MinecraftProfileTexture.Type, Identifier> textures = new HashMap<>();
    private String model;
    private Identifier cape;

    public FakePlayer(ClientWorld world, ClientPlayNetworkHandler handler) {
        super(MinecraftClient.getInstance(), world, handler, new StatHandler(), new ClientRecipeBook(), false, false);
    }

    public static FakePlayer create() {
        ClientPlayNetworkHandler handler = new ClientPlayNetworkHandler(MinecraftClient.getInstance(),null,new ClientConnection(NetworkSide.CLIENTBOUND),MinecraftClient.getInstance().getSession().getProfile());
        return new FakePlayer(new ClientWorld(handler,new ClientWorld.Properties(Difficulty.EASY,false,false), World.OVERWORLD, DimensionTypeAccessor.getOVERWORLD(),10,MinecraftClient.getInstance()::getProfiler,MinecraftClient.getInstance().worldRenderer,false,0),handler);
    }

    @Override
    public String getModel() {
        loadTextures();
        return model == null ? DefaultSkinHelper.getModel(getGameProfile().getId()) : model;
    }

    private void loadTextures() {
        synchronized(this) {
            if (!this.texturesLoaded) {
                this.texturesLoaded = true;
                MinecraftClient.getInstance().getSkinProvider().loadSkin(this.getGameProfile(), (type, identifier, minecraftProfileTexture) -> {
                    this.textures.put(type, identifier);
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        this.model = minecraftProfileTexture.getMetadata("model");
                        if (this.model == null) {
                            this.model = "default";
                        }
                    }

                }, true);
            }

        }
    }

    @Override
    public @Nullable Identifier getCapeTexture() {
        if (cape == null) {
            Cape c = (Cape) ClientCollectibleCache.getSelected(CollectibleType.CAPE);
            return c == null ? null : c.getTextureId();
        }
        return cape;
    }

    public void setCape(Identifier cape) {
        this.cape = cape;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    protected @Nullable PlayerListEntry getPlayerListEntry() {
        if (MinecraftClient.getInstance().world == null) return null;
        return super.getPlayerListEntry();
    }

    @Override
    public Identifier getSkinTexture() {
        loadTextures();
        return textures.getOrDefault(MinecraftProfileTexture.Type.SKIN,DefaultSkinHelper.getTexture(uuid));
    }

    @Override
    public boolean canRenderCapeTexture() {
        return true;
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    public void render(MatrixStack stack, float x, float y, float width, float height) {
        stack.push();
        stack.translate(x,y,1050f);
        stack.scale(1f,1f,-1f);
        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale(width, height, width);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(0);
        quaternion.hamiltonProduct(quaternion2);
        stack.multiply(quaternion);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        Camera cam = MinecraftClient.getInstance().gameRenderer.getCamera();
        cam.update(world, this, true, false, 1);
        client.getEntityRenderDispatcher().configure(world, cam, this);
        if (MinecraftClient.getInstance().world == null) {
            client.player = this;
        }

        RenderSystem.runAsFancy(()-> {
            client.getEntityRenderDispatcher().render(this, 0, 0, 0, 0, 1, stack, immediate, 15728880);
        });
        if (MinecraftClient.getInstance().world == null) {
            client.getEntityRenderDispatcher().setWorld(null);
            client.player = null;
        }
        immediate.draw();
        stack.pop();
    }
}
