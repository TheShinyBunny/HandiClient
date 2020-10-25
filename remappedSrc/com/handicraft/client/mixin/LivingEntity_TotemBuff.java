/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_TotemBuff extends Entity {

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean clearStatusEffects();

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    public LivingEntity_TotemBuff(EntityType<?> type, World world) {
        super(type, world);
    }

    @Overwrite
    private boolean tryUseTotem(DamageSource source) {
        ItemStack itemStack = null;

        if ((Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object)this;
            for (ItemStack s : ((PlayerInventoryAccessor)player.inventory).getCombinedInventory().stream().flatMap(Collection::stream).collect(Collectors.toList())) {
                if (s.getItem() == Items.TOTEM_OF_UNDYING) {
                    itemStack = s.copy();
                    s.decrement(1);
                    break;
                }
            }
        } else {
            for (Hand hand : Hand.values()) {
                ItemStack itemStack2 = this.getStackInHand(hand);
                if (itemStack2.getItem() == Items.TOTEM_OF_UNDYING) {
                    itemStack = itemStack2.copy();
                    itemStack2.decrement(1);
                    break;
                }
            }
        }

        if (itemStack != null) {
            if ((Object)this instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;
                serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
            }

            this.setHealth(1.0F);
            this.clearStatusEffects();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            if (source.isOutOfWorld()) {
                BlockPos dest = null;
                for (BlockPos pos : BlockPos.iterateOutwards(this.getBlockPos(),50,1,50)) {
                    BlockPos top = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,pos);
                    if (isSafe(top)) {
                        dest = top;
                        break;
                    }
                }
                if (dest != null) {
                    teleport(dest.getX() + 0.5,dest.getY(),dest.getZ() + 0.5);
                    fallDistance = 0;
                }
            }
            this.world.sendEntityStatus(this, (byte) 35);
        }
        return itemStack != null;
    }


    private boolean isSafe(BlockPos pos) {
        BlockState ground = world.getBlockState(pos.down());
        return !ground.isAir() && ground.getMaterial().isSolid() && world.isAir(pos);
    }

}
