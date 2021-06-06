/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.screen.EnderChestScreenHandler;
import com.handicraft.client.screen.ShulkerPreviewScreenHandler;
import com.handicraft.client.screen.cash_register.AbstractCashRegisterScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandler_ShulkerBoxHelper {

    @Shadow @Final public DefaultedList<Slot> slots;

    @Shadow public abstract ItemStack getCursorStack();

    @Shadow public abstract void setCursorStack(ItemStack stack);

    @Inject(method = "internalOnSlotClick",at = @At(value = "INVOKE",target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;",ordinal = 1),cancellable = true)
    private void mergeOrSwapItem(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        Slot slot = this.slots.get(slotIndex);
        ItemStack stack = slot.getStack();
        ItemStack cursor = getCursorStack();
        if ((Object)this instanceof AbstractCashRegisterScreenHandler && !(slot.inventory instanceof PlayerInventory)) {
            return;
        }
        if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock && !cursor.isEmpty() && !(Block.getBlockFromItem(cursor.getItem()) instanceof ShulkerBoxBlock)) {
            NbtCompound tag = stack.getOrCreateSubTag("BlockEntityTag");
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27,ItemStack.EMPTY);

            Inventories.readNbt(tag,stacks);
            if (insertIntoShulkerBox(stacks,cursor)) {
                Inventories.writeNbt(tag, stacks);
            }
            ci.cancel();
        }
    }

    private boolean insertIntoShulkerBox(DefaultedList<ItemStack> items, ItemStack stack) {
        boolean success = false;
        if (stack.isStackable()) {
            for (ItemStack current : items) {
                if (!current.isEmpty() && ItemStack.canCombine(stack, current)) {
                    int j = current.getCount() + stack.getCount();
                    if (j <= stack.getMaxCount()) {
                        stack.setCount(0);
                        current.setCount(j);
                        success = true;
                    } else if (current.getCount() < stack.getMaxCount()) {
                        stack.decrement(stack.getMaxCount() - current.getCount());
                        current.setCount(stack.getMaxCount());
                        success = true;
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                ItemStack current = items.get(i);
                if (current.isEmpty()) {
                    items.set(i,stack.split(stack.getCount()));
                    success = true;
                    break;
                }
            }
        }

        return success;
    }

    @Inject(method = "internalOnSlotClick",at = @At(value = "HEAD"),cancellable = true)
    private void clickSlot(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (slotIndex < 0) return;
        Slot slot = slots.get(slotIndex);
        if (player.currentScreenHandler instanceof ShulkerPreviewScreenHandler) {
            if (slotIndex >= 27) {
                int playerSlot = ((ShulkerPreviewScreenHandler)player.currentScreenHandler).getSlotInPlayer();

                if (slot.inventory instanceof PlayerInventory && ((SlotAccessor)slot).getIndex() == playerSlot) {
                    ci.cancel();
                    return;
                }
            } else {
                ItemStack cursor = getCursorStack();
                if (Block.getBlockFromItem(cursor.getItem()) instanceof ShulkerBoxBlock) {
                    ci.cancel();
                    return;
                }
            }
        }
        if ((Object)this instanceof AbstractCashRegisterScreenHandler && !(slot.inventory instanceof PlayerInventory)) return;
        if (actionType == SlotActionType.PICKUP_ALL) {
            ItemStack cursor = getCursorStack();
            if (Block.getBlockFromItem(cursor.getItem()) instanceof ShulkerBoxBlock) {
                NbtCompound tag = cursor.getOrCreateSubTag("BlockEntityTag");
                DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
                Inventories.readNbt(tag, stacks);
                Text title;
                if (player.world.isClient) {
                    title = CommonMod.getCurrentWindowTitle();
                } else {
                    title = CommonMod.currentTitles.get(player);
                    ((ScreenHandlerAccessor)player.currentScreenHandler).getListeners().remove((ServerPlayerEntity)player);
                }
                int slotInPlayer = -1;
                if (slot.inventory instanceof PlayerInventory) {
                    slotInPlayer = ((SlotAccessor)slot).getIndex();
                }
                Inventory inventory = new SimpleInventory(stacks.toArray(new ItemStack[0]));
                setCursorStack(ItemStack.EMPTY);
                if (slot.hasStack()) {
                    player.dropItem(slot.getStack(),false);
                }
                slot.setStack(cursor);
                player.openHandledScreen(ShulkerPreviewScreenHandler.createFactory(inventory,slotInPlayer,cursor.getName(),player.currentScreenHandler,title,slotIndex));

                ci.cancel();
            } else if (cursor.getItem() == Items.ENDER_CHEST) {
                Text title;
                if (player.world.isClient) {
                    title = CommonMod.getCurrentWindowTitle();
                } else {
                    title = CommonMod.currentTitles.get(player);
                    ((ScreenHandlerAccessor)player.currentScreenHandler).getListeners().remove((ServerPlayerEntity)player);
                }
                setCursorStack(ItemStack.EMPTY);
                if (slot.hasStack()) {
                    player.dropItem(slot.getStack(),false);
                }
                slot.setStack(cursor);
                player.openHandledScreen(EnderChestScreenHandler.create(title,player.currentScreenHandler));

                ci.cancel();
            }
        }
    }

}
