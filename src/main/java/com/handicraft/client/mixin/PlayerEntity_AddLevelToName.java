/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.collectibles.PlayerCollectibles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntity_AddLevelToName extends LivingEntity {


    protected PlayerEntity_AddLevelToName(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract Text getName();

    @Shadow protected abstract MutableText addTellClickEvent(MutableText component);

    @Overwrite
    public Text getDisplayName() {
        if (world.isClient) {
            MutableText mutableText = Team.decorateName(this.getScoreboardTeam(), this.getName());
            return this.addTellClickEvent(mutableText);
        }
        return this.getName().copy().append(new TranslatableText("display.name.level",PlayerCollectibles.of((ServerPlayerEntity)(Object)this).getLevel()));
    }
}
