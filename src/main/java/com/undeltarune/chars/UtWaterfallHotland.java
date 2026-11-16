package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.function.Predicate;

/** Undertale: Waterfall + Hotland. (Mettaton NEO lives in UtCoreFinale as `mettaton_neo`.) */
public final class UtWaterfallHotland {
    // ---------- tiny DSL ----------
    interface Tk { void run(AttackCtx c, int t); }
    interface Ps { void run(Pose p, float t, UtMob m); }
    static Attack a(String id, int dur, Tk tk, Ps ps) { return a(id, dur, m -> true, tk, ps); }
    static Attack a(String id, int dur, Predicate<UtMob> use, Tk tk, Ps ps) {
        return new Attack(id, dur) {
            public void tick(AttackCtx c, int t) { tk.run(c, t); }
            public void pose(Pose p, float t, UtMob m) { ps.run(p, t, m); }
            public boolean usable(UtMob m) { return use.test(m); }
        };
    }
    static Predicate<UtMob> above(float f) { return m -> m.hpFrac() >= f; }
    static Predicate<UtMob> below(float f) { return m -> m.hpFrac() < f; }
    /** 0..1 ramp between ticks a and b. */
    static float up(float t, float a, float b) { return Math.max(0, Math.min(1, (t - a) / (b - a))); }
    /** Envelope: rises a..b, holds, falls c..d (wind-up / strike / recovery). */
    static float env(float t, float a, float b, float c, float d) { return up(t, a, b) * (1 - up(t, c, d)); }
    static float sin(float t, float k) { return (float) Math.sin(t * k); }
    static Vec3 ring(Vec3 c, double ang, double r, double y) { return c.add(Math.cos(ang) * r, y, Math.sin(ang) * r); }

    static UtProjectile.B shot(AttackCtx c, Vec3 from, Vec3 dir, double spd, float w, float h, float l, int col, float dmg, int life) {
        return c.proj().at(from).vel(dir.normalize().scale(spd)).box(w, h, l).color(col).dmg(dmg).life(life);
    }
    /** Thin harmless line from->to (telegraph). */
    static void tele(AttackCtx c, Vec3 from, Vec3 to, int col, int life) {
        Vec3 d = to.subtract(from);
        c.proj().at(from.add(d.scale(.5))).face(d).box(.06f, .06f, (float) d.length()).color(col).dmg(0).warn(life).life(life).spawn();
    }
    /** Flat warning tile on the ground. */
    static void mark(AttackCtx c, Vec3 at, float r, int col, int life) {
        c.proj().at(at.add(0, .05, 0)).box(r, .05f, r).color(col).dmg(0).warn(life).life(life).spawn();
    }
    /** Radial burst of n bullets. */
    static void burst(AttackCtx c, Vec3 at, int n, double spd, float s, int col, float dmg, int life, double phase) {
        for (int i = 0; i < n; i++) {
            double ang = phase + i * Math.PI * 2 / n;
            shot(c, at, new Vec3(Math.cos(ang), 0, Math.sin(ang)), spd, s, s, s, col, dmg, life).spawn();
        }
    }
    /** Something falls on the target area with a ground marker. */
    static void drop(AttackCtx c, double spread, float w, float h, int col, float dmg, double spd) {
        Vec3 g = c.target.position().add(c.rnd.nextGaussian() * spread, 0, c.rnd.nextGaussian() * spread);
        mark(c, g, w + .3f, col, (int) (7 / spd));
        shot(c, g.add(0, 7, 0), new Vec3(0, -1, 0), spd, w, h, w, col, dmg, (int) (8 / spd)).spawn();
    }
    static void root(AttackCtx c, int ticks, int amp) { c.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ticks, amp, false, true)); }

    static final int SPEAR = 0x3ff2ff, YELLOW = 0xffe14d, PURPLE = 0xb04dff, WHITE = 0xffffff;

    public static void register(List<CharacterDef> out) {
        // ======================= WATERFALL =======================
        out.add(CharacterDef.of("undyne", "Undyne", Faction.ROYAL_GUARD).hp(220).boss().keep(7).rest(16).speed(.32f)
            .egg(0x2b2f4a, 0x3fa7d6).size(.7f, 2.2f).scale(1.1f)
            .model(knight(0x2b2f4a, 0x6d79a8, 0x3fa7d6, 0xd8263a))
            .attacks(
                greenSpears("undyne_green_spears", 4, 9, 4, above(.5f)),
                reverseSpears("undyne_reverse_spears", 4, above(.35f)),
                spearVolley("undyne_spear_volley", 5, 6, 4.5f, below(.75f)),
                spearRise("undyne_spear_rise", 3, 8, 5, below(.6f)),
                spearRain("undyne_spear_rain", 4, below(.45f)),
                greenSpears("undyne_green_frenzy", 8, 6, 5, below(.3f))).build());

        out.add(CharacterDef.of("undyne_the_undying", "Undyne the Undying", Faction.ROYAL_GUARD).hp(420).boss().keep(7).rest(10).speed(.36f)
            .egg(0xe8ecf5, 0x3ff2ff).size(.7f, 2.3f).scale(1.15f)
            .model(knight(0xdfe4ef, 0x3ff2ff, 0x3fa7d6, 0xd8263a).glow("heart", "body", 0, 3, -2.7f, 2, 2, .4f, 0x3ff2ff))
            .attacks(
                greenSpears("undying_green_spears", 8, 5, 6, m -> true),
                reverseSpears("undying_reverse_spears", 6, m -> true),
                undyingSpiral(),
                spearRise("undying_spear_rise", 5, 5, 7, m -> true),
                spearVolley("undying_spear_barrage", 9, 3, 6, m -> true),
                spearRain("undying_spear_rain", 7, below(.7f)),
                undyingWalls(),
                spearOfJustice()).build());

        out.add(CharacterDef.of("aaron", "Aaron", Faction.UNDERGROUND).hp(60).keep(5).speed(.28f).egg(0xd9b48f, 0xf2d6b8).size(.8f, 1.8f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 6, 10, 5, 0xd9b48f)
                .box("belly", "body", 0, -1, -2.6f, 4, 6, .4f, 0xf2d6b8)
                .box("head", "body", 0, 8, -1, 5, 6, 8, 0xd9b48f)
                .box("snout", "head", 0, -1, -5, 2.5f, 2.5f, 3, 0xd9b48f)
                .glow("eye", "head", 1.6f, 1, -3.2f, 1, 1, .3f, 0x111111)
                .box("fin", "head", 0, 3.5f, 2, 1, 3, 4, 0xc49a70)
                .limb("armR", "body", 4.5f, 4, 0, 5, 7, 5, 0xd9b48f)
                .box("bicep", "armR", 0, -2, -1, 6, 4, 6, 0xe3c09b)
                .limb("armL", "body", -3.5f, 4, 0, 2, 5, 2, 0xd9b48f)
                .box("tail", null, 0, 2, 1, 3, 4, 3, 0xc49a70))
            .attacks(
                a("aaron_sweat_chase", 70, (c, t) -> {
                    if (t >= 10 && t < 55 && t % 5 == 0) {
                        Vec3 from = c.eye().add(c.right().scale(c.rnd.nextGaussian() * .6));
                        shot(c, from, c.aim(from), .38 + t * .004, .25f, .35f, .25f, 0x8fd8ff, 3, 40).spawn();
                        c.particle(ParticleTypes.SPLASH, from, 6, .2, .1);
                    }
                    if (t == 10) c.sound(SoundEvents.PLAYER_SPLASH, .7f, 1.6f);
                }, (p, t, m) -> {
                    float f = env(t, 0, 8, 55, 68);
                    p.r("armR", -2.5f * f, 0, -.6f * f).r("armL", -.6f * f, 0, .3f).r("body", 0, sin(t, .5f) * .2f * f, 0);
                }),
                a("aaron_flex", 60, (c, t) -> {
                    if (t == 14 || t == 30 || t == 46) {
                        burst(c, c.eye(), 10, .35, .25f, 0x8fd8ff, 3, 34, t * .3);
                        c.sound(SoundEvents.PLAYER_ATTACK_STRONG, .8f, .7f);
                    }
                }, (p, t, m) -> {
                    float f = (float) Math.abs(Math.sin(t * Math.PI / 16));
                    p.r("armR", -1.6f * f, 0, -1.2f * f).o("bicep", 0, f, 0).r("head", 0, .3f * f, 0).o("body", 0, -.5f * f, 0);
                })).build());

        out.add(CharacterDef.of("woshua", "Woshua", Faction.UNDERGROUND).hp(50).keep(6).speed(.22f).egg(0xe8eef2, 0x5bc0eb).size(.8f, 1.4f)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 10, 6, 8, 0xe8eef2)
                .box("rim", "body", 0, 3, 0, 11, 1, 9, 0xcfd8de)
                .ghost("water", "body", 0, 3.6f, 0, 9, .6f, 7, 0x5bc0eb, .8f)
                .box("head", "body", 0, 7, -1, 4, 5, 4, 0x8ad3f0)
                .glow("eyeL", "head", -1, 1, -2.1f, .8f, .8f, .3f, 0x111111)
                .glow("eyeR", "head", 1, 1, -2.1f, .8f, .8f, .3f, 0x111111)
                .box("crest", "head", 0, 3, 0, 1, 2, 3, 0xffd34d)
                .box("tail", "body", 0, 2, 5, 3, 3, 3, 0x8ad3f0)
                .limb("legL", null, -3, 1.5f, 0, 1.5f, 1.5f, 1.5f, 0xcfd8de)
                .limb("legR", null, 3, 1.5f, 0, 1.5f, 1.5f, 1.5f, 0xcfd8de))
            .attacks(
                a("woshua_spiral_drops", 70, (c, t) -> {
                    if (t >= 8 && t < 60 && t % 3 == 0) {
                        double ang = t * .45;
                        Vec3 o = c.pos().add(0, .8, 0);
                        for (int k = 0; k < 3; k++) shot(c, o, new Vec3(Math.cos(ang + k * 2.094), 0, Math.sin(ang + k * 2.094)), .32, .2f, .3f, .2f, 0x9fe6ff, 3, 40).spawn();
                        c.particle(ParticleTypes.SPLASH, o, 4, .3, .1);
                    }
                }, (p, t, m) -> p.r("body", 0, t * .25f * up(t, 0, 8), 0).o("water", 0, sin(t, .8f) * .3f, 0).r("tail", sin(t, .6f) * .4f, 0, 0)),
                a("woshua_hope_soap", 80, (c, t) -> {
                    // "HOPE" soap bar bounces between walls around the target, leaving short-lived sparkle bullets
                    if (t < 10) return;
                    int k = t - 10; double side = (k / 14) % 2 == 0 ? 1 : -1; double ph = (k % 14) / 14.0;
                    Vec3 r = c.right(), f = c.fwd();
                    Vec3 at = c.tpos().add(r.scale(side * (ph * 8 - 4))).add(f.scale(Math.sin(k * .3) * 2.5));
                    if (k % 2 == 0) c.proj().at(at).box(.7f, .3f, .45f).color(0xfff2b3).dmg(4).life(3).spawn();
                    if (k % 3 == 0) c.proj().at(at).box(.18f, .18f, .18f).color(0xffffff).dmg(2).warn(4).life(22).shrink().spawn();
                    c.particle(ParticleTypes.BUBBLE_POP, at, 2, .2, .02);
                }, (p, t, m) -> p.r("head", -.4f * env(t, 0, 8, 70, 80), 0, 0).o("body", 0, Math.abs(sin(t, .4f)) * .6f, 0))).build());

        out.add(CharacterDef.of("moldbygg", "Moldbygg", Faction.UNDERGROUND).hp(60).keep(5).speed(.18f).egg(0x7ccf5a, 0x4b8f35).size(.9f, 1.6f)
            .model(new ModelDef()
                .box("body", null, 0, 6, 0, 10, 12, 10, 0x7ccf5a)
                .box("head", "body", 0, 7, 0, 7, 4, 7, 0x93dc73)
                .glow("eyeL", "body", -2, 2, -5.1f, 1, 2, .3f, 0x1a3d10)
                .glow("eyeR", "body", 2, 2, -5.1f, 1, 2, .3f, 0x1a3d10)
                .box("spot", "body", 3, -3, -5.1f, 2, 2, .3f, 0x4b8f35))
            .attacks(
                a("moldbygg_mold_hops", 70, (c, t) -> {
                    if (t >= 10 && t < 60 && t % 10 == 0) {
                        Vec3 from = c.pos().add(c.right().scale(c.rnd.nextGaussian())).add(0, .5, 0);
                        Vec3 d = c.tpos().subtract(from);
                        c.proj().at(from).vel(new Vec3(d.x * .04, .55, d.z * .04)).gravity(.04f).box(.5f, .4f, .5f).color(0x93dc73).dmg(4).life(40).spawn();
                        c.sound(SoundEvents.SLIME_JUMP, .8f, 1.2f);
                    }
                }, (p, t, m) -> { float sq = Math.abs(sin(t, (float) Math.PI / 10)); p.o("body", 0, -sq, 0).o("head", 0, sq * 2, 0); }),
                a("moldbygg_spore_ring", 50, (c, t) -> {
                    if (t == 18 || t == 32) { burst(c, c.pos().add(0, .7, 0), 12, .3, .3f, 0x4b8f35, 3, 36, t); c.sound(SoundEvents.SLIME_SQUISH, 1, .6f); }
                }, (p, t, m) -> p.o("head", 0, 2 * env(t, 0, 16, 18, 24), 0).o("body", 0, -env(t, 0, 16, 18, 24), 0))).build());

        out.add(CharacterDef.of("shyren", "Shyren", Faction.UNDERGROUND).hp(55).keep(7).speed(.2f).egg(0x8fa8c8, 0x5f6f9a).size(.7f, 1.7f)
            .model(new ModelDef()
                .box("body", null, 0, 7, 0, 5, 9, 4, 0x8fa8c8)
                .box("head", "body", 0, 7, -1, 8, 6, 9, 0x8fa8c8)
                .box("lips", "head", 0, -1.5f, -4.7f, 3, 1.5f, .5f, 0xd36f9a)
                .glow("eye", "head", -2.5f, 1, -4.6f, 1.4f, 1.4f, .3f, 0x1b2340)
                .box("fin", "head", 0, 3.5f, 1, 1, 3, 6, 0x5f6f9a)
                .limb("armL", "body", -3, 3, 0, 1.5f, 5, 1.5f, 0x8fa8c8)
                .limb("armR", "body", 3, 3, 0, 1.5f, 5, 1.5f, 0x8fa8c8)
                .box("tail", null, 0, 2, 1, 3, 4, 2, 0x5f6f9a))
            .attacks(
                a("shyren_notes", 80, (c, t) -> {
                    // music notes float out of her head on a sine wave; each wave is faster than the last
                    if (t >= 10 && t < 70 && t % 6 == 0) {
                        Vec3 from = c.eye().add(0, .5, 0), d = c.aim(from), r = c.right();
                        double w = Math.sin(t * .4) * .25;
                        shot(c, from, d.add(r.scale(w)), .2 + t * .003, .25f, .35f, .12f, 0xe0e6ff, 3, 60).spawn();
                        c.proj().at(from.add(0, .3, 0)).vel(d.add(r.scale(w)).normalize().scale(.2 + t * .003)).box(.06f, .45f, .06f).color(0xe0e6ff).dmg(0).life(60).spawn();
                        c.sound(SoundEvents.NOTE_BLOCK_FLUTE.value(), .9f, .6f + (t % 24) / 24f);
                    }
                }, (p, t, m) -> p.r("head", -.25f + sin(t, .3f) * .12f, sin(t, .2f) * .2f, 0).r("armL", -.5f, 0, .4f).r("armR", -.5f, 0, -.4f).o("lips", 0, Math.abs(sin(t, .8f)) * .5f, 0)),
                a("shyren_chorus", 70, (c, t) -> {
                    // fish-head chorus: notes spiral in from a ring around the target
                    if (t >= 10 && t < 60 && t % 5 == 0) {
                        double ang = t * .5; Vec3 at = ring(c.tpos(), ang, 5, .3);
                        tele(c, at, c.tpos(), 0xe0e6ff, 6);
                        c.proj().at(at).vel(c.aim(at).scale(.28)).box(.3f, .3f, .3f).color(0xc8d2ff).dmg(3).warn(6).life(30).spawn();
                        c.sound(SoundEvents.NOTE_BLOCK_CHIME.value(), .7f, .8f + (t % 20) / 20f);
                    }
                }, (p, t, m) -> p.r("body", 0, sin(t, .25f) * .3f, 0).r("armL", -1.2f * up(t, 0, 10), 0, .6f).r("armR", -1.2f * up(t, 0, 10), 0, -.6f))).build());

        out.add(CharacterDef.of("temmie", "Temmie", Faction.UNDERGROUND).hp(40).keep(4).speed(.34f).random().egg(0xf2efe6, 0x8a5a2b).size(.6f, 1.0f)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 6, 4, 8, 0xf2efe6)
                .box("head", "body", 0, 4, -3, 8, 6, 6, 0xf2efe6)
                .box("hair", "head", 0, 3.2f, 0, 8.5f, 1.2f, 6.5f, 0x8a5a2b)
                .box("earL", "head", -4.6f, 1, 0, 1.2f, 5, 2, 0xf2efe6)
                .box("earR", "head", 4.6f, 1, 0, 1.2f, 5, 2, 0xf2efe6)
                .glow("eyeL", "head", -2, .5f, -3.1f, 1.2f, 1.2f, .3f, 0x111111)
                .glow("eyeR", "head", 2, .5f, -3.1f, 1.2f, 1.2f, .3f, 0x111111)
                .box("blushL", "head", -2.6f, -1, -3.1f, 1.4f, .6f, .3f, 0xff9ab2)
                .box("blushR", "head", 2.6f, -1, -3.1f, 1.4f, .6f, .3f, 0xff9ab2)
                .limb("legL", null, -2, 2, -2, 1.5f, 2, 1.5f, 0xf2efe6)
                .limb("legR", null, 2, 2, -2, 1.5f, 2, 1.5f, 0xf2efe6))
            .attacks(
                a("temmie_stampede", 70, (c, t) -> {
                    // little temmies run across in rows (alternating sides)
                    if (t >= 6 && t < 56 && t % 7 == 0) {
                        double s = (t / 7) % 2 == 0 ? 1 : -1; Vec3 r = c.right(), f = c.fwd();
                        Vec3 at = c.tpos().add(r.scale(-s * 6)).add(f.scale(c.rnd.nextGaussian() * 1.5)).add(0, -.5, 0);
                        c.proj().at(at).vel(r.scale(s * .45)).box(.5f, .45f, .55f).color(0xf2efe6).dmg(3).warn(5).life(30).spawn();
                        c.proj().at(at.add(0, .35, 0)).vel(r.scale(s * .45)).box(.55f, .12f, .4f).color(0x8a5a2b).dmg(0).life(30).spawn();
                        c.sound(SoundEvents.WOLF_AMBIENT, .6f, 2f);
                    }
                }, (p, t, m) -> p.o("body", 0, Math.abs(sin(t, .9f)) * 1.5f, 0).r("earL", 0, 0, -.3f - sin(t, .9f) * .3f).r("earR", 0, 0, .3f + sin(t, .9f) * .3f)),
                a("temmie_hop", 50, (c, t) -> {
                    // hOI! temmie heads bounce toward you
                    if (t == 8 || t == 20 || t == 32) {
                        for (int k = -1; k <= 1; k++) {
                            Vec3 from = c.eye().add(c.right().scale(k * .8));
                            Vec3 d = c.tpos().subtract(from);
                            c.proj().at(from).vel(new Vec3(d.x * .05, .45, d.z * .05)).gravity(.045f).box(.4f, .35f, .4f).color(0xf2efe6).dmg(2).life(30).spawn();
                        }
                        c.sound(SoundEvents.CAT_AMBIENT, .8f, 1.8f);
                    }
                }, (p, t, m) -> { float j = Math.abs(sin(t, (float) Math.PI / 12)); p.o("body", 0, j * 3, 0).r("head", -.3f * j, 0, sin(t, 1.3f) * .2f); })).build());

        out.add(CharacterDef.of("mad_dummy", "Mad Dummy", Faction.UNDERGROUND).hp(180).boss().keep(7).rest(14).speed(.25f)
            .egg(0xd8cdb8, 0x7a5c3e).size(.7f, 1.8f)
            .model(dummy(0xd8cdb8, 0x7a5c3e))
            .attacks(madDummyBullets("mad_dummy_magic", 0xffffff, m -> true), madDummyMissiles("mad_dummy_robots", below(.75f)),
                madKnives("mad_dummy_knives", 0xd0d0d0, below(.5f)), madFinale("mad_dummy_finale", 0xd8cdb8, below(.25f))).build());

        out.add(CharacterDef.of("mad_mew_mew", "Mad Mew Mew", Faction.UNDERGROUND).hp(200).boss().keep(7).rest(14).speed(.26f)
            .egg(0xffb6d9, 0xff5fa8).size(.7f, 1.8f)
            .model(dummy(0xffc9e2, 0xff5fa8)
                .box("earL", "head", -2.5f, 3.5f, 0, 1.6f, 2.5f, 1, 0xffc9e2)
                .box("earR", "head", 2.5f, 3.5f, 0, 1.6f, 2.5f, 1, 0xffc9e2)
                .box("bow", "head", 0, 3.4f, -1.5f, 3, 1.2f, .8f, 0xff5fa8))
            .attacks(madDummyBullets("mad_mew_mew_paws", 0xff8cc6, m -> true), madDummyMissiles("mad_mew_mew_robots", below(.75f)),
                madKnives("mad_mew_mew_knives", 0xffd1e8, below(.5f)), madFinale("mad_mew_mew_finale", 0xffc9e2, below(.25f))).build());

        out.add(CharacterDef.of("napstablook", "Napstablook", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(88).keep(6).speed(.15f)
            .egg(0xe6eaff, 0x9aa3c8).size(.8f, 1.8f)
            .model(new ModelDef()
                .ghost("body", null, 0, 10, 0, 10, 16, 8, 0xe6eaff, .75f)
                .ghost("head", "body", 0, 6, 0, 9, 4, 7.5f, 0xe6eaff, .75f)
                .glow("eyeL", "body", -2.2f, 4, -4.1f, 2, 1.2f, .3f, 0x2a2e40)
                .glow("eyeR", "body", 2.2f, 4, -4.1f, 2, 1.2f, .3f, 0x2a2e40)
                .ghost("hem", "body", 0, -8, 0, 11, 1.5f, 9, 0xe6eaff, .5f))
            .attacks(
                a("napstablook_tears", 70, (c, t) -> {
                    if (t >= 8 && t < 62 && t % 3 == 0) {
                        Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 2.5, 5 + c.rnd.nextDouble(), c.rnd.nextGaussian() * 2.5);
                        c.proj().at(at).vel(new Vec3(0, -.3, 0)).gravity(.01f).box(.15f, .3f, .15f).color(0x8fb3ff).dmg(2).life(40).spawn();
                    }
                    if (t % 20 == 8) c.sound(SoundEvents.GHAST_AMBIENT, .4f, 1.6f);
                }, (p, t, m) -> p.o("body", 0, sin(t, .15f) * .8f, 0).r("head", .25f, 0, 0).o("eyeL", 0, -.4f, 0).o("eyeR", 0, -.4f, 0)),
                a("napstablook_tear_waves", 70, (c, t) -> {
                    // tears drift toward you in a swaying wave
                    if (t >= 8 && t < 60 && t % 4 == 0) {
                        Vec3 from = c.eye(), d = c.aim(from), r = c.right();
                        for (int k = -1; k <= 1; k += 2)
                            shot(c, from.add(r.scale(k * .4)), d.add(r.scale(Math.sin(t * .35) * .5 * k)), .26, .15f, .3f, .15f, 0x8fb3ff, 2, 50).spawn();
                    }
                }, (p, t, m) -> p.r("body", 0, sin(t, .2f) * .25f, sin(t, .3f) * .08f)),
                a("napstablook_dapper_blook", 50, (c, t) -> {
                    // tears pile up into a hat over Napstablook — harmless (canon: "dapper blook")
                    if (t < 40 && t % 2 == 0) {
                        Vec3 at = c.eye().add(c.rnd.nextGaussian() * .3, .9 + t * .015, c.rnd.nextGaussian() * .3);
                        c.proj().at(at).box(.15f, .15f, .15f).color(0x8fb3ff).dmg(0).life(60 - t).spawn();
                    }
                    if (t == 40) c.particle(ParticleTypes.NOTE, c.eye().add(0, 1.6, 0), 3, .4, 0);
                }, (p, t, m) -> p.r("head", -.15f * up(t, 30, 40), 0, 0))).build());

        out.add(CharacterDef.of("onionsan", "Onionsan", Faction.UNDERGROUND).att(Attitude.FRIENDLY).hp(100).keep(8).speed(.15f)
            .egg(0x9b6fb4, 0xf0e070).size(1.4f, 2.6f).scale(1.4f)
            .model(new ModelDef()
                .box("body", null, 0, 12, 0, 14, 12, 12, 0x9b6fb4)
                .box("head", "body", 0, 8, 0, 12, 6, 11, 0xa97fc2)
                .glow("eyeL", "body", -3, 2, -6.1f, 2.5f, 3, .3f, 0xf0e070)
                .glow("eyeR", "body", 3, 2, -6.1f, 2.5f, 3, .3f, 0xf0e070)
                .box("mouth", "body", 0, -3, -6.1f, 5, 1, .3f, 0x3a2050)
                .limb("armL", "body", -7, -4, -3, 2.5f, 9, 2.5f, 0x8a5fa3)
                .limb("armR", "body", 7, -4, -3, 2.5f, 9, 2.5f, 0x8a5fa3)
                .limb("legL", null, -3, 6, 2, 2.5f, 6, 2.5f, 0x8a5fa3)
                .limb("legR", null, 3, 6, 2, 2.5f, 6, 2.5f, 0x8a5fa3))
            .attacks(
                a("onionsan_tentacles", 70, (c, t) -> {
                    if (t >= 10 && t < 60 && t % 10 == 0) {
                        Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 1.5, 0, c.rnd.nextGaussian() * 1.5);
                        mark(c, g, 1.2f, 0x5bc0eb, 10);
                        c.proj().at(g.add(0, -1.5, 0)).vel(new Vec3(0, .35, 0)).box(.6f, 2.5f, .6f).color(0x8a5fa3).dmg(5).warn(10).life(22).spawn();
                        c.particle(ParticleTypes.SPLASH, g, 20, .5, .2);
                    }
                }, (p, t, m) -> p.r("armL", -1.5f - sin(t, .3f) * .5f, 0, .5f).r("armR", -1.5f + sin(t, .3f) * .5f, 0, -.5f).o("body", 0, sin(t, .2f), 0)),
                a("onionsan_splash", 50, (c, t) -> {
                    if (t == 15 || t == 30) { burst(c, c.pos().add(0, .4, 0), 16, .32, .3f, 0x5bc0eb, 3, 30, t); c.sound(SoundEvents.GENERIC_SPLASH, 1, .7f); }
                }, (p, t, m) -> p.o("body", 0, -2 * env(t, 0, 12, 15, 22) - 2 * env(t, 22, 28, 30, 38), 0))).build());

        // Glyde (optional miniboss outside the Mysterious Door)
        out.add(CharacterDef.of("glyde", "Glyde", Faction.UNDERGROUND).hp(110).keep(7).speed(.3f).egg(0xe8c84a, 0xffffff).size(1.2f, 1.6f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 6, 8, 5, 0xe8c84a)
                .box("head", "body", 0, 6, -1, 5, 4, 5, 0xe8c84a)
                .box("shades", "head", 0, .5f, -2.6f, 5, 1.2f, .4f, 0x111111)
                .box("wingL", "body", -8, 2, 0, 10, 1, 5, 0xffffff)
                .box("wingR", "body", 8, 2, 0, 10, 1, 5, 0xffffff)
                .box("tail", "body", 0, -6, 2, 2, 5, 2, 0xc9a832))
            .attacks(
                a("glyde_star_waves", 70, (c, t) -> {
                    if (t >= 8 && t < 60 && t % 8 == 0) {
                        Vec3 from = c.eye(), d = c.aim(from), r = c.right();
                        for (int k = -2; k <= 2; k++) shot(c, from, d.add(r.scale(k * .18)), .4, .3f, .3f, .3f, 0xffe14d, 3, 40).spawn();
                        c.sound(SoundEvents.FIREWORK_ROCKET_LAUNCH, .6f, 1.4f);
                    }
                }, (p, t, m) -> p.r("wingL", 0, 0, sin(t, .5f) * .4f).r("wingR", 0, 0, -sin(t, .5f) * .4f).o("body", 0, sin(t, .25f), 0)),
                a("glyde_cool_stars", 80, (c, t) -> {
                    // big stars that burst into small ones (COOL! RAD! VIBIN!)
                    if (t == 10 || t == 35 || t == 60) {
                        Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 2, 2.5, c.rnd.nextGaussian() * 2);
                        c.proj().at(at).box(.9f, .9f, .9f).color(0xffe14d).dmg(0).warn(14).life(14).spawn();
                    }
                    if (t == 24 || t == 49 || t == 74) {
                        Vec3 at = c.tpos().add(0, 2.5, 0);
                        for (int k = 0; k < 10; k++) {
                            double ang = k * Math.PI / 5;
                            shot(c, at, new Vec3(Math.cos(ang), -.5, Math.sin(ang)), .3, .25f, .25f, .25f, 0xffe14d, 3, 30).spawn();
                        }
                        c.particle(ParticleTypes.FIREWORK, at, 20, .3, .15); c.sound(SoundEvents.FIREWORK_ROCKET_BLAST, 1, 1);
                    }
                }, (p, t, m) -> p.r("wingL", 0, 0, -.6f * env(t, 0, 8, 70, 80)).r("wingR", 0, 0, .6f * env(t, 0, 8, 70, 80)).r("head", -.3f, 0, 0))).build());

        // ======================= HOTLAND =======================
        out.add(CharacterDef.of("vulkin", "Vulkin", Faction.UNDERGROUND).hp(60).keep(6).speed(.2f).egg(0x6e6e6e, 0xff6a00).size(.9f, 1.2f)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 12, 8, 10, 0x6e6e6e)
                .box("head", "body", 0, 6, 0, 8, 4, 7, 0x7d7d7d)
                .glow("lava", "head", 0, 2.2f, 0, 6, .6f, 5, 0xff6a00)
                .box("eyeL", "body", -2.5f, 1, -5.1f, 2, .5f, .3f, 0x222222)
                .box("eyeR", "body", 2.5f, 1, -5.1f, 2, .5f, .3f, 0x222222)
                .box("blushL", "body", -4, -.5f, -5.1f, 1.5f, 1, .3f, 0xff9a8a)
                .box("blushR", "body", 4, -.5f, -5.1f, 1.5f, 1, .3f, 0xff9a8a))
            .attacks(
                a("vulkin_thundercloud", 70, (c, t) -> {
                    // cloud above fires bolts in a widening fan
                    Vec3 cloud = c.tpos().add(c.fwd().scale(-1)).add(0, 5, 0).add(c.right().scale(Math.sin(t * .08) * 2));
                    if (t == 0) c.proj().at(cloud).box(2.5f, .7f, 2f).color(0x9aa0b0).dmg(0).life(70).spawn();
                    if (t >= 12 && t < 64 && t % 8 == 0) {
                        for (int k = -2; k <= 2; k++) {
                            Vec3 d = c.tpos().subtract(cloud).normalize().add(c.right().scale(k * .15));
                            shot(c, cloud, d, .45, .12f, .12f, .9f, 0xfff38a, 3, 30).spawn();
                        }
                        c.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, .25f, 2f);
                        c.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0)); // canon: hits speed you up
                    }
                }, (p, t, m) -> p.o("lava", 0, Math.abs(sin(t, .7f)) * .6f, 0).r("body", 0, 0, sin(t, .3f) * .06f)),
                a("vulkin_fire_columns", 70, (c, t) -> {
                    // fireballs shoot in from both sides, then return to their column
                    Vec3 r = c.right();
                    if (t >= 10 && t < 50 && t % 6 == 0) for (int s = -1; s <= 1; s += 2) {
                        Vec3 from = c.tpos().add(r.scale(s * 5)).add(0, (c.rnd.nextDouble() - .5) * 1.5, 0);
                        c.proj().at(from).vel(r.scale(-s * .3)).box(.35f, .35f, .35f).color(0xff6a00).dmg(4).life(17).spawn();
                        c.proj().at(from.add(r.scale(-s * 5))).vel(r.scale(s * .3)).box(.35f, .35f, .35f).color(0xff9a30).dmg(4).warn(17).life(34).spawn();
                        c.particle(ParticleTypes.FLAME, from, 4, .2, .02);
                    }
                }, (p, t, m) -> p.o("body", 0, Math.abs(sin(t, .5f)) * .8f, 0))).build());

        out.add(CharacterDef.of("tsunderplane", "Tsunderplane", Faction.UNDERGROUND).hp(85).keep(7).speed(.35f).egg(0xdfe8f2, 0xff9ab2).size(1.2f, 1.2f)
            .model(new ModelDef()
                .box("body", null, 0, 8, 0, 5, 5, 14, 0xdfe8f2)
                .box("head", "body", 0, 0, -7, 4.5f, 4.5f, 2, 0xc8d4e2)
                .glow("eyeL", "head", -1.2f, .6f, -1.1f, .8f, 1.2f, .3f, 0x111111)
                .glow("eyeR", "head", 1.2f, .6f, -1.1f, .8f, 1.2f, .3f, 0x111111)
                .box("blush", "head", 0, -.8f, -1.1f, 3.5f, .6f, .3f, 0xff9ab2)
                .box("wingL", "body", -6, 0, 0, 9, .8f, 5, 0xdfe8f2)
                .box("wingR", "body", 6, 0, 0, 9, .8f, 5, 0xdfe8f2)
                .box("fin", "body", 0, 3, 6, 1, 4, 3, 0x9fb4cc))
            .attacks(
                a("tsunderplane_bomb_run", 80, (c, t) -> {
                    // planes cross from either side dropping bombs; each bomb leaves a vertical smoke column
                    if (t >= 8 && t < 60 && t % 12 == 0) {
                        double s = (t / 12) % 2 == 0 ? 1 : -1; Vec3 r = c.right();
                        Vec3 at = c.tpos().add(r.scale(-s * 6)).add(0, 3, 0);
                        c.proj().at(at).vel(r.scale(s * .4)).box(.5f, .3f, 1f).color(0xdfe8f2).dmg(3).life(30).spawn();
                        Vec3 bomb = c.tpos().add(r.scale(c.rnd.nextGaussian() * 1.5)).add(0, 3, 0);
                        mark(c, c.target.position().add(bomb.subtract(c.tpos()).multiply(1, 0, 1)), .6f, 0x555555, 12);
                        c.proj().at(bomb).vel(new Vec3(0, -.3, 0)).box(.3f, .3f, .3f).color(0x333333).dmg(3).warn(6).life(10).spawn();
                    }
                    if (t >= 20 && t < 72 && t % 12 == 8) {
                        Vec3 g = c.target.position().add(c.rnd.nextGaussian(), 0, c.rnd.nextGaussian());
                        c.proj().at(g.add(0, 1.5, 0)).box(.5f, 3f, .5f).color(0x9a9a9a).dmg(3).warn(6).life(18).spawn();
                        c.particle(ParticleTypes.LARGE_SMOKE, g.add(0, 1, 0), 10, .2, .05); c.sound(SoundEvents.GENERIC_EXPLODE, .4f, 1.6f);
                    }
                }, (p, t, m) -> p.r("body", 0, 0, sin(t, .3f) * .3f).r("wingL", 0, 0, sin(t, .3f) * .1f)),
                a("tsunderplane_smoke_trail", 70, (c, t) -> {
                    // big planes dive top->bottom leaving a sweeping smoke trail
                    if (t == 10 || t == 35) {
                        Vec3 at = c.tpos().add(c.right().scale(c.rnd.nextGaussian() * 2)).add(0, 6, 0);
                        c.proj().at(at).vel(new Vec3(0, -.45, 0)).box(1.2f, 1.2f, 2f).color(0xdfe8f2).dmg(5).warn(4).life(20).spawn();
                    }
                    if (t == 22 || t == 47) {
                        Vec3 r = c.right(); double s = t == 22 ? 1 : -1;
                        c.proj().at(c.tpos().add(r.scale(-s * 5))).vel(r.scale(s * .3)).box(.6f, 1.6f, 2.5f).color(0xaaaaaa).dmg(3).warn(6).life(36).spawn();
                    }
                }, (p, t, m) -> p.r("body", -.6f * env(t, 4, 10, 18, 26), 0, 0).r("head", 0, 0, sin(t, .4f) * .15f))).build());

        out.add(CharacterDef.of("pyrope", "Pyrope", Faction.UNDERGROUND).hp(110).keep(6).speed(.3f).egg(0xcc3a1f, 0xff9a1f).size(.7f, 1.8f)
            .model(new ModelDef()
                .box("body", null, 0, 8, 0, 3, 10, 3, 0xcc3a1f)
                .glow("head", "body", 0, 7, 0, 5, 5, 5, 0xff9a1f)
                .glow("flame", "head", 0, 3.5f, 0, 3, 3, 3, 0xffe14d)
                .glow("eyeL", "head", -1.2f, 0, -2.6f, 1, 1, .3f, 0x1a0a00)
                .glow("eyeR", "head", 1.2f, 0, -2.6f, 1, 1, .3f, 0x1a0a00)
                .limb("armL", "body", -2, 3, 0, 1.5f, 6, 1.5f, 0xcc3a1f)
                .limb("armR", "body", 2, 3, 0, 1.5f, 6, 1.5f, 0xcc3a1f)
                .limb("legL", null, -1, 5, 0, 1.5f, 5, 1.5f, 0xcc3a1f)
                .limb("legR", null, 1, 5, 0, 1.5f, 5, 1.5f, 0xcc3a1f))
            .attacks(
                a("pyrope_bouncing_bombs", 70, (c, t) -> {
                    // bombs drop in from all sides, bounce, explode
                    if (t >= 6 && t < 50 && t % 9 == 0) {
                        double ang = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = ring(c.tpos(), ang, 4, 2);
                        Vec3 d = c.tpos().subtract(at);
                        c.proj().at(at).vel(new Vec3(d.x * .06, .3, d.z * .06)).gravity(.04f).box(.4f, .4f, .4f).color(0x333333).dmg(3).life(20).spawn();
                    }
                    if (t >= 26 && t < 70 && t % 9 == 2) {
                        Vec3 b = c.target.position().add(c.rnd.nextGaussian(), .3, c.rnd.nextGaussian());
                        c.particle(ParticleTypes.EXPLOSION, b, 1, 0, 0); c.aoe(b, 1.3, 4, .4); c.sound(SoundEvents.GENERIC_EXPLODE, .5f, 1.5f);
                    }
                }, (p, t, m) -> p.r("armL", -2 * env(t, 0, 6, 50, 60), 0, .4f).r("armR", -2 * env(t, 0, 6, 50, 60), 0, -.4f).o("flame", 0, Math.abs(sin(t, .9f)), 0)),
                a("pyrope_burning_rope", 70, (c, t) -> {
                    // a burning rope sweeps across; the fire is ORANGE — keep moving through it
                    if (t == 10 || t == 38) {
                        Vec3 r = c.right(), f = c.fwd(); double s = t == 10 ? 1 : -1;
                        Vec3 at = c.target.position().add(r.scale(-s * 6)).add(0, .4, 0);
                        c.proj().at(at).vel(r.scale(s * .35)).face(f).box(.15f, .15f, 8f).color(0xcc3a1f).dmg(3).warn(8).life(36).spawn();
                        c.proj().at(at.add(0, .5, 0)).vel(r.scale(s * .35)).box(.6f, 1.4f, 1.6f).orange().dmg(6).warn(8).life(36).spawn();
                        c.sound(SoundEvents.FIRECHARGE_USE, 1, .8f);
                    }
                }, (p, t, m) -> { float sw = sin(t, .6f); p.r("armL", -1.5f + sw, 0, .6f).r("armR", -1.5f - sw, 0, -.6f).o("body", 0, Math.abs(sw), 0); })).build());

        out.add(rg("royal_guard_01", "Royal Guard 01", 0x3a3a3a, 0xf5f5f5, true));
        out.add(rg("royal_guard_02", "Royal Guard 02", 0x2c2c2c, 0x9adf6a, false));

        out.add(CharacterDef.of("so_sorry", "So Sorry", Faction.UNDERGROUND).hp(130).keep(6).speed(.3f).egg(0x9b62c8, 0x2b1a3a).size(.8f, 1.8f)
            .model(new ModelDef()
                .box("body", null, 0, 9, 0, 6, 9, 5, 0x9b62c8)
                .box("vest", "body", 0, 0, -2.6f, 5, 6, .4f, 0x5a3a20)
                .box("head", "body", 0, 7, -1, 6, 5, 7, 0x9b62c8)
                .box("beret", "head", 0, 3, 0, 6.5f, 1.2f, 6.5f, 0x2b1a3a)
                .glow("eyeL", "head", -1.5f, .5f, -3.6f, 1, 1, .3f, 0xffffff)
                .glow("eyeR", "head", 1.5f, .5f, -3.6f, 1, 1, .3f, 0xffffff)
                .limb("armL", "body", -4, 3, 0, 2, 6, 2, 0x9b62c8)
                .limb("armR", "body", 4, 3, 0, 2, 6, 2, 0x9b62c8)
                .box("pencil", "armR", 0, -6, -1, .6f, .6f, 4, 0xffd34d)
                .box("tail", null, 0, 4, 4, 2, 2, 7, 0x9b62c8)
                .limb("legL", null, -1.5f, 5, 0, 2, 5, 2, 0x9b62c8)
                .limb("legR", null, 1.5f, 5, 0, 2, 5, 2, 0x9b62c8))
            .attacks(soSorryTail(), soSorryPaper(), soSorryDoodlebogs(),
                a("so_sorry_last_minute", 90, below(.3f), (c, t) -> {
                    soSorryTail().tick(c, t % 70); if (t % 2 == 0) soSorryPaper().tick(c, t % 60);
                }, (p, t, m) -> p.r("body", 0, t * .3f, 0).r("armR", -1.4f, 0, -.4f).r("tail", 0, sin(t, .6f), 0))).build());

        out.add(CharacterDef.of("muffet", "Muffet", Faction.UNDERGROUND).hp(260).boss().keep(7).rest(14).speed(.3f)
            .egg(0x9a52c7, 0xd0364e).size(.8f, 1.9f)
            .model(new ModelDef()
                .box("body", null, 0, 9, 0, 6, 7, 4, 0x9a52c7)
                .box("dress", "body", 0, -4, 0, 10, 4, 7, 0xd0364e)
                .box("bloomers", "dress", 0, -2.5f, 0, 9, 1.5f, 6, 0xf2c6d6)
                .box("head", "body", 0, 7, 0, 7, 6, 6, 0x9a52c7)
                .glow("eye1", "head", -2, .5f, -3.1f, 1.2f, 1.2f, .3f, 0x2a0a20)
                .glow("eye2", "head", 2, .5f, -3.1f, 1.2f, 1.2f, .3f, 0x2a0a20)
                .glow("eye3", "head", -1, 2, -3.1f, 1, 1, .3f, 0x2a0a20)
                .glow("eye4", "head", 1, 2, -3.1f, 1, 1, .3f, 0x2a0a20)
                .glow("eye5", "head", 0, -.8f, -3.1f, 1, 1, .3f, 0x2a0a20)
                .box("fang", "head", 0, -2.2f, -3.1f, 2, .7f, .3f, 0xffffff)
                .box("tailL", "head", -5, -1, 0, 2.5f, 6, 2.5f, 0x1b1b2a)
                .box("tailR", "head", 5, -1, 0, 2.5f, 6, 2.5f, 0x1b1b2a)
                .box("bowL", "tailL", 0, 3, 0, 2, 1.5f, 3, 0xd0364e)
                .box("bowR", "tailR", 0, 3, 0, 2, 1.5f, 3, 0xd0364e)
                .limb("armL", "body", -4, 3, 0, 1.5f, 6, 1.5f, 0x9a52c7)
                .limb("armR", "body", 4, 3, 0, 1.5f, 6, 1.5f, 0x9a52c7)
                .limb("arm2L", "body", -4, 1, -1, 1.5f, 5, 1.5f, 0x9a52c7)
                .limb("arm2R", "body", 4, 1, -1, 1.5f, 5, 1.5f, 0x9a52c7)
                .limb("arm3L", "body", -4, -1, 0, 1.5f, 5, 1.5f, 0x9a52c7)
                .limb("arm3R", "body", 4, -1, 0, 1.5f, 5, 1.5f, 0x9a52c7)
                .limb("legL", null, -1.5f, 4, 0, 1.5f, 4, 1.5f, 0x9a52c7)
                .limb("legR", null, 1.5f, 4, 0, 1.5f, 4, 1.5f, 0x9a52c7))
            .attacks(muffetSpiders(), muffetCroissants(), muffetDonuts(), muffetTea(), muffetCupcake(), muffetFinale()).build());

        out.add(CharacterDef.of("mettaton", "Mettaton", Faction.ROYAL_SCIENCE).hp(160).boss().keep(7).rest(16).speed(.3f)
            .egg(0x6b6b6b, 0xffd23a).size(.8f, 2f)
            .model(new ModelDef()
                .box("body", null, 0, 13, 0, 10, 14, 6, 0x6b6b6b)
                .box("head", "body", 0, 5, -3.1f, 8, 3, .3f, 0x1a1a1a)
                .glow("light1", "head", -2.5f, 0, -.2f, 1.5f, 1.5f, .2f, 0xffd23a)
                .glow("light2", "head", 0, 0, -.2f, 1.5f, 1.5f, .2f, 0xff4040)
                .glow("light3", "head", 2.5f, 0, -.2f, 1.5f, 1.5f, .2f, 0x40c0ff)
                .box("grid", "body", 0, -2, -3.1f, 8, 6, .3f, 0x2a2a2a)
                .box("dial", "body", 0, -6, -3.1f, 3, 1, .3f, 0xffd23a)
                .limb("armL", "body", -6, 5, 0, 1.5f, 7, 1.5f, 0x4a4a4a)
                .limb("armR", "body", 6, 5, 0, 1.5f, 7, 1.5f, 0x4a4a4a)
                .box("wheelpost", null, 0, 3, 0, 2, 5, 2, 0x4a4a4a)
                .box("wheel", "wheelpost", 0, -2, 0, 1.5f, 3, 3, 0x1a1a1a))
            .attacks(mttLasers(), mttMiniBombs(), mttQuiz(), mttNewsBombs(), mttCookingFire()).build());

        out.add(CharacterDef.of("mettaton_ex", "Mettaton EX", Faction.ROYAL_SCIENCE).hp(320).boss().keep(7).rest(12).speed(.34f)
            .egg(0x1b1b1b, 0xff3b6b).size(.7f, 2.4f).scale(1.15f)
            .model(new ModelDef()
                .box("body", null, 0, 15, 0, 6, 9, 4, 0xbfbfbf)
                .box("chest", "body", 0, 2, -2.1f, 5, 3, .3f, 0x1b1b1b)
                .glow("heart", "chest", 0, 0, -.2f, 2, 2, .3f, 0xff3b6b)
                .box("belt", "body", 0, -4, 0, 6.5f, 1.2f, 4.5f, 0x1b1b1b)
                .box("head", "body", 0, 7.5f, 0, 5, 5, 5, 0xd9d9d9)
                .box("hair", "head", -1, 1.5f, -.5f, 6, 3.5f, 6, 0x1b1b1b)
                .box("bang", "head", -1.5f, -.5f, -2.8f, 3, 4, .6f, 0x1b1b1b)
                .glow("eye", "head", 1.4f, 0, -2.6f, 1, 1, .3f, 0xff3b6b)
                .box("shoulderL", "body", -3.8f, 4, 0, 2, 1.5f, 3, 0xff3b6b)
                .box("shoulderR", "body", 3.8f, 4, 0, 2, 1.5f, 3, 0xff3b6b)
                .limb("armL", "body", -4, 3, 0, 1.6f, 9, 1.6f, 0x1b1b1b)
                .limb("armR", "body", 4, 3, 0, 1.6f, 9, 1.6f, 0x1b1b1b)
                .limb("legL", null, -1.5f, 10.5f, 0, 2, 10.5f, 2, 0x1b1b1b)
                .limb("legR", null, 1.5f, 10.5f, 0, 2, 10.5f, 2, 0x1b1b1b)
                .box("bootL", "legL", 0, -10, -.5f, 2.2f, 1, 3, 0xff3b6b)
                .box("bootR", "legR", 0, -10, -.5f, 2.2f, 1, 3, 0xff3b6b))
            .attacks(exLegs(), exMiniMettatons(), exArms(), exHeartBomb(), exBoxRain(), exDiscoLasers(), exRatingsFinale()).build());

        out.add(CharacterDef.of("alphys", "Alphys", Faction.ROYAL_SCIENCE).att(Attitude.FRIENDLY).hp(70).keep(8).speed(.24f)
            .egg(0xe9c74b, 0xf5f5f5).size(.7f, 1.5f)
            .model(new ModelDef()
                .box("body", null, 0, 7, 0, 8, 8, 6, 0xf5f5f5)
                .box("belly", "body", 0, -1, -3.1f, 5, 5, .3f, 0xe9c74b)
                .box("head", "body", 0, 6.5f, -1, 7, 5, 7, 0xe9c74b)
                .box("glasses", "head", 0, .6f, -3.6f, 6, 1.5f, .3f, 0xdfefff)
                .box("teeth", "head", 0, -1.5f, -3.6f, 3, .6f, .3f, 0xffffff)
                .limb("armL", "body", -5, 3, 0, 2, 5, 2, 0xf5f5f5)
                .limb("armR", "body", 5, 3, 0, 2, 5, 2, 0xf5f5f5)
                .box("phone", "armR", 0, -5, -.5f, 1.2f, 2, .5f, 0xff8ac0)
                .box("tail", null, 0, 2, 3, 2.5f, 2.5f, 5, 0xe9c74b)
                .limb("legL", null, -2, 3, 0, 2.5f, 3, 2.5f, 0xe9c74b)
                .limb("legR", null, 2, 3, 0, 2.5f, 3, 2.5f, 0xe9c74b))
            .attacks(
                a("alphys_lab_lasers", 70, (c, t) -> {
                    // remote-controlled blue/orange lasers (her Hotland puzzles)
                    if (t == 12 || t == 36) {
                        Vec3 r = c.right(), f = c.fwd(); double s = t == 12 ? 1 : -1;
                        UtProjectile.B b = c.proj().at(c.tpos().add(r.scale(-s * 6))).vel(r.scale(s * .3)).face(f).box(.25f, 3, 8).warn(8).dmg(5).life(44);
                        (t == 12 ? b.blue() : b.orange()).spawn();
                        c.sound(SoundEvents.BEACON_ACTIVATE, .6f, 1.8f);
                    }
                }, (p, t, m) -> p.r("armR", -1.3f * env(t, 0, 8, 60, 70), 0, -.3f).r("head", .3f * env(t, 0, 8, 60, 70), 0, 0)),
                a("alphys_mini_drones", 60, (c, t) -> {
                    if (t >= 10 && t < 50 && t % 10 == 0) {
                        Vec3 from = c.eye().add(c.right().scale(c.rnd.nextGaussian())).add(0, .6, 0);
                        tele(c, from, c.tpos(), 0xffd23a, 8);
                        shot(c, from, c.aim(from), .4, .3f, .4f, .2f, 0xff3b6b, 3, 30).warn(8).spawn();
                    }
                }, (p, t, m) -> p.r("armR", -1.6f, 0, -.2f).r("armL", -.8f, 0, .3f).o("body", 0, Math.abs(sin(t, .7f)) * .3f, 0))).build());

        out.add(CharacterDef.of("burgerpants", "Burgerpants", Faction.UNDERGROUND).att(Attitude.FRIENDLY).hp(45).keep(6).speed(.27f)
            .egg(0xe59a3a, 0x7a3fa0).size(.6f, 1.8f)
            .model(new ModelDef()
                .box("body", null, 0, 9, 0, 6, 8, 4, 0x7a3fa0)
                .box("apron", "body", 0, -1, -2.1f, 4.5f, 6, .3f, 0xf5f5f5)
                .box("head", "body", 0, 6.5f, 0, 6, 5, 5, 0xe59a3a)
                .box("hat", "head", 0, 3, 0, 5, 1.5f, 5, 0xf5f5f5)
                .box("earL", "head", -2.2f, 3, 0, 1.4f, 2, 1, 0xe59a3a)
                .box("earR", "head", 2.2f, 3, 0, 1.4f, 2, 1, 0xe59a3a)
                .glow("eyeL", "head", -1.4f, .3f, -2.6f, 1, .6f, .3f, 0x111111)
                .glow("eyeR", "head", 1.4f, .3f, -2.6f, 1, .6f, .3f, 0x111111)
                .limb("armL", "body", -4, 3, 0, 1.6f, 6, 1.6f, 0x7a3fa0)
                .limb("armR", "body", 4, 3, 0, 1.6f, 6, 1.6f, 0x7a3fa0)
                .limb("legL", null, -1.5f, 5, 0, 2, 5, 2, 0x3a2a50)
                .limb("legR", null, 1.5f, 5, 0, 2, 5, 2, 0x3a2a50))
            .attacks(
                a("burgerpants_glamburger_toss", 60, (c, t) -> {
                    if (t == 14 || t == 28 || t == 42) {
                        Vec3 from = c.eye().add(c.right().scale(.4)); Vec3 d = c.tpos().subtract(from);
                        c.proj().at(from).vel(new Vec3(d.x * .055, .35, d.z * .055)).gravity(.04f).box(.45f, .3f, .45f).color(0xc8843a).dmg(3).life(30).spawn();
                        c.proj().at(from.add(0, .2, 0)).vel(new Vec3(d.x * .055, .35, d.z * .055)).gravity(.04f).box(.5f, .1f, .5f).color(0xffd23a).dmg(0).life(30).spawn();
                        c.sound(SoundEvents.SNOWBALL_THROW, .8f, .8f);
                    }
                }, (p, t, m) -> { float w = env((t - 4) % 14, 0, 8, 9, 13); p.r("armR", -2.6f * w, 0, -.2f).r("head", .2f, 0, 0); }),
                a("burgerpants_starfait_spill", 50, (c, t) -> {
                    if (t == 18) { burst(c, c.eye(), 8, .3, .3f, 0xff9ad9, 2, 26, 0); c.sound(SoundEvents.BOTTLE_EMPTY, 1, 1.3f); }
                    if (t == 30) c.particle(ParticleTypes.ANGRY_VILLAGER, c.eye().add(0, .6, 0), 3, .2, 0);
                }, (p, t, m) -> p.r("armL", -1.6f * env(t, 0, 14, 18, 26), 0, .4f).r("armR", -1.6f * env(t, 0, 14, 18, 26), 0, -.4f).r("head", .4f * up(t, 26, 34), 0, 0))).build());
    }

    // ======================= shared models =======================
    static ModelDef knight(int armor, int trim, int skin, int hair) {
        return new ModelDef()
            .box("body", null, 0, 13, 0, 7, 10, 4.5f, armor)
            .box("chest", "body", 0, 2, -2.4f, 5, 3, .4f, trim)
            .box("pauldronL", "body", -4.8f, 4, 0, 3.5f, 2.5f, 5, trim)
            .box("pauldronR", "body", 4.8f, 4, 0, 3.5f, 2.5f, 5, trim)
            .box("head", "body", 0, 8, -.5f, 5.5f, 6, 5.5f, skin)
            .box("hair", "head", 0, 2.5f, 2.5f, 4.5f, 6, 3, hair)
            .box("finL", "head", -3.2f, .5f, 0, .6f, 3, 2, 0x2f7fa8)
            .box("finR", "head", 3.2f, .5f, 0, .6f, 3, 2, 0x2f7fa8)
            .box("patch", "head", -1.3f, .6f, -2.8f, 1.8f, 1.5f, .3f, 0x111111)
            .glow("eye", "head", 1.3f, .6f, -2.8f, 1.2f, 1.2f, .3f, 0xffe14d)
            .box("teeth", "head", 0, -1.6f, -2.8f, 3, .6f, .3f, 0xffffff)
            .limb("armL", "body", -4.5f, 4, 0, 2.2f, 9, 2.2f, armor)
            .limb("armR", "body", 4.5f, 4, 0, 2.2f, 9, 2.2f, armor)
            .glow("spear", "armR", 0, -8, -3, .5f, .5f, 10, SPEAR)
            .limb("legL", null, -1.8f, 8, 0, 2.5f, 8, 2.5f, armor)
            .limb("legR", null, 1.8f, 8, 0, 2.5f, 8, 2.5f, armor);
    }
    static ModelDef dummy(int cloth, int stitch) {
        return new ModelDef()
            .box("body", null, 0, 10, 0, 6, 9, 4, cloth)
            .box("seam", "body", 0, 0, -2.1f, .4f, 8, .3f, stitch)
            .box("head", "body", 0, 7, 0, 6, 6, 5, cloth)
            .glow("eyeL", "head", -1.4f, .5f, -2.6f, 1.6f, .6f, .3f, 0xff2020)
            .glow("eyeR", "head", 1.4f, .5f, -2.6f, 1.6f, .6f, .3f, 0xff2020)
            .box("mouth", "head", 0, -1.6f, -2.6f, 3, .8f, .3f, 0x2a1a10)
            .limb("armL", "body", -4, 3, 0, 1.6f, 5, 1.6f, cloth)
            .limb("armR", "body", 4, 3, 0, 1.6f, 5, 1.6f, cloth)
            .box("pole", null, 0, 3, 0, 1.2f, 6, 1.2f, 0x7a5c3e)
            .box("stand", "pole", 0, -3, 0, 5, .8f, 5, 0x5a4030);
    }

    // ======================= Undyne =======================
    /** Green soul: target is rooted; spears converge from `dirs` directions in sequence, telegraphed by lines. */
    static Attack greenSpears(String id, int dirs, int gap, float dmg, Predicate<UtMob> use) {
        return a(id, 20 + gap * 10, use, (c, t) -> {
            if (t == 0) { root(c, 20 + gap * 10, 6); c.sound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1, 1.4f); c.particle(ParticleTypes.HAPPY_VILLAGER, c.tpos(), 20, .4, .05); }
            if (t >= 8 && t < 8 + gap * 9 && (t - 8) % gap == 0) {
                int k = c.rnd.nextInt(dirs); double ang = k * Math.PI * 2 / dirs + c.target.getYRot() * Math.PI / 180;
                Vec3 at = ring(c.tpos(), ang, 7, 0);
                tele(c, at, c.tpos(), SPEAR, gap + 2);
                c.proj().at(at).vel(c.aim(at).scale(.5)).box(.14f, .14f, 1.6f).color(SPEAR).dmg(dmg).warn(gap + 2).life(gap + 18).spawn();
                c.sound(SoundEvents.TRIDENT_THROW, .8f, 1.2f);
            }
        }, (p, t, m) -> {
            float f = env(t, 0, 6, 8 + 9 * 6, 20 + 9 * 6);
            p.r("armR", -2.8f * f + sin(t, .8f) * .3f * f, 0, -.2f).r("armL", -.8f * f, 0, .4f * f).r("head", -.15f * f, 0, 0);
        });
    }
    /** Yellow spears that spin around at the last moment and come from the opposite side. */
    static Attack reverseSpears(String id, float dmg, Predicate<UtMob> use) {
        return a(id, 80, use, (c, t) -> {
            if (t == 0) root(c, 80, 6);
            if (t >= 8 && t < 64 && t % 8 == 0) {
                double ang = c.rnd.nextInt(4) * Math.PI / 2 + c.target.getYRot() * Math.PI / 180;
                Vec3 at = ring(c.tpos(), ang, 7, 0), opp = ring(c.tpos(), ang + Math.PI, 2.5, 0);
                c.proj().at(at).vel(c.aim(at).scale(.45)).box(.14f, .14f, 1.6f).color(YELLOW).dmg(0).life(10).spawn();
                tele(c, opp, c.tpos(), YELLOW, 10);
                c.proj().at(opp).vel(c.aim(opp).scale(.25)).box(.14f, .14f, 1.6f).color(YELLOW).dmg(dmg).warn(10).life(22).spawn();
                c.particle(ParticleTypes.ELECTRIC_SPARK, opp, 6, .2, .05);
            }
        }, (p, t, m) -> p.r("armR", -2.6f, sin(t, .4f) * .5f, -.2f).r("armL", -1.0f, 0, .3f).r("body", 0, sin(t, .2f) * .15f, 0));
    }
    /** Spears materialize in a fan behind Undyne, hang, then launch in a staggered volley. */
    static Attack spearVolley(String id, int n, int stagger, float dmg, Predicate<UtMob> use) {
        return a(id, 30 + n * stagger + 10, use, (c, t) -> {
            if (t == 6) for (int k = 0; k < n; k++) {
                Vec3 at = c.eye().add(c.right().scale((k - (n - 1) / 2.0) * .9)).add(0, 1 + Math.abs(k - (n - 1) / 2.0) * -.15, 0);
                c.proj().at(at).face(c.aim(at)).box(.14f, .14f, 1.6f).color(SPEAR).dmg(0).warn(20 + k * stagger).life(20 + k * stagger).spawn();
            }
            int k = (t - 26) / stagger;
            if (t >= 26 && (t - 26) % stagger == 0 && k < n) {
                Vec3 at = c.eye().add(c.right().scale((k - (n - 1) / 2.0) * .9)).add(0, 1 + Math.abs(k - (n - 1) / 2.0) * -.15, 0);
                shot(c, at, c.aim(at), .7, .14f, .14f, 1.6f, SPEAR, dmg, 30).spawn();
                c.sound(SoundEvents.TRIDENT_THROW, 1, 1 + k * .05f);
            }
        }, (p, t, m) -> {
            float w = up(t, 0, 20), s = up(t, 24, 30);
            p.r("armR", -3f * w + 2.2f * s, 0, -.3f).r("body", 0, .4f * w - .6f * s, 0).o("body", 0, 0, 0).r("head", .1f * s, 0, 0);
        });
    }
    /** Red soul: blue warning tiles light up around you, then spears erupt from the ground. */
    static Attack spearRise(String id, int waves, int gap, float dmg, Predicate<UtMob> use) {
        return a(id, 14 + waves * (gap + 8), use, (c, t) -> {
            if (t >= 6 && (t - 6) % (gap + 8) == 0 && (t - 6) / (gap + 8) < waves) {
                Vec3 g0 = c.target.position();
                for (int k = 0; k < 7; k++) {
                    Vec3 g = k == 0 ? g0 : g0.add(c.rnd.nextGaussian() * 2.2, 0, c.rnd.nextGaussian() * 2.2);
                    mark(c, g, .9f, SPEAR, gap);
                    c.proj().at(g.add(0, -1.2, 0)).vel(new Vec3(0, .32, 0)).face(new Vec3(0, 1, 0)).box(.18f, .18f, 2.2f).color(SPEAR).dmg(dmg).warn(gap).life(gap + 12).spawn();
                }
                c.sound(SoundEvents.TRIDENT_RETURN, 1, 1.4f);
            }
        }, (p, t, m) -> {
            float s = Math.abs(sin(t, (float) Math.PI / 16));
            p.r("armR", -2.6f + 2.4f * s, 0, -.2f).r("armL", -1.4f + 1.2f * s, 0, .2f).o("body", 0, -.8f * s, 0);
        });
    }
    /** Spears rain diagonally from the sky onto the target area. */
    static Attack spearRain(String id, float dmg, Predicate<UtMob> use) {
        return a(id, 70, use, (c, t) -> {
            if (t >= 10 && t < 58 && t % 3 == 0) {
                Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 2.5, 0, c.rnd.nextGaussian() * 2.5);
                Vec3 from = g.add(c.fwd().scale(-3)).add(0, 7, 0);
                tele(c, from, g, SPEAR, 6);
                c.proj().at(from).vel(g.subtract(from).normalize().scale(.6)).box(.14f, .14f, 1.6f).color(SPEAR).dmg(dmg).warn(6).life(20).spawn();
            }
            if (t == 10) c.sound(SoundEvents.TRIDENT_THUNDER, .6f, 1.6f);
        }, (p, t, m) -> {
            float f = env(t, 0, 10, 58, 68);
            p.r("armR", -3.1f * f, 0, -.1f).r("armL", -3.1f * f, 0, .1f).r("head", -.4f * f, 0, 0).o("body", 0, .6f * f, 0);
        });
    }
    static Attack undyingSpiral() {
        return a("undying_spear_spiral", 80, (c, t) -> {
            if (t >= 8 && t < 64 && t % 3 == 0) {
                double ang = t * .55; Vec3 at = ring(c.tpos(), ang, 6.5, Math.sin(t * .2) * .6);
                c.proj().at(at).vel(c.aim(at).scale(.42)).box(.14f, .14f, 1.6f).color(SPEAR).dmg(6).warn(6).life(26).spawn();
                c.particle(ParticleTypes.ELECTRIC_SPARK, at, 2, .1, 0);
            }
        }, (p, t, m) -> p.r("armR", -2.8f, t * .4f, -.3f).r("body", 0, t * .2f * up(t, 0, 8) * (1 - up(t, 64, 80)), 0));
    }
    /** Walls of spears with a single gap sweep toward the target from alternating sides. */
    static Attack undyingWalls() {
        return a("undying_spear_walls", 80, (c, t) -> {
            if (t >= 8 && t < 64 && t % 14 == 0) {
                Vec3 r = c.right(), f = c.fwd(); double s = (t / 14) % 2 == 0 ? 1 : -1; int gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) {
                    if (Math.abs(k - gap) < 1) continue;
                    Vec3 at = c.tpos().add(r.scale(-s * 7)).add(f.scale(k * .9));
                    c.proj().at(at).vel(r.scale(s * .5)).box(.14f, .14f, 1.6f).color(SPEAR).dmg(6).warn(8).life(32).spawn();
                }
                c.sound(SoundEvents.TRIDENT_THROW, 1, .9f);
            }
        }, (p, t, m) -> { float sw = sin(t, (float) Math.PI / 14); p.r("armR", -1.6f, sw * 1.4f, -.3f).r("body", 0, sw * .3f, 0); });
    }
    /** Final turn: a ring of spears closes in, giant spears cross from above, then everything at once. */
    static Attack spearOfJustice() {
        return a("undying_spear_of_justice", 110, below(.3f), (c, t) -> {
            if (t == 0) { root(c, 40, 6); c.sound(SoundEvents.WITHER_SPAWN, .6f, 1.6f); }
            if (t < 40 && t % 10 == 4) for (int k = 0; k < 12; k++) {
                Vec3 at = ring(c.tpos(), k * Math.PI / 6 + t, 6, 0);
                c.proj().at(at).vel(c.aim(at).scale(.35)).box(.14f, .14f, 1.6f).color(SPEAR).dmg(6).warn(8).life(24).spawn();
            }
            if (t == 46 || t == 60) {
                Vec3 g = c.tpos(); Vec3 from = g.add(c.right().scale(t == 46 ? 6 : -6)).add(0, 8, 0);
                tele(c, from, g, SPEAR, 10);
                c.proj().at(from).vel(g.subtract(from).normalize().scale(.9)).box(.6f, .6f, 6f).color(SPEAR).dmg(10).warn(10).life(24).pierce().spawn();
            }
            if (t >= 70 && t < 104 && t % 2 == 0) {
                Vec3 at = ring(c.tpos(), c.rnd.nextDouble() * Math.PI * 2, 7, c.rnd.nextGaussian() * .5);
                shot(c, at, c.aim(at), .55, .14f, .14f, 1.6f, c.rnd.nextInt(4) == 0 ? YELLOW : SPEAR, 6, 20).spawn();
            }
        }, (p, t, m) -> {
            float f = env(t, 40, 46, 60, 70);
            p.r("armR", -3f, 0, -.2f).r("armL", -3f * f, 0, .2f).o("body", 0, 2 * f, 0).r("head", -.3f, 0, 0).r("spear", 0, t * .5f, 0);
        });
    }

    // ======================= Mad Dummy / Mad Mew Mew =======================
    /** Dummy minions ring the target and fire magic bullets inward (canon: bullets can hit the boss too). */
    static Attack madDummyBullets(String id, int col, Predicate<UtMob> use) {
        return a(id, 80, use, (c, t) -> {
            if (t == 4) for (int k = 0; k < 4; k++) c.proj().at(ring(c.tpos(), k * Math.PI / 2, 5, -.3)).box(.6f, 1.2f, .5f).color(0xd8cdb8).dmg(0).life(70).spawn();
            if (t >= 14 && t < 70 && t % 7 == 0) for (int k = 0; k < 4; k++) {
                Vec3 at = ring(c.tpos(), k * Math.PI / 2, 5, 0);
                shot(c, at, c.aim(at), .3, .25f, .25f, .25f, col, 3, 30).warn(4).spawn();
            }
            if (t % 14 == 0) c.sound(SoundEvents.SHULKER_SHOOT, .6f, 1.4f);
        }, (p, t, m) -> p.r("body", 0, 0, sin(t, .6f) * .25f).r("armL", -2f, 0, sin(t, 1.2f) * .5f).r("armR", -2f, 0, -sin(t, 1.2f) * .5f).o("head", 0, Math.abs(sin(t, .9f)) * .6f, 0));
    }
    /** Robot dummies launch homing-ish missiles: each one rises, hesitates, then dives at the target. */
    static Attack madDummyMissiles(String id, Predicate<UtMob> use) {
        return a(id, 80, use, (c, t) -> {
            if (t >= 8 && t < 50 && t % 8 == 0) {
                Vec3 from = c.pos().add(c.right().scale(c.rnd.nextGaussian() * 2)).add(0, 1, 0);
                c.proj().at(from).vel(new Vec3(0, .25, 0)).box(.3f, .3f, .7f).color(0xa0a0a0).dmg(0).life(10).spawn();
                c.sound(SoundEvents.FIREWORK_ROCKET_LAUNCH, .7f, .8f);
            }
            if (t >= 18 && t < 60 && t % 8 == 2) {
                Vec3 from = c.pos().add(0, 3.5, 0).add(c.right().scale(c.rnd.nextGaussian() * 2));
                tele(c, from, c.tpos(), 0xff4040, 6);
                c.proj().at(from).vel(c.aim(from).scale(.5)).box(.3f, .3f, .7f).color(0xa0a0a0).dmg(5).warn(6).life(30).spawn();
                c.particle(ParticleTypes.SMOKE, from, 6, .1, .02);
            }
        }, (p, t, m) -> p.r("armR", -2.8f, 0, -.3f).r("head", 0, sin(t, .4f) * .3f, 0).o("body", 0, sin(t, 1.6f) * .2f, 0));
    }
    static Attack madKnives(String id, int col, Predicate<UtMob> use) {
        return a(id, 70, use, (c, t) -> {
            if (t >= 10 && t < 60 && t % 6 == 0) {
                Vec3 from = c.eye(), d = c.aim(from), r = c.right();
                for (int k = -2; k <= 2; k++) shot(c, from, d.add(r.scale(k * .14 + Math.sin(t) * .05)), .55, .1f, .3f, .9f, col, 4, 30).spawn();
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .8f, 1.5f);
            }
        }, (p, t, m) -> { float s = env((t - 4) % 6, 0, 3, 3, 6); p.r("armR", -2.4f * s, 0, -.6f * s).r("armL", -2.4f * (1 - s), 0, .6f * (1 - s)).r("body", 0, 0, sin(t, .5f) * .2f); });
    }
    /** Dummies rain from the sky in droves. */
    static Attack madFinale(String id, int col, Predicate<UtMob> use) {
        return a(id, 90, use, (c, t) -> {
            if (t >= 10 && t < 80 && t % 2 == 0) drop(c, 3, .6f, 1.1f, col, 4, .5);
            if (t % 15 == 10) c.sound(SoundEvents.ARMOR_STAND_FALL, 1, .8f);
        }, (p, t, m) -> p.r("armL", -3f, 0, .3f).r("armR", -3f, 0, -.3f).o("body", 0, Math.abs(sin(t, .5f)) * 2, 0).r("head", -.3f, sin(t, 1.1f) * .3f, 0));
    }

    // ======================= Royal Guards =======================
    static CharacterDef rg(String id, String name, int armor, int accent, boolean bunny) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 14, 0, 9, 11, 6, armor)
            .box("emblem", "body", 0, 2, -3.1f, 3, 3, .3f, accent)
            .box("head", "body", 0, 8.5f, 0, 7, 6, 7, armor)
            .glow("visor", "head", 0, 0, -3.6f, 5, 1, .3f, bunny ? 0xff4040 : 0x9adf6a)
            .limb("armL", "body", -6, 4, 0, 3, 10, 3, armor)
            .limb("armR", "body", 6, 4, 0, 3, 10, 3, armor)
            .box("sword", "armR", 0, -9, -3, .6f, 1, 8, 0xd8d8e0)
            .limb("legL", null, -2.2f, 8, 0, 3.5f, 8, 3.5f, armor)
            .limb("legR", null, 2.2f, 8, 0, 3.5f, 8, 3.5f, armor);
        if (bunny) m.box("earL", "head", -2, 5, 0, 1.5f, 5, 1.5f, armor).box("earR", "head", 2, 5, 0, 1.5f, 5, 1.5f, armor);
        else m.box("horn", "head", 0, 3.5f, -1, 1.5f, 2.5f, 1.5f, armor).box("snout", "head", 0, -1, -4.5f, 4, 2.5f, 2, armor);
        return CharacterDef.of(id, name, Faction.ROYAL_GUARD).hp(140).keep(6).speed(.28f).egg(armor, accent).size(.9f, 2.4f).scale(1.1f).model(m)
            .attacks(
                a(id + "_drops", 70, (c, t) -> {
                    // projectiles fall from above in columns (Vegetoid-style)
                    if (t >= 8 && t < 60 && t % 4 == 0) drop(c, 2.5, .35f, .7f, bunny ? 0xffffff : 0x9adf6a, 4, .45);
                }, (p, t, mm) -> p.r("armR", -2.8f * env(t, 0, 8, 60, 70), 0, -.2f).r("armL", -2.8f * env(t, 0, 8, 60, 70), 0, .2f)),
                a(id + "_stars", 70, (c, t) -> {
                    // star bullets fly vertically from below and above
                    if (t >= 8 && t < 60 && t % 5 == 0) {
                        Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 2, 0, c.rnd.nextGaussian() * 2);
                        boolean fromTop = (t / 5) % 2 == 0;
                        mark(c, g, .5f, YELLOW, 8);
                        c.proj().at(g.add(0, fromTop ? 6 : -1, 0)).vel(new Vec3(0, fromTop ? -.5 : .4, 0)).box(.35f, .35f, .35f).color(YELLOW).dmg(4).warn(8).life(22).spawn();
                    }
                }, (p, t, mm) -> p.r("armR", -1.5f + sin(t, .4f) * .8f, 0, -.3f).r("body", 0, sin(t, .2f) * .2f, 0)),
                a(id + "_sword_slash", 50, (c, t) -> {
                    if (t == 16) {
                        Vec3 f = c.fwd(); c.dash(f.scale(1.1).add(0, .2, 0));
                        c.proj().at(c.eye().add(f.scale(1.6))).face(c.right()).box(.3f, .3f, 3.5f).color(0xd8d8e0).dmg(0).life(6).spawn();
                    }
                    if (t == 19) { c.aoe(c.pos().add(c.fwd().scale(1.5)).add(0, 1, 0), 2.2, 7, 1); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .6f); c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(c.fwd().scale(1.5)).add(0, 1, 0), 3, .4, 0); }
                }, (p, t, mm) -> {
                    float w = up(t, 0, 14), s = up(t, 16, 20), r = 1 - up(t, 30, 46);
                    p.r("armR", (-2.6f * w + 3.4f * s) * r, (-1f * w + 1.6f * s) * r, -.3f).r("body", 0, (.5f * w - .9f * s) * r, 0);
                })).build();
    }

    // ======================= So Sorry =======================
    static Attack soSorryTail() {
        return a("so_sorry_tail_swing", 70, (c, t) -> {
            // turns around to dig in his vest; his tail sweeps through in blue and orange
            if (t == 12 || t == 40) {
                Vec3 r = c.right(); double s = t == 12 ? 1 : -1;
                UtProjectile.B b = c.proj().at(c.tpos().add(r.scale(-s * 5))).vel(r.scale(s * .35)).face(c.fwd()).box(.5f, 2.2f, 4f).dmg(5).warn(6).life(30);
                (t == 12 ? b.blue() : b.orange()).spawn();
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .5f);
            }
        }, (p, t, m) -> p.r("body", 0, (float) Math.PI * env(t, 0, 10, 56, 66), 0).r("tail", 0, sin(t, (float) Math.PI / 14) * 1.2f, 0).r("armL", -1f, 0, .5f));
    }
    static Attack soSorryPaper() {
        return a("so_sorry_crumpled_paper", 60, (c, t) -> {
            if (t >= 8 && t < 54 && t % 2 == 0) {
                Vec3 from = c.eye().add(c.right().scale(c.rnd.nextGaussian() * .5));
                Vec3 d = c.aim(from).add(c.rnd.nextGaussian() * .15, c.rnd.nextGaussian() * .1, c.rnd.nextGaussian() * .15);
                shot(c, from, d, .35 + c.rnd.nextDouble() * .15, .3f, .3f, .3f, 0xf5f2e6, 2, 30).spawn();
            }
            if (t % 8 == 0) c.sound(SoundEvents.BOOK_PAGE_TURN, .9f, 1.2f);
        }, (p, t, m) -> { float s = sin(t, 1.5f); p.r("armR", -1.5f + s * .8f, 0, -.3f).r("armL", -1.5f - s * .8f, 0, .3f).r("head", .2f, 0, 0); });
    }
    static Attack soSorryDoodlebogs() {
        return a("so_sorry_doodlebogs", 80, (c, t) -> {
            // he accidentally draws Doodlebogs, which throw doodles in circles
            if (t >= 10 && t < 20 && t % 5 == 0) {
                Vec3 at = ring(c.tpos(), c.rnd.nextDouble() * Math.PI * 2, 4, .5);
                c.proj().at(at).box(.8f, 1f, .2f).color(0x111111).dmg(0).warn(60).life(60).spawn();
            }
            if (t >= 24 && t < 72 && t % 8 == 0) {
                Vec3 at = ring(c.tpos(), t * .7, 4, .5);
                burst(c, at, 8, .22, .2f, 0x222222, 3, 30, t * .2);
            }
        }, (p, t, m) -> p.r("armR", -1.2f - sin(t, 1.8f) * .3f, sin(t, 1.2f) * .5f, -.2f).r("head", .35f, 0, 0).o("pencil", 0, 0, sin(t, 1.8f) * .5f));
    }

    // ======================= Muffet =======================
    static void purple(AttackCtx c, int ticks) { root(c, ticks, 2); }
    static Attack muffetSpiders() {
        return a("muffet_spider_lanes", 80, (c, t) -> {
            // purple soul: you're stuck on three web strands; spiders scurry down the strands
            Vec3 r = c.right(), f = c.fwd();
            if (t == 0) { purple(c, 80); for (int k = -1; k <= 1; k++) c.proj().at(c.tpos().add(r.scale(k * 1.4)).add(0, -.8, 0)).face(f).box(.05f, .05f, 14).color(PURPLE).dmg(0).life(78).spawn(); }
            if (t >= 10 && t < 70 && t % 5 == 0) {
                int lane = c.rnd.nextInt(3) - 1;
                Vec3 at = c.tpos().add(r.scale(lane * 1.4)).add(f.scale(7)).add(0, -.6, 0);
                c.proj().at(at).vel(f.scale(-.4)).box(.4f, .25f, .4f).color(0x2a0a3a).dmg(4).warn(3).life(36).spawn();
            }
        }, (p, t, m) -> p.r("armL", -1.6f, 0, .4f).r("armR", -1.6f, 0, -.4f).r("arm2L", -1f + sin(t, .5f) * .4f, 0, .6f).r("arm2R", -1f - sin(t, .5f) * .4f, 0, -.6f).o("fang", 0, Math.abs(sin(t, .4f)) * .3f, 0));
    }
    static Attack muffetCroissants() {
        return a("muffet_croissants", 70, (c, t) -> {
            // croissants fly out past you, then boomerang back
            if (t >= 8 && t < 48 && t % 10 == 0) {
                Vec3 from = c.eye(), d = c.aim(from); double off = (c.rnd.nextDouble() - .5) * .4;
                Vec3 dd = d.add(c.right().scale(off));
                shot(c, from, dd, .55, .7f, .25f, .35f, 0xe0a050, 4, 18).spawn();
                Vec3 far = from.add(dd.normalize().scale(.55 * 18));
                c.proj().at(far).vel(dd.normalize().scale(-.55)).box(.7f, .25f, .35f).color(0xe0a050).dmg(4).warn(18).life(36).spawn();
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .7f, 1.6f);
            }
        }, (p, t, m) -> { float s = env((t - 4) % 10, 0, 4, 5, 9); p.r("armR", -2.5f * s, 0, -.3f).r("arm2L", -2.5f * s, 0, .3f).r("body", 0, -.3f * s, 0); });
    }
    static Attack muffetDonuts() {
        return a("muffet_donuts", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 8 == 0) {
                Vec3 from = c.eye().add(0, 1, 0); Vec3 d = c.tpos().subtract(from);
                c.proj().at(from).vel(new Vec3(d.x * .05, .3, d.z * .05)).gravity(.035f).box(.6f, .25f, .6f).color(0xd06090).dmg(4).life(40).spawn();
                // spiders hop off the donut
                c.proj().at(from).vel(new Vec3(d.x * .045, .45, d.z * .045)).gravity(.035f).box(.3f, .2f, .3f).color(0x2a0a3a).dmg(3).life(40).spawn();
            }
        }, (p, t, m) -> p.r("arm3L", -2f, 0, .3f).r("arm3R", -2f, 0, -.3f).r("armR", -2.6f * Math.abs(sin(t, (float) Math.PI / 8)), 0, -.2f));
    }
    static Attack muffetTea() {
        return a("muffet_tea_party", 80, (c, t) -> {
            // spiders are poured from a teapot overhead
            Vec3 pot = c.tpos().add(c.fwd().scale(-1)).add(0, 5, 0);
            if (t == 4) c.proj().at(pot).box(1.2f, .9f, 1.2f).color(0xf5f2e6).dmg(0).life(72).spawn();
            if (t >= 14 && t < 72 && t % 3 == 0) {
                Vec3 at = pot.add(c.rnd.nextGaussian() * .8, -.5, c.rnd.nextGaussian() * .8);
                Vec3 d = c.tpos().subtract(at).normalize().add(c.rnd.nextGaussian() * .3, 0, c.rnd.nextGaussian() * .3);
                shot(c, at, d, .3, .3f, .2f, .3f, 0x2a0a3a, 3, 30).spawn();
                if (t % 9 == 0) c.sound(SoundEvents.SPIDER_AMBIENT, .6f, 1.5f);
            }
        }, (p, t, m) -> p.r("armL", -1.2f, 0, .3f).r("armR", -1.2f, 0, -.3f).r("head", -.25f, 0, 0).r("tailL", 0, 0, sin(t, .3f) * .2f).r("tailR", 0, 0, -sin(t, .3f) * .2f));
    }
    static Attack muffetCupcake() {
        return a("muffet_pet_cupcake", 100, below(.6f), (c, t) -> {
            // her pet: a giant cupcake-spider that lunges up the web at you
            if (t == 0) purple(c, 100);
            Vec3 f = c.fwd();
            if (t == 20 || t == 60) {
                Vec3 at = c.tpos().add(f.scale(9)).add(0, -.5, 0);
                tele(c, at, c.tpos(), 0xd06090, 12);
                c.proj().at(at).vel(f.scale(-.55)).box(3.5f, 2.5f, 3f).color(0xd06090).dmg(8).warn(12).life(40).pierce().spawn();
                c.proj().at(at.add(0, 1.6, 0)).vel(f.scale(-.55)).box(3f, .8f, 2.5f).color(0xf2c6d6).dmg(0).life(40).spawn();
                c.sound(SoundEvents.RAVAGER_ROAR, 1, 1.4f);
            }
            if (t >= 30 && t < 56 && t % 4 == 0) drop(c, 2, .3f, .25f, 0x2a0a3a, 3, .4);
        }, (p, t, m) -> {
            float f = env(t, 10, 18, 80, 95);
            p.r("armL", -2.8f * f, 0, .5f).r("armR", -2.8f * f, 0, -.5f).r("arm2L", -2.2f * f, 0, .7f).r("arm2R", -2.2f * f, 0, -.7f).r("arm3L", -1.6f * f, 0, .9f).r("arm3R", -1.6f * f, 0, -.9f);
        });
    }
    static Attack muffetFinale() {
        return a("muffet_spider_bake_sale", 110, below(.3f), (c, t) -> {
            muffetSpiders().tick(c, t % 80);
            if (t % 15 == 5) muffetCroissants().tick(c, 10);
            if (t > 60) muffetDonuts().tick(c, t - 52);
        }, (p, t, m) -> p.r("body", 0, sin(t, .3f) * .3f, 0).r("armL", -2f, 0, .4f).r("armR", -2f, 0, -.4f).r("arm2L", -1.4f, 0, .6f).r("arm2R", -1.4f, 0, -.6f).r("arm3L", -1f, 0, .8f).r("arm3R", -1f, 0, -.8f));
    }

    // ======================= Mettaton (box) =======================
    static Attack mttLasers() {
        return a("mettaton_lasers", 80, (c, t) -> {
            // blue/orange laser walls (Hotland laser rooms): stay still for blue, move for orange
            if (t >= 8 && t < 60 && t % 12 == 0) {
                Vec3 r = c.right(); double s = (t / 12) % 2 == 0 ? 1 : -1;
                UtProjectile.B b = c.proj().at(c.tpos().add(r.scale(-s * 6))).vel(r.scale(s * .35)).face(c.fwd()).box(.2f, 3f, 9f).dmg(5).warn(6).life(36);
                (c.rnd.nextBoolean() ? b.blue() : b.orange()).spawn();
                c.sound(SoundEvents.BEACON_POWER_SELECT, .7f, 1.8f);
            }
        }, (p, t, m) -> p.r("armL", -1.6f, 0, .6f).r("armR", -1.6f, 0, -.6f).r("body", 0, sin(t, .3f) * .3f, 0).r("wheel", t * .5f, 0, 0));
    }
    static Attack mttMiniBombs() {
        return a("mettaton_mini_bombs", 80, (c, t) -> {
            // mini-Mettatons drop, then blow up in a plus-shaped blast
            if (t == 10 || t == 30 || t == 50) {
                Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 1.2, 0, c.rnd.nextGaussian() * 1.2);
                c.proj().at(g.add(0, 4, 0)).vel(new Vec3(0, -.4, 0)).box(.4f, .6f, .3f).color(0x6b6b6b).dmg(0).life(10).spawn();
                for (int k = 0; k < 2; k++) {
                    Vec3 d = k == 0 ? c.fwd() : c.right();
                    c.proj().at(g.add(0, .5, 0)).face(d).box(.5f, .5f, 9f).color(0xffd23a).dmg(5).warn(16).life(22).pierce().spawn();
                }
            }
            if (t == 26 || t == 46 || t == 66) c.sound(SoundEvents.GENERIC_EXPLODE, .7f, 1.3f);
        }, (p, t, m) -> p.r("armR", -2.6f * Math.abs(sin(t, (float) Math.PI / 20)), 0, -.3f).o("body", 0, Math.abs(sin(t, .4f)) * .5f, 0));
    }
    static Attack mttQuiz() {
        return a("mettaton_quiz_bolt", 60, (c, t) -> {
            // "WRONG!" — lightning strikes the contestant
            if (t == 20 || t == 34) {
                Vec3 g = c.target.position();
                mark(c, g, 1.2f, YELLOW, 8);
                c.proj().at(g.add(0, 4, 0)).face(new Vec3(0, 1, 0)).box(.5f, .5f, 8f).color(YELLOW).dmg(6).warn(8).life(14).pierce().spawn();
            }
            if (t == 28 || t == 42) c.sound(SoundEvents.LIGHTNING_BOLT_IMPACT, .7f, 1.4f);
        }, (p, t, m) -> p.r("armR", -3f * env(t, 0, 16, 44, 54), 0, -.2f).r("armL", -1.2f, 0, .8f).o("light1", 0, 0, -Math.abs(sin(t, 1.2f)) * .3f));
    }
    static Attack mttNewsBombs() {
        return a("mettaton_news_bombs", 90, below(.7f), (c, t) -> {
            // News show: bombs scattered around the studio blast in lines — football, basketball, glass of water...
            if (t == 6) for (int k = 0; k < 5; k++) {
                Vec3 g = ring(c.target.position(), k * 1.256 + c.rnd.nextDouble(), 2 + c.rnd.nextDouble() * 2, .3);
                c.proj().at(g).box(.5f, .5f, .5f).color(0x222222).dmg(0).life(40 + k * 8).spawn();
                c.proj().at(g).face(k % 2 == 0 ? c.fwd() : c.right()).box(.6f, .6f, 10).color(0xff4040).dmg(6).warn(40 + k * 8).life(48 + k * 8).pierce().spawn();
            }
            if (t >= 46 && t % 8 == 6 && t < 90) c.sound(SoundEvents.GENERIC_EXPLODE, .7f, 1.2f);
        }, (p, t, m) -> p.r("armL", -1.6f, sin(t, .3f) * .5f, .3f).r("armR", -1.6f, -sin(t, .3f) * .5f, -.3f));
    }
    static Attack mttCookingFire() {
        return a("mettaton_cooking_flames", 70, below(.45f), (c, t) -> {
            // Cooking with a Killer Robot: fire jets and a chainsaw rush
            if (t >= 8 && t < 40 && t % 4 == 0) drop(c, 2, .4f, .6f, 0xff6a00, 4, .45);
            if (t == 44) { c.dash(c.aim(c.pos()).multiply(1, 0, 1).scale(1.4)); c.sound(SoundEvents.RAVAGER_ATTACK, 1, 1.6f); }
            if (t >= 46 && t < 54) { c.aoe(c.pos().add(0, 1, 0), 1.8, 4, .6); c.particle(ParticleTypes.CRIT, c.eye(), 6, .4, .1); }
        }, (p, t, m) -> p.r("armR", -1.6f - sin(t, 2f) * .2f * up(t, 40, 44), 0, -.2f).r("body", .4f * env(t, 40, 44, 54, 60), 0, 0).r("wheel", t * .8f, 0, 0));
    }

    // ======================= Mettaton EX =======================
    static Attack exLegs() {
        return a("mettaton_ex_leg_kicks", 70, (c, t) -> {
            // his legs scissor across the stage
            if (t >= 10 && t < 60 && t % 12 == 0) {
                Vec3 r = c.right(); double s = (t / 12) % 2 == 0 ? 1 : -1;
                Vec3 at = c.tpos().add(r.scale(-s * 6)).add(0, .4, 0);
                tele(c, at, at.add(r.scale(s * 12)), 0xff3b6b, 6);
                c.proj().at(at).vel(r.scale(s * .45)).face(c.fwd()).box(.6f, .6f, 4f).color(0x1b1b1b).dmg(5).warn(6).life(30).spawn();
                c.sound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1, 1.2f);
            }
        }, (p, t, m) -> { float s = sin(t, (float) Math.PI / 12); p.r("legR", -1.6f * Math.max(0, s), 0, 0).r("legL", -1.6f * Math.max(0, -s), 0, 0).r("armL", 0, 0, .8f).r("armR", 0, 0, -.8f); });
    }
    static Attack exMiniMettatons() {
        return a("mettaton_ex_mini_mettatons", 80, (c, t) -> {
            // mini Mettatons parade by, firing little hearts / bolts
            if (t >= 8 && t < 64 && t % 8 == 0) {
                Vec3 r = c.right(); Vec3 at = c.pos().add(r.scale((t / 8 % 2 == 0 ? 1 : -1) * 2)).add(0, 2.5, 0);
                c.proj().at(at).box(.3f, .5f, .2f).color(0x6b6b6b).dmg(0).life(10).spawn();
                shot(c, at, c.aim(at), .45, .25f, .25f, .25f, 0xff3b6b, 4, 30).warn(3).spawn();
                c.sound(SoundEvents.NOTE_BLOCK_BELL.value(), .6f, 1.6f);
            }
        }, (p, t, m) -> p.r("armR", -2.8f, 0, -.4f).r("armL", 0, 0, .6f).r("head", 0, sin(t, .3f) * .3f, 0).r("legL", -.4f, 0, 0));
    }
    static Attack exArms() {
        return a("mettaton_ex_arms", 70, (c, t) -> {
            // detached arms bounce across the stage (classic "arms" turn)
            if (t >= 10 && t < 58 && t % 6 == 0) {
                Vec3 at = ring(c.tpos(), c.rnd.nextDouble() * Math.PI * 2, 5, 1.5);
                Vec3 d = c.tpos().subtract(at);
                c.proj().at(at).vel(new Vec3(d.x * .07, .25, d.z * .07)).gravity(.03f).box(.25f, .25f, 1.4f).color(0x1b1b1b).dmg(4).warn(4).life(30).spawn();
            }
        }, (p, t, m) -> p.r("armL", -2.5f, 0, sin(t, .8f)).r("armR", -2.5f, 0, -sin(t, .8f)).o("body", 0, Math.abs(sin(t, .5f)) * 1.2f, 0));
    }
    static Attack exHeartBomb() {
        return a("mettaton_ex_heart_bomb", 70, (c, t) -> {
            // a heart bomb glides in, then bursts into a ring; shooting it apart in canon — here, dodge
            Vec3 g = c.tpos();
            if (t == 10) c.proj().at(c.eye()).vel(c.aim(c.eye()).scale(.18)).box(.8f, .8f, .5f).color(0xff3b6b).dmg(4).life(30).spawn();
            if (t == 40) {
                Vec3 at = c.eye().add(c.aim(c.eye()).scale(.18 * 30));
                burst(c, at, 14, .3, .25f, 0xff3b6b, 4, 30, 0);
                c.particle(ParticleTypes.HEART, at, 8, .4, .05); c.sound(SoundEvents.GENERIC_EXPLODE, .7f, 1.5f);
            }
        }, (p, t, m) -> p.r("armR", -2.4f * env(t, 0, 8, 12, 20), 0, -.3f).r("body", 0, 0, .15f * env(t, 0, 8, 12, 20)));
    }
    static Attack exBoxRain() {
        return a("mettaton_ex_box_rain", 80, below(.7f), (c, t) -> {
            // rows of rectangles fall with a single gap
            if (t >= 8 && t < 64 && t % 14 == 0) {
                Vec3 r = c.right(); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) {
                    if (Math.abs(k - gap) < 1) continue;
                    Vec3 at = c.target.position().add(r.scale(k * 1.0)).add(0, 6, 0);
                    mark(c, at.add(0, -6, 0), .9f, 0xffd23a, 10);
                    c.proj().at(at).vel(new Vec3(0, -.5, 0)).box(.9f, .6f, .9f).color(0x6b6b6b).dmg(5).warn(6).life(18).spawn();
                }
            }
        }, (p, t, m) -> p.r("armL", -3f, 0, .3f).r("armR", -3f, 0, -.3f).r("legR", -.6f * Math.abs(sin(t, .3f)), 0, 0));
    }
    static Attack exDiscoLasers() {
        return a("mettaton_ex_disco_lasers", 90, below(.45f), (c, t) -> {
            // disco ball descends; blue/orange lasers sweep around it
            Vec3 ball = c.tpos().add(0, 4, 0);
            if (t == 4) c.proj().at(ball).box(1f, 1f, 1f).color(0xdfe8f2).dmg(0).life(84).spawn();
            if (t >= 16 && t < 80 && t % 10 == 6) {
                double ang = t * .3; Vec3 d = new Vec3(Math.cos(ang), 0, Math.sin(ang));
                UtProjectile.B b = c.proj().at(c.tpos()).face(d).box(.3f, 2.5f, 12).dmg(5).warn(6).life(14);
                ((t / 10) % 2 == 0 ? b.blue() : b.orange()).spawn();
                c.particle(ParticleTypes.END_ROD, ball, 10, .5, .1);
            }
        }, (p, t, m) -> p.r("armR", -3f, 0, -.2f).r("armL", 0, 0, .9f).r("body", 0, t * .15f, 0).r("legL", -.5f, 0, 0));
    }
    static Attack exRatingsFinale() {
        return a("mettaton_ex_grand_finale", 110, below(.2f), (c, t) -> {
            // ratings are through the roof: everything at once
            exLegs().tick(c, t % 70);
            if (t % 3 == 0) exArms().tick(c, 10 + (t % 48));
            if (t >= 60) exBoxRain().tick(c, t - 52);
        }, (p, t, m) -> p.r("armL", -3f, 0, .5f).r("armR", -3f, 0, -.5f).r("body", 0, t * .25f, 0).o("body", 0, Math.abs(sin(t, .4f)) * 1.5f, 0));
    }
}
