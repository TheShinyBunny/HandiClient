/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Wearable;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JackSoulLanternBlock extends HorizontalFacingBlock implements Wearable {

    private BlockPattern darknessWizardPattern;

    protected JackSoulLanternBlock() {
        super(FabricBlockSettings.of(Material.GOURD, MapColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD).lightLevel(15));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING,ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        /*if (!oldState.isOf(state.getBlock())) {
            BlockPattern.Result result = this.getDarknessWizardPattern().searchAround(world, pos);

            if (result != null) {
                for(int k = 0; k < this.getDarknessWizardPattern().getHeight(); ++k) {
                    CachedBlockPosition cachedBlockPosition = result.translate(0, k, 0);
                    world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                    world.syncWorldEvent(2001, cachedBlockPosition.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
                }

                BlockPos blockPos = result.translate(0, 2, 0).getBlockPos();
                if (!world.isClient) {
                    DarknessWizardEntity darknessWizard = CommonMod.DARKNESS_WIZARD.create(((ServerWorld) world), null, null, null, blockPos, SpawnReason.MOB_SUMMONED, false, false);
                    world.spawnEntity(darknessWizard);
                }

                LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                lightning.setCosmetic(true);
                world.spawnEntity(lightning);

                for(int m = 0; m < this.getDarknessWizardPattern().getHeight(); ++m) {
                    CachedBlockPosition cachedBlockPosition2 = result.translate(0, m, 0);
                    world.updateNeighbors(cachedBlockPosition2.getBlockPos(), Blocks.AIR);
                }
            }
        }*/
    }

    private BlockPattern getDarknessWizardPattern() {
        if (this.darknessWizardPattern == null) {
            this.darknessWizardPattern = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(this))).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(ModBlocks.DARK_OBSIDIAN))).build();
        }
        return this.darknessWizardPattern;
    }
}
