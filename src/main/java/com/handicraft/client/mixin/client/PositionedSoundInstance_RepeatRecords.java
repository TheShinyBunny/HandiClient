/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PositionedSoundInstance.class)
public class PositionedSoundInstance_RepeatRecords {

    /*@Redirect(method = "record",at = @At(value = "NEW",target = "net/minecraft/client/sound/PositionedSoundInstance"))
    private static PositionedSoundInstance modifyRecord(SoundEvent sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, SoundInstance.AttenuationType type, double d, double e, double f) {
        return new PositionedSoundInstance(sound.getId(), category, volume, pitch, true, repeatDelay, type, d, e, f,true);
    }*/

}
