package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.function.Consumer;

import static com.undeltarune.chars.drlate.DrKit.*;

/**
 * Deltarune Chapter 5 "Festival Day" (released 2026-06-24, confirmed on deltarune.com and toby.fangamer.com):
 * Flower Kingdom enemies, the seven colored flowers, the Shadow Crystal boss Pink and the Trashy Trio dojo challenge.
 * Attack lists follow deltarune.wiki (checked 2026-11); one Attack = one enemy turn.
 * Factions: no Flower Kingdom faction exists, so everyone is NEUTRAL faction (no infighting) and HOSTILE to the player
 * as in their battles (same convention as DrCh3Ch4).
 * Not added: Pink's "date" turns and Flowery's climbing "Ascension" (menu / minigame segments, no bullets),
 * Chapter 6+ (not released as of 2026-11).
 */
public final class DrCh5Plus {
    static final int WHITE = 0xffffff, RED = 0xff3030, PETAL = 0xffd84a, LEAF = 0x5fbf4a, KNIFE = 0x9ff4ee, PINK = 0xff7ab8;

    public static void register(List<CharacterDef> out) {
        // ---------- Flower Kingdom ----------
        out.add(floradinn()); out.add(sheary()); out.add(netskie()); out.add(shi()); out.add(kawkaw()); out.add(leafling());
        out.add(terakota()); out.add(shinobeetle());
        // ---------- Seven colored flowers ----------
        out.add(aqua()); out.add(seth()); out.add(orange()); out.add(green()); out.add(blue()); out.add(yellow());
        out.add(pink()); out.add(flowery());
        // ---------- Castle Town dojo challenge ----------
        out.add(trashyTrio());
    }

    static CharacterDef.Builder fk(String id, String name) { return CharacterDef.of(id, name, Faction.NEUTRAL).att(Attitude.HOSTILE); }

    // ---- local helpers: bullets tagged by color act as simple state ----
    static List<UtProjectile> mine(AttackCtx c, int rgb) {
        return c.level.getEntitiesOfClass(UtProjectile.class, new AABB(c.pos(), c.pos()).inflate(40), p -> p.ownerId == c.me.getId() && p.color() == rgb);
    }
    static void each(AttackCtx c, int rgb, Consumer<UtProjectile> f) { for (UtProjectile p : mine(c, rgb)) f.accept(p); }
    /** Bullets of this color that reach the end of their life burst: f gets their last position. */
    static void pop(AttackCtx c, int rgb, Consumer<Vec3> f) {
        for (UtProjectile p : mine(c, rgb)) if (p.age() >= p.maxLife() - 1) { Vec3 at = p.position(); p.discard(); f.accept(at); }
    }
    /** Any bullet of color trig touching a bullet of color tgt detonates the latter. */
    static void detonate(AttackCtx c, int trig, int tgt, double r, Consumer<Vec3> f) {
        List<UtProjectile> ts = mine(c, trig);
        if (ts.isEmpty()) return;
        for (UtProjectile p : mine(c, tgt)) for (UtProjectile q : ts)
            if (q.position().distanceTo(p.position()) < r) { Vec3 at = p.position(); p.discard(); f.accept(at); break; }
    }
    /** Short-lived blades spinning around every anchor bullet (Devilsknife-style). */
    static void orbit(AttackCtx c, int anchor, int blade, int n, double r, double spin, int t, float dmg) {
        if (t % 2 != 0) return;
        each(c, anchor, p -> { if (p.age() < 10) return;
            for (int k = 0; k < n; k++) { double a = t * spin + k * Math.PI * 2 / n;
                c.proj().at(p.position().add(Math.cos(a) * r, 0, Math.sin(a) * r)).face(new Vec3(-Math.sin(a), 0, Math.cos(a)))
                    .box(.25f, .25f, 1.1f).color(blade).dmg(dmg).life(2).spawn(); } });
    }
    /** Pink heart pickups (Pink's fight): touching one heals 1. */
    static void hearts(AttackCtx c, int rgb) {
        each(c, rgb, p -> { if (p.position().distanceTo(c.tpos()) < 1) { c.target.heal(1); fx(c, ParticleTypes.HEART, p.position(), 3, .2); p.discard(); } });
    }
    static Vec3 arcVel(Vec3 from, Vec3 to, int n, float g) {
        Vec3 d = to.subtract(from); return new Vec3(d.x / n, (d.y + g * n * (n + 1) / 2) / n, d.z / n);
    }
    static boolean moving(AttackCtx c) { return c.target.getDeltaMovement().horizontalDistanceSqr() > .01; }

    // =====================================================================================================
    // FLORADINN - "Rudinn of Chapter 5": green slug body, diamond face with yellow petals, yellow sword
    // =====================================================================================================
    static final int MANE = 0xffd84b, MANE_P = 0xffe36b, TRI = 0xf6f0fe;
    static CharacterDef floradinn() {
        ModelDef m = hum(3, 9, 6, 6, 0x9be36a, 0x9be36a, 0x9be36a, 0x7cc552)
            .box("spot1", "body", -1.5f, 1, -1.9f, 1.4f, 1.4f, .3f, 0xff8ac8).box("spot2", "body", 1.6f, -2, -1.9f, 1.2f, 1.2f, .3f, 0xff8ac8)
            .box("petN", "head", 0, 4, 0, 2.4f, 2, 1.4f, PETAL).box("petS", "head", 0, -4, 0, 2.4f, 2, 1.4f, PETAL)
            .box("petW", "head", -4, 0, 0, 2, 2.4f, 1.4f, PETAL).box("petE", "head", 4, 0, 0, 2, 2.4f, 1.4f, PETAL)
            .glow("mouth", "head", 0, -1.6f, -3.15f, 2.6f, .5f, .3f, 0xff4fa0)
            .box("blade", "armR", 0, -7, -1.5f, .5f, 7, 1.2f, PETAL).box("pommel", "armR", 0, -3.2f, -1.5f, 1, 1, 1, 0x2f8a2f);
        m = eyes(m, 6, .6f, 1.3f, 1.2f, 1.4f, 0xff4fa0);
        return fk("floradinn", "Floradinn").hp(46).egg(0x9be36a, PETAL).size(.7f, 1.7f).keep(6).model(m).attacks(flMane(), flTriangle()).build();
    }
    static Attack flMane() {
        return atk("floradinn_mane_thorn", 96, (c, t) -> {
            if (t >= 8 && t <= 62 && (t - 8) % 18 == 0) {
                Vec3 land = c.target.position().add(c.rnd.nextGaussian() * 1.3, .2, c.rnd.nextGaussian() * 1.3), from = c.eye().add(0, .7, 0);
                c.proj().at(from).vel(arcVel(from, land, 20, .03f)).box(1.1f, .5f, 1.1f).color(MANE).dmg(4).gravity(.03f).life(20).spawn();
                prop(c, land.add(0, -.15, 0), toT(c), 2.2f, .05f, 2.2f, RED, 20);
                c.sound(SoundEvents.CHERRY_LEAVES_BREAK, 1, 1.2f);
            }
            pop(c, MANE, at -> { ring(c, at, 10, .22, .3f, MANE_P, 3, c.rnd.nextDouble() * 6, 0, 28);
                fx(c, ParticleTypes.CHERRY_LEAVES, at, 12, .6); c.sound(SoundEvents.CHERRY_LEAVES_BREAK, 1, .8f); });
        }, (p, t, m) -> { throwR(p, (t + 16) % 18, 6, 17); p.r("petN", 0, 0, sin(t, .4f) * .3f); });
    }
    static Attack flTriangle() {
        return atk("floradinn_homing_triangle", 80, (c, t) -> {
            if (t == 10 || t == 34) {
                Vec3 f = toT(c), r = side(f);
                for (int k = -2; k <= 2; k++) { Vec3 at = c.tpos().add(0, 3.2, 0).add(r.scale(k * 1.1)).add(f.scale(-1.5));
                    c.proj().at(at).face(c.tpos().subtract(at)).box(.35f, .35f, 1).color(TRI).dmg(4).warn(14).life(56).spawn(); }
                c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, 1.6f);
            }
            if (t == 24 || t == 48) { retarget(c, TRI, .55); c.sound(SoundEvents.ARROW_SHOOT, 1, 1.3f); }
        }, (p, t, m) -> { pointLoop(p, t, "armR", 24); p.r("armL", -.5f, 0, .4f); });
    }

    // =====================================================================================================
    // SHEARY - tall black-and-white scissors dancer, bladed arms fanned out
    // =====================================================================================================
    static final int SCIS = 0xd8d8da, SCIS_B = 0xe2e2e4;
    static CharacterDef sheary() {
        ModelDef m = hum(9, 10, 4, 5, 0x161616, WHITE, 0xe8e8e8, 0x161616)
            .box("bladeL", "armL", 0, -7, 0, .6f, 8, 2, 0xe8e8e8).box("bladeR", "armR", 0, -7, 0, .6f, 8, 2, 0xe8e8e8)
            .box("pivot", "body", 0, 5, -1.6f, 1.6f, 1.6f, .6f, 0x888888);
        m = eyes(m, 5, .5f, 1, .8f, .8f, 0x161616);
        return fk("sheary", "Sheary").hp(44).egg(0x161616, WHITE).size(.6f, 2.4f).keep(6).model(m).attacks(shScissor()).build();
    }
    static Attack shScissor() {
        return atk("sheary_scissor_attack", 120, (c, t) -> {
            Vec3 f = toT(c), r = side(f), tp = c.tpos();
            if (t == 6) { prop(c, tp.add(r.scale(5)).add(0, 1.4, 0), f, .4f, 2.6f, 1.2f, SCIS, 16); c.sound(SoundEvents.SHEEP_SHEAR, 1, .7f); }
            if (t == 12) { line(c, tp, f, 14, .25f, WHITE, 5, 10, 6); }
            if (t == 22) { c.sound(SoundEvents.SHEEP_SHEAR, 1, 1.2f); fx(c, ParticleTypes.SWEEP_ATTACK, tp, 6, 1); }
            if (t >= 32 && t <= 92 && (t - 32) % 20 == 0) {
                int g = (t - 32) / 20, s = g % 2 == 0 ? 1 : -1;
                for (int k = -1; k <= 1; k++)
                    bolt(c, tp.add(r.scale(k * 1.8)).add(0, 5, 0), new Vec3(0, -.3, 0).add(r.scale(k * .08 * s)), .2f, .9f, SCIS, 3, 5, 30);
                Vec3 big = tp.add(r.scale(-7 * s)).add(0, g < 2 ? -.6 : .9, 0);
                c.proj().at(big).vel(r.scale(.32 * s)).box(.5f, .6f, 2.6f).color(SCIS_B).dmg(5).warn(6).life(46).spawn();
                c.sound(SoundEvents.SHEEP_SHEAR, .8f, 1 + g * .1f);
            }
        }, (p, t, m) -> { float open = .3f + .5f * Math.abs(sin(t, .35f));
            p.r("armL", -2.6f, 0, open).r("armR", -2.6f, 0, -open).r("body", 0, t * .15f, 0); });
    }

    // =====================================================================================================
    // NETSKIE - wooden fox decoy with a nine-leaf purple tail
    // =====================================================================================================
    static final int PAW = 0x7a4ad0, PAW_B = 0xc8a0ff, FAKE = 0xb08ae0;
    static CharacterDef netskie() {
        ModelDef m = hum(5, 7, 5, 5, 0xa8743a, 0xa8743a, 0x8a5a2a, 0x8a5a2a)
            .box("earL", "head", -1.6f, 3.4f, 0, 1.2f, 2, 1, 0xa8743a).box("earR", "head", 1.6f, 3.4f, 0, 1.2f, 2, 1, 0xa8743a)
            .box("snout", "head", 0, -.8f, -3.2f, 2, 1.6f, 2, 0xc89050).glow("grin", "head", 0, -1.4f, -4.25f, 1.8f, .4f, .2f, WHITE)
            .box("tail", "body", 0, 2, 4, 9, 9, 3, 0x8a4ad0).box("tailTip", "tail", 0, 4, 0, 6, 3, 2.6f, 0xb07ae8);
        m = eyes(m, 5, .6f, 1.1f, .9f, .4f, 0x1a0a00);
        return fk("netskie", "Netskie").hp(70).egg(0xa8743a, 0x8a4ad0).size(.7f, 1.5f).keep(6).random()
            .model(m).attacks(nkPaw(), nkFakeBounce(), nkFakeTommy(), nkFakeTriangle()).build();
    }
    static Attack nkPaw() {
        return atk("netskie_paw_stamp", 100, (c, t) -> {
            if (t >= 8 && t < 86 && (t - 8) % 24 == 0) {
                Vec3 at = c.target.position();
                for (int i = 1; i <= 6; i++) prop(c, c.pos().add(at.subtract(c.pos()).scale(i / 7.)).add(0, .05, 0), toT(c), .35f, .05f, .35f, PAW_B, 14);
                prop(c, at.add(0, .05, 0), toT(c), 2, .05f, 2, PAW, 13);
                c.proj().at(at.add(0, 5.2, 0)).vel(new Vec3(0, -.42, 0)).box(1.8f, .9f, 1.8f).color(PAW).dmg(5).warn(10).life(12).spawn();
                c.sound(SoundEvents.FOX_SNIFF, 1, 1);
            }
            pop(c, PAW, at -> { for (int k = 0; k < 8; k++) { double a = k * Math.PI / 4;
                    bolt(c, at.add(0, .5, 0), new Vec3(Math.cos(a), 0, Math.sin(a)).scale(.24), .25f, .4f, PAW_B, 3, 0, 30); }
                fx(c, ParticleTypes.POOF, at, 10, .5); c.sound(SoundEvents.RAVAGER_STEP, 1, 1.4f); });
        }, (p, t, m) -> { float k = ((t + 16) % 24) / 24f; p.r("tail", -.5f * env(k, 0, .4f, .5f, .7f), 0, 0).r("armR", -1.4f * env(k, 0, .4f, .5f, .7f), 0, 0); });
    }
    /** Faker: Rabbick's Bounce with mismatched rabbits. */
    static Attack nkFakeBounce() {
        return atk("netskie_faker_bounce", 90, (c, t) -> {
            if (t >= 8 && t < 72 && t % 12 == 8) { Vec3 r = side(toT(c)); int s = t % 24 == 8 ? 1 : -1;
                Vec3 at = c.target.position().add(r.scale(6 * s)).add(0, .3, 0);
                c.proj().at(at).vel(r.scale(-.2 * s).add(0, .3, 0)).box(.7f, .8f, .7f).color(FAKE).dmg(3).gravity(.03f).life(60).spawn();
                c.sound(SoundEvents.RABBIT_JUMP, 1, 1.3f); }
            each(c, FAKE, p -> { if (p.getY() < c.target.getY() && p.getDeltaMovement().y < 0) p.setDeltaMovement(p.getDeltaMovement().multiply(1, -1, 1)); });
        }, (p, t, m) -> { hop(p, t, 12, 1.2f); p.r("tail", 0, sin(t, .4f) * .6f, 0); });
    }
    /** Faker: Shadowguy's Tommy Gun with oddly-shaped bullets. */
    static Attack nkFakeTommy() {
        return atk("netskie_faker_tommy", 80, (c, t) -> {
            if (t >= 14 && t < 64 && t % 3 == 0) { Vec3 at = c.eye().add(toT(c).scale(1.1));
                float w = .15f + (t % 9) * .06f;
                bolt(c, at, rot(c.aim(at), Math.sin(t * .2) * .45).scale(.75), w, .6f - w, 0xffe0a0, 2.5f, 0, 30);
                if (t % 6 == 0) c.sound(SoundEvents.SKELETON_SHOOT, .6f, 1.7f); }
        }, (p, t, m) -> { float s = ramp(t, 0, 10); p.r("armR", -1.5f * s, .3f, 0).r("armL", -1.4f * s, -.4f, 0).r("tail", 0, sin(t, 1.5f) * .2f, 0); });
    }
    /** Faker: Floradinn's Homing Triangles in mismatched shapes. */
    static Attack nkFakeTriangle() {
        return atk("netskie_faker_triangles", 80, (c, t) -> {
            if (t == 10 || t == 34) { Vec3 f = toT(c), r = side(f);
                for (int k = -2; k <= 2; k++) { Vec3 at = c.tpos().add(0, 3.2, 0).add(r.scale(k * 1.2)).add(f.scale(-1.5)); float l = .5f + (k + 2) * .25f;
                    c.proj().at(at).face(c.tpos().subtract(at)).box(.3f + .1f * Math.abs(k), .3f, l).color(0xd8c8ff).dmg(4).warn(14).life(56).spawn(); }
                c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, 1.2f); }
            if (t == 24 || t == 48) retarget(c, 0xd8c8ff, .5);
        }, (p, t, m) -> { pointLoop(p, t, "armR", 24); p.r("tail", sin(t, .3f) * .3f, 0, 0); });
    }

    // =====================================================================================================
    // SHI - T-posing scarecrow (士): brown head, red triangular body, green scarf, pointed red hat
    // =====================================================================================================
    static final int SQ = 0xc04030, SCY = 0xe0e0e0, CROW = 0xc23a2a, SPR0 = 0xb83224, SPR1 = 0xb83225, SPR2 = 0xb83226;
    static CharacterDef shi() {
        ModelDef m = hum(7, 8, 7, 5, 0xc23a2a, 0x8a5a2a, 0xc8a050, 0x6a4a2a)
            .box("hat", "head", 0, 3.6f, 0, 3, 3, 3, 0xc23a2a).box("brim", "head", 0, 2.6f, 0, 6.4f, .5f, 6.4f, 0xc23a2a)
            .box("scarf", "body", 0, 4.2f, 0, 7.6f, 1.4f, 4.2f, 0x3aa04a).box("scarfTail", "scarf", 3, -2, 1.8f, 1.4f, 4, .4f, 0x3aa04a)
            .box("pole", "body", 0, -5, 0, 1, 6, 1, 0x6a4a2a);
        m = eyes(m, 5, .4f, 1, .7f, .7f, 0x1a0a00);
        return fk("shi", "Shi").hp(44).egg(0xc23a2a, 0x3aa04a).size(.7f, 1.8f).keep(6).speed(.2f)
            .model(m).attacks(shiScythe(), shiEasy(), shiSpring()).build();
    }
    static Attack shiScythe() {
        return atk("shi_scythe_bomb", 110, (c, t) -> {
            if (t >= 8 && t <= 56 && (t - 8) % 16 == 0) { Vec3 from = c.eye(), to = c.target.position().add(c.rnd.nextGaussian() * 2, 1, c.rnd.nextGaussian() * 2);
                c.proj().at(from).vel(arcVel(from, to, 12, .03f)).box(.7f, .7f, .7f).color(SQ).dmg(3).gravity(.03f).life(66).spawn();
                c.sound(SoundEvents.WITCH_THROW, 1, 1); }
            each(c, SQ, p -> { if (p.age() == 12) { p.setDeltaMovement(Vec3.ZERO); p.grav = 0; fx(c, ParticleTypes.CRIT, p.position(), 8, .4); c.sound(SoundEvents.CHAIN_PLACE, .7f, 1.4f); } });
            orbit(c, SQ, SCY, 4, 1.6, .22, t, 4);
        }, (p, t, m) -> { float k = ((t + 8) % 16) / 16f; p.r("armR", -2 * env(k, 0, .3f, .4f, .8f), 0, -1.5f).r("armL", 0, 0, 1.5f).r("scarfTail", sin(t, .5f) * .4f, 0, 0); });
    }
    static Attack shiEasy() {
        return atk("shi_easy_attack", 80, (c, t) -> {
            if (t == 6) { Vec3 f = toT(c); int s = c.rnd.nextBoolean() ? 1 : -1;
                c.proj().at(c.target.position().add(side(f).scale(2.6 * s)).add(0, .9, 0)).box(.6f, 1.8f, .6f).color(CROW).dmg(3).life(70).pierce().spawn();
                fx(c, ParticleTypes.POOF, c.target.position(), 6, 1); }
            each(c, CROW, p -> { boolean near = p.position().distanceTo(c.tpos()) < 2.4;
                p.setDeltaMovement(near ? new Vec3((t % 2 == 0 ? 1 : -1) * .12, 0, 0) : Vec3.ZERO); if (near && t % 6 == 0) c.sound(SoundEvents.WOOD_HIT, .6f, 1.5f); });
        }, (p, t, m) -> p.r("body", 0, 0, sin(t, .3f) * .15f).r("armL", 0, 0, 1.5f).r("armR", 0, 0, -1.5f));
    }
    /** Shi Spring (Top of Castle, 3 Shis + Seth): stacked scarecrows on a moving spring, bouncing apart. */
    static Attack shiSpring() {
        int[] tags = {SPR0, SPR1, SPR2}; double[] vy = {.18, .3, .42};
        return atk("shi_spring", 120, (c, t) -> {
            Vec3 r = side(toT(c));
            if (t == 8) { for (int k = 0; k < 3; k++)
                    c.proj().at(c.target.position().add(r.scale(-7)).add(0, .6 + k * 1.2, 0)).vel(r.scale(.14)).box(.8f, 1.2f, .8f).color(tags[k]).dmg(4).warn(6).life(104).spawn();
                c.proj().at(c.target.position().add(r.scale(-7)).add(0, .1, 0)).vel(r.scale(.14)).box(1, .2f, 1).color(0x999999).dmg(0).life(104).spawn(); }
            for (int b : new int[]{30, 60, 90}) {
                if (t == b) { for (int k = 0; k < 3; k++) { double v = vy[k];
                        each(c, tags[k], p -> { p.setDeltaMovement(p.getDeltaMovement().x, v, p.getDeltaMovement().z); p.grav = (float) (2 * v / 21); }); }
                    c.sound(SoundEvents.SLIME_JUMP, 1, 1.2f); }
                if (t == b + 20) for (int tag : tags) each(c, tag, p -> { p.setDeltaMovement(p.getDeltaMovement().x, 0, p.getDeltaMovement().z); p.grav = 0; });
            }
        }, (p, t, m) -> { hop(p, t, 30, 1.5f); p.r("armL", 0, 0, 1.5f).r("armR", 0, 0, -1.5f); });
    }

    // =====================================================================================================
    // KAWKAW - conical crow head on a pinecone body, long woodpecker tongue
    // =====================================================================================================
    static final int FEATH = 0x2a2a3a, STAR5 = 0xfff27b;
    static CharacterDef kawkaw() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 4, 0, 6, 8, 6, 0x8a5a2a).box("scale1", "body", 0, 1, -3.1f, 6.4f, 1, .4f, 0x6a4020).box("scale2", "body", 0, -2, -3.1f, 6.4f, 1, .4f, 0x6a4020)
            .box("head", "body", 0, 6, 0, 5, 4, 5, 0x1e1e2a).box("beak", "head", 0, -.5f, -3.5f, 2, 1.4f, 2.4f, 0xe0b030)
            .box("tongue", "beak", 0, -.8f, -1.2f, .6f, .3f, 2, 0xff6a8a)
            .limb("legL", null, -1.5f, 1, 0, 1, 1, 1, 0xe0b030).limb("legR", null, 1.5f, 1, 0, 1, 1, 1, 0xe0b030);
        m = eyes(m, 5, .6f, 1.2f, .8f, .8f, 0xfff0a0);
        return fk("kawkaw", "Kawkaw").hp(44).egg(0x1e1e2a, 0x8a5a2a).size(.6f, 1.1f).keep(5).model(m).attacks(kwFeathers(), kwPchoo()).build();
    }
    static Attack kwFeathers() {
        return atk("kawkaw_falling_feathers", 100, (c, t) -> {
            if (t >= 6 && t < 80 && t % 5 == 0) {
                Vec3 at = c.target.position().add((c.rnd.nextDouble() * 2 - 1) * 3.5, 6, (c.rnd.nextDouble() * 2 - 1) * 3.5);
                c.proj().at(at).vel(new Vec3(0, -.07, 0)).box(.15f, .1f, .8f).color(FEATH).dmg(3).warn(4).life(95).spawn();
                if (t % 15 == 0) c.sound(SoundEvents.PARROT_FLY, .6f, .8f);
            }
            Vec3 r = side(toT(c));
            each(c, FEATH, p -> p.setDeltaMovement(r.scale(Math.sin(p.age() * .15) * .12).add(0, -.07, 0)));
        }, (p, t, m) -> { p.r("head", -.3f + sin(t, .5f) * .2f, 0, 0); hop(p, t, 10, .6f); });
    }
    static Attack kwPchoo() {
        return atk("kawkaw_pchoo", 84, (c, t) -> {
            if (t >= 12 && t < 72 && t % 15 == 12) { Vec3 at = c.eye().add(toT(c).scale(.8));
                fan(c, at, c.aim(at), 3, .5, .32, .4f, .4f, STAR5, 3, 2, 40);
                fx(c, ParticleTypes.END_ROD, at, 4, .2); c.sound(SoundEvents.PARROT_AMBIENT, 1, 1.5f); }
        }, (p, t, m) -> { float k = ((t + 3) % 15) / 15f, lick = env(k, 0, .4f, .6f, .9f);
            p.o("tongue", 0, 0, -2.5f * lick).r("head", .4f * lick - .2f, 0, 0); });
    }

    // =====================================================================================================
    // LEAFLING - four leaves (head, body, ears) held together by twigs
    // =====================================================================================================
    static final int BURST = 0x6fcf55, WIND = 0x7fd860;
    static CharacterDef leafling() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 6, 0, 5, 6, 1, LEAF).box("vein", "body", 0, 0, -.6f, .4f, 6, .2f, 0x3a8a2a)
            .box("head", "body", 0, 5.5f, 0, 5, 5, 1, 0x6fcf55).box("earL", "head", -2.5f, 3, 0, 2, 3, .6f, LEAF).box("earR", "head", 2.5f, 3, 0, 2, 3, .6f, LEAF)
            .box("twig", "body", 0, 0, .7f, .5f, 14, .5f, 0x7a5a30)
            .limb("legL", null, -1.2f, 3, 0, .6f, 3, .6f, 0x7a5a30).limb("legR", null, 1.2f, 3, 0, .6f, 3, .6f, 0x7a5a30)
            .limb("armL", "body", -3, 2, 0, .5f, 3, .5f, 0x7a5a30).limb("armR", "body", 3, 2, 0, .5f, 3, .5f, 0x7a5a30);
        m = eyes(m, 1, .5f, 1, .6f, .6f, 0x102010);
        return fk("leafling", "Leafling").hp(44).egg(LEAF, 0x7a5a30).size(.6f, 1.4f).keep(6).model(m).attacks(lfBurst(), lfWind()).build();
    }
    static Attack lfBurst() {
        return atk("leafling_petal_burst", 96, (c, t) -> {
            if (t >= 8 && t <= 68 && (t - 8) % 30 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 1.6, 0, c.rnd.nextGaussian() * 1.6);
                for (int k = 0; k < 12; k++) { double a = k * Math.PI / 6; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                    c.proj().at(at.add(d.scale(.7))).vel(d.scale(.005)).box(.3f, .1f, .5f).color(BURST).dmg(3).warn(12).life(32).spawn(); }
                fx(c, ParticleTypes.COMPOSTER, at, 10, .6); c.sound(SoundEvents.AZALEA_LEAVES_PLACE, 1, 1);
            }
            each(c, BURST, p -> { if (p.age() == 12) { p.setDeltaMovement(p.getDeltaMovement().normalize().scale(.26)); c.sound(SoundEvents.PHANTOM_FLAP, .3f, 2); } });
        }, (p, t, m) -> { float k = ((t + 22) % 30) / 30f; raise(p, env(k, 0, .3f, .5f, .8f) * .7f); p.r("earL", 0, 0, sin(t, .4f) * .3f).r("earR", 0, 0, -sin(t, .4f) * .3f); });
    }
    static Attack lfWind() {
        return atk("leafling_petal_wind", 110, (c, t) -> {
            Vec3 r = side(toT(c)); int s = t < 55 ? 1 : -1;
            if (t >= 8 && t < 100 && t % 4 == 0) {
                Vec3 at = c.tpos().add(r.scale(-7 * s)).add(toT(c).scale(c.rnd.nextGaussian() * 2)).add(0, c.rnd.nextGaussian() * .8, 0);
                c.proj().at(at).vel(r.scale(.24 * s).add(0, Math.sin(t * .1) * .06, 0)).box(.3f, .1f, .5f).color(WIND).dmg(3).life(60).spawn();
            }
            boolean gust = (t % 40) > 22 && (t % 40) < 36;
            if (gust) { c.target.push(r.x * .035 * s, 0, r.z * .035 * s); c.target.hurtMarked = true;
                if (t % 4 == 0) prop(c, c.tpos().add(r.scale(-2.5 * s)).add(0, c.rnd.nextGaussian(), 0), r, .06f, .06f, 2, 0xb0b0b0, 6); }
            if (t % 20 == 0) c.sound(SoundEvents.ELYTRA_FLYING, .3f, 1.6f);
        }, (p, t, m) -> { sway(p, t, .35f); p.r("armL", 0, 0, .8f + sin(t, .3f) * .3f).r("armR", 0, 0, -.8f - sin(t, .3f) * .3f); });
    }

    // =====================================================================================================
    // TERAKOTA - towering knight armored in flower pots, shovel in small clay hands
    // =====================================================================================================
    static final int CLAY = 0xc0643a, PINE = 0x2f7a3a, LEAFC = 0x7fd860, LDROP = 0x9fe080, BUD = 0xff9ac8, BUD_P = 0xffb0d8;
    static CharacterDef terakota() {
        ModelDef m = hum(7, 11, 10, 8, CLAY, CLAY, 0xd88a5a, 0xa04a2a)
            .box("potRim", "body", 0, 5.5f, 0, 11, 1.4f, 7, 0xa04a2a).box("headRim", "head", 0, 4, 0, 9, 1, 9, 0xa04a2a)
            .box("sprout", "head", 0, 5.5f, 0, .8f, 2.5f, .8f, LEAF).box("leaf", "sprout", 1, 1, 0, 1.6f, .6f, 1, LEAF)
            .glow("slit", "head", 0, .5f, -4.15f, 4, 2, .3f, 0x1a0a00)
            .box("shovel", "armR", 0, -9, -1, .5f, 12, .5f, 0x7a5a30).box("spade", "armR", 0, -15, -1, 2.4f, 3, .4f, 0x9a9aa0);
        m = eyes(m, 8, .5f, 1, .8f, .8f, 0xffe04a);
        return fk("terakota", "Terakota").hp(80).egg(CLAY, LEAF).size(1, 2.4f).keep(6).speed(.15f)
            .model(m).attacks(tkPine(), tkLeaf(), tkFlower()).build();
    }
    static Vec3 pot(AttackCtx c, int i) { return c.target.position().add(side(toT(c)).scale((i - 2) * 1.8)).add(toT(c).scale(1.5)); }
    static void pots(AttackCtx c, int t) { if (t == 2) for (int i = 0; i < 5; i++) prop(c, pot(c, i).add(0, .3, 0), toT(c), .8f, .6f, .8f, CLAY, 100); }
    static Attack tkPine() {
        return atk("terakota_pine_tree", 100, (c, t) -> {
            pots(c, t);
            if (t >= 10 && t < 90 && t % 14 == 10) { Vec3 b = pot(c, c.rnd.nextInt(5));
                prop(c, b.add(0, .8, 0), toT(c), .3f, .6f, .3f, LEAF, 12);
                pillar(c, b, 1, 5, PINE, 5, 12, 10); c.sound(SoundEvents.CHORUS_FLOWER_GROW, 1, 1.2f); }
        }, (p, t, m) -> chop(p, (t + 4) % 14 * 1.5f, 10, 20));
    }
    static Attack tkLeaf() {
        return atk("terakota_leaf_creature", 100, (c, t) -> {
            pots(c, t);
            if (t >= 10 && t < 80 && t % 18 == 10) { Vec3 b = pot(c, c.rnd.nextInt(5)).add(0, .6, 0);
                c.proj().at(b).vel(new Vec3(0, .45, 0)).box(.7f, .4f, .7f).color(LEAFC).dmg(4).gravity(.02f).life(40).spawn();
                c.sound(SoundEvents.FROG_LONG_JUMP, 1, 1.4f); }
            if (t % 4 == 0) each(c, LEAFC, p -> { if (p.age() > 8) c.proj().at(p.position()).vel(new Vec3(c.rnd.nextGaussian() * .03, -.1, c.rnd.nextGaussian() * .03))
                    .box(.25f, .25f, .25f).color(LDROP).dmg(3).gravity(.01f).life(40).spawn(); });
        }, (p, t, m) -> { p.r("armL", -1.2f, 0, .3f).r("armR", -.8f + sin(t, .3f) * .3f, 0, -.3f); hop(p, t, 18, .8f); });
    }
    static Attack tkFlower() {
        return atk("terakota_flower_split", 100, (c, t) -> {
            pots(c, t);
            if (t >= 10 && t < 82 && t % 16 == 10) { Vec3 b = pot(c, c.rnd.nextInt(5)).add(0, .6, 0);
                c.proj().at(b).vel(new Vec3(0, .16, 0)).box(.8f, .8f, .8f).color(BUD).dmg(3).life(16).spawn(); c.sound(SoundEvents.CHORUS_FLOWER_GROW, 1, 1.6f); }
            pop(c, BUD, at -> { ring(c, at, 5, .2, .35f, BUD_P, 3, c.rnd.nextDouble() * 6, 0, 30);
                fx(c, ParticleTypes.CHERRY_LEAVES, at, 8, .4); c.sound(SoundEvents.CHERRY_LEAVES_BREAK, 1, 1.3f); });
        }, (p, t, m) -> { float k = ((t + 6) % 16) / 16f; p.r("armR", -1.2f * env(k, 0, .3f, .4f, .8f), 0, 0).r("sprout", 0, t * .2f, 0); });
    }

    // =====================================================================================================
    // SHINOBEETLE - stag-beetle ninja: bluish gray, yellow horn tips and eyes, white obi, sword
    // =====================================================================================================
    static final int ROACH = 0xa02a2a, SHUR = 0xd0d0e0, MIGO = 0x3a2a2a;
    static CharacterDef shinobeetle() {
        ModelDef m = hum(6, 9, 7, 6, 0x5a6a7a, 0x5a6a7a, 0x5a6a7a, 0x4a5a6a)
            .box("hornL", "head", -1.8f, 4, -1.5f, 1, 4, 1, 0x5a6a7a).box("hornR", "head", 1.8f, 4, -1.5f, 1, 4, 1, 0x5a6a7a)
            .box("tipL", "hornL", 0, 2.4f, 0, 1.1f, 1, 1.1f, 0xffd84a).box("tipR", "hornR", 0, 2.4f, 0, 1.1f, 1, 1.1f, 0xffd84a)
            .box("obi", "body", 0, -2, 0, 7.4f, 1.6f, 4.6f, WHITE).box("obijime", "obi", 0, 0, -2.4f, 7.6f, .4f, .3f, 0x1a1a1a)
            .box("sword", "armR", 0, -7, -1, .4f, 8, .8f, 0xc0c0c8);
        m = eyes(m, 6, .4f, 1.3f, 1, .6f, 0xffd84a);
        return fk("shinobeetle", "Shinobeetle").hp(46).egg(0x5a6a7a, 0xffd84a).size(.7f, 1.9f).keep(6)
            .model(m).attacks(sbShuriken(), sbMigosp(), sbDance()).build();
    }
    static Attack sbShuriken() {
        return atk("shinobeetle_jumping_shuriken", 96, (c, t) -> {
            if (t >= 8 && t < 76 && t % 14 == 8) { Vec3 r = side(toT(c)); int s = t % 28 == 8 ? 1 : -1;
                Vec3 from = c.target.position().add(r.scale(5 * s)), to = c.tpos().add(r.scale(2 * s)).add(0, 1.5, 0);
                c.proj().at(from).vel(arcVel(from, to, 14, .03f)).box(.5f, .3f, .7f).color(ROACH).dmg(3).gravity(.03f).life(14).spawn();
                c.sound(SoundEvents.SILVERFISH_AMBIENT, 1, 1.4f); }
            pop(c, ROACH, at -> { fan(c, at, c.aim(at), 3, .8, .3, .45f, .45f, SHUR, 3, 0, 30); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .7f, 1.8f); });
        }, (p, t, m) -> { float k = ((t + 6) % 14) / 14f; throwR(p, k * 14, 5, 13); p.r("hornL", 0, 0, .1f).r("hornR", 0, 0, -.1f); });
    }
    static Attack sbMigosp() {
        return atk("shinobeetle_migosp_roach", 96, (c, t) -> {
            if (t >= 8 && t < 84 && t % 3 == 0) { Vec3 f = toT(c), r = side(f);
                double off = 1.4 + c.rnd.nextDouble() * 2.6, along = c.rnd.nextGaussian() * 1.2;
                c.proj().at(c.target.position().add(r.scale(-off)).add(f.scale(along)).add(0, -.5, 0)).vel(new Vec3(0, .28, 0)).box(.4f, .5f, .3f).color(MIGO).dmg(3).life(26).spawn();
                c.proj().at(c.target.position().add(r.scale(off)).add(f.scale(-along)).add(0, 6.5, 0)).vel(new Vec3(0, -.28, 0)).box(.4f, .5f, .3f).color(MIGO).dmg(3).life(26).spawn();
                if (t % 12 == 0) c.sound(SoundEvents.SILVERFISH_STEP, 1, 1.6f); }
        }, (p, t, m) -> { p.r("armL", -1.6f, 0, .5f).r("armR", -1.6f, 0, -.5f).r("body", 0, sin(t, .25f) * .3f, 0); });
    }
    /** Lone Shinobeetle: a harmless dancing roach (ends the turn in canon). */
    static Attack sbDance() {
        return phase("shinobeetle_dancing_beetle", 70, -1, .35f, (c, t) -> {
            if (t == 6) prop(c, c.target.position().add(toT(c).scale(-1.5)).add(0, .3, 0), toT(c), .5f, .6f, .4f, ROACH, 60);
            if (t % 10 == 0) fx(c, ParticleTypes.NOTE, c.target.position().add(0, 1, 0), 1, .3);
        }, (p, t, m) -> { hop(p, t, 10, .8f); p.r("armL", -2.5f, 0, sin(t, .6f) * .5f).r("armR", -2.5f, 0, -sin(t, .6f) * .5f); });
    }

    // =====================================================================================================
    // AQUA - aqua plant girl: dress, red shoes, bat-wing black hair, red heart ribbon, one to three eyes. Knives.
    // =====================================================================================================
    static final int KF = 0x9ff4ef, KC = 0x9ff4f0, KR = 0x9ff4f1, KO = 0x9ff4f2, DUCK = 0xffe04a, GPETAL = 0xb0b0b0;
    static CharacterDef aqua() {
        ModelDef m = hum(7, 8, 6, 6, 0x3fd8d0, 0x7fe8e0, 0x7fe8e0, 0x7fe8e0)
            .box("dress", "body", 0, -4.5f, 0, 9, 3, 6, 0x2fc0b8).box("shoeL", "legL", 0, -6.5f, -.4f, 2, 1, 3, 0xd02828).box("shoeR", "legR", 0, -6.5f, -.4f, 2, 1, 3, 0xd02828)
            .box("hair", "head", 0, 1, 1, 7, 6.6f, 6, 0x111111).box("wingL", "hair", -4.6f, 2, 0, 3, 3, 2, 0x111111).box("wingR", "hair", 4.6f, 2, 0, 3, 3, 2, 0x111111)
            .box("collar", "body", 0, 4, 0, 6.4f, .8f, 4.2f, 0xd02828).box("ribbon", "head", 0, 3.8f, -1, 2.4f, 1.6f, .6f, 0xd02828)
            .glow("eye2", "head", 0, 1.8f, -3.15f, .8f, .8f, .3f, 0x0a3a3a);
        m = eyes(m, 6, .3f, 1.3f, .8f, .8f, 0x0a3a3a);
        return fk("aqua", "Aqua").hp(150).boss().egg(0x3fd8d0, 0xd02828).size(.6f, 1.9f).keep(6).rest(18)
            .model(m).attacks(aqFan(), aqChain(), aqCircle(), aqDuck(), aqEverything(), aqOmega()).build();
    }
    static Attack aqFan() { return phase("aqua_knife_fan", 110, .25f, 2, DrCh5Plus::knifeFan, (p, t, m) -> throwR(p, (t + 6) % 13, 5, 12)); }
    static Attack aqChain() { return phase("aqua_knife_chain", 100, .25f, 2, DrCh5Plus::knifeChain, (p, t, m) -> { raise(p, .5f + .2f * sin(t, .45f)); p.o("body", sin(t, .22f) * 1.5f, 0, 0); }); }
    static Attack aqCircle() { return phase("aqua_knife_circle", 110, .25f, 2, DrCh5Plus::knifeCircle, (p, t, m) -> { pointLoop(p, t, "armR", 8); pointLoop(p, t + 4, "armL", 8); }); }
    static void knifeFan(AttackCtx c, int t) {
        if (t >= 6 && t < 96 && (t - 6) % 13 == 0) { Vec3 at = c.eye().add(toT(c).scale(.8));
            for (int k = 0; k < 4; k++) bolt(c, at, rot(toT(c), -.45 + k * .3).scale(.55).add(0, .04 * k - .06, 0), .15f, .8f, KF, 4, 2, 40);
            for (int k = 0; k < 3; k++) c.proj().at(c.eye().add(c.rnd.nextGaussian(), .5, c.rnd.nextGaussian())).vel(new Vec3(0, -.2, 0)).box(.3f, .05f, .3f).color(GPETAL).dmg(0).life(12).spawn();
            c.sound(SoundEvents.TRIDENT_THROW, .7f, 1.6f); }
        each(c, KF, p -> { if (p.age() == 16) { p.setDeltaMovement(c.tpos().subtract(p.position()).normalize().scale(.6)); c.sound(SoundEvents.TRIDENT_RETURN, .4f, 1.6f); } });
    }
    static void knifeChain(AttackCtx c, int t) {
        if (t >= 8 && t < 90 && (t - 8) % 14 == 0) { Vec3 f = toT(c), r = side(f); double wave = Math.sin(t * .3) * .08;
            for (int s = -1; s <= 1; s += 2) for (int k = 0; k < 8; k++) {
                Vec3 at = c.tpos().add(r.scale(5 * s)).add(f.scale((k - 3.5) * .7)).add(0, Math.sin(k * .8) * .5, 0);
                bolt(c, at, r.scale(-.22 * s).add(f.scale(wave * s)), .15f, .7f, KC, 4, 6, 40); }
            c.sound(SoundEvents.CHAIN_HIT, .8f, 1.5f); }
    }
    static void knifeCircle(AttackCtx c, int t) {
        if (t >= 8 && t < 96 && (t - 8) % 8 == 0) { Vec3 ctr = c.tpos().add((c.rnd.nextDouble() * 2 - 1) * 3.5, c.rnd.nextGaussian() * .4, (c.rnd.nextDouble() * 2 - 1) * 3.5);
            double ph = c.rnd.nextDouble() * 6;
            for (int k = 0; k < 10; k++) { double a = ph + k * Math.PI / 5; bolt(c, ctr, new Vec3(Math.cos(a), 0, Math.sin(a)).scale(.2), .15f, .7f, KR, 4, 10, 40); }
            fx(c, ParticleTypes.ENCHANTED_HIT, ctr, 6, .3); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, .8f, 1.8f); }
    }
    static Attack aqOmega() {
        return phase("aqua_omega_knife", 200, -1, .25f, (c, t) -> {
            if (t >= 8 && t < 170 && t % 6 == 0) { double a = t * .37; Vec3 at = around(c, 7, a, c.rnd.nextGaussian() * .6);
                bolt(c, at, new Vec3(-Math.sin(a), 0, Math.cos(a)).scale(.25).add(c.tpos().subtract(at).normalize().scale(.15)), .18f, .9f, KO, 5, 6, 90);
                if (t % 18 == 0) c.sound(SoundEvents.TRIDENT_RIPTIDE_1, .6f, 1.5f); }
            steer(c, KO, t < 170 ? .025 : 0, t < 170 ? .38 : .1);
        }, (p, t, m) -> { raise(p, ramp(t, 0, 12)); p.r("body", 0, t * .25f, 0); hover(p, t, 1.2f); });
    }
    /** Seth & Aqua's duck: slow, harmless-looking, always 1 damage; it falls cartoonishly at the end. */
    static Attack aqDuck() {
        return phase("aqua_duck", 90, .25f, .9f, (c, t) -> {
            if (t == 10) { Vec3 r = side(toT(c)); Vec3 at = c.tpos().add(r.scale(-6));
                c.proj().at(at).vel(r.scale(.14)).box(.7f, .6f, .9f).color(DUCK).dmg(1).life(80).spawn(); c.sound(SoundEvents.CHICKEN_AMBIENT, 1, 1.6f); }
            each(c, DUCK, p -> { if (p.age() == 60) { p.grav = .04f; c.sound(SoundEvents.CHICKEN_HURT, 1, 1.4f); } });
        }, (p, t, m) -> { clap(p, t, 30); clap(p, t, 50); });
    }
    /** Seth's "everything" order: fan + chain + circle at once (only after a no-hit first round). */
    static Attack aqEverything() {
        return phase("aqua_everything_attack", 120, .25f, .5f, (c, t) -> { knifeFan(c, t); if (t % 2 == 0) knifeChain(c, t + 6); knifeCircle(c, t + 4); },
            (p, t, m) -> { throwR(p, (t + 6) % 13, 5, 12); p.r("armL", -1.5f - sin(t, .5f) * .4f, 0, .3f); });
    }

    // =====================================================================================================
    // SETH - purple plant with glasses, magenta three-tuft hair, lavender tie. Knife-splitting glasses, eye mine, books.
    // =====================================================================================================
    static final int LENS = 0xd8f0ff, SK = 0x9ff4f3, SK2 = 0x9ff4f4, EYE = 0x9a9aa0, MINE = 0xd02020, WORD = 0xf0f0f0;
    static CharacterDef seth() {
        ModelDef m = hum(7, 9, 6, 6, 0x8a4ac0, 0xa070d0, 0xa070d0, 0x7a3ab0)
            .box("hair", "head", 0, 2.4f, .6f, 7, 3, 6.6f, 0xb02a7a).box("tuft1", "hair", -2, 2.4f, 0, 2, 2.4f, 2, 0xb02a7a)
            .box("tuft2", "hair", 0, 3, 0, 2, 3, 2, 0xb02a7a).box("tuft3", "hair", 2, 2.4f, 0, 2, 2.4f, 2, 0xb02a7a)
            .glow("glasses", "head", 0, .4f, -3.15f, 5.4f, 1.6f, .3f, 0xe8f4ff).box("tie", "body", 0, 1.5f, -1.9f, 1, 4, .4f, 0xc8a8f0)
            .box("dress", "body", -.5f, -5, 0, 7, 2.5f, 5, 0x7a3ab0).box("book", "armL", 0, -5, -1.2f, 2.5f, 3, .8f, 0x5a2a8a);
        return fk("seth", "Seth").hp(170).boss().egg(0x8a4ac0, 0xb02a7a).size(.6f, 1.9f).keep(7).rest(18)
            .model(m).attacks(sethGlasses(), sethEye(), sethBooks()).build();
    }
    static Attack sethGlasses() {
        return atk("seth_glasses_split", 120, (c, t) -> {
            Vec3 f = toT(c), r = side(f), mid = c.pos().add(c.tpos().subtract(c.pos()).scale(.45)).add(0, c.tpos().y - c.pos().y, 0);
            if (t == 4) { for (int s = -1; s <= 1; s += 2) prop(c, mid.add(r.scale(.9 * s)), f, 1.4f, 1.4f, .15f, LENS, 112);
                prop(c, mid, f, .5f, .2f, .15f, 0x333333, 112); c.sound(SoundEvents.GLASS_PLACE, 1, 1.2f); }
            if (t >= 14 && t < 104 && t % 9 == 5) { Vec3 at = c.eye().add(f.scale(.8)), lens = mid.add(r.scale(.9 * (t % 18 == 5 ? 1 : -1)));
                bolt(c, at, lens.subtract(at).normalize().scale(.4), .15f, .7f, SK, 4, 0, 40); c.sound(SoundEvents.TRIDENT_THROW, .6f, 1.5f); }
            int n = t < 60 ? 2 : 3;
            detonate(c, LENS, SK, .75, at -> { Vec3 d = c.tpos().subtract(at).normalize();
                for (int k = 0; k < n; k++) bolt(c, at, rot(d, (k - (n - 1) / 2.) * .5).scale(.42), .15f, .7f, SK2, 4, 0, 30);
                fx(c, ParticleTypes.ENCHANTED_HIT, at, 4, .2); });
        }, (p, t, m) -> { p.r("armR", -1.4f, .4f, 0).r("head", 0, sin(t, .2f) * .2f, 0); pointLoop(p, t, "armL", 18); });
    }
    static Attack sethEye() {
        return atk("seth_eye_mine", 120, (c, t) -> {
            if (t == 6) { c.proj().at(c.tpos().add(side(toT(c)).scale(4))).box(1.4f, 1.4f, .2f).color(EYE).dmg(0).life(112).spawn(); c.sound(SoundEvents.ENDER_EYE_DEATH, 1, 1); }
            int k = (t - 6) % 34;
            each(c, EYE, p -> {
                if (k < 16) p.setDeltaMovement(c.tpos().subtract(p.position()).scale(.12));
                else p.setDeltaMovement(Vec3.ZERO);
                if (k == 16) { prop(c, p.position(), toT(c), 1.6f, 1.6f, .25f, MINE, 14); c.sound(SoundEvents.TRIPWIRE_CLICK_ON, 1, 1.4f); }
                if (k > 16 && k <= 24) { double a = (k - 16) * Math.PI / 4; prop(c, p.position().add(Math.cos(a) * 1.3, Math.sin(a) * 1.3, 0), toT(c), .25f, .25f, .25f, MINE, 32 - k); c.sound(SoundEvents.NOTE_BLOCK_HAT.get(), .8f, 1 + k * .05f); }
                if (k == 30) { c.aoe(p.position(), 1.6, 6, .4); fx(c, ParticleTypes.EXPLOSION, p.position(), 2, .4); c.sound(SoundEvents.GENERIC_EXPLODE, .6f, 1.5f); }
            });
        }, (p, t, m) -> { p.r("armR", -1.8f, 0, -.3f).r("head", .15f, 0, 0); hover(p, t, .4f); });
    }
    static Attack sethBooks() {
        return atk("seth_book_words", 130, (c, t) -> {
            Vec3 f = toT(c); Vec3 ax = rot(side(f), t * .012);
            if (t == 4) for (int s = -1; s <= 1; s += 2) prop(c, c.tpos().add(ax.scale(5.5 * s)), ax, 1.6f, 2, .6f, 0x5a2a8a, 120);
            if (t >= 12 && t < 118 && t % 6 == 0) { int s = (t / 6) % 2 == 0 ? 1 : -1;
                Vec3 at = c.tpos().add(ax.scale(5 * s)).add(f.scale(c.rnd.nextGaussian() * 1.6)).add(0, c.rnd.nextGaussian() * .7, 0);
                c.proj().at(at).vel(ax.scale(-.24 * s)).box(.5f, .3f, 1.6f).color(WORD).dmg(3).warn(4).life(46).spawn();
                if (t % 18 == 0) c.sound(SoundEvents.BOOK_PAGE_TURN, 1, 1.2f); }
        }, (p, t, m) -> { p.r("armL", -1.2f, .5f, 0).r("armR", -1.2f, -.5f, 0).r("body", 0, t * .012f, 0); });
    }

    // =====================================================================================================
    // ORANGE - small mouse-like plant: orange fur coat, bandanna, boxing gloves, no arms
    // =====================================================================================================
    static final int GLOVE = 0xff7a1a, GLOVE2 = 0xff7a1b, GLOVE3 = 0xff7a1c;
    static CharacterDef orange() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 4, 0, 6, 6, 5, 0xff9a3a).box("head", "body", 0, 5, 0, 6, 5, 5, 0xffaa4a)
            .box("earL", "head", -3, 3, 0, 2.6f, 2.6f, 1, 0xffaa4a).box("earR", "head", 3, 3, 0, 2.6f, 2.6f, 1, 0xffaa4a)
            .box("bandanna", "head", 0, 1.8f, 0, 6.4f, 1, 5.4f, 0xd02828).box("snout", "head", 0, -1, -3, 1.4f, 1, 1, 0x3a2010)
            .limb("armL", "body", -4.5f, 1, -1, 3, 3, 3, RED).limb("armR", "body", 4.5f, 1, -1, 3, 3, 3, RED)
            .limb("legL", null, -1.5f, 1, 0, 1.6f, 1, 2, 0xff9a3a).limb("legR", null, 1.5f, 1, 0, 1.6f, 1, 2, 0xff9a3a);
        m = eyes(m, 5, .2f, 1.2f, .8f, .8f, 0x1a0a00);
        return fk("flower_orange", "Orange").hp(110).boss().egg(0xff9a3a, RED).size(.6f, 1.1f).keep(4).rest(16).speed(.35f)
            .model(m).attacks(orDash(), orOmega(), orFinal()).build();
    }
    /** JA-ORANGE: wind-up flash then a dash at the SOUL; dodge by moving (a "parry") and two gloves curve after you. */
    static Attack orDash() {
        return atk("orange_ja_orange_dash", 70, (c, t) -> {
            if (t == 4) { fx(c, ParticleTypes.FLASH, c.eye(), 1, 0); c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, 1.6f); }
            if (t == 16) c.dash(c.tpos().subtract(c.pos()).normalize().scale(1.3).add(0, .2, 0));
            if (t == 20) { if (moving(c)) { fx(c, ParticleTypes.CRIT, c.tpos(), 12, .5); c.sound(SoundEvents.SHIELD_BLOCK, 1, 1.4f);
                    for (int s = -1; s <= 1; s += 2) c.proj().at(c.pos().add(0, 1, 0)).vel(side(toT(c)).scale(.35 * s)).box(.6f, .6f, .6f).color(GLOVE).dmg(4).life(50).spawn();
                } else c.aoe(c.pos(), 1.8, 7, .8); }
            if (t > 24) steer(c, GLOVE, .04, .35);
        }, (p, t, m) -> { float w = ramp(t, 0, 14), go = ramp(t, 14, 18), back = ramp(t, 40, 60);
            p.r("armR", (-1.5f * w + 2.8f * go) * (1 - back), 0, 0).r("armL", -.8f * w * (1 - back), 0, 0).r("body", (.6f * go) * (1 - back), 0, 0); });
    }
    static Attack orOmega() {
        return phase("orange_omega", 140, -1, .5f, (c, t) -> {
            if (t >= 10 && t < 130 && t % 2 == 0) { double rad = 2.6 + 1.6 * Math.sin(t * .08), y = 2.6 - (t - 10) * .025;
                for (int ring = 0; ring < 2; ring++) for (int k = 0; k < 6; k++) { double a = t * (ring == 0 ? .07 : -.07) + k * Math.PI / 3;
                    c.proj().at(c.tpos().add(Math.cos(a) * (rad + ring * 1.2), y + ring * .6, Math.sin(a) * (rad + ring * 1.2))).box(.55f, .55f, .55f).color(GLOVE2).dmg(4).warn(t < 20 ? 3 : 0).life(2).spawn(); } }
            if (t % 20 == 0) c.sound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, .7f, 1.3f);
        }, (p, t, m) -> { p.r("body", 0, t * .3f, 0); p.r("armL", -1.5f, 0, .6f).r("armR", -1.5f, 0, -.6f); hover(p, t, 1); });
    }
    static Attack orFinal() {
        return phase("orange_final_attack", 120, -1, .3f, (c, t) -> {
            Vec3 r = side(toT(c));
            if (t == 8) for (int k = 0; k < 26; k++) c.proj().at(c.tpos().add((c.rnd.nextDouble() * 2 - 1) * 5, c.rnd.nextGaussian() * .8, (c.rnd.nextDouble() * 2 - 1) * 5))
                    .box(.55f, .55f, .55f).color(GLOVE3).dmg(4).warn(20).life(100).spawn();
            for (int b : new int[]{40, 75}) {
                int s = b == 40 ? 1 : -1;
                if (b == t) { c.dash(r.scale(1.1 * s).add(0, .3, 0)); c.sound(SoundEvents.PHANTOM_SWOOP, 1, 1.2f); fx(c, ParticleTypes.CLOUD, c.pos(), 12, .6); }
                if (t == b + 4) each(c, GLOVE3, p -> p.setDeltaMovement(r.scale(.3 * s)));
                if (t == b + 22) each(c, GLOVE3, p -> p.setDeltaMovement(Vec3.ZERO));
            }
        }, (p, t, m) -> { p.r("body", 0, 0, (t > 38 && t < 60 ? -.8f : t > 73 && t < 95 ? .8f : 0)).r("armL", -2.6f, 0, .3f).r("armR", -2.6f, 0, -.3f); });
    }

    // =====================================================================================================
    // GREEN - green chef: lime suit, green apron, chef hat, frying pan
    // =====================================================================================================
    static final int FIRE = 0xff8a2a, EGG = 0xfff8e8, SWEET = 0xff9ad8, PAN0 = 0x3a3a3c, PAN1 = 0x3a3a3d, PAN2 = 0x3a3a3e, FLAME = 0xff6a1a;
    static CharacterDef green() {
        ModelDef m = hum(7, 9, 6, 6, 0xa8e05a, 0x4ab84a, 0x4ab84a, 0x2a8a2a)
            .box("apron", "body", 0, -1, -1.9f, 5, 7, .4f, 0x2a8a2a).box("hat", "head", 0, 4.5f, 0, 5, 3, 5, 0xc8f08a).box("puff", "hat", 0, 2, 0, 6.4f, 2, 6.4f, 0xc8f08a)
            .box("pan", "armR", 0, -7, -2, 3.6f, .5f, 3.6f, 0x3a3a3c).box("handle", "armR", 0, -5, -.4f, .6f, .4f, 2, 0x5a3a20);
        m = eyes(m, 6, .3f, .7f, .7f, .9f, 0x101010);
        return fk("flower_green", "Green").hp(110).boss().egg(0x4ab84a, 0xc8f08a).size(.6f, 1.9f).keep(6).rest(16)
            .model(m).attacks(grCooking(), grFakeout(), grOmega()).build();
    }
    static Attack grCooking() {
        return atk("green_cooking_time", 120, (c, t) -> {
            Vec3 f = toT(c), r = side(f), pan = c.eye().add(f.scale(1)).add(0, -.4, 0);
            if (t >= 8 && t < 104 && t % 6 == 0) { c.proj().at(pan).vel(new Vec3(c.rnd.nextGaussian() * .04, .12, c.rnd.nextGaussian() * .04)).box(.3f, .5f, .3f).color(FIRE).dmg(3).life(20).spawn();
                fx(c, ParticleTypes.FLAME, pan, 3, .2); }
            if (t >= 14 && t < 100 && t % 14 == 0) { boolean egg = (t / 14) % 2 == 0;
                if (egg) c.proj().at(c.tpos().add(r.scale(-3)).add(f.scale(-3))).vel(r.scale(.18).add(f.scale(.15))).box(.7f, .15f, .7f).color(EGG).dmg(4).life(100).spawn();
                else rain(c, 2.5, 4, .35f, SWEET, 3, 6, .04f);
                c.sound(egg ? SoundEvents.TURTLE_EGG_CRACK : SoundEvents.GENERIC_EAT, 1, 1.3f); }
            each(c, EGG, p -> { Vec3 d = p.position().subtract(c.tpos()), v = p.getDeltaMovement();
                if (Math.abs(d.dot(r)) > 3.2 && v.dot(r) * d.dot(r) > 0) v = v.subtract(r.scale(2 * v.dot(r)));
                if (Math.abs(d.dot(f)) > 3.2 && v.dot(f) * d.dot(f) > 0) v = v.subtract(f.scale(2 * v.dot(f)));
                p.setDeltaMovement(v); });
        }, (p, t, m) -> { p.r("armR", -1.3f + Math.abs(sin(t, .5f)) * -.6f, 0, 0).r("armL", -.8f, .4f, 0); });
    }
    /** Ja-st kidding! A fake dash, then a string of flames that can't be dashed through. */
    static Attack grFakeout() {
        return atk("green_fakeout_flames", 90, (c, t) -> {
            if (t == 4) { fx(c, ParticleTypes.FLASH, c.eye(), 1, 0); c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, 1.6f); }
            if (t == 16) { fx(c, ParticleTypes.POOF, c.eye(), 8, .3); c.sound(SoundEvents.VILLAGER_NO, 1, 1.5f); }
            if (t >= 22 && t < 80 && t % 5 == 2) { Vec3 from = c.eye(), to = c.target.position().add(c.rnd.nextGaussian() * 1.2, 0, c.rnd.nextGaussian() * 1.2);
                c.proj().at(from).vel(arcVel(from, to, 16, .03f)).box(.45f, .6f, .45f).color(FLAME).dmg(4).gravity(.03f).life(22).spawn();
                c.sound(SoundEvents.FIRECHARGE_USE, .5f, 1.4f); }
        }, (p, t, m) -> { if (t < 18) { float w = ramp(t, 0, 14); p.r("armR", -1.5f * w, 0, 0).r("body", .3f * w, 0, 0); } else throwR(p, (t + 3) % 5 * 2.5f, 5, 12); });
    }
    static Attack grOmega() {
        int[] pans = {PAN0, PAN1, PAN2};
        return phase("green_omega_pan", 130, -1, .4f, (c, t) -> {
            if (t == 4) for (int k = 0; k < 3; k++) prop(c, around(c, 4, k * Math.PI * 2 / 3, .6), toT(c), 1.6f, .3f, 1.6f, pans[k], 124);
            if (t >= 14 && t < 120 && t % 12 == 2) { int k = (t / 12) % 3;
                each(c, pans[k], p -> { prop(c, p.position().add(0, .3, 0), toT(c), 1.8f, .1f, 1.8f, WHITE, 6); c.sound(SoundEvents.ANVIL_PLACE, .5f, 1.8f); }); }
            if (t >= 20 && t < 126 && t % 12 == 8) { int k = (t / 12) % 3;
                each(c, pans[k], p -> { fan(c, p.position().add(0, .4, 0), c.tpos().subtract(p.position()), 3, .5, .3, .4f, .6f, FLAME, 4, 0, 40); c.sound(SoundEvents.BLAZE_SHOOT, .6f, 1.3f); }); }
        }, (p, t, m) -> { raise(p, ramp(t, 0, 10) * .8f); hover(p, t, .6f); });
    }

    // =====================================================================================================
    // BLUE (ballet dancer, butterfly) & YELLOW (cowboy sharpshooter): their duo attacks, each from either side
    // =====================================================================================================
    static final int BAL_A = 0x7fb8ff, BAL_B = 0x7fb8fe, GPATH = 0xbfe0ff, GFLOWER = 0x3f7aff, GSHOT = 0xffe04b, PETAL8 = 0xff7ab9, NOTE_C = 0x9fd0ff, NFLOWER = 0x3f7afe;
    static CharacterDef blue() {
        ModelDef m = hum(9, 8, 5, 5, 0xa8d0ff, 0x1a2a6a, 0x1a2a6a, 0x7fb0f0)
            .box("tutu", "body", 0, -3.5f, 0, 10, 1.4f, 8, 0xa8d0ff).box("hair", "head", 0, 2, 1, 5.4f, 2.4f, 5.4f, 0x9fd0ff)
            .box("pony", "hair", 0, 0, 3.2f, 1.6f, 4, 1.6f, 0x9fd0ff).box("brow", "head", 0, 1, -2.7f, 4, .5f, .3f, 0x0a1440);
        return fk("flower_blue", "Blue").hp(110).boss().egg(0x1a2a6a, 0xa8d0ff).size(.6f, 2.1f).keep(6).rest(16).random()
            .model(m).attacks(dancers("blue"), guided("blue"), gallery("blue"), singing("blue")).build();
    }
    static CharacterDef yellow() {
        ModelDef m = hum(9, 8, 5, 5, 0xc8a020, 0xffe04a, 0xffe04a, 0xc8a020)
            .box("hatBrim", "head", 0, 2.8f, 0, 9, .5f, 9, 0xb08a10).box("hatTop", "head", 0, 4, 0, 5.4f, 2.4f, 5.4f, 0xb08a10)
            .box("jaw", "head", 0, -2.2f, -.2f, 5.4f, 1.4f, 5.4f, 0xffe04a).glow("grin", "head", 0, -1.2f, -2.75f, 3.6f, .5f, .2f, WHITE)
            .box("gun", "armR", 0, -6, -1.6f, .6f, .8f, 2.4f, 0x8a8a90).box("spurL", "legL", 0, -8.5f, 1.5f, .8f, .8f, .8f, 0xffd040);
        m = eyes(m, 5, .5f, 1, .5f, .5f, 0x101010);
        return fk("flower_yellow", "Yellow").hp(110).boss().egg(0xffe04a, 0xb08a10).size(.6f, 2.1f).keep(7).rest(16).random()
            .model(m).attacks(dancers("yellow"), guided("yellow"), gallery("yellow"), singing("yellow")).build();
    }
    static void petals8(AttackCtx c, Vec3 at) {
        for (int k = 0; k < 8; k++) { double a = k * Math.PI / 4; bolt(c, at, new Vec3(Math.cos(a), 0, Math.sin(a)).scale(.22), .2f, .45f, PETAL8, 3, 0, 30); }
        fx(c, ParticleTypes.CHERRY_LEAVES, at, 8, .4); c.sound(SoundEvents.FIREWORK_ROCKET_BLAST, .6f, 1.5f);
    }
    static void yPose(Pose p, String who, float t, float period) {
        if (who.equals("yellow")) { float k = (t % period) / period; p.r("armR", -1.6f, 0, 0).o("armR", 0, 0, k < .15f ? 1.2f : 0).r("head", 0, .2f, 0); }
        else { p.r("armL", -2.8f, 0, .3f).r("armR", -1.2f, 0, -1.2f).r("legR", -1.2f, 0, 0).r("body", 0, t * .2f, 0); }
    }
    static Attack dancers(String who) {
        return atk(who + "_dancers", 110, (c, t) -> {
            Vec3 r = side(toT(c));
            if (t >= 6 && t < 60 && t % 6 == 0) { boolean a = (t / 6) % 2 == 0;
                c.proj().at(c.target.position().add(r.scale(7)).add(0, .5, 0)).vel(r.scale(-.16)).box(.5f, 1, .5f).color(a ? BAL_A : BAL_B).dmg(4).life(90).spawn(); }
            for (int slam : new int[]{50, 80}) {
                if (t == slam - 10) c.dash(new Vec3(0, .7, 0));
                if (t == slam) { c.sound(SoundEvents.ANVIL_LAND, .6f, 1.4f); fx(c, ParticleTypes.CLOUD, c.pos(), 10, .6);
                    each(c, slam == 50 ? BAL_A : BAL_B, p -> { p.setDeltaMovement(p.getDeltaMovement().x, .38, p.getDeltaMovement().z); p.grav = .038f; }); }
                if (t == slam + 20) each(c, slam == 50 ? BAL_A : BAL_B, p -> { p.setDeltaMovement(p.getDeltaMovement().x, 0, p.getDeltaMovement().z); p.grav = 0; });
            }
        }, (p, t, m) -> { if (who.equals("blue")) { float up = env(t, 38, 42, 48, 52) + env(t, 68, 72, 78, 82); raise(p, up); p.r("legR", -1.4f * up, 0, 0).r("body", 0, t * .25f, 0); }
            else yPose(p, who, t, 20); });
    }
    static Attack guided(String who) {
        return atk(who + "_guided_bullet", 120, (c, t) -> {
            Vec3 f = toT(c), r = side(f), start = c.tpos().add(r.scale(-6)).add(f.scale(-1));
            if (t == 4) for (int i = 0; i < 24; i++) prop(c, start.add(r.scale(i * .5)), r, .12f, .05f, .3f, GPATH, 110);
            if (t >= 10 && t < 70 && t % 12 == 10) { Vec3 at = start.add(r.scale(1.5 + (t - 10) / 12 * 2.0));
                c.proj().at(at).box(.8f, .8f, .2f).color(GFLOWER).dmg(2).life(100).spawn(); fx(c, ParticleTypes.CHERRY_LEAVES, at, 4, .3); }
            if (t == 40) { c.proj().at(start).vel(r.scale(.16)).box(.3f, .3f, .6f).color(GSHOT).dmg(4).life(80).spawn(); c.sound(SoundEvents.CROSSBOW_SHOOT, 1, 1.2f); }
            detonate(c, GSHOT, GFLOWER, .7, at -> petals8(c, at));
        }, (p, t, m) -> yPose(p, who, t, 40));
    }
    static Attack gallery(String who) {
        return atk(who + "_shooting_gallery", 110, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t >= 6 && t < 46 && t % 8 == 6) { int i = (t - 6) / 8;
                c.proj().at(c.tpos().add(r.scale(-4 + i * 2)).add(0, 1.2 + (i % 2) * .8, 0)).box(.8f, .8f, .2f).color(GFLOWER).dmg(2).life(40 + (4 - i) * 10).spawn();
                c.sound(SoundEvents.AZALEA_LEAVES_PLACE, 1, 1.4f); }
            pop(c, GFLOWER, at -> { prop(c, c.eye().add(at.subtract(c.eye()).scale(.5)), at.subtract(c.eye()), .06f, .06f, (float) at.distanceTo(c.eye()), GSHOT, 3);
                c.sound(SoundEvents.CROSSBOW_SHOOT, 1, 1.4f); petals8(c, at); });
        }, (p, t, m) -> yPose(p, who, t, 10));
    }
    static Attack singing(String who) {
        return atk(who + "_singing", 120, (c, t) -> {
            Vec3 r = side(toT(c));
            for (int w = 0; w < 3; w++) { int start = 8 + w * 28;
                if (t >= start && t < start + 18 && (t - start) % 3 == 0) { boolean fl = w == 1 && t == start + 9;
                    Vec3 at = c.tpos().add(r.scale(7)).add(toT(c).scale((w - 1) * 1.2));
                    c.proj().at(at).vel(r.scale(-.2)).box(fl ? .8f : .35f, fl ? .8f : .35f, fl ? .2f : .35f).color(fl ? NFLOWER : NOTE_C).dmg(3).life(fl ? 30 : 70).spawn();
                    if (t % 6 == 0) c.sound(SoundEvents.NOTE_BLOCK_FLUTE.get(), .6f, .8f + (t % 12) * .05f); } }
            each(c, NOTE_C, p -> p.setDeltaMovement(p.getDeltaMovement().x, Math.cos(p.age() * .25) * .09, p.getDeltaMovement().z));
            pop(c, NFLOWER, at -> petals8(c, at));
        }, (p, t, m) -> { if (who.equals("blue")) { raise(p, .6f + .2f * sin(t, .2f)); p.r("head", -.3f, 0, 0); } else yPose(p, who, t, 28); });
    }

    // =====================================================================================================
    // PINK - Shadow Crystal boss: ghost in a Darkner body; cat ears, pink tutu, bell earrings, heart necklace, staff.
    // Purple-SOUL lanes become a 3D grid around you; pink hearts heal.
    // =====================================================================================================
    static final int CATF = 0xff9ad0, HEART = 0xff5aa0, BOMB = 0xc03a8a, BOMB_G = 0xc03a8b, DUMMY = 0xc8a070, DHEART = 0xff5aa1,
        RDUMMY = 0xd04040, CIRC = 0xd8a0ff, ZAP = 0xa0e0ff, ARROWC = 0xffc0e0;
    static CharacterDef pink() {
        ModelDef m = hum(8, 8, 5, 6, 0xff9ad0, 0xffe0e8, 0xffe0e8, 0xffe0e8)
            .box("tutu", "body", 0, -3.5f, 0, 10, 1.6f, 8, 0xff7ab8).box("hair", "head", 0, 1.6f, .6f, 6.6f, 3.6f, 6.6f, 0xffb0d8)
            .box("earL", "head", -2.2f, 4.2f, 0, 1.6f, 2, 1, 0xffb0d8).box("earR", "head", 2.2f, 4.2f, 0, 1.6f, 2, 1, 0xffb0d8)
            .glow("necklace", "body", 0, 2.5f, -1.9f, 1.4f, 1.4f, .3f, 0xffe04a).glow("bellL", "head", -3.4f, -1.5f, 0, .6f, .6f, .6f, 0xffe04a)
            .box("tail", "body", 0, -2, 3, 1, 1, 6, 0xff9ad0).box("tailTip", "tail", 0, 0, 3.5f, 1, 1, 2, WHITE)
            .box("staff", "armR", 0, -6, -1.4f, .5f, 10, .5f, 0xffe04a).glow("staffTop", "armR", 0, -1, -1.4f, 1.4f, 1.4f, 1.4f, 0xff7ab8)
            .ghost("ghost", "body", 0, 10, 2, 5, 6, 5, 0xffd0f0, .4f);
        m = eyes(m, 6, .4f, 1.2f, .9f, 1.1f, 0xff4fa0);
        return fk("pink", "Pink").hp(420).boss().egg(0xff9ad0, 0xffe04a).size(.6f, 2).keep(7).rest(14).speed(.3f)
            .model(m).attacks(pkCatFaces(), pkBombs(), pkStage(), pkPlus(), pkTunnel(), pkGiantBomb(), pkRedDummies(), pkKnock(), pkBombVolley(), pkCatMix()).build();
    }
    /** Grid tile centers: 4x4 around the target (1.6 block tiles). */
    static Vec3 tile(AttackCtx c, int i, int j) { Vec3 f = toT(c), r = side(f); return c.target.position().add(r.scale((i - 1.5) * 1.6)).add(f.scale((j - 1.5) * 1.6)); }
    static void bomb(AttackCtx c, int i, int j, int fuse, int tag) {
        Vec3 to = tile(c, i, j).add(0, .3, 0), from = c.eye().add(0, 1, 0);
        c.proj().at(from).vel(arcVel(from, to, fuse, .03f)).box(.7f, .7f, .7f).color(tag).dmg(5).gravity(.03f).life(fuse).spawn();
        prop(c, to.add(0, -.25, 0), toT(c), 1.4f, .05f, 1.4f, 0xff4fa0, fuse);
    }
    static void blast(AttackCtx c, Vec3 at) {
        Vec3 f = toT(c), r = side(f);
        line(c, at, r, 6.4f, .9f, 0xff8ad0, 5, 0, 5); line(c, at, f, 6.4f, .9f, 0xff8ad0, 5, 0, 5);
        c.proj().at(at.add(0, .3, 0)).box(.5f, .5f, .5f).color(HEART).dmg(0).life(80).spawn();
        fx(c, ParticleTypes.EXPLOSION, at, 1, 0); c.sound(SoundEvents.GENERIC_EXPLODE, .5f, 1.6f);
    }
    static void catLane(AttackCtx c, int s, double lane, double y, boolean heart) {
        Vec3 f = toT(c), r = side(f), at = c.tpos().add(r.scale(7 * s)).add(f.scale(lane)).add(0, y, 0);
        c.proj().at(at).vel(r.scale(-.18 * s)).box(heart ? .5f : .9f, heart ? .5f : .8f, heart ? .5f : .3f).color(heart ? HEART : CATF).dmg(heart ? 0 : 4).life(80).spawn();
    }
    static Attack pkCatFaces() {
        return phase("pink_cat_faces", 120, .66f, 2, (c, t) -> {
            if (t >= 8 && t < 50 && t % 8 == 0) for (int l = -1; l <= 1; l++) catLane(c, 1, l * 1.6, 0, l == ((t / 8) % 3) - 1 && t > 24);
            if (t >= 56 && t < 100 && t % 5 == 0) { catLane(c, -1, ((t / 5) % 3 - 1) * 1.6, 0, true); if (t % 10 == 0) catLane(c, -1, ((t / 5 + 1) % 3 - 1) * 1.6, 0, false); }
            hearts(c, HEART); if (t % 16 == 0) c.sound(SoundEvents.CAT_AMBIENT, .7f, 1.5f);
        }, (p, t, m) -> { pointLoop(p, t, "armR", 16); p.r("tail", 0, sin(t, .3f) * .5f, 0); });
    }
    static Attack pkBombs() {
        return phase("pink_bombs", 110, .66f, 2, (c, t) -> {
            int[] at = {10, 34, 54, 70, 84}; for (int k = 0; k < at.length; k++) if (t == at[k]) { int n = k == at.length - 1 ? 4 : 3;
                for (int b = 0; b < n; b++) bomb(c, c.rnd.nextInt(4), c.rnd.nextInt(4), 22 - k * 2, BOMB); c.sound(SoundEvents.TNT_PRIMED, .6f, 1.5f); }
            pop(c, BOMB, a -> blast(c, a)); hearts(c, HEART);
        }, (p, t, m) -> { throwR(p, t % 16, 5, 15); if (t > 95) p.r("head", -.3f, 0, 0).r("body", sin(t, 1.2f) * .1f, 0, 0); });
    }
    static Attack pkStage() {
        return phase("pink_dummy_stage", 130, .66f, 2, (c, t) -> {
            if (t == 6) for (int k = 0; k < 5; k++) prop(c, around(c, 4.5, k * Math.PI * 2 / 5, -.4), toT(c), .7f, 1.4f, .7f, DUMMY, 120);
            if (t >= 16 && t < 118 && t % 10 == 6) each(c, DUMMY, d -> { if (c.rnd.nextInt(3) == 0)
                    shot(c, d.position().add(0, .5, 0), c.tpos().subtract(d.position()).normalize().scale(.22), .4f, DHEART, 4, 2, 40); });
            if (t % 12 == 0) { c.sound(SoundEvents.NOTE_BLOCK_BELL.get(), .7f, .8f + (t % 36) * .02f); fx(c, ParticleTypes.NOTE, c.eye().add(0, .6, 0), 2, .3); }
        }, (p, t, m) -> { raise(p, .4f + .2f * sin(t, .15f)); p.r("head", -.25f, sin(t, .2f) * .2f, 0); sway(p, t, .2f); });
    }
    /** Plus-shaped 5-point grid; circles and half-circles slide in along the four arms. */
    static void plusWave(AttackCtx c, int t, int every, double spd, double turn) {
        if (t >= 8 && t < 112 && t % every == 0) { int d = (t / every) % 4; Vec3 dir = rot(toT(c), d * Math.PI / 2 + turn);
            Vec3 at = c.tpos().add(dir.scale(7)); boolean half = (t / every) % 3 == 0;
            c.proj().at(at).vel(dir.scale(-spd)).box(half ? 1.6f : .9f, half ? .45f : .9f, .9f).color(CIRC).dmg(4).warn(4).life((int) (14 / spd)).spawn();
            if (t % (every * 2) == 0) c.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, .6f, 1.4f); }
    }
    static Attack pkPlus() {
        return phase("pink_plus_circles", 120, .33f, .66f, (c, t) -> { plusWave(c, t, c.me.hpFrac() < .45f ? 4 : 6, c.me.hpFrac() < .45f ? .32 : .22, 0); hearts(c, HEART); },
            (p, t, m) -> { pointLoop(p, t, "armR", 12); p.o("ghost", sin(t, .3f) * 2, 0, 0); });
    }
    static Attack pkTunnel() {
        return phase("pink_tunnel", 130, .0f, .66f, (c, t) -> {
            Vec3 f = toT(c);
            if (t >= 12 && t < 120 && t % 10 == 2) { double a = c.rnd.nextDouble() * Math.PI * 2, w = .8 + c.rnd.nextDouble() * 1.4;
                Vec3 at = c.tpos().add(f.scale(8)).add(rot(side(f), 0).scale(Math.cos(a) * 1.5)).add(0, Math.sin(a) * 1.2, 0);
                c.proj().at(at).vel(f.scale(-.26)).box((float) w, (float) w * .6f, .3f).color(ZAP).dmg(4).life(40).spawn(); fx(c, ParticleTypes.ELECTRIC_SPARK, at, 4, .3); }
            if (t >= 20 && t < 120 && t % 16 == 4) { int n = 6 + (t / 16) % 2 * 2; double ph = t * .1 * ((t / 16) % 2 == 0 ? 1 : -1);
                for (int k = 0; k < n; k++) { double a = ph + k * Math.PI * 2 / n; Vec3 off = side(f).scale(Math.cos(a) * 2.2).add(0, Math.sin(a) * 1.6, 0);
                    bolt(c, c.tpos().add(f.scale(8)).add(off), f.scale(-.3), .2f, .5f, ARROWC, 4, 4, 34); } c.sound(SoundEvents.BEACON_AMBIENT, .5f, 1.8f); }
            if (t % 30 == 0) catLane(c, 1, 0, 0, true);
            hearts(c, HEART);
        }, (p, t, m) -> { raise(p, .5f); p.r("staffTop", 0, t * .4f, 0); hover(p, t, .8f); });
    }
    static Attack pkGiantBomb() {
        return phase("pink_giant_cat_bomb", 120, .33f, .66f, (c, t) -> {
            int[] at = {10, 32, 52}; for (int k = 0; k < 3; k++) if (t == at[k]) { for (int b = 0; b < 3; b++) bomb(c, c.rnd.nextInt(4), c.rnd.nextInt(4), 20, BOMB); c.sound(SoundEvents.TNT_PRIMED, .6f, 1.5f); }
            if (t == 72) { int ci = c.rnd.nextInt(2) * 3, cj = c.rnd.nextInt(2) * 3;
                for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) if (i != ci || j != cj) prop(c, tile(c, i, j).add(0, .05, 0), toT(c), 1.4f, .05f, 1.4f, 0xff4fa0, 26);
                prop(c, tile(c, ci, cj).add(0, .05, 0), toT(c), 1.4f, .05f, 1.4f, 0x40ff80, 30);
                c.proj().at(c.eye().add(0, 2, 0)).box(2.4f, 2.4f, 2.4f).color(BOMB_G).dmg(0).life(26).spawn(); c.sound(SoundEvents.CAT_HISS, 1, .8f); }
            each(c, BOMB_G, p -> { if (p.age() == 25) {
                    for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) { Vec3 tl = tile(c, i, j);
                        if (c.target.position().distanceTo(tl) < 1.1 && c.level.getEntitiesOfClass(UtProjectile.class, new AABB(tl, tl).inflate(.5),
                                q -> q.ownerId == c.me.getId() && q.color() == 0x40ff80).isEmpty()) c.hit(c.target, 7); }
                    fx(c, ParticleTypes.EXPLOSION_EMITTER, c.target.position(), 1, 0); c.sound(SoundEvents.GENERIC_EXPLODE, 1, 1.1f); } });
            pop(c, BOMB, a -> blast(c, a)); hearts(c, HEART);
        }, (p, t, m) -> { if (t < 70) throwR(p, t % 20, 6, 18); else raise(p, ramp(t, 70, 76)); });
    }
    static Attack pkRedDummies() {
        return phase("pink_red_dummies", 130, .33f, .66f, (c, t) -> {
            if (t == 6) { for (int k = 0; k < 4; k++) prop(c, around(c, 4.5, k * Math.PI / 2, -.4), toT(c), .7f, 1.4f, .7f, DUMMY, 120);
                for (int k = 0; k < 2; k++) c.proj().at(around(c, 5, k * Math.PI + .7, 0)).box(.7f, 1.4f, .7f).color(RDUMMY).dmg(4).warn(10).life(120).spawn(); }
            steer(c, RDUMMY, .01, .08);
            if (t >= 16 && t < 118 && t % 14 == 6) each(c, RDUMMY, d -> { Vec3 at = d.position().add(0, .8, 0);
                c.proj().at(at).vel(c.tpos().subtract(at).scale(.06).add(0, .3, 0)).box(.4f, .4f, .4f).color(0xc8a071).dmg(4).gravity(.03f).life(40).spawn(); });
            if (t >= 16 && t < 118 && t % 10 == 6) each(c, DUMMY, d -> { if (c.rnd.nextInt(3) == 0) shot(c, d.position().add(0, .5, 0), c.tpos().subtract(d.position()).normalize().scale(.22), .4f, DHEART, 4, 2, 40); });
            if (t % 12 == 0) c.sound(SoundEvents.NOTE_BLOCK_BELL.get(), .7f, 1);
        }, (p, t, m) -> { raise(p, .5f); sway(p, t, .3f); });
    }
    static Attack pkKnock() {
        return phase("pink_ghost_knock", 130, -1, .33f, (c, t) -> {
            double turn = (t / 32) * Math.PI / 2;
            plusWave(c, t, 4, .3, turn);
            if (t % 32 == 30) { c.sound(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1.2f); fx(c, ParticleTypes.SWEEP_ATTACK, c.tpos(), 3, 1); }
            hearts(c, HEART);
        }, (p, t, m) -> { float k = (t % 32) / 32f; p.o("ghost", -6 * env(k, .7f, .9f, .95f, 1), 4 * env(k, .7f, .9f, .95f, 1), 0).r("armL", -1.6f * env(k, .7f, .9f, .95f, 1), 0, 0); });
    }
    static Attack pkBombVolley() {
        return phase("pink_bomb_volleys", 130, -1, .33f, (c, t) -> {
            if (t >= 10 && t < 80 && t % 22 == 10) { int si = c.rnd.nextInt(4), sj = c.rnd.nextInt(4);
                for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) if (Math.abs(i - si) + Math.abs(j - sj) > 0 && (i + j) % 2 == 0) bomb(c, i, j, 20, BOMB);
                prop(c, tile(c, si, sj).add(0, .05, 0), toT(c), 1.4f, .05f, 1.4f, 0x40ff80, 22); c.sound(SoundEvents.TNT_PRIMED, .7f, 1.3f); }
            if (t == 92) { boolean top = c.rnd.nextBoolean(); for (int i = 0; i < 4; i++) for (int j = top ? 2 : 0; j < (top ? 4 : 2); j++) blast(c, tile(c, i, j)); }
            pop(c, BOMB, a -> blast(c, a)); hearts(c, HEART);
        }, (p, t, m) -> { if (t < 80) throwR(p, t % 22, 6, 20); else { raise(p, 1); p.o("body", 0, 0, sin(t, .5f) * 3); } });
    }
    static Attack pkCatMix() {
        return phase("pink_cat_bomb_mix", 120, -1, .33f, (c, t) -> {
            if (t >= 8 && t < 100 && t % 8 == 0) { int s = (t / 8) % 2 == 0 ? 1 : -1;
                for (int l = -2; l <= 2; l++) catLane(c, s, l * 1.2, 0, Math.abs(l) == 0 && (t / 8) % 3 == 0); }
            if (t >= 16 && t < 96 && t % 24 == 16) { for (int b = 0; b < 2; b++) bomb(c, c.rnd.nextInt(4), c.rnd.nextInt(4), 20, BOMB); c.sound(SoundEvents.TNT_PRIMED, .6f, 1.5f); }
            pop(c, BOMB, a -> blast(c, a)); hearts(c, HEART);
        }, (p, t, m) -> { throwR(p, t % 24, 6, 20); p.r("tail", 0, sin(t, .4f) * .6f, 0); });
    }

    // =====================================================================================================
    // FLOWERY - yellow-haired "best friend" of Asgore, eyes closed, skateboard. Orange-SOUL dash fight:
    // blue segments only hurt if you stand still (dash through them), "JARONA!" charges can be parried by moving.
    // =====================================================================================================
    static final int STALK = 0xf0f0e0, STALK_B = 0x3fa7fe, THORN = 0x3a6a2a, FANB = 0xffe04c, LILY = 0x4ac060, BUTTER = 0x7fb8fd, TWORD = 0x60ff70;
    static CharacterDef flowery() {
        ModelDef m = hum(8, 9, 6, 6, 0xffe08a, 0xffe08a, 0xffe08a, 0x5a4a8a)
            .box("hair", "head", .6f, 2.4f, .4f, 7, 2.6f, 6.6f, 0xffd040).box("bang", "hair", 2.4f, .4f, -2.6f, 2.6f, 2, 1.4f, 0xffd040)
            .box("shirt", "body", 0, 0, 0, 6.4f, 9.2f, 4.2f, 0xf0f0f0).box("vest", "shirt", 0, 1, -2.2f, 4, 5, .3f, 0x4ab84a)
            .box("board", null, 0, .3f, 0, 4, .6f, 12, 0xd04040)
            .glow("lidL", "head", -1.3f, .4f, -3.15f, 1.6f, .3f, .3f, 0x5a3a10).glow("lidR", "head", 1.3f, .4f, -3.15f, 1.6f, .3f, .3f, 0x5a3a10);
        return fk("flowery", "Flowery").hp(420).boss().egg(0xffe08a, 0x4ab84a).size(.6f, 2.1f).keep(8).rest(14).speed(.35f)
            .model(m).attacks(fwStalks(), fwJarona("flowery_jarona", .8f, 2, 7, 0), fwThicket("flowery_thicket_of_thorns", .8f, 2, 1),
                fwBooks(), fwAquaKnives(), fwJaronaFake(), fwThicket("flowery_thicket_of_thorns_2", .4f, .6f, 2),
                fwThicketButterfly(), fwSplitJarona(), fwJarona("flowery_jarona_5", -1, .2f, 7, 2), fwLastJarona()).build();
    }
    /** One bamboo stalk: white damaging pole with a blue (dash-through) segment at gapY. */
    static void stalk(AttackCtx c, double gapY, double gapH, double spd, boolean allBlue) {
        Vec3 f = toT(c), at = c.target.position().add(f.scale(8));
        double lo = gapY - gapH / 2, hi = gapY + gapH / 2, top = 4;
        if (!allBlue && lo > 0) c.proj().at(at.add(0, lo / 2, 0)).vel(f.scale(-spd)).box(4, (float) lo, .5f).color(STALK).dmg(5).life((int) (14 / spd)).pierce().spawn();
        if (!allBlue && hi < top) c.proj().at(at.add(0, (hi + top) / 2, 0)).vel(f.scale(-spd)).box(4, (float) (top - hi), .5f).color(STALK).dmg(5).life((int) (14 / spd)).pierce().spawn();
        double bl = allBlue ? 0 : lo, bh = allBlue ? top : gapH;
        c.proj().at(at.add(0, bl + bh / 2, 0)).vel(f.scale(-spd)).box(4, (float) bh, .5f).orange().color(STALK_B).dmg(5).life((int) (14 / spd)).pierce().spawn();
    }
    static void jaronaStrike(AttackCtx c, int t, int t0, int fans) {
        int k = t - t0;
        if (k == 0) { fx(c, ParticleTypes.FLASH, c.eye(), 1, 0); c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, 1.5f); }
        if (k == 8) { c.dash(c.tpos().subtract(c.pos()).normalize().scale(1.4).add(0, .15, 0)); c.sound(SoundEvents.PHANTOM_SWOOP, 1, 1.4f); }
        if (k == 11) { if (moving(c)) { fx(c, ParticleTypes.CRIT, c.tpos(), 14, .5); c.sound(SoundEvents.SHIELD_BLOCK, 1, 1.5f);
                for (int i = 0; i < fans; i++) fan(c, c.eye(), rot(toT(c), (i - (fans - 1) / 2.) * .9), 5, .9, .3, .3f, .5f, FANB, 4, 2, 36);
            } else c.aoe(c.pos(), 1.8, 8, 1); }
    }
    static void jaronaPose(Pose p, float t, float period) {
        float k = (t % period) / period * period, w = ramp(k, 0, 8), go = ramp(k, 8, 11), back = ramp(k, 11, period);
        p.r("body", (-.3f * w + .9f * go) * (1 - back), 0, 0).r("armL", -1.2f * w * (1 - back), 0, .6f).r("armR", -1.2f * w * (1 - back), 0, -.6f);
    }
    static Attack fwStalks() {
        return phase("flowery_bamboo_stalks", 130, .8f, 2, (c, t) -> {
            if (t >= 8 && t < 108 && t % 12 == 8) { int i = (t - 8) / 12; stalk(c, .8 + c.rnd.nextDouble() * 2.2, 2.2 - i * .15, .28, false); c.sound(SoundEvents.BAMBOO_PLACE, 1, 1); }
            if (t == 104) prop(c, c.target.position().add(toT(c).scale(2)).add(0, .05, 0), toT(c), 1.6f, .1f, 1.6f, LILY, 24);
        }, (p, t, m) -> { pointLoop(p, t, "armR", 12); p.r("board", 0, 0, sin(t, .3f) * .1f); });
    }
    static Attack fwJarona(String id, float lo, float hi, int n, int fans) {
        return phase(id, 20 + n * 16, lo, hi, (c, t) -> { if (t >= 6 && (t - 6) / 16 < n) jaronaStrike(c, t, 6 + (t - 6) / 16 * 16, fans); },
            (p, t, m) -> jaronaPose(p, Math.max(0, t - 6), 16));
    }
    static Attack fwThicket(String id, float lo, float hi, int level) {
        return phase(id, 130, lo, hi, (c, t) -> {
            Vec3 f = toT(c);
            if (t == 6) { c.proj().at(c.target.position().add(f.scale(-6)).add(0, 1.6, 0)).vel(f.scale(.07)).box(7, 3.2f, 1.2f).color(THORN).dmg(6).warn(14).life(120).pierce().spawn();
                c.sound(SoundEvents.SWEET_BERRY_BUSH_PLACE, 1, .6f); }
            if (t >= 16 && t < 110 && t % 10 == 6) stalk(c, .6 + c.rnd.nextDouble() * 2, level == 2 ? 1.2 : 1.6, .3, false);
            if (level == 2 && t >= 110 && t < 126 && t % 4 == 2) stalk(c, 3.4, .7, .34, false);
            if (moving(c) && t % 4 == 0) each(c, THORN, p -> p.setPos(p.position().subtract(f.scale(.15))));   // outpacing the thorns
            if (t % 10 == 0) fx(c, ParticleTypes.COMPOSTER, c.target.position().add(f.scale(-3)), 4, .8);
        }, (p, t, m) -> { pointLoop(p, t, "armR", 10); p.r("board", 0, sin(t, .5f) * .2f, 0); });
    }
    static Attack fwBooks() {
        return phase("flowery_seths_books", 130, .6f, .8f, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t >= 8 && t < 116 && t % 22 == 8) prop(c, c.tpos().add(f.scale(5)), f, 4, 3, .3f, 0x3fa7ff, 40);
            if (t >= 10 && t < 116 && t % 3 == 0) { boolean good = c.rnd.nextInt(5) == 0;
                Vec3 at = c.tpos().add(f.scale(1 + c.rnd.nextDouble() * 6)).add(r.scale((c.rnd.nextDouble() * 2 - 1) * 2)).add(0, 4, 0);
                c.proj().at(at).vel(new Vec3(0, -.22, 0)).box(.9f, .3f, .3f).color(good ? TWORD : WORD).dmg(good ? 0 : 4).life(34).spawn(); }
            each(c, TWORD, p -> { if (p.position().distanceTo(c.tpos()) < 1) { c.target.heal(1); fx(c, ParticleTypes.HAPPY_VILLAGER, p.position(), 4, .2); p.discard(); } });
            if (t % 18 == 0) c.sound(SoundEvents.BOOK_PAGE_TURN, 1, 1);
        }, (p, t, m) -> { p.r("armL", -1.3f, .4f, 0).r("armR", -1.3f, -.4f, 0); hover(p, t, .5f); });
    }
    static Attack fwAquaKnives() {
        return phase("flowery_aquas_knives", 130, .6f, .8f, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t >= 10 && t < 110 && t % 20 == 10) { int gap = c.rnd.nextInt(6);
                for (int k = 0; k < 7; k++) if (k != gap) bolt(c, c.tpos().add(f.scale(7)).add(r.scale((k - 3) * .8)), f.scale(-.26), .15f, .8f, KNIFE, 5, 6, 40);
                prop(c, c.target.position().add(f.scale(2)).add(r.scale((gap - 3) * .8)).add(0, .05, 0), f, 1.2f, .1f, 1.2f, LILY, 20);
                c.sound(SoundEvents.TRIDENT_THROW, .7f, 1.5f); }
        }, (p, t, m) -> { throwR(p, (t + 10) % 20, 8, 18); });
    }
    /** Jarona 3/4: Orange dashes for some strikes; once, Green's "Ja-st kidding!" fire volley instead. */
    static Attack fwJaronaFake() {
        return phase("flowery_jarona_fakeout", 120, .4f, .6f, (c, t) -> {
            for (int i = 0; i < 6; i++) { int t0 = 6 + i * 18; if (i == 3) continue; if (t >= t0 && t < t0 + 18) jaronaStrike(c, t, t0, i % 2); }
            if (t == 60) { fx(c, ParticleTypes.FLASH, c.eye(), 1, 0); c.sound(SoundEvents.VILLAGER_NO, 1, 1.5f); }
            if (t > 66 && t < 76 && t % 2 == 0) { Vec3 from = c.eye().add(0, 1, 0), to = c.target.position().add(c.rnd.nextGaussian(), 0, c.rnd.nextGaussian());
                c.proj().at(from).vel(arcVel(from, to, 14, .03f)).box(.45f, .6f, .45f).color(FLAME).dmg(4).gravity(.03f).life(20).spawn(); }
        }, (p, t, m) -> jaronaPose(p, Math.max(0, t - 6), 18));
    }
    static Attack fwThicketButterfly() {
        return phase("flowery_thicket_of_thorns_3", 130, .2f, .4f, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 6) c.proj().at(c.target.position().add(f.scale(-6)).add(0, 1.6, 0)).vel(f.scale(.08)).box(7, 3.2f, 1.2f).color(THORN).dmg(6).warn(14).life(120).pierce().spawn();
            if (t >= 16 && t < 116 && t % 12 == 4) stalk(c, 2, 4, .3, true);
            if (t >= 20 && t < 100 && t % 20 == 0) { Vec3 at = c.tpos().add(f.scale(4)).add(r.scale(c.rnd.nextGaussian() * 2)).add(0, 1.5, 0);
                c.proj().at(at).box(.8f, .8f, .2f).color(GFLOWER).dmg(2).life(14).spawn(); }
            pop(c, GFLOWER, at -> { for (int k = 0; k < 6; k++) { double a = k * Math.PI / 3; c.proj().at(at).vel(new Vec3(Math.cos(a) * .12, Math.sin(a * 2) * .05, Math.sin(a) * .12))
                    .box(.45f, .1f, .35f).color(BUTTER).dmg(4).life(50).spawn(); } c.sound(SoundEvents.CROSSBOW_SHOOT, 1, 1.3f); });
            each(c, BUTTER, p -> p.setDeltaMovement(p.getDeltaMovement().add(Math.sin(p.age() * .4) * .02, 0, Math.cos(p.age() * .4) * .02).scale(.98)));
            if (moving(c) && t % 4 == 0) each(c, THORN, p -> p.setPos(p.position().subtract(f.scale(.15))));
        }, (p, t, m) -> { pointLoop(p, t, "armR", 12); p.r("board", 0, sin(t, .5f) * .2f, 0); });
    }
    /** Phase 5 opener: 9 stalks, then four Flowery copies each Jarona once; parrying the fourth sprays fans. */
    static Attack fwSplitJarona() {
        return phase("flowery_stalks_and_jarona", 150, -1, .2f, (c, t) -> {
            if (t >= 6 && t < 78 && t % 8 == 6) stalk(c, .8 + c.rnd.nextDouble() * 2, 2 - (t - 6) / 8 * .14, .32, false);
            if (t >= 84) { int i = (t - 84) / 16; if (i < 4) { int t0 = 84 + i * 16, k = t - t0;
                    if (k == 0) { Vec3 at = around(c, 4, i * Math.PI / 2, 0); prop(c, at, c.tpos().subtract(at), .8f, 2, .6f, 0xffe08a, 14); fx(c, ParticleTypes.FLASH, at, 1, 0); }
                    jaronaStrike(c, t, t0, i == 3 ? 3 : 0); } }
        }, (p, t, m) -> { if (t < 84) pointLoop(p, t, "armR", 8); else jaronaPose(p, t - 84, 16); });
    }
    /** Last Jarona (Petal Feather mode): horizontal charges at varying heights; hitting him back cycles the seven colors. */
    static Attack fwLastJarona() {
        int[] seven = {0x8a4ac0, 0x3fd8d0, 0xff9a3a, 0x4ab84a, 0x3f7aff, 0xffe04a, 0xff7ab8};
        return phase("flowery_last_jarona", 150, -1, .1f, (c, t) -> {
            Vec3 r = side(toT(c));
            if (t >= 6 && t < 140 && (t - 6) % 19 == 0) { int i = (t - 6) / 19, s = i % 2 == 0 ? 1 : -1;
                c.tp(c.tpos().add(r.scale(7 * s)).add(0, (i % 3) * .9 - .5, 0)); fx(c, ParticleTypes.FLASH, c.eye(), 1, 0); c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, 1.5f); }
            if (t >= 6 && (t - 6) % 19 == 6) { c.dash(r.scale(-(((t - 6) / 19) % 2 == 0 ? 1 : -1) * 1.5)); c.sound(SoundEvents.PHANTOM_SWOOP, 1, 1.3f); }
            if (t >= 6 && (t - 6) % 19 == 9) { if (c.me.hurtTime > 0) { int col = seven[((t - 6) / 19) % 7];
                    for (int k = 0; k < 8; k++) prop(c, around(c, 3, k * Math.PI / 4, 1), toT(c), .5f, .5f, .5f, col, 20); }
                else if (c.me.position().distanceTo(c.target.position()) < 2) c.hit(c.target, 6); }
        }, (p, t, m) -> { float k = ((t + 13) % 19); p.r("body", 1.2f * ramp(k, 0, 6), 0, 0).r("armL", -2.4f, 0, .4f).r("armR", -2.4f, 0, -.4f); });
    }

    // =====================================================================================================
    // TRASHY TRIO - Trashy (trash can), Ball (pink ball), Nubert (stretchy blob) as one dojo-challenge entity
    // =====================================================================================================
    static final int BAG = 0x2a2a2a, TRASH = 0x6a7a5a, PBALL = 0xff7ab7, NUB = 0xa0e8ff, BEAM = 0xfff8a0, LASER = 0xffe0f0;
    static CharacterDef trashyTrio() {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 6, 0, 8, 10, 8, 0x8a9aa0).box("lid", "body", 0, 5.6f, 0, 9, 1.2f, 9, 0x9aaab0).box("handle", "lid", 0, 1, 0, 3, 1, 1, 0x6a7a80)
            .box("head", "body", 0, 2, -4.1f, 5, 4, .4f, 0x8a9aa0).glow("grin", "head", 0, -1, -.3f, 3.6f, .7f, .3f, WHITE)
            .box("ball", "body", -8, -2, 0, 4, 4, 4, 0xff7ab8).glow("ballEye", "ball", 0, .5f, -2.1f, 1, 1, .2f, 0x101010)
            .ghost("nubert", "body", 8, -2, 0, 4.4f, 6, 4.4f, 0xa0e8ff, .85f).glow("nubEyes", "nubert", 0, 1, -2.3f, 2.6f, .6f, .2f, 0x101010)
            .limb("armL", "body", -5, 2, 0, 1.6f, 5, 1.6f, 0x8a9aa0).limb("armR", "body", 5, 2, 0, 1.6f, 5, 1.6f, 0x8a9aa0)
            .limb("legL", null, -2, 1, 0, 2, 1, 2, 0x5a6a70).limb("legR", null, 2, 1, 0, 2, 1, 2, 0x5a6a70);
        m = eyes(m, 1, 1, 1.2f, .8f, .8f, 0x101010);
        return fk("trashy_trio", "Trashy Trio").hp(130).boss().egg(0x8a9aa0, 0xff7ab8).size(1.6f, 1.6f).keep(6).rest(18)
            .model(m).attacks(ttRush(), ttBall(), ttNubert(), ttBeam(), ttBeamQ()).build();
    }
    static void litter(AttackCtx c, int n) {
        for (int k = 0; k < n; k++) c.proj().at(c.target.position().add((c.rnd.nextDouble() * 2 - 1) * 4, .3, (c.rnd.nextDouble() * 2 - 1) * 4))
            .box(.4f, .3f, .4f).color(TRASH).dmg(2).warn(10).life(220).spawn();
    }
    /** Five lasers bursting from a piece of trash. */
    static void trashBurst(AttackCtx c, Vec3 at) {
        for (int k = 0; k < 5; k++) { double a = k * Math.PI * 2 / 5 + c.rnd.nextDouble(); line(c, at.add(Math.cos(a) * 2, .2, Math.sin(a) * 2), new Vec3(Math.cos(a), 0, Math.sin(a)), 4, .2f, LASER, 4, 6, 5); }
        c.sound(SoundEvents.FIREWORK_ROCKET_BLAST, .5f, 1.6f);
    }
    static Attack ttRush() {
        return atk("trashy_trio_trashy_rush", 100, (c, t) -> {
            if (t == 10) { c.dash(c.tpos().subtract(c.pos()).normalize().scale(1.1)); c.sound(SoundEvents.IRON_DOOR_CLOSE, 1, .8f); }
            if (t == 18) { c.aoe(c.pos(), 1.8, 6, .6); fx(c, ParticleTypes.POOF, c.pos(), 10, .6); }
            if (t >= 24 && t < 60 && t % 6 == 0) { Vec3 from = c.eye(), to = c.target.position().add(c.rnd.nextGaussian() * 2.5, .3, c.rnd.nextGaussian() * 2.5);
                c.proj().at(from).vel(arcVel(from, to, 14, .03f)).box(.6f, .6f, .6f).color(BAG).dmg(3).gravity(.03f).life(14).spawn(); c.sound(SoundEvents.WOOL_PLACE, 1, .8f); }
            pop(c, BAG, at -> { for (int k = 0; k < 3; k++) c.proj().at(at.add(c.rnd.nextGaussian() * .8, 0, c.rnd.nextGaussian() * .8))
                    .vel(new Vec3(c.rnd.nextGaussian() * .05, 0, c.rnd.nextGaussian() * .05)).box(.4f, .3f, .4f).color(TRASH).dmg(2).life(220).spawn(); });
            each(c, TRASH, p -> p.setDeltaMovement(p.getDeltaMovement().scale(.85)));
        }, (p, t, m) -> { float w = ramp(t, 0, 10), fall = ramp(t, 16, 22), up = ramp(t, 70, 90);
            p.r("body", (-.3f * w + 1.6f * fall) * (1 - up), 0, 0).r("lid", -1.2f * fall * (1 - up), 0, 0); });
    }
    static Attack ttBall() {
        return atk("trashy_trio_ball_toss", 100, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t >= 10 && t < 60 && t % 7 == 3) { c.proj().at(c.tpos().add(r.scale(-4)).add(f.scale(c.rnd.nextGaussian())).add(0, 3, 0)).vel(r.scale(.16)).box(.5f, .5f, .5f).color(PBALL).dmg(3).gravity(.025f).life(90).spawn();
                c.sound(SoundEvents.SLIME_JUMP, .8f, 1.6f); }
            each(c, PBALL, p -> { Vec3 v = p.getDeltaMovement(), d = p.position().subtract(c.tpos());
                if (p.getY() < c.target.getY() + .25 && v.y < 0) v = new Vec3(v.x, -v.y * .95, v.z);
                if (Math.abs(d.dot(r)) > 4 && v.dot(r) * d.dot(r) > 0) v = v.subtract(r.scale(2 * v.dot(r)));
                p.setDeltaMovement(v); });
        }, (p, t, m) -> { p.o("ball", 0, Math.abs(sin(t, .45f)) * 4, 0).r("armL", -1.5f, 0, 0); });
    }
    static Attack ttNubert() {
        return atk("trashy_trio_nubert_spear", 100, (c, t) -> {
            Vec3 r = side(toT(c));
            if (t == 6) litter(c, 6);
            if (t == 16) { c.proj().at(c.target.position().add(r.scale(-3)).add(0, .5, 0)).vel(new Vec3(0, .3, 0).add(r.scale(.12))).box(.9f, .9f, .9f).color(NUB).dmg(5).warn(4).life(70).spawn();
                c.sound(SoundEvents.SLIME_SQUISH, 1, .7f); }
            each(c, NUB, p -> { Vec3 v = p.getDeltaMovement();
                if (p.getY() > c.target.getY() + 3.5 && v.y > 0 || p.getY() < c.target.getY() + .3 && v.y < 0) v = new Vec3(v.x, -v.y, v.z);
                p.setDeltaMovement(v); if (p.age() % 2 == 0) c.proj().at(p.position()).box(.7f, .7f, .7f).color(0xa0e8fe).dmg(4).life(8).spawn(); });
            detonate(c, NUB, TRASH, 1, at -> trashBurst(c, at));
        }, (p, t, m) -> p.r("nubert", 0, 0, 0).o("nubert", 0, 6 * env(t, 10, 20, 70, 90), 0));
    }
    static Attack ttBeam() {
        return atk("trashy_trio_trashy_beam", 110, (c, t) -> {
            Vec3 f = toT(c);
            if (t == 6) litter(c, 8);
            if (t == 20) { prop(c, c.eye().add(f.scale(6)), f, 2.2f, 2.2f, 12, 0xffffff, 18); c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.4f); }
            if (t == 38) { Vec3 mid = c.pos().add(0, .8, 0).add(f.scale(7));
                c.proj().at(mid).face(f).box(2.2f, 2.2f, 14).color(BEAM).dmg(9).life(16).pierce().spawn(); c.sound(SoundEvents.WARDEN_SONIC_BOOM, .7f, 1.4f); }
            if (t >= 38 && t < 54) detonate(c, BEAM, TRASH, 1.6, at -> trashBurst(c, at));
        }, (p, t, m) -> { p.r("body", 1.5f, 0, 0).r("lid", -1.4f * ramp(t, 10, 20), 0, 0).o("body", 0, -2, 0); });
    }
    static Attack ttBeamQ() {
        return atk("trashy_trio_trashy_beam_q", 110, (c, t) -> {
            Vec3 f = toT(c), r = side(f);
            if (t == 6) litter(c, 8);
            if (t >= 20 && t < 70) { double k = (t - 20) / 50.0; Vec3 at = c.target.position().add(f.scale(-4 + k * 8)).add(r.scale(Math.sin(k * Math.PI * 2) * 3)).add(0, .6, 0);
                c.proj().at(at).box(.9f, .9f, .9f).color(NUB).dmg(5).warn(2).life(30).pierce().spawn(); if (t % 6 == 0) c.sound(SoundEvents.SLIME_SQUISH, .8f, .8f + (float) k); }
            detonate(c, NUB, TRASH, 1, at -> trashBurst(c, at));
        }, (p, t, m) -> { p.r("body", 1.5f, 0, 0).o("body", 0, -2, 0).o("nubert", -8 + 8 * ramp(t, 10, 20), 0, -10 * ramp(t, 20, 70)); });
    }

    private DrCh5Plus() {}
}
