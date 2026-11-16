package com.undeltarune.attack;

import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Helpers handed to Attack.tick(). */
public class AttackCtx {
    public final UtMob me; public final LivingEntity target; public final ServerLevel level; public final RandomSource rnd;
    public AttackCtx(UtMob me, LivingEntity target) { this.me = me; this.target = target; level = (ServerLevel) me.level(); rnd = me.getRandom(); }

    public Vec3 pos() { return me.position(); }
    public Vec3 eye() { return me.getEyePosition(); }
    public Vec3 tpos() { return target.position().add(0, target.getBbHeight() * .5, 0); }
    /** Unit vector from point to the target's center. */
    public Vec3 aim(Vec3 from) { return tpos().subtract(from).normalize(); }
    public Vec3 fwd() { return Vec3.directionFromRotation(0, me.getYRot()); }
    public Vec3 right() { Vec3 f = fwd(); return new Vec3(-f.z, 0, f.x); }

    public UtProjectile.B proj() { return new UtProjectile.B(this); }

    public void sound(SoundEvent s, float vol, float pitch) { level.playSound(null, me.blockPosition(), s, SoundSource.HOSTILE, vol, pitch); }
    public void particle(ParticleOptions p, Vec3 at, int n, double spread, double speed) {
        level.sendParticles(p, at.x, at.y, at.z, n, spread, spread, spread, speed);
    }
    /** Instant damage + knockback to every valid victim within radius. */
    public void aoe(Vec3 c, double r, float dmg, double knock) {
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(r), x -> x != me && me.isFoe(x))) {
            if (e.position().distanceTo(c) > r) continue;
            e.hurt(me.damageSources().mobAttack(me), dmg);
            Vec3 k = e.position().subtract(c).multiply(1, 0, 1).normalize().scale(knock);
            e.push(k.x, .3, k.z); e.hurtMarked = true;
        }
    }
    public void hit(LivingEntity e, float dmg) { e.hurt(me.damageSources().mobAttack(me), dmg); }
    public void dash(Vec3 v) { me.setDeltaMovement(v); me.hurtMarked = true; }
    public void tp(Vec3 p) { me.teleportTo(p.x, p.y, p.z); }
    public void face(Vec3 p) { me.getLookControl().setLookAt(p); }
}
