package com.undeltarune.chars.drlate;

import com.undeltarune.attack.Attack;
import com.undeltarune.attack.AttackCtx;
import com.undeltarune.attack.Pose;
import com.undeltarune.def.ModelDef;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Shared toolkit for the Deltarune Ch3+ groups (DrCh3Ch4, DrCh5Plus): lambda attacks, bullet shapes, poses, models. */
public final class DrKit {
    public interface Tk { void t(AttackCtx c, int t); }
    public interface Ps { void p(Pose p, float t, UtMob m); }

    public static Attack atk(String id, int dur, Tk tk, Ps ps) { return phase(id, dur, -1, 2, tk, ps); }
    /** Usable while lo < hpFrac <= hi. */
    public static Attack phase(String id, int dur, float lo, float hi, Tk tk, Ps ps) {
        return new Attack(id, dur) {
            public void tick(AttackCtx c, int t) { tk.t(c, t); }
            public void pose(Pose p, float t, UtMob m) { ps.p(p, t, m); }
            public boolean usable(UtMob m) { float f = m.hpFrac(); return f > lo && f <= hi; }
        };
    }

    // ---- math ----
    public static float ramp(float t, float a, float b) { return Math.max(0, Math.min(1, (t - a) / (b - a))); }
    /** 0 -> 1 over [a,b], hold, back to 0 over [c,d]. */
    public static float env(float t, float a, float b, float c, float d) { return t < c ? ramp(t, a, b) : 1 - ramp(t, c, d); }
    public static float sin(float t, float f) { return (float) Math.sin(t * f); }
    public static Vec3 flat(Vec3 v) { Vec3 f = new Vec3(v.x, 0, v.z); return f.lengthSqr() < 1e-6 ? new Vec3(0, 0, 1) : f.normalize(); }
    /** Unit horizontal direction me -> target. */
    public static Vec3 toT(AttackCtx c) { return flat(c.tpos().subtract(c.pos())); }
    public static Vec3 side(Vec3 f) { return new Vec3(-f.z, 0, f.x); }
    /** Point on a circle of radius r around the target's center, angle a (radians), height dy. */
    public static Vec3 around(AttackCtx c, double r, double a, double dy) { return c.tpos().add(Math.cos(a) * r, dy, Math.sin(a) * r); }
    public static Vec3 rot(Vec3 v, double a) { double cs = Math.cos(a), sn = Math.sin(a); return new Vec3(v.x * cs - v.z * sn, v.y, v.x * sn + v.z * cs); }
    public static Vec3 jit(AttackCtx c, double s) { return new Vec3(c.rnd.nextGaussian() * s, c.rnd.nextGaussian() * s * .4, c.rnd.nextGaussian() * s); }

    // ---- bullets ----
    public static UtProjectile shot(AttackCtx c, Vec3 at, Vec3 vel, float s, int rgb, float dmg, int warn, int life) {
        return c.proj().at(at).vel(vel).box(s, s, s).color(rgb).dmg(dmg).warn(warn).life(life).spawn();
    }
    /** Elongated bullet oriented along its velocity (knives, arrows, swords, lasers). */
    public static UtProjectile bolt(AttackCtx c, Vec3 at, Vec3 vel, float w, float l, int rgb, float dmg, int warn, int life) {
        return c.proj().at(at).vel(vel).box(w, w, l).color(rgb).dmg(dmg).warn(warn).life(life).spawn();
    }
    /** Harmless decoration (props, faces, telegraph markers). */
    public static UtProjectile prop(AttackCtx c, Vec3 at, Vec3 face, float w, float h, float l, int rgb, int life) {
        return c.proj().at(at).face(face).box(w, h, l).color(rgb).dmg(0).life(life).spawn();
    }
    /** Static line hazard: telegraphs for warn ticks then cuts (slashes, lasers, pillars). */
    public static UtProjectile line(AttackCtx c, Vec3 center, Vec3 dir, float len, float w, int rgb, float dmg, int warn, int active) {
        return c.proj().at(center).face(dir).box(w, w, len).color(rgb).dmg(dmg).warn(warn).life(warn + active).pierce().spawn();
    }
    public static UtProjectile pillar(AttackCtx c, Vec3 base, float w, float h, int rgb, float dmg, int warn, int active) {
        return c.proj().at(base.add(0, h / 2, 0)).box(w, h, w).color(rgb).dmg(dmg).warn(warn).life(warn + active).pierce().spawn();
    }
    /** Radial burst of n bullets in the horizontal plane (optionally tilted upward by up). */
    public static void ring(AttackCtx c, Vec3 at, int n, double spd, float s, int rgb, float dmg, double phase, int warn, int life) {
        for (int i = 0; i < n; i++) {
            double a = phase + i * Math.PI * 2 / n;
            shot(c, at, new Vec3(Math.cos(a) * spd, 0, Math.sin(a) * spd), s, rgb, dmg, warn, life);
        }
    }
    /** Fan of n bolts centered on dir, total spread in radians. */
    public static void fan(AttackCtx c, Vec3 at, Vec3 dir, int n, double spread, double spd, float w, float l, int rgb, float dmg, int warn, int life) {
        for (int i = 0; i < n; i++) {
            double a = n == 1 ? 0 : -spread / 2 + spread * i / (n - 1);
            bolt(c, at, rot(dir.normalize(), a).scale(spd), w, l, rgb, dmg, warn, life);
        }
    }
    /** Drops falling onto random points within r of the target. */
    public static void rain(AttackCtx c, double r, int n, float s, int rgb, float dmg, int warn, float g) {
        for (int i = 0; i < n; i++) {
            Vec3 at = c.target.position().add((c.rnd.nextDouble() * 2 - 1) * r, 7 + c.rnd.nextDouble() * 2, (c.rnd.nextDouble() * 2 - 1) * r);
            c.proj().at(at).vel(new Vec3(0, -.15, 0)).box(s, s * 1.6f, s).color(rgb).dmg(dmg).warn(warn).gravity(g).life(60).spawn();
        }
    }
    /** Steers every live bullet of this owner with the given color toward the target (homing). */
    public static void steer(AttackCtx c, int rgb, double accel, double max) {
        for (UtProjectile p : c.level.getEntitiesOfClass(UtProjectile.class, new AABB(c.pos(), c.pos()).inflate(32),
                p -> p.ownerId == c.me.getId() && p.color() == rgb)) {
            Vec3 v = p.getDeltaMovement().add(c.tpos().subtract(p.position()).normalize().scale(accel));
            if (v.length() > max) v = v.normalize().scale(max);
            p.setDeltaMovement(v);
        }
    }
    /** Re-colors (and thereby re-tags) this owner's bullets: e.g. white -> red when they "turn hostile". */
    public static void retarget(AttackCtx c, int rgb, double spd) {
        for (UtProjectile p : c.level.getEntitiesOfClass(UtProjectile.class, new AABB(c.pos(), c.pos()).inflate(32),
                p -> p.ownerId == c.me.getId() && p.color() == rgb))
            p.setDeltaMovement(c.tpos().subtract(p.position()).normalize().scale(spd));
    }
    /** Stops this owner's bullets of a color in place (camera "freeze frame"). */
    public static void freeze(AttackCtx c, int rgb) {
        for (UtProjectile p : c.level.getEntitiesOfClass(UtProjectile.class, new AABB(c.pos(), c.pos()).inflate(32),
                p -> p.ownerId == c.me.getId() && p.color() == rgb)) { p.setDeltaMovement(Vec3.ZERO); p.grav = 0; }
    }
    /**
     * Green-SOUL style arrow chart. seq tokens: L R U D UL UR DL DR (direction the arrow comes FROM, relative to the
     * attacker->target axis; U = from above, D = from the attacker's side), "-" = rest beat, "H" = hammer from above.
     * Fires token i at tick start + i*gap; arrows travel ~6 blocks to the target.
     */
    public static void arrows(AttackCtx c, String seq, int t, int start, int gap, float spd, int rgb, float dmg) {
        if (t < start || (t - start) % gap != 0) return;
        String[] k = seq.trim().split("\\s+"); int i = (t - start) / gap;
        if (i >= k.length) return;
        String d = k[i]; if (d.equals("-")) return;
        Vec3 f = toT(c), r = side(f), tp = c.tpos();
        if (d.equals("H")) { line(c, tp.add(0, 4, 0), new Vec3(0, -1, 0).add(f.scale(.01)), 3f, 1.4f, 0xb8b8c8, dmg * 1.5f, 14, 6); return; }
        Vec3 from = Vec3.ZERO;
        if (d.contains("L")) from = from.add(r.scale(-1));
        if (d.contains("R")) from = from.add(r);
        if (d.contains("U")) from = from.add(0, 1, 0);
        if (d.contains("D")) from = from.add(f.scale(-1));
        from = from.normalize();
        Vec3 at = tp.add(from.scale(6.5));
        bolt(c, at, from.scale(-spd), .28f, 1.1f, rgb, dmg, 4, (int) (8 / spd));
    }
    public static int seqLen(String seq) { return seq.trim().split("\\s+").length; }
    public static void fx(AttackCtx c, ParticleOptions p, Vec3 at, int n, double spread) { c.particle(p, at, n, spread, .02); }

    // ---- poses (client) ----
    /** Both arms raised overhead, body lifts. */
    public static void raise(Pose p, float s) { p.r("armL", -2.7f * s, 0, .25f * s).r("armR", -2.7f * s, 0, -.25f * s).o("body", 0, .6f * s, 0); }
    /** Right arm winds back over [0,w] then whips forward until w+4, then recovers by rec. */
    public static void throwR(Pose p, float t, float w, float rec) {
        float wind = ramp(t, 0, w), hit = ramp(t, w, w + 4), back = ramp(t, rec - 8, rec);
        float a = (-2.4f * wind + 3.4f * hit) * (1 - back);
        p.r("armR", a, 0, -.2f * wind).r("body", 0, (-.35f * wind + .6f * hit) * (1 - back), 0);
    }
    /** Overhead slash with both arms: lift during [0,w], chop at w. */
    public static void chop(Pose p, float t, float w, float rec) {
        float up = ramp(t, 0, w), down = ramp(t, w, w + 3), back = ramp(t, rec - 8, rec);
        float a = (-2.9f * up + 2.2f * down) * (1 - back);
        p.r("armL", a, 0, .15f).r("armR", a, 0, -.15f).r("body", .35f * down * (1 - back), 0, 0);
    }
    /** Repeating point/cast with one arm every period ticks. */
    public static void pointLoop(Pose p, float t, String arm, float period) {
        float k = (t % period) / period, s = k < .3f ? k / .3f : 1 - (k - .3f) / .7f;
        p.r(arm, -1.6f * s - .2f, 0, 0).r("head", .1f * s, 0, 0);
    }
    public static void hover(Pose p, float t, float amp) { p.o("body", 0, sin(t, .2f) * amp, 0); }
    public static void sway(Pose p, float t, float amp) { p.r("body", 0, 0, sin(t, .15f) * amp); }
    public static void bow(Pose p, float s) { p.r("body", .6f * s, 0, 0).r("head", .3f * s, 0, 0); }
    public static void clap(Pose p, float t, float at) {
        float s = env(t, at - 6, at, at + 2, at + 10);
        p.r("armL", -1.5f * s, -.9f * s, 0).r("armR", -1.5f * s, .9f * s, 0);
    }
    public static void hop(Pose p, float t, float period, float h) {
        float k = (t % period) / period; p.o("body", 0, (float) Math.sin(k * Math.PI) * h, 0);
        p.r("legL", -.5f * (float) Math.sin(k * Math.PI), 0, 0).r("legR", -.5f * (float) Math.sin(k * Math.PI), 0, 0);
    }

    // ---- models ----
    /** Generic biped: legs legH tall, torso bodyH x bodyW, cube head headS. Front is -z. */
    public static ModelDef hum(float legH, float bodyH, float bodyW, float headS, int body, int head, int arms, int legs) {
        float by = legH + bodyH / 2, legW = Math.max(1.4f, bodyW / 2 - .4f);
        return new ModelDef()
            .box("body", null, 0, by, 0, bodyW, bodyH, Math.max(2.5f, bodyW * .6f), body)
            .box("head", "body", 0, bodyH / 2 + headS / 2, 0, headS, headS, headS, head)
            .limb("armL", "body", -(bodyW / 2 + 1.1f), bodyH / 2 - .5f, 0, 2, bodyH * .9f, 2, arms)
            .limb("armR", "body", bodyW / 2 + 1.1f, bodyH / 2 - .5f, 0, 2, bodyH * .9f, 2, arms)
            .limb("legL", null, -bodyW / 4, legH, 0, legW, legH, 2.4f, legs)
            .limb("legR", null, bodyW / 4, legH, 0, legW, legH, 2.4f, legs);
    }
    /** Two glowing eyes on the front face of a head of size headS. */
    public static ModelDef eyes(ModelDef m, float headS, float y, float dx, float w, float h, int rgb) {
        return m.glow("eyeL", "head", -dx, y, -headS / 2 - .15f, w, h, .3f, rgb).glow("eyeR", "head", dx, y, -headS / 2 - .15f, w, h, .3f, rgb);
    }
    private DrKit() {}
}
