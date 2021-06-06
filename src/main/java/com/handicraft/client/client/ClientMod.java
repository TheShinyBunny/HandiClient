/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.block.entity.CauldronBlockEntity;
import com.handicraft.client.challenge.*;
import com.handicraft.client.challenge.client.ClientChallengesManager;
import com.handicraft.client.client.entity.CashRegisterRenderer;
import com.handicraft.client.client.entity.DarkBlazeRenderer;
import com.handicraft.client.client.entity.DarkPillagerRenderer;
import com.handicraft.client.client.entity.DarknessWizardRenderer;
import com.handicraft.client.client.screen.*;
import com.handicraft.client.collectibles.ClientCollectibleCache;
import com.handicraft.client.collectibles.Emote;
import com.handicraft.client.collectibles.Music;
import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.emotes.ClientEmoteManager;
import com.handicraft.client.emotes.EmoteManager;
import com.handicraft.client.fluid.ColoredWaterFluid;
import com.handicraft.client.fluid.ModFluids;
import com.handicraft.client.particle.HerobrineContrail;
import com.handicraft.client.particle.JackOContrailParticle;
import com.handicraft.client.particle.RubyContrail;
import com.handicraft.client.screen.cash_register.CashRegisterScreenHandler;
import com.handicraft.client.util.CapeHolder;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Style;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Environment(EnvType.CLIENT)
public class ClientMod implements ClientModInitializer {

    public static final Identifier ALMONI = new Identifier("hcclient:almoni");
    public static final Identifier NOTO = new Identifier("hcclient:noto");

    public static final ServerInfo HANDICRAFT_SERVER = new ServerInfo("HandiCraft","handicraft.mc.gg",false);

    public static final AtomicBoolean handicraftOnline = new AtomicBoolean(true);
    public static final boolean VANILLA_GUI = false;
    public static final UnaryOperator<Style> FONT_APPLIER = s->{
        if (VANILLA_GUI) return s;
        return s.withFont(NOTO);
    };

    public static final PlayedBoardHud PLAYED_BOARD = new PlayedBoardHud();
    public static final ServerAddress SERVER_ADDRESS = new ServerAddress("", 0);

    private static final MultiplayerServerListPinger SERVER_LIST_PINGER = new MultiplayerServerListPinger();
    public static boolean isAlwaysSnowing;

    public static void requestCape(UUID uuid) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(uuid);
            ClientSidePacketRegistry.INSTANCE.sendToServer(CommonMod.REQUEST_CAPE_TEXTURE,buf);
        }
    }

    @Override
    public void onInitializeClient() {
        //region Render Layers
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TOMBSTONE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DARK_ROSE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DARK_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GREEN_DARK_FIRE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PURPLE_DARK_FIRE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GREEN_FIRE_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PURPLE_FIRE_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GREEN_FIRE_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PURPLE_FIRE_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GRASS_BLOCK_SLAB, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CASH_REGISTER,RenderLayer.getCutout());

        //endregion

        //region Particles
        ParticleFactoryRegistry.getInstance().register(CommonMod.JACK_O_CONTRAIL_PARTICLE, JackOContrailParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CommonMod.RUBY_CONTRAIL, RubyContrail.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CommonMod.HEROBRINE_TRAIL, HerobrineContrail.Factory::new);
        //endregion

        //region HandledScreens
        ScreenRegistry.register(CommonMod.ENDER_CHEST_HANDLER_TYPE, EnderChestScreen::new);
        ScreenRegistry.register(CommonMod.ITEM_CLAIM_HANDLER_TYPE, ItemClaimScreen::new);
        ScreenRegistry.register(CommonMod.NETHERITE_FURNACE_HANDLER_TYPE, NetheriteFurnaceScreen::new);
        ScreenRegistry.register(CommonMod.SHULKER_PREVIEW_SCREEN_HANDLER_TYPE, ShulkerPreviewScreen::new);
        ScreenRegistry.register(CommonMod.CANDY_BUCKET_HANDLER_TYPE, CandyBucketScreen::new);
        ScreenRegistry.register(CommonMod.CASH_REGISTER_SCREEN, CashRegisterScreen::new);
        ScreenRegistry.register(CommonMod.CASH_REGISTER_OWNER_SCREEN, CashRegisterOwnerScreen::new);
        //endregion

        //region Entity Renders
        EntityRendererRegistry.INSTANCE.register(CommonMod.DARKNESS_WIZARD, DarknessWizardRenderer::new);
        EntityRendererRegistry.INSTANCE.register(CommonMod.DARK_BLAZE, DarkBlazeRenderer::new);
        EntityRendererRegistry.INSTANCE.register(CommonMod.DARK_PILLAGER, DarkPillagerRenderer::new);
        //endregion

        BlockEntityRendererRegistry.INSTANCE.register(CommonMod.CASH_REGISTER_BLOCK_ENTITY, ctx -> new CashRegisterRenderer());

        ClientPlayNetworking.registerGlobalReceiver(CommonMod.RESPONSE_CAPE_TEXTURE,(mc, h, buf, sender) -> {
            UUID id = buf.readUuid();
            Identifier cape;
            if (buf.readBoolean()) {
                cape = buf.readIdentifier();
            } else {
                cape = null;
            }
            if (MinecraftClient.getInstance().world != null) {
                ((CapeHolder)MinecraftClient.getInstance().world.getPlayerByUuid(id)).setCape(cape);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(EmoteManager.SEND_EMOTE_PACKET, (mc, h, buf, sender) -> {
            PlayerEntity player = h.getWorld().getPlayerByUuid(buf.readUuid());
            if (player instanceof AbstractClientPlayerEntity) {
                ClientEmoteManager.displayEmote((AbstractClientPlayerEntity) player,buf.readIdentifier(),buf.readVarInt());
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerChallenges.UPDATE_CHALLENGES,(mc, h, buf, sender) -> {
            int count = buf.readVarInt();
            List<ChallengeInstance> c = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                c.add(ChallengeInstance.readFullPacket(buf));
            }
            ClientChallengesManager.setChallenges(c);
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerChallenges.COMPLETED,(mc, h, buf, sender) -> {
            ClientChallengesManager.completed(buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerChallenges.PROGRESS_UPDATE,(mc, h, buf, sender) -> {
            ClientChallengesManager.updateProgress(buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerCollectibles.UPDATE_COLLECTIBLES, (mc, h, buf, sender)->{
            ClientCollectibleCache.readResponse(buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerCollectibles.RESET_CAPES,(mc, h, buf, sender)->{
            for (AbstractClientPlayerEntity p : MinecraftClient.getInstance().world.getPlayers()) {
                ((CapeHolder)p).resetCape();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(Music.START_PLAYING,Music::onPlay);
        ClientPlayNetworking.registerGlobalReceiver(Music.UPDATE_MUSIC,Music::onUpdate);

        ClientPlayNetworking.registerGlobalReceiver(CashRegisterScreenHandler.INVALID_PASSWORD,(mc, h, buf, sender) -> {
            if (MinecraftClient.getInstance().currentScreen instanceof CashRegisterScreen) {
                ((CashRegisterScreen) MinecraftClient.getInstance().currentScreen).onInvalidPassword();
            }
        });

        Identifier stillTexture = new Identifier("block/water_still");
        Identifier flowingTexture = new Identifier("block/water_flow");

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((spriteAtlasTexture, registry) -> {
            registry.register(stillTexture);
            registry.register(flowingTexture);
        });

        for (DyeColor color : DyeColor.values()) {
            ColoredWaterFluid still = ModFluids.getColoredStill(color);
            ColoredWaterFluid flowing = ModFluids.getColoredFlowing(color);
            Identifier id = Registry.FLUID.getId(still);
            Identifier listenerId = new Identifier(id.getNamespace(), id.getPath() + "_reload_listener");
            Sprite[] fluidSprites = {null, null};
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return listenerId;
                }

                @Override
                public void reload(ResourceManager manager) {
                    Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                    fluidSprites[0] = atlas.apply(stillTexture);
                    fluidSprites[1] = atlas.apply(flowingTexture);
                }
            });

            FluidRenderHandler handler = new FluidRenderHandler() {
                @Override
                public Sprite[] getFluidSprites(BlockRenderView blockRenderView, BlockPos blockPos, FluidState fluidState) {
                    return fluidSprites;
                }

                @Override
                public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
                    return view.getColor(pos, BiomeColors.WATER_COLOR);
                }
            };

            FluidRenderHandlerRegistry.INSTANCE.register(still, handler);
            FluidRenderHandlerRegistry.INSTANCE.register(flowing, handler);

            BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), still, flowing);

        }

        for (Emote e : EmoteManager.EMOTES) {
            KeyBinding kb = KeyBindingHelper.registerKeyBinding(new KeyBinding("emote." + e.getEmote().getPath(), InputUtil.UNKNOWN_KEY.getCode(), "keybinding.emotes"));
            ClientTickEvents.END_CLIENT_TICK.register(client->{
                if (kb.wasPressed()) {
                    ClientSidePacketRegistry.INSTANCE.sendToServer(new ChatMessageC2SPacket("/emote " + e.getEmote().getPath()));
                }
            });
        }
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) return -1;
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CauldronBlockEntity) {
                if (((CauldronBlockEntity) be).isDefaultPotion()) {
                    if (((CauldronBlockEntity) be).getWaterColor() != null) {
                        DyeColor color = ((CauldronBlockEntity) be).getWaterColor();
                        return WaterColorRenderer.COLOR_MAP.get(color);
                    }
                } else {
                    return PotionUtil.getColor(((CauldronBlockEntity) be).getPotion());
                }
            }
            return BiomeColors.getWaterColor(world,pos);
        },Blocks.CAULDRON);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D);
        },ModBlocks.GRASS_BLOCK_SLAB);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            BlockState blockState = ((BlockItem)stack.getItem()).getBlock().getDefaultState();
            return MinecraftClient.getInstance().getBlockColors().getColor(blockState, null, null, tintIndex);
        },ModBlocks.GRASS_BLOCK_SLAB);
    }

    public static void pingServer() {
        System.out.println("pinging...");

        SERVER_LIST_PINGER.cancel();
        new Thread(()->{
            try {
                SERVER_LIST_PINGER.add(HANDICRAFT_SERVER, () -> {});
                handicraftOnline.set(true);
            } catch (Exception e) {
                handicraftOnline.set(false);
            }
        }).start();

    }


}
