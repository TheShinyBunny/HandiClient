/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class CandyItem extends Item {
    public CandyItem() {
        super(new Settings().food(new FoodComponent.Builder().snack().hunger(1).saturationModifier(0.1f).build()).group(ItemGroup.FOOD));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricModelPredicateProviderRegistry.register(this,new Identifier("kind"), (stack, world, entity) -> {
                if (stack.hasTag()) {
                    return stack.getTag().getInt("kind");
                }
                return 0;
            });
        }
    }


    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (isIn(group)) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = new ItemStack(this);
                stack.putSubTag("kind", IntTag.of(i));
                stacks.add(stack);
            }
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int kind = stack.getTag() == null ? 0 : stack.getTag().getInt("kind");
        return super.getTranslationKey(stack) + "." + kind;
    }
}
