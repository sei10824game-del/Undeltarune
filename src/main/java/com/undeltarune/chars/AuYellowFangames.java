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

/** Undertale Yellow (Clover's journey): every boss/miniboss and the Feisty Four. North Star is Starlo's alias, so one entry. */
public final class AuYellowFangames {
    static final int YEL = 0xffd400, TEAL = 0x2ad4c4, PINK = 0xffb7d5, FIRE = 0xffa020, PURP = 0x8a4cd0;

    public static void register(List<CharacterDef> out) {
        out.add(clover()); out.add(decibat()); out.add(dalv()); out.add(martlet()); out.add(bailador());
        out.add(starlo()); out.add(ed()); out.add(moray()); out.add(ace()); out.add(mooch());
        out.add(guardener()); out.add(ceroba()); out.add(axis()); out.add(chujin()); out.add(flowey()); out.add(zenith());
    }

    /** Generic UTY humanoid body. */
    static ModelDef hum(int torso, int skin, int legs, int arms) {
        return new ModelDef()
            .box("body", null, 0, 9, 0, 7, 8, 4, torso)
            .box("head", "body", 0, 8, 0, 7, 7, 7, skin)
            .limb("armL", "body", -4.6f, 3, 0, 2.2f, 7, 2.2f, arms)
            .limb("armR", "body", 4.6f, 3, 0, 2.2f, 7, 2.2f, arms)
            .limb("legL", null, -1.8f, 5, 0, 2.6f, 5, 2.6f, legs)
            .limb("legR", null, 1.8f, 5, 0, 2.6f, 5, 2.6f, legs);
    }
    static ModelDef eyes(ModelDef m, int rgb, float y) {
        return m.glow("eyeL", "head", -1.6f, y, -3.6f, 1.2f, 1.4f, .3f, rgb).glow("eyeR", "head", 1.6f, y, -3.6f, 1.2f, 1.4f, .3f, rgb);
    }
    static Vec3 hand(AttackCtx c, int side) { return c.pos().add(c.right().scale(side * .4)).add(c.fwd().scale(.5)).add(0, 1, 0); }
    static void ringBO(AttackCtx c, Vec3 at, boolean blue, int n) {
        for (int i = 0; i < n; i++) {
            double a = i * Math.PI * 2 / n;
            var b = c.proj().at(at).vel(new Vec3(Math.cos(a) * .4, 0, Math.sin(a) * .4)).box(.5f, .9f, .5f).dmg(5).life(24);
            (blue ? b.blue() : b.orange()).spawn();
        }
    }
    static void eff(AttackCtx c, net.minecraft.world.effect.MobEffect e, int dur, int amp) { c.target.addEffect(new MobEffectInstance(e, dur, amp)); }

    // ============================================================ CLOVER (the yellow SOUL of justice)
    static CharacterDef clover() {
        return CharacterDef.of("uty_clover", "Clover", Faction.NEUTRAL).att(Attitude.FRIENDLY).hp(60).keep(8).rest(16).speed(.3f)
            .egg(YEL, 0x7a4a20).size(.6f, 1.5f).scale(.85f)
            .model(eyes(hum(YEL, 0xf2c79a, 0x3a3a6a, YEL), 0x222222, .5f)
                .box("stripe", "body", 0, 0, -2.1f, 7.2f, 1.2f, .3f, 0x6a3a1a)
                .box("hair", "head", 0, 2.5f, .5f, 7.4f, 2.4f, 7.4f, 0x5a3a1a)
                .box("hat", "head", 0, 4.2f, 0, 7.6f, 2.4f, 7.6f, 0x8a5a2a)
                .box("brim", "hat", 0, -1.2f, 0, 11, .5f, 11, 0x7a4a20)
                .box("gun", "armR", 0, -7.5f, -1.5f, .9f, 1.2f, 3.6f, 0x9a9aa8))
            .attacks(clShot(), clCharged(), clIce(), clFlint(), clDash()).build();
    }
    /** Six quick revolver shots of yellow justice. */
    static Attack clShot() {
        return atk("clover_six_shooter", 50, (c, t) -> {
            if (t >= 8 && t < 44 && t % 6 == 2) {
                Vec3 o = hand(c, 1); shot(c, o, c.aim(o).scale(1.1), .25f, YEL, 4, 0, 24);
                c.sound(SoundEvents.CROSSBOW_SHOOT, .7f, 1.8f); c.particle(ParticleTypes.SMOKE, o, 2, .05, .01);
            }
        }, (p, t, m) -> {
            float rec = (t % 6) < 2 && t > 8 ? .3f : 0;
            p.r("armR", -1.57f - rec, 0, 0).r("armL", -1.2f, .5f, 0).r("body", 0, -.2f, 0);
        });
    }
    /** Charged big shot that pierces. */
    static Attack clCharged() {
        return atk("clover_charged_shot", 46, (c, t) -> {
            if (t < 24 && t % 3 == 0) c.particle(ParticleTypes.WAX_ON, hand(c, 1), 3, .1, .02);
            if (t == 24) { Vec3 o = hand(c, 1); c.proj().at(o).vel(c.aim(o).scale(1.2)).box(.8f, .8f, .8f).color(YEL).dmg(10).life(24).pierce().spawn(); c.sound(SoundEvents.FIREWORK_ROCKET_BLAST, 1, 1.2f); }
        }, (p, t, m) -> p.r("armR", -1.57f - (t > 24 && t < 30 ? .5f : 0), 0, 0).r("armL", -1.57f, -.3f, 0).o("armR", 0, 0, t < 24 ? t * .04f : 0));
    }
    /** Ice Pellets ammo: wide scatter. */
    static Attack clIce() {
        return atk("clover_ice_pellets", 44, (c, t) -> {
            if (t == 12 || t == 26) { fan(c, hand(c, 1), 5, 40, .8, .25f, 0xaef0ff, 3, 0, 18); c.sound(SoundEvents.GLASS_BREAK, .5f, 1.8f); }
        }, (p, t, m) -> p.r("armR", -1.57f - ((t % 14) < 3 ? .4f : 0), 0, 0));
    }
    /** Flint ammo: fiery shot that sets the target alight. */
    static Attack clFlint() {
        return atk("clover_flint_shot", 40, (c, t) -> {
            if (t == 14) { Vec3 o = hand(c, 1); shot(c, o, c.aim(o).scale(1), .35f, FIRE, 5, 0, 24); c.sound(SoundEvents.FIRECHARGE_USE, 1, 1.2f); }
            if (t == 20 && c.target.distanceTo(c.me) < 10) c.target.setSecondsOnFire(3);
        }, (p, t, m) -> p.r("armR", -1.57f - (t > 14 && t < 18 ? .4f : 0), 0, 0).r("head", .1f, 0, 0));
    }
    /** SOUL dash (unlocked against Ceroba): darts sideways and fires mid-air. */
    static Attack clDash() {
        return atk("clover_soul_dash", 40, (c, t) -> {
            if (t == 6) c.dash(c.right().scale(c.rnd.nextBoolean() ? 1.2 : -1.2).add(0, .25, 0));
            if (t == 12) { Vec3 o = hand(c, 1); shot(c, o, c.aim(o).scale(1.1), .3f, YEL, 5, 0, 22); c.sound(SoundEvents.CROSSBOW_SHOOT, .7f, 1.6f); }
        }, (p, t, m) -> p.r("body", .2f, 0, t > 6 && t < 14 ? .4f : 0).r("armR", -1.57f, 0, 0).r("legL", -.6f, 0, 0).r("legR", .6f, 0, 0))
            .when(m -> m.hpFrac() < .7f);
    }

    // ============================================================ DECIBAT (Dark Ruins miniboss)
    static CharacterDef decibat() {
        return CharacterDef.of("uty_decibat", "Decibat", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(70).boss().keep(6).rest(16).speed(.32f)
            .egg(0x4a2a6a, 0xff6060).size(.8f, 1.2f).scale(1f)
            .model(new ModelDef()
                .box("body", null, 0, 12, 0, 7, 7, 6, 0x4a2a6a)
                .box("head", "body", 0, 6, 0, 7, 6, 6, 0x5a3a7a)
                .box("earL", "head", -2.5f, 4, 0, 1.5f, 3, 1, 0x5a3a7a).box("earR", "head", 2.5f, 4, 0, 1.5f, 3, 1, 0x5a3a7a)
                .box("phones", "head", 0, 1, 0, 8, 2, 2, 0x222222)
                .glow("eyeL", "head", -1.5f, .5f, -3.1f, 1.2f, 1.2f, .3f, 0xff4040).glow("eyeR", "head", 1.5f, .5f, -3.1f, 1.2f, 1.2f, .3f, 0xff4040)
                .limb("armL", "body", -3.5f, 2, 0, 9, .6f, 5, 0x3a1a5a).limb("armR", "body", 3.5f, 2, 0, 9, .6f, 5, 0x3a1a5a)
                .limb("legL", null, -1.5f, 8.5f, 0, 1.2f, 3, 1.2f, 0x2a1a3a).limb("legR", null, 1.5f, 8.5f, 0, 1.2f, 3, 1.2f, 0x2a1a3a))
            .attacks(dbWaves(), dbNotes(), dbScreech(), dbSwoop()).build();
    }
    static void wingFlap(Pose p, float t, float amp) { p.r("armL", 0, 0, wave(t, .8f) * amp).r("armR", 0, 0, -wave(t, .8f) * amp).o("body", 0, wave(t, .8f) * .5f, 0); }
    /** Concentric sound waves pulse out (louder = faster). */
    static Attack dbWaves() {
        return atk("decibat_sound_waves", 64, (c, t) -> {
            if (t >= 8 && t % 12 == 8 && t < 60) { ring(c, c.eye(), 14, .3 + t * .004, t * .2, .3f, 0xd0b0ff, 4, 4, 30); c.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1, .8f + t * .01f); }
        }, (p, t, m) -> { wingFlap(p, t, .6f); p.r("head", -.2f, 0, 0); });
    }
    /** Volume notes rain from his headphones. */
    static Attack dbNotes() {
        return atk("decibat_loud_notes", 60, (c, t) -> {
            if (t >= 6 && t < 50 && t % 3 == 0) {
                c.proj().at(c.target.position().add(c.rnd.nextGaussian() * 3, 6, c.rnd.nextGaussian() * 3)).vel(new Vec3(0, -.5, 0)).box(.35f, .5f, .2f).color(0xff6060).dmg(3).warn(4).life(20).spawn();
                if (t % 9 == 0) c.particle(ParticleTypes.NOTE, c.eye(), 3, .6, 1);
            }
        }, (p, t, m) -> { wingFlap(p, t, .4f); p.r("head", 0, wave(t, .5f) * .4f, 0); });
    }
    /** Screech: a cone of sonic force that shoves and dazes. */
    static Attack dbScreech() {
        return atk("decibat_screech", 50, (c, t) -> {
            if (t == 18) {
                c.aoe(c.pos().add(c.fwd().scale(2.5)), 3, 6, 1.6); eff(c, MobEffects.CONFUSION, 80, 0);
                c.sound(SoundEvents.PHANTOM_SWOOP, 1.5f, .6f); fan(c, c.eye(), 7, 60, .6, .4f, 0xffffff, 3, 0, 14);
            }
        }, (p, t, m) -> { float s = env(t, 16, 50, 14); p.r("armL", 0, -1f * s, 1.2f * s).r("armR", 0, 1f * s, -1.2f * s).r("head", -.3f * s, 0, 0); });
    }
    /** Swoops through the target. */
    static Attack dbSwoop() {
        return atk("decibat_swoop", 40, (c, t) -> {
            if (t == 10) c.dash(c.aim(c.pos()).scale(1.4).add(0, .3, 0));
            if (t == 14) c.aoe(c.pos(), 1.6, 6, .6);
        }, (p, t, m) -> { wingFlap(p, t * 2, .9f); p.r("body", t > 10 && t < 18 ? .8f : 0, 0, 0); });
    }

    // ============================================================ DALV (Dark Ruins boss)
    static CharacterDef dalv() {
        return CharacterDef.of("uty_dalv", "Dalv", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(130).boss().keep(8).rest(14).speed(.28f)
            .egg(0x6a4a9a, 0x1a1a2a).size(.6f, 1.8f).scale(1f)
            .model(eyes(hum(0x1a1a2a, 0xb8a8d8, 0x1a1a2a, 0x1a1a2a), 0xffffff, 0)
                .box("cloak", "body", 0, -1, 1.6f, 9, 12, 1, 0x2a1a3a)
                .box("collar", "body", 0, 4, 0, 8, 2, 5, 0x3a2a5a)
                .box("hairD", "head", 0, 3, .5f, 7.6f, 2, 7.6f, 0x2a2a3a)
                .box("fangL", "head", -1, -2.5f, -3.6f, .5f, .8f, .2f, 0xffffff).box("fangR", "head", 1, -2.5f, -3.6f, .5f, .8f, .2f, 0xffffff)
                .glow("orb", "armR", 0, -8, 0, 1.4f, 1.4f, 1.4f, PURP))
            .attacks(dvOrbs(), dvLightning(), dvWalls(), dvSpiral(), dvDarkness()).build();
    }
    /** Orbs of purple magic orbit, then launch. */
    static Attack dvOrbs() {
        return atk("dalv_magic_orbs", 76, (c, t) -> {
            if (t < 24 && t % 3 == 0) c.particle(ParticleTypes.WITCH, around(c.pos(), 1.4, t * .4, 1.3), 2, .05, 0);
            if (t >= 24 && t < 64 && t % 5 == 0) { Vec3 o = around(c.pos(), 1.4, t * .4, 1.3); shot(c, o, c.aim(o).scale(.5), .45f, PURP, 4, 3, 40); }
        }, (p, t, m) -> { float s = env(t, 10, 76, 12); p.r("armR", -2.4f * s, 0, -.2f).r("armL", -1f * s, 0, .6f); });
    }
    /** Lightning bolts strike marked spots. */
    static Attack dvLightning() {
        return atk("dalv_dark_lightning", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 8 == 0) {
                Vec3 g = c.target.position().add(c.rnd.nextGaussian() * 1.5, 0, c.rnd.nextGaussian() * 1.5);
                pillar(c, g, 8, .5f, 0xd8c8ff, 7, 14, 20);
                if (t % 16 == 0) c.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, .4f, 1.6f);
            }
        }, (p, t, m) -> { float s = env(t, 8, 70, 12); p.r("armL", -2.9f * s, 0, .2f).r("armR", -2.9f * s, 0, -.2f).r("head", -.3f * s, 0, 0); });
    }
    /** Walls of dark magic slide in with a gap. */
    static Attack dvWalls() {
        return atk("dalv_shadow_walls", 70, (c, t) -> {
            if (t % 18 == 6 && t < 60) {
                Vec3 f = c.fwd(), r = c.right(); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) if (Math.abs(k - gap) > 0)
                    c.proj().at(c.pos().add(f.scale(1.5)).add(r.scale(k * .9)).add(0, .9, 0)).vel(f.scale(.55)).box(.8f, 1.8f, .3f).color(0x3a2a5a).dmg(5).warn(6).life(36).spawn();
            }
        }, (p, t, m) -> p.r("armL", -1.5f, 0, .5f).r("armR", -1.5f, 0, -.5f).o("cloak", 0, 0, wave(t, .3f) * .5f));
    }
    /** Spiral of purple bullets from his raised hand. */
    static Attack dvSpiral() {
        return atk("dalv_magic_spiral", 70, (c, t) -> {
            if (t >= 10 && t < 60 && t % 2 == 0) {
                double a = t * .45; Vec3 o = c.pos().add(0, 1.2, 0);
                shot(c, o, new Vec3(Math.cos(a) * .4, 0, Math.sin(a) * .4), .3f, PURP, 3, 0, 30);
            }
        }, (p, t, m) -> p.r("armR", -3f, 0, 0).r("body", 0, t * .12f, 0)).when(m -> m.hpFrac() < .6f);
    }
    /** Shrouds the area in darkness; eyes of light home in. */
    static Attack dvDarkness() {
        return atk("dalv_shroud", 80, (c, t) -> {
            if (t == 4) { eff(c, MobEffects.BLINDNESS, 70, 0); c.sound(SoundEvents.AMBIENT_CAVE.value(), 1, .8f); }
            if (t >= 16 && t < 70 && t % 9 == 0) { Vec3 o = around(c.tpos(), 6, c.rnd.nextDouble() * 6.28, .5); shot(c, o, c.aim(o).scale(.3), .35f, 0xffffff, 4, 8, 40); }
        }, (p, t, m) -> { float s = env(t, 8, 80, 14); p.r("armL", -1.6f * s, -.8f * s, 0).r("armR", -1.6f * s, .8f * s, 0).o("cloak", 0, 0, -s); })
            .when(m -> m.hpFrac() < .35f);
    }

    // ============================================================ MARTLET (Royal Guard, Snowdin)
    static ModelDef birdModel(int robe, int feather, int glow) {
        return new ModelDef()
            .box("body", null, 0, 9, 0, 7, 9, 4.5f, robe)
            .box("collar", "body", 0, 4, 0, 7.5f, 1.5f, 5, 0xf0f0f0)
            .box("head", "body", 0, 8.5f, 0, 6.5f, 6.5f, 6.5f, feather)
            .box("beak", "head", 0, -.5f, -4, 2, 1.2f, 2, 0xff9a1f)
            .glow("eyeL", "head", -1.5f, .8f, -3.3f, 1, 1.4f, .3f, glow).glow("eyeR", "head", 1.5f, .8f, -3.3f, 1, 1.4f, .3f, glow)
            .box("crest", "head", 0, 4, .5f, 1.4f, 2.5f, 3, feather)
            .limb("armL", "body", -4.2f, 3.5f, 0, 1, 8, 4, feather).limb("armR", "body", 4.2f, 3.5f, 0, 1, 8, 4, feather)
            .limb("legL", null, -1.5f, 4.5f, 0, 1.2f, 4.5f, 1.2f, 0xff9a1f).limb("legR", null, 1.5f, 4.5f, 0, 1.2f, 4.5f, 1.2f, 0xff9a1f);
    }
    static CharacterDef martlet() {
        return CharacterDef.of("uty_martlet", "Martlet", Faction.ROYAL_GUARD).att(Attitude.NEUTRAL).hp(140).boss().keep(7).rest(14).speed(.32f)
            .egg(0x3a6fe0, 0xf0f0f0).size(.6f, 1.7f).scale(1f)
            .model(birdModel(0x2a4fb0, 0x5a8fff, 0x111111))
            .attacks(mtFeathers(), mtGust(), mtMail(), mtSnow(), mtDive()).build();
    }
    static void flap(Pose p, float t, float s) { p.r("armL", 0, 0, (1f + wave(t, .9f) * .5f) * s).r("armR", 0, 0, -(1f + wave(t, .9f) * .5f) * s); }
    /** Feather darts flicked in volleys from her wings. */
    static Attack mtFeathers() {
        return atk("martlet_feather_darts", 64, (c, t) -> {
            if (t >= 10 && t < 54 && t % 6 == 0) {
                int s = (t / 6) % 2 == 0 ? 1 : -1; Vec3 o = hand(c, s).add(0, .4, 0);
                fan(c, o, 3, 18, .7, .18f, 0x5a8fff, 4, 2, 26);
                c.sound(SoundEvents.PARROT_FLY, .8f, 1.4f);
            }
        }, (p, t, m) -> flap(p, t, env(t, 8, 64, 10)));
    }
    /** Wind gusts that shove the target sideways while feathers drift through. */
    static Attack mtGust() {
        return atk("martlet_wind_gust", 70, (c, t) -> {
            if (t >= 12 && t < 60 && t % 4 == 0) {
                Vec3 r = c.right().scale(((t / 20) % 2 == 0 ? 1 : -1) * .25);
                c.target.push(r.x, 0, r.z); c.target.hurtMarked = true;
                c.particle(ParticleTypes.CLOUD, c.tpos(), 4, .8, .05);
                Vec3 at = c.tpos().subtract(r.normalize().scale(6)).add(0, c.rnd.nextGaussian() * .5, 0);
                dart(c, at, r.normalize().scale(.5), .15f, .9f, 0xf0f0f0, 3, 4, 26);
            }
        }, (p, t, m) -> { flap(p, t * 1.5f, 1); p.r("body", 0, ((t / 20) % 2 == 0 ? .3f : -.3f), 0); });
    }
    /** Mail delivery: packages drop from above. */
    static Attack mtMail() {
        return atk("martlet_mail_drop", 66, (c, t) -> {
            if (t >= 8 && t < 50 && t % 7 == 0) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 2, 7, c.rnd.nextGaussian() * 2);
                c.proj().at(at).vel(Vec3.ZERO).gravity(.03f).box(.8f, .6f, .8f).color(0xb07a40).dmg(5).warn(10).life(30).spawn();
            }
        }, (p, t, m) -> { flap(p, t, .6f); p.r("armR", -2.6f, 0, 0); });
    }
    /** Snowflakes swirl in a drifting curtain. */
    static Attack mtSnow() {
        return atk("martlet_snow_flurry", 70, (c, t) -> {
            if (t >= 8 && t < 60 && t % 2 == 0) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 4, 5, c.rnd.nextGaussian() * 4);
                c.proj().at(at).vel(new Vec3(Math.sin(t * .2) * .08, -.18, 0)).box(.25f, .25f, .25f).color(0xe8f8ff).dmg(2).warn(4).life(34).spawn();
                c.particle(ParticleTypes.SNOWFLAKE, at, 2, .3, .02);
            }
        }, (p, t, m) -> flap(p, t * .6f, .8f));
    }
    /** Phase 2: wings tucked, she dive-bombs. */
    static Attack mtDive() {
        return atk("martlet_dive_bomb", 46, (c, t) -> {
            if (t == 6) c.dash(new Vec3(0, .9, 0));
            if (t == 16) c.dash(c.aim(c.pos()).scale(1.6));
            if (t == 22) { c.aoe(c.pos(), 2.2, 8, 1); c.particle(ParticleTypes.CLOUD, c.pos(), 20, .8, .05); c.sound(SoundEvents.PHANTOM_SWOOP, 1, 1); }
        }, (p, t, m) -> { boolean dive = t > 14 && t < 24; p.r("armL", dive ? .5f : 0, 0, dive ? .2f : 1.6f).r("armR", dive ? .5f : 0, 0, dive ? -.2f : -1.6f).r("body", dive ? 1.2f : 0, 0, 0); })
            .when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ EL BAILADOR (Snowdin miniboss)
    static CharacterDef bailador() {
        return CharacterDef.of("uty_el_bailador", "El Bailador", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(110).boss().keep(6).rest(14).speed(.34f)
            .egg(0x2a8a4a, 0xd01a2a).size(.6f, 1.8f).scale(1f)
            .model(eyes(hum(0x1a1a1a, 0x3aaa5a, 0x1a1a1a, 0x1a1a1a), 0xffffff, .5f)
                .box("sash", "body", 0, -2, 0, 7.4f, 1.6f, 4.4f, 0xd01a2a)
                .box("hat", "head", 0, 4.4f, 0, 5, 2, 5, 0x1a1a1a).box("hatBrim", "hat", 0, -1, 0, 10, .5f, 10, 0x1a1a1a)
                .box("antL", "hat", -1.5f, 2.5f, 0, .4f, 3, .4f, 0x3aaa5a).box("antR", "hat", 1.5f, 2.5f, 0, .4f, 3, .4f, 0x3aaa5a)
                .box("rose", "head", 0, -2.2f, -3.8f, 1, 1, 1, 0xd01a2a)
                .box("guitar", "armL", 0, -6, -1.5f, 2.6f, 3.5f, .8f, 0xa86a2a))
            .attacks(ebNotes(), ebSpin(), ebRoses(), ebHat(), ebCastanets()).build();
    }
    /** Guitar strum: notes in a rhythmic wave. */
    static Attack ebNotes() {
        return atk("bailador_guitar_strum", 64, (c, t) -> {
            if (t >= 8 && t < 56 && t % 4 == 0) {
                double off = Math.sin(t * .3) * .6; Vec3 o = c.eye(), d = c.aim(o), side = new Vec3(-d.z, 0, d.x);
                shot(c, o.add(side.scale(off)), d.scale(.5), .35f, 0xffe23b, 3, 2, 30);
                c.particle(ParticleTypes.NOTE, o, 1, .3, 1);
            }
        }, (p, t, m) -> p.r("armL", -1f, .6f, 0).r("armR", -1f + (t % 4 < 2 ? .4f : 0), -.4f, 0).r("body", 0, wave(t, .4f) * .2f, 0));
    }
    /** Pirouette: spins and flings a ring of sharp rhinestones. */
    static Attack ebSpin() {
        return atk("bailador_pirouette", 56, (c, t) -> {
            if (t >= 10 && t < 46 && t % 6 == 0) { ring(c, c.pos().add(0, 1, 0), 10, .45, t * .3, .3f, 0xa0f0ff, 4, 0, 24); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, 1.5f); }
        }, (p, t, m) -> { float s = env(t, 8, 56, 10); p.r("body", 0, t * .7f * s, 0).r("armL", 0, 0, 1.4f * s).r("armR", 0, 0, -1.4f * s).r("legR", -.8f * s, 0, 0); });
    }
    /** Throws roses in arcing lobs. */
    static Attack ebRoses() {
        return atk("bailador_rose_toss", 60, (c, t) -> {
            if (t >= 12 && t < 48 && t % 9 == 3) {
                Vec3 o = hand(c, 1).add(0, .5, 0), d = c.tpos().subtract(o); double fl = 16;
                c.proj().at(o).vel(new Vec3(d.x / fl, d.y / fl + .02 * fl * .5, d.z / fl)).gravity(.02f).box(.3f, .3f, .8f).color(0xd01a2a).dmg(5).life(24).spawn();
                c.particle(ParticleTypes.CHERRY_LEAVES, o, 3, .2, .02);
            }
        }, (p, t, m) -> { float k = ((t + 6) % 9) / 9f; p.r("armR", -2.6f + 2.4f * k, 0, -.2f).r("head", 0, .3f, 0); });
    }
    /** Hat boomerang. */
    static Attack ebHat() {
        return atk("bailador_hat_throw", 50, (c, t) -> {
            if (t == 10) {
                Vec3 o = c.eye(), d = c.aim(o);
                c.proj().at(o).vel(d.scale(.6)).box(1.4f, .3f, 1.4f).color(0x1a1a1a).dmg(5).life(14).pierce().spawn();
                c.proj().at(o.add(d.scale(8.4))).vel(d.scale(-.6)).box(1.4f, .3f, 1.4f).color(0x1a1a1a).dmg(5).warn(14).life(28).pierce().spawn();
            }
        }, (p, t, m) -> p.o("hat", 0, t > 10 && t < 38 ? 20 : 0, 0).r("armR", t < 12 ? -2.5f : -.6f, 0, -.3f));
    }
    /** Phase 2: castanet clacks summon closing pincers. */
    static Attack ebCastanets() {
        return atk("bailador_castanets", 64, (c, t) -> {
            if (t % 14 == 6 && t < 60) for (int s = -1; s <= 1; s += 2) {
                Vec3 r = c.right(), at = c.tpos().add(r.scale(s * 5));
                c.proj().at(at).vel(r.scale(-s * .35)).box(.4f, 1.4f, 1.4f).color(0xa86a2a).dmg(5).warn(6).life(16).spawn();
            }
            if (t % 14 == 6) c.sound(SoundEvents.WOOD_HIT, 1, 1.6f);
        }, (p, t, m) -> { float k = (t % 14) < 3 ? 1 : 0; p.r("armL", -2.6f, 0, .5f - .5f * k).r("armR", -2.6f, 0, -.5f + .5f * k); })
            .when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ STARLO / "NORTH STAR" (Dunes boss, Feisty Five leader)
    static CharacterDef starlo() {
        ModelDef md = eyes(hum(0x7a4a20, 0xffe060, 0x6a3a1a, 0x7a4a20), 0x111111, -.2f)
            .box("poncho", "body", 0, 2, 0, 9, 4, 5.5f, 0x8a5a2a)
            .box("ponchoStripe", "poncho", 0, -1, -2.8f, 9.1f, .8f, .2f, TEAL)
            .glow("badge", "body", -1.8f, 1, -2.9f, 1.6f, 1.6f, .3f, YEL)
            .box("starL", "head", -4, 2, 0, 2, 2, 1, 0xffe060).box("starR", "head", 4, 2, 0, 2, 2, 1, 0xffe060)
            .box("hat", "head", 0, 4.6f, 0, 6, 2.4f, 6, 0x6a3a1a).box("brim", "hat", 0, -1.2f, 0, 11, .5f, 11, 0x6a3a1a)
            .box("hatBand", "hat", 0, -.5f, 0, 6.2f, .6f, 6.2f, TEAL)
            .box("gun", "armR", 0, -7.5f, -1.5f, .9f, 1.2f, 3.6f, 0x8a8a98)
            .box("lasso", "armL", 0, -7, 0, 3, .4f, 3, 0xc89a5a);
        return CharacterDef.of("uty_starlo", "Starlo", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(160).boss().keep(8).rest(14).speed(.32f)
            .egg(0xffe060, 0x7a4a20).size(.6f, 1.8f).scale(1f).model(md)
            .attacks(stDuel(), stLasso(), stStars(), stTumble(), stCactus(), stShowdown()).build();
    }
    /** Six-shooter volley; blue/orange shots alternate (stay still / keep moving). */
    static Attack stDuel() {
        return atk("starlo_six_shooter", 60, (c, t) -> {
            if (t >= 10 && t < 50 && t % 6 == 4) {
                Vec3 o = hand(c, 1); var b = c.proj().at(o).vel(c.aim(o).scale(.9)).box(.3f, .3f, .6f).dmg(5).life(28);
                ((t / 6) % 2 == 0 ? b.blue() : b.orange()).spawn();
                c.sound(SoundEvents.CROSSBOW_SHOOT, .8f, 1.4f); c.particle(ParticleTypes.SMOKE, o, 2, .05, .01);
            }
        }, (p, t, m) -> p.r("armR", -1.57f - ((t % 6) < 2 && t > 10 ? .35f : 0), 0, 0).r("armL", -.3f, 0, .3f));
    }
    /** Twirls his lasso overhead, then ropes the target in. */
    static Attack stLasso() {
        return atk("starlo_lasso", 64, (c, t) -> {
            if (t < 24 && t % 3 == 0) c.particle(ParticleTypes.CRIT, around(c.pos(), 1.2, t * .6, 2.4), 2, 0, 0);
            if (t == 24) c.sound(SoundEvents.FISHING_BOBBER_THROW, 1, .8f);
            if (t >= 26 && t < 44 && t % 3 == 0) {
                seg(c, hand(c, -1), c.tpos(), .08f, 0xc89a5a, 0, 0, 4);
                Vec3 d = c.pos().subtract(c.target.position()).normalize().scale(.35); c.target.setDeltaMovement(d.x, .05, d.z); c.target.hurtMarked = true;
            }
            if (t == 46) c.aoe(c.pos().add(c.fwd()), 1.8, 6, 1.2);
        }, (p, t, m) -> {
            if (t < 24) p.r("armL", -3f, (float) t * .6f, 0).o("lasso", 0, 0, 0);
            else p.r("armL", -1.57f, 0, 0).o("armL", 0, 0, t < 44 ? 1 : 0);
        });
    }
    /** Shooting stars curve down from the sky. */
    static Attack stStars() {
        return atk("starlo_shooting_stars", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 5 == 0) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3, 8, c.rnd.nextGaussian() * 3 - 3);
                c.proj().at(at).vel(new Vec3(0, -.6, .2)).box(.6f, .6f, .6f).color(YEL).dmg(5).warn(6).life(20).spawn();
                c.particle(ParticleTypes.END_ROD, at, 3, .2, .02);
            }
        }, (p, t, m) -> { float s = env(t, 6, 70, 10); p.r("armR", -3f * s, 0, 0).o("gun", 0, 0, 0).r("head", -.3f * s, 0, 0); });
    }
    /** Tumbleweeds bounce across the field. */
    static Attack stTumble() {
        return atk("starlo_tumbleweeds", 70, (c, t) -> {
            if (t >= 6 && t < 56 && t % 10 == 6) {
                int s = c.rnd.nextBoolean() ? 1 : -1; Vec3 r = c.right(), at = c.tpos().add(r.scale(s * 8)).add(0, 1.5, 0);
                c.proj().at(at).vel(r.scale(-s * .4).add(0, .15, 0)).gravity(.02f).box(1, 1, 1).color(0xb8925a).dmg(4).warn(6).life(40).spawn();
            }
        }, (p, t, m) -> p.r("armR", -.8f, 0, -.4f).r("armL", -.8f, 0, .4f).o("body", 0, wave(t, .5f) * .2f, 0));
    }
    /** Cacti burst up in a row toward the target. */
    static Attack stCactus() {
        return atk("starlo_cactus_row", 60, (c, t) -> {
            if (t >= 10 && t < 40 && t % 4 == 2) {
                Vec3 d = c.aim(c.pos()).multiply(1, 0, 1).normalize();
                Vec3 at = c.pos().add(d.scale(1.5 + (t - 10) * .3));
                pillar(c, at, 2, .7f, 0x3a9a4a, 5, 8, 18);
            }
        }, (p, t, m) -> { float s = env(t, 8, 60, 10); p.r("legR", -1.2f * s, 0, 0).r("body", .1f * s, 0, 0); });
    }
    /** Phase 2: high-noon quick draw — reticle, beat, then one massive shot. */
    static Attack stShowdown() {
        return atk("starlo_high_noon", 70, (c, t) -> {
            if (t < 36 && t % 6 == 0) { Vec3 tp = c.target.position().add(0, 1, 0); seg(c, tp.add(-1, 0, 0), tp.add(1, 0, 0), .06f, 0xff3030, 0, 0, 6); seg(c, tp.add(0, -1, 0), tp.add(0, 1, 0), .06f, 0xff3030, 0, 0, 6); }
            if (t == 30) c.sound(SoundEvents.BELL_BLOCK, 1, .7f);
            if (t == 40) { Vec3 o = hand(c, 1), d = c.aim(o); c.proj().at(o.add(d.scale(10))).face(d).box(.5f, .5f, 20).color(YEL).dmg(13).life(5).pierce().spawn(); c.sound(SoundEvents.GENERIC_EXPLODE, .6f, 1.8f); }
        }, (p, t, m) -> { boolean drawn = t >= 38; p.r("armR", drawn ? -1.57f : 0, 0, drawn ? 0 : -.2f).r("head", .2f, 0, 0).o("armR", 0, 0, t > 40 && t < 44 ? 1 : 0); })
            .when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ FEISTY FOUR
    /** Ed: pink ogre, Fierceness and Toughness — punches and ground pounds. */
    static CharacterDef ed() {
        return CharacterDef.of("uty_ed", "Ed", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(110).keep(3).rest(16).speed(.3f)
            .egg(0xff8ab0, 0x1a1a1a).size(.9f, 1.9f).scale(1.1f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 11, 10, 8, 0xff8ab0)
                .box("vest", "body", 0, 0, -3.6f, 9, 8, 1, 0x1a1a1a).box("tie", "vest", 0, 2, -.6f, 2, 2, .3f, TEAL)
                .box("head", "body", 0, 7, -1, 6, 5, 5, 0xff8ab0)
                .box("brow", "head", 0, 1.5f, -2.6f, 6, 1.2f, .5f, 0x3a2a2a).box("tooth", "head", 1, -2, -2.6f, 1, 1.2f, .3f, 0xffffff)
                .box("hat", "head", 0, 3.2f, 0, 4, 2, 4, 0x1a1a1a).box("hatBand", "hat", 0, -.6f, 0, 4.2f, .5f, 4.2f, TEAL)
                .limb("armL", "body", -7, 4, 0, 4, 9, 4, 0xff8ab0).limb("armR", "body", 7, 4, 0, 4, 9, 4, 0xff8ab0)
                .limb("legL", null, -2, 5, 0, 1.6f, 5, 1.6f, 0x1a1a1a).limb("legR", null, 2, 5, 0, 1.6f, 5, 1.6f, 0x1a1a1a))
            .attacks(atk("ed_haymaker", 40, (c, t) -> {
                if (t == 14) { c.aoe(c.pos().add(c.fwd().scale(1.6)), 1.8, 8, 1.6); c.sound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2f, .6f); }
            }, (p, t, m) -> { float w = Math.min(1, t / 12f), hit = t >= 12 && t < 20 ? 1 : 0; p.r("armR", -1.57f * hit + .6f * w * (1 - hit), 0, 0).r("body", 0, (-.5f * w + 1f * hit) * (t < 30 ? 1 : 0), 0); }),
              atk("ed_ground_pound", 50, (c, t) -> {
                if (t == 16) { c.aoe(c.pos(), 3.5, 7, 1); ring(c, c.pos().add(0, .3, 0), 16, .45, 0, .45f, 0xc8a070, 4, 0, 18); c.sound(SoundEvents.ANVIL_LAND, 1, .5f); c.particle(ParticleTypes.EXPLOSION, c.pos(), 3, .8, 0); }
              }, (p, t, m) -> { float up = Math.min(1, t / 14f), dn = t >= 14 ? 1 : 0; p.r("armL", -3f * up * (1 - dn) + .3f * dn, 0, .3f).r("armR", -3f * up * (1 - dn) + .3f * dn, 0, -.3f).o("body", 0, -2 * dn * (t < 26 ? 1 : 0), 0); }),
              atk("ed_rock_toss", 46, (c, t) -> {
                if (t == 18) { Vec3 o = c.eye().add(0, .8, 0), d = c.tpos().subtract(o); double fl = 16; c.proj().at(o).vel(new Vec3(d.x / fl, d.y / fl + .03 * fl * .5, d.z / fl)).gravity(.03f).box(1.2f, 1.2f, 1.2f).color(0x8a7a6a).dmg(8).life(24).spawn(); }
              }, (p, t, m) -> { float k = t < 16 ? t / 16f : Math.max(0, 1 - (t - 16) / 6f); p.r("armL", -3f * k, 0, .2f).r("armR", -3f * k, 0, -.2f); })).build();
    }
    /** Moray: fish musketeer — fencing thrusts and a guitar flourish. */
    static CharacterDef moray() {
        return CharacterDef.of("uty_moray", "Moray", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(90).keep(4).rest(16).speed(.34f)
            .egg(0x3a8ad0, TEAL).size(.6f, 1.8f).scale(1f)
            .model(hum(0x6a6a7a, 0x3a8ad0, 0x2a2a4a, 0x6a6a7a)
                .box("hairM", "head", -1, 1.5f, -1, 8, 3, 6, 0x1a3a6a)
                .glow("eyeR", "head", 1.6f, .5f, -3.6f, 1.2f, 1.4f, .3f, 0xffffff)
                .box("beret", "head", 0, 4, 0, 7.5f, 1.4f, 7.5f, 0x2a2a6a).box("beretBand", "beret", 0, 0, 0, 7.6f, .5f, 7.6f, TEAL)
                .box("finL", "head", -4, 0, 1, .4f, 3, 3, 0x2a6ab0).box("finR", "head", 4, 0, 1, .4f, 3, 3, 0x2a6ab0)
                .box("rapier", "armR", 0, -7.5f, -4, .3f, .3f, 9, 0xe0e0e8))
            .attacks(atk("moray_fleche", 44, (c, t) -> {
                if (t == 12) c.dash(c.aim(c.pos()).multiply(1.3, 0, 1.3));
                if (t == 15) { Vec3 o = hand(c, 1); seg(c, o, o.add(c.fwd().scale(3)), .15f, 0xe0e0e8, 8, 0, 4); c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, 1.5f); }
              }, (p, t, m) -> { float w = Math.min(1, t / 12f), th = t >= 12 && t < 22 ? 1 : 0; p.r("armR", -1.57f, 0, 0).o("armR", 0, 0, -3 * th + 1.5f * w * (1 - th)).r("legL", -.8f * th, 0, 0); }),
              atk("moray_flurry", 50, (c, t) -> {
                if (t >= 10 && t < 40 && t % 4 == 0) { Vec3 o = hand(c, 1); dart(c, o, c.aim(o).add(c.rnd.nextGaussian() * .08, c.rnd.nextGaussian() * .05, 0).scale(.8), .12f, 1f, 0xe0e0e8, 3, 0, 14); }
              }, (p, t, m) -> p.r("armR", -1.57f, (t % 4 < 2 ? .2f : -.2f), 0).o("armR", 0, 0, t % 4 < 2 ? -1.5f : 0)),
              atk("moray_serenade", 50, (c, t) -> {
                if (t >= 10 && t < 44 && t % 5 == 0) { ring(c, c.eye(), 6, .3, t * .3, .3f, TEAL, 3, 2, 26); c.particle(ParticleTypes.NOTE, c.eye(), 2, .4, 1); }
              }, (p, t, m) -> p.r("armL", -1f, .6f, 0).r("armR", -1f + (t % 5 < 2 ? .3f : 0), -.4f, 0))).build();
    }
    /** Ace: top-hat gunslinger in a tux — card throws and a precise shot. */
    static CharacterDef ace() {
        return CharacterDef.of("uty_ace", "Ace", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(90).keep(7).rest(16).speed(.32f)
            .egg(0x8a8a8a, 0x1a1a1a).size(.6f, 1.9f).scale(1f)
            .model(hum(0x1a1a1a, 0x8a8a8a, 0x1a1a1a, 0x1a1a1a)
                .box("shirtA", "body", 0, 1, -2.1f, 2.4f, 5, .3f, 0xffffff)
                .box("bandanna", "head", 0, -1.5f, -3.6f, 7.4f, 3, .4f, 0x6a1a1a)
                .glow("eyeL", "head", -1.6f, 1, -3.6f, 1.2f, 1.2f, .3f, 0xffffff)
                .box("topHat", "head", 0, 6, 0, 5, 5, 5, 0x1a1a1a).box("topBrim", "topHat", 0, -2.4f, 0, 8, .4f, 8, 0x1a1a1a)
                .box("topBand", "topHat", 0, -1.6f, 0, 5.2f, .7f, 5.2f, TEAL))
            .attacks(atk("ace_card_fan", 50, (c, t) -> {
                if (t == 12 || t == 24 || t == 36) { fan(c, hand(c, 1), 5, 50, .7, .25f, 0xf8f8f8, 4, 0, 20); c.sound(SoundEvents.BOOK_PAGE_TURN, 1, 1.6f); }
              }, (p, t, m) -> { float k = (t % 12) / 12f; p.r("armR", -1.2f, -1f + 2f * k, 0); }),
              atk("ace_deadeye", 50, (c, t) -> {
                if (t < 26 && t % 5 == 0) c.particle(ParticleTypes.CRIT, c.tpos(), 3, .2, 0);
                if (t == 28) { Vec3 o = hand(c, 1); shot(c, o, c.aim(o).scale(1.4), .3f, 0xe0e0e8, 9, 0, 20); c.sound(SoundEvents.CROSSBOW_SHOOT, 1, 1.1f); }
              }, (p, t, m) -> p.r("armR", -1.57f, 0, 0).r("head", .1f, -.2f, 0)),
              atk("ace_hat_trick", 50, (c, t) -> {
                if (t == 16) for (int i = 0; i < 3; i++) { Vec3 at = around(c.tpos(), 3, i * 2.09, 1.5); c.proj().at(at).box(.6f, .6f, .6f).color(0xd01a2a).dmg(0).life(12).spawn(); ring(c, at, 6, .2, i, .25f, 0xf8f8f8, 3, 10, 30); }
              }, (p, t, m) -> p.r("armR", -2.8f, 0, 0).o("topHat", 0, t > 10 && t < 22 ? 4 : 0, 0))).build();
    }
    /** Mooch: squirrel pickpocket — steals, flings junk, scampers. */
    static CharacterDef mooch() {
        return CharacterDef.of("uty_mooch", "Mooch", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(70).keep(5).rest(14).speed(.4f)
            .egg(0x9a9aa8, 0x3a8a3a).size(.5f, 1.3f).scale(.85f)
            .model(eyes(hum(0x3a8a3a, 0x9a9aa8, 0x3a3a3a, 0x3a8a3a), 0x111111, .8f)
                .box("shirtM", "body", 0, 0, -2.1f, 3, 6, .3f, PURP)
                .box("scarfM", "head", 0, -2.8f, 0, 7.4f, 1.6f, 7.4f, 0x3a8a3a)
                .box("hairMo", "head", 0, 3, -2.5f, 5, 1.5f, 2.5f, PURP)
                .box("hatM", "head", 0, 4.5f, 0, 8, 2, 8, 0x2a6a2a).box("hatMBand", "hatM", 0, -.8f, 0, 8.2f, .5f, 8.2f, TEAL)
                .box("earL", "hatM", -2.5f, 1.8f, 0, 1.2f, 2, 1, 0x9a9aa8).box("earR", "hatM", 2.5f, 1.8f, 0, 1.2f, 2, 1, 0x9a9aa8)
                .box("tail", "body", 0, 1, 3.5f, 3, 9, 3, 0x9a9aa8))
            .attacks(atk("mooch_pickpocket", 40, (c, t) -> {
                if (t == 8) c.dash(c.aim(c.pos()).multiply(1.2, 0, 1.2));
                if (t == 12 && c.target.distanceTo(c.me) < 2.5) { c.hit(c.target, 3); eff(c, MobEffects.WEAKNESS, 100, 0); c.me.heal(4); c.sound(SoundEvents.ITEM_PICKUP, 1, 1.4f); }
                if (t == 16) c.dash(c.aim(c.pos()).scale(-1.1).add(0, .3, 0));
              }, (p, t, m) -> p.r("armR", t > 8 && t < 14 ? -1.57f : 0, 0, 0).r("tail", wave(t, .9f) * .4f, 0, 0)),
              atk("mooch_junk_toss", 50, (c, t) -> {
                if (t >= 10 && t < 40 && t % 6 == 0) {
                    int[] cols = {0xd0d0d8, 0xffd400, 0x8a5a2a, 0x6aaaff}; Vec3 o = hand(c, 1).add(0, .5, 0), d = c.tpos().subtract(o); double fl = 14;
                    c.proj().at(o).vel(new Vec3(d.x / fl, d.y / fl + .025 * fl * .5, d.z / fl)).gravity(.025f).box(.4f, .4f, .4f).color(cols[(t / 6) % 4]).dmg(3).life(22).spawn();
                }
              }, (p, t, m) -> { float k = (t % 6) / 6f; p.r("armR", -2.6f + 2.4f * k, 0, 0).r("tail", wave(t, .9f) * .4f, 0, 0); }),
              atk("mooch_scamper", 36, (c, t) -> {
                if (t % 8 == 0) c.dash(c.right().scale((t / 8) % 2 == 0 ? .9 : -.9).add(0, .2, 0));
                if (t % 8 == 4) shot(c, c.eye(), c.aim(c.eye()).scale(.8), .25f, 0x8a5a2a, 3, 0, 18);
              }, (p, t, m) -> p.r("legL", wave(t, 1.5f), 0, 0).r("legR", -wave(t, 1.5f), 0, 0).r("tail", .6f, 0, 0))).build();
    }

    // ============================================================ GUARDENER (Steamworks miniboss: gardening robot)
    static CharacterDef guardener() {
        return CharacterDef.of("uty_guardener", "Guardener", Faction.ROYAL_SCIENCE).hp(130).boss().keep(5).rest(14).speed(.28f)
            .egg(0x4a8a4a, 0x8a8a8a).size(.9f, 1.9f).scale(1.05f)
            .model(new ModelDef()
                .box("body", null, 0, 11, 0, 10, 10, 7, 0x4a8a4a)
                .box("panel", "body", 0, 0, -3.6f, 6, 5, .3f, 0x2a5a2a)
                .box("head", "body", 0, 7.5f, 0, 7, 5, 6, 0x8a8a8a)
                .glow("visor", "head", 0, .5f, -3.1f, 5.5f, 1.4f, .3f, 0x60ff60)
                .box("pot", "head", 0, 3.5f, 0, 5, 2, 5, 0xa0502a).box("sprout", "pot", 0, 2, 0, 1, 2.5f, 1, 0x40c040)
                .limb("armL", "body", -6.2f, 4, 0, 2.4f, 8, 2.4f, 0x8a8a8a).limb("armR", "body", 6.2f, 4, 0, 2.4f, 8, 2.4f, 0x8a8a8a)
                .box("shears", "armR", 0, -9, -1.5f, 1.4f, .6f, 5, 0xd0d0d8).box("can", "armL", 0, -9, 0, 2.4f, 2.4f, 2.4f, 0x5a8acd)
                .limb("legL", null, -2.5f, 6, 0, 3, 6, 3, 0x5a5a5a).limb("legR", null, 2.5f, 6, 0, 3, 6, 3, 0x5a5a5a))
            .attacks(gdShears(), gdWater(), gdSeeds(), gdVines()).build();
    }
    static Attack gdShears() {
        return atk("guardener_hedge_shears", 50, (c, t) -> {
            if (t >= 14 && t < 34 && t % 5 == 4) { c.aoe(c.pos().add(c.fwd().scale(1.8)), 1.8, 5, .5); c.sound(SoundEvents.SHEEP_SHEAR, 1, .9f); c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(c.fwd().scale(1.8)).add(0, 1, 0), 1, 0, 0); }
            if (t == 10) c.dash(c.aim(c.pos()).multiply(.8, 0, .8));
        }, (p, t, m) -> p.r("armR", -1.57f, (t % 5 < 2 ? .3f : -.3f), 0).o("shears", t % 5 < 2 ? .4f : 0, 0, 0));
    }
    static Attack gdWater() {
        return atk("guardener_watering_can", 60, (c, t) -> {
            if (t >= 12 && t < 48 && t % 2 == 0) {
                Vec3 o = hand(c, -1).add(0, .6, 0), d = c.aim(o).add(c.rnd.nextGaussian() * .1, .2, c.rnd.nextGaussian() * .1);
                c.proj().at(o).vel(d.scale(.55)).gravity(.03f).box(.2f, .3f, .2f).color(0x5ab0ff).dmg(2).life(24).spawn();
                c.particle(ParticleTypes.SPLASH, o, 2, .1, .02);
            }
        }, (p, t, m) -> p.r("armL", -1.57f, .3f, 0).r("can", .5f, 0, 0));
    }
    static Attack gdSeeds() {
        return atk("guardener_seed_planter", 70, (c, t) -> {
            if (t >= 8 && t < 32 && t % 4 == 0) { Vec3 o = c.eye().add(0, .8, 0); c.proj().at(o).vel(c.aim(o).scale(.4).add(c.rnd.nextGaussian() * .1, .3, c.rnd.nextGaussian() * .1)).gravity(.03f).box(.25f, .25f, .25f).color(0x8a5a2a).dmg(2).life(26).spawn(); }
            if (t >= 30 && t < 62 && t % 4 == 2) pillar(c, c.target.position().add(c.rnd.nextGaussian() * 2, 0, c.rnd.nextGaussian() * 2), 1.6f, .5f, 0x40c040, 5, 10, 18);
        }, (p, t, m) -> p.o("pot", 0, t < 32 && t % 4 < 2 ? .5f : 0, 0).r("head", -.2f, 0, 0));
    }
    /** Phase 2: overgrown — thorny vines lash out in a ring. */
    static Attack gdVines() {
        return atk("guardener_overgrowth", 60, (c, t) -> {
            if (t == 14 || t == 32) for (int i = 0; i < 8; i++) { Vec3 o = c.pos().add(0, .4, 0); seg(c, o, around(o, 5.5, i * Math.PI / 4 + t, 0), .4f, 0x2a8a2a, 6, 8, 14); }
        }, (p, t, m) -> { float s = env(t, 10, 60, 10); p.r("armL", 0, 0, 1.2f * s).r("armR", 0, 0, -1.2f * s).o("sprout", 0, 2 * s, 0); })
            .when(m -> m.hpFrac() < .5f);
    }

    // ============================================================ CEROBA (Steamworks / New Home boss)
    static CharacterDef ceroba() {
        return CharacterDef.of("uty_ceroba", "Ceroba", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(260).boss().keep(8).rest(12).speed(.32f)
            .egg(0xffb040, 0x6a2a8a).size(.6f, 1.9f).scale(1.05f)
            .model(eyes(hum(0xf0e8e0, 0xffb040, 0xffb040, 0xf0e8e0), 0x2a1a1a, .5f)
                .box("snout", "head", 0, -1.5f, -4.2f, 2.5f, 2, 2, 0xfff0e0)
                .box("earL", "head", -2.4f, 4.5f, 0, 2, 3, 1, 0xffb040).box("earR", "head", 2.4f, 4.5f, 0, 2, 3, 1, 0xffb040)
                .box("scarfC", "body", 0, 3.5f, 0, 7.6f, 1.6f, 4.6f, 0x6a2a8a)
                .box("obi", "body", 0, -1.5f, 0, 7.4f, 1.4f, 4.4f, 0x6a2a8a)
                .box("tail", "body", 0, -2, 3.5f, 3, 3, 6, 0xffb040).box("tailTip", "tail", 0, 0, 3.5f, 2.6f, 2.6f, 1.5f, 0xffffff)
                .box("staff", "armR", 0, -7, -1, .6f, 14, .6f, 0x8a5a2a)
                .glow("bell", "staff", 0, 7.5f, 0, 1.8f, 1.8f, 1.8f, YEL)
                .ghost("mask", "head", 0, 0, -4.1f, 6, 6, .4f, 0xfff0a0, 0))
            .attacks(ceLight(), ceShotgun(), cePetals(), ceDiamond(), ceFlowerCircle(), ceBells(), ceBlackHole(), ceStaff(),
                ceLanterns(), ceCrest(), ceGeyser(), ceTwirl()).build();
    }
    static void staffPose(Pose p, float t, float s) { p.r("armR", -1.8f * s, 0, -.3f * s).r("tail", 0, wave(t, .3f) * .4f, 0); }
    static boolean p1(AttackCtx c) { return c.me.hpFrac() >= .66f; }
    /** Three large balls of light rise, swerve down and burst into shards. */
    static Attack ceLight() {
        return atk("ceroba_light_bullets", 80, (c, t) -> {
            if (t % 18 == 6 && t < 60) {
                Vec3 at = c.target.position().add(c.right().scale((t / 18 - 1) * 2.5)).add(0, 4, 0);
                c.proj().at(at).vel(new Vec3(0, -.2, 0)).box(1.1f, 1.1f, 1.1f).color(0xfff6c0).dmg(6).warn(8).life(16).spawn();
            }
            if (t % 18 == 0 && t >= 18 && t < 78) ring(c, c.target.position().add(c.right().scale((t / 18 - 2) * 2.5)).add(0, .8, 0), 8, .35, t, .3f, 0xfff6c0, 3, 2, 18);
        }, (p, t, m) -> staffPose(p, t, env(t, 6, 80, 12)));
    }
    /** "Shotgun": walls of flowers from random sides, each with one gap. */
    static Attack ceShotgun() {
        return atk("ceroba_flower_walls", 80, (c, t) -> {
            if (t % 16 == 4 && t < 68) {
                double a = c.rnd.nextInt(4) * Math.PI / 2; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a)), side = new Vec3(-d.z, 0, d.x);
                int gap = c.rnd.nextInt(7) - 3;
                for (int k = -4; k <= 4; k++) if (Math.abs(k - gap) > 0)
                    shot(c, c.tpos().subtract(d.scale(7)).add(side.scale(k * .8)), d.scale(.45), .45f, PINK, 4, 6, 32);
                c.sound(SoundEvents.CHERRY_LEAVES_BREAK, 1, 1.2f);
            }
        }, (p, t, m) -> { staffPose(p, t, 1); p.r("armL", -1.3f, 0, .6f); }).when(AuYellowFangames::phase1);
    }
    static boolean phase1(com.undeltarune.entity.UtMob m) { return m.hpFrac() >= .66f; }
    /** Cherry blossom petals flood the ground, forcing the target upward. */
    static Attack cePetals() {
        return atk("ceroba_blossom_pile", 70, (c, t) -> {
            if (t < 60 && t % 2 == 0) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 4, 4, c.rnd.nextGaussian() * 4);
                c.proj().at(at).vel(new Vec3(Math.sin(t * .3) * .1, -.2, Math.cos(t * .3) * .1)).box(.3f, .1f, .3f).color(PINK).dmg(2).warn(4).life(26).spawn();
                c.particle(ParticleTypes.CHERRY_LEAVES, at, 2, .4, 0);
            }
            if (t == 30) c.proj().at(c.target.position().add(0, .3, 0)).box(6, .6f, 6).color(PINK).dmg(3).warn(10).life(30).pierce().spawn();
        }, (p, t, m) -> { float s = env(t, 8, 70, 10); p.r("armL", 0, 0, 1.2f * s).r("armR", -2f * s, 0, -.5f); }).when(AuYellowFangames::phase1);
    }
    /** Paralyzing diamond, then red beams lock onto where the target froze. */
    static Attack ceDiamond() {
        return atk("ceroba_diamond_beams", 80, (c, t) -> {
            Vec3 tp = c.target.position();
            if (t == 6) {
                for (int i = 0; i < 4; i++) seg(c, around(tp, 1.6, i * Math.PI / 2, .1), around(tp, 1.6, (i + 1) * Math.PI / 2, .1), .1f, 0xffffff, 0, 0, 16);
                eff(c, MobEffects.MOVEMENT_SLOWDOWN, 30, 6); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, .6f);
            }
            if (t == 14 || t == 38 || t == 62) {
                Vec3 f = c.fwd(), r = c.right(); int k = (t - 14) / 24;
                if (k != 1) c.proj().at(tp.add(0, 1, 0)).face(f).box(.7f, 8, 16).color(0xff3030).dmg(7).warn(14).life(20).pierce().spawn();
                if (k != 0) c.proj().at(tp.add(0, 1, 0)).face(r).box(.7f, 8, 16).color(0xff3030).dmg(7).warn(14).life(20).pierce().spawn();
            }
        }, (p, t, m) -> { staffPose(p, t, 1); p.r("armL", -1.57f, 0, 0); });
    }
    /** Five flowers circle the target, lock in and close to an explosion. */
    static Attack ceFlowerCircle() {
        return atk("ceroba_flower_circle", 70, (c, t) -> {
            if (t % 22 == 4 && t < 66) {
                Vec3 tp = c.tpos();
                for (int i = 0; i < 6; i++) { Vec3 at = around(tp, 3.5, i * Math.PI / 3, 0); c.proj().at(at).vel(tp.subtract(at).scale(1 / 18.0)).box(.5f, .5f, .5f).color(PINK).dmg(4).warn(8).life(18).spawn(); }
            }
            if (t % 22 == 0 && t >= 22) { Vec3 g = c.tpos(); ringBO(c, g, (t / 22) % 2 == 0, 10); c.sound(SoundEvents.FIREWORK_ROCKET_BLAST, .7f, 1.4f); }
        }, (p, t, m) -> { staffPose(p, t, 1); p.r("armL", -1.2f, 0, 1f); });
    }
    /** Giant bells bounce across and burst into blue or orange rings. */
    static Attack ceBells() {
        return atk("ceroba_bells", 80, (c, t) -> {
            if (t % 20 == 4 && t < 64) {
                int s = (t / 20) % 2 == 0 ? 1 : -1; Vec3 r = c.right(), at = c.tpos().add(r.scale(s * 6)).add(0, 2, 0);
                c.proj().at(at).vel(r.scale(-s * .3).add(0, .1, 0)).gravity(.02f).box(1.2f, 1.4f, 1.2f).color(YEL).dmg(6).warn(6).life(18).spawn();
                c.sound(SoundEvents.BELL_BLOCK, 1, 1.2f);
            }
            if (t % 20 == 2 && t >= 22) { ringBO(c, c.tpos().subtract(0, .4, 0), (t / 20) % 2 == 0, 12); }
        }, (p, t, m) -> { staffPose(p, t, 1); p.o("bell", 0, 0, wave(t, 1.2f) * .5f); });
    }
    /** A black hole drags the target while flower spirals feed into it. */
    static Attack ceBlackHole() {
        return atk("ceroba_black_hole", 90, (c, t) -> {
            Vec3 hole = c.pos().add(c.fwd().scale(3)).add(0, 1, 0);
            if (t == 6) c.proj().at(hole).box(1.2f, 1.2f, 1.2f).color(0x0a0010).dmg(8).warn(10).life(80).pierce().spawn();
            if (t >= 14 && t < 80) {
                if (t % 2 == 0) { Vec3 d = hole.subtract(c.tpos()).normalize().scale(.12); c.target.push(d.x, 0, d.z); c.target.hurtMarked = true; }
                if (t % 6 == 0) for (int i = 0; i < 3; i++) { Vec3 at = around(hole, 6, t * .15 + i * 2.09, 0); shot(c, at, hole.subtract(at).normalize().scale(.3).add(new Vec3(-(at.z - hole.z), 0, at.x - hole.x).normalize().scale(.12)), .35f, PINK, 3, 2, 24); }
                c.particle(ParticleTypes.REVERSE_PORTAL, hole, 4, .4, .1);
            }
        }, (p, t, m) -> { float s = env(t, 8, 90, 12); p.r("armR", -2.8f * s, 0, 0).r("armL", -2.8f * s, 0, 0).r("tail", 0, t * .2f, 0); });
    }
    /** Twin staffs stab diagonally, turn orange and spin (move through them). */
    static Attack ceStaff() {
        return atk("ceroba_staff_spin", 80, (c, t) -> {
            Vec3 tp = c.target.position().add(0, 1, 0);
            if (t == 6) for (int s = -1; s <= 1; s += 2) seg(c, tp.add(c.right().scale(s * 3)).add(0, 2, 0), tp.add(c.right().scale(-s * 3)).add(0, -1, 0), .3f, 0xf0f0f0, 6, 10, 10);
            if (t >= 20 && t < 66 && t % 3 == 0) for (int s = -1; s <= 1; s += 2) {
                double a = s * t * .15; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a)).scale(3);
                c.proj().at(tp).face(d).box(.3f, .3f, 6).dmg(5).life(3).pierce().orange().spawn();
            }
        }, (p, t, m) -> { p.r("armR", -1.57f, t > 20 && t < 66 ? t * .3f : 0, 0); }).when(m -> m.hpFrac() < .8f);
    }
    /** Phase 2: chochin lanterns float above and rain fireballs. */
    static Attack ceLanterns() {
        return atk("ceroba_chochin_lanterns", 90, (c, t) -> {
            if (t >= 6 && t < 80) for (int i = 0; i < 3; i++) {
                Vec3 at = around(c.tpos(), 4, i * 2.09 + t * .05, 3.5);
                if (t % 4 == 0) c.particle(ParticleTypes.FLAME, at, 2, .2, .01);
                if (t % 15 == 5 + i * 5) shot(c, at, c.aim(at).scale(.45), .35f, FIRE, 4, 2, 24);
            }
            if (t == 6) for (int i = 0; i < 3; i++) c.proj().at(around(c.tpos(), 4, i * 2.09, 3.5)).box(.9f, 1.2f, .9f).color(YEL).dmg(0).life(74).spawn();
        }, (p, t, m) -> { staffPose(p, t, 1); p.o("mask", 0, 0, 0); }).when(m -> m.hpFrac() < .66f);
    }
    /** Phase 2: Founder's Crests slide along the flanks and spit fire. */
    static Attack ceCrest() {
        return atk("ceroba_founders_crest", 80, (c, t) -> {
            Vec3 r = c.right(), f = c.fwd();
            if (t >= 8 && t < 70 && t % 8 == 0) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = c.tpos().add(r.scale(s * 5)).add(f.scale(Math.sin(t * .1) * 4)).add(0, 1, 0);
                c.proj().at(at).box(1.2f, 1.2f, .3f).color(YEL).dmg(0).life(8).spawn();
                shot(c, at, c.aim(at).scale(.5), .35f, FIRE, 4, 0, 20);
            }
        }, (p, t, m) -> { staffPose(p, t, 1); p.r("armL", -1.57f, .5f, 0); }).when(m -> m.hpFrac() < .66f);
    }
    /** Phase 3: geysers erupt after a warning. */
    static Attack ceGeyser() {
        return atk("ceroba_geysers", 70, (c, t) -> {
            if (t % 14 == 2 && t < 60) {
                Vec3 g = c.target.position().add(c.target.getDeltaMovement().scale(8));
                pillar(c, g, 7, 1.8f, FIRE, 9, 14, 24); c.particle(ParticleTypes.LAVA, g, 6, .5, .1);
            }
            if (t % 14 == 2 && t < 60) c.sound(SoundEvents.FIRECHARGE_USE, 1, .6f);
        }, (p, t, m) -> { float k = (t % 14) / 14f; p.r("armR", -2.8f + 2.6f * k, 0, 0).o("body", 0, -k, 0); }).when(m -> m.hpFrac() < .4f);
    }
    /** Phase 4: twirls the staff overhead into a wave of fireballs, ending in twin geysers (Chujin's mask aglow). */
    static Attack ceTwirl() {
        return atk("ceroba_final_twirl", 90, (c, t) -> {
            if (t >= 10 && t < 56 && t % 3 == 0) {
                double a = t * .4; Vec3 o = c.pos().add(0, 2.5, 0);
                shot(c, o, new Vec3(Math.cos(a) * .4, -.05, Math.sin(a) * .4), .4f, FIRE, 4, 0, 30);
            }
            if (t == 64) for (int s = -1; s <= 1; s += 2) pillar(c, c.target.position().add(c.right().scale(s * 1.8)), 8, 2.2f, FIRE, 10, 10, 20);
        }, (p, t, m) -> { p.r("armR", -3f, t * .6f, 0).o("mask", 0, 0, 0).r("tail", 0, wave(t, .5f) * .6f, 0); if (t > 58) p.r("armR", 1.4f, 0, 0); })
            .when(m -> m.hpFrac() < .2f);
    }

    // ============================================================ AXIS (Steamworks boss robot, Model 014)
    static CharacterDef axis() {
        return CharacterDef.of("uty_axis", "Axis", Faction.ROYAL_SCIENCE).hp(200).boss().keep(7).rest(14).speed(.26f)
            .egg(0x5a6a8a, YEL).size(1f, 2.3f).scale(1.15f)
            .model(new ModelDef()
                .box("body", null, 0, 13, 0, 12, 12, 8, 0x5a6a8a)
                .box("chest", "body", 0, 1, -4.1f, 8, 6, .3f, 0x3a4a6a)
                .glow("core", "chest", 0, 0, -.2f, 2.5f, 2.5f, .2f, YEL)
                .box("head", "body", 0, 8.5f, 0, 8, 5, 7, 0x6a7a9a)
                .glow("visor", "head", 0, .3f, -3.6f, 6.5f, 1.6f, .3f, YEL)
                .box("antenna", "head", 0, 3.5f, 0, .5f, 2.5f, .5f, 0x3a3a3a)
                .limb("armL", "body", -7.5f, 5, 0, 3.4f, 11, 3.4f, 0x4a5a7a).limb("armR", "body", 7.5f, 5, 0, 3.4f, 11, 3.4f, 0x4a5a7a)
                .box("fistL", "armL", 0, -11.5f, 0, 4, 3, 4, 0x3a4a6a).box("fistR", "armR", 0, -11.5f, 0, 4, 3, 4, 0x3a4a6a)
                .limb("legL", null, -3, 7, 0, 4, 7, 4, 0x3a4a6a).limb("legR", null, 3, 7, 0, 4, 7, 4, 0x3a4a6a))
            .attacks(axPellets(), axBounce(), axSlam(), axLaser(), axShield(), axOverdrive()).build();
    }
    static Attack axPellets() {
        return atk("axis_energy_pellets", 64, (c, t) -> {
            if (t >= 10 && t < 54 && t % 4 == 0) { Vec3 o = hand(c, (t / 4) % 2 == 0 ? 1 : -1).add(0, .2, 0); shot(c, o, c.aim(o).scale(.6), .35f, YEL, 4, 2, 30); c.sound(SoundEvents.BLAZE_SHOOT, .5f, 1.6f); }
        }, (p, t, m) -> { boolean l = (t / 4) % 2 == 1; p.r("armR", l ? -1.2f : -1.57f, 0, 0).r("armL", l ? -1.57f : -1.2f, 0, 0); });
    }
    /** Energy balls that bounce along the ground toward the target. */
    static Attack axBounce() {
        return atk("axis_bouncing_orbs", 70, (c, t) -> {
            if (t >= 10 && t < 50 && t % 10 == 0) { Vec3 o = c.eye(); c.proj().at(o).vel(c.aim(o).multiply(.35, 0, .35).add(0, .45, 0)).gravity(.04f).box(.7f, .7f, .7f).color(0x6ad8ff).dmg(6).life(40).spawn(); }
        }, (p, t, m) -> p.r("armR", -2.2f + (t % 10 < 3 ? .5f : 0), 0, -.2f));
    }
    static Attack axSlam() {
        return atk("axis_shockwave_slam", 56, (c, t) -> {
            if (t == 20) { c.aoe(c.pos(), 3.5, 9, 1.4); ring(c, c.pos().add(0, .3, 0), 20, .5, 0, .45f, YEL, 4, 0, 18); c.sound(SoundEvents.ANVIL_LAND, 1.2f, .5f); c.particle(ParticleTypes.EXPLOSION, c.pos(), 2, .6, 0); }
        }, (p, t, m) -> { float up = Math.min(1, t / 18f), dn = t >= 18 && t < 32 ? 1 : 0; p.r("armL", -3f * up * (1 - dn), 0, 0).r("armR", -3f * up * (1 - dn), 0, 0).o("body", 0, -2 * dn, 0); });
    }
    /** Visor laser sweep. */
    static Attack axLaser() {
        return atk("axis_visor_laser", 70, (c, t) -> {
            if (t >= 20 && t < 50 && t % 3 == 0) {
                Vec3 o = c.eye(), d = c.aim(o); double a = Math.atan2(d.z, d.x) + (t - 35) * .04; Vec3 dir = new Vec3(Math.cos(a), d.y, Math.sin(a));
                c.proj().at(o.add(dir.scale(9))).face(dir).box(.4f, .4f, 18).color(YEL).dmg(6).warn(1).life(4).pierce().spawn();
            }
            if (t == 18) c.sound(SoundEvents.BEACON_POWER_SELECT, 1, 2);
        }, (p, t, m) -> p.r("head", 0, (t - 35) * .03f, 0).o("visor", 0, 0, t > 12 && t < 52 ? -.4f : 0));
    }
    /** Raises an energy shield, regenerating, while guard drones fire. */
    static Attack axShield() {
        return atk("axis_guard_shield", 70, (c, t) -> {
            if (t == 6) { c.me.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 2)); c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.4f); }
            if (t < 60 && t % 4 == 0) c.particle(ParticleTypes.ELECTRIC_SPARK, c.pos().add(c.fwd().scale(1.2)).add(0, 1.2, 0), 4, .6, .02);
            if (t >= 16 && t < 60 && t % 11 == 0) for (int s = -1; s <= 1; s += 2) { Vec3 o = c.pos().add(c.right().scale(s * 2)).add(0, 2.4, 0); shot(c, o, c.aim(o).scale(.5), .3f, YEL, 3, 2, 26); }
        }, (p, t, m) -> { float s = env(t, 6, 70, 10); p.r("armL", -1.57f * s, .6f * s, 0).r("armR", -1.57f * s, -.6f * s, 0); }).when(m -> m.hpFrac() < .7f);
    }
    /** Phase 2: overdrive — spinning arm cannons spray a spiral. */
    static Attack axOverdrive() {
        return atk("axis_overdrive", 80, (c, t) -> {
            if (t >= 10 && t < 70 && t % 2 == 0) for (int s = 0; s < 2; s++) { double a = t * .3 + s * Math.PI; shot(c, c.pos().add(0, 1.4, 0), new Vec3(Math.cos(a) * .5, 0, Math.sin(a) * .5), .3f, YEL, 3, 0, 26); }
        }, (p, t, m) -> p.r("armL", 0, 0, 1.57f).r("armR", 0, 0, -1.57f).r("body", 0, t * .3f, 0)).when(m -> m.hpFrac() < .4f);
    }

    // ============================================================ CHUJIN (the old inventor; appears via his creations)
    static CharacterDef chujin() {
        return CharacterDef.of("uty_chujin", "Chujin", Faction.ROYAL_SCIENCE).att(Attitude.NEUTRAL).hp(50).keep(8).rest(20).speed(.22f)
            .egg(0x5a8a5a, 0xf0e0b0).size(.6f, 1.6f).scale(.9f)
            .model(eyes(hum(0x3a5a8a, 0x5a8a5a, 0x2a2a3a, 0x3a5a8a), 0x111111, .5f)
                .box("shell", "body", 0, 0, 2.6f, 8, 8, 2, 0x4a6a3a)
                .box("goggles", "head", 0, 1.2f, -3.6f, 6, 1.4f, .5f, 0x8a5a2a)
                .box("beard", "head", 0, -2.5f, -3.4f, 4, 2.5f, .6f, 0xf0f0f0)
                .box("wrench", "armR", 0, -8, -1, .8f, .8f, 3, 0x9a9aa8))
            .attacks(atk("chujin_drone_swarm", 70, (c, t) -> {
                if (t == 10) for (int i = 0; i < 3; i++) c.proj().at(around(c.pos(), 1.5, i * 2.09, 2)).box(.6f, .4f, .6f).color(0x8a8a98).dmg(0).life(56).spawn();
                if (t >= 20 && t < 66 && t % 8 == 0) for (int i = 0; i < 3; i++) { Vec3 o = around(c.pos(), 1.5, i * 2.09, 2); shot(c, o, c.aim(o).scale(.5), .2f, YEL, 2, 2, 24); }
              }, (p, t, m) -> p.r("armR", -2f, 0, -.3f).r("head", -.2f, 0, 0)),
              atk("chujin_steam_vent", 50, (c, t) -> {
                if (t == 14) { c.aoe(c.pos(), 3, 4, 1.2); c.particle(ParticleTypes.CLOUD, c.pos().add(0, .5, 0), 40, 1.2, .1); c.sound(SoundEvents.FIRE_EXTINGUISH, 1, .6f); }
              }, (p, t, m) -> p.r("armL", -1f, 0, .4f).r("armR", -1f, 0, -.4f).o("body", 0, t < 14 ? -t * .05f : 0, 0))).build();
    }

    // ============================================================ FLOWEY (Undertale Yellow final boss)
    static CharacterDef flowey() {
        return CharacterDef.of("uty_flowey", "Flowey (Yellow)", Faction.FLOWEY_CHAOS).hp(320).boss().keep(9).rest(10).speed(.2f)
            .egg(YEL, 0x2a8a2a).size(.9f, 2.2f).scale(1.2f)
            .model(new ModelDef()
                .box("body", null, 0, 6, 0, 1.6f, 12, 1.6f, 0x2a8a2a)
                .box("leafL", "body", -2.5f, -2, 0, 4, .4f, 2, 0x3aaa3a).box("leafR", "body", 2.5f, 0, 0, 4, .4f, 2, 0x3aaa3a)
                .box("head", "body", 0, 9, 0, 5, 5, 2, 0xfff0d0)
                .box("petals", "head", 0, 0, .6f, 10, 10, .6f, YEL)
                .glow("eyeL", "head", -1.2f, .8f, -1.1f, .8f, 1.6f, .2f, 0x111111).glow("eyeR", "head", 1.2f, .8f, -1.1f, .8f, 1.6f, .2f, 0x111111)
                .box("mouth", "head", 0, -1.2f, -1.1f, 2.6f, .6f, .2f, 0x111111)
                .limb("armL", "body", -1, 0, 0, 1, 10, 1, 0x1a6a1a).limb("armR", "body", 1, 0, 0, 1, 10, 1, 0x1a6a1a)
                .limb("legL", null, -2, 1, 0, 1, 1, 3, 0x1a6a1a).limb("legR", null, 2, 1, 0, 1, 1, 3, 0x1a6a1a))
            .attacks(flPellets(), flVines(), flFlytraps(), flFlame(), flSoulCharge()).build();
    }
    static Attack flPellets() {
        return atk("uty_flowey_friendliness_pellets", 70, (c, t) -> {
            if (t == 8) for (int i = 0; i < 12; i++) { Vec3 at = around(c.tpos(), 4, i * Math.PI / 6, .2); c.proj().at(at).vel(c.tpos().subtract(at).scale(1 / 40.0)).box(.3f, .3f, .3f).color(0xffffff).dmg(5).warn(14).life(46).spawn(); }
            if (t >= 30 && t < 62 && t % 4 == 0) fan(c, c.eye(), 5, 40, .55, .3f, 0xffffff, 4, 0, 28);
        }, (p, t, m) -> p.r("head", 0, 0, wave(t, .3f) * .2f).r("petals", 0, 0, t * .05f).o("mouth", 0, -.3f, 0));
    }
    static Attack flVines() {
        return atk("uty_flowey_vine_eruption", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 4 == 0) pillar(c, c.target.position().add(c.target.getDeltaMovement().scale(6)).add(c.rnd.nextGaussian() * .5, 0, c.rnd.nextGaussian() * .5), 3, .6f, 0x2a8a2a, 6, 10, 18);
        }, (p, t, m) -> p.r("armL", -1.2f + wave(t, .5f) * .4f, 0, .6f).r("armR", -1.2f - wave(t, .5f) * .4f, 0, -.6f).r("head", .2f, 0, 0));
    }
    static Attack flFlytraps() {
        return atk("uty_flowey_flytrap_snap", 60, (c, t) -> {
            if (t % 15 == 5 && t < 55) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = c.tpos().add(c.right().scale(s * 3.5));
                c.proj().at(at).vel(c.right().scale(-s * .25)).box(1, 1.4f, 1.4f).color(0x8a1a3a).dmg(7).warn(8).life(16).spawn();
            }
        }, (p, t, m) -> { float k = (t % 15) < 4 ? 1 : 0; p.r("leafL", 0, 0, -.6f * k).r("leafR", 0, 0, .6f * k); });
    }
    static Attack flFlame() {
        return atk("uty_flowey_flamethrower", 70, (c, t) -> {
            if (t >= 16 && t < 56) { Vec3 o = c.eye(), d = c.aim(o); double a = Math.atan2(d.z, d.x) + Math.sin(t * .15) * .5; shot(c, o, new Vec3(Math.cos(a), d.y, Math.sin(a)).scale(.6), .5f, FIRE, 2, 0, 14); if (t % 3 == 0) c.particle(ParticleTypes.FLAME, o, 4, .1, .05); }
        }, (p, t, m) -> p.o("mouth", 0, 0, 0).r("head", -.3f, (float) Math.sin(t * .15) * .5f, 0).o("mouth", 0, -.6f, 0)).when(m -> m.hpFrac() < .7f);
    }
    /** Phase 2: overflowing with stolen SAVE power — blasts of light and a pellet nova. */
    static Attack flSoulCharge() {
        return atk("uty_flowey_save_power", 90, (c, t) -> {
            if (t < 30 && t % 3 == 0) c.particle(ParticleTypes.END_ROD, c.eye(), 6, .6, .1);
            if (t == 30) for (int i = 0; i < 6; i++) { Vec3 at = around(c.tpos(), 6, i * Math.PI / 3, 2); blaster(c, at, c.tpos().subtract(at), 20, 12, 9, .9f, YEL, 0xffffff, 0xfff6c0); }
            if (t >= 60 && t % 8 == 0) ring(c, c.eye(), 18, .45, t, .35f, 0xffffff, 4, 0, 30);
        }, (p, t, m) -> { float s = env(t, 20, 90, 14); p.r("petals", 0, 0, t * .3f * s).o("body", 0, 2 * s, 0).r("armL", -2.5f * s, 0, .6f).r("armR", -2.5f * s, 0, -.6f); })
            .when(m -> m.hpFrac() < .4f);
    }

    // ============================================================ ZENITH MARTLET (Genocide finale, Flowey-infused)
    static CharacterDef zenith() {
        return CharacterDef.of("uty_zenith_martlet", "Zenith Martlet", Faction.ROYAL_GUARD).hp(360).boss().keep(9).rest(10).speed(.34f)
            .egg(0x1a2a5a, 0x40c040).size(.7f, 1.9f).scale(1.15f)
            .model(birdModel(0x1a2a5a, 0x3a5aaf, 0xff3030)
                .box("vineBand", "body", 0, -1, 0, 7.6f, 1.2f, 5, 0x2a8a2a)
                .box("wingBigL", "armL", -2, -3, 2, 1, 10, 7, 0x2a3a8a).box("wingBigR", "armR", 2, -3, 2, 1, 10, 7, 0x2a3a8a)
                .glow("halo", "head", 0, 5, 0, 6, .4f, 6, 0xfff07a)
                .glow("soul", "body", 0, 1, -2.4f, 1.6f, 1.6f, .3f, 0xff3030))
            .attacks(zmFeatherStorm(), zmHoming(), zmTornado(), zmVineWings(), zmSeeds(), zmDive(), zmZenith()).build();
    }
    static void zFlap(Pose p, float t, float s) { p.r("armL", 0, -.3f * s, (1.3f + wave(t, .7f) * .4f) * s).r("armR", 0, .3f * s, -(1.3f + wave(t, .7f) * .4f) * s).o("body", 0, (1.5f + wave(t, .7f) * .5f) * s, 0); }
    static Attack zmFeatherStorm() {
        return atk("zenith_feather_storm", 70, (c, t) -> {
            if (t >= 10 && t < 60 && t % 3 == 0) { Vec3 at = around(c.tpos(), 7, c.rnd.nextDouble() * 6.28, c.rnd.nextGaussian()); dart(c, at, c.tpos().subtract(at).normalize().scale(.55), .15f, 1.1f, 0x3a5aaf, 5, 8, 28); }
        }, (p, t, m) -> zFlap(p, t, env(t, 8, 70, 10)));
    }
    static Attack zmHoming() {
        return atk("zenith_seeking_feathers", 80, (c, t) -> {
            if (t >= 8 && t < 64 && t % 8 == 0) for (int s = -1; s <= 1; s += 2) { Vec3 o = hand(c, s).add(0, 1, 0); dart(c, o, c.aim(o).scale(.75), .18f, 1.3f, 0xff3030, 6, 4, 26); }
        }, (p, t, m) -> zFlap(p, t * 1.4f, 1));
    }
    static Attack zmTornado() {
        return atk("zenith_wind_tornado", 80, (c, t) -> {
            Vec3 core = c.tpos().add(c.right().scale(Math.sin(t * .08) * 3));
            if (t >= 10 && t < 70 && t % 2 == 0) {
                for (int i = 0; i < 3; i++) { double a = t * .5 + i * 2.09; shot(c, around(core, 1.2, a, (t % 6) * .4 - 1), Vec3.ZERO, .3f, 0xd0e8ff, 3, 2, 4); }
                c.particle(ParticleTypes.CLOUD, core, 3, .6, .05);
                if (t % 6 == 0) { Vec3 d = core.subtract(c.tpos()).normalize().scale(.15); c.target.push(d.x, .05, d.z); c.target.hurtMarked = true; }
            }
        }, (p, t, m) -> { zFlap(p, t * 2, 1); p.r("body", 0, t * .2f, 0); });
    }
    /** Vine-laden wings sweep across in huge arcs. */
    static Attack zmVineWings() {
        return atk("zenith_vine_wings", 60, (c, t) -> {
            if (t == 16 || t == 34) { Vec3 o = c.pos().add(0, 1.2, 0); for (int i = -3; i <= 3; i++) seg(c, o, around(o, 7, Math.atan2(c.fwd().z, c.fwd().x) + i * .3, (t == 16 ? -.5 : .8)), .35f, 0x2a8a2a, 7, 6, 10); c.sound(SoundEvents.PHANTOM_FLAP, 1.5f, .5f); }
        }, (p, t, m) -> { float k = t < 16 ? t / 16f : t < 34 ? 1 - (t - 16) / 18f : Math.max(0, (t - 34) / 10f); p.r("armL", 0, -1.2f * k, 1.8f * k).r("armR", 0, 1.2f * k, -1.8f * k); }).when(m -> m.hpFrac() < .75f);
    }
    /** Flowey seeds bloom into pellet rings. */
    static Attack zmSeeds() {
        return atk("zenith_flowey_seeds", 70, (c, t) -> {
            if (t >= 8 && t < 40 && t % 8 == 0) { Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3, .2, c.rnd.nextGaussian() * 3); c.proj().at(at).box(.4f, .4f, .4f).color(0x2a8a2a).dmg(0).life(24).spawn(); }
            if (t >= 32 && t < 64 && t % 8 == 0) ring(c, c.target.position().add(c.rnd.nextGaussian() * 3, .5, c.rnd.nextGaussian() * 3), 10, .3, t, .3f, 0xffffff, 4, 6, 26);
        }, (p, t, m) -> { zFlap(p, t, .7f); p.r("head", -.3f, 0, 0); }).when(m -> m.hpFrac() < .6f);
    }
    static Attack zmDive() {
        return atk("zenith_meteor_dive", 54, (c, t) -> {
            if (t == 6) c.dash(new Vec3(0, 1.1, 0));
            if (t == 18) c.dash(c.aim(c.pos()).scale(2));
            if (t == 24) { c.aoe(c.pos(), 3, 12, 1.4); ring(c, c.pos().add(0, .4, 0), 16, .5, 0, .35f, 0x3a5aaf, 5, 0, 18); c.sound(SoundEvents.GENERIC_EXPLODE, .8f, 1.2f); }
        }, (p, t, m) -> { boolean dive = t > 16 && t < 26; p.r("body", dive ? 1.3f : 0, 0, 0).r("armL", 0, 0, dive ? .2f : 1.6f).r("armR", 0, 0, dive ? -.2f : -1.6f); });
    }
    /** Phase 3: Zenith — halo blazes, blaster-like feather lances from all directions. */
    static Attack zmZenith() {
        return atk("zenith_ascension", 100, (c, t) -> {
            if (t < 50 && t % 6 == 0) { Vec3 at = around(c.tpos(), 6, t * .4, 1.5 + (t % 12) * .2); blaster(c, at, c.tpos().subtract(at), 18, 10, 9, .7f, 0x3a5aaf, 0xff3030, 0xfff07a); }
            if (t == 60) c.me.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1));
        }, (p, t, m) -> { zFlap(p, t * .5f, 1); p.o("halo", 0, 1 + wave(t, .2f), 0).r("head", -.4f, 0, 0); }).when(m -> m.hpFrac() < .3f);
    }

    private AuYellowFangames() {}
}
