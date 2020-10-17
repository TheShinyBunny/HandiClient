/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.item.*;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.tools.Tool;

public enum ToolType implements ObjectiveParameter<ItemStack> {
    NETHERITE(10,0.6f, ToolMaterials.NETHERITE, "Netherite"),
    GOLD(3,0.3f,ToolMaterials.GOLD, "Golden"),
    IRON(1,0.5f,ToolMaterials.IRON, "Iron"),
    STONE(1,0.3f,ToolMaterials.STONE, "Stone"),
    WOOD(1,0.2f,ToolMaterials.WOOD, "Wooden");

    private final int weight;
    private final float countModifier;
    private final ToolMaterial material;
    private final String name;

    ToolType(int weight, float countModifier, ToolMaterial material, String name) {
        this.weight = weight;
        this.countModifier = countModifier;
        this.material = material;
        this.name = name;
    }

    @Override
    public float getCountModifier() {
        return countModifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean test(ItemStack input) {
        return input.getItem() instanceof ToolItem && ((ToolItem) input.getItem()).getMaterial() == material;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public ItemConvertible getIcon(String tool) {
        return Registry.ITEM.get(new Identifier(name.toLowerCase() + "_" + tool.toLowerCase()));
    }
}
