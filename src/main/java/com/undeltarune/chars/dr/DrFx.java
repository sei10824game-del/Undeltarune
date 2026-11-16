package com.undeltarune.chars.dr;

import com.undeltarune.attack.Attack;
import com.undeltarune.attack.AttackCtx;
import com.undeltarune.attack.Pose;
import com.undeltarune.def.ModelDef;
import com.undeltarune.entity.UtMob;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import java.util.function.Predicate;

/** Shared Deltarune (Ch1/Ch2) helpers: lambda attacks, suit bullets, rings/fans, per-mob scratch memory, biped models. */
public final class DrFx {
    public interface Tick { void run(AttackCtx c, int t); }
    public interface Anim { void run(Pose p, float t, UtMob m); }

    /** Attack from lambdas; gate == null means always usable. */
    public static Attack atk(String id, int dur, Tick tick, Anim pose, Predicate<UtMob> gate) {
        return new Attack(id, dur) {
            public void tick(AttackCtx c, int t) { tick.run(c, t); }
            public void pose(Pose p, float t, UtMob m) { if (pose != null) pose.run(p, t, m); }
            public boolean usable(UtMob m) { return gate == null || gate.test(m); }
        };
    }
    public static Attack atk(String id, int dur, Tick tick, Anim pose) { return atk(id, dur, tick, pose, null); }
    public static Predicate<UtMob> below(float f) { return m -> m.hpFrac() < f; }
    public static Predicate<UtMob> above(float f) { return m -> m.hpFrac() >= f; }

    // ---- easing for poses ----
    /** 0..1 ramp between ticks a and b. */
    public static float ramp(float t, float a, float b) { return t <= a ? 0 : t >= b ? 1 : (t - a) / (b - a); }
    /** Wind-up to 1 over [a,b], hold, fall back to 0 over [c,d]. */
    public static float env(float t, float a, float b, float c, float d) { return ramp(t, a, b) * (1 - ramp(t, c, d)); }
    public static float wave(float t, float speed) { return (float) Math.sin(t * speed); }

    // ---- geometry ----
    public static Vec3 rotY(Vec3 v, double a) { double c = Math.cos(a), s = Math.sin(a); return new Vec3(v.x * c - v.z * s, v.y, v.x * s + v.z * c); }
    public static Vec3 flat(Vec3 v) { Vec3 f = new Vec3(v.x, 0, v.z); return f.lengthSqr() < 1e-6 ? new Vec3(0, 0, 1) : f.normalize(); }
    public static Vec3 side(Vec3 f) { return new Vec3(-f.z, 0, f.x); }

    // ---- per-mob scratch memory (attacks are shared singletons, so state lives on the entity) ----
    static CompoundTag mem(AttackCtx c) { return c.me.getPersistentData(); }
    public static void put(AttackCtx c, String k, Vec3 v) { CompoundTag m = mem(c); m.putDouble("dr_" + k + "x", v.x); m.putDouble("dr_" + k + "y", v.y); m.putDouble("dr_" + k + "z", v.z); }
    public static Vec3 get(AttackCtx c, String k) { CompoundTag m = mem(c); return new Vec3(m.getDouble("dr_" + k + "x"), m.getDouble("dr_" + k + "y"), m.getDouble("dr_" + k + "z")); }
    public static void puti(AttackCtx c, String k, int v) { mem(c).putInt("dr_" + k, v); }
    public static int geti(AttackCtx c, String k) { return mem(c).getInt("dr_" + k); }

    // ---- bullets ----
    public static final int SPADE = 0x7b6cff, HEART = 0xff3c64, DIAMOND = 0xffd84a, CLUB = 0x5cff8a, WHITE = 0xffffff;

    /** Card suit bullet: a lethal core plus harmless accent pieces that ride along to form the silhouette. */
    public static void suit(AttackCtx c, Vec3 at, Vec3 vel, int color, float dmg, int life, int warn, float s) {
        c.proj().at(at).vel(vel).box(.45f * s, .45f * s, .45f * s).color(color).dmg(dmg).life(life).warn(warn).spawn();
        if (color == SPADE || color == CLUB) // stem
            c.proj().at(at.add(0, -.32 * s, 0)).vel(vel).box(.12f * s, .3f * s, .12f * s).color(color).dmg(0).life(life).spawn();
        if (color == HEART || color == CLUB || color == SPADE) // lobes
            c.proj().at(at.add(0, .2 * s, 0)).vel(vel).box(.6f * s, .22f * s, .3f * s).color(color).dmg(0).life(life).spawn();
        if (color == DIAMOND) // elongated tip
            c.proj().at(at).vel(vel).box(.2f * s, .75f * s, .2f * s).color(0xfff2a8).dmg(0).life(life).spawn();
    }
    public static void suit(AttackCtx c, Vec3 at, Vec3 vel, int color, float dmg, int life) { suit(c, at, vel, color, dmg, life, 0, 1); }

    /** n bullets evenly around center (horizontal ring), moving outward at speed (negative = inward). */
    public static void ring(AttackCtx c, Vec3 center, int n, double r, double phase, double speed, int color, float dmg, int life, int warn) {
        for (int i = 0; i < n; i++) {
            double a = phase + i * Math.PI * 2 / n; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
            suit(c, center.add(d.scale(r)), d.scale(speed), color, dmg, life, warn, 1);
        }
    }
    /** n bullets fanned around dir (spread total radians, yaw only). */
    public static void fan(AttackCtx c, Vec3 from, Vec3 dir, int n, double spread, double speed, int color, float dmg, int life) {
        Vec3 d = dir.normalize();
        for (int i = 0; i < n; i++) {
            double a = n == 1 ? 0 : -spread / 2 + spread * i / (n - 1);
            suit(c, from, rotY(d, a).scale(speed), color, dmg, life, 0, 1);
        }
    }
    /** Plain glowing orb/box. */
    public static void orb(AttackCtx c, Vec3 at, Vec3 vel, float size, int color, float dmg, int life, int warn) {
        c.proj().at(at).vel(vel).box(size, size, size).color(color).dmg(dmg).life(life).warn(warn).spawn();
    }
    /** Static beam from `from` along dir: thin telegraph for `warn` ticks, then lethal for `fire`. */
    public static void beam(AttackCtx c, Vec3 from, Vec3 dir, double len, float width, int color, int warn, int fire, float dmg) {
        Vec3 d = dir.normalize();
        c.proj().at(from.add(d.scale(len / 2))).face(d).box(width, width, (float) len).color(color).dmg(dmg).warn(warn).life(warn + fire).pierce().spawn();
    }
    public static void puff(AttackCtx c, ParticleOptions p, Vec3 at, int n) { c.particle(p, at, n, .4, .05); }

    // ---- models ----
    /** Standard Deltarune-proportioned biped (big head, short body). Caller adds face/hair/extras. */
    public static ModelDef biped(int head, int torso, int arms, int legs, float bodyW, float headS) {
        return new ModelDef()
            .box("body", null, 0, 8, 0, bodyW, 7, 4, torso)
            .box("head", "body", 0, 8.5f, 0, headS, headS, headS, head)
            .limb("armL", "body", -(bodyW / 2 + 1.1f), 3, 0, 2.2f, 6.5f, 2.2f, arms)
            .limb("armR", "body", bodyW / 2 + 1.1f, 3, 0, 2.2f, 6.5f, 2.2f, arms)
            .limb("legL", null, -1.5f, 4.6f, 0, 2.6f, 4.6f, 3, legs)
            .limb("legR", null, 1.5f, 4.6f, 0, 2.6f, 4.6f, 3, legs);
    }
    private DrFx() {}
}
