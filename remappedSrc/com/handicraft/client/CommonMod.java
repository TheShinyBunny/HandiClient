/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client;

import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.challenge.ChallengesManager;
import com.handicraft.client.data.HandiDataGenerator;
import com.handicraft.client.datafix.BukkitEnchantmentsFix;
import com.handicraft.client.datafix.BukkitXPFix;
import com.handicraft.client.datafix.PluginSchema;
import com.handicraft.client.emotes.EmoteManager;
import com.handicraft.client.enchantments.HeatWalkerEnchantment;
import com.handicraft.client.item.ModItems;
import com.handicraft.client.rewards.*;
import com.handicraft.client.screen.EnderChestScreenHandler;
import com.handicraft.client.util.Register;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.SocketAddress;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommonMod implements ModInitializer {


    public static final DefaultParticleType JACK_O_CONTRAIL_PARTICLE = FabricParticleTypes.simple(true);
    public static final DefaultParticleType RUBY_CONTRAIL = FabricParticleTypes.simple(true);

    public static final Identifier CHANGE_STORED_XP = new Identifier("hcclient:store_xp");
    public static final HeatWalkerEnchantment HEAT_WALKER = new HeatWalkerEnchantment();

    private static final Identifier SPOOKY_SOUND_ID = new Identifier("hcclient:spooky");
    public static final SoundEvent SPOOKY_SOUND = new SoundEvent(SPOOKY_SOUND_ID);

    public static final ServerInfo HANDICRAFT_SERVER = new ServerInfo("HandiCraft","apx132542.apexmc.co",false);


    public static final ScreenHandlerType<EnderChestScreenHandler> ENDER_CHEST_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("hcclient:enderchest"),(i, playerInventory, packetByteBuf) -> {
        int stored = packetByteBuf.readVarInt();
        return new EnderChestScreenHandler(i,playerInventory,new EnderChestInventory(),stored);
    });

    public static final Identifier REQUEST_CAPE_TEXTURE = new Identifier("hcclient:request_cape");
    public static final Identifier RESPONSE_CAPE_TEXTURE = new Identifier("hcclient:response_cape");


    public static final AtomicReference<MinecraftServer> SERVER = new AtomicReference<>();

    public static final int DATA_VERSION = 1;
    public static DataFixer DATA_FIXER;

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(3)));

    public static float capeModifier() {
        return 42;
    }




    @Override
    public void onInitialize() {

        registerAll(ModItems.class,Item.class,Registry.ITEM);
        registerAll(ModBlocks.class,Block.class,Registry.BLOCK,this::registerBlockItem);

        Registry.register(Registry.ENCHANTMENT,new Identifier("heat_walker"),HEAT_WALKER);

        Registry.register(Registry.PARTICLE_TYPE,new Identifier("hcclient:jack_o_contrail"),JACK_O_CONTRAIL_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE,new Identifier("hcclient:ruby_contrail"),RUBY_CONTRAIL);

        Registry.register(Registry.SOUND_EVENT,SPOOKY_SOUND_ID,SPOOKY_SOUND);

        ServerSidePacketRegistry.INSTANCE.register(REQUEST_CAPE_TEXTURE,(ctx,buf)->{
            UUID id = buf.readUuid();
            Identifier capeTex = PlayerPersistentData.of(SERVER.get().getPlayerManager().getPlayer(id)).cape;
            PacketByteBuf buff = new PacketByteBuf(Unpooled.buffer());
            buff.writeUuid(id);
            buff.writeIdentifier(capeTex);

            ServerSidePacketRegistry.INSTANCE.sendToPlayer(ctx.getPlayer(),RESPONSE_CAPE_TEXTURE,buff);
        });

        ServerSidePacketRegistry.INSTANCE.register(CHANGE_STORED_XP,XPStorageHelper::update);

        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            SERVER.set(minecraftServer);

        });

        Registry.register(Reward.REGISTRY,new Identifier("hcclient:spooky_disc"),new ItemReward("Spooky Music Disc",1,138,new ItemStack(ModItems.SPOOKY_MUSIC_DISC)));

        Registry.register(Reward.REGISTRY,new Identifier("hcclient:barvazy_emote"),new EmoteReward("Barvazy Emote",2, 89, EmoteManager.BARVAZY));
        Registry.register(Reward.REGISTRY,new Identifier("hcclient:darkvazy_emote"),new EmoteReward("Darkvazy Emote",2, 89, EmoteManager.DARKVAZY));

        Registry.register(Reward.REGISTRY,new Identifier("hcclient:jack_o_lantrail"),new ParticleReward("Jack-o-Lantrail",3, 138,JACK_O_CONTRAIL_PARTICLE));
        Registry.register(Reward.REGISTRY,new Identifier("hcclient:ruby_contrail"),new ParticleReward("Ruby Contrail",6, 138,RUBY_CONTRAIL));
        Registry.register(Reward.REGISTRY,new Identifier("hcclient:50_diamonds"),new ItemReward("x50 Diamonds",4, 89, new ItemStack(Items.DIAMOND,50)));

        Registry.register(Reward.REGISTRY,new Identifier("hcclient:pumpkin_cape"),new CapeReward("Pumpkin Cape",5,138,new Identifier("hcclient:textures/capes/pumpkin.png")));

        Items.BEETROOT.getFoodComponent().getStatusEffects().add(Pair.of(new StatusEffectInstance(StatusEffects.HASTE,30 * 20,1,false,true,true),1.0f));

        CommandRegistrationCallback.EVENT.register((d,a)->registerCommands(d));

        HandiDataGenerator.run();

        DataFixerBuilder builder = new DataFixerBuilder(DATA_VERSION);
        Schema v1 = builder.addSchema(1,PluginSchema::new);
        builder.addFixer(new BukkitEnchantmentsFix(v1,false));
        builder.addFixer(new BukkitXPFix(v1,false));
        DATA_FIXER = builder.build(Util.getBootstrapExecutor());

        ServerTickEvents.START_WORLD_TICK.register(serverWorld -> {
            if (serverWorld == serverWorld.getServer().getOverworld()) {
                ChallengesManager.get(serverWorld).tick(serverWorld.getServer());
            }
        });

        ServerWorldEvents.LOAD.register((minecraftServer, serverWorld) -> {
            if (serverWorld == minecraftServer.getOverworld()) {
                ChallengesManager.get(serverWorld).init();
            }
        });
    }

    private <T> void registerAll(Class<?> container, Class<T> type, Registry<T> registry) {
        registerAll(container,type,registry,null);
    }

    private <T> void registerAll(Class<?> container, Class<T> type, Registry<T> registry, TriConsumer<Field,Identifier,T> onRegistered) {
        for (Field f : container.getDeclaredFields()) {
            Register r = f.getAnnotation(Register.class);
            if (Modifier.isStatic(f.getModifiers()) && type.isAssignableFrom(f.getType()) && r != null) {
                try {
                    T t = type.cast(f.get(null));
                    Identifier id = new Identifier(r.value());
                    Registry.register(registry,id,t);
                    if (onRegistered != null) {
                        onRegistered.accept(f, id, t);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void registerBlockItem(Field f, Identifier id, Block block) {
        com.handicraft.client.util.BlockItem b = f.getAnnotation(com.handicraft.client.util.BlockItem.class);
        if (b != null) {
            Registry.register(Registry.ITEM,id,new net.minecraft.item.BlockItem(block,new Item.Settings().group(b.value().getGroup())));
        }
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("emote")
                .then(argument("emote", IdentifierArgumentType.identifier())
                        .suggests((ctx,builder)->CommandSource.suggestIdentifiers(EmoteManager.EMOTES,builder))
                        .executes(this::displayEmote)
                )
        );
        dispatcher.register(literal("challenges")
                .requires(src->src.hasPermissionLevel(2))
                .then(literal("reset")
                        .executes(ctx->{
                            ChallengesManager.get(ctx.getSource().getMinecraftServer().getOverworld()).reset();
                            ctx.getSource().sendFeedback(new LiteralText("Challenges has been reset!"),true);
                            return 1;
                        })
                )
                .then(literal("generate")
                        .executes(ctx->{
                            ChallengesManager.get(ctx.getSource().getMinecraftServer().getOverworld()).restock(ctx.getSource().getMinecraftServer());
                            ctx.getSource().sendFeedback(new LiteralText("Generated a new challenge!"),true);
                            return 1;
                        })
                )
        );
    }

    public static void updateNBT(CompoundTag tag, CompoundTag to) {
        tag.copyFrom(to);
        for (String k : new ArrayList<>(tag.getKeys())) {
            if (!to.contains(k)) {
                tag.remove(k);
            }
            if (to.contains(k, NbtType.COMPOUND)) {
                updateNBT(tag.getCompound(k),to.getCompound(k));
            }
        }
    }

    public static boolean isHandicraft(@Nullable ServerInfo address) {
        if (address == HANDICRAFT_SERVER) return true;
        return HANDICRAFT_SERVER.address.equalsIgnoreCase(address.address);
    }

    private int displayEmote(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier emote = IdentifierArgumentType.getIdentifier(ctx,"emote");
        EmoteManager.sendEmote(ctx.getSource().getPlayer(),emote,350);
        return 1;
    }
}
