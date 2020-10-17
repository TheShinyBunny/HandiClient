/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.screen;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.entity.NetheriteFurnaceBlockEntity;
import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class NetheriteFurnaceScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private World world;

    public NetheriteFurnaceScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId,inventory,new SimpleInventory(13),new ArrayPropertyDelegate(14));
    }

    public NetheriteFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(CommonMod.NETHERITE_FURNACE_HANDLER_TYPE, syncId);
        this.inventory = inventory;
        this.world = playerInventory.player.world;
        this.propertyDelegate = propertyDelegate;
        addSlot(new FuelSlot(this,inventory,0, 77, 59));
        int x = 20;
        int y = 13;
        for (int i = 1; i < 7; i++) {
            addSlot(new Slot(inventory,i,x,y));
            if (i == 3) {
                y = 13;
                x = 135;
            } else {
                y += 23;
            }
        }
        x = 55;
        y = 13;
        for (int i = 7; i < 13; i++) {
            addSlot(new OutputSlot(playerInventory.player,inventory,i,x,y));
            if (i == 9) {
                y = 13;
                x = 100;
            } else {
                y += 23;
            }
        }

        int l;
        for(l = 0; l < 3; ++l) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 5 + k * 18, 84 + l * 18));
            }
        }
        for(l = 0; l < 9; ++l) {
            addSlot(new Slot(playerInventory, l, 5 + l * 18, 142));
        }

        addProperties(propertyDelegate);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    public boolean isFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index > 6 && index < 13) {
                if (!this.insertItem(itemStack2, 13, 49, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (index > 12) {
                if (this.isSmeltable(itemStack2)) {
                    if (!this.spreadItemEqually(itemStack2, 1, 7)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 13 && index < 40) {
                    if (!this.insertItem(itemStack2, 40, 49, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 40 && index < 49 && !this.insertItem(itemStack2, 13, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 13, 49, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    private boolean spreadItemEqually(ItemStack stack, int startIndex, int endIndex) {
        while (!stack.isEmpty()) {
            boolean allOccupied = true;
            for (int i = startIndex; i < endIndex; i++) {
                ItemStack single = stack.copy().split(1);
                boolean b = insertItem(single, i, i + 1, false);
                if (b) {
                    stack.decrement(1);
                    allOccupied = false;
                }
            }
            if (allOccupied) return false;
        }
        return true;
    }

    private boolean isSmeltable(ItemStack stack) {
        return this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(stack), this.world).isPresent();
    }

    public boolean isBurning() {
        return propertyDelegate.get(0) > 0;
    }

    public int getFuelProgress() {
        int i = propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }
        return propertyDelegate.get(0) * 13 / i;
    }

    public int getCookProgress(int module) {
        int i = propertyDelegate.get(2 + module * 2);
        int j = propertyDelegate.get(2 + module * 2 + 1);
        return j != 0 && i != 0 ? i * 12 / j : 0;
    }

    private static class FuelSlot extends Slot {
        private final NetheriteFurnaceScreenHandler handler;

        public FuelSlot(NetheriteFurnaceScreenHandler handler, Inventory inventory, int index, int x, int y) {
            super(inventory,index,x,y);
            this.handler = handler;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return handler.isFuel(stack) || stack.getItem() == Items.BUCKET;
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return stack.getItem() == Items.BUCKET ? 1 : super.getMaxItemCount(stack);
        }
    }


    private static class OutputSlot extends Slot {
        private PlayerEntity player;
        private int amount;
        private int index;

        public OutputSlot(PlayerEntity player, Inventory inventory, int index, int x, int y) {
            super(inventory,index,x,y);
            this.player = player;
            this.index = index;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack takeStack(int amount) {
            if (hasStack()) {
                this.amount += Math.min(amount,getStack().getCount());
            }
            return super.takeStack(amount);
        }

        @Override
        public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
            this.onCrafted(stack);
            super.onTakeItem(player, stack);
            return stack;
        }

        @Override
        protected void onCrafted(ItemStack stack, int amount) {
            this.amount += amount;
            this.onCrafted(stack);
        }

        @Override
        protected void onCrafted(ItemStack stack) {
            stack.onCraft(player.world,player,amount);
            if (!player.world.isClient && inventory instanceof NetheriteFurnaceBlockEntity) {
                Objectives.NETHERITE_SMELT.trigger(player,stack.getCount());
                Objectives.SMELT.trigger(player,i->i.test(stack),stack.getCount());
                ((NetheriteFurnaceBlockEntity) inventory).dropExperience(player,index);
            }
        }
    }
}
