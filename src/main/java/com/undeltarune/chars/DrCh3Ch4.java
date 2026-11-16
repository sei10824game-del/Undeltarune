package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import java.util.List;

import static com.undeltarune.chars.drlate.DrKit.*;

/**
 * Deltarune Chapter 3 (TV World) and Chapter 4 (Dark Sanctuaries + Castle Town) enemies and bosses.
 * Attack lists follow deltarune.wiki (checked 2026-11); one Attack = one enemy turn.
 * Factions: no TV-World/Sanctuary faction exists, so those Darkners are NEUTRAL faction (no infighting) but HOSTILE
 * to the player as in their battles; the Knight, Titan and Titan Spawn are RUNAWAY_SHADOW (the darkness side).
 * Not added on purpose: Rabbick (Ch1 group), Rouxls Kaard (Ch2 group), Tasque Manager (Ch2, no Ch3/4 fight),
 * Toriel/Asgore/Sans (never fought in Deltarune), the Original Game / Tenna minigame sprites (2D minigame-only
 * "enemies"), FRIEND (only a summon of the Shadow Mantle holder, used as its bullets) and the unused "Spear boss".
 */
public final class DrCh3Ch4 {
    static final int WHITE = 0xffffff, RED = 0xff3030, YEL = 0xffe14a, STAR = 0xfff27a;

    public static void register(List<CharacterDef> out) {
        // ---------- Chapter 3: TV World ----------
        out.add(shadowguy()); out.add(pippins()); out.add(shuttah()); out.add(zapper()); out.add(watercooler());
        out.add(ribbick()); out.add(laninoElnina()); out.add(tenna()); out.add(knight()); out.add(shadowMantle());
        // ---------- Chapter 4 ----------
        out.add(guei()); out.add(balthizard()); out.add(mizzle());
       
       
       
    }

    static CharacterDef.Builder tv(String id, String name) { return CharacterDef.of(id, name, Faction.NEUTRAL).att(Attitude.HOSTILE); }

    // =====================================================================================================
    // SHADOWGUY - silent-era extra; black silhouette, white grin, fedora
    // =====================================================================================================
    static CharacterDef shadowguy() {
        ModelDef m = hum(8, 10, 8, 7, 0x0c0c0c, 0x0c0c0c, 0x0c0c0c, 0x0c0c0c)
            .box("brim", "head", 0, 3.8f, 0, 10, .8f, 10, 0x050505).box("crown", "head", 0, 5.2f, 0, 7, 2.4f, 7, 0x050505)
            .box("band", "head", 0, 4.4f, 0, 7.2f, .6f, 7.2f, 0xb02020)
            .glow("grin", "head", 0, -1.6f, -3.65f, 4.6f, .9f, .3f, WHITE)
            .glow("sockL", "legL", 0, -7.4f, 0, 3.6f, 1.2f, 2.6f, WHITE).glow("sockR", "legR", 0, -7.4f, 0, 3.6f, 1.2f, 2.6f, WHITE)
            .box("coat", "body", 0, -5.5f, 0, 8.6f, 4, 5.2f, 0x0c0c0c);
        return tv("shadowguy", "Shadowguy").hp(40).egg(0x0c0c0c, 0xffffff).size(.7f, 2.1f).keep(7)
            .model(m).attacks(sgSax(), sgTommy(), sgCensored(), sgBiplane(), sgHeli(), sgYarn()).build();
    }
    static Attack sgSax() {
        return atk("shadowguy_saxophone", 80, (c, t) -> {
            if (t >= 10 && t < 66 && t % 7 == 3) {
                Vec3 f = toT(c), at = c.eye().add(f.scale(1.2)).add(side(f).scale(.6));
                fan(c, at, f, 4, .9 + .3 * Math.sin(t * .5), .32, .35f, .5f, 0xf0f0f0, 3, 4, 50);
                fx(c, ParticleTypes.NOTE, at, 4, .4); c.sound(SoundEvents.FIREWORK_ROCKET_TWINKLE, .5f, 1.4f + .05f * (t % 5));
            }
        }, (p, t, m) -> { float s = ramp(t, 0, 8);
            p.r("armR", -1.3f * s, .6f * s, 0).r("armL", -1.1f * s, -.5f * s, 0).r("head", -.25f * s, 0, 0).r("body", -.15f * s + sin(t, .5f) * .08f * s, 0, 0); });
    }
    static Attack sgTommy() {
        return atk("shadowguy_tommy_gun", 76, (c, t) -> {
            if (t >= 14 && t < 62 && t % 2 == 0) {
                Vec3 f = toT(c), at = c.eye().add(f.scale(1.1)).add(0, -.5, 0);
                double sweep = Math.sin((t - 14) * .18) * .45;
                bolt(c, at, rot(c.aim(at), sweep).scale(.85), .14f, .35f, 0xfff3c0, 2.5f, 0, 30);
                if (t % 6 == 0) c.sound(SoundEvents.SKELETON_SHOOT, .6f, 1.9f);
                fx(c, ParticleTypes.SMOKE, at, 2, .1);
            }
        }, (p, t, m) -> { float s = ramp(t, 0, 10), kick = t > 14 && t < 62 ? sin(t, 3.1f) * .08f : 0;
            p.r("armR", -1.5f * s + kick, .3f, 0).r("armL", -1.4f * s + kick, -.5f, 0).r("body", 0, -.25f * s, 0).o("body", 0, 0, kick * 3); });
    }
    static Attack sgCensored() {
        return atk("shadowguy_censored", 90, (c, t) -> {
            if (t % 22 == 6 && t < 75) {
                Vec3 f = toT(c), r = side(f); int s = (t / 22) % 2 == 0 ? 1 : -1;
                Vec3 at = c.tpos().add(r.scale(7 * s));
                c.proj().at(at).vel(r.scale(-.38 * s)).box(.5f, 2.6f, 2.4f).color(0x080808).dmg(5).warn(10).life(46).pierce().spawn();
                prop(c, at.add(f.scale(-1.3)), f, 1.8f, .4f, .1f, 0xff4fa0, 12);
                c.sound(SoundEvents.ANVIL_PLACE, .4f, 1.8f);
            }
        }, (p, t, m) -> { float k = (t % 22) / 22f; p.r("armL", -1.8f * env(k, 0, .2f, .5f, .9f), 0, .4f).r("armR", -1.8f * env(k, 0, .2f, .5f, .9f), 0, -.4f).r("head", 0, sin(t, .3f) * .3f, 0); });
    }
    static Attack sgBiplane() {
        return atk("shadowguy_rouxls_shoot_biplane", 90, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 8) { Vec3 at = c.tpos().add(r.scale(-10)).add(0, 4, 0);
                c.proj().at(at).vel(r.scale(.34)).box(1.6f, .5f, 1.2f).color(0x3c7bd6).dmg(0).life(62).spawn();
                c.proj().at(at).vel(r.scale(.34)).box(.3f, .2f, 2.6f).color(0xd6d6d6).dmg(0).life(62).spawn();
                c.sound(SoundEvents.PHANTOM_FLAP, 1, .6f); }
            if (t > 14 && t < 68 && t % 4 == 0) {
                Vec3 at = c.tpos().add(r.scale(-10 + (t - 8) * .34)).add(0, 3.6, 0);
                c.proj().at(at).vel(new Vec3(0, -.05, 0).add(r.scale(.12))).box(.35f, .35f, .35f).color(0x222222).dmg(4).gravity(.03f).life(40).spawn();
            }
        }, (p, t, m) -> { float s = ramp(t, 0, 8); p.r("armR", -2.4f * s, 0, -.4f).r("armL", -.6f * s, 0, .2f).r("head", -.4f * s, sin(t, .15f) * .6f, 0); });
    }
    static Attack sgHeli() {
        return atk("shadowguy_rouxls_shoot_helicopter", 96, (c, t) -> {
            Vec3 top = c.tpos().add(0, 4.2, 0);
            if (t == 6) { prop(c, top, toT(c), 1.4f, .9f, 2, 0xd04040, 80); prop(c, top.add(0, .7, 0), new Vec3(1, 0, 0), .15f, .1f, 4.2f, 0xbbbbbb, 80);
                c.sound(SoundEvents.PHANTOM_FLAP, 1, .4f); }
            if (t > 18 && t < 84 && t % 3 == 0) {
                double a = t * .45;
                shot(c, top.add(0, -.6, 0), new Vec3(Math.cos(a) * .12, -.32, Math.sin(a) * .12), .3f, 0xffd0d0, 3, 0, 26);
                if (t % 9 == 0) c.sound(SoundEvents.SHULKER_SHOOT, .4f, 1.6f);
            }
        }, (p, t, m) -> { pointLoop(p, t, "armR", 12); p.r("armL", -2.8f * ramp(t, 0, 8), 0, .2f); });
    }
    static Attack sgYarn() {
        return atk("shadowguy_yarn_balls", 80, (c, t) -> {
            if (t >= 10 && t < 64 && t % 9 == 1) {
                Vec3 f = toT(c), at = c.eye().add(f.scale(1)).add(0, .5, 0);
                Vec3 v = c.tpos().subtract(at).multiply(.045, 0, .045).add(0, .38, 0).add(side(f).scale(c.rnd.nextGaussian() * .1));
                c.proj().at(at).vel(v).box(.6f, .6f, .6f).color(0xe86fd0).dmg(4).gravity(.035f).life(50).spawn();
                c.sound(SoundEvents.CAT_AMBIENT, .7f, 1.4f);
            }
        }, (p, t, m) -> throwR(p, (t + 8) % 9 * 2.2f, 7, 19.8f));
    }

    // =====================================================================================================
    // PIPPINS - dice Darkner; white hex/die head with red pips, red coat, neon-blue outline
    // =====================================================================================================
    static CharacterDef pippins() {
        ModelDef m = hum(4, 6, 6, 8, 0xc4262e, 0xf4f4f4, 0x181818, 0x181818)
            .glow("pip1", "head", -2, 2, -4.15f, 1.2f, 1.2f, .3f, 0xd01818).glow("pip2", "head", 2, -2, -4.15f, 1.2f, 1.2f, .3f, 0xd01818)
            .glow("mouth", "head", 0, -.5f, -4.15f, 3.4f, .7f, .3f, 0xd01818)
            .ghost("outline", "body", 0, 0, 0, 7, 7, 4.4f, 0x2fd7ff, .35f)
            .box("tie", "body", 0, 1.5f, -1.9f, 1, 3, .4f, 0xd01818).box("collar", "body", 0, 2.6f, -1.8f, 3, .8f, .4f, WHITE);
        return tv("pippins", "Pippins").hp(36).egg(0xf4f4f4, 0xc4262e).size(.6f, 1.2f).keep(6).random()
            .model(m).attacks(pipRoll(1), pipRoll(2), pipRoll(3), pipRoll(4)).build();
    }
    /** The die lands on n: a 1 is one aimed bullet; 2-4 launch n explosions rippling out at 360/n degrees. */
    static Attack pipRoll(int n) {
        return atk("pippins_roll_" + n, 70, (c, t) -> {
            if (t == 4) { c.sound(SoundEvents.ITEM_PICKUP, 1, .6f); fx(c, ParticleTypes.CRIT, c.eye(), 10, .3); }
            if (t == 18 && n == 1) for (int k = 0; k < 3; k++) shot(c, c.eye(), c.aim(c.eye()).scale(.55 + k * .08), .55f, WHITE, 5, 2, 40);
            if (n > 1 && t == 18) {
                double base = c.rnd.nextDouble() * Math.PI;
                for (int k = 0; k < n; k++) { Vec3 at = around(c, 2.2 + n * .3, base + k * Math.PI * 2 / n, -.4);
                    prop(c, at, new Vec3(0, 0, 1), .5f, .1f, .5f, 0xd01818, 14); }
            }
            if (n > 1 && t >= 30 && t < 30 + 3 * 6 && (t - 30) % 6 == 0) {
                int wave = (t - 30) / 6; double base = 0;
                for (int k = 0; k < n; k++) {
                    Vec3 at = c.target.position().add(Math.cos(base + k * Math.PI * 2 / n) * (2.2 + n * .3), .3, Math.sin(base + k * Math.PI * 2 / n) * (2.2 + n * .3));
                    ring(c, at, 6 + n * 2, .18 + wave * .06, .3f + .1f * n, 0xffe0e0, 2 + n, wave * .3, 0, 16);
                    fx(c, ParticleTypes.EXPLOSION, at, 1, 0);
                }
                c.sound(SoundEvents.GENERIC_EXPLODE, .5f, 1.6f);
            }
        }, (p, t, m) -> { float roll = ramp(t, 0, 16); p.r("head", roll * 12.6f, 0, roll * 6.3f).o("body", 0, (float) Math.sin(roll * Math.PI) * 3, 0); throwR(p, t, 14, 40); });
    }

    // =====================================================================================================
    // SHUTTAH - camera Darkner; vertical rainbow stripes, film-strip grin, eyes on its arms
    // =====================================================================================================
    static CharacterDef shuttah() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 12, 0, 6, 14, 4, 0xf2c530)
            .box("stripeR", "body", -1.5f, 0, -.1f, 1.5f, 14, 4.1f, 0xe0323a).box("stripeB", "body", 0, 0, -.15f, 1.5f, 14, 4.2f, 0x2f6fe0)
            .box("stripeG", "body", 1.5f, 0, -.1f, 1.5f, 14, 4.1f, 0x2fb34a)
            .box("head", "body", 0, 7.5f, 0, 6.5f, 2.5f, 4.5f, 0x1a1a1a)
            .glow("film", "head", 0, 0, -2.3f, 5.5f, 1.2f, .3f, 0xfff3d0)
            .limb("armL", "body", -4, 6, 0, 1.6f, 9, 1.6f, 0xf2c530).limb("armR", "body", 4, 6, 0, 1.6f, 9, 1.6f, 0xf2c530)
            .glow("lensL", "armL", 0, -9.5f, 0, 2.6f, 2.6f, 2.6f, 0xffe066).glow("lensR", "armR", 0, -9.5f, 0, 2.6f, 2.6f, 2.6f, 0xffe066)
            .limb("legL", null, -1.5f, 5, 0, 1.4f, 5, 1.4f, 0x1a1a1a).limb("legR", null, 1.5f, 5, 0, 1.4f, 5, 1.4f, 0x1a1a1a);
        return tv("shuttah", "Shuttah").hp(110).egg(0xf2c530, 0x2f6fe0).size(.6f, 1.8f).keep(7)
            .model(m).attacks(shFreeze(), shCross(), shDiamond(), shPhotoAssist()).build();
    }
    static final int DROP = 0x6fc7ff;
    static void flash(AttackCtx c) { fx(c, ParticleTypes.FLASH, c.tpos(), 1, 0); c.sound(SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR, 1, 1.8f); }
    static Attack shFreeze() {
        return atk("shuttah_frozen_rain", 100, (c, t) -> {
            if (t < 84 && t % 3 == 0) rain(c, 3.2, 1, .3f, DROP, 3, 6, .02f);
            if (t % 25 == 24) { flash(c); freeze(c, DROP); }
            if (t % 25 == 8 && t > 25) retarget(c, DROP, .0);
        }, (p, t, m) -> { float k = (t % 25) / 25f, s = env(k, .6f, .9f, .95f, 1); p.r("armL", -1.6f * s, .3f, 0).r("armR", -1.6f * s, -.3f, 0).r("body", 0, 0, sin(t, .3f) * .1f); });
    }
    static Attack shCross() {
        return atk("shuttah_rotating_drops", 100, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t < 80 && t % 5 == 0) { boolean h = (t / 5) % 2 == 0; Vec3 d = h ? r : f;
                Vec3 at = c.tpos().add(d.scale(-6)).add(d == r ? f.scale(c.rnd.nextGaussian() * 1.5) : r.scale(c.rnd.nextGaussian() * 1.5));
                shot(c, at, d.scale(.32), .3f, DROP, 3, 5, 45); }
            if (t % 30 == 29) { flash(c); retarget(c, DROP, .3); }
        }, (p, t, m) -> { float s = env((t % 30) / 30f, .5f, .9f, .95f, 1); p.r("armR", -1.7f * s, 0, -.2f).r("armL", -1.7f * s, 0, .2f).r("head", 0, sin(t, .2f) * .4f, 0); });
    }
    static Attack shDiamond() {
        return atk("shuttah_snapshot_diamonds", 100, (c, t) -> {
            int every = t < 40 ? 5 : 9;
            if (t % every == 0 && t < 90) { Vec3 at = c.eye().add(toT(c).scale(1)); bolt(c, at, c.aim(at).scale(.42), .35f, .5f, 0xffffff, 3, 3, 40); }
            if (t == 40) { flash(c); freeze(c, WHITE); }
            if (t > 40 && t % 6 == 0) for (var pr : c.level.getEntitiesOfClass(com.undeltarune.entity.UtProjectile.class, c.me.getBoundingBox().inflate(32),
                    pr -> pr.ownerId == c.me.getId() && pr.color() == WHITE && pr.getDeltaMovement().lengthSqr() < 1e-4)) {
                Vec3 o = pr.position().subtract(c.tpos()); pr.setPos(c.tpos().add(rot(o, .25))); }
        }, (p, t, m) -> { pointLoop(p, t, "armR", t < 40 ? 5 : 9); p.r("armL", -1.6f * env(t, 30, 38, 42, 50), 0, .3f); });
    }
    static Attack shPhotoAssist() {
        return atk("shuttah_photo_rotate", 90, (c, t) -> {
            if (t % 8 == 0 && t < 70) { Vec3 at = around(c, 6, t * .4, .4); shot(c, at, c.aim(at).scale(.25), .3f, 0xffb0ff, 3, 6, 50); }
            if (t % 20 == 19) { flash(c); for (var pr : c.level.getEntitiesOfClass(com.undeltarune.entity.UtProjectile.class, c.me.getBoundingBox().inflate(32),
                    pr -> pr.ownerId == c.me.getId() && pr.color() == 0xffb0ff)) pr.setDeltaMovement(rot(pr.getDeltaMovement(), Math.PI / 2)); }
        }, (p, t, m) -> { float s = env((t % 20) / 20f, .4f, .85f, .95f, 1); p.r("armR", -1.8f * s, 0, 0).r("armL", -1.8f * s, 0, 0).o("body", 0, s * .5f, 0); });
    }

    // =====================================================================================================
    // ZAPPER - TV remote bouncer; navy body, red light, triangle buttons, red gloves
    // =====================================================================================================
    static CharacterDef zapper() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 11, 0, 7, 10, 4, 0x1d2a5c)
            .box("head", "body", 0, 9, 0, 5, 8, 4, 0x1d2a5c).glow("light", "head", 0, 4.4f, 0, 2, 1, 2, 0xff2020)
            .box("padL", "body", -4, 4.5f, 0, 2.5f, 1.2f, 4.2f, WHITE).box("padR", "body", 4, 4.5f, 0, 2.5f, 1.2f, 4.2f, WHITE)
            .glow("b1", "body", -1.6f, 2, -2.1f, 1.2f, 1.2f, .3f, 0xff3030).glow("b2", "body", 1.6f, 2, -2.1f, 1.2f, 1.2f, .3f, WHITE)
            .glow("b3", "body", -1.6f, -.5f, -2.1f, 1.2f, 1.2f, .3f, WHITE).glow("b4", "body", 1.6f, -.5f, -2.1f, 1.2f, 1.2f, .3f, 0xff3030)
            .glow("b5", "body", -1.6f, -3, -2.1f, 1.2f, 1.2f, .3f, 0xff3030).glow("b6", "body", 1.6f, -3, -2.1f, 1.2f, 1.2f, .3f, WHITE)
            .limb("armL", "body", -4.6f, 4, 0, 2, 9, 2, 0x1d2a5c).limb("armR", "body", 4.6f, 4, 0, 2, 9, 2, 0x1d2a5c)
            .box("gloveL", "armL", 0, -9.5f, 0, 2.6f, 2.2f, 2.6f, 0xd02020).box("gloveR", "armR", 0, -9.5f, 0, 2.6f, 2.2f, 2.6f, 0xd02020)
            .limb("legL", null, -1, 6, 0, 2, 6, 2.6f, 0x141c40).limb("legR", null, 1, 6, 0, 2, 6, 2.6f, 0x141c40);
        return tv("zapper", "Zapper").hp(55).egg(0x1d2a5c, 0xff2020).size(.6f, 2.3f).keep(7).model(m).attacks(zapLaser(), zapCannon()).build();
    }
    /** White lasers bounce off floor (turning RED - normal) or ceiling (turning BLUE - stay still). */
    static Attack zapLaser() {
        return atk("zapper_laser", 96, (c, t) -> {
            Vec3 f = toT(c), head = c.eye().add(0, .9, 0);
            if (t >= 12 && t < 72 && t % 12 == 0) {
                boolean down = (t / 12) % 2 == 0;
                Vec3 mid = c.target.position().add(f.scale(-1.5)).add(0, down ? 0.1 : 3.6, 0);
                Vec3 v = mid.subtract(head).normalize().scale(.5);
                bolt(c, head, v, .2f, 1.4f, WHITE, 3, 6, (int) (mid.distanceTo(head) / .5));
                Vec3 out = new Vec3(v.x, -v.y, v.z);
                var b = c.proj().at(mid).vel(out).box(.2f, .2f, 1.4f).dmg(4).warn((int) (mid.distanceTo(head) / .5)).life(60);
                (down ? b.color(RED) : b.blue()).spawn();
                c.sound(SoundEvents.BEACON_ACTIVATE, .6f, 2f);
            }
        }, (p, t, m) -> { pointLoop(p, t, "armR", 12); p.r("head", -.2f * ramp(t, 0, 10), 0, 0); });
    }
    static Attack zapCannon() {
        return atk("zapper_cannon", 90, (c, t) -> {
            if (t >= 20 && t < 80 && t % 15 == 5) {
                Vec3 at = c.eye().add(toT(c).scale(1.3)).add(0, .3, 0);
                fan(c, at, c.aim(at), 7, .7, .5, .35f, .55f, 0xff6060, 4, 2, 40);
                fx(c, ParticleTypes.LARGE_SMOKE, at, 4, .2); c.sound(SoundEvents.FIREWORK_ROCKET_BLAST, 1, .6f);
            }
        }, (p, t, m) -> { float s = ramp(t, 4, 18), kick = t >= 20 && (t - 5) % 15 < 3 ? -.25f : 0; bow(p, s * 1.2f + kick); p.r("armL", .5f * s, 0, .3f).r("armR", .5f * s, 0, -.3f); });
    }

    // =====================================================================================================
    // WATERCOOLER - Mizzle hidden in a cooler; gray base, red/blue taps, blue jug
    // =====================================================================================================
    static ModelDef coolerModel(int jug) {
        return new ModelDef()
            .box("body", null, 0, 6, 0, 8, 12, 7, 0x9ea3ab)
            .glow("tapR", "body", -1.6f, 2, -3.7f, 1.2f, 1.2f, .5f, 0xe03030).glow("tapB", "body", 1.6f, 2, -3.7f, 1.2f, 1.2f, .5f, 0x3060e0)
            .box("cups", "body", 5, 1, 0, 1.6f, 6, 1.6f, 0xe8e8e8)
            .ghost("head", "body", 0, 11, 0, 7, 10, 7, jug, .75f).box("cap", "head", 0, 5.5f, 0, 3, 1, 3, 0x3a6fd0)
            .glow("eyeL", "head", -1.5f, 0, -3.6f, 1, 1.6f, .3f, 0x0b1c4a).glow("eyeR", "head", 1.5f, 0, -3.6f, 1, 1.6f, .3f, 0x0b1c4a);
    }
    static CharacterDef watercooler() {
        return tv("watercooler", "Watercooler").hp(60).egg(0x9ea3ab, 0x4fa8ff).size(.8f, 1.8f).keep(6).speed(.15f)
            .model(coolerModel(0x4fa8ff)).attacks(coolerRain("watercooler", 1), coolerRain("watercooler", 2), coolerRain("watercooler", 3)).build();
    }
    /** Droplets converge on the target and pool; v2 the pool wobbles side to side; v3 droplets spiral in. Shared with Miss Mizzle. */
    static Attack coolerRain(String who, int v) {
        return atk(who + "_rain_" + v, 100, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t < 84 && t % (v == 3 ? 2 : 3) == 0) {
                double a = v == 3 ? t * .35 : c.rnd.nextDouble() * Math.PI * 2, rad = 6;
                Vec3 at = around(c, rad, a, 1.5 + c.rnd.nextDouble() * 1.5);
                Vec3 vel = c.tpos().subtract(at).normalize().scale(.24);
                if (v == 3) vel = rot(vel, .55);
                shot(c, at, vel, .28f, DROP, 3, 4, 34);
            }
            if (t % 20 == 10) {
                float size = 1 + t / 30f; double wob = v == 2 ? Math.sin(t * .2) * 2 : 0;
                c.proj().at(c.target.position().add(r.scale(wob)).add(0, .05, 0)).vel(v == 2 ? r.scale(Math.cos(t * .2) * .1) : Vec3.ZERO)
                    .box(size, .15f, size).color(0x3a8ff0).dmg(2).warn(8).life(26).pierce().spawn();
                c.sound(SoundEvents.BUCKET_EMPTY, .6f, 1.4f);
            }
        }, (p, t, m) -> { p.r("body", 0, 0, sin(t, .4f) * .06f).o("head", 0, sin(t, .25f) * .6f, 0); });
    }

    // =====================================================================================================
    // RIBBICK - four-eyed frog head made of dirty dust
    // =====================================================================================================
    static CharacterDef ribbick() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 5, 0, 10, 8, 9, 0xa9d48a).box("head", "body", 0, 3, -1, 9, 3, 8, 0xb9e09a)
            .glow("e1", "head", -3, 2, -3.5f, 1.6f, 1.6f, 1.6f, 0x203018).glow("e2", "head", -1, 2.4f, -3.8f, 1.6f, 1.6f, 1.6f, 0x203018)
            .glow("e3", "head", 1, 2.4f, -3.8f, 1.6f, 1.6f, 1.6f, 0x203018).glow("e4", "head", 3, 2, -3.5f, 1.6f, 1.6f, 1.6f, 0x203018)
            .box("whiskL", "body", -6, 0, -3, 3, .3f, .3f, 0x6b7f5a).box("whiskR", "body", 6, 0, -3, 3, .3f, .3f, 0x6b7f5a)
            .box("dirt", "body", 0, -3.5f, 0, 10.5f, 1.5f, 9.5f, 0x7a8a60)
            .limb("legL", null, -3, 1.5f, 0, 2.5f, 1.5f, 3, 0x93bd76).limb("legR", null, 3, 1.5f, 0, 2.5f, 1.5f, 3, 0x93bd76);
        return tv("ribbick", "Ribbick").hp(42).egg(0xa9d48a, 0x203018).size(.7f, .8f).keep(5).model(m).attacks(ribJump(), ribFly(), ribBounce()).build();
    }
    static Attack ribJump() {
        return atk("ribbick_jump", 90, (c, t) -> {
            if (t >= 8 && t < 76 && t % 17 == 8) for (int s = -1; s <= 1; s += 2) {
                Vec3 f = toT(c), r = side(f), at = c.target.position().add(r.scale(4 * s)).add(f.scale(-1.5)).add(0, .3, 0);
                c.proj().at(at).vel(r.scale(-.18 * s).add(0, .48, 0)).box(.8f, .6f, .8f).color(0x8fd06a).dmg(4).gravity(.045f).warn(4).life(24).spawn();
                c.sound(SoundEvents.FROG_LONG_JUMP, .8f, 1.2f);
            }
        }, (p, t, m) -> hop(p, t, 17, 3));
    }
    static final int FLY = 0x3a3a2a;
    static Attack ribFly() {
        return atk("ribbick_fly", 90, (c, t) -> {
            if (t >= 10 && t < 40 && t % 3 == 0) shot(c, c.eye().add(jit(c, .6)), jit(c, .15), .18f, FLY, 2, 0, 70);
            if (t >= 12 && t < 80) steer(c, FLY, .03, .22);
            if (t % 10 == 0) c.sound(SoundEvents.BEE_LOOP, .3f, 1.8f);
        }, (p, t, m) -> { float s = env(t, 2, 10, 30, 40); p.o("head", 0, 2.2f * s, 0).r("head", -.4f * s, 0, 0); });
    }
    /** Rabbick's wave attack (used when a Rabbick was revealed). */
    static Attack ribBounce() {
        return atk("ribbick_rabbick_bounce", 80, (c, t) -> {
            if (t >= 10 && t < 70 && t % 6 == 0) {
                Vec3 f = toT(c), r = side(f), at = c.tpos().add(f.scale(-6)).add(r.scale(Math.sin(t * .25) * 2.5)).add(0, Math.abs(Math.sin(t * .4)) * 1.5 - .6, 0);
                shot(c, at, f.scale(.3), .4f, 0xd8c8a8, 3, 4, 40);
            }
        }, (p, t, m) -> hop(p, t, 12, 2));
    }

    // =====================================================================================================
    // LANINO & ELNINA - weather duo (moon-headed Lanino in navy suit / sheep-like Elnina with umbrella skirt)
    // =====================================================================================================
    static CharacterDef laninoElnina() {
        ModelDef m = hum(7, 9, 6, 6, 0x1c2a6a, 0xf1d84a, 0xd02828, 0x1c2a6a)
            .box("hair", "head", 0, 3.4f, .5f, 7.5f, 2.5f, 7, WHITE).box("moon", "head", 2.2f, 1, 0, 2.4f, 7, 6, 0xf1d84a)
            .glow("glasses", "head", 0, .5f, -3.15f, 5, 1.1f, .3f, 0x0c1240)
            .glow("fireL", "body", -3.6f, 5.2f, 0, 1.6f, 1.6f, 1.6f, 0xff7a1a).glow("fireR", "body", 3.6f, 5.2f, 0, 1.6f, 1.6f, 1.6f, 0xff7a1a)
            .box("staffL", "armR", 0, -8, -1, .6f, 12, .6f, 0x8a6a3a)
            // Elnina stands beside him (parented to the root so she bobs with the duo)
            .box("elnina", "body", 9, -3, 0, 5, 6, 4, WHITE).box("skirt", "elnina", 0, -4.5f, 0, 9, 3, 9, 0x2a3a8a)
            .box("skirtY", "skirt", 0, -.5f, 0, 9.4f, 1, 9.4f, 0xf1d84a).box("elHead", "elnina", 0, 5, 0, 5.5f, 5.5f, 5.5f, 0x9fd8ff)
            .box("elHair", "elHead", 0, 1.5f, .5f, 7.5f, 4.5f, 7, WHITE).glow("elGlasses", "elHead", 0, 0, -2.9f, 4.4f, 1, .3f, 0x0c1240)
            .box("bootL", "elnina", -1.5f, -8, 0, 2, 3, 2.4f, 0xf1d84a).box("bootR", "elnina", 1.5f, -8, 0, 2, 3, 2.4f, 0xf1d84a);
        return tv("lanino_and_elnina", "Lanino & Elnina").hp(140).egg(0x1c2a6a, 0x9fd8ff).size(1.2f, 1.8f).keep(7)
            .model(m).attacks(leSun(), leMoon(), leDroplets(), leCrystals(), leFire(), leLightning()).build();
    }
    static Attack leSun() {
        return atk("lanino_elnina_sun", 90, (c, t) -> {
            Vec3 sun = c.tpos().add(0, 5, 0);
            if (t == 6) { prop(c, sun, toT(c), 1.6f, 1.6f, 1.6f, 0xffd020, 76); c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.4f); }
            if (t >= 20 && t < 80 && t % 10 == 0) for (int i = 0; i < 8; i++) {
                double a = i * Math.PI / 4 + t * .07; shot(c, sun, new Vec3(Math.cos(a) * .16, -.2, Math.sin(a) * .16), .35f, 0xffb020, 3, 3, 40); }
        }, (p, t, m) -> { raise(p, ramp(t, 0, 10)); hover(p, t, .6f); });
    }
    static Attack leMoon() {
        return atk("lanino_elnina_moon", 90, (c, t) -> {
            if (t >= 10 && t < 78 && t % 11 == 0) {
                double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = around(c, 6.5, a, 1.2);
                Vec3 v = c.tpos().subtract(at).normalize();
                for (int k = -2; k <= 2; k++) bolt(c, at.add(side(v).scale(k * .35)).add(v.scale(-Math.abs(k) * .3)), v.scale(.28), .3f, .3f, 0xf6eeb0, 3, 8, 45);
                c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .8f);
            }
        }, (p, t, m) -> { pointLoop(p, t, "armR", 11); hover(p, t, .4f); });
    }
    static Attack leDroplets() {
        return atk("lanino_elnina_droplets", 90, (c, t) -> { if (t < 80 && t % 2 == 0) rain(c, 3.5, 1, .3f, DROP, 3, 5, .025f); if (t % 15 == 0) c.sound(SoundEvents.WEATHER_RAIN, .6f, 1.2f); },
            (p, t, m) -> { p.r("skirt", 0, t * .3f, 0).r("armL", -2.4f * ramp(t, 0, 8), 0, .3f); hover(p, t, .3f); });
    }
    static Attack leCrystals() {
        return atk("lanino_elnina_crystals", 90, (c, t) -> {
            if (t < 80 && t % 4 == 0) { Vec3 at = c.target.position().add(jit(c, 3.5)).add(0, 6, 0);
                c.proj().at(at).vel(new Vec3(Math.sin(t * .3) * .05, -.16, 0)).box(.35f, .35f, .35f).color(0xd8f6ff).dmg(3).warn(5).life(60).spawn(); }
            if (t % 6 == 0) fx(c, ParticleTypes.SNOWFLAKE, c.tpos().add(0, 3, 0), 10, 2);
        }, (p, t, m) -> { p.r("skirt", 0, -t * .2f, 0); raise(p, ramp(t, 0, 10) * .6f); });
    }
    static Attack leFire() {
        return atk("lanino_elnina_red_fire", 90, (c, t) -> {
            if (t >= 12 && t < 80 && t % 6 == 0) { Vec3 at = c.eye().add(toT(c).scale(1)).add(0, 1, 0);
                Vec3 v = c.tpos().subtract(at).multiply(.05, 0, .05).add(0, .35, 0).add(jit(c, .05));
                c.proj().at(at).vel(v).box(.45f, .45f, .45f).color(0xff3a1a).dmg(4).gravity(.03f).life(50).spawn();
                fx(c, ParticleTypes.FLAME, at, 6, .2); c.sound(SoundEvents.FIRECHARGE_USE, .5f, 1.2f); }
        }, (p, t, m) -> throwR(p, (t % 6) * 3, 10, 18));
    }
    static Attack leLightning() {
        return atk("lanino_elnina_lightning", 100, (c, t) -> {
            if (t >= 8 && t < 88 && t % 10 == 8) {
                Vec3 at = c.target.position().add(jit(c, 1.5));
                pillar(c, at, .6f, 7, 0xfff36a, 6, 14, 5);
                if (t > 20) c.sound(SoundEvents.LIGHTNING_BOLT_IMPACT, .7f, 1.4f);
            }
        }, (p, t, m) -> { raise(p, env((t % 10) / 10f, 0, .5f, .8f, 1)); p.r("staffL", 0, t * .4f, 0); });
    }

    // =====================================================================================================
    // TENNA - TV host boss: lilac CRT head with antennas, red tailcoat, yellow tie, long yellow shoes
    // =====================================================================================================
    static CharacterDef tenna() {
        ModelDef m = hum(13, 12, 8, 9, 0xc42630, 0xc9a8e8, WHITE, 0x101010)
            .glow("screen", "head", 0, 0, -4.6f, 7, 6, .3f, 0xf8f8ff).box("nose", "head", 0, .5f, -5.6f, 1, 1, 2.2f, WHITE)
            .glow("grin", "screen", 0, -1.8f, -.2f, 5, .8f, .2f, 0x222222)
            .box("antL", "head", -2.5f, 6, 0, .5f, 4, .5f, 0x8a70b0).box("antR", "head", 2.5f, 6, 0, .5f, 4, .5f, 0x8a70b0)
            .box("knobL", "antL", 0, 2.4f, 0, 1.3f, 1.3f, 1.3f, 0xc9a8e8).box("knobR", "antR", 0, 2.4f, 0, 1.3f, 1.3f, 1.3f, 0xc9a8e8)
            .box("tie", "body", 0, 3, -2.6f, 1.6f, 5, .4f, YEL).glow("tv", "body", -2.4f, 2, -2.5f, 2, 1.2f, .3f, YEL)
            .box("tails", "body", 0, -8, 1.4f, 7, 5, 1, 0xc42630).box("buckle", "body", 0, -5.4f, -2.5f, 2, 1.2f, .3f, 0xe8c040)
            .box("shoeL", "legL", 0, -13, -2, 3, 1.5f, 6, YEL).box("shoeR", "legR", 0, -13, -2, 3, 1.5f, 6, YEL);
        return tv("tenna", "Tenna").hp(260).boss().egg(0xc42630, 0xc9a8e8).size(.7f, 2.6f).keep(8).rest(16).speed(.32f)
            .model(m).attacks(tennaAllStar(), tennaSmashCut(), tennaRimshot(), tennaMinigame(), tennaFinalEpisode()).build();
    }
    static Attack tennaAllStar() {
        return phase("tenna_all_star_cast", 100, .2f, 2, (c, t) -> {
            if (t < 84 && t % 3 == 0) { double a = t * .22; Vec3 at = around(c, 6, a, Math.sin(t * .1) * 1.2);
                Vec3 tang = new Vec3(-Math.sin(a), 0, Math.cos(a));
                shot(c, at, tang.scale(.32).add(c.tpos().subtract(at).normalize().scale(.12)), .4f, STAR, 3, 4, 44); }
            if (t % 20 == 0) c.sound(SoundEvents.FIREWORK_ROCKET_TWINKLE, .7f, 1.1f);
        }, (p, t, m) -> { raise(p, ramp(t, 0, 8)); p.r("body", 0, t * .12f, 0); });
    }
    static Attack tennaSmashCut() {
        return phase("tenna_smash_cut", 90, .2f, 2, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 6) c.dash(new Vec3(0, .7, 0));
            if (t == 22) { line(c, c.tpos(), r, 12, .5f, WHITE, 7, 8, 8); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .5f); fx(c, ParticleTypes.FLASH, c.tpos(), 1, 0); }
            if (t >= 30 && t < 80 && t % 4 == 0) { Vec3 at = c.target.position().add(r.scale((c.rnd.nextDouble() * 2 - 1) * 4)).add(f.scale(c.rnd.nextGaussian()));
                pillar(c, at, .4f, 6, 0xfff36a, 4, 10, 4); }
        }, (p, t, m) -> { clap(p, t, 22); p.o("body", 0, env(t, 4, 12, 20, 26) * 4, 0); });
    }
    static Attack tennaRimshot() {
        return phase("tenna_rimshot", 110, .2f, 2, (c, t) -> {
            if (t == 10 || t == 18) { Vec3 at = around(c, 4, t * 1.3, .5);
                c.proj().at(at).vel(rot(c.aim(at), 1.2).scale(.14)).box(.9f, .9f, .9f).color(0xffe85a).dmg(4).life(90).pierce().spawn(); }
            if (t > 24 && t % 4 == 0) steer(c, 0xffe85a, .006, .14);
            if (t > 24 && t % 18 == 0) {
                for (var pr : c.level.getEntitiesOfClass(com.undeltarune.entity.UtProjectile.class, c.me.getBoundingBox().inflate(32), pr -> pr.ownerId == c.me.getId() && pr.color() == 0xffe85a))
                    for (int k = 0; k < 5; k++) { double a = k * Math.PI * 2 / 5 + t; shot(c, pr.position(), new Vec3(Math.cos(a) * .25, 0, Math.sin(a) * .25), .25f, STAR, 3, 0, 30); }
                c.sound(SoundEvents.NOTE_BLOCK_SNARE.value(), 1, 1);
            }
            if (t > 20 && t % 6 == 0) for (var pr : c.level.getEntitiesOfClass(com.undeltarune.entity.UtProjectile.class, c.me.getBoundingBox().inflate(32), pr -> pr.ownerId == c.me.getId() && pr.color() == 0xffe85a))
                shot(c, pr.position(), Vec3.ZERO, .3f, 0xfff6c0, 2, 0, 14);
        }, (p, t, m) -> { float k = (t % 18) / 18f; p.r("armR", -1.2f - .6f * env(k, 0, .2f, .3f, .6f), 0, -.3f).r("armL", -.8f, 0, .3f); hover(p, t, .3f); });
    }
    /** One of Tenna's minigames, picked per cast: quiz buzzers, cooking (falling food), or a rhythm pulse. */
    static Attack tennaMinigame() {
        return phase("tenna_minigame", 100, .2f, 2, (c, t) -> {
            int game = (int) ((c.me.tickCount / 100) % 3);
            Vec3 f = toT(c), r = side(f);
            if (game == 0 && t % 20 == 10) for (int k = -1; k <= 1; k++) { if (k == ((t / 20) % 3) - 1) continue;
                pillar(c, c.target.position().add(r.scale(k * 2.2)), 1.6f, 3, k < 0 ? 0xff4040 : 0x40a0ff, 5, 12, 5); }
            if (game == 1 && t < 84 && t % 3 == 0) { Vec3 at = c.target.position().add(jit(c, 3)).add(0, 6, 0);
                c.proj().at(at).vel(new Vec3(0, -.1, 0)).box(.5f, .4f, .5f).color(t % 2 == 0 ? 0xf0a040 : 0xa0f060).dmg(3).gravity(.02f).warn(4).life(50).spawn(); }
            if (game == 2 && t % 12 == 0) ring(c, c.target.position().add(f.scale(-5)).add(0, .3, 0), 18, .3, .3f, 0xff60ff, 3, t * .1, 4, 30);
            if (t % 20 == 0) c.sound(SoundEvents.NOTE_BLOCK_PLING.value(), 1, .8f + game * .3f);
        }, (p, t, m) -> { pointLoop(p, t, "armR", 20); p.r("antL", 0, 0, sin(t, .5f) * .3f).r("antR", 0, 0, -sin(t, .5f) * .3f); });
    }
    static Attack tennaFinalEpisode() {
        return phase("tenna_final_episode", 160, -1, .2f, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            int seg = t / 30;
            if (t < 120) switch (seg) {
                case 0 -> { if (t % 3 == 0) shot(c, around(c, 6, t * .3, 0), Vec3.ZERO.add(c.aim(around(c, 6, t * .3, 0)).scale(.3)), .4f, STAR, 4, 3, 40); }
                case 1 -> { if (t % 5 == 0) pillar(c, c.target.position().add(r.scale((c.rnd.nextDouble() * 2 - 1) * 4)), .4f, 6, 0xfff36a, 4, 8, 4); }
                case 2 -> { if (t % 2 == 0) { Vec3 at = c.target.position().add(jit(c, 3)).add(0, 6, 0); c.proj().at(at).vel(new Vec3(0, -.3, 0)).box(.5f, .4f, .5f).color(0xf0a040).dmg(4).warn(3).life(40).spawn(); } }
                default -> { if (t % 10 == 0) ring(c, c.tpos(), 20, .0, .4f, 0xff60ff, 0, 0, 0, 1); if (t % 8 == 0) ring(c, c.target.position().add(f.scale(-5)).add(0, .4, 0), 20, .32, .3f, 0xff60ff, 4, t, 3, 30); }
            }
            if (t >= 120 && t % 5 == 0) { Vec3 at = around(c, 5, t * .7, 1.5); c.proj().at(at).vel(c.aim(at).scale(.12)).box(.8f, .8f, .8f).color(STAR).dmg(6).life(50).spawn(); }
            if (t % 30 == 0) { fx(c, ParticleTypes.FLASH, c.tpos(), 1, 0); c.sound(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 1, .8f); }
        }, (p, t, m) -> { raise(p, .6f + .4f * sin(t, .4f)); p.r("body", 0, t * .25f, 0).r("antL", 0, 0, sin(t, .8f) * .5f).r("antR", 0, 0, -sin(t, .8f) * .5f); });
    }

    // =====================================================================================================
    // THE KNIGHT (Roaring Knight) - black silhouette with white outline, horned helm, hollow palms
    // Phases: 1 (>70%), 2 (40-70%), 3 (15-40%), Roaring (<=15%)
    // =====================================================================================================
    static final int KN = 0xc9a0ff, KNRED = 0xff2a3a;
    static CharacterDef knight() {
        ModelDef m = hum(14, 13, 7, 7, 0x050505, 0x050505, 0x050505, 0x050505)
            .ghost("aura", "body", 0, 0, 0, 8, 14, 5, WHITE, .25f)
            .box("helm", "head", 0, 0, -1, 5, 8, 5, 0x050505).ghost("helmEdge", "head", 0, 0, -1, 5.6f, 8.6f, 5.6f, WHITE, .3f)
            .box("hornL", "head", -4.5f, 3, 0, 5, .8f, .8f, 0x050505).box("hornR", "head", 4.5f, 3, 0, 5, .8f, .8f, 0x050505)
            .box("prongL", "hornL", -2.4f, 1.6f, 0, .8f, 3, .8f, 0x050505).box("prongR", "hornR", 2.4f, 1.6f, 0, .8f, 3, .8f, 0x050505)
            .box("pauldronL", "body", -5, 6, 0, 4, 2.4f, 5, 0x050505).box("pauldronR", "body", 5, 6, 0, 4, 2.4f, 5, 0x050505)
            .glow("palmL", "armL", 0, -12, 0, 1.2f, 1.2f, 2.2f, WHITE).glow("palmR", "armR", 0, -12, 0, 1.2f, 1.2f, 2.2f, WHITE)
            .glow("visor", "head", 0, .5f, -3.7f, 2.4f, .5f, .3f, 0xeeeeee);
        return CharacterDef.of("roaring_knight", "The Knight", Faction.RUNAWAY_SHADOW).att(Attitude.HOSTILE).hp(320).boss()
            .egg(0x050505, 0xc9a0ff).size(.7f, 2.9f).keep(8).rest(12).speed(.34f).model(m).attacks(
                knStars(1), knTrack(1), knSplit(1), knTunnel(1), knSpin(1),
                knStars(2), knSplit(2), knTunnel(2), knTrack(2), knSpin(2),
                knStars(3), knSplit(3), knTrack(3), knTunnel(3), knSpin(3),
                knRoaring()).build();
    }
    static float knLo(int lv) { return lv == 1 ? .7f : lv == 2 ? .4f : .15f; }
    static float knHi(int lv) { return lv == 1 ? 2 : lv == 2 ? .7f : .4f; }
    static Attack knStars(int lv) {
        return phase("knight_stars_" + lv, 100, knLo(lv), knHi(lv), (c, t) -> {
            Vec3 hand = c.eye().add(toT(c).scale(1.2));
            if (t >= 10 && t < 46 && t % 9 == 1) { // cone of growing six-point stars
                Vec3 v = rot(c.aim(hand), (c.rnd.nextDouble() - .5) * .9).scale(.3);
                c.proj().at(hand).vel(v).box(.7f, .7f, .7f).color(KN).dmg(5).life(lv == 2 && t % 2 == 0 ? 40 : 30).spawn();
                c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .6f);
            }
            if (t == 52) { freeze(c, KN); retarget(c, KN, -.06); fx(c, ParticleTypes.END_ROD, c.tpos(), 20, 3); }
            if (t == 64) for (var pr : c.level.getEntitiesOfClass(com.undeltarune.entity.UtProjectile.class, c.me.getBoundingBox().inflate(32), pr -> pr.ownerId == c.me.getId() && pr.color() == KN)) {
                Vec3 at = pr.position(); pr.discard();
                if (lv == 3) { for (int k = 0; k < 2; k++) bolt(c, at, c.aim(at).scale(.3 + k * .15), .25f, .6f, KNRED, 5, 6 + k * 8, 40); }
                else { line(c, at, c.aim(at), 6, .15f, WHITE, 4, 5, 3); for (int k = 0; k < 6; k++) { double a = k * Math.PI / 3;
                    bolt(c, at, new Vec3(Math.cos(a), 0, Math.sin(a)).scale(.35), .22f, .5f, KNRED, 4, 0, lv == 1 && k % 2 == 1 ? 10 : 20); } }
                c.sound(SoundEvents.GLASS_BREAK, .6f, .7f);
            }
        }, (p, t, m) -> { p.r("armR", -1.6f * env(t, 4, 10, 46, 56), 0, 0); hover(p, t, .5f); });
    }
    static Attack knTrack(int lv) {
        int reps = lv == 3 ? 16 : 10;
        return phase("knight_tracking_swords_" + lv, 30 + reps * (lv == 3 ? 6 : 8), knLo(lv), knHi(lv), (c, t) -> {
            int per = lv == 3 ? 6 : 8, i = (t - 10) / per;
            if (t >= 10 && (t - 10) % per == 0 && i < reps) {
                double a = (c.rnd.nextInt(8)) * Math.PI / 4; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                int warn = Math.max(6, 16 - i);
                line(c, c.tpos(), d, 10, .35f, WHITE, 6, warn, 3);
                bolt(c, c.tpos().add(d.scale(-4.5)), Vec3.ZERO, .3f, 1.6f, KNRED, 0, 0, warn).setYRot((float) Math.toDegrees(Math.atan2(-d.x, d.z)));
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .8f, 1.4f);
            }
            if (lv == 2 && t % 20 == 5) ring(c, c.tpos(), 8, -.05, .3f, 0xdddddd, 0, t * .2, 0, 20); // contracting sword circle (visual)
        }, (p, t, m) -> { float k = ((t - 10) % (lv == 3 ? 6 : 8)) / (lv == 3 ? 6f : 8f); p.r("armR", -2.2f + 2.6f * k, 0, -.3f); hover(p, t, .4f); });
    }
    static Attack knSplit(int lv) {
        return phase("knight_box_splitter_" + lv, 110, knLo(lv), knHi(lv), (c, t) -> {
            if (t >= 12 && t < 96 && (t - 12) % 12 == 0) {
                Vec3 f = toT(c); int mode = lv == 1 ? 0 : c.rnd.nextInt(lv == 3 ? 3 : 2);
                Vec3 d = mode == 0 ? side(f) : mode == 1 ? f : rot(f, Math.PI / 4);
                line(c, c.tpos(), d, 14, .3f, WHITE, 7, 8, 4);
                Vec3 n = side(d);
                for (int k = 0; k < 6; k++) { Vec3 at = c.tpos().add(d.scale(-5 + k * 2));
                    for (int s = -1; s <= 1; s += 2) shot(c, at, n.scale(s * (k % 2 == 0 ? .35 : .18)), .3f, 0xe0d0ff, 3, 8, 30); }
                if (mode == 2) ring(c, c.tpos(), 8, .3, .3f, 0xe0d0ff, 3, 0, 8, 30);
                c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, .6f);
            }
        }, (p, t, m) -> chop(p, (t - 12 + 120) % 12 * 2, 14, 24));
    }
    static Attack knTunnel(int lv) {
        return phase("knight_sword_tunnel_" + lv, 120, knLo(lv), knHi(lv), (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            double gap = lv == 3 ? 1.1 : 1.6, shift = lv == 3 ? 0 : Math.sin(t * .08) * 1.4;
            Vec3 dir = lv == 2 ? rot(f, t * .01) : f, dr = side(dir);
            if (t < 90 && t % 4 == 0) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = c.tpos().add(dir.scale(-9)).add(dr.scale(s * gap + shift));
                bolt(c, at, dir.scale(.6), .25f, 1.4f, 0xd8d8f0, 5, 2, 30);
            }
            if (t == 94) for (int k = -3; k <= 3; k++) { Vec3 at = c.tpos().add(r.scale(k * 1.2)).add(f.scale(-7)); line(c, at.add(f.scale(7)), f, 14, .25f, KNRED, 6, 12, 4); }
        }, (p, t, m) -> { raise(p, ramp(t, 0, 10) * .7f); p.r("body", 0, sin(t, .08f) * .4f, 0); hover(p, t, .5f); });
    }
    static Attack knSpin(int lv) {
        int[] cuts = lv == 1 ? new int[]{1, 2, 2, 3, 3, 4} : lv == 2 ? new int[]{3, 3, 4, 4, 4, 4} : new int[]{3, 4, 4, 4, 4, 4};
        return phase("knight_rotating_slash_" + lv, 112, knLo(lv), knHi(lv), (c, t) -> {
            if (t >= 10 && (t - 10) % 15 == 0 && (t - 10) / 15 < 6) {
                int n = cuts[(t - 10) / 15]; double base = c.rnd.nextDouble() * Math.PI;
                for (int k = 0; k < n; k++) { double a = base + k * Math.PI / n; line(c, c.tpos(), new Vec3(Math.cos(a), 0, Math.sin(a)), 14, .3f, WHITE, 7, 10, 3); }
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .8f);
            }
            if (lv == 3 && t >= 100 && t % 2 == 0) { double a = t * .5; line(c, c.tpos().add(Math.cos(a) * 2, 0, Math.sin(a) * 2), new Vec3(Math.cos(a + 1.2), 0, Math.sin(a + 1.2)), 8, .25f, WHITE, 6, 8, 2); }
        }, (p, t, m) -> { float k = ((t - 10 + 150) % 15) / 15f; p.r("armR", -2.6f + 3.2f * k, 0, -.4f).r("body", 0, k * 1.2f, 0); });
    }
    static Attack knRoaring() {
        return phase("knight_roaring", 170, -1, .15f, (c, t) -> {
            if (t == 1) { c.sound(SoundEvents.ENDER_DRAGON_GROWL, 2, .5f); c.sound(SoundEvents.WARDEN_ROAR, 2, .7f); }
            if (t >= 10 && t < 60 && t % 2 == 0) { Vec3 at = around(c, 9, t * .5, 1 + Math.sin(t) * 2); // stars fly to the Knight
                c.proj().at(at).vel(c.eye().subtract(at).normalize().scale(.25)).box(.5f, .5f, .5f).color(KN).dmg(4).life(30).spawn(); }
            if (t >= 64 && t < 104 && t % 2 == 0) { double a = t * .6; // spiral out
                for (int k = 0; k < 3; k++) { double b = a + k * Math.PI * 2 / 3; shot(c, c.eye(), new Vec3(Math.cos(b) * .35, -.04, Math.sin(b) * .35), .45f, KN, 5, 0, 40); } }
            if (t == 110) { freeze(c, KN); retarget(c, KN, -.08); }
            if (t == 130) { for (var pr : c.level.getEntitiesOfClass(com.undeltarune.entity.UtProjectile.class, c.me.getBoundingBox().inflate(40), pr -> pr.ownerId == c.me.getId() && pr.color() == KN)) {
                    Vec3 at = pr.position(); pr.discard(); for (int k = 0; k < 6; k++) { double a = k * Math.PI / 3; bolt(c, at, new Vec3(Math.cos(a), 0, Math.sin(a)).scale(.3), .2f, .5f, KNRED, 5, 0, 20); } }
                c.sound(SoundEvents.GENERIC_EXPLODE, 1.5f, .6f); }
            if (t == 150) { line(c, c.tpos(), toT(c), 30, 1.2f, WHITE, 12, 12, 6); c.sound(SoundEvents.WARDEN_SONIC_BOOM, 1.5f, .8f); }
        }, (p, t, m) -> { float s = ramp(t, 0, 10); raise(p, s); p.r("head", -.5f * s, 0, 0).o("body", 0, 2 * s + sin(t, .6f) * .4f, 0).r("hornL", 0, 0, .3f * s).r("hornR", 0, 0, -.3f * s); });
    }

    // =====================================================================================================
    // SHADOW MANTLE HOLDER - Sword Route secret boss: flowing black cloak, orange fanged mouth, horn-like collar
    // =====================================================================================================
    static CharacterDef shadowMantle() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 9, 0, 10, 14, 7, 0x0a0a0a).box("hem", "body", 0, -7.5f, 0, 12, 3, 9, 0x0a0a0a)
            .box("head", "body", 0, 8, 0, 8, 4, 7, 0x0a0a0a)
            .box("collarL", "head", -5, 3, 0, 2, 5, 4, 0x0a0a0a).box("collarR", "head", 5, 3, 0, 2, 5, 4, 0x0a0a0a)
            .glow("mouth", "body", 0, 2, -3.6f, 5, 2, .4f, 0xff8a1a).glow("fangL", "mouth", -1.5f, .6f, -.2f, .6f, .9f, .3f, WHITE)
            .glow("fangR", "mouth", 1.5f, .6f, -.2f, .6f, .9f, .3f, WHITE)
            .limb("armL", "body", -6, 5, 0, 2.5f, 10, 3, 0x0a0a0a).limb("armR", "body", 6, 5, 0, 2.5f, 10, 3, 0x0a0a0a);
        return tv("shadow_mantle_holder", "Shadow Mantle Holder").hp(200).boss().egg(0x0a0a0a, 0xff8a1a).size(.9f, 1.8f).keep(7).speed(.3f)
            .model(m).attacks(smHexOrbs(), smBombs(), smRam(), smFriends(), smTriBeam()).build();
    }
    static Attack smHexOrbs() {
        return atk("shadow_mantle_hex_orbs", 100, (c, t) -> {
            if (t < 80) { double a = t * .08; c.dash(side(toT(c)).scale(Math.sin(a) * .25).add(toT(c).scale(Math.sin(2 * a) * .12))); }
            if (t >= 10 && t < 90 && (t - 10) % 20 == 0) { ring(c, c.eye(), 6, .3, .45f, 0xff9a3a, 4, t * .3, 3, 40); c.sound(SoundEvents.BLAZE_SHOOT, .8f, 1.2f); }
        }, (p, t, m) -> { hover(p, t, 1); p.r("collarL", 0, 0, sin(t, .4f) * .3f).r("collarR", 0, 0, -sin(t, .4f) * .3f); });
    }
    static Attack smBombs() {
        return atk("shadow_mantle_bombs", 100, (c, t) -> {
            if ((t == 10 || t == 50)) for (int k = 0; k < 3 + c.rnd.nextInt(2); k++) {
                Vec3 v = c.target.position().add(jit(c, 3)).subtract(c.eye()).multiply(.04, 0, .04).add(0, .3, 0);
                c.proj().at(c.eye()).vel(v).box(.6f, .6f, .6f).color(0x262626).dmg(3).gravity(.03f).life(26).spawn(); c.sound(SoundEvents.TNT_PRIMED, .8f, 1.4f); }
            if (t == 36 || t == 76) for (var pr : c.level.getEntitiesOfClass(com.undeltarune.entity.UtProjectile.class, c.me.getBoundingBox().inflate(32), pr -> pr.ownerId == c.me.getId() && pr.color() == 0x262626)) {
                Vec3 at = pr.position(); pr.discard(); for (int k = 0; k < 4; k++) { double a = k * Math.PI / 2;
                    bolt(c, at, new Vec3(Math.cos(a), 0, Math.sin(a)).scale(.3), .35f, .9f, 0xff6a1a, 4, 0, 24); }
                fx(c, ParticleTypes.EXPLOSION, at, 1, 0); c.sound(SoundEvents.GENERIC_EXPLODE, .6f, 1.3f); }
        }, (p, t, m) -> { p.r("head", -.4f * env((t % 40) / 40f, .1f, .25f, .3f, .45f), 0, 0); hover(p, t, .8f); });
    }
    static Attack smRam() {
        return atk("shadow_mantle_ram", 100, (c, t) -> {
            int n = c.me.hpFrac() > .5f ? 3 : 4;
            if (t >= 10 && (t - 10) % 20 == 0 && (t - 10) / 20 < n) {
                Vec3 f = toT(c), r = side(f), at = c.tpos().add(r.scale(-8)).add(0, 2.5, 0);
                c.proj().at(at).vel(r.scale(.45).add(0, -.1, 0)).box(1.4f, 1.6f, 1.4f).color(0x0a0a0a).dmg(6).warn(6).life(40).pierce().spawn();
                c.sound(SoundEvents.PHANTOM_SWOOP, 1, .8f);
            }
            if (t > 16 && t % 2 == 0) for (var pr : c.level.getEntitiesOfClass(com.undeltarune.entity.UtProjectile.class, c.me.getBoundingBox().inflate(32), pr -> pr.ownerId == c.me.getId() && pr.color() == 0x0a0a0a))
                shot(c, pr.position().add(0, -.6, 0), Vec3.ZERO, .4f, 0xff5a10, 3, 0, 22);
        }, (p, t, m) -> { float k = ((t + 10) % 20) / 20f; bow(p, k); p.o("body", 0, 0, -k * 2); });
    }
    /** Summons 5 FRIENDs (little smiling shades that dart at the SOUL). */
    static Attack smFriends() {
        return atk("shadow_mantle_summon_friend", 110, (c, t) -> {
            if (t >= 10 && t < 35 && t % 5 == 0) { Vec3 at = around(c, 5, t, 1.5); prop(c, at, c.aim(at), .7f, .7f, .7f, 0xffffff, 10);
                shot(c, at, Vec3.ZERO, .55f, 0x2a2a2a, 4, 10, 80); fx(c, ParticleTypes.SMOKE, at, 8, .3); }
            if (t > 40 && t < 100) steer(c, 0x2a2a2a, .025, .28);
            if (t == 40) c.sound(SoundEvents.VEX_CHARGE, 1, 1);
        }, (p, t, m) -> raise(p, env(t, 0, 10, 35, 45)));
    }
    static Attack smTriBeam() {
        return atk("shadow_mantle_tri_beam", 110, (c, t) -> {
            if (t >= 16 && t < 96 && t % 4 == 0) for (int k = 0; k < 3; k++) { double a = -t * .05 + k * Math.PI * 2 / 3;
                Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a)); for (int j = 1; j < 6; j++) shot(c, c.eye().add(d.scale(j * 1.4)).add(0, -.6, 0), Vec3.ZERO, .4f, 0xff9a3a, 4, 0, 5); }
            if (t >= 20 && t % 18 == 0) { Vec3 v = c.aim(c.eye()).scale(.2).add(0, .2, 0); c.proj().at(c.eye()).vel(v).box(.6f, .6f, .6f).color(0x262626).dmg(5).gravity(.025f).life(40).spawn(); }
            if (t == 16) c.sound(SoundEvents.BEACON_POWER_SELECT, 1, .6f);
        }, (p, t, m) -> { raise(p, ramp(t, 4, 14)); p.r("body", 0, -t * .05f, 0); });
    }

    // =====================================================================================================
    // GUEI - grounded spectre: white cross-faced sphere head, blue ruff, long clawed bird limbs, wisps
    // =====================================================================================================
    static CharacterDef guei() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 13, 0, 5, 7, 4, 0x2a3a6a).box("ruff", "body", 0, 3.5f, 0, 8, 1.5f, 7, 0x4a7ae0)
            .box("head", "body", 0, 7.5f, 0, 6, 6, 6, 0xf4f4f4).glow("crossH", "head", 0, 0, -3.1f, 5, .7f, .3f, 0x101010)
            .glow("crossV", "head", 0, 0, -3.1f, .7f, 5, .3f, 0x101010).box("spike", "head", 0, 4, 0, 1, 2.5f, 1, 0x101010)
            .glow("wispL", "head", -5, 2, 0, 1.4f, 1.8f, 1.4f, 0x7fdfff).glow("wispR", "head", 5, 2, 0, 1.4f, 1.8f, 1.4f, 0x7fdfff)
            .limb("armL", "body", -3.5f, 3, 0, 1.2f, 12, 1.2f, 0x2a3a6a).limb("armR", "body", 3.5f, 3, 0, 1.2f, 12, 1.2f, 0x2a3a6a)
            .box("clawL", "armL", 0, -12.5f, -1, 2.5f, 1, 3, 0xe0e0e0).box("clawR", "armR", 0, -12.5f, -1, 2.5f, 1, 3, 0xe0e0e0)
            .limb("legL", null, -1.5f, 9.5f, 0, 1.2f, 9.5f, 1.2f, 0x2a3a6a).limb("legR", null, 1.5f, 9.5f, 0, 1.2f, 9.5f, 1.2f, 0x2a3a6a);
        return tv("guei", "Guei").hp(48).egg(0x2a3a6a, 0x7fdfff).size(.6f, 2.2f).keep(5).model(m).attacks(gueiClaw(), gueiFlames()).build();
    }
    static Attack gueiClaw() {
        return atk("guei_claw_drop", 90, (c, t) -> {
            if (t >= 10 && t < 80 && t % 14 == 10) {
                Vec3 at = c.target.position().add(c.target.getDeltaMovement().scale(8));
                for (int k = -1; k <= 1; k++) pillar(c, at.add(side(toT(c)).scale(k * .7)), .35f, 2.6f, 0xe8e8e8, 5, 12, 5);
                c.sound(SoundEvents.PHANTOM_BITE, .8f, 1.2f);
            }
        }, (p, t, m) -> chop(p, (t + 4) % 14 * 1.5f, 10, 20));
    }
    static final int WISP = 0x5fcfff;
    static Attack gueiFlames() {
        return atk("guei_haunted_flames", 90, (c, t) -> {
            if (t >= 8 && t < 40 && t % 6 == 2) { Vec3 at = around(c, 5, t * .7, 1.2); shot(c, at, Vec3.ZERO, .5f, WISP, 4, 10, 80); fx(c, ParticleTypes.SOUL_FIRE_FLAME, at, 6, .2); }
            if (t >= 24 && t < 88) steer(c, WISP, .02, .2);
            if (t == 8) c.sound(SoundEvents.SOUL_ESCAPE, 1, 1);
        }, (p, t, m) -> { raise(p, env(t, 0, 8, 40, 50) * .8f); p.o("wispL", sin(t, .2f) * 2, 0, 0).o("wispR", -sin(t, .2f) * 2, 0, 0); });
    }

    // =====================================================================================================
    // BALTHIZARD - censer turtle; dull pink shell, purple underside, smokestack horns/tail
    // =====================================================================================================
    static CharacterDef balthizard() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 6, 0, 12, 7, 10, 0xc78a9a).box("belly", "body", 0, -3, 0, 11, 1.5f, 9, 0x6a3a8a)
            .box("stack1", "body", -3, 4.5f, 0, 1.5f, 3, 1.5f, 0x6a3a8a).box("stack2", "body", 3, 4.5f, 0, 1.5f, 3, 1.5f, 0x6a3a8a)
            .box("tail", "body", 0, 1, 6, 1.6f, 1.6f, 4, 0x6a3a8a)
            .box("head", "body", 0, 2, -7, 6, 6, 5, 0xc78a9a).glow("crossH", "head", 0, 0, -2.6f, 5, .5f, .3f, 0x101010)
            .glow("crossV", "head", 0, 0, -2.6f, .5f, 5, .3f, 0x101010).glow("dot", "head", 0, 0, -2.75f, .8f, .8f, .3f, WHITE)
            .box("hornL", "head", -2.5f, 4, 0, 1.2f, 3, 1.2f, 0x6a3a8a).box("hornR", "head", 2.5f, 4, 0, 1.2f, 3, 1.2f, 0x6a3a8a)
            .limb("legL", null, -4, 3, -2, 3, 3, 3, 0x6a3a8a).limb("legR", null, 4, 3, -2, 3, 3, 3, 0x6a3a8a);
        return tv("balthizard", "Balthizard").hp(52).egg(0xc78a9a, 0x6a3a8a).size(.9f, 1).keep(6).speed(.2f).model(m).attacks(balRing(), balPendulum()).build();
    }
    static Attack balRing() {
        return atk("balthizard_smoke_ring", 90, (c, t) -> {
            if (t == 10 || t == 45) { for (int k = 0; k < 14; k++) { Vec3 at = around(c, 3.2, k * Math.PI * 2 / 14, 0);
                    c.proj().at(at).vel(c.tpos().subtract(at).normalize().scale(.06)).box(.7f, .9f, .7f).color(0xd8a8d8).dmg(3).warn(12).life(40).pierce().spawn(); }
                c.sound(SoundEvents.FIRE_EXTINGUISH, 1, .7f); }
            if (t % 4 == 0) fx(c, ParticleTypes.CAMPFIRE_COSY_SMOKE, c.eye().add(0, .6, 0), 2, .2);
        }, (p, t, m) -> { p.r("head", -.3f * env((t % 35) / 35f, 0, .2f, .4f, .6f), 0, 0); });
    }
    static Attack balPendulum() {
        return atk("balthizard_pendulum", 100, (c, t) -> {
            Vec3 r = side(toT(c)); double sw = Math.sin(t * .12) * 4;
            Vec3 over = c.tpos().add(r.scale(sw)).add(0, 4, 0);
            if (t > 6 && t < 92 && t % 5 == 0) {
                boolean lit = c.me.hpFrac() < .5f;
                c.proj().at(over).vel(new Vec3(0, -.12, 0)).box(.6f, .6f, .6f).color(lit ? 0xff6a1a : 0xc8a0c8).dmg(lit ? 5 : 3).gravity(.02f).life(40).spawn();
                fx(c, lit ? ParticleTypes.FLAME : ParticleTypes.CAMPFIRE_COSY_SMOKE, over, 3, .2);
            }
            if (t % 25 == 0) c.sound(SoundEvents.CHAIN_PLACE, 1, .8f);
        }, (p, t, m) -> p.r("body", 0, 0, sin(t, .12f) * .5f).o("body", sin(t, .12f) * 3, 2 + Math.abs(sin(t, .12f)), 0));
    }

    // =====================================================================================================
    // MIZZLE - holy-water angel; flowing blue dress, twirling cone hat
    // =====================================================================================================
    static final int HOLY = 0x8fd8ff;
    static ModelDef mizzleModel(float k) {
        return new ModelDef()
            .ghost("body", null, 0, 7 * k, 0, 7 * k, 10 * k, 5 * k, 0x3a9fff, .85f)
            .ghost("dress", "body", 0, -4 * k, 0, 10 * k, 4 * k, 8 * k, 0x5ab8ff, .7f)
            .ghost("head", "body", 0, 7 * k, 0, 5.5f * k, 5.5f * k, 5 * k, 0x7fd0ff, .9f)
            .glow("eyeL", "head", -1.2f * k, .4f * k, -2.6f * k, .8f * k, 1.6f * k, .3f, 0x0b2a5a).glow("eyeR", "head", 1.2f * k, .4f * k, -2.6f * k, .8f * k, 1.6f * k, .3f, 0x0b2a5a)
            .box("coneL", "head", -1.5f * k, 4 * k, 0, 2 * k, 3 * k, 2 * k, WHITE).box("coneR", "head", 1.5f * k, 4 * k, 0, 2 * k, 3 * k, 2 * k, WHITE)
            .limb("armL", "body", -2 * k, 4 * k, -1.5f * k, 1.4f * k, 5 * k, 1.4f * k, 0x7fd0ff).limb("armR", "body", 2 * k, 4 * k, -1.5f * k, 1.4f * k, 5 * k, 1.4f * k, 0x7fd0ff);
    }
    static CharacterDef mizzle() {
        return tv("mizzle", "Mizzle").hp(40).egg(0x3a9fff, WHITE).size(.6f, 1.3f).keep(6).model(mizzleModel(1))
            .attacks(holyWater("mizzle_holy_water", .25, 1.2), spotlight("mizzle_spotlight", 1)).build();
    }
    /** Rings of droplets close in on the SOUL with a gap; sway rotates the ring. */
    static Attack holyWater(String id, double spd, double sway) {
        return atk(id, 100, (c, t) -> {
            if (t >= 8 && t < 84 && t % 16 == 8) {
                double gap = c.rnd.nextDouble() * Math.PI * 2, rot = Math.sin(t * .1) * sway * .3; int n = 22;
                for (int k = 0; k < n; k++) { double a = k * Math.PI * 2 / n; if (Math.abs(Math.IEEEremainder(a - gap, Math.PI * 2)) < .5) continue;
                    Vec3 at = around(c, 7, a, 0);
                    shot(c, at, rot(c.tpos().subtract(at).normalize(), rot).scale(spd), .3f, HOLY, 3, 4, (int) (7 / spd) + 4); }
                c.sound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1, 1.3f);
            }
        }, (p, t, m) -> { p.r("armL", -1.2f, .6f, 0).r("armR", -1.2f, -.6f, 0).r("coneL", 0, t * .4f, 0).r("coneR", 0, -t * .4f, 0); hover(p, t, .5f); });
    }
    static Attack spotlight(String id, int tired) {
        return atk(id, 110, (c, t) -> {
            if (t >= 10 && t < 100 && t % 3 == 0) for (int k = 0; k < 4; k++) {
                double r = Math.max(1.4, 5 - t * .035 * tired), a = t * .06 + k * Math.PI / 2;
                Vec3 at = c.target.position().add(Math.cos(a) * r, .05, Math.sin(a) * r);
                c.proj().at(at).box(1.3f + tired * .3f, .1f, 1.3f + tired * .3f).color(0xfff8c0).dmg(3).life(4).pierce().spawn();
            }
            if (t == 10) c.sound(SoundEvents.BEACON_AMBIENT, 1, 1.4f);
        }, (p, t, m) -> { raise(p, ramp(t, 0, 10) * .6f); p.r("coneL", 0, t * .2f, 0).r("coneR", 0, -t * .2f, 0); });
    }

    // =====================================================================================================
    // BIBLIOX - blue book-wizard, page beard, lettered robe
    // =====================================================================================================
}
