/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.client.ChallengesScreen;
import com.handicraft.client.client.ClientMod;
import com.handicraft.client.client.screen.NewTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClient_ChangeScreens {

    @ModifyVariable(method = "openScreen",at = @At(value = "FIELD",opcode = Opcodes.PUTFIELD,target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",shift = At.Shift.BEFORE),name = "screen")
    private Screen modifyTitleScreen(Screen screen) {
        if (screen instanceof TitleScreen) {
            return new NewTitleScreen(((TitleScreenAccessor)screen).isDoBackgroundFade());
        }
        if (screen instanceof AdvancementsScreen) {
            return new ChallengesScreen(false);
        }
        return screen;
    }

}
