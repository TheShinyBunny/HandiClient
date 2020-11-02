/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.entity.SpotifyBlockEntity;
import com.handicraft.client.client.screen.SpotifyScreen;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpotifyBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;

    protected SpotifyBlock() {
        super(FabricBlockSettings.of(Material.METAL, MaterialColor.GREEN).breakByTool(FabricToolTags.PICKAXES).requiresTool().sounds(BlockSoundGroup.WOOL).strength(0.3f));
        setDefaultState(getStateManager().getDefaultState().with(POWERED,false));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(POWERED,ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            boolean powered = state.get(POWERED);
            if (powered != world.isReceivingRedstonePower(pos)) {
                world.setBlockState(pos,state.cycle(POWERED),2);
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof SpotifyBlockEntity) {
                    ((SpotifyBlockEntity) be).loopStateUpdated(!powered);
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SpotifyBlockEntity) {
            if (!world.isClient) {
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,CommonMod.OPEN_SPOTIFY, ((SpotifyBlockEntity) be).toPacket());
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new SpotifyBlockEntity();
    }
}
