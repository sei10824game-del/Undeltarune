package com.undeltarune.entity;

import com.undeltarune.attack.Attack;
import com.undeltarune.attack.AttackCtx;
import com.undeltarune.def.CharacterDef;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.def.Faction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/** Generic character entity: turn-based attack runner driven by a CharacterDef. */
public class UtMob extends PathfinderMob {
    static final EntityDataAccessor<Integer> ATK = SynchedEntityData.defineId(UtMob.class, EntityDataSerializers.INT);
    static final EntityDataAccessor<Integer> ATK_T = SynchedEntityData.defineId(UtMob.class, EntityDataSerializers.INT);
    public final CharacterDef def;
    private int rest = 40, next = 0;
    private final ServerBossEvent bar;

    public UtMob(EntityType<? extends UtMob> t, Level l, CharacterDef def) {
        super(t, l); this.def = def;
        bar = def.boss() ? new ServerBossEvent(Component.literal(def.name()), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS) : null;
    }
    public static AttributeSupplier.Builder attrs(CharacterDef d) {
        return PathfinderMob.createMobAttributes().add(Attributes.MAX_HEALTH, d.hp()).add(Attributes.MOVEMENT_SPEED, d.speed())
            .add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, d.boss() ? 1 : 0).add(Attributes.ATTACK_DAMAGE, 4);
    }
    @Override protected void defineSynchedData() { super.defineSynchedData(); entityData.define(ATK, -1); entityData.define(ATK_T, 0); }
    public int atkIndex() { return entityData.get(ATK); }
    public int atkTick() { return entityData.get(ATK_T); }
    public Attack current() { int i = atkIndex(); return i >= 0 && i < def.attacks().size() ? def.attacks().get(i) : null; }
    public float hpFrac() { return getHealth() / getMaxHealth(); }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new Goal() {
            { setFlags(java.util.EnumSet.of(Flag.MOVE, Flag.LOOK)); }
            public boolean canUse() { return getTarget() != null; }
            public void tick() {
                LivingEntity t = getTarget(); getLookControl().setLookAt(t, 30, 30);
                if (atkIndex() >= 0) return;
                if (distanceTo(t) > def.keepDist()) getNavigation().moveTo(t, 1.0); else getNavigation().stop();
            }
        });
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, .6));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isFoe) {
            @Override public boolean canUse() { return !def.attacks().isEmpty() && super.canUse(); }
        });
    }
    /** Canon hostility: players by Attitude, other characters by Faction. */
    public boolean isFoe(LivingEntity e) {
        if (e instanceof Player p) return def.toPlayer() == Attitude.HOSTILE && !p.isCreative() && !p.isSpectator();
        if (e instanceof UtMob m) return def.foes().contains(m.def.faction()) || Faction.hostile(def.faction(), m.def.faction());
        return false;
    }

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        if (bar != null) bar.setProgress(hpFrac());
        LivingEntity t = getTarget();
        int i = atkIndex();
        if (i >= 0) {
            Attack a = def.attacks().get(i); int k = atkTick();
            if (t == null || !t.isAlive()) { endAtk(); return; }
            a.tick(new AttackCtx(this, t), k);
            if (k + 1 >= a.duration) endAtk(); else entityData.set(ATK_T, k + 1);
        } else if (t != null && --rest <= 0 && distanceTo(t) < 28) {
            List<Integer> ok = new ArrayList<>();
            for (int j = 0; j < def.attacks().size(); j++) if (def.attacks().get(j).usable(this)) ok.add(j);
            if (ok.isEmpty()) { rest = 20; return; }
            int pick = def.randomOrder() ? ok.get(random.nextInt(ok.size())) : ok.get(next++ % ok.size());
            entityData.set(ATK, pick); entityData.set(ATK_T, 0);
        }
    }
    private void endAtk() { entityData.set(ATK, -1); entityData.set(ATK_T, 0); rest = def.rest(); }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); if (bar != null) bar.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); if (bar != null) bar.removePlayer(p); }
    @Override public Component getName() { return hasCustomName() ? super.getName() : Component.literal(def.name()); }
    @Override public boolean removeWhenFarAway(double d) { return false; }
}
