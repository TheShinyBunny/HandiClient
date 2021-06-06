/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class DarknessMaterial implements ToolMaterial {

    public static final DarknessMaterial INSTANCE = new DarknessMaterial();

    @Override
    public int getDurability() {
        return 2500;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 14f;
    }

    @Override
    public float getAttackDamage() {
        return 5f;
    }

    @Override
    public int getMiningLevel() {
        return 4;
    }

    @Override
    public int getEnchantability() {
        return 22;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.DARKNESS_STAR);
    }
}
