/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Map;

public class CanStructGenCommand implements CustomCommand {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> b = literal("cangenerate")
                .requires(src->src.hasPermissionLevel(2));

        for (Map.Entry<String, StructureFeature<?>> e : StructureFeature.STRUCTURES.entrySet()) {
            b = b.then(literal(e.getKey()).executes(ctx->execute(ctx.getSource(),e.getKey(),e.getValue())));
        }

        dispatcher.register(b);
    }

    private int execute(ServerCommandSource source, String key, StructureFeature<?> value) {
        ServerWorld world = source.getWorld();
        if (!world.getChunkManager().getChunkGenerator().getBiomeSource().hasStructureFeature(value)) {
            source.sendFeedback(new LiteralText("The biome source in world " + world.getRegistryKey() + " does not contain this structure!"),false);
            return 0;
        }
        StructureConfig config = world.getChunkManager().getChunkGenerator().getStructuresConfig().getForType(value);
        if (config == null) {
            source.sendFeedback(new LiteralText("The structure has no config for this world (" + world.getRegistryKey() + ")"),false);
            return 0;
        }
        source.sendFeedback(new LiteralText("Feature.locateStructure should have been called"),false);
        return 0;
    }
}
