/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.entity;

import com.handicraft.client.item.ModItems;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class IllusionerBossEntity extends IllusionerEntity {

    private ServerBossBar bossBar;

    public IllusionerBossEntity(EntityType<? extends IllusionerEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = (ServerBossBar)(new ServerBossBar(this.getDisplayName(), BossBar.Color.BLUE, BossBar.Style.PROGRESS)).setDarkenSky(true).setDragonMusic(true);
    }

    public static DefaultAttributeContainer.Builder createBossAttributes() {
        return IllusionerEntity.createIllusionerAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH,200);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(3,new SpawnVexGoal());
        this.goalSelector.add(4,new TeleportBehindTargetGoal());
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        setAbleToJoinRaid(false);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public boolean canLead() {
        return false;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        bossBar.setPercent(getHealth() / getMaxHealth());
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        bossBar.removePlayer(player);
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        dropItem(ModItems.DARKNESS_STAR);
    }

    private class SpawnVexGoal extends CastSpellGoal {

        @Override
        protected void castSpell() {
            ServerWorld serverWorld = (ServerWorld) IllusionerBossEntity.this.world;

            for(int i = 0; i < 3; ++i) {
                BlockPos blockPos = IllusionerBossEntity.this.getBlockPos().add(-2 + IllusionerBossEntity.this.random.nextInt(5), 1, -2 + IllusionerBossEntity.this.random.nextInt(5));
                VexEntity vexEntity = EntityType.VEX.create(IllusionerBossEntity.this.world);
                vexEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
                vexEntity.initialize(serverWorld, IllusionerBossEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null);
                vexEntity.setOwner(IllusionerBossEntity.this);
                vexEntity.setBounds(blockPos);
                vexEntity.setLifeTicks(20 * (30 + IllusionerBossEntity.this.random.nextInt(90)));
                serverWorld.spawnEntityAndPassengers(vexEntity);
            }
        }

        @Override
        protected int getSpellTicks() {
            return 100;
        }

        @Override
        protected int startTimeDelay() {
            return 140;
        }

        @Override
        protected @Nullable SoundEvent getSoundPrepare() {
            return null;
        }

        @Override
        protected Spell getSpell() {
            return Spell.SUMMON_VEX;
        }
    }

    private class TeleportBehindTargetGoal extends Goal {
        @Override
        public boolean canStart() {
            return getTarget() != null && random.nextInt(5) == 0;
        }

        @Override
        public void start() {
            LivingEntity target = getTarget();
            if (target == null) return;
            Vec3d eyes = target.getPos().add(0,target.getEyeY(),0);
            Vec3d look = target.getRotationVector();
            BlockHitResult res = world.raycast(new RaycastContext(eyes,eyes.add(look.normalize().multiply(-15)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,IllusionerBossEntity.this));
            BlockPos.Mutable pos = new BlockPos.Mutable(res.getBlockPos().getX(),target.getY() - 3,res.getBlockPos().getZ());
            for (int i = 0; i < 15 && world.getBlockCollisions(IllusionerBossEntity.this,new Box(pos)).findAny().isPresent(); i++) {
                pos.move(Direction.UP);
            }
            if (!world.getBlockCollisions(IllusionerBossEntity.this,new Box(pos,pos.up())).findAny().isPresent()) {
                IllusionerBossEntity.this.refreshPositionAndAngles(pos.toImmutable(),MathHelper.wrapDegrees(IllusionerBossEntity.this.getYaw() + 180),IllusionerBossEntity.this.getPitch());
            }
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }
    }
}
