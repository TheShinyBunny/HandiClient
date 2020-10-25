/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.ModPotions;
import com.handicraft.client.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRegistry_AddCustom {


    @Shadow
    private static void registerPotionRecipe(Potion input, Item item, Potion output) {
    }

    @Inject(method = "registerDefaults",at = @At("TAIL"))
    private static void registerRecipes(CallbackInfo ci) {
        /*registerPotionRecipe(Potions.AWKWARD, ModItems.GOLDEN_BEETROOT, ModPotions.HASTE_POTION);
        registerPotionRecipe(ModPotions.HASTE_POTION,Items.REDSTONE,ModPotions.LONG_HASTE_POTION);
        registerPotionRecipe(ModPotions.HASTE_POTION,Items.GLOWSTONE_DUST,ModPotions.STRONG_HASTE_POTION);*/
    }

}
