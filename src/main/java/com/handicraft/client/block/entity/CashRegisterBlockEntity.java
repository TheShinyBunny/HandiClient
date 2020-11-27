/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block.entity;

import com.handicraft.client.CommonMod;
import com.handicraft.client.item.ModItems;
import com.handicraft.client.screen.cash_register.CashRegisterOwnerHandler;
import com.handicraft.client.screen.cash_register.CashRegisterScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;


import java.util.UUID;

public class CashRegisterBlockEntity extends LockableContainerBlockEntity implements SidedInventory, BlockEntityClientSerializable {

    private String password;
    private DefaultedList<ItemStack> stock;
    private int cost;
    private int profits;
    private ItemStack display;

    public CashRegisterBlockEntity() {
        super(CommonMod.CASH_REGISTER_BLOCK_ENTITY);
        this.stock = DefaultedList.ofSize(54,ItemStack.EMPTY);
        this.cost = 0;
        this.profits = 0;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.cash_register");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CashRegisterScreenHandler(syncId,playerInventory,this,cost);
    }

    public void writeExtraScreenData(PacketByteBuf buf) {
        buf.writeVarInt(cost);
    }

    @Override
    public int size() {
        return stock.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : stock) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return stock.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        markDirty();
        return Inventories.splitStack(stock,slot,amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        markDirty();
        return Inventories.removeStack(stock,slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stock.set(slot,stack);
        markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        System.out.println("marking dirty");
        for (ItemStack s : stock) {
            if (!s.isEmpty()) {
                System.out.println("set display to " + display);
                display = s.copy();
                break;
            }
        }
        sync();

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        stock.clear();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag,stock);
        tag.putInt("Cost",cost);
        tag.putInt("Profits",profits);
        if (password != null) {
            tag.putString("Password", password);
        }
        if (display != null && !display.isEmpty()) {
            tag.put("Display",display.toTag(new CompoundTag()));
        }
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag,stock);
        profits = tag.getInt("Profits");
        password = tag.getString("Password");
        if (password.isEmpty()) {
            password = null;
        }
        cost = tag.getInt("Cost");
        if (tag.contains("Display")) {
            display = ItemStack.fromTag(tag.getCompound("Display"));
            System.out.println("set display from NBT to " + display);
        }
    }

    public void setPassword(String password) {
        this.password = password;
        markDirty();
    }

    public int getProfits() {
        return profits;
    }

    public void addProfits(int profits) {
        this.profits += profits;
        markDirty();
    }

    public void takeProfits() {
        profits = 0;
        markDirty();
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
        markDirty();
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        if (compoundTag.contains("Display")) {
            display = ItemStack.fromTag(compoundTag.getCompound("Display"));
            System.out.println("set display for client to " + display);
        }
        cost = compoundTag.getInt("Cost");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        compoundTag.putInt("Cost",cost);
        if (display != null) {
            compoundTag.put("Display", display.toTag(new CompoundTag()));
        }
        return compoundTag;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getDisplayStack() {
        return display;
    }
}
