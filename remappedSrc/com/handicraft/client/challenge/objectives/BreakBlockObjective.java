/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.HandiUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Predicate;

import static com.handicraft.client.challenge.objectives.BreakBlockObjective.*;

public class BreakBlockObjective extends ObjectiveType<Instance> {


    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return new Instance(PlayerPredicate.ANY,modifier.modify(HandiUtils.randomEnum(random,BreakableBlock.class)),random.nextInt(3) == 0 ? modifier.modify(HandiUtils.randomEnum(random,ToolType.class)) : null);
    }

    @Override
    public Instance fromNBT(CompoundTag tag, PlayerPredicate player) {
        BreakableBlock b = BreakableBlock.valueOf(tag.getString("block"));
        ToolType t = tag.contains("tool") ? ToolType.valueOf(tag.getString("tool")) : null;
        return new Instance(player,b,t);
    }

    public void trigger(PlayerEntity player, Block block, ItemStack tool) {
        System.out.println("broken block");
        triggerListeners(player,i->i.test(player,block,tool));
    }

    public static class Instance extends ObjectiveInstance {

        private BreakableBlock block;
        @Nullable
        private ToolType tool;

        public Instance(PlayerPredicate player, BreakableBlock block, @Nullable ToolType tool) {
            super(player);
            this.block = block;
            this.tool = tool;
        }

        public boolean test(PlayerEntity player, Block b, ItemStack t) {
            return super.test(player) && block.test(b) && (tool == null || tool.test(t));
        }

        @Override
        public Text getText(int count) {
            String bname = HandiUtils.pluralize(count,block.name);
            if (tool == null) {
                return new TranslatableText("objective.break_block", count, bname);
            } else {
                return new TranslatableText("objective.break_block.with", count, bname, tool.getName() + " " + block.toolName);
            }
        }

        @Override
        public ItemConvertible[] getIcons() {
            if (tool == null) {
                return new ItemConvertible[]{block.icon};
            }
            return new ItemConvertible[]{block.icon,tool.getIcon(block.toolName)};
        }

        @Override
        public void toNBT(CompoundTag tag) {
            tag.putString("block",block.name());
            if (tool != null) {
                tag.putString("tool",tool.name());
            }
        }

    }

    public enum BreakableBlock implements ObjectiveParameter<Block> {
        STONE(8,5.0F, Blocks.STONE, "block(s) of Stone", "Pickaxe"),
        GRASS(3,2.0F, Blocks.GRASS_BLOCK, "Grass Block(s)", "Shovel"),
        LOG(4,2.0F, b->b.isIn(BlockTags.LOGS), "Log block(s)", "Axe", Blocks.OAK_LOG),
        NETHERRACK(6,3.0f,Blocks.NETHERRACK,"Netherrack block(s)", "Pickaxe"),
        COAL_ORE(3,0.5F,Blocks.COAL_ORE,"Coal Ore(s)", "Pickaxe"),
        IRON_ORE(2,0.4F,Blocks.IRON_ORE,"Iron Ore(s)", "Pickaxe");


        private final int weight;
        private final float countModifier;
        private final Predicate<Block> predicate;
        private final String name;
        private final String toolName;
        private Block icon;

        BreakableBlock(int weight, float countModifier, Predicate<Block> predicate, String name, String toolName, Block icon) {
            this.weight = weight;
            this.countModifier = countModifier;
            this.predicate = predicate;
            this.name = name;
            this.toolName = toolName;
            this.icon = icon;
        }

        BreakableBlock(int weight, float countModifier, Block block, String name, String toolName) {
            this(weight, countModifier, b->b == block, name, toolName, block);
        }

        @Override
        public float getCountModifier() {
            return countModifier;
        }

        @Override
        public String getName() {
            return name;
        }

        public String getToolName() {
            return toolName;
        }

        @Override
        public boolean test(Block input) {
            return predicate.test(input);
        }

        @Override
        public int getWeight() {
            return weight;
        }
    }

}
