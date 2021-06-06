/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block.entity;

import com.handicraft.client.CommonMod;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class CauldronBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private Potion potion;
    private DyeColor waterColor;

    public CauldronBlockEntity(BlockPos pos, BlockState state) {
        super(CommonMod.CAULDRON_BLOCK_ENTITY,pos,state);
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
    public void markDirty() {
        super.markDirty();
        if (!world.isClient) {
            sync();
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt("Potion", Registry.POTION.getRawId(potion));
        tag.putInt("Color",waterColor == null ? -1 : waterColor.getId());
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        potion = Registry.POTION.get(tag.getInt("Potion"));
        if (potion == null) {
            potion = Potions.WATER;
        }
        if (tag.contains("Color") && tag.getInt("Color") >= 0) {
            waterColor = DyeColor.byId(tag.getInt("Color"));
        }
    }

    @Override
    public void fromClientTag(NbtCompound compoundTag) {
        readNbt(compoundTag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound compoundTag) {
        return writeNbt(compoundTag);
    }
}
