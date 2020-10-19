/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.handicraft.client.CommonMod;
import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.item.ModItems;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static com.handicraft.client.block.ModBlocks.*;
import static com.handicraft.client.item.ModItems.*;
import static net.minecraft.item.Items.*;

public class RecipeData implements DataProvider {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator root;

    public RecipeData(DataGenerator generator) {
        this.root = generator;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path = this.root.getOutput();
        Set<Identifier> set = Sets.newHashSet();
        generate((recipeJsonProvider) -> {
            if (!set.add(recipeJsonProvider.getRecipeId())) {
                throw new IllegalStateException("Duplicate recipe " + recipeJsonProvider.getRecipeId());
            } else {
                saveRecipe(cache, recipeJsonProvider.toJson(), path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/recipes/" + recipeJsonProvider.getRecipeId().getPath() + ".json"));
                /*JsonObject jsonObject = recipeJsonProvider.toAdvancementJson();
                if (jsonObject != null) {
                    saveRecipeAdvancement(cache, jsonObject, path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/advancements/" + recipeJsonProvider.getAdvancementId().getPath() + ".json"));
                }*/
            }
        });
    }

    private static void saveRecipeAdvancement(DataCache cache, JsonObject jsonObject, Path path) {
        try {
            String string = GSON.toJson(jsonObject);
            String string2 = SHA1.hashUnencodedChars(string).toString();
            if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
                Files.createDirectories(path.getParent());
                BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
                Throwable var6 = null;

                try {
                    bufferedWriter.write(string);
                } catch (Throwable var16) {
                    var6 = var16;
                    throw var16;
                } finally {
                    if (bufferedWriter != null) {
                        if (var6 != null) {
                            try {
                                bufferedWriter.close();
                            } catch (Throwable var15) {
                                var6.addSuppressed(var15);
                            }
                        } else {
                            bufferedWriter.close();
                        }
                    }

                }
            }

            cache.updateSha1(path, string2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Recipes";
    }

    private static void saveRecipe(DataCache cache, JsonObject json, Path path) {
        try {
            String string = GSON.toJson(json);
            String string2 = SHA1.hashUnencodedChars(string).toString();
            if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path)) {
                Files.createDirectories(path.getParent());

                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write(string);
                }
            }

            cache.updateSha1(path, string2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generate(Consumer<RecipeJsonProvider> consumer) {
        ShapedRecipeJsonFactory.create(Blocks.PEONY).input('#', ModBlocks.PEONY).pattern("#").pattern("#").criterion("has_peony",conditionsFromItem(ModBlocks.PEONY)).offerTo(consumer);
        ComplexRecipeJsonFactory.create(CommonMod.CANDY_RECIPE_SERIALIZER).offerTo(consumer,"candy_smelting");
        ShapedRecipeJsonFactory.create(HALLOWEEN_CAKE).pattern("PPP").pattern("SES").pattern("WWW").input('P', Ingredient.ofItems(Blocks.PUMPKIN,Blocks.CARVED_PUMPKIN)).input('S', SUGAR).input('E', EGG).input('W',WHEAT).criterion("has_pumpkin",conditionsFromTag(ModItems.Tags.PUMPKINS)).offerTo(consumer);
        surrounded(consumer, CANDY_BUCKET, BUCKET, CARVED_PUMPKIN);
        ShapedRecipeJsonFactory.create(TOMBSTONE).pattern(" C ").pattern("CCC").pattern("SSS").input('C',Ingredient.ofItems(STONE,COBBLESTONE)).input('S',Ingredient.fromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS)).criterion("has_soul_block",conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS)).offerTo(consumer);
        compressedItem(consumer, RUBY_BLOCK, RUBY);
        compressedItem(consumer, DARK_RUBY_BLOCK, DARK_RUBY);
        compressedItem(consumer, RUBY, RUBY_NUGGET);
        ShapedRecipeJsonFactory.create(NETHERITE_FURNACE).pattern("SSS").pattern("SFS").pattern("SNS").input('S',Blocks.SOUL_SOIL).input('F',Blocks.FURNACE).input('N', NETHERITE_INGOT).criterion("has_netherite",conditionsFromItem(NETHERITE_INGOT)).offerTo(consumer);
        surrounded(consumer, GOLDEN_BEETROOT, BEETROOT, GOLD_NUGGET);

        woodType(consumer, ModItems.Tags.DARK_LOGS, DARK_LOG, STRIPPED_DARK_LOG, DARK_WOOD, STRIPPED_DARK_WOOD, DARK_PLANKS, DARK_SLAB, DARK_STAIRS, DARK_FENCE, DARK_FENCE_GATE, DARK_BUTTON, DARK_PRESSURE_PLATE);

        SmithingRecipeJsonFactory.create(Ingredient.ofItems(NETHERITE_SWORD), Ingredient.ofItems(DARKNESS_STAR), DARKNESS_SWORD).criterion("has_dark_star",conditionsFromItem(DARKNESS_STAR)).offerTo(consumer,"darkness_sword");

        ShapedRecipeJsonFactory.create(ModItems.GREEN_FIRE_TORCH,4).pattern("C").pattern("S").pattern("M").input('C',ItemTags.COALS).input('S',STICK).input('M',DARK_LEAVES).criterion("has_leaves",conditionsFromItem(DARK_LEAVES)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(ModItems.PURPLE_FIRE_TORCH,4).pattern("C").pattern("S").pattern("M").input('C',ItemTags.COALS).input('S',STICK).input('M',ModItems.Tags.DARK_STONES).criterion("has_stones",conditionsFromTag(ModItems.Tags.DARK_STONES)).offerTo(consumer);
        surrounded(consumer,GREEN_FIRE_LANTERN,ModItems.GREEN_FIRE_TORCH,IRON_NUGGET);
        surrounded(consumer,PURPLE_FIRE_LANTERN,ModItems.PURPLE_FIRE_TORCH,IRON_NUGGET);

        surrounded(consumer,DARK_OBSIDIAN,DARK_RUBY,OBSIDIAN);
    }

    private static void woodType(Consumer<RecipeJsonProvider> consumer, Tag<Item> logs, Block log, Block strippedLog, Block wood, Block strippedWood, Block planks, Block slab, Block stairs, Block fence, Block fenceGate, Block button, Block pressurePlate) {
        wood(consumer,wood,log);
        wood(consumer,strippedWood,strippedLog);
        ShapelessRecipeJsonFactory.create(planks,4).input(logs).group("planks").criterion("has_logs", conditionsFromTag(logs)).offerTo(consumer);
        sticks(consumer,planks);
        ShapedRecipeJsonFactory.create(slab,6).pattern("###").input('#',planks).group("wooden_slab").criterion("has_planks",conditionsFromItem(planks)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(stairs,4).pattern("#  ").pattern("## ").pattern("###").input('#',planks).group("wooden_stairs").criterion("has_planks",conditionsFromItem(planks)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(fence,3).pattern("W#W").pattern("W#W").input('#', STICK).input('W',planks).group("wooden_fence").criterion("has_planks",conditionsFromItem(planks)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(fenceGate).pattern("#W#").pattern("#W#").input('#', STICK).input('W',planks).group("wooden_fence_gate").criterion("has_planks",conditionsFromItem(planks)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(button).input(planks).criterion("has_planks",conditionsFromItem(planks)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(pressurePlate).pattern("##").input('#',planks).group("wooden_pressure_plate").criterion("has_planks",conditionsFromItem(planks)).offerTo(consumer);
    }

    private static void sticks(Consumer<RecipeJsonProvider> consumer, Block planks) {
        ShapedRecipeJsonFactory.create(STICK,4).pattern("#").pattern("#").input('#',planks).criterion("has_planks",conditionsFromItem(planks)).offerTo(consumer);
    }

    private static void surrounded(Consumer<RecipeJsonProvider> consumer, ItemConvertible output, ItemConvertible center, ItemConvertible around) {
        ShapedRecipeJsonFactory.create(output).pattern("###").pattern("#@#").pattern("###").input('#',around).input('@',center).criterion("has_center",conditionsFromItem(center)).offerTo(consumer);
    }

    private static void compressedItem(Consumer<RecipeJsonProvider> consumer, ItemConvertible compressed, Item mineralItem) {
        ShapelessRecipeJsonFactory.create(mineralItem,9).input(compressed).criterion("has_block",conditionsFromItem(compressed)).offerTo(consumer, recipeIdFrom(compressed,mineralItem));
        ShapedRecipeJsonFactory.create(compressed).input('#',mineralItem).pattern("###").pattern("###").pattern("###").criterion("has_mineral",conditionsFromItem(mineralItem)).offerTo(consumer,recipeIdFrom(mineralItem,compressed));
    }

    private static String recipeIdFrom(ItemConvertible from, ItemConvertible to) {
        return Registry.ITEM.getId(to.asItem()).getPath() + "_from_" + Registry.ITEM.getId(from.asItem()).getPath();
    }

    private static void wood(Consumer<RecipeJsonProvider> consumer, ItemConvertible wood, ItemConvertible log) {
        ShapedRecipeJsonFactory.create(wood, 3).input('#', log).pattern("##").pattern("##").group("bark").criterion("has_log",conditionsFromItem(log)).offerTo(consumer);
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible itemConvertible) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().item(itemConvertible).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... itemPredicates) {
        return new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, itemPredicates);
    }
}
