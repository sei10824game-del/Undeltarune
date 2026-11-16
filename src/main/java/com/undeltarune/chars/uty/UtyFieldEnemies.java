package com.undeltarune.chars.uty;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import java.util.List;

import static com.undeltarune.attack.AuKit.*;

/** Undertale Yellow regular enemies and minibosses (wiki-confirmed rosters/attacks), area by area.
 *  Monsters are UNDERGROUND random encounters; Steamworks robots (Chujin's machines) are ROYAL_SCIENCE. */
public final class UtyFieldEnemies {
    static final int YEL = 0xffd400, PINK = 0xffb7d5, FIRE = 0xffa020, GREEN = 0x40d040, WHITE = 0xf4f4f4, RED = 0xff3030, BLUE = 0x3fa7ff, ORANGE = 0xff9a1f;

    public static void register(List<CharacterDef> out) {
        // Dark Ruins
        out.add(flier()); out.add(penilla()); out.add(sweetCorn()); out.add(rorrim()); out.add(crispyScroll()); out.add(microFroggit());
        // Snowdin
        out.add(frostermit()); out.add(insomnitot()); out.add(knowCone()); out.add(trihecta());
        // Dunes
        out.add(dunebud()); out.add(cactony()); out.add(sirSlither()); out.add(bowll());
        out.add(flowerGirl("uty_violetta", "Violetta", 0x9a5ad8)); out.add(flowerGirl("uty_pedla", "Pedla", 0xffc0e0)); out.add(flowerGirl("uty_rosa", "Rosa", 0xe0303a));
        // Steamworks
        out.add(jandroid()); out.add(goosic()); out.add(sousborg()); out.add(tellyVis()); out.add(macroFroggit());
    }

    // ---------------------------------------------------------------- small shared helpers
    static Vec3 flat(Vec3 v) { Vec3 f = new Vec3(v.x, 0, v.z); return f.lengthSqr() < 1e-6 ? new Vec3(0, 0, 1) : f.normalize(); }
    static Vec3 side(Vec3 f) { return new Vec3(-f.z, 0, f.x); }
    /** Ground telegraph that only flashes (no damage). */
    static void mark(AttackCtx c, Vec3 at, float w, float d, int rgb, int warn) {
        c.proj().at(at.add(0, .05, 0)).box(w, .1f, d).color(rgb).dmg(0).warn(warn).life(warn + 2).spawn();
    }
    /** Triangle-wave reflection of x into [-r, r]: stateless "bounces off the bullet-board walls". */
    static double bounce(double x, double r) { double p = ((x + r) % (4 * r) + 4 * r) % (4 * r); return p < 2 * r ? p - r : 3 * r - p; }
    static float rmp(float t, float a, float b) { return t <= a ? 0 : t >= b ? 1 : (t - a) / (b - a); }
    /** 0 -> 1 over [a,b], 1 -> 0 over [c,d]. */
    static float win(float t, float a, float b, float c, float d) { return rmp(t, a, b) * (1 - rmp(t, c, d)); }
    static void store(AttackCtx c, String k, Vec3 v) { var m = c.me.getPersistentData(); m.putDouble("uty_" + k + "x", v.x); m.putDouble("uty_" + k + "y", v.y); m.putDouble("uty_" + k + "z", v.z); }
    static Vec3 load(AttackCtx c, String k) { var m = c.me.getPersistentData(); return new Vec3(m.getDouble("uty_" + k + "x"), m.getDouble("uty_" + k + "y"), m.getDouble("uty_" + k + "z")); }
    static void storei(AttackCtx c, String k, int v) { c.me.getPersistentData().putInt("uty_" + k, v); }
    static int loadi(AttackCtx c, String k) { return c.me.getPersistentData().getInt("uty_" + k); }
    /** Standard small biped/blob: root "body" + "head", limbs named for the default walk. */
    static ModelDef blob(float bw, float bh, float bd, int body, float hs, int head, int limbs, float legH) {
        return new ModelDef()
            .box("body", null, 0, legH + bh / 2, 0, bw, bh, bd, body)
            .box("head", "body", 0, bh / 2 + hs / 2, 0, hs, hs, hs, head)
            .limb("armL", "body", -bw / 2 - .8f, bh * .3f, 0, 1.4f, bh * .7f, 1.4f, limbs).limb("armR", "body", bw / 2 + .8f, bh * .3f, 0, 1.4f, bh * .7f, 1.4f, limbs)
            .limb("legL", null, -bw * .25f, legH, 0, 1.6f, legH, 1.6f, limbs).limb("legR", null, bw * .25f, legH, 0, 1.6f, legH, 1.6f, limbs);
    }
    static ModelDef eyes(ModelDef m, String parent, float y, float z, float gap, int rgb) {
        return m.glow("eyeL", parent, -gap, y, z, 1.1f, 1.4f, .3f, rgb).glow("eyeR", parent, gap, y, z, 1.1f, 1.4f, .3f, rgb);
    }
    static CharacterDef.Builder mon(String id, String name, float hp) { return CharacterDef.of(id, name, Faction.UNDERGROUND).hp(hp).keep(5).rest(26).random(); }
    static CharacterDef.Builder bot(String id, String name, float hp) { return CharacterDef.of(id, name, Faction.ROYAL_SCIENCE).hp(hp).keep(5).rest(24).random(); }
    static void fly(AttackCtx c, Vec3 at, Vec3 v, float dmg, int warn, int life) { // body + flickering wings
        c.proj().at(at).vel(v).box(.3f, .3f, .35f).color(0x2a2a2a).dmg(dmg).warn(warn).life(life).spawn();
        c.proj().at(at.add(0, .18, 0)).vel(v).box(.6f, .06f, .2f).color(0xd8e8ff).dmg(0).warn(warn).life(life).spawn();
    }

    // ================================================================ DARK RUINS
    static CharacterDef flier() {
        ModelDef m = eyes(blob(5, 6, 4, WHITE, 5, WHITE, WHITE, 3), "head", .4f, -2.6f, 1.2f, 0x6a3a1a)
            .box("dotA", "body", 0, 1.6f, -2.1f, 1.2f, 1.2f, .3f, ORANGE).box("dotB", "body", 0, 0, -2.1f, 1.2f, 1.2f, .3f, 0x6a3a1a).box("dotC", "body", 0, -1.6f, -2.1f, 1.2f, 1.2f, .3f, ORANGE)
            .box("wingL", "body", -2.4f, 1.5f, 2.4f, 4, 3, .3f, ORANGE).box("wingR", "body", 2.4f, 1.5f, 2.4f, 4, 3, .3f, ORANGE)
            .box("antL", "head", -1.2f, 3.4f, 0, .3f, 2.2f, .3f, 0x6a3a1a).box("antR", "head", 1.2f, 3.4f, 0, .3f, 2.2f, .3f, 0x6a3a1a);
        return mon("uty_flier", "Flier", 24).size(.5f, 1f).scale(.7f).speed(.3f).egg(WHITE, ORANGE).model(m)
            .attacks(flierFire(), flierFlies(), flierSwarm()).build();
    }
    static void flierFlap(Pose p, float t) { p.r("wingL", 0, .6f * wave(t, 2.2f), 0).r("wingR", 0, -.6f * wave(t, 2.2f), 0).o("body", 0, .6f + .4f * wave(t, .4f), 0); }
    /** Fire: three fireballs bob up and down; the outer two also slide sideways while the middle one holds. */
    static Attack flierFire() {
        return atk("uty_flier_fire", 70, (c, t) -> {
            if (t == 4) { store(c, "ffo", c.target.position()); store(c, "fff", flat(c.aim(c.pos()))); }
            Vec3 o = load(c, "ffo"), r = side(load(c, "fff"));
            if (t == 4) for (int i = -1; i <= 1; i++) mark(c, o.add(r.scale(i * 2.2)), 1, 1, FIRE, 10);
            if (t >= 14 && t < 64) for (int i = -1; i <= 1; i++) {
                double y = .4 + 1.1 * (1 + Math.sin(t * .18 + i * 1.2)), x = i * 2.2 + (i == 0 ? 0 : -i * 2.2 * Math.sin((t - 14) * .06));
                c.proj().at(o.add(r.scale(x)).add(0, y, 0)).box(.7f, .7f, .7f).color(FIRE).dmg(3).life(2).spawn();
                if (t % 3 == 0) c.particle(ParticleTypes.FLAME, o.add(r.scale(x)).add(0, y, 0), 1, .1, .01);
            }
            if (t % 12 == 2) c.sound(SoundEvents.FIRECHARGE_USE, .4f, 1.6f);
        }, (p, t, m) -> { flierFlap(p, t); float s = win(t, 0, 6, 60, 70); p.r("armL", -2.4f * s, 0, .4f).r("armR", -2.4f * s, 0, -.4f).r("head", -.2f * s, 0, 0); });
    }
    /** Flies: flies stream in from both flanks in a sine wave toward the far side. */
    static Attack flierFlies() {
        return atk("uty_flier_flies", 64, (c, t) -> {
            if (t >= 8 && t < 52 && t % 4 == 0) { int s = (t / 4) % 2 == 0 ? 1 : -1; Vec3 r = c.right(), f = c.fwd();
                Vec3 at = c.tpos().add(r.scale(6 * s)).add(f.scale(c.rnd.nextGaussian() * 1.2)); double ph = c.rnd.nextDouble() * 6;
                fly(c, at, r.scale(-.32 * s).add(0, Math.sin(ph) * .08, 0), 3, 4, 40); }
            if (t % 8 == 0) c.sound(SoundEvents.BEE_LOOP, .3f, 1.8f);
        }, (p, t, m) -> { flierFlap(p, t * 1.4f); p.r("armR", -1.6f, .4f * wave(t, .5f), 0); });
    }
    /** Swarm: flies pour in from every side at once and cross the whole area. */
    static Attack flierSwarm() {
        return atk("uty_flier_swarm", 70, (c, t) -> {
            if (t >= 10 && t < 56 && t % 3 == 0) { double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = around(c.tpos(), 6.5, a, c.rnd.nextGaussian() * .4);
                Vec3 v = c.tpos().subtract(at).normalize().scale(.38).add(c.rnd.nextGaussian() * .05, 0, c.rnd.nextGaussian() * .05); fly(c, at, v, 3, 6, 36); }
            if (t == 10) c.sound(SoundEvents.BEE_LOOP_AGGRESSIVE, .6f, 1.6f);
        }, (p, t, m) -> { flierFlap(p, t * 2); float s = win(t, 4, 10, 56, 66); p.r("armL", 0, 0, -1.6f * s).r("armR", 0, 0, 1.6f * s); });
    }

    static CharacterDef penilla() {
        ModelDef m = eyes(blob(5, 5, 6, YEL, 5, YEL, YEL, 2.4f), "head", .6f, -2.6f, 1.2f, 0x2a2a2a)
            .box("tip", "head", 0, .5f, 3.6f, 2, 2, 2.2f, 0xf0d8b0).box("lead", "tip", 0, 0, 1.4f, .8f, .8f, .8f, 0x3a3a3a)
            .box("tail", "body", 0, -1.4f, 4.4f, 1.6f, 1.6f, 4, YEL).box("eraser", "tail", 0, 0, 2.4f, 1.8f, 1.8f, 1.4f, PINK)
            .box("band", "tail", 0, 0, 1.5f, 1.9f, 1.9f, .4f, 0xc0c0c8);
        return mon("uty_penilla", "Penilla", 26).size(.55f, .9f).scale(.75f).speed(.3f).egg(YEL, PINK).model(m).attacks(penillaLines(), penillaDrawing()).build();
    }
    /** Lines: a cross-shaped pencil stroke spins while circling around the target. */
    static Attack penillaLines() {
        return atk("uty_penilla_lines", 76, (c, t) -> {
            if (t == 4) store(c, "plo", c.target.position().add(0, .9, 0));
            Vec3 o = load(c, "plo");
            if (t >= 6 && t < 70 && t % 2 == 0) { Vec3 cen = around(o, 2.4, t * .07, 0); double sp = t * .16;
                for (int k = 0; k < 2; k++) { Vec3 d = new Vec3(Math.cos(sp + k * Math.PI / 2), 0, Math.sin(sp + k * Math.PI / 2)).scale(1.8);
                    seg(c, cen.subtract(d), cen.add(d), .22f, 0x3a3a3a, 3, t == 6 ? 14 : 0, 2); } }
            if (t % 10 == 6) c.sound(SoundEvents.BOOK_PAGE_TURN, .5f, 1.6f);
        }, (p, t, m) -> { float s = win(t, 0, 6, 68, 76); p.r("armR", -1.8f * s, .5f * wave(t, .3f), 0).r("head", 0, .3f * wave(t, .14f), 0).r("tail", 0, .3f * wave(t, .5f), 0); });
    }
    /** Drawing: she sketches lines across the area one at a time; they linger, then trail off sideways. */
    static Attack penillaDrawing() {
        return atk("uty_penilla_drawing", 90, (c, t) -> {
            if (t == 4) store(c, "pdo", c.target.position().add(0, .8, 0));
            Vec3 o = load(c, "pdo");
            for (int k = 0; k < 4; k++) { int b = 8 + k * 10;
                if (t == b) { double a = c.rnd.nextDouble() * Math.PI; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a)); store(c, "pdd" + k, d); store(c, "pdc" + k, o.add(side(d).scale(c.rnd.nextGaussian() * 1.2)));
                    c.sound(SoundEvents.VILLAGER_WORK_CARTOGRAPHER, .6f, 1.4f); }
                if (t >= b && t < b + 6) { Vec3 d = load(c, "pdd" + k), cc = load(c, "pdc" + k); double u = (t - b) / 6.0 * 8 - 4; // the pencil tip racing along
                    c.proj().at(cc.add(d.scale(u))).box(.3f, .3f, .3f).color(0x3a3a3a).dmg(0).life(2).spawn(); }
                if (t == b + 6) { Vec3 d = load(c, "pdd" + k), cc = load(c, "pdc" + k), drift = side(d).scale(.08);
                    c.proj().at(cc).vel(Vec3.ZERO).face(d).box(.18f, .18f, 8).color(0x3a3a3a).dmg(3).warn(6).life(80 - t).pierce().spawn();
                    c.proj().at(cc.add(drift.scale(10))).vel(drift.scale(.5)).face(d).box(.12f, .12f, 8).color(0x8a8a8a).dmg(2).warn(80 - t - 18).life(80 - t).pierce().spawn(); } } // trailing smear
        }, (p, t, m) -> { boolean draw = t >= 8 && t < 50 && (t - 8) % 10 < 6; p.r("armR", draw ? -2.2f : -1f, draw ? .8f * wave(t, 1.2f) : 0, 0).r("head", draw ? .3f : 0, 0, 0).r("body", 0, .2f * wave(t, .2f), 0); });
    }

    static CharacterDef sweetCorn() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 7, 0, 5, 10, 5, 0xffd84a).box("tipA", "body", 0, -3, 0, 5.4f, 3, 5.4f, ORANGE).box("tipW", "body", 0, 4.4f, 0, 4, 2, 4, WHITE)
            .box("head", "body", 0, 1.4f, -2.6f, 3.4f, 2.4f, .3f, 0xffe27a).glow("eyeL", "head", -.8f, .4f, -.2f, .6f, .8f, .2f, 0x2a2a2a).glow("eyeR", "head", .8f, .4f, -.2f, .6f, .8f, .2f, 0x2a2a2a)
            .box("smile", "head", 0, -.6f, -.2f, 1.6f, .4f, .2f, 0x2a2a2a)
            .box("husk", "body", 0, -1, 2.6f, 4.6f, 7, .4f, 0x6ab04a)
            .limb("armL", "body", -3.2f, 0, 0, 1, 4, 1, 0x6ab04a).limb("armR", "body", 3.2f, 0, 0, 1, 4, 1, 0x6ab04a)
            .limb("legL", null, -1.2f, 2, 0, 1.2f, 2, 1.2f, 0x6ab04a).limb("legR", null, 1.2f, 2, 0, 1.2f, 2, 1.2f, 0x6ab04a);
        return mon("uty_sweet_corn", "Sweet Corn", 28).size(.55f, 1.1f).scale(.75f).speed(.26f).egg(0xffd84a, ORANGE).model(m)
            .attacks(cornCandy(), cornSpears(), cornHoming()).build();
    }
    static void candyCorn(AttackCtx c, Vec3 at, Vec3 v, int warn, int life) {
        c.proj().at(at).vel(v).box(.45f, .25f, .45f).color(0xffd84a).dmg(3).warn(warn).life(life).spawn();
        c.proj().at(at.add(0, .2, 0)).vel(v).box(.35f, .2f, .35f).color(ORANGE).dmg(0).warn(warn).life(life).spawn();
        c.proj().at(at.add(0, .38, 0)).vel(v).box(.2f, .15f, .2f).color(WHITE).dmg(0).warn(warn).life(life).spawn();
    }
    /** Candy Corn: three pieces appear low/mid/high on one flank one by one and fire across; then from the other flank. */
    static Attack cornCandy() {
        return atk("uty_sweet_corn_candy_corn", 80, (c, t) -> {
            if (t == 4) { store(c, "cco", c.target.position()); store(c, "ccr", c.right()); }
            Vec3 o = load(c, "cco"), r = load(c, "ccr");
            for (int w = 0; w < 2; w++) { int s = w == 0 ? 1 : -1;
                for (int i = 0; i < 3; i++) if (t == 8 + w * 32 + i * 5) { Vec3 at = o.add(r.scale(-5 * s)).add(0, .3 + i * .8, 0);
                    candyCorn(c, at, r.scale(.5 * s), 22 - i * 5, 44); c.sound(SoundEvents.NOTE_BLOCK_PLING.value(), .5f, 1 + i * .2f); } }
        }, (p, t, m) -> { float a = win(t, 4, 8, 22, 28), b = win(t, 36, 40, 54, 60); p.r("armL", -2.2f * a, 0, .3f).r("armR", -2.2f * b, 0, -.3f).r("body", 0, .4f * (b - a), 0); });
    }
    /** Spear Corn: thin stalks spike up from beneath the target's position, a beat behind. */
    static Attack cornSpears() {
        return atk("uty_sweet_corn_spear_corn", 70, (c, t) -> {
            if (t >= 8 && t < 60 && t % 6 == 2) { Vec3 g = c.target.position();
                pillar(c, g, 2.8f, .35f, 0x6ab04a, 4, 10, 18); c.proj().at(g.add(0, 2.6, 0)).vel(new Vec3(0, .02, 0)).box(.4f, .6f, .4f).color(0xffd84a).dmg(0).warn(10).life(18).spawn();
                c.particle(ParticleTypes.COMPOSTER, g.add(0, .2, 0), 4, .2, .02); }
            if (t % 12 == 0 && t >= 18) c.sound(SoundEvents.CROP_BREAK, .8f, 1.2f);
        }, (p, t, m) -> { float s = t >= 8 && t < 60 ? Math.abs(wave(t, .52f)) : 0; p.o("body", 0, -.8f * s, 0).r("armL", 0, 0, -1.4f * s).r("armR", 0, 0, 1.4f * s); });
    }
    /** Homing Corn: a big cob drops in overhead and chases the target around. */
    static Attack cornHoming() {
        return atk("uty_sweet_corn_homing_corn", 76, (c, t) -> {
            if (t == 6) { store(c, "chp", c.tpos().add(0, 4, 0)); c.sound(SoundEvents.ITEM_FRAME_PLACE, .8f, .6f); }
            if (t >= 6 && t < 70) { Vec3 p = load(c, "chp"); Vec3 nx = p.add(c.tpos().subtract(p).scale(t < 18 ? .06 : .045)); store(c, "chp", nx);
                c.proj().at(nx).face(c.aim(nx)).box(.9f, .9f, 1.8f).color(0xffd84a).dmg(t < 18 ? 0 : 5).life(2).spawn();
                c.proj().at(nx).face(c.aim(nx)).box(1, .5f, 1.2f).color(0x6ab04a).dmg(0).life(2).spawn();
                if (t % 4 == 0) c.particle(ParticleTypes.WAX_ON, nx, 2, .3, .02); }
        }, (p, t, m) -> { float s = win(t, 0, 6, 70, 76); p.r("armL", -2.6f * s, 0, .5f).r("armR", -2.6f * s, 0, -.5f).r("head", -.3f * s, 0, 0); });
    }

    static CharacterDef rorrim() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 8, 0, 7, 11, 1.4f, 0x8a6a4a).glow("glass", "body", 0, -.4f, -.75f, 6, 9.6f, .2f, 0xd8f4ff)
            .box("head", "body", 0, 6.6f, 0, 6, 2.6f, 1.6f, 0x8a6a4a).glow("eyeL", "head", -1.2f, .2f, -.9f, .8f, .8f, .2f, 0x2a2a2a).glow("eyeR", "head", 1.2f, .2f, -.9f, .8f, .8f, .2f, 0x2a2a2a)
            .box("smile", "head", 0, -.7f, -.9f, 2, .4f, .2f, 0x2a2a2a)
            .limb("armL", "body", -4.2f, 2, 0, 1.2f, 5, 1.2f, 0x8a6a4a).limb("armR", "body", 4.2f, 2, 0, 1.2f, 5, 1.2f, 0x8a6a4a)
            .limb("legL", null, -2, 2.5f, 0, 1.4f, 2.5f, 1.4f, 0x6a4a2a).limb("legR", null, 2, 2.5f, 0, 1.4f, 2.5f, 1.4f, 0x6a4a2a);
        return mon("uty_rorrim", "Rorrim", 30).size(.6f, 1.2f).scale(.8f).speed(.24f).egg(0xd8f4ff, 0x8a6a4a).model(m).attacks(rorrimMirror(), rorrimSparkle()).build();
    }
    /** Mirror: mirrors are flung down from above and shatter into shards where they land. */
    static Attack rorrimMirror() {
        return atk("uty_rorrim_mirror", 76, (c, t) -> {
            for (int k = 0; k < 3; k++) { int b = 6 + k * 18;
                if (t == b) { Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 1.5, 0, c.rnd.nextGaussian() * 1.5); store(c, "rm" + k, g); mark(c, g, 1.6f, 1.6f, 0xd8f4ff, 14);
                    c.proj().at(g.add(0, 7, 0)).vel(new Vec3(0, -.5, 0)).box(1, 1.4f, .15f).color(0xd8f4ff).dmg(5).life(14).spawn(); }
                if (t == b + 14) { Vec3 g = load(c, "rm" + k).add(0, .4, 0); c.sound(SoundEvents.GLASS_BREAK, .9f, 1.2f); c.particle(ParticleTypes.END_ROD, g, 8, .4, .05);
                    for (int i = 0; i < 7; i++) { double a = i * Math.PI * 2 / 7 + k; c.proj().at(g).vel(new Vec3(Math.cos(a) * .3, .25, Math.sin(a) * .3)).gravity(.03f).box(.25f, .35f, .08f).color(0xd8f4ff).dmg(3).life(26).spawn(); } } }
        }, (p, t, m) -> { float ph = (t - 6) % 18, s = t >= 6 && t < 60 ? win(ph, 0, 5, 7, 12) : 0; p.r("armL", -2.8f * s, 0, .3f).r("armR", -2.8f * s, 0, -.3f).o("body", 0, .4f * s, 0); });
    }
    /** Sparkle: shards blink in around the target and then fire at it in lines. */
    static Attack rorrimSparkle() {
        return atk("uty_rorrim_sparkle", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 8 == 0) { double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 base = around(c.tpos(), 5, a, 0), d = c.tpos().subtract(base).normalize();
                for (int k = 0; k < 4; k++) dart(c, base.subtract(d.scale(k * .7)), d.scale(.45), .15f, .6f, 0xd8f4ff, 3, 10 + k * 2, 30);
                c.particle(ParticleTypes.END_ROD, base, 4, .2, .02); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, .7f, 1.6f); }
        }, (p, t, m) -> p.r("armL", -1.2f, 0, .5f * wave(t, .4f)).r("armR", -1.2f, 0, -.5f * wave(t, .4f)).r("body", 0, .2f * wave(t, .25f), 0));
    }

    static CharacterDef crispyScroll() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 9, 0, 4, 9, 1, 0xf0e0b0).box("rollT", "body", 0, 5, 0, 5.4f, 1.4f, 1.4f, 0xc09a5a).box("rollB", "body", 0, -5, 0, 5.4f, 1.4f, 1.4f, 0xc09a5a)
            .box("head", "body", 0, 2.4f, -.6f, 3.4f, 2, .3f, 0xf0e0b0).glow("eyeL", "head", -.8f, 0, -.2f, 1, .5f, .2f, 0x2a2a2a).glow("eyeR", "head", .8f, 0, -.2f, 1, .5f, .2f, 0x2a2a2a)
            .box("brows", "head", 0, .6f, -.2f, 2.8f, .3f, .2f, 0x2a2a2a)
            .limb("armL", "body", -2.6f, 3, 0, .6f, 6, .4f, 0xd02a2a).limb("armR", "body", 2.6f, 3, 0, .6f, 6, .4f, 0xd02a2a);
        return mon("uty_crispy_scroll", "Crispy Scroll", 28).size(.5f, 1.2f).scale(.8f).speed(.26f).egg(0xf0e0b0, 0xd02a2a).model(m)
            .attacks(scrollLaser(), scrollSlash(), scrollTub()).build();
    }
    static void scrollWave(Pose p, float t) { p.r("armL", 0, 0, -.8f + .4f * wave(t, .5f)).r("armR", 0, 0, .8f - .4f * wave(t, .5f)).o("body", 0, 1.2f + .4f * wave(t, .25f), 0); }
    /** Laser: circles appear off to one side, then each fires a horizontal beam through the area. */
    static Attack scrollLaser() {
        return atk("uty_crispy_scroll_laser", 76, (c, t) -> {
            if (t >= 8 && t < 56 && t % 12 == 8) { Vec3 r = c.right(), at = c.tpos().add(r.scale(7)).add(c.fwd().scale(c.rnd.nextGaussian() * 1.5)).add(0, c.rnd.nextInt(2) * .8 - .3, 0);
                c.proj().at(at).box(.8f, .8f, .8f).color(0xd02a2a).dmg(0).life(22).spawn();
                c.proj().at(at.add(r.scale(-7))).face(r).box(.5f, .5f, 14).color(0xffe0a0).dmg(5).warn(14).life(20).pierce().spawn();
                c.sound(SoundEvents.BEACON_POWER_SELECT, .6f, 1.8f); }
        }, (p, t, m) -> { scrollWave(p, t); p.r("armR", -1.4f, 0, 0); });
    }
    /** Slash: lines appear along the target's movement, turn into slashes (x4), then a final crossed pair. */
    static Attack scrollSlash() {
        return atk("uty_crispy_scroll_slash", 80, (c, t) -> {
            for (int k = 0; k < 5; k++) { int b = 8 + k * 12;
                if (t == b) { Vec3 at = c.tpos(), mv = c.target.getDeltaMovement(); Vec3 d = mv.horizontalDistanceSqr() > 1e-4 ? flat(mv) : flat(c.aim(c.pos()));
                    if (k < 4) seg(c, at.subtract(d.scale(2.2)), at.add(d.scale(2.2)), .2f, WHITE, 5, 10, 13);
                    else for (int s = -1; s <= 1; s += 2) { Vec3 x = rotY(d, s * Math.PI / 4); seg(c, at.subtract(x.scale(2.5)), at.add(x.scale(2.5)), .25f, WHITE, 6, 12, 15); }
                    c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .7f, 1.4f + k * .1f); }
                if (t == b + 10) c.particle(ParticleTypes.SWEEP_ATTACK, c.tpos(), 1, 0, 0); }
        }, (p, t, m) -> { scrollWave(p, t); float s = t >= 8 ? win((t - 8) % 12, 0, 8, 10, 12) : 0; p.r("armR", -2.4f * s, .8f * s, 0).r("armL", -2.4f * s, -.8f * s, 0); });
    }
    static Vec3 rotY(Vec3 v, double a) { double co = Math.cos(a), si = Math.sin(a); return new Vec3(v.x * co - v.z * si, v.y, v.x * si + v.z * co); }
    /** Tub: wash tubs drop from above and bounce once before rolling off. */
    static Attack scrollTub() {
        return atk("uty_crispy_scroll_tub", 76, (c, t) -> {
            for (int k = 0; k < 3; k++) { int b = 6 + k * 16;
                if (t == b) { Vec3 g = c.target.position().add(c.rnd.nextGaussian(), 0, c.rnd.nextGaussian()); store(c, "st" + k, g); mark(c, g, 1.3f, 1.3f, 0xffe0a0, 12);
                    c.proj().at(g.add(0, 6, 0)).vel(new Vec3(0, -.5, 0)).box(1.2f, .7f, 1.2f).color(0x8a8a9a).dmg(5).life(12).spawn(); }
                if (t == b + 12) { Vec3 g = load(c, "st" + k).add(0, .4, 0); Vec3 h = flat(c.right()).scale(c.rnd.nextBoolean() ? .2 : -.2);
                    c.proj().at(g).vel(h.add(0, .45, 0)).gravity(.04f).box(1.2f, .7f, 1.2f).color(0x8a8a9a).dmg(4).life(24).spawn(); c.sound(SoundEvents.ANVIL_LAND, .4f, 1.8f); } }
        }, (p, t, m) -> { scrollWave(p, t); float s = t >= 6 && t < 54 ? win((t - 6) % 16, 0, 4, 6, 10) : 0; p.r("armL", 0, 0, -2 * s).r("armR", 0, 0, 2 * s); });
    }

    static CharacterDef microFroggit() {
        ModelDef m = new ModelDef().box("body", null, 0, .5f, 0, 1, 1, 1, WHITE).box("head", "body", 0, .6f, -.3f, .6f, .4f, .6f, WHITE)
            .limb("legL", null, -.3f, .3f, 0, .2f, .3f, .2f, WHITE).limb("legR", null, .3f, .3f, 0, .2f, .3f, .2f, WHITE);
        return mon("uty_micro_froggit", "Micro Froggit", 12).size(.3f, .3f).scale(.5f).speed(.36f).egg(WHITE, 0x8fbf6f).model(m).attacks(microFly(), microBarrage()).build();
    }
    /** Fly: a comically oversized fly hatches out and chases the target. */
    static Attack microFly() {
        return atk("uty_micro_froggit_fly", 70, (c, t) -> {
            if (t == 6) { store(c, "mfp", c.pos().add(0, 1, 0)); c.sound(SoundEvents.BEE_LOOP_AGGRESSIVE, .8f, .7f); }
            if (t >= 6 && t < 66) { Vec3 p = load(c, "mfp"), nx = p.add(c.tpos().subtract(p).normalize().scale(.16)).add(0, Math.sin(t * .5) * .06, 0); store(c, "mfp", nx);
                c.proj().at(nx).box(1.2f, 1, 1.4f).color(0x2a2a2a).dmg(t < 14 ? 0 : 4).life(2).spawn();
                c.proj().at(nx.add(0, .7, 0)).box(t % 2 == 0 ? 2.4f : 1.6f, .1f, .8f).color(0xd8e8ff).dmg(0).life(2).spawn();
                c.proj().at(nx.add(flat(c.aim(nx)).scale(.7)).add(0, .2, 0)).box(.9f, .4f, .2f).color(RED).dmg(0).life(2).spawn(); }
        }, (p, t, m) -> p.o("body", 0, .5f * Math.abs(wave(t, .6f)), 0));
    }
    /** Barrage: a stream of Micro Froggits hop at the target from two corners. */
    static Attack microBarrage() {
        return atk("uty_micro_froggit_barrage", 64, (c, t) -> {
            if (t >= 8 && t < 54 && t % 3 == 0) { Vec3 f = flat(c.aim(c.pos())); Vec3 at = c.target.position().add(f.scale(-4)).add(side(f).scale((t / 3) % 2 == 0 ? 3 : -3)).add(0, .2, 0);
                Vec3 d = c.target.position().subtract(at); c.proj().at(at).vel(new Vec3(d.x / 16, .35, d.z / 16)).gravity(.045f).box(.2f, .2f, .2f).color(WHITE).dmg(2).warn(4).life(20).spawn(); }
            if (t % 9 == 0) c.sound(SoundEvents.FROG_LONG_JUMP, .4f, 2);
        }, (p, t, m) -> p.o("body", 0, .4f * Math.abs(wave(t, 1)), 0).r("legL", -.6f * Math.abs(wave(t, 1)), 0, 0).r("legR", -.6f * Math.abs(wave(t, 1)), 0, 0));
    }

    // ================================================================ SNOWDIN
    static CharacterDef frostermit() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 4, 0, 7, 5, 7, 0xe8f6ff).box("dome", "body", 0, 3.2f, .6f, 5.6f, 2, 5.6f, 0xe8f6ff).box("door", "body", 0, -1, -3.6f, 2.4f, 2.6f, .3f, 0x3a5a7a)
            .box("head", "body", 0, -.4f, -4.2f, 2.6f, 2, 1.4f, 0xffa080).glow("eyeL", "head", -.6f, 1.4f, -.2f, .5f, .9f, .5f, 0x2a2a2a).glow("eyeR", "head", .6f, 1.4f, -.2f, .5f, .9f, .5f, 0x2a2a2a)
            .limb("armL", "body", -3, -1, -4, 1.2f, 1.4f, 2.6f, 0xff8060).limb("armR", "body", 3, -1, -4, 1.2f, 1.4f, 2.6f, 0xff8060)
            .box("pinL", "armL", 0, -.8f, -1.8f, 1.8f, 1, 1.6f, 0xff8060).box("pinR", "armR", 0, -.8f, -1.8f, 1.8f, 1, 1.6f, 0xff8060)
            .limb("legL", null, -2.4f, 1.6f, 0, 1, 1.6f, 1, 0xff8060).limb("legR", null, 2.4f, 1.6f, 0, 1, 1.6f, 1, 0xff8060);
        return mon("uty_frostermit", "Frostermit", 32).size(.6f, .7f).scale(.8f).speed(.22f).egg(0xe8f6ff, 0xff8060).model(m).attacks(frostSnowflakes(), frostIceCubes(), frostPinchers()).build();
    }
    static Attack frostSnowflakes() {
        return atk("uty_frostermit_snowflakes", 66, (c, t) -> {
            if (t >= 6 && t < 56 && t % 2 == 0) { Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3, 6, c.rnd.nextGaussian() * 3);
                c.proj().at(at).vel(new Vec3(Math.sin(t * .4) * .05, -.32, Math.cos(t * .3) * .05)).box(.3f, .3f, .3f).color(WHITE).dmg(2).warn(3).life(22).spawn(); c.particle(ParticleTypes.SNOWFLAKE, at, 1, .2, .01); }
        }, (p, t, m) -> { float s = win(t, 0, 6, 56, 66); p.o("head", 0, 0, .8f * s).r("armL", -.8f * s, 0, 0).r("armR", -.8f * s, 0, 0).o("dome", 0, .2f * wave(t, .5f) * s, 0); });
    }
    /** Ice cubes fall one at a time onto a few columns and stack up, walling off space. */
    static Attack frostIceCubes() {
        return atk("uty_frostermit_ice_cubes", 84, (c, t) -> {
            if (t == 4) { store(c, "fio", c.target.position()); store(c, "fir", c.right()); for (int i = 0; i < 4; i++) storei(c, "fih" + i, 0); }
            Vec3 o = load(c, "fio"), r = load(c, "fir");
            if (t >= 8 && t < 64 && t % 5 == 3) { int col = c.rnd.nextInt(4); storei(c, "fil" + (t % 2), col); Vec3 g = o.add(r.scale((col - 1.5) * 1.2)); int h = loadi(c, "fih" + col);
                c.proj().at(g.add(0, 6, 0)).vel(new Vec3(0, -.55, 0)).box(1, 1, 1).color(0xbfeaff).dmg(4).warn(4).life((int) ((6 - h - .5) / .55)).spawn();
                c.proj().at(g.add(0, h + .5, 0)).box(1, 1, 1).color(0xbfeaff).dmg(3).warn((int) ((6 - h - .5) / .55) + 4).life(84 - t).spawn();
                storei(c, "fih" + col, Math.min(h + 1, 3)); c.sound(SoundEvents.GLASS_PLACE, .6f, 1.4f); }
        }, (p, t, m) -> { float s = t >= 8 && t < 64 ? Math.abs(wave(t, .6f)) : 0; p.r("armL", -1.6f * s, 0, 0).r("armR", -1.6f * (1 - s), 0, 0); });
    }
    /** The area shrinks around the target, then big pinchers snap in from the left and right. */
    static Attack frostPinchers() {
        return atk("uty_frostermit_pinchers", 80, (c, t) -> {
            if (t == 4) { store(c, "fpo", c.target.position()); store(c, "fpr", c.right()); c.sound(SoundEvents.POWDER_SNOW_STEP, 1, .6f); }
            Vec3 o = load(c, "fpo"), r = load(c, "fpr");
            if (t >= 6 && t < 74 && t % 2 == 0) for (int i = 0; i < 8; i++) seg(c, around(o, 2.2, i * Math.PI / 4, .6), around(o, 2.2, (i + 1) * Math.PI / 4, .6), .25f, 0xbfeaff, 3, t == 6 ? 10 : 0, 2);
            for (int k = 0; k < 3; k++) { int b = 20 + k * 16, s = k % 2 == 0 ? 1 : -1;
                if (t == b) mark(c, o.add(r.scale(s * 1.2)), 1.6f, 1.2f, RED, 10);
                if (t >= b + 10 && t < b + 16) { double x = s * (3.6 - (t - b - 10) * .45); c.proj().at(o.add(r.scale(x)).add(0, .7, 0)).box(1.6f, .9f, 1.2f).color(0xff8060).dmg(6).life(2).spawn(); }
                if (t == b + 14) c.sound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1, 1.4f); }
        }, (p, t, m) -> { float ph = t >= 20 ? (t - 20) % 16 : 0, s = t >= 20 && t < 68 ? win(ph, 4, 10, 12, 16) : 0; p.r("armL", 0, -1.2f * s, 0).r("armR", 0, 1.2f * s, 0).r("pinL", -.6f * s, 0, 0).r("pinR", -.6f * s, 0, 0); });
    }

    static CharacterDef insomnitot() {
        ModelDef m = eyes(blob(6, 5, 6, 0x9ab0e0, 0.1f, 0x9ab0e0, 0x7a90c0, 1.4f), "body", .6f, -3.1f, 1.4f, 0x2a2a2a)
            .box("bagL", "body", -1.4f, -.3f, -3.1f, 1.2f, .4f, .2f, 0x5a5a8a).box("bagR", "body", 1.4f, -.3f, -3.1f, 1.2f, .4f, .2f, 0x5a5a8a)
            .box("cap", "body", 0, 3, .4f, 5, 1.6f, 5, 0x3a4a8a).box("capTip", "cap", 2.4f, .6f, 1.6f, 1.4f, 1.4f, 1.4f, WHITE)
            .box("antL", "body", -1.4f, 4.2f, 0, .3f, 2.2f, .3f, 0x5a6a9a).box("antR", "body", 1.4f, 4.2f, 0, .3f, 2.2f, .3f, 0x5a6a9a);
        return mon("uty_insomnitot", "Insomnitot", 30).size(.55f, .7f).scale(.75f).speed(.2f).egg(0x9ab0e0, 0x3a4a8a).model(m).attacks(insomStars(), insomZzz(), insomSheep()).build();
    }
    static void doze(Pose p, float t) { p.r("body", .15f * wave(t, .12f), 0, .1f * wave(t, .07f)).r("cap", 0, 0, .1f * wave(t, .12f)); }
    /** Stars: a flurry of small stars zooms past the target. */
    static Attack insomStars() {
        return atk("uty_insomnitot_stars", 64, (c, t) -> {
            if (t >= 8 && t < 54 && t % 2 == 0) { Vec3 r = c.right(); int s = (t / 12) % 2 == 0 ? 1 : -1; Vec3 at = c.tpos().add(r.scale(-7 * s)).add(c.fwd().scale(c.rnd.nextGaussian() * 1.6)).add(0, c.rnd.nextGaussian() * .5, 0);
                c.proj().at(at).vel(r.scale(.7 * s)).box(.3f, .3f, .3f).color(YEL).dmg(2).warn(3).life(22).spawn(); }
            if (t % 6 == 0 && t >= 8 && t < 54) c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, .4f, 2);
        }, (p, t, m) -> { doze(p, t); p.r("armR", -1.2f, 0, -.3f * wave(t, .4f)); });
    }
    /** ZZZ: towers of three Zs zig-zag upward from the ground around the target. */
    static Attack insomZzz() {
        return atk("uty_insomnitot_zzz", 76, (c, t) -> {
            for (int k = 0; k < 4; k++) { int b = 6 + k * 12;
                if (t == b) { Vec3 g = c.target.position().add(c.right().scale((k % 2 == 0 ? 1 : -1) * (1 + c.rnd.nextDouble() * 1.5))); store(c, "zz" + k, g); mark(c, g, 1, 1, 0x9ab0e0, 10); }
                if (t >= b + 10 && t < b + 34) { Vec3 g = load(c, "zz" + k); double y = (t - b - 10) * .18, x = Math.sin(y * 2.4) * .8;
                    for (int z = 0; z < 3; z++) c.proj().at(g.add(c.right().scale(x)).add(0, y + z * .55, 0)).box(.45f - z * .08f, .12f, .12f).color(0x9ab0e0).dmg(z == 0 ? 3 : 2).life(2).spawn(); } }
            if (t % 10 == 0) c.sound(SoundEvents.FOX_SLEEP, .6f, 1.2f);
        }, (p, t, m) -> { doze(p, t * 1.5f); p.o("body", 0, -.3f * Math.abs(wave(t, .2f)), 0); });
    }
    /** Sheep: the space shrinks; sheep hop along while a fence slides toward the target to be jumped. */
    static Attack insomSheep() {
        return atk("uty_insomnitot_sheep", 90, (c, t) -> {
            if (t == 4) { store(c, "iso", c.target.position()); store(c, "isf", flat(c.aim(c.pos()))); }
            Vec3 o = load(c, "iso"), f = load(c, "isf"), r = side(f);
            if (t >= 6 && t < 84 && t % 2 == 0) for (int s = -1; s <= 1; s += 2) seg(c, o.add(r.scale(s * 1.8)).add(f.scale(-4)), o.add(r.scale(s * 1.8)).add(f.scale(4)), .2f, WHITE, 3, t == 6 ? 10 : 0, 2);
            for (int k = 0; k < 3; k++) { int b = 18 + k * 20;
                if (t == b) { c.proj().at(o.add(f.scale(-5)).add(0, .3, 0)).vel(f.scale(.3)).face(r).box(.15f, .55f, 3.4f).color(0x8a5a2a).dmg(5).warn(6).life(34).spawn(); c.sound(SoundEvents.SHEEP_AMBIENT, .7f, 1.2f); }
                if (t >= b && t < b + 30 && t % 2 == 0) { double u = (t - b) / 30.0; Vec3 at = o.add(f.scale(5 - u * 10)).add(r.scale(1.2)).add(0, .4 + Math.abs(Math.sin(u * Math.PI * 3)) * 1.2, 0);
                    c.proj().at(at).box(.7f, .5f, .5f).color(WHITE).dmg(0).life(2).spawn(); } }
        }, (p, t, m) -> { doze(p, t); float s = win(t, 4, 10, 80, 90); p.r("armL", -1f * s, 0, .4f).r("armR", -1f * s, 0, -.4f).r("head", 0, 0, 0); });
    }

    static CharacterDef knowCone() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 5, 0, 4, 7, 4, 0xd8a050).box("head", "body", 0, 5, 0, 6, 4, 6, 0xffe8f0).glow("eyeL", "head", -1.2f, .4f, -3.1f, .8f, 1, .2f, 0x2a2a2a).glow("eyeR", "head", 1.2f, .4f, -3.1f, .8f, 1, .2f, 0x2a2a2a)
            .box("antlerL", "head", -2.6f, 3, 0, .5f, 2.4f, .5f, 0x8a5a3a).box("antlerR", "head", 2.6f, 3, 0, .5f, 2.4f, .5f, 0x8a5a3a)
            .glow("ball", "head", 0, 5.4f, 0, 2, 2, 2, 0xd02a3a)
            .limb("armL", "body", -2.6f, 2, 0, 1, 4, 1, 0xd8a050).limb("armR", "body", 2.6f, 2, 0, 1, 4, 1, 0xd8a050);
        return mon("uty_know_cone", "Know Cone", 30).size(.5f, 1f).scale(.8f).speed(.24f).egg(0xffe8f0, 0xd8a050).model(m).attacks(coneCherry(), coneBerries(), coneFig()).build();
    }
    /** Cherry: a single cherry swings like a pendulum as it descends onto the target. */
    static Attack coneCherry() {
        return atk("uty_know_cone_cherry", 76, (c, t) -> {
            if (t == 4) { store(c, "kco", c.target.position().add(0, 6, 0)); storei(c, "kcs", c.rnd.nextBoolean() ? 1 : 0); }
            Vec3 o = load(c, "kco"); double fall = loadi(c, "kcs") == 1 ? .1 : .06;
            if (t >= 6 && t < 70) { double y = Math.max(.4, 6 - (t - 6) * fall * 1.0), sw = Math.sin(t * .18) * 2.2; Vec3 at = o.add(c.right().scale(sw)).add(0, -6 + y, 0);
                c.proj().at(at).box(.8f, .8f, .8f).color(0xd02a3a).dmg(5).life(2).spawn(); seg(c, at.add(0, .4, 0), at.add(0, .4, 0).add(c.right().scale(-sw * .2)).add(0, .8, 0), .08f, 0x3a8a2a, 0, 0, 2); }
        }, (p, t, m) -> p.r("armR", -2.6f, 0, -.3f * wave(t, .18f)).o("ball", .6f * wave(t, .18f), 0, 0));
    }
    /** Berries: spheres rise from the ground and flanks; a few are blue (stand still) or orange (keep moving). */
    static Attack coneBerries() {
        return atk("uty_know_cone_berries", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 4 == 0) { Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 2.4, 0, c.rnd.nextGaussian() * 2.4); int kind = c.rnd.nextInt(5);
                var b = c.proj().at(g.add(0, -.3, 0)).vel(new Vec3(0, .32, 0)).box(.55f, .55f, .55f).dmg(3).warn(8).life(20);
                (kind == 0 ? b.blue() : kind == 1 ? b.orange() : b.color(0x5a3ad0)).spawn(); }
            if (t % 8 == 0 && t >= 8 && t < 56) c.sound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, .6f, 1.2f);
        }, (p, t, m) -> p.r("armL", 0, 0, -1.4f * Math.abs(wave(t, .4f))).r("armR", 0, 0, 1.4f * Math.abs(wave(t, .4f))).o("ball", 0, .6f * Math.abs(wave(t, .4f)), 0));
    }
    /** Fig: a single fig is hurled down and keeps bouncing toward the target. */
    static Attack coneFig() {
        return atk("uty_know_cone_fig", 66, (c, t) -> {
            for (int k = 0; k < 3; k++) if (t == 10 + k * 14) { Vec3 at = (k == 0 ? c.tpos().add(0, 5, 0) : load(c, "kf")), g = c.target.position(); Vec3 d = g.subtract(at);
                store(c, "kf", g.add(0, .4, 0)); if (k == 0) mark(c, g, 1, 1, 0x7a3a8a, 14);
                c.proj().at(at).vel(new Vec3(d.x / 14, (d.y + .5 * .05 * 14 * 14) / 14, d.z / 14)).gravity(.05f).box(.9f, 1, .9f).color(0x7a3a8a).dmg(5).life(14).spawn(); c.sound(SoundEvents.SLIME_JUMP, .8f, .8f); }
        }, (p, t, m) -> { float s = win(t, 0, 8, 10, 16); p.r("armR", -3f * s, 0, 0).r("head", .3f * s, 0, 0); });
    }

    static CharacterDef trihecta() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 2.6f, 0, 5, 5, 5, 0x40b040).box("hec", "body", 0, 4.6f, 0, 4.4f, 4.4f, 4.4f, 0xd03a3a).box("head", "hec", 0, 4, 0, 3.8f, 3.8f, 3.8f, 0x3a6ad0)
            .glow("eyeT", "body", 0, .6f, -2.6f, 1.6f, .6f, .2f, 0x1a1a1a).glow("eyeH", "hec", 0, .6f, -2.3f, 1.4f, .6f, .2f, 0x1a1a1a).glow("eyeA", "head", 0, .4f, -2f, 1.2f, .6f, .2f, 0x1a1a1a)
            .limb("armL", "body", -3, 0, 0, 1, 1, 1, 0x40b040).limb("armR", "body", 3, 0, 0, 1, 1, 1, 0x40b040)
            .limb("legL", null, -1.4f, .6f, 0, 1, .6f, 1, 0x40b040).limb("legR", null, 1.4f, .6f, 0, 1, .6f, 1, 0x40b040);
        return mon("uty_trihecta", "Trihecta", 40).size(.5f, 1.2f).scale(.85f).speed(.24f).egg(0x40b040, 0xd03a3a).model(m).attacks(triBounce(), triReflect(), triStack()).build();
    }
    static void triWobble(Pose p, float t) { p.r("hec", 0, 0, .15f * wave(t, .3f)).r("head", 0, 0, -.2f * wave(t, .3f)).o("body", 0, .3f * Math.abs(wave(t, .5f)), 0); }
    /** Circle Bounce: three balls (green, red, blue) bounce along the ground toward the target, one lagging. */
    static Attack triBounce() {
        return atk("uty_trihecta_circle_bounce", 76, (c, t) -> {
            if (t == 4) { store(c, "tbo", c.target.position()); store(c, "tbf", flat(c.aim(c.pos()))); }
            Vec3 o = load(c, "tbo"), f = load(c, "tbf"), r = side(f); int[] col = {0x40b040, 0xd03a3a, 0x3a6ad0};
            if (t >= 8 && t < 72) for (int i = 0; i < 3; i++) { double u = (t - 8 - (i == 2 ? 10 : 0)) * .16; if (u < 0) continue;
                Vec3 at = o.add(f.scale(5 - u)).add(r.scale((i - 1) * 1.3)).add(0, .4 + Math.abs(Math.sin(u * 1.4)) * 1.6, 0);
                c.proj().at(at).box(.8f, .8f, .8f).color(col[i]).dmg(4).warn(t < 14 ? 6 : 0).life(2).spawn(); }
            if (t % 7 == 0 && t >= 8) c.sound(SoundEvents.SLIME_JUMP_SMALL, .5f, 1.2f);
        }, (p, t, m) -> triWobble(p, t));
    }
    /** Circle Reflect: two large balls ricochet off the walls of an invisible box around the target. */
    static Attack triReflect() {
        return atk("uty_trihecta_circle_reflect", 80, (c, t) -> {
            if (t == 4) { store(c, "tro", c.target.position().add(0, .7, 0)); store(c, "trf", flat(c.aim(c.pos()))); }
            Vec3 o = load(c, "tro"), f = load(c, "trf"), r = side(f);
            if (t >= 8 && t < 76) for (int i = 0; i < 2; i++) { double x = bounce((t - 8) * (.17 + i * .05) + i * 3, 3), z = bounce((t - 8) * (.11 + i * .07) + i * 1.7, 3);
                c.proj().at(o.add(r.scale(x)).add(f.scale(z))).box(1.3f, 1.3f, 1.3f).color(i == 0 ? 0xd03a3a : 0x3a6ad0).dmg(5).warn(t < 16 ? 8 : 0).life(2).spawn(); }
            if (t % 6 == 0 && t >= 8) for (int k = 0; k < 4; k++) seg(c, o.add(r.scale(k < 2 ? -3.6 : 3.6)).add(f.scale(-3.6)), o.add(r.scale(k < 2 ? -3.6 : 3.6)).add(f.scale(3.6)), .1f, 0x9ab0e0, 0, 0, 6);
        }, (p, t, m) -> { triWobble(p, t * 1.4f); p.r("armL", 0, 0, -1.2f).r("armR", 0, 0, 1.2f); });
    }
    /** Circle Stack: three small balls carom off the walls, changing direction each hit. */
    static Attack triStack() {
        return atk("uty_trihecta_circle_stack", 76, (c, t) -> {
            if (t == 4) { store(c, "tso", c.target.position().add(0, .5, 0)); store(c, "tsf", flat(c.aim(c.pos()))); }
            Vec3 o = load(c, "tso"), f = load(c, "tsf"), r = side(f); int[] col = {0x40b040, 0xd03a3a, 0x3a6ad0};
            if (t >= 8 && t < 72) for (int i = 0; i < 3; i++) { double x = bounce((t - 8) * (.28 + i * .06) + i * 2, 2.6), z = bounce((t - 8) * (.21 - i * .04) + i * 1.3, 2.6), y = bounce((t - 8) * .1 + i, .6) + .6;
                c.proj().at(o.add(r.scale(x)).add(f.scale(z)).add(0, y, 0)).box(.5f, .5f, .5f).color(col[i]).dmg(3).warn(t < 14 ? 6 : 0).life(2).spawn(); }
        }, (p, t, m) -> { triWobble(p, t * 2); p.o("head", 0, .6f * Math.abs(wave(t, .7f)), 0); });
    }

    // ================================================================ DUNES
    static CharacterDef dunebud() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 2.6f, 0, 7, 4, 6, 0xe8c070).box("head", "body", 0, 2.4f, 0, 5, 1.2f, 4, 0x8a5a2a).box("sapling", "head", 0, 1.6f, 0, .4f, 2, .4f, 0x3a8a2a)
            .box("leaf", "sapling", .8f, 1, 0, 1.4f, .3f, .8f, 0x40c040)
            .glow("eyeL", "body", -1.4f, .6f, -3.1f, .9f, 1, .2f, 0x2a2a2a).glow("eyeR", "body", 1.4f, .6f, -3.1f, .9f, 1, .2f, 0x2a2a2a)
            .limb("legL", null, -2, .6f, 0, 1.4f, .6f, 1.4f, 0xd0a860).limb("legR", null, 2, .6f, 0, 1.4f, .6f, 1.4f, 0xd0a860);
        return mon("uty_dunebud", "Dunebud", 30).size(.6f, .5f).scale(.8f).speed(.3f).egg(0xe8c070, 0x3a8a2a).model(m).attacks(budSandcastle(), budTumbleweeds(), budChase()).build();
    }
    /** Sandcastle: a bucket is set down, a castle builds up, then it fires nine arrows at the target. */
    static Attack budSandcastle() {
        return atk("uty_dunebud_sandcastle", 84, (c, t) -> {
            if (t == 6) { Vec3 g = c.target.position().add(flat(c.aim(c.pos())).scale(3)); store(c, "dsc", g); c.proj().at(g.add(0, .5, 0)).box(.9f, 1, .9f).color(0xd03a3a).dmg(0).life(16).spawn(); c.sound(SoundEvents.SAND_PLACE, 1, 1); }
            Vec3 g = load(c, "dsc");
            if (t >= 20 && t < 36 && t % 4 == 0) { int k = (t - 20) / 4; c.proj().at(g.add(0, .3 + k * .45, 0)).box(1.6f - k * .3f, .45f, 1.6f - k * .3f).color(0xe8c070).dmg(0).life(80 - t).spawn(); c.sound(SoundEvents.SAND_BREAK, .8f, 1.2f); }
            if (t >= 40 && t < 76 && t % 4 == 0) { Vec3 at = g.add(0, 1.6, 0); dart(c, at, c.aim(at).scale(.5), .12f, .7f, 0x8a5a2a, 3, 4, 26); c.sound(SoundEvents.ARROW_SHOOT, .6f, 1.4f); }
        }, (p, t, m) -> { float s = win(t, 2, 6, 34, 40); p.r("body", .3f * s, 0, 0).o("sapling", 0, .4f * wave(t, .8f) * s, 0).r("leaf", 0, t * .2f, 0); });
    }
    static Attack budTumbleweeds() {
        return atk("uty_dunebud_tumbleweeds", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 8 == 0) { Vec3 r = c.right(); int s = c.rnd.nextBoolean() ? 1 : -1; Vec3 at = c.target.position().add(r.scale(-7 * s)).add(c.fwd().scale(c.rnd.nextGaussian() * 1.4)).add(0, .5, 0);
                c.proj().at(at).vel(r.scale(.42 * s).add(0, .2, 0)).gravity(.03f).box(.9f, .9f, .9f).color(0xb08a4a).dmg(3).warn(6).life(36).spawn();
                c.particle(ParticleTypes.WHITE_ASH, at, 8, .5, .02); }
            if (t % 16 == 0) c.sound(SoundEvents.AZALEA_LEAVES_BREAK, .6f, .8f);
        }, (p, t, m) -> p.r("sapling", 0, 0, .4f * wave(t, .3f)).o("body", 0, .2f * Math.abs(wave(t, .4f)), 0));
    }
    /** Chase: hops into the fray and leaps at the target whenever it lines up (slow crawl at low HP). */
    static Attack budChase() {
        return atk("uty_dunebud_chase", 70, (c, t) -> {
            boolean weak = c.me.hpFrac() < .35f;
            if (t >= 6 && t < 64 && t % 14 == 6) { Vec3 d = flat(c.aim(c.pos())); c.dash(weak ? d.scale(.3) : d.scale(.7).add(0, .55, 0)); c.sound(SoundEvents.SAND_STEP, 1, 1.2f); }
            if (!weak && t >= 6 && t < 64 && t % 14 == 12) { c.aoe(c.pos(), 1.4, 4, .5); c.particle(ParticleTypes.CLOUD, c.pos(), 6, .4, .02); }
        }, (p, t, m) -> { float s = t >= 6 ? win((t - 6) % 14, 0, 2, 6, 10) : 0; p.r("body", -.4f * s, 0, 0).r("legL", -.8f * s, 0, 0).r("legR", -.8f * s, 0, 0); });
    }

    static CharacterDef cactony() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 7, 0, 4, 12, 4, 0x3a9a4a).box("head", "body", 0, 7, 0, 4, 2, 4, 0x3a9a4a)
            .glow("eyeL", "body", -.9f, 3.6f, -2.1f, .6f, .6f, .2f, 0x1a1a1a).glow("eyeR", "body", .9f, 3.6f, -2.1f, .6f, .6f, .2f, 0x1a1a1a).box("frown", "body", 0, 2.6f, -2.1f, 1.4f, .3f, .2f, 0x1a1a1a)
            .limb("armL", "body", -3, 1, 0, 2, 1.2f, 1.2f, 0x3a9a4a).box("armLu", "armL", -.8f, 1.2f, 0, 1.2f, 2.4f, 1.2f, 0x3a9a4a)
            .limb("armR", "body", 3, 1, 0, 2, 1.2f, 1.2f, 0x3a9a4a).box("armRu", "armR", .8f, 1.2f, 0, 1.2f, 2.4f, 1.2f, 0x3a9a4a)
            .box("flower", "head", 0, 1.4f, 0, 1.6f, .8f, 1.6f, 0xff6ac1);
        return mon("uty_cactony", "Cactony", 34).size(.5f, 1.3f).scale(.85f).speed(.2f).egg(0x3a9a4a, 0xff6ac1).model(m).attacks(cactTop(), cactSide(), cactGreen()).build();
    }
    /** Needle Top: a spiny ball forms overhead and fires needles at the target; spent needles stick in the ground a moment. */
    static Attack cactTop() {
        return atk("uty_cactony_needle_top", 70, (c, t) -> {
            if (t == 6) { store(c, "cto", c.tpos().add(0, 4.5, 0)); c.proj().at(load(c, "cto")).box(1, 1, 1).color(0x3a9a4a).dmg(0).life(52).spawn(); }
            Vec3 o = load(c, "cto");
            if (t >= 16 && t < 56 && t % 5 == 1) { Vec3 g = c.target.position().add(c.rnd.nextGaussian() * .6, 0, c.rnd.nextGaussian() * .6), d = g.subtract(o).normalize();
                dart(c, o, d.scale(.55), .1f, .7f, 0xd8f0a0, 3, 4, (int) (g.distanceTo(o) / .55));
                c.proj().at(g.add(0, .3, 0)).box(.1f, .7f, .1f).color(0xd8f0a0).dmg(2).warn((int) (g.distanceTo(o) / .55) + 4).life((int) (g.distanceTo(o) / .55) + 24).spawn(); c.sound(SoundEvents.ARROW_SHOOT, .5f, 1.8f); }
        }, (p, t, m) -> { float s = win(t, 0, 6, 56, 66); p.r("armL", 0, 0, -.4f * s).r("armR", 0, 0, .4f * s).r("head", -.2f * s, 0, 0); });
    }
    /** Needle Side: needles lob in from both flanks and arc down, sticking where they land. */
    static Attack cactSide() {
        return atk("uty_cactony_needle_side", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 4 == 0) { int s = (t / 4) % 2 == 0 ? 1 : -1; Vec3 r = c.right(); Vec3 at = c.tpos().add(r.scale(5 * s)).add(c.fwd().scale(c.rnd.nextGaussian())).add(0, 1.2, 0);
                c.proj().at(at).vel(r.scale(-.32 * s).add(0, .2, 0)).gravity(.035f).box(.1f, .1f, .6f).color(0xd8f0a0).dmg(3).warn(4).life(30).spawn(); }
            if (t % 8 == 0 && t >= 8) c.sound(SoundEvents.ARROW_SHOOT, .4f, 1.5f);
        }, (p, t, m) -> { boolean l = (t / 4) % 2 == 0; p.r("armL", 0, 0, l ? -1 : 0).r("armR", 0, 0, l ? 0 : 1); });
    }
    /** Needle Green (after a Touch): rows of needle columns roll past with green gaps that heal and calm it. */
    static Attack cactGreen() {
        return atk("uty_cactony_needle_green", 80, (c, t) -> {
            if (t == 4) { store(c, "cgo", c.target.position()); store(c, "cgf", flat(c.aim(c.pos()))); }
            Vec3 o = load(c, "cgo"), f = load(c, "cgf"), r = side(f);
            for (int w = 0; w < 3; w++) { int b = 8 + w * 20;
                if (t == b) storei(c, "cgg" + w, c.rnd.nextInt(5) - 2);
                if (t >= b && t < b + 34 && t % 2 == 0) { double u = 5 - (t - b) * .3; int gap = loadi(c, "cgg" + w);
                    for (int k = -3; k <= 3; k++) { Vec3 at = o.add(f.scale(u)).add(r.scale(k * .9)).add(0, .6, 0);
                        if (k == gap) { c.proj().at(at).box(.8f, 1.2f, .3f).color(GREEN).dmg(0).life(2).spawn();
                            if (c.target.position().distanceTo(at.add(0, -.6, 0)) < .7) { c.target.heal(1); c.particle(ParticleTypes.HAPPY_VILLAGER, at, 4, .3, .05); } }
                        else c.proj().at(at).box(.15f, 1.2f, .15f).color(0xd8f0a0).dmg(3).warn(t == b ? 6 : 0).life(2).spawn(); } } }
        }, (p, t, m) -> { p.r("armL", 0, 0, -.6f * Math.abs(wave(t, .2f))).r("armR", 0, 0, .6f * Math.abs(wave(t, .2f))).o("flower", 0, .3f * wave(t, .4f), 0); });
    }

    static CharacterDef sirSlither() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 3.4f, 0, 6, 6, 6, 0x7a3aa8).box("stripe", "body", 0, 0, 0, 6.2f, 1, 6.2f, YEL).box("handleL", "body", -3.6f, 1.4f, 0, .8f, 2.4f, .8f, YEL).box("handleR", "body", 3.6f, 1.4f, 0, .8f, 2.4f, .8f, YEL)
            .box("neck", "body", 0, 5, 0, 2.4f, 5, 2.4f, 0xc03030).box("belly", "neck", 0, 0, -1.25f, 1.6f, 4.6f, .2f, YEL)
            .box("head", "neck", 0, 3.4f, -.6f, 3, 2.4f, 3.4f, 0xc03030).box("hood", "head", 0, .6f, .8f, 4.6f, 3, 1.2f, 0x6a4a2a)
            .glow("eyeL", "head", -.8f, .4f, -1.8f, .6f, .7f, .2f, YEL).glow("eyeR", "head", .8f, .4f, -1.8f, .6f, .7f, .2f, YEL)
            .box("tongue", "head", 0, -.8f, -2.2f, .3f, .1f, 1, 0xff4060)
            .limb("armL", "body", -2, -2.4f, 0, .1f, .1f, .1f, 0x7a3aa8).limb("armR", "body", 2, -2.4f, 0, .1f, .1f, .1f, 0x7a3aa8);
        return mon("uty_sir_slither", "Sir Slither", 34).size(.6f, 1.2f).scale(.85f).speed(.18f).egg(0x7a3aa8, 0xc03030).model(m).attacks(slitherPottery(), slitherSnakes()).build();
    }
    static void snake(AttackCtx c, Vec3 head, Vec3 dir, double t, float dmg, int warn) { // 6-segment wiggling body trailing the head
        Vec3 s = side(flat(dir));
        for (int i = 0; i < 6; i++) { Vec3 at = head.subtract(dir.normalize().scale(i * .45)).add(s.scale(Math.sin(t * .5 - i * .9) * .35));
            c.proj().at(at).box(.4f, .35f, .4f).color(i == 0 ? 0xc03030 : (i % 2 == 0 ? 0xc03030 : 0xe05050)).dmg(dmg).warn(warn).life(2).spawn(); }
    }
    /** Pottery: a warning, a pot spins into shape and spits bullets, then two snakes rise to shape it. */
    static Attack slitherPottery() {
        return atk("uty_sir_slither_pottery", 84, (c, t) -> {
            if (t == 4) { Vec3 g = c.target.position().add(flat(c.aim(c.pos())).scale(2.5)); store(c, "spo", g); mark(c, g, 2, 2, RED, 16); }
            Vec3 g = load(c, "spo");
            if (t >= 20 && t < 70 && t % 2 == 0) { double h = Math.min(1.6, (t - 20) * .06); for (int k = 0; k < 4; k++) c.proj().at(around(g, .8 - k * .05, t * .4 + k * 1.57, h * .5)).box(.4f, (float) h, .4f).color(0xa86a3a).dmg(0).life(2).spawn(); }
            if (t >= 28 && t < 54 && t % 5 == 3) { Vec3 at = g.add(0, 1.4, 0); for (int i = 0; i < 3; i++) shot(c, at, c.aim(at).scale(.35).add(rotY(new Vec3(.12, 0, 0), i * 2.1 + t)).add(0, .1, 0), .3f, 0xa86a3a, 3, 0, 26); c.sound(SoundEvents.DECORATED_POT_HIT, .8f, 1.2f); }
            if (t >= 56 && t < 80) for (int s = -1; s <= 1; s += 2) snake(c, around(g, 1.1, s * (t - 56) * .25 + (s < 0 ? Math.PI : 0), (t - 56) * .1), new Vec3(0, 1, 0), t, 4, 0);
        }, (p, t, m) -> { float s = win(t, 16, 22, 70, 80); p.r("neck", .4f * wave(t, .3f) * s, 0, .3f * wave(t, .2f)).o("tongue", 0, 0, -.6f * Math.abs(wave(t, 1.2f))).r("head", -.3f * s, 0, 0); });
    }
    /** Snake: snakes slither straight across the area along or across the target's line. */
    static Attack slitherSnakes() {
        return atk("uty_sir_slither_snake", 76, (c, t) -> {
            for (int k = 0; k < 4; k++) { int b = 6 + k * 14;
                if (t == b) { Vec3 f = k % 2 == 0 ? c.right() : flat(c.aim(c.pos())); int s = c.rnd.nextBoolean() ? 1 : -1; store(c, "ssd" + k, f.scale(s));
                    store(c, "ss0" + k, c.target.position().add(f.scale(-6 * s)).add(side(f).scale(c.rnd.nextGaussian() * .8)).add(0, .3, 0)); c.sound(SoundEvents.SNIFFER_SCENTING, .4f, 2); }
                if (t >= b && t < b + 40) { Vec3 d = load(c, "ssd" + k); snake(c, load(c, "ss0" + k).add(d.scale((t - b) * .32)), d, t, 3, t < b + 6 ? 6 : 0); } }
        }, (p, t, m) -> p.r("neck", 0, .5f * wave(t, .25f), .3f * wave(t, .5f)).o("tongue", 0, 0, -.6f * Math.abs(wave(t, 1.5f))));
    }

    static CharacterDef bowll() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 5, 0, 9, 5, 9, 0xe8e8f0).box("rim", "body", 0, 2.6f, 0, 9.6f, .8f, 9.6f, 0xd0d0e0).box("crack", "body", -2, 0, -4.55f, .3f, 3, .1f, 0x6a6a7a)
            .box("head", "body", 0, 4.4f, -3.4f, 4, 3.4f, 4, 0x8a5a3a).box("hornL", "head", -2.6f, 1.6f, 0, 2, .8f, .8f, 0xf0e0c0).box("hornR", "head", 2.6f, 1.6f, 0, 2, .8f, .8f, 0xf0e0c0)
            .box("snout", "head", 0, -1, -2.2f, 2.6f, 1.4f, .8f, 0xc09a7a).glow("eyeL", "head", -1, .6f, -2.05f, .6f, .6f, .2f, 0x1a1a1a).glow("eyeR", "head", 1, .6f, -2.05f, .6f, .6f, .2f, 0x1a1a1a)
            .box("tail", "body", 0, 0, 5.4f, .4f, .4f, 3, 0x8a5a3a).box("plate", "tail", 0, .4f, 1.6f, 2.4f, .2f, 2.4f, WHITE)
            .box("dog", "body", 2, 2.6f, 2, 1.4f, 1.2f, 1.4f, WHITE)
            .limb("legL", null, -2.6f, 2.5f, 0, 1.6f, 2.5f, 1.6f, 0x8a5a3a).limb("legR", null, 2.6f, 2.5f, 0, 1.6f, 2.5f, 1.6f, 0x8a5a3a);
        return mon("uty_bowll", "Bowll", 44).size(.8f, .9f).scale(.95f).speed(.24f).egg(0xe8e8f0, 0x8a5a3a).model(m).attacks(bowllBull(), bowllLiquid(), bowllSilverware()).build();
    }
    static void bowllIdle(Pose p, float t) { p.r("plate", 0, t * .5f, 0).o("dog", 0, (t % 40) < 8 ? 1.2f : -2, 0); }
    /** Bull: red cloths pop up around the target; a bull charges at each cloth in turn. */
    static Attack bowllBull() {
        return atk("uty_bowll_bull", 84, (c, t) -> {
            for (int k = 0; k < 4; k++) { int b = 6 + k * 16;
                if (t == b) { double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 cloth = around(c.target.position(), 1.6, a, 0), from = around(c.target.position(), 7, a + Math.PI, .5);
                    store(c, "bbc" + k, cloth); store(c, "bbf" + k, from); c.proj().at(cloth.add(0, .8, 0)).face(cloth.subtract(from)).box(1.4f, 1.2f, .1f).color(0xd02a2a).dmg(0).life(28).spawn(); }
                if (t == b + 10) { Vec3 cloth = load(c, "bbc" + k), from = load(c, "bbf" + k), v = cloth.subtract(from).normalize().scale(.6);
                    c.proj().at(from).vel(v).face(v).box(1, 1.1f, 1.6f).color(0x6a3a1a).dmg(6).warn(6).life(26).spawn(); c.proj().at(from.add(0, .6, 0)).vel(v).face(v).box(1.6f, .25f, .25f).color(0xf0e0c0).dmg(0).warn(6).life(26).spawn();
                    c.sound(SoundEvents.RAVAGER_STEP, .8f, 1.3f); } }
        }, (p, t, m) -> { bowllIdle(p, t); float s = t >= 6 ? win((t - 6) % 16, 6, 10, 12, 16) : 0; p.r("head", -.4f * s, 0, 0).r("legL", -.6f * s, 0, 0); });
    }
    /** Liquid: the area becomes a mug; marshmallows plop in from above and the cocoa rises toward the brim. */
    static Attack bowllLiquid() {
        return atk("uty_bowll_liquid", 100, (c, t) -> {
            if (t == 4) { store(c, "blo", c.target.position()); for (int i = 0; i < 12; i++) c.proj().at(around(load(c, "blo"), 3, i * Math.PI / 6, 1.3)).box(.25f, 2.6f, .25f).color(WHITE).dmg(0).life(92).spawn(); c.sound(SoundEvents.DECORATED_POT_PLACE, 1, 1.2f); }
            Vec3 o = load(c, "blo");
            if (t >= 12 && t < 80 && t % 5 == 2) { Vec3 at = o.add(c.target.position().subtract(o).multiply(1, 0, 1)).add(c.rnd.nextGaussian() * .6, 5, c.rnd.nextGaussian() * .6);
                c.proj().at(at).vel(new Vec3(0, -.4, 0)).box(.45f, .45f, .45f).color(WHITE).dmg(3).warn(4).life(14).spawn(); }
            if (t >= 20 && t < 94) { double lv = (t - 20) / 74.0 * 2.2; if (t % 4 == 0) c.proj().at(o.add(0, lv - .4, 0)).box(5.6f, .3f, 5.6f).color(0x6a3a1a).dmg(0).life(4).spawn();
                if (c.target.position().y < o.y + lv - .5 && c.target.position().distanceTo(o) < 3.2) { c.hit(c.target, 1); c.target.setDeltaMovement(c.target.getDeltaMovement().x, .4, c.target.getDeltaMovement().z); c.target.hurtMarked = true; } }
        }, (p, t, m) -> { bowllIdle(p, t); float s = win(t, 4, 12, 80, 96); p.r("head", .3f * s * Math.abs(wave(t, .4f)), 0, 0); });
    }
    /** Silverware: two rows of forks and knives sweep the area - one half descends, the other rises. */
    static Attack bowllSilverware() {
        return atk("uty_bowll_silverware", 80, (c, t) -> {
            if (t == 4) { store(c, "bso", c.target.position()); store(c, "bsr", c.right()); store(c, "bsf", c.fwd()); }
            Vec3 o = load(c, "bso"), r = load(c, "bsr"), f = load(c, "bsf");
            if (t >= 8 && t < 72) for (int k = -3; k <= 3; k++) { boolean down = k < 0; double cyc = (t - 8) * .1 + Math.abs(k) * .5, y = down ? 4 - (cyc % 1) * 5 : -1 + (cyc % 1) * 5;
                if (k == 0) continue; c.proj().at(o.add(r.scale(k * .9)).add(f.scale(Math.sin(k) * .6)).add(0, y, 0)).box(.15f, 1.4f, .3f).color(k % 2 == 0 ? 0xc0c0c8 : 0xe0e0e8).dmg(4).warn(t < 16 ? 8 : 0).life(2).spawn(); }
            if (t % 10 == 0) c.sound(SoundEvents.CHAIN_HIT, .4f, 1.8f);
        }, (p, t, m) -> { bowllIdle(p, t); p.r("body", 0, 0, .1f * wave(t, .3f)); });
    }

    static CharacterDef flowerGirl(String id, String name, int flower) {
        ModelDef m = eyes(blob(5, 6, 4, flower, 5, 0xf2d0b0, 0xf2d0b0, 2), "head", .4f, -2.6f, 1.1f, 0x2a2a2a)
            .box("dress", "body", 0, -3, 0, 7.6f, 4.4f, 6.4f, flower).box("hat", "head", 0, 2.8f, 0, 6, 1, 6, flower).box("brim", "hat", 0, -.4f, 0, 10, .3f, 10, 0xf0e0c0)
            .box("bloom", "hat", 2.4f, .6f, -2, 1.6f, 1.6f, 1.6f, flower).box("gloveL", "armL", 0, -3.4f, 0, 1.6f, 1.2f, 1.6f, WHITE).box("gloveR", "armR", 0, -3.4f, 0, 1.6f, 1.2f, 1.6f, WHITE);
        return CharacterDef.of(id, name, Faction.UNDERGROUND).hp(40).keep(5).rest(24).size(.6f, 1.5f).scale(.85f).speed(.24f).egg(flower, 0xf0e0c0).model(m)
            .attacks(fgGrowing(id, flower), fgColored(id, flower), fgSpinning(id, flower), fgFalling(id, flower)).build();
    }
    static void curtsy(Pose p, float t, float s) { p.r("armL", -.6f * s, 0, .5f * s).r("armR", -.6f * s, 0, -.5f * s).o("body", 0, -.5f * s, 0).r("head", .2f * s, 0, 0); }
    /** Growing Flowers: big droplets fall above the target; where they splash a vine shoots up and three droplets spray out. */
    static Attack fgGrowing(String id, int flower) {
        return atk(id + "_growing_flowers", 80, (c, t) -> {
            for (int k = 0; k < 4; k++) { int b = 6 + k * 14;
                if (t == b) { Vec3 g = c.target.position(); store(c, "fgg" + k, g); c.proj().at(g.add(0, 6, 0)).vel(new Vec3(0, -.5, 0)).box(.7f, .9f, .7f).color(0x6ac8ff).dmg(4).warn(4).life(12).spawn(); }
                if (t == b + 12) { Vec3 g = load(c, "fgg" + k); pillar(c, g, 2.6f, .4f, 0x3a8a2a, 5, 4, 20); c.particle(ParticleTypes.SPLASH, g.add(0, .3, 0), 10, .4, .1);
                    c.proj().at(g.add(0, 2.5, 0)).vel(new Vec3(0, .03, 0)).box(.7f, .7f, .7f).color(flower).dmg(0).warn(4).life(20).spawn();
                    for (int i = 0; i < 3; i++) { double a = i * 2.09 + k; shot(c, g.add(0, .4, 0), new Vec3(Math.cos(a) * .25, .3, Math.sin(a) * .25), .3f, 0x6ac8ff, 2, 0, 18); } c.sound(SoundEvents.PLAYER_SPLASH, .5f, 1.6f); } }
        }, (p, t, m) -> { float s = t >= 6 ? win((t - 6) % 14, 0, 4, 8, 12) : 0; p.r("armR", -2.6f * s, 0, -.3f).r("armL", -1.2f * s, 0, .3f); });
    }
    /** Colored Flowers: flowers whiz across; their petals are blue (stand still) or orange (keep moving). */
    static Attack fgColored(String id, int flower) {
        return atk(id + "_colored_flowers", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 6 == 2) { int s = (t / 6) % 2 == 0 ? 1 : -1; Vec3 r = c.right(); Vec3 at = c.tpos().add(r.scale(-6 * s)).add(c.fwd().scale(c.rnd.nextGaussian() * 1.2));
                boolean blue = c.rnd.nextBoolean(); var b = c.proj().at(at).vel(r.scale(.55 * s)).box(1.1f, 1.1f, .3f).dmg(4).warn(6).life(26); (blue ? b.blue() : b.orange()).spawn();
                c.proj().at(at).vel(r.scale(.55 * s)).box(.4f, .4f, .35f).color(YEL).dmg(0).warn(6).life(26).spawn(); c.sound(SoundEvents.CHERRY_LEAVES_BREAK, .6f, 1.3f); }
        }, (p, t, m) -> { curtsy(p, t, .4f); p.r("armR", -1.8f, .5f * wave(t, .5f), 0); });
    }
    /** Spinning Flower: a giant flower turns around the target with one petal missing - stay in the gap. */
    static Attack fgSpinning(String id, int flower) {
        return atk(id + "_spinning_flower", 84, (c, t) -> {
            if (t == 4) { store(c, "fso", c.target.position().add(0, .7, 0)); storei(c, "fsd", c.rnd.nextBoolean() ? 1 : -1); }
            Vec3 o = load(c, "fso"); int dir = loadi(c, "fsd");
            if (t >= 6 && t < 78 && t % 2 == 0) { double rot = dir * (t - 6) * .045;
                for (int i = 1; i < 8; i++) { double a = rot + i * Math.PI / 4; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                    c.proj().at(o.add(d.scale(1.6))).face(d).box(.8f, .8f, 2.6f).color(flower).dmg(4).warn(t == 6 ? 14 : 0).life(2).pierce().spawn(); }
                c.proj().at(o).box(.8f, .8f, .8f).color(YEL).dmg(0).life(2).spawn(); }
            if (t % 16 == 6) c.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, .5f, 1.4f);
        }, (p, t, m) -> { float s = win(t, 0, 6, 76, 84); p.r("armL", -2.6f * s, 0, .4f).r("armR", -2.6f * s, 0, -.4f).r("body", 0, .2f * wave(t, .1f) * s, 0); });
    }
    /** Falling Flowers (after Encourage/Accept/Inquire): flowers are tossed in from the sides with one green flower that heals. */
    static Attack fgFalling(String id, int flower) {
        return atk(id + "_falling_flowers", 70, (c, t) -> {
            if (t >= 8 && t < 50 && t % 6 == 2) { int s = (t / 6) % 2 == 0 ? 1 : -1; boolean green = t == 26; Vec3 r = c.right(), at = c.tpos().add(r.scale(5 * s)).add(0, 1.2, 0);
                c.proj().at(at).vel(r.scale(-.25 * s).add(c.fwd().scale(c.rnd.nextGaussian() * .05)).add(0, .3, 0)).gravity(.03f).box(.6f, .3f, .6f).color(green ? GREEN : flower).dmg(green ? 0 : 3).life(34).spawn();
                if (green) store(c, "fgf", at); }
            if (t >= 26 && t < 60) { Vec3 g = load(c, "fgf"); // touching the green flower's fall path heals (canon: green = HP)
                if (t % 4 == 0 && c.target.distanceToSqr(g) < 9 && c.rnd.nextInt(3) == 0) { c.target.heal(1); c.particle(ParticleTypes.HAPPY_VILLAGER, c.tpos(), 4, .3, .05); } }
        }, (p, t, m) -> { boolean l = (t / 6) % 2 == 0; p.r(l ? "armL" : "armR", -2.4f, 0, 0).r("head", 0, l ? .3f : -.3f, 0); });
    }

    // ================================================================ STEAMWORKS (robots)
    static CharacterDef jandroid() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 2.4f, 0, 5, 3.6f, 5, 0x6a8aa8).box("pole", "body", 0, 3.4f, 0, .6f, 3.4f, .6f, 0x8a8a98)
            .box("head", "pole", 0, 3.6f, 0, 5, 5, 5, 0xc8d0d8).glow("screen", "head", 0, 1, -2.6f, 4.2f, 2, .2f, 0x40e0ff)
            .glow("eyeL", "screen", -1, 0, -.1f, .8f, 1, .1f, 0x1a1a1a).glow("eyeR", "screen", 1, 0, -.1f, .8f, 1, .1f, 0x1a1a1a)
            .box("phones", "head", 0, .6f, 0, 6, 1.6f, 1.6f, 0x2a2a3a).box("screw", "head", 0, 3, 0, 1, 1.6f, 1, 0xd8b040)
            .limb("armL", "head", -3.6f, -2.6f, -1.6f, 1, 1, 1, 0xc8d0d8).limb("armR", "head", 3.6f, -2.6f, -1.6f, 1, 1, 1, 0xc8d0d8)
            .box("mop", "armL", 0, -2.6f, 0, .3f, 5, .3f, 0x8a5a2a).box("mopHead", "mop", 0, -2.6f, 0, 1.6f, .8f, 1.6f, 0xe0e0d0)
            .box("bottle", "armR", 0, -.6f, -.4f, .8f, 1.4f, .8f, 0x40c0ff);
        return bot("uty_jandroid", "Jandroid", 40).size(.6f, 1.4f).scale(.85f).speed(.24f).egg(0x6a8aa8, 0x40e0ff).model(m).attacks(jandSpray(), jandBuckets(), jandTrash()).build();
    }
    static Attack jandSpray() {
        return atk("uty_jandroid_spray", 66, (c, t) -> {
            if (t >= 10 && t < 58 && t % 8 == 2) { Vec3 at = c.eye().add(c.right().scale(.6)); double tilt = Math.sin(t * .3) * .3;
                for (int i = -2; i <= 2; i++) { Vec3 d = rotY(c.aim(at), i * .18).add(0, tilt + .15, 0); c.proj().at(at).vel(d.scale(.4)).gravity(.015f).box(.22f, .22f, .22f).color(0x40c0ff).dmg(2).life(28).spawn(); }
                c.particle(ParticleTypes.SPLASH, at, 6, .2, .05); c.sound(SoundEvents.BUCKET_EMPTY, .4f, 1.8f); }
        }, (p, t, m) -> p.r("armR", -1.6f + .5f * wave(t, .3f), 0, 0).r("screw", 0, t * .4f, 0).r("head", 0, .2f * wave(t, .15f), 0));
    }
    /** Buckets: exclamation marks flash at the edges, then mop buckets skid across to the far side. */
    static Attack jandBuckets() {
        return atk("uty_jandroid_buckets", 76, (c, t) -> {
            for (int k = 0; k < 4; k++) { int b = 6 + k * 14;
                if (t == b) { Vec3 f = flat(c.aim(c.pos())); Vec3 d = k % 2 == 0 ? side(f) : f; int s = c.rnd.nextBoolean() ? 1 : -1; Vec3 at = c.target.position().subtract(d.scale(6 * s)).add(side(d).scale(c.rnd.nextGaussian() * .8)).add(0, .5, 0);
                    store(c, "jb" + k, at); store(c, "jbv" + k, d.scale(.5 * s)); c.proj().at(at.add(0, 1.4, 0)).box(.2f, .7f, .2f).color(YEL).dmg(0).life(10).spawn(); c.proj().at(at.add(0, .85, 0)).box(.2f, .2f, .2f).color(YEL).dmg(0).life(10).spawn();
                    c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), .6f, 1.8f); }
                if (t == b + 10) { Vec3 at = load(c, "jb" + k), v = load(c, "jbv" + k); c.proj().at(at).vel(v).box(.9f, .9f, .9f).color(0x6a8aa8).dmg(4).life(26).spawn();
                    c.proj().at(at.add(0, .3, 0)).vel(v).box(.7f, .2f, .7f).color(0x6ac8ff).dmg(0).life(26).spawn(); c.sound(SoundEvents.MINECART_RIDING, .4f, 1.6f); } }
        }, (p, t, m) -> { float s = t >= 6 ? win((t - 6) % 14, 6, 10, 12, 14) : 0; p.r("armL", -1.6f * s, 0, 0).r("mop", .8f * s, 0, 0); });
    }
    /** Trash (after Clean): a bucket of junk is dumped from above, piling up on the floor; a green soap bar heals if caught. */
    static Attack jandTrash() {
        return atk("uty_jandroid_trash", 76, (c, t) -> {
            if (t == 4) store(c, "jto", c.target.position());
            Vec3 o = load(c, "jto");
            if (t == 4) c.proj().at(o.add(0, 5.5, 0)).box(1.2f, 1, 1.2f).color(0x6a8aa8).dmg(0).life(50).spawn();
            if (t >= 10 && t < 50 && t % 2 == 0) { Vec3 at = o.add(c.rnd.nextGaussian() * .5, 5, c.rnd.nextGaussian() * .5);
                c.proj().at(at).vel(new Vec3(c.rnd.nextGaussian() * .08, -.3, c.rnd.nextGaussian() * .08)).gravity(.01f).box(.35f, .3f, .35f).color(c.rnd.nextBoolean() ? 0x8a7a5a : 0x5a6a5a).dmg(3).life(18).spawn();
                if (t % 6 == 0) c.proj().at(o.add(c.rnd.nextGaussian() * 1.6, .2, c.rnd.nextGaussian() * 1.6)).box(.5f, .3f, .5f).color(0x6a5a4a).dmg(2).life(76 - t).spawn(); }
            if (t == 30) { c.proj().at(o.add(0, 5, 0)).vel(new Vec3(0, -.25, 0)).box(.5f, .3f, .3f).color(GREEN).dmg(0).life(20).spawn(); store(c, "jts", o); }
            if (t == 50 && c.target.position().distanceTo(o) < 1.4) { c.target.heal(4); c.particle(ParticleTypes.HAPPY_VILLAGER, c.tpos(), 8, .4, .05); }
        }, (p, t, m) -> { float s = win(t, 2, 8, 48, 56); p.r("armL", -2.8f * s, 0, .4f).r("armR", -2.8f * s, 0, -.4f).r("head", -.3f * s, 0, 0); });
    }

    static CharacterDef goosic() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 2.6f, 0, 7, 4, 7, 0x6a3a1a).glow("disc", "body", 0, 2.2f, 0, 6, .3f, 6, 0x1a1a1a)
            .box("chain", "body", 0, 5, 0, .4f, 4, .4f, 0xb0b0b8)
            .box("head", "chain", 0, 3.6f, -1, 3.6f, 3.2f, 3.6f, 0xd8b040).box("horn", "head", 0, .4f, -2.6f, 4.6f, 4.6f, 2, 0xd8b040).box("hornIn", "horn", 0, 0, -.9f, 3, 3, .3f, 0x3a2a1a)
            .box("crown", "head", 0, 2.4f, 0, 3, 1.2f, 3, YEL).glow("eyeL", "head", -1, .8f, -1.9f, .6f, .6f, .2f, RED).glow("eyeR", "head", 1, .8f, -1.9f, .6f, .6f, .2f, RED)
            .limb("legL", null, -2.6f, .6f, 0, 1, .6f, 1, 0x3a2a1a).limb("legR", null, 2.6f, .6f, 0, 1, .6f, 1, 0x3a2a1a);
        return bot("uty_goosic", "Goosic", 44).size(.7f, 1.3f).scale(.9f).speed(.2f).egg(0xd8b040, 0x6a3a1a).model(m).attacks(goosicRecord(), goosicSpeakers(), goosicMixer()).build();
    }
    /** A record spins up, an arrow locks onto the target, then flies at it. */
    static Attack goosicRecord() {
        return atk("uty_goosic_record", 70, (c, t) -> {
            for (int k = 0; k < 3; k++) { int b = 6 + k * 18;
                if (t == b) { Vec3 at = c.eye().add(0, 1, 0); store(c, "gr" + k, at); c.sound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), .8f, .8f); }
                if (t >= b && t < b + 10) { Vec3 at = load(c, "gr" + k); c.proj().at(at).face(c.aim(at)).box(.15f, .15f, .9f).color(RED).dmg(0).life(2).spawn(); c.proj().at(at).box(1, .1f, 1).color(0x1a1a1a).dmg(0).life(2).spawn(); }
                if (t == b + 10) { Vec3 at = load(c, "gr" + k); dart(c, at, c.aim(at).scale(.75), .25f, 1.1f, RED, 5, 0, 22); c.sound(SoundEvents.ARROW_SHOOT, .7f, 1.2f); } }
        }, (p, t, m) -> p.r("disc", 0, t * .8f, 0).r("chain", .2f * wave(t, .4f), 0, 0).r("head", -.2f, .3f * wave(t, .2f), 0));
    }
    /** Speakers flank the target; one lights up and a flurry of notes blasts upward on its side. */
    static Attack goosicSpeakers() {
        return atk("uty_goosic_speakers", 84, (c, t) -> {
            if (t == 4) { store(c, "gso", c.target.position()); store(c, "gsr", c.right()); }
            Vec3 o = load(c, "gso"), r = load(c, "gsr");
            if (t >= 4 && t < 80 && t % 4 == 0) for (int s = -1; s <= 1; s += 2) c.proj().at(o.add(r.scale(s * 3.4)).add(0, .8, 0)).box(1, 1.6f, 1).color(0x2a2a2a).dmg(0).life(4).spawn();
            for (int k = 0; k < 4; k++) { int b = 10 + k * 16, s = c.rnd.nextBoolean() ? 1 : -1;
                if (t == b) { storei(c, "gss" + k, s); mark(c, o.add(r.scale(s * 1.6)), 3, 3.6f, YEL, 10); }
                if (t >= b + 10 && t < b + 16) { int sd = loadi(c, "gss" + k); Vec3 g = o.add(r.scale(sd * (.4 + c.rnd.nextDouble() * 2.4))).add(c.fwd().scale(c.rnd.nextGaussian() * 1.2));
                    c.proj().at(g).vel(new Vec3(0, .45, 0)).box(.35f, .35f, .35f).color(YEL).dmg(3).life(14).spawn(); c.particle(ParticleTypes.NOTE, g.add(0, .4, 0), 1, .1, 1);
                    if (t == b + 10) c.sound(SoundEvents.NOTE_BLOCK_BASS.value(), 1, .6f + k * .2f); } }
        }, (p, t, m) -> p.r("disc", 0, t * .6f, 0).o("horn", 0, 0, -.4f * Math.abs(wave(t, .8f))).r("chain", 0, 0, .2f * wave(t, .8f)));
    }
    /** Mixer: the floor flashes red and mixer-bar pillars shoot up at different heights. */
    static Attack goosicMixer() {
        return atk("uty_goosic_mixer", 70, (c, t) -> {
            if (t == 6) { store(c, "gmo", c.target.position()); store(c, "gmr", c.right()); mark(c, load(c, "gmo"), 6, 3, RED, 14); c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), 1, .6f); }
            Vec3 o = load(c, "gmo"), r = load(c, "gmr");
            if (t >= 20 && t < 60 && t % 10 == 0) for (int k = -3; k <= 3; k++) { float h = .6f + c.rnd.nextFloat() * 2.2f;
                pillar(c, o.add(r.scale(k * .8)), h, .6f, k % 2 == 0 ? 0x40e0ff : 0x40ff80, 4, 4, 8); }
            if (t % 10 == 0 && t >= 20 && t < 60) c.sound(SoundEvents.NOTE_BLOCK_SNARE.value(), .8f, 1);
        }, (p, t, m) -> p.r("disc", 0, t * 1.2f, 0).o("head", 0, .5f * Math.abs(wave(t, .3f)), 0));
    }

    static CharacterDef sousborg() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 8, 0, 9, 9, 7, WHITE).box("btnA", "body", 0, 2, -3.6f, .8f, .8f, .2f, 0x9a9aa8).box("btnB", "body", 0, -1, -3.6f, .8f, .8f, .2f, 0x9a9aa8)
            .box("skirt", "body", 0, -5.4f, 0, 7, 2.4f, 6, 0x8a8a98)
            .box("head", "body", 0, 7, 0, 6, 5, 6, 0xb0b8c0).glow("eyeL", "head", -1.4f, .8f, -3.1f, 1, .7f, .2f, 0x40e0ff).glow("eyeR", "head", 1.4f, .8f, -3.1f, 1, .7f, .2f, 0x40e0ff)
            .box("stache", "head", 0, -.8f, -3.1f, 4, .8f, .3f, 0x2a2a2a).box("hat", "head", 0, 4.4f, 0, 5.4f, 4, 5.4f, WHITE).box("hatTop", "hat", 0, 2, 0, 6.4f, 1.6f, 6.4f, WHITE)
            .limb("armL", "body", -5.6f, 3, 0, 2, 7, 2, WHITE).limb("armR", "body", 5.6f, 3, 0, 2, 7, 2, WHITE)
            .box("cleaver", "armR", 0, -7.4f, -1.4f, .3f, 2.4f, 3, 0xd0d0d8).box("plates", "armL", 0, -7.4f, -1, 3, 2, 3, 0xe8f0ff);
        return CharacterDef.of("uty_sousborg", "Sousborg", Faction.ROYAL_SCIENCE).hp(120).boss().keep(5).rest(16).size(.9f, 1.9f).scale(1.15f).speed(.18f).egg(WHITE, 0x40e0ff)
            .model(m).attacks(sousPan(), sousCrack(), sousFry(), sousSeason()).build();
    }
    /** Pan: a frying pan circles the floor flipping food and utensils up in arcs. */
    static Attack sousPan() {
        return atk("uty_sousborg_pan", 76, (c, t) -> {
            if (t == 4) store(c, "spo", c.target.position());
            Vec3 o = load(c, "spo");
            if (t >= 6 && t < 70) { Vec3 pan = around(o, 2.6, t * .09, .2); c.proj().at(pan).box(1.4f, .2f, 1.4f).color(0x2a2a2a).dmg(3).life(2).spawn();
                if (t % 7 == 0) { Vec3 d = c.target.position().subtract(pan); c.proj().at(pan.add(0, .3, 0)).vel(new Vec3(d.x / 18, .55, d.z / 18)).gravity(.05f).box(.4f, .3f, .4f).color(c.rnd.nextBoolean() ? 0xd8a050 : 0x6ab04a).dmg(3).life(26).spawn(); c.sound(SoundEvents.CAMPFIRE_CRACKLE, .8f, 1.4f); } }
        }, (p, t, m) -> p.r("armR", -1.4f, .4f * wave(t, .3f), 0).r("armL", -1f, 0, .3f).r("head", 0, .3f * wave(t, .09f), 0));
    }
    /** Crack: an egg taps the arena's edge spraying shell, then splits overhead and drops its yolk through the middle. */
    static Attack sousCrack() {
        return atk("uty_sousborg_crack", 84, (c, t) -> {
            if (t == 4) { store(c, "sco", c.target.position()); store(c, "scr", c.right()); }
            Vec3 o = load(c, "sco"), r = load(c, "scr"), corner = o.add(r.scale(3)).add(c.fwd().scale(-3)).add(0, 1, 0);
            if (t >= 6 && t < 44) { c.proj().at(corner.add(0, Math.abs(Math.sin(t * .4)) * .8, 0)).box(.8f, 1, .8f).color(0xfff6e0).dmg(0).life(2).spawn();
                if (t % 8 == 6) { for (int i = 0; i < 3; i++) shot(c, corner, c.aim(corner).scale(.4).add(rotY(new Vec3(.08, .05, 0), i * 2 + t)), .25f, 0xfff6e0, 3, 0, 22); c.sound(SoundEvents.TURTLE_EGG_CRACK, .8f, 1.2f); } }
            if (t == 48) { mark(c, o, 1.6f, 1.6f, YEL, 12); c.proj().at(o.add(-.4, 5, 0)).box(.6f, 1, .8f).color(0xfff6e0).dmg(0).life(14).spawn(); c.proj().at(o.add(.4, 5, 0)).box(.6f, 1, .8f).color(0xfff6e0).dmg(0).life(14).spawn(); }
            if (t == 58) { c.proj().at(o.add(0, 4.6, 0)).vel(new Vec3(0, -.45, 0)).box(1.4f, .6f, 1.4f).color(YEL).dmg(6).life(10).spawn(); c.sound(SoundEvents.SLIME_SQUISH, 1, .8f); }
            if (t == 68) { c.aoe(o, 1.6, 4, .4); c.proj().at(o.add(0, .1, 0)).box(2.6f, .15f, 2.6f).color(0xfff6e0).dmg(2).life(14).spawn(); c.proj().at(o.add(0, .2, 0)).box(1.2f, .2f, 1.2f).color(YEL).dmg(0).life(14).spawn(); }
        }, (p, t, m) -> { float s = win(t, 4, 8, 40, 46), d = win(t, 44, 48, 58, 66); p.r("armL", -1.8f * s, 0, -.3f * s * wave(t, .4f)).r("armR", -2.8f * d, 0, 0).r("cleaver", 1.2f * d, 0, 0); });
    }
    /** Fry: steam rises off the egg; halfway through it catches fire and faster flames shoot up instead. */
    static Attack sousFry() {
        return atk("uty_sousborg_fry", 84, (c, t) -> {
            if (t == 4) { store(c, "sfo", c.target.position().add(flat(c.aim(c.pos())).scale(1.5))); c.proj().at(load(c, "sfo").add(0, .1, 0)).box(2.4f, .15f, 2.4f).color(0xfff6e0).dmg(0).life(78).spawn(); }
            Vec3 o = load(c, "sfo"); boolean fire = t >= 44;
            if (t == 44) { c.sound(SoundEvents.FIRECHARGE_USE, 1, .8f); c.particle(ParticleTypes.FLAME, o, 20, .8, .05); }
            if (t >= 10 && t < 80 && t % (fire ? 2 : 4) == 0) { Vec3 at = o.add(c.rnd.nextGaussian() * 1.6, .2, c.rnd.nextGaussian() * 1.6);
                c.proj().at(at).vel(new Vec3(c.rnd.nextGaussian() * .03, fire ? .4 : .18, c.rnd.nextGaussian() * .03)).box(.45f, .6f, .45f).color(fire ? FIRE : 0xe8f0ff).dmg(3).warn(fire ? 2 : 4).life(fire ? 14 : 24).spawn();
                if (!fire && t % 8 == 0) c.sound(SoundEvents.FIRE_EXTINGUISH, .3f, 1.8f); }
        }, (p, t, m) -> { float s = win(t, 0, 8, 76, 84); p.r("armR", -1.4f * s, 0, 0).r("armL", -1.2f * s, .4f * wave(t, .3f), 0).r("head", .3f * s, 0, 0); });
    }
    /** Season: a salt shaker shakes waves of seasoning; on the fifth shake the cap pops and a stream floods the floor. */
    static Attack sousSeason() {
        return atk("uty_sousborg_season", 100, (c, t) -> {
            if (t == 4) store(c, "sso", c.target.position());
            Vec3 o = load(c, "sso"), top = o.add(0, 5, 0);
            if (t >= 6 && t < 90) c.proj().at(top.add(0, Math.sin(t * .6) * .3, 0)).box(.8f, 1.4f, .8f).color(WHITE).dmg(0).life(2).spawn();
            for (int k = 0; k < 4; k++) if (t == 12 + k * 12) { for (int i = 0; i < 10; i++) { double a = i * .63 + k; shot(c, top, new Vec3(Math.cos(a) * .15, -.3, Math.sin(a) * .15), .2f, WHITE, 2, 0, 24); } c.sound(SoundEvents.SAND_STEP, .8f, 1.6f); }
            if (t == 60) { c.sound(SoundEvents.ITEM_BREAK, 1, 1.4f); mark(c, o, 4, 4, WHITE, 8); }
            if (t >= 62 && t < 90) { c.proj().at(top.add(0, -2.5, 0)).box(.6f, 5, .6f).color(WHITE).dmg(5).life(2).pierce().spawn();
                if (t % 3 == 0) c.proj().at(o.add(0, .1 + (t - 62) * .015, 0)).box(4.4f, .2f, 4.4f).color(0xe8e8e8).dmg(2).life(4).spawn(); }
        }, (p, t, m) -> { float s = win(t, 0, 6, 88, 100); p.r("armL", -2.8f * s, 0, .3f * wave(t, .6f)).r("plates", 0, .4f * wave(t, .6f), 0).r("head", -.3f * s, 0, 0); });
    }

    static CharacterDef tellyVis() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 6, 0, 8, 7, 6, 0x6a5a4a).glow("head", "body", 0, 1, -3.1f, 6, 4.4f, .2f, 0x40a0ff)
            .glow("eyeL", "head", -1.4f, .6f, -.1f, .9f, .9f, .1f, WHITE).glow("eyeR", "head", 1.4f, .6f, -.1f, .9f, .9f, .1f, WHITE)
            .box("vcr", "body", 0, -3, -3.1f, 6, 1, .3f, 0x1a1a1a).box("visL", "body", -3.6f, -3, -3.1f, .6f, .6f, .2f, WHITE).box("visR", "body", 3.6f, -3, -3.1f, .6f, .6f, .2f, WHITE)
            .box("antL", "body", -1.4f, 5, 0, .3f, 3, .3f, 0x2a2a2a).box("antR", "body", 1.4f, 5, 0, .3f, 3, .3f, 0x2a2a2a)
            .limb("armL", "body", -4.8f, 1, 0, 1.2f, 5, 1.2f, YEL).limb("armR", "body", 4.8f, 1, 0, 1.2f, 5, 1.2f, YEL)
            .box("remote", "armR", 0, -5, -.6f, .8f, .4f, 1.6f, 0x2a2a2a)
            .limb("legL", null, -2, 2.5f, 0, 1.4f, 2.5f, 1.4f, YEL).limb("legR", null, 2, 2.5f, 0, 1.4f, 2.5f, 1.4f, YEL);
        return bot("uty_telly_vis", "Telly-Vis", 46).size(.7f, 1.2f).scale(.9f).speed(.24f).egg(0x6a5a4a, 0x40a0ff).model(m).attacks(tvLogo(), tvTape(), tvRemote()).build();
    }
    /** Logo: an "SWS Video" logo ricochets around the area, flipping white/blue/orange on every wall hit. */
    static Attack tvLogo() {
        return atk("uty_telly_vis_logo", 84, (c, t) -> {
            if (t == 4) { store(c, "tlo", c.target.position().add(0, .8, 0)); store(c, "tlf", flat(c.aim(c.pos()))); }
            Vec3 o = load(c, "tlo"), f = load(c, "tlf"), r = side(f);
            if (t >= 8 && t < 80) { double ux = (t - 8) * .3, uz = (t - 8) * .22; double x = bounce(ux, 3), z = bounce(uz, 3);
                int hits = (int) Math.floor((ux + 3) / 6) + (int) Math.floor((uz + 3) / 6), mode = hits % 3;
                var b = c.proj().at(o.add(r.scale(x)).add(f.scale(z))).box(1.4f, .8f, .2f).dmg(4).warn(t < 16 ? 8 : 0).life(2); (mode == 0 ? b.color(WHITE) : mode == 1 ? b.blue() : b.orange()).spawn(); }
            if (t % 12 == 8) c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), .4f, 1.5f);
        }, (p, t, m) -> p.r("armR", -1.4f, 0, 0).r("antL", 0, 0, .3f * wave(t, .5f)).r("antR", 0, 0, -.3f * wave(t, .5f)));
    }
    /** Tape: VHS tapes snap into line with the target at the edge and shoot across, one at a time. */
    static Attack tvTape() {
        return atk("uty_telly_vis_tape", 72, (c, t) -> {
            for (int k = 0; k < 5; k++) { int b = 6 + k * 11;
                if (t == b) { double a = c.rnd.nextInt(4) * Math.PI / 2; Vec3 at = around(c.tpos(), 6, a, 0); store(c, "tt" + k, at); c.proj().at(at).face(c.aim(at)).box(1, .25f, .6f).color(0x1a1a1a).dmg(0).life(8).spawn(); c.sound(SoundEvents.UI_BUTTON_CLICK.value(), .5f, 1.4f); }
                if (t == b + 8) { Vec3 at = load(c, "tt" + k); Vec3 v = c.aim(at).scale(.8); c.proj().at(at).vel(v).face(v).box(1, .25f, .6f).color(0x1a1a1a).dmg(5).life(18).spawn(); c.proj().at(at.add(0, .14, 0)).vel(v).face(v).box(.6f, .05f, .4f).color(WHITE).dmg(0).life(18).spawn(); } }
        }, (p, t, m) -> { float s = t >= 6 ? win((t - 6) % 11, 0, 4, 8, 11) : 0; p.o("vcr", 0, 0, -.6f * s).r("armL", -1.6f * s, 0, 0); });
    }
    /** Remote: channel-surfing through four earlier enemies' attacks (snowflakes, needles, flies, mirror shards). */
    static Attack tvRemote() {
        return atk("uty_telly_vis_remote", 96, (c, t) -> {
            int ch = Math.min(3, (t - 6) / 22);
            if (t >= 6 && (t - 6) % 22 == 0 && t < 94) { c.sound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1.8f); c.particle(ParticleTypes.ELECTRIC_SPARK, c.eye(), 10, .4, .05); }
            if (t >= 10 && t < 94 && (t - 6) % 22 >= 4) switch (ch) {
                case 0 -> { if (t % 2 == 0) c.proj().at(c.target.position().add(c.rnd.nextGaussian() * 3, 6, c.rnd.nextGaussian() * 3)).vel(new Vec3(0, -.34, 0)).box(.3f, .3f, .3f).color(WHITE).dmg(2).warn(3).life(20).spawn(); }
                case 1 -> { if (t % 4 == 0) { int s = (t / 4) % 2 == 0 ? 1 : -1; Vec3 at = c.tpos().add(c.right().scale(5 * s)).add(0, 1.2, 0); c.proj().at(at).vel(c.right().scale(-.32 * s).add(0, .2, 0)).gravity(.035f).box(.1f, .1f, .6f).color(0xd8f0a0).dmg(3).warn(4).life(28).spawn(); } }
                case 2 -> { if (t % 3 == 0) { Vec3 at = around(c.tpos(), 6.5, c.rnd.nextDouble() * 6.28, 0); fly(c, at, c.tpos().subtract(at).normalize().scale(.36), 3, 6, 34); } }
                default -> { if (t % 7 == 0) { Vec3 base = around(c.tpos(), 5, c.rnd.nextDouble() * 6.28, 0), d = c.tpos().subtract(base).normalize(); for (int k = 0; k < 3; k++) dart(c, base.subtract(d.scale(k * .7)), d.scale(.45), .15f, .6f, 0xd8f4ff, 3, 8 + k * 2, 28); } }
            }
        }, (p, t, m) -> { boolean click = t >= 6 && (t - 6) % 22 < 4; p.r("armR", click ? -1.9f : -1.5f, 0, 0).o("head", 0, 0, click ? -.2f : 0); });
    }

    static CharacterDef macroFroggit() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 9, 0, 14, 10, 12, WHITE).box("head", "body", 0, 7, -1, 14, 6, 12, WHITE)
            .glow("eyeL", "head", -3.6f, 3.4f, -4, 2.6f, 2.6f, 2.6f, 0x1a1a1a).glow("eyeR", "head", 3.6f, 3.4f, -4, 2.6f, 2.6f, 2.6f, 0x1a1a1a)
            .box("mouth", "head", 0, -1.4f, -6.05f, 7, .6f, .2f, 0x1a1a1a).box("cheekL", "head", -5, -.6f, -6.05f, 1.6f, .8f, .2f, 0xffb0b0).box("cheekR", "head", 5, -.6f, -6.05f, 1.6f, .8f, .2f, 0xffb0b0)
            .limb("armL", "body", -6, -2, -4, 2.4f, 4, 2.4f, WHITE).limb("armR", "body", 6, -2, -4, 2.4f, 4, 2.4f, WHITE)
            .limb("legL", null, -5, 4, 2, 3, 4, 5, WHITE).limb("legR", null, 5, 4, 2, 3, 4, 5, WHITE);
        return CharacterDef.of("uty_macro_froggit", "Macro Froggit", Faction.UNDERGROUND).hp(130).boss().keep(7).rest(16).size(1.6f, 1.8f).scale(1.4f).speed(.2f).egg(WHITE, GREEN)
            .model(m).attacks(macroTongues(), macroScooters(), macroSwarm(), macroBoats(), macroDuel(), macroSpace()).build();
    }
    static void ribbit(Pose p, float t, float s) { p.o("body", 0, s * Math.abs(wave(t, .3f)), 0).r("legL", -.5f * s * Math.abs(wave(t, .3f)), 0, 0).r("legR", -.5f * s * Math.abs(wave(t, .3f)), 0, 0); }
    /** Tongues: frogs line both flanks while flies wander between; tongues lash across to snap up flies, briefly walling the lane. */
    static Attack macroTongues() {
        return atk("uty_macro_froggit_tongues", 90, (c, t) -> {
            if (t == 4) { store(c, "mto", c.target.position()); store(c, "mtr", c.right()); store(c, "mtf", c.fwd()); }
            Vec3 o = load(c, "mto"), r = load(c, "mtr"), f = load(c, "mtf");
            if (t >= 4 && t < 86 && t % 4 == 0) for (int s = -1; s <= 1; s += 2) for (int k = -2; k <= 2; k++) c.proj().at(o.add(r.scale(s * 3.4)).add(f.scale(k * 1.4)).add(0, .3, 0)).box(.7f, .5f, .7f).color(WHITE).dmg(0).life(4).spawn();
            if (t >= 8 && t < 80 && t % 3 == 0) for (int i = 0; i < 3; i++) { Vec3 at = o.add(r.scale(Math.sin(t * .07 + i * 2) * 2)).add(f.scale(Math.cos(t * .05 + i * 1.3) * 2.6)).add(0, .8, 0); fly(c, at, Vec3.ZERO, 2, t < 14 ? 6 : 0, 3); }
            if (t >= 20 && t < 80 && t % 12 == 8) { int s = (t / 12) % 2 == 0 ? 1 : -1, k = c.rnd.nextInt(5) - 2; Vec3 from = o.add(r.scale(s * 3.4)).add(f.scale(k * 1.4)).add(0, .5, 0);
                mark(c, from.add(r.scale(-s * 3.4)).add(0, -.5, 0), 6.8f, .6f, 0xff8aa0, 8); c.proj().at(from.add(r.scale(-s * 3.4))).face(r).box(.35f, .35f, 6.8f).color(0xff8aa0).dmg(4).warn(8).life(14).pierce().spawn(); c.sound(SoundEvents.FROG_TONGUE, 1, .8f); }
        }, (p, t, m) -> { ribbit(p, t, .6f); p.o("mouth", 0, -.6f * (t % 12 < 4 ? 1 : 0), 0); });
    }
    /** Scooters: two roads; frogs on scooters zip down them puffing steam; a green frog strolls across to be caught. */
    static Attack macroScooters() {
        return atk("uty_macro_froggit_scooters", 90, (c, t) -> {
            if (t == 4) { store(c, "mso", c.target.position()); store(c, "msr", c.right()); store(c, "msf", c.fwd()); for (int l = 0; l < 2; l++) mark(c, load(c, "mso").add(load(c, "msf").scale(l == 0 ? -1.4 : 1.4)), 12, 1.4f, 0x5a5a5a, 16); }
            Vec3 o = load(c, "mso"), r = load(c, "msr"), f = load(c, "msf");
            if (t >= 12 && t < 76 && t % 9 == 3) { int lane = (t / 9) % 2, s = lane == 0 ? 1 : -1; Vec3 at = o.add(f.scale(lane == 0 ? -1.4 : 1.4)).add(r.scale(-7 * s)).add(0, .5, 0), v = r.scale(.5 * s);
                c.proj().at(at).vel(v).box(1, .8f, .8f).color(WHITE).dmg(4).life(30).spawn(); c.proj().at(at.add(0, -.4, 0)).vel(v).box(1.4f, .2f, .5f).color(RED).dmg(0).life(30).spawn(); c.sound(SoundEvents.MINECART_RIDING, .4f, 1.8f); }
            if (t >= 14 && t < 76 && t % 4 == 0) for (int l = 0; l < 2; l++) { Vec3 at = o.add(f.scale(l == 0 ? -1.4 : 1.4)).add(r.scale(c.rnd.nextGaussian() * 3)).add(0, .3, 0); c.proj().at(at).vel(new Vec3(0, .12, 0)).box(.4f, .4f, .4f).color(0xe8f0ff).dmg(2).warn(4).life(10).spawn(); }
            if (t >= 40 && t < 80) { Vec3 g = o.add(f.scale(-3 + (t - 40) * .15)).add(0, .3, 0); c.proj().at(g).box(.5f, .4f, .5f).color(GREEN).dmg(0).life(2).spawn();
                if (c.target.position().distanceTo(g) < .8 && loadi(c, "msg") != t / 100 + 1) { c.target.heal(3); storei(c, "msg", t / 100 + 1); c.particle(ParticleTypes.HAPPY_VILLAGER, g, 8, .3, .05); } }
            if (t == 4) storei(c, "msg", 0);
        }, (p, t, m) -> { ribbit(p, t, .4f); p.r("armL", -.6f, 0, .3f * wave(t, .4f)); });
    }
    /** Swarm: flies fill the area and loosely home on the target. */
    static Attack macroSwarm() {
        return atk("uty_macro_froggit_fly_swarm", 76, (c, t) -> {
            if (t >= 8 && t < 60 && t % 3 == 0) { Vec3 at = around(c.tpos(), 5 + c.rnd.nextDouble() * 2, c.rnd.nextDouble() * 6.28, c.rnd.nextGaussian() * .6);
                fly(c, at, c.aim(at).scale(.22).add(c.rnd.nextGaussian() * .08, c.rnd.nextGaussian() * .03, c.rnd.nextGaussian() * .08), 3, 6, 40); }
            if (t == 8) c.sound(SoundEvents.BEE_LOOP_AGGRESSIVE, 1, 1.2f);
        }, (p, t, m) -> { ribbit(p, t, .3f); p.r("head", -.2f, .3f * wave(t, .3f), 0); });
    }
    /** Boats: three boats of four frogs row up; the frogs leap at the target two at a time (one green, catch it to heal). */
    static Attack macroBoats() {
        return atk("uty_macro_froggit_boats", 96, (c, t) -> {
            if (t == 4) { store(c, "mbo", c.target.position()); store(c, "mbr", c.right()); store(c, "mbf", flat(c.aim(c.pos()))); }
            Vec3 o = load(c, "mbo"), r = load(c, "mbr"), f = load(c, "mbf");
            if (t >= 6 && t < 40) for (int b = -1; b <= 1; b++) { Vec3 at = o.add(f.scale(7 - (t - 6) * .12)).add(r.scale(b * 2.4)).add(0, .3, 0); c.proj().at(at).box(1.8f, .4f, 1).color(0x8a5a2a).dmg(0).life(2).spawn();
                for (int k = 0; k < 4; k++) c.proj().at(at.add(r.scale((k - 1.5) * .4)).add(0, .4, 0)).box(.3f, .3f, .3f).color(WHITE).dmg(0).life(2).spawn(); }
            if (t >= 44 && t < 88 && t % 7 == 2) { int j = (t - 44) / 7; for (int k = 0; k < 2; k++) { Vec3 at = o.add(f.scale(2.9)).add(r.scale((c.rnd.nextInt(3) - 1) * 2.4)).add(0, .7, 0), d = c.target.position().subtract(at);
                    boolean green = j == 3 && k == 0; c.proj().at(at).vel(new Vec3(d.x / 14, .35 + d.y / 14, d.z / 14)).gravity(.05f).box(.4f, .4f, .4f).color(green ? GREEN : WHITE).dmg(green ? 0 : 3).life(16).spawn();
                    if (green) store(c, "mbg", c.target.position()); }
                c.sound(SoundEvents.FROG_LONG_JUMP, .6f, 1.4f); }
            if (t == 80 && c.target.position().distanceTo(load(c, "mbg")) < 1.2) { c.target.heal(3); c.particle(ParticleTypes.HAPPY_VILLAGER, c.tpos(), 8, .4, .05); }
        }, (p, t, m) -> { ribbit(p, t, .5f); p.r("armL", -1.2f * Math.abs(wave(t, .4f)), 0, 0).r("armR", -1.2f * Math.abs(wave(t, .4f + 1.5f)), 0, 0); });
    }
    /** Duel: a gentleman fly and a lady fly trade three-way volleys across the target, reticles warning each shot (blue/red). */
    static Attack macroDuel() {
        return atk("uty_macro_froggit_fly_duel", 84, (c, t) -> {
            if (t == 4) { store(c, "mdo", c.target.position().add(0, 1, 0)); store(c, "mdr", c.right()); }
            Vec3 o = load(c, "mdo"), r = load(c, "mdr"), a = o.add(r.scale(-4)), b = o.add(r.scale(4));
            if (t >= 4 && t < 80 && t % 3 == 0) { fly(c, a.add(0, Math.sin(t * .2) * .4, 0), Vec3.ZERO, 0, 0, 3); fly(c, b.add(0, Math.cos(t * .2) * .4, 0), Vec3.ZERO, 0, 0, 3);
                c.proj().at(a.add(0, .45, 0)).box(.35f, .3f, .35f).color(0x1a1a1a).dmg(0).life(3).spawn(); c.proj().at(b.add(0, .45, 0)).box(.5f, .15f, .5f).color(PINK).dmg(0).life(3).spawn(); }
            for (int k = 0; k < 6; k++) { int s0 = 10 + k * 11; boolean male = k % 2 == 0; Vec3 from = male ? a : b;
                if (t == s0) { Vec3 aimAt = c.tpos(); store(c, "mdt" + k, aimAt); mark(c, c.target.position(), .9f, .9f, male ? BLUE : RED, 8); }
                if (t == s0 + 8) { Vec3 d = load(c, "mdt" + k).subtract(from).normalize(); for (int i = -1; i <= 1; i++) shot(c, from, rotY(d, i * .25).scale(.45), .3f, male ? BLUE : RED, 3, 0, 26); c.sound(SoundEvents.BLAZE_SHOOT, .4f, 1.8f); } }
        }, (p, t, m) -> { ribbit(p, t, .2f); p.r("head", 0, .5f * wave(t, .1f), 0); });
    }
    /** Space: starfield; a frog spaceship advances, lasering at the target's height while meteors drift in and burst when hit. */
    static Attack macroSpace() {
        return atk("uty_macro_froggit_space", 110, (c, t) -> {
            if (t == 2) { store(c, "mxo", c.target.position()); store(c, "mxr", c.right()); c.target.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0)); c.sound(SoundEvents.BEACON_ACTIVATE, .8f, .6f); }
            Vec3 o = load(c, "mxo"), r = load(c, "mxr"), ship = o.add(r.scale(7 - Math.max(0, t - 10) * .05)).add(0, 1, 0);
            if (t % 3 == 0) c.particle(ParticleTypes.END_ROD, o.add(c.rnd.nextGaussian() * 4, 2 + c.rnd.nextDouble() * 2, c.rnd.nextGaussian() * 4), 1, 0, 0);
            if (t >= 6 && t < 104) { c.proj().at(ship).box(1.4f, .6f, 1).color(0xb0b8c0).dmg(5).life(2).spawn(); c.proj().at(ship.add(0, .45, 0)).box(.5f, .4f, .5f).color(WHITE).dmg(0).life(2).spawn(); }
            if (t >= 16 && t < 100 && t % 14 == 2) { Vec3 at = ship.add(r.scale(-.8)); double y = c.tpos().y - at.y; Vec3 aim = r.scale(-1).add(0, y / 8, 0).normalize();
                c.proj().at(at.add(aim.scale(6))).face(aim).box(.3f, .3f, 12).color(GREEN).dmg(4).warn(8).life(12).pierce().spawn(); c.sound(SoundEvents.BEACON_POWER_SELECT, .5f, 2); }
            if (t >= 10 && t < 96 && t % 16 == 10) { Vec3 at = o.add(r.scale(-6)).add(c.fwd().scale(c.rnd.nextGaussian() * 1.6)).add(0, .5 + c.rnd.nextDouble() * 1.4, 0); store(c, "mxm", at);
                c.proj().at(at).vel(r.scale(.18)).box(.9f, .9f, .9f).color(0x8a7a6a).dmg(4).life(30).spawn(); }
            if (t >= 26 && t < 106 && t % 16 == 8) { Vec3 m0 = load(c, "mxm").add(r.scale(.18 * 14)); for (int i = 0; i < 4; i++) shot(c, m0, new Vec3(0, .25, 0).add(r.scale((i - 1.5) * .12)).add(c.fwd().scale((i % 2 - .5) * .1)), .35f, 0x8a7a6a, 2, 0, 20);
                c.particle(ParticleTypes.EXPLOSION, m0, 1, 0, 0); }
        }, (p, t, m) -> { ribbit(p, t, .2f); p.r("head", -.3f, 0, 0).r("eyeL", 0, t * .1f, 0); }).when(m -> m.hpFrac() < .6f);
    }

    private UtyFieldEnemies() {}
}
