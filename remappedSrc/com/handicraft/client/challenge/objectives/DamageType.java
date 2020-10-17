/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.function.Predicate;

public enum DamageType implements ObjectiveParameter<DamageSource> {
    TRIDENT(1,0.5F, source->source.getSource() instanceof TridentEntity, "Trident", Items.TRIDENT),
    CROSSBOW(1,0.5f, source->source.getSource() instanceof ArrowEntity && ((PersistentProjectileEntity) source.getSource()).isShotFromCrossbow(), "Crossbow", Items.CROSSBOW),
    PROJECTILE(2,0.5F, DamageSource::isProjectile, "projectile", Items.ARROW),
    ANY(5,1,source->true, "", Items.AIR);

    private final int weight;
    private final float countModifier;
    private final Predicate<DamageSource> sourcePredicate;
    private final String name;
    private final Item icon;

    DamageType(int weight, float countModifier, Predicate<DamageSource> sourcePredicate, String name, Item icon) {
        this.weight = weight;
        this.countModifier = countModifier;
        this.sourcePredicate = sourcePredicate;
        this.name = name;
        this.icon = icon;
    }

    @Override
    public int getWeight() {
        return weight;
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
    public boolean test(DamageSource input) {
        return sourcePredicate.test(input);
    }

    public Item getIcon() {
        return icon;
    }
}
