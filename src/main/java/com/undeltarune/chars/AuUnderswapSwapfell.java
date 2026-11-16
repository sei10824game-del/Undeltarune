package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import java.util.List;

/**
 * UNDERSWAP (Popcornpr1nce's canon swaps + TS!Underswap / fangame boss fights) and
 * SWAPFELL (the "purple" Underswap-gone-fell AU + common fanon: purple Sans, dark-orange Papyrus with collar,
 * "black attack" and the two-headed blaster).
 * Swaps: Sans<->Papyrus, Alphys<->Undyne, Toriel<->Asgore, Mettaton<->Napstablook (Napstaton), Grillby<->Muffet,
 * Monster Kid<->Asriel. Where canon is thin, attacks are invented from the swapped role (e.g. Grillby uses
 * Muffet's pet-boss structure, Alphys inherits Undyne's spears, Undyne inherits Alphys's Hotland tech).
 * Attacks are built from parametrized choreography factories below, so each AU gets its own palette/damage.
 */
public final class AuUnderswapSwapfell {
    static final int BLU = -1, ORG = -2;

    public static void register(List<CharacterDef> out) {
        underswap(out);
        swapfell(out);
    }

    // =====================================================================================================
    // UNDERSWAP
    // =====================================================================================================
    static void underswap(List<CharacterDef> out) {
        int sBlue = 0x2f6bff, beamCyan = 0xbfe8ff, honey = 0xffa030, fire = 0xff7a1a;

        // Sans ("Blueberry"): Royal Guard hopeful, blue attacks + his famous Special Attack.
        out.add(CharacterDef.of("underswap_sans", "Sans", Faction.ROYAL_GUARD).att(Attitude.HOSTILE).hp(140).boss()
            .keep(7).rest(14).speed(.34f).egg(0x2f6bff, 0xd9dde6).size(.7f, 1.1f).scale(.9f)
            .model(skeletonShort(0xd9dde6, sBlue, 0xcfd3dc, sBlue, 0x66e0ff, false))
            .attacks(boneRows("blue_bone_rush", 0xffffff, BLU, 4),
                     boneHurdles("bone_hurdles", 0xffffff, 4),
                     soulSlam("blue_soul_slam", 0xffffff, 5),
                     boneBoomerang("bone_boomerang", 0xffffff, 4),
                     blasterCross("magnificent_blasters", 0xf2f2f2, 0x66e0ff, beamCyan, 6),
                     boneStar("star_eye_burst", BLU, 4),
                     blasterFinale("special_attack", .4f, 0xf2f2f2, 0x66e0ff, beamCyan, BLU, 6)).build());

        // Papyrus ("Honey"/"Stretch" in fanon): lazy, orange hoodie, orange attacks, blasters.
        out.add(CharacterDef.of("underswap_papyrus", "Papyrus", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(150).boss()
            .keep(8).rest(18).speed(.26f).egg(0xf08a24, 0xf5f5f5).size(.7f, 2.1f)
            .model(skeletonTall(0xf08a24, 0xd8761a, 0xb59a5b, 0xe0e0e0, 0xffb347, 0, false))
            .attacks(boneRain("orange_bone_rain", ORG, 4),
                     blasterVolley("honey_blaster", 0xf2f2f2, honey, 0xffd59a, 6),
                     groundGrid("lazy_bone_grid", 0xffffff, 5),
                     smokeBones("smoke_and_bones", ORG, 4),
                     napDrift("sentry_nap", 0xffffff, ORG, 3),
                     liftBlades("orange_lift", ORG, 5),
                     blasterFinale("bad_time_blasters", .35f, 0xf2f2f2, honey, 0xffd59a, ORG, 7)).build());

        // Asgore: left Toriel, now the gentle caretaker of the Ruins (Toriel's role, his own fire).
        out.add(CharacterDef.of("underswap_asgore", "Asgore", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(110).boss()
            .keep(6).rest(20).speed(.24f).egg(0x6b4fa0, 0xf0d060).size(.9f, 2.6f).scale(1.3f)
            .model(goat(0x6b4fa0, 0x5a3f88, 0xf2f2ee, 0xf0d060, true, 0xe8e0ff, false, 0, 0))
            .attacks(fireHandSweep("fire_hand_sweep", fire, 4),
                     fireCascade("fireball_cascade", fire, 4),
                     fireRing("flame_circle", fire, 4, 14),
                     homingFlames("homing_flames", fire, 3),
                     gentleFlames("gentle_flames", .3f)).build());

        // Toriel: the ruthless Queen who declared war on humanity; wields the trident.
        out.add(CharacterDef.of("underswap_toriel", "Toriel", Faction.ROYAL_GUARD).att(Attitude.HOSTILE).hp(240).boss()
            .keep(6).rest(14).speed(.26f).egg(0x3a2a5c, 0xd02020).size(.9f, 2.6f).scale(1.3f)
            .model(goat(0x3a2a5c, 0x2a1f44, 0xf2f2ee, 0, false, 0xffd040, true, 0xd02020, 0x8a1f2b))
            .attacks(tridentSwipe("trident_swipe", 0xd02020, fire, 6),
                     fireRing("fire_circle_shrink", fire, 5, 18),
                     fireHandSweep("hand_fire_waves", fire, 5),
                     blueOrangeFlames("blue_orange_flames", 5),
                     tridentLunge("trident_lunge", 0xd02020, 7),
                     fireStorm("queens_wrath", .4f, fire, 6)).build());

        // Alphys: Royal Guard Captain (Undyne's role) - spears, green-soul shield, axe; secretly a coward.
        out.add(CharacterDef.of("underswap_alphys", "Alphys", Faction.ROYAL_GUARD).att(Attitude.HOSTILE).hp(170).boss()
            .keep(6).rest(14).speed(.3f).egg(0xf2d14b, 0x2a2d3a).size(.8f, 1.7f).scale(1.05f)
            .model(lizard(0xf2d14b, 0x2a2d3a, 0x5b6275, 0x111111, false, true, 0xffe066))
            .attacks(spearsFromGround("spear_volley", 0x5ff0ff, 5),
                     spearConverge("green_soul_shield", 0x5ff0ff, 5),
                     spearRain("spear_rain", 0x5ff0ff, 4, 3),
                     axeCleave("axe_cleave", 0xb0b8c8, 8),
                     thunderSpears("thunder_spears", 0xffe066, 5),
                     spearBarrage("captains_resolve", .35f, 0x5ff0ff, 5)).build());

        // Undyne: shy Royal Scientist (Alphys's role) who secretly runs the rebellion; built Napstaton.
        out.add(CharacterDef.of("underswap_undyne", "Undyne", Faction.ROYAL_SCIENCE).att(Attitude.NEUTRAL).foes(Faction.ROYAL_GUARD).hp(130).boss()
            .keep(8).rest(18).speed(.28f).egg(0x4aa3d8, 0xf0f0f0).size(.7f, 1.9f)
            .model(fish(0x4aa3d8, 0xd8262b, 0xf0f0f0, 0x3a3f55, 0xccf0ff, false))
            .attacks(drones("lab_drones", 0x9aa3b5, 0xff4040, 4),
                     laserSweep("blue_orange_lasers", 5),
                     bombs("bomb_defusal", 0x333333, 0xff3030, 6),
                     spearRain("aqua_spears", 0x40c8ff, 4, 2),
                     spearBarrage("rebellion_signal", .4f, 0x40c8ff, 5)).build());

        // Napstaton: Napstablook in Undyne's robot body, #1 radio DJ with retractable turntables.
        out.add(CharacterDef.of("underswap_napstaton", "Napstaton", Faction.ROYAL_SCIENCE).att(Attitude.HOSTILE).hp(200).boss()
            .keep(7).rest(14).speed(.3f).egg(0x22222a, 0xff4fa0).size(.8f, 2.2f).scale(1.05f)
            .model(robot(0x22222a, 0x3a3a48, 0xff4fa0, 0x9a6bff, false))
            .attacks(discThrow("vinyl_scratch", 0x111111, 0xff4fa0, 5),
                     beatRings("beat_drop", 0xff4fa0, 5),
                     speakerBeam("soundwave_beam", 0xff8fd0, 7),
                     bombs("speaker_bombs", 0x2a2a2a, 0xff4fa0, 6),
                     spotlights("disco_lights", 0xfff0a0, 4),
                     remix("ex_remix", .5f, 0xff4fa0, 0x9a6bff, 6)).build());

        // Mettaton: shy ghost (Napstablook's role) who dreams of stardom.
        out.add(CharacterDef.of("underswap_mettaton", "Mettaton", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(70)
            .keep(5).rest(24).speed(.2f).egg(0xf3d6e8, 0x222222).size(.7f, 1.7f)
            .model(ghost(0xf3d6e8, 0x222222, 0x222222))
            .attacks(tears("glitter_tears", 0xbfe8ff, 3),
                     hearts("dramatic_hearts", 0xff6fb0, 3),
                     spotlights("spotlight_sulk", 0xfff6d0, 3)).build());

        // Grillby: takes Muffet's place as the Hotland miniboss, with a fiery pet instead of a cupcake.
        out.add(CharacterDef.of("underswap_grillby", "Grillby", Faction.UNDERGROUND).att(Attitude.HOSTILE).hp(150).boss()
            .keep(6).rest(16).speed(.28f).egg(0xff7b1f, 0x111111).size(.7f, 1.9f)
            .model(flameMan(0xff7b1f, 0xffd040, 0x111111, 0xf0f0f0))
            .attacks(bottleToss("bottle_toss", 0x6bbf59, fire, 4),
                     flamePour("flame_pour", fire, 4),
                     glassRain("glass_rain", 0xd8f4ff, 3),
                     firePet("fire_pet", fire, 7)).build());

        // Muffet: runs the Snowdin maid cafe on roller skates (Grillby's role). Friendly.
        out.add(CharacterDef.of("underswap_muffet", "Muffet", Faction.UNDERGROUND).att(Attitude.FRIENDLY).hp(70)
            .keep(5).rest(22).speed(.36f).egg(0x9a4fd0, 0xffffff).size(.7f, 1.6f)
            .model(spider(0x9a4fd0, 0x2a1440, 0xffffff, 0xff2a6a, false))
            .attacks(traySpin("roller_tray_spin", 0xd8d8e0, 3), teaSplash("tea_splash", 0xc87a3a, 3)).build());

        // Asriel: tags along like Monster Kid, star-struck by the Royal Guard.
        out.add(CharacterDef.of("underswap_asriel", "Asriel", Faction.CHILDREN).att(Attitude.FRIENDLY).hp(40)
            .keep(4).rest(30).speed(.33f).egg(0xf2f2ee, 0x3fae5a).size(.5f, 1.1f).scale(.7f)
            .model(kidGoat())
            .attacks(starToss("star_toss", 0xffe066, 2)).build());
    }

    // =====================================================================================================
    // SWAPFELL
    // =====================================================================================================
    static void swapfell(List<CharacterDef> out) {
        int purple = 0x9b30ff, hell = 0xff3a1a, blood = 0xd01818;

        // Sans ("Black"/"Razz" in fanon): tyrannical Royal Guard lieutenant, purple magic, scarred.
        out.add(CharacterDef.of("swapfell_sans", "Sans", Faction.ROYAL_GUARD).att(Attitude.HOSTILE).hp(190).boss()
            .keep(6).rest(12).speed(.36f).egg(0x1a1420, 0x9b30ff).size(.7f, 1.1f).scale(.9f)
            .model(skeletonShort(0x1a1420, purple, 0x2a2030, 0x3a0a0a, purple, true))
            .attacks(boneRows("purple_bone_rush", purple, BLU, 6),
                     spikeCage("spike_cage", purple, 6),
                     soulSlam("blue_soul_impale", purple, 7),
                     boneWhip("bone_whip", purple, 6),
                     blasterCross("dark_blasters", 0xd8d0e0, purple, 0xd8a8ff, 8),
                     blasterFinale("execution", .4f, 0xd8d0e0, purple, 0xd8a8ff, purple, 8)).build());

        // Papyrus ("Mutt"/"Rus" in fanon): collared, gold-toothed, dark-orange magic; black attack; two-headed blaster.
        out.add(CharacterDef.of("swapfell_papyrus", "Papyrus", Faction.UNDERGROUND).att(Attitude.HOSTILE).hp(180).boss()
            .keep(8).rest(14).speed(.28f).egg(0xb4501a, 0x222222).size(.7f, 2.1f)
            .model(skeletonTall(0xb4501a, 0xd8c8a0, 0x2a2a2a, 0x1a1a1a, 0xff5a1f, 0xffd040, true))
            .attacks(blackAttack("black_attack", 9),
                     polyBlaster("polycephaly_blaster", 0xc8c0b0, 0xff5a1f, 0xffb070, 8),
                     groundGrid("rust_bone_spikes", 0xc8a080, 6),
                     smokeBones("ember_rain", ORG, 5),
                     chainLash("collar_chain", 0x6a6a6a, 6),
                     liftBlades("orange_drop", ORG, 6),
                     blasterFinale("pack_of_blasters", .4f, 0xc8c0b0, 0xff5a1f, 0xffb070, ORG, 8)).build());

        // Alphys: brutal Royal Guard Captain, executioner's axe and crimson spears.
        out.add(CharacterDef.of("swapfell_alphys", "Alphys", Faction.ROYAL_GUARD).att(Attitude.HOSTILE).hp(200).boss()
            .keep(6).rest(12).speed(.32f).egg(0xd8b030, 0x200a0a).size(.8f, 1.7f).scale(1.1f)
            .model(lizard(0xd8b030, 0x200a0a, 0x6a1010, blood, true, true, blood))
            .attacks(axeCleave("executioner_axe", 0x8a8a90, 10),
                     spearConverge("spear_gauntlet", blood, 6),
                     spearRain("red_spear_rain", blood, 6, 4),
                     spearsFromGround("impaling_spears", blood, 6),
                     spearBarrage("berserk_charge", .4f, blood, 7)).build());

        // Undyne: cold, cruel Royal Scientist; experiments and determination.
        out.add(CharacterDef.of("swapfell_undyne", "Undyne", Faction.ROYAL_SCIENCE).att(Attitude.HOSTILE).hp(160).boss()
            .keep(8).rest(14).speed(.3f).egg(0x2a6a8a, 0x8a0a0a).size(.7f, 1.9f)
            .model(fish(0x2a6a8a, 0x8a0a0a, 0xd0d0c8, 0x1a1a1a, 0xff4040, true))
            .attacks(drones("experiment_drones", 0x4a4a55, blood, 6),
                     spearRain("determination_syringes", 0xff2020, 5, 3),
                     spearsFromGround("acid_spears", 0x8aff3a, 6),
                     laserSweep("laser_grid", 7),
                     bombs("failed_experiments", 0x3a3a3a, 0x8aff3a, 7)).build());

        // Toriel: tyrant Queen of the fell underground.
        out.add(CharacterDef.of("swapfell_toriel", "Toriel", Faction.ROYAL_GUARD).att(Attitude.HOSTILE).hp(280).boss()
            .keep(6).rest(12).speed(.28f).egg(0x1a0a14, 0xff3a1a).size(.9f, 2.6f).scale(1.35f)
            .model(goat(0x1a0a14, 0x120810, 0xe8e2dc, 0, false, 0xd0a020, true, 0x5a0a0a, 0x5a0a0a))
            .attacks(tridentSwipe("hellfire_trident", 0x8a0a0a, hell, 8),
                     fireRing("crimson_fire_ring", hell, 7, 20),
                     blueOrangeFlames("judgment_flames", 7),
                     tridentLunge("tyrant_lunge", 0x8a0a0a, 9),
                     fireStorm("tyrant_judgment", .45f, hell, 8)).build());

        // Asgore: the Ruins' grim warden; his fire leaves no mercy.
        out.add(CharacterDef.of("swapfell_asgore", "Asgore", Faction.UNDERGROUND).att(Attitude.HOSTILE).hp(150).boss()
            .keep(6).rest(14).speed(.26f).egg(0x3a1a3a, 0xff3a1a).size(.9f, 2.6f).scale(1.3f)
            .model(goat(0x3a1a3a, 0x2a102a, 0xe8e2dc, 0xc0a040, true, 0xff3a1a, false, 0, 0))
            .attacks(fireRing("fire_cage", hell, 6, 18),
                     fireHandSweep("ruthless_sweep", hell, 6),
                     fireCascade("ember_cascade", hell, 6),
                     homingFlames("hunting_flames", hell, 5),
                     fireStorm("no_mercy_flames", .3f, hell, 7)).build());

        // Napstaton: distorted, aggressive DJ.
        out.add(CharacterDef.of("swapfell_napstaton", "Napstaton", Faction.ROYAL_SCIENCE).att(Attitude.HOSTILE).hp(220).boss()
            .keep(7).rest(12).speed(.32f).egg(0x111111, 0xd01818).size(.8f, 2.2f).scale(1.05f)
            .model(robot(0x141414, 0x2a1a1a, blood, 0xff7a1a, true))
            .attacks(discThrow("buzzsaw_discs", 0x2a2a2a, blood, 7),
                     beatRings("distortion_bass", blood, 7),
                     speakerBeam("feedback_beam", 0xff6060, 9),
                     spotlights("strobe_lights", 0xff9090, 6),
                     remix("encore_of_death", .45f, blood, 0xff7a1a, 8)).build());

        // Mettaton: bitter, venomous ghost.
        out.add(CharacterDef.of("swapfell_mettaton", "Mettaton", Faction.UNDERGROUND).att(Attitude.HOSTILE).hp(90)
            .keep(5).rest(20).speed(.22f).egg(0x6a5a70, 0xd01818).size(.7f, 1.7f)
            .model(ghost(0x8a7a90, 0x1a1a1a, blood))
            .attacks(tears("venom_tears", 0x8aff3a, 4), hearts("spite_hearts", blood, 4), spotlights("glare", 0xffd0d0, 4)).build());

        // Grillby: furious bar-brawler miniboss.
        out.add(CharacterDef.of("swapfell_grillby", "Grillby", Faction.UNDERGROUND).att(Attitude.HOSTILE).hp(170).boss()
            .keep(6).rest(14).speed(.3f).egg(0xff3a1a, 0x1a0a0a).size(.7f, 1.9f)
            .model(flameMan(0xc01a1a, 0xff7a1a, 0x1a0a0a, 0x5a0a0a))
            .attacks(bottleToss("molotov_toss", 0x5a3a1a, hell, 6),
                     flamePour("hellfire_pour", hell, 6),
                     glassRain("broken_glass", 0xffd0d0, 5),
                     firePet("inferno_pet", hell, 9)).build());

        // Muffet: Snowdin's ruthless cafe owner on bladed skates.
        out.add(CharacterDef.of("swapfell_muffet", "Muffet", Faction.UNDERGROUND).att(Attitude.HOSTILE).hp(110)
            .keep(5).rest(16).speed(.4f).egg(0x4a1a6a, 0xd01818).size(.7f, 1.6f)
            .model(spider(0x4a1a6a, 0x120612, 0x1a1a1a, blood, true))
            .attacks(traySpin("razor_skates", 0xa0a0b0, 5), teaSplash("poison_tea", 0x8aff3a, 5)).build());
    }

    // =====================================================================================================
    // DSL
    // =====================================================================================================
    interface Tk { void run(AttackCtx c, int t); }
    interface Ps { void run(Pose p, float t, UtMob m); }
    static Attack a(String id, int dur, Tk tk, Ps ps) { return a(id, dur, 1f, tk, ps); }
    /** gate < 1: phase attack, only usable while hpFrac < gate. */
    static Attack a(String id, int dur, float gate, Tk tk, Ps ps) {
        return new Attack(id, dur) {
            public void tick(AttackCtx c, int t) { tk.run(c, t); }
            public void pose(Pose p, float t, UtMob m) { ps.run(p, t, m); }
            public boolean usable(UtMob m) { return gate >= 1 || m.hpFrac() < gate; }
        };
    }
    /** 0 -> 1 over [0,in], hold until `hold`, back to 0 over `out`. Wind-up / strike / recovery envelope. */
    static float env(float t, float in, float hold, float out) {
        return t <= 0 ? 0 : t < in ? t / in : t < hold ? 1 : Math.max(0, 1 - (t - hold) / out);
    }
    static float sin(float t, float sp) { return (float) Math.sin(t * sp); }
    static Vec3 rotY(Vec3 v, double a) { double c = Math.cos(a), s = Math.sin(a); return new Vec3(v.x * c - v.z * s, v.y, v.x * s + v.z * c); }
    static Vec3 flat(Vec3 v) { Vec3 f = new Vec3(v.x, 0, v.z); return f.lengthSqr() < 1e-6 ? new Vec3(0, 0, 1) : f.normalize(); }
    static Vec3 feet(AttackCtx c) { return c.target.position(); }

    static UtProjectile.B tint(UtProjectile.B b, int col) { return col == BLU ? b.blue() : col == ORG ? b.orange() : b.color(col); }
    /** Bone flying horizontally (h = vertical length). */
    static void bone(AttackCtx c, Vec3 at, Vec3 vel, float h, int col, float dmg, int warn, int life) {
        tint(c.proj().at(at).vel(vel).box(.22f, h, .22f), col).dmg(dmg).warn(warn).life(life).spawn();
    }
    /** Bone erupting straight up from the ground (length along travel). */
    static void boneUp(AttackCtx c, Vec3 base, float h, double sp, int col, float dmg, int warn, int life) {
        tint(c.proj().at(base).vel(new Vec3(0, sp, 0)).box(.25f, .25f, h), col).dmg(dmg).warn(warn).life(life).spawn();
    }
    static void spear(AttackCtx c, Vec3 at, Vec3 vel, int col, float dmg, int warn, int life) {
        c.proj().at(at).vel(vel).box(.14f, .14f, 1.7f).color(col).dmg(dmg).warn(warn).life(life).spawn();
    }
    static void orb(AttackCtx c, Vec3 at, Vec3 vel, float s, int col, float dmg, int warn, int life, ParticleOptions trail) {
        c.proj().at(at).vel(vel).box(s, s, s).color(col).dmg(dmg).warn(warn).life(life).spawn();
        if (trail != null) c.particle(trail, at, 3, s * .4, .01);
    }
    static void fireball(AttackCtx c, Vec3 at, Vec3 vel, float s, int col, float dmg, int warn, int life) {
        orb(c, at, vel, s, col, dmg, warn, life, ParticleTypes.FLAME);
    }
    /** Gaster-Blaster style skull/emitter with custom palette; charges (thin telegraph) then fires. */
    static void blaster(AttackCtx c, Vec3 at, Vec3 dir, int charge, int fire, float dmg, float width, int skull, int eye, int beam) {
        dir = dir.normalize(); double len = 28; int l = charge + fire + 6;
        c.proj().at(at).face(dir).box(1.1f, 1.0f, 1.2f).color(skull).dmg(0).life(l).spawn();
        c.proj().at(at.add(dir.scale(.3)).add(0, -.55, 0)).face(dir).box(.8f, .35f, .9f).color(skull - 0x181818).dmg(0).life(l).spawn();
        c.proj().at(at.add(dir.scale(.55)).add(0, .15, 0)).face(dir).box(.9f, .2f, .12f).color(eye).dmg(0).life(l).spawn();
        c.proj().at(at.add(dir.scale(len / 2 + .6))).face(dir).box(width, width, (float) len).color(beam)
            .dmg(dmg).warn(charge).life(charge + fire).pierce().spawn();
        c.particle(ParticleTypes.END_ROD, at, 6, .4, .02);
        c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1f, 1.6f);
    }

    // ---- common poses ----
    static void throwPose(Pose p, float t, float period) {
        float ph = (t % period) / period, s = Math.min(1, t / 6f);
        float swing = ph < .4f ? -2.4f * (ph / .4f) : -2.4f + 3.0f * ((ph - .4f) / .6f);
        p.r("armR", swing * s, 0, -.2f * s).r("body", 0, .25f * s * (float) Math.sin(ph * Math.PI), 0);
    }
    static void castPose(Pose p, float t, float in, float hold, float out) {
        float s = env(t, in, hold, out);
        p.r("armL", -2.6f * s, 0, .4f * s).r("armR", -2.6f * s, 0, -.4f * s).r("head", -.3f * s, 0, 0).o("body", 0, .8f * s, 0);
    }
    static void slamPose(Pose p, float t, float hit) {
        float up = t < hit ? t / hit : 0, down = t >= hit ? Math.max(0, 1 - (t - hit) / 10f) : 0;
        p.r("armR", -2.9f * up - .3f * down, 0, -.2f).r("armL", -2.9f * up - .3f * down, 0, .2f)
         .r("body", .35f * down, 0, 0).o("body", 0, -1.2f * down + .6f * up, 0);
    }

    // =====================================================================================================
    // BONE ATTACKS (skeletons)
    // =====================================================================================================
    /** Rows of bones rush toward the target with a sliding gap; every third row is tall and coloured (blue: stay still). */
    static Attack boneRows(String id, int col, int alt, float dmg) {
        return a(id, 80, (c, t) -> {
            c.face(c.tpos());
            if (t >= 8 && t < 56 && t % 6 == 2) {
                int row = (t - 8) / 6; boolean tall = row % 3 == 2; Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x);
                int gap = row % 7 - 3;
                for (int k = -4; k <= 4; k++) {
                    if (!tall && Math.abs(k - gap) < 1) continue;
                    Vec3 at = c.pos().add(f.scale(2)).add(r.scale(k * .8)).add(0, tall ? 1.1 : .35, 0);
                    bone(c, at, f.scale(.75), tall ? 2.3f : .7f, tall ? alt : col, dmg, 5, 40);
                }
                c.particle(ParticleTypes.SNOWFLAKE, c.pos().add(f.scale(2)).add(0, .3, 0), 6, .6, .02);
                c.sound(SoundEvents.SKELETON_SHOOT, .8f, tall ? 1.1f : 1.6f);
            }
        }, (p, t, m) -> { if (t < 8) p.r("armR", .6f * t / 8f, 0, 0).r("body", 0, -.3f * t / 8f, 0); else if (t < 60) throwPose(p, t - 8, 6); });
    }
    /** Bones erupt from the ground in three lanes, the wave travelling from the caster to the target. */
    static Attack boneHurdles(String id, int col, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t >= 10 && t < 70 && t % 3 == 0) {
                int wave = (t - 10) / 20, step = ((t - 10) % 20) / 3;
                Vec3 from = c.pos(), f = flat(c.tpos().subtract(from)), r = new Vec3(-f.z, 0, f.x);
                for (int lane = -1; lane <= 1; lane++) {
                    if (wave % 2 == 1 && lane == 0) continue;
                    Vec3 base = from.add(f.scale(1.5 + step * 1.3)).add(r.scale(lane * 1.6)).add(0, -.9, 0);
                    boneUp(c, base, 1.4f + (step % 2) * .5f, .18, col, dmg, 6, 14);
                    c.particle(ParticleTypes.POOF, base.add(0, 1, 0), 2, .2, .01);
                }
                c.sound(SoundEvents.BONE_BLOCK_BREAK, .7f, 1.4f);
            }
        }, (p, t, m) -> {
            float ph = ((t - 10) % 20) / 20f, lift = t < 10 ? t / 10f : (ph < .2f ? 1 - ph * 5 : 0);
            p.r("legR", -1.2f * lift, 0, 0).o("body", 0, .5f * lift, 0).r("armL", -.8f, 0, .5f).r("armR", -.4f * lift, 0, -.6f);
        });
    }
    /** Blue-soul slam: target is hurled to the ground twice; after each slam a ring of bones erupts with one gap. */
    static Attack soulSlam(String id, int col, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t == 6 || t == 44) c.particle(ParticleTypes.SOUL_FIRE_FLAME, c.tpos(), 20, .5, .05);
            if (t == 12 || t == 50) {
                c.target.setDeltaMovement(c.target.getDeltaMovement().x, -1.6, c.target.getDeltaMovement().z); c.target.hurtMarked = true;
                c.sound(SoundEvents.ANVIL_LAND, 1, .6f); c.particle(ParticleTypes.EXPLOSION, feet(c), 2, .5, 0);
            }
            if (t == 16 || t == 54) {
                Vec3 ctr = feet(c); int gap = c.rnd.nextInt(12);
                for (int i = 0; i < 12; i++) {
                    if (i == gap) continue;
                    double ang = i * Math.PI / 6; Vec3 base = ctr.add(Math.cos(ang) * 2.2, -1, Math.sin(ang) * 2.2);
                    boneUp(c, base, 2.0f, .22, col, dmg, 10, 18);
                }
            }
            if (t >= 24 && t < 40 && t % 4 == 0) boneUp(c, feet(c).add(c.rnd.nextGaussian(), -1.2, c.rnd.nextGaussian()), 1.6f, .3, col, dmg, 8, 12);
        }, (p, t, m) -> { float tt = t < 38 ? t : t - 38; slamPose(p, tt, 12); p.r("head", .25f * env(tt, 6, 14, 8), 0, 0); });
    }
    /** Three spinning bones thrown in a fan, then flying back to the thrower. */
    static Attack boneBoomerang(String id, int col, float dmg) {
        return a(id, 70, (c, t) -> {
            Vec3 f = flat(c.aim(c.pos()));
            if (t == 10 || t == 34) {
                for (int k = -1; k <= 1; k++) {
                    Vec3 d = rotY(f, k * .5), at = c.pos().add(0, 1, 0).add(d);
                    c.proj().at(at).vel(d.scale(.8)).box(1.6f, .3f, .3f).color(col).dmg(dmg).warn(3).life(16).pierce().spawn();
                }
                c.sound(SoundEvents.TRIDENT_THROW, 1, 1.4f);
            }
            if (t == 26 || t == 50) {
                for (int k = -1; k <= 1; k++) {
                    Vec3 d = rotY(f, k * .5), at = c.pos().add(0, 1, 0).add(d.scale(13));
                    c.proj().at(at).vel(d.scale(-.8)).box(1.6f, .3f, .3f).color(col).dmg(dmg).life(16).pierce().spawn();
                    c.particle(ParticleTypes.CRIT, at, 6, .3, .1);
                }
            }
        }, (p, t, m) -> {
            float tt = t < 30 ? t : t - 24, s = env(tt, 8, 10, 8);
            p.r("armR", -2.2f * s + 1.2f * env(tt - 10, 2, 4, 6), (float) -Math.PI / 4 * s, 0).r("body", 0, -.4f * s, 0);
        });
    }
    /** Blasters summoned on a cross around the target: cardinals then diagonals, each set fires together. */
    static Attack blasterCross(String id, int skull, int eye, int beam, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t == 6 || t == 36) {
                double off = t == 36 ? Math.PI / 4 : 0;
                for (int i = 0; i < 4; i++) {
                    double ang = off + i * Math.PI / 2; Vec3 at = c.tpos().add(Math.cos(ang) * 7, 2.5, Math.sin(ang) * 7);
                    blaster(c, at, c.tpos().subtract(at), 22, 12, dmg, .9f, skull, eye, beam);
                }
            }
            if (t == 66) { Vec3 at = c.pos().add(0, 3.5, 0); blaster(c, at, c.tpos().subtract(at), 14, 10, dmg, 1.3f, skull, eye, beam); }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 10);
            p.r("armL", -1.6f * s, 0, 1.2f * s).r("armR", -1.6f * s, 0, -1.2f * s).r("head", -.2f * s, 0, 0)
             .r("armR", (t > 60 && t < 70 ? -1.2f : 0), 0, 0);
        });
    }
    /** Five-pointed star of bones bursting outward from the caster, twice, rotated. */
    static Attack boneStar(String id, int col, float dmg) {
        return a(id, 60, (c, t) -> {
            if (t == 12 || t == 30) {
                double rot = t == 30 ? Math.PI / 5 : 0;
                for (int i = 0; i < 5; i++) for (int j = 0; j < 3; j++) {
                    double ang = rot + i * 2 * Math.PI / 5; Vec3 d = new Vec3(Math.cos(ang), 0, Math.sin(ang));
                    bone(c, c.pos().add(0, .8, 0).add(d.scale(1 + j * .6)), d.scale(.5 + j * .08), 1.4f, col, dmg, 4, 30);
                }
                c.particle(ParticleTypes.FIREWORK, c.eye(), 16, .3, .15);
                c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, 1.5f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 34, 10);
            p.r("armL", -.5f * s, 0, -.6f * s).r("armR", -.5f * s, 0, .6f * s).r("head", 0, 0, .25f * s * sin(t, .5f)).o("body", 0, .4f * s, 0);
        });
    }
    /** Finale: walls of bones, a ring of blasters, then a giant bone sweeping through. */
    static Attack blasterFinale(String id, float gate, int skull, int eye, int beam, int boneCol, float dmg) {
        return a(id, 160, gate, (c, t) -> {
            Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x);
            if (t < 50 && t % 10 == 0) {
                int gap = c.rnd.nextInt(7) - 3;
                for (int s = -1; s <= 1; s += 2) for (int k = -4; k <= 4; k++) {
                    if (Math.abs(k - gap) <= 1) continue;
                    Vec3 at = c.tpos().add(r.scale(s * 8)).add(f.scale(k * .8)).add(0, -.4, 0);
                    bone(c, at, r.scale(-s * .6), 2.1f, (t / 10) % 2 == 0 ? 0xffffff : boneCol, dmg * .7f, 8, 26);
                }
                c.sound(SoundEvents.SKELETON_SHOOT, 1, 1.2f);
            }
            if (t >= 50 && t < 98 && t % 4 == 2) {
                double ang = (t - 50) * .4; Vec3 at = c.tpos().add(Math.cos(ang) * 7, 2 + ((t / 4) % 3), Math.sin(ang) * 7);
                blaster(c, at, c.tpos().subtract(at), 16, 10, dmg, .8f, skull, eye, beam);
            }
            if (t == 110) {
                Vec3 at = c.tpos().add(r.scale(-10)).add(0, .2, 0);
                c.proj().at(at).vel(r.scale(.55)).box(.7f, 5.5f, .7f).color(boneCol == BLU || boneCol == ORG ? 0xffffff : boneCol)
                    .dmg(dmg * 1.5f).warn(14).life(50).pierce().spawn();
                c.sound(SoundEvents.ENDER_DRAGON_GROWL, .8f, 1.6f);
            }
            if (t > 130 && t % 6 == 0) c.particle(ParticleTypes.FIREWORK, c.eye(), 10, .5, .1);
        }, (p, t, m) -> {
            float s = Math.min(1, t / 10f), fl = sin(t, .4f);
            if (t < 50) throwPose(p, t, 10);
            else if (t < 100) p.r("armL", -2.8f * s, 0, .5f).r("armR", -2.8f * s, 0, -.5f).o("body", 0, 2f + fl * .4f, 0);
            else { float sw = env(t - 100, 10, 30, 20); p.r("armR", -1.4f * sw, 1.2f * sw, 0).r("body", 0, .9f * sw, 0); }
            p.r("bandanaTail", .5f + .3f * fl, 0, 0).r("scarfTail", .5f + .3f * fl, 0, 0);
        });
    }
    /** Purple bone cage closes around the target from all sides. */
    static Attack spikeCage(String id, int col, float dmg) {
        return a(id, 70, (c, t) -> {
            if (t == 8 || t == 36) {
                Vec3 ctr = c.tpos(); int gap = c.rnd.nextInt(16);
                for (int i = 0; i < 16; i++) {
                    if (i == gap || i == (gap + 1) % 16) continue;
                    double ang = i * Math.PI / 8; Vec3 d = new Vec3(Math.cos(ang), 0, Math.sin(ang));
                    bone(c, ctr.add(d.scale(6)), d.scale(-.22), 2.6f, col, dmg, 12, 32);
                }
                c.particle(ParticleTypes.WITCH, ctr, 30, 3, .05); c.sound(SoundEvents.CHAIN_PLACE, 1, .6f);
            }
        }, (p, t, m) -> {
            float s = env(t % 28, 6, 12, 10);
            p.r("armL", -1.4f * s, 0, 1.0f * s).r("armR", -1.4f * s, 0, -1.0f * s).r("armL", 0, 0, -1.6f * env(t % 28, 12, 14, 6))
             .r("armR", 0, 0, 1.6f * env(t % 28, 12, 14, 6));
        });
    }
    /** A chain of bones whipped around in a widening sweep (Swapfell Sans's riding-crop flourish). */
    static Attack boneWhip(String id, int col, float dmg) {
        return a(id, 70, (c, t) -> {
            if (t >= 10 && t < 46 && t % 2 == 0) {
                double ang = Math.atan2(c.fwd().z, c.fwd().x) - 1.6 + (t - 10) * .09;
                for (int k = 1; k <= 5; k++) {
                    Vec3 at = c.pos().add(Math.cos(ang) * k * 1.1, 1.0, Math.sin(ang) * k * 1.1);
                    c.proj().at(at).box(.3f, 1.4f, .3f).color(col).dmg(dmg).warn(k == 5 ? 3 : 1).life(4).spawn();
                }
                if (t % 8 == 0) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .8f);
            }
            if (t == 50) c.aoe(c.pos(), 4, dmg, 1.2);
        }, (p, t, m) -> {
            float s = env(t, 10, 46, 12), sweep = t < 10 ? -1.2f * t / 10f : -1.2f + Math.min(1, (t - 10) / 36f) * 3.2f;
            p.r("armR", -1.4f * s, sweep * s, 0).r("body", 0, sweep * .4f * s, 0).r("scarfTail", .6f * s, 0, 0);
        });
    }

    // ---- Papyrus-style ----
    static Attack boneRain(String id, int col, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t >= 8 && t < 60 && t % 3 == 0) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 2.5, 7, c.rnd.nextGaussian() * 2.5);
                tint(c.proj().at(at).vel(new Vec3(0, -.55, 0)).box(.22f, .22f, 1.6f), col).dmg(dmg).warn(8).life(24).spawn();
            }
            if (t >= 8 && t < 60 && t % 12 == 0) {
                Vec3 ctr = feet(c);
                for (int i = 0; i < 8; i++) { double an = i * Math.PI / 4 + t; tint(c.proj().at(ctr.add(Math.cos(an) * 3, 8, Math.sin(an) * 3)).vel(new Vec3(0, -.6, 0)).box(.22f, .22f, 1.6f), col).dmg(dmg).warn(10).life(24).spawn(); }
                c.sound(SoundEvents.SKELETON_SHOOT, .9f, .9f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 60, 12);
            p.r("armR", -2.8f * s, 0, -.2f * s).r("armR", .25f * s * sin(t, .8f), 0, 0).r("armL", -.3f * s, 0, .25f * s).r("head", -.25f * s, 0, 0);
        });
    }
    /** One lazy blaster, then two, then four, each set triggered by a finger-snap. */
    static Attack blasterVolley(String id, int skull, int eye, int beam, float dmg) {
        return a(id, 95, (c, t) -> {
            int n = t == 8 ? 1 : t == 34 ? 2 : t == 60 ? 4 : 0;
            for (int i = 0; i < n; i++) {
                double ang = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = c.tpos().add(Math.cos(ang) * 6, 3 + c.rnd.nextDouble() * 2, Math.sin(ang) * 6);
                blaster(c, at, c.tpos().subtract(at), 20, 12, dmg, 1f, skull, eye, beam);
            }
            if (n > 0) c.particle(ParticleTypes.SMOKE, c.eye(), 6, .2, .02);
        }, (p, t, m) -> {
            float snap = 0; for (int k : new int[]{8, 34, 60}) snap = Math.max(snap, env(t - k + 4, 4, 5, 6));
            p.r("armR", -1.8f * snap, 0, -.3f * snap).r("armL", .2f, 0, .2f).r("head", .15f, .3f * snap, 0);
        });
    }
    /** Checkerboard of bones stabbing up under the target, alternating squares. */
    static Attack groundGrid(String id, int col, float dmg) {
        return a(id, 84, (c, t) -> {
            if (t == 10 || t == 30 || t == 50) {
                Vec3 ctr = feet(c); int par = (t / 20) % 2;
                for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) {
                    if (((x + z) & 1) != par) continue;
                    boneUp(c, ctr.add(x * 1.1, -1.4, z * 1.1), 1.8f, .26, col, dmg, 12, 18);
                }
                c.sound(SoundEvents.BONE_BLOCK_PLACE, 1, .7f);
            }
        }, (p, t, m) -> {
            float y = env(t, 8, 12, 10);
            p.r("head", -.5f * y, 0, 0).r("armL", -2.8f * y, 0, .6f * y).r("armR", -2.8f * y, 0, -.6f * y)
             .r("armR", 0, 0, .4f * env((t - 10) % 20, 2, 4, 6));
        });
    }
    /** Exhaled smoke cloud around the target, then bones lance out of it from every side. */
    static Attack smokeBones(String id, int col, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t >= 16 && t < 30 && t % 2 == 0) { c.particle(ParticleTypes.CAMPFIRE_COSY_SMOKE, c.tpos(), 6, 2.2, .01); c.particle(ParticleTypes.LAVA, c.eye(), 1, .1, 0); }
            if (t >= 30 && t < 74 && t % 4 == 0) {
                double ang = c.rnd.nextDouble() * Math.PI * 2; Vec3 at = c.tpos().add(Math.cos(ang) * 4.5, c.rnd.nextDouble() - .3, Math.sin(ang) * 4.5);
                bone(c, at, c.aim(at).scale(.6), 1.2f, (t / 4) % 3 == 0 ? col : 0xffffff, dmg, 6, 20);
                c.particle(ParticleTypes.SMOKE, at, 4, .2, .02);
            }
        }, (p, t, m) -> {
            float drag = env(t, 6, 12, 6), blow = env(t - 14, 3, 14, 6);
            p.r("armR", -2.0f * drag, .6f * drag, 0).r("head", -.2f * drag + .3f * blow, 0, 0).o("head", 0, 0, -.6f * blow);
        });
    }
    /** Dozes at his sentry post while slow alternating walls drift past. */
    static Attack napDrift(String id, int c1, int c2, float dmg) {
        return a(id, 100, (c, t) -> {
            if (t % 14 == 0) c.particle(ParticleTypes.CLOUD, c.eye().add(0, .6, 0), 2, .1, .02);
            if (t >= 10 && t < 86 && t % 15 == 10) {
                Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x); int w = (t - 10) / 15; boolean alt = w % 2 == 1;
                for (int k = -5; k <= 5; k++) bone(c, c.tpos().add(f.scale(-9)).add(r.scale(k * .7)).add(0, alt ? .4 : -.2, 0), f.scale(.35), alt ? 2.4f : .8f, alt ? c2 : c1, dmg, 8, 52);
            }
        }, (p, t, m) -> {
            float s = Math.min(1, t / 12f);
            p.r("head", .6f * s, 0, .3f * s).r("body", .2f * s, 0, 0).r("armL", .2f * s, 0, .1f).r("armR", .2f * s, 0, -.1f).o("body", 0, .2f * sin(t, .1f), 0);
        });
    }
    /** Flicks the target into the air, then blade-bones cross at its height; in the air you must keep moving. */
    static Attack liftBlades(String id, int col, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t == 8) { c.target.setDeltaMovement(c.target.getDeltaMovement().add(0, 1.1, 0)); c.target.hurtMarked = true; c.sound(SoundEvents.SHULKER_SHOOT, 1, 1.3f); c.particle(ParticleTypes.END_ROD, feet(c), 12, .4, .1); }
            if (t >= 14 && t < 60 && t % 6 == 2) {
                Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x); int s = (t / 6) % 2 == 0 ? 1 : -1;
                Vec3 at = c.tpos().add(r.scale(s * 7)).add(f.scale(c.rnd.nextGaussian()));
                tint(c.proj().at(at).vel(r.scale(-s * .7)).box(.25f, .25f, 2.6f), col).dmg(dmg).warn(6).life(22).spawn();
            }
        }, (p, t, m) -> { float s = env(t, 6, 10, 10); p.r("armR", -2.8f * s, 0, -.3f).r("armR", .4f * env(t - 6, 1, 3, 4), 0, 0); });
    }
    /** Swapfell Papyrus: slow, huge black bones that hit hard (fanon "black attack"). */
    static Attack blackAttack(String id, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t < 20 && t % 3 == 0) c.particle(ParticleTypes.SQUID_INK, c.eye(), 6, .4, .02);
            if (t == 20 || t == 45 || t == 70) {
                Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x); int gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) {
                    if (k == gap) continue;
                    c.proj().at(c.pos().add(f.scale(2)).add(r.scale(k * 1.1)).add(0, 1.2, 0)).vel(f.scale(.42)).box(.6f, 3f, .6f)
                        .color(0x0a0a0a).dmg(dmg).warn(10).life(46).pierce().spawn();
                }
                c.sound(SoundEvents.WITHER_SHOOT, .8f, .6f);
            }
        }, (p, t, m) -> {
            float k = Math.max(env(t - 12, 8, 10, 6), Math.max(env(t - 37, 8, 10, 6), env(t - 62, 8, 10, 6)));
            p.r("armR", -2.4f * k, 0, -.2f).r("armL", -2.4f * k, 0, .2f).r("body", -.2f * k, 0, 0).r("head", .3f * Math.min(1, t / 10f), 0, 0);
        });
    }
    /** Swapfell Papyrus: two-headed blaster; both heads fire diverging beams that sweep inward. */
    static Attack polyBlaster(String id, int skull, int eye, int beam, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t == 8 || t == 40) {
                Vec3 base = c.pos().add(0, 3.2, 0), d = flat(c.aim(base)); Vec3 r = new Vec3(-d.z, 0, d.x);
                for (int s = -1; s <= 1; s += 2) {
                    Vec3 at = base.add(r.scale(s * .9));
                    blaster(c, at, rotY(d, s * (t == 8 ? .35 : .12)).add(0, -.12, 0), 20, 14, dmg, 1.1f, skull, eye, beam);
                }
            }
            if (t == 70) {
                Vec3 at = c.tpos().add(0, 6, 0); for (int s = -1; s <= 1; s += 2) blaster(c, at.add(s * .9, 0, 0), new Vec3(s * .25, -1, 0), 14, 12, dmg, 1.2f, skull, eye, beam);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 80, 10);
            p.r("armL", -1.6f * s, -.6f * s, 0).r("armR", -1.6f * s, .6f * s, 0).r("head", -.2f * s, 0, 0).o("body", 0, .4f * s * sin(t, .5f), 0);
        });
    }
    /** Swapfell Papyrus: chain from his collar lashes out in a line toward the target, then yanks back sideways. */
    static Attack chainLash(String id, int col, float dmg) {
        return a(id, 70, (c, t) -> {
            Vec3 f = flat(c.aim(c.pos()));
            if (t >= 10 && t < 24) {
                int k = t - 9; Vec3 at = c.pos().add(0, 1.4, 0).add(f.scale(k * .8));
                c.proj().at(at).face(f).box(.35f, .35f, .7f).color(k % 2 == 0 ? col : 0x9a9a9a).dmg(dmg).warn(2).life(30 - k).spawn();
                if (k % 3 == 0) c.sound(SoundEvents.CHAIN_HIT, 1, 1.0f);
            }
            if (t == 36) {
                Vec3 r = new Vec3(-f.z, 0, f.x);
                for (int k = 1; k <= 12; k++) c.proj().at(c.pos().add(0, 1, 0).add(f.scale(k * .9)).add(r.scale(-3))).vel(r.scale(.45)).box(.4f, .4f, .9f)
                    .color(col).dmg(dmg).warn(6).life(18).spawn();
                c.sound(SoundEvents.CHAIN_BREAK, 1, .7f);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 26, 8), y = env(t - 30, 6, 10, 10);
            p.r("armR", -1.6f * s - .5f * y, 1.5f * y, 0).r("head", .3f * s, 0, 0).r("body", 0, -.6f * y, 0);
        });
    }

    // =====================================================================================================
    // FIRE ATTACKS (goats)
    // =====================================================================================================
    /** Two fire hands sweep along the target's sides leaving fireballs that drift inward. */
    static Attack fireHandSweep(String id, int col, float dmg) {
        return a(id, 84, (c, t) -> {
            Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x);
            if (t >= 10 && t < 58 && t % 3 == 1) {
                double prog = (t - 10) / 48.0 * 14 - 7;
                for (int s = -1; s <= 1; s += 2) {
                    Vec3 hand = c.tpos().add(r.scale(s * 5)).add(f.scale(prog));
                    c.particle(ParticleTypes.FLAME, hand, 8, .3, .02);
                    fireball(c, hand, r.scale(-s * .22), .45f, col, dmg, 4, 30);
                }
                if (t % 9 == 1) c.sound(SoundEvents.BLAZE_SHOOT, .7f, 1f);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 58, 12);
            p.r("armL", -1.3f * s, 0, 1.1f * s).r("armR", -1.3f * s, 0, -1.1f * s).r("head", .4f * s, 0, 0).o("body", 0, -.3f * s, 0);
        });
    }
    /** Volleys of fireballs lobbed high, falling around and onto the target. */
    static Attack fireCascade(String id, int col, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t >= 8 && t < 62 && t % 5 == 3) {
                Vec3 from = c.eye().add(0, .5, 0), to = feet(c).add(c.rnd.nextGaussian() * 1.8, 0, c.rnd.nextGaussian() * 1.8);
                Vec3 d = to.subtract(from); double T = 22; double g = .03;
                Vec3 v = new Vec3(d.x / T, (d.y + .5 * g * T * T) / T, d.z / T);
                c.proj().at(from).vel(v).box(.5f, .5f, .5f).color(col).dmg(dmg).gravity((float) g).life(30).spawn();
                c.particle(ParticleTypes.FLAME, from, 5, .2, .03); c.sound(SoundEvents.FIRECHARGE_USE, .6f, 1.2f);
            }
        }, (p, t, m) -> {
            float ph = ((t - 3) % 5) / 5f, s = env(t, 8, 62, 10);
            p.r("armR", (-2.6f + .8f * ph) * s, 0, -.3f * s).r("armL", (-1.0f - .8f * ph) * s, 0, .3f * s).r("head", -.2f * s, 0, 0);
        });
    }
    /** Ring of flames materialises around the target and closes in; there is a gap to slip through. */
    static Attack fireRing(String id, int col, float dmg, int n) {
        return a(id, 80, (c, t) -> {
            if (t == 10 || t == 40) {
                Vec3 ctr = c.tpos(); int gap = c.rnd.nextInt(n);
                for (int i = 0; i < n; i++) {
                    if (i == gap || i == (gap + 1) % n) continue;
                    double ang = i * Math.PI * 2 / n; Vec3 d = new Vec3(Math.cos(ang), 0, Math.sin(ang));
                    fireball(c, ctr.add(d.scale(6)), d.scale(-.16), .55f, col, dmg, 12, 40);
                }
                c.sound(SoundEvents.FIRE_AMBIENT, 1.5f, .8f);
            }
            if (t > 10 && t % 4 == 0) c.particle(ParticleTypes.FLAME, c.tpos(), 3, 3, .01);
        }, (p, t, m) -> { castPose(p, t % 30, 8, 14, 10); });
    }
    /** Small flames spawn around the caster and hunt the target in two re-aimed waves. */
    static Attack homingFlames(String id, int col, float dmg) {
        return a(id, 76, (c, t) -> {
            if (t == 10 || t == 34 || t == 52) {
                for (int i = 0; i < 6; i++) {
                    double ang = i * Math.PI / 3 + t; Vec3 at = c.eye().add(Math.cos(ang) * 1.8, Math.sin(ang * 2) * .5, Math.sin(ang) * 1.8);
                    fireball(c, at, c.aim(at).scale(.38), .35f, col, dmg, 8 + i * 2, 40);
                }
                c.sound(SoundEvents.BLAZE_SHOOT, .8f, 1.4f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 60, 10);
            p.r("armR", -1.7f * s, -.3f * s, 0).r("armL", -1.0f * s, .3f * s, .4f * s).o("armR", 0, .5f * sin(t, .6f) * s, 0);
        });
    }
    /** Low HP: flames deliberately swerve away from the target (Asgore can't bring himself to hurt the child). */
    static Attack gentleFlames(String id, float gate) {
        return a(id, 70, gate, (c, t) -> {
            if (t >= 8 && t < 56 && t % 4 == 0) {
                Vec3 at = c.eye(), aim = c.aim(at), side = new Vec3(-aim.z, 0, aim.x).scale(c.rnd.nextBoolean() ? .25 : -.25);
                fireball(c, at, aim.add(side).normalize().scale(.4), .35f, 0xffa860, .5f, 4, 36);
            }
            if (t % 10 == 0) c.particle(ParticleTypes.HEART, c.eye().add(0, .8, 0), 1, .2, 0);
        }, (p, t, m) -> {
            float s = Math.min(1, t / 10f);
            p.r("head", .5f * s, 0, 0).r("armR", -1.0f * s, 0, 0).r("armL", -.2f * s, 0, .3f * s).r("body", .15f * s, 0, 0);
        });
    }
    /** Trident sweep: a sequential arc of blade segments in front of the caster, followed by fire trails. */
    static Attack tridentSwipe(String id, int col, int fire, float dmg) {
        return a(id, 70, (c, t) -> {
            Vec3 f = flat(c.aim(c.pos()));
            if (t >= 16 && t < 28) {
                double ang = -1.4 + (t - 16) * .25; Vec3 d = rotY(f, ang);
                for (int k = 1; k <= 5; k++) c.proj().at(c.pos().add(0, 1.2, 0).add(d.scale(k * .9))).face(d).box(.3f, .3f, 1f).color(k == 5 ? fire : col).dmg(dmg).life(5).spawn();
                if (t == 16) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.4f, .6f);
            }
            if (t == 28 || t == 44) for (int i = -3; i <= 3; i++) {
                Vec3 d = rotY(f, i * .22); fireball(c, c.pos().add(0, 1, 0).add(d.scale(2)), d.scale(.45), .5f, fire, dmg * .7f, 4, 30);
            }
            if (t == 6) c.particle(ParticleTypes.FLAME, c.eye(), 10, .5, .02);
        }, (p, t, m) -> {
            float w = env(t, 14, 16, 2), sw = t < 16 ? 0 : env(t - 16, 10, 16, 10);
            p.r("armR", -1.4f * (w + sw), 1.4f * w - 2.2f * sw, 0).r("body", 0, .6f * w - .8f * sw, 0).r("cape", .3f * sw, 0, 0);
        });
    }
    /** Alternating rows of blue (stay still) and orange (keep moving) fire. */
    static Attack blueOrangeFlames(String id, float dmg) {
        return a(id, 84, (c, t) -> {
            if (t >= 10 && t < 70 && t % 12 == 10) {
                Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x); boolean blue = ((t - 10) / 12) % 2 == 0;
                for (int k = -6; k <= 6; k++) {
                    UtProjectile.B b = c.proj().at(c.pos().add(f.scale(1.5)).add(r.scale(k * .6)).add(0, .8, 0)).vel(f.scale(.5)).box(.55f, 1.8f, .4f).dmg(dmg).warn(6).life(40);
                    (blue ? b.blue() : b.orange()).spawn();
                }
                c.particle(blue ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, c.pos().add(f.scale(1.5)).add(0, 1, 0), 20, 1.5, .02);
                c.sound(SoundEvents.BLAZE_SHOOT, 1, blue ? .7f : 1.3f);
            }
        }, (p, t, m) -> {
            float ph = ((t - 4) % 12) / 12f, s = env(t, 8, 70, 10);
            p.r("armR", (-2.4f + 2.2f * Math.min(1, ph * 3)) * s, 0, -.3f).r("armL", -.8f * s, 0, .3f);
        });
    }
    /** Three telegraphed dashes at the target, trident first, with a fire burst at each end. */
    static Attack tridentLunge(String id, int col, float dmg) {
        return a(id, 84, (c, t) -> {
            int ph = t % 26;
            if (t < 78 && ph == 0) {
                Vec3 f = c.aim(c.pos()); c.face(c.tpos());
                for (int k = 1; k <= 8; k++) c.proj().at(c.pos().add(0, 1, 0).add(f.scale(k))).face(f).box(.15f, .15f, 1f).color(col).dmg(0).warn(10).life(10).spawn();
            }
            if (t < 78 && ph == 10) { c.dash(flat(c.aim(c.pos())).scale(1.6).add(0, .15, 0)); c.sound(SoundEvents.TRIDENT_RIPTIDE_1, 1, 1); }
            if (t < 78 && ph >= 11 && ph <= 16) { c.aoe(c.pos().add(0, 1, 0).add(c.fwd()), 1.8, dmg, .8); c.particle(ParticleTypes.FLAME, c.pos().add(0, 1, 0), 6, .4, .05); }
        }, (p, t, m) -> {
            float ph = t % 26, w = env(ph, 8, 10, 2), st = env(ph - 10, 1, 6, 8);
            p.r("armR", -1.6f * (w + st), 0, 0).o("armR", 0, 0, 3f * w - 4f * st).r("body", .5f * st - .2f * w, 0, 0).r("cape", .9f * st, 0, 0);
        });
    }
    /** Phase finale: fire rain, rotating rings and erupting pillars. */
    static Attack fireStorm(String id, float gate, int col, float dmg) {
        return a(id, 140, gate, (c, t) -> {
            if (t < 50 && t % 2 == 0) fireball(c, feet(c).add(c.rnd.nextGaussian() * 3, 8, c.rnd.nextGaussian() * 3), new Vec3(0, -.5, 0), .45f, col, dmg, 8, 24);
            if (t >= 50 && t < 96 && t % 8 == 2) {
                Vec3 ctr = c.pos().add(0, 1, 0); double off = t * .2;
                for (int i = 0; i < 10; i++) { double a = off + i * Math.PI / 5; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a)); fireball(c, ctr.add(d), d.scale(.4), .5f, col, dmg, 3, 40); }
                c.sound(SoundEvents.BLAZE_SHOOT, 1, .8f);
            }
            if (t >= 96 && t < 128 && t % 6 == 0) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian(), -1, c.rnd.nextGaussian());
                c.proj().at(at).vel(new Vec3(0, .35, 0)).box(1.2f, 1.2f, 3f).color(col).dmg(dmg * 1.2f).warn(12).life(22).pierce().spawn();
                c.particle(ParticleTypes.LAVA, at.add(0, 1, 0), 4, .4, 0);
            }
            if (t == 0) c.sound(SoundEvents.RAVAGER_ROAR, 1, .7f);
        }, (p, t, m) -> {
            if (t < 50) castPose(p, t, 8, 46, 6);
            else if (t < 96) p.r("armL", -1.5f, 0, 1.4f).r("armR", -1.5f, 0, -1.4f).r("body", 0, (t - 50) * .25f, 0);
            else slamPose(p, (t - 96) % 12 + 4, 6);
            p.r("cape", .4f + .2f * sin(t, .5f), 0, 0);
        });
    }

    // =====================================================================================================
    // SPEARS / GUARD
    // =====================================================================================================
    static Attack spearsFromGround(String id, int col, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t >= 8 && t < 64 && t % 5 == 3) {
                Vec3 at = feet(c).add(c.target.getDeltaMovement().scale(6)).add(c.rnd.nextGaussian() * .4, -1.6, c.rnd.nextGaussian() * .4);
                c.proj().at(at).vel(new Vec3(0, .4, 0)).box(.18f, .18f, 2.2f).color(col).dmg(dmg).warn(10).life(16).spawn();
                c.particle(ParticleTypes.ENCHANTED_HIT, at.add(0, 1.6, 0), 6, .3, .02);
            }
            if (t % 15 == 3) c.sound(SoundEvents.TRIDENT_HIT_GROUND, 1, 1.3f);
        }, (p, t, m) -> {
            float ph = ((t - 3) % 5) / 5f, s = env(t, 8, 64, 10);
            p.r("armR", (-2.8f + 1.4f * ph) * s, 0, -.2f).r("legR", .2f * s, 0, 0).r("body", .1f * s, 0, 0);
        });
    }
    /** Green soul: spears converge from four sides on the target, one side at a time, then two at once. */
    static Attack spearConverge(String id, int col, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t == 2) c.particle(ParticleTypes.HAPPY_VILLAGER, c.tpos(), 20, .4, .1);
            if (t >= 10 && t < 74 && t % 8 == 2) {
                int i = (t - 10) / 8; int[] sides = i < 4 ? new int[]{i % 4} : new int[]{i % 4, (i + 2) % 4};
                for (int s : sides) {
                    double ang = s * Math.PI / 2 + Math.atan2(c.fwd().z, c.fwd().x); Vec3 d = new Vec3(Math.cos(ang), 0, Math.sin(ang));
                    Vec3 at = c.tpos().add(d.scale(8)); spear(c, at, d.scale(-.6), col, dmg, 6, 16);
                }
                c.sound(SoundEvents.TRIDENT_THROW, 1, 1.2f);
            }
        }, (p, t, m) -> { throwPose(p, Math.max(0, t - 2), 8); p.r("armL", -1.0f, 0, .4f); });
    }
    /** Spears fall from the sky in staggered volleys of n columns. */
    static Attack spearRain(String id, int col, float dmg, int n) {
        return a(id, 76, (c, t) -> {
            if (t >= 8 && t < 60 && t % 6 == 2) {
                for (int i = 0; i < n; i++) {
                    Vec3 at = feet(c).add(c.rnd.nextGaussian() * 2, 8 + i, c.rnd.nextGaussian() * 2);
                    spear(c, at, new Vec3(0, -.7, 0), col, dmg, 10, 20);
                }
                c.particle(ParticleTypes.ENCHANTED_HIT, feet(c).add(0, 7, 0), 10, 2, .02);
                c.sound(SoundEvents.ARROW_SHOOT, 1, 1.4f);
            }
        }, (p, t, m) -> { float s = env(t, 8, 60, 10); p.r("armR", -2.9f * s, 0, -.1f).r("armL", -2.9f * s, 0, .1f).r("armR", .3f * sin(t, 1.2f) * s, 0, 0); });
    }
    /** Leaps, brings an axe down overhead; shockwave ring travels outward. */
    static Attack axeCleave(String id, int col, float dmg) {
        return a(id, 70, (c, t) -> {
            if (t == 10) { Vec3 d = flat(c.aim(c.pos())); c.dash(d.scale(c.pos().distanceTo(c.tpos()) * .09).add(0, .7, 0)); c.sound(SoundEvents.RAVAGER_ROAR, .8f, 1.4f); }
            if (t == 24) {
                Vec3 p = c.pos(); c.aoe(p.add(c.fwd()), 2.6, dmg, 1.4); c.sound(SoundEvents.ANVIL_LAND, 1, .5f); c.particle(ParticleTypes.EXPLOSION, p, 3, .8, 0);
                for (int i = 0; i < 16; i++) { double a = i * Math.PI / 8; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                    c.proj().at(p.add(d).add(0, .3, 0)).vel(d.scale(.5)).box(.6f, .5f, .4f).color(col).dmg(dmg * .5f).life(16).spawn(); }
            }
        }, (p, t, m) -> {
            float up = env(t, 10, 18, 2), dn = t >= 18 ? env(t - 18, 4, 12, 10) : 0;
            p.r("armR", -3.0f * up - .6f * dn, 0, -.1f).r("armL", -3.0f * up - .6f * dn, 0, .1f).r("body", -.2f * up + .5f * dn, 0, 0)
             .r("axe", 0, 0, 0).r("legL", -.6f * up, 0, 0).r("legR", .4f * up, 0, 0);
        });
    }
    /** Spears fall where lightning strikes were telegraphed (electric captain). */
    static Attack thunderSpears(String id, int col, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t >= 6 && t < 60 && t % 9 == 6) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 1.4, 0, c.rnd.nextGaussian() * 1.4);
                c.proj().at(at.add(0, 4, 0)).box(.12f, 8f, .12f).color(col).dmg(0).warn(10).life(10).spawn();
                c.proj().at(at.add(0, 8, 0)).vel(new Vec3(0, -1.1, 0)).box(.25f, .25f, 2.4f).color(col).dmg(dmg).warn(10).life(18).spawn();
                c.particle(ParticleTypes.ELECTRIC_SPARK, at.add(0, .5, 0), 20, .5, .2);
                c.sound(SoundEvents.LIGHTNING_BOLT_IMPACT, .6f, 1.6f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 60, 10);
            p.r("armR", -3.0f * s, 0, -.2f).r("armL", -.8f * s, 0, .5f).o("armR", 0, .3f * sin(t, 2f) * s, 0).r("head", -.3f * s, 0, 0);
        });
    }
    /** Phase finale: everything at once - rings of spears, rain and a charge. */
    static Attack spearBarrage(String id, float gate, int col, float dmg) {
        return a(id, 130, gate, (c, t) -> {
            if (t < 60 && t % 10 == 0) {
                int n = 10; double off = t * .1;
                for (int i = 0; i < n; i++) { double a = off + i * Math.PI * 2 / n; Vec3 d = new Vec3(Math.cos(a), .15, Math.sin(a)); spear(c, c.tpos().add(d.scale(8)), d.scale(-.55), col, dmg, 10, 22); }
                c.sound(SoundEvents.TRIDENT_THROW, 1.2f, .9f);
            }
            if (t >= 60 && t < 96 && t % 3 == 0) spear(c, feet(c).add(c.rnd.nextGaussian() * 2.5, 9, c.rnd.nextGaussian() * 2.5), new Vec3(0, -.8, 0), col, dmg, 8, 20);
            if (t == 100) { c.dash(flat(c.aim(c.pos())).scale(1.8).add(0, .2, 0)); c.sound(SoundEvents.RAVAGER_ROAR, 1, 1.2f); }
            if (t > 100 && t < 110) c.aoe(c.pos().add(0, 1, 0), 2, dmg, 1.2);
        }, (p, t, m) -> {
            if (t < 60) throwPose(p, t, 10);
            else if (t < 96) p.r("armR", -3f, 0, -.2f).r("armL", -3f, 0, .2f).r("head", -.4f, 0, 0);
            else { float w = env(t - 96, 4, 14, 10); p.r("armR", -1.6f * w, 0, 0).r("body", .5f * w, 0, 0).r("legL", -.8f * w, 0, 0).r("legR", .6f * w, 0, 0); }
        });
    }

    // =====================================================================================================
    // TECH (scientists)
    // =====================================================================================================
    /** Drones fly out, hover around the target, then each fires a laser bolt. */
    static Attack drones(String id, int body, int laser, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t == 8) for (int i = 0; i < 6; i++) {
                double a = i * Math.PI / 3; Vec3 at = c.tpos().add(Math.cos(a) * 4, 2.5, Math.sin(a) * 4);
                c.proj().at(at).box(.6f, .3f, .6f).color(body).dmg(0).life(70).spawn();
                c.proj().at(at.add(0, -.25, 0)).box(.2f, .15f, .2f).color(laser).dmg(0).life(70).spawn();
                c.particle(ParticleTypes.ELECTRIC_SPARK, at, 6, .3, .05);
            }
            if (t >= 30 && t < 78 && t % 8 == 6) {
                int i = (t - 30) / 8; double a = i * Math.PI / 3; Vec3 at = c.tpos().add(Math.cos(a) * 4, 2.3, Math.sin(a) * 4);
                c.proj().at(at).vel(c.aim(at).scale(.8)).box(.12f, .12f, 1.4f).color(laser).dmg(dmg).warn(6).life(14).spawn();
                c.sound(SoundEvents.BEACON_DEACTIVATE, .6f, 2f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 80, 10);
            p.r("armR", -1.5f * s, 0, 0).r("armL", -1.2f * s, .5f * s, 0).o("armR", 0, 0, .3f * sin(t, 1.5f) * s).r("head", .3f * s, 0, 0);
        });
    }
    /** Hotland lasers: alternating blue/orange beams sweep across the target's position. */
    static Attack laserSweep(String id, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t >= 10 && t < 76 && t % 11 == 10) {
                Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x); int i = (t - 10) / 11; int s = i % 2 == 0 ? 1 : -1;
                Vec3 at = c.tpos().add(r.scale(s * 8));
                UtProjectile.B b = c.proj().at(at).vel(r.scale(-s * .5)).box(.25f, 3f, 12f).dmg(dmg).warn(8).life(36).pierce();
                (i % 3 == 2 ? b.orange() : b.blue()).spawn();
                c.sound(SoundEvents.BEACON_POWER_SELECT, .8f, 1.6f);
            }
        }, (p, t, m) -> { float s = env(t, 8, 78, 10); p.r("armR", -1.4f * s, 0, 0).r("armL", -1.4f * s, 0, 0).o("armR", 0, .2f * sin(t, 2f), 0).r("head", .25f * s, 0, 0); });
    }
    /** Bombs drop around the target, blink, then detonate in sequence. */
    static Attack bombs(String id, int body, int light, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t >= 8 && t < 40 && t % 8 == 0) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 2.5, .4, c.rnd.nextGaussian() * 2.5);
                c.proj().at(at.add(0, 6, 0)).vel(new Vec3(0, -.4, 0)).box(.7f, .7f, .7f).color(body).dmg(0).life(14).spawn();
                c.proj().at(at).box(.8f, .8f, .8f).color(light).dmg(0).warn(40).life(40).spawn();
                c.proj().at(at).box(4f, 3f, 4f).color(light).dmg(dmg).warn(40).life(44).pierce().spawn();
            }
            if (t >= 48 && t < 80 && t % 8 == 0) c.sound(SoundEvents.GENERIC_EXPLODE, .7f, 1.3f);
            if (t >= 48 && t < 80 && t % 8 == 0) c.particle(ParticleTypes.EXPLOSION, c.tpos(), 3, 2, 0);
        }, (p, t, m) -> {
            float ph = (t % 8) / 8f, s = env(t, 6, 40, 10);
            p.r("armR", (-2f + 1.4f * ph) * s, 0, 0).r("head", -.2f * s, 0, 0).r("armL", -.4f, 0, .3f);
        });
    }

    // =====================================================================================================
    // MUSIC / GHOSTS
    // =====================================================================================================
    /** Turntables pop out and fling vinyl discs that ricochet back. */
    static Attack discThrow(String id, int disc, int label, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t >= 10 && t < 60 && t % 10 == 0) {
                Vec3 f = flat(c.aim(c.pos())); int k = (t - 10) / 10;
                for (int s = -1; s <= 1; s += 2) {
                    Vec3 d = rotY(f, s * (.3 + .1 * k)), at = c.pos().add(0, 1.4, 0).add(d);
                    c.proj().at(at).vel(d.scale(.7)).box(1.2f, .1f, 1.2f).color(disc).dmg(dmg).warn(2).life(18).pierce().spawn();
                    c.proj().at(at).vel(d.scale(.7)).box(.4f, .14f, .4f).color(label).dmg(0).life(18).spawn();
                    Vec3 back = at.add(d.scale(12.6));
                    c.proj().at(back).vel(c.aim(back).scale(.6)).box(1.2f, .1f, 1.2f).color(disc).dmg(dmg).warn(18).life(36).spawn();
                }
                c.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1, 1.8f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 60, 10), ph = (t % 10) / 10f;
            p.r("armL", -1.5f * s, 0, (1.2f - ph) * s).r("armR", -1.5f * s, 0, -(1.2f - ph) * s).r("turnL", 0, t * .6f, 0).r("turnR", 0, -t * .6f, 0);
        });
    }
    /** Shockwave rings pulse outward on every beat; every fourth beat is a heavy drop. */
    static Attack beatRings(String id, int col, float dmg) {
        return a(id, 96, (c, t) -> {
            if (t >= 8 && t < 88 && t % 10 == 8) {
                int beat = (t - 8) / 10; boolean drop = beat % 4 == 3; int n = drop ? 24 : 14;
                for (int i = 0; i < n; i++) { double a = i * Math.PI * 2 / n + beat * .2; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                    c.proj().at(c.pos().add(d).add(0, drop ? .4 : .2, 0)).vel(d.scale(drop ? .6 : .45)).box(.5f, drop ? 1.2f : .5f, .3f).color(col).dmg(dmg).life(26).spawn(); }
                c.particle(ParticleTypes.NOTE, c.eye().add(0, .8, 0), 4, .6, 1);
                c.sound(drop ? SoundEvents.WARDEN_SONIC_BOOM : SoundEvents.NOTE_BLOCK_BASEDRUM.value(), drop ? .6f : 1.2f, drop ? 1.6f : 1f);
            }
        }, (p, t, m) -> {
            float b = env((t + 2) % 10, 2, 3, 5);
            p.o("body", 0, -.8f * b, 0).r("head", .4f * b, 0, 0).r("armR", -1.2f, 0, -.2f).r("armL", -1.2f, 0, .2f).r("turnL", 0, t * .5f, 0).r("turnR", 0, -t * .5f, 0);
        });
    }
    /** Chest speaker opens; one huge soundwave beam, then three narrow sweeps. */
    static Attack speakerBeam(String id, int col, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t == 8) { Vec3 at = c.pos().add(0, 1.6, 0); blaster(c, at, c.tpos().subtract(at), 22, 18, dmg, 1.6f, 0x2a2a30, col, col); }
            if (t == 50 || t == 60 || t == 70) {
                Vec3 at = c.pos().add(0, 1.6, 0), d = rotY(c.aim(at), (t - 60) * .03);
                blaster(c, at, d, 10, 8, dmg * .7f, .6f, 0x2a2a30, col, col);
            }
            if (t > 30 && t < 48) c.particle(ParticleTypes.NOTE, c.pos().add(0, 1.6, 0).add(c.aim(c.pos()).scale(3)), 2, .8, 1);
        }, (p, t, m) -> {
            float s = env(t, 8, 46, 8), r = env(t - 30, 4, 18, 6);
            p.r("armL", -.4f * s, 0, 1.4f * s).r("armR", -.4f * s, 0, -1.4f * s).r("body", -.25f * s - .1f * r, 0, 0).o("body", 0, 0, .8f * r * sin(t, 3f));
        });
    }
    /** Spotlight columns rotate around the caster, with stage-light telegraphs. */
    static Attack spotlights(String id, int col, float dmg) {
        return a(id, 90, (c, t) -> {
            if (t >= 10 && t < 76 && t % 4 == 2) {
                for (int i = 0; i < 3; i++) {
                    double a = t * .09 + i * Math.PI * 2 / 3; Vec3 at = c.pos().add(Math.cos(a) * 4.5, 2, Math.sin(a) * 4.5);
                    c.proj().at(at).box(1.2f, 5f, 1.2f).color(col).dmg(dmg).warn(4).life(6).spawn();
                }
                if (t % 12 == 2) c.particle(ParticleTypes.END_ROD, c.eye().add(0, 2, 0), 6, 1.5, .02);
            }
            if (t == 10) c.sound(SoundEvents.BEACON_ACTIVATE, 1, 1.5f);
        }, (p, t, m) -> { float s = env(t, 10, 76, 10); p.r("armR", -2.9f * s, 0, -.6f * s).r("armL", -.3f * s, 0, .8f * s).r("body", 0, t * .05f * s, 0).r("head", -.3f * s, 0, 0); });
    }
    /** Encore: discs, beats and a beam layered on a strobe, faster as the track builds. */
    static Attack remix(String id, float gate, int col, int col2, float dmg) {
        return a(id, 140, gate, (c, t) -> {
            if (t % 8 == 0 && t < 120) {
                int n = 12; for (int i = 0; i < n; i++) { double a = i * Math.PI * 2 / n + t * .07; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                    c.proj().at(c.pos().add(d).add(0, .4, 0)).vel(d.scale(.5)).box(.4f, .8f, .3f).color((t / 8) % 2 == 0 ? col : col2).dmg(dmg * .7f).life(24).spawn(); }
                c.sound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 1.2f, 1 + (t / 40) * .2f);
            }
            if (t >= 30 && t < 110 && t % 14 == 2) {
                Vec3 f = flat(c.aim(c.pos())), at = c.pos().add(0, 1.4, 0);
                c.proj().at(at).vel(rotY(f, c.rnd.nextGaussian() * .3).scale(.75)).box(1.2f, .1f, 1.2f).color(0x111111).dmg(dmg).warn(3).life(20).pierce().spawn();
            }
            if (t == 112) { Vec3 at = c.pos().add(0, 1.8, 0); blaster(c, at, c.tpos().subtract(at), 16, 14, dmg * 1.3f, 1.8f, 0x2a2a30, col, col2); }
            if (t % 5 == 0) c.particle(ParticleTypes.NOTE, c.eye().add(0, 1, 0), 3, 1, 1);
        }, (p, t, m) -> {
            float b = env((t + 2) % 8, 2, 3, 3);
            p.o("body", 0, -.6f * b + (t > 100 ? 1.4f : 0), 0).r("armL", -2.2f, 0, .8f * b).r("armR", -2.2f, 0, -.8f * b).r("head", .3f * b, t * .03f, 0)
             .r("turnL", 0, t * .9f, 0).r("turnR", 0, -t * .9f, 0);
        });
    }
    /** The ghost cries: tears drip from its eyes and arc down onto the target. */
    static Attack tears(String id, int col, float dmg) {
        return a(id, 70, (c, t) -> {
            if (t >= 10 && t < 58 && t % 3 == 0) {
                Vec3 at = c.eye().add(c.right().scale(t % 6 == 0 ? .25 : -.25)), d = c.aim(at);
                c.proj().at(at).vel(d.scale(.35).add(c.rnd.nextGaussian() * .05, .25, c.rnd.nextGaussian() * .05)).box(.2f, .3f, .2f).color(col).dmg(dmg).gravity(.03f).life(30).spawn();
                c.particle(ParticleTypes.FALLING_WATER, at, 2, .1, 0);
            }
            if (t % 9 == 0) c.particle(ParticleTypes.END_ROD, c.eye(), 2, .4, .01);
        }, (p, t, m) -> { float s = Math.min(1, t / 10f); p.r("head", .4f * s, 0, .1f * sin(t, .3f)).o("body", 0, .4f * sin(t, .2f), 0); });
    }
    /** Hearts float out slowly toward the target in a swaying line, then a burst of small ones. */
    static Attack hearts(String id, int col, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t >= 8 && t < 50 && t % 6 == 2) {
                Vec3 at = c.eye().add(c.right().scale(Math.sin(t * .4) * 1.5)); Vec3 v = c.aim(at).scale(.25);
                c.proj().at(at).vel(v).box(.55f, .5f, .3f).color(col).dmg(dmg).life(50).spawn();
                c.particle(ParticleTypes.HEART, at, 1, .1, 0);
            }
            if (t == 60) for (int i = 0; i < 8; i++) { double a = i * Math.PI / 4; Vec3 d = new Vec3(Math.cos(a), .1, Math.sin(a));
                c.proj().at(c.eye()).vel(d.scale(.35)).box(.3f, .3f, .2f).color(col).dmg(dmg * .6f).warn(2).life(30).spawn(); }
        }, (p, t, m) -> { float s = env(t, 8, 60, 10); p.r("head", -.25f * s, .3f * sin(t, .2f), 0).o("body", 0, .5f * s * sin(t, .15f), 0); });
    }

    // =====================================================================================================
    // GRILLBY / MUFFET / ASRIEL
    // =====================================================================================================
    /** Bottles lobbed in arcs; each shatters into a puddle of fire where it lands. */
    static Attack bottleToss(String id, int glass, int fire, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t >= 8 && t < 56 && t % 8 == 0) {
                Vec3 from = c.eye(), to = feet(c).add(c.rnd.nextGaussian(), 0, c.rnd.nextGaussian());
                double T = 20, g = .035; Vec3 d = to.subtract(from);
                c.proj().at(from).vel(new Vec3(d.x / T, (d.y + .5 * g * T * T) / T, d.z / T)).box(.25f, .5f, .25f).color(glass).dmg(dmg).gravity((float) g).life(20).spawn();
                c.proj().at(to.add(0, .2, 0)).box(2.2f, .4f, 2.2f).color(fire).dmg(dmg).warn(20).life(46).pierce().spawn();
                c.sound(SoundEvents.SPLASH_POTION_THROW, 1, .9f);
            }
            if (t >= 28 && t < 76 && t % 8 == 4) { c.sound(SoundEvents.GLASS_BREAK, .8f, 1.2f); c.particle(ParticleTypes.FLAME, c.tpos(), 8, 1.5, .02); }
        }, (p, t, m) -> throwPose(p, t, 8));
    }
    /** Pours a line of flame along the ground, sweeping a snake of fire toward the target. */
    static Attack flamePour(String id, int col, float dmg) {
        return a(id, 76, (c, t) -> {
            if (t >= 10 && t < 60) {
                Vec3 f = flat(c.aim(c.pos())); double sway = Math.sin(t * .25) * 2.5; Vec3 r = new Vec3(-f.z, 0, f.x);
                Vec3 at = c.pos().add(f.scale(1.5 + (t - 10) * .18)).add(r.scale(sway)).add(0, .3, 0);
                c.proj().at(at).box(.8f, .8f, .8f).color(col).dmg(dmg).warn(5).life(20).spawn();
                c.particle(ParticleTypes.FLAME, at, 3, .2, .02);
                if (t % 10 == 0) c.sound(SoundEvents.FIRE_AMBIENT, 1, 1);
            }
        }, (p, t, m) -> { float s = env(t, 10, 60, 8); p.r("armR", -1.2f * s, .4f * sin(t, .25f) * s, 0).r("body", .2f * s, 0, 0).r("head", .2f * s, 0, 0); });
    }
    /** Shot glasses rain down and shatter into shards. */
    static Attack glassRain(String id, int col, float dmg) {
        return a(id, 70, (c, t) -> {
            if (t >= 6 && t < 54 && t % 3 == 0) {
                Vec3 at = feet(c).add(c.rnd.nextGaussian() * 2.6, 7, c.rnd.nextGaussian() * 2.6);
                c.proj().at(at).vel(new Vec3(0, -.6, 0)).box(.35f, .45f, .35f).color(col).dmg(dmg).warn(8).life(16).spawn();
            }
            if (t % 9 == 0 && t > 20) c.sound(SoundEvents.GLASS_BREAK, .6f, 1.5f);
        }, (p, t, m) -> castPose(p, t, 6, 54, 10));
    }
    /** His pet - a great fire hound - charges across the field twice, telegraphed by a scorch line. */
    static Attack firePet(String id, int col, float dmg) {
        return a(id, 100, (c, t) -> {
            if (t == 10 || t == 55) {
                Vec3 f = flat(c.aim(c.pos())), r = new Vec3(-f.z, 0, f.x); int s = t == 10 ? 1 : -1;
                Vec3 start = c.tpos().add(r.scale(s * 12)).add(0, .4, 0);
                for (int k = -6; k <= 6; k++) c.proj().at(c.tpos().add(r.scale(k * 2)).add(0, -.8, 0)).box(1.6f, .1f, 1.6f).color(col).dmg(0).warn(18).life(18).spawn();
                c.proj().at(start).vel(r.scale(-s * .8)).box(2.4f, 2.2f, 3.6f).color(col).dmg(dmg).warn(18).life(50).pierce().spawn();
                c.proj().at(start.add(0, 1.4, 0)).vel(r.scale(-s * .8)).box(1.4f, 1f, 1.4f).color(0xffd040).dmg(0).warn(18).life(50).spawn();
                c.sound(SoundEvents.ENDER_DRAGON_GROWL, .6f, 1.8f);
            }
            if ((t > 28 && t < 50) || (t > 73 && t < 95)) c.particle(ParticleTypes.FLAME, c.tpos(), 6, 3, .05);
        }, (p, t, m) -> { float s = Math.max(env(t, 8, 16, 8), env(t - 45, 8, 16, 8)); p.r("armR", -2.4f * s, 0, -.5f * s).r("armL", -.8f * s, 0, .5f * s).r("head", -.2f * s, 0, 0); });
    }
    /** Skates in a circle around the target flinging serving trays like discs. */
    static Attack traySpin(String id, int col, float dmg) {
        return a(id, 80, (c, t) -> {
            if (t >= 6 && t < 66) {
                double a = t * .16; Vec3 ctr = c.target.position(); Vec3 p = ctr.add(Math.cos(a) * 5, 0, Math.sin(a) * 5);
                c.dash(p.subtract(c.pos()).multiply(.4, 0, .4));
                if (t % 7 == 0) { Vec3 at = c.pos().add(0, 1.1, 0); c.proj().at(at).vel(c.aim(at).scale(.6)).box(.9f, .08f, .9f).color(col).dmg(dmg).warn(3).life(24).spawn(); c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, .6f, 1.6f); }
                if (t % 3 == 0) c.particle(ParticleTypes.CRIT, c.pos(), 2, .2, .01);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 66, 10);
            p.r("body", .25f * s, 0, -.2f * s).r("legL", -.5f * s * sin(t, .5f), 0, 0).r("legR", .5f * s * sin(t, .5f), 0, 0).r("armR", -1.6f * s, 0, -.6f * s).r("armUL", 0, 0, -.6f * s).r("armUR", 0, 0, .6f * s);
        });
    }
    /** Splashes a fan of tea droplets toward the target from the teapot. */
    static Attack teaSplash(String id, int col, float dmg) {
        return a(id, 60, (c, t) -> {
            if (t == 14 || t == 32) {
                Vec3 at = c.eye(), f = c.aim(at);
                for (int i = -4; i <= 4; i++) c.proj().at(at).vel(rotY(f, i * .12).scale(.5).add(0, .12, 0)).box(.25f, .25f, .25f).color(col).dmg(dmg).gravity(.02f).life(30).spawn();
                c.particle(ParticleTypes.SPLASH, at, 12, .3, .1); c.sound(SoundEvents.BUCKET_EMPTY, 1, 1.4f);
            }
        }, (p, t, m) -> { float s = Math.max(env(t, 10, 14, 6), env(t - 18, 10, 14, 6)); p.r("armR", -1.4f * s, 0, -.3f * s).r("body", .3f * s, 0, 0).r("armUR", -.8f * s, 0, 0); });
    }
    /** Asriel: tosses little sparkling stars (a Star Blazing in miniature). */
    static Attack starToss(String id, int col, float dmg) {
        return a(id, 50, (c, t) -> {
            if (t == 10 || t == 22 || t == 34) {
                Vec3 at = c.eye(), f = c.aim(at);
                for (int i = -1; i <= 1; i++) c.proj().at(at).vel(rotY(f, i * .25).scale(.5)).box(.35f, .35f, .35f).color(col).dmg(dmg).life(24).spawn();
                c.particle(ParticleTypes.FIREWORK, at, 6, .2, .05); c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, 1.8f);
            }
        }, (p, t, m) -> throwPose(p, t, 12));
    }

    // =====================================================================================================
    // MODELS
    // =====================================================================================================
    /** Short skeleton (Sans types). fell: spiked pauldrons + scar + tattered scarf. */
    static ModelDef skeletonShort(int armor, int scarf, int legs, int boots, int eye, boolean fell) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 8, 0, 8, 7, 5, armor)
            .box("belt", "body", 0, -3, 0, 8.4f, 1, 5.4f, scarf)
            .box("chest", "body", 0, .5f, -2.6f, 5, 3, .4f, fell ? 0x2a0a2a : 0xf0f0f8)
            .box("head", "body", 0, 9.5f, 0, 9, 7, 7, 0xf5f5f5)
            .glow("eyeL", "head", -2, .5f, -3.6f, 1.6f, 1.6f, .4f, eye)
            .glow("eyeR", "head", 2, .5f, -3.6f, 1.6f, 1.6f, .4f, fell ? 0x111111 : eye)
            .box("grin", "head", 0, -2, -3.6f, 5, .7f, .4f, 0x222222)
            .box(fell ? "scarf" : "bandana", "body", 0, 3.6f, -.2f, 9.4f, 1.6f, 6, scarf)
            .box(fell ? "scarfTail" : "bandanaTail", "body", 1.8f, 2.2f, 3.4f, 2, fell ? 6 : 4, .6f, scarf)
            .limb("armL", "body", -5, 3, 0, 2.4f, 6, 2.4f, armor)
            .limb("armR", "body", 5, 3, 0, 2.4f, 6, 2.4f, armor)
            .box("gloveL", "armL", 0, -5.5f, 0, 2.9f, 2, 2.9f, scarf)
            .box("gloveR", "armR", 0, -5.5f, 0, 2.9f, 2, 2.9f, scarf)
            .limb("legL", null, -2, 5, 0, 3, 5, 3.5f, legs)
            .limb("legR", null, 2, 5, 0, 3, 5, 3.5f, legs)
            .box("bootL", "legL", 0, -4.3f, -.4f, 3.5f, 1.8f, 4.3f, boots)
            .box("bootR", "legR", 0, -4.3f, -.4f, 3.5f, 1.8f, 4.3f, boots);
        if (fell) m.box("scar", "head", 2, 1.2f, -3.65f, .5f, 3.5f, .3f, 0x6a0a0a)
                   .box("spikeL", "armL", 0, .8f, 0, 3.4f, 1.6f, 3.4f, 0x3a2a40)
                   .box("spikeR", "armR", 0, .8f, 0, 3.4f, 1.6f, 3.4f, 0x3a2a40)
                   .box("teethL", "head", -1.5f, -2.6f, -3.65f, .6f, .8f, .3f, 0xeeeeee)
                   .box("teethR", "head", 1.5f, -2.6f, -3.65f, .6f, .8f, .3f, 0xeeeeee);
        else m.glow("starL", "eyeL", 0, 0, -.25f, .5f, .5f, .2f, 0xffffff).glow("starR", "eyeR", 0, 0, -.25f, .5f, .5f, .2f, 0xffffff);
        return m;
    }
    /** Tall skeleton (Papyrus types): hoodie + cigarette; fell adds spiked collar and gold tooth. */
    static ModelDef skeletonTall(int hoodie, int trim, int legs, int shoes, int eye, int tooth, boolean fell) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 14, 0, 9, 9, 5, hoodie)
            .box("hood", "body", 0, 5, 1.6f, 10, 2, 5, trim)
            .box("pocket", "body", 0, -2.5f, -2.6f, 6, 2.4f, .3f, trim)
            .box("head", "body", 0, 9, 0, 8, 8, 7, 0xf5f5f5)
            .glow("eyeL", "head", -2, .5f, -3.6f, .8f, .8f, .4f, eye)
            .glow("eyeR", "head", 2, .5f, -3.6f, .8f, .8f, .4f, eye)
            .box("sockL", "head", -2, .5f, -3.55f, 2, 2, .3f, 0x111111)
            .box("sockR", "head", 2, .5f, -3.55f, 2, 2, .3f, 0x111111)
            .box("grin", "head", 0, -2.4f, -3.6f, 5, .7f, .4f, 0x222222)
            .box("cig", "head", 2.2f, -2.4f, -4.4f, .5f, .5f, 2.2f, 0xeeeeee)
            .glow("ember", "cig", 0, 0, -1.2f, .6f, .6f, .4f, 0xff5a1f)
            .limb("armL", "body", -6, 3.5f, 0, 2.6f, 9, 2.6f, hoodie)
            .limb("armR", "body", 6, 3.5f, 0, 2.6f, 9, 2.6f, hoodie)
            .box("handL", "armL", 0, -8.5f, 0, 2.2f, 2, 2.2f, 0xf5f5f5)
            .box("handR", "armR", 0, -8.5f, 0, 2.2f, 2, 2.2f, 0xf5f5f5)
            .limb("legL", null, -2.2f, 9.5f, 0, 3, 9, 3, legs)
            .limb("legR", null, 2.2f, 9.5f, 0, 3, 9, 3, legs)
            .box("shoeL", "legL", 0, -8.6f, -.5f, 3.4f, 1.6f, 4.4f, shoes)
            .box("shoeR", "legR", 0, -8.6f, -.5f, 3.4f, 1.6f, 4.4f, shoes);
        if (fell) m.box("collar", "body", 0, 4.6f, 0, 6, 1.4f, 4.6f, 0x1a1a1a)
                   .box("spike1", "collar", -2, 0, -2.6f, .6f, .6f, .8f, 0xc0c0c0).box("spike2", "collar", 2, 0, -2.6f, .6f, .6f, .8f, 0xc0c0c0)
                   .glow("tooth", "head", 1.2f, -2.4f, -3.7f, .8f, .8f, .3f, tooth)
                   .box("fur", "hood", 0, .6f, 0, 10.6f, 1.2f, 5.6f, trim);
        return m;
    }
    /** Boss monster goat. robe body; trident/cape/crown optional. */
    static ModelDef goat(int robe, int legs, int fur, int beard, boolean rune, int accent, boolean queen, int trident, int cape) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 13, 0, 12, 12, 8, robe)
            .box("hem", "body", 0, -5.5f, 0, 13, 2, 9, accent == 0 ? robe : accent)
            .box("head", "body", 0, 10, 0, 9, 8, 8, fur)
            .box("snout", "head", 0, -1.5f, -4.6f, 5, 3.5f, 2, fur)
            .box("nose", "snout", 0, .8f, -1.1f, 1.5f, .8f, .4f, 0x5a3a3a)
            .box("eyeL", "head", -2.2f, 1, -4.05f, 1.4f, 1, .3f, 0x222222)
            .box("eyeR", "head", 2.2f, 1, -4.05f, 1.4f, 1, .3f, 0x222222)
            .limb("armL", "body", -7.5f, 5, 0, 3.5f, 10, 3.5f, robe)
            .limb("armR", "body", 7.5f, 5, 0, 3.5f, 10, 3.5f, robe)
            .box("pawL", "armL", 0, -9.5f, 0, 3, 2.5f, 3, fur)
            .box("pawR", "armR", 0, -9.5f, 0, 3, 2.5f, 3, fur)
            .limb("legL", null, -2.8f, 7, 0, 4, 7, 4, legs)
            .limb("legR", null, 2.8f, 7, 0, 4, 7, 4, legs);
        if (queen) m.limb("earL", "head", -4.8f, 2.5f, .5f, 1.6f, 6, 2.5f, fur).limb("earR", "head", 4.8f, 2.5f, .5f, 1.6f, 6, 2.5f, fur)
                    .glow("crown", "head", 0, 4.8f, 0, 6, 1.6f, 6, accent)
                    .box("cape", "body", 0, 4, 4.6f, 13, 16, .8f, cape)
                    .box("trident", "armR", 0, -9.5f, -1.5f, .8f, 20, .8f, trident)
                    .box("prongs", "trident", 0, 10, 0, 4.5f, .9f, .8f, trident)
                    .box("tineL", "prongs", -2, 1.5f, 0, .7f, 3, .7f, trident).box("tineM", "prongs", 0, 2, 0, .7f, 4, .7f, trident)
                    .box("tineR", "prongs", 2, 1.5f, 0, .7f, 3, .7f, trident);
        else m.box("hornL", "head", -4.2f, 4.5f, 0, 1.5f, 4, 1.5f, 0xe9e2c8).box("hornR", "head", 4.2f, 4.5f, 0, 1.5f, 4, 1.5f, 0xe9e2c8)
              .box("mane", "head", 0, 3.6f, .8f, 9.6f, 2, 8.6f, beard)
              .box("beard", "head", 0, -4.6f, -3.4f, 6, 3, 2, beard);
        if (rune) m.glow("rune", "body", 0, 1, -4.1f, 4, 4, .3f, accent);
        return m;
    }
    /** Alphys types: lizard in Royal Guard armour with an axe. */
    static ModelDef lizard(int skin, int armor, int trim, int eye, boolean fell, boolean axe, int glowCol) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 10, 0, 10, 9, 7, armor)
            .box("plate", "body", 0, 1, -3.6f, 7, 5, .4f, trim)
            .box("head", "body", 0, 8, 0, 8, 7, 7, skin)
            .box("snout", "head", 0, -1.4f, -4.4f, 6, 3, 2.2f, skin)
            .box("eyeL", "head", -2.2f, 1.2f, -3.6f, 1.4f, 1.4f, .3f, eye)
            .box("eyeR", "head", 2.2f, 1.2f, -3.6f, 1.4f, 1.4f, .3f, eye)
            .box("helmet", "head", 0, 3.4f, 0, 8.6f, 1.6f, 7.6f, trim)
            .limb("armL", "body", -6.3f, 3.5f, 0, 3, 8, 3, armor)
            .limb("armR", "body", 6.3f, 3.5f, 0, 3, 8, 3, armor)
            .box("clawL", "armL", 0, -7.6f, 0, 2.6f, 1.6f, 2.6f, skin)
            .box("clawR", "armR", 0, -7.6f, 0, 2.6f, 1.6f, 2.6f, skin)
            .limb("legL", null, -2.4f, 6, 0, 3.6f, 6, 3.6f, armor)
            .limb("legR", null, 2.4f, 6, 0, 3.6f, 6, 3.6f, armor)
            .box("tail", "body", 0, -4, 5, 3, 3, 6, skin);
        if (axe) m.box("axe", "armR", 0, -8.5f, -3, .8f, .8f, 9, 0x5a3a20).box("axeHead", "axe", 0, 0, -4.5f, .5f, 4, 3, fell ? 0x6a6a70 : 0xb0b8c8)
                  .glow("edge", "axeHead", 0, 0, -1.6f, .6f, 4.2f, .3f, glowCol);
        if (fell) m.box("spikeL", "armL", 0, .8f, 0, 4, 1.8f, 4, 0x3a0a0a).box("spikeR", "armR", 0, .8f, 0, 4, 1.8f, 4, 0x3a0a0a).box("scar", "snout", 1.4f, .5f, -1.15f, .4f, 2.2f, .2f, 0x6a1010);
        return m;
    }
    /** Undyne types: fish scientist with lab coat and glasses. */
    static ModelDef fish(int skin, int hair, int coat, int pants, int lens, boolean fell) {
        ModelDef m = new ModelDef()
            .box("body", null, 0, 13, 0, 8, 9, 5, coat)
            .box("shirt", "body", 0, 1, -2.6f, 3, 6, .3f, pants)
            .box("head", "body", 0, 8.5f, 0, 7, 7, 6, skin)
            .box("hair", "head", 0, 3.6f, .6f, 7.6f, 2, 6.6f, hair)
            .limb("ponytail", "hair", 0, .6f, 3.4f, 2.4f, 6, 2.4f, hair)
            .box("finL", "head", -4, .5f, 0, 1, 3, 3, skin)
            .box("finR", "head", 4, .5f, 0, 1, 3, 3, skin)
            .box("glasses", "head", 0, .8f, -3.1f, 6, .5f, .3f, 0x222222)
            .glow("lensL", "glasses", -1.6f, -.6f, -.05f, 2, 1.5f, .2f, lens)
            .glow("lensR", "glasses", 1.6f, -.6f, -.05f, 2, 1.5f, .2f, lens)
            .box("teeth", "head", 0, -2.2f, -3.1f, 4, .8f, .3f, 0xf8f8f8)
            .limb("armL", "body", -5.2f, 3.5f, 0, 2.4f, 8.5f, 2.4f, coat)
            .limb("armR", "body", 5.2f, 3.5f, 0, 2.4f, 8.5f, 2.4f, coat)
            .limb("legL", null, -2, 8.5f, 0, 3, 8.5f, 3, pants)
            .limb("legR", null, 2, 8.5f, 0, 3, 8.5f, 3, pants)
            .box("coatTail", "body", 0, -6, 0, 8.4f, 4, 5.4f, coat);
        if (fell) m.box("eyepatch", "head", -1.6f, 1.2f, -3.2f, 2.2f, 2, .3f, 0x111111).box("stain", "coatTail", 1.5f, 0, -2.75f, 2, 2, .2f, 0x7a0a0a);
        return m;
    }
    /** Napstaton: boxy DJ robot with headphones and turntable arms. */
    static ModelDef robot(int body, int limbs, int glow1, int glow2, boolean fell) {
        return new ModelDef()
            .box("body", null, 0, 14, 0, 10, 10, 6, body)
            .glow("speaker", "body", 0, 0, -3.1f, 5, 5, .3f, glow2)
            .glow("cone", "speaker", 0, 0, -.2f, 2, 2, .2f, glow1)
            .box("head", "body", 0, 8.5f, 0, 8, 7, 7, limbs)
            .glow("visor", "head", 0, .8f, -3.6f, 7, 2, .3f, glow1)
            .box("phones", "head", 0, 4, 0, 9, 1, 2, fell ? 0x1a1a1a : 0xeeeeee)
            .glow("cupL", "phones", -4.8f, -3, 0, 1.4f, 3, 3, glow1)
            .glow("cupR", "phones", 4.8f, -3, 0, 1.4f, 3, 3, glow1)
            .box("hair", "head", -1.5f, 3, -1, 6, 2, 6, fell ? 0x3a0a0a : 0x1a1a1a)
            .limb("armL", "body", -6.5f, 4, 0, 3, 10, 3, limbs)
            .limb("armR", "body", 6.5f, 4, 0, 3, 10, 3, limbs)
            .box("turnL", "armL", -1.2f, -6, 0, .4f, 4.5f, 4.5f, 0x111111)
            .glow("labelL", "turnL", -.25f, 0, 0, .1f, 1.5f, 1.5f, glow2)
            .box("turnR", "armR", 1.2f, -6, 0, .4f, 4.5f, 4.5f, 0x111111)
            .glow("labelR", "turnR", .25f, 0, 0, .1f, 1.5f, 1.5f, glow2)
            .limb("legL", null, -2.6f, 9, 0, 3.4f, 9, 3.4f, limbs)
            .limb("legR", null, 2.6f, 9, 0, 3.4f, 9, 3.4f, limbs)
            .box("bootL", "legL", 0, -8.4f, -.5f, 3.8f, 1.6f, 4.6f, fell ? glow1 : 0xffffff)
            .box("bootR", "legR", 0, -8.4f, -.5f, 3.8f, 1.6f, 4.6f, fell ? glow1 : 0xffffff);
    }
    /** Mettaton's ghost form: translucent sheet with his swept hair. */
    static ModelDef ghost(int sheet, int eye, int mouth) {
        return new ModelDef()
            .ghost("body", null, 0, 9, 0, 10, 14, 8, sheet, .6f)
            .ghost("head", "body", 0, 7, 0, 10, 2, 8, sheet, .6f)
            .box("eyeL", "body", -2.2f, 3.5f, -4.05f, 1.8f, 1.2f, .3f, eye)
            .box("eyeR", "body", 2.2f, 3.5f, -4.05f, 1.8f, 1.2f, .3f, eye)
            .box("mouth", "body", 0, .8f, -4.05f, 2, .6f, .3f, mouth)
            .box("hair", "head", -2, 1.2f, -1, 7, 2.4f, 7, 0x1a1a1a)
            .box("bang", "hair", -3, -2, -3.4f, 2, 4, 1, 0x1a1a1a)
            .ghost("tailL", "body", -3, -7.5f, 0, 3, 2, 7, sheet, .5f)
            .ghost("tailR", "body", 3, -7.5f, 0, 3, 2, 7, sheet, .5f);
    }
    /** Grillby: living flame in a bartender's vest and glasses. */
    static ModelDef flameMan(int flame, int core, int vest, int shirt) {
        return new ModelDef()
            .box("body", null, 0, 13, 0, 8, 9, 5, shirt)
            .box("vest", "body", 0, -.5f, 0, 8.4f, 7.6f, 5.4f, vest)
            .box("bowtie", "body", 0, 3.6f, -2.8f, 2.4f, 1, .3f, vest)
            .glow("head", "body", 0, 8.5f, 0, 7, 8, 6, flame)
            .glow("core", "head", 0, -.5f, 0, 4.5f, 5, 4, core)
            .glow("flick1", "head", -1.5f, 5, 0, 2.5f, 3, 2.5f, flame)
            .glow("flick2", "head", 1.6f, 6, .5f, 2, 3.5f, 2, core)
            .box("glasses", "head", 0, .6f, -3.15f, 6, 1.5f, .3f, 0xeeeeee)
            .limb("armL", "body", -5.2f, 3.5f, 0, 2.4f, 8.5f, 2.4f, shirt)
            .limb("armR", "body", 5.2f, 3.5f, 0, 2.4f, 8.5f, 2.4f, shirt)
            .glow("handL", "armL", 0, -8, 0, 2, 2, 2, flame)
            .glow("handR", "armR", 0, -8, 0, 2, 2, 2, flame)
            .limb("legL", null, -2, 8.5f, 0, 3, 8.5f, 3, vest)
            .limb("legR", null, 2, 8.5f, 0, 3, 8.5f, 3, vest);
    }
    /** Muffet: six-armed spider maid on roller skates. */
    static ModelDef spider(int skin, int dress, int apron, int eye, boolean fell) {
        return new ModelDef()
            .box("body", null, 0, 11, 0, 8, 8, 5, dress)
            .box("apron", "body", 0, -1, -2.6f, 6, 7, .3f, apron)
            .box("skirt", "body", 0, -4.5f, 0, 10, 3, 7, dress)
            .box("head", "body", 0, 8, 0, 7, 7, 6, skin)
            .glow("eye1", "head", -2, 1.5f, -3.1f, 1, 1, .2f, eye).glow("eye2", "head", 2, 1.5f, -3.1f, 1, 1, .2f, eye)
            .glow("eye3", "head", -1, .2f, -3.1f, .8f, .8f, .2f, eye).glow("eye4", "head", 1, .2f, -3.1f, .8f, .8f, .2f, eye)
            .glow("eye5", "head", 0, 2.6f, -3.1f, .8f, .8f, .2f, eye)
            .box("pigtailL", "head", -4.2f, 1, 1, 2, 4, 2, 0x1a0a2a).box("pigtailR", "head", 4.2f, 1, 1, 2, 4, 2, 0x1a0a2a)
            .box("bow", "head", 0, 4, 0, 4, 1.2f, 2, fell ? 0x8a0a0a : 0xff6fb0)
            .limb("armL", "body", -5, 3, 0, 2, 7, 2, skin).limb("armR", "body", 5, 3, 0, 2, 7, 2, skin)
            .limb("armUL", "body", -5, 1, 0, 1.8f, 6, 1.8f, skin).limb("armUR", "body", 5, 1, 0, 1.8f, 6, 1.8f, skin)
            .limb("armLL", "body", -5, -1, 0, 1.6f, 5, 1.6f, skin).limb("armLR", "body", 5, -1, 0, 1.6f, 5, 1.6f, skin)
            .box("tray", "armR", 0, -7, -1.5f, 4, .3f, 4, 0xd8d8e0)
            .limb("legL", null, -1.8f, 7, 0, 2.4f, 6, 2.4f, skin).limb("legR", null, 1.8f, 7, 0, 2.4f, 6, 2.4f, skin)
            .box("skateL", "legL", 0, -6, -.4f, 3, 1.5f, 4.4f, fell ? 0x1a1a1a : 0xff6fb0)
            .box("skateR", "legR", 0, -6, -.4f, 3, 1.5f, 4.4f, fell ? 0x1a1a1a : 0xff6fb0)
            .box("bladeL", "skateL", 0, -1, 0, .4f, .6f, 4, fell ? 0xc0c0c0 : 0x333333)
            .box("bladeR", "skateR", 0, -1, 0, .4f, .6f, 4, fell ? 0xc0c0c0 : 0x333333);
    }
    /** Child Asriel in a green striped sweater. */
    static ModelDef kidGoat() {
        return new ModelDef()
            .box("body", null, 0, 8, 0, 7, 6, 4, 0x3fae5a)
            .box("stripe", "body", 0, 0, 0, 7.2f, 1.2f, 4.2f, 0xf0d040)
            .box("head", "body", 0, 7.5f, 0, 7, 7, 6, 0xf2f2ee)
            .box("snout", "head", 0, -1.5f, -3.4f, 4, 2.5f, 1.4f, 0xf2f2ee)
            .box("eyeL", "head", -1.7f, .8f, -3.05f, 1, 1.2f, .2f, 0x3a6a3a)
            .box("eyeR", "head", 1.7f, .8f, -3.05f, 1, 1.2f, .2f, 0x3a6a3a)
            .limb("earL", "head", -4, 2.3f, .5f, 1.4f, 4.5f, 2, 0xf2f2ee)
            .limb("earR", "head", 4, 2.3f, .5f, 1.4f, 4.5f, 2, 0xf2f2ee)
            .limb("armL", "body", -4.5f, 2.5f, 0, 2, 5.5f, 2, 0x3fae5a)
            .limb("armR", "body", 4.5f, 2.5f, 0, 2, 5.5f, 2, 0x3fae5a)
            .limb("legL", null, -1.7f, 5, 0, 2.6f, 5, 2.6f, 0x5a4a3a)
            .limb("legR", null, 1.7f, 5, 0, 2.6f, 5, 2.6f, 0x5a4a3a);
    }

    private AuUnderswapSwapfell() {}
}
