package com.undeltarune.attack;

import com.undeltarune.entity.UtMob;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import java.util.function.Predicate;

/** Lambda-friendly attacks + colored multi-part visuals for the AU / fangame groups. */
public final class AuKit {
    public interface Tick { void tick(AttackCtx c, int t); }
    public interface Anim { void pose(Pose p, float t, UtMob m); }

    public static final class L extends Attack {
        final Tick tk; final Anim an; Predicate<UtMob> gate = m -> true;
        L(String id, int dur, Tick tk, Anim an) { super(id, dur); this.tk = tk; this.an = an; }
        public void tick(AttackCtx c, int t) { tk.tick(c, t); }
        public void pose(Pose p, float t, UtMob m) { an.pose(p, t, m); }
        public boolean usable(UtMob m) { return gate.test(m); }
        /** Phase gate, e.g. .when(m -> m.hpFrac() < .5f) */
        public L when(Predicate<UtMob> g) { gate = g; return this; }
    }
    public static L atk(String id, int dur, Tick tk, Anim an) { return new L(id, dur, tk, an); }

    /** 0..1 wind-up over [0,in], hold, then 1..0 recovery over the last `out` ticks before `end`. */
    public static float env(float t, float in, float end, float out) {
        if (t < in) return t / in;
        if (t > end - out) return Math.max(0, (end - t) / out);
        return 1;
    }
    public static float wave(float t, float speed) { return (float) Math.sin(t * speed); }

    /** Colored blaster: skull + jaw + eye glow, thin telegraph that turns into a beam. */
    public static void blaster(AttackCtx c, Vec3 at, Vec3 dir, int charge, int fire, float dmg, float width,
                               int skull, int eye, int beam) {
        Fx.blaster(c, at, dir, charge, fire, dmg, width, skull, eye, beam);
    }
    /** Static segment a->b (strings, slashes, lasers, vines). */
    public static void seg(AttackCtx c, Vec3 a, Vec3 b, float thick, int rgb, float dmg, int warn, int life) {
        Vec3 d = b.subtract(a);
        c.proj().at(a.add(d.scale(.5))).face(d).box(thick, thick, (float) d.length()).color(rgb).dmg(dmg).warn(warn).life(life).pierce().spawn();
    }
    /** Moving cube bullet. */
    public static void shot(AttackCtx c, Vec3 at, Vec3 vel, float size, int rgb, float dmg, int warn, int life) {
        c.proj().at(at).vel(vel).box(size, size, size).color(rgb).dmg(dmg).warn(warn).life(life).spawn();
    }
    /** Bone/blade/feather: thin long box flying along vel. */
    public static void dart(AttackCtx c, Vec3 at, Vec3 vel, float w, float len, int rgb, float dmg, int warn, int life) {
        c.proj().at(at).vel(vel).box(w, w, len).color(rgb).dmg(dmg).warn(warn).life(life).spawn();
    }
    /** Upright pillar (bone, tentacle, vine) rising out of the ground at `base`. */
    public static void pillar(AttackCtx c, Vec3 base, float h, float w, int rgb, float dmg, int warn, int life) {
        c.proj().at(base.add(0, h * .5 - .2, 0)).vel(new Vec3(0, .02, 0)).box(w, h, w).color(rgb).dmg(dmg).warn(warn).life(life).pierce().spawn();
    }
    /** n bullets fanning radially (horizontal ring) from `at`. */
    public static void ring(AttackCtx c, Vec3 at, int n, double speed, double phase, float size, int rgb, float dmg, int warn, int life) {
        for (int i = 0; i < n; i++) {
            double a = phase + i * Math.PI * 2 / n;
            shot(c, at, new Vec3(Math.cos(a) * speed, 0, Math.sin(a) * speed), size, rgb, dmg, warn, life);
        }
    }
    /** Spread of n bullets toward target, `deg` total arc. */
    public static void fan(AttackCtx c, Vec3 at, int n, double deg, double speed, float size, int rgb, float dmg, int warn, int life) {
        Vec3 d = c.aim(at); double base = Math.atan2(d.z, d.x);
        for (int i = 0; i < n; i++) {
            double a = base + Math.toRadians(n == 1 ? 0 : -deg / 2 + deg * i / (n - 1));
            shot(c, at, new Vec3(Math.cos(a) * speed, d.y * speed, Math.sin(a) * speed), size, rgb, dmg, warn, life);
        }
    }
    /** Point on a horizontal circle around p. */
    public static Vec3 around(Vec3 p, double r, double a, double y) { return p.add(Math.cos(a) * r, y, Math.sin(a) * r); }
    public static void puff(AttackCtx c, Vec3 at) { c.particle(ParticleTypes.POOF, at, 10, .3, .03); }
    private AuKit() {}
}
