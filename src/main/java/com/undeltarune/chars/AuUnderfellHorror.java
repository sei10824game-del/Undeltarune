package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import java.util.List;

/** Dark AUs: Underfell (kill-or-be-killed), Horrortale (famine & madness), Disbelief Papyrus (4 phases), Insanity Sans.
 *  Canon/fan sources: Underfell & Horrortale fanon wikis, the Disbelief fan battle; thin spots invented in-style. */
public final class AuUnderfellHorror {
    // ---------------------------------------------------------------- tiny attack DSL
    interface Tk { void run(AttackCtx c, int t); }
    interface Ps { void run(Pose p, float t, UtMob m); }
    /** Attack usable while lo < hpFrac <= hi (phases). */
    static Attack atk(String id, int dur, float lo, float hi, Tk tk, Ps ps) {
        return new Attack(id, dur) {
            public void tick(AttackCtx c, int t) { tk.run(c, t); }
            public void pose(Pose p, float t, UtMob m) { ps.run(p, t, m); }
            public boolean usable(UtMob m) { float f = m.hpFrac(); return f > lo && f <= hi; }
        };
    }
    static Attack atk(String id, int dur, Tk tk, Ps ps) { return atk(id, dur, -1, 2, tk, ps); }

    static float cl(float v) { return v < 0 ? 0 : v > 1 ? 1 : v; }
    static float up(float t, float a, float b) { return cl((t - a) / (b - a)); }
    /** 0->1 wind-up over [0,a], hold, 1->0 recovery over [b,e]. */
    static float env(float t, float a, float b, float e) { return Math.min(up(t, 0, a), 1 - up(t, b, e)); }
    static float sin(float t, float f) { return (float) Math.sin(t * f); }

    static Vec3 dir(AttackCtx c) { Vec3 d = c.tpos().subtract(c.pos()); d = new Vec3(d.x, 0, d.z); return d.lengthSqr() < 1e-4 ? c.fwd() : d.normalize(); }
    static Vec3 side(Vec3 d) { return new Vec3(-d.z, 0, d.x); }
    static Vec3 ring(Vec3 c, double a, double r, double y) { return c.add(Math.cos(a) * r, y, Math.sin(a) * r); }
    static Vec3 feet(AttackCtx c) { return c.target.position(); }

    static UtProjectile shot(AttackCtx c, Vec3 at, Vec3 vel, float w, float h, float l, int col, float dmg, int warn, int life) {
        return c.proj().at(at).vel(vel).box(w, h, l).color(col).dmg(dmg).warn(warn).life(life).spawn();
    }
    /** Upright bone (moves sideways/forward). */
    static UtProjectile bone(AttackCtx c, Vec3 at, Vec3 vel, float h, int col, float dmg, int warn, int life) {
        return shot(c, at, vel, .22f, h, .22f, col, dmg, warn, life);
    }
    /** Bone/spear that flies along its own length. */
    static UtProjectile lance(AttackCtx c, Vec3 at, Vec3 vel, float len, float thick, int col, float dmg, int warn, int life) {
        return shot(c, at, vel, thick, thick, len, col, dmg, warn, life);
    }
    static UtProjectile blueBone(AttackCtx c, Vec3 at, Vec3 vel, float h, float dmg, int warn, int life) {
        return c.proj().at(at).vel(vel).box(.22f, h, .22f).blue().dmg(dmg).warn(warn).life(life).spawn();
    }
    static UtProjectile orangeBone(AttackCtx c, Vec3 at, Vec3 vel, float h, float dmg, int warn, int life) {
        return c.proj().at(at).vel(vel).box(.22f, h, .22f).orange().dmg(dmg).warn(warn).life(life).spawn();
    }
    /** Recolorable Gaster Blaster: skull, jaw, eye glints, charge telegraph then beam. */
    static void blaster(AttackCtx c, Vec3 at, Vec3 d, int charge, int fire, float dmg, float width, int skull, int eye, int beam) {
        d = d.normalize(); double len = 28; int life = charge + fire + 6;
        c.proj().at(at).face(d).box(1.1f, 1f, 1.2f).color(skull).dmg(0).life(life).spawn();
        c.proj().at(at.add(d.scale(.3)).add(0, -.55, 0)).face(d).box(.8f, .35f, .9f).color(skull & 0xdadada).dmg(0).life(life).spawn();
        c.proj().at(at.add(d.scale(.55)).add(0, .15, 0)).face(d).box(.9f, .2f, .12f).color(eye).dmg(0).life(life).spawn();
        c.proj().at(at.add(d.scale(len / 2 + .6))).face(d).box(width, width, (float) len).color(beam).dmg(dmg).warn(charge).life(charge + fire).pierce().spawn();
        c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1.1f, 1.7f);
    }
    static void dust(AttackCtx c, Vec3 at, int rgb, int n, double spread) {
        c.particle(new DustParticleOptions(new Vector3f((rgb >> 16 & 255) / 255f, (rgb >> 8 & 255) / 255f, (rgb & 255) / 255f), 1.2f), at, n, spread, .02);
    }
    static void slam(AttackCtx c, double vy) { c.target.setDeltaMovement(c.target.getDeltaMovement().multiply(.2, 0, .2).add(0, vy, 0)); c.target.hurtMarked = true; }
    static void fling(AttackCtx c, Vec3 v) { c.target.setDeltaMovement(v); c.target.hurtMarked = true; }

    // palette
    static final int RED = 0xd01818, BLOOD = 0x8a0b0b, BLACK = 0x161616, BONE = 0xf2f2f2, GOLD = 0xf2c200, FIRE = 0xff5a1a, DFIRE = 0xc21a1a;

    // ---------------------------------------------------------------- shared models
    /** Short skeleton (Sans silhouette). */
    static ModelDef sansBody(int jacket, int shirt, int shorts, int eyeL, int eyeR, int hood) {
        return new ModelDef()
            .box("body", null, 0, 8, 0, 8, 7, 5, jacket)
            .box("shirt", "body", 0, 0, -2.3f, 4, 6, .6f, shirt)
            .box("head", "body", 0, 9.5f, 0, 9, 7, 7, BONE)
            .glow("eyeL", "head", -2, .5f, -3.6f, 1.6f, 1.6f, .4f, eyeL)
            .glow("eyeR", "head", 2, .5f, -3.6f, 1.6f, 1.6f, .4f, eyeR)
            .box("grin", "head", 0, -2, -3.6f, 5, .7f, .4f, 0x222222)
            .box("hood", "body", 0, 3.4f, 0, 9, 1.5f, 6, hood)
            .limb("armL", "body", -5, 3, 0, 2.4f, 6, 2.4f, jacket)
            .limb("armR", "body", 5, 3, 0, 2.4f, 6, 2.4f, jacket)
            .limb("legL", null, -2, 5, 0, 3, 5, 3.5f, shorts)
            .limb("legR", null, 2, 5, 0, 3, 5, 3.5f, shorts);
    }
    /** Tall skeleton (Papyrus silhouette). */
    static ModelDef papBody(int armor, int scarf, int pants, int boots, int eyes) {
        return new ModelDef()
            .box("body", null, 0, 15, 0, 7, 10, 4, armor)
            .box("pauldronL", "body", -4.5f, 4, 0, 3, 2, 5, armor)
            .box("pauldronR", "body", 4.5f, 4, 0, 3, 2, 5, armor)
            .box("scarf", "body", 0, 5.2f, 0, 8, 1.6f, 5, scarf)
            .box("cape", "scarf", 2, -3, 2.6f, 2, 6, .5f, scarf)
            .box("head", "body", 0, 9, 0, 6, 7, 6, BONE)
            .box("jaw", "head", 0, -3.6f, -.4f, 5, 1.4f, 5, 0xe2e2e2)
            .glow("eyeL", "head", -1.5f, .6f, -3.1f, 1.4f, 1.2f, .3f, eyes)
            .glow("eyeR", "head", 1.5f, .6f, -3.1f, 1.4f, 1.2f, .3f, eyes)
            .limb("armL", "body", -4.5f, 4, 0, 1.6f, 10, 1.6f, BONE)
            .limb("armR", "body", 4.5f, 4, 0, 1.6f, 10, 1.6f, BONE)
            .box("gloveL", "armL", 0, -9, 0, 2.4f, 2.4f, 2.4f, scarf)
            .box("gloveR", "armR", 0, -9, 0, 2.4f, 2.4f, 2.4f, scarf)
            .limb("legL", null, -1.8f, 10, 0, 2, 10, 2, pants)
            .limb("legR", null, 1.8f, 10, 0, 2, 10, 2, pants)
            .box("bootL", "legL", 0, -9, -.5f, 2.8f, 2.4f, 3.6f, boots)
            .box("bootR", "legR", 0, -9, -.5f, 2.8f, 2.4f, 3.6f, boots);
    }
    /** Fish knight (Undyne). */
    static ModelDef undyneBody(int armor, int trim, int hair, int skin, int eye) {
        return new ModelDef()
            .box("body", null, 0, 15, 0, 8, 10, 5, armor)
            .box("belt", "body", 0, -4, 0, 8.4f, 1.2f, 5.4f, trim)
            .box("pauldronL", "body", -5, 4, 0, 3.4f, 2.4f, 5.4f, armor)
            .box("pauldronR", "body", 5, 4, 0, 3.4f, 2.4f, 5.4f, armor)
            .box("head", "body", 0, 9, 0, 6, 7, 6, skin)
            .box("fin", "head", -3.4f, 1, .5f, .6f, 4, 3, skin)
            .box("fin2", "head", 3.4f, 1, .5f, .6f, 4, 3, skin)
            .box("hair", "head", 0, 3.4f, 1.5f, 5, 2, 4, hair)
            .box("ponytail", "head", 0, 1.5f, 3.8f, 2.5f, 6, 2.5f, hair)
            .box("patch", "head", -1.5f, .8f, -3.1f, 2, 2, .3f, BLACK)
            .glow("eyeR", "head", 1.5f, .8f, -3.1f, 1.6f, 1.2f, .3f, eye)
            .box("teeth", "head", 0, -2, -3.1f, 3.6f, .7f, .3f, BONE)
            .limb("armL", "body", -5, 4, 0, 2.6f, 10, 2.6f, armor)
            .limb("armR", "body", 5, 4, 0, 2.6f, 10, 2.6f, armor)
            .limb("legL", null, -2, 10, 0, 2.8f, 10, 2.8f, armor)
            .limb("legR", null, 2, 10, 0, 2.8f, 10, 2.8f, armor);
    }
    /** Goat monster (Toriel / Asgore base). */
    static ModelDef goatBody(int robe, int fur, int eye, float sz) {
        return new ModelDef()
            .box("body", null, 0, 15 * sz, 0, 10 * sz, 12 * sz, 6 * sz, robe)
            .box("head", "body", 0, 10 * sz, 0, 7 * sz, 7 * sz, 7 * sz, fur)
            .box("snout", "head", 0, -1.5f * sz, -3.8f * sz, 3.5f * sz, 2.5f * sz, 2 * sz, fur)
            .box("earL", "head", -4.6f * sz, 1 * sz, 0, 3 * sz, 1.2f * sz, 2 * sz, fur)
            .box("earR", "head", 4.6f * sz, 1 * sz, 0, 3 * sz, 1.2f * sz, 2 * sz, fur)
            .glow("eyeL", "head", -1.7f * sz, 1 * sz, -3.6f * sz, 1.4f * sz, 1 * sz, .3f, eye)
            .glow("eyeR", "head", 1.7f * sz, 1 * sz, -3.6f * sz, 1.4f * sz, 1 * sz, .3f, eye)
            .limb("armL", "body", -6.2f * sz, 5 * sz, 0, 2.8f * sz, 10 * sz, 2.8f * sz, robe)
            .limb("armR", "body", 6.2f * sz, 5 * sz, 0, 2.8f * sz, 10 * sz, 2.8f * sz, robe)
            .box("pawL", "armL", 0, -10 * sz, 0, 2.6f * sz, 2 * sz, 2.6f * sz, fur)
            .box("pawR", "armR", 0, -10 * sz, 0, 2.6f * sz, 2 * sz, 2.6f * sz, fur)
            .limb("legL", null, -2.5f * sz, 9 * sz, 0, 3.4f * sz, 9 * sz, 3.4f * sz, robe)
            .limb("legR", null, 2.5f * sz, 9 * sz, 0, 3.4f * sz, 9 * sz, 3.4f * sz, robe);
    }
    /** Short yellow lizard scientist (Alphys). */
    static ModelDef alphysBody(int coat, int under, int lens) {
        return new ModelDef()
            .box("body", null, 0, 8, 0, 9, 8, 6, coat)
            .box("under", "body", 0, -.5f, -2.8f, 4, 6, .6f, under)
            .box("head", "body", 0, 8, 0, 8, 6, 7, 0xe6cc3a)
            .box("snout", "head", 0, -1.5f, -4, 5, 2.5f, 2, 0xe6cc3a)
            .glow("lensL", "head", -1.8f, .8f, -3.7f, 2.4f, 2, .3f, lens)
            .glow("lensR", "head", 1.8f, .8f, -3.7f, 2.4f, 2, .3f, lens)
            .box("tail", "body", 0, -3, 4, 2.4f, 2.4f, 5, 0xe6cc3a)
            .limb("armL", "body", -5.6f, 3, 0, 2.2f, 6, 2.2f, coat)
            .limb("armR", "body", 5.6f, 3, 0, 2.2f, 6, 2.2f, coat)
            .limb("legL", null, -2, 4, 0, 3, 4, 3, 0xe6cc3a)
            .limb("legR", null, 2, 4, 0, 3, 4, 3, 0xe6cc3a);
    }

    // ================================================================ REGISTER
    public static void register(List<CharacterDef> out) {
        underfell(out); horrortale(out); disbelief(out); insanity(out);
    }

    // ================================================================ UNDERFELL
    static void underfell(List<CharacterDef> out) {
        out.add(CharacterDef.of("underfell_sans", "Sans (Underfell)", Faction.UNDERGROUND).hp(140).boss().keep(7).rest(12).speed(.3f)
            .egg(BLACK, RED).size(.7f, 1.1f).scale(.9f)
            .model(sansBody(0x1d1d1d, RED, BLACK, 0x111111, 0xff1a1a, 0xc9a65a)
                .box("tooth", "head", 1.5f, -2, -3.7f, .9f, .9f, .3f, GOLD)
                .box("stripe", "legL", -1.6f, -2.5f, 0, .3f, 5, 3.6f, GOLD)
                .box("collar", "body", 0, 2.6f, -2.4f, 6, 1, .5f, 0x555555))
            .attacks(ufSansRedBones(), ufSansBlasterJaws(), ufSansGravity(), ufSansSharpGaps(), ufSansBloodRain(), ufSansFinal()).build());

        out.add(CharacterDef.of("underfell_papyrus", "Papyrus (Underfell)", Faction.ROYAL_GUARD).hp(120).boss().keep(6).rest(14)
            .egg(BLACK, RED).size(.7f, 1.9f)
            .model(papBody(0x1a1a1a, RED, 0x2a2a2a, 0x8a0b0b, RED)
                .box("spikeL", "pauldronL", -1, 1.4f, 0, 1, 1.6f, 1, BLOOD)
                .box("spikeR", "pauldronR", 1, 1.4f, 0, 1, 1.6f, 1, BLOOD)
                .box("crack", "head", -1.5f, 2.5f, -3.1f, .4f, 2.2f, .3f, 0x333333))
            .attacks(ufPapSpikeWall(), ufPapBlueTrap(), ufPapBoneJavelins(), ufPapCage(), ufPapSpecial()).build());

        out.add(CharacterDef.of("underfell_undyne", "Undyne (Underfell)", Faction.ROYAL_GUARD).hp(160).boss().keep(6).rest(12).speed(.32f)
            .egg(BLACK, 0x3fa0d8).size(.75f, 1.95f)
            .model(undyneBody(0x1c1c1c, RED, 0xb0101a, 0x3a8cc8, GOLD)
                .box("spikes", "pauldronR", 1, 1.6f, 0, 1.2f, 1.8f, 1.2f, RED)
                .box("spikes2", "pauldronL", -1, 1.6f, 0, 1.2f, 1.8f, 1.2f, RED)
                .glow("spear", "armR", 0, -9, -4, .5f, .5f, 12, RED))
            .attacks(ufUndyneVolley(), ufUndyneGreenShield(), ufUndynePillars(), ufUndyneRain(), ufUndyneCaptain()).build());

        out.add(CharacterDef.of("underfell_alphys", "Alphys (Underfell)", Faction.ROYAL_SCIENCE).hp(100).boss().keep(9).rest(16)
            .egg(0xe6cc3a, BLACK).size(.8f, 1.1f)
            .model(alphysBody(0x2a2a2a, RED, 0xff3030)
                .box("scar", "head", 2.6f, 2, -3.6f, .4f, 2.5f, .3f, BLOOD)
                .glow("remote", "armR", 0, -6, -.8f, 1.2f, 2, .8f, RED))
            .attacks(ufAlphysLaserGrid(), ufAlphysDrones(), ufAlphysTesla(), ufAlphysAmalgam()).build());

        out.add(CharacterDef.of("underfell_asgore", "Asgore (Underfell)", Faction.UNDERGROUND).hp(260).boss().keep(7).rest(14).speed(.22f)
            .egg(BLACK, GOLD).size(1.1f, 2.9f).scale(1.1f)
            .model(goatBody(0x1a1a1a, 0xe8e2d6, RED, 1.35f)
                .box("cape", "body", 0, 0, 5, 15, 20, 1, BLOOD)
                .box("crown", "head", 0, 6.2f, 0, 6, 1.6f, 6, GOLD)
                .box("hornL", "head", -4, 6, 0, 1.5f, 4, 1.5f, 0xe0d0a0)
                .box("hornR", "head", 4, 6, 0, 1.5f, 4, 1.5f, 0xe0d0a0)
                .box("beard", "head", 0, -4.5f, -3, 5, 3, 2, GOLD)
                .box("spikeL", "body", -8, 8, 0, 3, 3, 6, 0x333333)
                .box("spikeR", "body", 8, 8, 0, 3, 3, 6, 0x333333)
                .box("trident", "armR", 0, -12, -4, .8f, .8f, 26, RED)
                .box("tines", "trident", 0, 0, -13, 3, .8f, 1.6f, RED))
            .attacks(ufAsgoreSweep(), ufAsgoreFireRing(), ufAsgoreHands(), ufAsgoreSlam(), ufAsgoreKillOrBeKilled()).build());

        out.add(CharacterDef.of("underfell_toriel", "Toriel (Underfell)", Faction.UNDERGROUND).hp(110).boss().keep(7).rest(16)
            .egg(BLOOD, BLACK).size(.8f, 2.1f)
            .model(goatBody(0x3b0d14, 0xeeeeee, RED, 1f)
                .box("rune", "body", 0, 1.5f, -3.1f, 4, 3, .3f, 0x0a0a0a)
                .box("thornL", "armL", 0, -5, 0, 3.4f, .6f, 3.4f, 0x2b2b2b)
                .box("thornR", "armR", 0, -5, 0, 3.4f, .6f, 3.4f, 0x2b2b2b))
            .attacks(ufTorielHands(), ufTorielCircle(), ufTorielThorns(), ufTorielBarrage()).build());

        out.add(CharacterDef.of("underfell_mettaton", "Mettaton EX (Underfell)", Faction.UNDERGROUND).hp(130).boss().keep(8).rest(12).speed(.34f)
            .egg(BLACK, 0xff2a6a).size(.7f, 2.1f)
            .model(new ModelDef()
                .box("body", null, 0, 17, 0, 7, 9, 4, 0x1a1a1a)
                .glow("core", "body", 0, 1.5f, -2.1f, 3, 3, .4f, 0xff2a2a)
                .box("head", "body", 0, 8.5f, 0, 6, 6, 6, 0x2a2a2a)
                .box("hair", "head", -1.2f, 1, -2.4f, 4.6f, 5, 2, 0x111111)
                .glow("eyeR", "head", 1.6f, .5f, -3.1f, 1.4f, 1, .3f, RED)
                .box("shoulders", "body", 0, 4, 0, 10, 2, 4, 0xff2a6a)
                .limb("armL", "body", -5.5f, 4, 0, 1.8f, 10, 1.8f, 0x1a1a1a)
                .limb("armR", "body", 5.5f, 4, 0, 1.8f, 10, 1.8f, 0x1a1a1a)
                .limb("legL", null, -1.8f, 12.5f, 0, 2, 12.5f, 2, 0x1a1a1a)
                .limb("legR", null, 1.8f, 12.5f, 0, 2, 12.5f, 2, 0x1a1a1a)
                .box("heelL", "legL", 0, -12, -.6f, 2.2f, 1.2f, 3.4f, 0xff2a6a)
                .box("heelR", "legR", 0, -12, -.6f, 2.2f, 1.2f, 3.4f, 0xff2a6a))
            .attacks(ufMttLegRain(), ufMttBombs(), ufMttHeartLaser(), ufMttMinis(), ufMttFinale()).build());

        out.add(CharacterDef.of("underfell_flowey", "Flowey (Underfell)", Faction.FLOWEY_CHAOS).hp(70).boss().keep(9).rest(14).speed(.18f)
            .egg(0x2a6a1a, RED).size(.6f, .9f)
            .model(new ModelDef()
                .box("body", null, 0, 3, 0, 1.4f, 6, 1.4f, 0x2e7a22)
                .box("leafL", "body", -2, -1.5f, 0, 3, .4f, 1.6f, 0x2e7a22)
                .box("leafR", "body", 2, -1.5f, 0, 3, .4f, 1.6f, 0x2e7a22)
                .box("head", "body", 0, 6.5f, 0, 5, 5, 2, 0xf4f0e0)
                .box("petals", "head", 0, 0, .8f, 9, 9, .6f, 0xb02020)
                .glow("eyeL", "head", -1.1f, .8f, -1.1f, .9f, 1.4f, .3f, BLACK)
                .glow("eyeR", "head", 1.1f, .8f, -1.1f, .9f, 1.4f, .3f, RED)
                .box("fangs", "head", 0, -1.4f, -1.1f, 3, .8f, .3f, 0x202020))
            .attacks(ufFloweyPellets(), ufFloweyVines(), ufFloweySpiral(), ufFloweyLaugh()).build());

        out.add(CharacterDef.of("underfell_muffet", "Muffet (Underfell)", Faction.UNDERGROUND).hp(110).boss().keep(7).rest(14)
            .egg(0x4a1838, RED).size(.7f, 1.5f)
            .model(new ModelDef()
                .box("body", null, 0, 11, 0, 6, 6, 4, 0x2a0a1a)
                .box("skirt", "body", 0, -4, 0, 9, 3, 7, BLOOD)
                .box("head", "body", 0, 6.5f, 0, 6, 6, 6, 0x7a3a8a)
                .box("pigtailL", "head", -4, 0, 1, 2, 4, 2, 0x111111)
                .box("pigtailR", "head", 4, 0, 1, 2, 4, 2, 0x111111)
                .glow("eye1", "head", -1.8f, 1.6f, -3.1f, 1, 1, .3f, RED)
                .glow("eye2", "head", 1.8f, 1.6f, -3.1f, 1, 1, .3f, RED)
                .glow("eye3", "head", 0, 2.2f, -3.1f, .8f, .8f, .3f, RED)
                .glow("eyeL", "head", -1.3f, .2f, -3.1f, 1.1f, 1.1f, .3f, RED)
                .glow("eyeR", "head", 1.3f, .2f, -3.1f, 1.1f, 1.1f, .3f, RED)
                .box("fangs", "head", 0, -1.8f, -3.1f, 2, .8f, .3f, BONE)
                .limb("armL", "body", -4, 2, 0, 1.4f, 6, 1.4f, 0x7a3a8a)
                .limb("armR", "body", 4, 2, 0, 1.4f, 6, 1.4f, 0x7a3a8a)
                .limb("arm2L", "body", -4, 0, 0, 1.4f, 6, 1.4f, 0x7a3a8a)
                .limb("arm2R", "body", 4, 0, 0, 1.4f, 6, 1.4f, 0x7a3a8a)
                .limb("arm3L", "body", -4, -2, 0, 1.4f, 5, 1.4f, 0x7a3a8a)
                .limb("arm3R", "body", 4, -2, 0, 1.4f, 5, 1.4f, 0x7a3a8a)
                .limb("legL", null, -1.5f, 6, 0, 2, 6, 2, 0x7a3a8a)
                .limb("legR", null, 1.5f, 6, 0, 2, 6, 2, 0x7a3a8a))
            .attacks(ufMuffetStrings(), ufMuffetCroissant(), ufMuffetPet(), ufMuffetTeaRain()).build());

        out.add(CharacterDef.of("underfell_napstablook", "Napstablook (Underfell)", Faction.UNDERGROUND).hp(60).keep(8).rest(20).speed(.2f)
            .egg(0x3a3a3a, RED).size(.7f, 1.3f)
            .model(new ModelDef()
                .ghost("body", null, 0, 10, 0, 8, 16, 6, 0x444444, .8f)
                .box("head", "body", 0, 5, -3.2f, 6, 3, .3f, 0x444444)
                .glow("eyeL", "head", -1.4f, 0, -.2f, 1.6f, .7f, .2f, RED)
                .glow("eyeR", "head", 1.4f, 0, -.2f, 1.6f, .7f, .2f, RED)
                .ghost("hem", "body", 0, -8.5f, 0, 9, 1.5f, 7, 0x333333, .6f))
            .attacks(ufBlookTears(), ufBlookStorm(), ufBlookHeartache()).build());

        out.add(CharacterDef.of("underfell_grillby", "Grillby (Underfell)", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(90).keep(5).rest(16)
            .egg(BLACK, DFIRE).size(.7f, 1.9f)
            .model(new ModelDef()
                .box("body", null, 0, 15, 0, 7, 10, 4, 0x111111)
                .box("vest", "body", 0, 0, -2.1f, 4, 9, .3f, 0x5a0a0a)
                .glow("head", "body", 0, 8.5f, 0, 6, 7, 6, DFIRE)
                .glow("flame", "head", 0, 4.5f, 0, 4, 3, 4, 0xff8a2a)
                .box("glasses", "head", 0, .8f, -3.1f, 5, 1.4f, .3f, 0xeeeeee)
                .limb("armL", "body", -4.6f, 4, 0, 2.2f, 10, 2.2f, 0x111111)
                .limb("armR", "body", 4.6f, 4, 0, 2.2f, 10, 2.2f, 0x111111)
                .glow("handR", "armR", 0, -10, 0, 2, 2, 2, DFIRE)
                .limb("legL", null, -1.8f, 10, 0, 2.4f, 10, 2.4f, 0x111111)
                .limb("legR", null, 1.8f, 10, 0, 2.4f, 10, 2.4f, 0x111111))
            .attacks(ufGrillbyBottles(), ufGrillbyFireWall(), ufGrillbyFlare()).build());
    }

    // ---- Underfell Sans: red bones, crimson blasters, red-soul gravity, KR-like blood rain
    static Attack ufSansRedBones() {
        return atk("underfell_sans_red_bones", 70, (c, t) -> {
            if (t == 2) dust(c, c.eye(), RED, 10, .3);
            if (t >= 8 && t < 48 && t % 4 == 0) {
                Vec3 d = dir(c), r = side(d); int gap = (t / 4) % 7 - 3;
                for (int k = -3; k <= 3; k++) {
                    if (k == gap || k == gap + 1) continue;
                    bone(c, c.pos().add(d.scale(2.5)).add(r.scale(k * .9)).add(0, .6, 0), d.scale(.95), 1.2f, RED, 5, 5, 40);
                }
                c.sound(SoundEvents.SKELETON_SHOOT, .8f, 1.2f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 50, 64);
            p.r("armR", -1.5f * s, 0, -.3f * s).r("body", 0, .25f * s * sin(t, .4f), 0).o("head", 0, -.4f * s, 0).o("eyeR", 0, .2f * sin(t, 2), 0);
        });
    }
    static Attack ufSansBlasterJaws() {
        return atk("underfell_sans_crimson_blasters", 95, (c, t) -> {
            if (t < 36 && t % 6 == 0) {
                int i = t / 6; double a = i * Math.PI / 3 + c.rnd.nextDouble() * .3;
                Vec3 at = ring(c.tpos(), a, 6.5, 2 + c.rnd.nextDouble() * 1.5);
                blaster(c, at, c.tpos().subtract(at), 36 - t + 4, 14, 7, 1f, 0xe8e0e0, RED, 0xff6060);
            }
            if (t == 40) c.sound(SoundEvents.GENERIC_EXPLODE, .8f, 1.6f);
        }, (p, t, m) -> {
            float s = env(t, 8, 44, 60);
            p.r("armL", -2.7f * s, 0, .4f * s).r("armR", -2.7f * s, 0, -.4f * s).o("body", 0, 1.2f * s, 0).r("head", -.35f * s, 0, 0);
        });
    }
    static Attack ufSansGravity() {
        return atk("underfell_sans_red_gravity", 84, (c, t) -> {
            if (t == 4) dust(c, c.tpos(), RED, 20, .5);
            if (t == 10) { slam(c, -1.6); c.sound(SoundEvents.ANVIL_LAND, 1, .5f); }
            if (t >= 14 && t < 60 && t % 5 == 0) {
                Vec3 base = feet(c).add(c.rnd.nextGaussian() * 2, -1.4, c.rnd.nextGaussian() * 2);
                lance(c, base, new Vec3(0, .5, 0), 1.8f, .25f, RED, 5, 8, 18);
            }
            if (t == 62) { fling(c, side(dir(c)).scale(1.2).add(0, .6, 0)); c.sound(SoundEvents.ANVIL_LAND, .7f, .8f); }
        }, (p, t, m) -> {
            float s = t < 10 ? up(t, 0, 10) : 1 - up(t, 10, 26);
            float s2 = env(t - 56, 6, 10, 20);
            p.r("armR", -1.4f * s, 0, -.2f).o("armR", 0, 1.2f * s, 0).r("head", .25f * s, 0, 0).r("armL", -1.2f * s2, 0, 1.2f * s2);
        });
    }
    static Attack ufSansSharpGaps() {
        return atk("underfell_sans_sharpened_gaps", 80, (c, t) -> {
            if (t % 18 == 4 && t < 60) {
                Vec3 r = side(dir(c)), f = dir(c); int gap = c.rnd.nextInt(5) - 2; boolean blue = (t / 18) % 2 == 1;
                for (int s = -1; s <= 1; s += 2) for (int k = -3; k <= 3; k++) {
                    if (Math.abs(k - gap) <= 1) continue;
                    Vec3 at = c.tpos().add(r.scale(s * 7)).add(f.scale(k * .8)).add(0, -.6, 0);
                    if (blue) blueBone(c, at, r.scale(-s * .6), 2.2f, 5, 10, 30); else bone(c, at, r.scale(-s * .6), 2.2f, RED, 5, 10, 30);
                }
            }
        }, (p, t, m) -> {
            p.r("armL", -1f, 0, .3f).r("armR", -1f, 0, -.3f).o("body", 0, sin(t, .6f) * .3f, 0).r("head", 0, sin(t, .3f) * .2f, 0);
        });
    }
    static Attack ufSansBloodRain() {
        return atk("underfell_sans_blood_rain", 90, -1, .6f, (c, t) -> {
            if (t < 70 && t % 3 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 3.5, 9, c.rnd.nextGaussian() * 3.5);
                lance(c, at, new Vec3(0, -.75, 0), 1.3f, .2f, t % 9 == 0 ? BONE : RED, 4, 10, 30);
                if (t % 9 == 0) dust(c, at.add(0, -8.5, 0), RED, 4, .4);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 72, 86);
            p.r("armL", -3f * s, 0, .2f).r("armR", -3f * s, 0, -.2f).r("head", -.4f * s, 0, 0);
        });
    }
    /** Kill-or-be-killed finale: blaster cross + bone ring tightening. */
    static Attack ufSansFinal() {
        return atk("underfell_sans_kill_or_be_killed", 120, -1, .3f, (c, t) -> {
            if (t == 0) c.sound(SoundEvents.WITHER_SPAWN, .6f, 1.8f);
            if (t < 60 && t % 15 == 0) {
                double base = t / 15 * Math.PI / 4;
                for (int i = 0; i < 4; i++) { Vec3 at = ring(c.tpos(), base + i * Math.PI / 2, 7, 1.5); blaster(c, at, c.tpos().subtract(at), 18, 10, 8, 1.1f, 0xe0d0d0, RED, 0xff3030); }
            }
            if (t >= 60 && t < 100 && t % 10 == 0) for (int i = 0; i < 12; i++) {
                Vec3 at = ring(feet(c), i * Math.PI / 6, 6, .8);
                bone(c, at, c.tpos().subtract(at).multiply(1, 0, 1).normalize().scale(.22), 1.6f, RED, 5, 6, 30);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 100, 118);
            p.r("armL", -2.2f * s, -.5f * s, .6f * s).r("armR", -2.2f * s, .5f * s, -.6f * s).o("body", 0, 2 * s + sin(t, .3f) * .4f, 0).r("head", -.3f * s, 0, sin(t, .5f) * .1f);
        });
    }

    // ---- Underfell Papyrus: spiked bones, blue trap, javelins, cage, red-blaster "special attack"
    static Attack ufPapSpikeWall() {
        return atk("underfell_papyrus_spike_wall", 70, (c, t) -> {
            if (t >= 10 && t < 50 && t % 10 == 0) {
                Vec3 d = dir(c), r = side(d); boolean high = (t / 10) % 2 == 0;
                for (int k = -4; k <= 4; k++)
                    bone(c, c.pos().add(d.scale(2)).add(r.scale(k * .7)).add(0, high ? 1.6 : .4, 0), d.scale(.8), high ? 1.4f : .8f, high ? BONE : RED, 5, 6, 40);
                c.sound(SoundEvents.SKELETON_SHOOT, .9f, .9f);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 50, 66);
            p.r("armR", -1.6f * s, 0, -.2f).r("armL", -.4f * s, 0, .5f * s).r("body", 0, -.3f * s, 0).o("cape", 0, 0, sin(t, .5f) * .6f);
        });
    }
    static Attack ufPapBlueTrap() {
        return atk("underfell_papyrus_blue_trap", 80, (c, t) -> {
            if (t == 6) { slam(c, -1.2); dust(c, c.tpos(), 0x3fa7ff, 18, .5); c.sound(SoundEvents.ANVIL_LAND, .9f, .7f); }
            if (t >= 14 && t < 64 && t % 8 == 0) {
                Vec3 d = dir(c), r = side(d); int s = (t / 8) % 2 == 0 ? 1 : -1;
                for (int k = -2; k <= 2; k++) blueBone(c, c.tpos().add(r.scale(s * 7)).add(d.scale(k * .9)).add(0, -.7, 0), r.scale(-s * .55), 1.8f, 5, 8, 30);
            }
        }, (p, t, m) -> {
            float s = t < 6 ? up(t, 0, 6) : 1 - up(t, 6, 20);
            p.r("armR", -2.6f * s, 0, -.2f).o("body", 0, -.8f * s, 0).r("armL", -.8f, 0, .9f).r("head", .2f * s, 0, 0);
        });
    }
    static Attack ufPapBoneJavelins() {
        return atk("underfell_papyrus_bone_javelins", 76, (c, t) -> {
            if (t >= 12 && t < 60 && t % 8 == 4) {
                Vec3 from = c.eye().add(side(dir(c)).scale(.8)).add(0, .6, 0);
                lance(c, from, c.aim(from).scale(1.2), 2.4f, .25f, BONE, 6, 4, 40);
                c.sound(SoundEvents.TRIDENT_THROW, 1, .8f);
            }
        }, (p, t, m) -> {
            float ph = ((t - 4) % 8) / 8f, s = env(t, 10, 60, 72);
            p.r("armR", (-2.8f + 1.8f * ph) * s, 0, -.2f * s).r("body", 0, (-.4f + .5f * ph) * s, 0);
        });
    }
    static Attack ufPapCage() {
        return atk("underfell_papyrus_bone_cage", 84, -1, .7f, (c, t) -> {
            if (t == 8) for (int i = 0; i < 14; i++) bone(c, ring(feet(c), i * Math.PI / 7, 4, .9), Vec3.ZERO, 2.2f, BONE, 6, 14, 70);
            if (t >= 24 && t < 72 && t % 6 == 0) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 1.6, -1.2, c.rnd.nextGaussian() * 1.6);
                lance(c, at, new Vec3(0, .45, 0), 1.6f, .24f, RED, 5, 10, 16);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 72, 82);
            p.r("armL", -1.2f * s, 0, 1f * s).r("armR", -1.2f * s, 0, -1f * s).r("head", -.2f * s, 0, 0).o("body", 0, sin(t, .4f) * .3f, 0);
        });
    }
    /** "Boss's special attack": huge red bone + blaster pair. */
    static Attack ufPapSpecial() {
        return atk("underfell_papyrus_special_attack", 100, -1, .4f, (c, t) -> {
            if (t == 0) dust(c, c.eye(), RED, 30, .6);
            if (t == 20) {
                Vec3 d = dir(c);
                c.proj().at(c.pos().add(d.scale(3)).add(0, 3, 0)).vel(d.scale(.5)).box(1.2f, 6f, 1.2f).color(RED).dmg(10).warn(14).life(60).pierce().spawn();
                c.sound(SoundEvents.WITHER_SHOOT, 1, .6f);
            }
            if (t == 50) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = c.tpos().add(side(dir(c)).scale(s * 6)).add(0, 2, 0);
                blaster(c, at, c.tpos().subtract(at), 16, 14, 8, 1f, 0xdddddd, RED, 0xff5050);
            }
        }, (p, t, m) -> {
            float s = env(t, 18, 80, 96);
            p.r("armR", -3f * s, 0, -.1f).r("armL", -3f * s, 0, .1f).o("body", 0, 1.5f * s, 0).o("cape", 0, 1 * s, 1.5f * s);
        });
    }

    // ---- Underfell Undyne: red spears, green-soul shield, pillars, rain, "Captain" phase
    static Attack ufUndyneVolley() {
        return atk("underfell_undyne_spear_volley", 70, (c, t) -> {
            if (t >= 10 && t < 50 && t % 5 == 0) {
                Vec3 from = c.eye().add(side(dir(c)).scale((c.rnd.nextDouble() - .5) * 4)).add(0, 1 + c.rnd.nextDouble(), 0);
                lance(c, from, c.aim(from).scale(1.1), 2.2f, .2f, RED, 5, 6, 40);
                c.sound(SoundEvents.TRIDENT_THROW, .9f, 1.2f);
            }
        }, (p, t, m) -> {
            float ph = (t % 5) / 5f, s = env(t, 10, 50, 66);
            p.r("armR", (-2.6f + 1.6f * ph) * s, 0, -.2f).r("body", 0, -.3f * s, 0).o("ponytail", 0, 0, sin(t, .6f) * .5f);
        });
    }
    static Attack ufUndyneGreenShield() {
        return atk("underfell_undyne_green_soul", 96, (c, t) -> {
            if (t == 4) { c.target.setDeltaMovement(Vec3.ZERO); c.target.hurtMarked = true; dust(c, c.tpos(), 0x30d040, 25, .6); c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.4f); }
            if (t >= 12 && t < 84 && t % 6 == 0) {
                double a = c.rnd.nextInt(4) * Math.PI / 2; Vec3 at = ring(c.tpos(), a, 9, 0);
                lance(c, at, c.tpos().subtract(at).normalize().scale(.55), 1.6f, .22f, (t / 6) % 3 == 0 ? GOLD : RED, 5, 8, 22);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 84, 94);
            p.r("armL", -1.6f * s, 0, .4f).r("armR", -1.6f * s, 0, -.4f).r("head", -.1f * s, sin(t, .2f) * .3f, 0);
        });
    }
    static Attack ufUndynePillars() {
        return atk("underfell_undyne_spear_pillars", 80, (c, t) -> {
            if (t >= 8 && t < 64 && t % 8 == 0) {
                Vec3 f = feet(c);
                for (int i = 0; i < 3; i++) {
                    Vec3 at = f.add(c.rnd.nextGaussian() * 2.2, -2.2, c.rnd.nextGaussian() * 2.2);
                    shot(c, at, new Vec3(0, .55, 0), .3f, .3f, 3f, RED, 6, 12, 14);
                    c.particle(ParticleTypes.CRIT, at.add(0, 2.2, 0), 6, .3, .1);
                }
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 66, 78), stab = (t % 8) < 3 ? 1 : 0;
            p.r("armR", (-1.2f - .6f * stab) * s, 0, 0).o("body", 0, -.8f * stab * s, 0);
        });
    }
    static Attack ufUndyneRain() {
        return atk("underfell_undyne_spear_rain", 90, -1, .7f, (c, t) -> {
            if (t < 72 && t % 2 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 4, 10, c.rnd.nextGaussian() * 4);
                lance(c, at, new Vec3(0, -.9, 0), 2, .2f, RED, 5, 8, 24);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 74, 88);
            p.r("armR", -3.1f * s, 0, -.1f).r("head", -.4f * s, 0, 0).o("ponytail", 0, 0, sin(t, .5f) * .6f);
        });
    }
    /** Phase 2: the true Captain charges with a spear ring and lunges. */
    static Attack ufUndyneCaptain() {
        return atk("underfell_undyne_captain_lunge", 90, -1, .4f, (c, t) -> {
            if (t == 0) for (int i = 0; i < 16; i++) { Vec3 at = ring(c.tpos(), i * Math.PI / 8, 6, .2); lance(c, at, c.tpos().subtract(at).normalize().scale(.3), 1.8f, .22f, RED, 5, 18, 40); }
            if (t == 30 || t == 55) { c.dash(dir(c).scale(1.8).add(0, .2, 0)); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .7f); }
            if (t == 36 || t == 61) { c.aoe(c.pos().add(dir(c)), 2.5, 9, 1.2); c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(dir(c)).add(0, 1, 0), 3, .4, 0); }
        }, (p, t, m) -> {
            float l = Math.max(env(t - 26, 4, 10, 16), env(t - 51, 4, 10, 16));
            p.r("armR", -1.6f - 1.2f * l, 0, 0).r("body", .4f * l, 0, 0).r("legL", -.8f * l, 0, 0).r("legR", .6f * l, 0, 0);
        });
    }

    // ---- Underfell Alphys (invented: lab-tech lethal tests)
    static Attack ufAlphysLaserGrid() {
        return atk("underfell_alphys_laser_grid", 80, (c, t) -> {
            if (t % 20 == 4 && t < 64) {
                Vec3 d = dir(c), r = side(d); boolean blue = (t / 20) % 2 == 1;
                for (int k = -2; k <= 2; k++) {
                    var b = c.proj().at(c.tpos().add(d.scale(k * 1.6)).add(0, -.5, 0)).face(r).box(.12f, 1.4f, 14).dmg(5).warn(16).life(26).pierce();
                    if (blue) b.blue(); else b.orange(); b.spawn();
                }
                c.sound(SoundEvents.BEACON_POWER_SELECT, 1, 1.6f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 66, 78);
            p.r("armR", -1.4f * s, 0, -.3f).o("remote", 0, sin(t, 1.2f) * .2f, 0).r("head", .2f * s, 0, 0);
        });
    }
    static Attack ufAlphysDrones() {
        return atk("underfell_alphys_bomb_drones", 90, (c, t) -> {
            if (t >= 6 && t < 54 && t % 12 == 6) {
                Vec3 at = c.pos().add(0, 3, 0);
                Vec3 v = c.tpos().subtract(at).scale(1 / 26.0).add(0, .32, 0);
                c.proj().at(at).vel(v).box(.6f, .4f, .6f).color(0x555555).dmg(4).life(26).gravity(.025f).spawn();
                c.sound(SoundEvents.DISPENSER_LAUNCH, 1, 1.2f);
            }
            if (t >= 32 && t < 80 && t % 12 == 8) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 1.5, .3, c.rnd.nextGaussian() * 1.5);
                c.proj().at(at).box(2.6f, .3f, 2.6f).color(RED).dmg(7).warn(10).life(14).pierce().spawn();
                c.particle(ParticleTypes.EXPLOSION, at, 1, 0, 0);
                c.sound(SoundEvents.GENERIC_EXPLODE, .6f, 1.4f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 70, 86);
            p.r("armL", -2f * s, 0, .3f).r("armR", -.6f * s, 0, -.6f).o("body", 0, sin(t, .8f) * .2f, 0);
        });
    }
    static Attack ufAlphysTesla() {
        return atk("underfell_alphys_tesla_coils", 84, -1, .7f, (c, t) -> {
            if (t == 6) for (int i = 0; i < 3; i++) shot(c, ring(feet(c), i * 2.094, 4, 1), Vec3.ZERO, .5f, 2.4f, .5f, 0x888888, 0, 0, 74);
            if (t >= 20 && t < 76 && t % 8 == 0) {
                for (int i = 0; i < 3; i++) {
                    Vec3 a0 = ring(feet(c), i * 2.094, 4, 1.2), a1 = ring(feet(c), (i + 1) * 2.094, 4, 1.2);
                    c.proj().at(a0.add(a1).scale(.5)).face(a1.subtract(a0)).box(.15f, .15f, (float) a0.distanceTo(a1)).color(0xfff070).dmg(5).warn(4).life(6).pierce().spawn();
                }
                c.particle(ParticleTypes.ELECTRIC_SPARK, c.tpos(), 12, 2, .2);
                c.sound(SoundEvents.LIGHTNING_BOLT_IMPACT, .4f, 2f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 76, 84);
            p.r("armL", -2.4f * s, 0, .5f).r("armR", -2.4f * s, 0, -.5f).o("lensL", 0, 0, -.1f * sin(t, 2)).o("lensR", 0, 0, -.1f * sin(t, 2));
        });
    }
    /** Releases an amalgamate: dripping, melting projectiles that splash on the ground. */
    static Attack ufAlphysAmalgam() {
        return atk("underfell_alphys_amalgam_release", 96, -1, .4f, (c, t) -> {
            if (t == 10) { dust(c, c.pos().add(0, 1, 0), 0xffffff, 40, 1); c.sound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1, .5f); }
            if (t >= 16 && t < 86 && t % 4 == 0) {
                Vec3 at = c.pos().add(dir(c).scale(1.5)).add(0, 2.5, 0);
                Vec3 v = c.tpos().subtract(at).scale(1 / 22.0).add(c.rnd.nextGaussian() * .08, .25, c.rnd.nextGaussian() * .08);
                shot(c, at, v, .5f, .5f, .5f, 0xf0f0f0, 4, 0, 30).grav = .022f;
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 86, 94);
            p.r("armL", -1f * s, 0, 1.2f * s).r("armR", -1f * s, 0, -1.2f * s).o("body", 0, -1f * s, 0);
        });
    }

    // ---- Underfell Asgore: red trident, crimson fire
    static Attack ufAsgoreSweep() {
        return atk("underfell_asgore_trident_sweep", 70, (c, t) -> {
            if (t == 20 || t == 44) {
                Vec3 d = dir(c); c.dash(d.scale(1.2)); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, .5f);
            }
            if (t == 24 || t == 48) {
                Vec3 d = dir(c); c.aoe(c.pos().add(d.scale(2.2)).add(0, 1, 0), 3.2, 10, 1.4);
                c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(d.scale(2.2)).add(0, 1.5, 0), 4, .8, 0);
                for (int k = -2; k <= 2; k++) lance(c, c.pos().add(d.scale(2)).add(0, 1, 0), d.yRot(k * .25f).scale(.7), 1.4f, .3f, DFIRE, 4, 0, 24);
            }
        }, (p, t, m) -> {
            float a = env(t - 8, 12, 16, 22), b = env(t - 32, 12, 16, 22);
            p.r("armR", -1.4f, (-1.2f + 2.4f * up(t, 18, 26)) * a + (1.2f - 2.4f * up(t, 42, 50)) * b, 0).r("body", 0, -.4f * (a - b), 0);
        });
    }
    static Attack ufAsgoreFireRing() {
        return atk("underfell_asgore_fire_ring", 90, (c, t) -> {
            if (t == 6) for (int i = 0; i < 20; i++) {
                Vec3 at = ring(c.tpos(), i * Math.PI / 10, 7, 0);
                shot(c, at, c.tpos().subtract(at).normalize().scale(.15), .45f, .45f, .45f, DFIRE, 4, 10, 70);
            }
            if (t == 30) c.sound(SoundEvents.BLAZE_SHOOT, 1, .6f);
            if (t >= 20 && t < 80 && t % 3 == 0) c.particle(ParticleTypes.FLAME, c.tpos(), 6, 3, .02);
        }, (p, t, m) -> {
            float s = env(t, 6, 76, 88);
            p.r("armL", -1.8f * s, 0, .8f * s).r("armR", -1.2f * s, 0, -.4f).r("head", -.2f * s, 0, 0);
        });
    }
    static Attack ufAsgoreHands() {
        return atk("underfell_asgore_hand_flames", 80, (c, t) -> {
            if (t >= 10 && t < 64 && t % 6 == 0) {
                int s = (t / 6) % 2 == 0 ? 1 : -1;
                Vec3 from = c.eye().add(side(dir(c)).scale(s * 1.4));
                for (int k = -1; k <= 1; k++) shot(c, from, c.aim(from).yRot(k * .18f).scale(.6), .4f, .4f, .4f, k == 0 ? FIRE : DFIRE, 4, 0, 40);
                c.sound(SoundEvents.BLAZE_SHOOT, .8f, .8f);
            }
        }, (p, t, m) -> {
            int s = ((int) t / 6) % 2; float e = env(t, 8, 64, 76);
            p.r(s == 0 ? "armR" : "armL", -1.6f * e, 0, 0).r(s == 0 ? "armL" : "armR", -.6f * e, 0, 0);
        });
    }
    static Attack ufAsgoreSlam() {
        return atk("underfell_asgore_trident_slam", 76, -1, .6f, (c, t) -> {
            if (t == 26) {
                Vec3 d = dir(c); c.aoe(c.pos().add(d.scale(2.5)), 3.5, 12, 1.6); c.sound(SoundEvents.GENERIC_EXPLODE, 1.2f, .6f);
                for (int i = 0; i < 16; i++) shot(c, c.pos().add(0, .3, 0), new Vec3(Math.cos(i * Math.PI / 8), 0, Math.sin(i * Math.PI / 8)).scale(.45), .6f, .8f, .6f, DFIRE, 5, 0, 30);
                c.particle(ParticleTypes.LAVA, c.pos(), 20, 1.5, .1);
            }
        }, (p, t, m) -> {
            float w = up(t, 0, 20), hit = up(t, 22, 27), rec = up(t, 40, 70);
            float x = (-3f * w + 3.6f * hit) * (1 - rec);
            p.r("armR", x, 0, -.2f).r("armL", x, 0, .2f).r("body", .5f * hit * (1 - rec), 0, 0).o("body", 0, -1.5f * hit * (1 - rec), 0);
        });
    }
    /** Phase 2: "Kill or be killed" — trident thrust shockwave + homing-ish fire rain. */
    static Attack ufAsgoreKillOrBeKilled() {
        return atk("underfell_asgore_kill_or_be_killed", 110, -1, .3f, (c, t) -> {
            if (t == 0) { c.sound(SoundEvents.RAVAGER_ROAR, 1, .6f); dust(c, c.eye(), RED, 30, 1); }
            if (t >= 10 && t < 90 && t % 4 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 3, 9, c.rnd.nextGaussian() * 3);
                shot(c, at, new Vec3(0, -.6, 0), .5f, .5f, .5f, (t / 4) % 3 == 0 ? FIRE : DFIRE, 5, 6, 26);
            }
            if (t == 50 || t == 80) {
                Vec3 d = dir(c); c.proj().at(c.pos().add(d.scale(8)).add(0, 1, 0)).face(d).box(1.2f, 1.2f, 14).color(RED).dmg(10).warn(12).life(20).pierce().spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 96, 108), th = Math.max(env(t - 46, 4, 14, 18), env(t - 76, 4, 14, 18));
            p.r("armR", -1.6f * s - .3f * th, 0, 0).o("armR", 0, 0, -2 * th).r("armL", -2.6f * s, 0, .6f * s).r("head", -.2f * s, 0, 0);
        });
    }

    // ---- Underfell Toriel: cruel ruins fire, thorns
    static Attack ufTorielHands() {
        return atk("underfell_toriel_hand_fire", 76, (c, t) -> {
            if (t >= 10 && t < 60 && t % 5 == 0) {
                for (int s = -1; s <= 1; s += 2) {
                    Vec3 at = c.tpos().add(side(dir(c)).scale(s * 5)).add(0, .2 + c.rnd.nextDouble(), 0);
                    shot(c, at, side(dir(c)).scale(-s * .4), .4f, .5f, .4f, DFIRE, 4, 6, 30);
                }
                c.sound(SoundEvents.FIRECHARGE_USE, .6f, 1.2f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 60, 72);
            p.r("armL", -1.6f * s, -.6f * s, 0).r("armR", -1.6f * s, .6f * s, 0).o("pawL", 0, sin(t, .8f) * .3f, 0);
        });
    }
    static Attack ufTorielCircle() {
        return atk("underfell_toriel_closing_circle", 80, (c, t) -> {
            if (t == 6) for (int i = 0; i < 16; i++) {
                Vec3 at = ring(feet(c), i * Math.PI / 8, 6, .8); shot(c, at, feet(c).add(0, .8, 0).subtract(at).normalize().scale(.12), .4f, .5f, .4f, DFIRE, 4, 10, 60);
            }
            if (t == 30) for (int i = 0; i < 10; i++) {
                Vec3 at = ring(feet(c), i * Math.PI / 5 + .3, 7.5, 1.4); shot(c, at, feet(c).add(0, 1.4, 0).subtract(at).normalize().scale(.18), .4f, .5f, .4f, FIRE, 4, 8, 50);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 60, 76);
            p.r("armL", -2.4f * s, 0, .8f * s).r("armR", -2.4f * s, 0, -.8f * s).r("head", -.25f * s, 0, 0);
        });
    }
    static Attack ufTorielThorns() {
        return atk("underfell_toriel_thorn_snare", 84, -1, .7f, (c, t) -> {
            if (t >= 8 && t < 72 && t % 6 == 0) {
                Vec3 d = dir(c), at = c.pos().add(d.scale(1.5 + (t - 8) / 6 * 1.2)).add(0, -1.3, 0);
                for (int k = -1; k <= 1; k++) lance(c, at.add(side(d).scale(k * .8)), new Vec3(0, .5, 0), 1.4f, .2f, 0x2b2b2b, 5, 8, 12);
                c.particle(ParticleTypes.CRIMSON_SPORE, at.add(0, 1.3, 0), 6, .4, 0);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 72, 82);
            p.r("armR", -1f * s, 0, -.4f).o("body", 0, -.6f * s, 0).r("body", .25f * s, 0, 0);
        });
    }
    static Attack ufTorielBarrage() {
        return atk("underfell_toriel_fireball_barrage", 80, -1, .4f, (c, t) -> {
            if (t >= 8 && t < 64 && t % 3 == 0) {
                Vec3 from = c.eye().add(0, .4, 0);
                shot(c, from, c.aim(from).add(c.rnd.nextGaussian() * .12, c.rnd.nextGaussian() * .06, c.rnd.nextGaussian() * .12).normalize().scale(.7), .45f, .45f, .45f, t % 2 == 0 ? FIRE : DFIRE, 5, 0, 30);
                if (t % 9 == 0) c.sound(SoundEvents.BLAZE_SHOOT, .7f, 1);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 64, 78);
            p.r("armL", -1.6f * s + .2f * sin(t, 2), 0, .2f).r("armR", -1.6f * s - .2f * sin(t, 2), 0, -.2f);
        });
    }

    // ---- Underfell Mettaton EX
    static Attack ufMttLegRain() {
        return atk("underfell_mettaton_leg_rain", 72, (c, t) -> {
            if (t >= 6 && t < 60 && t % 4 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 3, 8, c.rnd.nextGaussian() * 3);
                lance(c, at, new Vec3(0, -.8, 0), 2.4f, .45f, 0x1a1a1a, 5, 8, 24);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 60, 70);
            p.r("legR", -1.6f * s * (sin(t, .9f) * .5f + .5f), 0, 0).r("armL", -2.8f * s, 0, .3f).o("body", 0, .5f * sin(t, .9f), 0);
        });
    }
    static Attack ufMttBombs() {
        return atk("underfell_mettaton_bomb_blocks", 88, (c, t) -> {
            if (t >= 6 && t < 48 && t % 7 == 0) {
                Vec3 d = dir(c), at = c.tpos().add(side(d).scale((c.rnd.nextDouble() - .5) * 6)).add(d.scale(5));
                c.proj().at(at).vel(d.scale(-.18)).box(.8f, .8f, .8f).color((t / 7) % 2 == 0 ? 0x333333 : RED).dmg(5).life(40).spawn();
            }
            if (t >= 50 && t < 80 && t % 10 == 0) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 1.5, .5, c.rnd.nextGaussian() * 1.5);
                c.proj().at(at).box(.5f, 1.2f, 6).color(0xff4040).dmg(6).warn(6).life(10).pierce().spawn();
                c.proj().at(at).box(6, 1.2f, .5f).color(0xff4040).dmg(6).warn(6).life(10).pierce().spawn();
                c.sound(SoundEvents.GENERIC_EXPLODE, .5f, 1.6f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 76, 86);
            p.r("armR", -2.2f * s, 0, -.4f).r("armL", -.4f, 0, .6f * s).r("body", 0, .3f * sin(t, .5f), 0);
        });
    }
    static Attack ufMttHeartLaser() {
        return atk("underfell_mettaton_heart_core_laser", 80, -1, .7f, (c, t) -> {
            if (t == 14 || t == 44) {
                Vec3 at = c.eye().add(0, -.4, 0), d = c.aim(at);
                c.proj().at(at.add(d.scale(10))).face(d).box(.8f, .8f, 20).color(0xff2a6a).dmg(8).warn(14).life(26).pierce().spawn();
                c.sound(SoundEvents.BEACON_ACTIVATE, 1, 2f);
            }
        }, (p, t, m) -> {
            float s = Math.max(env(t - 8, 6, 30, 34), env(t - 38, 6, 30, 34));
            p.r("armL", -1f * s, 0, 1.3f * s).r("armR", -1f * s, 0, -1.3f * s).o("core", 0, 0, -.5f * s).r("body", -.2f * s, 0, 0);
        });
    }
    static Attack ufMttMinis() {
        return atk("underfell_mettaton_mini_bots", 84, -1, .7f, (c, t) -> {
            if (t >= 4 && t < 60 && t % 8 == 4) {
                Vec3 at = c.pos().add(side(dir(c)).scale(c.rnd.nextBoolean() ? 2 : -2)).add(0, 1.5, 0);
                c.proj().at(at).vel(c.aim(at).scale(.35)).box(.45f, .65f, .45f).color(0xc0c0c0).dmg(5).life(50).spawn();
                c.sound(SoundEvents.IRON_GOLEM_STEP, .6f, 2f);
            }
        }, (p, t, m) -> {
            float s = env(t, 4, 64, 80);
            p.r("armL", -1.4f * s, -.8f * s, 0).r("armR", -1.4f * s, .8f * s, 0).o("body", 0, 1f * s * Math.abs(sin(t, .4f)), 0);
        });
    }
    /** "Grand Finale": spinning arm lasers with rating-ticker sparkles. */
    static Attack ufMttFinale() {
        return atk("underfell_mettaton_grand_finale", 110, -1, .35f, (c, t) -> {
            if (t >= 10 && t < 100 && t % 5 == 0) {
                double a = t * .12;
                for (int i = 0; i < 3; i++) {
                    Vec3 d = new Vec3(Math.cos(a + i * 2.094), 0, Math.sin(a + i * 2.094));
                    c.proj().at(c.pos().add(0, 1.4, 0).add(d.scale(7))).face(d).box(.4f, .4f, 14).color(0xff2a6a).dmg(5).warn(2).life(5).pierce().spawn();
                }
                c.particle(ParticleTypes.FIREWORK, c.pos().add(0, 2, 0), 4, .6, .1);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 100, 108);
            p.r("body", 0, t * .3f * s, 0).r("armL", 0, 0, 1.5f * s).r("armR", 0, 0, -1.5f * s);
        });
    }

    // ---- Underfell Flowey
    static Attack ufFloweyPellets() {
        return atk("underfell_flowey_kill_pellets", 70, (c, t) -> {
            if (t == 10) for (int i = 0; i < 12; i++) {
                Vec3 at = ring(c.tpos(), i * Math.PI / 6, 5, 0);
                c.proj().at(at).box(.25f, .25f, .25f).color(BONE).dmg(6).warn(18).life(60).vel(c.tpos().subtract(at).normalize().scale(.12)).spawn();
            }
            if (t == 10) c.sound(SoundEvents.WITCH_CELEBRATE, 1, 1.4f);
        }, (p, t, m) -> {
            float s = env(t, 8, 50, 66);
            p.r("head", 0, 0, .3f * s * sin(t, .5f)).o("petals", 0, 0, .2f * s).r("leafL", 0, 0, .6f * s).r("leafR", 0, 0, -.6f * s);
        });
    }
    static Attack ufFloweyVines() {
        return atk("underfell_flowey_vine_lash", 80, (c, t) -> {
            if (t >= 10 && t < 70 && t % 6 == 0) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 1.8, -1.6, c.rnd.nextGaussian() * 1.8);
                lance(c, at, new Vec3(0, .6, 0), 2.6f, .35f, 0x2e7a22, 6, 10, 12);
                c.particle(ParticleTypes.COMPOSTER, at.add(0, 1.6, 0), 8, .3, .02);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 70, 78);
            p.o("body", 0, -1.5f * s, 0).r("head", .3f * s, 0, 0);
        });
    }
    static Attack ufFloweySpiral() {
        return atk("underfell_flowey_pellet_spiral", 84, -1, .7f, (c, t) -> {
            if (t >= 8 && t < 72 && t % 2 == 0) {
                double a = t * .35; Vec3 from = c.pos().add(0, .9, 0);
                for (int i = 0; i < 2; i++) shot(c, from, new Vec3(Math.cos(a + i * Math.PI), 0, Math.sin(a + i * Math.PI)).scale(.45), .25f, .25f, .25f, i == 0 ? BONE : RED, 4, 0, 40);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 72, 82);
            p.r("head", 0, t * .35f * s, 0);
        });
    }
    /** "In this world, it's KILL or BE killed": giant face + pellet cage that closes. */
    static Attack ufFloweyLaugh() {
        return atk("underfell_flowey_kill_or_be_killed", 90, -1, .4f, (c, t) -> {
            if (t == 0) c.sound(SoundEvents.WITCH_CELEBRATE, 1.2f, .5f);
            if (t == 6 || t == 36) for (int i = 0; i < 24; i++) {
                double a = i * Math.PI / 12; Vec3 at = ring(c.tpos(), a, 4.5, Math.sin(i) * .6);
                c.proj().at(at).box(.25f, .25f, .25f).color(BONE).dmg(5).warn(20).life(50).vel(c.tpos().subtract(at).normalize().scale(.13)).spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 76, 88);
            p.o("head", 0, 1.5f * s, 0).r("head", -.3f * s, 0, .15f * sin(t, 1.5f) * s);
        });
    }

    // ---- Underfell Muffet
    static Attack ufMuffetStrings() {
        return atk("underfell_muffet_string_spiders", 80, (c, t) -> {
            if (t >= 6 && t < 66 && t % 5 == 0) {
                Vec3 d = dir(c), r = side(d); int lane = c.rnd.nextInt(3) - 1;
                Vec3 at = c.tpos().add(r.scale(lane * 1.4 + 7)).add(0, -.4, 0);
                shot(c, at, r.scale(-.6), .35f, .35f, .5f, 0x7a3a8a, 4, 6, 26);
                c.proj().at(c.tpos().add(r.scale(lane * 1.4)).add(0, -.7, 0)).face(r).box(.05f, .05f, 16).color(0xdddddd).dmg(0).life(8).spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 66, 78);
            p.r("armL", -1.4f * s, 0, .4f).r("armR", -1.4f * s, 0, -.4f).r("arm2L", -1f * s, 0, .6f).r("arm2R", -1f * s, 0, -.6f).r("arm3L", -.6f * s * sin(t, .7f), 0, .8f).r("arm3R", .6f * s * sin(t, .7f), 0, -.8f);
        });
    }
    static Attack ufMuffetCroissant() {
        return atk("underfell_muffet_croissant", 70, (c, t) -> {
            if (t == 12 || t == 36) {
                Vec3 from = c.eye(); Vec3 d = c.aim(from);
                shot(c, from, d.scale(.9), 1.2f, .4f, .5f, 0xc8862a, 5, 0, 30).spin = 25;
                c.sound(SoundEvents.TRIDENT_THROW, .7f, 1.4f);
            }
            if (t == 26 || t == 50) {
                Vec3 at = c.tpos().add(c.aim(c.eye()).scale(3)); shot(c, at, c.eye().subtract(at).normalize().scale(.6), 1.2f, .4f, .5f, 0xc8862a, 5, 4, 20).spin = 25; // boomerang return
            }
        }, (p, t, m) -> {
            float a = Math.max(env(t - 4, 8, 10, 16), env(t - 28, 8, 10, 16));
            p.r("armR", -2.4f * a + 1f, 0, -.3f).r("body", 0, -.4f * a, 0);
        });
    }
    static Attack ufMuffetPet() {
        return atk("underfell_muffet_cupcake_pet", 96, -1, .6f, (c, t) -> {
            if (t == 10) { c.sound(SoundEvents.SPIDER_AMBIENT, 1.5f, .4f); dust(c, c.pos().add(dir(c).scale(-3)).add(0, 1, 0), 0xff4a90, 30, 1); }
            if (t >= 20 && t < 80 && t % 15 == 5) {
                Vec3 d = dir(c), at = c.tpos().subtract(d.scale(6)).add(0, -.2, 0);
                c.proj().at(at).vel(d.scale(.55)).box(2.4f, 2.2f, 2).color(0xff4a90).dmg(9).warn(8).life(24).pierce().spawn();
                c.proj().at(at.add(0, .3, 0)).vel(d.scale(.55)).box(2.6f, .3f, 2.2f).color(BONE).dmg(0).warn(8).life(24).spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 80, 92);
            p.r("armL", -2.6f * s, 0, .3f).r("armR", -2.6f * s, 0, -.3f).r("arm2L", -2f * s, 0, .5f).r("arm2R", -2f * s, 0, -.5f).o("head", 0, sin(t, .5f) * .3f, 0);
        });
    }
    static Attack ufMuffetTeaRain() {
        return atk("underfell_muffet_spider_rain", 80, (c, t) -> {
            if (t >= 6 && t < 70 && t % 3 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 3, 7, c.rnd.nextGaussian() * 3);
                shot(c, at, new Vec3(0, -.55, 0), .35f, .35f, .35f, t % 2 == 0 ? 0x7a3a8a : RED, 4, 6, 26);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 70, 78);
            p.r("armL", -.8f * s, 0, .3f).r("armR", -.8f * s, 0, -.3f).r("head", -.2f * s, 0, 0).o("body", 0, sin(t, .4f) * .2f, 0);
        });
    }

    // ---- Underfell Napstablook
    static Attack ufBlookTears() {
        return atk("underfell_napstablook_red_tears", 70, (c, t) -> {
            if (t >= 6 && t < 60 && t % 4 == 0) {
                Vec3 at = c.pos().add(0, 1.4, 0).add(side(dir(c)).scale(t % 8 == 0 ? .3 : -.3));
                shot(c, at, c.aim(at).scale(.25).add(0, .3, 0), .2f, .35f, .2f, RED, 3, 0, 40).grav = .02f;
            }
        }, (p, t, m) -> { p.o("body", 0, sin(t, .3f) * .4f, 0).o("eyeL", 0, -.2f, 0).o("eyeR", 0, -.2f, 0); });
    }
    static Attack ufBlookStorm() {
        return atk("underfell_napstablook_raincloud", 80, (c, t) -> {
            if (t == 4) for (int i = 0; i < 3; i++) shot(c, c.tpos().add(i - 1, 4, 0), Vec3.ZERO, 1.6f, .6f, 1.6f, 0x333333, 0, 0, 74);
            if (t >= 10 && t < 76 && t % 2 == 0) shot(c, c.tpos().add(c.rnd.nextGaussian() * 1.4, 3.5, c.rnd.nextGaussian() * 1.4), new Vec3(0, -.45, 0), .1f, .1f, .4f, 0x3060c0, 3, 0, 14);
        }, (p, t, m) -> { p.o("body", 0, sin(t, .2f) * .5f, 0).r("body", 0, 0, sin(t, .3f) * .1f); });
    }
    static Attack ufBlookHeartache() {
        return atk("underfell_napstablook_heartache", 70, -1, .5f, (c, t) -> {
            if (t == 10 || t == 30) for (int i = 0; i < 8; i++) {
                Vec3 at = ring(c.tpos(), i * Math.PI / 4 + t, 5, .5);
                c.proj().at(at).box(.5f, .5f, .2f).color(BLOOD).dmg(4).warn(14).life(40).vel(c.tpos().subtract(at).normalize().scale(.15)).spawn();
            }
        }, (p, t, m) -> { float s = env(t, 8, 50, 66); p.o("body", 0, 1.5f * s, 0).r("body", -.2f * s, 0, 0); });
    }

    // ---- Underfell Grillby
    static Attack ufGrillbyBottles() {
        return atk("underfell_grillby_firebottles", 70, (c, t) -> {
            if (t == 10 || t == 26 || t == 42) {
                Vec3 at = c.eye(); Vec3 v = c.tpos().subtract(at).scale(1 / 18.0).add(0, .25, 0);
                c.proj().at(at).vel(v).box(.25f, .5f, .25f).color(0x2a7a3a).dmg(3).life(18).gravity(.028f).spawn();
                c.sound(SoundEvents.WITCH_THROW, 1, .8f);
            }
            if (t == 28 || t == 44 || t == 60) {
                c.proj().at(feet(c).add(0, .4, 0)).box(2.8f, .8f, 2.8f).color(DFIRE).dmg(5).warn(6).life(20).pierce().spawn();
                c.particle(ParticleTypes.FLAME, feet(c), 25, 1, .05);
                c.sound(SoundEvents.GLASS_BREAK, 1, 1);
            }
        }, (p, t, m) -> {
            float ph = up(t % 16, 0, 10);
            p.r("armR", -2.6f * ph + .4f, 0, -.2f).o("flame", 0, sin(t, .8f) * .5f, 0);
        });
    }
    static Attack ufGrillbyFireWall() {
        return atk("underfell_grillby_fire_wall", 76, (c, t) -> {
            if (t >= 10 && t < 50 && t % 10 == 0) {
                Vec3 d = dir(c), r = side(d); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -4; k <= 4; k++) if (k != gap) shot(c, c.pos().add(d.scale(2)).add(r.scale(k * .8)).add(0, .7, 0), d.scale(.6), .7f, 1.4f, .5f, DFIRE, 5, 6, 30);
                c.sound(SoundEvents.FIRECHARGE_USE, 1, .7f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 52, 70);
            p.r("armL", -1.6f * s, 0, .2f).r("armR", -1.6f * s, 0, -.2f).o("flame", 0, 1.2f * s, 0);
        });
    }
    static Attack ufGrillbyFlare() {
        return atk("underfell_grillby_flare_up", 60, -1, .5f, (c, t) -> {
            if (t == 20) { c.aoe(c.pos().add(0, 1, 0), 4, 8, 1.4); c.particle(ParticleTypes.FLAME, c.pos().add(0, 1, 0), 60, 1.5, .15); c.sound(SoundEvents.BLAZE_SHOOT, 1.4f, .5f); }
        }, (p, t, m) -> {
            float w = up(t, 0, 18), b = env(t - 18, 2, 10, 30);
            p.o("body", 0, -1 * w * (1 - b), 0).r("armL", 0, 0, 1.4f * b).r("armR", 0, 0, -1.4f * b).o("flame", 0, 3 * b, 0);
        });
    }

    // ================================================================ HORRORTALE
    static void horrortale(List<CharacterDef> out) {
        out.add(CharacterDef.of("horrortale_sans", "Sans (Horrortale)", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(150).boss().keep(4).rest(12).speed(.32f)
            .egg(0x34466e, BLOOD).size(.75f, 1.15f).scale(.95f)
            .model(sansBody(0x34466e, 0xd8d0c0, 0x1a1a1a, 0x050505, RED, 0xb8b0a0)
                .box("hole", "head", -2.2f, 2.2f, -1, 3, 2.6f, 5.5f, 0x0a0a0a)
                .box("crackA", "head", -.2f, 1.8f, -3.6f, .4f, 2.4f, .3f, 0x222222)
                .box("stain", "shirt", 1, -1, -.4f, 2, 2.5f, .3f, BLOOD)
                .box("axeHaft", "armR", 0, -6, -2, .7f, .7f, 9, 0x5a3a1a)
                .box("axeHead", "axeHaft", 0, 1.2f, -4.2f, .5f, 3.6f, 2.4f, 0x9a9a9a)
                .box("axeBlood", "axeHead", 0, 1.4f, -.6f, .55f, 1, 1.4f, BLOOD))
            .attacks(htSansAxeChop(), htSansAxeThrow(), htSansBoneHunger(), htSansCrackedBlasters(), htSansStarving()).build());

        out.add(CharacterDef.of("horrortale_papyrus", "Papyrus (Horrortale)", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(120).boss().keep(6).rest(14)
            .egg(0xe8e8e8, 0xb02020).size(.7f, 1.95f)
            .model(papBody(0xe8e8e8, 0xa01818, 0x2a3a8a, 0x8a4a1a, 0xff4040)
                .box("crack", "head", 1.4f, 2, -3.1f, .5f, 3, .3f, 0x222222)
                .box("tear", "cape", 0, -2.6f, 0, 2.2f, 1.4f, .6f, 0x5a0a0a))
            .attacks(htPapBoneWall(), htPapBlueBones(), htPapSpiral(), htPapProtect()).build());

        out.add(CharacterDef.of("horrortale_undyne", "Queen Undyne (Horrortale)", Faction.ROYAL_GUARD).hp(200).boss().keep(6).rest(12).speed(.33f)
            .egg(0x2a6a9a, BLOOD).size(.8f, 2f)
            .model(undyneBody(0x4a4a5a, BLOOD, 0x8a1010, 0x2f6f9f, 0xffd000)
                .box("crown", "head", 0, 4.6f, 0, 5, 1.2f, 5, GOLD)
                .box("rip", "body", -2, -1, -2.6f, 3, 4, .3f, 0x1a1a1a)
                .box("scar", "head", 1.5f, 2.2f, -3.1f, .4f, 3.5f, .3f, BLOOD)
                .glow("spear", "armR", 0, -9, -4, .5f, .5f, 12, 0x3fa7ff))
            .attacks(htUndyneSpearStorm(), htUndyneLunge(), htUndyneWall(), htUndyneTyrant()).build());

        out.add(CharacterDef.of("horrortale_alphys", "Alphys (Horrortale)", Faction.ROYAL_SCIENCE).hp(120).boss().keep(5).rest(14)
            .egg(0xc8b030, BLOOD).size(.8f, 1.15f)
            .model(alphysBody(0xd8d8d8, 0x2a2a2a, 0xffffff)
                .box("stain", "body", -2, -1, -3.05f, 3, 3, .3f, BLOOD)
                .box("cleaver", "armR", 0, -6, -1.5f, .4f, 2.4f, 3.2f, 0xaaaaaa)
                .box("grin", "snout", 0, -.8f, -1.05f, 4, .6f, .2f, BONE))
            .attacks(htAlphysSyringes(), htAlphysDissect(), htAlphysExperiment(), htAlphysDetermination()).build());

        out.add(CharacterDef.of("horrortale_toriel", "Toriel (Horrortale)", Faction.UNDERGROUND).hp(140).boss().keep(5).rest(14)
            .egg(0x5a2a7a, BLOOD).size(.8f, 2.1f)
            .model(goatBody(0x4a2a6a, 0xd8d0c8, RED, 1f)
                .box("rune", "body", 0, 1.5f, -3.1f, 4, 3, .3f, 0xc8c040)
                .box("apron", "body", 0, -3, -3.15f, 6, 5, .3f, 0xc8c0b0)
                .box("blood", "apron", 1, -1, -.1f, 2.5f, 2, .2f, BLOOD)
                .box("knife", "armR", 0, -11, -2, .3f, 1.2f, 4, 0xbbbbbb))
            .attacks(htTorielFire(), htTorielCleaver(), htTorielCage(), htTorielHunger()).build());
    }

    // ---- Horrortale Sans: the axe, hungry bones, cracked blasters
    static Attack htSansAxeChop() {
        return atk("horrortale_sans_axe_chop", 64, (c, t) -> {
            if (t == 18 || t == 40) { c.dash(dir(c).scale(1.3).add(0, .2, 0)); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .6f); }
            if (t == 22 || t == 44) {
                Vec3 at = c.pos().add(dir(c).scale(1.5)).add(0, .8, 0);
                c.aoe(at, 2.2, 11, 1); c.particle(ParticleTypes.DAMAGE_INDICATOR, at, 8, .4, .1); dust(c, at, BLOOD, 12, .4);
                c.sound(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, 1, .7f);
            }
        }, (p, t, m) -> {
            float a = env(t - 6, 12, 18, 26), b = env(t - 28, 12, 18, 26);
            float swing = Math.max(a, b), down = Math.max(up(t, 18, 23) * a, up(t, 40, 45) * b);
            p.r("armR", -3f * swing + 2.6f * down, 0, -.2f).r("body", .3f * down, 0, 0).o("eyeR", 0, 0, -.2f * swing);
        });
    }
    static Attack htSansAxeThrow() {
        return atk("horrortale_sans_axe_throw", 70, (c, t) -> {
            if (t == 16) {
                Vec3 from = c.eye().add(0, .3, 0), d = c.aim(from);
                c.proj().at(from).vel(d.scale(.75)).box(1.3f, 1.3f, .3f).color(0x9a9a9a).dmg(10).life(18).pierce().spawn();
                c.sound(SoundEvents.TRIDENT_THROW, 1, .5f);
            }
            if (t == 34) {
                Vec3 from = c.tpos().add(c.aim(c.eye()).scale(3)), back = c.eye().subtract(from).normalize();
                c.proj().at(from).vel(back.scale(.75)).box(1.3f, 1.3f, .3f).color(0x9a9a9a).dmg(10).warn(4).life(18).pierce().spawn();
            }
        }, (p, t, m) -> {
            float wind = env(t, 14, 16, 20), catchA = env(t - 46, 4, 8, 20);
            p.r("armR", -3f * wind + (t > 16 && t < 50 ? 1.2f : 0), 0, -.3f).r("armR", -1f * catchA, 0, 0);
        });
    }
    static Attack htSansBoneHunger() {
        return atk("horrortale_sans_bone_maw", 84, (c, t) -> {
            if (t >= 10 && t < 72 && t % 12 == 10) {
                Vec3 f = feet(c);
                for (int i = 0; i < 10; i++) {
                    Vec3 at = ring(f, i * Math.PI / 5, 2.2, -1.4);
                    lance(c, at, f.add(0, 1, 0).subtract(at).normalize().scale(.35), 1.8f, .24f, BONE, 6, 12, 10);
                }
                c.sound(SoundEvents.EVOKER_FANGS_ATTACK, 1, .6f);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 72, 82);
            p.r("armL", -1.2f * s, 0, .6f * s).o("body", 0, -.5f * s, 0).r("head", .3f * s, sin(t, .5f) * .2f, 0);
        });
    }
    /** Damaged blasters: misfire flicker, offset beams. */
    static Attack htSansCrackedBlasters() {
        return atk("horrortale_sans_cracked_blasters", 90, -1, .7f, (c, t) -> {
            if (t < 40 && t % 10 == 0) {
                Vec3 at = ring(c.tpos(), c.rnd.nextDouble() * Math.PI * 2, 6, 1.5 + c.rnd.nextDouble());
                Vec3 aim = c.tpos().add(c.rnd.nextGaussian() * .8, 0, c.rnd.nextGaussian() * .8).subtract(at);
                blaster(c, at, aim, 22, 12, 8, 1f, 0xc8c0b0, RED, 0xffc0c0);
                dust(c, at, 0x333333, 8, .4);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 60, 84);
            p.r("armL", -2.4f * s, 0, .3f).r("head", -.2f * s, 0, .2f * sin(t, 2.5f) * s).o("eyeR", .2f * sin(t, 3), 0, 0);
        });
    }
    /** Phase 2 "starving": berserk axe spin while bones erupt. */
    static Attack htSansStarving() {
        return atk("horrortale_sans_starving_frenzy", 100, -1, .35f, (c, t) -> {
            if (t == 0) { c.sound(SoundEvents.ZOMBIE_AMBIENT, 1.4f, .5f); dust(c, c.eye(), RED, 30, .6); }
            if (t >= 14 && t < 80) {
                if (t % 10 == 4) c.dash(dir(c).scale(.7));
                if (t % 5 == 0) { c.aoe(c.pos().add(0, .8, 0), 2.6, 7, .9); c.particle(ParticleTypes.SWEEP_ATTACK, c.pos().add(0, 1, 0), 2, 1, 0); }
                if (t % 8 == 0) lance(c, feet(c).add(c.rnd.nextGaussian(), -1.4, c.rnd.nextGaussian()), new Vec3(0, .5, 0), 1.6f, .24f, BONE, 5, 8, 14);
            }
        }, (p, t, m) -> {
            float s = env(t, 14, 80, 96);
            p.r("body", 0, t * .9f * s, 0).r("armR", -1.5f * s, 0, -1.2f * s).r("armL", 0, 0, 1f * s);
        });
    }

    // ---- Horrortale Papyrus: still believes; protective
    static Attack htPapBoneWall() {
        return atk("horrortale_papyrus_bone_wall", 70, (c, t) -> {
            if (t >= 10 && t < 50 && t % 10 == 0) {
                Vec3 d = dir(c), r = side(d);
                for (int k = -4; k <= 4; k++) bone(c, c.pos().add(d.scale(2)).add(r.scale(k * .7)).add(0, .3, 0), d.scale(.7), .7f, BONE, 4, 6, 40);
                c.sound(SoundEvents.SKELETON_SHOOT, .8f, 1);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 50, 66);
            p.r("armR", -1.2f * s, 0, -.4f).r("armL", -1.2f * s, 0, .4f).o("cape", 0, 0, sin(t, .4f) * .5f);
        });
    }
    static Attack htPapBlueBones() {
        return atk("horrortale_papyrus_blue_attack", 80, (c, t) -> {
            if (t == 6) { slam(c, -1); dust(c, c.tpos(), 0x3fa7ff, 14, .4); }
            if (t >= 14 && t < 66 && t % 9 == 0) {
                Vec3 d = dir(c);
                for (int k = -3; k <= 3; k++) blueBone(c, c.tpos().subtract(d.scale(7)).add(side(d).scale(k * .8)).add(0, -.5, 0), d.scale(.5), 2.2f, 5, 8, 30);
            }
        }, (p, t, m) -> {
            float s = t < 6 ? up(t, 0, 6) : 1 - up(t, 6, 22);
            p.r("armR", -2.5f * s, 0, -.2f).o("body", 0, -.6f * s, 0);
        });
    }
    static Attack htPapSpiral() {
        return atk("horrortale_papyrus_bone_spiral", 86, -1, .7f, (c, t) -> {
            if (t >= 8 && t < 74 && t % 3 == 0) {
                double a = t * .3; Vec3 at = ring(c.tpos(), a, 6, -.3);
                bone(c, at, c.tpos().subtract(at).multiply(1, 0, 1).normalize().scale(.35), 1.6f, t % 6 == 0 ? 0xffd0d0 : BONE, 4, 6, 20);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 74, 84);
            p.r("armL", -2.8f * s, 0, .2f).r("armR", -2.8f * s, 0, -.2f).r("body", 0, sin(t, .3f) * .3f * s, 0);
        });
    }
    /** "Protect brother" special: giant bone slam and a guardian bone ring. */
    static Attack htPapProtect() {
        return atk("horrortale_papyrus_special_attack", 96, -1, .4f, (c, t) -> {
            if (t == 6) for (int i = 0; i < 8; i++) bone(c, ring(c.pos(), i * Math.PI / 4, 1.8, .9), Vec3.ZERO, 2f, BONE, 4, 0, 80);
            if (t == 30) {
                Vec3 at = c.tpos().add(0, 6, 0);
                lance(c, at, new Vec3(0, -.6, 0), 6, .9f, BONE, 12, 14, 24);
            }
            if (t == 54) c.particle(ParticleTypes.EXPLOSION, feet(c), 2, .5, 0);
        }, (p, t, m) -> {
            float s = env(t, 26, 40, 80);
            p.r("armR", -3.1f * s, 0, -.1f).r("armL", -3.1f * s, 0, .1f).o("body", 0, 1f * s, 0);
        });
    }

    // ---- Horrortale Undyne: tyrant queen
    static Attack htUndyneSpearStorm() {
        return atk("horrortale_undyne_spear_storm", 80, (c, t) -> {
            if (t >= 8 && t < 66 && t % 4 == 0) {
                double a = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = ring(c.tpos(), a, 8, 1 + c.rnd.nextDouble() * 2);
                lance(c, at, c.tpos().subtract(at).normalize().scale(.6), 2f, .22f, 0x3fa7ff, 6, 10, 26);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 66, 78);
            p.r("armR", -3f * s, 0, -.1f).r("armL", -1f * s, 0, .8f * s).o("ponytail", 0, 0, sin(t, .5f) * .5f);
        });
    }
    static Attack htUndyneLunge() {
        return atk("horrortale_undyne_spear_lunge", 70, (c, t) -> {
            if (t == 14 || t == 38) { c.dash(dir(c).scale(2).add(0, .15, 0)); c.sound(SoundEvents.TRIDENT_RIPTIDE_1, 1, 1); }
            if (t == 18 || t == 42) c.aoe(c.pos().add(dir(c).scale(1.5)).add(0, 1, 0), 2.4, 12, 1.5);
        }, (p, t, m) -> {
            float l = Math.max(env(t - 8, 6, 12, 18), env(t - 32, 6, 12, 18));
            p.r("armR", -1.6f, 0, 0).o("armR", 0, 0, -3 * l).r("body", .5f * l, 0, 0).r("legR", .8f * l, 0, 0);
        });
    }
    static Attack htUndyneWall() {
        return atk("horrortale_undyne_spear_wall", 80, -1, .7f, (c, t) -> {
            if (t % 16 == 6 && t < 70) {
                Vec3 d = dir(c), r = side(d); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -4; k <= 4; k++) if (Math.abs(k - gap) > 0)
                    lance(c, c.tpos().subtract(d.scale(8)).add(r.scale(k * .7)), d.scale(.6), 1.8f, .2f, 0x3fa7ff, 5, 10, 30);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 70, 78);
            p.r("armR", -2f * s, -.6f * s, 0).r("armL", -2f * s, .6f * s, 0);
        });
    }
    /** Tyrant phase: spear eruption grid then a skyfall of spears. */
    static Attack htUndyneTyrant() {
        return atk("horrortale_undyne_tyrant_judgement", 110, -1, .35f, (c, t) -> {
            if (t == 0) c.sound(SoundEvents.RAVAGER_ROAR, 1, 1.2f);
            if (t >= 10 && t < 50 && t % 10 == 0) {
                Vec3 f = feet(c);
                for (int i = -2; i <= 2; i++) for (int j = -2; j <= 2; j++) if ((i + j + t / 10) % 2 == 0)
                    lance(c, f.add(i * 1.4, -2, j * 1.4), new Vec3(0, .6, 0), 2.4f, .3f, 0x3fa7ff, 7, 12, 12);
            }
            if (t >= 56 && t < 100 && t % 2 == 0) lance(c, c.tpos().add(c.rnd.nextGaussian() * 4, 10, c.rnd.nextGaussian() * 4), new Vec3(0, -1, 0), 2.2f, .22f, 0x3fa7ff, 6, 8, 20);
        }, (p, t, m) -> {
            float s = env(t, 10, 100, 108), st = t < 50 && (t % 10) < 4 ? 1 : 0;
            p.r("armR", -3f * s + 1.5f * st, 0, 0).o("body", 0, -1f * st, 0).r("head", -.2f * s, 0, 0);
        });
    }

    // ---- Horrortale Alphys: mad scientist
    static Attack htAlphysSyringes() {
        return atk("horrortale_alphys_syringe_darts", 70, (c, t) -> {
            if (t >= 10 && t < 56 && t % 4 == 0) {
                Vec3 from = c.eye().add(side(dir(c)).scale(.6));
                lance(c, from, c.aim(from).add(c.rnd.nextGaussian() * .05, 0, c.rnd.nextGaussian() * .05).scale(.9), .8f, .15f, 0x60ff60, 4, 0, 30);
                c.sound(SoundEvents.ARROW_SHOOT, .7f, 1.8f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 56, 68);
            p.r("armR", -1.5f * s - .3f * Math.abs(sin(t, .8f)), 0, 0).r("head", .1f, 0, sin(t, 1.3f) * .15f);
        });
    }
    static Attack htAlphysDissect() {
        return atk("horrortale_alphys_dissection", 72, (c, t) -> {
            if (t == 20 || t == 34 || t == 48) {
                c.dash(dir(c).scale(1.1)); Vec3 at = c.pos().add(dir(c).scale(1.2)).add(0, .7, 0);
                c.aoe(at, 1.8, 8, .6); dust(c, at, BLOOD, 10, .3); c.sound(SoundEvents.PLAYER_ATTACK_CRIT, 1, .8f);
            }
        }, (p, t, m) -> {
            float h = (t >= 14 && t < 54) ? up((t - 14) % 14, 0, 6) : 0, s = env(t, 14, 54, 66);
            p.r("armR", (-2.8f + 2.6f * h) * s, 0, -.3f).r("body", .3f * h * s, 0, 0);
        });
    }
    /** Releases a failed experiment (melting white mass that crawls at the target). */
    static Attack htAlphysExperiment() {
        return atk("horrortale_alphys_failed_experiment", 96, -1, .7f, (c, t) -> {
            if (t == 12) c.sound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1, .5f);
            if (t >= 16 && t < 86 && t % 14 == 2) {
                Vec3 at = c.pos().add(dir(c).scale(1.5)).add(0, .5, 0);
                c.proj().at(at).vel(c.aim(at).multiply(1, 0, 1).normalize().scale(.3)).box(1.2f, 1f, 1.2f).color(0xe8e8e0).dmg(7).life(50).spawn();
                dust(c, at, 0xffffff, 10, .4);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 86, 94);
            p.r("armL", -2f * s, 0, .6f).r("armR", -.6f * s, 0, -.8f).r("head", -.3f * s, 0, 0);
        });
    }
    static Attack htAlphysDetermination() {
        return atk("horrortale_alphys_determination_ray", 90, -1, .4f, (c, t) -> {
            if (t == 20 || t == 50) {
                Vec3 at = c.eye(), d = c.aim(at);
                c.proj().at(at.add(d.scale(10))).face(d).box(1, 1, 20).color(0xff0000).dmg(10).warn(16).life(28).pierce().spawn();
                c.sound(SoundEvents.BEACON_DEACTIVATE, 1, .5f);
            }
            if (t % 5 == 0) dust(c, c.eye(), RED, 4, .3);
        }, (p, t, m) -> {
            float s = env(t, 18, 76, 88);
            p.r("armL", -1.6f * s, 0, .2f).r("armR", -1.6f * s, 0, -.2f).o("body", 0, 0, .5f * s);
        });
    }

    // ---- Horrortale Toriel: the Ruins keeper
    static Attack htTorielFire() {
        return atk("horrortale_toriel_fire_magic", 72, (c, t) -> {
            if (t >= 8 && t < 60 && t % 4 == 0) {
                Vec3 from = c.eye().add(side(dir(c)).scale(t % 8 == 0 ? 1 : -1));
                shot(c, from, c.aim(from).scale(.55), .45f, .45f, .45f, FIRE, 5, 0, 36);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 60, 70);
            p.r("armL", -1.5f * s * (t % 8 < 4 ? 1 : .5f), 0, .2f).r("armR", -1.5f * s * (t % 8 < 4 ? .5f : 1), 0, -.2f);
        });
    }
    static Attack htTorielCleaver() {
        return atk("horrortale_toriel_butcher_knife", 66, (c, t) -> {
            if (t == 18) { c.dash(dir(c).scale(1.2)); }
            if (t == 22) { Vec3 at = c.pos().add(dir(c).scale(1.6)).add(0, 1, 0); c.aoe(at, 2.2, 12, 1); dust(c, at, BLOOD, 14, .4); c.sound(SoundEvents.PLAYER_ATTACK_STRONG, 1, .6f); }
            if (t == 40) for (int k = -1; k <= 1; k++) { Vec3 from = c.eye(); lance(c, from, c.aim(from).yRot(k * .2f).scale(.8), .9f, .15f, 0xbbbbbb, 5, 0, 30); }
        }, (p, t, m) -> {
            float a = env(t - 4, 14, 18, 24), th = env(t - 32, 8, 10, 20);
            p.r("armR", -3f * a + 2.4f * up(t, 18, 23) * a - 2.4f * th, 0, -.2f);
        });
    }
    static Attack htTorielCage() {
        return atk("horrortale_toriel_fire_cage", 90, -1, .7f, (c, t) -> {
            if (t == 6) for (int i = 0; i < 18; i++) shot(c, ring(feet(c), i * Math.PI / 9, 3.5, .8), Vec3.ZERO, .5f, 1.6f, .5f, FIRE, 5, 12, 76);
            if (t >= 24 && t < 80 && t % 6 == 0) {
                Vec3 at = c.tpos().add(c.rnd.nextGaussian() * 1.5, 5, c.rnd.nextGaussian() * 1.5);
                shot(c, at, new Vec3(0, -.4, 0), .5f, .5f, .5f, DFIRE, 5, 6, 20);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 88);
            p.r("armL", -2.8f * s, 0, .6f).r("armR", -2.8f * s, 0, -.6f);
        });
    }
    static Attack htTorielHunger() {
        return atk("horrortale_toriel_mothers_hunger", 100, -1, .35f, (c, t) -> {
            if (t == 0) c.sound(SoundEvents.RAVAGER_ROAR, .8f, 1.4f);
            if (t >= 10 && t < 90 && t % 5 == 0) {
                double a = t * .25;
                for (int i = 0; i < 4; i++) shot(c, c.pos().add(0, 1.2, 0), new Vec3(Math.cos(a + i * Math.PI / 2), 0, Math.sin(a + i * Math.PI / 2)).scale(.4), .45f, .45f, .45f, i % 2 == 0 ? FIRE : DFIRE, 5, 0, 40);
            }
            if (t == 60) { c.dash(dir(c).scale(1.5)); }
            if (t == 64) c.aoe(c.pos().add(dir(c)), 2.5, 12, 1.2);
        }, (p, t, m) -> {
            float s = env(t, 10, 90, 98), lu = env(t - 56, 4, 8, 16);
            p.r("armL", -1.6f * s, 0, 1f * s).r("armR", -1.6f * s - 1f * lu, 0, -1f * s).r("body", .3f * lu, t * .25f * s * (1 - lu), 0);
        });
    }

    // ================================================================ DISBELIEF PAPYRUS (4 phases)
    static final int DB_ORANGE = 0xff8a1a, HOODIE = 0x2a6fd6;
    static final float P1 = .75f, P2 = .5f, P3 = .25f;
    static void disbelief(List<CharacterDef> out) {
        out.add(CharacterDef.of("disbelief_papyrus", "Papyrus (Disbelief)", Faction.UNDERGROUND).hp(320).boss().keep(7).rest(10).speed(.3f)
            .egg(HOODIE, DB_ORANGE).size(.7f, 1.95f)
            .model(papBody(HOODIE, 0xc81818, 0x222222, 0xc81818, 0x000000)
                .box("hood", "body", 0, 5, 2.4f, 7, 2, 2, 0xffffff)
                .box("pocket", "body", 0, -3, -2.1f, 5, 2, .3f, 0x2563c0)
                .glow("eyeGlow", "head", 1.5f, .6f, -3.2f, 1.6f, 1.6f, .3f, DB_ORANGE)
                .ghost("aura", "eyeGlow", .8f, .6f, 0, .8f, 2.4f, .2f, DB_ORANGE, .5f))
            .attacks(
                dbP1BoneStab(), dbP1BlueOrange(), dbP1Blasters(), dbP1BoneThrow(),
                dbP2Slams(), dbP2SkullBlasters(), dbP2Platforms(),
                dbP3HatredStorm(), dbP3Tornado(), dbP3SlamChain(),
                dbP4GiantBlaster(), dbP4Hurricane(), dbP4FinalBelief()).build());
    }
    // Phase 1 (100%-75%): Papyrus in Sans's hoodie, his brother's techniques, still holding back.
    static Attack dbP1BoneStab() {
        return atk("disbelief_p1_bone_stab", 70, P1, 2, (c, t) -> {
            if (t >= 6 && t < 56 && t % 10 == 6) {
                Vec3 d = dir(c), r = side(d); int s = (t / 10) % 2 == 0 ? 1 : -1;
                Vec3 base = c.tpos().add(r.scale(s * 6)).add(0, -.5, 0);
                for (int k = -2; k <= 2; k++) lance(c, base.add(d.scale(k * .7)), r.scale(-s * .9), 3, .22f, BONE, 6, 10, 20);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 56, 66);
            p.r("armR", -1.4f * s, 0, -.6f * s * sin(t, .6f)).o("aura", 0, sin(t, 1) * .3f, 0);
        });
    }
    static Attack dbP1BlueOrange() {
        return atk("disbelief_p1_blue_orange_march", 84, P1, 2, (c, t) -> {
            if (t >= 8 && t < 70 && t % 7 == 0) {
                Vec3 d = dir(c), r = side(d); boolean blue = (t / 7) % 2 == 0;
                for (int k = -4; k <= 4; k++) {
                    Vec3 at = c.pos().add(d.scale(2)).add(r.scale(k * .7)).add(0, .7, 0);
                    if (blue) blueBone(c, at, d.scale(.6), 1.6f, 5, 4, 40); else orangeBone(c, at, d.scale(.6), 1.6f, 5, 4, 40);
                }
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 70, 80);
            p.r("armL", -1.4f * s * ((t / 7) % 2 == 0 ? 1 : .3f), 0, .3f).r("armR", -1.4f * s * ((t / 7) % 2 == 1 ? 1 : .3f), 0, -.3f);
        });
    }
    static Attack dbP1Blasters() {
        return atk("disbelief_p1_twin_blasters", 80, P1, 2, (c, t) -> {
            if (t == 6 || t == 36) for (int s = -1; s <= 1; s += 2) {
                Vec3 at = c.tpos().add(side(dir(c)).scale(s * 6)).add(dir(c).scale(-2)).add(0, 2, 0);
                blaster(c, at, c.tpos().subtract(at), 20, 12, 8, 1, BONE, DB_ORANGE, 0xffffff);
            }
        }, (p, t, m) -> {
            float s = Math.max(env(t, 6, 30, 36), env(t - 30, 6, 30, 36));
            p.r("armL", -2.2f * s, 0, .8f * s).r("armR", -2.2f * s, 0, -.8f * s).o("aura", 0, 1f * s, 0);
        });
    }
    static Attack dbP1BoneThrow() {
        return atk("disbelief_p1_bone_throw", 70, P1, 2, (c, t) -> {
            if (t >= 10 && t < 60 && t % 7 == 3) {
                Vec3 from = c.eye().add(0, .5, 0);
                c.proj().at(from).vel(c.aim(from).scale(.7)).box(.3f, .3f, 1.8f).color(BONE).dmg(6).life(36).spawn().spin = 20;
                c.sound(SoundEvents.TRIDENT_THROW, .8f, 1.3f);
            }
        }, (p, t, m) -> {
            float ph = ((t + 4) % 7) / 7f, s = env(t, 10, 60, 68);
            p.r("armR", (-2.6f + 2f * ph) * s, 0, -.2f).r("body", 0, (-.3f + .4f * ph) * s, 0);
        });
    }
    // Phase 2 (75%-50%): "I never thought I'd have to do this" — slams and Sans-style blasters.
    static Attack dbP2Slams() {
        return atk("disbelief_p2_gravity_slams", 96, P2, P1, (c, t) -> {
            if (t % 24 == 4 && t < 80) {
                int k = t / 24; Vec3 r = side(dir(c));
                Vec3 v = switch (k % 4) { case 0 -> new Vec3(0, -1.5, 0); case 1 -> r.scale(1.3).add(0, .2, 0); case 2 -> new Vec3(0, 1.1, 0); default -> r.scale(-1.3).add(0, .2, 0); };
                fling(c, v); c.sound(SoundEvents.ANVIL_LAND, .9f, .6f);
            }
            if (t % 24 == 12 && t < 88) for (int i = 0; i < 8; i++) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 2.5, -1.3, c.rnd.nextGaussian() * 2.5);
                lance(c, at, new Vec3(0, .5, 0), 1.6f, .24f, BONE, 6, 6, 14);
            }
        }, (p, t, m) -> {
            int k = (int) t / 24; float e = env(t % 24, 4, 8, 18);
            float z = k % 4 == 1 ? -1.2f : k % 4 == 3 ? 1.2f : 0;
            p.r("armR", (k % 4 == 0 ? -2.6f + 2.4f * e : -1.4f * e), 0, z * e).o("body", 0, -.6f * e, 0).o("aura", 0, e, 0);
        });
    }
    static Attack dbP2SkullBlasters() {
        return atk("disbelief_p2_skull_barrage", 96, P2, P1, (c, t) -> {
            if (t < 48 && t % 6 == 0) {
                double a = t / 6 * 2.4; Vec3 at = ring(c.tpos(), a, 7, 2 + (t / 6) % 3 * .6);
                blaster(c, at, c.tpos().subtract(at), 20, 10, 8, 1, BONE, DB_ORANGE, 0xffe0c0);
            }
            if (t == 60) {
                Vec3 at = c.tpos().add(0, 8, 0); blaster(c, at, new Vec3(0, -1, 0), 18, 14, 10, 1.6f, BONE, DB_ORANGE, 0xffffff);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 76, 92);
            p.r("armL", -2.8f * s, 0, .3f).r("armR", -2.8f * s, 0, -.3f).o("body", 0, 1.5f * s, 0).r("head", -.3f * s, 0, 0);
        });
    }
    static Attack dbP2Platforms() {
        return atk("disbelief_p2_bone_corridor", 100, P2, P1, (c, t) -> {
            if (t >= 6 && t < 80 && t % 6 == 0) {
                Vec3 d = dir(c), r = side(d); double off = Math.sin(t * .15) * 1.5;
                bone(c, c.tpos().subtract(d.scale(8)).add(r.scale(off - 1.4)).add(0, -.4, 0), d.scale(.7), 2.4f, BONE, 5, 4, 30);
                bone(c, c.tpos().subtract(d.scale(8)).add(r.scale(off + 1.4)).add(0, -.4, 0), d.scale(.7), 2.4f, BONE, 5, 4, 30);
                if (t % 18 == 0) orangeBone(c, c.tpos().subtract(d.scale(8)).add(r.scale(off)).add(0, -.4, 0), d.scale(.7), 2.4f, 5, 4, 30);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 94);
            p.r("armR", -1.6f * s, sin(t, .15f) * .6f, 0).r("armL", -.6f * s, 0, .4f).o("aura", 0, sin(t, .7f) * .4f, 0);
        });
    }
    // Phase 3 (50%-25%): hatred — the eye blazes, attacks become relentless.
    static Attack dbP3HatredStorm() {
        return atk("disbelief_p3_hatred_storm", 110, P3, P2, (c, t) -> {
            if (t == 0) { dust(c, c.eye(), DB_ORANGE, 40, .5); c.sound(SoundEvents.WITHER_SPAWN, .7f, 1.5f); }
            if (t >= 6 && t < 80 && t % 5 == 0) {
                Vec3 at = ring(c.tpos(), c.rnd.nextDouble() * Math.PI * 2, 6 + c.rnd.nextDouble() * 2, 1 + c.rnd.nextDouble() * 3);
                blaster(c, at, c.tpos().subtract(at), 16, 8, 9, 1, BONE, DB_ORANGE, 0xffd8a0);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 90, 106);
            p.r("armL", -2.6f * s, -.4f * sin(t, .5f), .6f).r("armR", -2.6f * s, .4f * sin(t, .5f), -.6f).o("body", 0, 2f * s, 0).o("aura", 0, 1.4f * s, 0).o("cape", 0, 1 * s, 2 * s);
        });
    }
    static Attack dbP3Tornado() {
        return atk("disbelief_p3_bone_tornado", 110, P3, P2, (c, t) -> {
            if (t >= 10 && t < 96 && t % 2 == 0) {
                double a = t * .45; float r = 7 - (t - 10) * .045f;
                for (int i = 0; i < 2; i++) {
                    Vec3 at = ring(feet(c), a + i * Math.PI, r, .3 + (t % 6) * .3);
                    bone(c, at, new Vec3(-Math.sin(a + i * Math.PI), 0, Math.cos(a + i * Math.PI)).scale(.4), 1.2f, BONE, 5, 4, 10);
                }
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 96, 108);
            p.r("body", 0, t * .45f * s, 0).r("armL", 0, 0, 1.4f * s).r("armR", 0, 0, -1.4f * s);
        });
    }
    static Attack dbP3SlamChain() {
        return atk("disbelief_p3_slam_chain", 100, P3, P2, (c, t) -> {
            if (t % 14 == 4 && t < 80) {
                double a = c.rnd.nextDouble() * Math.PI * 2;
                fling(c, new Vec3(Math.cos(a) * 1.4, (t / 14) % 2 == 0 ? -1.2 : .8, Math.sin(a) * 1.4)); c.sound(SoundEvents.ANVIL_LAND, .9f, .5f);
                Vec3 at = c.tpos().add(Math.cos(a) * 5, 1, Math.sin(a) * 5);
                blaster(c, at, c.tpos().subtract(at).add(Math.cos(a) * 2, 0, Math.sin(a) * 2), 10, 8, 8, 1.2f, BONE, DB_ORANGE, 0xffffff);
            }
        }, (p, t, m) -> {
            float e = env(t % 14, 3, 6, 12), s = env(t, 4, 84, 98);
            p.r("armR", (-2.8f + 2.6f * e) * s, 0, 0).r("armL", -1.5f * e * s, 0, 1f * e).o("aura", 0, e, 0);
        });
    }
    // Phase 4 (25%-0): tired, tearful, all-in — a final display of belief.
    static Attack dbP4GiantBlaster() {
        return atk("disbelief_p4_giant_blaster", 100, -1, P3, (c, t) -> {
            if (t == 10) {
                Vec3 d = dir(c), at = c.pos().add(d.scale(-2)).add(0, 4, 0);
                c.proj().at(at).face(d).box(3, 2.6f, 3.2f).color(BONE).dmg(0).life(86).spawn();
                c.proj().at(at.add(d.scale(1.5)).add(0, .3, 0)).face(d).box(2.6f, .5f, .3f).color(DB_ORANGE).dmg(0).life(86).spawn();
                Vec3 aim = c.tpos().subtract(at).normalize();
                c.proj().at(at.add(aim.scale(15))).face(aim).box(3.2f, 3.2f, 30).color(0xffffff).dmg(16).warn(40).life(70).pierce().spawn();
                c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1.5f, .8f);
            }
            if (t == 50) c.sound(SoundEvents.WARDEN_SONIC_BOOM, 1.5f, .8f);
        }, (p, t, m) -> {
            float s = env(t, 10, 80, 96);
            p.r("armR", -3f * s, 0, -.1f).r("armL", -.4f * s, 0, .3f).r("head", -.4f * s, 0, 0).o("aura", 0, 2 * s, 0);
        });
    }
    static Attack dbP4Hurricane() {
        return atk("disbelief_p4_bone_hurricane", 120, -1, P3, (c, t) -> {
            if (t >= 6 && t < 104) {
                if (t % 3 == 0) {
                    double a = t * .21;
                    for (int i = 0; i < 3; i++) {
                        Vec3 at = ring(c.tpos(), a + i * 2.094, 8, -.2);
                        lance(c, at, c.tpos().subtract(at).normalize().scale(.4), 1.6f, .22f, i == 0 ? 0x3fa7ff : BONE, 5, 4, 24);
                    }
                }
                if (t % 26 == 0) { Vec3 at = ring(c.tpos(), c.rnd.nextDouble() * 6.28, 6, 2); blaster(c, at, c.tpos().subtract(at), 16, 10, 9, 1, BONE, DB_ORANGE, 0xffffff); }
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 104, 118);
            p.r("armL", -2.2f * s, sin(t, .2f) * .8f, .4f).r("armR", -2.2f * s, -sin(t, .2f) * .8f, -.4f).o("cape", 0, 0, 2 * s);
        });
    }
    /** The last, desperate attack: slams in all directions, bone ring, then he slumps (sparing window). */
    static Attack dbP4FinalBelief() {
        return atk("disbelief_p4_final_belief", 160, -1, P3, (c, t) -> {
            if (t == 0) dust(c, c.eye(), DB_ORANGE, 50, .8);
            if (t < 80 && t % 10 == 0) {
                int k = t / 10; Vec3 r = side(dir(c));
                fling(c, switch (k % 4) { case 0 -> new Vec3(0, -1.6, 0); case 1 -> r.scale(1.4); case 2 -> new Vec3(0, 1, 0); default -> r.scale(-1.4); });
                for (int i = 0; i < 6; i++) lance(c, feet(c).add(c.rnd.nextGaussian() * 2, -1.4, c.rnd.nextGaussian() * 2), new Vec3(0, .55, 0), 1.6f, .24f, BONE, 6, 5, 12);
            }
            if (t == 84) for (int i = 0; i < 6; i++) { Vec3 at = ring(c.tpos(), i * Math.PI / 3, 7, 2); blaster(c, at, c.tpos().subtract(at), 20, 14, 10, 1.1f, BONE, DB_ORANGE, 0xffffff); }
            if (t == 130) { c.sound(SoundEvents.SOUL_ESCAPE, 1.5f, .7f); c.particle(ParticleTypes.SOUL, c.eye(), 10, .4, .02); }
        }, (p, t, m) -> {
            float att = env(t, 6, 110, 126), slump = up(t, 120, 140);
            float e = t < 80 ? env(t % 10, 3, 5, 9) : 0;
            p.r("armR", (-2.6f + 2.4f * e) * att, 0, 0).r("armL", -2.4f * up(t, 80, 88) * att, 0, .4f)
             .o("body", 0, -3f * slump, 0).r("head", .5f * slump, 0, 0).r("armL", .3f * slump, 0, .3f * slump).r("armR", .3f * slump, 0, -.3f * slump)
             .o("aura", 0, -5 * slump, 0);
        });
    }

    // ================================================================ INSANITY SANS (Insanitytale, similar dark AU)
    static void insanity(List<CharacterDef> out) {
        out.add(CharacterDef.of("insanity_sans", "Sans (Insanity)", Faction.UNDERGROUND).hp(150).boss().keep(5).rest(10).speed(.35f)
            .egg(0x2a2a2a, 0xff0040).size(.7f, 1.1f).scale(.9f)
            .model(sansBody(0x2a2a3a, 0x8a0b0b, BLACK, 0xff0040, 0xff0040, 0x555566)
                .box("smear", "head", 0, -2.8f, -3.6f, 4, 1, .3f, BLOOD)
                .box("knife", "armR", 0, -6.5f, -1.4f, .3f, 1, 3.6f, 0xcccccc))
            .attacks(insKnifeFlurry(), insBoneRows(), insGrinBlasters(), insMadness()).build());
    }
    static Attack insKnifeFlurry() {
        return atk("insanity_sans_knife_flurry", 66, (c, t) -> {
            if (t >= 8 && t < 52 && t % 4 == 0) {
                Vec3 from = c.eye().add(side(dir(c)).scale((c.rnd.nextDouble() - .5) * 3)).add(0, c.rnd.nextDouble(), 0);
                lance(c, from, c.aim(from).scale(1), .9f, .14f, 0xdddddd, 5, 4, 30);
            }
            if (t == 54) { c.dash(dir(c).scale(1.6)); }
            if (t == 58) c.aoe(c.pos().add(dir(c)), 2, 9, 1);
        }, (p, t, m) -> {
            float s = env(t, 8, 52, 60), l = env(t - 52, 2, 6, 12);
            p.r("armR", -1.8f * s + .6f * sin(t, 1.5f) * s - 1.4f * l, 0, -.2f).r("head", 0, 0, .25f * sin(t, .7f)).r("body", .4f * l, 0, 0);
        });
    }
    static Attack insBoneRows() {
        return atk("insanity_sans_bone_rows", 80, (c, t) -> {
            if (t >= 6 && t < 66 && t % 6 == 0) {
                Vec3 d = dir(c), r = side(d); boolean flip = (t / 6) % 2 == 0;
                for (int k = -4; k <= 4; k++) if (((k + 4) % 2 == 0) == flip)
                    bone(c, c.pos().add(d.scale(2)).add(r.scale(k * .6)).add(0, .7, 0), d.scale(.85), 1.4f, (t / 6) % 3 == 0 ? 0xff0040 : BONE, 5, 3, 36);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 66, 76);
            p.r("armL", -1.2f * s, 0, .4f).r("armR", -1.2f * s, 0, -.4f).r("head", 0, sin(t, .9f) * .3f, sin(t, .5f) * .2f);
        });
    }
    static Attack insGrinBlasters() {
        return atk("insanity_sans_grinning_blasters", 90, -1, .7f, (c, t) -> {
            if (t < 42 && t % 7 == 0) {
                Vec3 at = ring(c.tpos(), t * .9, 6, 1.5 + (t % 14) * .1);
                blaster(c, at, c.tpos().subtract(at), 18, 10, 8, 1, 0xe8e8e8, 0xff0040, 0xff8090);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 60, 84);
            p.r("armL", -2.6f * s, 0, .3f).r("armR", -2.6f * s, 0, -.3f).r("head", -.3f * s, 0, .3f * sin(t, 2) * s);
        });
    }
    /** Laughing breakdown: erratic teleports and stabbing bones from everywhere. */
    static Attack insMadness() {
        return atk("insanity_sans_madness", 110, -1, .35f, (c, t) -> {
            if (t % 20 == 0 && t < 90) {
                Vec3 to = ring(c.target.position(), c.rnd.nextDouble() * 6.28, 4, 0);
                c.particle(ParticleTypes.LARGE_SMOKE, c.pos().add(0, 1, 0), 10, .3, .02); c.tp(to); c.sound(SoundEvents.ENDERMAN_TELEPORT, 1, .6f);
            }
            if (t % 20 == 8 && t < 98) for (int i = 0; i < 10; i++) {
                Vec3 at = ring(c.tpos(), i * .628 + t, 6, (i % 3) - 1);
                lance(c, at, c.tpos().subtract(at).normalize().scale(.5), 1.4f, .22f, i % 2 == 0 ? 0xff0040 : BONE, 5, 8, 20);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 96, 108);
            p.r("head", .3f * sin(t, 1.7f) * s, .4f * sin(t, 1.1f) * s, .3f * sin(t, 2.3f) * s).r("body", 0, 0, .15f * sin(t, 1.3f) * s)
             .r("armL", -1f * s, 0, 1.2f * s).r("armR", -1.8f * s, 0, -.4f);
        });
    }

    private AuUnderfellHorror() {}
}
