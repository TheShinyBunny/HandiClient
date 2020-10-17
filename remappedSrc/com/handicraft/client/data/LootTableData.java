/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handicraft.client.block.ModBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.LootTablesProvider;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LootTableData extends LootTablesProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private static final LootTableRange ONE_ROLL = ConstantLootTableRange.create(1);

    private final List<Pair<Consumer<BiConsumer<Block, LootTable.Builder>>, LootContextType>> lootTypeGenerators;
    private final DataGenerator root;

    public LootTableData(DataGenerator dataGenerator) {
        super(dataGenerator);
        this.root = dataGenerator;
        lootTypeGenerators = ImmutableList.of(Pair.of(LootTableData::blocks,LootContextTypes.BLOCK));
    }

    @Override
    public void run(DataCache cache) {
        Path path = this.root.getOutput();
        Map<Identifier, LootTable> map = Maps.newHashMap();
        this.lootTypeGenerators.forEach((pair) -> {
            pair.getFirst().accept((block, builder) -> {
                if (map.put(block.getLootTableId(), builder.type(pair.getSecond()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + block.getLootTableId());
                }
            });
        });
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, i->null, map::get);

        map.forEach((identifierx, lootTable) -> {
            LootManager.validate(lootTableReporter, identifierx, lootTable);
        });
        Multimap<String, String> multimap = lootTableReporter.getMessages();
        if (!multimap.isEmpty()) {
            multimap.forEach((string, string2) -> {
                LOGGER.warn("Found validation problem in " + string + ": " + string2);
            });
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        } else {
            map.forEach((identifierx, lootTable) -> {
                Path path2 = getOutput(path, identifierx);

                try {
                    DataProvider.writeToPath(GSON, cache, LootManager.toJson(lootTable), path2);
                } catch (IOException var6) {
                    LOGGER.error("Couldn't save loot table {}", path2, var6);
                }

            });
        }
    }

    private static Path getOutput(Path rootOutput, Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_tables/" + lootTableId.getPath() + ".json");
    }

    public static void blocks(BiConsumer<Block, LootTable.Builder> consumer) {
        consumer.accept(ModBlocks.PEONY,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(ModBlocks.PEONY)).conditionally(SurvivesExplosionLootCondition.builder())));
        consumer.accept(Blocks.PEONY,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(Blocks.PEONY).conditionally(BlockStatePropertyLootCondition.builder(Blocks.PEONY).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))))));
        consumer.accept(ModBlocks.POTTED_PEONY,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(Blocks.FLOWER_POT))).pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(ModBlocks.PEONY))));
        consumer.accept(ModBlocks.HEATED_OBSIDIAN,LootTable.builder());
    }
}
