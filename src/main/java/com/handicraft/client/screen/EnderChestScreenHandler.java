/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.screen;

import com.handicraft.client.CommonMod;
import com.handicraft.client.PlayerPersistentData;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class EnderChestScreenHandler extends ScreenHandler implements PreviewScreen {

    private final PlayerEntity player;
    private final Text prevScreenTitle;
    private final ScreenHandler prevScreen;
    private EnderChestInventory inventory;
    private int storedXP;

    public EnderChestScreenHandler(int syncId, PlayerInventory playerInventory, EnderChestInventory inventory, int storedXP, Text prevScreenTitle, ScreenHandler prevScreen) {
        super(CommonMod.ENDER_CHEST_HANDLER_TYPE, syncId);
        this.storedXP = storedXP;
        this.player = playerInventory.player;
        this.prevScreenTitle = prevScreenTitle;
        this.prevScreen = prevScreen;
        checkSize(inventory, 27);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        int i = -18;

        int n;
        int m;
        for(n = 0; n < 3; ++n) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + n * 9, 8 + m * 18, 18 + n * 18));
            }
        }

        for(n = 0; n < 3; ++n) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i));
            }
        }

        for(n = 0; n < 9; ++n) {
            this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 161 + i));
        }
    }

    public static NamedScreenHandlerFactory create(Text prevScreenTitle, ScreenHandler prevScreen) {
        return new ExtendedScreenHandlerFactory(){
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                packetByteBuf.writeVarInt(PlayerPersistentData.of(serverPlayerEntity).storedXP);
            }

            @Override
            public Text getDisplayName() {
                return new TranslatableText("container.enderchest");
            }

            @Override
            public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new EnderChestScreenHandler(syncId, inv, player.getEnderChestInventory(), PlayerPersistentData.of(player).storedXP, prevScreenTitle, prevScreen);
            }
        };
    }

    @Override
    public boolean shouldOverrideClosing() {
        return prevScreen != null && prevScreenTitle != null;
    }

    @Override
    public ScreenHandler getPreviousScreen() {
        return prevScreen;
    }

    @Override
    public Text getPreviousScreenTitle() {
        return prevScreenTitle;
    }

    @Override
    public void onPreviewClosed(PlayerEntity player) {

    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

    public int getStoredXP() {
        return storedXP;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    public int takeXP() {
        if (storedXP > 0) {
            float f = player.experienceProgress;
            int points = (int) (getPointsToLevelUp(player.experienceLevel) * (1 - f) + getPointsToLevelUp(player.experienceLevel + 1) * f);
            if (points > storedXP) {
                points = storedXP;
            }
            storedXP -= points;
            return points;
        }
        return 0;
    }

    public int addXP() {
        if (player.experienceLevel > 0) {
            float f = player.experienceProgress;
            int points = (int) (getPointsToLevelUp(player.experienceLevel) * f + getPointsToLevelUp(player.experienceLevel - 1) * (1 - f));
            storedXP += points;
            return points;
        }
        return 0;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 27) {
                if (!this.insertItem(itemStack2, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemStack;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public int getStoredLevels() {
        return pointsToLevels(storedXP);
    }

    public float getProgress() {
        int p = 0;
        int level = 0;
        int myLevel = getStoredLevels();
        while (level < myLevel) {
            p += getPointsToLevelUp(level);
            level++;
        }
        return (storedXP - p) / (float)getPointsToLevelUp(myLevel);
    }

    public static int pointsToLevels(int points) {
        int level = 0;
        int c = 0;
        while (points > c) {
            level++;
            c += getPointsToLevelUp(level);
        }
        return level;
    }

    private static int getPointsToLevelUp(int levels) {
        if (levels < 16) return levels * 2 + 7;
        if (levels < 31) return levels * 5 - 38;
        return levels * 9 - 158;
    }
}
