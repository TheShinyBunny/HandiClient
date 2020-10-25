package com.handicraft.client.mixin;

import com.handicraft.client.screen.ShulkerPreviewScreenHandler;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandler_CloseShulker {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onGuiClose",at = @At("HEAD"),cancellable = true)
    private void onGuiClose(GuiCloseC2SPacket packet, CallbackInfo ci) {
        if (player.currentScreenHandler instanceof ShulkerPreviewScreenHandler) {
            ci.cancel();
            player.currentScreenHandler.close(player);
        }
    }

}
