/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.CommonMod;
import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.challenge.objectives.*;
import com.handicraft.client.util.HandiUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.world.PersistentState;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChallengesManager extends PersistentState {

    private List<ChallengeBuilder<?>> challengeRepository = new ArrayList<>();

    private List<ServerChallenge<?>> challenges;
    private long lastRestockTime;
    private int season = 1;

    private ChallengesManager() {
        super("challenges");
        challenges = new ArrayList<>();
        registerListeners();

        addChallenge(1,new CraftItemObjective.Instance(new ItemStack(ModBlocks.NETHERITE_FURNACE),"Netherite Furnace(s)"),1,1);
        addChallenge(2,Objectives.PAINT_WATER.create("Paint water in any color",new ItemStack(Items.WATER_BUCKET)),1,1);
        addChallenge(3,new CraftItemObjective.Instance(new ItemStack(Blocks.JACK_O_LANTERN),"Jack o' Lantern(s)"),250,1);
        addChallenge(4,new GetMobHeadObjective.Instance(DynamicSerializableUuid.toUuid(new int[]{660946143,-1861662693,-1885387128,-1784114526}),"Bat","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGEyOWM5ZWNlNDI4ZmEyMzM5NWFjMjAxOWJmMmQwMjc0NDA1MjlmMjUzM2ZjODIwMWU3YjNkYTBmNjBmMjAwNSJ9fX0="),1,1);
        addChallenge(5,new KillEntityObjective.Instance(EntityType.SQUID,"Kill 200 Squids",new ItemStack(Items.INK_SAC)),200,1);
        addChallenge(6,new KillEntityObjective.Instance(EntityType.VEX,"Kill 25 Vexes",new ItemStack(Items.IRON_SWORD)),25,1);
        addChallenge(7,Objectives.NETHERITE_SMELT.create("Smelt 500 items in a Netherite Furnace and take them",new ItemStack(ModBlocks.NETHERITE_FURNACE)),500,1);
        addChallenge(8,new SmeltObjective.Instance(new ItemStack(Items.ANCIENT_DEBRIS),new ItemStack(Items.NETHERITE_SCRAP),"Smelt 30 Ancient Debris"),30,1);
        addChallenge(9,new KillEntityObjective.Instance(EntityType.IRON_GOLEM,"Kill 10 Iron Golems",new ItemStack(Items.IRON_INGOT)),10,1);
        addChallenge(10,Objectives.TRADE.create("Trade with a villager 70 times",new ItemStack(Items.EMERALD)),70,1);

        addChallenge(11,new BreakBlockObjective.Instance(Blocks.STONE,"Stone"),2000,1);
        addChallenge(12,new KillEntityObjective.Instance(Entity::isInvisible,"Kill 10 invisible mobs with Spectral Arrows",new ItemStack(Items.SPECTRAL_ARROW), s->s.getSource() instanceof SpectralArrowEntity),10,1);
        addChallenge(13,new WinRaidObjective.Instance(3),1,1);
        addChallenge(14,new KillEntityObjective.Instance(EntityType.SPIDER,"Kill 20 Spiders",new ItemStack(Items.SPIDER_EYE)),20,1);
        addChallenge(15,new CraftItemObjective.Instance(new ItemStack(Items.ENDER_CHEST),"Ender Chests"),10,1);
        addChallenge(16,new FishObjective.Instance(new ItemStack(Items.ENCHANTED_BOOK),"Enchanted Book(s)"),3,1);
        addChallenge(17,new CraftItemObjective.Instance(new ItemStack(Items.END_CRYSTAL),"End Crystal(s)"),4,1);
        addChallenge(18,Objectives.GET_DRAGONS_BREATH.create("Collect 50 Dragon's Breath",new ItemStack(Items.DRAGON_BREATH)),50,1);
        addChallenge(19,new KillEntityObjective.Instance(EntityType.ENDER_DRAGON,"Kill an Ender Dragon",new ItemStack(Items.DRAGON_HEAD)),1,1);
        addChallenge(20,Objectives.CURE_ZOMBIE.create("Cure 5 Zombie Villagers",new ItemStack(Items.GOLDEN_APPLE)),5,1);

        addChallenge(21,new StripLogObjective.Instance(b->b.isIn(BlockTags.LOGS),new ItemStack(Items.DIAMOND_AXE),"Strip 300 log blocks"),300,1);
        addChallenge(22,new SmeltObjective.Instance(new ItemStack(Items.CLAY_BALL),new ItemStack(Items.BRICK),"Smelt 100 Clay Balls into Bricks"),100,1);
        addChallenge(23,new BreedObjective.Instance(EntityType.PANDA,new ItemStack(Items.BAMBOO),"Breed between 2 Pandas"),1,1);
        addChallenge(24,new KillEntityObjective.Instance(e->true,"Kill 30 mobs with a Crossbow",new ItemStack(Items.CROSSBOW),s->s.getSource() instanceof ArrowEntity && ((ArrowEntity) s.getSource()).isShotFromCrossbow()),30,1);
        addChallenge(25,new KillEntityObjective.Instance(EntityType.HUSK,"Kill 30 Husks",new ItemStack(Items.ROTTEN_FLESH)),30,1);
        addChallenge(26,new KillEntityObjective.Instance(EntityType.BLAZE,"Kill 15 Blazes",new ItemStack(Items.BLAZE_ROD)),15,1);
        addChallenge(27,new BlockDamageObjective.Instance(s->s.getAttacker() instanceof SkeletonEntity,"Block 50 Skeleton arrows with a Shield",new ItemStack(Items.SHIELD)),50,1);
        addChallenge(28,Objectives.IGNITE_CREEPER.create("Ignite 5 Creepers with a Flint & Steel",new ItemStack(Items.FLINT_AND_STEEL)),5,1);
        addChallenge(29,new NameMobObjective.Instance(EntityType.CHICKEN,"Barvazy","Name a Chicken \"Barvazy\""),1,1);
        addChallenge(30,new CraftItemObjective.Instance(new ItemStack(Items.BEACON),"Beacon(s)"),1,1);
    }


    private <T extends ObjectiveInstance> void addChallenge(int id, T instance, int count, int levels) {
        challengeRepository.add(new ChallengeBuilder<>(id,instance,count,levels));
    }

    public static void registerListeners() {
        PlayerBlockBreakEvents.AFTER.register((world, playerEntity, blockPos, blockState, blockEntity) -> {
            if (!world.isClient) {
                Objectives.BREAK_BLOCK.trigger(playerEntity, blockState.getBlock());
            }
        });
    }

    public void init() {

    }

    public static ChallengesManager get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(ChallengesManager::new,"challenges");
    }

    public void tick() {
        if (challenges.size() >= challengeRepository.size()) {
            if (season == CommonMod.SEASON) {
                return;
            }
            season = CommonMod.SEASON;
            challenges.clear();
            lastRestockTime = 0;
            markDirty();
        }
        Calendar now = Calendar.getInstance(CommonMod.TIME_ZONE);
        int today = now.get(Calendar.DAY_OF_WEEK);
        if (lastRestockTime > 0) {
            Calendar c = Calendar.getInstance(CommonMod.TIME_ZONE);
            c.setTimeInMillis(lastRestockTime);
            int last = c.get(Calendar.DAY_OF_WEEK);
            if ((today == Calendar.SUNDAY && last == Calendar.SATURDAY) || (today - last == 1)) {
                restock();
            }
        } else {
            restock();
        }
    }

    public void restock() {
        if (challenges.size() >= challengeRepository.size()) return;
        ServerChallenge<?> challenge = challengeRepository.get(challenges.size()).create();
        challenges.add(challenge);

        updatePlayers();

        lastRestockTime = System.currentTimeMillis();
        markDirty();
    }

    public void updatePlayers() {
        for (PlayerEntity p : CommonMod.SERVER.get().getPlayerManager().getPlayerList()) {
            PlayerPersistentData.of(p).challenges.update(challenges);
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        season = tag.contains("season") ? tag.getInt("season") : 1;
        lastRestockTime = tag.getLong("LastRestockTime");
        int cc = tag.getInt("ChallengeCount");
        challenges.clear();
        for (int i = 0; i < cc; i++) {
            challenges.add(challengeRepository.get(i).create());
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("season",season);
        tag.putLong("LastRestockTime",lastRestockTime);
        tag.putInt("ChallengeCount",challenges.size());
        return tag;
    }

    public List<ServerChallenge<?>> getChallenges() {
        return challenges;
    }

    public ServerChallenge<?> get(int id) {
        return challenges.stream().filter(c->c.getId() == id).findFirst().orElse(null);
    }

    public void reset() {
        challenges.clear();
        lastRestockTime = 0;
        markDirty();
        updatePlayers();
    }

    public <I extends ObjectiveInstance> void trigger(ObjectiveType<I> type, PlayerEntity player, Predicate predicate, int times) {
        for (ServerChallenge<?> c : challenges) {
            if (c.getObjective() == type && predicate.test(c.getData())) {
                PlayerPersistentData.of(player).challenges.trigger(c,times);
            }
            /*if (c.getObjective() == Objectives.COMPOSITE) {
                Objectives.COMPOSITE.trigger(player,c,type,predicate,times);
            }*/
        }
    }
}
