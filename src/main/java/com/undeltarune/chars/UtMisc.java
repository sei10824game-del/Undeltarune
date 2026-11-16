package com.undeltarune.chars;

import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import java.util.List;

import static com.undeltarune.chars.UtCoreFinale.*;

/** Temmie Village extras, Annoying Dog, secret/joke enemies (Mad Mew Mew, Glyde, So Sorry), W.D. Gaster + followers, Napstablook. */
public final class UtMisc {
    public static void register(List<CharacterDef> out) { temmies(out); dog(out); secrets(out); gaster(out); blook(out); }

    static ModelDef tem(int fur, int extra) {
        return new ModelDef()
            .box("body", null, 0, 5, 0, 7, 6, 6, fur)
            .box("head", "body", 0, 6, -1, 9, 6, 7, fur)
            .box("earL", "head", -5.5f, 1, 0, 3, 2, 3, fur).box("earR", "head", 5.5f, 1, 0, 3, 2, 3, fur)
            .box("hair", "head", 0, 3.5f, 0, 6, 2, 5, 0xffe0a0)
            .glow("eyeL", "head", -2, .5f, -3.6f, 1, 1.5f, .2f, 0x000000).glow("eyeR", "head", 2, .5f, -3.6f, 1, 1.5f, .2f, 0x000000)
            .box("blushL", "head", -3, -1, -3.6f, 1.5f, .6f, .2f, 0xff9ab0).box("blushR", "head", 3, -1, -3.6f, 1.5f, .6f, .2f, 0xff9ab0)
            .box("extra", "head", 0, -4, -3.6f, 3, 1, .3f, extra)
            .limb("legL", null, -2, 2, 0, 2, 2, 2, fur).limb("legR", null, 2, 2, 0, 2, 2, 2, fur)
            .limb("armL", "body", -4, 2, 0, 1.5f, 3, 1.5f, fur).limb("armR", "body", 4, 2, 0, 1.5f, 3, 1.5f, fur);
    }
    // ============================== TEMMIE VILLAGE ==============================
    static void temmies(List<CharacterDef> out) {
        out.add(CharacterDef.of("bob_temmie", "Bob", Faction.UNDERGROUND).att(Attitude.FRIENDLY).hp(20).keep(4).speed(.3f).egg(0xffffff, 0x6fb0ff).size(.6f, .9f)
            .model(tem(0xfafafa, 0x222222))
            .attacks(atk("calm_hoi", 50, (c, t) -> {
                // Bob is the calm one: polite little hello-pellets
                if (t == 10 || t == 30) for (int k = -1; k <= 1; k++) orb(c, c.eye(), c.aim(c.eye()).add(c.right().scale(k * .2)).normalize().scale(.25), .2f, 0xfafafa, 1, 30, 0);
                if (t == 10) c.sound(SoundEvents.CAT_AMBIENT, .8f, 1.2f);
            }, (p, t, m) -> p.r("head", 0, 0, .1f * sin(t, .3f))))
            .build());
        out.add(CharacterDef.of("tem_shopkeeper", "Tem Shopkeeper", Faction.MERCHANT).att(Attitude.FRIENDLY).hp(30).keep(4).speed(.25f).egg(0xffffff, 0xf0c040).size(.6f, .9f)
            .model(tem(0xfafafa, 0xff6060).box("apron", "body", 0, -1, -3.1f, 6, 4, .3f, 0x6fb0ff))
            .attacks(
                atk("tem_flakes", 56, (c, t) -> {
                    // hurls handfuls of tem flakes (the fud of tem)
                    if (t % 6 == 0 && t < 40) { Vec3 at = c.eye(); Vec3 d = c.tpos().subtract(at); c.proj().at(at).vel(new Vec3(d.x / 18, .3, d.z / 18)).gravity(.035f).box(.3f, .1f, .3f).color(0xf0e0b0).dmg(1).life(24).spawn(); }
                }, (p, t, m) -> p.r("armR", -2f * Math.abs(sin(t, .5f)), 0, 0)),
                atk("college_fund", 60, (c, t) -> {
                    // "paying for colleg": a vibrating tem explodes into gold coins
                    if (t == 30) { for (int i = 0; i < 10; i++) { var o = orb(c, c.eye(), new Vec3(c.rnd.nextGaussian() * .2, .35, c.rnd.nextGaussian() * .2), .25f, 0xf0c040, 2, 30, 0); o.grav = .04f; }
                        c.sound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, .8f); }
                }, (p, t, m) -> p.o("body", .4f * sin(t, 6) * env(t, 10, 30, 32), 0, 0)))
            .build());
        out.add(CharacterDef.of("rich_temmie", "Rich Temmie", Faction.UNDERGROUND).att(Attitude.FRIENDLY).hp(20).keep(4).speed(.25f).egg(0xffffff, 0xf0c040).size(.6f, 1.0f)
            .model(tem(0xfafafa, 0xf0c040).box("hat", "head", 0, 5, 0, 5, 3, 5, 0x222222).glow("monocle", "head", 2, .5f, -3.7f, 1.6f, 1.6f, .2f, 0xf0c040))
            .attacks(atk("make_it_rain", 60, (c, t) -> {
                if (t % 3 == 0 && t < 45) drop(c, near(c, 2), 5, .25f, 0xf0c040, 1, 10);
                if (t % 9 == 0) c.sound(SoundEvents.EXPERIENCE_ORB_PICKUP, .6f, 1.4f);
            }, (p, t, m) -> p.r("armL", -2.6f, 0, .4f * sin(t, .8f)).r("armR", -2.6f, 0, -.4f * sin(t, .8f))))
            .build());
        out.add(CharacterDef.of("temmie_egg", "Temmie Egg", Faction.UNDERGROUND).att(Attitude.FRIENDLY).hp(10).keep(3).speed(0).egg(0xffffff, 0xffe0a0).size(.5f, .6f)
            .model(new ModelDef().box("body", null, 0, 4, 0, 6, 8, 6, 0xfaf6ea).box("head", "body", 0, 5, 0, 4, 2, 4, 0xfaf6ea).box("crack", "body", 0, 1, -3.1f, 4, .5f, .2f, 0x9a9a9a))
            .attacks(atk("hatch_wobble", 40, (c, t) -> {
                // "a temmie hatching" — hoi! a burst of shell bits
                if (t == 30) { burst(c, c.pos().add(0, .4, 0), 8, 0, .2, .2f, 0xfaf6ea, 1, 14); c.sound(SoundEvents.TURTLE_EGG_CRACK, 1, 1.4f); }
            }, (p, t, m) -> p.r("body", 0, 0, .3f * sin(t, 1.5f) * env(t, 5, 28, 32))))
            .build());
    }

    // ============================== ANNOYING DOG ==============================
    static void dog(List<CharacterDef> out) {
        out.add(CharacterDef.of("annoying_dog_misc", "Annoying Dog", Faction.NEUTRAL).att(Attitude.NEUTRAL).hp(999).keep(3).speed(.35f).rest(40).egg(0xffffff, 0x222222).size(.5f, .5f)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 5, 4, 8, 0xffffff)
                .box("head", "body", 0, 3, -4.5f, 5, 4, 4, 0xffffff).box("snout", "head", 0, -.8f, -2.4f, 3, 2, 2, 0xffffff).box("nose", "snout", 0, .5f, -1.1f, 1, .8f, .3f, 0x111111)
                .box("earL", "head", -2.5f, .5f, 0, 1, 3, 2, 0xeeeeee).box("earR", "head", 2.5f, .5f, 0, 1, 3, 2, 0xeeeeee)
                .glow("eyeL", "head", -1.2f, .8f, -2.1f, .6f, .6f, .2f, 0x000000).glow("eyeR", "head", 1.2f, .8f, -2.1f, .6f, .6f, .2f, 0x000000)
                .box("tail", "body", 0, 1.5f, 4.5f, 1, 1, 3, 0xffffff)
                .limb("armL", null, -1.5f, 2, -3, 1.5f, 2, 1.5f, 0xffffff).limb("armR", null, 1.5f, 2, -3, 1.5f, 2, 1.5f, 0xffffff)
                .limb("legL", null, -1.5f, 2, 3, 1.5f, 2, 1.5f, 0xffffff).limb("legR", null, 1.5f, 2, 3, 1.5f, 2, 1.5f, 0xffffff))
            .attacks(
                atk("absorb_the_bone", 60, (c, t) -> {
                    // the dog steals your bone (attack) and runs off with it
                    if (t == 10) { c.dash(c.aim(c.pos()).scale(.9).add(0, .3, 0)); c.sound(SoundEvents.WOLF_AMBIENT, 1, 1.6f); }
                    if (t == 16) { c.hit(c.target, 1); c.target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0)); }
                    if (t == 18) c.dash(c.aim(c.pos()).scale(-1).add(0, .2, 0));
                }, (p, t, m) -> p.r("tail", 0, .8f * sin(t, 2), 0).r("head", .2f * sin(t, 1), 0, 0)),
                atk("dog_ambush", 70, (c, t) -> {
                    // a whole parade of tiny dogs bounding across
                    if (t % 7 == 0 && t < 56) { Vec3 r = c.right(); int s = c.rnd.nextBoolean() ? 1 : -1; Vec3 at = c.tpos().add(r.scale(s * 7)).add(0, -.4, 0);
                        c.proj().at(at).vel(r.scale(-s * .45).add(0, .25, 0)).gravity(.03f).box(.4f, .3f, .6f).color(0xffffff).dmg(2).warn(4).life(34).spawn(); c.sound(SoundEvents.WOLF_AMBIENT, .5f, 1.8f); }
                }, (p, t, m) -> p.r("tail", 0, 1f * sin(t, 2.5f), 0)),
                atk("toby_piano", 60, (c, t) -> {
                    // the dog sits on the piano: sound-wave notes ring outward
                    if (t % 10 == 0 && t < 50) { burst(c, c.pos().add(0, .4, 0), 10, t, .25, .25f, 0xffffff, 2, 26); c.particle(ParticleTypes.NOTE, c.eye().add(0, .4, 0), 3, .4, 0); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .5f + (t / 10) * .2f); }
                }, (p, t, m) -> p.o("body", 0, .6f * Math.abs(sin(t, .3f)), 0)))
            .build());
    }

    // ============================== SECRET / JOKE ENEMIES ==============================
    static void secrets(List<CharacterDef> out) {
        int pink = 0xff8fc8, cotton = 0xf2e4d8;
        // Mad Mew Mew: Mad Dummy possessing the Mew Mew Kissie Cutie doll (Switch secret boss).
        out.add(CharacterDef.of("mad_mew_mew_misc", "Mad Mew Mew", Faction.UNDERGROUND).hp(260).boss().keep(6).rest(14).speed(.3f).egg(0xff8fc8, 0xffffff).size(.8f, 1.6f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 7, 8, 5, pink).box("skirt", "body", 0, -4.5f, 0, 9, 2, 7, 0xffffff)
                .box("head", "body", 0, 8, 0, 9, 8, 7, cotton)
                .box("earL", "head", -3.5f, 5, 0, 2.5f, 3, 2, cotton).box("earR", "head", 3.5f, 5, 0, 2.5f, 3, 2, cotton)
                .box("innerL", "earL", 0, 0, -1.1f, 1.2f, 2, .2f, pink).box("innerR", "earR", 0, 0, -1.1f, 1.2f, 2, .2f, pink)
                .glow("eyeL", "head", -2, .5f, -3.6f, 1.6f, 1.6f, .2f, 0x000000).glow("eyeR", "head", 2, .5f, -3.6f, 1.6f, 1.6f, .2f, 0x000000)
                .box("browL", "head", -2, 2, -3.6f, 2, .5f, .2f, 0x5a2a2a).box("browR", "head", 2, 2, -3.6f, 2, .5f, .2f, 0x5a2a2a)
                .box("bow", "head", 0, 4.3f, -1, 4, 1.5f, 1, 0xff4f9a)
                .limb("armL", "body", -4.5f, 3, 0, 2, 5, 2, cotton).limb("armR", "body", 4.5f, 3, 0, 2, 5, 2, cotton)
                .limb("legL", null, -1.8f, 6, 0, 2, 6, 2, cotton).limb("legR", null, 1.8f, 6, 0, 2, 6, 2, cotton))
            .attacks(
                atk("cotton_barrage", 70, (c, t) -> {
                    // she flies in circles spraying cotton bullets
                    if (t % 3 == 0 && t < 60) { Vec3 at = c.eye(); orb(c, at, c.aim(at).add(c.rnd.nextGaussian() * .12, 0, c.rnd.nextGaussian() * .12).normalize().scale(.45), .25f, cotton, 3, 30, 0); }
                    if (t % 10 == 0 && t < 60) c.dash(c.right().scale(.5 * (t % 20 == 0 ? 1 : -1)).add(0, .15, 0));
                }, (p, t, m) -> p.r("armL", -1.5f, 0, .5f * sin(t, 1)).r("armR", -1.5f, 0, -.5f * sin(t, 1)).o("body", 0, 2 * sin(t, .3f), 0)),
                atk("kissie_hearts", 70, (c, t) -> {
                    // pink hearts that bounce off the floor
                    if (t % 8 == 0 && t < 56) { Vec3 at = c.eye().add(0, 1, 0); Vec3 d = c.tpos().subtract(at); c.proj().at(at).vel(new Vec3(d.x / 22, .35, d.z / 22)).gravity(.03f).box(.5f, .45f, .2f).color(0xff4f9a).dmg(4).life(30).spawn(); c.particle(ParticleTypes.HEART, at, 1, 0, 0); }
                }, (p, t, m) -> p.r("head", 0, 0, .3f * sin(t, .5f))),
                atk("dummy_knives", 64, (c, t) -> {
                    // knives fly in from both sides in staggered rows
                    if (t % 10 == 4 && t < 54) { Vec3 r = c.right(); for (int s = -1; s <= 1; s += 2) for (int k = -1; k <= 1; k++) c.proj().at(c.tpos().add(r.scale(s * 7)).add(c.fwd().scale(k * 1.4)).add(0, (t / 10) % 2 * .6 - .3, 0))
                        .vel(r.scale(-s * .5)).box(.12f, .25f, .9f).color(0xd0d0d0).dmg(4).warn(8).life(30).spawn(); c.sound(SoundEvents.TRIDENT_THROW, .6f, 1.6f); }
                }, (p, t, m) -> p.r("armR", -2.4f * env(t % 10, 3, 4, 10), 0, -.3f)),
                atk("doll_rampage", 80, below(.5f), (c, t) -> {
                    // furious: she rips the stuffing out and it rains, then dashes through
                    if (t % 2 == 0 && t < 50) drop(c, near(c, 3), 7, .35f, cotton, 4, 14);
                    if (t == 56 || t == 66) { c.dash(c.aim(c.pos()).scale(1.3)); c.sound(SoundEvents.CAT_HISS, 1, .7f); }
                    if (t == 60 || t == 70) c.aoe(c.pos(), 1.8, 7, 1);
                }, (p, t, m) -> { float s = env(t, 8, 50, 56); p.r("armL", -3f * s, 0, .6f * sin(t, 2)).r("armR", -3f * s, 0, -.6f * sin(t, 2)).r("browL", 0, 0, -.4f).r("browR", 0, 0, .4f); }),
                atk("possession_end", 70, below(.2f), (c, t) -> {
                    // the ghost tries to escape the doll: a final ring of everything at once
                    if (t % 12 == 0 && t < 60) { burst(c, c.eye(), 16, t * .2, .35, .3f, t % 24 == 0 ? pink : cotton, 5, 32); c.sound(SoundEvents.GHAST_SCREAM, .7f, 1.6f); }
                }, (p, t, m) -> p.o("head", .5f * sin(t, 5), 0, 0).r("body", 0, t * .3f, 0)))
            .build());

        // Glyde: secret Snowdin enemy; a smug winged beast. Swoops and feather-darts.
        out.add(CharacterDef.of("glyde_misc", "Glyde", Faction.UNDERGROUND).hp(220).keep(6).speed(.35f).egg(0xd0d8f8, 0xe060a0).size(1.4f, 1.4f)
            .model(new ModelDef()
                .box("body", null, 0, 12, 0, 10, 6, 6, 0xd0d8f8)
                .box("head", "body", 0, 4, -2, 6, 5, 5, 0xd0d8f8).box("smirk", "head", 1, -1.2f, -2.6f, 3, .5f, .2f, 0x303060)
                .glow("eyeL", "head", -1.5f, .8f, -2.6f, 1, 1, .2f, 0x000000).glow("eyeR", "head", 1.5f, .8f, -2.6f, 1, 1, .2f, 0x000000)
                .box("wingL", "body", -11, 1, 0, 12, 2, 6, 0xe060a0).box("wingR", "body", 11, 1, 0, 12, 2, 6, 0xe060a0)
                .box("tail", "body", 0, -1, 5, 4, 2, 5, 0xd0d8f8))
            .attacks(
                atk("swoop", 60, (c, t) -> {
                    if (t == 6) beam(c, c.pos().add(0, .5, 0), c.aim(c.pos()), .8f, 10, 0xe060a0, 0, 12, 1);
                    if (t == 18) { c.dash(c.aim(c.pos()).scale(1.6)); c.sound(SoundEvents.PHANTOM_SWOOP, 1, 1.2f); }
                    if (t >= 20 && t < 28) c.aoe(c.pos(), 1.6, 6, 1);
                }, (p, t, m) -> { float s = env(t, 16, 28, 40); p.r("wingL", 0, 0, -.6f * s).r("wingR", 0, 0, .6f * s).r("body", .4f * s, 0, 0); }),
                atk("glyde_darts", 64, (c, t) -> {
                    if (t % 6 == 0 && t < 54) { Vec3 at = c.eye().add(c.right().scale((t / 6) % 2 == 0 ? 1.2 : -1.2)); c.proj().at(at).vel(c.aim(at).scale(.55)).box(.15f, .15f, .8f).color(0xe060a0).dmg(4).life(28).spawn(); }
                }, (p, t, m) -> p.r("wingL", 0, 0, .5f * sin(t, 1.5f)).r("wingR", 0, 0, -.5f * sin(t, 1.5f))),
                atk("vanity_ring", 60, (c, t) -> {
                    if (t == 8 || t == 30) for (int i = 0; i < 10; i++) { Vec3 at = c.tpos().add(circ(i * .628, 4.5, 0)); orb(c, at, c.aim(at).scale(.2), .35f, 0xd0d8f8, 4, 36, 12); }
                }, (p, t, m) -> p.r("head", -.3f, .3f * sin(t, .4f), 0)))
            .build());

        // So Sorry: the apologetic artist secret enemy (Hotland, 10/10 at 8pm). Doodles that attack.
        out.add(CharacterDef.of("so_sorry_misc", "So Sorry", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(160).keep(6).speed(.28f).egg(0x7a3a8a, 0xffe060).size(.8f, 1.8f)
            .model(new ModelDef()
                .box("body", null, 0, 11, 0, 8, 10, 6, 0x7a3a8a)
                .box("head", "body", 0, 8, -1, 7, 6, 6, 0x8a4a9a).box("mouth", "head", 0, -1.5f, -3.1f, 3, 1, .2f, 0x2a0a2a)
                .glow("eyeL", "head", -1.5f, .8f, -3.1f, 1.2f, 1.2f, .2f, 0xffffff).glow("eyeR", "head", 1.5f, .8f, -3.1f, 1.2f, 1.2f, .2f, 0xffffff)
                .box("hornL", "head", -2.5f, 4, 0, 1, 3, 1, 0x5a2a6a).box("hornR", "head", 2.5f, 4, 0, 1, 3, 1, 0x5a2a6a)
                .box("tail", "body", 0, -3, 4, 2, 2, 6, 0x7a3a8a)
                .limb("armL", "body", -5, 4, 0, 2, 7, 2, 0x7a3a8a).limb("armR", "body", 5, 4, 0, 2, 7, 2, 0x7a3a8a)
                .box("pencil", "armR", 0, -7.5f, -1, .6f, .6f, 4, 0xffe060)
                .limb("legL", null, -2, 6, 0, 2.5f, 6, 2.5f, 0x6a2a7a).limb("legR", null, 2, 6, 0, 2.5f, 6, 2.5f, 0x6a2a7a))
            .attacks(
                atk("doodle_scribble", 64, (c, t) -> {
                    // scribbled lines streak across where the pencil points (sorry!!)
                    if (t % 8 == 4 && t < 56) { Vec3 at = c.tpos().add(circ(c.rnd.nextDouble() * 6.28, 5, .2)); beam(c, at, c.tpos().subtract(at), .2f, 10, 0x202020, 4, 8, 4); }
                }, (p, t, m) -> p.r("armR", -1.4f + .4f * sin(t, 1.8f), .4f * sin(t, 1.1f), 0)),
                atk("art_critique", 60, (c, t) -> {
                    // drawings come to life and hop toward you
                    if (t % 10 == 0 && t < 50) { Vec3 at = c.pos().add(c.fwd().scale(1.5)).add(0, .4, 0); Vec3 d = c.tpos().subtract(at);
                        c.proj().at(at).vel(new Vec3(d.x / 20, .4, d.z / 20)).gravity(.04f).box(.6f, .6f, .1f).color(0xffe060).dmg(4).life(26).spawn(); }
                }, (p, t, m) -> p.r("head", .3f * env(t, 4, 50, 60), 0, 0)),
                atk("apology_flurry", 50, (c, t) -> {
                    if (t % 5 == 0 && t < 40) for (int k = -1; k <= 1; k++) orb(c, c.eye(), c.aim(c.eye()).add(c.right().scale(k * .3)).normalize().scale(.3), .25f, 0xff8080, 2, 30, 0);
                }, (p, t, m) -> p.r("armL", -2.2f, 0, .4f).r("armR", -2.2f, 0, -.4f).o("head", 0, -.6f, 0)))
            .build());
    }

    // ============================== W.D. GASTER ==============================
    static void gaster(List<CharacterDef> out) {
        int black = 0x101010, white = 0xf0f0f0;
        out.add(CharacterDef.of("wd_gaster", "W. D. Gaster", Faction.ROYAL_SCIENCE).att(Attitude.NEUTRAL).hp(300).boss().keep(8).rest(16).speed(.15f).egg(0x101010, 0xf0f0f0).size(.8f, 2.4f).scale(1.2f)
            .model(new ModelDef()
                .box("body", null, 0, 14, 0, 9, 18, 6, black)
                .box("head", "body", 0, 13, 0, 8, 9, 7, white)
                .box("crackU", "head", -1.5f, 2.5f, -3.6f, .6f, 4, .2f, black).box("crackD", "head", 1.5f, -2.5f, -3.6f, .6f, 3, .2f, black)
                .glow("eyeL", "head", -2, .5f, -3.6f, 2, 2, .2f, black).glow("eyeR", "head", 2, .5f, -3.6f, 2, 2, .2f, black)
                .glow("pupil", "eyeL", 0, 0, -.2f, .6f, .6f, .1f, white)
                .box("smile", "head", 0, -3, -3.6f, 4, .6f, .2f, black)
                .limb("armL", "body", -6, 7, 0, 2.5f, 14, 2.5f, black).limb("armR", "body", 6, 7, 0, 2.5f, 14, 2.5f, black)
                .box("handL", "armL", 0, -15, 0, 4, 4, 1, white).box("holeL", "handL", 0, 0, -.6f, 1.4f, 1.4f, .2f, black)
                .box("handR", "armR", 0, -15, 0, 4, 4, 1, white).box("holeR", "handR", 0, 0, -.6f, 1.4f, 1.4f, .2f, black)
                .ghost("drip", "body", 0, -11, 0, 9, 4, 6, black, .6f))
            .attacks(
                atk("hand_blasters", 90, (c, t) -> {
                    // floating hands with holes in the palms fire through their centers (the original blasters)
                    if (t < 36 && t % 6 == 0) { Vec3 at = c.tpos().add(circ(t * .9, 6, 2 + c.rnd.nextDouble()));
                        c.proj().at(at).face(c.tpos().subtract(at)).box(1.2f, 1.2f, .3f).color(white).dmg(0).life(44 - t + 14).spawn();
                        beam(c, at, c.tpos().subtract(at), .8f, 18, white, 7, 40 - t, 12); c.sound(SoundEvents.WARDEN_SONIC_CHARGE, .8f, 1.9f); }
                }, (p, t, m) -> { float s = env(t, 8, 70, 90); p.r("armL", -2.4f * s, 0, .6f).r("armR", -2.4f * s, 0, -.6f).o("handL", 0, sin(t, .4f), 0).o("handR", 0, -sin(t, .4f), 0); }),
                atk("dark_darker", 80, (c, t) -> {
                    // "dark, darker, yet darker": the void closes in; ink columns and blindness
                    if (t == 6) c.target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0));
                    if (t >= 10 && t < 64 && t % 5 == 0) pillar(c, near(c, 3), 1f, 6, black, 6, 10, 6);
                    if (t % 4 == 0) c.particle(ParticleTypes.SQUID_INK, c.tpos(), 8, 2, .02);
                }, (p, t, m) -> p.o("drip", 0, -2 * env(t, 10, 60, 80), 0).r("head", .2f, .2f * sin(t, .2f), 0)),
                atk("entry_seventeen", 80, (c, t) -> {
                    // wingdings glyph shards orbit and fire one by one ("ENTRY NUMBER SEVENTEEN")
                    if (t >= 6 && t < 70 && t % 4 == 0) { int i = t / 4; Vec3 at = c.eye().add(circ(i * .785, 2, Math.sin(i) * .5));
                        c.proj().at(at).vel(c.aim(at).scale(.45)).box(.35f, .5f, .1f).color(white).dmg(5).warn(6).life(34).spawn(); }
                    if (t % 16 == 0) c.sound(SoundEvents.ENCHANTMENT_TABLE_USE, 1, .5f);
                }, (p, t, m) -> p.r("armR", -1.6f, 0, -.3f).o("handR", .6f * sin(t, .8f), .6f * sin(t, 1.1f), 0)),
                atk("shattered_core", 80, below(.5f), (c, t) -> {
                    // the CORE accident: he splits apart; fragments scatter in every direction
                    if (t % 6 == 0 && t < 60) { burst(c, c.eye(), 10, t * .37, .35, .3f, t % 12 == 0 ? white : 0x606060, 5, 30); c.particle(ParticleTypes.REVERSE_PORTAL, c.eye(), 20, .6, .1); }
                    if (t == 40) { c.tp(c.target.position().add(circ(c.rnd.nextDouble() * 6.28, 6, 0))); c.sound(SoundEvents.ENDERMAN_TELEPORT, 1, .5f); }
                }, (p, t, m) -> { float s = env(t, 10, 60, 80); p.o("head", sin(t, 7) * s, 2 * s, 0).o("armL", -2 * s, 0, 0).o("armR", 2 * s, 0, 0); }))
            .build());

        // Gaster Followers: the three grey "mystery men" who hint about Gaster. Peaceful; they glitch away if pushed.
        int grey = 0x7a7a7a;
        for (int n = 1; n <= 3; n++) {
            final int k = n;
            out.add(CharacterDef.of("gaster_follower_" + n, "Gaster Follower " + n, Faction.ROYAL_SCIENCE).att(Attitude.NEUTRAL).hp(40).keep(6).speed(.2f).rest(40).egg(0x7a7a7a, 0x303030).size(.7f, 1.9f)
                .model(new ModelDef()
                    .box("body", null, 0, 12, 0, 8, 12, 5, grey)
                    .box("head", "body", 0, 9, 0, k == 2 ? 9 : 7, k == 3 ? 5 : 7, 6, 0x8a8a8a)
                    .glow("eyeL", "head", -1.5f, .5f, -3.1f, .8f, k == 1 ? 2 : 1, .2f, 0x000000).glow("eyeR", "head", 1.5f, .5f, -3.1f, .8f, k == 1 ? 2 : 1, .2f, 0x000000)
                    .limb("armL", "body", -5, 5, 0, 2, 8, 2, grey).limb("armR", "body", 5, 5, 0, 2, 8, 2, grey)
                    .limb("legL", null, -2, 6, 0, 2.5f, 6, 2.5f, 0x5a5a5a).limb("legR", null, 2, 6, 0, 2.5f, 6, 2.5f, 0x5a5a5a))
                .attacks(
                    atk("static_glitch", 50, (c, t) -> {
                        // grey static flickers at you, then they blink elsewhere
                        if (t % 5 == 0 && t < 30) orb(c, c.eye(), c.aim(c.eye()).add(c.rnd.nextGaussian() * .15, 0, c.rnd.nextGaussian() * .15).normalize().scale(.4), .25f, 0x9a9a9a, 2, 26, 0);
                        if (t == 40) { c.tp(c.pos().add(circ(c.rnd.nextDouble() * 6.28, 4, 0))); c.particle(ParticleTypes.SMOKE, c.eye(), 20, .4, .02); }
                    }, (p, t, m) -> p.o("body", .5f * sin(t, 9) * (t % 7 < 2 ? 1 : 0), 0, 0)),
                    atk("dark_whisper", 50, (c, t) -> {
                        if (t == 10) { c.target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0)); c.sound(SoundEvents.SCULK_SHRIEKER_SHRIEK, .4f, .5f); }
                        if (t == 20) burst(c, c.eye(), 8, 0, .25, .2f, 0x303030, 2, 24);
                    }, (p, t, m) -> p.r("head", .3f * env(t, 6, 30, 50), 0, 0)))
                .build());
        }
    }

    // ============================== NAPSTABLOOK ==============================
    static void blook(List<CharacterDef> out) {
        out.add(CharacterDef.of("napstablook_misc", "Napstablook", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(88).keep(6).speed(.15f).egg(0xf0f0f0, 0x60a0ff).size(.8f, 1.6f)
            .model(new ModelDef()
                .ghost("body", null, 0, 12, 0, 10, 16, 8, 0xf0f0f0, .8f)
                .ghost("head", "body", 0, 5, -.5f, 9, 5, 7, 0xf4f4f4, .8f)
                .glow("eyeL", "head", -2, 0, -3.6f, 2, 1, .2f, 0x101010).glow("eyeR", "head", 2, 0, -3.6f, 2, 1, .2f, 0x101010)
                .box("mouth", "head", 0, -2.2f, -3.6f, 2, .5f, .2f, 0x101010)
                .glow("tearL", "head", -2, -1.5f, -3.7f, .6f, 1.5f, .2f, 0x60a0ff).glow("tearR", "head", 2, -1.5f, -3.7f, .6f, 1.5f, .2f, 0x60a0ff)
                .ghost("hem", "body", 0, -8, 0, 11, 2, 9, 0xe8e8e8, .6f))
            .attacks(
                atk("tear_rain", 64, (c, t) -> {
                    // tears fall around you in gentle sheets
                    if (t % 3 == 0 && t < 54) { Vec3 g = near(c, 2.5); c.proj().at(g.add(0, 5, 0)).vel(new Vec3(0, -.3, 0)).box(.15f, .35f, .15f).color(0x60a0ff).dmg(2).life(20).spawn(); }
                }, (p, t, m) -> p.o("tearL", 0, -2 * ((t * .1f) % 1), 0).o("tearR", 0, -2 * ((t * .1f + .5f) % 1), 0).r("head", .25f, 0, 0)),
                atk("tear_spiral", 64, (c, t) -> {
                    // the tears swirl outward from both eyes
                    if (t % 3 == 0 && t < 54) for (int s = -1; s <= 1; s += 2) orb(c, c.eye().add(c.right().scale(s * .25)), circ(t * .2 * s, .25, -.02), .18f, 0x60a0ff, 2, 34, 0);
                }, (p, t, m) -> p.o("body", 0, 1.5f * sin(t, .15f), 0)),
                atk("dapper_blook", 70, (c, t) -> {
                    // "let me try...": the tears rise up and gather into a dapper top hat... which then falls on you
                    if (t >= 6 && t < 40 && t % 2 == 0) orb(c, c.eye().add(c.rnd.nextGaussian() * .3, -.2, c.rnd.nextGaussian() * .3), new Vec3(0, .12, 0), .15f, 0x60a0ff, 0, 14, 0);
                    if (t == 40) { Vec3 g = ground(c); c.proj().at(g.add(0, 4, 0)).vel(new Vec3(0, -4 / 14.0, 0)).box(1.2f, 1.2f, 1.2f).color(0x60a0ff).dmg(5).warn(6).life(16).spawn();
                        c.proj().at(g.add(0, .05, 0)).box(1.6f, .05f, 1.6f).color(0x60a0ff).dmg(0).warn(14).life(14).spawn(); }
                    if (t == 54) c.particle(ParticleTypes.SPLASH, ground(c), 30, .6, .2);
                }, (p, t, m) -> p.o("body", 0, 2 * env(t, 6, 40, 54), 0).r("head", -.2f * env(t, 6, 40, 54), 0, 0)))
            .build());
    }
}
