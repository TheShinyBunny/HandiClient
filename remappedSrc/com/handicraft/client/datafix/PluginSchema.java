/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixer.TypeReferences;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PluginSchema extends Schema {
    public PluginSchema(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        return new HashMap<>();
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        registerType(true, TypeReferences.ITEM_STACK, ()->{
            System.out.println("creating template");
            return DSL.optionalFields("tag",DSL.remainder());
        });
        registerType(true,TypeReferences.PLAYER, ()->{
            return DSL.optionalFields("Inventory",DSL.list(TypeReferences.ITEM_STACK.in(schema)),"EnderItems",DSL.list(TypeReferences.ITEM_STACK.in(schema)),"BukkitValues",DSL.remainder());
        });
    }
}
