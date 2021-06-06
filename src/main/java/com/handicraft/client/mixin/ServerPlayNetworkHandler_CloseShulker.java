package com.handicraft.client.mixin;

import com.handicraft.client.screen.PreviewScreen;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
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

    @Inject(method = "onCloseHandledScreen",at = @At("HEAD"),cancellable = true)
    private void onGuiClose(CloseHandledScreenC2SPacket packet, CallbackInfo ci) {
        if (player.currentScreenHandler instanceof PreviewScreen && ((PreviewScreen) player.currentScreenHandler).shouldOverrideClosing()) {
            ci.cancel();
            player.currentScreenHandler.close(player);
            ((PreviewScreen) player.currentScreenHandler).returnToPrevious(player);
        }
    }

}
