/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.commands;

import com.handicraft.client.collectibles.Emote;
import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.emotes.EmoteManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class EmoteCommand implements CustomCommand {

    private static final DynamicCommandExceptionType DOESNT_OWN_EMOTE = new DynamicCommandExceptionType(o->new TranslatableText("commands.emote.doesnt_own",o));

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> b = literal("emote");
        for (Emote e : EmoteManager.EMOTES) {
            b.then(literal(e.getId().getPath())
                    .executes(ctx -> displayEmote(ctx, e))
            );
        }
        dispatcher.register(b);
    }

    private int displayEmote(CommandContext<ServerCommandSource> ctx, Emote emote) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (!PlayerCollectibles.of(player).owns(emote)) throw DOESNT_OWN_EMOTE.create(new TranslatableText("emote." + emote.getEmote().getPath()));
        EmoteManager.sendEmote(ctx.getSource().getPlayer(),emote.getEmote(),350);
        return 1;
    }
}
