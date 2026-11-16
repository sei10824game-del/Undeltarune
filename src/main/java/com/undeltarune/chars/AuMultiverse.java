package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import java.util.List;

import static com.undeltarune.attack.AuKit.*;

/** Multiverse AUs (fan canon): destroyers (Error, Nightmare + gang), guardians (Ink, Dream) and other famous Sanses.
 *  Blueberry/Swap, Dust, Horror, Underfell variants live in their own group files. */
public final class AuMultiverse {
    static final int STR = 0x2f6bff, RED = 0xd11a1a, YEL = 0xffd400, WHITE = 0xffffff, BLACK = 0x0c0c12, CYAN = 0x40e0ff;
    static final int[] RAINBOW = {0xff3b3b, 0xff9a1f, 0xffe23b, 0x4cff6a, 0x3fa7ff, 0x8a5cff, 0xff5cd6};

    public static void register(List<CharacterDef> out) {
        out.add(errorSans()); out.add(inkSans()); out.add(nightmare()); out.add(dream());
        out.add(cross()); out.add(killer()); out.add(epic()); out.add(fresh());
        out.add(geno()); out.add(reaper()); out.add(outertale());
    }

    /** Shared Sans skeleton (same proportions as the reference Sans). */
    static ModelDef skel(int jacket, int hood, int legs, int skull, int eyeL, int eyeR, int grin) {
        return new ModelDef()
            .box("body", null, 0, 8, 0, 8, 7, 5, jacket)
            .box("head", "body", 0, 9.5f, 0, 9, 7, 7, skull)
            .glow("eyeL", "head", -2, .5f, -3.6f, 1.6f, 1.6f, .4f, eyeL)
            .glow("eyeR", "head", 2, .5f, -3.6f, 1.6f, 1.6f, .4f, eyeR)
            .box("grin", "head", 0, -2, -3.6f, 5, .7f, .4f, grin)
            .box("hood", "body", 0, 3.4f, 0, 9, 1.5f, 6, hood)
            .limb("armL", "body", -5, 3, 0, 2.4f, 6, 2.4f, jacket)
            .limb("armR", "body", 5, 3, 0, 2.4f, 6, 2.4f, jacket)
            .limb("legL", null, -2, 5, 0, 3, 5, 3.5f, legs)
            .limb("legR", null, 2, 5, 0, 3, 5, 3.5f, legs);
    }
    static Vec3 hand(AttackCtx c, int side) { return c.pos().add(c.right().scale(side * .4)).add(c.fwd().scale(.4)).add(0, .8, 0); }
    static void pull(AttackCtx c, double s) {
        Vec3 d = c.pos().subtract(c.target.position()).normalize().scale(s);
        c.target.setDeltaMovement(d.x, .1, d.z); c.target.hurtMarked = true;
    }
    static void eff(AttackCtx c, net.minecraft.world.effect.MobEffect e, int dur, int amp) { c.target.addEffect(new MobEffectInstance(e, dur, amp)); }

    // ============================================================ ERROR SANS (destroyer of AUs)
    static CharacterDef errorSans() {
        return CharacterDef.of("error_sans", "Error!Sans", Faction.ERROR_AU).hp(280).boss().keep(8).rest(12).speed(.32f)
            .egg(0x111111, 0xd11a1a).size(.7f, 1.1f).scale(.95f)
            .model(skel(0x161616, RED, 0x111111, 0x1b1b1b, RED, YEL, 0xffffff)
                .glow("eyeRing", "eyeR", 0, 0, -.1f, .7f, .7f, .2f, STR)
                .glow("tearL", "head", -2, -1.6f, -3.6f, .6f, 2.4f, .3f, STR)
                .glow("tearR", "head", 2, -1.6f, -3.6f, .6f, 2.4f, .3f, STR)
                .box("shirt", "body", 0, 0, -2.6f, 5, 6, .4f, 0x222222)
                .box("sleeveL", "armL", 0, -5, 0, 2.7f, 1.6f, 2.7f, RED)
                .box("sleeveR", "armR", 0, -5, 0, 2.7f, 1.6f, 2.7f, RED)
                .glow("handL", "armL", 0, -6.5f, 0, 2, 1.2f, 2, 0x3a5cff)
                .glow("handR", "armR", 0, -6.5f, 0, 2, 1.2f, 2, 0x3a5cff)
                .ghost("glitch1", "head", 3.5f, 2.5f, -3.7f, 2.5f, .8f, .2f, RED, .6f)
                .ghost("glitch2", "body", -3.5f, -1, -2.7f, 2, .6f, .2f, YEL, .6f)
                .ghost("glitch3", "head", -3, -3, -3.7f, 1.6f, .5f, .2f, STR, .6f))
            .attacks(errStringWeb(), errBlasters(), errBoneRain(), errStringBind(), errGlitchWarp(), errCocoon()).build();
    }
    /** Strings drop from the sky and stake the ground in a closing lattice around the target. */
    static Attack errStringWeb() {
        return atk("error_string_web", 80, (c, t) -> {
            if (t == 0) c.sound(SoundEvents.TRIPWIRE_ATTACH, 1, .6f);
            if (t < 48 && t % 6 == 0) {
                Vec3 tp = c.target.position(); double a = c.rnd.nextDouble() * Math.PI;
                Vec3 sky = around(tp, 1.5, a, 9), gnd = around(tp, 4.5, a + Math.PI, 0);
                seg(c, sky, gnd, .12f, STR, 5, 12, 26);
                Vec3 sky2 = around(tp, 1.5, a + Math.PI / 2, 9), gnd2 = around(tp, 4.5, a - Math.PI / 2, 0);
                seg(c, sky2, gnd2, .12f, STR, 5, 12, 26);
                c.particle(ParticleTypes.ELECTRIC_SPARK, tp.add(0, 1, 0), 8, 1.5, .1);
                c.sound(SoundEvents.TRIPWIRE_CLICK_ON, .8f, .5f + c.rnd.nextFloat());
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 80, 12), w = wave(t, .8f) * .25f * s;
            p.r("armL", -2.7f * s, 0, .4f * s + w).r("armR", -2.7f * s, 0, -.4f * s - w).r("head", .2f * s, 0, w * .5f)
                .o("glitch1", w * 4, 0, 0).o("glitch2", -w * 4, 0, 0);
        });
    }
    /** Black-and-red "ERROR" blasters: staggered volleys, then a full ring at low HP. */
    static Attack errBlasters() {
        return atk("error_blasters", 96, (c, t) -> {
            boolean enraged = c.me.hpFrac() < .5f;
            if (t < 40 && t % (enraged ? 6 : 10) == 0) {
                double a = c.rnd.nextDouble() * Math.PI * 2;
                Vec3 at = around(c.tpos(), 6, a, 2 + c.rnd.nextDouble() * 2);
                blaster(c, at, c.tpos().subtract(at), 20, 12, 8, .9f, 0x141414, RED, 0xff3030);
                c.particle(ParticleTypes.SMOKE, at, 8, .4, .02);
            }
            if (enraged && t == 50) for (int i = 0; i < 8; i++) {
                Vec3 at = around(c.tpos(), 7, i * Math.PI / 4, 1.2);
                blaster(c, at, c.tpos().subtract(at), 24, 14, 8, .8f, 0x141414, YEL, 0xff3030);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 96, 14);
            p.r("armR", -1.6f * s, -.3f, -.2f).r("armL", -.4f * s, 0, .6f * s).o("body", 0, .6f * s, 0)
                .o("glitch3", wave(t, 2.3f) * 3, 0, 0);
        });
    }
    /** Glitched bones (black/red) rain in jittering columns. */
    static Attack errBoneRain() {
        return atk("error_bone_rain", 70, (c, t) -> {
            if (t >= 6 && t < 54 && t % 3 == 0) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3, 7, c.rnd.nextGaussian() * 3);
                int col = c.rnd.nextInt(3) == 0 ? RED : 0x111111;
                c.proj().at(at).vel(new Vec3(0, -.55, 0)).box(.24f, 1.4f, .24f).color(col).dmg(4).warn(6).life(26).spawn();
                if (t % 9 == 0) c.particle(ParticleTypes.ELECTRIC_SPARK, at, 4, .2, .1);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 70, 10), j = (t % 4 < 2 ? 1 : -1) * .1f * s;
            p.r("armL", -3f * s, 0, .2f).r("armR", -3f * s, 0, -.2f).r("head", -.3f * s, j, 0).o("body", j * 5, 0, 0);
        });
    }
    /** Strings latch onto the target, drag it in (heavy slow) and a blaster pair fires point blank. */
    static Attack errStringBind() {
        return atk("error_string_bind", 90, (c, t) -> {
            if (t == 10) c.sound(SoundEvents.FISHING_BOBBER_THROW, 1, .5f);
            if (t >= 10 && t < 50 && t % 5 == 0) {
                for (int s = -1; s <= 1; s += 2) seg(c, hand(c, s), c.tpos(), .07f, STR, 1.5f, 0, 6);
                pull(c, .35); eff(c, MobEffects.MOVEMENT_SLOWDOWN, 30, 3);
                c.particle(ParticleTypes.ELECTRIC_SPARK, c.tpos(), 6, .4, .05);
            }
            if (t == 52) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = c.tpos().add(c.right().scale(s * 3.5)).add(0, 1.5, 0);
                blaster(c, at, c.tpos().subtract(at), 16, 14, 10, 1f, 0x141414, RED, 0xff3030);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 90, 16), yank = t > 10 && t < 50 ? wave(t, 1.25f) * .3f : 0;
            p.r("armL", -1.5f * s + yank, .3f, .3f).r("armR", -1.5f * s - yank, -.3f, -.3f).o("body", 0, 0, 1.5f * s);
        });
    }
    /** Phase 2: glitches between three points around the target, each arrival bursting strings and glitch squares. */
    static Attack errGlitchWarp() {
        return atk("error_glitch_warp", 84, (c, t) -> {
            if (t % 24 == 4 && t < 76) {
                Vec3 at = around(c.target.position(), 5, c.rnd.nextDouble() * Math.PI * 2, 0);
                c.particle(ParticleTypes.REVERSE_PORTAL, c.pos().add(0, 1, 0), 30, .5, .1);
                c.tp(at); c.face(c.tpos()); c.sound(SoundEvents.ENDERMAN_TELEPORT, 1, .4f);
            }
            if (t % 24 == 10 && t < 76) {
                Vec3 o = c.pos().add(0, 1, 0);
                for (int i = 0; i < 10; i++) {
                    double a = i * Math.PI / 5 + t;
                    seg(c, o, around(o, 7, a, -1), .09f, STR, 4, 6, 16);
                }
                ring(c, o, 12, .5, t, .45f, RED, 4, 4, 30);
            }
        }, (p, t, m) -> {
            float k = (t % 24) / 24f, f = k < .3f ? k / .3f : 1 - (k - .3f) / .7f;
            p.r("armL", -2f * f, 0, 1.2f * f).r("armR", -2f * f, 0, -1.2f * f).r("body", 0, 6.28f * k, 0)
                .o("glitch1", wave(t, 3) * 4, 0, 0).o("glitch2", wave(t, 2.5f) * 4, 0, 0);
        }).when(m -> m.hpFrac() < .5f);
    }
    /** Phase 3: a rotating cage of strings spirals inward to cocoon the target, then snaps. */
    static Attack errCocoon() {
        return atk("error_puppet_cocoon", 100, (c, t) -> {
            Vec3 tp = c.target.position();
            if (t < 70 && t % 7 == 0) {
                double r = 6 - t * .07;
                for (int i = 0; i < 6; i++) {
                    double a = i * Math.PI / 3 + t * .08;
                    seg(c, around(tp, r, a, 8), around(tp, r, a + 1.2, 0), .1f, STR, 4, 4, 9);
                }
                eff(c, MobEffects.MOVEMENT_SLOWDOWN, 20, 1);
            }
            if (t == 76) { c.aoe(c.tpos(), 2.5, 12, 1.2); c.particle(ParticleTypes.ELECTRIC_SPARK, c.tpos(), 60, 1, .3); c.sound(SoundEvents.GLASS_BREAK, 1.2f, .5f); }
        }, (p, t, m) -> {
            float s = env(t, 10, 100, 20), twitch = wave(t, 1.6f) * .4f;
            p.r("armL", -2.2f * s, twitch, .8f).r("armR", -2.2f * s, -twitch, -.8f).r("head", 0, 0, twitch * .3f);
            if (t > 70 && t < 80) p.r("armL", .8f, 0, 0).r("armR", .8f, 0, 0);
        }).when(m -> m.hpFrac() < .3f);
    }

    // ============================================================ INK SANS (guardian of AUs)
    static CharacterDef inkSans() {
        return CharacterDef.of("ink_sans", "Ink!Sans", Faction.GUARDIAN_AU).att(Attitude.NEUTRAL).hp(240).boss().keep(5).rest(14).speed(.33f)
            .egg(0x6b4a2b, 0xfff2c8).size(.7f, 1.1f).scale(.95f)
            .model(skel(0x3a2c22, 0x6b4a2b, 0x2a2a2a, 0xf5f5f5, 0xffe23b, 0x3fa7ff, 0x222222)
                .box("inkStain", "head", -3, 1.5f, -3.6f, 2.5f, 3, .4f, 0x111111)
                .box("scarf", "body", 0, 3.6f, -1, 9.5f, 1.4f, 7, 0x8a6a45)
                .box("scarfTail", "scarf", -2.5f, -4, 3.4f, 2.5f, 7, .6f, 0x8a6a45)
                .box("sash", "body", 0, -2, -2.6f, 8.4f, 1.2f, .4f, 0x5a3a20)
                .glow("vial1", "sash", -3, -1.4f, -.5f, 1, 2, 1, 0xff3b3b)
                .glow("vial2", "sash", -1, -1.4f, -.5f, 1, 2, 1, 0xffe23b)
                .glow("vial3", "sash", 1, -1.4f, -.5f, 1, 2, 1, 0x4cff6a)
                .glow("vial4", "sash", 3, -1.4f, -.5f, 1, 2, 1, 0x8a5cff)
                .box("brush", "armR", 0, -7, -3, .9f, .9f, 12, 0x7a5230)
                .box("bristles", "brush", 0, 0, -7, 2.4f, 2.4f, 3, 0x111111))
            .attacks(inkBroomie(), inkSplash(), inkBlasters(), inkVial(), inkPortal(), inkGiantStroke()).build();
    }
    /** Broomie (giant brush) overhead swing: an arc of ink blobs paints itself in front of him. */
    static Attack inkBroomie() {
        return atk("ink_broomie_sweep", 50, (c, t) -> {
            if (t >= 14 && t <= 24) {
                double k = (t - 14) / 10.0, a = Math.atan2(c.fwd().z, c.fwd().x) - 1.3 + 2.6 * k;
                Vec3 at = around(c.pos(), 2.8, a, .8);
                c.proj().at(at).box(1.1f, 1.1f, 1.1f).color(0x111111).dmg(6).life(10).pierce().spawn();
                c.particle(ParticleTypes.SQUID_INK, at, 6, .3, .05);
                if (t == 14) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .7f);
            }
            if (t == 26) c.aoe(c.pos().add(c.fwd().scale(2.5)), 2.6, 4, .8);
        }, (p, t, m) -> {
            float up = Math.min(1, t / 12f), sw = t < 12 ? 0 : Math.min(1, (t - 12) / 10f), rec = env(t, 1, 50, 18);
            p.r("armR", (-3f * up + 3.6f * sw) * rec, 0, -.3f).r("body", 0, (-.5f * up + 1.1f * sw) * rec, 0).o("scarfTail", 0, 0, sw * 2);
        });
    }
    /** Lobbed rainbow paint blobs that splatter into ink puddles. */
    static Attack inkSplash() {
        return atk("ink_paint_splash", 70, (c, t) -> {
            if (t >= 8 && t < 48 && t % 5 == 0) {
                Vec3 o = hand(c, 1).add(0, .6, 0), d = c.tpos().subtract(o);
                double flight = 18; Vec3 v = new Vec3(d.x / flight, d.y / flight + .025f * flight * .5, d.z / flight)
                    .add(c.rnd.nextGaussian() * .05, 0, c.rnd.nextGaussian() * .05);
                int col = RAINBOW[(t / 5) % RAINBOW.length];
                c.proj().at(o).vel(v).box(.5f, .5f, .5f).color(col).dmg(4).gravity(.025f).life(24).spawn();
                // puddle where it lands
                c.proj().at(c.target.position().add(v.x * 2, .05, v.z * 2)).box(1.6f, .1f, 1.6f).color(col).dmg(3).warn(18).life(40).pierce().spawn();
                c.sound(SoundEvents.SLIME_SQUISH, .8f, 1.4f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 70, 12), flick = (t % 5) < 2 ? 1 : 0;
            p.r("armR", (-2.2f + flick * 1.4f) * s, 0, -.2f).r("armL", -.6f * s, 0, .4f).r("head", 0, wave(t, .4f) * .2f, 0);
        });
    }
    /** Paint blasters: each skull a different color, fired in a sweeping fan. */
    static Attack inkBlasters() {
        return atk("ink_paint_blasters", 92, (c, t) -> {
            if (t < 42 && t % 6 == 0) {
                int i = t / 6;
                Vec3 at = c.pos().add(c.right().scale((i - 3) * 1.6)).add(0, 2.6 + (i % 2) * .8, 0);
                blaster(c, at, c.tpos().subtract(at), 26 - i * 2, 12, 7, .8f, 0xf2f2f2, RAINBOW[i % 7], RAINBOW[i % 7]);
                c.particle(ParticleTypes.SQUID_INK, at, 6, .4, .02);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 92, 14);
            p.r("armR", -2.8f * s, 0, -.2f).r("armL", -1.2f * s, 0, .6f * s).o("body", 0, s, 0).o("scarfTail", 0, 0, wave(t, .3f));
        });
    }
    /** Drinks an emotion vial for a buff, then a rainbow wave of bones. */
    static Attack inkVial() {
        return atk("ink_emotion_vial", 70, (c, t) -> {
            if (t == 14) {
                MobEffectInstance[] buffs = {
                    new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0), new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1),
                    new MobEffectInstance(MobEffects.REGENERATION, 120, 1), new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 0)};
                c.me.addEffect(buffs[c.rnd.nextInt(buffs.length)]);
                c.sound(SoundEvents.GENERIC_DRINK, 1, 1.2f); c.particle(ParticleTypes.HAPPY_VILLAGER, c.eye(), 16, .5, .1);
            }
            if (t >= 26 && t < 56 && t % 5 == 0) {
                Vec3 f = c.fwd(), r = c.right(); int col = RAINBOW[(t / 5) % 7];
                for (int k = -3; k <= 3; k++) {
                    if (k == ((t / 5) % 7) - 3) continue;
                    c.proj().at(c.pos().add(f.scale(1.8)).add(r.scale(k * .8)).add(0, .7, 0)).vel(f.scale(.75))
                        .box(.22f, 1.3f, .22f).color(col).dmg(4).warn(4).life(34).spawn();
                }
            }
        }, (p, t, m) -> {
            float drink = t < 20 ? Math.min(1, t / 8f) : Math.max(0, 1 - (t - 20) / 6f);
            p.r("armL", -2.4f * drink, .6f * drink, 0).r("head", -.5f * drink, 0, 0).o("vial1", 0, 2 * drink, 0);
            if (t >= 22) p.r("armR", -1.4f, 0, -.3f);
        });
    }
    /** Sinks into an ink puddle, reappears behind the target and slams Broomie down. */
    static Attack inkPortal() {
        return atk("ink_portal_dash", 56, (c, t) -> {
            if (t == 0) c.proj().at(c.pos().add(0, .05, 0)).box(2, .1f, 2).color(0x111111).dmg(0).life(16).spawn();
            if (t == 12) {
                Vec3 behind = c.target.position().subtract(c.aim(c.pos()).multiply(1, 0, 1).scale(2.2));
                c.particle(ParticleTypes.SQUID_INK, c.pos().add(0, .5, 0), 30, .6, .05);
                c.proj().at(behind.add(0, .05, 0)).box(2, .1f, 2).color(0x111111).dmg(0).life(16).spawn();
                c.tp(behind); c.face(c.tpos()); c.sound(SoundEvents.ENDERMAN_TELEPORT, 1, 1.3f);
            }
            if (t == 24) {
                Vec3 hit = c.pos().add(c.fwd().scale(1.8));
                c.aoe(hit, 2.4, 9, 1.2); c.particle(ParticleTypes.SQUID_INK, hit, 40, 1, .1); c.sound(SoundEvents.SLIME_BLOCK_FALL, 1.2f, .6f);
                ring(c, hit.add(0, .3, 0), 10, .45, 0, .4f, 0x111111, 3, 0, 18);
            }
        }, (p, t, m) -> {
            float sink = t < 12 ? t / 12f : Math.max(0, 1 - (t - 12) / 4f);
            float up = t > 14 && t < 22 ? (t - 14) / 8f : 0, down = t >= 22 ? Math.max(0, 1 - (t - 24) / 14f) : 0;
            p.o("body", 0, -10 * sink, 0).r("armR", -3f * up + .6f * down, 0, -.2f).r("body", .3f * down, 0, 0);
        });
    }
    /** Phase 2: a gigantic brush stroke paints a lethal ribbon across the ground toward the target. */
    static Attack inkGiantStroke() {
        return atk("ink_giant_stroke", 80, (c, t) -> {
            if (t >= 16 && t < 46 && t % 2 == 0) {
                Vec3 d = c.aim(c.pos()).multiply(1, 0, 1).normalize(), side = new Vec3(-d.z, 0, d.x);
                double k = (t - 16) / 2.0;
                Vec3 at = c.pos().add(d.scale(1.5 + k * .9)).add(side.scale(Math.sin(k * .5) * 1.5)).add(0, .4, 0);
                c.proj().at(at).box(1.6f, .9f, 1.6f).color(RAINBOW[(int) k % 7]).dmg(6).warn(6).life(30).pierce().spawn();
                c.particle(ParticleTypes.SQUID_INK, at, 4, .4, .02);
            }
        }, (p, t, m) -> {
            float up = Math.min(1, t / 14f), sw = t < 16 ? 0 : Math.min(1, (t - 16) / 30f);
            p.r("armR", -2.8f * up + 3.2f * sw, 0, -.3f).r("armL", -2.8f * up + 3f * sw, 0, .3f).o("body", 0, 0, -2 * sw);
        }).when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ NIGHTMARE (king of negativity)
    static CharacterDef nightmare() {
        ModelDef md = skel(0x101018, 0x0b0b14, 0x0b0b14, 0x0b0b14, 0x0b0b14, CYAN, 0x30304a)
            .box("goopCrown", "head", -1, 4, 0, 10, 2, 8, 0x05050a)
            .box("goopEye", "head", -2, 0, -3.8f, 3, 3.5f, .6f, 0x05050a)
            .box("cape", "body", 0, -1, 2.8f, 9, 10, .6f, 0x1a1030)
            .glow("pupil", "eyeR", 0, 0, -.2f, .6f, .6f, .2f, 0xc8ffff);
        String[] tn = {"tent1", "tent2", "tent3", "tent4"};
        float[][] tp = {{-3, 2, 2.6f}, {3, 2, 2.6f}, {-2, -1, 2.6f}, {2, -1, 2.6f}};
        for (int i = 0; i < 4; i++) {
            md.limb(tn[i], "body", tp[i][0], tp[i][1], tp[i][2], 2, 10, 2, 0x07070d)
              .glow(tn[i] + "tip", tn[i], 0, -10, 0, 1.4f, 1.4f, 1.4f, 0x2050ff);
        }
        return CharacterDef.of("nightmare_sans", "Nightmare", Faction.NIGHTMARE_AU).hp(300).boss().keep(7).rest(12).speed(.3f)
            .egg(0x0b0b14, CYAN).size(.75f, 1.2f).scale(1.05f).model(md)
            .attacks(nmLash(), nmWhip(), nmOrbs(), nmEye(), nmApples(), nmCage()).build();
    }
    static void tentPose(Pose p, float t, float s) {
        p.r("tent1", -1.2f * s + wave(t, .5f) * .3f, 0, .8f * s).r("tent2", -1.2f * s - wave(t, .5f) * .3f, 0, -.8f * s)
         .r("tent3", -.6f * s, 0, 1.2f * s + wave(t, .7f) * .2f).r("tent4", -.6f * s, 0, -1.2f * s - wave(t, .7f) * .2f);
    }
    /** Tentacles erupt from the ground in a line that hunts the target. */
    static Attack nmLash() {
        return atk("nm_tentacle_eruption", 80, (c, t) -> {
            if (t >= 8 && t < 60 && t % 4 == 0) {
                Vec3 at = c.target.position().add(c.target.getDeltaMovement().scale(6)).add(c.rnd.nextGaussian() * .4, 0, c.rnd.nextGaussian() * .4);
                pillar(c, at, 3.2f, .8f, 0x07070d, 6, 10, 22);
                c.particle(ParticleTypes.SQUID_INK, at.add(0, .2, 0), 10, .4, .05);
                if (t % 8 == 0) c.sound(SoundEvents.SCULK_SHRIEKER_SHRIEK, .5f, .5f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 80, 12);
            tentPose(p, t, s); p.r("armR", -1.4f * s, 0, -.3f).o("body", 0, -.5f * s, 0);
        });
    }
    /** Tentacle whips sweep wide horizontal arcs in front of him. */
    static Attack nmWhip() {
        return atk("nm_tentacle_whip", 70, (c, t) -> {
            if (t >= 10 && t < 50 && t % 4 == 0) {
                int i = (t - 10) / 4; double base = Math.atan2(c.fwd().z, c.fwd().x);
                double a = base + (i % 2 == 0 ? -1 : 1) * (1.2 - (i / 2) * .25);
                Vec3 o = c.pos().add(0, .6 + (i % 3) * .5, 0);
                seg(c, o, around(o, 6.5, a, 0), .5f, 0x07070d, 7, 6, 10);
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .8f, .5f);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 70, 12), sw = wave(t, .785f);
            tentPose(p, t, s);
            p.r("tent1", 0, sw * 1.2f, 0).r("tent2", 0, -sw * 1.2f, 0).r("body", 0, sw * .3f * s, 0);
        });
    }
    /** Orbs of negativity orbit him, then home in one by one; nearby foes are steeped in darkness. */
    static Attack nmOrbs() {
        return atk("nm_negativity_orbs", 90, (c, t) -> {
            if (t == 0) { eff(c, MobEffects.DARKNESS, 100, 0); c.sound(SoundEvents.WARDEN_HEARTBEAT, 1.5f, .7f); }
            if (t < 30 && t % 3 == 0) c.particle(ParticleTypes.SCULK_SOUL, around(c.pos(), 1.5, t * .5, 1.4), 2, .1, .02);
            if (t >= 30 && t < 78 && t % 6 == 0) {
                Vec3 o = around(c.pos(), 1.6, t * .4, 1.6);
                shot(c, o, c.aim(o).scale(.45), .55f, 0x2a0a40, 6, 6, 50);
                c.particle(ParticleTypes.SCULK_SOUL, o, 4, .1, .02);
            }
        }, (p, t, m) -> {
            float s = env(t, 12, 90, 12);
            tentPose(p, t, s); p.r("armL", -2f * s, 0, .8f * s).r("armR", -2f * s, 0, -.8f * s).o("body", 0, 1.5f * s, 0);
        });
    }
    /** His cyan eye fires three tracking beams. */
    static Attack nmEye() {
        return atk("nm_eye_beam", 84, (c, t) -> {
            if (t % 26 == 0 && t < 78) {
                Vec3 o = c.eye().add(c.fwd().scale(.5));
                Vec3 d = c.tpos().subtract(o).normalize();
                c.proj().at(o.add(d.scale(14))).face(d).box(.7f, .7f, 28).color(CYAN).dmg(9).warn(14).life(24).pierce().spawn();
                c.particle(ParticleTypes.GLOW, o, 10, .2, .05); c.sound(SoundEvents.BEACON_POWER_SELECT, 1, 1.6f);
            }
        }, (p, t, m) -> {
            float k = (t % 26) / 26f, s = k < .55f ? k / .55f : 1;
            tentPose(p, t, .6f); p.r("head", -.25f * s, 0, 0).o("eyeR", 0, 0, -.6f * s).r("armR", -1.3f * s, 0, -.2f);
        });
    }
    /** Phase 2: corrupted black apples fall, each bursting into a ring of negativity. */
    static Attack nmApples() {
        return atk("nm_corrupt_apples", 80, (c, t) -> {
            if (t >= 10 && t < 50 && t % 8 == 2) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 2.5, 8, c.rnd.nextGaussian() * 2.5);
                c.proj().at(at).vel(new Vec3(0, -.3, 0)).gravity(.02f).box(.7f, .7f, .7f).color(0x1a0010).dmg(6).warn(4).life(22).spawn();
            }
            if (t >= 30 && t < 70 && t % 8 == 6) {
                Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 2.5, .5, c.rnd.nextGaussian() * 2.5);
                ring(c, g, 8, .35, t, .35f, 0x2a0a40, 4, 6, 24);
                c.particle(ParticleTypes.SQUID_INK, g, 12, .4, .05);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 80, 12);
            tentPose(p, t, s); p.r("tent1", -1.8f * s, 0, 0).r("tent2", -1.8f * s, 0, 0).r("head", -.4f * s, 0, 0);
        }).when(m -> m.hpFrac() < .6f);
    }
    /** Phase 3: a cage of tentacles rings the target and crushes inward. */
    static Attack nmCage() {
        return atk("nm_tentacle_cage", 90, (c, t) -> {
            if (t == 6) {
                Vec3 tp = c.target.position();
                for (int i = 0; i < 12; i++) {
                    double a = i * Math.PI / 6; Vec3 at = around(tp, 5, a, 1.6);
                    c.proj().at(at).vel(tp.add(0, 1.6, 0).subtract(at).normalize().scale(.12)).box(.8f, 3.2f, .8f)
                        .color(0x07070d).dmg(9).warn(24).life(60).pierce().spawn();
                }
                eff(c, MobEffects.DARKNESS, 80, 0); c.sound(SoundEvents.WARDEN_EMERGE, 1, 1.2f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 90, 14), close = Math.min(1, Math.max(0, (t - 30) / 40f));
            tentPose(p, t, s);
            p.r("armL", -1.6f * s, 0, (1 - close) * 1f).r("armR", -1.6f * s, 0, -(1 - close) * 1f);
        }).when(m -> m.hpFrac() < .3f);
    }

    // ============================================================ DREAM (guardian of positivity)
    static CharacterDef dream() {
        return CharacterDef.of("dream_sans", "Dream", Faction.GUARDIAN_AU).att(Attitude.NEUTRAL).hp(220).boss().keep(9).rest(14).speed(.3f)
            .egg(0xffd34a, 0xffffff).size(.7f, 1.1f).scale(.95f)
            .model(skel(0xfff2b0, 0xffd34a, 0x8a6a2a, 0xfaf6e8, 0xffd400, 0xffd400, 0x333333)
                .box("crown", "head", 0, 4.2f, 0, 6, 1.6f, 6, 0xffcc33)
                .glow("crownGem", "crown", 0, .2f, -3.1f, 1, 1, .3f, 0xfff7a0)
                .box("cape", "body", 0, -1, 2.8f, 9, 10, .5f, 0xffd34a)
                .box("capeIn", "cape", 0, 0, -.3f, 8, 9, .1f, 0x3a6fe0)
                .box("bow", "armL", 0, -6, -1.5f, .6f, 9, .6f, 0xffcc33)
                .glow("bowString", "bow", 0, 0, .5f, .1f, 8.5f, .1f, 0xfff7a0))
            .attacks(drArrows(), drRain(), drAura(), drCharged(), drCrescent()).build();
    }
    static void bowPose(Pose p, float draw) {
        p.r("armL", -1.57f, .3f, 0).r("armR", -1.57f, -.6f + .4f * draw, 0).o("armR", 0, 0, 2.5f * draw).r("body", 0, -.4f, 0);
    }
    /** Golden arrows of positivity loosed in three spreading volleys. */
    static Attack drArrows() {
        return atk("dream_golden_arrows", 72, (c, t) -> {
            if (t % 22 == 14 && t < 72) {
                Vec3 o = c.eye(); Vec3 d = c.aim(o); double base = Math.atan2(d.z, d.x);
                int n = 3 + t / 22 * 2;
                for (int i = 0; i < n; i++) {
                    double a = base + (i - (n - 1) / 2.0) * .14;
                    dart(c, o, new Vec3(Math.cos(a), d.y, Math.sin(a)).scale(.9), .15f, 1.1f, 0xffcc33, 5, 0, 30);
                }
                c.particle(ParticleTypes.END_ROD, o, 8, .2, .05); c.sound(SoundEvents.ARROW_SHOOT, 1, 1.3f);
            }
        }, (p, t, m) -> bowPose(p, Math.min(1, (t % 22) / 14f)));
    }
    /** Shoots skyward; golden arrows rain onto marked spots around the target. */
    static Attack drRain() {
        return atk("dream_arrow_rain", 84, (c, t) -> {
            if (t == 12) { c.sound(SoundEvents.ARROW_SHOOT, 1, .8f); dart(c, c.eye(), new Vec3(0, 1.2, 0), .15f, 1.1f, 0xffcc33, 0, 0, 12); }
            if (t >= 24 && t < 70 && t % 2 == 0) {
                Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 3, 0, c.rnd.nextGaussian() * 3);
                c.proj().at(g.add(0, 9, 0)).vel(new Vec3(0, -.9, 0)).box(.15f, .15f, 1.2f).color(0xffcc33).dmg(4).warn(8).life(18).spawn();
                c.particle(ParticleTypes.WAX_ON, g.add(0, .1, 0), 3, .2, 0);
            }
        }, (p, t, m) -> {
            float d = Math.min(1, t / 12f);
            p.r("armL", -2.9f, 0, .1f).r("armR", -2.9f + .3f * d, 0, -.1f).r("head", -.5f, 0, 0);
        });
    }
    /** Positivity aura: heals himself, then expanding rings of golden stars. */
    static Attack drAura() {
        return atk("dream_positivity_aura", 70, (c, t) -> {
            if (t == 6) { c.me.heal(14); c.me.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0)); c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.4f); }
            if (t < 30 && t % 3 == 0) c.particle(ParticleTypes.HEART, c.pos().add(0, 1.5, 0), 2, .6, .02);
            if (t >= 20 && t < 60 && t % 10 == 0) ring(c, c.pos().add(0, .8, 0), 14, .35, t * .2, .4f, 0xfff07a, 4, 4, 30);
        }, (p, t, m) -> {
            float s = env(t, 10, 70, 14);
            p.r("armL", -.5f * s, 0, 1.4f * s).r("armR", -.5f * s, 0, -1.4f * s).r("head", -.4f * s, 0, 0).o("body", 0, 2 * s, 0);
        });
    }
    /** Fully drawn star-arrow: a piercing golden beam. */
    static Attack drCharged() {
        return atk("dream_charged_star_arrow", 60, (c, t) -> {
            if (t < 30 && t % 4 == 0) c.particle(ParticleTypes.END_ROD, c.eye().add(c.fwd().scale(.6)), 4, .1, .02);
            if (t == 30) {
                Vec3 o = c.eye(), d = c.aim(o);
                c.proj().at(o.add(d.scale(14))).face(d).box(.6f, .6f, 28).color(0xffe866).dmg(12).warn(4).life(14).pierce().spawn();
                c.sound(SoundEvents.CROSSBOW_SHOOT, 1.2f, .7f);
            }
        }, (p, t, m) -> { bowPose(p, Math.min(1, t / 28f)); if (t > 30) p.o("armR", 0, 0, -2); });
    }
    /** Phase 2: crescent waves of light sweep outward from a staff swing. */
    static Attack drCrescent() {
        return atk("dream_crescent_waves", 70, (c, t) -> {
            if (t >= 14 && t < 54 && t % 10 == 4) {
                Vec3 o = c.pos().add(0, .8, 0), d = c.aim(o), side = new Vec3(-d.z, 0, d.x);
                for (int k = -3; k <= 3; k++) {
                    Vec3 at = o.add(side.scale(k * .55)).subtract(d.scale(k * k * .12));
                    shot(c, at, d.scale(.6), .4f, 0xfff07a, 5, 2, 30);
                }
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1.5f);
            }
        }, (p, t, m) -> {
            float k = ((t + 6) % 10) / 10f;
            p.r("armR", -2.5f + 2f * k, 0, -.3f).r("body", 0, -.6f + 1.2f * k, 0);
        }).when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ CROSS (XTale, Nightmare's gang)
    static CharacterDef cross() {
        return CharacterDef.of("cross_sans", "Cross", Faction.NIGHTMARE_AU).hp(230).boss().keep(4).rest(12).speed(.36f)
            .egg(0xf0f0f0, 0x6a0dad).size(.7f, 1.1f).scale(.95f)
            .model(skel(0xf0f0f0, 0x1a1a1a, 0x1a1a1a, 0xf5f5f5, 0xffffff, 0xff2a2a, 0x222222)
                .box("xScar", "head", 2, 1, -3.65f, 3, .5f, .3f, 0x660000)
                .box("xScar2", "head", 2, 1, -3.7f, .5f, 3, .3f, 0x660000)
                .box("xBelt", "body", 0, -2.5f, -2.6f, 8.4f, 1, .4f, 0x1a1a1a)
                .glow("xMark", "body", 0, 1.2f, -2.7f, 2, 2, .2f, 0x9b30ff)
                .box("knife", "armR", 0, -7, -3.5f, .4f, 1.6f, 9, 0xe8e8e8)
                .box("knifeEdge", "knife", 0, -.8f, 0, .3f, .3f, 8.6f, 0xff2a2a)
                .box("knifeHilt", "knife", 0, 0, 4.5f, .8f, .8f, 2, 0x1a1a1a))
            .attacks(crSlash(), crXBlasters(), crKnifeThrow(), crBoneX(), crCharaMode()).build();
    }
    static void xSlash(AttackCtx c, Vec3 at, Vec3 d, float dmg, int warn) {
        Vec3 side = new Vec3(-d.z, 0, d.x);
        seg(c, at.add(side.scale(-2)).add(0, 2, 0), at.add(side.scale(2)).add(0, -1, 0), .25f, 0xff2a2a, dmg, warn, warn + 6);
        seg(c, at.add(side.scale(2)).add(0, 2, 0), at.add(side.scale(-2)).add(0, -1, 0), .25f, 0xff2a2a, dmg, warn, warn + 6);
    }
    /** Lunges in and carves a red X with his giant knife. */
    static Attack crSlash() {
        return atk("cross_x_slash", 50, (c, t) -> {
            if (t == 10) c.dash(c.aim(c.pos()).multiply(1.4, 0, 1.4).add(0, .2, 0));
            if (t == 16) {
                Vec3 d = c.fwd(); xSlash(c, c.pos().add(d.scale(2)).add(0, .8, 0), d, 9, 0);
                c.aoe(c.pos().add(d.scale(2)), 2.2, 4, .6); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, .6f);
                c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(d.scale(2)).add(0, 1, 0), 3, .4, 0);
            }
        }, (p, t, m) -> {
            float wind = Math.min(1, t / 10f), cut = t < 14 ? 0 : Math.min(1, (t - 14) / 4f), rec = env(t, 1, 50, 20);
            p.r("armR", (-2.8f * wind + 3.4f * cut) * rec, 0, (-.6f + 1.2f * cut) * rec).r("body", 0, (-.6f * wind + 1f * cut) * rec, 0).r("legL", -.6f * wind * rec, 0, 0);
        });
    }
    /** Blasters pair up at the four ends of an X and fire crosswise through the target. */
    static Attack crXBlasters() {
        return atk("cross_x_blasters", 80, (c, t) -> {
            if (t == 4 || t == 34) {
                double off = t == 4 ? Math.PI / 4 : 0;
                for (int i = 0; i < 4; i++) {
                    Vec3 at = around(c.tpos(), 6, off + i * Math.PI / 2, 1.5);
                    blaster(c, at, c.tpos().subtract(at), 22, 12, 8, .85f, 0xf2f2f2, 0x9b30ff, 0xd9b3ff);
                }
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 12);
            p.r("armL", -2.6f * s, 0, .8f * s).r("armR", -2.6f * s, 0, -.8f * s).r("head", -.2f * s, 0, 0);
        });
    }
    /** Hurls spinning knives in alternating arcs. */
    static Attack crKnifeThrow() {
        return atk("cross_knife_throw", 64, (c, t) -> {
            if (t >= 8 && t < 48 && t % 8 == 0) {
                Vec3 o = hand(c, 1).add(0, .4, 0);
                Vec3 d = c.aim(o); double base = Math.atan2(d.z, d.x);
                for (int i = -1; i <= 1; i++) {
                    double a = base + i * .2;
                    dart(c, o, new Vec3(Math.cos(a), d.y, Math.sin(a)).scale(.8), .2f, 1.4f, 0xe8e8e8, 5, 0, 30);
                }
                c.sound(SoundEvents.TRIDENT_THROW, .8f, 1.2f);
            }
        }, (p, t, m) -> {
            float k = (t % 8) / 8f;
            p.r("armR", -2.6f + 2.4f * k, 0, -.2f).r("body", 0, .3f - .6f * k, 0);
        });
    }
    /** X-shaped bone crosses fly in from both flanks. */
    static Attack crBoneX() {
        return atk("cross_bone_crosses", 70, (c, t) -> {
            if (t >= 6 && t < 54 && t % 8 == 0) {
                int s = (t / 8) % 2 == 0 ? 1 : -1;
                Vec3 r = c.right(), at = c.tpos().add(r.scale(s * 7)), v = r.scale(-s * .5);
                c.proj().at(at).vel(v).box(.22f, 2.2f, .22f).color(WHITE).dmg(5).warn(8).life(30).spawn();
                c.proj().at(at).vel(v).box(.22f, .22f, 2.2f).color(WHITE).dmg(5).warn(8).life(30).spawn();
                c.sound(SoundEvents.SKELETON_SHOOT, .8f, 1.3f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 70, 10);
            p.r("armL", -1.2f * s, 0, 1.2f * s).r("armR", -1.2f * s, 0, -1.2f * s).o("body", 0, wave(t, .6f) * .3f, 0);
        });
    }
    /** Phase 2: X-Chara takes over — red eye blazes, three teleport-slashes in quick succession. */
    static Attack crCharaMode() {
        return atk("cross_chara_mode", 84, (c, t) -> {
            if (t % 26 == 6 && t < 80) {
                Vec3 at = around(c.target.position(), 2.5, c.rnd.nextDouble() * Math.PI * 2, 0);
                c.particle(ParticleTypes.CRIMSON_SPORE, c.pos().add(0, 1, 0), 20, .4, .05);
                c.tp(at); c.face(c.tpos()); c.sound(SoundEvents.ENDERMAN_TELEPORT, 1, .7f);
            }
            if (t % 26 == 16 && t < 80) {
                Vec3 d = c.aim(c.pos()); xSlash(c, c.pos().add(d.scale(1.5)).add(0, .8, 0), d, 10, 2);
                c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1.2f, .6f);
            }
        }, (p, t, m) -> {
            float k = (t % 26) / 26f, cut = k > .5f && k < .7f ? (k - .5f) / .2f : k >= .7f ? 1 - (k - .7f) / .3f : 0;
            p.r("armR", -2.6f + 3.2f * cut, 0, -.5f).o("eyeR", 0, 0, -.5f).r("body", .2f, 0, 0);
        }).when(m -> m.hpFrac() < .4f);
    }

    // ============================================================ KILLER (Nightmare's gang)
    static CharacterDef killer() {
        return CharacterDef.of("killer_sans", "Killer", Faction.NIGHTMARE_AU).hp(210).boss().keep(5).rest(12).speed(.36f)
            .egg(0x1a1a1a, 0xff2a2a).size(.7f, 1.1f).scale(.95f)
            .model(skel(0x1a1a1a, 0xffffff, 0x101010, 0xf5f5f5, 0x000000, 0x000000, 0x222222)
                .glow("target", "body", 0, 1, -2.7f, 2.4f, 2.4f, .2f, 0xff2a2a)
                .glow("targetC", "target", 0, 0, -.1f, .8f, .8f, .2f, 0xffffff)
                .box("tearL", "head", -2, -2, -3.6f, .9f, 3.5f, .3f, 0x050505)
                .box("tearR", "head", 2, -2, -3.6f, .9f, 3.5f, .3f, 0x050505)
                .box("knife", "armR", 0, -7, -2, .3f, .9f, 4, 0xd8d8d8))
            .attacks(kiKnives(), kiTarget(), kiTears(), kiLunge(), kiStorm()).build();
    }
    /** Rapid-fire knife flurry. */
    static Attack kiKnives() {
        return atk("killer_knife_flurry", 60, (c, t) -> {
            if (t >= 8 && t < 44 && t % 3 == 0) {
                Vec3 o = hand(c, t % 6 == 0 ? 1 : -1).add(0, .3, 0);
                dart(c, o, c.aim(o).add(c.rnd.nextGaussian() * .05, 0, c.rnd.nextGaussian() * .05).scale(.95), .12f, .8f, 0xd8d8d8, 4, 0, 26);
                c.sound(SoundEvents.TRIDENT_THROW, .6f, 1.6f);
            }
        }, (p, t, m) -> {
            float k = (t % 6) / 6f;
            p.r("armR", k < .5f ? -2.2f : -.4f, 0, -.2f).r("armL", k < .5f ? -.4f : -2.2f, 0, .2f).r("head", .2f, 0, 0);
        });
    }
    /** Locks a red target reticle on the foe, then blasters converge on the bullseye. */
    static Attack kiTarget() {
        return atk("killer_target_lock", 80, (c, t) -> {
            if (t < 24 && t % 4 == 0) {
                Vec3 tp = c.target.position().add(0, .1, 0);
                for (int i = 0; i < 4; i++) seg(c, around(tp, 2, i * Math.PI / 2, 0), around(tp, 2, (i + 1) * Math.PI / 2, 0), .12f, 0xff2a2a, 0, 0, 6);
                c.sound(SoundEvents.NOTE_BLOCK_HAT.value(), .8f, 1.8f);
            }
            if (t == 26) for (int i = 0; i < 5; i++) {
                Vec3 at = around(c.tpos(), 6, i * Math.PI * 2 / 5, 2);
                blaster(c, at, c.tpos().subtract(at), 18, 12, 9, .9f, 0xf2f2f2, 0xff2a2a, 0xff8080);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 14);
            p.r("armR", -1.57f * s, 0, 0).r("head", 0, wave(t, .3f) * .15f, 0).o("target", 0, 0, -.3f * s);
        });
    }
    /** Black tears drip upward into the air and fall as a black rain, bones sweep low. */
    static Attack kiTears() {
        return atk("killer_black_tears", 76, (c, t) -> {
            if (t < 30 && t % 3 == 0) c.particle(ParticleTypes.SQUID_INK, c.eye(), 4, .2, .05);
            if (t >= 20 && t < 60 && t % 2 == 0)
                c.proj().at(c.target.position().add(c.rnd.nextGaussian() * 3, 8, c.rnd.nextGaussian() * 3)).vel(new Vec3(0, -.7, 0))
                    .box(.25f, .5f, .25f).color(0x050505).dmg(3).warn(4).life(18).spawn();
            if (t == 40) for (int k = -4; k <= 4; k++)
                c.proj().at(c.pos().add(c.right().scale(k * .7)).add(0, .3, 0)).vel(c.fwd().scale(.6)).box(.22f, .7f, .22f).color(WHITE).dmg(5).warn(6).life(30).spawn();
        }, (p, t, m) -> {
            float s = env(t, 10, 76, 14);
            p.r("head", -.5f * s, 0, 0).r("armL", -.3f, 0, .4f * s).r("armR", -.3f, 0, -.4f * s).o("tearL", 0, -wave(t, .3f), 0);
        });
    }
    /** Grinning dash-stab. */
    static Attack kiLunge() {
        return atk("killer_lunge", 40, (c, t) -> {
            if (t == 10) c.dash(c.aim(c.pos()).multiply(1.6, 0, 1.6).add(0, .15, 0));
            if (t == 14) { c.aoe(c.pos().add(c.fwd()), 1.8, 10, .9); c.sound(SoundEvents.PLAYER_ATTACK_CRIT, 1, .8f); c.particle(ParticleTypes.CRIT, c.pos().add(c.fwd()).add(0, 1, 0), 14, .4, .2); }
        }, (p, t, m) -> {
            float w = Math.min(1, t / 10f), st = t >= 10 ? Math.max(0, 1 - (t - 12) / 20f) : 0;
            p.r("armR", -1.57f * st + .8f * w * (1 - st), 0, 0).r("body", .4f * st, -.4f * w, 0).o("armR", 0, 0, -3 * st);
        });
    }
    /** Phase 2: blaster storm in a rotating spiral. */
    static Attack kiStorm() {
        return atk("killer_blaster_storm", 96, (c, t) -> {
            if (t < 56 && t % 4 == 0) {
                Vec3 at = around(c.tpos(), 6.5, t * .35, 1 + (t % 8) * .3);
                blaster(c, at, c.tpos().subtract(at), 16, 10, 8, .7f, 0xf2f2f2, 0xff2a2a, 0xffb0b0);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 96, 16);
            p.r("armL", -2.8f * s, 0, .3f).r("armR", -2.8f * s, 0, -.3f).r("body", 0, t * .1f * s, 0);
        }).when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ EPIC (Epictale)
    static CharacterDef epic() {
        return CharacterDef.of("epic_sans", "Epic!Sans", Faction.NEUTRAL).att(Attitude.NEUTRAL).hp(190).boss().keep(5).rest(14).speed(.33f)
            .egg(0x5a2a8a, 0xffffff).size(.7f, 1.1f).scale(.95f)
            .model(skel(0x5a2a8a, 0x3a1a5a, 0x1a1a1a, 0xf5f5f5, 0xb060ff, 0xb060ff, 0x222222)
                .box("hoodUp", "head", 0, 3, 1, 10, 3, 8, 0x3a1a5a)
                .box("scythe", "armR", 0, -6.5f, 0, .7f, 14, .7f, 0x3a2a2a)
                .box("blade", "scythe", 0, 7, -3, .4f, 1.8f, 6, 0xc0c0d0))
            .attacks(epScythe(), epBlasters(), epBones(), epPurpleRings()).build();
    }
    /** Spinning scythe whirl. */
    static Attack epScythe() {
        return atk("epic_scythe_spin", 50, (c, t) -> {
            if (t >= 12 && t < 32 && t % 4 == 0) {
                c.aoe(c.pos(), 2.8, 5, .8); c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(0, 1, 0), 4, 1.2, 0);
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .8f + t * .02f);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 50, 14);
            p.r("armR", -1.57f * s, 0, -1.2f * s).r("body", 0, t > 10 && t < 34 ? (t - 10) * .8f : 0, 0);
        });
    }
    /** Purple-eyed blasters in a sliding line. */
    static Attack epBlasters() {
        return atk("epic_purple_blasters", 80, (c, t) -> {
            if (t < 30 && t % 6 == 0) {
                Vec3 at = c.tpos().add(c.right().scale((t / 6 - 2) * 1.8)).add(c.fwd().scale(-6)).add(0, 2.5, 0);
                blaster(c, at, c.tpos().subtract(at), 24 - t / 2, 12, 7, .8f, 0xf2f2f2, 0xb060ff, 0xd9b3ff);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 12); p.r("armL", -2.4f * s, 0, .2f).r("head", -.2f * s, 0, 0);
        });
    }
    /** "Totally epic" bone waves with alternating tall/short gaps. */
    static Attack epBones() {
        return atk("epic_bone_waves", 70, (c, t) -> {
            if (t >= 6 && t < 54 && t % 8 == 0) {
                boolean tall = (t / 8) % 2 == 0; Vec3 f = c.fwd(), r = c.right();
                for (int k = -4; k <= 4; k++)
                    c.proj().at(c.pos().add(f.scale(2)).add(r.scale(k * .7)).add(0, tall ? 1 : .3, 0)).vel(f.scale(.6))
                        .box(.22f, tall ? 2 : .6f, .22f).color(tall ? 0xb060ff : WHITE).dmg(4).warn(6).life(34).spawn();
            }
        }, (p, t, m) -> p.r("armL", -1f, 0, .3f).r("armR", -1f, 0, -.3f).o("body", 0, wave(t, .8f) * .4f, 0));
    }
    /** Expanding purple rings. */
    static Attack epPurpleRings() {
        return atk("epic_purple_rings", 60, (c, t) -> {
            if (t >= 10 && t < 50 && t % 10 == 0) ring(c, c.pos().add(0, .8, 0), 16, .4, t * .1, .35f, 0xb060ff, 4, 4, 30);
        }, (p, t, m) -> {
            float s = env(t, 8, 60, 10); p.r("armL", 0, 0, 1.4f * s).r("armR", 0, 0, -1.4f * s).o("body", 0, 1.5f * s, 0);
        }).when(m -> m.hpFrac() < .6f);
    }

    // ============================================================ FRESH (the 90s parasite)
    static CharacterDef fresh() {
        return CharacterDef.of("fresh_sans", "Fresh!Sans", Faction.NEUTRAL).hp(200).boss().keep(6).rest(12).speed(.38f)
            .egg(0xff5cd6, 0x3fffd0).size(.7f, 1.1f).scale(.95f)
            .model(skel(0x3fffd0, 0xff5cd6, 0xffe23b, 0xf5f5f5, 0x000000, 0x000000, 0x222222)
                .box("cap", "head", 0, 4, -1, 9.5f, 1.6f, 9, 0x8a5cff)
                .box("capBill", "cap", 0, 0, -5, 7, .5f, 4, 0xff3b3b)
                .glow("shades", "head", 0, .6f, -3.8f, 8, 2, .3f, 0x111111)
                .glow("shadeL", "shades", -2, 0, -.2f, 2.6f, 1.3f, .1f, 0xffe23b)
                .glow("shadeR", "shades", 2, 0, -.2f, 2.6f, 1.3f, .1f, 0xff5cd6)
                .glow("parasite", "head", 0, 0, 0, 3, 3, 3, 0x7a00ff)
                .glow("yoyo", "armR", 0, -7.5f, 0, 1.6f, 1.6f, .8f, 0x4cff6a))
            .attacks(frYoyo(), frShadeBeam(), frNeonBones(), frBoombox(), frParasite()).build();
    }
    /** Yo-yo slung out and yanked back (two passes along the same line). */
    static Attack frYoyo() {
        return atk("fresh_yoyo", 56, (c, t) -> {
            if (t == 8 || t == 30) {
                Vec3 o = hand(c, 1), d = c.aim(o);
                c.proj().at(o).vel(d.scale(.8)).box(.6f, .6f, .3f).color(0x4cff6a).dmg(5).life(12).spawn();
                c.proj().at(o.add(d.scale(9.6))).vel(d.scale(-.8)).box(.6f, .6f, .3f).color(0x4cff6a).dmg(5).warn(12).life(24).spawn();
                seg(c, o, o.add(d.scale(9.6)), .05f, 0xffffff, 0, 0, 22);
                c.sound(SoundEvents.FISHING_BOBBER_THROW, 1, 1.5f);
            }
        }, (p, t, m) -> {
            float k = (t % 22) / 22f;
            p.r("armR", -1.57f + (k < .3f ? -1 : 0), 0, -.2f).o("yoyo", 0, 0, k < .5f ? -8 * k * 2 : -8 * (1 - k) * 2);
        });
    }
    /** "YOLO" shades fire a sweeping rainbow beam. */
    static Attack frShadeBeam() {
        return atk("fresh_rad_shades_beam", 70, (c, t) -> {
            if (t >= 18 && t < 50 && t % 4 == 2) {
                Vec3 o = c.eye(), d = c.aim(o);
                double a = Math.atan2(d.z, d.x) + (t - 34) * .03;
                Vec3 dir = new Vec3(Math.cos(a), d.y, Math.sin(a));
                c.proj().at(o.add(dir.scale(10))).face(dir).box(.5f, .5f, 20).color(RAINBOW[(t / 4) % 7]).dmg(6).warn(2).life(6).pierce().spawn();
            }
            if (t == 16) c.sound(SoundEvents.BEACON_ACTIVATE, 1, 2f);
        }, (p, t, m) -> {
            float s = env(t, 14, 70, 12);
            p.r("armR", -2.3f * s, 0, -.5f).o("armR", 0, 2 * s, -1 * s).r("head", 0, (t - 34) * .02f * s, 0);
        });
    }
    /** Neon multicolor bones bounce in from every side. */
    static Attack frNeonBones() {
        return atk("fresh_neon_bones", 64, (c, t) -> {
            if (t >= 6 && t < 50 && t % 4 == 0) {
                double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = around(c.tpos(), 7, a, 0);
                c.proj().at(at).vel(c.tpos().subtract(at).normalize().scale(.5)).box(.22f, 1.3f, .22f).color(RAINBOW[c.rnd.nextInt(7)]).dmg(4).warn(6).life(30).spawn();
            }
        }, (p, t, m) -> p.r("armL", -1.6f, 0, wave(t, .9f) * .6f).r("armR", -1.6f, 0, -wave(t, .9f) * .6f).r("body", 0, wave(t, .45f) * .4f, 0));
    }
    /** Boombox bass drop: pulsing shockwave rings. */
    static Attack frBoombox() {
        return atk("fresh_bass_drop", 60, (c, t) -> {
            if (t >= 10 && t % 12 == 10 && t < 58) {
                c.aoe(c.pos(), 3.5, 4, 1.4); ring(c, c.pos().add(0, .4, 0), 20, .5, 0, .3f, RAINBOW[(t / 12) % 7], 3, 0, 20);
                c.sound(SoundEvents.WARDEN_SONIC_BOOM, .6f, 1.6f); c.particle(ParticleTypes.NOTE, c.eye(), 6, .6, 1);
            }
        }, (p, t, m) -> {
            float b = (t % 12) < 3 ? 1 : 0;
            p.o("body", 0, -b, 0).r("head", .3f * b, 0, 0).r("armL", -.8f, 0, .5f).r("armR", -.8f, 0, -.5f);
        });
    }
    /** Phase 2: the parasite lunges out and latches on, draining and disorienting. */
    static Attack frParasite() {
        return atk("fresh_parasite_grab", 70, (c, t) -> {
            if (t >= 14 && t < 40 && t % 4 == 0) {
                seg(c, c.eye(), c.tpos(), .3f, 0x7a00ff, 2, 0, 5); pull(c, .3);
                if (t == 20) { eff(c, MobEffects.CONFUSION, 120, 0); eff(c, MobEffects.WITHER, 60, 0); c.sound(SoundEvents.SLIME_ATTACK, 1, .5f); }
            }
            if (t == 44) c.me.heal(10);
        }, (p, t, m) -> {
            float s = env(t, 12, 70, 16);
            p.o("parasite", 0, 0, -4 * s).r("head", -.4f * s, 0, 0).r("armL", -1.2f * s, 0, .6f).r("armR", -1.2f * s, 0, -.6f);
        }).when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ GENO (Aftertale)
    static CharacterDef geno() {
        return CharacterDef.of("geno_sans", "Geno!Sans", Faction.NEUTRAL).att(Attitude.NEUTRAL).hp(200).boss().keep(7).rest(14).speed(.28f)
            .egg(0xf5f5f5, 0xb00000).size(.7f, 1.1f).scale(.95f)
            .model(skel(0x2a4fb0, 0xe0e0e0, 0x111111, 0xf5f5f5, 0x000000, 0xff1a1a, 0x222222)
                .box("scarf", "body", 0, 3.6f, -1, 9.5f, 1.4f, 7, 0xe0e0e0)
                .box("scarfBlood", "scarf", 2, 0, -3.6f, 3, 1.5f, .2f, 0x9a0000)
                .box("scarfTail", "scarf", 2.5f, -5, 3.4f, 2.5f, 9, .6f, 0xe0e0e0)
                .box("wound", "body", -1.5f, 0, -2.6f, 2.5f, 3, .3f, 0x9a0000)
                .ghost("glitchG", "body", 0, 0, 0, 10, 2, 7, 0xffffff, .3f))
            .attacks(gnBlasters(), gnSaveVoid(), gnGlitchBones(), gnScarf()).build();
    }
    /** Bleeding red blasters. */
    static Attack gnBlasters() {
        return atk("geno_red_blasters", 84, (c, t) -> {
            if (t < 36 && t % 9 == 0) {
                Vec3 at = around(c.tpos(), 6, c.rnd.nextDouble() * 6.28, 2);
                blaster(c, at, c.tpos().subtract(at), 20, 12, 9, .9f, 0xf2f2f2, 0xff1a1a, 0xff4040);
                c.particle(ParticleTypes.DAMAGE_INDICATOR, at, 6, .3, .05);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 84, 12); p.r("armR", -1.8f * s, 0, -.2f).r("armL", .3f, 0, .2f).r("body", .15f, 0, 0);
        });
    }
    /** The SAVE screen: a white void walls the target in while bones cross it. */
    static Attack gnSaveVoid() {
        return atk("geno_save_void", 90, (c, t) -> {
            Vec3 tp = c.target.position();
            if (t == 4) {
                for (int i = 0; i < 12; i++) pillar(c, around(tp, 4.5, i * Math.PI / 6, 0), 4, .9f, 0xffffff, 6, 10, 70);
                eff(c, MobEffects.MOVEMENT_SLOWDOWN, 60, 1); c.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1.4f, .5f);
            }
            if (t >= 20 && t < 72 && t % 6 == 0) {
                double a = c.rnd.nextDouble() * Math.PI; Vec3 at = around(tp, 4, a, .5);
                c.proj().at(at).vel(tp.add(0, .5, 0).subtract(at).normalize().scale(.4)).box(.22f, 1.2f, .22f).color(0xff1a1a).dmg(5).warn(6).life(24).spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 4, 90, 16);
            p.r("armL", -2.2f * s, 0, .6f).r("armR", -2.2f * s, 0, -.6f).o("glitchG", wave(t, 3) * 2, 0, 0);
        });
    }
    /** Glitching bones that flicker between positions mid-flight. */
    static Attack gnGlitchBones() {
        return atk("geno_glitch_bones", 64, (c, t) -> {
            if (t >= 6 && t < 50 && t % 3 == 0) {
                Vec3 at = c.pos().add(c.right().scale(c.rnd.nextGaussian() * 2)).add(0, .3 + c.rnd.nextFloat() * 1.4, 0);
                c.proj().at(at).vel(c.aim(at).scale(.55)).box(.2f, .9f, .2f).color(c.rnd.nextBoolean() ? WHITE : 0xff1a1a).dmg(4).warn(4).life(30).spawn();
                if (t % 9 == 0) c.particle(ParticleTypes.ELECTRIC_SPARK, at, 4, .3, .1);
            }
        }, (p, t, m) -> p.r("armR", -1.4f, 0, -.3f).o("body", (t % 6 < 2 ? .6f : 0), 0, 0).r("head", 0, 0, (t % 8 < 2 ? .2f : 0)));
    }
    /** Lashes the blood-stained scarf like a whip. */
    static Attack gnScarf() {
        return atk("geno_scarf_lash", 50, (c, t) -> {
            if (t == 14 || t == 28) {
                Vec3 o = c.pos().add(0, 1.2, 0), d = c.aim(o);
                seg(c, o, o.add(d.scale(5)), .4f, 0xe0e0e0, 7, 4, 8); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1.1f);
            }
        }, (p, t, m) -> {
            float k = (t % 14) / 14f;
            p.r("scarfTail", -2.5f * (1 - k), 0, 0).r("body", 0, .5f - k, 0);
        }).when(m -> m.hpFrac() < .6f);
    }

    // ============================================================ REAPER (Aftertale's Death)
    static CharacterDef reaper() {
        return CharacterDef.of("reaper_sans", "Reaper!Sans", Faction.NEUTRAL).att(Attitude.NEUTRAL).hp(230).boss().keep(4).rest(14).speed(.3f)
            .egg(0x111111, 0xf5f5f5).size(.7f, 1.1f).scale(1f)
            .model(skel(0x111111, 0x111111, 0x111111, 0xf5f5f5, 0x000000, 0x000000, 0x222222)
                .box("robe", "body", 0, -4, 0, 9, 7, 6, 0x0a0a0a)
                .box("cowl", "head", 0, 1.5f, .8f, 10, 9, 8, 0x0a0a0a)
                .glow("halo", "head", 0, 5.5f, 0, 7, .4f, 7, 0xfff7a0)
                .box("scythe", "armR", 0, -6.5f, 0, .7f, 16, .7f, 0x2a1a10)
                .box("blade", "scythe", 0, 8, -3.5f, .4f, 2, 7, 0xd0d0d0))
            .attacks(rpSweep(), rpReap(), rpBlasters(), rpGraves()).build();
    }
    /** Wide scythe sweep; whatever it touches withers. */
    static Attack rpSweep() {
        return atk("reaper_scythe_sweep", 44, (c, t) -> {
            if (t == 16) {
                Vec3 at = c.pos().add(c.fwd().scale(2));
                c.aoe(at, 3, 9, .8); c.particle(ParticleTypes.SWEEP_ATTACK, at.add(0, 1, 0), 5, 1.2, 0); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, .5f);
                if (c.target.distanceTo(c.me) < 4) eff(c, MobEffects.WITHER, 80, 1);
            }
        }, (p, t, m) -> {
            float w = Math.min(1, t / 14f), sw = t < 14 ? 0 : Math.min(1, (t - 14) / 4f), rec = env(t, 1, 44, 18);
            p.r("armR", -1.4f * rec, (1.3f * w - 2.6f * sw) * rec, 0).r("body", 0, (.6f * w - 1.2f * sw) * rec, 0);
        });
    }
    /** Death's touch: a chain of souls drags the target in. */
    static Attack rpReap() {
        return atk("reaper_soul_reap", 64, (c, t) -> {
            if (t >= 10 && t < 40 && t % 3 == 0) {
                pull(c, .4); c.particle(ParticleTypes.SOUL, c.tpos(), 4, .3, .05);
                seg(c, hand(c, -1), c.tpos(), .1f, 0xbfe9ff, 1, 0, 3);
            }
            if (t == 42) { c.aoe(c.pos().add(c.fwd()), 2, 8, 1); c.me.heal(6); c.sound(SoundEvents.SOUL_ESCAPE, 1.5f, .6f); }
        }, (p, t, m) -> {
            float s = env(t, 8, 64, 14);
            p.r("armL", -1.57f * s, 0, 0).o("armL", 0, 0, t > 10 && t < 40 ? 1.5f : 0).r("head", -.2f * s, 0, 0);
        });
    }
    /** Black blasters with white beams. */
    static Attack rpBlasters() {
        return atk("reaper_death_blasters", 80, (c, t) -> {
            if (t < 30 && t % 10 == 0) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = c.pos().add(c.right().scale(s * 2.5)).add(0, 2.4, 0);
                blaster(c, at, c.tpos().subtract(at), 20, 12, 8, .9f, 0x1a1a1a, 0xffffff, 0xf0f0f0);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 12); p.r("armL", -2.8f * s, 0, .3f).o("halo", 0, s, 0);
        });
    }
    /** Phase 2: gravestone-bones burst up across the field. */
    static Attack rpGraves() {
        return atk("reaper_grave_bones", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 5 == 0) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 2.5, 0, c.rnd.nextGaussian() * 2.5);
                pillar(c, at, 2.4f, .6f, 0xd8d8d8, 7, 10, 20); c.particle(ParticleTypes.SOUL, at, 6, .3, .05);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 70, 12); p.r("armR", .6f * s, 0, -.2f).o("body", 0, -1.2f * s, 0);
        }).when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ OUTERTALE SANS (space)
    static CharacterDef outertale() {
        return CharacterDef.of("outertale_sans", "Outertale Sans", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(150).boss().keep(7).rest(14).speed(.3f)
            .egg(0x1a1a5a, 0xfff07a).size(.7f, 1.1f).scale(.9f)
            .model(skel(0x1a1a5a, 0x2a2a7a, 0x111111, 0xf5f5f5, 0x000000, 0x8ad8ff, 0x222222)
                .glow("star1", "body", -2, 1.5f, -2.6f, .6f, .6f, .2f, 0xfff07a)
                .glow("star2", "body", 2, -1, -2.6f, .5f, .5f, .2f, 0xfff07a)
                .glow("star3", "body", 0, -2.5f, -2.6f, .4f, .4f, .2f, 0xfff07a)
                .ghost("nebula", "body", 0, 6, 3, 12, 12, .3f, 0x6a3ad0, .25f))
            .attacks(otMeteors(), otStarBones(), otPlanets(), otConstellation()).build();
    }
    /** Meteor shower streaking diagonally down. */
    static Attack otMeteors() {
        return atk("outer_meteor_shower", 76, (c, t) -> {
            if (t >= 8 && t < 60 && t % 3 == 0) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3 - 4, 9, c.rnd.nextGaussian() * 3);
                c.proj().at(at).vel(new Vec3(.35, -.7, 0)).box(.5f, .5f, .9f).color(0xffb84a).dmg(5).warn(6).life(22).spawn();
                c.particle(ParticleTypes.FLAME, at, 3, .1, .01);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 76, 12); p.r("armR", -2.8f * s, 0, -.2f).r("head", -.4f * s, 0, 0);
        });
    }
    /** Star-tipped bones drifting in zero gravity. */
    static Attack otStarBones() {
        return atk("outer_star_bones", 70, (c, t) -> {
            if (t >= 6 && t < 50 && t % 6 == 0) for (int k = -2; k <= 2; k++) {
                Vec3 at = c.pos().add(c.right().scale(k * 1.1)).add(0, 1 + Math.abs(k) * .3, 0);
                Vec3 v = c.aim(at).scale(.35).add(0, Math.sin(t + k) * .03, 0);
                c.proj().at(at).vel(v).box(.2f, 1f, .2f).color(WHITE).dmg(4).warn(4).life(44).spawn();
                c.proj().at(at.add(0, .55, 0)).vel(v).box(.4f, .4f, .4f).color(0xfff07a).dmg(2).warn(4).life(44).spawn();
            }
        }, (p, t, m) -> p.o("body", 0, 1 + wave(t, .2f), 0).r("armL", -1f, 0, .5f).r("armR", -1f, 0, -.5f));
    }
    /** Little planets orbit him, then launch one after another. */
    static Attack otPlanets() {
        return atk("outer_planet_orbit", 80, (c, t) -> {
            if (t < 30 && t % 2 == 0) c.particle(ParticleTypes.END_ROD, around(c.pos(), 2, t * .3, 1.2), 1, 0, 0);
            if (t >= 30 && t < 70 && t % 8 == 0) {
                Vec3 o = around(c.pos(), 2, t * .3, 1.2);
                int[] cols = {0x6ac0ff, 0xff8a5a, 0x9aff8a, 0xd0a0ff, 0xfff07a};
                shot(c, o, c.aim(o).scale(.5), .9f, cols[(t / 8) % 5], 7, 4, 40);
                c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .8f);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 80, 12); p.r("armL", 0, 0, 1.3f * s).r("armR", 0, 0, -1.3f * s).o("body", 0, 1.5f * s, 0);
        });
    }
    /** Phase 2: blasters appear at stars of a constellation, connected by lines, then fire. */
    static Attack otConstellation() {
        return atk("outer_constellation_blasters", 90, (c, t) -> {
            if (t == 4) {
                Vec3 prev = null;
                for (int i = 0; i < 6; i++) {
                    Vec3 at = around(c.tpos(), 6, i * 1.05 + .3, 1.5 + (i % 3));
                    if (prev != null) seg(c, prev, at, .05f, 0xfff07a, 0, 0, 40);
                    blaster(c, at, c.tpos().subtract(at), 26 + i * 4, 12, 8, .8f, 0xf2f2f2, 0x8ad8ff, 0xcfefff);
                    prev = at;
                }
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 90, 14); p.r("armR", -2.9f * s, 0, -.1f).r("head", -.5f * s, 0, 0);
        }).when(m -> m.hpFrac() < .5f);
    }

    private AuMultiverse() {}
}
