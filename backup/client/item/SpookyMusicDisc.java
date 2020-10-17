/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import com.handicraft.client.CommonMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.Rarity;

public class SpookyMusicDisc extends MusicDiscItem {
    public SpookyMusicDisc() {
        super(14, CommonMod.SPOOKY_SOUND, new Settings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.UNCOMMON));
    }
}
