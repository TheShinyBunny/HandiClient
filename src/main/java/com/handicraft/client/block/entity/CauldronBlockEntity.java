/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block.entity;

import com.handicraft.client.CommonMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class CauldronBlockEntity extends BlockEntity {

    private Potion potion;
    private DyeColor waterColor;

    public CauldronBlockEntity() {
        super(CommonMod.CAULDRON_BLOCK_ENTITY);
        this.potion = Potions.WATER;
    }

    public boolean canFillWith(Potion potion) {
        return this.potion == potion;
    }

    public void setPotion(Potion potion) {
        this.potion = potion;
        markDirty();
    }

    public Potion getPotion() {
        return potion;
    }

    public boolean isDefaultPotion() {
        return potion == Potions.WATER;
    }

    public DyeColor getWaterColor() {
        return waterColor;
    }

    public void setWaterColor(DyeColor waterColor) {
        this.waterColor = waterColor;
        markDirty();
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return toTag(new CompoundTag());
    }

    @Override
    public @Nullable BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(pos,100,toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("Potion", Registry.POTION.getRawId(potion));
        tag.putInt("Color",waterColor == null ? -1 : waterColor.getId());
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        potion = Registry.POTION.get(tag.getInt("Potion"));
        if (potion == null) {
            potion = Potions.WATER;
        }
        if (tag.contains("Color") && tag.getInt("Color") >= 0) {
            waterColor = DyeColor.byId(tag.getInt("Color"));
        }
    }
}
