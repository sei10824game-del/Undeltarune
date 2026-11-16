package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

import static com.undeltarune.chars.dr.DrFx.*;

/** Deltarune Chapter 1: the Lightner heroes, Card Kingdom (Lancer, King, Rouxls, field enemies), Jevil, Seam.
 *  Canon allegiance: heroes are HEROES/friendly; Card Kingdom under King is hostile to them; Lancer is a
 *  rival-turned-friend (neutral); Jevil is a free agent imprisoned by King (own NEUTRAL faction, hostile to the player). */
public final class DrCh1 {
    public static void register(List<CharacterDef> out) {
        heroes(out); lancer(out); fieldEnemies(out); rouxls(out); jevil(out); king(out); seam(out);
    }

    // =====================================================================================================
    // HEROES
    // =====================================================================================================
    static void heroes(List<CharacterDef> out) {
        ModelDef kris = biped(0x7fc6dd, 0x2b3a8f, 0x2b3a8f, 0x1d2459, 6, 7)
            .box("hair", "head", 0, 1.2f, .3f, 7.6f, 5.4f, 7.6f, 0x3b2626)          // bangs hide the eyes
            .box("bangs", "head", 0, .2f, -3.7f, 6.6f, 2.6f, .6f, 0x3b2626)
            .box("capeL", "body", -2.4f, 2.4f, 1.2f, 2.2f, 2.4f, 3, 0xd23f86)         // magenta shoulder cape
            .box("capeR", "body", 2.4f, 2.4f, 1.2f, 2.2f, 2.4f, 3, 0xd23f86)
            .box("cape", "body", 0, -.5f, 2.6f, 6.4f, 6, .6f, 0xd23f86)
            .box("sword", "armR", 0, -6.4f, -3.6f, .7f, 1, 8, 0xd8212d)
            .box("hilt", "armR", 0, -6.4f, -.4f, 2.6f, .8f, .8f, 0x2b2b2b);
        out.add(CharacterDef.of("kris", "Kris", Faction.HEROES).att(Attitude.FRIENDLY).hp(90).keep(3).rest(22)
            .egg(0x2b3a8f, 0xd23f86).model(kris).attacks(krisCombo(), krisThrust(), krisRedBuster()).build());

        ModelDef susie = biped(0x9b62c4, 0x2e2a40, 0x9b62c4, 0x1e1b2a, 7, 7.4f)
            .box("hair", "head", 0, 1.8f, .8f, 8.6f, 5.6f, 8, 0x6c2e78)
            .box("mane", "head", 0, -1.5f, 3.6f, 8, 6, 2, 0x6c2e78)
            .glow("eyes", "head", 0, 0, -3.8f, 4, .8f, .3f, 0xffe23a)
            .box("snout", "head", 0, -1.6f, -4.2f, 3.4f, 2, 1.4f, 0x8a4fae)
            .box("teeth", "head", 0, -2.5f, -4.9f, 2.6f, .5f, .2f, 0xffffff)
            .box("pauldron", "body", 0, 3.4f, 0, 8, 1.6f, 4.6f, 0x4b4566)
            .box("axe_haft", "armR", 0, -6.4f, -2.8f, .6f, .6f, 7, 0x6b3b1f)
            .box("axe_head", "armR", 0, -5f, -6.2f, .8f, 4, 2.6f, 0xd33c6a);
        out.add(CharacterDef.of("susie", "Susie", Faction.HEROES).att(Attitude.FRIENDLY).hp(130).keep(3.5).rest(26).scale(1.15f)
            .size(.7f, 2f).egg(0x9b62c4, 0xd33c6a).model(susie).attacks(susieAxeSlam(), susieRudeBuster(), susieAxeVolley()).build());

        ModelDef ralsei = biped(0x2c2c32, 0x2c8a55, 0x2c8a55, 0x2c2c32, 6, 7)
            .box("hat", "head", 0, 4.6f, 0, 8.4f, 1.2f, 8.4f, 0x2c8a55)
            .box("hat_tip", "head", 0, 8, 0, 4, 6, 4, 0x2c8a55)
            .box("hat_top", "head", 0, 11.5f, 0, 1.6f, 2, 1.6f, 0x2c8a55)
            .glow("eyes", "head", 0, .2f, -3.6f, 4, .9f, .3f, 0x8fffb0)
            .box("scarf", "body", 0, 3.2f, -.4f, 6.6f, 1.6f, 4.8f, 0xf28ab4)
            .box("scarf_tail", "body", 1.6f, 0, -2.6f, 1.4f, 4, .5f, 0xf28ab4)
            .box("robe", "body", 0, -3.6f, 0, 7.2f, 3, 5, 0x2c8a55);
        out.add(CharacterDef.of("ralsei", "Ralsei", Faction.HEROES).att(Attitude.FRIENDLY).hp(80).keep(6).rest(30).scale(.95f)
            .egg(0x2c8a55, 0xf28ab4).model(ralsei).attacks(ralseiScarfWhip(), ralseiPacify(), ralseiHealPrayer()).build());
    }

    static Attack krisCombo() {
        return atk("kris_sword_combo", 46, (c, t) -> {
            c.face(c.tpos());
            if (t == 4) c.dash(c.aim(c.pos()).multiply(1, 0, 1).scale(.9));
            for (int hit : new int[]{10, 20, 30}) if (t == hit) {
                Vec3 f = flat(c.aim(c.pos())), r = side(f);
                for (int k = -2; k <= 2; k++) // crescent slash
                    c.proj().at(c.eye().add(f.scale(1.2 + Math.abs(k) * -.15)).add(r.scale(k * .45)).add(0, -.4 + (hit == 20 ? .3 : 0), 0))
                        .vel(f.scale(.25)).box(.3f, .15f, .5f).color(0xff4040).dmg(hit == 30 ? 6 : 4).life(5).pierce().spawn();
                c.particle(ParticleTypes.SWEEP_ATTACK, c.eye().add(f.scale(1.4)), 1, 0, 0);
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, hit == 30 ? .8f : 1.2f);
            }
        }, (p, t, m) -> {
            float a = env(t, 4, 10, 12, 16), b = env(t, 14, 20, 22, 26), d = env(t, 24, 30, 34, 42);
            p.r("armR", -2.2f * a - .6f * b - 2.6f * d, .8f * b, 0).r("body", 0, .5f * a - .6f * b, 0).o("body", 0, 0, -1.5f * d);
        });
    }
    static Attack krisThrust() {
        return atk("kris_dash_thrust", 40, (c, t) -> {
            if (t < 14) { c.face(c.tpos()); if (t % 4 == 0) c.particle(ParticleTypes.CRIT, c.eye(), 4, .3, .05); }
            if (t == 14) { Vec3 f = c.aim(c.eye()); c.dash(f.multiply(1, 0, 1).scale(1.6));
                c.proj().at(c.eye().add(f.scale(1.5))).vel(f.scale(1.6)).box(.25f, .25f, 2.2f).color(0xff3040).dmg(7).life(8).pierce().warn(0).spawn();
                c.sound(SoundEvents.TRIDENT_THROW, 1, 1.3f); }
        }, (p, t, m) -> {
            float w = ramp(t, 0, 12), s = env(t, 13, 15, 22, 34);
            p.r("armR", -.4f * w - 1.6f * s, 0, 0).o("armR", 0, 0, 2f * w - 3 * s).r("body", .25f * w - .2f * s, 0, 0).r("legL", -.5f * s, 0, 0);
        });
    }
    /** Red Buster: Kris + Susie's red-flame X-Action blast. */
    static Attack krisRedBuster() {
        return atk("kris_red_buster", 40, (c, t) -> {
            if (t < 16 && t % 3 == 0) c.particle(ParticleTypes.FLAME, c.eye().add(0, .4, 0), 5, .3, .02);
            if (t == 16) { Vec3 f = c.aim(c.eye());
                c.proj().at(c.eye().add(f)).vel(f.scale(.9)).box(1.2f, 1.2f, .6f).color(0xff2424).dmg(8).life(26).spawn();
                c.proj().at(c.eye().add(f)).vel(f.scale(.9)).box(1.6f, .4f, .4f).color(0xff9a9a).dmg(0).life(26).spawn();
                c.sound(SoundEvents.FIRECHARGE_USE, 1, .8f); }
        }, (p, t, m) -> {
            float w = env(t, 0, 14, 18, 30);
            p.r("armL", -2.8f * w, 0, .3f * w).r("armR", -2.8f * w, 0, -.3f * w).r("head", -.3f * w, 0, 0);
        });
    }

    static Attack susieAxeSlam() {
        return atk("susie_axe_slam", 44, (c, t) -> {
            c.face(c.tpos());
            if (t == 10) c.dash(c.aim(c.pos()).multiply(1, 0, 1).scale(.8).add(0, .5, 0));
            if (t == 20) {
                Vec3 at = c.pos().add(flat(c.aim(c.pos())).scale(1.6));
                c.aoe(at, 2.6, 8, 1.2); c.particle(ParticleTypes.EXPLOSION, at, 2, .4, 0);
                c.sound(SoundEvents.ANVIL_LAND, 1, .7f);
                for (int i = 0; i < 8; i++) { double a = i * Math.PI / 4; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                    c.proj().at(at.add(d).add(0, .2, 0)).vel(d.scale(.5)).box(.4f, .4f, .4f).color(0xd33c6a).dmg(3).life(10).spawn(); }
            }
        }, (p, t, m) -> {
            float up = env(t, 0, 12, 18, 21), down = env(t, 18, 21, 30, 40);
            p.r("armR", -3f * up - .6f * down, 0, 0).r("armL", -3f * up - .6f * down, 0, 0).r("body", -.25f * up + .5f * down, 0, 0);
        });
    }
    static Attack susieRudeBuster() {
        return atk("susie_rude_buster", 50, (c, t) -> {
            c.face(c.tpos());
            if (t < 20 && t % 2 == 0) c.particle(ParticleTypes.DRAGON_BREATH, c.eye().add(c.fwd().scale(.8)), 4, .25, .01);
            if (t == 20) { Vec3 f = c.aim(c.eye());
                // the canon purple "X"-shaped blast: two crossed slabs + a core
                c.proj().at(c.eye().add(f)).vel(f.scale(1.0)).box(2.2f, .5f, .6f).color(0xa040ff).dmg(0).life(24).spawn();
                c.proj().at(c.eye().add(f)).vel(f.scale(1.0)).box(.5f, 2.2f, .6f).color(0xa040ff).dmg(0).life(24).spawn();
                c.proj().at(c.eye().add(f)).vel(f.scale(1.0)).box(1.4f, 1.4f, .8f).color(0xe0a0ff).dmg(11).life(24).pierce().spawn();
                c.sound(SoundEvents.WITHER_SHOOT, .8f, 1.4f); }
        }, (p, t, m) -> {
            float w = env(t, 0, 16, 20, 24), s = env(t, 20, 22, 30, 44);
            p.r("armR", -2.9f * w - 1.4f * s, 0, -.5f * w).r("body", 0, -.6f * w + .5f * s, 0).o("body", 0, .6f * w, 0);
        });
    }
    /** Susie riding Lancer's bike in the Forest fight: lobbed spinning axes. */
    static Attack susieAxeVolley() {
        return atk("susie_axe_volley", 52, (c, t) -> {
            if (t == 6) c.dash(new Vec3(0, .7, 0));
            if (t >= 12 && t < 36 && t % 6 == 0) {
                Vec3 f = c.aim(c.eye());
                c.proj().at(c.eye().add(f)).vel(f.scale(.7).add(0, .25, 0)).gravity(.03f).box(.9f, .9f, .25f).color(0xd33c6a).dmg(5).life(40).spawn();
                c.sound(SoundEvents.TRIDENT_THROW, .8f, .9f);
            }
        }, (p, t, m) -> {
            float sw = t >= 12 && t < 38 ? (float) Math.sin((t - 12) * Math.PI / 3) : 0;
            p.r("armR", -1.5f - 1.2f * sw, 0, 0).o("body", 0, 2 * env(t, 4, 10, 40, 50), 0).r("legL", -.6f, 0, 0).r("legR", -.6f, 0, 0);
        });
    }

    static Attack ralseiScarfWhip() {
        return atk("ralsei_scarf_whip", 34, (c, t) -> {
            c.face(c.tpos());
            if (t == 12) { Vec3 f = flat(c.aim(c.pos())), r = side(f);
                for (int k = 0; k < 5; k++) c.proj().at(c.eye().add(f.scale(1 + k * .5)).add(r.scale(Math.sin(k) * .4)))
                    .vel(f.scale(.1)).box(.35f, .2f, .5f).color(0xf28ab4).dmg(3).life(6 + k).pierce().spawn();
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .8f, 1.6f); }
        }, (p, t, m) -> {
            float w = env(t, 0, 10, 12, 24);
            p.r("armR", -2.4f * w, -.6f * w, 0).r("scarf_tail", -1.4f * w, 0, 0).r("body", 0, .5f * w, 0);
        });
    }
    /** Pacify: drowsy stars circle the target and put it to sleep (slowness + weakness). */
    static Attack ralseiPacify() {
        return atk("ralsei_pacify", 60, (c, t) -> {
            if (t < 30 && t % 3 == 0) { double a = t * .4; c.particle(ParticleTypes.END_ROD, c.tpos().add(Math.cos(a) * 1.2, .8, Math.sin(a) * 1.2), 2, .05, 0); }
            if (t == 30) { c.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3));
                c.target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
                c.particle(ParticleTypes.NOTE, c.tpos().add(0, 1, 0), 6, .5, .5); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .6f); }
        }, (p, t, m) -> {
            float w = env(t, 0, 10, 40, 56);
            p.r("armL", -1.6f * w, .4f * w, 0).r("armR", -1.6f * w, -.4f * w, 0).o("hat_tip", 0, wave(t, .3f) * .4f * w, 0);
        });
    }
    /** Heal Prayer: heals self and nearby non-hostile allies. */
    static Attack ralseiHealPrayer() {
        return atk("ralsei_heal_prayer", 50, (c, t) -> {
            if (t % 5 == 0 && t < 25) c.particle(ParticleTypes.HAPPY_VILLAGER, c.pos().add(0, 1, 0), 6, .8, .05);
            if (t == 25) {
                for (LivingEntity e : c.level.getEntitiesOfClass(LivingEntity.class, new AABB(c.pos(), c.pos()).inflate(6), e -> !c.me.isFoe(e))) {
                    e.heal(8); c.particle(ParticleTypes.HEART, e.position().add(0, e.getBbHeight() + .3, 0), 3, .3, 0);
                }
                c.sound(SoundEvents.PLAYER_LEVELUP, .6f, 1.8f);
            }
        }, (p, t, m) -> {
            float w = env(t, 0, 12, 30, 46);
            p.r("armL", -.9f * w, 0, .5f * w).r("armR", -.9f * w, 0, -.5f * w).r("head", .3f * w, 0, 0);
        });
    }

    // =====================================================================================================
    // LANCER (rival-turned-friend: NEUTRAL faction/attitude, fights back if provoked)
    // =====================================================================================================
    static void lancer(List<CharacterDef> out) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 6, 0, 8, 7, 6, 0x232340)
            .box("belly", "body", 0, -.5f, -3.1f, 5, 4, .3f, 0x34345a)
            .box("head", "body", 0, 7, 0, 7, 6, 6, 0x232340)
            .box("spade_tip", "head", 0, 4.2f, 0, 3, 3, 3, 0x232340)
            .box("spade_l", "head", -3, 1.5f, 0, 2.4f, 3, 4, 0x232340)
            .box("spade_r", "head", 3, 1.5f, 0, 2.4f, 3, 4, 0x232340)
            .box("grin", "head", 0, -1.2f, -3.1f, 5.6f, 1.8f, .3f, 0xffffff)
            .glow("eyeL", "head", -1.6f, 1.2f, -3.1f, 1.2f, 1.2f, .3f, 0xffffff)
            .glow("eyeR", "head", 1.6f, 1.2f, -3.1f, 1.2f, 1.2f, .3f, 0xffffff)
            .box("cape", "body", 0, 0, 3.2f, 7, 6, .5f, 0x1a1a2c)
            .limb("armL", "body", -4.8f, 2.5f, 0, 1.8f, 4, 1.8f, 0x232340)
            .limb("armR", "body", 4.8f, 2.5f, 0, 1.8f, 4, 1.8f, 0x232340)
            .limb("legL", null, -1.8f, 3, 0, 2.2f, 3, 2.4f, 0x232340)
            .limb("legR", null, 1.8f, 3, 0, 2.2f, 3, 2.4f, 0x232340);
        out.add(CharacterDef.of("lancer", "Lancer", Faction.NEUTRAL).att(Attitude.NEUTRAL).hp(70).keep(5).rest(26).scale(.75f)
            .size(.7f, 1.2f).egg(0x232340, 0xffffff).model(m)
            .attacks(lancerBikeCharge(), lancerSpadeSpit(), lancerSpadeDrops(), lancerSideSpades(), lancerAxeBikeCombo()).build());
    }
    /** Castle Town 1/3: bob up and down, HONK, wheelie charge. */
    static Attack lancerBikeCharge() {
        return atk("lancer_bike_charge", 56, (c, t) -> {
            c.face(c.tpos());
            if (t < 24 && t % 8 == 0) c.dash(c.right().scale(t % 16 == 0 ? .4 : -.4));
            if (t == 26) { c.sound(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 1, 1.5f); c.particle(ParticleTypes.CLOUD, c.pos(), 10, .5, .05); }
            if (t == 32) { put(c, "lc", c.aim(c.pos()).multiply(1, 0, 1).normalize()); }
            if (t >= 32 && t < 44) { Vec3 d = get(c, "lc"); c.dash(d.scale(1.1)); c.aoe(c.pos().add(0, .5, 0), 1.3, 6, 1.4);
                c.particle(ParticleTypes.SMOKE, c.pos(), 3, .2, .02); }
        }, (p, t, m) -> {
            float bob = t < 24 ? wave(t, .8f) : 0, w = env(t, 26, 32, 44, 52);
            p.o("body", 0, bob, 0).r("body", -.5f * w, 0, 0).r("armL", -1.4f * w, 0, 0).r("armR", -1.4f * w, 0, 0);
        });
    }
    /** Castle Town 2/4: charges across low, returns from above spitting spades at the SOUL. */
    static Attack lancerSpadeSpit() {
        return atk("lancer_spade_spit", 64, (c, t) -> {
            if (t == 4) c.dash(c.right().scale(1.2));
            if (t == 20) c.dash(new Vec3(0, .9, 0).add(c.right().scale(-.8)));
            if (t >= 28 && t < 56 && t % 5 == 0) { suit(c, c.eye(), c.aim(c.eye()).scale(.6), SPADE, 3, 40); c.sound(SoundEvents.CHICKEN_EGG, .8f, 1.6f); }
        }, (p, t, m) -> {
            float s = t >= 28 && t < 56 ? (t % 5 < 2 ? 1 : 0) : 0;
            p.r("head", -.3f * s, 0, 0).o("grin", 0, -.5f * s, 0).r("armL", -1f * s, 0, .4f).r("armR", -1f * s, 0, -.4f);
        });
    }
    /** Forest / Card Castle: spade rain from random points above the target. */
    static Attack lancerSpadeDrops() {
        return atk("lancer_spade_drops", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 3 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 2.5, 7, c.rnd.nextGaussian() * 2.5);
                suit(c, at, new Vec3(0, -.45, 0), SPADE, 3, 30, 4, 1);
            }
            if (t % 12 == 8) c.sound(SoundEvents.SNOW_GOLEM_SHOOT, .6f, 1.4f);
        }, (p, t, m) -> {
            float w = env(t, 0, 8, 56, 66);
            p.r("armL", -2.8f * w, 0, .3f * w * wave(t, .6f)).r("armR", -2.8f * w, 0, -.3f * w * wave(t, .6f)).o("body", 0, .5f * w * Math.abs(wave(t, .5f)), 0);
        });
    }
    /** Card Castle 2: spades slide in from both flanks. */
    static Attack lancerSideSpades() {
        return atk("lancer_side_spades", 64, (c, t) -> {
            if (t >= 10 && t < 52 && t % 7 == 0) {
                Vec3 r = c.right(); double s = (t / 7) % 2 == 0 ? 1 : -1; double y = c.rnd.nextDouble() * 1.4 - .4;
                for (int k = -1; k <= 1; k++) suit(c, c.tpos().add(r.scale(7 * s)).add(c.fwd().scale(k * 1.2)).add(0, y, 0), r.scale(-.5 * s), SPADE, 3, 30, 6, 1);
            }
        }, (p, t, m) -> {
            float s = t >= 10 && t < 52 ? wave(t, .45f) : 0;
            p.r("armL", -1.6f, 0, .6f * s).r("armR", -1.6f, 0, -.6f * s).r("body", 0, .4f * s, 0);
        });
    }
    /** Forest: Susie jumps on the bike, Lancer hops over the board while axes fly. */
    static Attack lancerAxeBikeCombo() {
        return atk("lancer_axe_bike_combo", 60, (c, t) -> {
            if (t == 10) c.dash(c.aim(c.pos()).multiply(.6, 0, .6).add(0, .9, 0));
            if (t >= 14 && t < 40 && t % 5 == 0) {
                Vec3 f = flat(c.aim(c.pos()));
                c.proj().at(c.eye().add(0, .6, 0)).vel(f.scale(.65).add(0, .1, 0)).gravity(.02f).box(.9f, .9f, .25f).color(0xd33c6a).dmg(4).life(36).spawn();
                c.proj().at(c.eye().add(0, .6, 0)).vel(f.scale(.65).add(0, .1, 0)).gravity(.02f).box(.15f, .15f, 1.2f).color(0x6b3b1f).dmg(0).life(36).spawn();
            }
            if (t == 10) c.sound(SoundEvents.HORSE_GALLOP, 1, 1.2f);
        }, (p, t, m) -> {
            float j = env(t, 6, 12, 36, 46);
            p.r("body", -.6f * j, 0, 0).r("legL", -1.2f * j, 0, 0).r("legR", -1.2f * j, 0, 0).r("armL", -2f * j, 0, 0).r("armR", -2f * j, 0, 0);
        });
    }

    // =====================================================================================================
    // CARD KINGDOM FIELD ENEMIES (all serve King: hostile)
    // =====================================================================================================
    static void fieldEnemies(List<CharacterDef> out) {
        // Rudinn: diamond-headed serpent
        out.add(CharacterDef.of("rudinn", "Rudinn", Faction.CARD_KINGDOM).hp(28).keep(5).rest(28).size(.6f, 1.6f).egg(0xd93636, 0xe7c33a)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 4, 8, 4, 0xe7c33a)
                .box("tail", "body", 0, -3.5f, 2.5f, 3, 2, 5, 0xe7c33a)
                .box("head", "body", 0, 7, 0, 6, 6, 6, 0xd93636)
                .box("head_tip", "head", 0, 4, 0, 3, 3, 3, 0xd93636)
                .box("head_chin", "head", 0, -4, 0, 3, 2.5f, 3, 0xd93636)
                .glow("eyeL", "head", -1.3f, .3f, -3.1f, 1, 1.6f, .3f, 0xffffff)
                .glow("eyeR", "head", 1.3f, .3f, -3.1f, 1, 1.6f, .3f, 0xffffff)
                .limb("armL", "body", -2.8f, 2, 0, 1.4f, 3.5f, 1.4f, 0xe7c33a)
                .limb("armR", "body", 2.8f, 2, 0, 1.4f, 3.5f, 1.4f, 0xe7c33a))
            .attacks(rudinnDiamondSwerve(), rudinnDiamondWave()).build());
        // Hathy: heart-headed, tentacled
        ModelDef hathy = new ModelDef()
            .box("body", null, 0, 4, 0, 3, 3, 3, 0x2a2a3c)
            .box("head", "body", 0, 5, 0, 8, 6, 5, 0xff7aa8)
            .box("lobeL", "head", -2, 3.4f, 0, 4, 3, 5, 0xff7aa8)
            .box("lobeR", "head", 2, 3.4f, 0, 4, 3, 5, 0xff7aa8)
            .box("point", "head", 0, -3.6f, 0, 3, 2.4f, 4, 0xff7aa8)
            .glow("eyeL", "head", -1.6f, .6f, -2.6f, 1, 1.4f, .3f, 0x1a1a1a)
            .glow("eyeR", "head", 1.6f, .6f, -2.6f, 1, 1.4f, .3f, 0x1a1a1a)
            .limb("legL", null, -1.5f, 3, 0, 1, 3, 1, 0x2a2a3c).limb("legR", null, 1.5f, 3, 0, 1, 3, 1, 0x2a2a3c)
            .limb("armL", "body", -2, 0, -1, 1, 3, 1, 0x2a2a3c).limb("armR", "body", 2, 0, -1, 1, 3, 1, 0x2a2a3c);
        out.add(CharacterDef.of("hathy", "Hathy", Faction.CARD_KINGDOM).hp(26).keep(5).rest(28).size(.6f, 1.2f).egg(0xff7aa8, 0x2a2a3c)
            .model(hathy).attacks(hathyHeartClose(), hathyHeartOrbit()).build());
        out.add(CharacterDef.of("head_hathy", "Head Hathy", Faction.CARD_KINGDOM).hp(45).keep(6).rest(26).scale(1.5f).size(.9f, 1.8f).egg(0xff4f8a, 0x2a2a3c)
            .model(new ModelDef()
                .box("body", null, 0, 5, 0, 4, 4, 4, 0x2a2a3c)
                .box("head", "body", 0, 6, 0, 10, 7, 6, 0xff4f8a)
                .box("lobeL", "head", -2.5f, 4, 0, 5, 3.5f, 6, 0xff4f8a).box("lobeR", "head", 2.5f, 4, 0, 5, 3.5f, 6, 0xff4f8a)
                .box("point", "head", 0, -4.4f, 0, 4, 3, 5, 0xff4f8a)
                .glow("eyeL", "head", -2, .6f, -3.1f, 1.4f, 1.8f, .3f, 0xffffff).glow("eyeR", "head", 2, .6f, -3.1f, 1.4f, 1.8f, .3f, 0xffffff)
                .box("head2", "body", -6, 2, 1, 4, 3.5f, 3, 0xffa0c4).box("head3", "body", 6, 2, 1, 4, 3.5f, 3, 0xffa0c4)
                .limb("legL", null, -1.5f, 4, 0, 1.2f, 4, 1.2f, 0x2a2a3c).limb("legR", null, 1.5f, 4, 0, 1.2f, 4, 1.2f, 0x2a2a3c))
            .attacks(headHathyHeads(), headHathyTentacle()).build());
        // Rudinn Ranger: armored Rudinn with lance
        out.add(CharacterDef.of("rudinn_ranger", "Rudinn Ranger", Faction.CARD_KINGDOM).hp(42).keep(5).rest(24).size(.7f, 1.8f).egg(0x3a5fd9, 0xd93636)
            .model(new ModelDef()
                .box("body", null, 0, 5, 0, 5, 9, 5, 0x3a5fd9)
                .box("tail", "body", 0, -4, 3, 3.5f, 2, 6, 0xe7c33a)
                .box("head", "body", 0, 8, 0, 6.4f, 6.4f, 6.4f, 0xd93636)
                .box("helm", "head", 0, 3, 0, 7, 2, 7, 0x2a3f9a).box("plume", "head", 0, 5.4f, 1, 1, 3, 4, 0xffd84a)
                .glow("visor", "head", 0, .5f, -3.3f, 4.5f, 1, .3f, 0xffffff)
                .limb("armL", "body", -3.4f, 3, 0, 1.6f, 4.5f, 1.6f, 0x3a5fd9)
                .limb("armR", "body", 3.4f, 3, 0, 1.6f, 4.5f, 1.6f, 0x3a5fd9)
                .box("lance", "armR", 0, -4.5f, -3, .6f, .6f, 9, 0xffd84a))
            .attacks(rangerLanceCross(), rangerDiamondWheel()).build());
        // Ponman: chess pawn
        out.add(CharacterDef.of("ponman", "Ponman", Faction.CARD_KINGDOM).hp(30).keep(5).rest(26).size(.6f, 1.3f).egg(0xf0f0f0, 0x1a1a1a)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 6, 4, 6, 0xf0f0f0)
                .box("base", "body", 0, -3, 0, 8, 2, 8, 0xf0f0f0)
                .box("neck", "body", 0, 3, 0, 3, 3, 3, 0xf0f0f0)
                .box("head", "body", 0, 7, 0, 5, 5, 5, 0xf0f0f0)
                .glow("eyeL", "head", -1, 0, -2.6f, .8f, 1.2f, .3f, 0x1a1a1a).glow("eyeR", "head", 1, 0, -2.6f, .8f, 1.2f, .3f, 0x1a1a1a)
                .limb("legL", null, -1.5f, 2, 0, 1.6f, 2, 1.6f, 0xd0d0d0).limb("legR", null, 1.5f, 2, 0, 1.6f, 2, 1.6f, 0xd0d0d0))
            .attacks(ponmanMarch(), ponmanHop()).build());
        // Rabbick: dust bunny
        out.add(CharacterDef.of("rabbick", "Rabbick", Faction.CARD_KINGDOM).hp(30).keep(5).rest(28).size(.8f, 1.1f).egg(0x9a9aa8, 0x5a5a68)
            .model(new ModelDef()
                .box("body", null, 0, 5, 0, 8, 7, 8, 0x9a9aa8)
                .box("fluff", "body", 0, 0, 0, 9, 5, 9, 0x8a8a98)
                .box("head", "body", 0, 4.5f, -1, 6, 4, 5, 0xa8a8b6)
                .box("earL", "head", -1.6f, 4, 0, 1.4f, 5, 1, 0xa8a8b6).box("earR", "head", 1.6f, 4, 0, 1.4f, 5, 1, 0xa8a8b6)
                .glow("eyes", "head", 0, .3f, -2.6f, 3.6f, .8f, .3f, 0x1a1a1a)
                .limb("legL", null, -2, 2, 0, 2, 2, 2, 0x8a8a98).limb("legR", null, 2, 2, 0, 2, 2, 2, 0x8a8a98))
            .attacks(rabbickDustBunnies(), rabbickDustCloud()).build());
        // Bloxer: angry block
        out.add(CharacterDef.of("bloxer", "Bloxer", Faction.CARD_KINGDOM).hp(34).keep(6).rest(26).size(.8f, 1.2f).egg(0x4a7ad9, 0xffd84a)
            .model(new ModelDef()
                .box("body", null, 0, 6, 0, 9, 9, 9, 0x4a7ad9)
                .box("head", "body", 0, 0, -4.6f, 7, 7, .3f, 0x5e8ee8)
                .glow("eyeL", "head", -1.8f, 1, -.2f, 1.6f, 1, .3f, 0xffd84a).glow("eyeR", "head", 1.8f, 1, -.2f, 1.6f, 1, .3f, 0xffd84a)
                .box("mouth", "head", 0, -1.8f, -.2f, 4, .8f, .3f, 0x1a1a1a)
                .limb("legL", null, -2.5f, 2, 0, 2, 2, 2, 0x2a4a99).limb("legR", null, 2.5f, 2, 0, 2, 2, 2, 0x2a4a99))
            .attacks(bloxerBlockFall(), bloxerTetris()).build());
        // Jigsawry: puzzle-piece knight
        out.add(CharacterDef.of("jigsawry", "Jigsawry", Faction.CARD_KINGDOM).hp(32).keep(5).rest(26).egg(0xc76ad9, 0x3a3a5a)
            .model(biped(0xc76ad9, 0xc76ad9, 0xc76ad9, 0x8a3fa0, 6, 6)
                .box("knob", "head", 0, 3.8f, 0, 2.4f, 2, 2.4f, 0xc76ad9).box("knobL", "head", -3.8f, 0, 0, 2, 2.4f, 2.4f, 0xc76ad9)
                .glow("eyes", "head", 0, .3f, -3.1f, 3.6f, .9f, .3f, 0x1a1a1a)
                .box("knobBody", "body", 3.6f, 0, 0, 1.6f, 2.4f, 2.4f, 0xc76ad9))
            .attacks(jigsawPieces()).build());
        // Clover: three heads on a clover body
        out.add(CharacterDef.of("clover", "Clover", Faction.CARD_KINGDOM).hp(60).keep(6).rest(24).scale(1.2f).size(.9f, 1.8f).egg(0x3faa5a, 0xffd84a)
            .model(new ModelDef()
                .box("body", null, 0, 6, 0, 8, 8, 6, 0x3faa5a)
                .box("leafL", "body", -4.4f, 3, 0, 4, 4, 5, 0x3faa5a).box("leafR", "body", 4.4f, 3, 0, 4, 4, 5, 0x3faa5a)
                .box("head", "body", 0, 7, 0, 4.4f, 4.4f, 4.4f, 0xffe0a8)
                .box("hairM", "head", 0, 2.6f, 0, 4.8f, 1.6f, 4.8f, 0xe8c040)
                .box("headL", "body", -5, 6, 0, 4, 4, 4, 0xffe0a8).box("hairL", "headL", 0, 2.4f, 0, 4.4f, 1.4f, 4.4f, 0x5ad07a)
                .box("headR", "body", 5, 6, 0, 4, 4, 4, 0xffe0a8).box("hairR", "headR", 0, 2.4f, 0, 4.4f, 1.4f, 4.4f, 0xff8a3a)
                .glow("eyes", "head", 0, .2f, -2.3f, 3, .7f, .3f, 0x1a1a1a)
                .limb("armL", "body", -4.5f, 2, -2, 1.4f, 4.5f, 1.4f, 0xffe0a8).limb("armR", "body", 4.5f, 2, -2, 1.4f, 4.5f, 1.4f, 0xffe0a8)
                .limb("legL", null, -1.8f, 3, 0, 1.6f, 3, 1.6f, 0x2a7a42).limb("legR", null, 1.8f, 3, 0, 1.6f, 3, 1.6f, 0x2a7a42))
            .attacks(cloverClubFan(), cloverTrivia(), cloverHeadsSpin()).build());
        // Checkers: C. Round (small) and K. Round (the king checker miniboss)
        out.add(CharacterDef.of("c_round", "C. Round", Faction.CARD_KINGDOM).hp(36).keep(5).rest(26).size(.9f, .8f).egg(0xc93232, 0x1a1a1a)
            .model(checker(0xc93232, false)).attacks(checkerBounce(), checkerRoll()).build());
        out.add(CharacterDef.of("k_round", "K. Round", Faction.CARD_KINGDOM).hp(110).boss().keep(6).rest(22).scale(2.2f).size(1.8f, 1.6f)
            .egg(0xc93232, 0xffd84a).model(checker(0xc93232, true)).attacks(kRoundStomp(), checkerBounce(), kRoundCrownSpit(), checkerRoll()).build());
    }
    static ModelDef checker(int c, boolean king) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 2.5f, 0, 12, 5, 12, c)
            .box("rim", "body", 0, 0, 0, 12.6f, 1, 12.6f, 0x8a1a1a)
            .box("head", "body", 0, 2.6f, 0, 8, .4f, 8, 0xd84848)
            .glow("eyeL", "body", -2, 1, -6.2f, 1.2f, 1.6f, .3f, 0xffffff).glow("eyeR", "body", 2, 1, -6.2f, 1.2f, 1.6f, .3f, 0xffffff)
            .limb("armL", "body", -6.6f, 1, 0, 1.4f, 3, 1.4f, 0x1a1a1a).limb("armR", "body", 6.6f, 1, 0, 1.4f, 3, 1.4f, 0x1a1a1a);
        if (king) m.box("crown", "head", 0, 1.8f, 0, 6, 3, 6, 0xffd84a).box("crownPt", "head", 0, 4, 0, 1.4f, 2, 6.2f, 0xffd84a)
            .glow("gem", "head", 0, 2, -3.1f, 1.2f, 1.2f, .3f, 0x3fa7ff);
        return m;
    }

    static Attack rudinnDiamondSwerve() {
        return atk("rudinn_diamond_swerve", 60, (c, t) -> {
            if (t >= 8 && t < 44 && t % 6 == 0) {
                Vec3 at = c.eye().add(c.right().scale((t / 6) % 2 == 0 ? 1.5 : -1.5));
                Vec3 v = c.aim(at).scale(.55).add(c.right().scale((t / 6) % 2 == 0 ? -.15 : .15));
                suit(c, at, v, DIAMOND, 3, 40, 3, 1); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, .7f, 1.4f);
            }
        }, (p, t, m) -> { float s = wave(t, .5f) * env(t, 0, 8, 44, 56); p.r("body", 0, 0, .35f * s).r("head", 0, .3f * s, -.3f * s); });
    }
    static Attack rudinnDiamondWave() {
        return atk("rudinn_diamond_wave", 64, (c, t) -> {
            if (t >= 10 && t < 50 && t % 10 == 0) {
                Vec3 f = flat(c.aim(c.pos())), r = side(f); double off = Math.sin(t * .3) * 1.5;
                for (int k = -2; k <= 2; k++) suit(c, c.tpos().subtract(f.scale(7)).add(r.scale(k * 1.4 + off)), f.scale(.5), DIAMOND, 3, 34, 8, 1);
            }
        }, (p, t, m) -> { float w = env(t, 0, 10, 50, 60); p.r("armL", -2.2f * w, 0, .4f).r("armR", -2.2f * w, 0, -.4f).o("body", 0, Math.abs(wave(t, .4f)) * w, 0); });
    }
    static Attack hathyHeartClose() {
        return atk("hathy_heart_ring", 60, (c, t) -> {
            if (t == 6 || t == 26) { put(c, "hr", c.tpos()); ring(c, c.tpos(), 8, 5, t * .2, 0, HEART, 0, 14, 0); }
            if (t == 18 || t == 38) ring(c, get(c, "hr"), 8, 5, (t - 12) * .2, -.3, HEART, 3, 30, 0);
            if (t == 18 || t == 38) c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, .8f, 1);
        }, (p, t, m) -> { float s = env(t, 0, 6, 46, 58); p.r("lobeL", 0, 0, .3f * s * wave(t, .7f)).r("lobeR", 0, 0, -.3f * s * wave(t, .7f)).r("armL", -1.6f * s, 0, 0).r("armR", -1.6f * s, 0, 0); });
    }
    static Attack hathyHeartOrbit() {
        return atk("hathy_heart_orbit", 64, (c, t) -> {
            if (t >= 6 && t < 54 && t % 4 == 0) {
                double a = t * .35; Vec3 at = c.tpos().add(Math.cos(a) * 4, 0, Math.sin(a) * 4);
                suit(c, at, c.aim(at).scale(.3), HEART, 3, 28, 6, .9f);
            }
        }, (p, t, m) -> p.r("head", 0, t * .25f * env(t, 0, 6, 54, 62), 0).o("body", 0, Math.abs(wave(t, .3f)), 0));
    }
    static Attack headHathyHeads() {
        return atk("head_hathy_heart_heads", 70, (c, t) -> {
            if (t >= 12 && t < 56 && t % 8 == 4) {
                Vec3 at = c.eye().add(c.right().scale(c.rnd.nextBoolean() ? 1.8 : -1.8)).add(0, .6, 0);
                c.proj().at(at).vel(c.aim(at).scale(.5)).box(.9f, .7f, .7f).color(0xffa0c4).dmg(4).warn(4).life(40).spawn();
                c.proj().at(at).vel(c.aim(at).scale(.5)).box(.3f, .3f, .75f).color(0x1a1a1a).dmg(0).life(40).spawn();
                c.sound(SoundEvents.SLIME_SQUISH, 1, 1.4f);
            }
        }, (p, t, m) -> { float s = t >= 12 && t < 56 ? (t % 8 < 4 ? 1 : 0) : 0; p.o("head2", 0, 1.5f * s, 0).o("head3", 0, 1.5f * (1 - s), 0).r("head", .2f * s, 0, 0); });
    }
    static Attack headHathyTentacle() {
        return atk("head_hathy_tentacle", 60, (c, t) -> {
            if (t == 8) put(c, "ht", c.tpos());
            if (t >= 8 && t < 40 && t % 4 == 0) { // tentacle segments reach out along a line toward the target
                int k = (t - 8) / 4; Vec3 from = c.eye(), to = get(c, "ht"); Vec3 at = from.add(to.subtract(from).scale(k / 7.0));
                c.proj().at(at).box(.5f, .5f, .5f).color(0x2a2a3c).dmg(3).warn(4).life(46 - t).spawn();
            }
            if (t == 40) { c.aoe(get(c, "ht"), 1.8, 5, .6); ring(c, get(c, "ht"), 6, .5, 0, .35, HEART, 3, 20, 0); c.sound(SoundEvents.SLIME_SQUISH, 1, .6f); }
        }, (p, t, m) -> { float s = env(t, 4, 10, 40, 52); p.r("body", -.4f * s, 0, 0).r("head", -.3f * s, 0, 0); });
    }
    static Attack rangerLanceCross() {
        return atk("rudinn_ranger_lance_cross", 60, (c, t) -> {
            if (t == 6) put(c, "rc", c.tpos());
            if (t == 6 || t == 30) { Vec3 at = t == 6 ? get(c, "rc") : c.tpos(); put(c, "rc", at);
                for (int i = 0; i < 4; i++) { Vec3 d = rotY(new Vec3(1, 0, 0), i * Math.PI / 2 + (t == 30 ? Math.PI / 4 : 0));
                    beam(c, at.add(d.scale(-6)), d, 12, .35f, DIAMOND, 14, 8, 4); } }
            if (t == 20 || t == 44) c.sound(SoundEvents.TRIDENT_THROW, 1, 1.1f);
        }, (p, t, m) -> { float s = env(t, 0, 14, 20, 26) + env(t, 24, 38, 44, 52); p.r("armR", -2.6f * s, 0, 0).r("body", .2f * s, 0, 0); });
    }
    static Attack rangerDiamondWheel() {
        return atk("rudinn_ranger_diamond_wheel", 70, (c, t) -> {
            if (t >= 10 && t < 58 && t % 4 == 0) {
                double a = t * .3; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                suit(c, c.eye().add(d), d.scale(.45).add(c.aim(c.eye()).scale(.2)), DIAMOND, 3, 34);
            }
        }, (p, t, m) -> p.r("armR", -1.6f, t * .4f * env(t, 6, 10, 58, 66), 0).r("body", 0, .2f * wave(t, .3f), 0));
    }
    static Attack ponmanMarch() {
        return atk("ponman_pawn_march", 70, (c, t) -> {
            if (t >= 8 && t < 50 && t % 14 == 8) {
                Vec3 f = flat(c.aim(c.pos())), r = side(f); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) if (k != gap) {
                    Vec3 at = c.pos().add(f.scale(1.5)).add(r.scale(k)).add(0, .4, 0);
                    c.proj().at(at).vel(f.scale(.35)).box(.5f, .8f, .5f).color(0xf0f0f0).dmg(3).warn(6).life(40).spawn();
                    c.proj().at(at.add(0, .6, 0)).vel(f.scale(.35)).box(.4f, .4f, .4f).color(0xf0f0f0).dmg(0).life(40).spawn();
                }
                c.sound(SoundEvents.WOOD_PLACE, 1, .8f);
            }
        }, (p, t, m) -> p.o("body", 0, Math.abs(wave(t, .45f)) * 1.5f, 0).r("body", 0, 0, .15f * wave(t, .45f)));
    }
    static Attack ponmanHop() {
        return atk("ponman_pawn_hop", 64, (c, t) -> {
            if (t >= 8 && t < 50 && t % 7 == 1) {
                Vec3 at = c.eye().add(c.right().scale(c.rnd.nextGaussian())), d = c.tpos().subtract(at); double h = Math.sqrt(d.x * d.x + d.z * d.z);
                // ballistic hop: arc lands on the target's position in ~20 ticks
                c.proj().at(at).vel(new Vec3(d.x / 20, .5 + d.y / 20, d.z / 20)).gravity(.05f).box(.5f, .8f, .5f).color(0xf0f0f0).dmg(3).life(26).spawn();
                if (h > 0) c.sound(SoundEvents.WOOD_STEP, 1, 1.4f);
            }
        }, (p, t, m) -> { float j = t >= 8 && t < 50 ? Math.abs(wave(t, (float) (Math.PI / 7))) : 0; p.o("body", 0, 2.5f * j, 0); });
    }
    static Attack rabbickDustBunnies() {
        return atk("rabbick_dust_bunnies", 66, (c, t) -> {
            if (t >= 8 && t < 48 && t % 8 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 3, -.5, c.rnd.nextGaussian() * 3);
                c.proj().at(at).vel(c.aim(at).scale(.12)).box(.5f, .5f, .5f).color(0x9a9aa8).dmg(2).warn(6).life(30).spawn();
                c.proj().at(at.add(0, .4, 0)).vel(c.aim(at).scale(.12)).box(.9f, .9f, .9f).color(0xb8b8c6).dmg(2).warn(14).life(30).spawn(); // grows
                c.particle(ParticleTypes.POOF, at, 4, .3, .01);
            }
        }, (p, t, m) -> p.o("fluff", 0, wave(t, .7f) * .6f, 0).r("earL", .4f * wave(t, .5f), 0, 0).r("earR", -.4f * wave(t, .5f), 0, 0));
    }
    static Attack rabbickDustCloud() {
        return atk("rabbick_dust_cloud", 50, (c, t) -> {
            if (t < 20 && t % 2 == 0) c.particle(ParticleTypes.POOF, c.pos().add(0, .6, 0), 4, .6, .02);
            if (t == 22) { c.dash(c.aim(c.pos()).multiply(1, 0, 1).scale(1.0)); c.sound(SoundEvents.WOOL_BREAK, 1, .8f); }
            if (t >= 22 && t < 32) { c.aoe(c.pos().add(0, .5, 0), 1.6, 3, .4); c.particle(ParticleTypes.CLOUD, c.pos(), 3, .4, .01); }
        }, (p, t, m) -> { float s = ramp(t, 0, 20) * (1 - ramp(t, 32, 44)); p.r("body", 0, t * .6f * s, 0).o("fluff", 0, 0, 0); });
    }
    static Attack bloxerBlockFall() {
        return atk("bloxer_block_fall", 64, (c, t) -> {
            if (t >= 6 && t < 50 && t % 6 == 0) {
                Vec3 at = c.tpos().add(Math.round(c.rnd.nextGaussian() * 2), 6, Math.round(c.rnd.nextGaussian() * 2));
                c.proj().at(at).vel(new Vec3(0, -.5, 0)).box(1f, 1f, 1f).color(0x4a7ad9).dmg(4).warn(8).life(26).spawn();
                c.proj().at(at.add(0, 0, -.51)).vel(new Vec3(0, -.5, 0)).box(.6f, .6f, .05f).color(0x5e8ee8).dmg(0).warn(8).life(26).spawn();
            }
        }, (p, t, m) -> { float s = env(t, 0, 6, 50, 60); p.o("body", 0, 1.2f * s * Math.abs(wave(t, .52f)), 0).r("body", 0, .3f * s * wave(t, .26f), 0); });
    }
    static Attack bloxerTetris() {
        return atk("bloxer_block_slide", 60, (c, t) -> {
            if (t == 10 || t == 34) {
                Vec3 r = c.right(); int gap = c.rnd.nextInt(4) - 2; double s = t == 10 ? 1 : -1;
                for (int y = 0; y < 3; y++) for (int k = -3; k <= 3; k++) if (k != gap && k != gap + 1)
                    c.proj().at(c.tpos().add(r.scale(6 * s)).add(c.fwd().scale(k)).add(0, y - 1, 0)).vel(r.scale(-.35 * s))
                        .box(.95f, .95f, .95f).color(y % 2 == 0 ? 0x4a7ad9 : 0x2a4a99).dmg(4).warn(10).life(40).spawn();
                c.sound(SoundEvents.PISTON_EXTEND, 1, .8f);
            }
        }, (p, t, m) -> { float s = env(t, 4, 10, 14, 20) + env(t, 28, 34, 38, 44); p.r("body", 0, 0, .3f * s).o("body", 0, 1.5f * s, 0); });
    }
    static Attack jigsawPieces() {
        return atk("jigsawry_puzzle_pieces", 64, (c, t) -> {
            if (t >= 8 && t < 50 && t % 7 == 1) {
                double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = c.tpos().add(Math.cos(a) * 5, 1.5, Math.sin(a) * 5);
                c.proj().at(at).vel(c.aim(at).scale(.4)).box(.7f, .7f, .25f).color(0xc76ad9).dmg(3).warn(8).life(30).spawn();
                c.proj().at(at.add(0, .45, 0)).vel(c.aim(at).scale(.4)).box(.3f, .3f, .25f).color(0xc76ad9).dmg(0).warn(8).life(30).spawn();
                c.sound(SoundEvents.ITEM_PICKUP, .8f, .7f);
            }
        }, (p, t, m) -> p.r("armL", -2f, 0, .5f * wave(t, .4f)).r("armR", -2f, 0, -.5f * wave(t, .4f)).r("knob", 0, t * .3f, 0));
    }
    static Attack cloverClubFan() {
        return atk("clover_club_fan", 60, (c, t) -> {
            if (t == 12 || t == 26 || t == 40) { fan(c, c.eye(), c.aim(c.eye()), 3, .5, .5, CLUB, 3, 40); c.sound(SoundEvents.SNOW_GOLEM_SHOOT, 1, 1.2f); }
        }, (p, t, m) -> { float s = env(t, 8, 12, 14, 18) + env(t, 22, 26, 28, 32) + env(t, 36, 40, 42, 46);
            p.r("head", -.3f * s, 0, 0).r("headL", -.3f * s, .3f, 0).r("headR", -.3f * s, -.3f, 0).r("armL", -1.6f * s, 0, 0).r("armR", -1.6f * s, 0, 0); });
    }
    /** Clover's trivia: four quadrants around the target, all but the "right answer" get struck. */
    static Attack cloverTrivia() {
        return atk("clover_trivia", 70, (c, t) -> {
            if (t == 6) { put(c, "cq", c.tpos()); puti(c, "cqa", c.rnd.nextInt(4)); c.sound(SoundEvents.BOOK_PAGE_TURN, 1, 1); }
            Vec3 o = get(c, "cq"); int ans = geti(c, "cqa");
            if (t == 6) for (int q = 0; q < 4; q++) {
                Vec3 qc = o.add((q % 2 == 0 ? -1.5 : 1.5), -.7, (q < 2 ? -1.5 : 1.5));
                c.proj().at(qc).box(2.8f, .1f, 2.8f).color(q == ans ? 0x5cff8a : 0xff4040).dmg(0).life(40).spawn();
            }
            if (t == 40) for (int q = 0; q < 4; q++) if (q != ans) {
                Vec3 qc = o.add((q % 2 == 0 ? -1.5 : 1.5), .3, (q < 2 ? -1.5 : 1.5));
                c.proj().at(qc).box(2.8f, 2.2f, 2.8f).color(0xfff27a).dmg(5).life(6).pierce().spawn();
                c.particle(ParticleTypes.ELECTRIC_SPARK, qc, 12, .8, .1);
            }
            if (t == 40) c.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, .5f, 1.6f);
        }, (p, t, m) -> { float s = env(t, 0, 6, 40, 50); p.r("armR", -2.8f * s, 0, -.3f).r("headL", 0, .5f * wave(t, .3f), 0).r("headR", 0, -.5f * wave(t, .3f), 0); });
    }
    static Attack cloverHeadsSpin() {
        return atk("clover_heads_spin", 60, (c, t) -> {
            if (t >= 10 && t < 48 && t % 3 == 0) { double a = t * .5;
                for (int i = 0; i < 3; i++) { Vec3 d = new Vec3(Math.cos(a + i * 2.09), 0, Math.sin(a + i * 2.09));
                    suit(c, c.eye().add(d.scale(1.2)), d.scale(.35), i == 0 ? CLUB : i == 1 ? HEART : DIAMOND, 2, 30); } }
        }, (p, t, m) -> p.r("body", 0, t * .5f * env(t, 6, 10, 48, 56), 0));
    }
    static Attack checkerBounce() {
        return atk("checker_bounce", 64, (c, t) -> {
            if (t >= 8 && t < 50 && t % 8 == 0) {
                Vec3 at = c.eye().add(0, .5, 0), d = c.tpos().subtract(at);
                c.proj().at(at).vel(new Vec3(d.x / 24, .6, d.z / 24)).gravity(.05f).box(.9f, .3f, .9f).color((t / 8) % 2 == 0 ? 0xc93232 : 0x1a1a1a).dmg(4).life(30).spawn();
                c.sound(SoundEvents.WOOD_PLACE, 1, 1.2f);
            }
        }, (p, t, m) -> p.o("body", 0, 2 * Math.abs(wave(t, (float) (Math.PI / 8))), 0));
    }
    static Attack checkerRoll() {
        return atk("checker_roll", 56, (c, t) -> {
            if (t == 14) put(c, "cr", flat(c.aim(c.pos())));
            if (t >= 14 && t < 34) { c.dash(get(c, "cr").scale(.9)); c.aoe(c.pos().add(0, .5, 0), c.me.getBbWidth(), 5, 1); }
            if (t == 14) c.sound(SoundEvents.MINECART_RIDING, 1, 1.4f);
        }, (p, t, m) -> { float s = env(t, 6, 14, 34, 44); p.r("body", 0, 0, 1.5f * s).r("body", t * .6f * ramp(t, 14, 34) * (1 - ramp(t, 34, 36)), 0, 0); });
    }
    static Attack kRoundStomp() {
        return atk("k_round_stomp", 64, (c, t) -> {
            if (t == 10) c.dash(new Vec3(0, 1.1, 0).add(c.aim(c.pos()).multiply(.5, 0, .5)));
            if (t == 10) put(c, "ks", c.target.position());
            if (t == 10) c.proj().at(get(c, "ks").add(0, .05, 0)).box(4, .1f, 4).color(0xff4040).dmg(0).warn(26).life(28).spawn();
            if (t == 30) c.dash(new Vec3(0, -2, 0));
            if (t == 34) { Vec3 at = c.pos(); c.aoe(at, 3.6, 9, 1.6); c.particle(ParticleTypes.EXPLOSION, at, 4, 1.2, 0); c.sound(SoundEvents.ANVIL_LAND, 1.4f, .5f);
                for (int i = 0; i < 12; i++) { Vec3 d = rotY(new Vec3(1, 0, 0), i * Math.PI / 6);
                    c.proj().at(at.add(d.scale(2)).add(0, .3, 0)).vel(d.scale(.4)).box(.6f, .3f, .6f).color(0x1a1a1a).dmg(3).life(20).spawn(); } }
        }, (p, t, m) -> { float up = env(t, 4, 12, 28, 34); p.r("armL", 0, 0, -1.4f * up).r("armR", 0, 0, 1.4f * up).o("head", 0, 1.5f * up, 0); });
    }
    static Attack kRoundCrownSpit() {
        return atk("k_round_crown_spit", 60, (c, t) -> {
            if (t >= 10 && t < 46 && t % 9 == 1) { fan(c, c.eye(), c.aim(c.eye()), 5, .9, .45, DIAMOND, 3, 40); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .8f); }
        }, (p, t, m) -> p.o("head", 0, t >= 10 && t < 46 && t % 9 < 4 ? 1.5f : 0, 0).r("head", 0, .1f * wave(t, .7f), 0));
    }

    // =====================================================================================================
    // ROUXLS KAARD, Duke of Puzzles (King's servant: hostile)
    // =====================================================================================================
    static void rouxls(List<CharacterDef> out) {
        ModelDef m = biped(0xdde4ff, 0x1f2a6b, 0x1f2a6b, 0x141a44, 5.6f, 6.4f)
            .box("hat", "head", 0, 3.8f, 0, 7.4f, 1.2f, 7.4f, 0x141a44).box("hat_top", "head", 0, 5.6f, 0, 4.2f, 2.6f, 4.2f, 0x141a44)
            .box("feather", "head", 2.8f, 6.4f, 1, .6f, 4, 1, 0x3ab0c7)
            .box("mustache", "head", 0, -1.6f, -3.3f, 5.2f, .8f, .4f, 0x1a1a1a)
            .glow("eyes", "head", 0, .6f, -3.3f, 3.8f, .7f, .3f, 0x1a1a1a)
            .box("cape", "body", 0, -.4f, 2.4f, 7, 8, .5f, 0x3ab0c7)
            .box("collar", "body", 0, 3.6f, 0, 7.4f, 1.4f, 5, 0xf0f0f0);
        out.add(CharacterDef.of("rouxls_kaard", "Rouxls Kaard", Faction.CARD_KINGDOM).hp(70).keep(6).rest(24).egg(0x1f2a6b, 0x3ab0c7)
            .model(m).attacks(rouxlsWorms(), rouxlsBlockPuzzle(), rouxlsThouFool()).build());
    }
    /** Ch1/Ch2 battles: green "wormes" crawl in along the ground. */
    static Attack rouxlsWorms() {
        return atk("rouxls_wormes", 70, (c, t) -> {
            if (t >= 8 && t < 54 && t % 6 == 2) {
                double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = c.target.position().add(Math.cos(a) * 6, .2, Math.sin(a) * 6);
                Vec3 v = c.target.position().subtract(at).multiply(1, 0, 1).normalize().scale(.22);
                for (int s = 0; s < 3; s++) c.proj().at(at.subtract(v.scale(s * 1.6))).vel(v).box(.35f, .3f, .35f).color(s == 0 ? 0x7cff5a : 0x4ad03a).dmg(s == 0 ? 3 : 2).life(44).spawn();
                c.sound(SoundEvents.SLIME_SQUISH, .7f, 1.6f);
            }
        }, (p, t, m) -> p.r("armR", -2.2f, 0, -.4f * wave(t, .4f)).r("head", 0, .3f * wave(t, .2f), 0).r("cape", .2f * wave(t, .5f), 0, 0));
    }
    /** Shoves a row of puzzle blocks at the target. */
    static Attack rouxlsBlockPuzzle() {
        return atk("rouxls_block_puzzle", 60, (c, t) -> {
            if (t == 16) { Vec3 f = flat(c.aim(c.pos())), r = side(f); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) if (k != gap) c.proj().at(c.pos().add(f.scale(1.5)).add(r.scale(k)).add(0, .5, 0)).vel(f.scale(.4))
                    .box(.95f, .95f, .95f).color(0x6a5acd).dmg(4).warn(8).life(36).spawn();
                c.sound(SoundEvents.PISTON_EXTEND, 1, .7f); }
        }, (p, t, m) -> { float s = env(t, 4, 14, 18, 30); p.r("armL", -1.6f * s, 0, 0).r("armR", -1.6f * s, 0, 0).o("body", 0, 0, -1.5f * s); });
    }
    /** "THOU FOOL": a flourish of white letters spiralling out. */
    static Attack rouxlsThouFool() {
        return atk("rouxls_thou_fool", 56, (c, t) -> {
            if (t >= 10 && t < 40 && t % 2 == 0) { double a = t * .6; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                c.proj().at(c.eye().add(d)).vel(d.scale(.35).add(c.aim(c.eye()).scale(.15))).box(.3f, .5f, .1f).color(0xffffff).dmg(2).life(30).spawn(); }
        }, (p, t, m) -> { float s = env(t, 4, 10, 40, 50); p.r("armR", -2.8f * s, 0, -.6f * s).r("body", 0, t * .3f * s, 0); });
    }

    // =====================================================================================================
    // JEVIL, the secret boss (free agent: own NEUTRAL faction, hostile to the player). All 16 canon turns.
    // =====================================================================================================
    static void jevil(List<CharacterDef> out) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 7, 0, 6, 7, 4, 0x6a2f9a)
            .box("stripe", "body", 0, 0, -2.1f, 1.4f, 7, .3f, 0xffd84a)
            .box("collar", "body", 0, 3.6f, 0, 8, 1.6f, 5.4f, 0xffd84a)
            .box("head", "body", 0, 7.5f, 0, 6, 6, 6, 0x3b3b6b)
            .box("grin", "head", 0, -1.4f, -3.1f, 4.6f, 1.4f, .3f, 0xffffff)
            .glow("eyeL", "head", -1.4f, .8f, -3.1f, 1.1f, 1.3f, .3f, 0xffd84a)
            .glow("eyeR", "head", 1.4f, .8f, -3.1f, 1.1f, 1.3f, .3f, 0xffd84a)
            .box("hatL", "head", -3, 4, 0, 3, 4, 3, 0x6a2f9a).box("hatR", "head", 3, 4, 0, 3, 4, 3, 0xffd84a)
            .glow("bellL", "hatL", -1.6f, 2.2f, 0, 1.2f, 1.2f, 1.2f, 0xffd84a).glow("bellR", "hatR", 1.6f, 2.2f, 0, 1.2f, 1.2f, 1.2f, 0x6a2f9a)
            .limb("armL", "body", -4, 3, 0, 1.6f, 6, 1.6f, 0x6a2f9a).limb("armR", "body", 4, 3, 0, 1.6f, 6, 1.6f, 0xffd84a)
            .limb("legL", null, -1.5f, 4, 0, 1.8f, 4, 1.8f, 0xffd84a).limb("legR", null, 1.5f, 4, 0, 1.8f, 4, 1.8f, 0x6a2f9a);
        out.add(CharacterDef.of("jevil", "Jevil", Faction.NEUTRAL).att(Attitude.HOSTILE).hp(260).boss().keep(7).rest(16).speed(.32f).egg(0x6a2f9a, 0xffd84a)
            .model(m).attacks(
                // phase 1 (hp >= 66%)
                jvFiveSpade(), jvSpadeSpirals(false), jvBomb("jevil_heart_bombs", HEART, above(.66f)), jvScythes(false), jvCarousel(false),
                jvBomb("jevil_club_bombs", CLUB, above(.66f)), jvDiamondRelease(false),
                // phase 2 (33..66%)
                jvSpadeSpirals(true), jvCarousel(true), jvBomb("jevil_spade_bombs", SPADE, m2 -> m2.hpFrac() < .66f && m2.hpFrac() >= .33f),
                jvThreeClub(), jvScythes(true), jvDiamondToss(),
                // phase 3 (< 33%)
                jvChaosBomb(), jvDiamondRelease(true), jvFinalChaos()).build());
    }
    static java.util.function.Predicate<com.undeltarune.entity.UtMob> mid() { return m -> m.hpFrac() < .66f && m.hpFrac() >= .33f; }
    static Attack jvFiveSpade() {
        return atk("jevil_five_spade", 70, (c, t) -> {
            if (t % 16 == 2 && t < 60) { Vec3 to = c.target.position().add(c.right().scale(c.rnd.nextBoolean() ? 6 : -6)).add(c.fwd().scale(c.rnd.nextGaussian()));
                c.particle(ParticleTypes.WITCH, c.pos().add(0, 1, 0), 10, .4, .05); c.tp(to); c.sound(SoundEvents.ENDERMAN_TELEPORT, 1, 1.6f); }
            if (t % 16 == 10 && t < 60) { fan(c, c.eye(), c.aim(c.eye()), 5, .9, .55, SPADE, 4, 40); c.sound(SoundEvents.SNOW_GOLEM_SHOOT, 1, .8f); }
        }, (p, t, m) -> { float s = (t % 16 >= 8 && t % 16 < 13) ? 1 : 0; p.r("armR", -1.8f * s, 0, 0).r("armL", -1.8f * s, 0, 0).r("head", 0, 0, .3f * wave(t, .5f)); }, above(.66f));
    }
    static Attack jvSpadeSpirals(boolean two) {
        return atk(two ? "jevil_spade_spirals_ii" : "jevil_spade_spirals", two ? 96 : 70, (c, t) -> {
            if (t == 4) put(c, "js", c.tpos());
            int n = two ? 16 : 10, gap = 4, start = 10;
            if (t == 4) ring(c, get(c, "js"), n, 6, 0, 0, SPADE, 0, start + n * gap, 0); // the circle forms (harmless markers)
            int i = (t - start) / gap;
            if (t >= start && (t - start) % gap == 0 && i < n) {
                double a = i * Math.PI * 2 / n + (two ? (c.rnd.nextDouble() - .5) * .6 : 0); Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                Vec3 at = get(c, "js").add(d.scale(6)); suit(c, at, d.scale(two ? -.65 : -.5), SPADE, 4, 40, 0, 1.6f);
                c.sound(SoundEvents.SNOW_GOLEM_SHOOT, .7f, 1 + i * .05f);
            }
        }, (p, t, m) -> p.r("body", 0, t * .4f, 0).o("body", 0, 1.2f * wave(t, .3f), 0).r("armL", 0, 0, -1.4f).r("armR", 0, 0, 1.4f), two ? mid() : above(.66f));
    }
    /** Bomb drops above the target, then bursts in the suit's canon pattern. */
    static Attack jvBomb(String id, int suitColor, java.util.function.Predicate<com.undeltarune.entity.UtMob> gate) {
        return atk(id, 76, (c, t) -> {
            int k = t / 18, ph = t % 18;
            if (k >= 4) return;
            if (ph == 0) { Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 2, 5, c.rnd.nextGaussian() * 2); put(c, "jb", at.add(0, -4.5, 0));
                c.proj().at(at).vel(new Vec3(0, -.4, 0)).box(.9f, .9f, .9f).color(0x2a2a2a).dmg(0).life(11).spawn();
                c.proj().at(at).vel(new Vec3(0, -.4, 0)).box(.5f, .5f, .95f).color(suitColor).dmg(0).life(11).spawn();
                c.sound(SoundEvents.TNT_PRIMED, .6f, 1.8f); }
            if (ph == 12) { Vec3 b = get(c, "jb"); c.particle(ParticleTypes.EXPLOSION, b, 1, 0, 0); c.sound(SoundEvents.GENERIC_EXPLODE, .5f, 1.6f);
                if (suitColor == HEART) for (int i = 0; i < 4; i++) { Vec3 at = b.add(rotY(new Vec3(.8, 0, .8), i * Math.PI / 2)); suit(c, at, c.aim(at).scale(.45), HEART, 4, 30); }
                else if (suitColor == CLUB) fan(c, b, c.aim(b), 3, .6, .5, CLUB, 4, 30);
                else if (suitColor == SPADE) ring(c, b, 10, .4, 0, .45, SPADE, 4, 26, 0);
                else fan(c, b, c.aim(b), 3, .3, .55, DIAMOND, 4, 30);
            }
        }, (p, t, m) -> { float s = (t % 18 < 4) ? 1 : 0; p.r("armR", -2.8f * s, 0, -.4f).r("armL", -.4f, 0, .4f).o("body", 0, Math.abs(wave(t, .35f)), 0); }, gate);
    }
    static Attack jvScythes(boolean two) {
        return atk(two ? "jevil_spinning_scythes_ii" : "jevil_spinning_scythes", 80, (c, t) -> {
            if (t == 4) put(c, "jsc", c.tpos());
            Vec3 o = get(c, "jsc");
            if (t >= 10 && t < 70 && t % 3 == 0) for (int i = 0; i < 4; i++) {
                double a = i * Math.PI / 2 + t * .12, r = 2.6 + Math.sin(t * .1) * 1.4; Vec3 at = o.add(Math.cos(a) * r, 0, Math.sin(a) * r);
                c.proj().at(at).face(new Vec3(-Math.sin(a), 0, Math.cos(a))).box(.2f, .2f, 1.6f).color(0xe8e8e8).dmg(4).life(3).warn(t == 10 ? 6 : 0).spawn();
                c.proj().at(at.add(0, .4, 0)).face(new Vec3(Math.cos(a), 0, Math.sin(a))).box(.2f, .6f, .8f).color(0xb0b0c0).dmg(0).life(3).spawn();
            }
            if (two && t >= 14 && t % 20 == 14) { double s = (t / 20) % 2 == 0 ? 1 : -1; Vec3 r = c.right();
                Vec3 at = o.add(r.scale(-8 * s)).add(0, (t / 20) % 2 == 0 ? 1.2 : -.4, 0);
                c.proj().at(at).vel(r.scale(.7 * s)).box(.4f, 2.6f, .4f).color(0xd02a2a).dmg(6).warn(8).life(30).pierce().spawn();
                c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .6f); }
            if (t % 12 == 0) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .5f, 1.4f);
        }, (p, t, m) -> p.r("body", 0, t * .35f, 0).r("armL", 0, 0, -1.5f).r("armR", 0, 0, 1.5f), two ? mid() : above(.66f));
    }
    /** Carousel: rows of horses (and ducks in II) sweep past bobbing on opposite phases. */
    static Attack jvCarousel(boolean two) {
        return atk(two ? "jevil_carousel_ii" : "jevil_carousel", 90, (c, t) -> {
            if (t == 4) put(c, "jc", c.tpos());
            if (t >= 10 && t < 70 && t % 10 == 0) {
                Vec3 r = c.right(), f = c.fwd(), o = get(c, "jc"); int row = t / 10;
                for (int k = -1; k <= 1; k++) {
                    boolean duck = two && (row + k) % 2 == 0; double bob = Math.sin(row * 1.3 + k) * .9;
                    Vec3 at = o.add(r.scale(-8)).add(f.scale(k * 1.6)).add(0, bob, 0);
                    c.proj().at(at).vel(r.scale(.38).add(0, duck ? -.02 : .02, 0)).box(.5f, .9f, 1.2f).color(duck ? 0xffd84a : 0xf0f0f0).dmg(4).warn(6).life(44).spawn();
                    c.proj().at(at.add(0, .6, -.5)).vel(r.scale(.38)).box(.35f, .5f, .35f).color(duck ? 0xff8a2a : 0xf0f0f0).dmg(0).life(44).spawn(); // head
                    c.proj().at(at.add(0, 1.4, 0)).vel(r.scale(.38)).box(.08f, 1.2f, .08f).color(0xffd84a).dmg(0).life(44).spawn();       // pole
                }
                c.sound(SoundEvents.NOTE_BLOCK_BELL.value(), .6f, .8f + (row % 4) * .2f);
            }
        }, (p, t, m) -> p.r("armL", -2.6f, 0, .3f).r("armR", -2.6f, 0, -.3f).r("head", 0, 0, .4f * wave(t, .3f)).o("body", 0, 1.2f * Math.abs(wave(t, .3f)), 0), two ? mid() : above(.66f));
    }
    static Attack jvDiamondRelease(boolean two) {
        return atk(two ? "jevil_diamond_release_ii" : "jevil_diamond_release", 70, (c, t) -> {
            if (t >= 6 && t < 60 && t % (two ? 4 : 2) == 0) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3, -1.5, c.rnd.nextGaussian() * 3);
                suit(c, at, new Vec3(0, two ? .2 : .45, 0), DIAMOND, 3, 26, 5, 1);
            }
        }, (p, t, m) -> p.r("armL", 0, 0, -2.4f + .3f * wave(t, .6f)).r("armR", 0, 0, 2.4f - .3f * wave(t, .6f)).o("body", 0, 1.5f, 0), two ? below(.33f) : above(.66f));
    }
    static Attack jvThreeClub() {
        return atk("jevil_three_club", 70, (c, t) -> {
            if (t == 6) for (int i = 0; i < 3; i++) put(c, "jtc" + i, c.tpos().add(rotY(new Vec3(5, 1, 0), i * 2.09 + c.rnd.nextDouble())));
            if (t == 6) for (int i = 0; i < 3; i++) suit(c, get(c, "jtc" + i), Vec3.ZERO, CLUB, 0, 54, 0, 1.6f);
            for (int b = 0; b < 3; b++) if (t == 22 + b * 12) for (int i = 0; i < 3; i++) { Vec3 at = get(c, "jtc" + i); fan(c, at, c.aim(at), 3, .5, .5, CLUB, 4, 30); }
            if (t == 22 || t == 34 || t == 46) c.sound(SoundEvents.SNOW_GOLEM_SHOOT, 1, 1);
        }, (p, t, m) -> p.r("armL", -2.2f, 0, .5f).r("armR", -2.2f, 0, -.5f).r("head", 0, 0, .3f * wave(t, .5f)), mid());
    }
    static Attack jvDiamondToss() {
        return atk("jevil_diamond_toss", 72, (c, t) -> {
            if (t % 12 == 2 && t < 64) { Vec3 to = c.target.position().add(rotY(new Vec3(6, 0, 0), c.rnd.nextDouble() * Math.PI * 2));
                c.particle(ParticleTypes.WITCH, c.pos().add(0, 1, 0), 8, .4, .05); c.tp(to); c.sound(SoundEvents.ENDERMAN_TELEPORT, .8f, 1.8f); }
            if (t % 12 == 8 && t < 64) { suit(c, c.eye(), c.aim(c.eye()).scale(.6), DIAMOND, 5, 30, 0, 1.4f); c.sound(SoundEvents.TRIDENT_THROW, .8f, 1.6f); }
        }, (p, t, m) -> { float s = (t % 12 >= 4 && t % 12 < 10) ? 1 : 0; p.r("armR", -2.8f * s + .6f * (t % 12 == 8 ? 1 : 0), 0, 0); }, mid());
    }
    static Attack jvChaosBomb() {
        return atk("jevil_chaos_bomb", 80, (c, t) -> {
            int[] suits = {HEART, SPADE, CLUB, DIAMOND};
            if (t % 10 == 0 && t < 60) { int s = suits[c.rnd.nextInt(4)]; puti(c, "jcs" + (t / 10) % 2, s);
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 2.5, 5, c.rnd.nextGaussian() * 2.5); put(c, "jcb" + (t / 10) % 2, at.add(0, -4.4, 0));
                c.proj().at(at).vel(new Vec3(0, -.4, 0)).box(.9f, .9f, .9f).color(0x2a2a2a).dmg(0).life(11).spawn();
                c.proj().at(at).vel(new Vec3(0, -.4, 0)).box(.5f, .5f, .95f).color(s).dmg(0).life(11).spawn(); }
            if (t % 10 == 1 && t > 10 && t < 72) { int slot = ((t - 11) / 10) % 2; Vec3 b = get(c, "jcb" + slot); int s = geti(c, "jcs" + slot);
                c.particle(ParticleTypes.EXPLOSION, b, 1, 0, 0); c.sound(SoundEvents.GENERIC_EXPLODE, .4f, 1.7f);
                if (s == HEART) for (int i = 0; i < 4; i++) { Vec3 at = b.add(rotY(new Vec3(.8, 0, .8), i * Math.PI / 2)); suit(c, at, c.aim(at).scale(.45), HEART, 4, 30); }
                else if (s == CLUB) fan(c, b, c.aim(b), 3, .6, .5, CLUB, 4, 30);
                else if (s == SPADE) ring(c, b, 10, .4, 0, .45, SPADE, 4, 26, 0);
                else fan(c, b, c.aim(b), 3, .3, .55, DIAMOND, 4, 30); }
        }, (p, t, m) -> p.r("body", 0, t * .5f, 0).r("armL", -2.8f, 0, .6f).r("armR", -2.8f, 0, -.6f).o("body", 0, 1.5f * Math.abs(wave(t, .4f)), 0), below(.33f));
    }
    /** "CHAOS, CHAOS!": giant Devilsknives fall into white pillars, finishing with a screen-filling knife. */
    static Attack jvFinalChaos() {
        return atk("jevil_final_chaos", 120, (c, t) -> {
            if (t == 2) { c.sound(SoundEvents.WITHER_SPAWN, .6f, 1.6f); put(c, "jf", c.target.position()); }
            if (t >= 10 && t < 80 && t % 10 == 0) {
                Vec3 base = c.target.position().add(c.rnd.nextGaussian() * 2.5, 0, c.rnd.nextGaussian() * 2.5);
                c.proj().at(base.add(0, 8, 0)).vel(new Vec3(0, -.8, 0)).box(.6f, 4, .6f).color(0xd02a2a).dmg(6).warn(6).life(10).spawn();
                c.proj().at(base.add(0, 10, 0)).vel(new Vec3(0, -.8, 0)).box(2.4f, .5f, .5f).color(0xd02a2a).dmg(0).life(10).spawn();
                beam(c, base.add(0, -.5, 0), new Vec3(0, 1, 0), 12, 1.2f, 0xffffff, 10, 8, 6); // pillar of light
            }
            if (t == 86) put(c, "jf", c.target.position());
            if (t == 86) c.proj().at(get(c, "jf").add(0, .05, 0)).box(5, .1f, 5).color(0xff3030).dmg(0).warn(18).life(20).spawn();
            if (t == 104) { Vec3 at = get(c, "jf"); c.proj().at(at.add(0, 3, 0)).box(4, 8, 4).color(0xd02a2a).dmg(12).life(8).pierce().spawn();
                c.particle(ParticleTypes.EXPLOSION_EMITTER, at, 1, 0, 0); c.sound(SoundEvents.GENERIC_EXPLODE, 1.4f, .6f); }
        }, (p, t, m) -> { float s = env(t, 0, 10, 100, 116); p.r("armR", -3f * s, 0, 0).r("armL", -3f * s, 0, 0).o("body", 0, 3f * s, 0).r("body", 0, t * .2f * s, 0); }, below(.15f));
    }

    // =====================================================================================================
    // KING of Spades (Ch1 final boss: hostile)
    // =====================================================================================================
    static void king(List<CharacterDef> out) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 9, 0, 9, 10, 6, 0x1e1e3a)
            .box("belly", "body", 0, -1, -3.1f, 6, 6, .3f, 0x2c2c50)
            .box("cape", "body", 0, -1, 3.4f, 11, 12, .6f, 0x8a1c2a)
            .box("capeCollar", "body", 0, 5, 0, 11, 1.6f, 7, 0x8a1c2a)
            .box("head", "body", 0, 9, 0, 8, 7, 7, 0x1e1e3a)
            .box("spade_tip", "head", 0, 5, 0, 3.4f, 3.4f, 3.4f, 0x1e1e3a)
            .box("crown", "head", 0, 4.2f, 0, 5, 2, 5, 0xffd84a)
            .box("grin", "head", 0, -1.6f, -3.6f, 6.4f, 2, .3f, 0xffffff)
            .glow("eyeL", "head", -1.8f, 1.2f, -3.6f, 1.6f, 1, .3f, 0xffd84a).glow("eyeR", "head", 1.8f, 1.2f, -3.6f, 1.6f, 1, .3f, 0xffd84a)
            .box("beard", "head", 0, -3.8f, -2, 6, 2, 4, 0xf0f0f0)
            .limb("armL", "body", -5.8f, 4, 0, 2.6f, 8, 2.6f, 0x1e1e3a).limb("armR", "body", 5.8f, 4, 0, 2.6f, 8, 2.6f, 0x1e1e3a)
            .box("chain", "armR", 0, -8.4f, -1, .8f, .8f, .8f, 0xb0b0c0)
            .limb("legL", null, -2.2f, 4.5f, 0, 3, 4.5f, 3, 0x1e1e3a).limb("legR", null, 2.2f, 4.5f, 0, 3, 4.5f, 3, 0x1e1e3a);
        out.add(CharacterDef.of("king", "King", Faction.CARD_KINGDOM).hp(240).boss().keep(7).rest(18).scale(1.45f).size(1f, 2.6f).egg(0x1e1e3a, 0x8a1c2a)
            .model(m).attacks(kingSpades(), kingSwing(), kingChains(), kingSpikedCage(), kingChainedCage()).build());
    }
    static Attack kingSpades() {
        return atk("king_spade_attack", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 4 == 0) {
                double s = (t / 16) % 2 == 0 ? 1 : -1; Vec3 r = c.right();
                Vec3 at = c.tpos().add(r.scale(-8 * s)).add(c.fwd().scale(c.rnd.nextGaussian() * 1.5)).add(0, c.rnd.nextDouble() * 1.5 - .5, 0);
                suit(c, at, r.scale(.55 * s), SPADE, 5, 32, 4, 1.3f);
            }
            if (t % 8 == 0 && t >= 8 && t < 56) c.sound(SoundEvents.SNOW_GOLEM_SHOOT, .8f, .7f);
        }, (p, t, m) -> { float s = t >= 8 && t < 56 ? wave(t, (float) (Math.PI / 8)) : 0; p.r("armR", -1.8f + s, 0, -.3f).r("armL", -1.8f - s, 0, .3f).r("body", 0, .2f * s, 0); });
    }
    /** Grapples the target with his chain and swings it while a line of spades marches across. */
    static Attack kingSwing() {
        return atk("king_swing_attack", 80, (c, t) -> {
            if (t == 10) { c.sound(SoundEvents.CHAIN_PLACE, 1.2f, .6f); for (int k = 0; k < 8; k++)
                c.proj().at(c.eye().add(c.tpos().subtract(c.eye()).scale(k / 8.0))).box(.3f, .3f, .3f).color(0xb0b0c0).dmg(0).life(56).spawn(); }
            if (t >= 12 && t < 64) { double sw = Math.sin((t - 12) * .2) * (t > 40 ? .6 : .4);
                c.target.setDeltaMovement(0, sw, 0); c.target.hurtMarked = true; }
            if (t >= 14 && t < 64 && t % 5 == 0) { Vec3 r = c.right(); Vec3 at = c.tpos().add(r.scale(-7)).add(0, (t / 5) % 2 == 0 ? .2 : 1.4, 0);
                suit(c, at, r.scale(.5), SPADE, 4, 30, 0, 1.2f); }
        }, (p, t, m) -> { float s = env(t, 4, 10, 64, 74); p.r("armR", -2f * s + .8f * s * wave(t - 12, .2f), 0, 0).r("body", .1f * s * wave(t, .2f), 0, 0); });
    }
    static Attack kingChains() {
        return atk("king_chain_attack", 76, (c, t) -> {
            int rate = c.me.hpFrac() < .5f ? 6 : 9, warn = c.me.hpFrac() < .5f ? 10 : 16;
            if (t >= 6 && t < 60 && t % rate == 0) {
                double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                Vec3 from = c.tpos().add(d.scale(-7));
                beam(c, from, d, 14, .3f, 0xff3030, warn, 2, 0); // red trail telegraph
                c.proj().at(from).vel(d.scale(14.0 / 10)).box(.5f, .5f, .5f).color(0x8a8aa0).dmg(5).warn(warn).life(warn + 10).spawn(); // spade-topped chain head
                c.proj().at(from).vel(d.scale(14.0 / 10)).box(.35f, .35f, 1.2f).color(0xb0b0c0).dmg(0).warn(warn).life(warn + 10).spawn();
            }
            if (t % 12 == 6) c.sound(SoundEvents.CHAIN_BREAK, .8f, .8f);
        }, (p, t, m) -> p.r("armR", -2.4f, .4f * wave(t, .4f), 0).r("armL", -1.2f, 0, .3f).r("cape", .2f * wave(t, .3f), 0, 0));
    }
    /** Spiked cage traps the target and bounces off a shrinking square of walls. */
    static Attack kingSpikedCage() {
        return atk("king_spiked_cage", 90, (c, t) -> {
            if (t == 6) { put(c, "kco", c.target.position()); put(c, "kcp", c.target.position()); put(c, "kcv", new Vec3(.3, 0, .22)); c.sound(SoundEvents.IRON_DOOR_CLOSE, 1, .6f); }
            if (t < 6) return;
            double half = c.me.hpFrac() < .5f ? 2.6 : 3.6;
            Vec3 o = get(c, "kco"), p = get(c, "kcp"), v = get(c, "kcv");
            if (t > 14) { p = p.add(v); if (Math.abs(p.x - o.x) > half) v = new Vec3(-v.x, 0, v.z); if (Math.abs(p.z - o.z) > half) v = new Vec3(v.x, 0, -v.z); put(c, "kcp", p); put(c, "kcv", v); }
            if (t % 2 == 0 && t < 84) for (int i = 0; i < 8; i++) { Vec3 d = rotY(new Vec3(1.3, 0, 0), i * Math.PI / 4 + t * .05);
                c.proj().at(p.add(d).add(0, .9, 0)).box(.3f, .3f, .3f).color(0xd0d0e0).dmg(3).warn(t < 14 ? 6 : 0).life(2).spawn(); }
            if (t % 6 == 0 && t < 84) for (int i = 0; i < 4; i++) { Vec3 w = rotY(new Vec3(half + 1.3, 0, 0), i * Math.PI / 2);
                c.particle(ParticleTypes.CRIT, o.add(w).add(0, .5, 0), 4, .6, 0); }
            // the cage drags the target along: stay inside the bars
            if (t > 14 && t < 84 && c.target.position().distanceTo(p) > 1.2) { Vec3 pull = p.subtract(c.target.position()).multiply(.25, 0, .25); c.target.setDeltaMovement(pull); c.target.hurtMarked = true; }
        }, (p, t, m) -> p.r("armL", -2.6f, 0, .4f).r("armR", -2.6f, 0, -.4f).o("body", 0, .6f * wave(t, .25f), 0));
    }
    /** Cage on a chain is yanked to heart markers; on later turns a grid of spades also appears. */
    static Attack kingChainedCage() {
        return atk("king_chained_cage", 96, (c, t) -> {
            if (t == 4) put(c, "kq", c.target.position());
            int k = (t - 10) / 20, ph = (t - 10) % 20;
            if (t >= 10 && k < 4) {
                if (ph == 0) { Vec3 h = get(c, "kq").add(c.rnd.nextGaussian() * 3, 0, c.rnd.nextGaussian() * 3); put(c, "kh", h);
                    suit(c, h.add(0, .4, 0), Vec3.ZERO, HEART, 0, 14, 0, 1.2f); c.sound(SoundEvents.CHAIN_PLACE, 1, 1); }
                if (ph >= 10) { Vec3 to = get(c, "kh"), d = to.subtract(c.target.position()).multiply(.3, 0, .3); c.target.setDeltaMovement(d.x, .05, d.z); c.target.hurtMarked = true; }
                if (ph % 2 == 0) for (int i = 0; i < 6; i++) { Vec3 d = rotY(new Vec3(1.2, 0, 0), i * Math.PI / 3);
                    c.proj().at(c.target.position().add(d).add(0, .9, 0)).box(.28f, .28f, .28f).color(0xd0d0e0).dmg(3).life(2).spawn(); }
            }
            if (t == 20 && c.me.hpFrac() < .5f) for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) if ((x + z) % 2 == 0)
                suit(c, get(c, "kq").add(x * 1.8, .6, z * 1.8), Vec3.ZERO, SPADE, 4, 70, 14, 1);
        }, (p, t, m) -> { float y = (t - 10) % 20 >= 10 ? 1 : 0; p.r("armR", -2.2f - .8f * y, 0, 0).r("body", .15f * y, 0, 0); });
    }

    // =====================================================================================================
    // SEAM (shopkeeper, former mage; friendly unless provoked)
    // =====================================================================================================
    static void seam(List<CharacterDef> out) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 6, 0, 9, 9, 7, 0x7a5aa8)
            .box("patch", "body", 2, 1, -3.6f, 3, 3, .3f, 0xb48ad8)
            .box("head", "body", 0, 7.5f, 0, 8, 6, 6, 0x7a5aa8)
            .box("earL", "head", -3, 4, 0, 2.4f, 3, 2, 0x7a5aa8).box("earR", "head", 3, 4, 0, 2.4f, 3, 2, 0x7a5aa8)
            .glow("button_eye", "head", -1.8f, .6f, -3.1f, 1.8f, 1.8f, .3f, 0x2a2a2a)
            .glow("star_eye", "head", 1.8f, .6f, -3.1f, 1.6f, 1.6f, .3f, 0xffd84a)
            .box("stitch", "head", 0, -1.6f, -3.1f, 3, .4f, .3f, 0x2a2a2a)
            .limb("armL", "body", -5.4f, 3, 0, 2, 5, 2, 0x7a5aa8).limb("armR", "body", 5.4f, 3, 0, 2, 5, 2, 0x7a5aa8)
            .limb("legL", null, -2, 2.5f, 0, 2.4f, 2.5f, 2.4f, 0x6a4a98).limb("legR", null, 2, 2.5f, 0, 2.4f, 2.5f, 2.4f, 0x6a4a98);
        out.add(CharacterDef.of("seam", "Seam", Faction.MERCHANT).att(Attitude.NEUTRAL).hp(60).keep(6).rest(36).speed(.18f).size(.9f, 1.4f).egg(0x7a5aa8, 0xffd84a)
            .model(m).attacks(seamStarMagic()).build());
    }
    /** Seam's old stage magic: a twinkling star burst. */
    static Attack seamStarMagic() {
        return atk("seam_star_magic", 60, (c, t) -> {
            if (t < 20 && t % 2 == 0) c.particle(ParticleTypes.END_ROD, c.eye().add(0, .6, 0), 3, .3, .02);
            if (t == 20 || t == 32) { for (int i = 0; i < 5; i++) { Vec3 d = rotY(c.aim(c.eye()), (i - 2) * .25);
                c.proj().at(c.eye()).vel(d.scale(.5)).box(.4f, .4f, .1f).color(0xffd84a).dmg(3).life(30).spawn(); }
                c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, 1.4f); }
        }, (p, t, m) -> { float s = env(t, 0, 16, 36, 50); p.r("armR", -2.8f * s, 0, -.3f).r("head", 0, 0, .2f * wave(t, .3f) * s); });
    }
    private DrCh1() {}
}
