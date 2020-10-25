/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin.client;

import com.handicraft.client.challenge.client.ChallengesScreen;
import com.handicraft.client.client.ClientMod;
import com.handicraft.client.client.screen.NewGameMenuScreen;
import com.handicraft.client.client.screen.NewTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClient_ChangeScreens {

    @ModifyVariable(method = "openScreen",at = @At(value = "FIELD",opcode = Opcodes.PUTFIELD,target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",shift = At.Shift.BEFORE))
    private Screen modifyTitleScreen(Screen screen) {
        if (ClientMod.VANILLA_GUI) return screen;
        if (screen instanceof TitleScreen) {
            return new NewTitleScreen(((TitleScreenAccessor)screen).isDoBackgroundFade(),true);
        }
        if (screen instanceof AdvancementsScreen) {
            return new ChallengesScreen(false);
        }
        if (screen instanceof MultiplayerScreen) {
            return new NewTitleScreen(true,true);
        }
        if (screen instanceof GameMenuScreen) {
            return new NewGameMenuScreen();
        }
        if (screen instanceof PackScreen) {
            return null;
        }
        return screen;
    }

}
