/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.entity;

import com.handicraft.client.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class DarkPillagerEntity extends PillagerEntity {
    public DarkPillagerEntity(EntityType<? extends PillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        goalSelector.add(5,new MeleeAttackGoal(this,1.0,false));
    }

    public static DefaultAttributeContainer.Builder createDarkPillagerAttributes() {
        return createPillagerAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH,50).add(EntityAttributes.GENERIC_ATTACK_DAMAGE,7);
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        equipStack(EquipmentSlot.MAINHAND,new ItemStack(ModItems.DARKNESS_AXE));
        setEquipmentDropChance(EquipmentSlot.MAINHAND,0.05f);
    }

    @Override
    public boolean canJoinRaid() {
        return false;
    }

    @Override
    public boolean canLead() {
        return false;
    }


}
