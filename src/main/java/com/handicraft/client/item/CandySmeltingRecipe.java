/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtInt;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CandySmeltingRecipe extends SmeltingRecipe {

    private Map<Object,ItemStack> generatedOutputs = new HashMap<>();

    public CandySmeltingRecipe(Identifier id) {
        super(id, "", Ingredient.ofItems(Items.SUGAR), new ItemStack(ModItems.CANDY), 0.2f, 100);
    }

    public ItemStack getRandomOutput(Object owner) {
        ItemStack stack = generatedOutputs.getOrDefault(owner,new ItemStack(ModItems.CANDY));
        generatedOutputs.remove(owner);
        return stack;
    }

    public ItemStack createRandomOutput(Object owner) {
        ItemStack stack = new ItemStack(ModItems.CANDY);
        stack.putSubTag("kind", NbtInt.of((int)(Math.random() * 9)));
        generatedOutputs.put(owner,stack);
        return stack;
    }
}
