/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import com.handicraft.client.ModTags;
import com.handicraft.client.block.sapling.DarkSaplingGenerator;
import com.handicraft.client.util.BlockItem;
import com.handicraft.client.util.CreativeTab;
import com.handicraft.client.util.Register;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.HashMap;
import java.util.Map;

public class ModBlocks {

    @Register("peony")
    @BlockItem(CreativeTab.DECORATION)
    public static final FlowerBlock PEONY = new FlowerBlock(StatusEffects.GLOWING,20, FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));

    @Register("potted_peony")
    public static final FlowerPotBlock POTTED_PEONY = new FlowerPotBlock(PEONY,FabricBlockSettings.of(Material.SUPPORTED).breakInstantly().nonOpaque());

    @Register("heated_obsidian")
    public static final HeatedObsidianBlock HEATED_OBSIDIAN = new HeatedObsidianBlock();

    @Register("netherite_furnace")
    @BlockItem(CreativeTab.DECORATION)
    public static final NetheriteFurnaceBlock NETHERITE_FURNACE = new NetheriteFurnaceBlock();

    public static final Map<DyeColor,ColoredWaterBlock> COLORED_WATER_BLOCK_MAP = new HashMap<>();

    @Register("ruby_block")
    @BlockItem(value = CreativeTab.BLOCKS)
    public static final Block RUBY_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.PICKAXES,2));

    @Register("halloween_cake")
    @BlockItem(CreativeTab.FOOD)
    public static final CakeBlock HALLOWEEN_CAKE = new HalloweenCakeBlock(FabricBlockSettings.of(Material.CAKE,MaterialColor.ORANGE).strength(0.5F).sounds(BlockSoundGroup.WOOL));

    @Register("tombstone")
    @BlockItem(CreativeTab.DECORATION)
    public static final TombstoneBlock TOMBSTONE = new TombstoneBlock();

    @Register("dark_stone")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARK_STONE = new Block(FabricBlockSettings.of(Material.STONE,MaterialColor.STONE).breakByTool(FabricToolTags.PICKAXES).strength(1.5f,6f));

    @Register("dark_ore")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARK_ORE = new DarkOreBlock(3,7);

    @Register("dark_leaves")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARK_LEAVES = new Block(FabricBlockSettings.of(Material.STONE,MaterialColor.MAGENTA).strength(3f,4f).breakByTool(FabricToolTags.PICKAXES));

    @Register("shadow_stone")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block SHADOW_STONE = new Block(FabricBlockSettings.of(Material.STONE,MaterialColor.GRAY).breakByTool(FabricToolTags.PICKAXES).strength(1.5f,6f));

    @Register("dark_ruby_block")
    @BlockItem(value = CreativeTab.BLOCKS)
    public static final Block DARK_RUBY_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.PICKAXES,2));

    @Register("darkness_diamond_ore")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARKNESS_DIAMOND_ORE = new DarkOreBlock(3,7);

    @Register("dark_rose")
    @BlockItem(CreativeTab.DECORATION)
    public static final FlowerBlock DARK_ROSE = new FlowerBlock(StatusEffects.BLINDNESS,20,FabricBlockSettings.copyOf(Blocks.WITHER_ROSE)) {
        @Override
        protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
            return super.canPlantOnTop(floor, world, pos) || floor.isIn(Tags.DARK_STONES);
        }
    };

    @Register("dark_magma_block")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARK_MAGMA_BLOCK = new MagmaBlock(FabricBlockSettings.copyOf(Blocks.MAGMA_BLOCK).lightLevel(0));

    @Register("dark_log")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARK_LOG = new PillarBlock(FabricBlockSettings.of(Material.WOOD,state->state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.GRAY : MaterialColor.BLACK).strength(2.0f).sounds(BlockSoundGroup.WOOD));

    @Register("stripped_dark_log")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block STRIPPED_DARK_LOG = new PillarBlock(FabricBlockSettings.of(Material.WOOD,state->state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.GRAY : MaterialColor.BLACK).strength(2.0f).sounds(BlockSoundGroup.WOOD));

    @Register("dark_wood")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARK_WOOD = new PillarBlock(FabricBlockSettings.of(Material.WOOD,MaterialColor.BLACK).strength(2.0f).sounds(BlockSoundGroup.WOOD));

    @Register("stripped_dark_wood")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block STRIPPED_DARK_WOOD = new PillarBlock(FabricBlockSettings.of(Material.WOOD,MaterialColor.GRAY).strength(2.0f).sounds(BlockSoundGroup.WOOD));

    @Register("dark_planks")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARK_PLANKS = new Block(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.BLACK).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));

    @Register("dark_stairs")
    @BlockItem(CreativeTab.BLOCKS)
    public static final StairsBlock DARK_STAIRS = new CustomStairsBlock(DARK_PLANKS.getDefaultState(),FabricBlockSettings.copy(DARK_PLANKS));

    @Register("dark_slab")
    @BlockItem(CreativeTab.BLOCKS)
    public static final SlabBlock DARK_SLAB = new SlabBlock(FabricBlockSettings.copy(DARK_PLANKS));

    @Register("dark_fence")
    @BlockItem(CreativeTab.DECORATION)
    public static final FenceBlock DARK_FENCE = new FenceBlock(FabricBlockSettings.copy(DARK_PLANKS));

    @Register("dark_fence_gate")
    @BlockItem(CreativeTab.REDSTONE)
    public static final FenceGateBlock DARK_FENCE_GATE = new FenceGateBlock(FabricBlockSettings.copy(DARK_PLANKS));

    @Register("dark_button")
    @BlockItem(CreativeTab.REDSTONE)
    public static final CustomButtonBlock DARK_BUTTON = new CustomButtonBlock(true,FabricBlockSettings.copyOf(DARK_PLANKS).noCollision().strength(0.5f));

    @Register("dark_pressure_plate")
    @BlockItem(CreativeTab.REDSTONE)
    public static final DarkPressurePlateBlock DARK_PRESSURE_PLATE = new DarkPressurePlateBlock();

    @Register("dark_sapling")
    @BlockItem(CreativeTab.DECORATION)
    public static final SaplingBlock DARK_SAPLING = new CustomSaplingBlock(new DarkSaplingGenerator(),AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)) {
        @Override
        protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
            return super.canPlantOnTop(floor, world, pos) || floor.isIn(Tags.DARK_STONES);
        }
    };

    @Register("green_dark_fire")
    public static final DarkFireBlock GREEN_DARK_FIRE = new DarkFireBlock(FabricBlockSettings.of(Material.FIRE,MaterialColor.GREEN).noCollision().breakInstantly().luminance(s->13),()->Tags.GREEN_FLAMMABLES);

    @Register("purple_dark_fire")
    public static final DarkFireBlock PURPLE_DARK_FIRE = new DarkFireBlock(FabricBlockSettings.of(Material.FIRE,MaterialColor.PURPLE).noCollision().breakInstantly().luminance(s->13),()->Tags.PURPLE_FLAMMABLES);

    @Register("darkness_portal")
    public static final DarkPortalBlock DARKNESS_PORTAL = new DarkPortalBlock();

    @Register("green_fire_lantern")
    @BlockItem(CreativeTab.DECORATION)
    public static final LanternBlock GREEN_FIRE_LANTERN = new LanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN));

    @Register("purple_fire_lantern")
    @BlockItem(CreativeTab.DECORATION)
    public static final LanternBlock PURPLE_FIRE_LANTERN = new LanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN));

    @Register("green_fire_torch")
    public static final TorchBlock GREEN_FIRE_TORCH = new DarkTorchBlock(FabricBlockSettings.copyOf(Blocks.TORCH), ParticleTypes.SMOKE);

    @Register("purple_fire_torch")
    public static final TorchBlock PURPLE_FIRE_TORCH = new DarkTorchBlock(FabricBlockSettings.copyOf(Blocks.TORCH), ParticleTypes.SMOKE);

    @Register("green_fire_wall_torch")
    public static final WallTorchBlock GREEN_FIRE_WALL_TORCH = new DarkWallTorchBlock(FabricBlockSettings.copyOf(Blocks.WALL_TORCH), ParticleTypes.SMOKE);

    @Register("purple_fire_wall_torch")
    public static final WallTorchBlock PURPLE_FIRE_WALL_TORCH = new DarkWallTorchBlock(FabricBlockSettings.copyOf(Blocks.WALL_TORCH), ParticleTypes.SMOKE);

    @Register("dark_obsidian")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARK_OBSIDIAN = new Block(FabricBlockSettings.copyOf(Blocks.CRYING_OBSIDIAN).requiresTool().breakByTool(FabricToolTags.PICKAXES,3));

    @Register("darkness_bricks")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block DARKNESS_BRICKS = new Block(FabricBlockSettings.copyOf(Blocks.NETHER_BRICKS).requiresTool().breakByTool(FabricToolTags.PICKAXES));

    @Register("jack_soul_lantern")
    @BlockItem(CreativeTab.BLOCKS)
    public static final Block JACK_SOUL_LANTERN = new JackSoulLanternBlock();

    public static class Tags {

        public static final Tag.Identified<Block> DARK_LOGS = ModTags.block("dark_logs",DARK_LOG,DARK_WOOD,STRIPPED_DARK_LOG,STRIPPED_DARK_WOOD);

        public static final Tag.Identified<Block> DARK_STONES = ModTags.block("dark_stones",DARK_STONE,SHADOW_STONE);

        public static final Tag.Identified<Block> GREEN_FLAMMABLES = ModTags.block("green_flammables",DARK_LEAVES,DARKNESS_BRICKS);

        public static final Tag.Identified<Block> PURPLE_FLAMMABLES = ModTags.block("purple_flammables",DARK_STONE,SHADOW_STONE);
    }

}
