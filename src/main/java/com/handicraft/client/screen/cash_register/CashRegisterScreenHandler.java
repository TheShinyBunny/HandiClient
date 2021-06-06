/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.screen.cash_register;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.entity.CashRegisterBlockEntity;
import com.handicraft.client.item.ModItems;
import com.handicraft.client.item.MoneyLike;
import com.handicraft.client.mixin.SimpleInventoryAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CashRegisterScreenHandler extends AbstractCashRegisterScreenHandler {

    public static final Identifier ADMIN_LOGIN = new Identifier("hcclient:register_admin_login");
    public static final Identifier INVALID_PASSWORD = new Identifier("hcclient:invalid_admin_password");

    private Inventory paymentInventory;

    public CashRegisterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int cost) {
        super(CommonMod.CASH_REGISTER_SCREEN,syncId,playerInventory,inventory);
        this.cost = cost;

        this.paymentInventory = new SimpleInventory(2);
        addSlot(new PaymentSlot(paymentInventory,0,16,54));
        addSlot(new PaymentSlot(paymentInventory,1,34,54));

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new StockSlot(stockInventory,  i * 9 + j, j * 18 + 60, i * 18 + 18));
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

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        if (!player.world.isClient) {
            dropInventory(player,paymentInventory);
        }
    }

    public boolean canBuy() {
        return getInputMoney() >= cost;
    }

    public int getValueOf(ItemStack stack) {
        return stack.getItem() instanceof MoneyLike ? ((MoneyLike) stack.getItem()).getRubyValue() * stack.getCount() : 0;
    }

    public void onBuyItem() {
        /*int change = getInputMoney() - cost;
        if (change == 0) {
            paymentInventory.clear();
        } else if (change > 0) {
            ItemStack first;
            ItemStack second = ItemStack.EMPTY;
            if (change < 9) {
                first = new ItemStack(ModItems.RUBY_NUGGET,change);
            } else {
                first = new ItemStack(ModItems.RUBY,change / 9);
                if (change % 9 != 0) {
                    second = new ItemStack(ModItems.RUBY_NUGGET,change % 9);
                }
            }
            paymentInventory.setStack(0,first);
            paymentInventory.setStack(1,second);
        }*/
        sendContentUpdates();
        if (stockInventory instanceof CashRegisterBlockEntity) {
            ((CashRegisterBlockEntity) stockInventory).addProfits(cost);
        }
    }

    private int getInputMoney() {
        int n = 0;
        for (ItemStack stack : ((SimpleInventoryAccessor)paymentInventory).getStacks()) {
            n += getValueOf(stack);
        }
        return n;
    }

    public void tryLogin(PlayerEntity player, String password) {
        if (this.stockInventory instanceof CashRegisterBlockEntity) {
            CashRegisterBlockEntity be = (CashRegisterBlockEntity) this.stockInventory;
            if (be.getPassword() == null) {
                be.setPassword(password);
            } else if (!be.getPassword().equals(password)) {
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,INVALID_PASSWORD,new PacketByteBuf(Unpooled.buffer()));
                return;
            }
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                    packetByteBuf.writeVarInt(cost);
                    packetByteBuf.writeVarInt(be.getProfits());
                    packetByteBuf.writeString(password);
                }

                @Override
                public Text getDisplayName() {
                    return new TranslatableText("container.cash_register.admin.title");
                }

                @Override
                public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new CashRegisterOwnerHandler(syncId,inv,be,be.getCost(),be.getProfits(),password);
                }
            });
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 56) {
                if (!this.insertItem(itemStack2, 56, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 2, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            slot.onTakeItem(player,itemStack2);
        }

        return itemStack;
    }

    public class PaymentSlot extends Slot {

        public PaymentSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof MoneyLike && ((MoneyLike) stack.getItem()).canPurchase();
        }
    }

    public class StockSlot extends Slot {


        public StockSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);

        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return canBuy();
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        protected void onCrafted(ItemStack stack, int amount) {
            this.onCrafted(stack);
        }

        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            this.onCrafted(stack);
            onBuyItem();
        }
    }




}
