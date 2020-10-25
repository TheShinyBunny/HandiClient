/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.commands;

import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.rewards.Reward;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ClaimCommand implements CustomCommand {

    private static final DynamicCommandExceptionType ALREADY_CLAIMED = new DynamicCommandExceptionType(o->new TranslatableText("commands.claim.already_claimed",o));

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("claim")
                .then(argument("reward", StringArgumentType.word())
                        .suggests((ctx,builder)->{
                            PlayerCollectibles c = PlayerCollectibles.of(ctx.getSource().getPlayer());
                            int level = c.getLevel();
                            return CommandSource.suggestMatching(Reward.REGISTRY.stream().filter(r->!c.didClaim(r) && r.isObtainable(level)).map(r->Reward.REGISTRY.getId(r).getPath()),builder);
                        })
                        .executes(ctx->{
                            String rname = StringArgumentType.getString(ctx,"reward");
                            PlayerCollectibles c = PlayerCollectibles.of(ctx.getSource().getPlayer());
                            int level = c.getLevel();
                            Reward r = Reward.REGISTRY.get(new Identifier("hcclient",rname));
                            if (r == null) {
                                return 0;
                            }
                            if (!c.didClaim(r) && r.isObtainable(level)) {
                                c.claim(ctx.getSource().getPlayer(),r);
                                ctx.getSource().sendFeedback(new TranslatableText("commands.claim.success",r.getName()),false);
                                return 1;
                            }
                            throw ALREADY_CLAIMED.create(r.getName());
                        })
                )
        );
    }
}
