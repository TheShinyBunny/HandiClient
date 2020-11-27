/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.block.entity.CauldronBlockEntity;
import com.handicraft.client.client.WaterColorRenderer;
import net.minecraft.block.*;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CauldronBlock.class)
public abstract class CauldronBlock_Extend implements BlockEntityProvider, FluidDrainable, FluidFillable {

    @Shadow
    @Final
    public static IntProperty LEVEL;

    @Shadow
    public abstract void setLevel(World world, BlockPos pos, BlockState state, int level);

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new CauldronBlockEntity();
    }

    @Override
    public Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CauldronBlockEntity)) return null;
        CauldronBlockEntity cauldron = ((CauldronBlockEntity) be);
        int level = state.get(LEVEL);
        if (level == 3 && cauldron.isDefaultPotion()) {
            cauldron.setWaterColor(null);
            setLevel((World) world,pos,state,0);
            return Fluids.WATER;
        }
        return null;
    }

    @Overwrite
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        int i = state.get(LEVEL);
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CauldronBlockEntity)) return;
        CauldronBlockEntity cauldron = ((CauldronBlockEntity) be);
        float f = (float) pos.getY() + (6.0F + (float) (3 * i)) / 16.0F;
        if (!world.isClient && i > 0 && entity.getY() <= (double) f) {
            boolean used = false;
            if (cauldron.isDefaultPotion() && entity.isOnFire()) {
                entity.extinguish();
                used = true;
            } else if (entity instanceof LivingEntity && !cauldron.getPotion().getEffects().isEmpty()) {
                for (StatusEffectInstance effect : cauldron.getPotion().getEffects()) {
                    if (effect.getEffectType().isInstant()) {
                        effect.getEffectType().applyInstantEffect(null,null, (LivingEntity) entity,effect.getAmplifier(),1.0f);
                        used = true;
                    } else {
                        if (!((LivingEntity) entity).hasStatusEffect(effect.getEffectType()) && ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(effect))) {
                            used = true;
                        }
                    }
                }
            }
            if (used) {
                this.setLevel(world, pos, state, i - 1);
            }
        }

    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CauldronBlockEntity)) return false;
        CauldronBlockEntity cauldron = ((CauldronBlockEntity) be);
        int level = state.get(LEVEL);
        return fluid == Fluids.WATER && level < 3 && cauldron.isDefaultPotion();
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CauldronBlockEntity)) return false;
        CauldronBlockEntity cauldron = ((CauldronBlockEntity) be);
        int level = state.get(LEVEL);
        if (fluidState.getFluid() == Fluids.WATER && level < 3 && cauldron.isDefaultPotion()) {
            setLevel((World) world,pos,state,3);
            return true;
        }
        return false;
    }

    @Overwrite
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CauldronBlockEntity)) return ActionResult.PASS;
        CauldronBlockEntity cauldron = ((CauldronBlockEntity) be);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            int i = state.get(LEVEL);
            Item item = itemStack.getItem();
            if (item == Items.WATER_BUCKET) {
                if (!cauldron.canFillWith(Potions.WATER)) return ActionResult.CONSUME;
                if (i < 3) {
                    cauldron.setPotion(Potions.WATER);
                    cauldron.setWaterColor(null);
                    if (!world.isClient){
                        if (!player.abilities.creativeMode) {
                            player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                        }

                        player.incrementStat(Stats.FILL_CAULDRON);
                        this.setLevel(world, pos, state, 3);
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }

                }

                return ActionResult.success(world.isClient);
            } else if (item == Items.BUCKET) {
                if (!cauldron.isDefaultPotion()) return ActionResult.FAIL;
                if (i == 3) {
                    cauldron.setWaterColor(null);
                    if (!world.isClient) {
                        if (!player.abilities.creativeMode) {
                            itemStack.decrement(1);
                            if (itemStack.isEmpty()) {
                                player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
                            } else if (!player.inventory.insertStack(new ItemStack(Items.WATER_BUCKET))) {
                                player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                            }
                        }

                        player.incrementStat(Stats.USE_CAULDRON);
                        this.setLevel(world, pos, state, 0);
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }

                return ActionResult.success(world.isClient);
            } else {
                ItemStack itemStack4;
                if (item == Items.GLASS_BOTTLE) {
                    if (i > 0 && !world.isClient) {
                        if (!player.abilities.creativeMode) {
                            itemStack4 = PotionUtil.setPotion(new ItemStack(Items.POTION), cauldron.getPotion());
                            player.incrementStat(Stats.USE_CAULDRON);
                            itemStack.decrement(1);
                            if (itemStack.isEmpty()) {
                                player.setStackInHand(hand, itemStack4);
                            } else if (!player.inventory.insertStack(itemStack4)) {
                                player.dropItem(itemStack4, false);
                            } else if (player instanceof ServerPlayerEntity) {
                                ((ServerPlayerEntity) player).openHandledScreen(player.playerScreenHandler);
                            }
                        }

                        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        this.setLevel(world, pos, state, i - 1);
                    }
                    if (i == 1) {
                        cauldron.setPotion(Potions.WATER);
                        world.updateListeners(pos,state,state,2);
                    }

                    return ActionResult.success(world.isClient);
                } else if (item == Items.POTION) {
                    if (i < 3 && (i == 0 || cauldron.canFillWith(PotionUtil.getPotion(itemStack)))) {
                        cauldron.setPotion(PotionUtil.getPotion(itemStack));
                        if (!world.isClient) {
                            if (!player.abilities.creativeMode) {
                                itemStack4 = new ItemStack(Items.GLASS_BOTTLE);
                                player.incrementStat(Stats.USE_CAULDRON);
                                player.setStackInHand(hand, itemStack4);
                                if (player instanceof ServerPlayerEntity) {
                                    ((ServerPlayerEntity) player).openHandledScreen(player.playerScreenHandler);
                                }
                            }

                            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            this.setLevel(world, pos, state, i + 1);
                        }

                    }

                    return ActionResult.success(world.isClient);
                } else if (item instanceof DyeItem) {
                    if (i > 0 && cauldron.isDefaultPotion()) {
                        if (!world.isClient) {
                            if (!player.abilities.creativeMode) {
                                player.incrementStat(Stats.USE_CAULDRON);
                                itemStack.decrement(1);
                                if (player instanceof ServerPlayerEntity) {
                                    ((ServerPlayerEntity) player).openHandledScreen(player.playerScreenHandler);
                                }
                            }
                        }
                        if (((DyeItem) item).getColor() == DyeColor.LIGHT_BLUE) {
                            cauldron.setWaterColor(null);
                        } else {
                            cauldron.setWaterColor(((DyeItem) item).getColor());
                        }
                        world.updateListeners(pos,state,state,2);

                    }

                    return ActionResult.success(world.isClient);
                } else {
                    if (i > 0 && item instanceof DyeableItem) {
                        DyeableItem dyeableItem = (DyeableItem) item;
                        if (cauldron.isDefaultPotion() && cauldron.getWaterColor() == null && !world.isClient) {
                            dyeableItem.removeColor(itemStack);
                            this.setLevel(world, pos, state, i - 1);
                            player.incrementStat(Stats.CLEAN_ARMOR);
                            return ActionResult.SUCCESS;
                        } else if (!world.isClient && cauldron.getWaterColor() != null) {
                            int color;
                            if (cauldron.isDefaultPotion()) {
                                color = WaterColorRenderer.COLOR_MAP.get(cauldron.getWaterColor());
                            } else {
                                Potion p = cauldron.getPotion();
                                color = PotionUtil.getColor(p);
                            }
                            dyeableItem.setColor(itemStack,color);
                            this.setLevel(world, pos, state, i - 1);
                            return ActionResult.SUCCESS;
                        }
                    }

                    if (i > 0 && item instanceof BannerItem) {
                        if (cauldron.getPotion() == Potions.WATER && BannerBlockEntity.getPatternCount(itemStack) > 0 && !world.isClient) {
                            itemStack4 = itemStack.copy();
                            itemStack4.setCount(1);
                            BannerBlockEntity.loadFromItemStack(itemStack4);
                            player.incrementStat(Stats.CLEAN_BANNER);
                            if (!player.abilities.creativeMode) {
                                itemStack.decrement(1);
                                this.setLevel(world, pos, state, i - 1);
                            }

                            if (itemStack.isEmpty()) {
                                player.setStackInHand(hand, itemStack4);
                            } else if (!player.inventory.insertStack(itemStack4)) {
                                player.dropItem(itemStack4, false);
                            } else if (player instanceof ServerPlayerEntity) {
                                ((ServerPlayerEntity) player).openHandledScreen(player.playerScreenHandler);
                            }
                        }

                        return ActionResult.success(world.isClient);
                    } else if (i > 0 && item instanceof BlockItem) {
                        Block block = ((BlockItem) item).getBlock();
                        if (cauldron.isDefaultPotion() && block instanceof ShulkerBoxBlock && !world.isClient()) {
                            ItemStack itemStack5 = new ItemStack(Blocks.SHULKER_BOX, 1);
                            if (itemStack.hasTag()) {
                                itemStack5.setTag(itemStack.getTag().copy());
                            }

                            player.setStackInHand(hand, itemStack5);
                            this.setLevel(world, pos, state, i - 1);
                            player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                            return ActionResult.SUCCESS;
                        } else {
                            return ActionResult.PASS;
                        }
                    } else {
                        return ActionResult.PASS;
                    }
                }
            }
        }
    }
}
