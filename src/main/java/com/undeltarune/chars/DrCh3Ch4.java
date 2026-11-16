package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import com.undeltarune.entity.UtProjectile;
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
        out.add(guei()); out.add(balthizard()); out.add(mizzle()); out.add(bibliox()); out.add(jackenstein()); out.add(missMizzle());
        out.add(hammerOfJustice()); out.add(winglade()); out.add(organikk()); out.add(wicabel()); out.add(soundOfJustice());
        out.add(titanSpawn()); out.add(titan()); out.add(smallMike()); out.add(catMike()); out.add(cowboyMike());
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

    // ---- shared Ch4 helpers ----
    /** This attacker's live bullets of one color (colors double as tags). */
    static List<UtProjectile> own(AttackCtx c, int rgb) {
        return c.level.getEntitiesOfClass(UtProjectile.class, c.me.getBoundingBox().inflate(40), p -> p.ownerId == c.me.getId() && p.color() == rgb);
    }
    /** One-tick hazard re-spawned every tick at a moving point: hands, pendulum bobs, centipede segments. */
    static UtProjectile frame(AttackCtx c, Vec3 at, Vec3 face, float w, float h, float l, int rgb, float dmg) {
        return c.proj().at(at).face(face).box(w, h, l).color(rgb).dmg(dmg).life(1).pierce().spawn();
    }
    /** Swaps a bullet for an identical one of another color (bullets can't be re-tinted in place). */
    static UtProjectile recolor(AttackCtx c, UtProjectile k, int rgb, float dmg) {
        UtProjectile n = c.proj().at(k.position()).vel(k.getDeltaMovement()).box(k.w(), k.h(), k.l()).color(rgb).dmg(dmg).life(Math.max(10, k.maxLife() - k.age())).spawn();
        k.discard(); return n;
    }

    // =====================================================================================================
    // BIBLIOX - blue hard-cover book that opens into a stout wizard: page beard, pointed hat
    // =====================================================================================================
    static final int BIB = 0x2f5fc8, PAGE = 0xf4ecd0, TORN = 0xc8e8ff, BFIRE = 0xff7a1a, BUB = 0x9fe8ff, BUB2 = 0xc8f4ff;
    static CharacterDef bibliox() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 8, 0, 9, 11, 6, BIB).box("trim", "body", 0, -5, 0, 9.4f, 1, 6.4f, 0xe8c040)
            .box("coverL", "body", -4.8f, 0, -1.5f, .6f, 11.4f, 6.4f, 0x1c3a80).box("coverR", "body", 4.8f, 0, -1.5f, .6f, 11.4f, 6.4f, 0x1c3a80)
            .box("letters", "body", 0, 1, -3.1f, 6, 5, .2f, PAGE)
            .box("head", "body", 0, 8, 0, 7, 5, 6, 0xd8e4ff)
            .glow("eyeL", "head", -1.6f, .8f, -3.1f, 1, 1, .3f, 0x101830).glow("eyeR", "head", 1.6f, .8f, -3.1f, 1, 1, .3f, 0x101830)
            .box("beard", "head", 0, -3.5f, -2.6f, 6, 6, 1.4f, PAGE).box("beard2", "beard", 0, -3.5f, 0, 4, 2.5f, 1.2f, PAGE)
            .box("hat", "head", 0, 3.5f, 0, 8, 2, 7, BIB).box("hat2", "hat", 0, 2, 0, 5, 2.5f, 5, BIB).box("hat3", "hat2", 0, 2.2f, 0, 2.5f, 2.5f, 2.5f, BIB)
            .glow("star", "hat2", 0, 0, -2.6f, 1.2f, 1.2f, .3f, YEL)
            .limb("armL", "body", -5.5f, 4, 0, 1.8f, 7, 1.8f, BIB).limb("armR", "body", 5.5f, 4, 0, 1.8f, 7, 1.8f, BIB)
            .limb("legL", null, -2, 2.5f, 0, 2, 2.5f, 3, 0x1c3a80).limb("legR", null, 2, 2.5f, 0, 2, 2.5f, 3, 0x1c3a80);
        return tv("bibliox", "Bibliox").hp(70).egg(BIB, PAGE).size(.7f, 2f).keep(6).model(m).attacks(bibTornado(), bibFire(), bibBubbles()).build();
    }
    /** The two spell books that open at either side of the board. */
    static Vec3 bibBook(AttackCtx c, int s) { return c.tpos().add(side(toT(c)).scale(4.5 * s)).add(0, .6, 0); }
    static void bibOpen(AttackCtx c, int t) {
        if (t != 4) return;
        for (int s = -1; s <= 1; s += 2) { Vec3 at = bibBook(c, s);
            prop(c, at, toT(c), 1.4f, 1.8f, .3f, BIB, 90); prop(c, at.add(0, .1, 0), side(toT(c)), .2f, 1.6f, 1.3f, PAGE, 90); fx(c, ParticleTypes.ENCHANT, at, 24, .6); }
        c.sound(SoundEvents.BOOK_PAGE_TURN, 1, .8f);
    }
    static void bibPose(Pose p, float t, float period) {
        float s = ramp(t, 0, 10), k = (t % period) / period, cast = env(k, 0, .3f, .5f, 1);
        p.r("coverL", 0, -1.3f * s, 0).r("coverR", 0, 1.3f * s, 0).r(((int) (t / period)) % 2 == 0 ? "armR" : "armL", -1.6f * cast * s, 0, 0)
            .r("hat", 0, 0, sin(t, .3f) * .1f).r("beard", sin(t, .2f) * .1f, 0, 0);
        hover(p, t, .4f);
    }
    /** Books on either side send alternating arcs of tornado bullets at the SOUL. */
    static Attack bibTornado() {
        return atk("bibliox_tornado_arcs", 96, (c, t) -> {
            bibOpen(c, t);
            if (t >= 16 && t < 84 && t % 8 == 0) {
                int s = (t / 8) % 2 == 0 ? 1 : -1; Vec3 at = bibBook(c, s), d = c.aim(at);
                for (int k = 0; k < 5; k++) shot(c, at, rot(d, (k - 2) * .22 * s).scale(.24 + k * .02), .4f, TORN, 3, 3, 50);
                fx(c, ParticleTypes.CLOUD, at, 6, .3); c.sound(SoundEvents.PHANTOM_FLAP, .6f, 1.6f);
            }
            if (t > 16) for (UtProjectile p : own(c, TORN)) { p.setDeltaMovement(rot(p.getDeltaMovement(), Math.sin(p.age() * .4) * .07));
                if (p.age() % 4 == 0) fx(c, ParticleTypes.CLOUD, p.position(), 1, .05); }
        }, (p, t, m) -> bibPose(p, t, 8));
    }
    /** Books shoot a fire pillar at the SOUL's position; the pillar throws fireballs left and right. */
    static Attack bibFire() {
        return atk("bibliox_fire_pillar", 100, (c, t) -> {
            bibOpen(c, t);
            if (t >= 16 && t < 80 && t % 16 == 0) {
                Vec3 base = c.target.position();
                pillar(c, base, .9f, 6, BFIRE, 5, 14, 6);
                for (int s = -1; s <= 1; s += 2) bolt(c, bibBook(c, s), base.add(0, .5, 0).subtract(bibBook(c, s)).scale(1 / 14.0), .3f, .6f, 0xffc060, 0, 0, 14);
                c.sound(SoundEvents.FIRECHARGE_USE, .8f, .9f);
            }
            Vec3 r = side(toT(c));
            for (UtProjectile b : own(c, BFIRE)) if (b.age() == 14) {
                Vec3 base = b.position().add(0, -2.5, 0);
                for (int s = -1; s <= 1; s += 2) for (int k = 0; k < 3; k++) shot(c, base.add(0, k * .8, 0), r.scale(s * (.2 + k * .05)), .45f, 0xffa040, 3, 0, 40);
                fx(c, ParticleTypes.FLAME, base, 16, .4); c.sound(SoundEvents.BLAZE_SHOOT, .7f, 1.1f);
            }
        }, (p, t, m) -> bibPose(p, t, 16));
    }
    /** Books blow bubbles; some stop and split into three small bubbles that shoot off, slow down and pop. */
    static Attack bibBubbles() {
        return atk("bibliox_bubbles", 100, (c, t) -> {
            bibOpen(c, t);
            if (t >= 14 && t < 80 && t % 6 == 0) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = bibBook(c, s); shot(c, at, rot(c.aim(at), (c.rnd.nextDouble() - .5) * .6).scale(.2), .6f, BUB, 3, 3, 60); }
            for (UtProjectile b : own(c, BUB)) {
                if (b.age() == 22 && c.rnd.nextInt(3) > 0) b.setDeltaMovement(Vec3.ZERO);
                else if (b.age() == 32 && b.getDeltaMovement().lengthSqr() < 1e-4) {
                    Vec3 at = b.position(); b.discard(); double a0 = c.rnd.nextDouble() * Math.PI * 2;
                    for (int k = 0; k < 3; k++) { double a = a0 + k * Math.PI * 2 / 3; shot(c, at, new Vec3(Math.cos(a), 0, Math.sin(a)).scale(.32), .3f, BUB2, 2, 0, 22); }
                    fx(c, ParticleTypes.BUBBLE_POP, at, 6, .3); c.sound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, 1, 1.4f);
                }
            }
            for (UtProjectile b : own(c, BUB2)) b.setDeltaMovement(b.getDeltaMovement().scale(.9));
        }, (p, t, m) -> bibPose(p, t, 6));
    }

    // =====================================================================================================
    // JACKENSTEIN - Dark Zone miniboss: carved pumpkin head, stitched coat, neck bolts, lantern
    // Each turn is one of his canon maze layouts fought in the Darkness (TP-starved), plus Bullet Rain.
    // =====================================================================================================
    static final int PUMP = 0xf07a18, GHOST = 0xe8e8f8, JWALL = 0x3a2a4a, TREASURE = 0xffd23a;
    static CharacterDef jackenstein() {
        ModelDef m = hum(10, 11, 9, 9, 0x3a2a4a, PUMP, 0x6a8a4a, 0x2a1a30)
            .glow("eyeL", "head", -2, 1.2f, -4.65f, 2, 1.6f, .3f, 0xffe04a).glow("eyeR", "head", 2, 1.2f, -4.65f, 2, 1.6f, .3f, 0xffe04a)
            .glow("mouth", "head", 0, -2, -4.65f, 5, 1.2f, .3f, 0xffe04a).box("stem", "head", 0, 5.2f, 0, 1.4f, 2, 1.4f, 0x3f7a2a)
            .box("ridge", "head", 0, 0, 0, 9.3f, 8.6f, 3, 0xd86a10)
            .box("boltL", "body", -5.2f, 5, 0, 1.6f, 1, 1, 0x9a9aa8).box("boltR", "body", 5.2f, 5, 0, 1.6f, 1, 1, 0x9a9aa8)
            .box("stitch", "body", 0, 1, -2.8f, .5f, 8, .3f, 0x101010).box("stitchX", "body", 0, 2, -2.8f, 3, .4f, .3f, 0x101010)
            .box("handle", "armR", 0, -10.5f, -1, .4f, 1.5f, .4f, 0x555555).glow("lantern", "handle", 0, -2, 0, 2, 2.5f, 2, 0xffd060);
        return tv("jackenstein", "Jackenstein").hp(170).boss().egg(PUMP, 0x3a2a4a).size(.7f, 2.4f).keep(7).rest(14)
            .model(m).attacks(jackMaze("starter", 0, 3, 0, .18), jackMaze("3x2", 1, 4, 0, .2), jackMaze("normal", 2, 5, 0, .22),
                jackMaze("chimney", 3, 5, 1, .22), jackMaze("too_long", 4, 6, 0, .24), jackMaze("no_pumpkin", 5, 6, 0, .24),
                jackMaze("two_pumpkins", 6, 5, 2, .24), jackMaze("your_tutu", 7, 7, 1, .26), jackMaze("final_maze", 8, 12, 0, .3), jackRain()).build();
    }
    /** Wall cells of each canon layout on a 7x7 grid around the SOUL (i = sideways, j = depth). */
    static boolean jackWall(int layout, int i, int j) {
        int ai = Math.abs(i), aj = Math.abs(j);
        return switch (layout) {
            case 0 -> ai == 2 && j != 0 || aj == 3 && ai < 2;                    // Starter: two dining rooms + dungeon
            case 1 -> (i == -1 || i == 2) && j != -1 && j != 2 || aj == 3;        // 3x2: six clock rooms
            case 2 -> i % 2 != 0 && j != Math.floorMod(i * 2, 5) - 2;             // Normal: tight vertical corridors
            case 3 -> ai == 2 && j < 2 || i == -3 && j >= 1;                      // Chimney: flue top-left
            case 4 -> ai >= 2 && Math.floorMod(j, 2) == 0;                        // Too Long: gappy tower
            case 5 -> ai % 2 == 0 && aj % 2 == 0;                                 // No Pumpkin: grid of pillars
            case 6 -> j == 1 && i < 2 || j == -1 && i > -2 || aj == 3;            // Two Pumpkins: snake path
            case 7 -> Math.floorMod(i - j, 3) == 0;                               // Your Tutu: diagonal maze
            default -> ai >= 2;                                                   // Final Maze: one long gauntlet
        };
    }
    static Attack jackMaze(String name, int layout, int ghosts, int pumpkins, double gspd) {
        return atk("jackenstein_maze_" + name, 124, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 6) {
                Vec3 o = c.target.position();
                for (int i = -3; i <= 3; i++) for (int j = -3; j <= 3; j++)
                    if (Math.abs(i) + Math.abs(j) > 1 && jackWall(layout, i, j)) pillar(c, o.add(r.scale(i * 1.6)).add(f.scale(j * 1.6)), 1.5f, 2.4f, JWALL, 4, 16, 100);
                c.sound(SoundEvents.WOODEN_DOOR_CLOSE, 1, .6f); c.sound(SoundEvents.AMBIENT_CAVE.value(), .8f, 1);
            }
            if (t % 5 == 0) fx(c, ParticleTypes.LARGE_SMOKE, c.tpos(), 8, 3.5);   // the Darkness closes in
            int every = Math.max(3, 84 / ghosts);
            if (t >= 24 && t < 112 && (t - 24) % every == 0) {
                int lane = c.rnd.nextInt(7) - 3, s = c.rnd.nextBoolean() ? 1 : -1; boolean deep = layout == 8 || c.rnd.nextBoolean();
                Vec3 d = deep ? f : r, n = deep ? r : f, at = c.target.position().add(n.scale(layout == 8 ? lane * .5 : lane * 1.6)).add(d.scale(-6 * s)).add(0, 1, 0);
                shot(c, at, d.scale(gspd * s), .7f, GHOST, 4, 8, (int) (12 / gspd)); fx(c, ParticleTypes.SOUL, at, 4, .3);
            }
            if (pumpkins > 0 && t >= 20 && t < 20 + pumpkins * 30 && (t - 20) % 30 == 0) {
                Vec3 at = c.target.position().add(r.scale(-5)).add(f.scale(c.rnd.nextGaussian())).add(0, .4, 0);
                c.proj().at(at).vel(r.scale(.12).add(0, .3, 0)).box(.9f, .8f, .9f).color(PUMP).dmg(5).gravity(.03f).warn(6).life(90).spawn();
            }
            for (UtProjectile p : own(c, PUMP)) if (p.age() % 20 == 0) { p.setDeltaMovement(new Vec3(p.getDeltaMovement().x, .3, p.getDeltaMovement().z)); c.sound(SoundEvents.SLIME_JUMP, .5f, .8f); }
            for (UtProjectile g : own(c, GHOST)) if (g.age() % 6 == 0) g.setDeltaMovement(g.getDeltaMovement().add(0, Math.sin(g.age() * .5) * .02, 0));
        }, (p, t, m) -> { float s = ramp(t, 0, 8); p.r("armR", -1.9f * s, 0, -.2f).r("armL", -.4f * s, 0, .4f).r("head", 0, sin(t, .08f) * .6f, 0).r("lantern", sin(t, .2f) * .3f, 0, 0); sway(p, t, .08f); });
    }
    static Attack jackRain() {
        return atk("jackenstein_bullet_rain", 100, (c, t) -> {
            if (t >= 10 && t < 88 && t % 2 == 0) rain(c, 3.5, 1, .45f, GHOST, 3, 5, .015f);
            if (t >= 10 && t < 88 && t % 11 == 0) { Vec3 at = c.target.position().add(jit(c, 3)).add(0, 7, 0); prop(c, at, toT(c), .6f, .6f, .6f, TREASURE, 60).grav = .01f; }
            if (t % 5 == 0) fx(c, ParticleTypes.LARGE_SMOKE, c.tpos(), 6, 3.5);
            if (t % 20 == 0) c.sound(SoundEvents.SOUL_ESCAPE, .8f, .8f);
        }, (p, t, m) -> { raise(p, ramp(t, 0, 10)); p.r("head", -.4f * ramp(t, 0, 10), sin(t, .3f) * .2f, 0); });
    }

    // =====================================================================================================
    // MISS MIZZLE - big Mizzle in the HolywaterCooler; true form has a flowing water dress and hair
    // =====================================================================================================
    static CharacterDef missMizzle() {
        ModelDef m = mizzleModel(1.5f).ghost("hair", "head", 0, -3, 3, 9, 15, 3, 0x5ab8ff, .6f)
            .ghost("train", "dress", 0, -1, 2, 16, 3, 13, 0x3a9fff, .5f).glow("blush", "head", 0, -1.2f, -3.95f, 4, .5f, .3f, 0xff9fd8);
        return tv("miss_mizzle", "Miss Mizzle").hp(190).boss().egg(0x3a9fff, 0xff9fd8).size(.9f, 2.2f).keep(7).speed(.2f).rest(14).model(m)
            .attacks(coolerRain("miss_mizzle", 1), coolerRain("miss_mizzle", 2), coolerRain("miss_mizzle", 3), holyWater("miss_mizzle_holy_water", .34, 2.4)).build();
    }

    // =====================================================================================================
    // HAMMER OF JUSTICE - the Old Man (Gerson) in silver armour, pink plume, giant hammer. Green-SOUL shield charts
    // (DrKit.arrows), shells, hammer tosses and slash flurries following the canon 21-turn order, grouped by HP.
    // =====================================================================================================
    static final int SILV = 0xb8bcc8, GSOUL = 0x40e060, SHELL = 0x3fd060;
    static ModelDef gersonModel(int armor, int hair, int trim) {
        return hum(12, 13, 9, 7, armor, armor, armor, trim)
            .glow("visor", "head", 0, .5f, -3.65f, 4, .6f, .3f, 0x101010).glow("slit", "head", 0, -1, -3.65f, .5f, 2.4f, .3f, 0x101010)
            .box("plume", "head", 0, 3, 2, 2.4f, 3, 6, hair).box("plume2", "plume", 0, -4, 2.5f, 2, 8, 2, hair)
            .box("pauldL", "body", -5.6f, 5.6f, 0, 3.6f, 2.4f, 5.4f, armor).box("pauldR", "body", 5.6f, 5.6f, 0, 3.6f, 2.4f, 5.4f, armor)
            .box("haft", "armR", 0, -12, -3, .9f, .9f, 10, 0x6a4a2a).box("hammer", "haft", 0, 0, -5.5f, 5, 4, 3.6f, armor)
            .box("bootL", "legL", 0, -11.5f, -.8f, 3.2f, 2, 4, trim).box("bootR", "legR", 0, -11.5f, -.8f, 3.2f, 2, 4, trim);
    }
    static CharacterDef hammerOfJustice() {
        String a17 = ring8("R", 5, true) + ring8("D", 5, false) + ring8("R", 11, true) + ring8("D", 30, false);
        return tv("hammer_of_justice", "Hammer of Justice").hp(420).boss().egg(SILV, 0xff8ac8).size(.8f, 2.6f).keep(7).rest(12).speed(.3f)
            .model(gersonModel(SILV, 0xff8ac8, 0x7a7e8a)).attacks(
                hojChart("hammer_of_justice_arrows_1", .75f, 2, "U U U L D R", 12, .25f),
                hojChart("hammer_of_justice_arrows_3", .75f, 2, "U U U L D - R R L L R L U U U", 8, .35f),
                hojChart("hammer_of_justice_arrows_4", .7f, 2, "R U D R D U L - R U D L D U L L R", 6, .45f),
                hojChart("hammer_of_justice_arrows_5_eight_way", .6f, 2, "U R U L U R U L U R U L UL UL UR", 8, .38f),
                hojHammerFall(), hojShell(), hojHammers(),
                hojChart("hammer_of_justice_arrows_9", .3f, .75f, "UL UL U U UR UR UL U UR U U UL U UR D DL D DR", 6, .4f),
                hojChart("hammer_of_justice_shells_10", .3f, .65f, "L L L R R R U UL D DR R R R L L L D DR U - - U L - - L R - - R", 6, .4f),
                hojChart("hammer_of_justice_shells_11", .15f, .5f, "U L R D - - U L R D U L DL D DR R UR U - D R L D UL DL DR DR U", 5, .45f),
                hojSlashes(), hojSqueeze(),
                hojChart("hammer_of_justice_cyan_shells_15", .1f, .4f, "U - - U - - U UR L UR U - U L R DR R UL L UR DL U", 6, .45f),
                hojChart("hammer_of_justice_shells_16", .1f, .4f, "L D DR R - L - U D R - D U R UR U - UL DR R UR - U U U U H", 6, .45f),
                hojChart("hammer_of_justice_sweep_17", -1, .35f, a17, 4, .45f),
                hojStorm("hammer_of_justice_storm_18", 32, false), hojStorm("hammer_of_justice_storm_19", 48, true),
                hojChart("hammer_of_justice_cyan_shells_20", -1, .3f, "U R DR D U UR R UR U UL R R UR U UR U L DL R U L R D UL", 5, .5f),
                hojFinale()).build();
    }
    /** n arrow directions walking around the 8-way compass from `from`, clockwise or counterclockwise. */
    static String ring8(String from, int n, boolean cw) {
        String[] o = {"U", "UR", "R", "DR", "D", "DL", "L", "UL"}; int i = List.of(o).indexOf(from); StringBuilder b = new StringBuilder();
        for (int k = 0; k < n; k++) b.append(o[Math.floorMod(i + (cw ? k : -k), 8)]).append(' ');
        return b.toString();
    }
    /** Green-SOUL turn: the SOUL flashes green and arrows (DrKit.arrows) come in on a fixed chart; repeated directions double as shells. */
    static Attack hojChart(String id, float lo, float hi, String seq, int gap, float spd) {
        int n = seqLen(seq);
        return phase(id, 14 + n * gap + 26, lo, hi, (c, t) -> {
            if (t == 2) { fx(c, ParticleTypes.HAPPY_VILLAGER, c.tpos(), 20, .6); c.sound(SoundEvents.ANVIL_LAND, .5f, 1.6f); }
            arrows(c, seq, t, 14, gap, spd, GSOUL, 4);
            if (t >= 14 && (t - 14) % gap == 0 && (t - 14) / gap < n) c.sound(SoundEvents.NOTE_BLOCK_HAT.value(), .6f, 1.2f + spd);
        }, (p, t, m) -> { pointLoop(p, t, "armL", gap * 2); p.r("armR", -.5f, 0, -.2f).r("plume2", sin(t, .3f) * .2f, 0, 0); });
    }
    /** Attack 6: arrows while a hammer slowly descends; it lands (SOUL turns red) and Gerson slashes down the middle. */
    static Attack hojHammerFall() {
        String seq = "D UL D UR D R D L";
        return phase("hammer_of_justice_falling_hammer", 140, .5f, 2, (c, t) -> {
            arrows(c, seq, t, 14, 10, .3f, GSOUL, 4);
            Vec3 tp = c.tpos(), f = toT(c);
            if (t >= 10 && t < 94 && t % 2 == 0) prop(c, tp.add(0, 9 - (t - 10) * .07, 0), f, 2.6f, 1.8f, 1.6f, SILV, 2);
            if (t == 94) { line(c, tp.add(0, 3, 0), new Vec3(0, -1, 0).add(f.scale(.01)), 3, 2.2f, SILV, 6, 6, 6); fx(c, ParticleTypes.EXPLOSION, c.target.position(), 2, .3); c.sound(SoundEvents.ANVIL_LAND, 1.2f, .6f); }
            if (t == 106) { line(c, c.tpos(), f, 12, .6f, WHITE, 6, 10, 5); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .6f); }
        }, (p, t, m) -> { if (t < 90) pointLoop(p, t, "armL", 20); chop(p, t - 96, 10, 34); });
    }
    /** Attack 7: a shell ricochets off the board's edges leaving stars; Gerson bats it back in, then slams it down for a star fountain. */
    static Attack hojShell() {
        return phase("hammer_of_justice_shell_volley", 130, .3f, .8f, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 10) { c.proj().at(c.tpos().add(r.scale(-5))).vel(r.scale(.3).add(f.scale(.14))).box(.9f, .6f, .9f).color(SHELL).dmg(5).life(110).pierce().spawn(); c.sound(SoundEvents.TURTLE_SHAMBLE, 1, .8f); }
            for (UtProjectile s : own(c, SHELL)) {
                Vec3 o = s.position().subtract(c.tpos()), v = s.getDeltaMovement();
                if (Math.abs(o.dot(f)) > 3 && Math.signum(o.dot(f)) == Math.signum(v.dot(f))) v = v.subtract(f.scale(2 * v.dot(f)));
                if (Math.abs(o.dot(r)) > 5.5 && Math.signum(o.dot(r)) == Math.signum(v.dot(r))) { v = rot(v.subtract(r.scale(2 * v.dot(r))), (c.rnd.nextDouble() - .5) * .8); c.sound(SoundEvents.ANVIL_PLACE, .6f, 1.5f); }
                if (t == 96) { v = Vec3.ZERO; s.setPos(c.tpos().add(0, 4, 0)); fx(c, ParticleTypes.CRIT, s.position(), 10, .3); }
                if (t == 104) v = new Vec3(0, -.6, 0);
                s.setDeltaMovement(v);
                if (t % 2 == 0 && t < 96) shot(c, s.position(), Vec3.ZERO, .25f, STAR, 3, 0, 30);
                if (t == 110) { Vec3 at = new Vec3(s.getX(), c.target.getY() + .2, s.getZ()); s.discard();
                    for (int k = 0; k < 18; k++) { double a = k * Math.PI / 9;
                        c.proj().at(at).vel(new Vec3(Math.cos(a) * .12, .45 + c.rnd.nextDouble() * .15, Math.sin(a) * .12)).box(.3f, .3f, .3f).color(STAR).dmg(3).gravity(.035f).life(40).spawn(); }
                    c.sound(SoundEvents.GENERIC_EXPLODE, .8f, 1.3f); }
            }
        }, (p, t, m) -> { if (t < 96) throwR(p, t % 24, 8, 22); else chop(p, t - 96, 8, 30); });
    }
    static void hojToss(AttackCtx c, Vec3 d, float s, float dmg) {
        Vec3 at = c.tpos().add(d.scale(-6)).add(0, 1, 0);
        c.proj().at(at).vel(d.scale(.28 + c.rnd.nextDouble() * .1).add(0, .32 + c.rnd.nextDouble() * .15, 0)).box(s, s * .8f, s * 1.3f).color(SILV).dmg(dmg).gravity(.03f).warn(3).life(50).spawn();
        c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .5f, 1.6f);
    }
    /** Attacks 8/14: board knocked diagonal, arcing hammer tosses, a volley of bigger ones, then a huge hammer down the center. */
    static Attack hojHammers() {
        return phase("hammer_of_justice_hammer_toss", 124, .2f, .75f, (c, t) -> {
            Vec3 f = toT(c), d = rot(side(f), Math.PI / 4);
            if (t == 4) { c.sound(SoundEvents.ANVIL_LAND, 1, 1.2f); fx(c, ParticleTypes.CRIT, c.tpos(), 20, 1); }
            if (t >= 14 && t < 60 && t % 6 == 0) hojToss(c, d, .5f, 4);
            if (t >= 64 && t < 90 && t % 9 == 1) hojToss(c, d, 1.1f, 6);
            if (t == 96) { pillar(c, c.target.position(), 2.6f, 6, SILV, 9, 16, 6); prop(c, c.target.position().add(0, 8, 0), f, 3.4f, 2.4f, 2.4f, SILV, 18); c.sound(SoundEvents.ANVIL_PLACE, 1, .5f); }
        }, (p, t, m) -> { if (t < 92) throwR(p, t < 64 ? t % 6 * 3 : (t - 1) % 9 * 2, 10, 18); else raise(p, env(t, 92, 100, 108, 118)); });
    }
    /** Attack 12: a flying hammer bats the (red) SOUL aside, slashes on mid/right/left lanes, a two-Gerson fakeout, then the real cut. */
    static Attack hojSlashes() {
        int[] lanes = {0, 1, -1, 0, -1, 1, -1};
        return phase("hammer_of_justice_slash_flurry", 150, .15f, .55f, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 6) { c.proj().at(c.tpos().add(r.scale(7))).vel(r.scale(-.5)).box(1.6f, 1.4f, 1.4f).color(SILV).dmg(6).warn(4).life(20).pierce().spawn(); c.sound(SoundEvents.ANVIL_LAND, 1, .8f); }
            if (t >= 24 && (t - 24) % 11 == 0 && (t - 24) / 11 < lanes.length) { int k = lanes[(t - 24) / 11];
                line(c, c.tpos().add(r.scale(k * 1.8)), f, 12, .6f, WHITE, 6, 9, 4); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .7f + k * .1f); }
            if (t == 110) for (int s = -1; s <= 1; s += 2) { prop(c, c.tpos().add(r.scale(4 * s)), f, .8f, 2.4f, .8f, SILV, 16); prop(c, c.tpos().add(r.scale(4 * s)).add(0, 1.4, 0), f, .5f, 1.2f, .5f, 0xff8ac8, 16); }
            if (t == 126) { line(c, c.tpos(), r, 14, .6f, WHITE, 8, 8, 5); c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, .6f); }
        }, (p, t, m) -> chop(p, (t + 86) % 11 * 2, 12, 22));
    }
    /** Attack 13: board squashed thin and wide; slash runs left->right, right->left, then wide cuts with one small safe gap. */
    static Attack hojSqueeze() {
        return phase("hammer_of_justice_squeeze_slashes", 140, .15f, .55f, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t >= 12 && t < 52 && t % 4 == 0) line(c, c.tpos().add(r.scale(-5 + (t - 12) * .2)), f, 4, .4f, WHITE, 5, 8, 3);
            if (t >= 56 && t < 96 && t % 4 == 0) line(c, c.tpos().add(r.scale(5 - (t - 56) * .2)), f, 4, .4f, WHITE, 5, 8, 3);
            if (t >= 100 && t < 132 && t % 10 == 0) { double safe = (c.rnd.nextDouble() * 2 - 1) * 3.5;
                for (int k = -5; k <= 5; k++) if (Math.abs(k - safe) > 1.2) line(c, c.tpos().add(r.scale(k)), f, 4, .7f, WHITE, 5, 10, 3);
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .5f); }
            if (t % 4 == 0 && t >= 12 && t < 96) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .4f, 1.6f);
        }, (p, t, m) -> { chop(p, t % 8 * 2, 6, 16); p.r("body", 0, t < 56 ? -.4f : .4f, 0); });
    }
    /** Attacks 18/19: arrows from random directions - 32 accelerating, or 48 slow but packed tight. */
    static final String[] DIR8 = {"L", "R", "U", "D", "UL", "UR", "DL", "DR"};
    static Attack hojStorm(String id, int n, boolean dense) {
        int gap = dense ? 3 : 4;
        return phase(id, 16 + n * gap + 24, -1, .3f, (c, t) -> {
            if (t < 16 || (t - 16) % gap != 0 || (t - 16) / gap >= n) return;
            int i = (t - 16) / gap;
            arrows(c, DIR8[c.rnd.nextInt(8)], 0, 0, 1, dense ? .2f : .25f + .35f * i / n, GSOUL, 4);
            if (i % 2 == 0) c.sound(SoundEvents.NOTE_BLOCK_HAT.value(), .5f, 1.2f + .8f * i / n);
        }, (p, t, m) -> { pointLoop(p, t, "armL", 8); pointLoop(p, t + 4, "armR", 8); });
    }
    /** Attack 21: shells (incl. a fast red one), a hammer from the left, then slashes sweeping counterclockwise from the top-right. */
    static Attack hojFinale() {
        return phase("hammer_of_justice_final_verdict", 170, -1, .25f, (c, t) -> {
            arrows(c, "U L UR R R - U L UR R R U - R R", t, 10, 5, .4f, GSOUL, 4);
            Vec3 f = toT(c), r = side(f);
            if (t == 86) { c.proj().at(c.tpos().add(r.scale(-7))).vel(r.scale(.5)).box(1.6f, 1.4f, 1.4f).color(SILV).dmg(6).warn(6).life(22).pierce().spawn(); c.sound(SoundEvents.ANVIL_LAND, 1, .7f); }
            if (t >= 100 && t < 162 && t % 4 == 0) { Vec3 d = rot(f.add(r).normalize(), (t - 100) * .16);
                line(c, c.tpos().add(d.scale(2.2)), d, 4.4f, .5f, WHITE, 6, 8, 3); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .7f, 1 + (t - 100) * .01f); }
        }, (p, t, m) -> { if (t < 96) pointLoop(p, t, "armL", 10); else { p.r("armR", -2.2f, 0, 0).r("body", 0, (t - 100) * .16f, 0); } });
    }

    // =====================================================================================================
    // WINGLADE - living quill pen: white blade body, wings, halo, single eye ("Halo, goodeye!")
    // =====================================================================================================
    static final int HALO = 0xffe680, BLADE = 0xe8f0ff, VGREEN = 0x60ff70;
    static CharacterDef winglade() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 12, 0, 3, 12, 2, BLADE).box("nib", "body", 0, -8, 0, 1.6f, 4, 1.6f, 0x9aa8c8).box("tip", "nib", 0, -2.6f, 0, .6f, 1.6f, .6f, 0x40485a)
            .box("head", "body", 0, 8, 0, 4, 4, 3, BLADE).glow("eye", "head", 0, 0, -1.6f, 1.6f, 1.6f, .3f, 0x2a6aff)
            .glow("halo", "head", 0, 4, 0, 6, .5f, 6, HALO)
            .box("wingL", "body", -5, 3, 1, 7, 5, .6f, WHITE).box("wingR", "body", 5, 3, 1, 7, 5, .6f, WHITE)
            .box("featherL", "wingL", -2, -3.5f, 0, 3, 3, .5f, 0xd8e0f0).box("featherR", "wingR", 2, -3.5f, 0, 3, 3, .5f, 0xd8e0f0);
        return tv("winglade", "Winglade").hp(50).egg(BLADE, HALO).size(.6f, 1.6f).keep(6).model(m).attacks(wingHoop(), wingVvvvv()).build();
    }
    /** Hoop Attack: circles the board and shoots hoops (rings of bullets) aimed at the SOUL. */
    static Attack wingHoop() {
        return atk("winglade_hoop", 100, (c, t) -> {
            Vec3 at = around(c, 5.5, t * .09, 1);
            if (t % 2 == 0) prop(c, at, c.aim(at), 1.2f, .2f, 1.2f, HALO, 2);
            if (t >= 14 && t < 90 && t % 12 == 2) { Vec3 d = c.aim(at), u = side(flat(d)), w = d.cross(u).normalize();
                for (int k = 0; k < 10; k++) { double b = k * Math.PI / 5; shot(c, at.add(u.scale(Math.cos(b) * .9)).add(w.scale(Math.sin(b) * .9)), d.scale(.3), .22f, HALO, 3, 4, 34); }
                fx(c, ParticleTypes.END_ROD, at, 6, .3); c.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1, 1.5f); }
        }, (p, t, m) -> { p.r("wingL", 0, sin(t, .5f) * .5f, 0).r("wingR", 0, -sin(t, .5f) * .5f, 0).r("halo", 0, t * .2f, 0).r("body", 0, t * .09f, 0); hover(p, t, 1); });
    }
    /** VVVVV Attack: rears back, flashes green, then stabs - zig-zag blade waves cross the board. */
    static Attack wingVvvvv() {
        return atk("winglade_vvvvv", 100, (c, t) -> {
            int k = (t - 10) % 22;
            if (t < 10 || t >= 98) return;
            if (k == 0) { fx(c, ParticleTypes.HAPPY_VILLAGER, c.eye(), 14, .5); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, 2); }
            if (k == 10) { Vec3 at = c.eye().add(toT(c)), d = c.aim(at);
                for (int j = 0; j < 5; j++) bolt(c, at.add(side(flat(d)).scale((j - 2) * .5)), rot(d, (j % 2 == 0 ? 1 : -1) * .12).scale(.5 + j * .02), .2f, 1.4f, VGREEN, 4, 2 + j, 30);
                c.sound(SoundEvents.TRIDENT_THROW, 1, 1.4f); c.dash(toT(c).scale(.4)); }
        }, (p, t, m) -> { float k = (t + 210) % 22, back = env(k, 0, 8, 9, 10), stab = env(k, 9, 11, 14, 20);
            p.r("body", .5f * back - .7f * stab, 0, 0).o("body", 0, 0, 2 * back - 3 * stab).r("wingL", 0, .6f * back, 0).r("wingR", 0, -.6f * back, 0); hover(p, t, .4f); });
    }

    // =====================================================================================================
    // ORGANIKK - robed organ Darkner: long pink/purple robes, mask of yellow flute pipes
    // =====================================================================================================
    static final int ROBE = 0xb04a9a, PIPE = 0xf0c840, NOTE = 0xf8f0ff, NOTE8 = 0xffd0f0, OBEAM = 0xff9ae0;
    static CharacterDef organikk() {
        ModelDef m = hum(3, 14, 8, 7, ROBE, 0x2a1030, ROBE, 0x7a2a6a)
            .box("hem", "body", 0, -7, 0, 10, 3, 6.4f, 0x7a2a6a).box("hood", "head", 0, 1, .8f, 8, 8, 7, ROBE);
        for (int k = 0; k < 5; k++) m.box("pipe" + k, "head", -2.4f + k * 1.2f, 1 - Math.abs(k - 2) * .6f, -3.7f, .9f, 4.5f + (2 - Math.abs(k - 2)), .6f, PIPE);
        return tv("organikk", "Organikk").hp(55).egg(ROBE, PIPE).size(.7f, 1.8f).keep(6).model(m).attacks(orgBeams(), orgSheet()).build();
    }
    /** Vertical beams span the board; each beam sounds notes that travel across it in opposite directions. */
    static Attack orgBeams() {
        return atk("organikk_pipe_beams", 100, (c, t) -> {
            Vec3 r = side(toT(c));
            if (t >= 10 && t < 84 && t % 16 == 10) { double off = c.rnd.nextDouble() * 2 - 1;
                for (int k = -1; k <= 1; k++) pillar(c, c.target.position().add(r.scale(k * 3 + off)), .5f, 4, OBEAM, 4, 12, 8);
                c.sound(SoundEvents.NOTE_BLOCK_FLUTE.value(), 1, .7f + c.rnd.nextFloat() * .6f); }
            for (UtProjectile b : own(c, OBEAM)) if (b.age() == 12) { Vec3 base = b.position(); int s = c.rnd.nextBoolean() ? 1 : -1;
                for (int y = -1; y <= 1; y++) shot(c, base.add(0, y * 1.1, 0), r.scale(s * (y == 0 ? .22 : -.22)), .35f, NOTE, 3, 0, 40);
                fx(c, ParticleTypes.NOTE, base, 3, .5); }
        }, (p, t, m) -> { float s = ramp(t, 0, 8), k = ((t + 6) % 16) / 16f; p.r("armL", -1.2f * s - .5f * env(k, 0, .2f, .4f, .8f), 0, .3f).r("armR", -1.2f * s - .5f * env(k, .1f, .3f, .5f, .9f), 0, -.3f); sway(p, t, .1f); });
    }
    /** Sheet music over the board; quarter/half notes fall straight, eighth notes wave as they fall. */
    static Attack orgSheet() {
        return atk("organikk_sheet_music", 110, (c, t) -> {
            Vec3 f = toT(c), r = side(f), top = c.target.position().add(0, 5, 0);
            if (t == 6) { for (int k = 0; k < 5; k++) prop(c, top.add(0, k * .35, 0), r, .06f, .06f, 9, 0x1a1a1a, 100); c.sound(SoundEvents.BOOK_PAGE_TURN, 1, 1.2f); }
            if (t >= 16 && t < 96 && t % 4 == 0) { boolean eighth = c.rnd.nextInt(3) == 0;
                Vec3 at = top.add(r.scale((c.rnd.nextDouble() * 2 - 1) * 4)).add(f.scale(c.rnd.nextGaussian() * .8)).add(0, c.rnd.nextInt(5) * .35, 0);
                c.proj().at(at).vel(new Vec3(0, eighth ? -.12 : -.16, 0)).box(.4f, .5f, .3f).color(eighth ? NOTE8 : NOTE).dmg(3).warn(4).life(60).spawn(); }
            if (t % 2 == 0) for (UtProjectile n : own(c, NOTE8)) n.setDeltaMovement(r.scale(Math.cos(n.age() * .25) * .15).add(0, -.12, 0));
            if (t % 8 == 0) c.sound(SoundEvents.NOTE_BLOCK_HARP.value(), .7f, .5f + (t % 32) / 32f);
        }, (p, t, m) -> { p.r("armR", -2 + sin(t, .4f) * .6f, 0, -.3f).r("armL", -1.6f - sin(t, .4f) * .4f, 0, .3f).r("head", 0, sin(t, .2f) * .3f, 0); hover(p, t, .3f); });
    }

    // =====================================================================================================
    // WICABEL - bell witch: sky-blue dress with a bell skirt, witch hat, shaded face
    // =====================================================================================================
    static final int WICA = 0x8fd0f0, BELL = 0xe8c040;
    static CharacterDef wicabel() {
        ModelDef m = hum(2, 8, 6, 6, WICA, 0x1a2a3a, WICA, 0x2a3a5a)
            .glow("eyeL", "head", -1.3f, 0, -3.15f, .9f, .9f, .3f, WHITE).glow("eyeR", "head", 1.3f, 0, -3.15f, .9f, .9f, .3f, WHITE)
            .box("skirt", "body", 0, -5, 0, 11, 5, 11, WICA).box("rim", "skirt", 0, -2.6f, 0, 12, 1, 12, BELL).box("clapper", "skirt", 0, -3.5f, 0, 2, 2, 2, BELL)
            .box("brim", "head", 0, 3.2f, 0, 10, .8f, 10, 0x2a3a6a).box("cone", "brim", 0, 2, .5f, 5, 3.5f, 5, 0x2a3a6a).box("tip", "cone", 0, 2.5f, 1, 2.5f, 2.5f, 2.5f, 0x2a3a6a)
            .box("bow", "brim", 0, .8f, 0, 5.4f, .8f, 5.4f, BELL);
        return tv("wicabel", "Wicabel").hp(55).egg(WICA, BELL).size(.7f, 1.4f).keep(6).model(m).attacks(wicPendulum()).build();
    }
    /** Pendulum Attack: from one of the four corners a pendulum swings at the SOUL; its strike releases a shock wave. */
    static Attack wicPendulum() {
        return atk("wicabel_pendulum", 120, (c, t) -> {
            if (t < 8 || (t - 8) / 36 >= 3) return;
            int sw = (t - 8) / 36, k = (t - 8) % 36, corner = (sw * 3 + c.me.getId()) & 3;
            Vec3 f = toT(c), r = side(f), out = r.scale((corner & 1) == 0 ? -1 : 1).add(f.scale((corner & 2) == 0 ? -1 : 1)).normalize();
            Vec3 pivot = c.tpos().add(out.scale(3.5)).add(0, 5, 0), tgt = c.target.position().add(0, .5, 0).subtract(pivot);
            float e = k <= 20 ? ramp(k, 2, 20) : 1 - ramp(k, 24, 36); e *= e;
            Vec3 dir = out.scale(1 - e).add(tgt.normalize().scale(e)).normalize(), bob = pivot.add(dir.scale(tgt.length()));
            if (k == 0) { c.sound(SoundEvents.BELL_BLOCK, .8f, 1.2f); fx(c, ParticleTypes.ENCHANT, pivot, 16, .5); }
            frame(c, bob, dir, 1.2f, 1.6f, 1.2f, BELL, k < 2 ? 0 : 5);
            if (k % 2 == 0) for (int j = 1; j < 5; j++) prop(c, pivot.add(dir.scale(tgt.length() * j / 5)), dir, .12f, .12f, .12f, 0x8a8a9a, 2);
            if (k == 20) { ring(c, new Vec3(bob.x, c.target.getY() + .3, bob.z), 16, .3, .35f, WICA, 3, sw * .2, 0, 24);
                fx(c, ParticleTypes.SONIC_BOOM, bob, 1, 0); c.sound(SoundEvents.BELL_BLOCK, 1.2f, .7f); }
        }, (p, t, m) -> { float k = ((t + 28) % 36) / 36f; p.r("armR", -2.2f * env(k, 0, .3f, .55f, .8f), 0, -.3f).r("skirt", 0, 0, sin(t, .35f) * .15f).r("clapper", sin(t, .7f) * .4f, 0, 0); sway(p, t, .12f); });
    }

    // =====================================================================================================
    // SOUND OF JUSTICE - brown statue of the Hammer of Justice brought to life; fought in Darkness by sound
    // =====================================================================================================
    static final int SOJ = 0x7a5a3a;
    static CharacterDef soundOfJustice() {
        ModelDef m = gersonModel(SOJ, 0x9a7a5a, 0x5a4028).glow("teeth", "head", 0, -2.2f, -3.65f, 3, .5f, .3f, 0xe8e0d0);
        return tv("sound_of_justice", "Sound of Justice").hp(240).boss().egg(SOJ, WHITE).size(.8f, 2.6f).keep(7).rest(16).speed(.22f)
            .model(m).attacks(sojEcho("sound_of_justice_echo_arrows", "L R U L - U R R L U - L L R U R", false),
                sojEcho("sound_of_justice_echo_smash", "U L R - R U L L - U", true), sojSmashes()).build();
    }
    /** An arrow from left/right/top with a voice cue (pitch tells the side); its flash fades so later arrows are heard, not seen. */
    static void sojArrow(AttackCtx c, String d, int i) {
        Vec3 r = side(toT(c)), from = d.equals("L") ? r.scale(-1) : d.equals("R") ? r : new Vec3(0, 1, 0);
        bolt(c, c.tpos().add(from.scale(6.5)), from.scale(-.35), .28f, 1.1f, i < 6 ? 0xc8a070 : 0x2a2018, 4, Math.max(0, 10 - i * 2), 22);
        c.sound(SoundEvents.NOTE_BLOCK_BELL.value(), 1, d.equals("L") ? .7f : d.equals("R") ? 1.4f : 1f);
    }
    /** White rectangle marks a lane (left/mid/right), then the statue's hammer comes down there. */
    static void sojSmash(AttackCtx c, int lane, int warn) {
        Vec3 f = toT(c), at = c.target.position().add(side(f).scale(lane * 2.2));
        prop(c, at.add(0, .05, 0), f, 2.2f, .08f, 4, WHITE, warn);
        pillar(c, at, 2.2f, 3.5f, SOJ, 6, warn, 5);
        c.sound(SoundEvents.ANVIL_PLACE, .5f, .7f);
    }
    static Attack sojEcho(String id, String seq, boolean smash) {
        String[] k = seq.split(" "); int n = k.length;
        return atk(id, 14 + n * 9 + (smash ? 40 : 20), (c, t) -> {
            if (t % 5 == 0) fx(c, ParticleTypes.LARGE_SMOKE, c.tpos(), 8, 3.5);
            if (t >= 14 && (t - 14) % 9 == 0 && (t - 14) / 9 < n) { int i = (t - 14) / 9; if (!k[i].equals("-")) sojArrow(c, k[i], i); }
            if (smash && t == 14 + n * 9 + 6) { fx(c, ParticleTypes.FLASH, c.tpos(), 1, 0); sojSmash(c, c.rnd.nextInt(3) - 1, 18); }
        }, (p, t, m) -> { pointLoop(p, t, "armL", 18); if (smash) chop(p, t - (14 + seqLen(seq) * 9), 18, 40); });
    }
    /** Red SOUL: indicators and hammer blows in quick succession on random lanes. */
    static Attack sojSmashes() {
        return atk("sound_of_justice_hammer_barrage", 110, (c, t) -> {
            if (t % 5 == 0) fx(c, ParticleTypes.LARGE_SMOKE, c.tpos(), 8, 3.5);
            if (t >= 10 && t < 92 && (t - 10) % 12 == 0) sojSmash(c, c.rnd.nextInt(3) - 1, Math.max(8, 16 - (t - 10) / 12));
        }, (p, t, m) -> chop(p, (t + 2) % 12 * 2, 14, 24));
    }

    // =====================================================================================================
    // TITAN SPAWN - darkness-wrapped embryo shapes (spiky, reptilian, amorphous), black with a white eye
    // =====================================================================================================
    static final int TSP = 0x0a0a0a, TSPRED = 0x5a0410, EVOLVE = 0x1a1a2a, BIGSHOT = 0x140014, TEYE = 0xf4f4f4, VISOR = 0xffe14a;
    static CharacterDef titanSpawn() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 9, 0, 8, 8, 8, TSP).ghost("aura", "body", 0, 0, 0, 11, 11, 11, 0x000000, .45f)
            .box("head", "body", 0, 5, -2, 6, 5, 6, TSP).glow("eye", "head", 0, 0, -3.1f, 1.4f, 1.4f, .3f, TEYE)
            .box("spike1", "body", -4, 3, 0, 2, 4, 1, TSP).box("spike2", "body", 4, 3, 0, 2, 4, 1, TSP).box("spike3", "body", 0, 4, 3, 1, 4, 2, TSP)
            .box("spike4", "body", -4.5f, -2, 1, 3, 1, 1, TSP).box("spike5", "body", 4.5f, -2, -1, 3, 1, 1, TSP)
            .box("tail", "body", 0, -3, 5, 2, 2, 6, TSP).box("tail2", "tail", 0, -1.5f, 3.5f, 1.5f, 3, 1.5f, TSP);
        return CharacterDef.of("titan_spawn", "Titan Spawn", Faction.RUNAWAY_SHADOW).att(Attitude.HOSTILE).hp(45).egg(TSP, TEYE).size(.7f, 1.2f).keep(5).speed(.28f)
            .model(m).attacks(tspSwarm()).build();
    }
    /** Spawns miniature copies around the board that home on the SOUL; red ones can't be purified. */
    static Attack tspSwarm() {
        return atk("titan_spawn_brood", 100, (c, t) -> {
            if (t >= 8 && t < 70 && t % 6 == 2) { Vec3 at = around(c, 6, c.rnd.nextDouble() * Math.PI * 2, c.rnd.nextDouble() * 2 - .5); boolean red = c.rnd.nextInt(4) == 0;
                shot(c, at, Vec3.ZERO, .5f, red ? TSPRED : TSP, red ? 4 : 3, 10, 80); fx(c, ParticleTypes.SQUID_INK, at, 6, .3); }
            if (t > 18) { steer(c, TSP, .012, .16); steer(c, TSPRED, .009, .12); }
            if (t % 16 == 0) c.sound(SoundEvents.SCULK_CLICKING, .8f, .6f);
        }, (p, t, m) -> { float s = sin(t, .5f); p.r("body", 0, t * .05f, s * .15f).o("body", 0, Math.abs(s) * 1.5f, 0).r("spike1", 0, 0, s * .4f).r("spike2", 0, 0, -s * .4f).r("tail", s * .5f, 0, 0); });
    }

    // =====================================================================================================
    // TITAN - Chapter 4 final boss: black body and hands covered in eyes, huge white wings, faceless winged head
    // behind a SAVE-point star visor. Attack tiers follow the canon cycle (Swarm / Centipedes / Hands / Big Shots).
    // =====================================================================================================
    static CharacterDef titan() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 30, 0, 22, 26, 14, TSP)
            .box("head", "body", 0, 19, 0, 12, 12, 10, TSP).box("hole", "head", 0, 0, -5.1f, 6, 7, .3f, 0x000000)
            .glow("visor", "head", 0, 0, -5.6f, 8, 8, .6f, VISOR).glow("visorX", "visor", 0, 0, -.2f, 10, 2, .3f, VISOR).glow("visorY", "visor", 0, 0, -.2f, 2, 10, .3f, VISOR)
            .box("headWingL", "head", -8, 3, 1, 5, 8, 1, WHITE).box("headWingR", "head", 8, 3, 1, 5, 8, 1, WHITE)
            .box("wingL", "body", -20, 8, 4, 20, 22, 1.4f, WHITE).box("wingR", "body", 20, 8, 4, 20, 22, 1.4f, WHITE)
            .box("wingL2", "wingL", -4, -13, 0, 12, 6, 1, 0xdcdcdc).box("wingR2", "wingR", 4, -13, 0, 12, 6, 1, 0xdcdcdc)
            .limb("armL", "body", -14, 10, 0, 5, 26, 5, TSP).limb("armR", "body", 14, 10, 0, 5, 26, 5, TSP)
            .box("handL", "armL", 0, -28, -1, 8, 6, 6, TSP).box("handR", "armR", 0, -28, -1, 8, 6, 6, TSP)
            .glow("handEyeL", "handL", 0, 0, -3.2f, 1.6f, 1.2f, .3f, TEYE).glow("handEyeR", "handR", 0, 0, -3.2f, 1.6f, 1.2f, .3f, TEYE)
            .box("tail", "body", 0, -15, 2, 14, 8, 10, TSP);
        int[][] eyes = {{-7, 6}, {-2, 9}, {4, 5}, {8, -2}, {-5, -4}, {1, -8}, {6, -9}, {-9, -10}, {3, 0}, {-1, 2}};
        for (int k = 0; k < eyes.length; k++) m.glow("eye" + k, "body", eyes[k][0], eyes[k][1], -7.15f, 1.6f, 1.2f, .3f, TEYE);
        return CharacterDef.of("titan", "Titan", Faction.RUNAWAY_SHADOW).att(Attitude.HOSTILE).hp(720).boss().egg(TSP, VISOR)
            .scale(2f).size(3f, 7f).keep(11).rest(14).speed(.16f).model(m).attacks(
                tiSwarm(1), tiCentipedes(1), tiHands(1), tiBigShots(1),
                tiSwarm(2), tiCentipedes(2), tiHands(2), tiBigShots(2),
                tiSwarm(3), tiCentipedes(3), tiHands(3), tiBigShots(3), tiDesperation()).build();
    }
    static float tiLo(int lv) { return lv == 1 ? .66f : lv == 2 ? .33f : -1; }
    static float tiHi(int lv) { return lv == 1 ? 2 : lv == 2 ? .66f : .33f; }
    static void tiPose(Pose p, float t, float arms) { float s = ramp(t, 0, 10);
        p.r("wingL", 0, sin(t, .15f) * .35f, 0).r("wingR", 0, -sin(t, .15f) * .35f, 0).r("armL", -arms * s, 0, .3f * s).r("armR", -arms * s, 0, -.3f * s).r("visorX", 0, 0, t * .05f); hover(p, t, 1.5f); }
    /** Swarm (Harder/Hardest): Titan Spawn gather around the board and chase the SOUL; red ones, evolving ones, desperate ones. */
    static Attack tiSwarm(int lv) {
        return phase(lv == 1 ? "titan_swarm" : lv == 2 ? "titan_swarm_harder" : "titan_swarm_hardest", 120, tiLo(lv), tiHi(lv), (c, t) -> {
            if (t >= 8 && t < 90 && t % (lv == 1 ? 8 : lv == 2 ? 6 : 4) == 0) {
                Vec3 at = around(c, 6.5, c.rnd.nextDouble() * Math.PI * 2, c.rnd.nextDouble() * 2 - .5); int kind = c.rnd.nextInt(4);
                int col = lv < 3 && kind == 0 ? TSPRED : lv >= 2 && kind == 1 ? EVOLVE : TSP;
                shot(c, at, Vec3.ZERO, col == EVOLVE ? .45f : .7f, col, col == TSPRED ? 5 : 4, 10, 110); fx(c, ParticleTypes.SQUID_INK, at, 6, .3);
            }
            if (t > 18) { steer(c, TSP, lv == 3 ? .02 : .012, lv == 3 ? .22 : .13); steer(c, TSPRED, .01, .1); steer(c, EVOLVE, .015, .14); }
            for (UtProjectile e : own(c, EVOLVE)) if (e.age() == 50) { fx(c, ParticleTypes.SQUID_INK, e.position(), 10, .4);
                c.proj().at(e.position()).vel(e.getDeltaMovement().scale(1.6)).box(1, 1, 1).color(TSP).dmg(5).life(60).spawn(); e.discard(); }
            if (t % 20 == 0) c.sound(SoundEvents.SCULK_CLICKING, 1, .5f);
        }, (p, t, m) -> tiPose(p, t, .8f));
    }
    /** Centipedes: segmented chains circle the board and periodically lunge at the SOUL (4 / 6 segments, then three 3-segment ones). */
    static Attack tiCentipedes(int lv) {
        int n = lv == 3 ? 3 : 2, seg = lv == 1 ? 4 : lv == 2 ? 6 : 3; double w = lv == 1 ? .07 : lv == 2 ? .085 : .1;
        return phase(lv == 1 ? "titan_centipedes" : lv == 2 ? "titan_centipedes_harder" : "titan_centipedes_hardest", 120, tiLo(lv), tiHi(lv), (c, t) -> {
            if (t == 2) c.sound(SoundEvents.SILVERFISH_AMBIENT, 1.5f, .4f);
            if (t < 10) return;
            for (int i = 0; i < n; i++) { double base = i * Math.PI * 2 / n + t * w, cyc = ((t + i * 13) % 40) / 40.0, rush = cyc > .6 ? Math.sin((cyc - .6) / .4 * Math.PI) : 0;
                if (cyc > .6 && cyc < .63) c.sound(SoundEvents.SILVERFISH_STEP, 1, .5f);
                for (int s = 0; s < seg; s++) { double a = base - s * .28;
                    Vec3 at = around(c, 5 - 4 * rush + s * .25 * rush, a, .2 + Math.sin(t * .3 + s) * .2);
                    frame(c, at, new Vec3(-Math.sin(a), 0, Math.cos(a)), s == 0 ? 1 : .8f, .8f, s == 0 ? 1.2f : .9f, s == 0 ? 0x1a1a1a : TSP, t < 20 ? 0 : 4);
                    if (s == 0 && t % 3 == 0) prop(c, at.add(0, .45, 0), Vec3.ZERO, .25f, .2f, .25f, TEYE, 2); }
            }
        }, (p, t, m) -> tiPose(p, t, 1.4f));
    }
    /** The Hands: two huge eyed hands bob at the board's sides, then clap shut over most of it (2 / 3 / 4 closes). */
    static Attack tiHands(int lv) {
        int closes = lv + 1;
        return phase(lv == 1 ? "titan_hands" : lv == 2 ? "titan_hands_fast" : "titan_hands_fastest", 110, tiLo(lv), tiHi(lv), (c, t) -> {
            if (t < 6) return;
            Vec3 f = toT(c), r = side(f); int per = 98 / closes; double k = ((t - 6) % per) / (double) per;
            double close = k < .4 ? 0 : k < .6 ? (k - .4) / .2 : k < .75 ? 1 : 1 - (k - .75) / .25, bob = Math.sin(t * .2 * (1 + lv * .3)) * 1.2;
            if (Math.abs(k - .6) < .5 / per) { c.sound(SoundEvents.ANVIL_LAND, 1, .4f); fx(c, ParticleTypes.SQUID_INK, c.tpos(), 20, 1); }
            for (int s = -1; s <= 1; s += 2) { Vec3 at = c.tpos().add(r.scale(s * (6 - 4.6 * close))).add(0, s * bob * (1 - close), 0);
                frame(c, at, f, 2.6f, 4, 3.4f, TSP, close > .1 ? 6 : 0);
                for (int fi = 0; fi < 4; fi++) frame(c, at.add(0, 2.6, 0).add(f.scale(-1.2 + fi * .8)), f, .6f, 1.4f, .6f, TSP, 0);
                if (t % 2 == 0) prop(c, at.add(r.scale(-s * 1.35)), r, .5f, .35f, .1f, TEYE, 2);
                if (close < .05 && t % (12 - lv * 2) == 0) shot(c, at, r.scale(-s * .25), .35f, TSP, 3, 2, 30);
            }
        }, (p, t, m) -> tiPose(p, t, 2.2f));
    }
    /** Big Shots 1-3: spawn trickle in while the Titan charges and fires twin shots of darkness (single, burst of 2, burst of 3). */
    static Attack tiBigShots(int lv) {
        int per = lv == 1 ? 34 : 26, burst = lv == 1 ? 1 : lv;
        return phase("titan_big_shots_" + lv, 130, tiLo(lv), tiHi(lv), (c, t) -> {
            if (t >= 6 && t < 110 && t % (lv == 1 ? 12 : 9) == 0) { Vec3 at = around(c, 6.5, c.rnd.nextDouble() * Math.PI * 2, 0); shot(c, at, Vec3.ZERO, .6f, TSP, 4, 10, 90); }
            if (t > 16) steer(c, TSP, .012, .12);
            if (t < 12 || t >= 118) return;
            int k = (t - 12) % per;
            if (k < per - 8 && k % 2 == 0) fx(c, ParticleTypes.REVERSE_PORTAL, c.eye(), 8, 1.5);
            if (k == per - 10) c.sound(SoundEvents.WARDEN_SONIC_CHARGE, .8f, 1.4f);
            if (k >= per - 6 && (k - (per - 6)) % 3 == 0 && (k - (per - 6)) / 3 < burst) {
                for (int s = -1; s <= 1; s += 2) { Vec3 at = c.eye().add(side(toT(c)).scale(s * 1.6)); shot(c, at, rot(c.aim(at), s * .06).scale(.4), 1.5f, BIGSHOT, 7, 4, 50); }
                c.sound(SoundEvents.WARDEN_SONIC_BOOM, .6f, 1.6f);
            }
        }, (p, t, m) -> { tiPose(p, t, 1); float k = ((t + per * 4 - 12) % per) / per; p.r("head", -.5f * env(k, 0, .7f, .8f, 1), 0, 0); });
    }
    /** Desperation Shots: rapid bursts of 2 or 3 big shots 30 degrees apart, absorbed back for a gigantic laser. */
    static Attack tiDesperation() {
        return phase("titan_desperation_shots", 170, -1, .2f, (c, t) -> {
            if (t >= 10 && t < 100 && t % 9 == 1) { int n = ((t - 10) / 9) % 2 == 0 ? 2 : 3; Vec3 d = c.aim(c.eye());
                for (int k = 0; k < n; k++) shot(c, c.eye(), rot(d, (k - (n - 1) / 2.0) * Math.toRadians(30)).scale(.32), 1.3f, BIGSHOT, 6, 3, 80);
                c.sound(SoundEvents.WARDEN_SONIC_BOOM, .5f, 1.8f); }
            if (t == 104) { for (UtProjectile p : own(c, BIGSHOT)) p.setDeltaMovement(c.eye().subtract(p.position()).normalize().scale(.5)); c.sound(SoundEvents.BEACON_DEACTIVATE, 1.4f, .5f); }
            if (t > 104 && t < 130 && t % 2 == 0) fx(c, ParticleTypes.REVERSE_PORTAL, c.eye(), 20, 2);
            if (t == 130) { for (UtProjectile p : own(c, BIGSHOT)) p.discard(); Vec3 d = c.aim(c.eye());
                line(c, c.eye().add(d.scale(12)), d, 24, 3.2f, BIGSHOT, 12, 16, 14); c.sound(SoundEvents.WARDEN_SONIC_BOOM, 2, .5f); }
        }, (p, t, m) -> { tiPose(p, t, t < 100 ? 1.2f : 2.6f); p.r("head", t > 100 ? -.6f : 0, 0, 0).r("wingL", 0, t > 100 ? -.9f : 0, 0).r("wingR", 0, t > 100 ? .9f : 0, 0); });
    }

    // =====================================================================================================
    // THE MIKES - Tenna's assistants in disguise (Ch4 optional fight): Small Mike (green Pippins), Cat Mike
    // (Shadowguy), Cowboy Hat Mike (Zapper). PC/console attack sets; "All Mikes" attacks split between them.
    // =====================================================================================================
    static final int AUD = 0xd8d8d8, CAT = 0xfafafa, CATRED = 0xff4848, FUR = 0xc8905a, PAW = 0xf4f4f4, BLUECAT = 0x6fb0ff, RECT_TOP = 0xff3030, RECT_LOW = 0xff3232;
    static ModelDef mic(ModelDef m) { return m.box("mic", "armR", 0, -6.5f, -1.2f, .6f, 2.5f, .6f, 0x8a8a8a).box("micHead", "mic", 0, -1.6f, 0, 1.4f, 1.4f, 1.4f, 0x2a2a2a); }
    static CharacterDef smallMike() {
        ModelDef m = mic(hum(4, 6, 6, 8, 0x2f9a40, 0xf4f4f4, 0x181818, 0x181818)
            .glow("pip1", "head", -2, 2, -4.15f, 1.2f, 1.2f, .3f, 0x2fb040).glow("pip2", "head", 2, -2, -4.15f, 1.2f, 1.2f, .3f, 0x2fb040)
            .glow("mouth", "head", 0, -.5f, -4.15f, 3.4f, .7f, .3f, 0x2fb040).ghost("outline", "body", 0, 0, 0, 7, 7, 4.4f, 0x7fff9f, .35f));
        return tv("small_mike", "Small Mike").hp(90).egg(0x2f9a40, 0xf4f4f4).size(.6f, 1.2f).keep(6).model(m)
            .attacks(mikeApplause(), mikeShutUp(), mikeNormalVolume(), mikeUltimate()).build();
    }
    static CharacterDef catMike() {
        ModelDef m = mic(hum(8, 10, 8, 7, 0x0c0c0c, 0x0c0c0c, 0x0c0c0c, 0x0c0c0c)
            .box("earL", "head", -2.4f, 4.4f, 0, 1.8f, 2.2f, 1.2f, 0x0c0c0c).box("earR", "head", 2.4f, 4.4f, 0, 1.8f, 2.2f, 1.2f, 0x0c0c0c)
            .glow("grin", "head", 0, -1.6f, -3.65f, 4.6f, .9f, .3f, WHITE).glow("eyeL", "head", -1.6f, .8f, -3.65f, 1.2f, .5f, .3f, WHITE).glow("eyeR", "head", 1.6f, .8f, -3.65f, 1.2f, .5f, .3f, WHITE)
            .box("whiskL", "head", -3.6f, -.6f, -3.6f, 2.4f, .2f, .2f, WHITE).box("whiskR", "head", 3.6f, -.6f, -3.6f, 2.4f, .2f, .2f, WHITE)
            .box("tail", "body", 0, -4, 3, .8f, .8f, 5, 0x0c0c0c));
        return tv("cat_mike", "Cat Mike").hp(90).egg(0x0c0c0c, WHITE).size(.7f, 2f).keep(6).model(m)
            .attacks(mikeCatBarrage(), mikeSleepingCats(), mikeCatWalls(), mikeBlueCats()).build();
    }
    static CharacterDef cowboyMike() {
        ModelDef m = mic(new ModelDef()
            .box("body", null, 0, 11, 0, 7, 10, 4, 0x1d2a5c).box("head", "body", 0, 9, 0, 5, 8, 4, 0x1d2a5c).glow("light", "head", 0, 1, -2.1f, 2, 1, .3f, 0xff2020)
            .box("brim", "head", 0, 4.4f, 0, 10, .6f, 8, 0x8a5a2a).box("crown", "brim", 0, 1.6f, 0, 5, 2.6f, 5, 0x8a5a2a).box("band", "crown", 0, -.8f, 0, 5.2f, .6f, 5.2f, 0x3a2410)
            .glow("b1", "body", -1.6f, 2, -2.1f, 1.2f, 1.2f, .3f, 0xff3030).glow("b2", "body", 1.6f, 2, -2.1f, 1.2f, 1.2f, .3f, WHITE)
            .glow("b3", "body", -1.6f, -1, -2.1f, 1.2f, 1.2f, .3f, WHITE).glow("b4", "body", 1.6f, -1, -2.1f, 1.2f, 1.2f, .3f, 0xff3030)
            .box("bandana", "body", 0, 4.4f, -1.8f, 5, 1.6f, .6f, 0xd02020)
            .limb("armL", "body", -4.6f, 4, 0, 2, 9, 2, 0x1d2a5c).limb("armR", "body", 4.6f, 4, 0, 2, 9, 2, 0x1d2a5c)
            .limb("legL", null, -1, 6, 0, 2, 6, 2.6f, 0x141c40).limb("legR", null, 1, 6, 0, 2, 6, 2.6f, 0x141c40)
            .box("bootL", "legL", 0, -6, -.6f, 2.4f, 1.4f, 3.4f, 0x6a4020).box("bootR", "legR", 0, -6, -.6f, 2.4f, 1.4f, 3.4f, 0x6a4020));
        return tv("cowboy_mike", "Cowboy Hat Mike").hp(90).egg(0x1d2a5c, 0x8a5a2a).size(.6f, 2.3f).keep(6).model(m)
            .attacks(mikeWhitePaws(), mikeBlackPaws(), mikeWhitePaws2(), mikeSleepingCats2()).build();
    }
    static void mikeDance(Pose p, float t) { hop(p, t, 10, 1.2f); p.r("armR", -1.4f + sin(t, .6f) * .4f, 0, -.2f).r("armL", sin(t, .6f) * .6f, 0, .3f).r("head", 0, sin(t, .3f) * .3f, 0); }

    // ---- Small Mike ----
    /** APPLAUSE: clapping microphones rise from below (a wave from the right on the first beat). */
    static Attack mikeApplause() {
        return atk("small_mike_applause", 100, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t >= 10 && t < 84 && t % 10 == 0) { int gap = c.rnd.nextInt(7) - 3;
                for (int k = -3; k <= 3; k++) if (k != gap) c.proj().at(c.target.position().add(r.scale(k * 1.1)).add(f.scale(c.rnd.nextGaussian() * .6)).add(0, -1, 0))
                    .vel(new Vec3(0, .24, 0)).box(.4f, 1.2f, .4f).color(AUD).dmg(3).warn(8).gravity(.006f).life(40).spawn();
                for (int k = 0; k < 3; k++) c.sound(SoundEvents.NOTE_BLOCK_HAT.value(), .6f, 1.6f + k * .1f); }
            if (t == 20) for (int k = 0; k < 5; k++) bolt(c, c.tpos().add(r.scale(7 + k * 1.2)).add(0, k % 2 * .8 - .4, 0), r.scale(-.3), .4f, .9f, AUD, 3, 6, 50);
        }, (p, t, m) -> { clap(p, t % 10 + 2, 6); hop(p, t, 10, 1.5f); });
    }
    /** SHUT UP!!: shushing audience members drop from above while the music cuts out. */
    static Attack mikeShutUp() {
        return atk("small_mike_shut_up", 100, (c, t) -> {
            Vec3 r = side(toT(c));
            if (t == 4) c.sound(SoundEvents.NOTE_BLOCK_BASS.value(), 1, .5f);
            if (t >= 12 && t < 84 && t % 8 == 0) { double x = (c.rnd.nextDouble() * 2 - 1) * 3.5;
                for (int k = -1; k <= 1; k++) c.proj().at(c.target.position().add(r.scale(x + k * .9)).add(0, 6 + Math.abs(k) * .6, 0)).vel(new Vec3(0, -.2, 0))
                    .box(.5f, 1.3f, .5f).color(AUD).dmg(3).warn(6).gravity(.02f).life(40).spawn(); }
        }, (p, t, m) -> { p.r("armL", -1.8f * ramp(t, 0, 6), .6f, 0).r("head", .2f, 0, 0).r("armR", -.4f, 0, 0); });
    }
    /** NORMAL VOLUME...: audience members stream in from the right, stretched to various lengths. */
    static Attack mikeNormalVolume() {
        return atk("small_mike_normal_volume", 100, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t >= 10 && t < 84 && t % 5 == 0) { float len = 1 + c.rnd.nextInt(4);
                bolt(c, c.tpos().add(r.scale(7)).add(f.scale((c.rnd.nextDouble() * 2 - 1) * 2.5)).add(0, c.rnd.nextDouble() - .5, 0), r.scale(-.3), .5f, len, AUD, 3, 5, 50); }
            if (t % 20 == 0) c.sound(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), .6f, 1);
        }, (p, t, m) -> mikeDance(p, t));
    }
    /** Ultimate Attack (all Mikes): they roll into a ball that drops on the SOUL, shrinking and speeding up each bounce, flanked by two big cats. */
    static Attack mikeUltimate() {
        return phase("small_mike_ultimate_attack", 140, -1, .35f, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 6) for (int s = -1; s <= 1; s += 2) c.proj().at(c.tpos().add(r.scale(4 * s))).box(1.8f, 1.4f, 2).color(CAT).dmg(0).life(130).spawn();
            if (t >= 14 && (t - 14) % 24 == 0 && (t - 14) / 24 < 5) { int i = (t - 14) / 24; float s = 2.6f - i * .4f;
                c.proj().at(c.target.position().add(0, 7, 0)).vel(new Vec3(0, -.1 - i * .06, 0)).box(s, s, s).color(0x2f9a40).dmg(6).gravity(.03f + i * .01f).warn(10).life(40).pierce().spawn();
                c.sound(SoundEvents.SLIME_JUMP, 1, 1.4f - i * .1f); }
            catTick(c);
        }, (p, t, m) -> { raise(p, ramp(t, 0, 10)); p.r("body", (t % 24) * .26f, 0, 0); });
    }

    // ---- cats (shared): white cats can be pet (touch) to vanish; unpet ones turn red and spit furballs; big sleeping cats just calm down ----
    static void cat(AttackCtx c, Vec3 at, Vec3 vel, float s, int life) { c.proj().at(at).vel(vel).box(s, s * .8f, s * 1.2f).color(CAT).dmg(0).life(life).spawn(); }
    static void catTick(AttackCtx c) {
        for (UtProjectile k : own(c, CAT)) { boolean big = k.w() > 1.2f;
            if (k.position().distanceTo(c.tpos()) < (big ? 2 : 1.3)) { if (!big) { fx(c, ParticleTypes.HEART, k.position(), 3, .3); c.sound(SoundEvents.CAT_PURR, 1, 1.2f); k.discard(); } }
            else if (k.age() >= 30) recolor(c, k, CATRED, big ? 0 : 3); }
        for (UtProjectile k : own(c, CATRED)) { boolean big = k.w() > 1.2f;
            if (big && k.position().distanceTo(c.tpos()) < 2) { recolor(c, k, CAT, 0); fx(c, ParticleTypes.HEART, k.position(), 4, .5); c.sound(SoundEvents.CAT_PURR, 1, .9f); }
            else if (k.age() % 16 == 8) { shot(c, k.position(), c.aim(k.position()).scale(.3), .25f, FUR, 3, 0, 30); c.sound(SoundEvents.CAT_HISS, .5f, 1.4f); } }
    }
    static Vec3 corner(AttackCtx c, int i, double rx, double fz) { Vec3 f = toT(c), r = side(f); return c.tpos().add(r.scale(i % 2 == 0 ? -rx : rx)).add(f.scale(i < 2 ? -fz : fz)); }
    // ---- Cat Mike ----
    static Attack mikeCatBarrage() {
        return atk("cat_mike_cat_barrage", 110, (c, t) -> {
            if (t >= 8 && t < 72 && t % 8 == 0) { Vec3 at = corner(c, (t / 8) % 4, 5, 4); cat(c, at, c.aim(at).scale(.05), .7f, 90); c.sound(SoundEvents.CAT_AMBIENT, .7f, 1.3f); }
            catTick(c);
        }, (p, t, m) -> mikeDance(p, t));
    }
    static Attack mikeSleepingCats() {
        return atk("cat_mike_sleeping_cats", 120, (c, t) -> {
            if (t == 6) { for (int i = 0; i < 4; i++) cat(c, corner(c, i, 3, 3), Vec3.ZERO, 1.8f, 110); c.sound(SoundEvents.CAT_PURREOW, 1, .8f); }
            if (t % 10 == 0) for (UtProjectile k : own(c, CAT)) if (k.w() > 1.2f) fx(c, ParticleTypes.NOTE, k.position().add(0, 1, 0), 1, .2);
            catTick(c);
        }, (p, t, m) -> { sway(p, t, .15f); p.r("armL", -2 * ramp(t, 0, 8), 0, .3f).r("earL", 0, 0, sin(t, .4f) * .2f).r("earR", 0, 0, -sin(t, .4f) * .2f); });
    }
    static Attack mikeCatWalls() {
        return atk("cat_mike_cat_walls", 110, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 4) prop(c, c.tpos().add(0, 3, 0).add(f.scale(-3)), f, 3, .8f, .1f, 0xfff27a, 30);
            if (t >= 10 && t < 90 && t % 20 == 10) { int gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) if (Math.abs(k - gap) > 0) c.proj().at(c.tpos().add(r.scale(7)).add(f.scale(k * .9))).vel(r.scale(-.2)).box(.8f, .7f, .8f).color(CATRED).dmg(3).warn(5).life(70).spawn();
                c.sound(SoundEvents.CAT_AMBIENT, 1, .9f); }
            if (t % 7 == 0) bolt(c, c.tpos().add(r.scale(7)).add(f.scale((c.rnd.nextDouble() * 2 - 1) * 3)), r.scale(-.3), .4f, 1 + c.rnd.nextInt(3), AUD, 3, 5, 50);
            catTick(c);
        }, (p, t, m) -> mikeDance(p, t));
    }
    /** Blue Cats (all Mikes): grab the blue cats (touch) while white cats get in the way. */
    static Attack mikeBlueCats() {
        return atk("cat_mike_blue_cats", 120, (c, t) -> {
            if (t >= 10 && t < 90 && t % 14 == 10) { Vec3 at = c.target.position().add(jit(c, 3)).add(0, 5, 0);
                c.proj().at(at).vel(new Vec3(0, -.12, 0)).box(.7f, .6f, .8f).color(BLUECAT).dmg(0).gravity(.01f).life(80).spawn(); }
            if (t >= 16 && t < 96 && t % 9 == 0) { Vec3 at = corner(c, c.rnd.nextInt(4), 5, 4); cat(c, at, c.aim(at).scale(.08), .7f, 70); }
            for (UtProjectile b : own(c, BLUECAT)) { if (b.getY() < c.target.getY() + .3) b.setDeltaMovement(Vec3.ZERO);
                if (b.position().distanceTo(c.tpos()) < 1.4) { fx(c, ParticleTypes.HEART, b.position(), 6, .4); c.sound(SoundEvents.CAT_PURR, 1, 1.4f); b.discard(); } }
            catTick(c);
        }, (p, t, m) -> { pointLoop(p, t, "armL", 14); hop(p, t, 14, 1); });
    }
    // ---- Cowboy Hat Mike ----
    /** Flashing red rectangle on the ceiling or floor, then a big paw slams through it. */
    static void pawWarn(AttackCtx c, Vec3 at, boolean top, float w, float l, int warn) {
        prop(c, new Vec3(at.x, c.target.getY() + (top ? 3 : .05), at.z), toT(c), w, .1f, l, top ? RECT_TOP : RECT_LOW, warn);
        c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), .6f, top ? 1.6f : 1.1f);
    }
    static void pawTick(AttackCtx c) {
        for (int col : new int[]{RECT_TOP, RECT_LOW}) for (UtProjectile r : own(c, col)) if (r.age() == r.maxLife() - 1) { boolean top = col == RECT_TOP;
            c.proj().at(r.position().add(0, top ? 2 : -2.45, 0)).vel(new Vec3(0, top ? -.8 : .6, 0)).box(r.w(), 2.6f, r.l()).color(PAW).dmg(6).life(6).pierce().spawn();
            fx(c, ParticleTypes.POOF, r.position(), 6, .5); c.sound(SoundEvents.WOOL_FALL, 1, .6f); }
    }
    static Attack mikeWhitePaws() {
        return atk("cowboy_mike_white_paws", 110, (c, t) -> {
            if (t >= 10 && t < 90 && t % 16 == 10) pawWarn(c, c.target.position().add(c.target.getDeltaMovement().scale(6)), c.rnd.nextBoolean(), 2.4f, 2.4f, 14);
            pawTick(c);
        }, (p, t, m) -> { float k = ((t + 6) % 16) / 16f; p.r("armR", -2.4f * env(k, 0, .4f, .7f, .9f), 0, -.3f).r("crown", 0, sin(t, .2f) * .2f, 0); });
    }
    static Attack mikeBlackPaws() {
        return atk("cowboy_mike_black_paws", 100, (c, t) -> {
            if (t >= 8 && t < 86 && t % 3 == 0) rain(c, 3.5, 1, .45f, 0x1a1a1a, 3, 6, .03f);
            if (t % 12 == 0) c.sound(SoundEvents.WOOL_STEP, .8f, 1.4f);
        }, (p, t, m) -> mikeDance(p, t));
    }
    /** White Paws 2: paws hit both edges at once, leaving only a small safe strip in the middle. */
    static Attack mikeWhitePaws2() {
        return atk("cowboy_mike_white_paws_2", 110, (c, t) -> {
            if (t >= 10 && t < 90 && t % 20 == 10) { Vec3 r = side(toT(c)); boolean top = c.rnd.nextBoolean();
                for (int s = -1; s <= 1; s += 2) pawWarn(c, c.target.position().add(r.scale(s * 2.3)), top, 3.2f, 5, 16); }
            pawTick(c);
        }, (p, t, m) -> { float k = ((t + 10) % 20) / 20f; p.r("armR", -2.4f * env(k, 0, .4f, .7f, .9f), 0, -.3f).r("armL", -2.4f * env(k, 0, .4f, .7f, .9f), 0, .3f); });
    }
    /** Sleeping Cats 2 (all Mikes): two big cats on opposite sides need petting while paws drop from the ceiling. */
    static Attack mikeSleepingCats2() {
        return atk("cowboy_mike_sleeping_cats_2", 120, (c, t) -> {
            Vec3 r = side(toT(c));
            if (t == 6) for (int s = -1; s <= 1; s += 2) cat(c, c.tpos().add(r.scale(4 * s)), Vec3.ZERO, 1.8f, 110);
            if (t >= 16 && t < 100 && t % 14 == 2) pawWarn(c, c.target.position().add(jit(c, 1.2)), true, 1.8f, 1.8f, 12);
            pawTick(c); catTick(c);
        }, (p, t, m) -> mikeDance(p, t));
    }
}
