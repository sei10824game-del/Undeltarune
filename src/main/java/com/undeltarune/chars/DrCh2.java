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

/** Deltarune Chapter 2: Cyber World. Canon allegiance: Queen and her residents (Berdly while under her, Werewires on her
 *  wires, mansion staff, field enemies, Sweet Cap'n Cakes at first) are CYBER_CITY and hostile to the heroes; Spamton is
 *  his own SPAMTON_NET (alley salesman is only provoked, NEO is hostile); Noelle is a hero; Addisons are merchants.
 *  Kris/Susie/Ralsei and Rouxls Kaard are defined in DrCh1. */
public final class DrCh2 {
    public static void register(List<CharacterDef> out) {
        noelle(out); berdly(out); queen(out); gigaQueen(out); spamton(out); spamtonNeo(out);
        mansion(out); fieldEnemies(out); sweetCapnCakes(out); addison(out);
    }

    static final int ACID = 0x8aff3a, CYAN = 0x46e0ff, PINK = 0xff6ac1, YELLOW = 0xffe23a;

    // =====================================================================================================
    // NOELLE (Lightner hero)
    // =====================================================================================================
    static void noelle(List<CharacterDef> out) {
        ModelDef m = biped(0xf2dccb, 0xa82a3a, 0xa82a3a, 0x6a1a26, 6, 7)
            .box("hair", "head", 0, 1.6f, .8f, 7.6f, 5, 7.4f, 0xf5d58a)
            .box("antlerL", "head", -2.4f, 5, 0, .8f, 3.4f, .8f, 0x8a5a3a).box("antlerL2", "head", -3.6f, 6, 0, 2, .8f, .8f, 0x8a5a3a)
            .box("antlerR", "head", 2.4f, 5, 0, .8f, 3.4f, .8f, 0x8a5a3a).box("antlerR2", "head", 3.6f, 6, 0, 2, .8f, .8f, 0x8a5a3a)
            .glow("eyes", "head", 0, .2f, -3.6f, 4, .8f, .3f, 0x2a6a3a)
            .box("trim", "body", 0, -3.4f, 0, 7, 1.2f, 5, 0xf8f8f8)
            .box("skirt", "body", 0, -4.6f, 0, 7.6f, 2.4f, 5.4f, 0xa82a3a)
            .box("ring", "armR", 0, -6.6f, -.6f, 1.6f, .4f, 1.6f, 0xd8e8ff);
        out.add(CharacterDef.of("noelle", "Noelle", Faction.HEROES).att(Attitude.FRIENDLY).hp(85).keep(6).rest(30).scale(1.1f).egg(0xa82a3a, 0xf5d58a)
            .model(m).attacks(noelleIceShock(), noelleHealPrayer(), noelleSnowGrave()).build());
    }
    static Attack noelleIceShock() {
        return atk("noelle_ice_shock", 50, (c, t) -> {
            if (t == 8) put(c, "ni", c.tpos());
            if (t >= 8 && t < 24 && t % 4 == 0) { Vec3 at = get(c, "ni").add(rotY(new Vec3(1.1, 0, 0), t * .8)).add(0, .6, 0);
                c.proj().at(at).box(.4f, .9f, .4f).color(0xbff0ff).dmg(0).life(28 - t + 6).spawn(); c.sound(SoundEvents.POWDER_SNOW_BREAK, 1, 1.5f); }
            if (t == 30) { Vec3 at = get(c, "ni"); c.aoe(at, 1.8, 7, .3); c.particle(ParticleTypes.SNOWFLAKE, at, 30, .8, .05);
                c.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1)); c.sound(SoundEvents.GLASS_BREAK, 1, 1.4f); }
        }, (p, t, m) -> { float s = env(t, 0, 8, 30, 42); p.r("armR", -2.6f * s, 0, -.3f).r("armL", -1f * s, 0, .3f).r("head", -.2f * s, 0, 0); });
    }
    static Attack noelleHealPrayer() {
        return atk("noelle_heal_prayer", 50, (c, t) -> {
            if (t < 24 && t % 4 == 0) c.particle(ParticleTypes.HAPPY_VILLAGER, c.pos().add(0, 1, 0), 5, .7, .05);
            if (t == 24) { for (LivingEntity e : c.level.getEntitiesOfClass(LivingEntity.class, new AABB(c.pos(), c.pos()).inflate(6), e -> !c.me.isFoe(e))) {
                e.heal(7); c.particle(ParticleTypes.HEART, e.position().add(0, e.getBbHeight() + .3, 0), 3, .3, 0); }
                c.sound(SoundEvents.PLAYER_LEVELUP, .6f, 1.6f); }
        }, (p, t, m) -> { float s = env(t, 0, 12, 30, 44); p.r("armL", -1f * s, 0, .5f * s).r("armR", -1f * s, 0, -.5f * s).r("head", .35f * s, 0, 0); });
    }
    /** SnowGrave (Weird Route spell): a storm column of snowflakes then a crushing pillar of ice. Last resort only. */
    static Attack noelleSnowGrave() {
        return atk("noelle_snowgrave", 110, (c, t) -> {
            if (t == 2) { put(c, "ng", c.target.position()); c.sound(SoundEvents.ELDER_GUARDIAN_CURSE, .8f, 1.5f); }
            Vec3 o = get(c, "ng");
            if (t < 60 && t % 2 == 0) for (int i = 0; i < 3; i++) { double a = t * .3 + i * 2.09, r = 3.5 - t * .04;
                Vec3 at = o.add(Math.cos(a) * r, 4 - (t % 20) * .2, Math.sin(a) * r);
                c.proj().at(at).vel(new Vec3(-Math.sin(a), -.1, Math.cos(a)).scale(.3)).box(.3f, .3f, .3f).color(0xe8f8ff).dmg(2).life(12).spawn(); }
            if (t == 60) c.proj().at(o.add(0, .05, 0)).box(3, .1f, 3).color(0x8fd8ff).dmg(0).warn(20).life(22).spawn();
            if (t == 82) { beam(c, o.add(0, -.5, 0), new Vec3(0, 1, 0), 10, 3, 0xbff0ff, 0, 14, 14); c.particle(ParticleTypes.SNOWFLAKE, o.add(0, 2, 0), 80, 1.5, .1);
                c.sound(SoundEvents.GLASS_BREAK, 1.4f, .5f); }
        }, (p, t, m) -> { float s = env(t, 0, 20, 90, 106); p.r("armR", -3f * s, 0, 0).r("armL", -3f * s, 0, 0).o("body", 0, 1.5f * s, 0).r("head", -.4f * s, 0, 0); }, below(.3f));
    }

    // =====================================================================================================
    // BERDLY (rival, later Queen's puppet: CYBER_CITY, hostile miniboss)
    // =====================================================================================================
    static void berdly(List<CharacterDef> out) {
        ModelDef m = biped(0x3a7ad9, 0x2fa35a, 0x3a7ad9, 0x24284a, 5.6f, 6.6f)
            .box("tuft", "head", 0, 4.2f, .6f, 3, 2.6f, 4, 0x2a5ab0).box("tuft2", "head", 0, 5.4f, 2, 2, 2, 2, 0x2a5ab0)
            .box("beak", "head", 0, -.6f, -4, 2.4f, 1.6f, 2.4f, 0xffc23a)
            .glow("glasses", "head", 0, 1, -3.5f, 5.6f, 1.6f, .3f, 0xe8f8ff)
            .box("bridge", "head", 0, 1, -3.7f, 6.4f, .3f, .2f, 0x1a1a1a)
            .box("wingL", "armL", -.6f, -2, 1, .6f, 4.4f, 3, 0x3a7ad9).box("wingR", "armR", .6f, -2, 1, .6f, 4.4f, 3, 0x3a7ad9)
            .box("cape", "body", 0, -.5f, 2.4f, 6, 7, .5f, 0xd8a83a);
        out.add(CharacterDef.of("berdly", "Berdly", Faction.CYBER_CITY).hp(120).boss().keep(7).rest(20).scale(1.15f).egg(0x3a7ad9, 0x2fa35a)
            .model(m).attacks(berdlyTornado1(), berdlyChirashi(), berdlySpearBlast(), berdlyTornado2()).build());
    }
    static void tornado(AttackCtx c, Vec3 at, Vec3 vel, int warn, int life) {
        for (int y = 0; y < 4; y++) // a funnel: stacked boxes widening upward
            c.proj().at(at.add(0, y * .45 - .7, 0)).vel(vel).box(.35f + y * .2f, .4f, .35f + y * .2f).color(y % 2 == 0 ? 0xe0f0ff : 0xb0d8ff).dmg(y == 1 ? 4 : 0).warn(warn).life(life).spawn();
    }
    /** Walls of four tornadoes blown sideways in one of the four canon orders. */
    static Attack berdlyTornado1() {
        return atk("berdly_tornado_1", 80, (c, t) -> {
            if (t == 4) puti(c, "bt", c.rnd.nextInt(4));
            int pat = geti(c, "bt");
            if (t >= 10 && t < 58 && t % 6 == 4) { int step = (t - 10) / 6; Vec3 r = c.right(), f = c.fwd();
                int[][] orders = {{0}, {1}, {2}, {3}}; // one-by-one; 'pairs' variant below
                int lane = pat < 2 ? step % 4 : (step % 2) * 2; if (pat % 2 == 1) lane = 3 - lane;
                for (int l : pat < 2 ? new int[]{lane} : new int[]{lane, lane + 1 > 3 ? 0 : lane + 1 == 1 ? 2 : lane})
                    tornado(c, c.tpos().add(r.scale(-7)).add(f.scale((l - 1.5) * 1.3)), r.scale(.45), 6, 40);
                if (orders.length > 0) c.sound(SoundEvents.PHANTOM_FLAP, 1, 1.2f); }
        }, (p, t, m) -> { float s = t >= 10 && t < 58 ? wave(t, .5f) : 0; p.r("armL", 0, 0, -1.2f - .5f * s).r("armR", 0, 0, 1.2f + .5f * s).o("body", 0, 1.5f + s, 0); });
    }
    /** Second encounter: four tornadoes converge on the center. */
    static Attack berdlyTornado2() {
        return atk("berdly_tornado_2", 70, (c, t) -> {
            if (t == 8 || t == 32) { Vec3 o = c.tpos(); for (int i = 0; i < 4; i++) { Vec3 d = rotY(new Vec3(1, 0, 0), i * Math.PI / 2 + (t == 32 ? Math.PI / 4 : 0));
                tornado(c, o.add(d.scale(6)), d.scale(-.3), 8, 40); } c.sound(SoundEvents.PHANTOM_FLAP, 1.2f, .9f); }
        }, (p, t, m) -> p.r("armL", 0, 0, -2.4f).r("armR", 0, 0, 2.4f).r("body", 0, t * .3f, 0).o("body", 0, 2, 0), below(.6f));
    }
    /** "Chirashi": A+ test papers fluttering at the target (more and faster at low HP, as in the Weird Route). */
    static Attack berdlyChirashi() {
        return atk("berdly_chirashi", 66, (c, t) -> {
            int rate = c.me.hpFrac() < .4f ? 4 : 7;
            if (t >= 8 && t < 54 && t % rate == 0) { Vec3 at = c.eye().add(c.right().scale(c.rnd.nextGaussian()));
                Vec3 v = c.aim(at).scale(.42).add(0, Math.sin(t) * .05, 0);
                c.proj().at(at).vel(v).box(.6f, .75f, .05f).color(0xffffff).dmg(3).life(36).spawn();
                c.proj().at(at.add(0, .1, 0)).vel(v).box(.3f, .2f, .07f).color(0xd02a2a).dmg(0).life(36).spawn(); // the red "A+"
                c.sound(SoundEvents.BOOK_PAGE_TURN, .8f, 1.4f); }
        }, (p, t, m) -> { float s = t >= 8 && t < 54 ? (t % 7 < 3 ? 1 : 0) : 0; p.r("armR", -2f - .6f * s, 0, 0).r("head", 0, 0, .15f * wave(t, .4f)); });
    }
    /** Spear Blast: a curvy red-telegraphed trail that hangs, then each node launches toward its pointing direction. */
    static Attack berdlySpearBlast() {
        return atk("berdly_spear_blast", 80, (c, t) -> {
            int n = c.me.hpFrac() < .4f ? 16 : 8;
            if (t == 8) { put(c, "bs0", c.eye()); put(c, "bsd", c.aim(c.eye())); }
            Vec3 o = get(c, "bs0"), d = get(c, "bsd"), sd = side(flat(d));
            if (t >= 8 && t < 8 + n) { int i = t - 8; Vec3 at = o.add(d.scale(i * 6.0 / n)).add(sd.scale(Math.sin(i * 6.0 / n * 1.6) * 1.6));
                Vec3 dir = rotY(d, Math.cos(i * .8) * .6);
                c.proj().at(at).face(dir).box(.2f, .2f, .6f).color(0xff3030).dmg(0).warn(6).life(40 - i).spawn();     // trail spear, waits
                put(c, "bsn" + i, at); put(c, "bsv" + i, dir); }
            if (t >= 48 && t < 48 + n) { int i = t - 48; c.proj().at(get(c, "bsn" + i)).vel(get(c, "bsv" + i).scale(.6)).box(.2f, .2f, .6f).color(YELLOW).dmg(4).life(26).spawn();
                if (i % 2 == 0) c.sound(SoundEvents.ARROW_SHOOT, .8f, 1.3f); }
        }, (p, t, m) -> { float s = env(t, 0, 8, 24, 32), f = env(t, 44, 48, 56, 66); p.r("armR", -2.8f * s - 1.4f * f, 0, 0).r("body", 0, .4f * f, 0).r("cape", .5f * f, 0, 0); });
    }

    // =====================================================================================================
    // QUEEN (Ch2 antagonist boss: all 8 canon turns)
    // =====================================================================================================
    static void queen(List<CharacterDef> out) {
        ModelDef m = biped(0xe8e8f0, 0x2a2a3a, 0x2a2a3a, 0x2a2a3a, 5.6f, 7)
            .box("dress", "body", 0, -4, 0, 8, 4, 6, 0x2a2a3a)
            .box("hair", "head", 0, 3.4f, 1, 7.6f, 2, 7, 0x3a3a4a).box("hair_spike", "head", 0, 6, 1.6f, 2.6f, 4, 2.6f, 0x3a3a4a)
            .box("glasses", "head", 0, .6f, -3.6f, 6.6f, 1.8f, .4f, 0x1a1a1a)
            .glow("lensL", "head", -1.6f, .6f, -3.85f, 2.2f, 1.2f, .2f, CYAN).glow("lensR", "head", 1.6f, .6f, -3.85f, 2.2f, 1.2f, .2f, CYAN)
            .box("smile", "head", 0, -1.8f, -3.6f, 3, .5f, .3f, 0x1a1a1a)
            .box("collar", "body", 0, 3.6f, 0, 8, 1.4f, 5.4f, 0x48485e)
            .box("glass_stem", "armR", 0, -7, -1, .3f, 1.6f, .3f, 0xd8f0ff).box("glass_cup", "armR", 0, -5.6f, -1, 1.4f, 1.4f, 1.4f, ACID);
        out.add(CharacterDef.of("queen", "Queen", Faction.CYBER_CITY).hp(250).boss().keep(7).rest(18).scale(1.35f).size(.8f, 2.4f).egg(0x2a2a3a, CYAN)
            .model(m).attacks(queenStomp(), queenExplodingHeads(), queenLaser(), queenBerdlyTornado(), queenSocialMedia(), queenPlug(), queenWine(), queenUltimate()).build());
    }
    static Attack queenStomp() {
        return atk("queen_stomp", 70, (c, t) -> {
            if (t % 20 == 4 && t < 64) { Vec3 at = c.target.position().add(c.right().scale((c.rnd.nextInt(3) - 1) * 2.2)); put(c, "qs", at);
                c.proj().at(at.add(0, .05, 0)).box(2.2f, .1f, 3.2f).color(0xff4040).dmg(0).warn(12).life(14).spawn(); }
            if (t % 20 == 16 && t < 64) { Vec3 at = get(c, "qs");
                c.proj().at(at.add(0, 6, 0)).vel(new Vec3(0, -1.2, 0)).box(2, 1.4f, 3).color(0x2a2a3a).dmg(7).life(5).pierce().spawn();
                c.proj().at(at.add(0, 7.6, .9)).vel(new Vec3(0, -1.2, 0)).box(.5f, 2, .5f).color(0x48485e).dmg(0).life(5).spawn(); // heel
                c.particle(ParticleTypes.CLOUD, at, 10, .8, .05); c.sound(SoundEvents.ANVIL_LAND, 1, .6f); }
        }, (p, t, m) -> { float s = (t % 20 >= 10 && t % 20 < 17) ? 1 : 0; p.r("legR", -1.4f * s, 0, 0).r("armL", -1.2f, 0, .3f).r("body", .1f * s, 0, 0); });
    }
    static Attack queenExplodingHeads() {
        return atk("queen_exploding_heads", 80, (c, t) -> {
            if (t == 6) for (int i = 0; i < 4; i++) { Vec3 at = c.tpos().add(rotY(new Vec3(4, 1, 0), i * Math.PI / 2 + Math.PI / 4)); put(c, "qh" + i, at);
                c.proj().at(at).box(1.2f, 1.2f, 1.2f).color(0xe8e8f0).dmg(0).life(30).spawn();
                c.proj().at(at.add(0, .1, -.62)).box(1.1f, .35f, .05f).color(CYAN).dmg(0).life(30).spawn(); }
            if (t == 20) c.sound(SoundEvents.WITCH_CELEBRATE, 1, 1.3f); // laughing
            if (t == 36) for (int i = 0; i < 4; i++) { Vec3 at = get(c, "qh" + i); c.particle(ParticleTypes.EXPLOSION, at, 1, 0, 0);
                for (int k = 0; k < 6; k++) { Vec3 d = rotY(new Vec3(1, 0, 0), k * Math.PI / 3 + i);
                    c.proj().at(at).vel(d.scale(.3).add(c.aim(at).scale(.15))).box(.4f, .4f, .4f).color(0xffffff).dmg(3).life(36).spawn(); } }
            if (t == 36) c.sound(SoundEvents.GENERIC_EXPLODE, .7f, 1.4f);
        }, (p, t, m) -> { float s = env(t, 0, 6, 36, 44); p.r("armL", -2.6f * s, 0, .4f).r("armR", -2.6f * s, 0, -.4f).r("head", -.3f * s * Math.abs(wave(t, .8f)), 0, 0); });
    }
    /** Screens with green cursors aim at the target, then fire quick beams along that line. */
    static Attack queenLaser() {
        return atk("queen_laser", 76, (c, t) -> {
            if (t >= 6 && t < 60 && t % 9 == 6) { double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = c.tpos().add(Math.cos(a) * 6, 1.5, Math.sin(a) * 6);
                c.proj().at(at).face(c.aim(at)).box(1.4f, 1, .2f).color(0x1a1a2a).dmg(0).life(16).spawn();             // the screen
                c.proj().at(at.add(c.aim(at).scale(.15))).face(c.aim(at)).box(.4f, .4f, .1f).color(0x5cff8a).dmg(0).life(16).spawn(); // cursor
                beam(c, at, c.tpos().subtract(at), 14, .35f, 0x5cff8a, 10, 4, 5); c.sound(SoundEvents.BEACON_POWER_SELECT, .6f, 2); }
        }, (p, t, m) -> p.r("armR", -1.6f, .3f * wave(t, .4f), 0).r("head", 0, .2f * wave(t, .2f), 0));
    }
    /** Berdly descends and unleashes six tornadoes (three per side) crossing in the middle. */
    static Attack queenBerdlyTornado() {
        return atk("queen_berdly_tornado", 80, (c, t) -> {
            if (t == 6) { put(c, "qb", c.tpos()); c.proj().at(get(c, "qb").add(0, 6, 0)).vel(new Vec3(0, -.18, 0)).box(.8f, 1.6f, .8f).color(0x3a7ad9).dmg(0).life(24).spawn(); }
            for (int w = 0; w < 2; w++) if (t == 30 + w * 18) { Vec3 o = get(c, "qb"), r = c.right(), f = c.fwd();
                for (int k = 0; k < 3; k++) { tornado(c, o.add(r.scale(-7)).add(f.scale((k - 1) * 1.6 + w * .8)), r.scale(.4).add(f.scale((1 - k) * .08)), 6, 40);
                    tornado(c, o.add(r.scale(7)).add(f.scale((k - 1) * 1.6 + .8 - w * .8)), r.scale(-.4).add(f.scale((k - 1) * .08)), 6, 40); }
                c.sound(SoundEvents.PHANTOM_FLAP, 1.2f, 1); }
        }, (p, t, m) -> p.r("armR", -2.8f, 0, -.3f).r("armL", -.6f, 0, .4f).o("head", 0, .3f * wave(t, .6f), 0));
    }
    /** New Social Media: boxed heads scroll past; Queen's icon crashes in and cursing red grawlixes fly everywhere. */
    static Attack queenSocialMedia() {
        return atk("queen_new_social_media", 90, (c, t) -> {
            if (t == 4) put(c, "qm", c.tpos());
            Vec3 o = get(c, "qm"), r = c.right(), f = c.fwd();
            if (t >= 6 && t < 50 && t % 6 == 0) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = o.add(r.scale(s * 3)).add(f.scale(-7)).add(0, (t / 6) % 2 * .8, 0);
                c.proj().at(at).vel(f.scale(.4)).box(1, 1, .1f).color(0xf0f0f0).dmg(3).life(36).spawn();
                c.proj().at(at.add(0, 0, .02)).vel(f.scale(.4)).box(.6f, .6f, .12f).color((t / 6) % 3 == 0 ? PINK : CYAN).dmg(0).life(36).spawn(); }
            if (t == 52) { c.proj().at(o.add(0, 4, 0)).vel(new Vec3(0, -.3, 0)).box(1.4f, 1.4f, 1.4f).color(0xe8e8f0).dmg(5).life(12).spawn(); c.sound(SoundEvents.WITCH_CELEBRATE, 1, 1.6f); }
            if (t >= 64 && t < 86 && t % 2 == 0) { Vec3 d = rotY(new Vec3(1, 0, 0), c.rnd.nextDouble() * Math.PI * 2);
                c.proj().at(o.add(0, .4, 0)).vel(d.scale(.35 + c.rnd.nextDouble() * .2)).box(.35f, .35f, .1f).color(0xff2a2a).dmg(2).life(24).spawn();
                if (t % 6 == 0) c.sound(SoundEvents.VILLAGER_NO, .6f, 1.8f); }
        }, (p, t, m) -> { float s = env(t, 50, 56, 84, 90); p.r("armL", -2.8f * s, 0, .6f * wave(t, 1f) * s).r("armR", -2.8f * s, 0, -.6f * wave(t, 1f) * s).r("head", 0, 0, .3f * s * wave(t, .9f)); });
    }
    /** Plug: two plugs electrify the arena edges and alternately spit sparks while the box rotates. */
    static Attack queenPlug() {
        return atk("queen_plug", 90, (c, t) -> {
            if (t == 4) put(c, "qp", c.tpos());
            Vec3 o = get(c, "qp"); double rot = t * .03;
            Vec3 p1 = o.add(rotY(new Vec3(4, 0, 0), rot)), p2 = o.add(rotY(new Vec3(-4, 0, 0), rot));
            if (t >= 6 && t < 84 && t % 2 == 0) for (int i = 0; i < 4; i++) { Vec3 d = rotY(new Vec3(1, 0, 0), rot + i * Math.PI / 2), e = side(d);
                c.proj().at(o.add(d.scale(4.4))).face(e).box(.25f, .25f, 8.8f).color(0xffe23a).dmg(3).life(2).warn(t == 6 ? 10 : 0).spawn(); } // electrified border
            if (t >= 6 && t < 84 && t % 4 == 0) { c.proj().at(p1).box(.8f, .5f, .5f).color(0x48485e).dmg(0).life(4).spawn(); c.proj().at(p2).box(.8f, .5f, .5f).color(0x48485e).dmg(0).life(4).spawn(); }
            if (t >= 20 && t < 80 && t % 8 == 0) { Vec3 src = (t / 8) % 2 == 0 ? p1 : p2;
                fan(c, src, o.subtract(src), 3, .9, .35, YELLOW, 3, 24); c.particle(ParticleTypes.ELECTRIC_SPARK, src, 10, .3, .1); c.sound(SoundEvents.REDSTONE_TORCH_BURNOUT, .5f, 1.6f); }
        }, (p, t, m) -> p.r("armL", -1.6f, 0, .5f).r("armR", -1.6f, 0, -.5f).r("body", 0, .2f * wave(t, .1f), 0));
    }
    /** Wine: chalice-shaped arena; white drops rain and fill it, forcing the target to the top. */
    static Attack queenWine() {
        return atk("queen_wine", 100, (c, t) -> {
            if (t == 4) put(c, "qw", c.target.position());
            Vec3 o = get(c, "qw");
            if (t == 4) { for (int i = 0; i < 12; i++) { Vec3 d = rotY(new Vec3(3.2, 0, 0), i * Math.PI / 6);
                c.proj().at(o.add(d).add(0, 1.2, 0)).box(.2f, 2.6f, .2f).color(0xd8f0ff).dmg(0).life(92).spawn(); } // the glass
                c.sound(SoundEvents.GLASS_PLACE, 1, 1.2f); }
            if (t >= 10 && t < 80 && t % 2 == 0) { Vec3 at = o.add(c.rnd.nextGaussian() * 1.5, 5, c.rnd.nextGaussian() * 1.5);
                c.proj().at(at).vel(new Vec3(0, -.5, 0)).box(.2f, .4f, .2f).color(0xffffff).dmg(2).warn(2).life(12).spawn(); }
            if (t >= 20 && t < 92) { double level = (t - 20) / 72.0 * 2.2; // rising wine
                if (t % 4 == 0) c.proj().at(o.add(0, level - .4, 0)).box(6, .3f, 6).color(0xb02a5a).dmg(0).life(4).spawn();
                if (c.target.position().y < o.y + level - .5 && c.target.position().distanceTo(o) < 4) { c.hit(c.target, 1); c.target.setDeltaMovement(c.target.getDeltaMovement().x, .4, c.target.getDeltaMovement().z); c.target.hurtMarked = true; } }
        }, (p, t, m) -> { float s = env(t, 0, 10, 80, 96); p.r("armR", -2.2f * s, 0, -.4f).r("glass_cup", 0, 0, 2.2f * s).r("head", .2f * s, 0, 0); });
    }
    /** "DOWNLOADING: ULTIMATE ATTACK" fails at 60%: pop-ups and vector heads flood the area. */
    static Attack queenUltimate() {
        return atk("queen_ultimate_attack", 110, (c, t) -> {
            if (t == 4) put(c, "qu", c.tpos());
            Vec3 o = get(c, "qu"), r = c.right();
            if (t >= 6 && t < 50 && t % 4 == 0) { int k = (t - 6) / 4; // progress bar fills to 60%
                c.proj().at(o.add(0, 3, 0).add(r.scale(-3 + k * .33))).box(.3f, .4f, .2f).color(0x5cff8a).dmg(0).life(90 - t).spawn();
                if (k % 3 == 0) c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), .6f, .8f + k * .1f); }
            if (t == 50) c.sound(SoundEvents.ANVIL_LAND, .7f, 1.8f);
            if (t >= 56 && t < 100 && t % 3 == 0) { Vec3 at = o.add(c.rnd.nextGaussian() * 3, c.rnd.nextDouble() * 2, c.rnd.nextGaussian() * 3);
                boolean head = c.rnd.nextBoolean();
                c.proj().at(at).vel(c.aim(at).scale(.12)).box(head ? .8f : 1.4f, head ? .8f : 1, head ? .8f : .1f).color(head ? 0xe8e8f0 : 0xf0f0f0).dmg(3).warn(8).life(26).spawn();
                if (!head) c.proj().at(at.add(0, .45, 0)).vel(c.aim(at).scale(.12)).box(1.4f, .2f, .12f).color(0x2a6ad9).dmg(0).warn(8).life(26).spawn(); }
        }, (p, t, m) -> { float s = env(t, 0, 10, 50, 56), f = env(t, 54, 60, 96, 108); p.r("armL", -3f * s - 1.4f * f, 0, .3f).r("armR", -3f * s - 1.4f * f, 0, -.3f).r("head", -.4f * s + .3f * f * wave(t, 1.2f), 0, 0); }, below(.3f));
    }

    // =====================================================================================================
    // GIGA QUEEN (Queen's mecha, Ch2 final boss: all 10 canon moves)
    // =====================================================================================================
    static void gigaQueen(List<CharacterDef> out) {
        ModelDef m = biped(0xd8d8e0, 0x3a3a4e, 0x9a9aae, 0x3a3a4e, 7, 7.4f)
            .box("visor", "head", 0, .6f, -3.8f, 7, 2, .4f, 0x1a1a1a)
            .glow("lens", "head", 0, .6f, -4.05f, 6, 1.2f, .2f, CYAN)
            .box("crest", "head", 0, 5.4f, 1.6f, 2.6f, 4, 2.6f, 0x3a3a4e)
            .box("chest", "body", 0, 1, -2.2f, 5, 3, .6f, 0x9a9aae).glow("core", "body", 0, 1, -2.6f, 2, 1.4f, .2f, PINK)
            .box("fistL", "armL", 0, -7, 0, 3, 3, 3, 0x9a9aae).box("fistR", "armR", 0, -7, 0, 3, 3, 3, 0x9a9aae)
            .box("glass", "armR", 0, -9, -1, 1.6f, 2, 1.6f, ACID);
        out.add(CharacterDef.of("giga_queen", "GIGA Queen", Faction.CYBER_CITY).hp(400).boss().keep(8).rest(16).scale(3f).size(2f, 6f).speed(.22f).egg(0x9a9aae, PINK)
            .model(m).attacks(gqPunch(), gqKick(), gqBaseballs(), gqWheels(), gqLightCone(), gqCopies(), gqPirouette(), gqMissiles(), gqFireLine(), gqFinalBaseball()).build());
    }
    static void ping(AttackCtx c) { c.sound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 2); c.particle(ParticleTypes.FLASH, c.eye(), 1, 0, 0); }
    static Attack gqPunch() {
        return atk("giga_queen_acid_punch", 70, (c, t) -> {
            int reps = c.me.hpFrac() < .5f ? 3 : 1;
            for (int r = 0; r < reps; r++) { int b = 4 + r * 20;
                if (t == b) { c.dash(c.fwd().scale(-.6)); ping(c); }
                if (t == b + 10) { Vec3 f = c.aim(c.eye()); c.dash(flat(f).scale(1.2));
                    c.proj().at(c.eye().add(f.scale(2))).vel(f.scale(1.1)).box(1.6f, 1.6f, 2.4f).color(0x9a9aae).dmg(9).life(10).spawn();
                    c.proj().at(c.eye().add(f.scale(2.2))).vel(f.scale(1.1)).box(.8f, .8f, .8f).color(ACID).dmg(0).life(10).spawn();
                    c.sound(SoundEvents.IRON_GOLEM_ATTACK, 1.4f, .6f); } }
        }, (p, t, m) -> { float ph = (t - 4) % 20; float w = env(ph, 0, 8, 10, 11), s = env(ph, 10, 12, 14, 18); p.r("armR", -1.6f * s + .6f * w, 0, 0).o("armR", 0, 0, -3 * s + 2 * w).r("body", .3f * w - .2f * s, 0, 0); });
    }
    static Attack gqKick() {
        return atk("giga_queen_crouch_kick", 70, (c, t) -> {
            int n = c.me.hpFrac() < .5f ? 3 : 2;
            if (t < n * 4 && t % 4 == 0) ping(c);
            for (int k = 0; k < n; k++) if (t == 20 + k * 12) { Vec3 at = c.pos().add(flat(c.aim(c.pos())).scale(3)); c.aoe(at, 2.8, 8, 1.6);
                c.proj().at(at.add(0, .8, 0)).box(4, 1.2f, 1.2f).color(0x9a9aae).dmg(0).life(4).spawn(); c.sound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.4f, .6f); }
        }, (p, t, m) -> { float cr = env(t, 0, 8, 16, 20), k = (t >= 20 && (t - 20) % 12 < 5) ? 1 : 0; p.o("body", 0, -2 * cr, 0).r("legR", -1.8f * k, 0, 0).r("body", .3f * k, 0, 0); });
    }
    static Attack gqBaseballs() {
        return atk("giga_queen_baseballs", 70, (c, t) -> {
            if (t >= 12 && t < 60 && t % 12 == 0) { Vec3 at = c.eye().add(0, 1, 0), d = c.tpos().subtract(at);
                c.proj().at(at).vel(new Vec3(d.x / 22, .35 + d.y / 22, d.z / 22)).gravity(.03f).box(.7f, .7f, .7f).color(0xff3a3a).dmg(5).life(30).spawn();
                c.sound(SoundEvents.SNOWBALL_THROW, 1.2f, .6f); }
        }, (p, t, m) -> { float s = t >= 6 && t < 60 ? env((t - 6) % 12, 0, 5, 6, 10) : 0; p.r("armR", -3f * s + 1.6f * (1 - s) * (t >= 6 && t < 60 ? 1 : 0), 0, 0); });
    }
    static Attack gqWheels() {
        return atk("giga_queen_wheels", 76, (c, t) -> {
            if (t >= 8 && t < 60 && t % 10 == 8) { Vec3 r = c.right(); double s = (t / 10) % 2 == 0 ? 1 : -1;
                Vec3 at = c.target.position().add(r.scale(-8 * s)).add(c.fwd().scale(c.rnd.nextGaussian() * 1.5)).add(0, .6, 0);
                c.proj().at(at).vel(r.scale(.5 * s)).box(.4f, 1.2f, 1.2f).color(YELLOW).dmg(5).warn(6).life(36).spawn();
                c.proj().at(at).vel(r.scale(.5 * s)).box(.5f, .4f, .4f).color(0x3a3a4e).dmg(0).warn(6).life(36).spawn(); c.sound(SoundEvents.MINECART_RIDING, .5f, 1.4f); }
        }, (p, t, m) -> p.r("armL", 0, 0, -1.2f).r("armR", 0, 0, 1.2f).r("body", 0, .2f * wave(t, .3f), 0));
    }
    static Attack gqLightCone() {
        return atk("giga_queen_acid_light_cone", 80, (c, t) -> {
            if (t == 6) { puti(c, "gl", c.rnd.nextBoolean() ? 1 : -1); c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.6f); }
            int s = geti(c, "gl");
            if (t == 10) { Vec3 f = flat(c.aim(c.pos())); for (int k = -2; k <= 2; k++) beam(c, c.eye(), rotY(f, s * .5 + k * .12), 14, .6f, ACID, 14, 10, 5); }
            if (t == 40) { Vec3 at = c.eye(), d = c.tpos().subtract(at); put(c, "gla", c.target.position());
                c.proj().at(at).vel(new Vec3(d.x / 16, .3 + d.y / 16, d.z / 16)).gravity(.04f).box(.8f, 1, .8f).color(ACID).dmg(4).life(16).spawn(); }
            if (t >= 56 && t < 76 && t % 2 == 0) { Vec3 at = get(c, "gla"); c.particle(ParticleTypes.SNEEZE, at.add(0, .5, 0), 8, 1, .02); c.aoe(at, 2, 2, 0); }
        }, (p, t, m) -> { float s = env(t, 4, 10, 24, 30), th = env(t, 34, 40, 42, 50); p.r("armR", -1.6f * s - 2.6f * th, .6f * s, 0).o("lens", 0, 0, 0); });
    }
    /** Many GIGA Queen copies charge in; kicking ones fill rows, crouching ones leave a gap. */
    static Attack gqCopies() {
        return atk("giga_queen_copies", 80, (c, t) -> {
            if (t >= 8 && t < 60 && t % 13 == 8) { Vec3 f = flat(c.aim(c.pos())), r = side(f); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) { boolean crouch = k == gap; Vec3 at = c.tpos().subtract(f.scale(-8)).add(r.scale(k * 1.2)).add(0, crouch ? -.6 : .4, 0);
                    c.proj().at(at).vel(f.scale(-.55)).box(1, crouch ? .5f : 2, .6f).color(0xd8d8e0).dmg(crouch ? 0 : 5).warn(6).life(30).spawn();
                    if (!crouch) c.proj().at(at.add(0, 1.1, 0)).vel(f.scale(-.55)).box(.7f, .7f, .7f).color(0x3a3a4e).dmg(0).life(30).spawn(); }
                c.sound(SoundEvents.RAVAGER_STEP, 1, 1.2f); }
        }, (p, t, m) -> p.r("armL", -1.6f, 0, .4f * wave(t, .5f)).r("armR", -1.6f, 0, -.4f * wave(t, .5f)));
    }
    static Attack gqPirouette() {
        return atk("giga_queen_pirouette", 60, (c, t) -> {
            if (t == 6) { c.dash(new Vec3(0, 1.4, 0)); ping(c); put(c, "gp", c.target.position()); }
            if (t == 8) c.proj().at(get(c, "gp").add(0, .05, 0)).box(3, .1f, 3).color(0xff4040).dmg(0).warn(18).life(20).spawn();
            if (t == 22) { Vec3 g = get(c, "gp"); c.tp(new Vec3(g.x, c.pos().y, g.z)); c.dash(new Vec3(0, -2.2, 0)); }
            if (t >= 22 && t < 30) c.aoe(c.pos().add(0, 1, 0), 2.6, 9, 1.2);
            if (t == 28) { c.particle(ParticleTypes.EXPLOSION, c.pos(), 3, 1, 0); c.sound(SoundEvents.ANVIL_LAND, 1.4f, .5f); }
        }, (p, t, m) -> { float s = env(t, 6, 10, 28, 40); p.r("body", 0, t * .9f * s, 0).r("armL", 0, 0, -2.6f * s).r("armR", 0, 0, 2.6f * s).r("legR", -1.2f * s, 0, 0); });
    }
    static Attack gqMissiles() {
        return atk("giga_queen_chest_missiles", 76, (c, t) -> {
            if (t >= 10 && t < 46 && t % 4 == 2) { Vec3 at = c.eye().add(0, .6, 0); Vec3 land = c.target.position().add(c.rnd.nextGaussian() * 2.5, 0, c.rnd.nextGaussian() * 2.5);
                Vec3 d = land.subtract(at); int k = (t - 10) / 4; put(c, "gm" + k, land);
                c.proj().at(at).vel(new Vec3(d.x / 24, .6 + d.y / 24 + .03 * 24 / 2.0 - .6, d.z / 24).add(0, .36, 0)).gravity(.03f).box(.35f, .35f, .9f).color(0xd8d8e0).dmg(3).life(24).spawn();
                c.proj().at(land.add(0, .05, 0)).box(1.8f, .1f, 1.8f).color(0xff4040).dmg(0).warn(24).life(26).spawn();
                c.sound(SoundEvents.FIREWORK_ROCKET_LAUNCH, .8f, .8f); }
            if (t >= 34 && t < 70 && (t - 34) % 4 == 2) { int k = (t - 34) / 4; if (k < 9) { Vec3 l = get(c, "gm" + k); c.aoe(l, 1.4, 5, .6);
                c.particle(ParticleTypes.EXPLOSION, l, 1, 0, 0); c.sound(SoundEvents.GENERIC_EXPLODE, .5f, 1.4f); } }
        }, (p, t, m) -> { float s = env(t, 4, 10, 46, 54); p.o("chest", 0, 0, -1.2f * s).r("armL", 0, 0, -1f * s).r("armR", 0, 0, 1f * s).o("core", 0, 0, -1f * s); });
    }
    static Attack gqFireLine() {
        return atk("giga_queen_fire_line", 80, (c, t) -> {
            if (t >= 12 && t < 64 && t % 12 == 0) { Vec3 f = flat(c.aim(c.pos())), r = side(f); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -4; k <= 4; k++) if (Math.abs(k - gap) > 0) c.proj().at(c.eye().add(f.scale(2)).add(r.scale(k * .9)).add(0, -2, 0)).vel(f.scale(.45))
                    .box(.9f, 1.4f, .6f).color(0xff7a2a).dmg(5).life(36).spawn();
                c.particle(ParticleTypes.FLAME, c.eye().add(f.scale(2)), 20, .6, .05); c.sound(SoundEvents.BLAZE_SHOOT, 1.2f, .6f); }
        }, (p, t, m) -> { float s = env(t, 6, 12, 64, 72); p.r("head", -.4f * s, 0, 0).o("head", 0, 0, -1 * s * Math.abs(wave(t, .26f))); });
    }
    /** Final: copies fly from above, then a comically huge slow baseball (hit it back by striking her). */
    static Attack gqFinalBaseball() {
        return atk("giga_queen_final_baseball", 110, (c, t) -> {
            if (t >= 6 && t < 40 && t % 4 == 2) { Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 3, 8, c.rnd.nextGaussian() * 3);
                c.proj().at(at).vel(new Vec3(0, -.6, 0)).box(.9f, 1.8f, .6f).color(0xd8d8e0).dmg(4).warn(6).life(20).spawn(); }
            if (t == 50) { Vec3 f = c.aim(c.eye()); c.proj().at(c.eye().add(f.scale(3))).vel(f.scale(.12)).box(3.5f, 3.5f, 3.5f).color(0xff3a3a).dmg(14).life(60).spawn();
                c.sound(SoundEvents.RAVAGER_ROAR, 1.2f, 1.3f); }
        }, (p, t, m) -> { float w = env(t, 40, 48, 50, 54), s = env(t, 50, 52, 70, 90); p.r("armR", -3f * w - 1.2f * s, 0, 0).r("body", -.2f * w + .3f * s, 0, 0); }, below(.15f));
    }

    // =====================================================================================================
    // SPAMTON (alley salesman: own SPAMTON_NET faction, neutral to the player until provoked)
    // =====================================================================================================
    static ModelDef spamtonModel(boolean neo) {
        ModelDef m = biped(0xf0f0f0, 0x1a1a1a, 0x1a1a1a, 0x1a1a1a, neo ? 7 : 5, neo ? 7 : 6.4f)
            .box("glassL", "head", -1.6f, .6f, -3.5f, 2.6f, 1.6f, .3f, PINK).box("glassR", "head", 1.6f, .6f, -3.5f, 2.6f, 1.6f, .3f, YELLOW)
            .box("grin", "head", 0, -1.8f, -3.4f, 4.4f, 1, .3f, 0x1a1a1a)
            .box("hair", "head", 0, 3.4f, 0, 6.6f, 1.2f, 6.6f, 0x1a1a1a)
            .box("bowtie", "body", 0, 3, -2.2f, 2.6f, 1, .4f, PINK)
            .box("shirt", "body", 0, 1, -2.05f, 1.6f, 4, .2f, 0xf0f0f0)
            .ghost("stringL", "armL", 0, 6, 0, .15f, 14, .15f, 0xdddddd, .7f).ghost("stringR", "armR", 0, 6, 0, .15f, 14, .15f, 0xdddddd, .7f)
            .ghost("stringH", "head", 0, 10, 0, .15f, 14, .15f, 0xdddddd, .7f);
        if (neo) m.box("wingL", "body", -6, 2, 2.4f, 8, 6, .5f, PINK).box("wingR", "body", 6, 2, 2.4f, 8, 6, .5f, YELLOW)
            .glow("heart", "body", 0, 0, -2.4f, 2.6f, 2.6f, .4f, 0xff2a2a)
            .box("cannon", "armR", 0, -6.6f, -1.6f, 2.4f, 2.4f, 4.6f, 0x3a3a3a).glow("muzzle", "armR", 0, -6.6f, -4, 1.6f, 1.6f, .3f, YELLOW)
            .box("cable", "head", 2.8f, -2, 2, .5f, .5f, 5, 0x2a2a2a);
        return m;
    }
    static void spamton(List<CharacterDef> out) {
        out.add(CharacterDef.of("spamton", "Spamton", Faction.SPAMTON_NET).att(Attitude.NEUTRAL).hp(90).keep(6).rest(24).scale(.85f).size(.6f, 1.5f)
            .egg(0xf0f0f0, PINK).model(spamtonModel(false)).attacks(spamtonMoneyVacuum(), spamtonMinitons(), spamtonWordBullets()).build());
    }
    static Attack spamtonMoneyVacuum() {
        return atk("spamton_money_vacuum", 70, (c, t) -> {
            if (t >= 10 && t < 56) { Vec3 pull = c.eye().subtract(c.tpos()).normalize().scale(.12); c.target.push(pull.x, 0, pull.z); c.target.hurtMarked = true;
                if (t % 3 == 0) { Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 2.5, c.rnd.nextGaussian(), c.rnd.nextGaussian() * 2.5);
                    c.proj().at(at).vel(c.eye().subtract(at).normalize().scale(.35)).box(.35f, .1f, .35f).color(YELLOW).dmg(2).life(14).spawn(); }   // KROMER sucked in
                if (t % 8 == 0) c.sound(SoundEvents.BEACON_AMBIENT, .8f, 1.8f); }
            if (t < 56) c.aoe(c.eye(), 1.2, 3, .5);
        }, (p, t, m) -> { float s = env(t, 4, 10, 56, 64); p.r("head", -.4f * s, 0, 0).o("grin", 0, -1.2f * s, 0).r("armL", -1.4f * s, 0, .6f).r("armR", -1.4f * s, 0, -.6f); });
    }
    static Attack spamtonMinitons() {
        return atk("spamton_minitons", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 5 == 3) { Vec3 at = c.eye().add(c.right().scale(c.rnd.nextGaussian() * 1.5)).add(0, .6, 0); Vec3 v = c.aim(at).scale(.5);
                c.proj().at(at).vel(v).box(.45f, .45f, .45f).color(0xf0f0f0).dmg(3).life(30).spawn();
                c.proj().at(at.add(0, .05, 0)).vel(v).box(.5f, .15f, .48f).color(PINK).dmg(0).life(30).spawn();
                c.sound(SoundEvents.VILLAGER_TRADE, .6f, 2); }
        }, (p, t, m) -> { float s = t >= 8 && t < 56 ? (t % 5 < 2 ? 1 : 0) : 0; p.r("armR", -2.4f * s, 0, -.4f).r("head", 0, 0, .4f * wave(t, 1.3f)); });
    }
    static Attack spamtonWordBullets() {
        return atk("spamton_word_bullets", 66, (c, t) -> {
            if (t >= 10 && t < 52 && t % 6 == 4) { Vec3 at = c.eye().add(0, .3, 0); Vec3 v = rotY(c.aim(at), (c.rnd.nextDouble() - .5) * .5).scale(.45);
                int len = 2 + c.rnd.nextInt(3); for (int i = 0; i < len; i++) // a word: a row of letter blocks
                    c.proj().at(at.add(side(flat(v)).scale(i * .4))).vel(v).box(.32f, .42f, .1f).color((t / 6) % 2 == 0 ? YELLOW : 0xffffff).dmg(i == 0 ? 3 : 1).life(36).spawn();
                c.sound(SoundEvents.VILLAGER_AMBIENT, .6f, 1.9f); }
        }, (p, t, m) -> p.r("head", .3f * wave(t, 1.6f), 0, 0).o("grin", 0, -.6f * Math.abs(wave(t, 1.6f)), 0).r("armL", -1f, 0, .5f * wave(t, .8f)));
    }

    // =====================================================================================================
    // SPAMTON NEO (Weird-route/secret boss: hostile; all canon turns)
    // =====================================================================================================
    static void spamtonNeo(List<CharacterDef> out) {
        out.add(CharacterDef.of("spamton_neo", "Spamton NEO", Faction.SPAMTON_NET).hp(320).boss().keep(8).rest(14).scale(1.9f).size(1.2f, 3.4f).speed(.2f)
            .egg(0x1a1a1a, YELLOW).model(spamtonModel(true)).attacks(
                neoFlyingHeads(), neoSpamMail(), neoHeartAttack(), neoCallForYou(), neoGrippingPhones(), neoFace(), neoCallForMe(), neoPipisExplosion(), neoPowerOfNeo()).build());
    }
    static void neoHead(AttackCtx c, Vec3 at, Vec3 v, float dmg, int life, int warn) {
        c.proj().at(at).vel(v).box(.6f, .6f, .6f).color(0xf0f0f0).dmg(dmg).life(life).warn(warn).spawn();
        c.proj().at(at.add(0, .1, 0)).vel(v).box(.65f, .2f, .62f).color(PINK).dmg(0).life(life).warn(warn).spawn();
    }
    static Attack neoFlyingHeads() {
        return atk("spamton_neo_flying_heads", 80, (c, t) -> {
            boolean hard = c.me.hpFrac() < .5f;
            if (t >= 8 && t < 60 && t % 6 == 2) { int lane = ((t - 8) / 6) % 4; Vec3 f = flat(c.aim(c.pos())), r = side(f);
                Vec3 at = hard ? c.tpos().add(rotY(new Vec3(7, 0, 0), c.rnd.nextDouble() * Math.PI * 2)) : c.eye().add(r.scale((lane - 1.5) * 1.3));
                neoHead(c, at, c.aim(at).scale(.55), 4, 30, 6); c.sound(SoundEvents.VILLAGER_TRADE, .5f, 2); }
            if (hard && t >= 12 && t < 60 && t % 16 == 12) { Vec3 f = c.aim(c.eye()); orb(c, c.eye().add(f), f.scale(.5), 1.1f, YELLOW, 6, 30, 4); c.sound(SoundEvents.BLAZE_SHOOT, 1, 1.4f); }
        }, (p, t, m) -> p.r("armR", -1.6f, 0, 0).r("head", .25f * wave(t, 1.5f), 0, .2f * wave(t, .7f)).r("wingL", 0, .3f * wave(t, .5f), 0).r("wingR", 0, -.3f * wave(t, .5f), 0));
    }
    static Attack neoSpamMail() {
        return atk("spamton_neo_spam_mail", 84, (c, t) -> {
            if (t >= 8 && t < 64 && t % 14 == 8) { Vec3 r = c.right(), f = c.fwd(); double s = (t / 14) % 2 == 0 ? 1 : -1; Vec3 at = c.tpos().add(r.scale(-9 * s)).add(f.scale(c.rnd.nextGaussian())).add(0, -.4, 0);
                Vec3 v = r.scale(.45 * s);
                c.proj().at(at).vel(v).box(2.2f, 1, 1.2f).color(0x3a6ad9).dmg(5).warn(8).life(42).spawn();                      // truck
                for (int y = 0; y < 3; y++) c.proj().at(at.add(0, .9 + y * .45, 0)).vel(v).box(1.2f, .4f, .9f).color(0xf0f0f0).dmg(3).life(42).spawn(); // mail stack
                if (c.rnd.nextInt(3) == 0) c.proj().at(at.add(0, 2.4, 0)).vel(v).box(.6f, .6f, .6f).color(0x1a1a1a).dmg(5).life(42).spawn();            // bomb
                else neoHead(c, at.add(0, 2.4, 0), v, 3, 42, 0);
                c.sound(SoundEvents.MINECART_RIDING, .6f, .8f); }
        }, (p, t, m) -> p.r("armL", -2.4f, 0, .5f).r("armR", -.6f, 0, 0).r("head", 0, .4f * wave(t, .9f), 0));
    }
    /** A heart (chained to his chest) flails back and forth shedding diamond bullets. */
    static Attack neoHeartAttack() {
        return atk("spamton_neo_heart_attack", 90, (c, t) -> {
            if (t == 6) c.sound(SoundEvents.CHAIN_PLACE, 1, .8f);
            if (t >= 10 && t < 80) { double sw = Math.sin((t - 10) * .14) * 4.5; Vec3 f = flat(c.aim(c.pos())); Vec3 at = c.eye().add(f.scale(2 + Math.abs(sw))).add(side(f).scale(sw * .6));
                c.proj().at(at).box(.9f, .9f, .5f).color(0xff2a2a).dmg(5).life(1).spawn();
                if (t % 2 == 0) for (int k = 1; k < 5; k++) c.proj().at(c.eye().add(at.subtract(c.eye()).scale(k / 5.0))).box(.2f, .2f, .2f).color(0x9a9aae).dmg(0).life(2).spawn();
                if (t % 7 == 0) for (int i = 0; i < 4; i++) suit(c, at, rotY(new Vec3(.35, 0, 0), i * Math.PI / 2 + t), DIAMOND, 3, 26); }
        }, (p, t, m) -> p.o("heart", 0, 0, -2 * env(t, 6, 10, 80, 86)).r("body", 0, .3f * wave(t - 10, .14f), 0).r("armL", -1f, 0, .6f).r("armR", -1f, 0, -.6f));
    }
    /** "A call for you": bouncing blue Pipis; on impact they fling rapid tiny Spamton heads. */
    static Attack neoCallForYou() {
        return atk("spamton_neo_call_for_you", 90, (c, t) -> {
            for (int k = 0; k < 3; k++) { int b = 10 + k * 22;
                if (t == b) { Vec3 at = c.eye().add(0, .5, 0), d = c.target.position().subtract(at); put(c, "np" + k, c.target.position());
                    c.proj().at(at).vel(new Vec3(d.x / 16, .3 + d.y / 16 + .4, d.z / 16)).gravity(.05f).box(.7f, .9f, .7f).color(0x3fa7ff).dmg(4).life(16).spawn(); // Pipis egg
                    c.sound(SoundEvents.CHICKEN_EGG, 1, .7f); }
                if (t == b + 16) { Vec3 at = get(c, "np" + k).add(0, .4, 0); c.sound(SoundEvents.TURTLE_EGG_CRACK, 1, 1);
                    for (int i = 0; i < 10; i++) { Vec3 d = rotY(new Vec3(1, 0, 0), i * Math.PI / 5); c.proj().at(at).vel(d.scale(.6).add(0, .05, 0)).box(.3f, .3f, .3f).color(0xf0f0f0).dmg(2).life(14).spawn(); } } }
        }, (p, t, m) -> { float s = (t - 10) % 22 < 6 && t >= 10 && t < 76 ? 1 : 0; p.r("armR", -1.8f - .5f * s, 0, 0).o("muzzle", 0, 0, -.5f * s).r("head", 0, 0, .2f * wave(t, 1f)); });
    }
    /** Phones grip the edges, a Spamton head approaches, and they fire big exploding bullets. */
    static Attack neoGrippingPhones() {
        return atk("spamton_neo_gripping_phones", 90, (c, t) -> {
            if (t == 6) { put(c, "ngo", c.tpos()); for (int i = 0; i < 4; i++) { Vec3 at = c.tpos().add(rotY(new Vec3(4.5, .6, 0), i * Math.PI / 2 + Math.PI / 4)); put(c, "ngp" + i, at);
                c.proj().at(at).box(.5f, 1.1f, .3f).color(0x2a2a2a).dmg(0).life(80).spawn(); c.proj().at(at.add(0, .2, 0)).box(.4f, .5f, .32f).color(0x6ac1ff).dmg(0).life(80).spawn(); }
                c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), 1, 1.5f); }
            if (t >= 20 && t < 80 && t % 15 == 5) { int i = ((t - 20) / 15) % 4; Vec3 at = get(c, "ngp" + i); put(c, "ngb" + (i % 2), at.add(c.aim(at).scale(3)));
                orb(c, at, c.aim(at).scale(.25), .9f, YELLOW, 5, 12, 4); c.sound(SoundEvents.FIRECHARGE_USE, .8f, 1.4f); }
            if (t >= 32 && t < 92 && t % 15 == 2) { Vec3 b = get(c, "ngb" + (((t - 32) / 15) % 2)); c.particle(ParticleTypes.EXPLOSION, b, 1, 0, 0); c.aoe(b, 1.6, 4, .5);
                ring(c, b, 6, .3, 0, .35, YELLOW, 2, 14, 0); }
        }, (p, t, m) -> p.r("armL", -2.2f, 0, .4f).r("armR", -2.2f, 0, -.4f).r("head", -.2f, .3f * wave(t, .3f), 0));
    }
    /** "Eyes, nose and mouth": a giant face appears; each feature launches its own projectile type. */
    static Attack neoFace() {
        return atk("spamton_neo_eyes_nose_mouth", 100, (c, t) -> {
            if (t == 6) { Vec3 f = flat(c.aim(c.pos())); Vec3 o = c.pos().add(f.scale(1.5)).add(0, 3, 0); put(c, "nf", o); put(c, "nff", f);
                c.proj().at(o).face(f).box(4, 4, .2f).color(0xf0f0f0).dmg(0).life(90).spawn(); }
            Vec3 o = get(c, "nf"), f = get(c, "nff"), r = side(f);
            if (t >= 16 && t < 90) {
                if (t % 10 == 0) for (int s = -1; s <= 1; s += 2) { Vec3 eye = o.add(r.scale(s * 1)).add(0, .8, 0).add(f.scale(.2));
                    suit(c, eye, c.aim(eye).scale(.45), s < 0 ? PINK : YELLOW, 3, 30); }                                             // eyes: aimed diamonds
                if (t % 14 == 4) { Vec3 nose = o.add(f.scale(.3)); for (int i = -1; i <= 1; i++) orb(c, nose, rotY(c.aim(nose), i * .35).scale(.3).add(0, -.05, 0), .4f, 0xff9a9a, 3, 36, 0); } // nose: slow spread
                if (t % 22 == 10) { Vec3 mouth = o.add(0, -1, 0).add(f.scale(.3)); beam(c, mouth, c.tpos().subtract(mouth), 12, .5f, 0xffffff, 8, 4, 4); }  // mouth: beam
            }
            if (t % 20 == 0) c.sound(SoundEvents.VILLAGER_AMBIENT, .6f, .6f);
        }, (p, t, m) -> p.r("armL", -2.8f, 0, .6f).r("armR", -2.8f, 0, -.6f).o("head", 0, 1.5f * env(t, 0, 6, 90, 98), 0));
    }
    /** "A call for me": he answers a phone and gets blasted by his own tiny heads. A free turn. */
    static Attack neoCallForMe() {
        return atk("spamton_neo_call_for_me", 60, (c, t) -> {
            if (t == 10) c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), 1, 1.8f);
            if (t >= 20 && t < 44 && t % 2 == 0) c.particle(ParticleTypes.POOF, c.eye().add(c.fwd().scale(.6)), 3, .2, .05);
            if (t >= 20 && t < 44 && t % 6 == 0) c.sound(SoundEvents.VILLAGER_HURT, .6f, 2);
        }, (p, t, m) -> { float s = env(t, 8, 14, 46, 56), j = t >= 20 && t < 44 ? wave(t, 2f) : 0; p.r("armR", -2.6f * s, 0, -.8f * s).r("head", -.4f * j, .3f * j, 0).o("body", -j * .5f, 0, 0); });
    }
    /** Weird Route: three Pipis appear beside the target and blow up into tiny Spamton heads. */
    static Attack neoPipisExplosion() {
        return atk("spamton_neo_pipis_explosion", 80, (c, t) -> {
            if (t == 6) for (int i = 0; i < 3; i++) { Vec3 at = c.target.position().add(c.right().scale(4)).add(c.fwd().scale((i - 1) * 2)).add(0, .5, 0); put(c, "nx" + i, at);
                c.proj().at(at).box(.8f, 1, .8f).color(0x3fa7ff).dmg(0).life(30 + i * 10).spawn(); }
            for (int i = 0; i < 3; i++) if (t == 36 + i * 10) { Vec3 at = get(c, "nx" + i); c.particle(ParticleTypes.EXPLOSION, at, 1, 0, 0); c.sound(SoundEvents.GENERIC_EXPLODE, .6f, 1.5f);
                c.aoe(at, 1.5, 5, .6); for (int k = 0; k < 8; k++) neoHead(c, at, rotY(new Vec3(.4, 0, 0), k * Math.PI / 4), 2, 20, 0); }
        }, (p, t, m) -> p.r("armR", -2f, 0, -.3f).r("head", 0, 0, .3f * wave(t, 1.1f)), below(.5f));
    }
    /** Power of NEO: darkness, an inhale that drags the target in, face bullets, then a board-tall BIG SHOT that explodes. */
    static Attack neoPowerOfNeo() {
        return atk("spamton_neo_power_of_neo", 140, (c, t) -> {
            if (t == 2) { c.target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 140, 0)); c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1.2f, .8f); }
            if (t >= 10 && t < 50) { Vec3 pull = c.eye().subtract(c.tpos()).normalize().scale(.09); c.target.push(pull.x, 0, pull.z); c.target.hurtMarked = true;
                if (t % 2 == 0) { Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 3, c.rnd.nextGaussian(), c.rnd.nextGaussian() * 3); c.particle(ParticleTypes.CLOUD, at, 1, 0, 0); } }
            if (t >= 56 && t < 100 && t % 6 == 2) { Vec3 at = c.eye().add(c.right().scale(c.rnd.nextGaussian() * 2)); Vec3 v = c.aim(at).scale(.45);
                c.proj().at(at).vel(v).box(1.1f, 1.1f, .6f).color(0xf0f0f0).dmg(5).life(30).spawn(); c.proj().at(at.add(0, .2, 0)).vel(v).box(1.15f, .3f, .62f).color(PINK).dmg(0).life(30).spawn(); }
            if (t == 106) { Vec3 f = c.aim(c.eye()); put(c, "nbs", f); c.sound(SoundEvents.WARDEN_SONIC_BOOM, 1.4f, 1.2f);
                c.proj().at(c.eye().add(f.scale(2))).vel(f.scale(.35)).box(3, 4, 2).color(YELLOW).dmg(14).life(24).pierce().spawn(); }
            if (t == 130) { Vec3 at = c.eye().add(get(c, "nbs").scale(2 + .35 * 24)); c.particle(ParticleTypes.EXPLOSION_EMITTER, at, 1, 0, 0); c.aoe(at, 3.5, 8, 1.5); c.sound(SoundEvents.GENERIC_EXPLODE, 1.4f, .8f); }
        }, (p, t, m) -> { float in = env(t, 8, 14, 46, 52), sh = env(t, 96, 106, 112, 128);
            p.r("head", -.5f * in, 0, 0).o("grin", 0, -1.5f * in, 0).r("armR", -1.6f * sh, 0, 0).o("muzzle", 0, 0, -1 * sh).r("wingL", 0, .8f * sh, 0).r("wingR", 0, -.8f * sh, 0); }, below(.35f));
    }

    // =====================================================================================================
    // QUEEN'S MANSION STAFF (CYBER_CITY, hostile)
    // =====================================================================================================
    static void mansion(List<CharacterDef> out) {
        ModelDef tm = biped(0x1a1a1a, 0x1a1a1a, 0x1a1a1a, 0x1a1a1a, 5.4f, 6.6f)
            .box("earL", "head", -2.2f, 4, 0, 1.6f, 2, 1.6f, 0x1a1a1a).box("earR", "head", 2.2f, 4, 0, 1.6f, 2, 1.6f, 0x1a1a1a)
            .box("glasses", "head", 0, .7f, -3.4f, 5.6f, 1.4f, .3f, 0xf0f0f0).glow("eyes", "head", 0, .7f, -3.55f, 4, .7f, .2f, 0x5cffd0)
            .box("apron", "body", 0, -1, -2.1f, 4, 5, .3f, 0xf0f0f0).box("collar", "body", 0, 3.4f, 0, 6, 1, 4.4f, 0xff6ac1)
            .box("tail", "body", 0, -2.8f, 2.8f, .6f, .6f, 4, 0x1a1a1a)
            .box("whip", "armR", 0, -6.6f, -2.5f, .3f, .3f, 5, 0xff6ac1);
        out.add(CharacterDef.of("tasque_manager", "Tasque Manager", Faction.CYBER_CITY).hp(110).boss().keep(6).rest(20).scale(1.15f).egg(0x1a1a1a, 0xff6ac1)
            .model(tm).attacks(tmWhip(), tmQuiz()).build());
        out.add(CharacterDef.of("tasque", "Tasque", Faction.CYBER_CITY).hp(32).keep(5).rest(26).size(.7f, .8f).speed(.3f).egg(0x1a1a1a, 0x5cffd0)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 5, 4, 8, 0x1a1a1a)
                .box("head", "body", 0, 3, -4.6f, 5, 5, 4, 0x1a1a1a)
                .box("earL", "head", -1.6f, 3, 0, 1.4f, 2, 1.4f, 0x1a1a1a).box("earR", "head", 1.6f, 3, 0, 1.4f, 2, 1.4f, 0x1a1a1a)
                .glow("eyeL", "head", -1.2f, .5f, -2.1f, 1.2f, 1.2f, .3f, 0x5cffd0).glow("eyeR", "head", 1.2f, .5f, -2.1f, 1.2f, 1.2f, .3f, 0x5cffd0)
                .box("tail", "body", 0, 1.5f, 4.6f, .6f, 4, .6f, 0x1a1a1a).glow("plug", "body", 0, 3.8f, 4.6f, 1, 1, 1, 0x5cffd0)
                .limb("armL", "body", -2, -1, -3, 1.4f, 3, 1.4f, 0x1a1a1a).limb("armR", "body", 2, -1, -3, 1.4f, 3, 1.4f, 0x1a1a1a)
                .limb("legL", null, -2, 3, 3, 1.4f, 3, 1.4f, 0x1a1a1a).limb("legR", null, 2, 3, 3, 1.4f, 3, 1.4f, 0x1a1a1a))
            .attacks(tasqueYarn(), tasqueMeowWow()).build());
        ModelDef sw = biped(0xd82a2a, 0x1a1a1a, 0x1a1a1a, 0x1a1a1a, 5.4f, 5.6f)
            .box("beak", "head", 0, -.4f, -3.4f, 1.6f, 1.2f, 1.6f, 0xffc23a)
            .glow("eyeL", "head", -1.2f, .8f, -2.9f, .9f, .9f, .3f, 0xffffff).glow("eyeR", "head", 1.2f, .8f, -2.9f, .9f, .9f, .3f, 0xffffff)
            .box("crest", "head", 0, 3.6f, .6f, 1.2f, 2, 3, 0xd82a2a)
            .box("shirt", "body", 0, .6f, -2.1f, 2.4f, 5, .3f, 0xf0f0f0).box("bow", "body", 0, 3, -2.3f, 2, .8f, .3f, 0x1a1a1a)
            .box("tray", "armR", 0, -6.6f, -1.6f, 3.6f, .3f, 3.6f, 0xd0d0e0);
        out.add(CharacterDef.of("swatchling", "Swatchling", Faction.CYBER_CITY).hp(40).keep(5).rest(24).egg(0xd82a2a, 0x1a1a1a).random()
            .model(sw).attacks(swatchBirds(), swatchCloches(), swatchCannonball()).build());
    }
    /** Whip: lash marks a spot with a cardinal or diagonal marker, then waves of bullets fly out that way. */
    static Attack tmWhip() {
        return atk("tasque_manager_whip", 70, (c, t) -> {
            for (int w = 0; w < 3; w++) { int b = 8 + w * 18;
                if (t == b) { Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 1.5, 0, c.rnd.nextGaussian() * 1.5); put(c, "tw" + w, at); puti(c, "twd" + w, c.rnd.nextInt(3));
                    for (int k = 0; k < 6; k++) c.proj().at(c.eye().add(at.subtract(c.eye()).scale(k / 6.0))).box(.15f, .15f, .15f).color(0xff6ac1).dmg(0).life(8).spawn();
                    c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1.6f); }
                if (t == b + 8) { Vec3 at = get(c, "tw" + w); int mode = geti(c, "twd" + w); // 0 = all directions (alone), 1 = cardinal, 2 = diagonal
                    int n = mode == 0 ? 12 : 4; double ph = mode == 2 ? Math.PI / 4 : 0;
                    ring(c, at, n, .2, ph, .4, 0xff6ac1, 3, 24, 0); c.particle(ParticleTypes.ELECTRIC_SPARK, at, 10, .3, .1); c.sound(SoundEvents.FIREWORK_ROCKET_BLAST, .6f, 1.6f); } }
        }, (p, t, m) -> { float ph = (t - 8 + 18) % 18; float s = env(ph, 0, 3, 5, 10); p.r("armR", -2.8f * (1 - s) * ramp(t, 0, 8) + 1.2f * s, 0, 0).r("whip", -.8f * s, 0, 0); });
    }
    /** Quiz: four lettered quadrants; every section except the called letter gets whipped with lightning. */
    static Attack tmQuiz() {
        return atk("tasque_manager_quiz", 76, (c, t) -> {
            if (t == 6) { put(c, "tq", c.target.position()); puti(c, "tqa", c.rnd.nextInt(4)); }
            Vec3 o = get(c, "tq"); int ans = geti(c, "tqa"); int[] cols = {0xff4a4a, 0x4aa8ff, 0x5cff8a, 0xffe23a};
            if (t == 6) for (int q = 0; q < 4; q++) c.proj().at(o.add(q % 2 == 0 ? -1.6 : 1.6, .05, q < 2 ? -1.6 : 1.6)).box(3, .1f, 3).color(cols[q]).dmg(0).life(50).spawn();
            if (t == 24) { c.proj().at(c.eye().add(0, 1.4, 0)).box(.8f, .8f, .1f).color(cols[ans]).dmg(0).life(30).spawn(); c.sound(SoundEvents.CAT_AMBIENT, 1, 1.4f); } // the called letter
            if (t == 46) { for (int q = 0; q < 4; q++) if (q != ans) { Vec3 qc = o.add(q % 2 == 0 ? -1.6 : 1.6, 1, q < 2 ? -1.6 : 1.6);
                c.proj().at(qc).box(3, 2.4f, 3).color(0xfff27a).dmg(6).life(6).pierce().spawn(); c.particle(ParticleTypes.ELECTRIC_SPARK, qc, 14, 1, .1); }
                c.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, .6f, 1.5f); }
        }, (p, t, m) -> { float s = env(t, 20, 24, 30, 36), w = env(t, 40, 46, 48, 56); p.r("armL", -2.6f * s, 0, .3f).r("armR", -2.8f * w, 0, -.3f).r("head", 0, .3f * s, 0); });
    }
    static Attack tasqueYarn() {
        return atk("tasque_yarn_balls", 60, (c, t) -> {
            if (t >= 8 && t < 48 && t % 8 == 0) { Vec3 at = c.eye(), d = c.target.position().add(c.rnd.nextGaussian(), 0, c.rnd.nextGaussian()).subtract(at);
                c.proj().at(at).vel(new Vec3(d.x / 20, .5 + d.y / 20, d.z / 20)).gravity(.05f).box(.5f, .5f, .5f).color(0x5cffd0).dmg(3).life(26).spawn();
                c.sound(SoundEvents.CAT_AMBIENT, .6f, 1.6f); }
        }, (p, t, m) -> { float s = t >= 4 && t < 48 ? env((t - 4) % 8, 0, 3, 4, 7) : 0; p.r("armR", -2f * s, 0, 0).r("tail", .6f * wave(t, .5f), 0, 0); });
    }
    static Attack tasqueMeowWow() {
        return atk("tasque_meow_wow", 60, (c, t) -> {
            if (t >= 10 && t < 46 && t % 3 == 1) { Vec3 f = c.aim(c.eye()); float s = .4f + ((t - 10) % 9) * .1f;
                c.proj().at(c.eye().add(f)).vel(f.scale(.55)).box(s, s, .12f).color(0x5cffd0).dmg(2).life(26).spawn(); }
            if (t == 10 || t == 28) c.sound(SoundEvents.CAT_HISS, 1, 1.4f);
        }, (p, t, m) -> { float s = env(t, 6, 10, 46, 52); p.r("head", -.3f * s, 0, 0).o("head", 0, 0, -.8f * s * Math.abs(wave(t, 1))); });
    }
    static Attack swatchBirds() {
        return atk("swatchling_birds", 64, (c, t) -> {
            if (t >= 8 && t < 50 && t % 7 == 1) { Vec3 at = c.tpos().add(c.right().scale((t / 7) % 2 == 0 ? 6 : -6)).add(0, 1, 0);
                Vec3 v = c.aim(at).scale(.5); // locks on to where the target was when spawned
                c.proj().at(at).vel(v).box(.5f, .4f, .7f).color(0xd82a2a).dmg(3).warn(8).life(30).spawn();
                c.proj().at(at.add(0, .2, 0)).vel(v).box(1, .1f, .3f).color(0xd82a2a).dmg(0).warn(8).life(30).spawn(); c.sound(SoundEvents.PARROT_AMBIENT, .7f, 1.4f); }
        }, (p, t, m) -> p.r("armL", 0, 0, -1.2f - .4f * wave(t, 1)).r("armR", 0, 0, 1.2f + .4f * wave(t, 1)));
    }
    /** Cloche-covered plates slide down alternating sides and open toward the target (some hold a healing candy). */
    static Attack swatchCloches() {
        return atk("swatchling_cloches", 70, (c, t) -> {
            for (int k = 0; k < 4; k++) { int b = 6 + k * 14;
                if (t == b) { Vec3 at = c.tpos().add(c.right().scale(k % 2 == 0 ? 2.5 : -2.5)).add(c.fwd().scale(-5)).add(0, 1, 0); put(c, "sc" + k, at);
                    c.proj().at(at).vel(c.fwd().scale(.3)).box(1, .6f, 1).color(0xd0d0e0).dmg(3).life(12).spawn(); }
                if (t == b + 12) { Vec3 at = get(c, "sc" + k).add(c.fwd().scale(3.6)); c.sound(SoundEvents.CHAIN_PLACE, .6f, 1.8f);
                    if (c.rnd.nextInt(3) == 0) c.proj().at(at).box(.4f, .4f, .4f).color(0x5cff8a).dmg(0).life(30).spawn(); // candy (decor)
                    fan(c, at, c.aim(at), 3, .5, .45, 0xffc23a, 3, 30); } }
        }, (p, t, m) -> p.r("armR", -1.6f, 0, 0).o("tray", 0, .4f * wave(t, .6f), 0).r("head", 0, .3f * wave(t, .3f), 0));
    }
    /** Blue Swatchling: cannonballs down, bouncing three times and throwing four stars each bounce. */
    static Attack swatchCannonball() {
        return atk("swatchling_cannonball", 80, (c, t) -> {
            if (t == 6) c.dash(new Vec3(0, 1.1, 0).add(c.aim(c.pos()).multiply(.4, 0, .4)));
            for (int b = 0; b < 3; b++) { int at = 22 + b * 16;
                if (t == at - 6) c.dash(c.aim(c.pos()).multiply(.4, 0, .4).add(0, -1.2, 0));
                if (t == at) { c.aoe(c.pos(), 1.8, 5, .8); ring(c, c.pos().add(0, .5, 0), 4, .3, b * .4, .4, 0x3fa7ff, 3, 20, 0); c.sound(SoundEvents.SLIME_SQUISH, 1, .8f); c.dash(new Vec3(0, .8, 0)); } }
        }, (p, t, m) -> { float s = env(t, 4, 8, 66, 76); p.r("body", t * .5f * s, 0, 0).r("armL", -2.6f * s, 0, 0).r("armR", -2.6f * s, 0, 0); });
    }

    // =====================================================================================================
    // CYBER FIELD / CITY ENEMIES (CYBER_CITY, hostile)
    // =====================================================================================================
    static void fieldEnemies(List<CharacterDef> out) {
        ModelDef ww = biped(0x3a8ad9, 0x24284a, 0x3a8ad9, 0x24284a, 5.6f, 6.4f)
            .glow("visor", "head", 0, .5f, -3.3f, 4.6f, 1.2f, .3f, 0xff4040)
            .box("plug", "head", 0, 4.2f, 0, 2.4f, 2, 2.4f, 0x9a9aae).ghost("wire", "head", 0, 12, 0, .4f, 14, .4f, 0x2a2a2a, 1f)
            .box("clawL", "armL", 0, -6.8f, -.6f, 2, 1, 2.6f, 0x9a9aae).box("clawR", "armR", 0, -6.8f, -.6f, 2, 1, 2.6f, 0x9a9aae);
        out.add(CharacterDef.of("werewire", "Werewire", Faction.CYBER_CITY).hp(38).keep(5).rest(26).egg(0x3a8ad9, 0x2a2a2a).random()
            .model(ww).attacks(werewireElectricBalls(), werewireArmArc(), werewireFingerLasers()).build());
        out.add(CharacterDef.of("virovirokun", "Virovirokun", Faction.CYBER_CITY).hp(36).keep(5).rest(26).size(.7f, 1.3f).egg(0x8a3ad9, 0x5cff8a)
            .model(new ModelDef()
                .box("body", null, 0, 5, 0, 7, 7, 7, 0x8a3ad9)
                .box("spikeU", "body", 0, 4.4f, 0, 1.6f, 2, 1.6f, 0x8a3ad9).box("spikeL", "body", -4.4f, 0, 0, 2, 1.6f, 1.6f, 0x8a3ad9)
                .box("spikeR", "body", 4.4f, 0, 0, 2, 1.6f, 1.6f, 0x8a3ad9).box("spikeB", "body", 0, 0, 4.4f, 1.6f, 1.6f, 2, 0x8a3ad9)
                .box("head", "body", 0, 4.2f, 0, 6, 1.4f, 6, 0xf0f0f0).box("cross", "head", 0, .2f, -3.1f, 1.6f, 1.6f, .3f, 0xd82a2a) // nurse cap
                .glow("eyeL", "body", -1.6f, .8f, -3.6f, 1.2f, 1.6f, .3f, 0x5cff8a).glow("eyeR", "body", 1.6f, .8f, -3.6f, 1.2f, 1.6f, .3f, 0x5cff8a)
                .box("mouth", "body", 0, -1.4f, -3.6f, 3, .6f, .3f, 0x1a1a1a)
                .limb("legL", null, -2, 2, 0, 1.6f, 2, 1.6f, 0x6a2aa9).limb("legR", null, 2, 2, 0, 1.6f, 2, 1.6f, 0x6a2aa9))
            .attacks(viroVirus(), viroInvader()).build());
        out.add(CharacterDef.of("maus", "Maus", Faction.CYBER_CITY).hp(26).keep(5).rest(24).size(.6f, .7f).speed(.33f).egg(0xd0d0e0, 0x3a3a3a)
            .model(new ModelDef()
                .box("body", null, 0, 2.5f, 0, 5, 4, 7, 0xd0d0e0)
                .box("head", "body", 0, 1, -3.6f, 4, 3, .3f, 0xb0b0c0).box("buttons", "body", 0, 2.1f, -1.6f, 4.2f, .3f, 3, 0x9a9aae)
                .box("divider", "body", 0, 2.2f, -1.6f, .3f, .3f, 3.2f, 0x3a3a3a)
                .glow("eyeL", "body", -1.1f, .6f, -3.6f, .8f, .8f, .3f, 0x1a1a1a).glow("eyeR", "body", 1.1f, .6f, -3.6f, .8f, .8f, .3f, 0x1a1a1a)
                .box("tail", "body", 0, -1.2f, 6, .4f, .4f, 5, 0x3a3a3a)
                .limb("legL", null, -1.5f, 1, 0, 1, 1, 1, 0xb0b0c0).limb("legR", null, 1.5f, 1, 0, 1, 1, 1, 0xb0b0c0))
            .attacks(mausHoles(), mausTrail()).build());
        out.add(CharacterDef.of("ambyu_lance", "Ambyu-Lance", Faction.CYBER_CITY).hp(40).keep(6).rest(26).size(1f, 1.2f).speed(.32f).egg(0xf0f0f0, 0xd82a2a)
            .model(new ModelDef()
                .box("body", null, 0, 5, 0, 8, 7, 11, 0xf0f0f0)
                .box("cab", "body", 0, -1, -6.4f, 8, 4.4f, 2, 0xf0f0f0).glow("windshield", "body", 0, 1.8f, -5.6f, 6.6f, 2, .3f, 0x6ac1ff)
                .box("cross_v", "body", -4.1f, 0, 0, .3f, 4, 1.4f, 0xd82a2a).box("cross_h", "body", -4.1f, 0, 0, .3f, 1.4f, 4, 0xd82a2a)
                .box("head", "body", 0, 4, -2, 2.4f, 1.2f, 2.4f, 0x2a2a2a).glow("siren", "head", 0, 1, 0, 1.8f, 1, 1.8f, 0xff3030)
                .glow("eyeL", "body", -2, 1.8f, -5.8f, 1, 1.4f, .3f, 0x1a1a1a).glow("eyeR", "body", 2, 1.8f, -5.8f, 1, 1.4f, .3f, 0x1a1a1a)
                .limb("legL", null, -4.2f, 2, -3.5f, 1.4f, 2, 2, 0x1a1a1a).limb("legR", null, 4.2f, 2, -3.5f, 1.4f, 2, 2, 0x1a1a1a)
                .limb("armL", null, -4.2f, 2, 3.5f, 1.4f, 2, 2, 0x1a1a1a).limb("armR", null, 4.2f, 2, 3.5f, 1.4f, 2, 2, 0x1a1a1a))
            .attacks(ambyuVaccine(), ambyuCarChase()).build());
        out.add(CharacterDef.of("poppup", "Poppup", Faction.CYBER_CITY).hp(28).keep(6).rest(24).size(.8f, 1f).egg(0xf0f0f0, 0x2a6ad9)
            .model(new ModelDef()
                .box("body", null, 0, 6, 0, 9, 7, 1.4f, 0xf0f0f0)
                .box("titlebar", "body", 0, 3.8f, 0, 9, 1.4f, 1.6f, 0x2a6ad9).box("close", "titlebar", 3.6f, 0, -.9f, 1, 1, .2f, 0xd82a2a)
                .box("head", "body", 0, 0, -.8f, 6, 4, .2f, 0xf0f0f0)
                .glow("eyeL", "head", -1.4f, .6f, -.2f, 1, 1.4f, .2f, 0x1a1a1a).glow("eyeR", "head", 1.4f, .6f, -.2f, 1, 1.4f, .2f, 0x1a1a1a)
                .box("mouth", "head", 0, -1.2f, -.2f, 3, .6f, .2f, 0xd82a2a)
                .limb("armL", "body", -5, 1, 0, 1, 3, 1, 0x2a6ad9).limb("armR", "body", 5, 1, 0, 1, 3, 1, 0x2a6ad9))
            .attacks(poppupBirds(), poppupWindows()).build());
    }
    static Attack werewireElectricBalls() {
        return atk("werewire_electric_balls", 66, (c, t) -> {
            if (t == 4) c.dash(new Vec3(0, .7, 0));
            if (t >= 10 && t < 54) { c.dash(c.right().scale(Math.sin(t * .15) * .2).add(0, .05, 0));
                if (t % 5 == 0) { Vec3 at = c.pos().add(0, -.2, 0); c.proj().at(at).vel(new Vec3(0, -.1, 0)).gravity(.02f).box(.5f, .5f, .5f).color(0xffe23a).dmg(3).life(36).spawn();
                    c.particle(ParticleTypes.ELECTRIC_SPARK, at, 5, .2, .05); } }
            if (t % 15 == 10) c.sound(SoundEvents.REDSTONE_TORCH_BURNOUT, .4f, 1.6f);
        }, (p, t, m) -> p.r("body", 0, 0, .35f * wave(t, .15f)).r("legL", -.4f, 0, 0).r("legR", -.4f, 0, 0).r("armL", 0, 0, -.6f).r("armR", 0, 0, .6f));
    }
    static Attack werewireArmArc() {
        return atk("werewire_arm_arc", 60, (c, t) -> {
            if (t == 14 || t == 36) { Vec3 f = c.aim(c.eye()); for (int i = 0; i < 5; i++) { Vec3 at = c.eye().add(rotY(flat(f), (i - 2) * .35).scale(1.2)).add(0, (i - 2) * .15 * (t == 14 ? 1 : -1), 0);
                c.proj().at(at).vel(f.scale(.45)).box(.4f, .4f, .4f).color(0x6ac1ff).dmg(3).life(30).spawn(); } c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .8f, 1.2f); }
        }, (p, t, m) -> { float a = env(t, 4, 12, 14, 20), b = env(t, 26, 34, 36, 42); p.r("armR", -2.4f * a + .4f * b, .8f * a, 0).r("armL", -2.4f * b, -.8f * b, 0); });
    }
    static Attack werewireFingerLasers() {
        return atk("werewire_finger_lasers", 56, (c, t) -> {
            if (t == 10 || t == 30) { Vec3 f = c.tpos().subtract(c.eye()); for (int i = -1; i <= 1; i++) beam(c, c.eye().add(c.right().scale(.6)), rotY(f, i * .2), 10, .2f, 0xff4040, 10, 5, 4);
                c.sound(SoundEvents.BEACON_POWER_SELECT, .6f, 2); }
        }, (p, t, m) -> { float s = env(t, 4, 10, 20, 26) + env(t, 24, 30, 40, 48); p.r("armR", -1.6f * s, 0, 0); });
    }
    /** Virus: purple arrows that burst into smoke; arrows "infect" into little homing Virovirokun icons. */
    static Attack viroVirus() {
        return atk("virovirokun_virus", 70, (c, t) -> {
            if (t >= 8 && t < 50 && t % 7 == 1) { Vec3 at = c.tpos().add(c.right().scale(6)).add(c.fwd().scale(c.rnd.nextGaussian() * 2)).add(0, .2, 0); put(c, "vv" + (t / 7) % 3, at);
                c.proj().at(at).vel(c.aim(at).scale(.45)).box(.25f, .25f, .8f).color(0x8a3ad9).dmg(3).life(12).spawn(); c.sound(SoundEvents.ARROW_SHOOT, .6f, 1.4f); }
            if (t >= 20 && t < 62 && t % 7 == 6) { Vec3 at = get(c, "vv" + ((t - 5) / 7) % 3).add(c.aim(get(c, "vv" + ((t - 5) / 7) % 3)).scale(5)); c.particle(ParticleTypes.WITCH, at, 10, .3, .02);
                c.proj().at(at).vel(c.aim(at).scale(.2)).box(.4f, .4f, .4f).color(0xb05aff).dmg(3).life(30).spawn(); }
        }, (p, t, m) -> p.r("spikeL", 0, 0, .3f * wave(t, .8f)).r("spikeR", 0, 0, -.3f * wave(t, .8f)).o("body", 0, Math.abs(wave(t, .4f)), 0));
    }
    /** Invader: alien rows sweep side to side above, firing down and stepping lower at each edge. */
    static Attack viroInvader() {
        return atk("virovirokun_invader", 90, (c, t) -> {
            if (t == 4) put(c, "vi", c.target.position().add(0, 5, 0));
            Vec3 o = get(c, "vi"), r = c.right();
            if (t >= 6 && t < 84 && t % 3 == 0) { int leg = (t - 6) / 20; double x = ((t - 6) % 20) / 20.0 * 6 - 3; if (leg % 2 == 1) x = -x;
                for (int k = -1; k <= 1; k++) { Vec3 at = o.add(r.scale(x + k * 1.6)).add(0, -leg * .6, 0);
                    c.proj().at(at).box(.6f, .4f, .4f).color(0x5cff8a).dmg(0).life(3).spawn();
                    if (t % 12 == 0 && k == ((t / 12) % 3) - 1) c.proj().at(at.add(0, -.4, 0)).vel(new Vec3(0, -.45, 0)).box(.15f, .4f, .15f).color(0xffffff).dmg(3).life(16).spawn(); } }
            if (t % 12 == 0) c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), .5f, .6f);
        }, (p, t, m) -> p.r("body", 0, .3f * wave(t, .3f), 0).o("body", 0, 1, 0));
    }
    /** Maus Holes: holes ring the target, eyes peek, then mice dash out of the marked tunnels. */
    static Attack mausHoles() {
        return atk("maus_holes", 76, (c, t) -> {
            if (t == 4) { Vec3 o = c.target.position(); put(c, "mo", o); for (int i = 0; i < 6; i++) { Vec3 h = o.add(rotY(new Vec3(4.5, .05, 0), i * Math.PI / 3)); put(c, "mh" + i, h);
                c.proj().at(h).box(1, .1f, 1).color(0x1a1a1a).dmg(0).life(68).spawn(); } }
            for (int w = 0; w < 4; w++) { int b = 12 + w * 14;
                if (t == b) { int i = c.rnd.nextInt(6); puti(c, "mi" + w, i); c.proj().at(get(c, "mh" + i).add(0, .25, 0)).box(.6f, .2f, .1f).color(0xffffff).dmg(0).life(10).spawn(); }
                if (t == b + 10) { Vec3 h = get(c, "mh" + geti(c, "mi" + w)), v = get(c, "mo").subtract(h).normalize().scale(.4);
                    c.proj().at(h.add(0, .3, 0)).vel(v).box(.5f, .4f, .8f).color(0xd0d0e0).dmg(4).life(26).spawn(); c.sound(SoundEvents.BAT_TAKEOFF, .5f, 1.8f); } }
        }, (p, t, m) -> p.r("tail", 0, .6f * wave(t, .7f), 0).o("body", 0, .5f * Math.abs(wave(t, .8f)), 0));
    }
    /** Maus Trail: a cursor chases the target with a delayed grey trail. */
    static Attack mausTrail() {
        return atk("maus_trail", 80, (c, t) -> {
            if (t == 6) put(c, "mc", c.tpos().add(c.right().scale(4)));
            if (t >= 6 && t < 74) { Vec3 cur = get(c, "mc"); cur = cur.add(c.tpos().subtract(cur).scale(.08)); put(c, "mc", cur);
                c.proj().at(cur).box(.5f, .7f, .2f).color(0xffffff).dmg(4).life(2).spawn();
                if (t % 2 == 0) c.proj().at(cur).box(.35f, .35f, .35f).color(0x8a8a9a).dmg(2).life(14).shrink().spawn(); }
            if (t % 10 == 6) c.sound(SoundEvents.UI_BUTTON_CLICK.value(), .4f, 1.6f);
        }, (p, t, m) -> p.r("body", 0, .3f * wave(t, .4f), 0));
    }
    /** Vaccine: crosses fly in from the side (hunting down any virus bullets) and on at the target. */
    static Attack ambyuVaccine() {
        return atk("ambyu_lance_vaccine", 64, (c, t) -> {
            if (t >= 8 && t < 50 && t % 6 == 2) { Vec3 at = c.tpos().add(c.right().scale(-6)).add(c.fwd().scale(c.rnd.nextGaussian() * 2)).add(0, c.rnd.nextGaussian() * .5, 0);
                Vec3 v = c.right().scale(.45).add(c.aim(at).scale(.1));
                c.proj().at(at).vel(v).box(.2f, .7f, .2f).color(0x5cff8a).dmg(3).warn(4).life(30).spawn(); c.proj().at(at).vel(v).box(.7f, .2f, .2f).color(0x5cff8a).dmg(0).warn(4).life(30).spawn();
                c.sound(SoundEvents.NOTE_BLOCK_BIT.value(), .4f, 1.8f); }
        }, (p, t, m) -> p.o("siren", 0, .3f * Math.abs(wave(t, 1.2f)), 0).r("body", 0, 0, .05f * wave(t, .6f)));
    }
    /** Car Chase: lanes of ambulances race by, faster ones signal and swerve into the next lane; cacti drift past. */
    static Attack ambyuCarChase() {
        return atk("ambyu_lance_car_chase", 90, (c, t) -> {
            if (t == 4) put(c, "ac", c.target.position());
            Vec3 o = get(c, "ac"), f = c.fwd(), r = c.right();
            if (t >= 8 && t < 74 && t % 8 == 0) { int lane = c.rnd.nextInt(3) - 1; boolean swerve = c.rnd.nextInt(3) == 0; Vec3 at = o.add(f.scale(9)).add(r.scale(lane * 1.6)).add(0, .5, 0);
                Vec3 v = f.scale(swerve ? -.65 : -.45).add(r.scale(swerve ? (lane >= 0 ? -.08 : .08) : 0));
                c.proj().at(at).vel(v).box(1.1f, .9f, 1.8f).color(0xf0f0f0).dmg(5).warn(6).life(36).spawn();
                c.proj().at(at.add(0, .55, 0)).vel(v).box(.4f, .25f, .4f).color(swerve ? 0xffc23a : 0xff3030).dmg(0).warn(6).life(36).spawn();
                c.proj().at(o.add(f.scale(9)).add(r.scale(c.rnd.nextBoolean() ? 3.6 : -3.6)).add(0, .6, 0)).vel(f.scale(-.3)).box(.4f, 1.2f, .4f).color(0x3faa5a).dmg(3).life(60).spawn(); // cactus
                c.sound(SoundEvents.MINECART_RIDING, .4f, 1.6f); }
        }, (p, t, m) -> p.o("siren", 0, .3f * Math.abs(wave(t, 1.6f)), 0).o("body", 0, .3f * Math.abs(wave(t, 1f)), 0));
    }
    static Attack poppupBirds() {
        return atk("poppup_birds", 64, (c, t) -> {
            if (t >= 8 && t < 50 && t % 4 == 0) { Vec3 at = c.tpos().add(rotY(new Vec3(6, 0, 0), c.rnd.nextDouble() * Math.PI * 2)).add(0, c.rnd.nextDouble() * 1.5, 0);
                Vec3 v = c.aim(at).scale(.4); c.proj().at(at).vel(v).box(.4f, .35f, .6f).color(0x2a6ad9).dmg(3).warn(6).life(30).spawn();
                c.proj().at(at.add(0, .2, 0)).vel(v).box(.9f, .1f, .25f).color(0x2a6ad9).dmg(0).warn(6).life(30).spawn(); }
            if (t % 10 == 8) c.sound(SoundEvents.PARROT_AMBIENT, .5f, 1.6f);
        }, (p, t, m) -> p.r("armL", 0, 0, -1.4f * Math.abs(wave(t, .5f))).r("armR", 0, 0, 1.4f * Math.abs(wave(t, .5f))));
    }
    /** Pop-ups: monochrome windows pop up around the target, briefly walling off space. */
    static Attack poppupWindows() {
        return atk("poppup_windows", 76, (c, t) -> {
            if (t >= 8 && t < 60 && t % 7 == 1) { Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 2.5, c.rnd.nextDouble(), c.rnd.nextGaussian() * 2.5); Vec3 face = c.aim(c.eye());
                c.proj().at(at).face(face).box(2, 1.4f, .15f).color(0xf0f0f0).dmg(3).warn(10).life(34).spawn();
                c.proj().at(at.add(0, .8, 0)).face(face).box(2, .3f, .17f).color(0x1a1a1a).dmg(0).warn(10).life(34).spawn();
                c.sound(SoundEvents.ITEM_FRAME_PLACE, .8f, 1.6f); }
        }, (p, t, m) -> p.o("body", 0, .8f * Math.abs(wave(t, .45f)), 0).r("close", 0, 0, t * .2f));
    }

    // =====================================================================================================
    // SWEET CAP'N CAKES: Sweet, Cap'n, K_K (Cyber Field miniboss band, hostile until recruited)
    // =====================================================================================================
    static void sweetCapnCakes(List<CharacterDef> out) {
        ModelDef sweet = biped(0xffb0d0, 0xff6ac1, 0xffb0d0, 0x9a2a7a, 5.4f, 6.4f)
            .box("hair", "head", 0, 3.4f, .6f, 7, 2.4f, 7, 0xff3a8a).box("bun", "head", 0, 5, 1.6f, 3, 2, 3, 0xff3a8a)
            .glow("shades", "head", 0, .6f, -3.3f, 5.6f, 1.2f, .3f, 0x1a1a1a)
            .box("headphones", "head", 0, 1, 0, 7.4f, 2, 2, 0xffe23a)
            .box("mic", "armR", 0, -6.8f, -.8f, .8f, 1.6f, .8f, 0x3a3a3a);
        ModelDef capn = biped(0x9adcff, 0x2a6ad9, 0x9adcff, 0x1a3a7a, 6, 6.6f)
            .box("hat", "head", 0, 3.8f, 0, 7.4f, 1.6f, 7.4f, 0xf0f0f0).box("brim", "head", 0, 3, -3.6f, 7.4f, .4f, 2, 0x1a1a1a)
            .box("anchor", "hat", 0, 0, -3.75f, 1.4f, 1.2f, .2f, 0xffe23a)
            .glow("eyes", "head", 0, .4f, -3.4f, 4, .8f, .3f, 0x1a1a1a)
            .box("keytar", "armR", -2, -5.6f, -2, 7, 1.6f, .6f, 0xff6ac1);
        ModelDef kk = biped(0xb0ff9a, 0x3aa83a, 0xb0ff9a, 0x1a5a1a, 6.4f, 6.4f)
            .box("speakerHead", "head", 0, 0, -3.3f, 5.6f, 5.6f, .4f, 0x1a1a1a).glow("cone", "head", 0, 0, -3.55f, 3, 3, .2f, 0x5cff8a)
            .box("antenna", "head", 0, 4.4f, 0, .5f, 2.6f, .5f, 0x3a3a3a)
            .box("drumstick", "armR", 0, -7, -1.6f, .4f, .4f, 3, 0xd8a86a);
        out.add(CharacterDef.of("sweet", "Sweet", Faction.CYBER_CITY).hp(55).keep(6).rest(24).egg(0xff6ac1, 0xffe23a).model(sweet).attacks(sccCircleDrops(), sccNoteSpeakers()).build());
        out.add(CharacterDef.of("capn", "Cap'n", Faction.CYBER_CITY).hp(55).keep(6).rest(24).egg(0x2a6ad9, 0xf0f0f0).model(capn).attacks(sccNoteSpeakers(), sccLongWaves()).build());
        out.add(CharacterDef.of("k_k", "K_K", Faction.CYBER_CITY).hp(55).keep(6).rest(24).egg(0x3aa83a, 0x1a1a1a).model(kk).attacks(sccVerticalWaves(), sccLongWaves(), sccCircleDrops()).build());
    }
    static void speakers(AttackCtx c, Vec3 o, int life) {
        for (int s = -1; s <= 1; s += 2) { Vec3 at = o.add(c.right().scale(s * 5));
            c.proj().at(at).box(1.2f, 1.8f, 1).color(0x1a1a1a).dmg(0).life(life).spawn(); c.proj().at(at.add(c.right().scale(-s * .52))).box(.05f, 1f, .8f).color(PINK).dmg(0).life(life).spawn(); }
    }
    /** Circle bullets "spoken" from the band's drawings fall from above, trailing behind them. */
    static Attack sccCircleDrops() {
        return atk("scc_circle_drops", 70, (c, t) -> {
            if (t >= 8 && t < 56 && t % 4 == 0) { Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 2.5, 6, c.rnd.nextGaussian() * 2.5);
                for (int k = 0; k < 3; k++) c.proj().at(at.add(0, k * .5, 0)).vel(new Vec3(0, -.45, 0)).box(.5f - k * .12f, .5f - k * .12f, .5f - k * .12f).color(k == 0 ? 0xffffff : 0xffb0d0).dmg(k == 0 ? 3 : 0).warn(4).life(22).spawn(); }
            if (t % 8 == 0) c.sound(SoundEvents.NOTE_BLOCK_BELL.value(), .4f, .8f + (t / 8 % 4) * .15f);
        }, (p, t, m) -> p.r("armR", -2.6f, 0, .3f * wave(t, .8f)).o("body", 0, Math.abs(wave(t, .8f)), 0).r("head", 0, 0, .25f * wave(t, .4f)));
    }
    /** Musical notes fire from side speakers, drifting to follow the target's height. */
    static Attack sccNoteSpeakers() {
        return atk("scc_note_speakers", 72, (c, t) -> {
            if (t == 4) { put(c, "sn", c.tpos()); speakers(c, c.tpos(), 66); }
            if (t >= 12 && t < 64 && t % 5 == 2) { int s = (t / 5) % 2 == 0 ? 1 : -1; Vec3 at = get(c, "sn").add(c.right().scale(s * 5));
                Vec3 v = c.right().scale(-s * .4).add(0, (c.tpos().y - at.y) * .04, 0);
                c.proj().at(at).vel(v).box(.4f, .4f, .4f).color(0xffe23a).dmg(3).life(30).spawn(); c.proj().at(at.add(0, .4, 0)).vel(v).box(.1f, .5f, .1f).color(0xffe23a).dmg(0).life(30).spawn();
                c.sound(SoundEvents.NOTE_BLOCK_HARP.value(), .6f, .6f + c.rnd.nextFloat()); }
        }, (p, t, m) -> p.r("armL", -1.4f, 0, .4f * wave(t, 1)).r("armR", -1.4f, 0, -.4f * wave(t, 1)).o("body", 0, Math.abs(wave(t, .6f)), 0));
    }
    /** Vertical sound-wave bullets with trails sweep out from the speakers. */
    static Attack sccVerticalWaves() {
        return atk("scc_vertical_waves", 70, (c, t) -> {
            if (t == 4) { put(c, "sv", c.tpos()); speakers(c, c.tpos(), 64); }
            if (t >= 12 && t < 60 && t % 6 == 0) { int s = (t / 6) % 2 == 0 ? 1 : -1; Vec3 at = get(c, "sv").add(c.right().scale(s * 5)).add(c.fwd().scale(c.rnd.nextGaussian()));
                for (int k = 0; k < 3; k++) c.proj().at(at.add(c.right().scale(s * k * .4))).vel(c.right().scale(-s * .4)).box(.15f, 1.6f - k * .3f, .4f).color(k == 0 ? 0x5cff8a : 0x2a8a3a).dmg(k == 0 ? 3 : 0).life(30).spawn();
                c.sound(SoundEvents.NOTE_BLOCK_BASS.value(), .6f, .8f); }
        }, (p, t, m) -> { float hit = t % 6 < 2 ? 1 : 0; p.r("armR", -1.6f - .8f * hit, 0, 0).r("armL", -1.6f - .8f * (1 - hit), 0, 0); });
    }
    /** Two sets of four long waves burst out of both speakers at once. */
    static Attack sccLongWaves() {
        return atk("scc_long_waves", 64, (c, t) -> {
            if (t == 4) { put(c, "sl", c.tpos()); speakers(c, c.tpos(), 58); }
            if (t == 20 || t == 40) { for (int s = -1; s <= 1; s += 2) { Vec3 at = get(c, "sl").add(c.right().scale(s * 5));
                for (int k = 0; k < 4; k++) c.proj().at(at.add(0, (k - 1.5) * .6, 0)).vel(rotY(c.right().scale(-s * .45), (k - 1.5) * .12)).box(.2f, .3f, 1.4f).color(0xffe23a).dmg(3).warn(6).life(30).spawn(); }
                c.sound(SoundEvents.NOTE_BLOCK_BASS.value(), 1, .5f); c.particle(ParticleTypes.NOTE, c.eye().add(0, .6, 0), 6, .5, .5); }
        }, (p, t, m) -> { float s = env(t, 12, 20, 22, 30) + env(t, 32, 40, 42, 50); p.r("armL", -2.8f * s, 0, .4f).r("armR", -2.8f * s, 0, -.4f).o("body", 0, 1.5f * s, 0); });
    }

    // =====================================================================================================
    // ADDISON (Cyber City advertisers: merchants, neutral)
    // =====================================================================================================
    static void addison(List<CharacterDef> out) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 7, 0, 6, 8, 1.2f, 0x3a8ad9)
            .box("head", "body", 0, 7, 0, 7, 6, 1.2f, 0x3a8ad9).box("point", "head", 0, 4, 0, 3.6f, 2.4f, 1.2f, 0x3a8ad9)
            .glow("eyeL", "head", -1.4f, .6f, -.7f, 1, 1, .2f, 0x1a1a1a).glow("eyeR", "head", 1.4f, .6f, -.7f, 1, 1, .2f, 0x1a1a1a)
            .box("mouth", "head", 0, -1.4f, -.7f, 3.4f, 1, .2f, 0xffffff)
            .box("tie", "body", 0, 1.4f, -.7f, 1, 4, .2f, 0xd82a2a)
            .limb("armL", "body", -3.6f, 3, 0, 1.2f, 6, 1.2f, 0x3a8ad9).limb("armR", "body", 3.6f, 3, 0, 1.2f, 6, 1.2f, 0x3a8ad9)
            .limb("legL", null, -1.4f, 3, 0, 1.4f, 3, 1.2f, 0x2a6ab0).limb("legR", null, 1.4f, 3, 0, 1.4f, 3, 1.2f, 0x2a6ab0);
        out.add(CharacterDef.of("addison", "Addison", Faction.MERCHANT).att(Attitude.NEUTRAL).hp(40).keep(5).rest(30).egg(0x3a8ad9, 0xd82a2a)
            .model(m).attacks(addisonAdBlast()).build());
    }
    /** Not fought in canon: when provoked, an Addison shoves ad flyers and a pointing arrow at you. */
    static Attack addisonAdBlast() {
        return atk("addison_ad_blast", 56, (c, t) -> {
            if (t >= 10 && t < 40 && t % 6 == 4) { Vec3 at = c.eye(); Vec3 v = rotY(c.aim(at), (c.rnd.nextDouble() - .5) * .6).scale(.45);
                c.proj().at(at).vel(v).box(.7f, .9f, .05f).color((t / 6) % 2 == 0 ? 0xffe23a : 0xff6ac1).dmg(2).life(30).spawn(); c.sound(SoundEvents.BOOK_PAGE_TURN, .7f, 1.2f); }
            if (t == 42) { Vec3 f = c.aim(c.eye()); c.proj().at(c.eye().add(f)).vel(f.scale(.6)).box(.3f, .3f, 1.2f).color(0xd82a2a).dmg(3).life(20).spawn(); }
        }, (p, t, m) -> p.r("armR", -1.6f, 0, -.4f * wave(t, .9f)).r("body", 0, 0, .15f * wave(t, .6f)).o("mouth", 0, -.3f * Math.abs(wave(t, 1.4f)), 0));
    }
    private DrCh2() {}
}
