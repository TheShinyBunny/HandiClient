/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import com.handicraft.client.util.Register;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;

public class ModItems {

    @Register("spooky_music_disc")
    public static final SpookyMusicDisc SPOOKY_MUSIC_DISC = new SpookyMusicDisc();

    @Register("darkness_star")
    public static final Item DARKNESS_STAR = new Item(new Item.Settings().rarity(Rarity.EPIC).group(ItemGroup.MISC).fireproof());

    @Register("darkness_sword")
    public static final DarknessSword DARKNESS_SWORD = new DarknessSword();
}
