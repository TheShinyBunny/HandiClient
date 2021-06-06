/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import com.handicraft.client.ModSounds;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.Rarity;

public class SpookyMusicDisc extends MusicDiscItem {
    public SpookyMusicDisc() {
        super(14, ModSounds.SPOOKY, new Settings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.UNCOMMON));
    }
}
