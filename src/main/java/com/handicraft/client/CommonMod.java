/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.handicraft.client.block.ColoredWaterBlock;
import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.block.entity.CashRegisterBlockEntity;
import com.handicraft.client.block.entity.CauldronBlockEntity;
import com.handicraft.client.block.entity.NetheriteFurnaceBlockEntity;
import com.handicraft.client.challenge.ChallengesManager;
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
import com.handicraft.client.item.CandySmeltingRecipe;
import com.handicraft.client.item.ModItems;
import com.handicraft.client.rewards.*;
import com.handicraft.client.screen.*;
import com.handicraft.client.screen.cash_register.CashRegisterOwnerHandler;
import com.handicraft.client.screen.cash_register.CashRegisterScreenHandler;
import com.handicraft.client.util.Register;
import com.mojang.brigadier.CommandDispatcher;
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
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class CommonMod implements ModInitializer {

    public static final int SEASON = 2;
    public static final int VERSION = 4;

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
        return new EnderChestScreenHandler(i,playerInventory,new EnderChestInventory(),stored, null, null);
    });

    public static final BlockEntityType<NetheriteFurnaceBlockEntity> NETHERITE_FURNACE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder.create(NetheriteFurnaceBlockEntity::new, ModBlocks.NETHERITE_FURNACE).build();

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

    public static final List<String> ALTS = new ArrayList<>();

    public static final AtomicReference<MinecraftServer> SERVER = new AtomicReference<>();


    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(3)));
    public static final RegistryKey<World> DARKNESS_KEY = RegistryKey.of(Registry.WORLD_KEY, new Identifier("handicraft:darkness"));
    //public static final BlockEntityType<SpeakerBlockEntity> SPEAKER_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.create(SpeakerBlockEntity::new,ModBlocks.SPEAKER_BLOCK).build(null);
    public static final Identifier UPDATE_SPEAKER = new Identifier("hcclient:update_speaker");
    public static final Identifier OPEN_SPEAKER = new Identifier("hcclient:open_speaker");

    public static final GameStateChangeS2CPacket.Reason ALWAYS_SNOW_CHANGED = new GameStateChangeS2CPacket.Reason(12);
    public static final GameRules.Key<GameRules.BooleanRule> DO_ALWAYS_SNOW = GameRuleRegistry.register("doAlwaysSnow", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false, (server,b)->{
        server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(ALWAYS_SNOW_CHANGED,b.get() ? 1 : 0));
    }));
    public static final BlockEntityType<CashRegisterBlockEntity> CASH_REGISTER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(CashRegisterBlockEntity::new,ModBlocks.CASH_REGISTER).build();
    public static final ScreenHandlerType<CashRegisterScreenHandler> CASH_REGISTER_SCREEN = ScreenHandlerRegistry.registerExtended(new Identifier("hcclient:cash_register"),(i, playerInventory, packetByteBuf) -> new CashRegisterScreenHandler(i,playerInventory,new SimpleInventory(54),packetByteBuf.readVarInt()));
    public static final ScreenHandlerType<CashRegisterOwnerHandler> CASH_REGISTER_OWNER_SCREEN = ScreenHandlerRegistry.registerExtended(new Identifier("hcclient:cash_register_owner"),(i, playerInventory, packetByteBuf) -> new CashRegisterOwnerHandler(i,playerInventory,new SimpleInventory(54),packetByteBuf));
    public static final BlockEntityType<CauldronBlockEntity> CAULDRON_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(CauldronBlockEntity::new, Blocks.CAULDRON).build();

    public static Text getCurrentWindowTitle() {
        return Objects.requireNonNull(MinecraftClient.getInstance().currentScreen).getTitle();
    }

    public static int invulTime() {
        return 13;
    }

    @Override
    public void onInitialize() {

        //region Auto Registries
        registerAll(ModSounds.class,SoundEvent.class,Registry.SOUND_EVENT,null, SoundEvent::new);
        registerAll(ModItems.class,Item.class,Registry.ITEM);
        registerAll(ModBlocks.class,Block.class,Registry.BLOCK,this::registerBlockItem,null);
        registerAll(ModPotions.class,Potion.class,Registry.POTION);
        //endregion

        ModFluids.register();

        ColoredWaterBlock.registerAll();

        //region Enchantments
        Registry.register(Registry.ENCHANTMENT,new Identifier("heat_walker"),HEAT_WALKER);
        Registry.register(Registry.ENCHANTMENT,new Identifier("farming_feet"),FARMING_FEET);
        //Registry.register(Registry.ENCHANTMENT,new Identifier("present_collector"),PRESENT_COLLECTOR);
        //endregion

        //region Particles
        Registry.register(Registry.PARTICLE_TYPE,new Identifier("hcclient:jack_o_contrail"),JACK_O_CONTRAIL_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE,new Identifier("hcclient:ruby_contrail"),RUBY_CONTRAIL);
        Registry.register(Registry.PARTICLE_TYPE,new Identifier("hcclient:herobrine_contrail"),HEROBRINE_TRAIL);
        //endregion

        //region Block Entities
        Registry.register(Registry.BLOCK_ENTITY_TYPE,new Identifier("hcclient:netherite_furnace"),NETHERITE_FURNACE_BLOCK_ENTITY_TYPE);
        //Registry.register(Registry.BLOCK_ENTITY_TYPE,new Identifier("hcclient:speaker_block"), SPEAKER_BLOCK_ENTITY_TYPE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE,new Identifier("hcclient:cash_register"), CASH_REGISTER_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK_ENTITY_TYPE,new Identifier("minecraft:cauldron"), CAULDRON_BLOCK_ENTITY);
        //endregion

        RecipeSerializer.register("cooking_special_candy",CANDY_RECIPE_SERIALIZER);

        //region Entities
        Registry.register(Registry.ENTITY_TYPE,new Identifier("darkness_wizard"),DARKNESS_WIZARD);
        FabricDefaultAttributeRegistry.register(DARKNESS_WIZARD,DarknessWizardEntity.createIllusionerAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH,200).add(EntityAttributes.GENERIC_FOLLOW_RANGE,48).add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,3f));

        Registry.register(Registry.ENTITY_TYPE,new Identifier("dark_blaze"),DARK_BLAZE);
        Registry.register(Registry.ENTITY_TYPE,new Identifier("dark_pillager"),DARK_PILLAGER);
        //endregion

        Items.BEETROOT.getFoodComponent().getStatusEffects().add(Pair.of(new StatusEffectInstance(StatusEffects.HASTE,30 * 20,1,false,true,true),1.0f));


        //region Rewards
        //Reward.register(new Identifier("hcclient:spooky_disc"), new ItemReward("Spooky Music Disc", 1, 138, new ItemStack(ModItems.SPOOKY_MUSIC_DISC)));
        Reward.register(new Identifier("hcclient:jack_o_lantrail"), new ParticleReward("Jack-o-Lantrail", 5, 138, Collectibles.PUMPKIN_TRAIL));
        Reward.register(new Identifier("hcclient:50_diamonds"), new ItemReward("x50 Diamonds", 10, 89, new ItemStack(Items.DIAMOND, 50)));
        Reward.register(new Identifier("hcclient:pumpkin_cape"), new CapeReward("Pumpkin Cape", 15, 138, Collectibles.PUMPKIN_CAPE));

        //Reward.register(new Identifier("hcclient:10_ruby_blocks"), new ItemReward("x10 Ruby Blocks", 9, 89, new ItemStack(ModBlocks.RUBY_BLOCK, 10)));
        Reward.register(new Identifier("hcclient:herobrine_contrail"), new ParticleReward("Herobrine Trail", 20, 138, Collectibles.HEROBRINE_TRAIL));
        //Reward.register(new Identifier("hcclient:spooky_scary_skeletons"), new ItemReward("Spooky Scary Skeletons Disc", 25, 138, new ItemStack(ModItems.SPOOKY_SCARY_SKELETONS_DISC)));
        Reward.register(new Identifier("hcclient:ruby_cape"), new CapeReward("Ruby Cape", 25, 138, Collectibles.RUBY_CAPE));

        //Reward.register(new Identifier("hcclient:herobrine_emote"), new EmoteReward("Herobrine Emote", 30, 89, EmoteManager.HEROBRINE));
        Reward.register(new Identifier("hcclient:ruby_contrail"), new ParticleReward("Ruby Trail", 30, 138, Collectibles.RUBY_TRAIL));
        Reward.register(new Identifier("hcclient:5_netherite"), new ItemReward("x5 Netherite Ingots", 35, 89, new ItemStack(Items.NETHERITE_INGOT, 5)));
        //Reward.register(new Identifier("hcclient:atla_disc"), new ItemReward("Avatar Music Disc", 27, 138, new ItemStack(ModItems.AVATAR_DISC)));
        Reward.register(new Identifier("hcclient:bat_cape"), new CapeReward("Bat Cape", 45, 138, Collectibles.BAT_CAPE));
        Reward.register(new Identifier("hcclient:sunset_cape"), new CapeReward("Sunset Cape", 55, 138, Collectibles.SUNSET_CAPE));

        //endregion

        //region Events
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER.set(server);
            File altsFile = new File(server.getRunDirectory(),"alts.json");
            if (altsFile.exists()) {
                try {
                    JsonElement element = new Gson().fromJson(new BufferedReader(new FileReader(altsFile)),JsonElement.class);
                    if (element.isJsonArray()) {
                        for (JsonElement e : element.getAsJsonArray()) {
                            ALTS.add(e.getAsString());
                        }
                        return;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                try {
                    altsFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JsonArray arr = new JsonArray();
            arr.add("Arbel2008");
            arr.add("TheSecondBunny");
            arr.add("RotmansCamera");
            arr.add("Barvazy");
            try {
                Gson gson = new Gson();
                JsonWriter w = new JsonWriter(new FileWriter(altsFile));
                w.setIndent("    ");
                gson.toJson(arr,w);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


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
                    if (!playerEntity.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                    return TypedActionResult.success(stack);
                }
            }
            return TypedActionResult.pass(stack);
        });
        //endregion

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_CAPE_TEXTURE,(server, player, h, buf, sender)->{
            UUID id = buf.readUuid();

            Cape cape = (Cape) PlayerCollectibles.of(SERVER.get().getPlayerManager().getPlayer(id)).getSelected(CollectibleType.CAPE);
            PacketByteBuf buff = new PacketByteBuf(Unpooled.buffer());
            buff.writeUuid(id);
            if (cape != null) {
                buff.writeBoolean(true);
                buff.writeIdentifier(cape.getTextureId());
            } else {
                buff.writeBoolean(false);
            }

            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,RESPONSE_CAPE_TEXTURE,buff);
        });

        ServerPlayNetworking.registerGlobalReceiver(CHANGE_STORED_XP,XPStorageHelper::update);

        ServerPlayNetworking.registerGlobalReceiver(CashRegisterScreenHandler.ADMIN_LOGIN,(server, player, h, buf, sender) -> {
            if (player.currentScreenHandler instanceof CashRegisterScreenHandler) {
                ((CashRegisterScreenHandler) player.currentScreenHandler).tryLogin(player,buf.readString());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(CashRegisterOwnerHandler.UPDATE_COST,(server, player, h, buf, sender) -> {
            if (player.currentScreenHandler instanceof CashRegisterOwnerHandler) {
                ((CashRegisterOwnerHandler) player.currentScreenHandler).setCost(buf.readVarInt());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(CashRegisterOwnerHandler.CHANGE_PASSWORD,(server, player, h, buf, sender) -> {
            if (player.currentScreenHandler instanceof CashRegisterOwnerHandler) {
                ((CashRegisterOwnerHandler) player.currentScreenHandler).setPassword(buf.readString());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(PlayerCollectibles.CLAIM_REWARD,(server, player, h, buf, sender)->{
            Reward r = Reward.REGISTRY.get(buf.readVarInt());
            PlayerCollectibles.of(player).claim(player,r);
        });

        ServerPlayNetworking.registerGlobalReceiver(PlayerCollectibles.SELECT_COLLECTIBLE,(server, player, h, buf, sender)->{
            CollectibleType type = buf.readEnumConstant(CollectibleType.class);
            Collectible c = null;
            if (buf.readBoolean()) {
                c = Collectibles.REGISTRY.get(buf.readVarInt());
            }
            PlayerCollectibles.of(player).select(player,type,c);
        });


        /*DispenserBlock.registerBehavior(Items.POTION, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                BlockState state = pointer.getWorld().getBlockState(pos);
                if (state.isOf(Blocks.CAULDRON)) {
                    CauldronBlockEntity be = (CauldronBlockEntity) pointer.getWorld().getBlockEntity(pos);
                    int level = state.get(CauldronBlock.LEVEL);
                    Potion potion = PotionUtil.getPotion(stack);
                    if (level < 3 && (level == 0 || be.canFillWith(potion))) {
                        pointer.getWorld().setBlockState(pos,state.with(CauldronBlock.LEVEL,level + 1),2);
                        pointer.getWorld().updateComparators(pos,Blocks.CAULDRON);

                        be.setPotion(potion);

                        setSuccess(true);
                        stack.decrement(1);
                        if (stack.isEmpty()) {
                            return new ItemStack(Items.GLASS_BOTTLE);
                        }
                    } else {
                        setSuccess(false);
                    }
                    return stack;
                } else {
                    setSuccess(true);
                    return super.dispenseSilently(pointer, stack);
                }
            }
        });


        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE, new FallibleItemDispenserBehavior() {

            private DispenserBehavior defaultBehavior = new ItemDispenserBehavior();

            private ItemStack fillBottle(BlockPointer blockPointer, ItemStack emptyBottleStack, ItemStack filledBottleStack) {
                emptyBottleStack.decrement(1);
                if (emptyBottleStack.isEmpty()) {
                    return filledBottleStack.copy();
                } else {
                    if (((DispenserBlockEntity)blockPointer.getBlockEntity()).addToFirstFreeSlot(filledBottleStack.copy()) < 0) {
                        this.defaultBehavior.dispense(blockPointer, filledBottleStack.copy());
                    }

                    return emptyBottleStack;
                }
            }

            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                this.setSuccess(false);
                ServerWorld serverWorld = pointer.getWorld();
                BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                BlockState blockState = serverWorld.getBlockState(blockPos);
                if (blockState.isOf(Blocks.CAULDRON) && blockState.get(CauldronBlock.LEVEL) > 0) {
                    CauldronBlockEntity be = (CauldronBlockEntity) serverWorld.getBlockEntity(blockPos);
                    Potion potion = be.getPotion();
                    int level = blockState.get(CauldronBlock.LEVEL);
                    if (level == 1) {
                        be.setPotion(Potions.WATER);
                        be.setWaterColor(null);
                    }
                    serverWorld.setBlockState(blockPos,blockState.with(CauldronBlock.LEVEL,level - 1),2);
                    setSuccess(true);
                    return fillBottle(pointer, stack, PotionUtil.setPotion(new ItemStack(Items.POTION),potion));
                } else if (blockState.method_27851(BlockTags.BEEHIVES, (state) -> state.contains(BeehiveBlock.HONEY_LEVEL)) && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
                    ((BeehiveBlock)blockState.getBlock()).takeHoney(serverWorld, blockState, blockPos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
                    this.setSuccess(true);
                    return this.fillBottle(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
                } else if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                    this.setSuccess(true);
                    return this.fillBottle(pointer, stack, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                } else {
                    return super.dispenseSilently(pointer, stack);
                }
            }
        });*/


        if (FabricLoader.getInstance().isDevelopmentEnvironment() && "true".equalsIgnoreCase(System.getProperty("data"))) {
            HandiDataGenerator.run();
            System.exit(0);
        }

        //StateRefresher.INSTANCE.addBlockProperty(Blocks.OAK_LEAVES, Properties.SNOWY, false);
    }

    /**
     * Registers all fields in the provided class to a registry. Only fields annotated with {@link Register} will be registered, under the assigned {@link Register#value() ID}.
     * @param container The class containing all of the fields
     * @param type The base object type that can be registered
     * @param registry The registry to register to
     * @param <T> The type of the objects
     */
    public <T> void registerAll(Class<?> container, Class<T> type, Registry<T> registry) {
        registerAll(container,type,registry,null,null);
    }

    /**
     * Registers all fields in the provided class to a registry. Only fields annotated with {@link Register} will be registered, under the assigned {@link Register#value() ID}.
     * @param container The class containing all of the fields
     * @param type The base object type that can be registered
     * @param registry The registry to register to
     * @param onRegistered A callback that will be called once an object is registered
     * @param defaultFactory A default value factory to assign to a field if it has not been initialized.
     * @param <T> The type of the objects
     */
    public <T> void registerAll(Class<?> container, Class<T> type, Registry<T> registry, TriConsumer<Field,Identifier,T> onRegistered, Function<Identifier,T> defaultFactory) {
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
        new CollectiblesCommand().register(dispatcher);
        //new KillExtendedCommand().register(dispatcher);
    }


}
