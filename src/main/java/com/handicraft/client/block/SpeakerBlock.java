/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.entity.SpeakerBlockEntity;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SpeakerBlock extends WallMountedBlock implements BlockEntityProvider {
    public static final VoxelShape WEST_FLOOR_LEG = Block.createCuboidShape(2.0D, 0.0D, 6.0D, 4.0D, 7.0D, 10.0D);
    public static final VoxelShape EAST_FLOOR_LEG = Block.createCuboidShape(12.0D, 0.0D, 6.0D, 14.0D, 7.0D, 10.0D);
    public static final VoxelShape WEST_FLOOR_HINGE = Block.createCuboidShape(2.0D, 7.0D, 5.0D, 4.0D, 13.0D, 11.0D);
    public static final VoxelShape EAST_FLOOR_HINGE = Block.createCuboidShape(12.0D, 7.0D, 5.0D, 14.0D, 13.0D, 11.0D);
    public static final VoxelShape WEST_FLOOR_SIDE = VoxelShapes.union(WEST_FLOOR_LEG, WEST_FLOOR_HINGE);
    public static final VoxelShape EAST_FLOOR_SIDE = VoxelShapes.union(EAST_FLOOR_LEG, EAST_FLOOR_HINGE);
    public static final VoxelShape Z_FLOOR_SIDES = VoxelShapes.union(WEST_FLOOR_SIDE, EAST_FLOOR_SIDE);
    public static final VoxelShape Z_FLOOR_SHAPE = VoxelShapes.union(Z_FLOOR_SIDES, Block.createCuboidShape(4.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D));
    public static final VoxelShape NORTH_FLOOR_LEG = Block.createCuboidShape(6.0D, 0.0D, 2.0D, 10.0D, 7.0D, 4.0D);
    public static final VoxelShape SOUTH_FLOOR_LEG = Block.createCuboidShape(6.0D, 0.0D, 12.0D, 10.0D, 7.0D, 14.0D);
    public static final VoxelShape NORTH_FLOOR_HINGE = Block.createCuboidShape(5.0D, 7.0D, 2.0D, 11.0D, 13.0D, 4.0D);
    public static final VoxelShape SOUTH_FLOOR_HINGE = Block.createCuboidShape(5.0D, 7.0D, 12.0D, 11.0D, 13.0D, 14.0D);
    public static final VoxelShape NORTH_FLOOR_SIDE = VoxelShapes.union(NORTH_FLOOR_LEG, NORTH_FLOOR_HINGE);
    public static final VoxelShape SOUTH_FLOOR_SIDE = VoxelShapes.union(SOUTH_FLOOR_LEG, SOUTH_FLOOR_HINGE);
    public static final VoxelShape X_FLOOR_SIDES = VoxelShapes.union(NORTH_FLOOR_SIDE, SOUTH_FLOOR_SIDE);
    public static final VoxelShape X_FLOOR_SHAPE = VoxelShapes.union(X_FLOOR_SIDES, Block.createCuboidShape(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 12.0D));
    public static final VoxelShape SOUTH_WALL_WEST_LEG = Block.createCuboidShape(2.0D, 6.0D, 0.0D, 4.0D, 10.0D, 7.0D);
    public static final VoxelShape SOUTH_WALL_EAST_LEG = Block.createCuboidShape(12.0D, 6.0D, 0.0D, 14.0D, 10.0D, 7.0D);
    public static final VoxelShape SOUTH_WALL_WEST_HINGE = Block.createCuboidShape(2.0D, 5.0D, 7.0D, 4.0D, 11.0D, 13.0D);
    public static final VoxelShape SOUTH_WALL_EAST_HINGE = Block.createCuboidShape(12.0D, 5.0D, 7.0D, 14.0D, 11.0D, 13.0D);
    public static final VoxelShape SOUTH_WALL_WEST_SIDE = VoxelShapes.union(SOUTH_WALL_WEST_LEG, SOUTH_WALL_WEST_HINGE);
    public static final VoxelShape SOUTH_WALL_EAST_SIDE = VoxelShapes.union(SOUTH_WALL_EAST_LEG, SOUTH_WALL_EAST_HINGE);
    public static final VoxelShape SOUTH_WALL_SIDES = VoxelShapes.union(SOUTH_WALL_WEST_SIDE, SOUTH_WALL_EAST_SIDE);
    public static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.union(SOUTH_WALL_SIDES, Block.createCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D));
    public static final VoxelShape NORTH_WALL_WEST_LEG = Block.createCuboidShape(2.0D, 6.0D, 7.0D, 4.0D, 10.0D, 16.0D);
    public static final VoxelShape NORTH_WALL_EAST_LEG = Block.createCuboidShape(12.0D, 6.0D, 7.0D, 14.0D, 10.0D, 16.0D);
    public static final VoxelShape NORTH_WALL_WEST_HINGE = Block.createCuboidShape(2.0D, 5.0D, 3.0D, 4.0D, 11.0D, 9.0D);
    public static final VoxelShape NORTH_WALL_EAST_HINGE = Block.createCuboidShape(12.0D, 5.0D, 3.0D, 14.0D, 11.0D, 9.0D);
    public static final VoxelShape NORTH_WALL_WEST_SIDE = VoxelShapes.union(NORTH_WALL_WEST_LEG, NORTH_WALL_WEST_HINGE);
    public static final VoxelShape NORTH_WALL_EAST_SIDE = VoxelShapes.union(NORTH_WALL_EAST_LEG, NORTH_WALL_EAST_HINGE);
    public static final VoxelShape NORTH_WALL_SIDES = VoxelShapes.union(NORTH_WALL_WEST_SIDE, NORTH_WALL_EAST_SIDE);
    public static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.union(NORTH_WALL_SIDES, Block.createCuboidShape(4.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D));
    public static final VoxelShape WEST_WALL_NORTH_LEG = Block.createCuboidShape(7.0D, 6.0D, 2.0D, 16.0D, 10.0D, 4.0D);
    public static final VoxelShape WEST_WALL_SOUTH_LEG = Block.createCuboidShape(7.0D, 6.0D, 12.0D, 16.0D, 10.0D, 14.0D);
    public static final VoxelShape WEST_WALL_NORTH_HINGE = Block.createCuboidShape(3.0D, 5.0D, 2.0D, 9.0D, 11.0D, 4.0D);
    public static final VoxelShape WEST_WALL_SOUTH_HINGE = Block.createCuboidShape(3.0D, 5.0D, 12.0D, 9.0D, 11.0D, 14.0D);
    public static final VoxelShape WEST_WALL_NORTH_SIDE = VoxelShapes.union(WEST_WALL_NORTH_LEG, WEST_WALL_NORTH_HINGE);
    public static final VoxelShape WEST_WALL_SOUTH_SIDE = VoxelShapes.union(WEST_WALL_SOUTH_LEG, WEST_WALL_SOUTH_HINGE);
    public static final VoxelShape WEST_WALL_SIDES = VoxelShapes.union(WEST_WALL_NORTH_SIDE, WEST_WALL_SOUTH_SIDE);
    public static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.union(WEST_WALL_SIDES, Block.createCuboidShape(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));
    public static final VoxelShape EAST_WALL_NORTH_LEG = Block.createCuboidShape(0.0D, 6.0D, 2.0D, 9.0D, 10.0D, 4.0D);
    public static final VoxelShape EAST_WALL_SOUTH_LEG = Block.createCuboidShape(0.0D, 6.0D, 12.0D, 9.0D, 10.0D, 14.0D);
    public static final VoxelShape EAST_WALL_NORTH_HINGE = Block.createCuboidShape(7.0D, 5.0D, 2.0D, 13.0D, 11.0D, 4.0D);
    public static final VoxelShape EAST_WALL_SOUTH_HINGE = Block.createCuboidShape(7.0D, 5.0D, 12.0D, 13.0D, 11.0D, 14.0D);
    public static final VoxelShape EAST_WALL_NORTH_SIDE = VoxelShapes.union(EAST_WALL_NORTH_LEG, EAST_WALL_NORTH_HINGE);
    public static final VoxelShape EAST_WALL_SOUTH_SIDE = VoxelShapes.union(EAST_WALL_SOUTH_LEG, EAST_WALL_SOUTH_HINGE);
    public static final VoxelShape EAST_WALL_SIDES = VoxelShapes.union(EAST_WALL_NORTH_SIDE, EAST_WALL_SOUTH_SIDE);
    public static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.union(EAST_WALL_SIDES, Block.createCuboidShape(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 12.0D));
    public static final VoxelShape WEST_CEILING_LEG = Block.createCuboidShape(2.0D, 9.0D, 6.0D, 4.0D, 16.0D, 10.0D);
    public static final VoxelShape EAST_CEILING_LEG = Block.createCuboidShape(12.0D, 9.0D, 6.0D, 14.0D, 16.0D, 10.0D);
    public static final VoxelShape WEST_CEILING_HINGE = Block.createCuboidShape(2.0D, 3.0D, 5.0D, 4.0D, 9.0D, 11.0D);
    public static final VoxelShape EAST_CEILING_HINGE = Block.createCuboidShape(12.0D, 3.0D, 5.0D, 14.0D, 9.0D, 11.0D);
    public static final VoxelShape WEST_CEILING_SIDE = VoxelShapes.union(WEST_CEILING_LEG, WEST_CEILING_HINGE);
    public static final VoxelShape EAST_CEILING_SIDE = VoxelShapes.union(EAST_CEILING_LEG, EAST_CEILING_HINGE);
    public static final VoxelShape Z_CEILING_SIDES = VoxelShapes.union(WEST_CEILING_SIDE, EAST_CEILING_SIDE);
    public static final VoxelShape Z_CEILING_SHAPE = VoxelShapes.union(Z_CEILING_SIDES, Block.createCuboidShape(4.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D));
    public static final VoxelShape NORTH_CEILING_LEG = Block.createCuboidShape(6.0D, 9.0D, 2.0D, 10.0D, 16.0D, 4.0D);
    public static final VoxelShape SOUTH_CEILING_LEG = Block.createCuboidShape(6.0D, 9.0D, 12.0D, 10.0D, 16.0D, 14.0D);
    public static final VoxelShape NORTH_CEILING_HINGE = Block.createCuboidShape(5.0D, 3.0D, 2.0D, 11.0D, 9.0D, 4.0D);
    public static final VoxelShape SOUTH_CEILING_HINGE = Block.createCuboidShape(5.0D, 3.0D, 12.0D, 11.0D, 9.0D, 14.0D);
    public static final VoxelShape NORTH_CEILING_SIDE = VoxelShapes.union(NORTH_CEILING_LEG, NORTH_CEILING_HINGE);
    public static final VoxelShape SOUTH_CEILING_SIDE = VoxelShapes.union(SOUTH_CEILING_LEG, SOUTH_CEILING_HINGE);
    public static final VoxelShape X_CEILING_SIDES = VoxelShapes.union(NORTH_CEILING_SIDE, SOUTH_CEILING_SIDE);
    public static final VoxelShape X_CEILING_SHAPE = VoxelShapes.union(X_CEILING_SIDES, Block.createCuboidShape(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 12.0D));

    protected SpeakerBlock() {
        super(FabricBlockSettings.of(Material.METAL, MaterialColor.BLACK).breakByTool(FabricToolTags.PICKAXES).requiresTool().sounds(BlockSoundGroup.WOOL).strength(0.3f));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(FACE, WallMountLocation.WALL));
    }

    private VoxelShape getShape(BlockState state) {
        Direction direction = state.get(FACING);
        switch(state.get(FACE)) {
            case FLOOR:
                if (direction != Direction.NORTH && direction != Direction.SOUTH) {
                    return X_FLOOR_SHAPE;
                }

                return Z_FLOOR_SHAPE;
            case WALL:
                if (direction == Direction.NORTH) {
                    return NORTH_WALL_SHAPE;
                } else if (direction == Direction.SOUTH) {
                    return SOUTH_WALL_SHAPE;
                } else {
                    if (direction == Direction.EAST) {
                        return EAST_WALL_SHAPE;
                    }

                    return WEST_WALL_SHAPE;
                }
            case CEILING:
                if (direction != Direction.NORTH && direction != Direction.SOUTH) {
                    return X_CEILING_SHAPE;
                }

                return Z_CEILING_SHAPE;
            default:
                return X_FLOOR_SHAPE;
        }
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getShape(state);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getShape(state);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SpeakerBlockEntity) {
            if (!world.isClient) {
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,CommonMod.OPEN_SPEAKER, ((SpeakerBlockEntity) be).toPacket());
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new SpeakerBlockEntity();
    }

}
