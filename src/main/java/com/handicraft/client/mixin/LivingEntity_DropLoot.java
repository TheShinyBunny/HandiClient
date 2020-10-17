/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.challenge.objectives.Objectives;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_DropLoot extends Entity {

    public LivingEntity_DropLoot(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract Identifier getLootTable();

    @Shadow protected abstract LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source);

    @Overwrite
    public void dropLoot(DamageSource source, boolean causedByPlayer) {
        Identifier identifier = this.getLootTable();
        LootTable lootTable = this.world.getServer().getLootManager().getTable(identifier);
        LootContext.Builder builder = this.getLootContextBuilder(causedByPlayer, source);
        lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), s->{
            if (source.getAttacker() instanceof PlayerEntity) {
                if (s.getItem() == Items.PLAYER_HEAD) {
                    Objectives.GET_MOB_HEAD.trigger(((PlayerEntity) source.getAttacker()),i->i.test(s),1);
                }
            }
            dropStack(s);
        });
    }
}
