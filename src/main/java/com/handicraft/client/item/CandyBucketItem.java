/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import com.handicraft.client.screen.CandyBucketScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CandyBucketItem extends Item {
    public CandyBucketItem() {
        super(new Settings().group(ItemGroup.MISC).maxCount(1).recipeRemainder(Items.BUCKET));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricModelPredicateProviderRegistry.register(this,new Identifier("filled"),(stack, world, entity, seed) -> {
                NbtCompound tag = stack.getTag();
                if (tag != null) {
                    if (tag.contains("Items", NbtType.LIST)) {
                        NbtList list = tag.getList("Items",NbtType.COMPOUND);
                        return list.size() > 5 ? 1 : 0;
                    }
                }
                return 0;
            });
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        DefaultedList<ItemStack> items = DefaultedList.ofSize(9,ItemStack.EMPTY);
        Inventories.readNbt(stack.getOrCreateTag(),items);
        int slot = hand == Hand.OFF_HAND ? -1 : user.getInventory().selectedSlot;
        if (!world.isClient) {
            user.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                    packetByteBuf.writeVarInt(slot);
                }

                @Override
                public Text getDisplayName() {
                    return new TranslatableText("container.candy_bucket");
                }

                @Override
                public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new CandyBucketScreenHandler(syncId, inv, new SimpleInventory(items.toArray(new ItemStack[0])), slot);
                }
            });
        }
        return TypedActionResult.success(stack);
    }

    public static void setItems(ItemStack stack, DefaultedList<ItemStack> items) {
        NbtCompound tag = Inventories.writeNbt(new NbtCompound(),items);
        stack.getOrCreateTag().copyFrom(tag);
    }
}
