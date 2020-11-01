package com.handicraft.client.entity;

import com.handicraft.client.item.ModItems;
import com.handicraft.client.mixin.client.ClientWorld_CalculateColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DarknessWizardEntity extends IllusionerEntity {

    private final ServerBossBar bossBar;

    public DarknessWizardEntity(EntityType<? extends IllusionerEntity> entityType, World world) {
        super(entityType, world);
        bossBar = (ServerBossBar)new ServerBossBar(getDisplayName(), BossBar.Color.BLUE, BossBar.Style.PROGRESS).setDarkenSky(true).setThickenFog(true);
        getNavigation().setCanSwim(true);
        experiencePoints = 100;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        goalSelector.add(6,new SpawnVexesGoal());
        goalSelector.add(7,new TeleportBehindTargetGoal());
    }

    @Override
    public boolean canLead() {
        return false;
    }

    @Override
    public boolean canJoinRaid() {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    protected boolean canClimb() {
        return true;
    }


    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        bossBar.setName(getDisplayName());
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
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData, entityTag);
        equipStack(EquipmentSlot.MAINHAND,EnchantmentHelper.enchant(random,getMainHandStack(), (int) (20f + difficulty.getClampedLocalDifficulty() * 18f),true));
        return data;
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        ItemEntity itemEntity = this.dropItem(ModItems.DARKNESS_STAR);
        if (itemEntity != null) {
            itemEntity.setCovetedItem();
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        return false;
    }

    class SpawnVexesGoal extends CastSpellGoal {

        private final TargetPredicate closeVexPredicate;

        private SpawnVexesGoal() {
            super();
            this.closeVexPredicate = (new TargetPredicate()).setBaseMaxDistance(16.0D).includeHidden().ignoreDistanceScalingFactor().includeInvulnerable().includeTeammates();
        }

        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            } else {
                int i = DarknessWizardEntity.this.world.getTargets(VexEntity.class, this.closeVexPredicate, DarknessWizardEntity.this, DarknessWizardEntity.this.getBoundingBox().expand(16.0D)).size();
                return DarknessWizardEntity.this.random.nextInt(8) + 1 > i;
            }
        }

        @Override
        protected int getSpellTicks() {
            return 100;
        }

        @Override
        protected int startTimeDelay() {
            return 340;
        }

        @Override
        protected void castSpell() {
            ServerWorld serverWorld = (ServerWorld)DarknessWizardEntity.this.world;

            for(int i = 0; i < 3; ++i) {
                BlockPos pos = DarknessWizardEntity.this.getBlockPos().add(-2 + DarknessWizardEntity.this.random.nextInt(5), 1, -2 + DarknessWizardEntity.this.random.nextInt(5));
                VexEntity vexEntity = EntityType.VEX.create(DarknessWizardEntity.this.world);
                vexEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
                vexEntity.initialize(serverWorld, DarknessWizardEntity.this.world.getLocalDifficulty(pos), SpawnReason.MOB_SUMMONED, null, null);
                vexEntity.setOwner(DarknessWizardEntity.this);
                vexEntity.setBounds(pos);
                vexEntity.setLifeTicks(20 * (30 + DarknessWizardEntity.this.random.nextInt(90)));
                serverWorld.spawnEntityAndPassengers(vexEntity);
            }

        }

        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
        }
    }

    private class TeleportBehindTargetGoal extends Goal {
        @Override
        public boolean canStart() {
            return DarknessWizardEntity.this.getTarget() != null && DarknessWizardEntity.this.random.nextInt(80) == 0;
        }

        @Override
        public void start() {
            LivingEntity target = getTarget();
            Vec3d rot = target.getRotationVec(1f).multiply(-1f);
            double dist = 10;
            BlockHitResult res = world.raycast(new RaycastContext(target.getPos(),target.getPos().add(rot.x * dist,rot.y * dist,rot.z * dist), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY,DarknessWizardEntity.this));
            BlockPos pos = res.getBlockPos();
            for (Direction dir : Direction.values()) {
                BlockPos current = pos.offset(dir);
                if (!world.getBlockState(current).getMaterial().blocksMovement() && !world.getBlockState(current.up()).getMaterial().blocksMovement()) {
                    teleportTo(pos.offset(dir));
                    break;
                }
            }
            while (pos.getY() - target.getY() < 40 && world.getBlockState(pos).getMaterial().blocksMovement()) {
                pos = pos.up();
            }
            if (!world.getBlockState(pos).getMaterial().blocksMovement()) {
                teleportTo(pos);
            }
        }

        private void teleportTo(BlockPos pos) {
            boolean succeed = teleport(pos.getX() + 0.5,pos.getY(),pos.getZ() + 0.5,true);
            if (succeed && !isSilent()) {
                world.playSound(null, prevX, prevY, prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, getSoundCategory(), 1.0F, 1.0F);
                playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }
    }
}
