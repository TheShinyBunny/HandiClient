/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client;

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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FakePlayer extends ClientPlayerEntity {

    public boolean visible = true;
    private boolean texturesLoaded;
    private Map<MinecraftProfileTexture.Type, Identifier> textures = new HashMap<>();
    private String model;
    private Identifier cape;

    public FakePlayer(ClientWorld world, ClientPlayNetworkHandler handler) {
        super(MinecraftClient.getInstance(), world, handler, new StatHandler(), new ClientRecipeBook(), false, false);
    }

    public static FakePlayer create() {
        ClientPlayNetworkHandler handler = new ClientPlayNetworkHandler(MinecraftClient.getInstance(),null,null,MinecraftClient.getInstance().getSession().getProfile());
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
        return this.cape;
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
        return null;
    }

    @Override
    public boolean canRenderCapeTexture() {
        return true;
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    public void render(float x, float y, float width, float height) {
        if (!visible) return;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale(width, height, width);
        Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vector3f.POSITIVE_X.getDegreesQuaternion(0);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        Camera cam = MinecraftClient.getInstance().gameRenderer.getCamera();
        cam.update(world,this,true,false,1);
        client.getEntityRenderDispatcher().configure(world,cam,this);
        client.player = this;

        RenderSystem.runAsFancy(()-> {
            client.getEntityRenderDispatcher().render(this, 0, 0, 0, 0, 1, matrixStack, immediate, 15728880);
        });
        client.getEntityRenderDispatcher().setWorld(null);
        client.player = null;
        immediate.draw();
        RenderSystem.popMatrix();
    }
}
