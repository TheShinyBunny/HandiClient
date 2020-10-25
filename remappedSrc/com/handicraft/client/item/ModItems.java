/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import com.handicraft.client.ModSounds;
import com.handicraft.client.ModTags;
import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.util.Register;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.tag.Tag;
import net.minecraft.util.Rarity;

public class ModItems {

    @Register("spooky_music_disc")
    public static final CustomMusicDisc SPOOKY_MUSIC_DISC = new CustomMusicDisc(14, ModSounds.SPOOKY);

    @Register("darkness_star")
    public static final Item DARKNESS_STAR = new DarknessStarItem();

    @Register("darkness_sword")
    public static final DarknessSwordItem DARKNESS_SWORD = new DarknessSwordItem();

    @Register("spooky_scary_music_disc")
    public static final CustomMusicDisc SPOOKY_SCARY_SKELETONS_DISC = new CustomMusicDisc(15,ModSounds.SPOOKY_SCARY_SKELETONS);

    @Register("atla_music_disc")
    public static final CustomMusicDisc AVATAR_DISC = new CustomMusicDisc(1, ModSounds.AVATAR_THEME);

    @Register("ruby")
    public static final Item RUBY = new Item(new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON).fireproof());

    @Register("golden_beetroot")
    public static final Item GOLDEN_BEETROOT = new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().saturationModifier(1f).hunger(6).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.HASTE,20 * 90),1f).build()));

    @Register("candy")
    public static final CandyItem CANDY = new CandyItem();

    @Register("dark_ruby")
    public static final Item DARK_RUBY = new Item(new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.EPIC).fireproof());

    @Register("candy_bucket")
    public static final Item CANDY_BUCKET = new CandyBucketItem();

    @Register("ruby_nugget")
    public static final Item RUBY_NUGGET = new Item(new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON));

    @Register("green_fire_torch")
    public static final Item GREEN_FIRE_TORCH = new WallStandingBlockItem(ModBlocks.GREEN_FIRE_TORCH,ModBlocks.GREEN_FIRE_WALL_TORCH,new Item.Settings().group(ItemGroup.DECORATIONS));

    @Register("purple_fire_torch")
    public static final Item PURPLE_FIRE_TORCH = new WallStandingBlockItem(ModBlocks.PURPLE_FIRE_TORCH,ModBlocks.PURPLE_FIRE_WALL_TORCH,new Item.Settings().group(ItemGroup.DECORATIONS));

    @Register("darkness_axe")
    public static final Item DARKNESS_AXE = new DarknessAxeItem();

    public static class Tags {

        public static final Tag.Identified<Item> DARK_LOGS = ModTags.item("dark_logs", ModBlocks.DARK_LOG, ModBlocks.DARK_WOOD, ModBlocks.STRIPPED_DARK_LOG, ModBlocks.STRIPPED_DARK_WOOD);

        public static final Tag.Identified<Item> PUMPKINS = ModTags.item("pumpkins", Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN);
        public static final Tag.Identified<Item> DARK_STONES = ModTags.item("dark_stones", ModBlocks.DARK_STONE, ModBlocks.SHADOW_STONE);
    }
}
