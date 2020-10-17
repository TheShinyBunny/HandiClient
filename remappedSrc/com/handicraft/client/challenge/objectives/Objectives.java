/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Objectives {

    public static final Registry<ObjectiveType> REGISTRY = FabricRegistryBuilder.createSimple(ObjectiveType.class,new Identifier("hcclient:objective_type")).buildAndRegister();

    public static <O extends ObjectiveType<?>> O register(String id,O ot) {
        return Registry.register(REGISTRY,new Identifier("hcclient",id),ot);
    }

    public static final DamageEntityObjective KILL_ENTITY = register("kill_entity",new DamageEntityObjective(true));
    public static final DamageEntityObjective DAMAGE_ENTITY = register("damage_entity",new DamageEntityObjective(false));
    public static final BreakBlockObjective BREAK_BLOCK = register("break_block",new BreakBlockObjective());
    public static final CraftItemObjective CRAFT_ITEM = register("craft_item",new CraftItemObjective());
    //public static final ObjectiveType PLACE_BLOCK = register("place_block",new ObjectiveType<>());
    public static final SimpleObjective GAIN_LEVEL = register("gain_level",new SimpleObjective("objective.gain_level", Items.EXPERIENCE_BOTTLE));
    public static final GrindItemObjective GRIND_ITEM = register("grind_item",new GrindItemObjective());

}
