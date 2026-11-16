package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import java.util.List;

/** Genocide-route AUs: Dusttale (Dust/Murder Sans, Phantom Papyrus), Undertale: Last Breath (Sans phases 1-3,
 *  Dusttale Papyrus/Undyne/Frisk, incl. the Last Corridor "Artifact Frenzy" and "Struggle" sub-phases), Disbelief Papyrus and Dustbelief Papyrus. */
public final class AuDustLastBreath {
    public static void register(List<CharacterDef> out) {
        out.add(dustSans());
        out.add(phantomPapyrus());
        out.add(dusttalePapyrus());
        out.add(dusttaleUndyne());
        out.add(dusttaleFrisk());
        out.add(lastBreathSans());
        out.add(dustbeliefPapyrus());
    }

    // ================================================================ palette / shared helpers
    static final int W = 0xffffff, BL = -1, OR = -2, PURPLE = 0xa23cff, DEEP = 0x6a1fd0, RED = 0xff2030, CYAN = 0x3fe8ff,
        GOLD = 0xffd23a, ASH = 0x8c8c8c, GREY = 0x9a9aa6, VOID = 0x2a2a36, ORANGE_EYE = 0xff8a1a, BONE = 0xf4f4ee;
    static final double PI = Math.PI;

    static UtProjectile.B p(AttackCtx c, Vec3 at, int col) {
        UtProjectile.B b = c.proj().at(at);
        return col == BL ? b.blue() : col == OR ? b.orange() : b.color(col);
    }
    static Vec3 ang(double a) { return new Vec3(Math.cos(a), 0, Math.sin(a)); }
    static Vec3 hz(Vec3 v) { Vec3 h = new Vec3(v.x, 0, v.z); return h.lengthSqr() < 1e-6 ? new Vec3(0, 0, 1) : h.normalize(); }
    static Vec3 side(Vec3 d) { Vec3 h = hz(d); return new Vec3(-h.z, 0, h.x); }
    static double angOf(Vec3 v) { return Math.atan2(v.z, v.x); }
    static float env(float t, float a, float b, float c, float d) {
        if (t <= a || t >= d) return 0; if (t < b) return (t - a) / (b - a); if (t <= c) return 1; return 1 - (t - c) / (d - c);
    }
    static float ramp(float t, float a, float b) { return t <= a ? 0 : t >= b ? 1 : (t - a) / (b - a); }
    static double ground(AttackCtx c) { return c.target.getY(); }

    /** Static bone erupting from the ground after a telegraph. */
    static void spike(AttackCtx c, Vec3 base, float h, int col, float dmg, int warn, int life) {
        p(c, new Vec3(base.x, base.y + h / 2, base.z), col).box(.24f, h, .24f).dmg(dmg).warn(warn).life(warn + life).pierce().spawn();
        c.particle(ParticleTypes.WHITE_ASH, base, 3, .2, .02);
    }
    /** Upright bone travelling along vel. */
    static void post(AttackCtx c, Vec3 at, Vec3 vel, float h, int col, float dmg, int warn, int life) {
        p(c, at, col).vel(vel).box(.24f, h, .24f).dmg(dmg).warn(warn).life(life).pierce().spawn();
    }
    /** Bone lying across its travel direction. */
    static void bar(AttackCtx c, Vec3 at, Vec3 vel, float len, int col, float dmg, int warn, int life) {
        p(c, at, col).vel(vel).box(len, .24f, .24f).dmg(dmg).warn(warn).life(life).pierce().spawn();
    }
    /** Bone flying point-first. */
    static void lance(AttackCtx c, Vec3 at, Vec3 vel, float len, int col, float dmg, int warn, int life) {
        p(c, at, col).vel(vel).box(.22f, .22f, len).dmg(dmg).warn(warn).life(life).spawn();
    }
    /** Harmless static visual piece. */
    static void deco(AttackCtx c, Vec3 at, Vec3 dir, float w, float h, float l, int col, int life) {
        p(c, at, col).face(dir).box(w, h, l).dmg(0).life(life).pierce().spawn();
    }
    /** Harmless moving visual piece. */
    static void decoV(AttackCtx c, Vec3 at, Vec3 vel, float w, float h, float l, int col, int life) {
        p(c, at, col).vel(vel).box(w, h, l).dmg(0).life(life).pierce().spawn();
    }
    /** Straight beam with telegraph (no skull). */
    static void beam(AttackCtx c, Vec3 from, Vec3 dir, double len, float width, int col, int warn, int fire, float dmg) {
        dir = dir.normalize();
        p(c, from.add(dir.scale(len / 2)), col).face(dir).box(width, width, (float) len).dmg(dmg).warn(warn).life(warn + fire).pierce().spawn();
    }
    /** Colour-able Gaster Blaster: cranium, jaw, horns, two eye glows, a mouth charge flash and the beam. */
    static void gb(AttackCtx c, Vec3 at, Vec3 aimAt, int charge, int fire, float dmg, float width, double len, int skull, int eye, int ray) {
        Vec3 d = aimAt.subtract(at).normalize(), r = side(d); int life = charge + fire + 6;
        deco(c, at, d, 1.2f, 1.0f, 1.3f, skull, life);
        deco(c, at.add(d.scale(.4)).add(0, -.62, 0), d, .9f, .32f, 1.0f, skull, life);
        deco(c, at.add(d.scale(.66)).add(r.scale(.3)).add(0, .14, 0), d, .26f, .24f, .08f, eye, life);
        deco(c, at.add(d.scale(.66)).add(r.scale(-.3)).add(0, .14, 0), d, .26f, .24f, .08f, eye, life);
        deco(c, at.subtract(d.scale(.45)).add(r.scale(.75)).add(0, .35, 0), d, .22f, .7f, .5f, skull, life);
        deco(c, at.subtract(d.scale(.45)).add(r.scale(-.75)).add(0, .35, 0), d, .22f, .7f, .5f, skull, life);
        p(c, at.add(d.scale(.9)).add(0, -.3, 0), ray).face(d).box(.8f, .8f, .3f).dmg(0).warn(charge).life(charge + fire).pierce().spawn();
        beam(c, at.add(d.scale(.9)), d, len, width, ray, charge, fire, dmg);
        c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1.1f, 1.7f);
    }
    static void boom(AttackCtx c, Vec3 at) {
        c.sound(SoundEvents.WARDEN_SONIC_BOOM, .9f, 1.5f); c.particle(ParticleTypes.FLASH, at, 1, 0, 0); c.particle(ParticleTypes.END_ROD, at, 10, .4, .1);
    }
    /** Cross-shaped spinner (two bones), moving or static. */
    static void cross(AttackCtx c, Vec3 at, Vec3 vel, float size, int col, float dmg, int warn, int life) {
        if (vel.lengthSqr() < 1e-6) {
            Vec3 d = ang(c.me.tickCount * .4);
            p(c, at, col).face(d).box(.22f, .22f, size).dmg(dmg).warn(warn).life(life).pierce().spawn();
            p(c, at, col).face(d).box(size, .22f, .22f).dmg(0).warn(warn).life(life).pierce().spawn();
        } else {
            lance(c, at, vel, size, col, dmg, warn, life);
            p(c, at, col).vel(vel).box(size, .22f, .22f).dmg(0).warn(warn).life(life).pierce().spawn();
        }
    }
    /** The Annoying Dog: body (the hitbox), head, black ears. */
    static void dog(AttackCtx c, Vec3 at, Vec3 vel, int warn, int life, float dmg) {
        Vec3 f = vel.lengthSqr() < 1e-6 ? c.fwd() : vel.normalize();
        p(c, at, W).vel(vel).box(.45f, .38f, .7f).dmg(dmg).warn(warn).life(life).spawn();
        decoV(c, at.add(f.scale(.38)).add(0, .26, 0), vel, .36f, .34f, .34f, W, life);
        Vec3 r = side(f);
        decoV(c, at.add(f.scale(.34)).add(r.scale(.2)).add(0, .3, 0), vel, .1f, .28f, .12f, 0x202020, life);
        decoV(c, at.add(f.scale(.34)).add(r.scale(-.2)).add(0, .3, 0), vel, .1f, .28f, .12f, 0x202020, life);
    }
    static void explode(AttackCtx c, Vec3 at, float dmg) {
        c.aoe(at, 1.8, dmg, .7); c.particle(ParticleTypes.EXPLOSION, at, 2, .4, 0); c.particle(ParticleTypes.CRIT, at, 14, .5, .3);
        c.sound(SoundEvents.GENERIC_EXPLODE, .7f, 1.4f);
    }
    /** Translucent-looking Sans clone (Last Breath "Unexpected Assist", Dustbelief phantom). */
    static void ghostSans(AttackCtx c, Vec3 feet, Vec3 face, int life, int tint, int eye) {
        Vec3 d = hz(face), r = side(d);
        deco(c, feet.add(0, .25, 0), d, .45f, .4f, .3f, 0x222233, life);
        deco(c, feet.add(0, .7, 0), d, .62f, .5f, .4f, tint, life);
        deco(c, feet.add(0, 1.18, 0), d, .58f, .48f, .5f, 0xe8e8f0, life);
        deco(c, feet.add(0, 1.2, 0).add(d.scale(.26)).add(r.scale(.13)), d, .12f, .12f, .04f, eye, life);
        deco(c, feet.add(0, 1.2, 0).add(d.scale(.26)).add(r.scale(-.13)), d, .12f, .12f, .04f, 0x111111, life);
    }
    /** W.D. Gaster hand: palm with a hole and four fingers. */
    static void gasterHand(AttackCtx c, Vec3 at, Vec3 face, int life) {
        Vec3 d = face.normalize(), r = side(d);
        deco(c, at, d, .9f, 1.0f, .22f, 0xf0f0f0, life);
        deco(c, at.add(d.scale(.12)), d, .3f, .3f, .05f, 0x101010, life);
        for (int k = -1; k <= 2; k++) deco(c, at.add(r.scale((k - .5) * .23)).add(0, .78, 0), d, .16f, .55f, .16f, 0xe6e6e6, life);
        deco(c, at.add(r.scale(.55)).add(0, .1, 0), d, .4f, .16f, .16f, 0xe6e6e6, life);
    }
    /** Phantom Papyrus head (Dusttale hallucination). */
    static void papHead(AttackCtx c, Vec3 at, Vec3 face, int life) {
        Vec3 d = hz(face), r = side(d);
        deco(c, at, d, .9f, 1.0f, .8f, 0xcfcfe0, life);
        deco(c, at.add(0, -.62, 0).add(d.scale(.1)), d, .7f, .3f, .7f, 0xbdbdd0, life);
        deco(c, at.add(d.scale(.42)).add(r.scale(.2)).add(0, .1, 0), d, .2f, .2f, .05f, RED, life);
        deco(c, at.add(d.scale(.42)).add(r.scale(-.2)).add(0, .1, 0), d, .2f, .2f, .05f, RED, life);
    }
    static DustParticleOptions dust(int rgb, float s) {
        return new DustParticleOptions(new Vector3f((rgb >> 16 & 255) / 255f, (rgb >> 8 & 255) / 255f, (rgb & 255) / 255f), s);
    }
    static void eff(AttackCtx c, MobEffect e, int dur, int amp) { c.target.addEffect(new MobEffectInstance(e, dur, amp)); }
    static void yank(AttackCtx c, Vec3 v) { c.target.setDeltaMovement(v); c.target.hurtMarked = true; }
    static void putV(AttackCtx c, String k, Vec3 v) {
        CompoundTag d = c.me.getPersistentData(); d.putDouble(k + "x", v.x); d.putDouble(k + "y", v.y); d.putDouble(k + "z", v.z);
    }
    static Vec3 getV(AttackCtx c, String k) {
        CompoundTag d = c.me.getPersistentData(); return new Vec3(d.getDouble(k + "x"), d.getDouble(k + "y"), d.getDouble(k + "z"));
    }
    /** Target velocity estimated from its position last tick (server player motion is not synced). Call every tick. */
    static Vec3 tvel(AttackCtx c) {
        Vec3 now = c.target.position(), was = c.me.getPersistentData().contains("utdlx") ? getV(c, "utdl") : now;
        putV(c, "utdl", now); Vec3 v = now.subtract(was); return v.lengthSqr() > 1 ? Vec3.ZERO : v;
    }
    static Vec3 predict(AttackCtx c, int ticks) { return c.target.position().add(tvel(c).multiply(ticks, 0, ticks)); }
    /** Ring of ground spikes around a centre. */
    static void spikeRing(AttackCtx c, Vec3 ctr, double rad, int n, double rot, int gapAt, float h, int col, float dmg, int warn, int life) {
        for (int k = 0; k < n; k++) {
            if (gapAt >= 0 && (k == gapAt || k == (gapAt + 1) % n)) continue;
            Vec3 a = ang(rot + k * 2 * PI / n).scale(rad);
            spike(c, new Vec3(ctr.x + a.x, ground(c), ctr.z + a.z), h, col, dmg, warn, life);
        }
    }
    static void tele(AttackCtx c, Vec3 to) {
        c.particle(dust(0x5ab0ff, 1.4f), c.pos().add(0, 1, 0), 20, .4, .05);
        c.tp(to); c.sound(SoundEvents.ENDERMAN_TELEPORT, .8f, 1.6f);
        c.particle(ParticleTypes.REVERSE_PORTAL, to.add(0, 1, 0), 24, .4, .2);
    }
    /** Phase gate by hp fraction: lo < hp <= hi. */
    static Attack ph(float lo, float hi, Attack a) {
        return new Attack(a.id, a.duration) {
            public void tick(AttackCtx c, int t) { a.tick(c, t); }
            public void pose(Pose p, float t, UtMob m) { a.pose(p, t, m); }
            public boolean usable(UtMob m) { return m.hpFrac() > lo && m.hpFrac() <= hi; }
        };
    }

    // ================================================================ DUSTTALE: Dust Sans (Murder Sans)
    static CharacterDef dustSans() {
        int hood = 0x24438f, hoodDark = 0x1a3270, dustC = 0xb0aaa0;
        return CharacterDef.of("dusttale_sans", "Dust Sans", Faction.GENOCIDE_ROUTE).att(Attitude.HOSTILE).hp(170).boss().keep(8).rest(12).speed(.32f)
            .egg(0x24438f, 0xa23cff).size(.7f, 1.15f).scale(.92f)
            .model(new ModelDef()
                .box("body", null, 0, 8, 0, 8.5f, 7, 5.2f, hood)
                .box("zip", "body", 0, 0, -2.65f, 1.2f, 6.5f, .2f, 0xdedede)
                .box("pocketL", "body", -2.4f, -2.2f, -2.7f, 2.6f, 1.6f, .3f, hoodDark)
                .box("pocketR", "body", 2.4f, -2.2f, -2.7f, 2.6f, 1.6f, .3f, hoodDark)
                .glow("dust1", "body", -3, 1.5f, -2.75f, .8f, .8f, .2f, dustC)
                .glow("dust2", "body", 2.2f, -.8f, -2.75f, 1.1f, .6f, .2f, dustC)
                .glow("dust3", "body", 1, 2.6f, -2.75f, .6f, .6f, .2f, dustC)
                .box("head", "body", 0, 9.5f, 0, 9, 7.5f, 7.5f, 0xf2f2ee)
                .box("hoodTop", "head", 0, 4.3f, .6f, 10.4f, 1.4f, 8.8f, hood)
                .box("hoodBack", "head", 0, .2f, 4.3f, 10.4f, 8.4f, 1.2f, hood)
                .box("hoodL", "head", -5, .2f, .6f, 1.1f, 8.4f, 7.6f, hood)
                .box("hoodR", "head", 5, .2f, .6f, 1.1f, 8.4f, 7.6f, hood)
                .box("hoodRim", "head", 0, 3.6f, -3.95f, 10.6f, .9f, .8f, 0xd8d8d8)
                .box("sockL", "head", -2.1f, .6f, -3.8f, 2.4f, 2.2f, .2f, 0x050505)
                .box("sockR", "head", 2.1f, .6f, -3.8f, 2.4f, 2.2f, .2f, 0x050505)
                .glow("eyeL", "head", -2.1f, .6f, -3.95f, 1.2f, 1.2f, .3f, RED)
                .ghost("eyeFlame", "head", -2.4f, 2.0f, -3.95f, 1.0f, 2.2f, .3f, PURPLE, .7f)
                .glow("eyeR", "head", 2.1f, .5f, -3.95f, .5f, .5f, .3f, 0xffffff)
                .box("grin", "head", 0, -2.2f, -3.85f, 5.6f, .8f, .2f, 0x1b1b1b)
                .box("teeth", "head", 0, -2.2f, -3.95f, 4.6f, .25f, .1f, 0x8a8a8a)
                .limb("armL", "body", -5.3f, 3, 0, 2.6f, 6, 2.6f, hood)
                .limb("armR", "body", 5.3f, 3, 0, 2.6f, 6, 2.6f, hood)
                .box("handL", "armL", 0, -6.6f, 0, 2.1f, 1.6f, 2.1f, 0xeeeeee)
                .box("handR", "armR", 0, -6.6f, 0, 2.1f, 1.6f, 2.1f, 0xeeeeee)
                .limb("legL", null, -2, 5, 0, 3.2f, 5, 3.6f, 0x151515)
                .limb("legR", null, 2, 5, 0, 3.2f, 5, 3.6f, 0x151515)
                .box("stripeL", "legL", -1.65f, -2.5f, 0, .2f, 4.5f, 3.6f, 0xe8e8e8)
                .box("stripeR", "legR", 1.65f, -2.5f, 0, .2f, 4.5f, 3.6f, 0xe8e8e8)
                .glow("dust4", "legL", .4f, -2, -1.85f, .8f, .8f, .2f, dustC)
                .box("shoeL", "legL", 0, -5.3f, -.6f, 3.4f, 1.1f, 4.6f, 0x2a2a2a)
                .box("shoeR", "legR", 0, -5.3f, -.6f, 3.4f, 1.1f, 4.6f, 0x2a2a2a))
            .attacks(dustFlurry(), purpleStrings(), telekinesis(), purpleBlasters(), boneKnife(), phantomAssist(), saveBreak(),
                ph(0, .6f, lvSurge()), ph(0, .35f, madTime()), ph(0, .5f, expDrain()))
            .build();
    }

    /** Dust swirls around the target, then contracting rings of white/purple bones erupt from it. */
    static Attack dustFlurry() {
        return new Attack("dusttale_dust_flurry", 84) {
            public void tick(AttackCtx c, int t) {
                c.face(c.tpos());
                if (t < 12) {
                    double a = t * .55;
                    for (int k = 0; k < 3; k++) c.particle(dust(0x9d968c, 1.6f), c.tpos().add(ang(a + k * 2.1).scale(4.2)), 4, .2, .02);
                    if (t == 0) c.sound(SoundEvents.SAND_BREAK, 1, .6f);
                }
                if (t == 10) c.sound(SoundEvents.SKELETON_SHOOT, 1, .5f);
                if (t >= 12 && t < 60 && (t - 12) % 8 == 0) {
                    int k = (t - 12) / 8; Vec3 ctr = c.tpos();
                    spikeRing(c, ctr, 4.6 - k * .65, 10 + 2 * k, k * .4, c.rnd.nextInt(10 + 2 * k), 1.3f + k * .1f, k % 2 == 0 ? W : PURPLE, 4, 8, 7);
                    c.particle(dust(0x9d968c, 1.2f), ctr, 18, 2.5, .02);
                    c.sound(SoundEvents.BONE_BLOCK_BREAK, .8f, 1.3f + k * .05f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                float up = env(t, 0, 6, 10, 16), snap = env(t, 9, 10, 11, 14);
                p.r("armR", -1.6f * up, 0, -.4f * up).r("armR", 0, 0, .3f * snap).o("head", 0, -.4f * up, 0)
                    .r("head", .15f * up, 0, .12f * up).r("armL", .2f, 0, .15f).r("body", 0, -.2f * up, 0);
                if (t > 14) p.o("body", 0, (float) Math.sin(t * .5) * .25f, 0);
            }
        };
    }
    /** Purple soul: three web strings lock the target's movement while purple bones race along the lanes. */
    static Attack purpleStrings() {
        return new Attack("dusttale_purple_strings", 112) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) {
                    Vec3 f = hz(c.tpos().subtract(c.pos())), anchor = c.target.position();
                    putV(c, "dsA", anchor); putV(c, "dsF", f);
                    for (int k = -1; k <= 1; k++)
                        deco(c, anchor.add(side(f).scale(k * 1.6)).add(0, .12, 0), f, .06f, .06f, 26, PURPLE, 108);
                    eff(c, MobEffects.MOVEMENT_SLOWDOWN, 110, 2);
                    c.sound(SoundEvents.SPIDER_AMBIENT, 1, .6f); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .5f);
                }
                Vec3 anchor = getV(c, "dsA"), f = getV(c, "dsF"), r = side(f);
                if (t % 3 == 0 && t > 4 && t < 104) {      // snap onto the nearest string, as the purple SOUL does
                    double s = c.target.position().subtract(anchor).dot(r); int lane = (int) Math.max(-1, Math.min(1, Math.round(s / 1.6)));
                    double off = lane * 1.6 - s; Vec3 v = c.target.getDeltaMovement();
                    if (Math.abs(off) > .15) yank(c, new Vec3(v.x + r.x * off * .5, v.y, v.z + r.z * off * .5));
                    c.particle(ParticleTypes.WITCH, c.tpos(), 2, .2, .02);
                }
                if (t >= 10 && t < 96 && (t - 10) % 9 == 0) {
                    int wave = (t - 10) / 9, free = wave % 3 - 1, twice = (wave + 1) % 3 - 1;
                    for (int k = -1; k <= 1; k++) {
                        if (k == free) continue;
                        Vec3 start = anchor.add(f.scale(11)).add(r.scale(k * 1.6)).add(0, .7, 0);
                        post(c, start, f.scale(-.62), 1.4f, PURPLE, 4, 4, 42);
                        if (wave >= 5 && k == twice) post(c, start.add(f.scale(1.4)), f.scale(-.62), .7f, W, 4, 4, 44);
                    }
                    c.sound(SoundEvents.SKELETON_SHOOT, .6f, 1.2f);
                }
                if (t % 4 == 0 && t < 104)
                    for (int k = -1; k <= 1; k++) c.particle(dust(PURPLE, .8f), anchor.add(r.scale(k * 1.6)).add(f.scale(c.rnd.nextDouble() * 20 - 8)).add(0, .15, 0), 1, .05, 0);
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = ramp(t, 0, 8) * (1 - ramp(t, 100, 112));
                float pull = (float) Math.sin(t * .35f) * .15f * s;
                p.r("armL", -1.3f * s + pull, 0, .5f * s).r("armR", -1.3f * s - pull, 0, -.5f * s).r("head", .25f * s, 0, 0)
                    .o("body", 0, -.3f * s, 0);
            }
        };
    }
    /** Telekinesis: the target is lifted, flung left and right, then slammed into a ring of bones. */
    static Attack telekinesis() {
        return new Attack("dusttale_telekinesis", 92) {
            public void tick(AttackCtx c, int t) {
                Vec3 r = side(c.tpos().subtract(c.pos()));
                if (t == 10) { yank(c, new Vec3(0, 1.05, 0)); c.sound(SoundEvents.ILLUSIONER_CAST_SPELL, 1, .7f); }
                if (t == 22) yank(c, r.scale(1.15).add(0, .25, 0));
                if (t == 32) yank(c, r.scale(-1.3).add(0, .3, 0));
                if (t == 44) { yank(c, new Vec3(0, -2.2, 0)); c.sound(SoundEvents.ANVIL_LAND, 1, .5f); }
                if (t >= 10 && t < 48 && t % 2 == 0) c.particle(dust(PURPLE, 1.3f), c.tpos(), 6, .35, .02);
                if (t == 50 || t == 55 || t == 60) {
                    int k = (t - 50) / 5; putV(c, "dsT", t == 50 ? c.target.position() : getV(c, "dsT"));
                    spikeRing(c, getV(c, "dsT"), 1.4 + k * 1.2, 8 + 4 * k, k * .3, -1, 1.6f - k * .2f, k == 1 ? PURPLE : W, 5, 4, 7);
                    c.particle(ParticleTypes.CLOUD, getV(c, "dsT"), 10, 1 + k, .02);
                }
                if (t == 66) for (int k = 0; k < 4; k++) { Vec3 at = c.target.position().add(ang(k * PI / 2 + .78).scale(5)).add(0, 1, 0);
                    lance(c, at, c.aim(at).scale(.8), 1.6f, PURPLE, 4, 6, 24); }
            }
            public void pose(Pose p, float t, UtMob m) {
                float lift = env(t, 0, 10, 20, 24), sw1 = env(t, 18, 22, 28, 32), sw2 = env(t, 28, 32, 40, 44), slam = env(t, 40, 44, 54, 64);
                p.r("armR", -1.4f * lift - 2.4f * slam - 1.3f * (sw1 + sw2), 0, -.5f * lift + .8f * sw1 - .9f * sw2)
                    .r("armR", 1.8f * slam, 0, 0)
                    .r("body", .25f * slam, .3f * sw1 - .3f * sw2, 0).r("head", -.2f * lift + .3f * slam, 0, 0)
                    .r("armL", .15f, 0, .2f);
            }
        };
    }
    /** Purple-beamed, red-eyed blasters in growing formations, ending in one huge blaster over his shoulder. */
    static Attack purpleBlasters() {
        return new Attack("dusttale_purple_blasters", 104) {
            public void tick(AttackCtx c, int t) {
                c.face(c.tpos());
                int[] waves = {0, 22, 44};
                for (int w = 0; w < 3; w++) if (t == waves[w]) {
                    int n = 2 + w * 2; double base = c.rnd.nextDouble() * PI;
                    for (int k = 0; k < n; k++) {
                        Vec3 at = c.tpos().add(ang(base + k * 2 * PI / n).scale(6.5)).add(0, 1.8 + (k % 2) * .8, 0);
                        gb(c, at, c.tpos(), 16, 9, 6, .9f, 16, 0xeaeaea, RED, PURPLE);
                    }
                }
                for (int w : waves) if (t == w + 16) boom(c, c.tpos());
                if (t == 66) {
                    Vec3 at = c.pos().add(c.right().scale(-1.6)).add(0, 3.2, 0);
                    gb(c, at, c.tpos(), 22, 12, 9, 2.4f, 28, 0xf4f4f4, RED, DEEP);
                    deco(c, at.add(0, 1.6, 0), new Vec3(0, 1, 0), .6f, .6f, .6f, PURPLE, 40);
                }
                if (t == 88) { boom(c, c.tpos()); c.particle(dust(DEEP, 2), c.tpos(), 40, 1.5, .1); }
            }
            public void pose(Pose p, float t, UtMob m) {
                float a = 0;
                for (int w : new int[]{0, 22, 44}) a += env(t, w, w + 4, w + 10, w + 16);
                float big = env(t, 60, 66, 90, 100);
                p.r("armR", -2.0f * a - 2.8f * big, 0, -.3f * a).r("armL", -2.8f * big, 0, .4f * big)
                    .r("head", -.25f * big, 0, .15f * a).o("body", 0, .8f * big, 0);
            }
        };
    }
    /** Three lunging slashes with a sharpened bone, each preceded by a thin purple telegraph. */
    static Attack boneKnife() {
        return new Attack("dusttale_bone_knife", 74) {
            public void tick(AttackCtx c, int t) {
                c.face(c.tpos());
                for (int s : new int[]{4, 24, 44}) {
                    if (t == s) { Vec3 d = hz(c.tpos().subtract(c.pos())); putV(c, "dsK", d);
                        beam(c, c.pos().add(0, .9, 0), d, 7, .1f, PURPLE, 10, 1, 0); c.sound(SoundEvents.ITEM_BREAK, .6f, .6f); }
                    if (t == s + 10) { c.dash(getV(c, "dsK").scale(1.5).add(0, .1, 0)); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .7f); }
                    if (t == s + 13) {
                        Vec3 d = getV(c, "dsK"), at = c.pos().add(d.scale(1.4)).add(0, .9, 0);
                        p(c, at, PURPLE).face(d).box(3.2f, .18f, 1.4f).dmg(7).life(5).pierce().spawn();
                        p(c, at.add(0, .3, 0), W).face(d).box(2.4f, .1f, .9f).dmg(0).life(4).pierce().spawn();
                        c.particle(ParticleTypes.SWEEP_ATTACK, at, 3, .6, 0); c.particle(dust(0x9d968c, 1.2f), c.pos().add(0, .5, 0), 12, .4, .05);
                    }
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                for (int s : new int[]{4, 24, 44}) {
                    float back = env(t, s, s + 8, s + 10, s + 12), sw = env(t, s + 10, s + 13, s + 15, s + 20);
                    p.r("armR", -1.2f * back - 1.6f * sw, .8f * back - 1.6f * sw, -.6f * back)
                        .r("body", .3f * sw, .5f * back - .7f * sw, 0).r("legL", -.5f * sw, 0, 0).r("legR", .4f * sw, 0, 0);
                }
            }
        };
    }
    /** Phantom Papyrus looms behind the target: his hands clap shut, then his skull spits bones. */
    static Attack phantomAssist() {
        return new Attack("dusttale_phantom_assist", 100) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) {
                    Vec3 tp = c.target.position(), f = hz(tp.subtract(c.pos())), head = tp.add(f.scale(3.5)).add(0, 3.6, 0);
                    putV(c, "dsP", head); papHead(c, head, f.reverse(), 96);
                    c.particle(ParticleTypes.SOUL, head, 20, .6, .03); c.sound(SoundEvents.SKELETON_AMBIENT, 1, .7f);
                }
                Vec3 head = getV(c, "dsP");
                if (t == 6) {
                    Vec3 r = side(c.tpos().subtract(head));
                    for (int s = -1; s <= 1; s += 2) {
                        Vec3 at = c.tpos().add(r.scale(s * 4.5));
                        p(c, at, 0xd8d8e8).vel(r.scale(-s * .3)).box(.9f, 1.1f, .35f).dmg(6).warn(10).life(16).pierce().spawn();
                        decoV(c, at.add(0, .7, 0), r.scale(-s * .3), .7f, .5f, .2f, 0xd8d8e8, 16);
                    }
                }
                if (t == 21) { c.sound(SoundEvents.ANVIL_PLACE, .8f, 1.6f); c.particle(ParticleTypes.CRIT, c.tpos(), 20, .3, .4); }
                if (t >= 34 && t < 82 && t % 4 == 2) {
                    Vec3 aim = c.aim(head).add(c.rnd.nextGaussian() * .08, 0, c.rnd.nextGaussian() * .08);
                    lance(c, head.add(0, -.6, 0), aim.scale(.7), 1.3f, (t / 4) % 3 == 0 ? OR : W, 4, 2, 30);
                    c.sound(SoundEvents.SKELETON_SHOOT, .5f, 1.6f);
                }
                if (t == 86) c.sound(SoundEvents.SKELETON_AMBIENT, 1, 1.4f);
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = ramp(t, 0, 10) * (1 - ramp(t, 88, 100));
                p.r("armL", -2.4f * s, 0, .5f * s).r("head", .35f * s, 0, -.2f * s)
                    .o("head", (float) Math.sin(t * .9f) * .2f * s, 0, 0).r("armR", .2f, 0, -.2f);
            }
        };
    }
    /** He finds the SAVE star and crushes it: gold shards burst out and darkness falls while bones jut blindly. */
    static Attack saveBreak() {
        return new Attack("dusttale_save_break", 84) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) {
                    Vec3 star = c.pos().add(c.tpos().subtract(c.pos()).scale(.5)).add(0, 1.4, 0); putV(c, "dsS", star);
                    Vec3 f = c.fwd();
                    deco(c, star, f, .9f, .25f, .25f, GOLD, 26); deco(c, star, f, .25f, .9f, .25f, GOLD, 26);
                    deco(c, star, f.yRot(.78f), .55f, .55f, .2f, 0xfff4a0, 26);
                    c.sound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, .7f);
                }
                Vec3 star = getV(c, "dsS");
                if (t < 24 && t % 3 == 0) c.particle(ParticleTypes.END_ROD, star, 2, .3, .02);
                if (t == 24 || t == 30) {
                    c.sound(SoundEvents.GLASS_BREAK, 1.2f, t == 24 ? .7f : .5f);
                    for (int k = 0; k < 12; k++) {
                        Vec3 d = ang(k * PI / 6 + (t == 30 ? PI / 12 : 0)).add(0, t == 30 ? -.15 : .05, 0).normalize();
                        lance(c, star, d.scale(.55), .9f, t == 24 ? GOLD : RED, 4, 0, 26);
                    }
                    if (t == 24) { eff(c, MobEffects.DARKNESS, 70, 0); c.particle(ParticleTypes.FLASH, star, 1, 0, 0); }
                }
                if (t >= 36 && t < 76 && t % 5 == 1)
                    spike(c, predict(c, 6).add(c.rnd.nextGaussian() * .6, 0, c.rnd.nextGaussian() * .6), 1.8f, t % 10 == 1 ? PURPLE : W, 4, 6, 6);
                else if (t >= 30) tvel(c);
            }
            public void pose(Pose p, float t, UtMob m) {
                float reach = env(t, 0, 10, 20, 24), crush = env(t, 20, 24, 34, 44);
                p.r("armR", -1.5f * reach - 1.2f * crush, 0, -.2f * reach).o("armR", 0, 0, -1.5f * crush)
                    .r("head", .3f * crush, 0, 0).r("body", .15f * crush, 0, 0);
            }
        };
    }
    /** LV surge (below 60% HP): his eye flares red, dust rains, and bones fall from the dark onto predicted footing. */
    static Attack lvSurge() {
        return new Attack("dusttale_lv_surge", 92) {
            public void tick(AttackCtx c, int t) {
                Vec3 tp = predict(c, 8);
                if (t < 16) {
                    c.particle(dust(RED, 1.5f), c.eye().add(ang(t * .8).scale(.8)), 3, .1, .02);
                    if (t == 0) c.sound(SoundEvents.ELDER_GUARDIAN_CURSE, .6f, 1.5f);
                }
                if (t >= 16 && t < 80 && t % 3 == 0) {
                    Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3, 9, c.rnd.nextGaussian() * 3);
                    lance(c, at, new Vec3(0, -.75, 0), 1.6f, t % 9 == 0 ? RED : W, 4, 5, 18);
                    c.particle(dust(0x9d968c, 1f), at.add(0, -1, 0), 3, .3, 0);
                }
                if (t >= 20 && t < 80 && t % 15 == 5) spike(c, tp, 2.4f, RED, 6, 9, 6);
                if (t == 30 || t == 60) eff(c, MobEffects.WITHER, 40, 0);    // KARMA
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = ramp(t, 0, 8) * (1 - ramp(t, 82, 92));
                p.r("head", -.45f * s, (float) Math.sin(t * 1.3f) * .1f * s, 0).r("body", -.15f * s, 0, 0)
                    .r("armL", -.4f * s, 0, 1.0f * s).r("armR", -.4f * s, 0, -1.0f * s).o("body", 0, (float) Math.sin(t * 1.9f) * .3f * s, 0);
            }
        };
    }
    /** MAD TIME MODE (below 35% HP): four blinks around the target, each leaving a blaster; all fire as one. */
    static Attack madTime() {
        return new Attack("dusttale_mad_time", 132) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { c.sound(SoundEvents.WITHER_SPAWN, .5f, 1.6f); c.particle(dust(PURPLE, 2), c.pos().add(0, 1, 0), 50, .8, .1);
                    c.particle(dust(RED, 2), c.pos().add(0, 1, 0), 30, .8, .1); }
                for (int k = 0; k < 4; k++) if (t == 10 + k * 18) {
                    Vec3 old = c.pos().add(0, 1.6, 0);
                    gb(c, old, c.tpos(), 100 - t, 14, 10, 1.4f, 20, 0xeaeaea, RED, PURPLE);
                    tele(c, c.target.position().add(ang(k * PI / 2 + .6).scale(7)));
                    spikeRing(c, c.tpos(), 3 - k * .4, 10, k * .5, c.rnd.nextInt(10), 1.4f, k % 2 == 0 ? PURPLE : W, 5, 10, 6);
                }
                if (t == 100) { boom(c, c.tpos()); c.particle(dust(PURPLE, 2.5f), c.tpos(), 50, 2, .1); }
                if (t > 102 && t < 126 && t % 2 == 0)
                    lance(c, c.target.position().add(c.rnd.nextGaussian() * 2.5, 8, c.rnd.nextGaussian() * 2.5), new Vec3(0, -.85, 0), 1.4f, PURPLE, 4, 4, 14);
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = ramp(t, 0, 6) * (1 - ramp(t, 124, 132)), shake = (float) Math.sin(t * 2.3f);
                p.r("armL", -.6f * s, 0, 1.4f * s).r("armR", -.6f * s, 0, -1.4f * s).r("head", .1f * s, 0, .35f * s + shake * .06f * s)
                    .o("body", shake * .2f * s, .4f * s, 0);
                for (int k = 0; k < 4; k++) { float b = env(t, 6 + k * 18, 9 + k * 18, 10 + k * 18, 13 + k * 18); p.o("body", 0, -6 * b, 0); }
            }
        };
    }

    // ================================================================ DUSTTALE: Phantom Papyrus
    static CharacterDef phantomPapyrus() {
        int g = 0xd4d4e6;
        return CharacterDef.of("dusttale_phantom_papyrus", "Phantom Papyrus", Faction.GENOCIDE_ROUTE).att(Attitude.HOSTILE).hp(70).keep(5).rest(22).speed(.34f)
            .egg(0xd4d4e6, 0xff2030).size(.8f, 1.6f)
            .model(new ModelDef()
                .ghost("body", null, 0, 16, 0, 1, 1, 1, g, .05f)
                .ghost("head", "body", 0, 4, 0, 8, 9, 7.5f, g, .7f)
                .ghost("brow", "head", 0, 3.4f, -3.5f, 8.4f, 1.2f, 1, 0xbdbdd2, .7f)
                .box("sockL", "head", -2, .8f, -3.8f, 2.4f, 2.4f, .2f, 0x080808)
                .box("sockR", "head", 2, .8f, -3.8f, 2.4f, 2.4f, .2f, 0x080808)
                .glow("eyeL", "head", -2, .8f, -3.95f, .9f, .9f, .3f, RED)
                .glow("eyeR", "head", 2, .8f, -3.95f, .9f, .9f, .3f, RED)
                .ghost("jaw", "head", 0, -5.2f, -.3f, 7, 2.2f, 6.5f, 0xc4c4d8, .7f)
                .box("teeth", "jaw", 0, .8f, -3.3f, 5.5f, .6f, .2f, 0x303040)
                .ghost("wisp1", "head", 0, -8, 1, 5, 3, 4, g, .35f)
                .ghost("wisp2", "head", 0, -10.5f, 2, 3, 2.5f, 2.5f, g, .18f)
                .limb("armL", "body", -9, 0, -2, 3.2f, 4, 2, 0xd0d0e0)
                .limb("armR", "body", 9, 0, -2, 3.2f, 4, 2, 0xd0d0e0)
                .box("glovL", "armL", 0, -4.5f, 0, 3.6f, 1.6f, 2.4f, 0xb33a3a)
                .box("glovR", "armR", 0, -4.5f, 0, 3.6f, 1.6f, 2.4f, 0xb33a3a))
            .attacks(phantomGrab(), phantomSpiral(), phantomBlue(), phantomNyeh()).build();
    }
    static void floaty(Pose p, float t) { p.o("body", 0, (float) Math.sin(t * .2f) * .8f, 0).r("head", 0, 0, (float) Math.sin(t * .13f) * .08f); }
    /** His hands clap shut on the target, then one slams down from above. */
    static Attack phantomGrab() {
        return new Attack("dusttale_phantom_grab", 64) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) {
                    Vec3 r = side(c.tpos().subtract(c.pos()));
                    for (int s = -1; s <= 1; s += 2) {
                        Vec3 at = c.tpos().add(r.scale(s * 4.5));
                        p(c, at, 0xd0d0e0).vel(r.scale(-s * .28)).box(1.0f, 1.2f, .4f).dmg(5).warn(10).life(17).pierce().spawn();
                        decoV(c, at.add(0, -.75, 0), r.scale(-s * .28), 1.1f, .35f, .5f, 0xb33a3a, 17);
                    }
                    c.sound(SoundEvents.SOUL_ESCAPE, 1, .8f);
                }
                if (t == 16) { c.sound(SoundEvents.ANVIL_PLACE, .7f, 1.8f); c.particle(ParticleTypes.CRIT, c.tpos(), 16, .3, .3); }
                if (t == 28) { Vec3 at = predict(c, 10).add(0, 5.5, 0); putV(c, "ppG", at);
                    p(c, at, 0xd0d0e0).vel(new Vec3(0, -.42, 0)).box(1.2f, 1.2f, .5f).dmg(6).warn(8).life(13).pierce().spawn(); }
                else tvel(c);
                if (t == 41) { Vec3 g = getV(c, "ppG"); g = new Vec3(g.x, ground(c), g.z);
                    spikeRing(c, g, 1.8, 10, 0, -1, 1.0f, W, 4, 3, 6); c.particle(ParticleTypes.SOUL, g, 14, .8, .05); }
            }
            public void pose(Pose p, float t, UtMob m) {
                floaty(p, t);
                float out = env(t, 0, 4, 12, 18), slam = env(t, 26, 30, 40, 48);
                p.o("armL", -4 * out, 0, -2 * out).o("armR", 4 * out, 0, -2 * out).r("armL", 0, 0, -1.4f * out).r("armR", 0, 0, 1.4f * out)
                    .o("armR", 0, 6 * slam, -4 * slam).r("head", -.3f * slam, 0, 0);
            }
        };
    }
    /** A spiral of bones pours out of his skull, alternating white and orange. */
    static Attack phantomSpiral() {
        return new Attack("dusttale_phantom_spiral", 72) {
            public void tick(AttackCtx c, int t) {
                Vec3 head = c.pos().add(0, 2.0, 0);
                if (t == 0) c.sound(SoundEvents.SKELETON_AMBIENT, 1, .6f);
                if (t >= 8 && t < 60 && t % 2 == 0) {
                    for (int arm = 0; arm < 2; arm++) {
                        double a = t * .32 + arm * PI;
                        lance(c, head, ang(a).add(0, -.12, 0).normalize().scale(.48), 1.1f, ((t / 2) % 4 == 0) ? OR : W, 4, 0, 34);
                    }
                    if (t % 6 == 0) c.sound(SoundEvents.SKELETON_SHOOT, .4f, 1.4f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                floaty(p, t); float s = env(t, 0, 8, 60, 70);
                p.r("head", -.25f * s, t * .0f, 0).o("head", 0, (float) Math.sin(t * 1.6f) * .3f * s, 0).o("armL", -2 * s, 2 * s, 0).o("armR", 2 * s, 2 * s, 0);
            }
        };
    }
    /** "YOU'RE BLUE NOW": gravity slam, then blue and white bone walls sweep across with a single gap. */
    static Attack phantomBlue() {
        return new Attack("dusttale_phantom_blue", 76) {
            public void tick(AttackCtx c, int t) {
                if (t == 6) { yank(c, new Vec3(0, -1.6, 0)); eff(c, MobEffects.SLOW_FALLING, 1, 0); c.sound(SoundEvents.ANVIL_LAND, .9f, .7f);
                    c.particle(dust(0x3fa7ff, 1.5f), c.tpos(), 20, .4, .05); }
                if (t >= 12 && t < 64 && (t - 12) % 13 == 0) {
                    Vec3 f = hz(c.tpos().subtract(c.pos())), r = side(f); int s = ((t - 12) / 13) % 2 == 0 ? 1 : -1, gap = c.rnd.nextInt(5) - 2;
                    for (int k = -3; k <= 3; k++) {
                        if (k == gap) continue;
                        Vec3 at = c.target.position().add(r.scale(s * 7)).add(f.scale(k * .8)).add(0, .8, 0);
                        post(c, at, r.scale(-s * .5), 1.6f, Math.abs(k - gap) == 1 ? W : BL, 4, 8, 32);
                    }
                    c.sound(SoundEvents.SKELETON_SHOOT, .6f, 1f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                floaty(p, t); float s = env(t, 0, 6, 8, 16);
                p.o("armR", 0, 5 * s, -3 * s).o("armL", 0, 5 * s, -3 * s).r("head", .3f * s, 0, 0);
            }
        };
    }
    /** "NYEH HEH HEH": miniature phantom skulls rain onto the target's predicted path. */
    static Attack phantomNyeh() {
        return new Attack("dusttale_phantom_nyeh", 70) {
            public void tick(AttackCtx c, int t) {
                if (t == 0 || t == 20 || t == 40) c.sound(SoundEvents.SKELETON_AMBIENT, 1, 1.5f + t * .005f);
                Vec3 pr = predict(c, 12);
                if (t >= 8 && t < 62 && t % 4 == 0) {
                    Vec3 at = pr.add(c.rnd.nextGaussian() * 1.3, 7, c.rnd.nextGaussian() * 1.3);
                    p(c, at, 0xd0d0e0).vel(new Vec3(0, -.6, 0)).box(.55f, .55f, .55f).dmg(4).warn(6).life(18).spawn();
                    decoV(c, at.add(0, -.1, -.28), new Vec3(0, -.6, 0), .3f, .1f, .1f, RED, 18);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                floaty(p, t); float s = env(t, 0, 4, 60, 70), laugh = (float) Math.sin(t * 1.4f);
                p.r("head", -.35f * s + laugh * .1f * s, 0, 0).o("jaw", 0, -.8f * s * Math.abs(laugh), 0).r("armL", 0, 0, -.8f * s).r("armR", 0, 0, .8f * s);
            }
        };
    }

    /** EXP Drain (low HP): grey dust is sucked out of the target into his hoodie; he heals off every LV he has stolen, ringed by purple bones. */
    static Attack expDrain() {
        return new Attack("dusttale_exp_drain", 90) {
            public void tick(AttackCtx c, int t) {
                Vec3 me = c.pos().add(0, 1.1, 0);
                if (t == 0) { c.sound(SoundEvents.SOUL_ESCAPE, 1, .5f); spikeRing(c, c.pos(), 2.6, 12, 0, -1, 1.6f, PURPLE, 0, 10, 70); }
                if (t >= 10 && t < 70) {
                    Vec3 from = c.tpos(), d = me.subtract(from);
                    c.particle(dust(0x9d968c, 1.3f), from.add(d.scale((t % 10) / 10.0)), 3, .15, .01);
                    if (t % 10 == 0) { c.hit(c.target, 2); c.me.heal(3); c.particle(ParticleTypes.SOUL, me, 4, .3, .02); c.sound(SoundEvents.SAND_BREAK, .7f, .7f + t * .01f); }
                }
                if (t == 72) { spikeRing(c, c.pos(), 4.5, 16, .2, -1, 1.4f, W, 5, 6, 8); c.sound(SoundEvents.BONE_BLOCK_BREAK, 1, .7f); }
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = env(t, 0, 10, 68, 76), pull = (float) Math.sin(t * .6f) * .15f * s;
                p.r("armL", -1.5f * s, -.6f * s, 0).r("armR", -1.5f * s, .6f * s, 0).r("armL", pull, 0, 0).r("head", -.3f * s, 0, 0).o("body", 0, .5f * s, 0);
            }
        };
    }

    // ================================================================ DUSTTALE: Papyrus (alive, 327th genocide run; Sans kills him for his LV)
    static CharacterDef dusttalePapyrus() {
        return CharacterDef.of("dusttale_papyrus", "Papyrus (Dusttale)", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(90).keep(6).rest(18).speed(.3f)
            .egg(0xeeeeee, 0xd42a2a).size(.7f, 2.3f)
            .model(papyrusModel(0xeeeeee, 0xd42a2a, 0xf6f6f2, 0xc23030, 0))
            .attacks(dpGauntlet(), dpBlue(), dpBelieve()).build();
    }
    /** Bone gauntlet: lanes of white bones with blue posts slide past from alternating sides while he points the way. */
    static Attack dpGauntlet() {
        return new Attack("dusttale_papyrus_gauntlet", 80) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) c.sound(SoundEvents.SKELETON_AMBIENT, 1, 1.3f);
                if (t >= 10 && t < 66 && (t - 10) % 14 == 0) {
                    Vec3 f = hz(c.tpos().subtract(c.pos())), r = side(f); int w = (t - 10) / 14, s = w % 2 == 0 ? 1 : -1;
                    for (int k = -2; k <= 2; k++) {
                        float h = w == 3 && k % 2 == 0 ? 1.8f : 1f;
                        post(c, c.target.position().add(r.scale(s * 6)).add(f.scale(k * .9)).add(0, h / 2, 0), r.scale(-s * .42), h, (k + w) % 3 == 0 ? BL : W, 3, 8, 34);
                    }
                    c.sound(SoundEvents.SKELETON_SHOOT, .6f, 1.2f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                for (int k = 0; k < 4; k++) { float pt = env(t, 4 + k * 14, 8 + k * 14, 12 + k * 14, 16 + k * 14);
                    p.r(k % 2 == 0 ? "armR" : "armL", -1.5f * pt, 0, (k % 2 == 0 ? -.6f : .6f) * pt).r("head", -.15f * pt, 0, 0); }
            }
        };
    }
    /** Blue attack: gravity slam, then low bones to hop and high blue bones to stand still under. */
    static Attack dpBlue() {
        return new Attack("dusttale_papyrus_blue_attack", 72) {
            public void tick(AttackCtx c, int t) {
                if (t == 8) { yank(c, new Vec3(0, -1.4, 0)); c.sound(SoundEvents.ANVIL_LAND, .8f, 1.1f); c.particle(dust(0x3fa7ff, 1.4f), c.tpos(), 18, .4, .05); }
                if (t >= 14 && t < 60 && (t - 14) % 9 == 0) {
                    Vec3 f = hz(c.tpos().subtract(c.pos())), at = c.pos().add(f.scale(1.5));
                    if ((t - 14) % 18 == 0) bar(c, at.add(0, .3, 0), f.scale(.45), 3.2f, W, 3, 4, 30);
                    else bar(c, at.add(0, 1.1, 0), f.scale(.45), 3.2f, BL, 3, 4, 30);
                    c.sound(SoundEvents.SKELETON_SHOOT, .5f, 1.4f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                float slam = env(t, 2, 7, 9, 14), proud = env(t, 14, 18, 62, 70);
                p.r("armR", -2.8f * slam + 2.8f * env(t, 7, 8, 9, 12), 0, 0).r("armL", -1.2f * proud, 0, .9f * proud).r("armR", -1.2f * proud, 0, -.9f * proud).r("head", -.2f * proud, 0, 0);
            }
        };
    }
    /** "I STILL BELIEVE IN YOU": he refuses to fight back; a ring of harmless bones turns into a heal. */
    static Attack dpBelieve() {
        return new Attack("dusttale_papyrus_believe", 60) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .8f);
                if (t < 40 && t % 4 == 0) { Vec3 a = ang(t * .16).scale(2.4); deco(c, c.tpos().add(a), a, .24f, 1.2f, .24f, W, 20); }
                if (t == 40) { eff(c, MobEffects.REGENERATION, 60, 0); c.particle(ParticleTypes.HEART, c.tpos().add(0, .8, 0), 8, .5, .05); c.sound(SoundEvents.PLAYER_LEVELUP, .6f, 1.4f); }
            }
            public boolean usable(UtMob m) { return m.hpFrac() < .6f; }
            public void pose(Pose p, float t, UtMob m) {
                float s = env(t, 0, 10, 50, 60);
                p.r("armL", -.4f * s, 0, 1.1f * s).r("armR", -.4f * s, 0, -1.1f * s).r("head", .2f * s, 0, .25f * s).o("body", 0, (float) Math.sin(t * .3f) * .2f * s, 0);
            }
        };
    }

    // ================================================================ DUSTTALE: Undyne (fights Murder Sans; her determination makes her undying)
    static final int SPEAR = 0x3cc8ff;
    /** Cyan energy spear flying point-first, with a pale tip. */
    static void spear(AttackCtx c, Vec3 at, Vec3 vel, float dmg, int warn, int life) {
        lance(c, at, vel, 1.8f, SPEAR, dmg, warn, life);
        decoV(c, at.add(vel.normalize().scale(.95)), vel, .28f, .28f, .28f, 0xbff6ff, life);
    }
    /** Spear bursting straight up out of the ground after a telegraph. */
    static void groundSpear(AttackCtx c, Vec3 at, float dmg, int warn) {
        p(c, new Vec3(at.x, ground(c) + .9, at.z), SPEAR).face(new Vec3(0, 1, 0)).box(.22f, .22f, 1.8f).dmg(dmg).warn(warn).life(warn + 8).pierce().spawn();
    }
    static CharacterDef dusttaleUndyne() {
        int armor = 0x3a3f4a, trim = 0xd8dde6, skin = 0x4a8fd8, hair = 0xd0202a;
        return CharacterDef.of("dusttale_undyne", "Undyne (Dusttale)", Faction.ROYAL_GUARD).att(Attitude.HOSTILE).hp(200).boss().keep(6).rest(12).speed(.36f)
            .egg(0x3a3f4a, 0x3cc8ff).size(.7f, 2.1f)
            .model(new ModelDef()
                .box("body", null, 0, 16, 0, 7.5f, 9, 4.8f, armor)
                .box("heart", "body", 0, .5f, -2.5f, 3.4f, 3, .3f, trim)
                .glow("heartCore", "body", 0, .5f, -2.7f, 1.6f, 1.4f, .2f, 0xff2a3a)
                .box("pauldL", "body", -4.6f, 3.6f, 0, 3, 2.2f, 5.4f, trim)
                .box("pauldR", "body", 4.6f, 3.6f, 0, 3, 2.2f, 5.4f, trim)
                .box("skirt", "body", 0, -5, 0, 8, 2, 5.2f, armor)
                .box("head", "body", 0, 9, 0, 6.5f, 7, 6.5f, skin)
                .box("finL", "head", -3.6f, 1.6f, .5f, .6f, 4, 3, 0x2a5fb0)
                .box("finR", "head", 3.6f, 1.6f, .5f, .6f, 4, 3, 0x2a5fb0)
                .box("hairTop", "head", 0, 3.9f, .4f, 6.9f, 1.2f, 6.9f, hair)
                .box("ponytail", "head", 0, 1.5f, 4.4f, 2.6f, 7, 2, hair)
                .box("patch", "head", -1.6f, .8f, -3.3f, 2.2f, 1.8f, .2f, 0x101010)
                .glow("eyeR", "head", 1.6f, .8f, -3.35f, 1.4f, 1.0f, .2f, 0xfff060)
                .box("grin", "head", 0, -1.8f, -3.3f, 4, .8f, .2f, 0xf0f0f0)
                .limb("armL", "body", -4.6f, 3, 0, 2.4f, 9, 2.4f, armor)
                .limb("armR", "body", 4.6f, 3, 0, 2.4f, 9, 2.4f, armor)
                .box("gauntL", "armL", 0, -8.5f, 0, 2.9f, 2, 2.9f, trim)
                .box("gauntR", "armR", 0, -8.5f, 0, 2.9f, 2, 2.9f, trim)
                .glow("heldSpear", "armR", 0, -9.5f, -2, .5f, .5f, 9, SPEAR)
                .limb("legL", null, -1.8f, 11.5f, 0, 2.8f, 11.5f, 3, armor)
                .limb("legR", null, 1.8f, 11.5f, 0, 2.8f, 11.5f, 3, armor)
                .box("bootL", "legL", 0, -10, -.4f, 3.3f, 3, 3.8f, trim)
                .box("bootR", "legR", 0, -10, -.4f, 3.3f, 3, 3.8f, trim))
            .attacks(ph(.5f, 1, undyneVolley()), undyneGreen(), undyneSpearRows(), undyneRing(), ph(0, .5f, undyneUndying()))
            .build();
    }
    /** Spear volley: a fan of spears forms over her head, flashes, and fires at where you're running (three throws). */
    static Attack undyneVolley() {
        return new Attack("dusttale_undyne_spear_volley", 70) {
            public void tick(AttackCtx c, int t) {
                if (t == 12 || t == 30 || t == 48) {
                    Vec3 aim = predict(c, 8), base = c.pos().add(0, 3.2, 0), r = side(aim.subtract(c.pos()));
                    for (int k = -2; k <= 2; k++) { Vec3 at = base.add(r.scale(k * .9)).add(0, -Math.abs(k) * .2, 0);
                        spear(c, at, aim.add(0, .9, 0).subtract(at).normalize().scale(.9), 5, 8, 30); }
                    c.sound(SoundEvents.TRIDENT_THROW, 1, 1.1f + t * .005f);
                } else tvel(c);
            }
            public void pose(Pose p, float t, UtMob m) {
                for (int s : new int[]{12, 30, 48}) { float w = env(t, s - 10, s - 2, s, s + 1), th = env(t, s, s + 2, s + 5, s + 12);
                    p.r("armR", -2.8f * w + .9f * th, 0, -.2f * w).r("body", .25f * th, .35f * w - .35f * th, 0).r("legL", -.3f * th, 0, 0); }
            }
        };
    }
    /** Green soul: you're slowed to a crawl and spears come at you from four sides with arrow telegraphs; yellow ones swerve round behind. */
    static Attack undyneGreen() {
        return new Attack("dusttale_undyne_green_soul", 96) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { eff(c, MobEffects.MOVEMENT_SLOWDOWN, 90, 4); c.particle(dust(0x30e040, 1.6f), c.tpos(), 24, .4, .05); c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.6f);
                    for (int k = 0; k < 4; k++) deco(c, c.tpos().add(ang(k * PI / 2).scale(1.1)), ang(k * PI / 2), .6f, .6f, .08f, 0x30e040, 90); }
                if (t >= 14 && t < 86 && (t - 14) % 6 == 0) {
                    int i = (t - 14) / 6; double a = c.rnd.nextInt(4) * PI / 2; boolean swerve = i % 4 == 3;
                    double from = swerve ? a + PI : a; int col = swerve ? 0xffe040 : SPEAR;
                    Vec3 at = c.tpos().add(ang(from).scale(9));
                    lance(c, at, c.tpos().subtract(at).normalize().scale(.55), 1.6f, col, 4, 6, 26);
                    deco(c, c.tpos().add(ang(from).scale(2)), ang(from), .4f, .05f, 1.2f, col, 6);
                    if (i % 2 == 0) c.sound(SoundEvents.TRIDENT_THROW, .6f, 1.5f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = env(t, 0, 8, 86, 96), beat = (float) Math.sin(t * 1.05f);
                p.r("armR", -1.8f * s, beat * .3f * s, 0).r("armL", -.6f * s, 0, .5f * s).r("body", 0, beat * .12f * s, 0).r("head", -.1f * s, 0, 0);
            }
        };
    }
    /** Rows of spears burst out of the ground in waves marching toward you, each with one gap. */
    static Attack undyneSpearRows() {
        return new Attack("dusttale_undyne_spear_rows", 84) {
            public void tick(AttackCtx c, int t) {
                if (t == 4) { putV(c, "duF", hz(c.tpos().subtract(c.pos()))); c.sound(SoundEvents.TRIDENT_RIPTIDE_1, 1, .9f); }
                if (t >= 14 && t < 70 && (t - 14) % 7 == 0) {
                    int row = (t - 14) / 7, gap = c.rnd.nextInt(7) - 3; Vec3 f = getV(c, "duF"), r = side(f), mid = c.pos().add(f.scale(2 + row * 1.3));
                    for (int k = -3; k <= 3; k++) if (k != gap) groundSpear(c, mid.add(r.scale(k * .9)), 5, 8);
                    c.particle(ParticleTypes.BUBBLE_POP, mid, 10, 1.5, .05);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = env(t, 2, 10, 70, 80), stab = env(t, 8, 12, 14, 20);
                p.r("armR", -3.0f * s + 1.4f * stab, 0, 0).r("armL", -3.0f * s + 1.4f * stab, 0, 0).r("body", .3f * stab, 0, 0).o("body", 0, -.6f * stab, 0);
            }
        };
    }
    /** Spear ring: a circle of spears appears around you, hangs, then all converge at once (twice, second ring denser). */
    static Attack undyneRing() {
        return new Attack("dusttale_undyne_spear_ring", 76) {
            public void tick(AttackCtx c, int t) {
                if (t == 8 || t == 40) {
                    Vec3 ctr = c.tpos(); int n = t == 8 ? 8 : 12; double rot = c.rnd.nextDouble() * PI, rad = t == 8 ? 5 : 4;
                    for (int k = 0; k < n; k++) { Vec3 at = ctr.add(ang(rot + k * 2 * PI / n).scale(rad)); spear(c, at, ctr.subtract(at).normalize().scale(rad / 22), 5, 14, 34); }
                    c.sound(SoundEvents.TRIDENT_RETURN, 1, .8f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                for (int s : new int[]{8, 40}) { float a = env(t, s - 6, s, s + 12, s + 20);
                    p.r("armR", -2.6f * a, 0, -.8f * a).r("armL", -2.6f * a, 0, .8f * a).r("head", -.3f * a, 0, 0); }
            }
        };
    }
    /** Undying (<50%): she refuses to die. Body dripping, she regenerates, aims a yellow sightline and hurls giant spears while spears rise around you. */
    static Attack undyneUndying() {
        return new Attack("dusttale_undyne_undying", 110) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { c.sound(SoundEvents.WARDEN_HEARTBEAT, 1.2f, .8f); c.me.heal(10); }
                if (t < 20) c.particle(dust(0xff2a3a, 1.2f), c.pos().add(0, 1.4, 0), 2, .3, .02);
                if (t % 5 == 0) c.particle(ParticleTypes.DRIPPING_WATER, c.pos().add(0, 1.2, 0), 3, .3, 0);
                boolean aimed = false;
                for (int s : new int[]{24, 54, 84}) {
                    if (t == s - 12) { aimed = true; Vec3 d = hz(predict(c, 12).subtract(c.pos())); putV(c, "duU", d);
                        p(c, c.pos().add(0, 1.2, 0).add(d.scale(9)), 0xffe040).face(d).box(.08f, .08f, 18).dmg(0).life(12).pierce().spawn(); }
                    if (t == s) { Vec3 d = getV(c, "duU");
                        p(c, c.pos().add(0, 1.2, 0), SPEAR).vel(d.scale(1.25)).box(.7f, .7f, 5).dmg(9).life(18).pierce().spawn();
                        c.sound(SoundEvents.TRIDENT_THUNDER, .8f, 1.6f); c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(d.scale(2)).add(0, 1.2, 0), 2, .2, 0); }
                }
                if (!aimed) tvel(c);
                if (t >= 30 && t < 100 && t % 10 == 0) groundSpear(c, c.target.position().add(c.rnd.nextGaussian() * 2.5, 0, c.rnd.nextGaussian() * 2.5), 4, 10);
            }
            public void pose(Pose p, float t, UtMob m) {
                float sh = (float) Math.sin(t * 2.3f) * .05f;
                p.r("head", sh, 0, sh).o("body", 0, -.3f, 0).r("armL", .3f, 0, .2f);
                for (int s : new int[]{24, 54, 84}) { float w = env(t, s - 12, s - 2, s, s + 1), th = env(t, s, s + 2, s + 4, s + 12);
                    p.r("armR", -3f * w + 1.2f * th, 0, 0).r("body", -.2f * w + .4f * th, .5f * w - .5f * th, 0).r("legR", .4f * th, 0, 0); }
            }
        };
    }

    // ================================================================ DUSTTALE: Frisk (the 327th genocide; Chara at the wheel)
    static CharacterDef dusttaleFrisk() {
        return CharacterDef.of("dusttale_frisk", "Frisk (Dusttale)", Faction.GENOCIDE_ROUTE).att(Attitude.HOSTILE).hp(110).keep(3).rest(14).speed(.38f)
            .egg(0x4a7fd0, 0xff2030).size(.55f, 1.3f).scale(.8f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 7, 7, 4, 0x4a7fd0)
                .box("stripe1", "body", 0, 1.5f, -2.05f, 7.1f, 1.2f, .2f, 0xc050c0)
                .box("stripe2", "body", 0, -1.5f, -2.05f, 7.1f, 1.2f, .2f, 0xc050c0)
                .glow("dust1", "body", 2, -.5f, -2.15f, 1, .8f, .2f, 0xb0aaa0)
                .box("head", "body", 0, 7.5f, 0, 7, 7, 7, 0xf2c8a0)
                .box("hair", "head", 0, 2.6f, .6f, 7.4f, 2.4f, 7.4f, 0x5a3418)
                .box("hairBack", "head", 0, -.6f, 3.4f, 7.4f, 5, 1, 0x5a3418)
                .glow("eyeL", "head", -1.6f, .2f, -3.55f, 1.2f, .5f, .2f, RED)
                .glow("eyeR", "head", 1.6f, .2f, -3.55f, 1.2f, .5f, .2f, RED)
                .box("smile", "head", 0, -1.8f, -3.55f, 3, .5f, .2f, 0x6a1010)
                .limb("armL", "body", -4.5f, 3, 0, 2, 6.5f, 2, 0x4a7fd0)
                .limb("armR", "body", 4.5f, 3, 0, 2, 6.5f, 2, 0x4a7fd0)
                .box("knife", "armR", 0, -7.5f, -1.6f, .3f, .8f, 3.6f, 0xd02020)
                .limb("legL", null, -1.6f, 6.5f, 0, 2.5f, 6.5f, 2.6f, 0x3a5aa0)
                .limb("legR", null, 1.6f, 6.5f, 0, 2.5f, 6.5f, 2.6f, 0x3a5aa0)
                .box("shoeL", "legL", 0, -6.2f, -.3f, 2.7f, 1, 3.2f, 0x5a3418)
                .box("shoeR", "legR", 0, -6.2f, -.3f, 2.7f, 1, 3.2f, 0x5a3418))
            .attacks(friskRealKnife(), friskDustKick(), friskReset()).build();
    }
    /** REAL KNIFE: three red slash lines flash over you, a dash, then the three slashes land in order. */
    static Attack friskRealKnife() {
        return new Attack("dusttale_frisk_real_knife", 58) {
            public void tick(AttackCtx c, int t) {
                if (t == 4) { Vec3 d = hz(c.tpos().subtract(c.pos())); putV(c, "dfK", d); putV(c, "dfP", c.tpos()); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .6f, .5f);
                    for (int k = 0; k < 3; k++) p(c, c.tpos().add(0, .2 * (k - 1), 0), RED).face(d.yRot((k - 1) * .7f)).box(.1f, .1f, 3).dmg(0).life(16 + k * 6).pierce().spawn(); }
                if (t == 16) c.dash(getV(c, "dfK").scale(1.2).add(0, .15, 0));
                for (int k = 0; k < 3; k++) if (t == 20 + k * 6) { Vec3 d = getV(c, "dfK"), at = getV(c, "dfP").add(0, .2 * (k - 1), 0);
                    p(c, at, RED).face(d.yRot((k - 1) * .7f)).box(.5f, .15f, 3).dmg(6).life(4).pierce().spawn();
                    c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, .8f + k * .1f); c.particle(ParticleTypes.SWEEP_ATTACK, at, 2, .3, 0); }
            }
            public void pose(Pose p, float t, UtMob m) {
                float w = env(t, 2, 12, 16, 18); p.r("armR", -2.4f * w, .6f * w, 0).r("body", 0, .4f * w, 0).r("legL", -.5f * w, 0, 0);
                for (int k = 0; k < 3; k++) { float s = env(t, 18 + k * 6, 20 + k * 6, 21 + k * 6, 24 + k * 6), dir = k % 2 == 0 ? 1 : -1;
                    p.r("armR", -1.6f * s, dir * 1.4f * s, 0).r("body", 0, -dir * .5f * s, 0); }
            }
        };
    }
    /** Kicks up a cloud of monster dust (blinding), blinks behind you and stabs from inside it. */
    static Attack friskDustKick() {
        return new Attack("dusttale_frisk_dust_kick", 52) {
            public void tick(AttackCtx c, int t) {
                if (t == 8) { c.particle(dust(0x9d968c, 2f), c.tpos(), 60, 1.2, .05); eff(c, MobEffects.BLINDNESS, 40, 0); c.sound(SoundEvents.SAND_BREAK, 1, .6f); }
                if (t == 22) tele(c, c.target.position().subtract(hz(c.target.getLookAngle()).scale(1.6)));
                if (t == 24) { Vec3 d = hz(c.tpos().subtract(c.pos())); p(c, c.pos().add(d).add(0, .9, 0), RED).face(d).box(.3f, .3f, 2).dmg(7).warn(4).life(10).pierce().spawn(); }
                if (t == 28) c.sound(SoundEvents.PLAYER_ATTACK_CRIT, 1, .7f);
            }
            public void pose(Pose p, float t, UtMob m) {
                float kick = env(t, 2, 7, 9, 14), stab = env(t, 24, 28, 31, 40);
                p.r("legR", -1.4f * kick, 0, 0).r("body", .2f * kick, 0, 0).r("armR", -1.6f * stab, 0, 0).r("body", .35f * stab, 0, 0);
            }
        };
    }
    /** RESET (<35%, twice per life): light gathers, the world flashes white, they reload a save and reappear somewhere else. */
    static Attack friskReset() {
        return new Attack("dusttale_frisk_reset", 50) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) c.sound(SoundEvents.BEACON_POWER_SELECT, 1, 1.4f);
                if (t < 24) c.particle(ParticleTypes.END_ROD, c.pos().add(0, .8, 0).add(ang(t * .5).scale(1.2)), 2, .05, .01);
                if (t == 24) { CompoundTag d = c.me.getPersistentData(); d.putInt("dfReset", d.getInt("dfReset") + 1);
                    c.me.heal(c.me.getMaxHealth() * .35f); c.particle(ParticleTypes.FLASH, c.pos().add(0, 1, 0), 1, 0, 0); eff(c, MobEffects.DARKNESS, 30, 0);
                    c.sound(SoundEvents.PLAYER_LEVELUP, 1, .5f); tele(c, c.target.position().add(ang(c.rnd.nextDouble() * 2 * PI).scale(5))); }
            }
            public boolean usable(UtMob m) { return m.hpFrac() < .35f && m.getPersistentData().getInt("dfReset") < 2; }
            public void pose(Pose p, float t, UtMob m) { float s = env(t, 0, 8, 22, 26); p.r("armL", -2.2f * s, 0, .3f * s).r("head", -.4f * s, 0, 0).o("body", 0, .4f * s, 0); }
        };
    }

    // ================================================================ LAST BREATH: Sans (phases 1, 1.5, 2, 2.5, 3, final)
    static final float P1 = .66f, P2 = .33f;
    static CharacterDef lastBreathSans() {
        int hoodie = 0x2f5fcf;
        return CharacterDef.of("last_breath_sans", "Last Breath Sans", Faction.UNDERGROUND).att(Attitude.HOSTILE).hp(260).boss().keep(8).rest(10).speed(.33f)
            .egg(0x2f5fcf, 0x3fe8ff).size(.7f, 1.12f).scale(.92f)
            .model(new ModelDef()
                .box("body", null, 0, 8, 0, 8.4f, 7, 5.2f, hoodie)
                .box("shirt", "body", 0, -.2f, -2.62f, 4.2f, 6.2f, .2f, 0xf0f0f0)
                .box("furL", "body", -3.2f, 3.6f, 0, 2.6f, 1.6f, 5.8f, 0xf4f4f4)
                .box("furR", "body", 3.2f, 3.6f, 0, 2.6f, 1.6f, 5.8f, 0xf4f4f4)
                .box("furB", "body", 0, 3.6f, 2.6f, 8.8f, 1.6f, 1.2f, 0xf4f4f4)
                .glow("slash", "body", 0, .4f, -2.78f, 7.2f, 1.2f, .3f, 0xc0101a)
                .glow("slashB", "body", 1.4f, -.6f, -2.8f, 3.5f, .8f, .3f, 0x7a0a10)
                .box("head", "body", 0, 9.5f, 0, 9, 7.5f, 7.5f, 0xf6f6f2)
                .box("sockL", "head", -2.1f, .6f, -3.8f, 2.4f, 2.2f, .2f, 0x050505)
                .box("sockR", "head", 2.1f, .6f, -3.8f, 2.4f, 2.2f, .2f, 0x050505)
                .glow("eyeL", "head", -2.1f, .6f, -3.95f, 1.1f, 1.1f, .3f, CYAN)
                .ghost("eyeTrail", "head", -3.2f, .9f, -3.95f, 1.6f, .5f, .3f, CYAN, .55f)
                .glow("eyeR", "head", 2.1f, .5f, -3.95f, .45f, .45f, .3f, 0xffffff)
                .box("grin", "head", 0, -2.2f, -3.85f, 5.8f, .8f, .2f, 0x1b1b1b)
                .glow("blood", "head", 2.6f, -3.0f, -3.9f, .6f, 1.4f, .2f, 0xb0101a)
                .box("crackL", "head", -2.3f, 2.8f, -3.85f, .5f, 2.4f, .2f, 0x101010)
                .box("crackR", "head", 2.4f, -1.0f, -3.85f, .5f, 1.6f, .2f, 0x101010)
                .limb("armL", "body", -5.3f, 3, 0, 2.6f, 6, 2.6f, hoodie)
                .limb("armR", "body", 5.3f, 3, 0, 2.6f, 6, 2.6f, hoodie)
                .box("handL", "armL", 0, -6.6f, 0, 2.1f, 1.6f, 2.1f, 0xeeeeee)
                .box("handR", "armR", 0, -6.6f, 0, 2.1f, 1.6f, 2.1f, 0xeeeeee)
                .limb("legL", null, -2, 5, 0, 3.2f, 5, 3.6f, 0x111111)
                .limb("legR", null, 2, 5, 0, 3.2f, 5, 3.6f, 0x111111)
                .box("stripeL", "legL", -1.65f, -2.5f, 0, .2f, 4.5f, 3.6f, 0xf0f0f0)
                .box("stripeR", "legR", 1.65f, -2.5f, 0, .2f, 4.5f, 3.6f, 0xf0f0f0)
                .box("slipL", "legL", 0, -5.3f, -.6f, 3.4f, 1.1f, 4.6f, 0xf08cb4)
                .box("slipR", "legR", 0, -5.3f, -.6f, 3.4f, 1.1f, 4.6f, 0xf08cb4))
            .attacks(
                ph(P1, 1, colorfulBlue()), ph(P1, 1, colorfulOrange()), ph(P1, 1, spinnerRicochet()), ph(P1, 1, spinmarrang()),
                ph(P1, 1, spinGimmick()), ph(P1, 1, slideTricks()), ph(P1, 1, blinkDilation()),
                ph(.55f, .8f, dogPile()), ph(.55f, .8f, barkshot()), ph(.55f, .8f, bungeeBones()),
                ph(P2, P1, aggressiveColors()), ph(P2, P1, suddenWaves()), ph(P2, P1, unexpectedAssist()), ph(P2, P1, shiftingBlink()),
                ph(P2, P1, vengefulSlash()), ph(P2, P1, forcefulCounter()), ph(P2, P1, screenTwist()), ph(P2, P1, slidingLegacy()),
                ph(.25f, .45f, struggleBones()), ph(.25f, .45f, struggleClones()),
                ph(0, P2, gasterHands()), ph(0, P2, crackedBlasters()), ph(0, P2, wingdingRain()), ph(0, P2, gasterRift()),
                ph(0, .12f, lastGravity()))
            .build();
    }
    /** Phase dressing shared by every Last Breath pose: chest slash from phase 2, skull cracks + head twitch in phase 3. */
    static void lb(Pose p, float t, UtMob m) {
        float f = m.hpFrac();
        if (f > P1) p.o("slash", 0, 0, 1.6f).o("slashB", 0, 0, 1.6f).o("blood", 0, 0, 1.2f);
        if (f > P2) p.o("crackL", 0, 0, 1.2f).o("crackR", 0, 0, 1.2f).o("eyeTrail", 0, 0, 1.2f);
        else p.r("head", 0, (float) Math.sin(t * 2.7f) * .18f, (float) Math.sin(t * 1.9f) * .06f).o("eyeL", 0, 0, (t % 6 < 3) ? 1.2f : 0);
    }
    static float pulse(float t, int at) { return env(t, at - 4, at, at + 2, at + 8); }

    /** Colorful Bones (Blue): warning box, then a blue wall marches out; a giant bone erupts under anyone standing still. */
    static Attack colorfulBlue() {
        return new Attack("last_breath_colorful_blue", 84) {
            public void tick(AttackCtx c, int t) {
                Vec3 f = hz(c.tpos().subtract(c.pos())), r = side(f);
                if (t == 0) { deco(c, c.pos().add(f.scale(2.4)).add(0, 1, 0), f, 4, 2, .08f, 0xffe040, 12); c.sound(SoundEvents.NOTE_BLOCK_PLING.get(), 1, 1.8f); }
                if (t == 12) {
                    for (int k = -5; k <= 5; k++) post(c, c.pos().add(f.scale(2.2)).add(r.scale(k * .7)).add(0, .7, 0), f.scale(.55), 1.4f, BL, 4, 3, 42);
                    c.sound(SoundEvents.SKELETON_SHOOT, 1, .9f);
                }
                if (t >= 20 && t < 72 && (t - 20) % 12 == 0) { spike(c, c.target.position(), 3.0f, W, 6, 9, 6); c.sound(SoundEvents.BONE_BLOCK_PLACE, 1, .7f); }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float th = env(t, 6, 12, 16, 24);
                p.r("armR", -1.6f * th, 0, -.1f * th).r("body", .1f * th, -.25f * th, 0);
            }
        };
    }
    /** Colorful Bones (Orange): grey bones jut out of him, turn orange and lunge; mini bone waves trace where you run. */
    static Attack colorfulOrange() {
        return new Attack("last_breath_colorful_orange", 80) {
            public void tick(AttackCtx c, int t) {
                Vec3 pv = predict(c, 8);
                if (t == 0) for (int k = 0; k < 8; k++) {
                    Vec3 d = ang(k * PI / 4).add(0, .25, 0).normalize();
                    deco(c, c.pos().add(0, 1, 0).add(d.scale(1.3)), d, .22f, .22f, 1.3f, GREY, 15);
                }
                if (t == 14) {
                    for (int k = 0; k < 8; k++) { Vec3 at = c.pos().add(0, 1, 0).add(ang(k * PI / 4).scale(1.3));
                        lance(c, at, c.aim(at).scale(.62), 1.3f, OR, 5, 2, 30); }
                    c.sound(SoundEvents.SKELETON_SHOOT, 1, 1.3f);
                }
                if (t == 28 || t == 38 || t == 48) {
                    Vec3 dv = hz(tvel(c).lengthSqr() > 1e-4 ? tvel(c) : c.fwd());
                    for (int k = 0; k < 4; k++) spike(c, pv.add(dv.scale(k * .7)), .9f, W, 4, 6, 6);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float flare = env(t, 0, 6, 12, 15), point = env(t, 12, 15, 24, 32);
                p.r("armL", -.3f * flare, 0, 1.2f * flare).r("armR", -.3f * flare - 1.5f * point, 0, -1.2f * flare).o("body", 0, .6f * flare, 0);
            }
        };
    }
    /** Spinner Ricochet: a spinner hovers over him; a blaster fires into it and the beam bounces at you. */
    static Attack spinnerRicochet() {
        return new Attack("last_breath_spinner_ricochet", 92) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) putV(c, "lbS", c.pos().add(0, 2.8, 0));
                Vec3 sp = getV(c, "lbS");
                if (t < 76 && t % 2 == 0) cross(c, sp, Vec3.ZERO, 1.6f, GREY, 0, 0, 2);
                for (int s : new int[]{8, 44}) {
                    if (t == s) {
                        Vec3 at = sp.add(side(c.tpos().subtract(c.pos())).scale(s == 8 ? -4.5 : 4.5)).add(0, .6, 0);
                        gb(c, at, sp, 16, 8, 0, .8f, at.distanceTo(sp) - .9, 0xf0f0f0, CYAN, W);
                    }
                    if (t == s + 16) { beam(c, sp, c.tpos().subtract(sp), 22, 1.0f, CYAN, 8, 8, 7); boom(c, sp); }
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float s = ramp(t, 0, 6) * (1 - ramp(t, 80, 92));
                p.r("armL", -2.7f * s, 0, .2f * s).r("armR", -1.4f * s, 0, -.3f * s).r("head", -.2f * s, 0, 0);
            }
        };
    }
    /** Spinmarrang: an orange cross-bone flies out, marks the spot, then homes back through you. */
    static Attack spinmarrang() {
        return new Attack("last_breath_spinmarrang", 72) {
            public void tick(AttackCtx c, int t) {
                for (int s : new int[]{4, 36}) {
                    if (t == s) {
                        Vec3 at = c.pos().add(0, 1, 0), v = hz(c.tpos().subtract(at)).yRot(s == 4 ? .35f : -.35f).scale(.6);
                        cross(c, at, v, 1.5f, OR, 5, 0, 16); putV(c, "lbM", at.add(v.scale(16)));
                        c.sound(SoundEvents.TRIDENT_THROW, 1, 1.4f);
                    }
                    if (t == s + 16) {
                        Vec3 e = getV(c, "lbM"); c.particle(ParticleTypes.END_ROD, e, 10, .3, .05);
                        cross(c, e, c.aim(e).scale(.75), 1.5f, OR, 5, 4, 26); c.sound(SoundEvents.TRIDENT_RETURN, 1, 1.4f);
                    }
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m);
                for (int s : new int[]{4, 36}) { float w = env(t, s - 6, s, s + 1, s + 6), th = env(t, s, s + 2, s + 4, s + 10);
                    p.r("armR", -2.2f * w - .8f * th, .6f * w - .9f * th, 0).r("body", 0, .3f * w - .4f * th, 0); }
            }
        };
    }
    /** Spin Gimmick: four blasters in a plus around you, then the plus rotated 45 degrees, then a full ring of eight. */
    static Attack spinGimmick() {
        return new Attack("last_breath_spin_gimmick", 92) {
            public void tick(AttackCtx c, int t) {
                int[][] waves = {{0, 4, 0}, {26, 4, 1}, {52, 8, 0}};
                for (int[] w : waves) {
                    if (t == w[0]) for (int k = 0; k < w[1]; k++) {
                        double a = k * 2 * PI / w[1] + w[2] * PI / 4 + c.fwd().x * 0;
                        gb(c, c.tpos().add(ang(a).scale(6.5)).add(0, .6, 0), c.tpos(), 18, 9, 6, 1.0f, 13, 0xf0f0f0, CYAN, W);
                    }
                    if (t == w[0] + 18) boom(c, c.tpos());
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m);
                for (int s : new int[]{0, 26, 52}) { float u = env(t, s, s + 4, s + 12, s + 20); p.r("armR", -2.4f * u, 0, -.4f * u).r("armL", -1.2f * u * (s == 52 ? 2 : 0), 0, .4f * u); }
            }
        };
    }
    /** Slide Tricks: white edge lanes slide across, five blue rows drive in, then an orange two-way slider. */
    static Attack slideTricks() {
        return new Attack("last_breath_slide_tricks", 104) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { putV(c, "lbF", hz(c.tpos().subtract(c.pos()))); putV(c, "lbC", c.target.position()); c.sound(SoundEvents.NOTE_BLOCK_BELL.get(), 1, 1.2f); }
                Vec3 f = getV(c, "lbF"), r = side(f), ctr = getV(c, "lbC");
                if (t == 0) for (int s = -1; s <= 1; s += 2) for (int k = -4; k <= 4; k++) spike(c, ctr.add(r.scale(s * 3.6)).add(f.scale(k * .8)), .8f, W, 3, 10, 10);
                if (t == 20) { for (int s = -1; s <= 1; s += 2) for (int k = -4; k <= 4; k++)
                    post(c, ctr.add(r.scale(s * 3.6)).add(f.scale(k * .8)).add(0, .4, 0), r.scale(-s * .34), .8f, W, 3, 0, 22);
                    c.sound(SoundEvents.SKELETON_SHOOT, 1, .8f); }
                if (t == 44) c.sound(SoundEvents.NOTE_BLOCK_BELL.get(), 1, 1.0f);
                if (t >= 46 && t < 66 && (t - 46) % 4 == 0) {
                    int row = (t - 46) / 4;
                    post(c, ctr.add(r.scale(7)).add(f.scale((row - 2) * 1.0)).add(0, .9, 0), r.scale(-.4), 1.8f, BL, 4, 4, 36);
                }
                if (t == 72) { for (int k = -4; k <= 4; k++) { int s = k < 0 ? 1 : -1;
                    post(c, ctr.add(r.scale(s * 6.5)).add(f.scale(k * .8)).add(0, .9, 0), r.scale(-s * .33), 1.8f, OR, 4, 6, 40); }
                    c.sound(SoundEvents.SKELETON_SHOOT, 1, 1.2f); }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float snap = pulse(t, 0) + pulse(t, 20) + pulse(t, 46) + pulse(t, 72);
                p.r("armR", -1.4f * snap, 0, -.3f * snap).r("head", 0, 0, .12f * snap);
            }
        };
    }
    /** Blink Dilation: short blink (leaving a blaster behind), a bone drops on you and ripples out in rings. */
    static Attack blinkDilation() {
        return new Attack("last_breath_blink_dilation", 84) {
            public void tick(AttackCtx c, int t) {
                if (t == 6) {
                    Vec3 old = c.pos().add(0, 1.6, 0); tele(c, c.target.position().add(ang(c.rnd.nextDouble() * 2 * PI).scale(5.5)));
                    gb(c, old, c.tpos(), 20, 8, 6, 1.1f, 16, 0xf0f0f0, CYAN, W);
                }
                if (t == 26) boom(c, c.tpos());
                if (t == 10) { Vec3 at = predict(c, 14); putV(c, "lbD", at); lance(c, at.add(0, 8.5, 0), new Vec3(0, -.6, 0), 2.2f, W, 6, 4, 14); }
                else tvel(c);
                for (int k = 0; k < 3; k++) if (t == 24 + k * 4) {
                    Vec3 g = getV(c, "lbD"); spikeRing(c, new Vec3(g.x, ground(c), g.z), 1.4 + k * 1.4, 8 + 4 * k, k * .25, -1, 1.1f, W, 4, 3, 5);
                    if (k == 0) { c.particle(ParticleTypes.CLOUD, g, 16, .6, .05); c.sound(SoundEvents.BONE_BLOCK_BREAK, 1, .6f); }
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float crouch = env(t, 0, 5, 6, 7), land = env(t, 6, 8, 12, 20);
                p.o("body", 0, -1.5f * crouch, 0).r("armL", -.5f * land, 0, 1.0f * land).r("armR", -.5f * land, 0, -1.0f * land);
            }
        };
    }
    // ---- phase 1.5: Artifact Frenzy (the Annoying Dog)
    static Attack dogPile() {
        return new Attack("last_breath_dog_pile", 84) {
            public void tick(AttackCtx c, int t) {
                for (int k = 0; k < 3; k++) {
                    int s = k * 20;
                    if (t == s) {
                        Vec3 at = predict(c, 14); putV(c, "lbP" + k, at);
                        deco(c, at.add(0, 1.1, 0), c.fwd(), .2f, .9f, .2f, RED, 16); deco(c, at.add(0, .35, 0), c.fwd(), .22f, .22f, .22f, RED, 16);
                        dog(c, at.add(0, 9.3, 0), new Vec3(0, -.6, 0), 4, 15, 4); c.sound(SoundEvents.WOLF_AMBIENT, 1, 1.6f);
                    } else if (t == s + 15) explode(c, getV(c, "lbP" + k).add(0, .4, 0), 5);
                }
                if (t % 20 != 0) tvel(c);
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float s = pulse(t, 2) + pulse(t, 22) + pulse(t, 42); p.r("armR", -2.6f * s, 0, -.2f * s).r("head", -.3f * s, 0, 0); }
        };
    }
    static Attack barkshot() {
        return new Attack("last_breath_barkshot", 74) {
            public void tick(AttackCtx c, int t) {
                for (int s : new int[]{0, 36}) {
                    Vec3 at = c.pos().add(c.right().scale(s == 0 ? 1.6 : -1.6)).add(0, 1.9, 0);
                    if (t == s) { Vec3 d = c.aim(at); deco(c, at, d, 1.1f, 1f, 1.2f, 0xf0f0f0, 14); deco(c, at.add(d.scale(.6)), d, .7f, .2f, .1f, CYAN, 14); }
                    if (t == s + 9) { p(c, at, RED).face(c.aim(at)).box(1.3f, 1.2f, 1.4f).dmg(0).warn(5).life(7).pierce().spawn(); c.sound(SoundEvents.CREEPER_PRIMED, 1, 1.5f); }
                    if (t == s + 16) {
                        explode(c, at, 0); Vec3 d = hz(c.tpos().subtract(at));
                        for (int k = -2; k <= 2; k++) dog(c, at.add(0, -1, 0), d.yRot(k * .28f).scale(.5).add(0, -.02, 0), 0, 30, 4);
                        c.sound(SoundEvents.WOLF_AMBIENT, 1, 1.3f);
                    }
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float a = env(t, 0, 4, 14, 18), b = env(t, 36, 40, 50, 54); p.r("armR", -1.2f * a, 0, -.8f * a).r("armL", -1.2f * b, 0, .8f * b); }
        };
    }
    static Attack bungeeBones() {
        return new Attack("last_breath_bungee_bones", 76) {
            public void tick(AttackCtx c, int t) {
                for (int s : new int[]{0, 34}) {
                    if (t == s) {
                        Vec3 g = predict(c, 20); putV(c, "lbB", g);
                        deco(c, g.add(0, 7.5, 0), new Vec3(0, 0, 1), .05f, 3.2f, .05f, 0xd0b080, 20);
                        dog(c, g.add(0, 5.3, 0), new Vec3(0, -.25, 0), 4, 20, 4); c.sound(SoundEvents.LEASH_KNOT_PLACE, 1, 1);
                    }
                    if (t == s + 20) {
                        Vec3 g = getV(c, "lbB").add(0, .35, 0);
                        for (int k = 0; k < 4; k++) dog(c, g, ang(PI / 4 + k * PI / 2).scale(.42), 0, 24, 4);
                        c.particle(ParticleTypes.POOF, g, 12, .3, .05); c.sound(SoundEvents.WOLF_AMBIENT, 1, 1.8f);
                    }
                }
                if (t % 34 != 0) tvel(c);
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); p.r("armR", -2.8f * (pulse(t, 2) + pulse(t, 36)), 0, 0); }
        };
    }
    // ---- phase 2
    static Attack aggressiveColors() {
        return new Attack("last_breath_aggressive_colors", 92) {
            public void tick(AttackCtx c, int t) {
                if (t < 40 && t % 8 == 0) {
                    Vec3 r = side(c.tpos().subtract(c.pos())); int s = (t / 8) % 2 == 0 ? 1 : -1; Vec3 at = c.tpos().add(r.scale(s * 7));
                    lance(c, at, c.aim(at).scale(.95), 2.0f, W, 5, 7, 18); c.sound(SoundEvents.ARROW_SHOOT, .8f, .7f);
                }
                if (t == 44) {
                    Vec3 f = hz(c.tpos().subtract(c.pos())), r = side(f);
                    for (int k = -6; k <= 6; k++) post(c, c.pos().add(f.scale(2)).add(r.scale(k * .65)).add(0, .75, 0), f.scale(.6), 1.5f, (k & 1) == 0 ? BL : W, 5, 3, 40);
                    c.sound(SoundEvents.SKELETON_SHOOT, 1, .8f);
                }
                if (t >= 52 && t < 88 && t % 10 == 2) spike(c, c.target.position(), 2.6f, W, 6, 8, 5);
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float s = 0; for (int k = 0; k < 5; k++) s += pulse(t, k * 8 + 2);
                float w = env(t, 38, 44, 48, 56);
                p.r("armR", -1.3f * s - 1.6f * w, 0, -.5f * s).r("armL", -1.6f * w, 0, .3f * w).r("body", .15f * w, 0, 0);
            }
        };
    }
    static Attack suddenWaves() {
        return new Attack("last_breath_sudden_waves", 96) {
            public void tick(AttackCtx c, int t) {
                for (int w = 0; w < 4; w++) {
                    int s = w * 20, dt = t - s;
                    if (dt == 0) {
                        Vec3 o = c.pos().add(c.tpos().subtract(c.pos()).scale(.3 + .1 * w)); o = new Vec3(o.x, ground(c), o.z);
                        Vec3 d = hz(c.tpos().subtract(o)).yRot((w - 1.5f) * .25f); putV(c, "lbW" + w, o); putV(c, "lbV" + w, d);
                        deco(c, o.add(0, .6, 0), d, .24f, 1.2f, .24f, GREY, 8);
                    }
                    if (dt >= 6 && dt < 18) {
                        Vec3 o = getV(c, "lbW" + w), d = getV(c, "lbV" + w), r = side(d);
                        for (int k = -1; k <= 1; k++) spike(c, o.add(d.scale((dt - 5) * .9)).add(r.scale(k * .7)), 1.0f + .1f * (dt - 6), W, 4, 2, 5);
                        if (dt == 6) c.sound(SoundEvents.BONE_BLOCK_BREAK, 1, .9f + w * .1f);
                    }
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); for (int w = 0; w < 4; w++) { float u = pulse(t, w * 20 + 2); p.r("armR", -1.0f * u, 0, -.9f * u).o("body", 0, -.5f * u, 0); } }
        };
    }
    static Attack unexpectedAssist() {
        return new Attack("last_breath_unexpected_assist", 76) {
            public void tick(AttackCtx c, int t) {
                for (int s : new int[]{0, 36}) {
                    if (t == s) { putV(c, "lbU", c.target.position()); double rot = s == 0 ? 0 : PI / 3;
                        for (int k = 0; k < 3; k++) { Vec3 at = c.target.position().add(ang(rot + k * 2 * PI / 3).scale(2.6)); ghostSans(c, at, c.target.position().subtract(at), 26, 0x6f9fff, CYAN); }
                        c.particle(ParticleTypes.SOUL_FIRE_FLAME, c.tpos(), 20, 1.6, .02); c.sound(SoundEvents.ILLUSIONER_MIRROR_MOVE, 1, 1.2f); }
                    if (t == s + 12) {
                        Vec3 ctr = getV(c, "lbU"); double rot = s == 0 ? 0 : PI / 3;
                        for (int k = 0; k < 3; k++) { Vec3 at = ctr.add(ang(rot + k * 2 * PI / 3).scale(2.6)), d = hz(ctr.subtract(at));
                            Vec3 mid = at.add(d.scale(1.6)).add(0, .9, 0);
                            p(c, mid, 0xbfe0ff).face(d).box(2.8f, .14f, .7f).dmg(6).warn(8).life(14).pierce().spawn(); }
                    }
                    if (t == s + 20) { c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1.1f); c.particle(ParticleTypes.SWEEP_ATTACK, c.tpos(), 6, 1, 0); }
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float a = env(t, 0, 4, 30, 36) + env(t, 36, 40, 60, 70); p.r("armL", -1.8f * a, 0, .6f * a).r("head", .2f * a, 0, 0); }
        };
    }
    static Attack shiftingBlink() {
        return new Attack("last_breath_shifting_blink", 84) {
            public void tick(AttackCtx c, int t) {
                if (t == 8) {
                    Vec3 old = c.pos().add(0, 1.6, 0), f = hz(c.tpos().subtract(c.pos()));
                    for (int k = 0; k < 4; k++) { Vec3 d = f.yRot((float) (k * PI / 2)); gb(c, old.add(d.scale(1.4)), old.add(d.scale(10)), 16, 10, 6, 1.0f, 18, 0xf0f0f0, CYAN, W); }
                    tele(c, c.target.position().add(ang(c.rnd.nextDouble() * 2 * PI).scale(4)));
                }
                if (t == 24) boom(c, c.pos());
                for (int s : new int[]{14, 26}) if (t == s) {
                    for (int k = 0; k < 16; k++) { if (k == (s == 14 ? 3 : 11) || k == (s == 14 ? 4 : 12)) continue;
                        Vec3 d = ang(k * PI / 8 + (s == 26 ? PI / 16 : 0)); post(c, c.pos().add(d.scale(.8)).add(0, .7, 0), d.scale(.42), 1.4f, s == 26 ? BL : W, 5, 3, 30); }
                    c.sound(SoundEvents.SKELETON_SHOOT, 1, 1);
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float land = env(t, 8, 12, 16, 30); p.r("armL", .2f * land, 0, 1.3f * land).r("armR", .2f * land, 0, -1.3f * land).o("body", 0, -1.2f * land, 0); }
        };
    }
    static Attack vengefulSlash() {
        return new Attack("last_breath_vengeful_slash", 62) {
            public void tick(AttackCtx c, int t) {
                c.face(c.tpos());
                if (t < 24 && t % 2 == 0) { double rad = 2.6 - t * .08; for (int k = 0; k < 8; k++) c.particle(ParticleTypes.END_ROD, c.pos().add(ang(k * PI / 4 + t * .2).scale(rad)).add(0, 1, 0), 1, 0, 0);
                    if (t == 0) c.sound(SoundEvents.BEACON_POWER_SELECT, 1, 1.6f); }
                Vec3 f = hz(c.tpos().subtract(c.pos()));
                if (t == 24) {
                    for (int k = 1; k <= 3; k++) p(c, c.pos().add(f.scale(1.2 + k * 1.3)).add(0, .5 + k * .35, 0), W).face(f).box(3.4f + k, .2f, 1.0f).dmg(9).life(6).pierce().spawn();
                    c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, .6f); c.sound(SoundEvents.WARDEN_SONIC_BOOM, .5f, 1.8f); c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(f.scale(3)).add(0, 1, 0), 5, 1, 0);
                }
                if (t == 38) { p(c, c.pos().add(f.scale(4)).add(0, 1.6, 0), W).face(f).box(.25f, 4, 6).dmg(9).warn(4).life(10).pierce().spawn(); c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, .7f); }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float ch = env(t, 0, 20, 23, 25), sw = env(t, 23, 25, 30, 36), up = env(t, 32, 38, 42, 50);
                p.r("armR", -.8f * ch - 1.8f * sw - 2.8f * up, 1.2f * ch - 1.4f * sw, 0).r("body", 0, .5f * ch - .7f * sw, 0).o("body", 0, -.8f * ch, 0);
            }
        };
    }
    static Attack forcefulCounter() {
        return new Attack("last_breath_forceful_counter", 82) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { Vec3 at = c.pos().add(0, 2.8, 0), f = c.fwd(); deco(c, at, f, .5f, .45f, .2f, 0x3fa7ff, 72);
                    deco(c, at.add(0, -.3, 0), f, .28f, .3f, .2f, 0x3fa7ff, 72); c.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1, 1.4f); }
                for (int k = 0; k < 3; k++) {
                    int s = 10 + k * 20, sgn = k % 2 == 0 ? 1 : -1; Vec3 r = side(c.tpos().subtract(c.pos())), f = hz(c.tpos().subtract(c.pos()));
                    if (t == s) { yank(c, r.scale(sgn * 1.4).add(0, .3, 0)); c.particle(dust(0x3fa7ff, 1.4f), c.tpos(), 14, .3, .05); c.sound(SoundEvents.ANVIL_LAND, .7f, 1.2f); }
                    if (t == s + 1) for (int j = -3; j <= 3; j++) spike(c, c.target.position().add(r.scale(sgn * 3.2)).add(f.scale(j * .7)), 2.2f, W, 5, 6, 8);
                    if (k == 2 && t == s + 2) gb(c, c.tpos().add(0, 6, 0), c.tpos(), 12, 8, 6, 1.2f, 10, 0xf0f0f0, CYAN, W);
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); for (int k = 0; k < 3; k++) { float u = pulse(t, 10 + k * 20); p.r("armR", -1.2f * u, 0, (k % 2 == 0 ? -1.4f : 1.2f) * u); } }
        };
    }
    /** Screen manipulation: the world "turns" (nausea) as gravity rotates right, up, left, down with homing blasters. */
    static Attack screenTwist() {
        return new Attack("last_breath_screen_twist", 104) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { eff(c, MobEffects.CONFUSION, 120, 0); c.sound(SoundEvents.PORTAL_TRIGGER, .5f, 1.8f); }
                Vec3 r = side(c.tpos().subtract(c.pos()));
                Vec3[] g = {r.scale(1.4).add(0, .2, 0), new Vec3(0, 1.2, 0), r.scale(-1.4).add(0, .2, 0), new Vec3(0, -2, 0)};
                for (int k = 0; k < 4; k++) {
                    int s = 10 + k * 20;
                    if (t == s) { yank(c, g[k]); c.particle(ParticleTypes.REVERSE_PORTAL, c.tpos(), 20, .4, .1); c.sound(SoundEvents.ANVIL_LAND, .6f, .9f + k * .1f); }
                    if (t == s + 4) {
                        Vec3 at = c.tpos().add(ang(c.rnd.nextDouble() * 2 * PI).scale(6)).add(0, 2.5, 0);
                        gb(c, at, predict(c, 12), 12, 8, 6, .9f, 16, 0xf0f0f0, CYAN, W);
                    } else tvel(c);
                    if (t == s + 2 && k != 1) for (int j = -3; j <= 3; j++) spike(c, c.target.position().add(g[k].multiply(1, 0, 1).normalize().scale(2.6)).add(side(g[k]).scale(j * .7)), 1.8f, W, 4, 6, 6);
                    if (t == s + 16) boom(c, c.tpos());
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float s = ramp(t, 0, 8) * (1 - ramp(t, 94, 104)), dir = (float) Math.sin((t - 10) * PI / 40);
                p.r("armR", -1.6f * s, 0, -1.0f * s * dir).r("body", 0, 0, .12f * s * dir).r("head", 0, 0, -.25f * s * dir);
            }
        };
    }
    static Attack slidingLegacy() {
        return new Attack("last_breath_sliding_legacy", 104) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { putV(c, "lbF", hz(c.tpos().subtract(c.pos()))); putV(c, "lbC", c.pos()); }
                Vec3 f = getV(c, "lbF"), r = side(f), me = getV(c, "lbC");
                if (t < 40 && t % 8 == 0) { int s = (t / 8) % 2 == 0 ? 1 : -1, gap = (t / 8) % 3 - 1;
                    for (int k = -2; k <= 2; k++) { if (k == gap) continue; post(c, c.target.position().add(r.scale(s * 7)).add(f.scale(k * .9)).add(0, .7, 0), r.scale(-s * .5), 1.4f, k == gap + 1 ? OR : W, 4, 4, 30); }
                    c.sound(SoundEvents.SKELETON_SHOOT, .7f, 1.1f); }
                if (t == 40) for (int k : new int[]{-4, -2, 2, 4}) gb(c, me.add(r.scale(k * 1.0)).add(0, 1.5, 0), me.add(r.scale(k * 1.0)).add(f.scale(20)).add(0, 1, 0), 18, 12, 6, .9f, 24, 0xf0f0f0, CYAN, W);
                if (t == 58) boom(c, me.add(f.scale(4)));
                if (t == 66) gb(c, me.add(0, 3.4, 0), me.add(f.scale(20)).add(0, 1, 0), 22, 14, 10, 3.0f, 26, 0xf8f8f8, CYAN, W);
                if (t == 88) { boom(c, me.add(f.scale(6))); c.particle(ParticleTypes.END_ROD, me.add(f.scale(6)).add(0, 1, 0), 30, 1.5, .2); }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float a = env(t, 36, 40, 56, 60), b = env(t, 60, 66, 90, 100); p.r("armL", -1.6f * a - 2.9f * b, 0, .9f * a).r("armR", -1.6f * a - 2.9f * b, 0, -.9f * a).o("body", 0, .9f * b, 0); }
        };
    }
    // ---- phase 2.5: Struggle
    static Attack struggleBones() {
        return new Attack("last_breath_struggle_bones", 74) {
            public void tick(AttackCtx c, int t) {
                if (t % 9 == 0 && t < 64) { Vec3 r = side(c.tpos().subtract(c.pos())); Vec3 at = c.tpos().add(r.scale(((t / 9) % 2 == 0 ? 1 : -1) * 6.5));
                    lance(c, at, c.aim(at).scale(.9), 1.8f, W, 5, 6, 18); }
                for (int s : new int[]{6, 26, 46}) if (t == s) {
                    float size = 1.2f + (s - 6) * .03f; Vec3 at = c.pos().add(0, 1, 0);
                    cross(c, at, c.aim(at).scale(.45), size, W, 5, 2, 36); c.sound(SoundEvents.TRIDENT_THROW, 1, .8f);
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); p.o("body", (float) Math.sin(t * 1.7f) * .3f, -.6f, 0).r("body", .25f, 0, 0).r("armL", .3f, 0, .3f);
                for (int s : new int[]{6, 26, 46}) p.r("armR", -1.8f * pulse(t, s), 0, 0); }
        };
    }
    static Attack struggleClones() {
        return new Attack("last_breath_struggle_clones", 84) {
            public void tick(AttackCtx c, int t) {
                Vec3 r = side(c.tpos().subtract(c.pos()));
                for (int k = 0; k < 3; k++) {
                    int s = k * 22;
                    if (t == s) { Vec3 pt = predict(c, 12), from = pt.add(r.scale(k % 2 == 0 ? -7 : 7)).add(0, 1.2, 0);
                        beam(c, from, pt.add(0, 1, 0).subtract(from), 14, .9f, W, 12, 8, 6); gb(c, from, pt.add(0, 1, 0), 12, 8, 0, .1f, .1, 0xf0f0f0, CYAN, W); }
                    if (t == s + 8) { Vec3 pt = predict(c, 10); putV(c, "lbK" + k, pt); Vec3 from = pt.add(r.scale(-8)).add(0, .6, 0);
                        dog(c, from, r.scale(.8), 2, 10, 4); deco(c, pt.add(0, 1.0, 0), r, .2f, .9f, .2f, RED, 10); c.sound(SoundEvents.WOLF_AMBIENT, 1, 1.7f); }
                    else if (t == s + 18) explode(c, getV(c, "lbK" + k).add(0, .5, 0), 5);
                    else tvel(c);
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); p.o("body", 0, -.6f, 0).r("body", .25f, 0, 0); for (int k = 0; k < 3; k++) p.r("armR", -1.4f * pulse(t, k * 22 + 2), 0, -.5f * pulse(t, k * 22 + 2)); }
        };
    }
    // ---- phase 3: W.D. Gaster's possession
    static Attack gasterHands() {
        return new Attack("last_breath_gaster_hands", 104) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) {
                    Vec3 r = side(c.tpos().subtract(c.pos()));
                    for (int s = -1; s <= 1; s += 2) { Vec3 at = c.tpos().add(r.scale(s * 4.2)).add(0, 1.6, 0); putV(c, s < 0 ? "lbHL" : "lbHR", at); gasterHand(c, at, c.tpos().subtract(at), 94); }
                    c.sound(SoundEvents.SCULK_SHRIEKER_SHRIEK, .6f, 1.8f);
                }
                if (t >= 16 && t < 86 && t % 3 == 0) {
                    Vec3 h = getV(c, (t / 3) % 2 == 0 ? "lbHL" : "lbHR"), v = c.aim(h).add(c.rnd.nextGaussian() * .12, c.rnd.nextGaussian() * .06, c.rnd.nextGaussian() * .12).normalize().scale(.5);
                    int shape = (t / 3) % 3; float w = shape == 0 ? .34f : shape == 1 ? .14f : .4f, hh = shape == 0 ? .34f : shape == 1 ? .44f : .14f;
                    p(c, h, (t / 3) % 4 == 0 ? VOID : W).vel(v).box(w, hh, .08f).dmg(3).warn(2).life(32).spawn();
                    if (t % 9 == 0) c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, .7f, .5f + c.rnd.nextFloat() * .3f);
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float s = ramp(t, 0, 10) * (1 - ramp(t, 90, 104));
                p.r("armL", -1.0f * s, 0, 1.2f * s).r("armR", -1.0f * s, 0, -1.2f * s).r("head", -.35f * s, 0, 0).o("body", 0, 1.5f * s, 0); }
        };
    }
    static Attack crackedBlasters() {
        return new Attack("last_breath_cracked_blasters", 112) {
            public void tick(AttackCtx c, int t) {
                if (t < 60 && t % 5 == 0) {
                    int k = t / 5; Vec3 at = c.tpos().add(ang(k * PI / 6).scale(7)).add(0, 1 + k * .25, 0);
                    gb(c, at, c.tpos(), 18, 8, 6, 1.1f, 15, 0x3a3a46, W, 0xe0e0ff);
                    deco(c, at.add(0, .3, 0), new Vec3(0, 1, 0), .08f, .8f, .08f, 0x0a0a0a, 32);
                }
                if (t >= 18 && t < 78 && t % 5 == 3) boom(c, c.tpos());
                if (t == 74) for (int k = 0; k < 4; k++) { Vec3 at = c.tpos().add(ang(k * PI / 2 + PI / 4).scale(3)).add(0, 7, 0);
                    gb(c, at, c.tpos().add(ang(k * PI / 2 + PI / 4).scale(-1)), 20, 12, 9, 2.2f, 14, 0x2a2a34, W, 0xe0e0ff); }
                if (t == 94) { boom(c, c.tpos()); c.particle(ParticleTypes.SCULK_SOUL, c.tpos(), 30, 2, .05); }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float s = ramp(t, 0, 8) * (1 - ramp(t, 100, 112)); p.r("armR", -2.6f * s, 0, -.3f * s).r("armL", -2.6f * s * ramp(t, 68, 74), 0, .3f * s); }
        };
    }
    static Attack wingdingRain() {
        return new Attack("last_breath_wingding_rain", 94) {
            public void tick(AttackCtx c, int t) {
                if (t % 20 == 0) { eff(c, MobEffects.DARKNESS, 12, 0); c.sound(SoundEvents.SCULK_CLICKING, 1, .5f); }
                if (t == 0) eff(c, MobEffects.CONFUSION, 90, 0);
                if (t < 84 && t % 2 == 0) {
                    Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3.5, 8, c.rnd.nextGaussian() * 3.5);
                    p(c, at, (t / 2) % 3 == 0 ? VOID : W).vel(new Vec3(0, -.5, 0)).box(.3f, .3f, .08f).dmg(3).warn(3).life(20).spawn();
                }
                if (t >= 10 && t < 84 && t % 14 == 10) {
                    Vec3 d = ang(c.rnd.nextDouble() * PI), r = side(d), ctr = predict(c, 8);
                    for (int k = -5; k <= 5; k++) spike(c, ctr.add(d.scale(k * .7)).add(r.scale(c.rnd.nextBoolean() ? .9 : -.9)), 1.2f, W, 4, 7, 5);
                } else tvel(c);
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float s = ramp(t, 0, 6); p.r("head", -.4f * s, 0, 0).r("armL", -.6f * s, 0, .9f * s).r("armR", -.6f * s, 0, -.9f * s).o("body", (float) Math.sin(t * 3.1f) * .25f, 0, 0); }
        };
    }
    /** Gaster's cracked face opens over the arena; rings of wingdings collapse inward with a rotating gap. */
    static Attack gasterRift() {
        return new Attack("last_breath_gaster_rift", 104) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) {
                    Vec3 at = c.target.position().add(0, 6.5, 0), f = hz(c.pos().subtract(at)); putV(c, "lbR", c.target.position());
                    deco(c, at, f, 2.2f, 3.0f, .4f, 0x18181e, 98);
                    deco(c, at.add(f.scale(.22)).add(side(f).scale(.5)).add(0, .3, 0), f, .5f, .6f, .05f, 0xe8e8e8, 98);
                    deco(c, at.add(f.scale(.22)).add(side(f).scale(-.5)).add(0, .3, 0), f, .5f, .6f, .05f, 0xe8e8e8, 98);
                    deco(c, at.add(f.scale(.24)).add(side(f).scale(.5)).add(0, 1.0, 0), f, .1f, .9f, .03f, 0x000000, 98);
                    deco(c, at.add(f.scale(.24)).add(side(f).scale(-.55)).add(0, -.5, 0), f, .1f, .9f, .03f, 0x000000, 98);
                    deco(c, at.add(f.scale(.22)).add(0, -.9, 0), f, 1.2f, .25f, .05f, 0xe8e8e8, 98);
                    c.sound(SoundEvents.WARDEN_EMERGE, .5f, 1.6f);
                }
                if (t >= 12 && t < 84 && (t - 12) % 12 == 0) {
                    int k = (t - 12) / 12; Vec3 ctr = c.target.position().add(0, 1, 0);
                    for (int j = 0; j < 18; j++) { if (j == k * 3 % 18 || j == (k * 3 + 1) % 18 || j == (k * 3 + 2) % 18) continue;
                        Vec3 at = ctr.add(ang(j * PI / 9 + k * .2).scale(8)); p(c, at, j % 3 == 0 ? VOID : W).vel(c.aim(at).scale(.3)).box(.34f, .34f, .08f).dmg(3).warn(4).life(30).spawn(); }
                    c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .4f);
                }
            }
            public void pose(Pose p, float t, UtMob m) { lb(p, t, m); float s = ramp(t, 0, 10) * (1 - ramp(t, 92, 104)); p.r("head", -.6f * s, 0, 0).r("armL", -2.9f * s, 0, .3f * s).r("armR", -2.9f * s, 0, -.3f * s); }
        };
    }
    /** Final attack (below 12%): gravity spam in every direction into bone walls, until he runs out of strength. */
    static Attack lastGravity() {
        return new Attack("last_breath_last_gravity", 150) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { c.sound(SoundEvents.BEACON_DEACTIVATE, 1, .5f); eff(c, MobEffects.CONFUSION, 140, 0); }
                if (t >= 8 && t < 124 && (t - 8) % 9 == 0) {
                    int k = (t - 8) / 9; Vec3 d = k % 3 == 2 ? new Vec3(0, -1, 0) : ang(c.rnd.nextInt(4) * PI / 2);
                    yank(c, d.y < 0 ? new Vec3(0, -2.2, 0) : d.scale(1.5).add(0, .25, 0));
                    c.sound(SoundEvents.ANVIL_LAND, .6f, .7f + k * .04f); c.particle(dust(0x3fa7ff, 1.5f), c.tpos(), 12, .3, .05);
                    if (d.y >= 0) { Vec3 r = side(d); for (int j = -3; j <= 3; j++) spike(c, c.target.position().add(d.scale(3.2)).add(r.scale(j * .7)), 2.4f, W, 5, 4, 6); }
                    else spikeRing(c, c.target.position(), 2.2, 10, k * .3, -1, 1.2f, W, 4, 5, 5);
                }
                if (t == 130) { c.sound(SoundEvents.SOUL_ESCAPE, 1, .5f); c.particle(dust(0x9d968c, 1.4f), c.pos().add(0, 1, 0), 30, .5, .02); }
            }
            public void pose(Pose p, float t, UtMob m) {
                lb(p, t, m); float s = ramp(t, 0, 8) * (1 - ramp(t, 124, 132)), tired = ramp(t, 124, 140);
                int k = (int) ((t - 8) / 9); float sw = env((t - 8) % 9, 0, 2, 4, 9) * s;
                p.r("armR", -1.4f * s - .6f * sw, 0, (k % 2 == 0 ? -1.2f : 1.0f) * sw).r("armL", -.4f * s, 0, .4f * s)
                    .o("body", 0, -2.2f * tired, 0).r("head", .5f * tired, 0, 0).r("armL", .3f * tired, 0, 0).r("armR", .3f * tired, 0, 0);
            }
        };
    }

    // ================================================================ shared Papyrus body (Dusttale Papyrus, Dust Papyrus)
    static ModelDef papyrusModel(int hoodie, int scarf, int eye, int boots, int dustSpecks) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 17, 0, 7, 9, 4.6f, hoodie)
            .box("armor", "body", 0, -.5f, -2.35f, 5, 7, .2f, 0xf0f0f0)
            .box("furL", "body", -2.6f, 4.4f, 0, 2.4f, 1.6f, 5.2f, 0xf2f2f2)
            .box("furR", "body", 2.6f, 4.4f, 0, 2.4f, 1.6f, 5.2f, 0xf2f2f2)
            .box("scarf", "body", 0, 5, 0, 8.4f, 1.8f, 5.6f, scarf)
            .box("scarfTail", "body", -2.6f, 3.6f, 3.2f, 1.6f, 7, .8f, scarf)
            .box("scarfTail2", "body", -1.2f, 2.8f, 3.4f, 1.4f, 5, .7f, scarf)
            .box("belt", "body", 0, -4.6f, 0, 7.2f, 1.2f, 4.8f, 0x8a6a2a)
            .box("head", "body", 0, 10.6f, 0, 7, 8, 7, 0xf6f6f2)
            .box("brow", "head", 0, 2.8f, -3.55f, 7.2f, 1, .3f, 0xe0e0e0)
            .box("sockL", "head", -1.7f, 1, -3.6f, 2.2f, 2, .2f, 0x050505)
            .box("sockR", "head", 1.7f, 1, -3.6f, 2.2f, 2, .2f, 0x050505)
            .glow("eyeL", "head", -1.7f, 1, -3.75f, 1.0f, 1.0f, .3f, eye)
            .ghost("eyeFlare", "head", -2.4f, 2.2f, -3.75f, .9f, 2, .3f, eye, .6f)
            .box("jaw", "head", 0, -3.4f, -.3f, 6, 2, 6.2f, 0xe8e8e2)
            .box("teeth", "head", 0, -2.8f, -3.5f, 5, .5f, .2f, 0x1c1c1c)
            .limb("armL", "body", -4.6f, 3.6f, 0, 2.4f, 9.5f, 2.4f, hoodie)
            .limb("armR", "body", 4.6f, 3.6f, 0, 2.4f, 9.5f, 2.4f, hoodie)
            .box("glovL", "armL", 0, -10, 0, 2.8f, 1.8f, 2.8f, scarf)
            .box("glovR", "armR", 0, -10, 0, 2.8f, 1.8f, 2.8f, scarf)
            .limb("legL", null, -1.7f, 12.5f, 0, 2.6f, 12.5f, 2.8f, 0x2d2d3a)
            .limb("legR", null, 1.7f, 12.5f, 0, 2.6f, 12.5f, 2.8f, 0x2d2d3a)
            .box("bootL", "legL", 0, -10.5f, -.4f, 3.2f, 4, 3.8f, boots)
            .box("bootR", "legR", 0, -10.5f, -.4f, 3.2f, 4, 3.8f, boots);
        if (dustSpecks != 0) m.glow("dust1", "body", -2, 1, -2.45f, .9f, .8f, .2f, dustSpecks).glow("dust2", "body", 2.2f, -2.4f, -2.45f, .7f, .7f, .2f, dustSpecks)
            .glow("dust3", "legR", .5f, -6, -1.45f, .8f, .8f, .2f, dustSpecks).box("tear", "scarf", 2.8f, -.6f, -2.6f, 1.6f, 1.2f, .4f, 0x3a0a0a);
        return m;
    }
    // ================================================================ DUSTBELIEF: Dust Papyrus (Papyrus who took the LV, haunted by Phantom Sans)
    static CharacterDef dustbeliefPapyrus() {
        return CharacterDef.of("dustbelief_papyrus", "Dust Papyrus", Faction.GENOCIDE_ROUTE).att(Attitude.HOSTILE).hp(220).boss().keep(7).rest(12).speed(.32f)
            .egg(0x26407a, 0xff2030).size(.7f, 2.3f)
            .model(papyrusModel(0x26407a, 0x8a1418, RED, 0x7a1a1a, 0xb0aaa0))
            .attacks(dustJavelins(), phantomSans(), scarfLash(), purpleCage(), ph(0, .4f, lvFrenzy())).build();
    }
    static Attack dustJavelins() {
        return new Attack("dustbelief_dust_javelins", 70) {
            public void tick(AttackCtx c, int t) {
                for (int s : new int[]{10, 34}) if (t == s) {
                    Vec3 at = c.pos().add(0, 2.5, 0), aim = hz(predict(c, 8).subtract(c.pos()));
                    for (int k = -2; k <= 2; k++) { Vec3 v = aim.yRot(k * .2f).add(0, -.06, 0).normalize().scale(.85); lance(c, at, v, 2.2f, s == 34 && k == 0 ? RED : W, 6, 4, 26);
                        c.particle(dust(0x9d968c, 1.2f), at.add(v.scale(2)), 4, .2, .02); }
                    c.sound(SoundEvents.TRIDENT_THROW, 1, .6f);
                } else tvel(c);
            }
            public void pose(Pose p, float t, UtMob m) { for (int s : new int[]{10, 34}) { float w = env(t, s - 10, s - 2, s, s + 1), th = env(t, s, s + 2, s + 4, s + 10);
                p.r("armR", -2.8f * w - .9f * th, 0, 0).r("body", -.1f * w + .25f * th, .3f * w - .3f * th, 0); } }
        };
    }
    /** His hallucination of Sans appears beside you, shrugs, and summons red-eyed blasters. */
    static Attack phantomSans() {
        return new Attack("dustbelief_phantom_sans", 92) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { Vec3 at = c.target.position().add(side(c.tpos().subtract(c.pos())).scale(3)); putV(c, "dbS", at);
                    ghostSans(c, at, c.target.position().subtract(at), 80, 0x2b4fa8, RED); c.particle(ParticleTypes.SOUL, at.add(0, 1, 0), 20, .4, .03); c.sound(SoundEvents.SOUL_ESCAPE, 1, .9f); }
                if (t == 12 || t == 40) { int n = t == 12 ? 3 : 4; double base = c.rnd.nextDouble() * PI;
                    for (int k = 0; k < n; k++) gb(c, c.tpos().add(ang(base + k * 2 * PI / n).scale(6)).add(0, 1.8, 0), c.tpos(), 18, 9, 7, 1.0f, 14, 0xe0e0e0, RED, 0xff8090); }
                if (t == 30 || t == 58) boom(c, c.tpos());
                if (t >= 62 && t < 86 && t % 4 == 2) { Vec3 g = getV(c, "dbS"); spike(c, g.add(c.target.position().subtract(g).scale((t - 62) / 24.0)), 1.6f, W, 4, 4, 5); }
            }
            public void pose(Pose p, float t, UtMob m) { float s = ramp(t, 0, 8) * (1 - ramp(t, 84, 92)); p.r("head", .3f * s, 0, -.25f * s).r("armL", -2.3f * s, 0, .5f * s).r("armR", .2f, 0, -.3f); }
        };
    }
    /** His torn scarf lashes around him in a sweeping crimson arc, twice. */
    static Attack scarfLash() {
        return new Attack("dustbelief_scarf_lash", 64) {
            public void tick(AttackCtx c, int t) {
                for (int s : new int[]{12, 38}) {
                    if (t == s - 10) { putV(c, "dbL", new Vec3(angOf(c.tpos().subtract(c.pos())) + (s == 12 ? -1.4 : 1.4), 0, 0)); c.sound(SoundEvents.WOOL_PLACE, 1, .5f); }
                    if (t >= s && t < s + 14) {
                        double a0 = getV(c, "dbL").x, a = a0 + (t - s) * (s == 12 ? .2 : -.2);
                        for (int k = 1; k <= 6; k++) p(c, c.pos().add(ang(a).scale(k * .95)).add(0, 1.3 + Math.sin(k + t) * .15, 0), 0xc01a20).face(ang(a)).box(.5f, .2f, 1.0f).dmg(5).life(3).pierce().spawn();
                        c.particle(dust(0xc01a20, 1.2f), c.pos().add(ang(a).scale(6)).add(0, 1.3, 0), 3, .2, .02);
                        if (t == s) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .5f);
                    }
                }
            }
            public void pose(Pose p, float t, UtMob m) { for (int s : new int[]{12, 38}) { float u = env(t, s - 6, s, s + 12, s + 18), sp = ramp(t, s, s + 14) * u; float dir = s == 12 ? 1 : -1;
                p.r("body", 0, dir * (-1.0f * u + 2.4f * sp), 0).r("armR", -1.4f * u, 0, -1.0f * u).r("armL", -1.4f * u, 0, 1.0f * u).r("scarfTail", -1.3f * u, 0, 0).r("scarfTail2", -1.1f * u, 0, 0); } }
        };
    }
    /** A cage of purple bones closes on the target from all sides, three times, each with a shifting gap. */
    static Attack purpleCage() {
        return new Attack("dustbelief_purple_cage", 92) {
            public void tick(AttackCtx c, int t) {
                for (int w = 0; w < 3; w++) if (t == 4 + w * 26) {
                    Vec3 ctr = c.target.position(); int n = 20, gap = c.rnd.nextInt(n);
                    for (int k = 0; k < n; k++) { if (k == gap || k == (gap + 1) % n || k == (gap + 2) % n) continue;
                        Vec3 d = ang(k * 2 * PI / n + w * .3); post(c, ctr.add(d.scale(6.5)).add(0, 1, 0), d.scale(-.22), 2.0f, w == 2 ? W : PURPLE, 4, 6, 34); }
                    c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .6f); c.particle(ParticleTypes.WITCH, ctr.add(0, 1, 0), 30, 3, .05);
                }
            }
            public void pose(Pose p, float t, UtMob m) { for (int w = 0; w < 3; w++) { float u = pulse(t, 6 + w * 26); p.r("armL", -1.2f * u, 0, 1.2f * u).r("armR", -1.2f * u, 0, -1.2f * u).r("head", -.2f * u, 0, 0); } }
        };
    }
    /** Below 40%: LV frenzy — five blinking red slashes around the target, ash bursting at each blink. */
    static Attack lvFrenzy() {
        return new Attack("dustbelief_lv_frenzy", 106) {
            public void tick(AttackCtx c, int t) {
                if (t == 0) { c.sound(SoundEvents.ELDER_GUARDIAN_CURSE, .5f, 1.2f); c.particle(dust(RED, 2), c.eye(), 30, .3, .1); }
                for (int k = 0; k < 5; k++) {
                    int s = 8 + k * 18;
                    if (t == s) { Vec3 to = c.target.position().add(ang(k * 2.4 + c.rnd.nextDouble()).scale(3.2)); c.particle(dust(0x9d968c, 1.6f), c.pos().add(0, 1.2, 0), 25, .5, .05);
                        tele(c, to); Vec3 d = hz(c.tpos().subtract(to)); putV(c, "dbF", d);
                        p(c, to.add(d.scale(2.2)).add(0, 1, 0), RED).face(d).box(3.4f, .2f, 1.1f).dmg(0).warn(7).life(8).pierce().spawn(); }
                    if (t == s + 7) { Vec3 d = getV(c, "dbF"), at = c.pos().add(d.scale(2.2)).add(0, 1, 0);
                        p(c, at, RED).face(d).box(3.4f, .25f, 1.4f).dmg(7).life(5).pierce().spawn(); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .6f); c.particle(ParticleTypes.SWEEP_ATTACK, at, 3, .5, 0); }
                }
                if (t == 60) eff(c, MobEffects.WITHER, 40, 0);
            }
            public void pose(Pose p, float t, UtMob m) { for (int k = 0; k < 5; k++) { int s = 8 + k * 18; float b = env(t, s, s + 4, s + 6, s + 7), sw = env(t, s + 6, s + 8, s + 9, s + 14);
                p.r("armR", -2.6f * b - .4f * sw, 1.0f * b - 1.4f * sw, 0).r("body", .3f * sw, .4f * b - .6f * sw, 0); } }
        };
    }
    private AuDustLastBreath() {}
}
