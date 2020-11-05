/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.commands;

import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.CollectibleType;
import com.handicraft.client.collectibles.Collectibles;
import com.handicraft.client.collectibles.PlayerCollectibles;
import com.handicraft.client.rewards.Reward;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LockerCommand implements CustomCommand {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("locker")
                .executes(ctx->{
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    player.sendMessage(new LiteralText("===== LOCKER ====="),false);
                    PlayerCollectibles collectibles = PlayerCollectibles.of(player);
                    for (CollectibleType<?> t : CollectibleType.all()) {
                        MutableText msg = t.getName().shallowCopy().append(": ");
                        if (collectibles.getSelected(t) != null) {
                            msg.append(new LiteralText("[DESELECT]").formatted(Formatting.GRAY).styled(s->s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/locker deselect " + t.getId()))));
                        }
                        player.sendMessage(msg,false);
                        for (Collectible c : collectibles.getOwned(t)) {
                            Reward r = Reward.getByCollectible(c);
                            if (r != null) {
                                MutableText msg2 = new LiteralText("    " + r.getName());
                                if (collectibles.getSelected(t) == c) {
                                    msg2.append(" [SELECTED]");
                                } else {
                                    msg2.append(new LiteralText(" [SELECT]").formatted(Formatting.GREEN).styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/locker select " + c.getId().getPath()))));
                                }
                                player.sendMessage(msg2, false);
                            }
                        }
                    }
                    return 1;
                })
                .then(literal("deselect")
                        .then(argument("type", StringArgumentType.word())
                                .suggests((ctx,builder)-> CommandSource.suggestMatching(CollectibleType.all().stream().map(CollectibleType::getId),builder))
                                .executes(ctx->deselectLocker(ctx.getSource(),StringArgumentType.getString(ctx,"type")))
                        )
                )
                .then(literal("select")
                        .then(argument("collectible",StringArgumentType.word())
                                .suggests((ctx,builder)->CommandSource.suggestMatching(Collectibles.REGISTRY.stream().filter(c->c.getType() != null).map(c->c.getId().getPath()),builder))
                                .executes(ctx->selectLocker(ctx.getSource(),StringArgumentType.getString(ctx,"collectible")))
                        )
                )
        );
    }

    private static final DynamicCommandExceptionType UNKNOWN_COLLECTIBLE_TYPE = new DynamicCommandExceptionType(o->new TranslatableText("commands.locker.deselect.unknown",o));

    private int deselectLocker(ServerCommandSource src, String typeId) throws CommandSyntaxException {
        CollectibleType<?> type = CollectibleType.get(typeId);
        if (type == null) {
            throw UNKNOWN_COLLECTIBLE_TYPE.create(typeId);
        }
        PlayerCollectibles collectibles = PlayerCollectibles.of(src.getPlayer());
        collectibles.select(src.getPlayer(),type,null);
        src.sendFeedback(new TranslatableText("commands.locker.deselect",type.getName()),false);
        return 1;
    }

    private static final DynamicCommandExceptionType UNKNOWN_COLLECTIBLE = new DynamicCommandExceptionType(o->new TranslatableText("commands.locker.select.unknown",o));
    private static final DynamicCommandExceptionType DOESNT_OWN_COLLECTIBLE = new DynamicCommandExceptionType(o->new TranslatableText("commands.locker.doesnt_own",o));

    private int selectLocker(ServerCommandSource src, String collectibleId) throws CommandSyntaxException {
        Collectible c = Collectibles.REGISTRY.get(new Identifier("hcclient",collectibleId));
        if (c == null || c.getType() == null) {
            throw UNKNOWN_COLLECTIBLE.create(collectibleId);
        }
        PlayerCollectibles collectibles = PlayerCollectibles.of(src.getPlayer());
        if (!collectibles.owns(c)) {
            throw DOESNT_OWN_COLLECTIBLE.create(c.getType().getName());
        }
        collectibles.select(src.getPlayer(),c.getType(),c);
        Reward r = Reward.getByCollectible(c);
        if (r != null) {
            src.sendFeedback(new TranslatableText("commands.locker.select", r.getName()), false);
        }
        return 1;
    }
}
