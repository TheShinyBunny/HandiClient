/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.screen.cash_register;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public abstract class AbstractCashRegisterScreenHandler extends ScreenHandler {

    protected Inventory stockInventory;
    public int cost;

    public AbstractCashRegisterScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(type, syncId);
        this.stockInventory = inventory;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return stockInventory.canPlayerUse(player);
    }
}
