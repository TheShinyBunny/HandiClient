/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public interface CustomCommand {

    void register(CommandDispatcher<ServerCommandSource> dispatcher);

    default LiteralArgumentBuilder<ServerCommandSource> literal(String literal) {
        return CommandManager.literal(literal);
    }

    default <T> RequiredArgumentBuilder<ServerCommandSource,T> argument(String name, ArgumentType<T> type) {
        return CommandManager.argument(name,type);
    }

    default boolean isOwner(ServerCommandSource src) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) return true;
        try {
            ServerPlayerEntity p = src.getPlayer();
            return p.getEntityName().equalsIgnoreCase("prettynice") || p.getEntityName().equalsIgnoreCase("theshinybunny") || p.getEntityName().equalsIgnoreCase("brener");
        } catch (Exception e) {
            return false;
        }
    }

}
