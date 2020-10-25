/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import com.handicraft.client.CommonMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Rarity;

public class CustomMusicDisc extends MusicDiscItem {
    public CustomMusicDisc(int comparator, SoundEvent event) {
        super(comparator, event, new Settings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.UNCOMMON));
    }
}
