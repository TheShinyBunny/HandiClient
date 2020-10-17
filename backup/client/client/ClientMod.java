/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client;

import com.handicraft.client.CommonMod;
import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.challenge.*;
import com.handicraft.client.challenge.client.ChallengesScreen;
import com.handicraft.client.challenge.client.ClientChallenge;
import com.handicraft.client.challenge.client.ClientChallengesManager;
import com.handicraft.client.client.screen.EnderChestScreen;
import com.handicraft.client.emotes.EmoteManager;
import com.handicraft.client.particle.JackOContrailParticle;
import com.handicraft.client.particle.RubyContrail;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ClientMod implements ClientModInitializer {

    public static final Identifier ALMONI = new Identifier("hcclient:almoni");
    public static final Identifier NOTO = new Identifier("hcclient:noto");


    public static void requestCape(UUID uuid) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(uuid);
            ClientSidePacketRegistry.INSTANCE.sendToServer(CommonMod.REQUEST_CAPE_TEXTURE,buf);
        }
    }

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEONY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_PEONY, RenderLayer.getCutout());

        ParticleFactoryRegistry.getInstance().register(CommonMod.JACK_O_CONTRAIL_PARTICLE, JackOContrailParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CommonMod.RUBY_CONTRAIL, RubyContrail.Factory::new);

        ScreenRegistry.register(CommonMod.ENDER_CHEST_HANDLER_TYPE, EnderChestScreen::new);

        ClientSidePacketRegistry.INSTANCE.register(CommonMod.RESPONSE_CAPE_TEXTURE,(ctx, buf) -> {
            UUID id = buf.readUuid();
            Identifier cape = buf.readIdentifier();
            if (MinecraftClient.getInstance().world != null) {
                PlayerPersistentData.of(MinecraftClient.getInstance().world.getPlayerByUuid(id)).setCape(cape);
            }
        });

        ClientSidePacketRegistry.INSTANCE.register(EmoteManager.SEND_EMOTE_PACKET, (ctx, buf) -> {
            PlayerEntity player = ctx.getPlayer().world.getPlayerByUuid(buf.readUuid());
            if (player instanceof AbstractClientPlayerEntity) {
                EmoteManager.displayEmote((AbstractClientPlayerEntity) player,buf.readIdentifier(),buf.readVarInt());
            }
        });

        ClientSidePacketRegistry.INSTANCE.register(PlayerChallenges.UPDATE_CHALLENGES,(ctx, buf) -> {
            int count = buf.readVarInt();
            List<ChallengeInstance> c = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                c.add(ChallengeInstance.readFullPacket(buf));
            }
            ClientChallengesManager.setChallenges(c,false);
        });

        ClientSidePacketRegistry.INSTANCE.register(PlayerChallenges.COMPLETED,(ctx, buf) -> {
            ClientChallengesManager.completed(buf);
        });

        ClientSidePacketRegistry.INSTANCE.register(PlayerChallenges.PROGRESS_UPDATE,(ctx, buf) -> {
            ClientChallengesManager.updateProgress(buf);
        });
    }
}
