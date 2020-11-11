/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.screen.cash_register;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.entity.CashRegisterBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class CashRegisterOwnerHandler extends AbstractCashRegisterScreenHandler {

    public static final Identifier UPDATE_COST = new Identifier("hcclient:update_register_cost");
    private int profits;

    public CashRegisterOwnerHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int cost, int profits) {
        super(CommonMod.CASH_REGISTER_OWNER_SCREEN,syncId,playerInventory,inventory);
        this.cost = cost;
        this.profits = profits;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(stockInventory,  i * 9 + j, j * 18 + 60, i * 18 + 18));
            }
        }

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, j * 18 + 34,i * 18 + 140));
            }
        }

        for(int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, i * 18 + 34, 198));
        }
    }

    public CashRegisterOwnerHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PacketByteBuf packetByteBuf) {
        this(syncId, playerInventory, inventory,packetByteBuf.readVarInt(),packetByteBuf.readVarInt());
    }

    public void setCost(int cost) {
        this.cost = cost;
        if (stockInventory instanceof CashRegisterBlockEntity) {
            ((CashRegisterBlockEntity) stockInventory).setCost(cost);
        }
    }

    public int getProfits() {
        return profits;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 54) {
                if (!this.insertItem(itemStack2, 54, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 54, false)) {
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
