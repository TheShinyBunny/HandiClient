/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.handicraft.client.screen.ShulkerPreviewScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(ScreenHandler.class)
public class ScreenHandler_ShulkerBoxHelper {


    @Shadow @Final public List<Slot> slots;

    @Inject(method = "method_30010",at = @At(value = "INVOKE",target = "Ljava/util/List;get(I)Ljava/lang/Object;",ordinal = 1),cancellable = true)
    private void mergeOrSwapItem(int slotId, int data, SlotActionType slotActionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        Slot slot = this.slots.get(slotId);
        ItemStack stack = slot.getStack();
        ItemStack cursor = playerEntity.inventory.getCursorStack();
        if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock && !cursor.isEmpty() && !(Block.getBlockFromItem(cursor.getItem()) instanceof ShulkerBoxBlock)) {
            ItemStack prev = stack.copy();
            CompoundTag tag = stack.getOrCreateSubTag("BlockEntityTag");
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27,ItemStack.EMPTY);

            Inventories.fromTag(tag,stacks);
            if (insertIntoShulkerBox(stacks,cursor)) {
                Inventories.toTag(tag, stacks);
            }

            cir.setReturnValue(prev);
        }
    }

    private boolean insertIntoShulkerBox(DefaultedList<ItemStack> items, ItemStack stack) {
        boolean success = false;
        if (stack.isStackable()) {
            for (ItemStack current : items) {
                if (!current.isEmpty() && ScreenHandler.canStacksCombine(stack, current)) {
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

    @Inject(method = "method_30010",at = @At(value = "HEAD"),cancellable = true)
    private void clickSlot(int slotId, int data, SlotActionType slotActionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (slotId < 0) return;
        Slot slot = slots.get(slotId);
        if (playerEntity.currentScreenHandler instanceof ShulkerPreviewScreenHandler) {
            if (slotId >= 27) {
                int playerSlot = ((ShulkerPreviewScreenHandler)playerEntity.currentScreenHandler).getSlotInPlayer();

                if (slot.inventory instanceof PlayerInventory && ((SlotAccessor)slot).getIndex() == playerSlot) {
                    cir.setReturnValue(slot.getStack());
                    return;
                }
            } else {
                ItemStack cursor = playerEntity.inventory.getCursorStack();
                if (Block.getBlockFromItem(cursor.getItem()) instanceof ShulkerBoxBlock) {
                    cir.setReturnValue(slot.getStack());
                    return;
                }
            }

        }
        if (slotActionType == SlotActionType.PICKUP_ALL) {
            ItemStack cursor = playerEntity.inventory.getCursorStack();
            if (Block.getBlockFromItem(cursor.getItem()) instanceof ShulkerBoxBlock) {
                CompoundTag tag = cursor.getOrCreateSubTag("BlockEntityTag");
                DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
                Inventories.fromTag(tag, stacks);
                Text title;
                if (playerEntity.world.isClient) {
                    title = CommonMod.getCurrentWindowTitle();
                } else {
                    title = CommonMod.currentTitles.get(playerEntity);
                    ((ScreenHandlerAccessor)playerEntity.currentScreenHandler).getListeners().remove((ServerPlayerEntity)playerEntity);
                }
                int slotInPlayer = -1;
                if (slot.inventory instanceof PlayerInventory) {
                    slotInPlayer = ((SlotAccessor)slot).getIndex();
                }
                Inventory inventory = new SimpleInventory(stacks.toArray(new ItemStack[0]));
                playerEntity.inventory.setCursorStack(ItemStack.EMPTY);
                if (slot.hasStack()) {
                    playerEntity.dropItem(slot.getStack(),false);
                }
                slot.setStack(cursor);
                playerEntity.openHandledScreen(ShulkerPreviewScreenHandler.createFactory(inventory,slotInPlayer,cursor.getName(),playerEntity.currentScreenHandler,title,slotId));

                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }

}
