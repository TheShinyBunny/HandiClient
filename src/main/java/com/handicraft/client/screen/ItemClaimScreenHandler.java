/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.screen;

import com.handicraft.client.CommonMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class ItemClaimScreenHandler extends ScreenHandler {
    private Inventory inv;
    private ScreenHandlerContext context;

    public ItemClaimScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inv, ScreenHandlerContext context) {
        super(CommonMod.ITEM_CLAIM_HANDLER_TYPE, syncId);
        this.context = context;
        checkSize(inv,1);
        this.inv = inv;

        this.addSlot(new TakeOnlySlot(inv,0,80,20));

        for(int m = 0; m < 3; ++m) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, m * 18 + 51));
            }
        }

        for(int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 109));
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        context.run((world, blockPos) -> {
            dropInventory(player,inv);
        });
    }

    public ItemClaimScreenHandler(int i, PlayerInventory playerInventory) {
        this(i,playerInventory,new SimpleInventory(1),ScreenHandlerContext.EMPTY);
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
            if (index == 0) {
                if (!this.insertItem(itemStack2, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 1, false)) {
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
}
