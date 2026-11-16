package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.function.Predicate;

/** CORE enemies, True Lab Amalgamates, and the finale bosses (Mettaton NEO, Asgore, Omega/Photoshop Flowey, Asriel, Chara). */
public final class UtCoreFinale {
    // ---------- shared helpers (also used by UtMisc) ----------
    interface Tk { void run(AttackCtx c, int t); }
    interface Ps { void run(Pose p, float t, UtMob m); }
    static Attack atk(String id, int dur, Tk tk, Ps ps) { return atk(id, dur, null, tk, ps); }
    static Attack atk(String id, int dur, Predicate<UtMob> use, Tk tk, Ps ps) {
        return new Attack(id, dur) {
            public void tick(AttackCtx c, int t) { tk.run(c, t); }
            public void pose(Pose p, float t, UtMob m) { ps.run(p, t, m); }
            public boolean usable(UtMob m) { return use == null || use.test(m); }
        };
    }
    /** Envelope: 0->1 over [0,a], hold until b, 1->0 by c. Drives wind-up / strike / recovery poses. */
    static float env(float t, float a, float b, float c) { return t < a ? t / a : t < b ? 1 : t < c ? 1 - (t - b) / (c - b) : 0; }
    static float sin(float t, float f) { return (float) Math.sin(t * f); }
    static Vec3 circ(double a, double r, double y) { return new Vec3(Math.cos(a) * r, y, Math.sin(a) * r); }
    static Vec3 flat(Vec3 v) { Vec3 f = new Vec3(v.x, 0, v.z); return f.lengthSqr() < 1e-6 ? new Vec3(1, 0, 0) : f.normalize(); }
    static UtProjectile orb(AttackCtx c, Vec3 at, Vec3 v, float s, int col, float dmg, int life, int warn) {
        return c.proj().at(at).vel(v).box(s, s, s).color(col).dmg(dmg).life(life).warn(warn).spawn();
    }
    /** Radial ring of orbs. */
    static void burst(AttackCtx c, Vec3 at, int n, double phase, double spd, float s, int col, float dmg, int life) {
        for (int i = 0; i < n; i++) orb(c, at, circ(phase + i * Math.PI * 2 / n, spd, 0), s, col, dmg, life, 0);
    }
    /** Ground marker telegraph, then an orb drops onto that spot exactly when the warning ends. */
    static void drop(AttackCtx c, Vec3 ground, double h, float s, int col, float dmg, int warn) {
        c.proj().at(ground.add(0, .05, 0)).box(s * 1.8f, .06f, s * 1.8f).color(col).dmg(0).warn(warn).life(warn).spawn();
        orb(c, ground.add(0, h, 0), new Vec3(0, -h / warn, 0), s, col, dmg, warn + 4, 0);
    }
    /** Straight beam from a point; thin pulsing telegraph for `warn` ticks, then lethal. */
    static void beam(AttackCtx c, Vec3 from, Vec3 dir, float w, double len, int col, float dmg, int warn, int fire) {
        dir = dir.normalize();
        c.proj().at(from.add(dir.scale(len / 2))).face(dir).box(w, w, (float) len).color(col).dmg(dmg).warn(warn).life(warn + fire).pierce().spawn();
    }
    /** Vertical column rising from the ground. */
    static void pillar(AttackCtx c, Vec3 ground, float w, float h, int col, float dmg, int warn, int fire) {
        c.proj().at(ground.add(0, h / 2, 0)).box(w, h, w).color(col).dmg(dmg).warn(warn).life(warn + fire).pierce().spawn();
    }
    static Vec3 ground(AttackCtx c) { return c.target.position(); }
    static Vec3 near(AttackCtx c, double r) { return ground(c).add(c.rnd.nextGaussian() * r, 0, c.rnd.nextGaussian() * r); }
    /** Per-mob scratch memory (positions remembered between ticks of one attack). */
    static void mark(AttackCtx c, String k, Vec3 v) { CompoundTag d = c.me.getPersistentData(); d.putDouble(k + "x", v.x); d.putDouble(k + "y", v.y); d.putDouble(k + "z", v.z); }
    static Vec3 recall(AttackCtx c, String k) { CompoundTag d = c.me.getPersistentData(); return new Vec3(d.getDouble(k + "x"), d.getDouble(k + "y"), d.getDouble(k + "z")); }
    static void pull(AttackCtx c, Vec3 to, double s) { Vec3 d = to.subtract(c.target.position()).normalize().scale(s); c.target.push(d.x, d.y * .2, d.z); c.target.hurtMarked = true; }
    static Predicate<UtMob> below(float f) { return m -> m.hpFrac() < f; }
    static Predicate<UtMob> above(float f) { return m -> m.hpFrac() >= f; }

    static final int FIRE = 0xff7a1a, WHITE = 0xffffff, YELLOW = 0xfff04a, RED = 0xff2030, PINK = 0xff5ec8;

    public static void register(List<CharacterDef> out) {
        core(out); amalgamates(out); neo(out); asgore(out); flowey(out); asriel(out); chara(out);
    }

    // ============================== CORE ==============================
    static void core(List<CharacterDef> out) {
        // Madjick: floating wizard, corner-orb chasers + spinning crosses.
        out.add(CharacterDef.of("madjick", "Madjick", Faction.UNDERGROUND).hp(190).keep(6).speed(.22f).egg(0x2b2a6e, 0xf5c542).size(.7f, 1.9f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 8, 10, 6, 0x3b2f8f)
                .box("robeHem", "body", 0, -6, 0, 10, 3, 8, 0x2a2170)
                .box("head", "body", 0, 7, 0, 7, 6, 6, 0x1a1a2a)
                .glow("eyeL", "head", -1.6f, .5f, -3.1f, 1.4f, 2.2f, .3f, 0xffffff).glow("eyeR", "head", 1.6f, .5f, -3.1f, 1.4f, 2.2f, .3f, 0xffffff)
                .box("hat", "head", 0, 5, 0, 8, 3, 8, 0x3b2f8f).box("hatTip", "hat", 1, 3, 1, 4, 4, 4, 0x3b2f8f)
                .glow("hatStar", "hatTip", 0, 2.5f, 0, 1.4f, 1.4f, 1.4f, 0xf5c542)
                .limb("armL", "body", -5, 4, 0, 2, 6, 2, 0x3b2f8f).limb("armR", "body", 5, 4, 0, 2, 6, 2, 0x3b2f8f)
                .glow("orbL", "body", -9, 2, -2, 2.5f, 2.5f, 2.5f, 0xb06cff).glow("orbR", "body", 9, 2, -2, 2.5f, 2.5f, 2.5f, 0xb06cff))
            .attacks(
                atk("chaser_orbs", 80, (c, t) -> {
                    // the two corner orbs detach and repeatedly lunge at the SOUL
                    if (t % 12 == 4 && t < 64) for (int s = -1; s <= 1; s += 2) {
                        Vec3 at = c.eye().add(c.right().scale(s * .6)).add(0, -.3, 0);
                        orb(c, at, c.aim(at).scale(.55), .5f, 0xb06cff, 4, 26, 4);
                        c.particle(ParticleTypes.WITCH, at, 6, .2, .02);
                    }
                    if (t % 12 == 4) c.sound(SoundEvents.EVOKER_CAST_SPELL, .6f, 1.4f);
                }, (p, t, m) -> { float s = env(t, 6, 66, 80); p.r("armL", -1.2f * s, 0, .4f * s).r("armR", -1.2f * s, 0, -.4f * s)
                    .o("orbL", 3 * s * sin(t, .5f), 2 * s, -4 * s).o("orbR", -3 * s * sin(t, .5f), 2 * s, -4 * s); }),
                atk("magic_crosses", 70, (c, t) -> {
                    // crosses appear in a ring, pause (warn), then fire inward
                    if (t == 6 || t == 30) for (int i = 0; i < 6; i++) {
                        Vec3 at = c.tpos().add(circ(i * Math.PI / 3 + t, 5, .2));
                        Vec3 v = c.tpos().subtract(at).normalize().scale(.35);
                        c.proj().at(at).vel(v).box(.15f, .9f, .15f).color(0xf5c542).dmg(4).warn(14).life(40).spawn();
                        c.proj().at(at).vel(v).box(.9f, .15f, .15f).color(0xf5c542).dmg(4).warn(14).life(40).spawn();
                    }
                    if (t == 6 || t == 30) c.sound(SoundEvents.ILLUSIONER_CAST_SPELL, .8f, 1.2f);
                }, (p, t, m) -> { float s = env(t, 6, 50, 70); p.r("armR", -2.6f * s, 0, -.2f).r("armL", -.6f * s, 0, .3f).r("head", -.2f * s, 0, 0); }),
                atk("orb_dance", 76, (c, t) -> {
                    // the orbs orbit the target, shedding sparks inward
                    if (t >= 10 && t < 66 && t % 3 == 0) {
                        double a = t * .25;
                        for (int s = 0; s < 2; s++) {
                            Vec3 at = c.tpos().add(circ(a + s * Math.PI, 4.5, 0));
                            orb(c, at, c.aim(at).scale(.28), .3f, 0xd9a6ff, 3, 22, 3);
                        }
                    }
                }, (p, t, m) -> { float s = env(t, 8, 66, 76); p.r("body", 0, t * .2f * s, 0).o("body", 0, 2 * s, 0); }))
            .build());

        // Knight Knight: morningstar swings + sun/moon rays of light.
        out.add(CharacterDef.of("knight_knight", "Knight Knight", Faction.UNDERGROUND).hp(230).keep(5).speed(.18f).egg(0x2a2a3a, 0xd8d8f0).size(1.2f, 3f).scale(1.4f)
            .model(new ModelDef()
                .box("body", null, 0, 14, 0, 12, 12, 8, 0x30304a)
                .box("plate", "body", 0, 1, -4.2f, 10, 8, .6f, 0x4a4a6a)
                .box("head", "body", 0, 9, 0, 9, 8, 8, 0x30304a)
                .box("visor", "head", 0, 0, -4.1f, 7, 1.2f, .4f, 0x0c0c12)
                .glow("eyes", "head", 0, 0, -4.3f, 4, .6f, .2f, 0xffffff)
                .box("crest", "head", 0, 5, 0, 1.5f, 3, 6, 0xd8d8f0)
                .limb("armL", "body", -7.5f, 5, 0, 3.5f, 11, 3.5f, 0x30304a)
                .limb("armR", "body", 7.5f, 5, 0, 3.5f, 11, 3.5f, 0x30304a)
                .box("haft", "armR", 0, -12, -1, 1, 7, 1, 0x7a5a3a)
                .box("mace", "haft", 0, -4.5f, 0, 4, 4, 4, 0x9a9ab0)
                .limb("legL", null, -3, 8, 0, 4.5f, 8, 4.5f, 0x22223a).limb("legR", null, 3, 8, 0, 4.5f, 8, 4.5f, 0x22223a))
            .attacks(
                atk("morningstar", 64, (c, t) -> {
                    // huge mace sweeps in an arc around the knight
                    if (t >= 16 && t < 40) {
                        double a = Math.atan2(c.fwd().z, c.fwd().x) - 1.6 + (t - 16) * .14;
                        for (double r = 1.5; r <= 5; r += 1.2) orb(c, c.pos().add(circ(a, r, 1)), Vec3.ZERO, .9f, 0x9a9ab0, 6, 3, 0);
                        if (t % 4 == 0) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .6f);
                    }
                    if (t == 6) for (int k = 0; k < 9; k++) c.proj().at(c.pos().add(circ(Math.atan2(c.fwd().z, c.fwd().x) - 1.6 + k * .4, 4, .05)))
                        .box(.8f, .05f, .8f).color(0x9a9ab0).dmg(0).warn(10).life(10).spawn();
                }, (p, t, m) -> { float w = env(t, 14, 16, 16), s = t < 16 ? 0 : env(t - 16, 1, 24, 40);
                    p.r("armR", -2.8f * w + (t >= 16 ? -2.8f + (t - 16) * .15f : 0) * s, 0, -.3f).r("body", 0, -.5f * w + .9f * s, 0); }),
                atk("rays_of_light", 84, (c, t) -> {
                    // sun and moon cross the sky; beams of light fall where they point
                    if (t >= 10 && t < 70 && t % 5 == 0) {
                        Vec3 g = near(c, 2.5);
                        pillar(c, g, .7f, 9, YELLOW, 5, 12, 6);
                        c.particle(ParticleTypes.END_ROD, g.add(0, 9, 0), 4, .3, .02);
                    }
                    if (t == 10) c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.6f);
                }, (p, t, m) -> { float s = env(t, 10, 70, 84); p.r("armL", -3f * s, 0, .2f).r("armR", -3f * s, 0, -.2f).r("head", -.4f * s, 0, 0); }),
                atk("lullaby", 60, (c, t) -> {
                    // the knight's lullaby: drowsy notes drift outward in a slow spiral
                    if (t % 4 == 0 && t < 48) { burst(c, c.eye(), 4, t * .3, .22, .35f, 0xc8c8ff, 3, 40); c.particle(ParticleTypes.NOTE, c.eye().add(0, .6, 0), 2, .3, 0); }
                    if (t == 48) c.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                }, (p, t, m) -> p.r("head", .3f * env(t, 10, 50, 60), 0, .2f * sin(t, .2f)).r("armL", -.5f, 0, .4f * sin(t, .2f))))
            .build());

        // Final Froggit: flies that swarm/home + leaping frogs. Wise and pale.
        out.add(CharacterDef.of("final_froggit", "Final Froggit", Faction.UNDERGROUND).hp(150).keep(5).speed(.28f).egg(0xe6e6e6, 0x3a3a3a).size(.9f, 1.1f)
            .model(new ModelDef()
                .box("body", null, 0, 5, 0, 10, 7, 8, 0xe8e8e8)
                .box("head", "body", 0, 5, -1, 11, 5, 8, 0xe8e8e8)
                .box("eyeL", "head", -3.5f, 3, -2, 3, 3, 3, 0xffffff).box("eyeR", "head", 3.5f, 3, -2, 3, 3, 3, 0xffffff)
                .box("pupilL", "eyeL", 0, 0, -1.6f, 1.2f, 1.6f, .3f, 0x111111).box("pupilR", "eyeR", 0, 0, -1.6f, 1.2f, 1.6f, .3f, 0x111111)
                .box("brow", "head", 0, 4.8f, -2, 10, 1, 1, 0x9a9a9a).box("mouth", "head", 0, -1.2f, -4.1f, 6, .6f, .3f, 0x4a4a4a)
                .box("mark", "body", 0, 0, -4.1f, 3, 4, .3f, 0x6f7f9f)
                .limb("legL", null, -3.5f, 2, 0, 3, 2, 5, 0xd0d0d0).limb("legR", null, 3.5f, 2, 0, 3, 2, 5, 0xd0d0d0))
            .attacks(
                atk("fly_swarm", 76, (c, t) -> {
                    if (t % 5 == 0 && t < 60) for (int i = 0; i < 2; i++) {
                        Vec3 at = c.tpos().add(circ(c.rnd.nextDouble() * 6.28, 4, c.rnd.nextGaussian() * .5));
                        orb(c, at, c.aim(at).scale(.25).add(c.rnd.nextGaussian() * .05, 0, c.rnd.nextGaussian() * .05), .22f, 0x222222, 3, 30, 6);
                    }
                    if (t % 10 == 0) c.sound(SoundEvents.BEE_LOOP, .4f, 1.6f);
                }, (p, t, m) -> p.o("head", 0, .4f * sin(t, .8f), 0).r("head", 0, .3f * sin(t, .15f), 0)),
                atk("frog_leap", 70, (c, t) -> {
                    // little frogs arc over in staggered hops
                    if (t % 9 == 0 && t < 54) {
                        Vec3 at = c.pos().add(c.right().scale(c.rnd.nextGaussian() * 2)).add(0, .3, 0);
                        Vec3 d = c.tpos().subtract(at); c.proj().at(at).vel(new Vec3(d.x / 20, .45, d.z / 20)).gravity(.045f)
                            .box(.5f, .35f, .5f).color(0xf0f0f0).dmg(4).life(24).spawn();
                        c.sound(SoundEvents.FROG_LONG_JUMP, .8f, 1.2f);
                    }
                }, (p, t, m) -> { float s = env(t % 9, 3, 4, 9); p.o("body", 0, 3 * s, 0).r("legL", .8f * s, 0, 0).r("legR", .8f * s, 0, 0); }),
                atk("frog_wisdom", 60, (c, t) -> {
                    // calm ring of flies that holds, then folds inward
                    if (t == 6) for (int i = 0; i < 10; i++) { Vec3 at = c.tpos().add(circ(i * .628, 3.5, 0)); orb(c, at, c.aim(at).scale(.18), .25f, 0x333333, 4, 40, 16); }
                }, (p, t, m) -> p.r("head", -.3f * env(t, 6, 40, 60), 0, 0)))
            .build());

        // Astigmatism: one huge eye; rolling eye bullets and "don't pick on me" closing rings.
        out.add(CharacterDef.of("astigmatism", "Astigmatism", Faction.UNDERGROUND).hp(170).keep(6).speed(.2f).egg(0xd0d0d0, 0x7a2bd4).size(1.0f, 1.6f)
            .model(new ModelDef()
                .box("body", null, 0, 13, 0, 13, 13, 10, 0xd6d6d6)
                .box("head", "body", 0, 0, -5.2f, 9, 9, .6f, 0xffffff)
                .glow("iris", "head", 0, 0, -.4f, 5, 5, .3f, 0x7a2bd4).box("pupil", "iris", 0, 0, -.2f, 2, 2, .2f, 0x000000)
                .box("hornL", "body", -5, 8, 0, 2, 4, 2, 0x9a9a9a).box("hornR", "body", 5, 8, 0, 2, 4, 2, 0x9a9a9a)
                .box("mouth", "body", 0, -5, -5.1f, 8, 1.5f, .3f, 0x2a0a2a)
                .limb("legL", null, -4, 7, 0, 3, 7, 3, 0xbdbdbd).limb("legR", null, 4, 7, 0, 3, 7, 3, 0xbdbdbd))
            .attacks(
                atk("eye_barrage", 70, (c, t) -> {
                    if (t % 6 == 0 && t < 54) { Vec3 at = c.eye(); for (int k = -1; k <= 1; k++) orb(c, at, c.aim(at).add(c.right().scale(k * .25)).normalize().scale(.4), .45f, 0x7a2bd4, 4, 36, 2); c.sound(SoundEvents.SHULKER_SHOOT, .6f, 1.5f); }
                }, (p, t, m) -> p.o("iris", 1.2f * sin(t, .9f), .8f * sin(t, 1.3f), 0)),
                atk("closing_ring", 70, (c, t) -> {
                    // a circle forms around the target and shrinks; the colour says whether to stay still (blue) or move (orange)
                    if (t == 4) mark(c, "ring", ground(c).add(0, .5, 0));
                    if (t >= 4 && t < 50 && t % 4 == 0) {
                        Vec3 ctr = recall(c, "ring"); double r = 5 - (t - 4) * .09; boolean blue = c.me.getId() % 2 == 0;
                        for (int i = 0; i < 12; i++) { var b = c.proj().at(ctr.add(circ(i * .5236 + t * .05, r, 0))).box(.4f, .8f, .4f).dmg(4).life(5); (blue ? b.blue() : b.orange()).spawn(); }
                    }
                }, (p, t, m) -> p.r("body", 0, 0, .15f * sin(t, .3f)).o("pupil", 0, 0, -.2f * env(t, 4, 50, 70))),
                atk("eye_roll", 64, (c, t) -> {
                    // big rolling eyeballs bounce across the floor
                    if (t % 14 == 0 && t < 50) { Vec3 r = c.right(); int s = c.rnd.nextBoolean() ? 1 : -1; Vec3 at = c.tpos().add(r.scale(s * 7)).add(0, -.3, 0);
                        c.proj().at(at).vel(r.scale(-s * .4).add(0, .3, 0)).gravity(.04f).box(1.1f, 1.1f, 1.1f).color(0xffffff).dmg(5).warn(6).life(40).spawn(); }
                }, (p, t, m) -> p.r("body", .25f * sin(t, .45f), 0, 0)))
            .build());

        // Whimsalot: armored Whimsun knight; spear lunges and falling petals/bullets.
        out.add(CharacterDef.of("whimsalot", "Whimsalot", Faction.UNDERGROUND).hp(150).keep(5).speed(.3f).egg(0x8a8ab8, 0xb8f0ff).size(.7f, 1.8f)
            .model(new ModelDef()
                .box("body", null, 0, 12, 0, 7, 9, 5, 0x9a9ac8)
                .box("head", "body", 0, 7, 0, 7, 6, 6, 0xb0b0d8).box("visor", "head", 0, 0, -3.1f, 5, 1, .3f, 0x1a1a2a)
                .box("antL", "head", -2, 4.5f, 0, .6f, 3, .6f, 0x6a6a90).box("antR", "head", 2, 4.5f, 0, .6f, 3, .6f, 0x6a6a90)
                .ghost("wingL", "body", -6, 2, 2, 7, 9, .4f, 0xb8f0ff, .55f).ghost("wingR", "body", 6, 2, 2, 7, 9, .4f, 0xb8f0ff, .55f)
                .limb("armL", "body", -4.5f, 3, 0, 2, 6, 2, 0x9a9ac8).limb("armR", "body", 4.5f, 3, 0, 2, 6, 2, 0x9a9ac8)
                .box("spear", "armR", 0, -6, -4, .6f, .6f, 12, 0xd0d0e0))
            .attacks(
                atk("spear_lunge", 56, (c, t) -> {
                    if (t == 14) { c.dash(c.fwd().scale(1.3).add(0, .1, 0)); c.sound(SoundEvents.TRIDENT_THROW, 1, 1.1f); }
                    if (t == 18) c.aoe(c.pos().add(c.fwd().scale(1.5)), 1.8, 6, 1);
                    if (t == 6) beam(c, c.pos().add(0, .8, 0), c.fwd(), .3f, 6, 0xd0d0e0, 0, 8, 1);
                }, (p, t, m) -> { float w = env(t, 12, 14, 14), s = t >= 14 ? env(t - 14, 2, 8, 20) : 0; p.r("armR", -1.4f * w - 1.6f * s, 0, 0).o("armR", 0, 0, 2 * w - 3 * s).r("body", -.2f * w + .4f * s, 0, 0); }),
                atk("petal_fall", 70, (c, t) -> {
                    if (t % 3 == 0 && t < 56) { Vec3 g = near(c, 3); c.proj().at(g.add(0, 7, 0)).vel(new Vec3(c.rnd.nextGaussian() * .05, -.25, c.rnd.nextGaussian() * .05))
                        .box(.3f, .1f, .5f).color(0xb8f0ff).dmg(3).life(36).spawn(); }
                }, (p, t, m) -> p.r("wingL", 0, .5f * sin(t, 1.2f), 0).r("wingR", 0, -.5f * sin(t, 1.2f), 0).o("body", 0, 2 * env(t, 8, 60, 70), 0)),
                atk("pendulum_bullets", 64, (c, t) -> {
                    if (t % 4 == 0 && t < 52) { double a = Math.sin(t * .15) * 1.1; Vec3 d = c.fwd().scale(Math.cos(a)).add(c.right().scale(Math.sin(a)));
                        orb(c, c.eye(), d.scale(.45), .35f, 0xffffff, 3, 30, 0); }
                }, (p, t, m) -> p.r("armR", -1.5f, (float) Math.sin(t * .15) * 1.1f, 0)))
            .build());
    }

    // ============================== AMALGAMATES ==============================
    static void amalgamates(List<CharacterDef> out) {
        // Memoryhead: three melting heads; "join us" wiggly lines + teeth.
        out.add(CharacterDef.of("memoryhead", "Memoryhead", Faction.ROYAL_SCIENCE).hp(140).keep(6).speed(.2f).egg(0xe8e8e8, 0x111111).size(.9f, 1.6f)
            .model(new ModelDef()
                .box("body", null, 0, 7, 0, 7, 10, 5, 0xe8e8e8)
                .box("head", "body", 0, 8, 0, 7, 6, 6, 0xf0f0f0).box("mouth", "head", 0, -1.5f, -3.1f, 4, 2, .3f, 0x000000)
                .box("headL", "body", -6, 6, 0, 5, 5, 5, 0xe0e0e0).box("mouthL", "headL", 0, -1, -2.6f, 3, 1.5f, .3f, 0x000000)
                .box("headR", "body", 6, 5, 0, 5, 5, 5, 0xe0e0e0).box("mouthR", "headR", 0, -1, -2.6f, 3, 1.5f, .3f, 0x000000)
                .box("drip", "body", 0, -7, 0, 9, 4, 7, 0xd8d8d8))
            .attacks(
                atk("join_us", 74, (c, t) -> {
                    // wavy streams from each mouth
                    if (t % 3 == 0 && t < 60) for (int s = -1; s <= 1; s++) {
                        Vec3 at = c.eye().add(c.right().scale(s * .4)); Vec3 d = c.aim(at).add(c.right().scale(Math.sin(t * .4 + s) * .4)).normalize();
                        orb(c, at, d.scale(.4), .25f, 0x111111, 3, 30, 0);
                    }
                }, (p, t, m) -> p.o("headL", 0, sin(t, .7f), 0).o("headR", 0, -sin(t, .7f), 0).r("head", 0, 0, .2f * sin(t, .5f))),
                atk("static_teeth", 60, (c, t) -> {
                    if (t % 8 == 0 && t < 48) { Vec3 at = c.tpos().add(circ(c.rnd.nextDouble() * 6.28, 5, 0));
                        c.proj().at(at).vel(c.aim(at).scale(.5)).box(.3f, .5f, .3f).color(WHITE).dmg(4).warn(10).life(30).spawn(); c.sound(SoundEvents.EVOKER_FANGS_ATTACK, .5f, 1.6f); }
                }, (p, t, m) -> p.o("mouth", 0, -.6f * Math.abs(sin(t, .8f)), 0)))
            .build());

        // Endogeny: the dog amalgamate; dog-bullets, beam from its core, "pet" ring.
        out.add(CharacterDef.of("endogeny", "Endogeny", Faction.ROYAL_SCIENCE).hp(210).keep(6).speed(.3f).egg(0xffffff, 0xb0b0b0).size(1.3f, 1.4f).scale(1.3f)
            .model(new ModelDef()
                .box("body", null, 0, 9, 0, 14, 9, 16, 0xf4f4f4)
                .box("head", "body", 0, 2, -9, 9, 6, 6, 0xf4f4f4).box("hole", "head", 0, 0, -3.1f, 3, 3, .3f, 0x000000)
                .box("earL", "head", -4, 4, 0, 2, 5, 3, 0xe0e0e0).box("earR", "head", 4, 4, 0, 2, 5, 3, 0xe0e0e0)
                .box("snoutB", "body", -5, 6, 4, 4, 4, 6, 0xeeeeee).box("snoutC", "body", 5, 6, 5, 4, 4, 6, 0xeeeeee)
                .limb("legL", null, -5, 5, -5, 3, 5, 3, 0xe8e8e8).limb("legR", null, 5, 5, -5, 3, 5, 3, 0xe8e8e8)
                .limb("armL", null, -5, 5, 5, 3, 5, 3, 0xe8e8e8).limb("armR", null, 5, 5, 5, 3, 5, 3, 0xe8e8e8))
            .attacks(
                atk("dog_bullets", 70, (c, t) -> {
                    if (t % 7 == 0 && t < 56) { Vec3 at = c.eye(); for (int k = -2; k <= 2; k++) orb(c, at, c.aim(at).add(c.right().scale(k * .18)).normalize().scale(.38), .45f, WHITE, 4, 34, 0); c.sound(SoundEvents.WOLF_AMBIENT, .7f, .5f); }
                }, (p, t, m) -> p.r("head", -.3f * Math.abs(sin(t, .45f)), 0, 0)),
                atk("core_beam", 70, (c, t) -> {
                    if (t == 10) beam(c, c.eye(), c.aim(c.eye()), 1.4f, 18, WHITE, 8, 22, 10);
                    if (t == 10) c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1, 1);
                    if (t >= 32 && t < 42) c.particle(ParticleTypes.END_ROD, c.eye().add(c.fwd()), 8, .4, .1);
                }, (p, t, m) -> { float s = env(t, 30, 42, 60); p.r("head", -.5f * s, 0, 0).o("hole", 0, 0, -.5f * s).o("body", 0, -1.5f * s, 0); }),
                atk("pounce", 60, (c, t) -> {
                    if (t == 18) { c.dash(c.aim(c.pos()).scale(1.4).add(0, .5, 0)); c.sound(SoundEvents.WOLF_GROWL, 1, .6f); }
                    if (t == 30) { c.aoe(c.pos(), 2.5, 6, 1.2); burst(c, c.pos().add(0, .4, 0), 10, 0, .3, .35f, WHITE, 3, 24); }
                }, (p, t, m) -> { float w = env(t, 16, 18, 18), s = t >= 18 ? env(t - 18, 2, 12, 30) : 0; p.o("body", 0, -2 * w, 0).r("body", .3f * w - .3f * s, 0, 0); }))
            .build());

        // Lemon Bread: dripping "teeth" blob; wall of teeth + bite.
        out.add(CharacterDef.of("lemon_bread", "Lemon Bread", Faction.ROYAL_SCIENCE).hp(160).keep(5).speed(.15f).egg(0xf0e070, 0x2a2a2a).size(1.1f, 1.4f)
            .model(new ModelDef()
                .box("body", null, 0, 7, 0, 14, 12, 10, 0xeeeedd)
                .box("head", "body", 0, 7, -1, 9, 5, 8, 0xeeeedd)
                .box("eye", "head", 0, 1, -4.1f, 3, 3, .3f, 0x111111)
                .box("jaw", "body", 0, -2, -5.1f, 12, 4, .4f, 0x2a1a1a)
                .box("teethU", "jaw", 0, 1.5f, -.3f, 11, 1, .3f, 0xffffff).box("teethD", "jaw", 0, -1.5f, -.3f, 11, 1, .3f, 0xffffff)
                .box("blob", "body", 0, -6, 0, 16, 3, 12, 0xe4e4cc))
            .attacks(
                atk("teeth_wall", 70, (c, t) -> {
                    if (t % 18 == 0 && t < 54) { Vec3 r = c.right(), f = c.fwd(); int gap = c.rnd.nextInt(5) - 2;
                        for (int k = -4; k <= 4; k++) if (Math.abs(k - gap) > 0) c.proj().at(c.tpos().add(f.scale(-6)).add(r.scale(k * .7)).add(0, -.4, 0)).vel(f.scale(.45))
                            .box(.4f, .9f, .25f).color(WHITE).dmg(4).warn(8).life(32).spawn(); }
                }, (p, t, m) -> p.o("jaw", 0, -1.2f * Math.abs(sin(t, .35f)), 0)),
                atk("worm_bite", 56, (c, t) -> {
                    if (t == 8) mark(c, "bite", ground(c));
                    if (t == 8) { Vec3 g = recall(c, "bite"); for (int i = 0; i < 8; i++) c.proj().at(g.add(circ(i * .785, 1.5, .1))).box(.3f, .2f, .3f).color(WHITE).dmg(0).warn(16).life(16).spawn(); }
                    if (t == 24) { Vec3 g = recall(c, "bite"); for (int i = 0; i < 8; i++) { Vec3 at = g.add(circ(i * .785, 1.5, .4)); c.proj().at(at).vel(g.add(0, .6, 0).subtract(at).scale(.18)).box(.35f, .9f, .35f).color(WHITE).dmg(5).life(10).spawn(); }
                        c.sound(SoundEvents.EVOKER_FANGS_ATTACK, 1, .8f); }
                }, (p, t, m) -> p.o("jaw", 0, t < 24 ? -2 * env(t, 20, 24, 24) : 0, 0)))
            .build());

        // Reaper Bird: bird skull with many wings; feather fans + circling flies.
        out.add(CharacterDef.of("reaper_bird", "Reaper Bird", Faction.ROYAL_SCIENCE).hp(170).keep(6).speed(.22f).egg(0xf5f5f5, 0x101010).size(1.2f, 1.8f)
            .model(new ModelDef()
                .box("body", null, 0, 11, 0, 8, 12, 6, 0xf0f0f0)
                .box("head", "body", 0, 8, 0, 6, 5, 6, 0xf5f5f5).box("beak", "head", 0, -1, -4, 2, 2, 3, 0xd8d8d8)
                .glow("eyeL", "head", -1.5f, .8f, -3.1f, 1, 1, .2f, 0x000000).glow("eyeR", "head", 1.5f, .8f, -3.1f, 1, 1, .2f, 0x000000)
                .box("wingL1", "body", -7, 3, 1, 8, 3, 1, 0xffffff).box("wingR1", "body", 7, 3, 1, 8, 3, 1, 0xffffff)
                .box("wingL2", "body", -7, -1, 1, 7, 3, 1, 0xeeeeee).box("wingR2", "body", 7, -1, 1, 7, 3, 1, 0xeeeeee)
                .box("wingL3", "body", -6, -5, 1, 6, 3, 1, 0xdddddd).box("wingR3", "body", 6, -5, 1, 6, 3, 1, 0xdddddd)
                .box("tail", "body", 0, -8, 1, 4, 5, 2, 0xeeeeee))
            .attacks(
                atk("feather_fan", 60, (c, t) -> {
                    if (t == 12 || t == 26 || t == 40) { Vec3 at = c.eye(); for (int k = -4; k <= 4; k++) c.proj().at(at).vel(c.aim(at).add(c.right().scale(k * .15)).normalize().scale(.5))
                        .box(.12f, .3f, .7f).color(WHITE).dmg(4).life(30).spawn(); c.sound(SoundEvents.PHANTOM_FLAP, 1, 1.3f); }
                }, (p, t, m) -> { float s = env(t % 14, 6, 10, 14); p.r("wingL1", 0, 0, -.8f * s).r("wingR1", 0, 0, .8f * s).r("wingL2", 0, 0, -.6f * s).r("wingR2", 0, 0, .6f * s); }),
                atk("death_flies", 70, (c, t) -> {
                    if (t >= 6 && t < 60 && t % 2 == 0) { double a = t * .3; Vec3 at = c.tpos().add(circ(a, 3.5 - t * .03, Math.sin(t * .2) * .5)); orb(c, at, circ(a + 1.57, .12, 0), .22f, 0x111111, 3, 14, 2); }
                }, (p, t, m) -> p.r("wingL3", 0, .4f * sin(t, 1.4f), 0).r("wingR3", 0, -.4f * sin(t, 1.4f), 0).o("body", 0, 2 * sin(t, .2f), 0)))
            .build());

        // Snowdrake's Mother: ice amalgamate; snowflake rings and "chilly" bad-pun icicles.
        out.add(CharacterDef.of("snowdrakes_mother", "Snowdrake's Mother", Faction.ROYAL_SCIENCE).hp(180).keep(6).speed(.18f).egg(0xd8f4ff, 0x203040).size(1.0f, 2.2f)
            .model(new ModelDef()
                .box("body", null, 0, 14, 0, 9, 16, 7, 0xe0f4ff)
                .box("head", "body", 0, 10, 0, 7, 6, 6, 0xeaf8ff).box("beak", "head", 0, -1, -4, 3, 2, 3, 0x9ad4ff)
                .glow("eyeL", "head", -1.8f, .8f, -3.1f, 1.2f, 1.2f, .2f, 0x203040).glow("eyeR", "head", 1.8f, .8f, -3.1f, 1.2f, 1.2f, .2f, 0x203040)
                .box("crestL", "head", -2.5f, 4, 0, 1.5f, 3, 4, 0x9ad4ff).box("crestR", "head", 2.5f, 4, 0, 1.5f, 3, 4, 0x9ad4ff)
                .box("melt", "body", 0, -10, 0, 11, 4, 9, 0xcfeaff)
                .limb("armL", "body", -5.5f, 5, 0, 2, 9, 2, 0xd0ecff).limb("armR", "body", 5.5f, 5, 0, 2, 9, 2, 0xd0ecff))
            .attacks(
                atk("snowflake_ring", 70, (c, t) -> {
                    if (t % 10 == 0 && t < 60) { burst(c, c.eye(), 8, t * .2, .3, .3f, 0xd8f4ff, 3, 34); c.particle(ParticleTypes.SNOWFLAKE, c.eye(), 10, .5, .05); }
                }, (p, t, m) -> p.r("armL", -1.2f, 0, .5f * sin(t, .3f)).r("armR", -1.2f, 0, -.5f * sin(t, .3f))),
                atk("icicle_puns", 60, (c, t) -> {
                    if (t % 6 == 0 && t < 48) drop(c, near(c, 2), 6, .45f, 0x9ad4ff, 4, 14);
                    if (t == 0) c.sound(SoundEvents.GLASS_BREAK, .6f, 1.4f);
                }, (p, t, m) -> p.r("head", -.3f * env(t, 6, 48, 60), 0, .15f * sin(t, .9f))))
            .build());
    }

    // ============================== METTATON NEO ==============================
    static void neo(List<CharacterDef> out) {
        int metal = 0x2a2a34, pink = 0xff4fb0;
        out.add(CharacterDef.of("mettaton_neo", "Mettaton NEO", Faction.ROYAL_SCIENCE).hp(90).boss().keep(7).rest(16).speed(.3f).egg(0x2a2a34, 0xff4fb0).size(.8f, 2.4f).scale(1.2f)
            .model(new ModelDef()
                .box("body", null, 0, 19, 0, 8, 10, 5, metal)
                .glow("heart", "body", 0, 1, -2.6f, 3.5f, 3, .4f, pink)
                .box("head", "body", 0, 8, 0, 6, 6, 6, 0xeeddcc).box("hair", "head", -1, 2.5f, -1, 7, 2.5f, 7, 0x111118)
                .box("bang", "hair", -1.5f, -2.5f, -3, 3, 4, 1, 0x111118).glow("eye", "head", 1.4f, .3f, -3.1f, 1.2f, .7f, .2f, pink)
                .limb("armL", "body", -5, 4, 0, 2.5f, 9, 2.5f, metal).limb("armR", "body", 5, 4, 0, 2.5f, 9, 2.5f, metal)
                .box("cannon", "armR", 0, -10, 0, 3.5f, 4, 3.5f, 0x55556a).glow("muzzle", "cannon", 0, -2.2f, 0, 2, .4f, 2, pink)
                .box("wingL", "body", -8, 5, 3, 10, 2.5f, 1, 0x3a3a48).box("wingR", "body", 8, 5, 3, 10, 2.5f, 1, 0x3a3a48)
                .glow("wingLg", "wingL", 0, -1.4f, 0, 9, .4f, .8f, pink).glow("wingRg", "wingR", 0, -1.4f, 0, 9, .4f, .8f, pink)
                .limb("legL", null, -2, 14, 0, 2.5f, 14, 2.5f, metal).limb("legR", null, 2, 14, 0, 2.5f, 14, 2.5f, metal))
            .attacks(
                atk("neo_cannon", 70, (c, t) -> {
                    if (t == 12) { Vec3 at = c.eye().add(c.right().scale(.5)); beam(c, at, c.aim(at), 1.6f, 24, pink, 12, 24, 12); c.sound(SoundEvents.BEACON_POWER_SELECT, 1.5f, .7f); }
                    if (t >= 36 && t < 48) c.particle(ParticleTypes.FLASH, c.eye().add(c.fwd().scale(2)), 1, 0, 0);
                }, (p, t, m) -> { float s = env(t, 10, 48, 66); p.r("armR", -1.57f * s, 0, -.1f).r("body", 0, -.3f * s, 0).o("body", 0, 0, 1.2f * (t >= 36 && t < 48 ? 1 : 0)); }),
                atk("wing_lasers", 76, (c, t) -> {
                    if (t % 6 == 0 && t >= 8 && t < 60) for (int s = -1; s <= 1; s += 2) { Vec3 at = c.eye().add(c.right().scale(s * 1.2)).add(0, .6, 0); beam(c, at, near(c, 1.5).add(0, .5, 0).subtract(at), .35f, 16, pink, 5, 8, 4); }
                    if (t % 12 == 8) c.sound(SoundEvents.GUARDIAN_ATTACK, .8f, 1.8f);
                }, (p, t, m) -> { float s = env(t, 8, 60, 76); p.r("wingL", 0, 0, -.5f * s).r("wingR", 0, 0, .5f * s).o("body", 0, 3 * s, 0); }),
                atk("heart_core", 64, (c, t) -> {
                    if (t == 14 || t == 26 || t == 38) { burst(c, c.eye().add(0, -.4, 0), 14, t, .4, .35f, pink, 4, 30); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.5f, .6f); }
                }, (p, t, m) -> p.o("heart", 0, 0, -.6f * Math.abs(sin(t, .25f))).r("armL", -.5f, 0, .8f).r("armR", -.5f, 0, -.8f)),
                atk("neo_missiles", 70, below(.5f), (c, t) -> {
                    if (t % 5 == 0 && t < 50) { Vec3 at = c.eye().add(0, 1, 0); Vec3 g = near(c, 2); c.proj().at(at).vel(new Vec3((g.x - at.x) / 18, .35, (g.z - at.z) / 18)).gravity(.04f).box(.3f, .3f, .7f).color(0xffd0f0).dmg(5).life(22).spawn(); }
                    if (t % 5 == 4 && t > 18) c.particle(ParticleTypes.EXPLOSION, near(c, 2), 1, 0, 0);
                }, (p, t, m) -> p.r("wingL", .4f * sin(t, .8f), 0, -.3f).r("wingR", .4f * sin(t, .8f), 0, .3f)),
                atk("leg_kick", 46, (c, t) -> {
                    if (t == 10) c.dash(c.aim(c.pos()).scale(1.2).add(0, .2, 0));
                    if (t == 16) { c.aoe(c.pos().add(c.fwd()), 2.2, 8, 1.6); c.sound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1, .8f); }
                }, (p, t, m) -> { float s = env(t, 10, 18, 30); p.r("legR", -1.8f * s, 0, 0).r("body", .3f * s, 0, 0); }))
            .build());
    }

    // ============================== ASGORE ==============================
    static void asgore(List<CharacterDef> out) {
        int purple = 0x5b3c8f, gold = 0xe0b040, fur = 0xf2f0e6;
        out.add(CharacterDef.of("asgore", "ASGORE", Faction.UNDERGROUND).hp(320).boss().keep(6).rest(18).speed(.22f).egg(0x5b3c8f, 0xe0b040).size(1.4f, 3.4f).scale(1.6f)
            .model(new ModelDef()
                .box("body", null, 0, 16, 0, 14, 14, 9, 0x6a6a7a)
                .box("cape", "body", 0, -1, 4.8f, 16, 22, 1, purple).box("pauldL", "body", -7.5f, 6, 0, 5, 3, 9, 0x8a8a9a).box("pauldR", "body", 7.5f, 6, 0, 5, 3, 9, 0x8a8a9a)
                .box("head", "body", 0, 11, -1, 9, 8, 8, fur).box("beard", "head", 0, -4.5f, -2, 9, 4, 5, gold).box("mane", "head", 0, 1, 2, 11, 7, 4, gold)
                .box("hornL", "head", -5, 4, 0, 2, 4, 2, 0xf8f0d0).box("hornR", "head", 5, 4, 0, 2, 4, 2, 0xf8f0d0)
                .box("crown", "head", 0, 4.5f, 0, 6, 1.5f, 6, gold).box("snout", "head", 0, -1, -4.5f, 4, 2.5f, 2, fur)
                .limb("armL", "body", -8, 5, 0, 4, 12, 4, 0x6a6a7a).limb("armR", "body", 8, 5, 0, 4, 12, 4, 0x6a6a7a)
                .box("trident", "armR", 0, -12, -2, 1, 1, 32, RED).box("tines", "trident", 0, 0, -16, 5, 1, 1, RED)
                .box("tineL", "tines", -2, 0, -1.5f, 1, 1, 3, RED).box("tineR", "tines", 2, 0, -1.5f, 1, 1, 3, RED).box("tineM", "tines", 0, 0, -2, 1, 1, 4, RED)
                .glow("flameL", "armL", 0, -13, 0, 3, 3, 3, FIRE)
                .limb("legL", null, -3.5f, 9, 0, 5, 9, 5, 0x55556a).limb("legR", null, 3.5f, 9, 0, 5, 9, 5, 0x55556a))
            .attacks(
                atk("mercy_break", 60, above(.95f), (c, t) -> {
                    // ASGORE destroys the MERCY button: one swing, the shards scatter, and mercy is gone
                    if (t == 6) c.proj().at(c.pos().add(c.fwd().scale(2.5)).add(0, 1, 0)).box(1.8f, .7f, .15f).color(0xff9a1f).dmg(0).life(24).spawn();
                    if (t == 24) { Vec3 at = c.pos().add(c.fwd().scale(2.5)).add(0, 1, 0); c.aoe(at, 2.5, 6, 1.4);
                        for (int i = 0; i < 10; i++) orb(c, at, new Vec3(c.rnd.nextGaussian() * .2, .2 + c.rnd.nextDouble() * .2, c.rnd.nextGaussian() * .2), .2f, 0xff9a1f, 0, 20, 0).grav = .03f;
                        c.target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 0)); c.sound(SoundEvents.GLASS_BREAK, 1.5f, .6f); c.sound(SoundEvents.ANVIL_LAND, 1, .5f); }
                }, (p, t, m) -> { float w = env(t, 20, 22, 22), s = t >= 22 ? env(t - 22, 2, 12, 38) : 0; p.r("armR", -3f * w + -.6f * s, 0, -.2f).r("body", -.2f * w + .3f * s, 0, 0); }),
                atk("fire_hands", 80, (c, t) -> {
                    // rows of flames slide out of each palm in a curving wave
                    if (t % 4 == 0 && t >= 10 && t < 60) for (int s = -1; s <= 1; s += 2) {
                        Vec3 at = c.eye().add(c.right().scale(s * 1.2)); Vec3 d = c.aim(at).add(c.right().scale(-s * Math.sin(t * .2) * .4)).normalize();
                        orb(c, at, d.scale(.4), .45f, FIRE, 5, 34, 0);
                    }
                    if (t % 8 == 2) c.sound(SoundEvents.BLAZE_SHOOT, .7f, .8f);
                }, (p, t, m) -> { float s = env(t, 10, 60, 74); p.r("armL", -1.6f * s, 0, .3f).r("armR", -1.6f * s, 0, -.3f); }),
                atk("fire_circle", 76, (c, t) -> {
                    // a circle of fire forms around the target and tightens; one gap to escape
                    if (t == 6) { mark(c, "fc", ground(c)); c.me.getPersistentData().putInt("fcGap", c.rnd.nextInt(16)); }
                    if (t >= 6 && t < 60 && t % 3 == 0) { Vec3 g = recall(c, "fc"); int gap = c.me.getPersistentData().getInt("fcGap"); double r = 6 - (t - 6) * .09;
                        for (int i = 0; i < 16; i++) if (Math.abs(i - gap) > 1) orb(c, g.add(circ(i * .3927 + t * .02, r, .6)), Vec3.ZERO, .5f, FIRE, 5, 4, t == 6 ? 4 : 0); }
                    if (t == 6) c.sound(SoundEvents.FIRECHARGE_USE, 1, .7f);
                }, (p, t, m) -> { float s = env(t, 6, 60, 76); p.r("armL", -2.4f * s, 0, .6f).r("armR", 0, 0, 0).o("flameL", 0, 0, 0).r("head", -.2f * s, 0, 0); }),
                atk("trident_sweep", 70, (c, t) -> {
                    // blue (stand still) then orange (keep moving) trident sweeps
                    if (t == 14 || t == 40) { boolean blue = t == 14; Vec3 r = c.right(), f = c.fwd(); int s = blue ? 1 : -1;
                        for (int k = 0; k < 4; k++) { var b = c.proj().at(c.pos().add(f.scale(1.5 + k * 1.1)).add(r.scale(s * 6)).add(0, .7, 0)).vel(r.scale(-s * .6)).box(.4f, 2.2f, .4f).dmg(7).warn(6).life(26).pierce(); (blue ? b.blue() : b.orange()).spawn(); }
                        c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, .5f); }
                }, (p, t, m) -> { float a = env(t, 12, 14, 26), b = t > 30 ? env(t - 30, 8, 10, 22) : 0; p.r("armR", -1.4f * (a + b), (a - b) * 1.6f, 0).r("body", 0, (a - b) * .5f, 0); }),
                atk("trident_thrust", 64, (c, t) -> {
                    if (t % 12 == 4 && t < 52) { Vec3 g = near(c, 1); pillar(c, g, .4f, 3.5f, RED, 7, 12, 6); c.sound(SoundEvents.TRIDENT_THROW, 1, .7f); }
                }, (p, t, m) -> { float s = env(t % 12, 8, 10, 12); p.r("armR", -2.5f + 1.2f * s, 0, -.2f).o("armR", 0, -1.5f * s, 0); }),
                atk("fire_rain", 80, below(.6f), (c, t) -> {
                    if (t % 3 == 0 && t < 64) drop(c, near(c, 3), 8, .55f, FIRE, 5, 16);
                    if (t % 9 == 0) c.particle(ParticleTypes.FLAME, c.eye().add(0, 1.5, 0), 10, .6, .05);
                }, (p, t, m) -> { float s = env(t, 10, 66, 80); p.r("armL", -3f * s, 0, .3f).r("armR", -3f * s, 0, -.3f).r("head", -.4f * s, 0, 0); }),
                atk("flame_spiral", 80, below(.4f), (c, t) -> {
                    if (t >= 10 && t < 68 && t % 2 == 0) for (int k = 0; k < 3; k++) orb(c, c.eye(), circ(t * .22 + k * 2.094, .35, 0), .4f, FIRE, 4, 36, 0);
                    if (t % 10 == 0) c.sound(SoundEvents.BLAZE_SHOOT, .6f, .7f);
                }, (p, t, m) -> { float s = env(t, 10, 68, 80); p.r("armL", -1.5f * s, 0, 1f * s).r("armR", -1.5f * s, 0, -1f * s).r("body", 0, t * .05f * s, 0); }),
                atk("final_swing", 70, below(.25f), (c, t) -> {
                    // desperate combo: trident slam into fire waves along the ground
                    if (t == 20) { c.aoe(c.pos().add(c.fwd().scale(2)), 2.8, 9, 1.5); c.sound(SoundEvents.GENERIC_EXPLODE, 1, .8f); }
                    if (t >= 20 && t < 44 && t % 4 == 0) for (int k = -2; k <= 2; k++) { Vec3 d = c.fwd().scale(Math.cos(k * .35)).add(c.right().scale(Math.sin(k * .35)));
                        pillar(c, c.pos().add(d.scale(2 + (t - 20) * .3)), .7f, 1.6f, FIRE, 5, 0, 5); }
                }, (p, t, m) -> { float w = env(t, 18, 20, 20), s = t >= 20 ? env(t - 20, 2, 20, 40) : 0; p.r("armR", -3f * w + .4f * s, 0, -.2f).r("armL", -3f * w + .4f * s, 0, .2f).r("body", -.3f * w + .4f * s, 0, 0); }))
            .build());
    }

    // ============================== OMEGA / PHOTOSHOP FLOWEY ==============================
    static ModelDef omegaModel() {
        int green = 0x3f8a3f, grey = 0x8a8a8a;
        return new ModelDef()
            .box("body", null, 0, 16, 0, 20, 14, 14, 0x6a5a4a)
            .box("head", "body", 0, 13, -2, 18, 13, 12, grey).box("screen", "head", 0, 0, -6.1f, 15, 10, .4f, 0xd8d0b0)
            .glow("eyeL", "screen", -4, 2, -.3f, 3, 3, .2f, 0x000000).glow("eyeR", "screen", 4, 2, -.3f, 3, 3, .2f, 0x000000)
            .box("grinM", "screen", 0, -2.5f, -.3f, 9, 2.5f, .2f, 0x401010)
            .box("pipeL", "head", -11, 4, 2, 3, 12, 3, grey).box("pipeR", "head", 11, 4, 2, 3, 12, 3, grey)
            .limb("armL", "body", -12, 4, -2, 5, 14, 5, green).limb("armR", "body", 12, 4, -2, 5, 14, 5, green)
            .box("handL", "armL", 0, -15, -1, 4, 2, 6, 0xeeeeee).box("handR", "armR", 0, -15, -1, 4, 2, 6, 0xeeeeee)
            .box("trapL", "body", -16, -6, -4, 6, 6, 6, 0x6ab04a).box("trapR", "body", 16, -6, -4, 6, 6, 6, 0x6ab04a)
            .box("vines", "body", 0, -9, 0, 26, 4, 18, green)
            .box("petals", "head", 0, -8, 0, 22, 2, 16, YELLOW);
    }
    static Attack[] floweyCore() {
        int green = 0x3f8a3f;
        return new Attack[]{
            atk("vine_rush", 70, (c, t) -> {
                // vines erupt from the ground in lines that track the target
                if (t % 10 == 0 && t < 56) { Vec3 from = c.pos(), d = flat(c.tpos().subtract(from));
                    for (int k = 2; k < 12; k++) pillar(c, from.add(d.scale(k * .9)), .45f, 2.5f, green, 5, 8 + k, 5); c.sound(SoundEvents.CHORUS_FLOWER_GROW, 1, .8f); }
            }, (p, t, m) -> p.o("vines", 0, -1 * Math.abs(sin(t, .3f)), 0).r("head", .1f * sin(t, .6f), 0, 0)),
            atk("finger_guns", 70, (c, t) -> {
                // the hands become finger guns spraying friendliness pellets
                if (t % 4 == 0 && t >= 8 && t < 60) for (int s = -1; s <= 1; s += 2) { Vec3 at = c.pos().add(c.right().scale(s * 2.4)).add(0, 1.2, 0);
                    orb(c, at, c.aim(at).add(c.rnd.nextGaussian() * .08, 0, c.rnd.nextGaussian() * .08).normalize().scale(.6), .25f, WHITE, 4, 28, 0); }
                if (t % 8 == 0) c.sound(SoundEvents.CROSSBOW_SHOOT, .6f, 1.6f);
            }, (p, t, m) -> { float s = env(t, 8, 60, 70); p.r("armL", -1.4f * s, 0, .3f).r("armR", -1.4f * s, 0, -.3f).o("handL", 0, .5f * sin(t, 2), 0).o("handR", 0, .5f * sin(t, 2), 0); }),
            atk("flamethrower", 70, (c, t) -> {
                if (t >= 16 && t < 56) { Vec3 at = c.eye().add(0, -.5, 0); orb(c, at, c.aim(at).add(c.rnd.nextGaussian() * .06, c.rnd.nextGaussian() * .04, c.rnd.nextGaussian() * .06).normalize().scale(.55), .5f, FIRE, 3, 20, 0);
                    c.particle(ParticleTypes.FLAME, at, 3, .2, .05); }
                if (t == 6) beam(c, c.eye().add(0, -.5, 0), c.aim(c.eye()), .2f, 10, FIRE, 0, 10, 0);
                if (t % 6 == 0 && t >= 16) c.sound(SoundEvents.FIRECHARGE_USE, .5f, .6f);
            }, (p, t, m) -> { float s = env(t, 14, 56, 70); p.o("grinM", 0, -1.5f * s, 0).r("head", .2f * s, 0, 0); }),
            atk("x_bombs", 74, (c, t) -> {
                // bombs drift in, then explode into X or + shaped beams
                if (t % 16 == 4 && t < 60) { Vec3 g = near(c, 2).add(0, .8, 0); mark(c, "bomb" + (t / 16), g); orb(c, g.add(0, 6, 0), new Vec3(0, -6 / 14.0, 0), .6f, 0x222222, 0, 14, 0); }
                if (t % 16 == 2 && t > 4) { Vec3 g = recall(c, "bomb" + ((t - 18) / 16)); boolean x = (t / 16) % 2 == 0;
                    for (int i = 0; i < 4; i++) { double a = i * Math.PI / 2 + (x ? Math.PI / 4 : 0); beam(c, g, circ(a, 1, 0), .5f, 6, 0xffe0a0, 6, 0, 6); }
                    c.particle(ParticleTypes.EXPLOSION, g, 1, 0, 0); c.sound(SoundEvents.GENERIC_EXPLODE, .7f, 1.3f); }
            }, (p, t, m) -> p.r("pipeL", -.3f * Math.abs(sin(t, .2f)), 0, 0).r("pipeR", -.3f * Math.abs(sin(t, .2f)), 0, 0)),
            atk("venus_flytraps", 64, (c, t) -> {
                // the flytraps snap open and spit flies in fans
                if (t % 12 == 6 && t < 56) for (int s = -1; s <= 1; s += 2) { Vec3 at = c.pos().add(c.right().scale(s * 3.2)).add(0, .6, 0);
                    for (int k = -2; k <= 2; k++) orb(c, at, c.aim(at).add(c.right().scale(k * .2)).normalize().scale(.45), .22f, 0x202020, 3, 30, 0); }
            }, (p, t, m) -> { float s = env(t % 12, 4, 6, 12); p.r("trapL", -.6f * s, 0, 0).r("trapR", -.6f * s, 0, 0); }),
            atk("friendliness_ring", 70, (c, t) -> {
                // "Howdy!" ring of friendliness pellets closes in, then the screen flashes with his face
                if (t == 8) for (int i = 0; i < 24; i++) { Vec3 at = c.tpos().add(circ(i * .2618, 5, 0)); orb(c, at, c.aim(at).scale(.16), .3f, WHITE, 6, 50, 18); }
                if (t == 8) c.sound(SoundEvents.WITCH_CELEBRATE, 1, .7f);
            }, (p, t, m) -> p.o("eyeL", 0, -.5f, 0).o("eyeR", 0, -.5f, 0).o("grinM", 0, 0, 0).r("head", 0, .3f * sin(t, .5f), 0)),
            atk("tv_static", 70, below(.6f), (c, t) -> {
                // the TV face glitches: "YOU IDIOT" mega laser from the screen
                if (t == 14) beam(c, c.eye(), c.aim(c.eye()), 2.4f, 26, 0xf0f0f0, 14, 26, 10);
                if (t < 40 && t % 3 == 0) c.particle(ParticleTypes.SMOKE, c.eye(), 6, .8, .02);
                if (t == 14) c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1.5f, .5f);
            }, (p, t, m) -> { float s = env(t, 30, 40, 56); p.r("head", -.2f * s + .05f * sin(t, 4), .05f * sin(t, 5), 0).o("screen", .3f * sin(t, 7), 0, 0); })
        };
    }
    static Attack[] humanSouls() {
        return new Attack[]{
            // Patience (cyan): the Toy Knife slashes; cyan slashes only hurt if you move
            atk("soul_patience", 64, below(.9f), (c, t) -> {
                if (t % 14 == 6 && t < 56) { Vec3 r = c.right(); for (int k = -2; k <= 2; k++) c.proj().at(c.tpos().add(r.scale(k * 1.4))).face(c.fwd()).box(.15f, 2.5f, 1.2f).dmg(6).warn(8).life(14).pierce().blue().color(0x55e8ff).spawn();
                    c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1.4f); }
            }, (p, t, m) -> p.r("armR", -1.2f * Math.abs(sin(t, .22f)), 0, 0)),
            // Bravery (orange): the Tough Glove punches; one huge fist, keep moving
            atk("soul_bravery", 60, below(.8f), (c, t) -> {
                if (t % 15 == 5 && t < 50) { Vec3 at = c.tpos().add(c.fwd().scale(-6)); c.proj().at(at).vel(c.aim(at).scale(.6)).box(1.6f, 1.6f, 1.2f).orange().dmg(7).warn(6).life(22).pierce().spawn();
                    c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, .7f); }
            }, (p, t, m) -> p.r("armL", -1.5f * env(t % 15, 4, 6, 15), 0, 0)),
            // Integrity (blue): Ballet Shoes; dancing feet streak across, trailing sparkles
            atk("soul_integrity", 64, below(.7f), (c, t) -> {
                if (t % 10 == 4 && t < 56) { Vec3 r = c.right(); int s = (t / 10) % 2 == 0 ? 1 : -1; Vec3 at = c.tpos().add(r.scale(s * 7)).add(c.fwd().scale(c.rnd.nextGaussian()));
                    c.proj().at(at).vel(r.scale(-s * .7)).box(.5f, .4f, 1f).color(0x3060ff).dmg(5).warn(6).life(26).spawn(); c.particle(ParticleTypes.END_ROD, at, 6, .3, .05); }
            }, (p, t, m) -> p.r("body", 0, .4f * sin(t, .5f), 0)),
            // Perseverance (purple): Torn Notebook; flying books and words, rows on strings
            atk("soul_perseverance", 70, below(.55f), (c, t) -> {
                if (t % 8 == 0 && t < 60) { Vec3 at = c.tpos().add(circ(c.rnd.nextDouble() * 6.28, 6, 1.5)); c.proj().at(at).vel(c.aim(at).scale(.4)).box(.8f, .2f, .6f).color(0xb050ff).dmg(5).warn(6).life(30).spawn();
                    for (int w = 0; w < 3; w++) orb(c, at, c.aim(at).add(0, .1 * (w - 1), 0).scale(.32), .2f, 0xe0b0ff, 3, 36, 6); }
            }, (p, t, m) -> p.r("head", .15f * sin(t, .9f), 0, 0)),
            // Kindness (green): Burnt Pan; fried eggs and a green shield, then flame flares
            atk("soul_kindness", 64, below(.4f), (c, t) -> {
                if (t % 9 == 0 && t < 54) { Vec3 at = c.eye().add(0, 1, 0); Vec3 d = c.tpos().subtract(at); c.proj().at(at).vel(new Vec3(d.x / 22, .3, d.z / 22)).gravity(.03f).box(.7f, .15f, .7f).color(0xfff6d0).dmg(4).life(26).spawn(); }
                if (t == 30) c.me.heal(6);
                if (t == 30) c.particle(ParticleTypes.HAPPY_VILLAGER, c.eye(), 20, 1, .1);
            }, (p, t, m) -> p.r("armR", -1f * Math.abs(sin(t, .35f)), 0, 0)),
            // Justice (yellow): Empty Gun; revolver shots, each shot splits on impact point
            atk("soul_justice", 64, below(.25f), (c, t) -> {
                if (t % 7 == 3 && t < 56) { Vec3 at = c.eye(); beam(c, at, c.aim(at), .25f, 20, YELLOW, 6, 6, 3); c.sound(SoundEvents.CROSSBOW_SHOOT, 1, .8f);
                    if (t > 10) burst(c, c.tpos(), 6, t, .25, .25f, YELLOW, 3, 16); }
            }, (p, t, m) -> p.r("armR", -1.57f, 0, 0).o("armR", 0, 0, .8f * env(t % 7, 3, 4, 7)))
        };
    }
    static Attack[] concat(Attack[] a, Attack[] b) { Attack[] r = java.util.Arrays.copyOf(a, a.length + b.length); System.arraycopy(b, 0, r, a.length, b.length); return r; }
    static void flowey(List<CharacterDef> out) {
        out.add(CharacterDef.of("omega_flowey", "Omega Flowey", Faction.FLOWEY_CHAOS).hp(300).boss().keep(9).rest(16).speed(0).egg(0x8a8a8a, 0x3f8a3f).size(2.4f, 3.6f).scale(2.2f)
            .model(omegaModel()).attacks(floweyCore()).build());
        out.add(CharacterDef.of("photoshop_flowey", "Photoshop Flowey", Faction.FLOWEY_CHAOS).hp(400).boss().keep(9).rest(14).speed(0).egg(0xd8d0b0, 0xff2030).size(2.4f, 3.6f).scale(2.4f)
            .model(omegaModel()).attacks(concat(floweyCore(), humanSouls())).build());
    }

    // ============================== ASRIEL DREEMURR ==============================
    static void asriel(List<CharacterDef> out) {
        int robe = 0x101018, fur = 0xfaf8f0, rainbow[] = {0xff4040, 0xffa040, 0xffff40, 0x40ff60, 0x40c0ff, 0x8060ff, 0xff60ff};
        out.add(CharacterDef.of("asriel_dreemurr", "Asriel Dreemurr", Faction.FLOWEY_CHAOS).hp(380).boss().keep(7).rest(12).speed(.32f).egg(0x101018, 0xff60ff).size(.9f, 2.6f).scale(1.3f)
            .model(new ModelDef()
                .box("body", null, 0, 14, 0, 9, 10, 6, robe).box("heartMark", "body", 0, 1, -3.1f, 3, 3, .3f, 0xffffff)
                .box("collar", "body", 0, 5, 0, 13, 2, 8, 0x2a2a3a).box("shoulderL", "body", -6.5f, 4.5f, 0, 5, 5, 7, 0x2a2a3a).box("shoulderR", "body", 6.5f, 4.5f, 0, 5, 5, 7, 0x2a2a3a)
                .box("head", "body", 0, 9, 0, 7, 7, 7, fur).box("earL", "head", -4.5f, 0, 0, 2, 5, 2, fur).box("earR", "head", 4.5f, 0, 0, 2, 5, 2, fur)
                .box("hornL", "head", -2.5f, 4.5f, 0, 1.5f, 3, 1.5f, 0xf0e8d0).box("hornR", "head", 2.5f, 4.5f, 0, 1.5f, 3, 1.5f, 0xf0e8d0)
                .glow("eyeL", "head", -1.6f, .5f, -3.6f, 1.2f, 1.2f, .2f, 0xff2040).glow("eyeR", "head", 1.6f, .5f, -3.6f, 1.2f, 1.2f, .2f, 0xff2040)
                .glow("wingL", "body", -8, 2, 4, 10, 10, .4f, 0xff60ff).glow("wingR", "body", 8, 2, 4, 10, 10, .4f, 0x60c0ff)
                .limb("armL", "body", -5.5f, 3, 0, 2.5f, 8, 2.5f, robe).limb("armR", "body", 5.5f, 3, 0, 2.5f, 8, 2.5f, robe)
                .ghost("saber", "armR", 0, -9, -6, .6f, 1.2f, 14, 0xffffff, .0f)
                .box("robeL", null, -2.5f, 9, 0, 4.5f, 9, 6, robe).box("robeR", null, 2.5f, 9, 0, 4.5f, 9, 6, robe))
            .attacks(
                atk("star_blazing", 80, above(.5f), (c, t) -> {
                    // stars rain diagonally; each one bursts into smaller stars where it lands
                    if (t % 6 == 0 && t < 42) { int i = t / 6; Vec3 g = near(c, 3).add(0, .4, 0); mark(c, "star" + i, g); Vec3 from = g.add(-6, 10, -3);
                        c.proj().at(from).vel(g.subtract(from).scale(1 / 18.0)).box(.8f, .8f, .8f).color(YELLOW).dmg(6).life(18).spawn(); }
                    if (t >= 18 && (t - 18) % 6 == 0 && t < 60) { Vec3 g = recall(c, "star" + ((t - 18) / 6)); burst(c, g, 8, t, .3, .3f, YELLOW, 4, 20); c.particle(ParticleTypes.FIREWORK, g, 8, .3, .1); }
                    if (t % 12 == 0) c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.5f, 1.5f);
                }, (p, t, m) -> { float s = env(t, 8, 60, 80); p.r("armL", -3f * s, 0, .2f).r("armR", -3f * s, 0, -.2f).r("head", -.3f * s, 0, 0); }),
                atk("galacta_blazing", 90, below(.5f), (c, t) -> {
                    // GALACTA BLAZING: a storm of large stars, finishing with one massive star
                    if (t % 3 == 0 && t < 48) { int i = t / 3; Vec3 g = near(c, 4).add(0, .4, 0); mark(c, "gstar" + i, g); Vec3 from = g.add(-7, 12, -4);
                        c.proj().at(from).vel(g.subtract(from).scale(1 / 16.0)).box(1f, 1f, 1f).color(YELLOW).dmg(6).life(16).spawn(); }
                    if (t >= 16 && (t - 16) % 3 == 0 && t < 64) { Vec3 g = recall(c, "gstar" + ((t - 16) / 3)); burst(c, g, 6, t, .35, .3f, YELLOW, 4, 18); }
                    if (t == 60) { Vec3 g = ground(c); drop(c, g, 14, 3f, YELLOW, 14, 20); mark(c, "big", g); }
                    if (t == 80) { burst(c, recall(c, "big").add(0, .5, 0), 20, 0, .5, .45f, YELLOW, 5, 26); c.sound(SoundEvents.GENERIC_EXPLODE, 1.5f, 1.2f); }
                }, (p, t, m) -> { float s = env(t, 8, 80, 90); p.r("armL", -3f * s, 0, .5f).r("armR", -3f * s, 0, -.5f).r("wingL", 0, .6f * s, 0).r("wingR", 0, -.6f * s, 0).o("body", 0, 3 * s, 0); }),
                atk("chaos_buster", 70, above(.5f), (c, t) -> {
                    // the gun: rapid bolts, then a final big blast
                    if (t % 4 == 0 && t >= 10 && t < 46) { Vec3 at = c.eye().add(c.right().scale(.5)); orb(c, at, c.aim(at).scale(.7), .3f, rainbow[(t / 4) % 7], 4, 26, 0); c.sound(SoundEvents.SHULKER_SHOOT, .7f, 1.8f); }
                    if (t == 50) { Vec3 at = c.eye().add(c.right().scale(.5)); beam(c, at, c.aim(at), 1.2f, 20, WHITE, 9, 8, 6); c.sound(SoundEvents.GENERIC_EXPLODE, 1, 1.6f); }
                }, (p, t, m) -> { float s = env(t, 8, 60, 70); p.r("armR", -1.57f * s, 0, 0).r("armL", -1.57f * s, 0, .4f).o("armR", 0, 0, t % 4 < 1 && t > 10 && t < 46 ? 1 : 0); }),
                atk("chaos_blaster", 76, below(.5f), (c, t) -> {
                    // CHAOS BLASTER: a fan of huge beams that walk across the field
                    if (t % 8 == 6 && t < 60) { Vec3 at = c.eye(); double sweep = (t - 30) * .03; Vec3 d = c.aim(at); Vec3 r = c.right();
                        for (int k = -1; k <= 1; k++) beam(c, at, d.add(r.scale(k * .35 + sweep)), 1.0f, 22, rainbow[(t / 8 + k + 3) % 7], 7, 8, 5); c.sound(SoundEvents.BEACON_POWER_SELECT, 1.2f, 1.6f); }
                }, (p, t, m) -> { float s = env(t, 6, 64, 76); p.r("armR", -1.57f * s, 0, 0).r("armL", -1.57f * s, 0, 0).r("body", 0, (t - 30) * .01f * s, 0); }),
                atk("chaos_saber", 64, above(.5f), (c, t) -> {
                    // CHAOS SABER: two great swords swing from both sides; slashes telegraph first
                    if (t == 6 || t == 30) { int s = t == 6 ? 1 : -1; Vec3 r = c.right(); Vec3 ctr = c.tpos();
                        c.proj().at(ctr.add(r.scale(s * 5))).vel(r.scale(-s * .55)).box(.3f, 3f, 5f).color(WHITE).dmg(8).warn(10).life(28).pierce().spawn();
                        c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, .8f); }
                }, (p, t, m) -> { float a = env(t, 8, 12, 24), b = t > 26 ? env(t - 26, 8, 12, 24) : 0; p.r("armR", -2.6f * a, 1.5f * a, 0).r("armL", -2.6f * b, -1.5f * b, 0).r("body", 0, (a - b) * .7f, 0); }),
                atk("chaos_slicer", 70, below(.5f), (c, t) -> {
                    // CHAOS SLICER: four slashes, alternating sides and heights
                    if (t % 14 == 4 && t < 60) { int i = t / 14; int s = i % 2 == 0 ? 1 : -1; Vec3 r = c.right(), f = c.fwd();
                        Vec3 axis = i < 2 ? r : f; c.proj().at(c.tpos().add(axis.scale(s * 5))).vel(axis.scale(-s * .65)).box(.3f, 3.4f, 6f).color(WHITE).dmg(8).warn(8).life(24).pierce().spawn();
                        c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, .7f); }
                }, (p, t, m) -> { float s = env(t % 14, 6, 8, 14); p.r("armR", -2.8f * s, ((t / 14) % 2 == 0 ? 1.4f : -1.4f) * s, 0).r("armL", -2.8f * s, ((t / 14) % 2 == 0 ? -1.4f : 1.4f) * s, 0); }),
                atk("shocker_breaker", 74, above(.5f), (c, t) -> {
                    // SHOCKER BREAKER: lightning columns telegraph in rows then strike
                    if (t % 12 == 4 && t < 52) { Vec3 g = ground(c); Vec3 r = c.right(); for (int k = -2; k <= 2; k++) pillar(c, g.add(r.scale(k * 1.6 + ((t / 12) % 2) * .8)), .8f, 12, 0xfff8a0, 7, 12, 4);
                        c.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, .6f, 1.6f); }
                    if (t % 12 == 4 && t > 4) c.particle(ParticleTypes.ELECTRIC_SPARK, ground(c), 20, 1.5, .2);
                }, (p, t, m) -> { float s = env(t % 12, 8, 10, 12); p.r("armL", -3f, 0, .3f).r("armR", -3f, 0, -.3f).o("body", 0, 1.5f * s, 0); }),
                atk("shocker_breaker_ii", 84, below(.5f), (c, t) -> {
                    // SHOCKER BREAKER II: bolts on the target, then an all-around blast ring
                    if (t % 8 == 2 && t < 50) { pillar(c, ground(c), 1.4f, 14, 0xfff8a0, 8, 10, 4); for (int i = 0; i < 3; i++) pillar(c, near(c, 3), .7f, 12, 0xfff8a0, 6, 10, 4);
                        c.sound(SoundEvents.LIGHTNING_BOLT_IMPACT, .7f, 1.3f); }
                    if (t == 60) { for (int i = 0; i < 12; i++) pillar(c, c.pos().add(circ(i * .5236, 3.5, 0)), .9f, 12, 0xfff8a0, 7, 10, 6); c.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.5f, .9f); }
                }, (p, t, m) -> { float s = env(t, 8, 72, 84); p.r("armL", -3f * s, 0, .6f).r("armR", -3f * s, 0, -.6f).r("wingL", 0, .4f * sin(t, 1), 0).r("wingR", 0, -.4f * sin(t, 1), 0); }),
                atk("hyper_goner", 110, below(.75f), (c, t) -> {
                    // HYPER GONER: a giant goat skull opens its maw and inhales; spinning diamonds are sucked in with you
                    Vec3 mouth = c.pos().add(c.fwd().scale(-2)).add(0, 3, 0);
                    if (t == 6) {
                        c.proj().at(mouth).face(c.fwd()).box(5, 4.5f, 4).color(WHITE).dmg(0).life(96).spawn();
                        c.proj().at(mouth.add(0, -2.6, -.5)).face(c.fwd()).box(4, 1.4f, 3.4f).color(0xe8e8e8).dmg(0).life(96).spawn();
                        for (int s = -1; s <= 1; s += 2) c.proj().at(mouth.add(c.right().scale(s * 1.2)).add(c.fwd().scale(2.05)).add(0, .5, 0)).face(c.fwd()).box(1.2f, 1f, .15f).color(0x000000).dmg(0).life(96).spawn();
                        c.sound(SoundEvents.ENDER_DRAGON_GROWL, 1.5f, .6f);
                    }
                    if (t >= 24 && t < 96) {
                        pull(c, mouth, .08);
                        if (t % 3 == 0) { Vec3 at = mouth.add(circ(t * .4, 9, (c.rnd.nextDouble() - .5) * 4)); c.proj().at(at).vel(mouth.subtract(at).scale(1 / 22.0)).box(.5f, .9f, .5f).color(WHITE).dmg(5).life(22).spawn(); }
                        if (t % 10 == 0) c.sound(SoundEvents.ELYTRA_FLYING, .6f, 1.5f);
                    }
                    if (t >= 24 && t < 96 && c.target.position().distanceTo(mouth) < 2.5 && t % 10 == 0) c.hit(c.target, 6);
                }, (p, t, m) -> { float s = env(t, 6, 96, 110); p.r("armL", -3f * s, 0, .8f).r("armR", -3f * s, 0, -.8f).r("head", -.5f * s, 0, 0).r("wingL", 0, .8f * s, 0).r("wingR", 0, -.8f * s, 0); }),
                atk("angel_of_death", 100, below(.2f), (c, t) -> {
                    // final form: rainbow wings unfurl, star bullets stream from them in every direction
                    if (t % 2 == 0 && t >= 10 && t < 90) for (int s = -1; s <= 1; s += 2) { Vec3 at = c.eye().add(c.right().scale(s * 1.8)).add(0, .6, 0); orb(c, at, circ(t * .18 * s, .4, Math.sin(t * .1) * .05), .35f, rainbow[(t / 2) % 7], 5, 34, 0); }
                    if (t % 20 == 0) { burst(c, c.eye(), 16, t, .3, .4f, WHITE, 6, 40); c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.8f); }
                    if (t % 4 == 0) c.particle(ParticleTypes.END_ROD, c.eye(), 6, 1.5, .1);
                }, (p, t, m) -> { float s = env(t, 10, 90, 100); p.r("wingL", 0, 1f * s + .2f * sin(t, .5f), -.3f * s).r("wingR", 0, -1f * s - .2f * sin(t, .5f), .3f * s).o("body", 0, 4 * s, 0).r("armL", 0, 0, .8f * s).r("armR", 0, 0, -.8f * s); }))
            .build());
    }

    // ============================== CHARA ==============================
    static void chara(List<CharacterDef> out) {
        out.add(CharacterDef.of("chara", "Chara", Faction.GENOCIDE_ROUTE).hp(240).boss().keep(4).rest(10).speed(.35f).egg(0x2f8a3a, 0xff1020).size(.6f, 1.6f)
            .model(new ModelDef()
                .box("body", null, 0, 9, 0, 7, 7, 4, 0x2f8a3a).box("stripe", "body", 0, 0, -2.1f, 7.2f, 1.5f, .3f, 0xf0d070)
                .box("head", "body", 0, 7, 0, 7, 7, 6, 0xf8dcc0).box("hair", "head", 0, 1.5f, .5f, 8, 6, 7, 0x6a3a1a)
                .glow("eyeL", "head", -1.6f, -.3f, -3.1f, 1.2f, .8f, .2f, 0xff1020).glow("eyeR", "head", 1.6f, -.3f, -3.1f, 1.2f, .8f, .2f, 0xff1020)
                .box("grin", "head", 0, -2.2f, -3.1f, 4, .6f, .2f, 0x3a0a0a)
                .limb("armL", "body", -4.5f, 3, 0, 2, 6, 2, 0x2f8a3a).limb("armR", "body", 4.5f, 3, 0, 2, 6, 2, 0x2f8a3a)
                .box("knife", "armR", 0, -6.5f, -2.5f, .4f, .8f, 5, 0xd0d0d0)
                .limb("legL", null, -1.8f, 5, 0, 2.5f, 5, 2.5f, 0x6a4a2a).limb("legR", null, 1.8f, 5, 0, 2.5f, 5, 2.5f, 0x6a4a2a))
            .attacks(
                atk("real_knife", 50, (c, t) -> {
                    // the giant red slash across the screen
                    if (t == 6) c.proj().at(c.tpos()).face(c.fwd().add(c.right()).normalize()).box(.3f, 3.5f, 6f).color(RED).dmg(14).warn(14).life(20).pierce().spawn();
                    if (t == 20) { c.sound(SoundEvents.PLAYER_ATTACK_CRIT, 1.5f, .5f); c.particle(ParticleTypes.CRIT, c.tpos(), 30, 1, .3); }
                }, (p, t, m) -> { float w = env(t, 18, 20, 20), s = t >= 20 ? env(t - 20, 2, 10, 30) : 0; p.r("armR", -2.8f * w + 1.2f * s, .6f * w - 1.4f * s, 0).r("body", 0, .4f * w - .6f * s, 0); }),
                atk("dash_stab", 50, (c, t) -> {
                    if (t == 10) { c.tp(c.target.position().add(c.target.getLookAngle().scale(-2).multiply(1, 0, 1))); c.sound(SoundEvents.ENDERMAN_TELEPORT, 1, .4f); }
                    if (t == 18) { c.aoe(c.pos().add(c.fwd()), 1.8, 10, .8); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1.4f); }
                }, (p, t, m) -> { float s = env(t, 14, 18, 30); p.r("armR", -1.5f * s, 0, 0).o("armR", 0, 0, -2 * s); }),
                atk("erase", 80, below(.5f), (c, t) -> {
                    // "ERASE": columns of void wipe the world in a closing sweep, the screen cracks red
                    if (t >= 10 && t < 60 && t % 4 == 0) { Vec3 r = c.right(); double off = 8 - (t - 10) * .3; for (int s = -1; s <= 1; s += 2) pillar(c, ground(c).add(r.scale(s * off)), 1.2f, 8, 0x000000, 8, 6, 6); }
                    if (t == 10) c.sound(SoundEvents.ELDER_GUARDIAN_CURSE, 1, .5f);
                    if (t > 50) c.particle(ParticleTypes.SQUID_INK, c.tpos(), 10, 2, .05);
                }, (p, t, m) -> { float s = env(t, 10, 60, 80); p.r("armL", -1.57f * s, 0, .6f).r("armR", -1.57f * s, 0, -.6f).r("head", .25f * s, 0, 0); }),
                atk("determination", 70, below(.3f), (c, t) -> {
                    if (t % 10 == 0 && t < 60) { burst(c, c.eye(), 12, t * .3, .4, .35f, RED, 6, 30); c.particle(ParticleTypes.DAMAGE_INDICATOR, c.eye(), 6, .3, .1); }
                }, (p, t, m) -> p.r("body", 0, t * .1f, 0).o("body", 0, 1.5f * sin(t, .3f), 0)))
            .build());
    }
}
