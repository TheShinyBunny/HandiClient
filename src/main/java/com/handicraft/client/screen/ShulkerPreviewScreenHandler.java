package com.handicraft.client.screen;

import com.handicraft.client.CommonMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.fabricmc.fabric.mixin.container.ServerPlayerEntityAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashSet;
import java.util.Set;

public class ShulkerPreviewScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final int slotInPlayer;
    private final ScreenHandler prev;
    private final Text prevTitle;
    private final int shulkerSlot;

    public ShulkerPreviewScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int slotInPlayer, ScreenHandler prev, Text prevTitle, int shulkerSlot) {
        super(CommonMod.SHULKER_PREVIEW_SCREEN_HANDLER_TYPE, syncId);
        this.inventory = inventory;
        this.slotInPlayer = slotInPlayer;
        this.prev = prev;
        this.prevTitle = prevTitle;
        this.shulkerSlot = shulkerSlot;

        inventory.onOpen(playerInventory.player);
        int i = -18;

        int n;
        int m;
        for(n = 0; n < 3; ++n) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new ShulkerBoxSlot(inventory, m + n * 9, 8 + m * 18, 18 + n * 18));
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

    public ShulkerPreviewScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId,playerInventory,new SimpleInventory(27),buf.readVarInt(),null,null,-1);
    }

    public static NamedScreenHandlerFactory createFactory(Inventory inventory, int slotInPlayer, Text title, ScreenHandler prev, Text prevTitle, int shulkerSlot) {
        return new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                packetByteBuf.writeVarInt(slotInPlayer);
            }

            @Override
            public Text getDisplayName() {
                return title;
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new ShulkerPreviewScreenHandler(syncId,inv,inventory,slotInPlayer,prev,prevTitle,shulkerSlot);
            }
        };
    }

    public int getSlotInPlayer() {
        return slotInPlayer;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        if (!player.world.isClient) {
            Slot slot = prev.getSlot(shulkerSlot);
            ItemStack stack = slot.getStack();
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            for (int i = 0; i < 27; i++) {
                Slot s = slots.get(i);
                stacks.set(i, s.getStack());
            }
            Inventories.toTag(stack.getOrCreateSubTag("BlockEntityTag"), stacks);

            slot.setStack(stack);

            player.currentScreenHandler = player.playerScreenHandler;
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
                        invs.forEach(i -> i.onOpen(player));
                        return prev;
                    }, prevTitle));
                }
            } else {
                player.currentScreenHandler.addListener((ScreenHandlerListener) player);
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
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
}
