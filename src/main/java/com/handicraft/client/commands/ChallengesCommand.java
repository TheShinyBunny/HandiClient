/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.commands;

import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.challenge.ChallengesManager;
import com.handicraft.client.challenge.PlayerChallenges;
import com.handicraft.client.challenge.ServerChallenge;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.Collection;

public class ChallengesCommand implements CustomCommand {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("challenges")
                .executes(ctx->{
                    PlayerEntity player = ctx.getSource().getPlayer();
                    PlayerPersistentData.of(player).challenges.print();
                    return 1;
                })
                .then(literal("reset")
                        .requires(src->src.hasPermissionLevel(2) && isOwner(src))
                        .executes(ctx->{
                            ChallengesManager.get(ctx.getSource().getMinecraftServer().getOverworld()).reset();
                            ctx.getSource().sendFeedback(new LiteralText("Challenges has been reset!"),true);
                            return 1;
                        })
                )
                .then(literal("generate")
                        .requires(src->src.hasPermissionLevel(2) && isOwner(src))
                        .executes(ctx->{
                            ChallengesManager.get(ctx.getSource().getMinecraftServer().getOverworld()).restock();
                            ctx.getSource().sendFeedback(new LiteralText("Generated a new challenge!"),true);
                            return 1;
                        })
                )
                .then(argument("player", EntityArgumentType.players())
                        .requires(src->src.hasPermissionLevel(2) && isOwner(src))
                        .executes(ctx->{
                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx,"player");
                            for (ServerPlayerEntity p : players) {
                                PlayerPersistentData.of(p).challenges.print();
                            }
                            return players.size();
                        })
                        .then(literal("reset")
                                .executes(ctx->{
                                    Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx,"player");
                                    for (ServerPlayerEntity p : players) {
                                        PlayerPersistentData.of(p).challenges.reset();
                                    }
                                    return players.size();
                                })
                        )
                        .then(literal("trigger")
                                .then(argument("challengeId", IntegerArgumentType.integer(1,30))
                                        .executes(ctx->triggerChallenge(ctx.getSource(),EntityArgumentType.getPlayers(ctx,"player"),IntegerArgumentType.getInteger(ctx,"challengeId"),1))
                                        .then(argument("times",IntegerArgumentType.integer(1))
                                                .executes(ctx->triggerChallenge(ctx.getSource(),EntityArgumentType.getPlayers(ctx,"player"),IntegerArgumentType.getInteger(ctx,"challengeId"),IntegerArgumentType.getInteger(ctx,"times")))
                                        )
                                )
                        )
                        .then(literal("complete")
                                .then(argument("challengeId",IntegerArgumentType.integer(1,30))
                                        .executes(ctx->completeChallenge(ctx.getSource(),EntityArgumentType.getPlayers(ctx,"player"),IntegerArgumentType.getInteger(ctx,"challengeId")))
                                )
                        )
                )
        );
    }


    private static final DynamicCommandExceptionType UNKNOWN_CHALLENGE = new DynamicCommandExceptionType(o->new TranslatableText("commands.challenge.trigger.unknown",o));

    private int triggerChallenge(ServerCommandSource src, Collection<ServerPlayerEntity> players, int challengeId, int times) throws CommandSyntaxException {
        ChallengesManager manager = ChallengesManager.get(src.getMinecraftServer().getOverworld());
        ServerChallenge<?> challenge = manager.get(challengeId);
        if (challenge == null) {
            throw UNKNOWN_CHALLENGE.create(challengeId);
        }
        for (ServerPlayerEntity p : players) {
            PlayerChallenges ch = PlayerPersistentData.of(p).challenges;
            ch.trigger(challenge,times);
        }
        return players.size() * times;
    }

    private int completeChallenge(ServerCommandSource src, Collection<ServerPlayerEntity> players, int challengeId) throws CommandSyntaxException {
        ChallengesManager manager = ChallengesManager.get(src.getMinecraftServer().getOverworld());
        ServerChallenge<?> challenge = manager.get(challengeId);
        if (challenge == null) {
            throw UNKNOWN_CHALLENGE.create(challengeId);
        }
        for (ServerPlayerEntity p : players) {
            PlayerChallenges ch = PlayerPersistentData.of(p).challenges;
            ch.trigger(challenge,challenge.getMinCount());
        }
        return players.size();
    }
}
