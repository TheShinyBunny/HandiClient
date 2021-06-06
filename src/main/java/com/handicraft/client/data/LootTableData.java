/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handicraft.client.CommonMod;
import com.handicraft.client.item.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.LootTablesProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.*;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.handicraft.client.block.ModBlocks.*;

public class LootTableData extends LootTablesProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private static final LootNumberProvider ONE_ROLL = ConstantLootNumberProvider.create(1);
    private static final LootCondition.Builder REQUIRE_SILK_TOUCH = MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))));
    private static final LootCondition.Builder REQUIRE_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().items(Items.SHEARS));
    private static final float[] SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final float[] JUNGLE_SAPLING_CHANCES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};

    private final List<Pair<Consumer<BiConsumer<Identifier, LootTable.Builder>>, LootContextType>> lootTypeGenerators;
    private final DataGenerator root;

    public LootTableData(DataGenerator dataGenerator) {
        super(dataGenerator);
        this.root = dataGenerator;
        lootTypeGenerators = ImmutableList.of(lootGenerator(LootTableData::blocks,Block::getLootTableId,LootContextTypes.BLOCK),lootGenerator(LootTableData::entities, EntityType::getLootTableId,LootContextTypes.ENTITY),Pair.of(cons->LootTableData.chests(cons),LootContextTypes.CHEST));
    }

    private <T> Pair<Consumer<BiConsumer<Identifier,LootTable.Builder>>,LootContextType> lootGenerator(Consumer<BiConsumer<T,LootTable.Builder>> consumer, Function<T,Identifier> lootTableId, LootContextType ctx) {
        return Pair.of(bc->consumer.accept((i,b)->bc.accept(lootTableId.apply(i),b)),ctx);
    }

    @Override
    public void run(DataCache cache) {
        Path path = this.root.getOutput();
        Map<Identifier, LootTable> map = Maps.newHashMap();
        this.lootTypeGenerators.forEach((pair) -> {
            pair.getFirst().accept((id, builder) -> {
                if (map.put(id, builder.type(pair.getSecond()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + id);
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

    private static void blocks(BiConsumer<Block, LootTable.Builder> consumer) {
        //dropSelf(consumer, PEONY);
        consumer.accept(Blocks.PEONY,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(Blocks.PEONY).conditionally(BlockStatePropertyLootCondition.builder(Blocks.PEONY).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))))));
        //consumer.accept(POTTED_PEONY,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(Blocks.FLOWER_POT))).pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(PEONY))));
        consumer.accept(HEATED_OBSIDIAN,LootTable.builder());
        dropSelf(consumer, DARK_LOG);
        dropSelf(consumer, DARK_WOOD);
        dropSelf(consumer, STRIPPED_DARK_LOG);
        dropSelf(consumer, STRIPPED_DARK_WOOD);
        dropSelf(consumer, DARK_PLANKS);
        //dropSelf(consumer, DARK_RUBY_BLOCK);
        dropSelf(consumer, DARK_MAGMA_BLOCK);
        dropSelf(consumer, DARK_STONE);
        dropSelf(consumer, DARK_LEAVES);
        dropSelf(consumer, SHADOW_STONE);
        dropSelf(consumer, DARK_BUTTON);
        dropSlab(consumer, DARK_SLAB);
        dropSelf(consumer, DARK_PRESSURE_PLATE);
        dropSelf(consumer, DARK_FENCE);
        dropSelf(consumer, DARK_FENCE_GATE);
        dropSelf(consumer, DARK_SAPLING);
        dropSelf(consumer, DARK_ROSE);
        //dropSelf(consumer, RUBY_BLOCK);
        dropSelf(consumer, NETHERITE_FURNACE);
        dropSelf(consumer, GREEN_FIRE_TORCH);
        dropSelf(consumer, PURPLE_FIRE_TORCH);
        drop(consumer, PURPLE_FIRE_WALL_TORCH, PURPLE_FIRE_TORCH);
        drop(consumer, GREEN_FIRE_WALL_TORCH, GREEN_FIRE_TORCH);
        dropSelf(consumer, GREEN_FIRE_LANTERN);
        dropSelf(consumer, PURPLE_FIRE_LANTERN);
        dropSelf(consumer, DARK_OBSIDIAN);
        dropSelf(consumer, DARKNESS_BRICKS);
        dropSelf(consumer, JACK_SOUL_LANTERN);
        dropSelf(consumer, CASH_REGISTER);
        dropSlab(consumer, DIRT_SLAB);
        dropSlab(consumer, GRASS_BLOCK_SLAB);
        dropSlab(consumer, SHADOW_STONE_SLAB);
        dropSelf(consumer, SHADOW_STONE_BRICKS);
        dropSelf(consumer, SHADOW_STONE_STAIRS);
        dropSelf(consumer, FROZEN_STONE);
        dropSelf(consumer, FROZEN_STONE_BRICKS);

        consumer.accept(HALLOWEEN_CAKE,LootTable.builder());
        dropOre(consumer, DARK_ORE, ModItems.DARK_RUBY, 1, 1, false);
        dropOre(consumer, DARKNESS_DIAMOND_ORE, Items.DIAMOND, 1, 1, true);
        dropSelf(consumer, TOMBSTONE);
        dropWithSilkTouch(consumer, Blocks.DIRT_PATH, Blocks.DIRT);
        dropWithSilkTouch(consumer, Blocks.FARMLAND, Blocks.DIRT);
        dropOreWithAdditionalItem(consumer, Blocks.LAPIS_ORE, Items.LAPIS_LAZULI, 4, 9, Items.DIAMOND, 0.3f, 0.1f);
        //dropOreWithAdditionalItem(consumer, Blocks.REDSTONE_ORE, Items.REDSTONE, 4, 5, ModItems.RUBY, 0.04f, 0.02f);

    }

    private static void drop(BiConsumer<Block, LootTable.Builder> consumer, Block block, ItemConvertible drop) {
        consumer.accept(block,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(drop)).conditionally(SurvivesExplosionLootCondition.builder())));
    }

    private static void dropOreWithAdditionalItem(BiConsumer<Block, LootTable.Builder> consumer, Block ore, Item defaultDrop, int min, int max, Item additional, float chance, float fortuneMultiplier) {
        consumer.accept(ore,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(AlternativeEntry.builder(
                ItemEntry.builder(ore).conditionally(REQUIRE_SILK_TOUCH),
                GroupEntryBuilder.create(
                        ItemEntry.builder(defaultDrop).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max))).apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)),
                        ItemEntry.builder(additional).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE,chance,chance + fortuneMultiplier,chance + fortuneMultiplier * 2,chance + fortuneMultiplier * 3))
                )
        ))));
    }

    private static void dropWithSilkTouch(BiConsumer<Block, LootTable.Builder> consumer, Block block, Block noSilkTouch) {
        consumer.accept(block,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(AlternativeEntry.builder(
                ItemEntry.builder(block).conditionally(REQUIRE_SILK_TOUCH),
                ItemEntry.builder(noSilkTouch).conditionally(SurvivesExplosionLootCondition.builder())
        ))));
    }

    private static void dropSlab(BiConsumer<Block,LootTable.Builder> consumer, Block slab) {
        consumer.accept(slab,LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                .with(ItemEntry.builder(slab)
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2))
                                .conditionally(BlockStatePropertyLootCondition.builder(slab)
                                        .properties(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE))
                                )
                        )
                        .conditionally(SurvivesExplosionLootCondition.builder())
                )
        ));
    }

    private static void dropOre(BiConsumer<Block, LootTable.Builder> consumer, Block ore, Item drop, int min, int max, boolean fortunable) {
        LeafEntry.Builder<?> entry = ItemEntry.builder(drop).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max)));
        if (fortunable) {
            entry = entry.apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE));
        }
        consumer.accept(ore,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(AlternativeEntry.builder(
                ItemEntry.builder(ore).conditionally(REQUIRE_SILK_TOUCH),
                entry
        ))));
    }

    private static void dropSelf(BiConsumer<Block, LootTable.Builder> consumer, Block block) {
        consumer.accept(block,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(block)).conditionally(SurvivesExplosionLootCondition.builder())));
    }

    private static void entities(BiConsumer<EntityType<?>, LootTable.Builder> consumer) {
        consumer.accept(CommonMod.DARK_BLAZE,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(Items.TOTEM_OF_UNDYING).conditionally(RandomChanceLootCondition.builder(0.05f)))));
        consumer.accept(CommonMod.DARK_PILLAGER,LootTable.builder().pool(LootPool.builder().rolls(ONE_ROLL).with(ItemEntry.builder(Items.NAME_TAG).conditionally(RandomChanceWithLootingLootCondition.builder(0.5f,0.1f)))));
    }

    private static void chests(BiConsumer<Identifier, LootTable.Builder> consumer) {
        /*consumer.accept(DarkFortressGenerator.CHEST_LOOT,LootTable.builder().pool(LootPool.builder().rolls(UniformLootNumberProvider.create(3,8))
                .with(ItemEntry.builder(Items.NETHERITE_CHESTPLATE).weight(5).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.NETHERITE_LEGGINGS).weight(5).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.NETHERITE_BOOTS).weight(5).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.NETHERITE_HELMET).weight(5).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.DIAMOND_CHESTPLATE).weight(10).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.DIAMOND_LEGGINGS).weight(10).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.NETHERITE_SWORD).weight(10).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.NETHERITE_PICKAXE).weight(10).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.TOTEM_OF_UNDYING).weight(10))
                .with(ItemEntry.builder(ModItems.RUBY).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                .with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(10).apply(SetNbtLootFunction.builder(createMendingEnchantTag())))
                .with(ItemEntry.builder(Items.BEACON).weight(1).conditionally(RandomChanceLootCondition.builder(0.005f)))
                .with(ItemEntry.builder(DARKNESS_BRICKS).weight(9).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(64))))
        ));
        consumer.accept(DarkTempleStructure.CHEST_LOOT, LootTable.builder().pool(LootPool.builder().rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
                .with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                .with(ItemEntry.builder(Items.IRON_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
                .with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
                .with(ItemEntry.builder(Items.EMERALD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                .with(ItemEntry.builder(Items.BONE).weight(25).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 6.0F))))
                .with(ItemEntry.builder(Items.SPIDER_EYE).weight(25).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                .with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(25).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 7.0F))))
                .with(ItemEntry.builder(Items.SADDLE).weight(20))
                .with(ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(15))
                .with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(10))
                .with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(5))
                .with(ItemEntry.builder(Items.BOOK).weight(20).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(30,39))))
                .with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(20))
                .with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(20))
                .with(EmptyEntry.Serializer().weight(15))
        ).pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(4))
                .with(ItemEntry.builder(Items.BONE).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
                .with(ItemEntry.builder(Items.GUNPOWDER).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
                .with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
                .with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
                .with(ItemEntry.builder(Blocks.SAND).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
                .with(ItemEntry.builder(Items.DIAMOND_BOOTS).weight(3).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.DIAMOND_CHESTPLATE).weight(3).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.DIAMOND_LEGGINGS).weight(3).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
                .with(ItemEntry.builder(Items.DIAMOND_HELMET).weight(3).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20,39)).allowTreasureEnchantments()))
        ));*/
    }

    private static NbtCompound createMendingEnchantTag() {
        NbtCompound tag = new NbtCompound();
        NbtList enchs = new NbtList();
        NbtCompound mending = new NbtCompound();
        mending.putString("id","minecraft:mending");
        mending.putShort("lvl", (short) 1);
        enchs.add(mending);
        tag.put("StoredEnchantments",enchs);
        return tag;
    }
}
