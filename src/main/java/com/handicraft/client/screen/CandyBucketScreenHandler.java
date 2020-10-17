/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.screen;

import com.handicraft.client.CommonMod;
import com.handicraft.client.item.CandyBucketItem;
import com.handicraft.client.mixin.SimpleInventoryAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public class CandyBucketScreenHandler extends ScreenHandler {

    private final int bucketSlot;
    private final SimpleInventory inventory;

    public CandyBucketScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId,playerInventory,new SimpleInventory(9),buf.readVarInt());
    }

    public CandyBucketScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory inventory, int bucketSlot) {
        super(CommonMod.CANDY_BUCKET_HANDLER_TYPE, syncId);
        int i = (1 - 4) * 18;
        this.inventory = inventory;
        this.bucketSlot = bucketSlot;

        int n;
        int m;
        for(m = 0; m < 9; ++m) {
            this.addSlot(new Slot(inventory, m, 8 + m * 18, 18));
        }

        for(n = 0; n < 3; ++n) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i));
            }
        }

        for(n = 0; n < 9; ++n) {
            if (n == bucketSlot) {
                this.addSlot(new UnmodifiableSlot(playerInventory, n, 8 + n * 18, 161 + i));
            } else {
                this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 161 + i));
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 9) {
                if (!this.insertItem(itemStack2, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 9, false)) {
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

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        int slot = bucketSlot == -1 ? 40 : bucketSlot;
        ItemStack bucket = player.inventory.getStack(slot);
        CandyBucketItem.setItems(bucket, ((SimpleInventoryAccessor) inventory).getStacks());
        player.inventory.setStack(slot,bucket);
    }
}
