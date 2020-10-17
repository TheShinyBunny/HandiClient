/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStack_DataFix {

    @Inject(method = "fromTag",at = @At("HEAD"))
    private static void fixItem(CompoundTag tag, CallbackInfoReturnable<ItemStack> cir) {
        try {
            Dynamic<Tag> res = CommonMod.DATA_FIXER.update(TypeReferences.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, tag), 0, CommonMod.DATA_VERSION);
            if (res.getValue() instanceof CompoundTag) {
                CommonMod.updateNBT(tag, (CompoundTag) res.getValue());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
