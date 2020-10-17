/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.HandiUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Random;

import static com.handicraft.client.challenge.objectives.DamageEntityObjective.*;

public class DamageEntityObjective extends ObjectiveType<Instance> {

    private boolean kill;

    public DamageEntityObjective(boolean kill) {
        this.kill = kill;
    }

    @Override
    public Instance generate(Random random, CountModifier modifier) {
        return new Instance(PlayerPredicate.ANY, modifier.modify(HandiUtils.randomEnum(random,Entities.class)),modifier.modify(HandiUtils.randomEnum(random,DamageType.class)),kill);
    }

    @Override
    public Instance fromNBT(CompoundTag tag, PlayerPredicate player) {
        Entities e = Entities.valueOf(tag.getString("entity"));
        DamageType t = DamageType.valueOf(tag.getString("damage"));
        return new Instance(player,e,t,kill);
    }

    public void trigger(PlayerEntity player, EntityType<?> type, DamageSource damageSource) {
        triggerListeners(player,i->i.test(player,type,damageSource));
    }

    public static class Instance extends ObjectiveInstance {

        private boolean kill;
        private Entities type;
        private DamageType damageType;

        public Instance(PlayerPredicate player, Entities type, DamageType damageType, boolean kill) {
            super(player);
            this.type = type;
            this.damageType = damageType;
            this.kill = kill;
        }

        public boolean test(PlayerEntity player, EntityType<?> type, DamageSource source) {
            return super.test(player) && this.type.test(type) && this.damageType.test(source);
        }

        @Override
        public Text getText(int count) {
            if (damageType == DamageType.ANY) {
                return new TranslatableText("objective." + (kill ? "kill_entity" : "damage_entity"),count,type.getName() + (count == 1 ? "" : "s"));
            }
            return new TranslatableText("objective." + (kill ? "kill_entity" : "damage_entity") + ".with",count,type.getName() + (count == 1 ? "" : "s"),damageType.getName());
        }

        @Override
        public ItemConvertible[] getIcons() {
            if (damageType == DamageType.ANY) {
                return new ItemConvertible[]{type.icon};
            }
            return new ItemConvertible[]{type.icon,damageType.getIcon()};
        }

        @Override
        public void toNBT(CompoundTag tag) {
            tag.putString("entity",type.name());
            tag.putString("damage",damageType.name());
        }
    }


    public enum Entities implements ObjectiveParameter<EntityType<?>> {
        CREEPER(EntityType.CREEPER,3,0.5F, Items.GUNPOWDER),
        ZOMBIE(EntityType.ZOMBIE,6,1, Items.ROTTEN_FLESH),
        SKELETON(EntityType.SKELETON,5,0.8F, Items.BONE),
        SPIDER(EntityType.SPIDER,1,0.4f, Items.SPIDER_EYE),
        PIGLIN(EntityType.PIGLIN,2,0.8F, Items.GOLD_INGOT),
        COW(EntityType.COW,1,0.7F, Items.COOKED_BEEF),
        SLIME(EntityType.SLIME,3,3F, Items.SLIME_BLOCK),
        BLAZE(EntityType.BLAZE,1,0.2F, Items.BLAZE_ROD);

        private final EntityType<?> type;
        private final int weight;
        private final float countModifier;
        private final ItemConvertible icon;

        Entities(EntityType<?> type, int weight, float countModifier, ItemConvertible icon) {
            this.type = type;
            this.weight = weight;
            this.countModifier = countModifier;
            this.icon = icon;
        }

        public static Entities get(EntityType<?> type) {
            for (Entities e : values()) {
                if (e.type == type) return e;
            }
            return null;
        }

        public int getWeight() {
            return weight;
        }

        public EntityType<?> getType() {
            return type;
        }

        public float getCountModifier() {
            return countModifier;
        }

        @Override
        public String getName() {
            return I18n.translate(type.getTranslationKey());
        }

        @Override
        public boolean test(EntityType<?> input) {
            return type == input;
        }
    }

}
