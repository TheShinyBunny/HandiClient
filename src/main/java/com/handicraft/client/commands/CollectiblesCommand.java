/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.commands;

import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.Collectibles;
import com.handicraft.client.collectibles.PlayerCollectibles;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class CollectiblesCommand implements CustomCommand {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("collectibles")
                .then(literal("get")
                        .requires(src->src.hasPermissionLevel(2))
                        .then(argument("collectible", IdentifierArgumentType.identifier())
                                .suggests((ctx,builder)-> CommandSource.suggestIdentifiers(Collectibles.REGISTRY.getIds(),builder))
                                .executes(ctx->{
                                    Identifier cname = IdentifierArgumentType.getIdentifier(ctx,"collectible");
                                    Collectible c = Collectibles.REGISTRY.get(cname);
                                    if (c != null) {
                                        PlayerCollectibles.give(ctx.getSource().getPlayer(), c);
                                        ctx.getSource().sendFeedback(new LiteralText("Gave collectible " + cname + "!"), true);
                                    }
                                    return 1;
                                })
                        )
                )
        );
    }
}
