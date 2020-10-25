/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Objectives {

    public static final Registry<ObjectiveType> REGISTRY = FabricRegistryBuilder.createSimple(ObjectiveType.class,new Identifier("hcclient:objective_type")).buildAndRegister();

    public static final CraftItemObjective CRAFT_ITEM = register("craft_item",new CraftItemObjective());
    public static final SimpleObjective PAINT_WATER = register("paint_water",new SimpleObjective());
    public static final SimpleObjective GET_DRAGONS_BREATH = register("get_dragons_breath",new SimpleObjective());
    public static final GetMobHeadObjective GET_MOB_HEAD = register("get_mob_head",new GetMobHeadObjective());
    public static final KillEntityObjective KILL_ENTITY = register("kill_entity",new KillEntityObjective());
    public static final SmeltObjective SMELT = register("smelt",new SmeltObjective());
    public static final SimpleObjective NETHERITE_SMELT = register("netherite_smelt",new SimpleObjective());
    public static final SimpleObjective TRADE = register("trade",new SimpleObjective());
    public static final BreakBlockObjective BREAK_BLOCK = register("break_block",new BreakBlockObjective());
    public static final WinRaidObjective WIN_RAID = register("win_raid", new WinRaidObjective());
    public static final FishObjective FISH = register("fish", new FishObjective());
    public static final SimpleObjective CURE_ZOMBIE = register("cure",new SimpleObjective());
    public static final StripLogObjective STRIP_WOOD = register("strip_log", new StripLogObjective());
    public static final BreedObjective BREED = register("breed", new BreedObjective());
    public static final BlockDamageObjective BLOCK_DAMAGE = register("block_damage", new BlockDamageObjective());
    public static final SimpleObjective IGNITE_CREEPER = register("ignite_creeper",new SimpleObjective());
    public static final NameMobObjective NAME_MOB = register("name_mob", new NameMobObjective());

    public static <O extends ObjectiveType<?>> O register(String id,O ot) {
        return Registry.register(REGISTRY,new Identifier("hcclient",id),ot);
    }




}
