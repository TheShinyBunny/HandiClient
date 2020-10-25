/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.item.CandySmeltingRecipe;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnace_MakeCandy {

    @Redirect(method = "craftRecipe",at = @At(value = "INVOKE",target = "Lnet/minecraft/recipe/Recipe;getOutput()Lnet/minecraft/item/ItemStack;"))
    private ItemStack getOutput(Recipe<?> recipe) {
        if (recipe instanceof CandySmeltingRecipe) {
            return ((CandySmeltingRecipe)recipe).getRandomOutput(this);
        }
        return recipe.getOutput();
    }

    @Redirect(method = "canAcceptRecipeOutput",at = @At(value = "INVOKE",target = "Lnet/minecraft/recipe/Recipe;getOutput()Lnet/minecraft/item/ItemStack;"))
    private ItemStack createOutput(Recipe<?> recipe) {
        if (recipe instanceof CandySmeltingRecipe) {
            return ((CandySmeltingRecipe)recipe).createRandomOutput(this);
        }
        return recipe.getOutput();
    }

}
