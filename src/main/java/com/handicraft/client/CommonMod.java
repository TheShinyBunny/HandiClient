/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client;

import com.handicraft.client.block.ColoredWaterBlock;
import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.block.entity.NetheriteFurnaceBlockEntity;
import com.handicraft.client.challenge.Challenge;
import com.handicraft.client.challenge.ChallengesManager;
import com.handicraft.client.challenge.PlayerChallenges;
import com.handicraft.client.challenge.ServerChallenge;
import com.handicraft.client.collectibles.*;
import com.handicraft.client.commands.*;
import com.handicraft.client.data.HandiDataGenerator;
import com.handicraft.client.emotes.EmoteManager;
import com.handicraft.client.enchantments.FarmingFeetEnchantment;
import com.handicraft.client.enchantments.HeatWalkerEnchantment;
import com.handicraft.client.entity.DarkBlazeEntity;
import com.handicraft.client.entity.DarkPillagerEntity;
import com.handicraft.client.entity.DarknessWizardEntity;
import com.handicraft.client.fluid.ModFluids;
import com.handicraft.client.gen.structure.DarkFortressStructure;
import com.handicraft.client.gen.structure.DarkTempleStructure;
import com.handicraft.client.gen.structure.ModStructurePieces;
import com.handicraft.client.item.CandySmeltingRecipe;
import com.handicraft.client.item.ModItems;
import com.handicraft.client.rewards.*;
import com.handicraft.client.screen.*;
import com.handicraft.client.util.Register;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
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
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.impl.object.builder.FabricEntityType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.tag.Tag;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.util.TriConsumer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommonMod implements ModInitializer {


    public static final DefaultParticleType JACK_O_CONTRAIL_PARTICLE = FabricParticleTypes.simple(true);
    public static final DefaultParticleType RUBY_CONTRAIL = FabricParticleTypes.simple(true);
    public static final DefaultParticleType HEROBRINE_TRAIL = FabricParticleTypes.simple(true);
    public static final SpecialRecipeSerializer<? extends Recipe<?>> CANDY_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(CandySmeltingRecipe::new);

    public static final GameRules.Key<GameRules.BooleanRule> ADVANCEMENT_GAME_RULE = GameRuleRegistry.register("advancements", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

    public static final Identifier CHANGE_STORED_XP = new Identifier("hcclient:store_xp");
    public static final HeatWalkerEnchantment HEAT_WALKER = new HeatWalkerEnchantment();
    public static final FarmingFeetEnchantment FARMING_FEET = new FarmingFeetEnchantment();

    public static final ScreenHandlerType<EnderChestScreenHandler> ENDER_CHEST_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("hcclient:enderchest"),(i, playerInventory, packetByteBuf) -> {
        int stored = packetByteBuf.readVarInt();
        return new EnderChestScreenHandler(i,playerInventory,new EnderChestInventory(),stored);
    });

    public static final BlockEntityType<NetheriteFurnaceBlockEntity> NETHERITE_FURNACE_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.create(NetheriteFurnaceBlockEntity::new, ModBlocks.NETHERITE_FURNACE).build(null);

    public static final ScreenHandlerType<NetheriteFurnaceScreenHandler> NETHERITE_FURNACE_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier("hcclient:netherite_furnace"),NetheriteFurnaceScreenHandler::new);

    public static final Map<PlayerEntity, Text> currentTitles = new HashMap<>();
    public static final ScreenHandlerType<ShulkerPreviewScreenHandler> SHULKER_PREVIEW_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("hcclient","shulker_preview"), ShulkerPreviewScreenHandler::new);

    public static final ScreenHandlerType<ItemClaimScreenHandler> ITEM_CLAIM_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier("hcclient:item_claim"), ItemClaimScreenHandler::new);

    public static final ScreenHandlerType<CandyBucketScreenHandler> CANDY_BUCKET_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("hcclient:candy_bucket"), CandyBucketScreenHandler::new);

    public static final EntityType<DarknessWizardEntity> DARKNESS_WIZARD = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER,DarknessWizardEntity::new).dimensions(new EntityDimensions(0.6F, 1.95F,true)).trackRangeBlocks(48).fireImmune().build();
    public static final EntityType<DarkBlazeEntity> DARK_BLAZE = FabricEntityTypeBuilder.<DarkBlazeEntity>createMob().entityFactory(DarkBlazeEntity::new).spawnGroup(SpawnGroup.MONSTER).spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlazeEntity::canSpawnIgnoreLightLevel).defaultAttributes(DarkBlazeEntity::createDarkBlazeAttributes).dimensions(new EntityDimensions(0.6F, 1.8F,true)).trackRangeChunks(8).fireImmune().build();
    public static final EntityType<DarkPillagerEntity> DARK_PILLAGER = FabricEntityTypeBuilder.<DarkPillagerEntity>createMob().entityFactory(DarkPillagerEntity::new).defaultAttributes(DarkPillagerEntity::createDarkPillagerAttributes).dimensions(new EntityDimensions(0.6f,1.8f,true)).spawnGroup(SpawnGroup.MONSTER).spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PillagerEntity::canSpawnIgnoreLightLevel).build();

    public static final Identifier REQUEST_CAPE_TEXTURE = new Identifier("hcclient:request_cape");
    public static final Identifier RESPONSE_CAPE_TEXTURE = new Identifier("hcclient:response_cape");

    public static final List<String> ALTS = Arrays.asList("TheSecondBunny","RotmansCamera","Arbel2008","Barvazy");

    public static final StructureFeature<DefaultFeatureConfig> DARK_TEMPLE = new DarkTempleStructure();
    public static final StructureFeature<DefaultFeatureConfig> DARK_FORTRESS = new DarkFortressStructure();

    public static final AtomicReference<MinecraftServer> SERVER = new AtomicReference<>();

    public static final int SEASON = 1;
    public static final int VERSION = 3;

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(3)));
    public static final RegistryKey<World> DARKNESS_KEY = RegistryKey.of(Registry.DIMENSION, new Identifier("handicraft:darkness"));


    public static float capeModifier() {
        return 42;
    }

    public static Text getCurrentWindowTitle() {
        return Objects.requireNonNull(MinecraftClient.getInstance().currentScreen).getTitle();
    }

    public static int invulTime() {
        return 13;
    }

    @Override
    public void onInitialize() {

        registerAll(ModSounds.class,SoundEvent.class,Registry.SOUND_EVENT,null, SoundEvent::new);
        registerAll(ModItems.class,Item.class,Registry.ITEM);
        registerAll(ModBlocks.class,Block.class,Registry.BLOCK,this::registerBlockItem,null);
        registerAll(ModPotions.class,Potion.class,Registry.POTION);

        //registerAll(ModFluids.class, Fluid.class,Registry.FLUID);
        ModFluids.register();

        ColoredWaterBlock.registerAll();

        Registry.register(Registry.ENCHANTMENT,new Identifier("heat_walker"),HEAT_WALKER);
        Registry.register(Registry.ENCHANTMENT,new Identifier("farming_feet"),FARMING_FEET);

        Registry.register(Registry.PARTICLE_TYPE,new Identifier("hcclient:jack_o_contrail"),JACK_O_CONTRAIL_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE,new Identifier("hcclient:ruby_contrail"),RUBY_CONTRAIL);
        Registry.register(Registry.PARTICLE_TYPE,new Identifier("hcclient:herobrine_contrail"),HEROBRINE_TRAIL);

        Registry.register(Registry.BLOCK_ENTITY_TYPE,new Identifier("hcclient:netherite_furnace"),NETHERITE_FURNACE_BLOCK_ENTITY_TYPE);

        RecipeSerializer.register("cooking_special_candy",CANDY_RECIPE_SERIALIZER);

        Registry.register(Registry.ENTITY_TYPE,new Identifier("darkness_wizard"),DARKNESS_WIZARD);
        FabricDefaultAttributeRegistry.register(DARKNESS_WIZARD,DarknessWizardEntity.createIllusionerAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH,200).add(EntityAttributes.GENERIC_FOLLOW_RANGE,48).add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,3f));

        Registry.register(Registry.ENTITY_TYPE,new Identifier("dark_blaze"),DARK_BLAZE);
        Registry.register(Registry.ENTITY_TYPE,new Identifier("dark_pillager"),DARK_PILLAGER);

        Registry.register(Registry.STRUCTURE_PIECE,new Identifier("handicraft","dark_temple_piece"), ModStructurePieces.DARK_TEMPLE_PIECE_TYPE);
        FabricStructureBuilder.create(new Identifier("handicraft","dark_temple"),DARK_TEMPLE)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(30,10,27842)
                .register();

        FabricStructureBuilder.create(new Identifier("handicraft","dark_fortress"),DARK_FORTRESS)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(20,15,238947)
                .register();

        ServerSidePacketRegistry.INSTANCE.register(REQUEST_CAPE_TEXTURE,(ctx,buf)->{
            UUID id = buf.readUuid();

            Cape cape = PlayerCollectibles.of(SERVER.get().getPlayerManager().getPlayer(id)).getSelected(CollectibleType.CAPE);
            PacketByteBuf buff = new PacketByteBuf(Unpooled.buffer());
            buff.writeUuid(id);
            if (cape != null) {
                buff.writeBoolean(true);
                buff.writeIdentifier(cape.getTextureId());
            } else {
                buff.writeBoolean(false);
            }

            ServerSidePacketRegistry.INSTANCE.sendToPlayer(ctx.getPlayer(),RESPONSE_CAPE_TEXTURE,buff);
        });

        ServerSidePacketRegistry.INSTANCE.register(CHANGE_STORED_XP,XPStorageHelper::update);

        ServerLifecycleEvents.SERVER_STARTED.register(SERVER::set);

        Reward.register(new Identifier("hcclient:spooky_disc"), new ItemReward("Spooky Music Disc", 1, 138, new ItemStack(ModItems.SPOOKY_MUSIC_DISC)));
        Reward.register(new Identifier("hcclient:barvazy_emote"), new EmoteReward("Barvazy Emote", 2, 89, EmoteManager.BARVAZY));
        Reward.register(new Identifier("hcclient:darkvazy_emote"), new EmoteReward("Darkvazy Emote", 2, 89, EmoteManager.DARKVAZY));
        Reward.register(new Identifier("hcclient:jack_o_lantrail"), new ParticleReward("Jack-o-Lantrail", 3, 138, Collectibles.PUMPKIN_TRAIL));
        Reward.register(new Identifier("hcclient:50_diamonds"), new ItemReward("x50 Diamonds", 4, 89, new ItemStack(Items.DIAMOND, 50)));
        Reward.register(new Identifier("hcclient:pumpkin_cape"), new CapeReward("Pumpkin Cape", 5, 138, Collectibles.PUMPKIN_CAPE));

        Reward.register(new Identifier("hcclient:skeleton_emote"), new EmoteReward("Skeleton Emote", 7, 89, EmoteManager.SKELETON));
        Reward.register(new Identifier("hcclient:10_ruby_blocks"), new ItemReward("x10 Ruby Blocks", 9, 89, new ItemStack(ModBlocks.RUBY_BLOCK, 10)));
        Reward.register(new Identifier("hcclient:herobrine_contrail"), new ParticleReward("Herobrine Trail", 11, 138, Collectibles.HEROBRINE_TRAIL));
        Reward.register(new Identifier("hcclient:spooky_scary_skeletons"), new ItemReward("Spooky Scary Skeletons Disc", 13, 138, new ItemStack(ModItems.SPOOKY_SCARY_SKELETONS_DISC)));
        Reward.register(new Identifier("hcclient:ruby_cape"), new CapeReward("Ruby Cape", 15, 138, Collectibles.RUBY_CAPE));

        Reward.register(new Identifier("hcclient:herobrine_emote"), new EmoteReward("Herobrine Emote", 18, 89, EmoteManager.HEROBRINE));
        Reward.register(new Identifier("hcclient:ruby_contrail"), new ParticleReward("Ruby Trail", 21, 138, Collectibles.RUBY_TRAIL));
        Reward.register(new Identifier("hcclient:5_netherite"), new ItemReward("x5 Netherite Ingots", 24, 89, new ItemStack(Items.NETHERITE_INGOT, 5)));
        Reward.register(new Identifier("hcclient:atla_disc"), new ItemReward("Avatar Music Disc", 27, 138, new ItemStack(ModItems.AVATAR_DISC)));
        Reward.register(new Identifier("hcclient:bat_cape"), new CapeReward("Bat Cape", 30, 138, Collectibles.BAT_CAPE));


        Items.BEETROOT.getFoodComponent().getStatusEffects().add(Pair.of(new StatusEffectInstance(StatusEffects.HASTE,30 * 20,1,false,true,true),1.0f));

        CommandRegistrationCallback.EVENT.register((d,a)->registerCommands(d));

        ServerTickEvents.START_WORLD_TICK.register(serverWorld -> {
            if (serverWorld == serverWorld.getServer().getOverworld()) {
                ChallengesManager.get(serverWorld).tick();
            }
        });

        ServerWorldEvents.LOAD.register((minecraftServer, serverWorld) -> {
            if (serverWorld == minecraftServer.getOverworld()) {
                ChallengesManager.get(serverWorld).init();
            }
        });

        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            ItemStack stack = playerEntity.getStackInHand(hand);
            if (stack.getItem() instanceof DyeItem) {
                DyeColor color = ((DyeItem) stack.getItem()).getColor();
                if (ColoredWaterBlock.usedColor(playerEntity,world,color)) {
                    if (!playerEntity.abilities.creativeMode) {
                        stack.decrement(1);
                    }
                    return TypedActionResult.success(stack);
                }
            }
            return TypedActionResult.pass(stack);
        });

        ServerSidePacketRegistry.INSTANCE.register(PlayerCollectibles.CLAIM_REWARD,(ctx,buf)->{
            Reward r = Reward.REGISTRY.get(buf.readVarInt());
            PlayerCollectibles.of((ServerPlayerEntity) ctx.getPlayer()).claim(ctx.getPlayer(),r);
        });

        ServerSidePacketRegistry.INSTANCE.register(PlayerCollectibles.SELECT_COLLECTIBLE,(ctx,buf)->{
            CollectibleType<?> type = CollectibleType.byIndex(buf.readVarInt());
            Collectible c = null;
            if (buf.readBoolean()) {
                c = Collectibles.REGISTRY.get(buf.readVarInt());
            }
            PlayerCollectibles.of((ServerPlayerEntity) ctx.getPlayer()).select(ctx.getPlayer(),type,c);
        });

        if (FabricLoader.getInstance().isDevelopmentEnvironment() && "true".equalsIgnoreCase(System.getProperty("data"))) {
            HandiDataGenerator.run();
            System.exit(0);
        }

    }

    private <T> void registerAll(Class<?> container, Class<T> type, Registry<T> registry) {
        registerAll(container,type,registry,null,null);
    }

    private <T> void registerAll(Class<?> container, Class<T> type, Registry<T> registry, TriConsumer<Field,Identifier,T> onRegistered, Function<Identifier,T> defaultFactory) {
        for (Field f : container.getDeclaredFields()) {
            Register r = f.getAnnotation(Register.class);
            if (Modifier.isStatic(f.getModifiers()) && type.isAssignableFrom(f.getType()) && r != null) {
                try {
                    Identifier id = new Identifier(r.value());
                    f.setAccessible(true);
                    Object o = f.get(null);
                    if (o == null) {
                        if (defaultFactory == null) continue;
                        o = defaultFactory.apply(id);
                        f.set(null,o);
                    }
                    T t = type.cast(o);

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
            Registry.register(Registry.ITEM,id,new BlockItem(block,new Item.Settings().group(b.value().getGroup())));
        }
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new ClaimCommand().register(dispatcher);
        new ChallengesCommand().register(dispatcher);
        new EmoteCommand().register(dispatcher);
        new HandipassCommand().register(dispatcher);
        new LockerCommand().register(dispatcher);
        new PingCommand().register(dispatcher);
        new CanStructGenCommand().register(dispatcher);
    }


}
