/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.commands;

import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.rewards.Reward;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HandipassCommand implements CustomCommand {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("handipass")
                .executes(ctx->{
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    PlayerCollectibles c = PlayerCollectibles.of(player);
                    c.print(player);
                    return 1;
                })
                .then(argument("player", EntityArgumentType.player())
                        .requires(src->src.hasPermissionLevel(2) && isOwner(src))
                        .then(literal("setlevel")
                                .then(argument("level", IntegerArgumentType.integer(0))
                                        .executes(ctx->{
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                            PlayerCollectibles c = PlayerCollectibles.of(player);
                                            c.setLevel(IntegerArgumentType.getInteger(ctx,"level"));
                                            c.sendUpdate(player);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("claim")
                                .then(argument("reward", IdentifierArgumentType.identifier())
                                        .suggests((ctx,builder)-> CommandSource.suggestIdentifiers(Reward.REGISTRY.getIds(),builder))
                                        .executes(ctx->{
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                            Reward r = Reward.REGISTRY.get(IdentifierArgumentType.getIdentifier(ctx,"reward"));
                                            PlayerCollectibles.of(player).claim(player,r);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("unclaim")
                                .then(argument("reward",IdentifierArgumentType.identifier())
                                        .suggests((ctx,builder)->CommandSource.suggestIdentifiers(Reward.REGISTRY.getIds(),builder))
                                        .executes(ctx->{
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                            Reward r = Reward.REGISTRY.get(IdentifierArgumentType.getIdentifier(ctx,"reward"));
                                            PlayerCollectibles.of(player).unclaim(player,r);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
