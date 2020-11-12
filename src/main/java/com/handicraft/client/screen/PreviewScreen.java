/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.screen;

import com.handicraft.client.mixin.ScreenHandlerAccessor;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.fabricmc.fabric.mixin.container.ServerPlayerEntityAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

public interface PreviewScreen {

    boolean shouldOverrideClosing();

    ScreenHandler getPreviousScreen();

    Text getPreviousScreenTitle();

    void onPreviewClosed(PlayerEntity player);

    default void returnToPrevious(PlayerEntity player) {
        if (!player.world.isClient && shouldOverrideClosing()) {
            onPreviewClosed(player);
            player.currentScreenHandler = player.playerScreenHandler;
            ScreenHandler prev = getPreviousScreen();
            Text prevTitle = getPreviousScreenTitle();
            if (!(prev instanceof PlayerScreenHandler)) {
                ((ServerPlayerEntityAccessor) player).setScreenHandlerSyncId(prev.syncId - 1);
                if (prev.getType() instanceof ExtendedScreenHandlerType) {
                    if (prev instanceof ShulkerPreviewScreenHandler) {
                        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                            @Override
                            public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                                packetByteBuf.writeVarInt(((ShulkerPreviewScreenHandler) prev).slotInPlayer);
                            }

                            @Override
                            public Text getDisplayName() {
                                return prevTitle;
                            }

                            @Override
                            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                                Set<Inventory> invs = new HashSet<>();
                                for (Slot s : prev.slots) {
                                    if (s.inventory != inv) {
                                        invs.add(s.inventory);
                                    }
                                }
                                invs.forEach(i -> i.onOpen(player));
                                ((ScreenHandlerAccessor)prev).setSyncId(syncId);
                                return prev;
                            }
                        });
                    } /*else if (prev instanceof EnderChestScreenHandler) {
                        player.openHandledScreen(EnderChestScreenHandler.create());
                    }*/
                } else {
                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player1) -> {
                        Set<Inventory> invs = new HashSet<>();
                        for (Slot s : prev.slots) {
                            if (s.inventory != inv) {
                                invs.add(s.inventory);
                            }
                        }
                        invs.forEach(i -> {
                            i.onOpen(player);
                        });
                        ((ScreenHandlerAccessor)prev).setSyncId(syncId);
                        return prev;
                    }, prevTitle));
                }
            } else {
                player.currentScreenHandler.addListener((ScreenHandlerListener) player);
            }
        }
    }

}
