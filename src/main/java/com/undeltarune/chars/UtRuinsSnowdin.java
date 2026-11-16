package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/** Undertale: Ruins + Snowdin. Attack lists follow the Undertale wiki (one Attack = one enemy turn). */
public final class UtRuinsSnowdin {
    public static void register(List<CharacterDef> out) {
        out.add(CharacterDef.of("sans", "sans", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(120).boss().keep(7).rest(14).speed(.3f)
            .egg(0x2a6fd6, 0xffffff).size(.7f, 1.1f).scale(.9f)
            .model(new ModelDef()
                .box("body", null, 0, 8, 0, 8, 7, 5, 0x3a6fe0)
                .box("head", "body", 0, 9.5f, 0, 9, 7, 7, 0xf5f5f5)
                .glow("eyeL", "head", -2, .5f, -3.6f, 1.6f, 1.6f, .4f, 0x000000)
                .glow("eyeR", "head", 2, .5f, -3.6f, 1.6f, 1.6f, .4f, 0x000000)
                .glow("eyeBlue", "head", -2, .5f, -2.2f, 1.2f, 1.2f, .4f, 0x35e4ff) // hidden in the skull; posed forward when his eye flashes
                .box("grin", "head", 0, -2, -3.6f, 5, .7f, .4f, 0x222222)
                .box("hood", "body", 0, 3.4f, 0, 9, 1.5f, 6, 0xffffff)
                .limb("armL", "body", -5, 3, 0, 2.4f, 6, 2.4f, 0x3a6fe0)
                .limb("armR", "body", 5, 3, 0, 2.4f, 6, 2.4f, 0x3a6fe0)
                .limb("legL", null, -2, 5, 0, 3, 5, 3.5f, 0x1a1a1a)
                .limb("legR", null, 2, 5, 0, 3, 5, 3.5f, 0x1a1a1a))
            .attacks(sansBadTime(), sansBoneLines(), sansBlueBones(), sansRandomLines(), sansPlatforms1(), sansPlatforms2(),
                sansPlatforms3(), sansPlatforms4(), sansSlides(), sansPlatformBlasters(), boneSweep(), blasterRing(), gravitySlam(), boneGaps(),
                sansFalseMercy(), sansMenuBones(), sansFinalAttack(), sansSpecialAttack()).build());

        // ---------------- Ruins ----------------
        out.add(CharacterDef.of("flowey", "Flowey", Faction.FLOWEY_CHAOS).hp(40).keep(6).rest(24).speed(.2f)
            .egg(0xffd800, 0x2e9e2e).size(.5f, .9f).scale(.8f)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 1.5f, 8, 1.5f, 0x2e9e2e)
                .box("leafL", "body", -2.5f, -2, 0, 4, 1, 2, 0x37b337).box("leafR", "body", 2.5f, -1, 0, 4, 1, 2, 0x37b337)
                .box("head", "body", 0, 7, 0, 6, 6, 2, 0xfafafa)
                .glow("eyeL", "head", -1.3f, .8f, -1.1f, 1, 1.8f, .3f, 0x111111).glow("eyeR", "head", 1.3f, .8f, -1.1f, 1, 1.8f, .3f, 0x111111)
                .box("mouth", "head", 0, -1.5f, -1.1f, 2.6f, .9f, .3f, 0x222222)
                .box("petalT", "head", 0, 4, .4f, 4, 3, 1, 0xffd800).box("petalL", "head", -4, 1, .4f, 3, 4, 1, 0xffd800)
                .box("petalR", "head", 4, 1, .4f, 3, 4, 1, 0xffd800).box("petalBL", "head", -3, -3.2f, .4f, 3, 3, 1, 0xffd800)
                .box("petalBR", "head", 3, -3.2f, .4f, 3, 3, 1, 0xffd800))
            .attacks(floweyPellets(), floweyFiveSpread(), floweySideRows(), floweyRing()).build());

        out.add(CharacterDef.of("toriel", "Toriel", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(160).boss().keep(7).rest(20).speed(.25f)
            .egg(0x6b3fa0, 0xffffff).size(.8f, 2.1f).scale(1.1f)
            .model(new ModelDef()
                .box("body", null, 0, 12, 0, 9, 12, 6, 0x6b3fa0)
                .glow("rune", "body", 0, 2.5f, -3.1f, 3.5f, 3, .3f, 0xf3e8ff)
                .box("head", "body", 0, 10.5f, 0, 8, 8, 7, 0xfafafa)
                .box("snout", "head", 0, -1.6f, -4.2f, 4, 3, 2, 0xfafafa)
                .glow("eyeL", "head", -1.9f, 1, -3.6f, 1.3f, 1, .3f, 0xb82020).glow("eyeR", "head", 1.9f, 1, -3.6f, 1.3f, 1, .3f, 0xb82020)
                .box("earL", "head", -4.8f, -.5f, 0, 1.5f, 5, 3, 0xf0f0f0).box("earR", "head", 4.8f, -.5f, 0, 1.5f, 5, 3, 0xf0f0f0)
                .box("hornL", "head", -2.5f, 4.6f, 0, 1, 2, 1, 0xeeeedd).box("hornR", "head", 2.5f, 4.6f, 0, 1, 2, 1, 0xeeeedd)
                .limb("armL", "body", -5.8f, 5, 0, 3, 9, 3, 0x6b3fa0).limb("armR", "body", 5.8f, 5, 0, 3, 9, 3, 0x6b3fa0)
                .box("pawL", "armL", 0, -9.5f, 0, 3, 2, 3, 0xfafafa).box("pawR", "armR", 0, -9.5f, 0, 3, 2, 3, 0xfafafa)
                .glow("fireL", "pawL", 0, 0, 0, 1.5f, 1.5f, 1.5f, 0xff6a1f).glow("fireR", "pawR", 0, 0, 0, 1.5f, 1.5f, 1.5f, 0xff6a1f)
                .limb("legL", null, -2, 6, 0, 3, 6, 3, 0x5a3390).limb("legR", null, 2, 6, 0, 3, 6, 3, 0x5a3390))
            .attacks(torielPawSweep(), torielFireWaves(), torielFireHelix(), torielFaltering()).build());

        out.add(CharacterDef.of("froggit", "Froggit", Faction.UNDERGROUND).hp(20).keep(5).rest(30).random()
            .egg(0xf0f0f0, 0x8fbf6f).size(.6f, .7f).scale(.7f)
            .model(new ModelDef()
                .box("body", null, 0, 4, 0, 9, 5, 7, 0xf0f0f0)
                .box("head", "body", 0, 4.5f, -1, 8, 4, 6, 0xf4f4f4)
                .glow("eyeL", "head", -2, .8f, -3.1f, 1.2f, 1.2f, .3f, 0x111111).glow("eyeR", "head", 2, .8f, -3.1f, 1.2f, 1.2f, .3f, 0x111111)
                .box("mouth", "head", 0, -1, -3.1f, 3, .5f, .3f, 0x555555)
                .limb("armL", "body", -3, -1, -3, 1.6f, 2, 1.6f, 0xe6e6e6).limb("armR", "body", 3, -1, -3, 1.6f, 2, 1.6f, 0xe6e6e6)
                .limb("legL", null, -3.5f, 2, .5f, 3, 2, 5, 0xe0e0e0).limb("legR", null, 3.5f, 2, .5f, 3, 2, 5, 0xe0e0e0))
            .attacks(flySwarm("fly_swarm", null), frogLeap(), flyDrift()).build());

        out.add(CharacterDef.of("whimsun", "Whimsun", Faction.UNDERGROUND).hp(16).keep(5).rest(30).speed(.2f)
            .egg(0xe8f3e0, 0x9fe8d8).size(.5f, .9f).scale(.6f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 5, 6, 4, 0xe8f3e0)
                .box("head", "body", 0, 5, 0, 5, 4, 4, 0xf2f7ee)
                .glow("eyeL", "head", -1, 0, -2.1f, .8f, 1.2f, .3f, 0x222222).glow("eyeR", "head", 1, 0, -2.1f, .8f, 1.2f, .3f, 0x222222)
                .box("antL", "head", -1.2f, 3, 0, .5f, 2.5f, .5f, 0xcfe0c0).box("antR", "head", 1.2f, 3, 0, .5f, 2.5f, .5f, 0xcfe0c0)
                .ghost("wingL", "body", -5, 1, 1.5f, 6, 7, .5f, 0x9fe8d8, .75f).ghost("wingR", "body", 5, 1, 1.5f, 6, 7, .5f, 0x9fe8d8, .75f)
                .limb("legL", "body", -1, -3, 0, 1, 3, 1, 0xe0ead8).limb("legR", "body", 1, -3, 0, 1, 3, 1, 0xe0ead8))
            .attacks(mothRing(), mothColumns()).build());

        out.add(CharacterDef.of("moldsmal", "Moldsmal", Faction.UNDERGROUND).hp(24).keep(5).rest(30).speed(.12f)
            .egg(0x9bdc5b, 0x6aa33a).size(.7f, .5f).scale(.8f)
            .model(new ModelDef()
                .box("body", null, 0, 3, 0, 10, 6, 8, 0x9bdc5b)
                .box("head", "body", 0, 4, 0, 6, 3, 5, 0xb5ea7c)
                .box("tip", "head", 0, 2, 0, 2, 2, 2, 0xc8f59a)
                .glow("eyeL", "body", -2, 0, -4.1f, 1, 1, .3f, 0x2b4a12).glow("eyeR", "body", 2, 0, -4.1f, 1, 1, .3f, 0x2b4a12))
            .attacks(moldZigzag(), moldBurst()).build());

        out.add(CharacterDef.of("loox", "Loox", Faction.UNDERGROUND).hp(28).keep(6).rest(28)
            .egg(0xdcdce6, 0xd22020).size(.6f, .9f).scale(.7f)
            .model(new ModelDef()
                .box("body", null, 0, 8, 0, 9, 9, 8, 0xdcdce6)
                .box("head", "body", 0, 0, 0, .1f, .1f, .1f, 0xdcdce6)
                .glow("eye", "body", 0, .5f, -4.1f, 5, 5, .3f, 0xffffff).glow("pupil", "body", 0, .5f, -4.3f, 2, 2, .3f, 0xd22020)
                .box("hornL", "body", -3, 5.5f, 0, 1.5f, 3, 1.5f, 0xc070c0).box("hornR", "body", 3, 5.5f, 0, 1.5f, 3, 1.5f, 0xc070c0)
                .box("grin", "body", 0, -2.5f, -4.1f, 4, .7f, .3f, 0x333333)
                .limb("armL", "body", -5, 0, 0, 1.6f, 3, 1.6f, 0xcfcfda).limb("armR", "body", 5, 0, 0, 1.6f, 3, 1.6f, 0xcfcfda)
                .limb("legL", null, -2.5f, 3.5f, 0, 2, 3.5f, 2, 0xc8c8d4).limb("legR", null, 2.5f, 3.5f, 0, 2, 3.5f, 2, 0xc8c8d4))
            .attacks(looxBouncers(), looxWorms()).build());

        out.add(CharacterDef.of("vegetoid", "Vegetoid", Faction.UNDERGROUND).hp(28).keep(6).rest(28).speed(.15f)
            .egg(0xf08a24, 0x3fae3f).size(.6f, 1.0f).scale(.8f)
            .model(new ModelDef()
                .box("body", null, 0, 6, 0, 8, 10, 8, 0xf08a24)
                .glow("eyeL", "body", -2, 2, -4.1f, 1.4f, 1.4f, .3f, 0x2a1a0a).glow("eyeR", "body", 2, 2, -4.1f, 1.4f, 1.4f, .3f, 0x2a1a0a)
                .box("mouth", "body", 0, -1, -4.1f, 4, 1.2f, .3f, 0x6a1f0a)
                .box("head", "body", 0, 7, 0, 3, 4, 3, 0x3fae3f)
                .box("leafL", "head", -2.5f, 2, 0, 3, 5, 1, 0x4cc24c).box("leafR", "head", 2.5f, 2, 0, 3, 5, 1, 0x4cc24c)
                .limb("armL", "body", -4.8f, 2, 0, 1.5f, 4, 1.5f, 0x3fae3f).limb("armR", "body", 4.8f, 2, 0, 1.5f, 4, 1.5f, 0x3fae3f))
            .attacks(veggieBounce(), carrotDrop(), greenDinner()).build());

        out.add(CharacterDef.of("migosp", "Migosp", Faction.UNDERGROUND).hp(24).keep(5).rest(28)
            .egg(0x8a5cc2, 0x2a1a3a).size(.5f, 1.1f).scale(.7f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 4, 8, 3, 0x8a5cc2)
                .box("head", "body", 0, 6.5f, 0, 5, 5, 4, 0x9b6dd6)
                .glow("eyeL", "head", -1.2f, .5f, -2.1f, 1, 1.4f, .3f, 0xfff27a).glow("eyeR", "head", 1.2f, .5f, -2.1f, 1, 1.4f, .3f, 0xfff27a)
                .box("antL", "head", -1.5f, 4, 0, .4f, 3, .4f, 0x5a3a82).box("antR", "head", 1.5f, 4, 0, .4f, 3, .4f, 0x5a3a82)
                .limb("armL", "body", -2.7f, 3, 0, 1, 6, 1, 0x7a4cb2).limb("armR", "body", 2.7f, 3, 0, 1, 6, 1, 0x7a4cb2)
                .limb("legL", null, -1.2f, 6, 0, 1.2f, 6, 1.2f, 0x6a3ca2).limb("legR", null, 1.2f, 6, 0, 1.2f, 6, 1.2f, 0x6a3ca2))
            .attacks(roachRise(), flySwarm("migosp_fly_swarm", m -> m.getRandom().nextFloat() < .1f), lonelyDance()).build());

        out.add(CharacterDef.of("napstablook", "Napstablook", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(40).keep(6).rest(30).speed(.18f)
            .egg(0xf0f0f0, 0x7fd0ff).size(.7f, 1.3f).scale(.9f)
            .model(new ModelDef()
                .ghost("body", null, 0, 9, 0, 10, 12, 6, 0xf0f0f0, .8f)
                .ghost("head", "body", 0, 7.5f, 0, 9, 4, 6, 0xf4f4f4, .8f)
                .glow("eyeL", "head", -2, -1, -3.1f, 2.2f, .9f, .3f, 0x151515).glow("eyeR", "head", 2, -1, -3.1f, 2.2f, .9f, .3f, 0x151515)
                .ghost("tearL", "head", -2, -2.5f, -3.1f, .6f, 1.5f, .3f, 0x7fd0ff, .9f).ghost("tearR", "head", 2, -2.5f, -3.1f, .6f, 1.5f, .3f, 0x7fd0ff, .9f)
                .ghost("hem", "body", 0, -6.5f, 0, 11, 1.5f, 7, 0xe8e8e8, .7f))
            .attacks(curvedTears(), notFeelinIt(), wigglyTears()).build());

        out.add(dummy("dummy", "Dummy", false));

        // ---------------- Snowdin ----------------
        out.add(CharacterDef.of("ice_cap", "Ice Cap", Faction.UNDERGROUND).hp(28).keep(6).rest(28)
            .egg(0x3c6cbf, 0xbfefff).size(.5f, 1.3f).scale(.8f)
            .model(new ModelDef()
                .box("body", null, 0, 6, 0, 6, 6, 5, 0x3c6cbf)
                .box("head", "body", 0, 5, 0, 5, 4, 5, 0xcfe8ff)
                .glow("eyeL", "head", -1.2f, 0, -2.6f, 1, .6f, .3f, 0x10203a).glow("eyeR", "head", 1.2f, 0, -2.6f, 1, .6f, .3f, 0x10203a)
                .glow("hat1", "head", 0, 3.5f, 0, 7, 3, 7, 0xbfefff).glow("hat2", "hat1", 0, 3, 0, 5, 3, 5, 0xd6f6ff)
                .glow("hat3", "hat2", 0, 3, 0, 3, 3, 3, 0xe8fbff).glow("hat4", "hat3", 0, 2.5f, 0, 1.4f, 2.5f, 1.4f, 0xffffff)
                .limb("armL", "body", -3.7f, 2, 0, 1.4f, 4, 1.4f, 0x3c6cbf).limb("armR", "body", 3.7f, 2, 0, 1.4f, 4, 1.4f, 0x3c6cbf)
                .limb("legL", null, -1.5f, 3, 0, 2, 3, 2, 0x2a4f8f).limb("legR", null, 1.5f, 3, 0, 2, 3, 2, 0x2a4f8f))
            .attacks(iceHats(), iceZigzag()).build());

        out.add(CharacterDef.of("jerry", "Jerry", Faction.UNDERGROUND).hp(30).keep(4).rest(40).speed(.15f)
            .egg(0xe6c99a, 0x6b5a3a).size(.8f, .9f).scale(.8f)
            .model(new ModelDef()
                .box("body", null, 0, 7, 0, 12, 5, 8, 0xe6c99a)
                .box("head", "body", 0, 3, 0, 8, 2, 6, 0xdcbd8c)
                .box("spike1", "head", -2.5f, 1.5f, 0, 1.4f, 2, 1.4f, 0xc8a878).box("spike2", "head", 0, 2, 0, 1.4f, 2.5f, 1.4f, 0xc8a878)
                .box("spike3", "head", 2.5f, 1.5f, 0, 1.4f, 2, 1.4f, 0xc8a878)
                .glow("eyeL", "body", -2.2f, .8f, -4.1f, 1.8f, 1, .3f, 0x111111).glow("eyeR", "body", 2.2f, .8f, -4.1f, 1.8f, 1, .3f, 0x111111)
                .box("lips", "body", 0, -1.3f, -4.2f, 3.5f, 1.5f, .6f, 0xd27a7a)
                .box("spot", "body", -4, 1.5f, -2, 1.5f, 1, 1.5f, 0x8a7350)
                .limb("armL", "body", -6.3f, 1, 0, .8f, 9, .8f, 0xd8bb8a).limb("armR", "body", 6.3f, 1, 0, .8f, 9, .8f, 0xd8bb8a)
                .limb("legL", null, -3, 4.5f, -1.5f, 1.4f, 4.5f, 1.4f, 0xc8a878).limb("legR", null, 3, 4.5f, -1.5f, 1.4f, 4.5f, 1.4f, 0xc8a878)
                .limb("legL2", null, -3, 4.5f, 1.5f, 1.4f, 4.5f, 1.4f, 0xc8a878).limb("legR2", null, 3, 4.5f, 1.5f, 1.4f, 4.5f, 1.4f, 0xc8a878))
            .attacks(jerryAura()).build());

        out.add(CharacterDef.of("snowdrake", "Snowdrake", Faction.UNDERGROUND).hp(30).keep(6).rest(26)
            .egg(0xf2f8ff, 0xffcc33).size(.6f, 1.4f).scale(.9f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 7, 8, 5, 0xf2f8ff)
                .box("head", "body", 0, 7, 0, 7, 6, 6, 0xf2f8ff)
                .box("beak", "head", 0, -1, -3.8f, 3, 2, 2.5f, 0xffcc33)
                .glow("eyeL", "head", -1.8f, 1, -3.1f, 1, 1.3f, .3f, 0x10203a).glow("eyeR", "head", 1.8f, 1, -3.1f, 1, 1.3f, .3f, 0x10203a)
                .glow("crest", "head", 0, 4, 0, 1, 3, 4, 0x8fd8ff)
                .limb("armL", "body", -4.3f, 3.5f, 0, 1.8f, 7, 3, 0xdcecff).limb("armR", "body", 4.3f, 3.5f, 0, 1.8f, 7, 3, 0xdcecff)
                .limb("legL", null, -1.5f, 6, 0, 1.4f, 6, 1.4f, 0xffcc33).limb("legR", null, 1.5f, 6, 0, 1.4f, 6, 1.4f, 0xffcc33))
            .attacks(crescentVolley(), crescentArcs("crescent_arcs", null)).build());

        out.add(CharacterDef.of("gyftrot", "Gyftrot", Faction.UNDERGROUND).hp(44).keep(6).rest(28)
            .egg(0x8a5a3c, 0xd23a3a).size(.7f, 2.0f).scale(1.1f)
            .model(new ModelDef()
                .box("body", null, 0, 12, 0, 8, 10, 7, 0x8a5a3c)
                .box("belly", "body", 0, -1, -3.6f, 5, 6, .4f, 0xc9a37a)
                .box("head", "body", 0, 8, -1, 6, 6, 7, 0x8a5a3c)
                .box("snout", "head", 0, -1.5f, -4, 3.5f, 2.5f, 2, 0x6b4430)
                .glow("eyeL", "head", -1.7f, 1, -3.6f, 1, 1, .3f, 0x111111).glow("eyeR", "head", 1.7f, 1, -3.6f, 1, 1, .3f, 0x111111)
                .box("antL", "head", -4, 5, 0, 1, 6, 1, 0xc9a06a).box("antR", "head", 4, 5, 0, 1, 6, 1, 0xc9a06a)
                .box("antL2", "antL", -1.5f, 1.5f, 0, 3, 1, 1, 0xc9a06a).box("antR2", "antR", 1.5f, 1.5f, 0, 3, 1, 1, 0xc9a06a)
                .glow("ornL", "antL", 0, 3.5f, 0, 1.5f, 1.5f, 1.5f, 0xd23a3a).glow("ornR", "antR", 0, 3.5f, 0, 1.5f, 1.5f, 1.5f, 0x3ad25a)
                .glow("ornL2", "antL2", -1.5f, -1, 0, 1.2f, 1.2f, 1.2f, 0xffd23a).glow("ornR2", "antR2", 1.5f, -1, 0, 1.2f, 1.2f, 1.2f, 0x3a8ad2)
                .limb("armL", "body", -5, 4, 0, 2, 8, 2, 0x7a4d32).limb("armR", "body", 5, 4, 0, 2, 8, 2, 0x7a4d32)
                .limb("legL", null, -2, 7, 0, 2.5f, 7, 2.5f, 0x6b4430).limb("legR", null, 2, 7, 0, 2.5f, 7, 2.5f, 0x6b4430))
            .attacks(presentShuffle(), snowflakeWaves()).build());

        out.add(CharacterDef.of("doggo", "Doggo", Faction.ROYAL_GUARD).hp(40).keep(5).rest(26)
            .egg(0x5d6f9c, 0x3fa7ff).size(.6f, 1.5f).scale(.9f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 7, 8, 5, 0x5d6f9c)
                .box("head", "body", 0, 7, 0, 6, 6, 6, 0xf3f3f3)
                .box("snout", "head", 0, -1.5f, -3.6f, 3, 2.5f, 2, 0xf3f3f3).box("nose", "snout", 0, .7f, -1.1f, 1, .8f, .3f, 0x111111)
                .glow("eyeL", "head", -1.5f, 1, -3.1f, 1.4f, .4f, .3f, 0x111111).glow("eyeR", "head", 1.5f, 1, -3.1f, 1.4f, .4f, .3f, 0x111111)
                .box("hood", "head", 0, 3.3f, .5f, 7, 1.5f, 7, 0x4b4b8a).box("earL", "head", -3.4f, 2, 1, 1, 3, 2, 0x4b4b8a).box("earR", "head", 3.4f, 2, 1, 1, 3, 2, 0x4b4b8a)
                .limb("armL", "body", -4.3f, 3.5f, 0, 1.8f, 7, 1.8f, 0x5d6f9c).limb("armR", "body", 4.3f, 3.5f, 0, 1.8f, 7, 1.8f, 0x5d6f9c)
                .glow("knifeL", "armL", 0, -7.5f, -2, .5f, .5f, 4.5f, 0x3fa7ff).glow("knifeR", "armR", 0, -7.5f, -2, .5f, .5f, 4.5f, 0x3fa7ff)
                .limb("legL", null, -1.6f, 6, 0, 2, 6, 2, 0x3a3a5a).limb("legR", null, 1.6f, 6, 0, 2, 6, 2, 0x3a3a5a))
            .attacks(doggoSweep(), crescentArcs("doggo_snowdrake_cameo", m -> m.getRandom().nextFloat() < .03f)).build());

        out.add(CharacterDef.of("lesser_dog", "Lesser Dog", Faction.ROYAL_GUARD).hp(40).keep(5).rest(26)
            .egg(0xf6f0de, 0x9a9aa6).size(.6f, 1.5f).scale(.9f)
            .model(new ModelDef()
                .box("body", null, 0, 10, 0, 7, 8, 5, 0x9a9aa6)
                .box("belt", "body", 0, -2.5f, 0, 7.4f, 1.2f, 5.4f, 0x6a6a74)
                .box("neck", "body", 0, 5, 0, 3, 3, 3, 0xf6f0de)
                .box("head", "neck", 0, 3.5f, 0, 6, 5, 6, 0xf6f0de)
                .box("snout", "head", 0, -1, -3.6f, 3, 2, 2, 0xf6f0de).box("tongue", "snout", 0, -1.4f, -.5f, 1.2f, 1.5f, .3f, 0xe23a3a)
                .glow("eyeL", "head", -1.5f, 1, -3.1f, 1, 1, .3f, 0x111111).glow("eyeR", "head", 1.5f, 1, -3.1f, 1, 1, .3f, 0x111111)
                .box("earL", "head", -3.3f, 0, 0, 1, 4, 2, 0xe6dcc0).box("earR", "head", 3.3f, 0, 0, 1, 4, 2, 0xe6dcc0)
                .limb("armL", "body", -4.3f, 3.5f, 0, 1.8f, 7, 1.8f, 0xf6f0de).limb("armR", "body", 4.3f, 3.5f, 0, 1.8f, 7, 1.8f, 0xf6f0de)
                .box("shield", "armL", -1.2f, -5, -1, .6f, 5, 4, 0x8a8a96).glow("shieldRune", "shield", -.4f, 0, 0, .2f, 2, 2, 0xf3e8ff)
                .glow("sword", "armR", 0, -7.5f, -2.5f, .4f, .6f, 6, 0xffffff)
                .limb("legL", null, -1.6f, 6, 0, 2, 6, 2, 0x7a7a86).limb("legR", null, 1.6f, 6, 0, 2, 6, 2, 0x7a7a86))
            .attacks(pupPounce(), spearPass()).build());

        out.add(guardDog("dogamy", "Dogamy", 0x3a3550, 0x2e2a3e, 0x9a9aa6, dogamyLonelyBark()));
        out.add(guardDog("dogaressa", "Dogaressa", 0x4a3a5a, 0x3e2e4e, 0xfafafa, dogaressaRapidAxes()));

        out.add(CharacterDef.of("greater_dog", "Greater Dog", Faction.ROYAL_GUARD).hp(70).keep(6).rest(26).speed(.22f)
            .egg(0xb7b7c7, 0xfafafa).size(.9f, 1.8f).scale(1.2f)
            .model(new ModelDef()
                .box("body", null, 0, 12, 0, 12, 12, 8, 0xb7b7c7)
                .box("plate", "body", 0, 1, -4.1f, 8, 6, .4f, 0x9a9aae).glow("rune", "plate", 0, 0, -.3f, 3, 3, .2f, 0xf3e8ff)
                .box("helm", "body", 0, 6.5f, 0, 10, 2, 7, 0xa8a8b8)
                .box("head", "body", 0, 7.5f, -1.5f, 5, 4, 5, 0xfafafa)
                .box("snout", "head", 0, -.8f, -3, 2.5f, 2, 2, 0xfafafa).box("nose", "snout", 0, .6f, -1.1f, .9f, .7f, .3f, 0x111111)
                .glow("eyeL", "head", -1.2f, .8f, -2.6f, .8f, .8f, .3f, 0x111111).glow("eyeR", "head", 1.2f, .8f, -2.6f, .8f, .8f, .3f, 0x111111)
                .box("earL", "head", -2, 3, 0, 1, 2, 1, 0xeeeeee).box("earR", "head", 2, 3, 0, 1, 2, 1, 0xeeeeee)
                .limb("armL", "body", -7.5f, 5, 0, 3, 9, 3, 0xa8a8b8).limb("armR", "body", 7.5f, 5, 0, 3, 9, 3, 0xa8a8b8)
                .glow("spear", "armR", 0, -9, -4, .5f, .5f, 14, 0xffffff).box("spearDog", "spear", 0, 0, -7.5f, 1.8f, 1.8f, 1.8f, 0xfafafa)
                .limb("legL", null, -3, 6, 0, 3.5f, 6, 3.5f, 0x8a8a9a).limb("legR", null, 3, 6, 0, 3.5f, 6, 3.5f, 0x8a8a9a))
            .attacks(flickerSpear(), sleepingBark()).build());

        out.add(CharacterDef.of("papyrus", "Papyrus", Faction.UNDERGROUND).hp(160).boss().keep(7).rest(16).speed(.3f)
            .egg(0xf5f5f5, 0xd32121).size(.7f, 1.9f).scale(1.05f)
            .model(new ModelDef()
                .limb("legL", null, -2, 9, 0, 2.4f, 9, 2.4f, 0xf0f0f0).limb("legR", null, 2, 9, 0, 2.4f, 9, 2.4f, 0xf0f0f0)
                .box("bootL", "legL", 0, -8, -.5f, 3, 2, 4, 0xd32121).box("bootR", "legR", 0, -8, -.5f, 3, 2, 4, 0xd32121)
                .box("body", null, 0, 13.5f, 0, 8, 9, 5, 0xf5f5f5)
                .box("trunks", "body", 0, -4, 0, 8.4f, 2, 5.4f, 0x2a5fd0).box("belt", "trunks", 0, 1, 0, 8.6f, .6f, 5.6f, 0xf4d03f)
                .box("padL", "body", -4.8f, 3.5f, 0, 3, 2, 4, 0xf5f5f5).box("padR", "body", 4.8f, 3.5f, 0, 3, 2, 4, 0xf5f5f5)
                .glow("emblem", "body", 0, 1, -2.6f, 3, 2, .3f, 0xf4d03f)
                .box("scarf", "body", 0, 4.5f, 0, 6, 2, 6, 0xd32121).box("scarfTail", "scarf", 2.5f, -2, 3, 1.5f, 5, .6f, 0xd32121)
                .box("head", "body", 0, 9, 0, 7, 8, 7, 0xffffff)
                .glow("eyeL", "head", -1.7f, .8f, -3.6f, 1.4f, 1.6f, .3f, 0x111111).glow("eyeR", "head", 1.7f, .8f, -3.6f, 1.4f, 1.6f, .3f, 0x111111)
                .box("teeth", "head", 0, -2.4f, -3.6f, 4.5f, 1, .3f, 0x333333)
                .limb("armL", "body", -5, 3, 0, 2, 8, 2, 0xf5f5f5).limb("armR", "body", 5, 3, 0, 2, 8, 2, 0xf5f5f5)
                .box("gloveL", "armL", 0, -8, 0, 3, 2.2f, 3, 0xd32121).box("gloveR", "armR", 0, -8, 0, 3, 2.2f, 3, 0xd32121))
            .attacks(papyBlueAttack(), papySeries(), papyStairs(), papyDangling(), papyBlueRush(), papyHump(), papyNarrow(),
                papyEndless(), papySpecialAttack(), papyNormalAttack()).build());

        out.add(dummy("glad_dummy", "Glad Dummy", true));

        out.add(CharacterDef.of("annoying_dog", "Annoying Dog", Faction.NEUTRAL).att(Attitude.FRIENDLY).hp(20).keep(3).speed(.3f)
            .egg(0xffffff, 0x222222).size(.5f, .5f).scale(.6f)
            .model(new ModelDef()
                .box("body", null, 0, 4, 1, 6, 4, 8, 0xffffff)
                .box("head", "body", 0, 2.5f, -5, 5, 5, 5, 0xffffff)
                .box("nose", "head", 0, -1, -2.6f, 1, 1, .4f, 0x111111)
                .glow("eyeL", "head", -1.2f, .5f, -2.6f, .7f, .7f, .3f, 0x111111).glow("eyeR", "head", 1.2f, .5f, -2.6f, .7f, .7f, .3f, 0x111111)
                .box("earL", "head", -2.8f, 0, 0, 1, 3.5f, 2.5f, 0x222222).box("earR", "head", 2.8f, 0, 0, 1, 3.5f, 2.5f, 0x222222)
                .box("tail", "body", 0, 1.5f, 4.5f, 1, 1, 2, 0xffffff)
                .limb("armL", null, -2, 2, -2.5f, 1.5f, 2, 1.5f, 0xf4f4f4).limb("armR", null, 2, 2, -2.5f, 1.5f, 2, 1.5f, 0xf4f4f4)
                .limb("legL", null, -2, 2, 3.5f, 1.5f, 2, 1.5f, 0xf4f4f4).limb("legR", null, 2, 2, 3.5f, 1.5f, 2, 1.5f, 0xf4f4f4))
            .build()); // canon: never fights
    }

    static CharacterDef dummy(String id, String name, boolean glad) {
        return CharacterDef.of(id, name, Faction.NEUTRAL).att(Attitude.FRIENDLY).hp(glad ? 20 : 30).speed(0).keep(3)
            .egg(0xd9b67a, glad ? 0xff8fb0 : 0x6b4a2a).size(.6f, 1.2f).scale(.9f)
            .model(new ModelDef()
                .box("pole", null, 0, 2, 0, 1.5f, 4, 1.5f, 0x6b4a2a).box("base", null, 0, .5f, 0, 5, 1, 4, 0x5a3a1a)
                .box("body", null, 0, 8, 0, 8, 8, 5, 0xd9b67a)
                .box("stitch", "body", 0, 0, -2.6f, .5f, 7, .3f, 0x8a6a3a)
                .box("head", "body", 0, 7, 0, 7, 6, 6, 0xdcbc82)
                .glow("eyeL", "head", -1.5f, 1, -3.1f, 1.2f, 1.2f, .3f, 0x2a1a0a).glow("eyeR", "head", 1.5f, 1, -3.1f, 1.2f, 1.2f, .3f, 0x2a1a0a)
                .box("mouth", "head", 0, glad ? -1.4f : -1.8f, -3.1f, glad ? 3.5f : 2, glad ? 1.2f : .4f, .3f, glad ? 0xc04060 : 0x6a4a2a)
                .limb("armL", "body", -5, 2.5f, 0, 2, 4, 2, 0xd0aa70).limb("armR", "body", 5, 2.5f, 0, 2, 4, 2, 0xd0aa70))
            .build(); // canon: dummies never attack
    }

    static CharacterDef guardDog(String id, String name, int robe, int hood, int snout, Attack solo) {
        return CharacterDef.of(id, name, Faction.ROYAL_GUARD).hp(50).keep(6).rest(26)
            .egg(robe, snout).size(.6f, 1.7f).scale(1f)
            .model(new ModelDef()
                .box("body", null, 0, 11, 0, 7, 10, 5, robe)
                .box("head", "body", 0, 8, 0, 6, 6, 7, hood)
                .box("snout", "head", 0, -1.2f, -4.2f, 3, 2.5f, 2.5f, snout).box("nose", "snout", 0, .7f, -1.3f, 1, .8f, .3f, 0x111111)
                .glow("eyeL", "head", -1.5f, 1, -3.6f, 1, .5f, .3f, 0xf0f0f0).glow("eyeR", "head", 1.5f, 1, -3.6f, 1, .5f, .3f, 0xf0f0f0)
                .box("earL", "head", -3.2f, 2.8f, 0, 1.2f, 2.5f, 1.5f, hood).box("earR", "head", 3.2f, 2.8f, 0, 1.2f, 2.5f, 1.5f, hood)
                .limb("armL", "body", -4.3f, 4, 0, 1.8f, 8, 1.8f, robe).limb("armR", "body", 4.3f, 4, 0, 1.8f, 8, 1.8f, robe)
                .box("haft", "armR", 0, -8, -1, .6f, .6f, 9, 0x6b4a2a).glow("blade", "haft", 0, 1.2f, -4.2f, .3f, 3, 2.2f, 0xd8d8e0)
                .limb("legL", null, -1.6f, 6, 0, 2, 6, 2, hood).limb("legR", null, 1.6f, 6, 0, 2, 6, 2, hood))
            .attacks(axeHeart(), heartRingBark(), solo).build();
    }

    // =================================================================== shared helpers
    interface Tick { void run(AttackCtx c, int t); }
    interface Anim { void run(Pose p, float t, UtMob m); }
    static Attack atk(String id, int dur, Predicate<UtMob> when, Tick tick, Anim anim) {
        return new Attack(id, dur) {
            public void tick(AttackCtx c, int t) { tick.run(c, t); }
            public void pose(Pose p, float t, UtMob m) { anim.run(p, t, m); }
            public boolean usable(UtMob m) { return when == null || when.test(m); }
        };
    }
    /** The "bullet board": frozen at t=0 on the target's feet. x = across (right), y = up, z = depth (away from the attacker). */
    record Board(Vec3 o, Vec3 r, Vec3 f) {
        Vec3 at(double x, double y, double z) { return o.add(r.scale(x)).add(f.scale(z)).add(0, y, 0); }
    }
    static Board board(AttackCtx c, int t) {
        CompoundTag d = c.me.getPersistentData();
        if (t == 0 || !d.contains("utBx")) {
            Vec3 o = c.target.position(), f = o.subtract(c.pos()).multiply(1, 0, 1);
            f = f.lengthSqr() < 1e-4 ? c.fwd() : f.normalize();
            d.putDouble("utBx", o.x); d.putDouble("utBy", o.y); d.putDouble("utBz", o.z); d.putDouble("utFx", f.x); d.putDouble("utFz", f.z);
        }
        Vec3 f = new Vec3(d.getDouble("utFx"), 0, d.getDouble("utFz"));
        return new Board(new Vec3(d.getDouble("utBx"), d.getDouble("utBy"), d.getDouble("utBz")), new Vec3(-f.z, 0, f.x), f);
    }
    static float ramp(float t, float a, float len) { return Math.max(0, Math.min(1, (t - a) / len)); }
    /** Wind-up over `in` ticks, hold, recover over the last `out` ticks of `dur`. */
    static float env(float t, float in, float dur, float out) { return Math.min(ramp(t, 0, in), 1 - ramp(t, dur - out, out)); }
    /** Deterministic pseudo-random in [0,1) so two ticks agree on the same bullet. */
    static double hr(int i) { double v = Math.sin(i * 12.9898 + 78.233) * 43758.5453; return v - Math.floor(v); }
    static UtProjectile.B dot(AttackCtx c, Vec3 at, Vec3 vel, float s, int col, float dmg) {
        return c.proj().at(at).vel(vel).box(s, s, s).color(col).dmg(dmg);
    }
    /** Moves a bullet along an arbitrary path p(tick) by chaining straight segments of n ticks. */
    static void step(AttackCtx c, int t, int t0, int t1, int n, IntFunction<Vec3> p, Consumer<UtProjectile.B> fin) {
        if (t < t0 || t >= t1 || (t - t0) % n != 0) return;
        Vec3 a = p.apply(t), b = p.apply(t + n);
        fin.accept(c.proj().at(a).vel(b.subtract(a).scale(1.0 / n)).life(n));
    }
    /** Flat harmless telegraph on the floor. */
    static void mark(AttackCtx c, Vec3 at, float w, float l, int col, int life) {
        c.proj().at(at.add(0, .03, 0)).box(w, .04f, l).color(col).dmg(0).warn(life).life(life).spawn();
    }
    /** A bone that shows as an outline for `warn` ticks, then stabs. */
    static void stab(AttackCtx c, Vec3 at, float h, int warn, float dmg) {
        c.proj().at(at.add(0, h / 2, 0)).box(.22f, h, .22f).color(0xffffff).dmg(dmg).warn(warn).life(warn + 8).spawn();
    }
    static final double CEIL = 3.4;
    /** One bone "line": a comb of upright bones spanning the board depth, scrolling across from side `from` (-1 left, +1 right). */
    static void row(AttackCtx c, Board b, int from, float h, boolean top, double spd, float dmg, boolean blue) {
        double y = top ? CEIL - h / 2 : h / 2; int life = (int) (13 / spd);
        for (int k = -3; k <= 3; k++) {
            UtProjectile.B p = c.proj().at(b.at(from * 6.5, y, k * .7)).vel(b.r().scale(-from * spd)).box(.22f, h, .22f)
                .color(0xffffff).dmg(dmg).warn(2).life(life);
            if (blue) p.blue();
            p.spawn();
        }
    }
    static boolean low(AttackCtx c) { return c.target.getHealth() <= 4; }
    /** Damage that never takes the target below 1 HP (Sans's late menu / final attacks). */
    static float cap(AttackCtx c, float d) { return Math.max(0, Math.min(d, c.target.getHealth() - 1)); }
    static void say(AttackCtx c, String s) {
        if (c.target instanceof Player p) p.displayClientMessage(Component.literal(s).withStyle(ChatFormatting.GRAY), true);
    }
    /** True when the target moved since last tick (server-side player velocity is unreliable, so compare positions). */
    static boolean moving(AttackCtx c) {
        CompoundTag d = c.me.getPersistentData(); Vec3 p = c.target.position();
        boolean mv = d.contains("utTx") && Math.hypot(p.x - d.getDouble("utTx"), p.z - d.getDouble("utTz")) > .03;
        d.putDouble("utTx", p.x); d.putDouble("utTz", p.z);
        return mv;
    }
    static boolean near(UtMob m, String id) {
        return !m.level().getEntitiesOfClass(UtMob.class, m.getBoundingBox().inflate(14), e -> e != m && e.isAlive() && e.def.id().equals(id)).isEmpty();
    }
    static Predicate<UtMob> ph(float lo, float hi) { return m -> m.hpFrac() >= lo && m.hpFrac() < hi; }
    /** Ballistic launch velocity reaching `to` in `ticks` under gravity g. */
    static Vec3 lob(Vec3 from, Vec3 to, int ticks, double g) {
        Vec3 d = to.subtract(from);
        return new Vec3(d.x / ticks, d.y / ticks + g * (ticks + 1) / 2, d.z / ticks);
    }
    static final int FIRE = 0xff6a1f, CYAN = 0x35e4ff;

    // =================================================================== SANS (Genocide fight)
    /** Shared Sans poses: eye flash + hand gesture. */
    static void sansEye(Pose p, float s) { p.o("eyeBlue", 0, 0, -1.7f * s); }
    static void sansSlam(Pose p, float t, float period) {
        float u = (t % period) / period, s = u < .5f ? u * 2 : 1 - (u - .5f) * 2;
        p.r("armR", -2.4f * s + .4f, 0, -.2f).o("body", 0, -.3f * s, 0);
    }
    /** Opening "bad time": blue slam onto floor bones, a bone wave, then 4 Gaster Blaster volleys (sides, X, sides, big). */
    static Attack sansBadTime() {
        return atk("bad_time", 150, ph(.9f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t == 2) { mark(c, b.at(0, 0, 0), 5, 6, 0xff2020, 14); c.target.setDeltaMovement(0, -1.6, 0); c.target.hurtMarked = true; c.sound(SoundEvents.ANVIL_LAND, 1, .6f); }
            if (t == 6) for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) stab(c, b.at(x * .9 + .45 * (z & 1), 0, z * .9), .9f, 10, 4);
            if (t >= 30 && t < 56 && t % 6 == 0) { row(c, b, -1, .45f, false, .4, 4, false); row(c, b, -1, .9f, true, .4, 4, false); }
            if (t == 62) for (int s = -1; s <= 1; s += 2) Fx.blaster(c, b.at(s * 6, 1.4, 0), b.r().scale(-s), 18, 12, 5, 1f);
            if (t == 82) for (int s = -1; s <= 1; s += 2) for (int u = -1; u <= 1; u += 2) {
                Vec3 at = b.at(s * 5, 1.8, u * 5); Fx.blaster(c, at, b.at(0, 1, 0).subtract(at), 18, 12, 5, 1f);
            }
            if (t == 102) for (int s = -1; s <= 1; s += 2) Fx.blaster(c, b.at(s * 6, 1.2, s * 1.5), b.r().scale(-s), 16, 12, 5, 1f);
            if (t == 120) for (int s = -1; s <= 1; s += 2) Fx.blaster(c, b.at(s * 7, 1.6, 0), b.r().scale(-s), 20, 14, 6, 2.2f);
        }, (p, t, m) -> {
            sansEye(p, env(t, 4, 150, 10));
            if (t < 30) sansSlam(p, t, 30);
            else p.r("armL", -2.6f * env(t - 56, 6, 94, 8), 0, .3f).r("armR", -2.6f * env(t - 56, 6, 94, 8), 0, -.3f);
        });
    }
    /** Turn 1: 16 lines, small bones below (8 jumps) alternating with long bones from above (stay low). */
    static Attack sansBoneLines() {
        return atk("bone_lines", 120, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 8 && t < 104 && t % 6 == 2) { int i = (t - 8) / 6; row(c, b, -1, i % 2 == 0 ? .45f : 1.4f, i % 2 == 1, .38, 4, false); }
            if (t % 12 == 2) c.sound(SoundEvents.SKELETON_SHOOT, .6f, 1.6f);
        }, (p, t, m) -> {
            float s = env(t, 6, 120, 8);
            p.r("armR", -1.3f * s, 0, -.4f * s).r("body", 0, .25f * s, 0).o("head", 0, -.4f * s, 0);
        });
    }
    /** Turn 2: 6 from the left (long blue, short white), 6 from the right (short white, long blue). */
    static Attack sansBlueBones() {
        return atk("blue_bones", 110, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 6 && t < 90 && t % 7 == 6) {
                int i = (t - 6) / 7, from = i < 6 ? -1 : 1; boolean blue = (i % 2 == 0) == (from < 0);
                row(c, b, from, blue ? 2.6f : .45f, false, .42, 4, blue);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 110, 8), side = t < 48 ? 1 : -1;
            p.r("armR", -1.5f * s, .4f * side * s, -.3f).r("armL", -.5f * s, 0, .3f).r("head", 0, .2f * side * s, 0);
            sansEye(p, s * (t > 4 && t < 14 ? 1 : 0));
        });
    }
    /** Turn 3: randomized pairs of left/right lines at varying speeds. */
    static Attack sansRandomLines() {
        return atk("random_bone_lines", 110, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 6 && t < 90 && t % 14 == 6) {
                double spd = .28 + c.rnd.nextDouble() * .3;
                row(c, b, -1, .45f, false, spd, 4, false); row(c, b, 1, .45f, false, spd * (.7 + c.rnd.nextDouble() * .6), 4, false);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 110, 8), w = (float) Math.sin(t * .35f) * s;
            p.r("armL", -1.1f * s, 0, .4f * w).r("armR", -1.1f * s, 0, -.4f * w);
        });
    }
    /** Bone sea filling the board except under the platforms (a platform = a moving safe lane at board x). */
    static void sea(AttackCtx c, Board b, int t, double[] plats, double half, boolean firstWarn) {
        if (t % 4 != 0) return;
        java.util.Arrays.sort(plats);
        double x0 = -5;
        for (int i = 0; i <= plats.length; i++) {
            double x1 = i < plats.length ? plats[i] - half : 5;
            if (x1 - x0 > .2) c.proj().at(b.at((x0 + x1) / 2, .25, 0)).face(b.r()).box(5f, .5f, (float) (x1 - x0)).color(0xf2f2f2)
                .dmg(4).warn(firstWarn ? 4 : 0).life(4).spawn();
            if (i < plats.length) x0 = Math.max(x0, plats[i] + half);
        }
        for (double x : plats) c.proj().at(b.at(x, .05, 0)).face(b.r()).box(5f, .1f, (float) (half * 2)).color(0x33dd44).dmg(0).life(4).spawn();
    }
    static double[] plats(int t, int start, int every, int count, double spd, int from) {
        int n = 0; double[] tmp = new double[count];
        for (int i = 0; i < count; i++) { int s = start + i * every; if (t >= s) { double x = from * (5.5 - (t - s) * spd); if (Math.abs(x) < 6) tmp[n++] = x; } }
        return java.util.Arrays.copyOf(tmp, n);
    }
    static Anim seaPose = (p, t, m) -> {
        float s = Math.min(1, t / 8f);
        p.r("armR", -1.2f * s, .5f * (float) Math.sin(t * .1f), -.2f).o("body", 0, .4f * s, 0);
        sansEye(p, s);
    };
    /** Turn 4: three platforms from the left over a sea of bones; three bones near the third; a long bone from the top at the end. */
    static Attack sansPlatforms1() {
        return atk("platforms_1", 130, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 8 && t < 120) sea(c, b, t, plats(t, 0, 30, 3, .09, -1), .9, t < 12);
            if (t == 66) for (int k = 0; k < 3; k++) row(c, b, -1, .45f, false, .09, 4, false);
            if (t == 110) row(c, b, -1, 1.5f, true, .5, 4, false);
        }, seaPose);
    }
    /** Turn 5: seven platforms from the right, bones above the 4th-6th, long bones top-left and bottom-right at the end. */
    static Attack sansPlatforms2() {
        return atk("platforms_2", 150, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 8 && t < 140) sea(c, b, t, plats(t, 0, 16, 7, .11, 1), .8, t < 12);
            for (int i = 3; i < 6; i++) if (t == i * 16) row(c, b, 1, 1.5f, true, .11, 4, false);
            if (t == 124) { row(c, b, -1, 1.5f, true, .5, 4, false); row(c, b, 1, .5f, false, .5, 4, false); }
        }, seaPose);
    }
    /** Turn 6: long moving platforms with bones blocking the paths, low ones from the right, high ones from the left. */
    static Attack sansPlatforms3() {
        return atk("platforms_3", 140, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            double[] a = plats(t, 0, 36, 4, .1, 1), d = plats(t, 18, 36, 3, .1, -1), all = new double[a.length + d.length];
            System.arraycopy(a, 0, all, 0, a.length); System.arraycopy(d, 0, all, a.length, d.length);
            if (t >= 8 && t < 132) sea(c, b, t, all, 1.3, t < 12);
            if (t % 36 == 10) row(c, b, 1, .45f, false, .1, 4, false);
            if (t % 36 == 28) row(c, b, -1, 1.5f, true, .1, 4, false);
        }, seaPose);
    }
    /** Turn 7: SOUL rides one small platform over the sea; three levitating centre bone sets, centre line sinks, outer lines rise. */
    static Attack sansPlatforms4() {
        return atk("platforms_4", 140, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 8 && t < 132) sea(c, b, t, new double[]{3.5 * Math.sin(t * .045)}, .8, t < 12);
            if (t == 20) for (int k = -3; k <= 3; k++) {
                c.proj().at(b.at(0, CEIL, k * .7)).vel(new Vec3(0, -.018, 0)).box(.22f, 1.2f, .22f).color(0xffffff).dmg(4).warn(10).life(100).spawn();
                for (int s = -1; s <= 1; s += 2)
                    c.proj().at(b.at(s * 1.6, -.4, k * .7)).vel(new Vec3(0, .015, 0)).box(.22f, 1.2f, .22f).color(0xffffff).dmg(4).warn(10).life(100).spawn();
            }
        }, seaPose);
    }
    /** Turn 8: small bones slide from the left along the bottom while big bones come from the right at the top. */
    static Attack sansSlides() {
        return atk("horizontal_bone_slides", 110, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 6 && t < 92 && t % 9 == 6) row(c, b, -1, .45f, false, .35, 4, false);
            if (t >= 10 && t < 92 && t % 13 == 10) row(c, b, 1, 1.5f, true, .3, 4, false);
        }, (p, t, m) -> {
            float s = env(t, 6, 110, 8);
            p.r("armL", -1.4f * s, -.5f * s, .2f).r("armR", -1.4f * s, .5f * s, -.2f).r("body", 0, (float) Math.sin(t * .2f) * .2f * s, 0);
        });
    }
    /** Flat (board-wide) Gaster Blaster beam: skull + thin full-depth slab at height y. */
    static void slabBlaster(AttackCtx c, Board b, int from, double y, int charge, int fire, float dmg) {
        Vec3 at = b.at(from * 7, y + .6, 0), dir = b.r().scale(-from);
        c.proj().at(at).face(dir).box(1.1f, 1.0f, 1.2f).color(0xf2f2f2).dmg(0).life(charge + fire + 4).spawn();
        c.proj().at(at.add(dir.scale(.55)).add(0, .15, 0)).face(dir).box(.9f, .2f, .12f).color(CYAN).dmg(0).life(charge + fire + 4).spawn();
        c.proj().at(b.at(0, y, 0)).face(dir).box(5f, .45f, 14f).color(0xcfefff).dmg(dmg).warn(charge).life(charge + fire).pierce().spawn();
        c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1f, 1.8f);
    }
    /** Turn 9: small moving platforms; thin blasters fire just above the floor (jump) or at head height (don't). */
    static Attack sansPlatformBlasters() {
        return atk("platforms_gaster_blasters", 140, ph(.12f, 1.01f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 8 && t < 132) sea(c, b, t, plats(t, 0, 28, 5, .1, t < 70 ? 1 : -1), .8, t < 12);
            if (t >= 20 && t < 120 && t % 24 == 20) slabBlaster(c, b, (t / 24) % 2 == 0 ? -1 : 1, (t / 12) % 2 == 0 ? .3 : 2.1, 16, 8, 4);
        }, (p, t, m) -> {
            float s = env(t, 6, 140, 8);
            p.r("armL", -2.4f * s, 0, .3f).r("armR", -2.4f * s, 0, -.3f); sansEye(p, s);
        });
    }
    /** False mercy: he stops and offers to spare. Hitting him refuses; leaving him be "accepts" and the board fills with bones. */
    static Attack sansFalseMercy() {
        return atk("false_mercy", 120, ph(.45f, .6f), (c, t) -> {
            if (t == 5) say(c, "* sans is sparing you.");
            if (t == 64 && c.me.getLastHurtByMobTimestamp() < c.me.tickCount - 64) {
                Vec3 o = c.target.position();
                for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) stab(c, o.add(x * .6, 0, z * .6), 2.4f, 4, 6);
                c.sound(SoundEvents.SKELETON_HURT, 1, .6f);
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 64, 4);
            p.r("armL", -1.2f * s, 0, .6f * s).r("armR", -1.2f * s, 0, -.6f * s).o("head", 0, -.3f * s, 0);
            if (t > 60) { float k = env(t - 60, 3, 60, 8); p.r("armR", -2.8f * k, 0, 0); sansEye(p, k); }
        });
    }
    /** Turns 13-23: bones jab at you wherever you stand ("menu attacks"), never taking your last HP. */
    static Attack sansMenuBones() {
        return atk("menu_bones", 110, ph(.12f, .5f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 6 && t < 90 && t % 8 == 6) {
                Vec3 o = c.target.position();
                for (int k = 0; k < 4; k++) stab(c, o.add(c.rnd.nextGaussian() * .8, 0, c.rnd.nextGaussian() * .8), 1.6f, 8, cap(c, 4));
                mark(c, o, 2.2f, 2.2f, 0xff2020, 8);
            }
            if (t == 40 || t == 70) for (int s = -1; s <= 1; s += 2) Fx.blaster(c, b.at(s * 6, 1.4, (t == 40 ? 1 : -1) * 1.2), b.r().scale(-s), 16, 10, cap(c, 5), 1f);
        }, (p, t, m) -> {
            float s = env(t, 6, 110, 8);
            sansSlam(p, t, 8); sansEye(p, s);
        });
    }
    /** The final ~40 s attack: chained gravity slams into each wall, bones waiting there, blasters in between. Stops at 1 HP. */
    static Attack sansFinalAttack() {
        return atk("final_attack", 280, ph(.12f, .25f), (c, t) -> {
            Board b = board(c, t);
            if (t >= 10 && t < 250 && t % 30 == 10) {
                int k = (t - 10) / 30 % 4;
                Vec3 d = k == 0 ? new Vec3(0, -1, 0) : k == 1 ? b.r().scale(-1) : k == 2 ? b.r() : b.f();
                c.target.setDeltaMovement(d.x * 1.3, d.y < 0 ? -1.6 : -.3, d.z * 1.3); c.target.hurtMarked = true;
                c.sound(SoundEvents.ANVIL_LAND, 1, .5f + k * .1f);
                Vec3 o = c.target.position();
                if (d.y < 0) for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) stab(c, o.add(x * .7, 0, z * .7), .8f, 6, cap(c, 4));
                else { Vec3 w = o.add(d.scale(3.2)), side = new Vec3(-d.z, 0, d.x); for (int j = -4; j <= 4; j++) stab(c, w.add(side.scale(j * .6)), 2.4f, 5, cap(c, 4)); }
            }
            if (t >= 25 && t < 250 && t % 30 == 25) {
                Vec3 at = c.target.position().add(c.rnd.nextGaussian() * 4, 2.5, c.rnd.nextGaussian() * 4);
                Fx.blaster(c, at, c.tpos().subtract(at), 14, 10, cap(c, 5), 1.1f);
            }
        }, (p, t, m) -> {
            sansSlam(p, t + 20, 30); sansEye(p, env(t, 6, 280, 10));
            p.o("head", 0, -.6f * ramp(t, 200, 80), 0);
        });
    }
    /** "Special attack": he just never ends his turn... and slowly falls asleep. Free hits. */
    static Attack sansSpecialAttack() {
        return atk("special_attack", 240, ph(0, .12f), (c, t) -> {
            if (t == 5) say(c, "* here we go. here's my special attack.");
            if (t == 160) say(c, "* sans is sleeping.");
            if (t > 160 && t % 20 == 0) c.particle(ParticleTypes.POOF, c.eye().add(0, .6, 0), 2, .1, .01);
        }, (p, t, m) -> {
            float s = ramp(t, 40, 140);
            p.o("eyeL", 0, 0, 1.2f * s).o("eyeR", 0, 0, 1.2f * s).r("head", .5f * s, 0, .15f * s).r("armL", .2f * s, 0, .3f * s).r("armR", .2f * s, 0, -.3f * s);
        });
    }

    /** Sweeping row of bones rushing toward the target; Sans shrugs one hand out. */
    static Attack boneSweep() {
        return new Attack("bone_sweep", 70) {
            public boolean usable(UtMob m) { return m.hpFrac() >= .12f; }
            public void tick(AttackCtx c, int t) {
                c.face(c.tpos());
                if (t < 40 && t % 4 == 0) {
                    Vec3 f = c.fwd(), r = c.right();
                    for (int k = -3; k <= 3; k++) {
                        if (k == (t / 4) % 7 - 3) continue; // moving gap
                        Vec3 at = c.pos().add(f.scale(2.5)).add(r.scale(k * .9)).add(0, .6, 0);
                        c.proj().at(at).vel(f.scale(.9)).box(.2f, 1.1f, .2f).color(0xffffff).dmg(4).warn(6).life(40).spawn();
                    }
                    c.sound(SoundEvents.SKELETON_SHOOT, .8f, 1.5f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = Math.min(1, t / 6f);
                p.r("armR", -1.4f * s, 0, -.3f * s).r("body", 0, .2f * s, 0).o("head", 0, -.5f * s, 0);
            }
        };
    }
    /** Blasters appear in an arc around the target one after another, then all fire. */
    static Attack blasterRing() {
        return new Attack("blaster_ring", 90) {
            public boolean usable(UtMob m) { return m.hpFrac() >= .12f; }
            public void tick(AttackCtx c, int t) {
                if (t < 30 && t % 5 == 0) {
                    double a = (t / 5) * Math.PI / 3 + c.rnd.nextDouble() * .3;
                    Vec3 at = c.tpos().add(Math.cos(a) * 6, 2.2 + c.rnd.nextDouble() * 1.5, Math.sin(a) * 6);
                    Fx.blaster(c, at, c.tpos().subtract(at), 32 - t, 14, 7, .9f);
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = Math.min(1, t / 8f);
                p.r("armL", -2.6f * s, 0, .3f * s).r("armR", -2.6f * s, 0, -.3f * s).o("body", 0, 1f * s, 0).r("head", -.3f * s, 0, 0);
                sansEye(p, s);
            }
        };
    }
    /** Blue-soul gravity: target is yanked to the floor, bones rise from below in a line. */
    static Attack gravitySlam() {
        return new Attack("gravity_slam", 80) {
            public boolean usable(UtMob m) { return m.hpFrac() >= .12f; }
            public void tick(AttackCtx c, int t) {
                if (t == 8) { c.dash(Vec3.ZERO); c.target.setDeltaMovement(0, -1.4, 0); c.target.hurtMarked = true; c.sound(SoundEvents.ANVIL_LAND, 1, .6f); }
                if (t >= 12 && t < 56 && t % 6 == 0) {
                    Vec3 base = c.target.position().add(c.rnd.nextGaussian() * 2, -1.2, c.rnd.nextGaussian() * 2);
                    c.proj().at(base).vel(new Vec3(0, .45, 0)).box(.25f, 1.6f, .25f).color(0xffffff).dmg(5).warn(8).life(16).spawn();
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                float s = t < 8 ? t / 8f : 1 - Math.min(1, (t - 8) / 14f);
                p.r("armR", -1.2f * s, 0, -.2f).o("armR", 0, 1f * s, 0).r("head", .2f * s, 0, 0);
                sansEye(p, s);
            }
        };
    }
    /** Two walls of bones with a gap closing in from either side. */
    static Attack boneGaps() {
        return new Attack("bone_gaps", 76) {
            public boolean usable(UtMob m) { return m.hpFrac() >= .12f; }
            public void tick(AttackCtx c, int t) {
                if (t % 20 == 0 && t < 60) {
                    Vec3 r = c.right(), f = c.fwd(); int gap = c.rnd.nextInt(5) - 2;
                    for (int s = -1; s <= 1; s += 2) for (int k = -3; k <= 3; k++) {
                        if (Math.abs(k - gap) <= 1) continue;
                        Vec3 at = c.tpos().add(r.scale(s * 7)).add(f.scale(k * .8)).add(0, -.6, 0);
                        c.proj().at(at).vel(r.scale(-s * .55)).box(.2f, 2.0f, .2f).color(0xffffff).dmg(4).warn(10).life(30).spawn();
                    }
                }
            }
            public void pose(Pose p, float t, UtMob m) {
                p.r("armL", -1.0f, 0, .3f).r("armR", -1.0f, 0, -.3f).o("body", 0, (float) Math.sin(t * .6) * .3f, 0);
            }
        };
    }

    // =================================================================== FLOWEY
    static Vec3 petalSpot(AttackCtx c, int i) {
        Vec3 r = new Vec3(1, 0, 0); double a = Math.PI * (.2 + .15 * i);
        return c.pos().add(r.scale(Math.cos(a) * 1.3)).add(0, 1.2 + Math.sin(a) * 1.0, 0);
    }
    /** "Friendliness pellets": five pellets hover around him, then drift at you. */
    static Attack floweyPellets() {
        return atk("friendliness_pellets", 70, null, (c, t) -> {
            if (t == 6) { for (int i = 0; i < 5; i++) dot(c, petalSpot(c, i), Vec3.ZERO, .25f, 0xffffff, 0).life(24).spawn(); say(c, "* Down here, LOVE is shared through little white... \"friendliness pellets.\""); }
            if (t == 30) { for (int i = 0; i < 5; i++) { Vec3 at = petalSpot(c, i); dot(c, at, c.aim(at).scale(.28), .25f, 0xffffff, 3).life(60).spawn(); } c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, 1.6f); }
        }, (p, t, m) -> {
            float s = env(t, 6, 70, 10), k = ramp(t, 26, 4) * (1 - ramp(t, 34, 10));
            p.r("head", -.35f * k, 0, (float) Math.sin(t * .3f) * .15f * s).o("head", 0, s, 0).r("body", 0, 0, (float) Math.sin(t * .2f) * .1f);
        });
    }
    /** Five-bullet spread that homes slightly (each volley re-aims). */
    static Attack floweyFiveSpread() {
        return atk("five_bullet_spread", 70, null, (c, t) -> {
            if (t >= 12 && t < 52 && t % 14 == 12) {
                Vec3 o = c.pos().add(0, 1.2, 0), a = c.aim(o), side = new Vec3(-a.z, 0, a.x);
                for (int i = -2; i <= 2; i++) dot(c, o, a.add(side.scale(i * .18)).normalize().scale(.32), .25f, 0xffffff, 3).warn(4).life(50).spawn();
                c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, .8f, 1.3f);
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 70, 10), k = (t - 12) % 14 < 4 ? 1 : 0;
            p.r("head", -.25f * k * s, 0, 0).o("head", 0, .8f * s, -.8f * k * s).r("petalL", 0, 0, .3f * s).r("petalR", 0, 0, -.3f * s);
        });
    }
    /** Rows of bullets sweeping in from outside the board's sides (pre-Asriel Flowey). */
    static Attack floweySideRows() {
        return atk("vertical_bullet_rows", 90, null, (c, t) -> {
            Board b = board(c, t);
            if (t >= 10 && t < 70 && t % 15 == 10) {
                int from = (t / 15) % 2 == 0 ? -1 : 1, gap = c.rnd.nextInt(5) - 2;
                for (int k = -3; k <= 3; k++) for (int y = 0; y < 3; y++) {
                    if (k == gap || k == gap + 1) continue;
                    dot(c, b.at(from * 6, .4 + y * .7, k * .7), b.r().scale(-from * .3), .3f, 0xffffff, 3).warn(6).life(44).spawn();
                }
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 90, 10);
            p.r("body", 0, (float) Math.sin(t * .15f) * .5f * s, 0).o("head", 0, s, 0);
        });
    }
    /** The inescapable ring of bullets that slowly closes in. "DIE." */
    static Attack floweyRing() {
        return atk("ring_of_bullets", 100, m -> m.hpFrac() < .6f, (c, t) -> {
            if (t == 4) {
                Vec3 o = c.tpos(); say(c, "* DIE.");
                for (int i = 0; i < 24; i++) {
                    double a = i * Math.PI * 2 / 24; Vec3 at = o.add(Math.cos(a) * 4, 0, Math.sin(a) * 4);
                    dot(c, at, o.subtract(at).scale(1 / 70.0), .3f, 0xffffff, 6).warn(20).life(80).spawn();
                }
                c.sound(SoundEvents.WITHER_AMBIENT, .7f, 1.6f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 100, 10), shake = (float) Math.sin(t * 2.1f) * .08f * s;
            p.o("head", 0, 1.5f * s, -1 * s).r("head", .2f * s, shake, 0).o("eyeL", 0, -.4f * s, 0).o("eyeR", 0, -.4f * s, 0).o("mouth", 0, -.5f * s, 0)
                .r("petalT", .4f * s, 0, 0).r("petalBL", 0, 0, .5f * s).r("petalBR", 0, 0, -.5f * s);
        });
    }

    // =================================================================== TORIEL
    static final Predicate<UtMob> MERCY_OFF = m -> m.getTarget() == null || m.getTarget().getHealth() > 4;
    static Anim torielCast = (p, t, m) -> {
        float s = Math.min(1, t / 8f);
        p.r("armL", -1.6f * s, 0, .5f * s).r("armR", -1.6f * s, 0, -.5f * s).r("head", -.15f * s, 0, 0);
    };
    /** Paw sweeps an arc leaving fireballs that then launch at you; a mirrored second paw in later rounds. */
    static Attack torielPawSweep() {
        return atk("paw_sweep", 100, MERCY_OFF, (c, t) -> {
            Board b = board(c, t);
            if (low(c)) return;
            boolean two = c.me.hpFrac() < .6f;
            IntFunction<Vec3> paw1 = k -> { double a = Math.PI * (1 - ramp(k, 6, 36)); return b.at(3.2 * Math.cos(a), 1.2 + 1.5 * Math.sin(a), -.5); };
            IntFunction<Vec3> paw2 = k -> { double a = Math.PI * ramp(k, 6, 36); return b.at(3.2 * Math.cos(a), .9 - .5 * Math.sin(a), .8); };
            step(c, t, 6, 42, 6, paw1, p -> p.box(.9f, .7f, .9f).color(0xfafafa).dmg(4).spawn());
            if (two) step(c, t, 6, 42, 6, paw2, p -> p.box(.9f, .7f, .9f).color(0xfafafa).dmg(4).spawn());
            if (t >= 6 && t <= 42 && t % 6 == 0) {
                dot(c, paw1.apply(t), Vec3.ZERO, .35f, FIRE, 0).life(52 - t).spawn(); c.particle(ParticleTypes.FLAME, paw1.apply(t), 3, .1, .01);
                if (two) dot(c, paw2.apply(t), Vec3.ZERO, .35f, FIRE, 0).life(52 - t).spawn();
            }
            if (t == 52) {
                for (int k = 6; k <= 42; k += 6) for (int w = 0; w < (two ? 2 : 1); w++) {
                    Vec3 at = (w == 0 ? paw1 : paw2).apply(k);
                    dot(c, at, c.aim(at).scale(.26).add(c.rnd.nextGaussian() * .03, 0, c.rnd.nextGaussian() * .03), .35f, FIRE, 3).life(50).spawn();
                }
                c.sound(SoundEvents.BLAZE_SHOOT, 1, 1.2f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 100, 10), u = ramp(t, 6, 36);
            p.r("armR", -2.2f * s, 0, (-1.2f + 2.4f * u) * s).r("body", 0, (-.3f + .6f * u) * s, 0);
            if (m.hpFrac() < .6f) p.r("armL", -1.2f * s, 0, (1.2f - 2.4f * u) * s);
            if (t > 48 && t < 58) p.r("armR", -.6f, 0, 0).r("armL", -.6f, 0, 0);
        });
    }
    /** Crisscrossing double helix of fire raining down, holes to slip through, safe top corners. */
    static Attack torielFireWaves() {
        return atk("fire_waves", 100, MERCY_OFF, (c, t) -> {
            Board b = board(c, t);
            if (low(c)) return;
            if (t >= 10 && t < 80 && t % 3 == 1 && (t / 3) % 6 != 0) {
                double sw = 2.6 * Math.sin(t * .09);
                for (int s = -1; s <= 1; s += 2) for (int z = -2; z <= 2; z++)
                    dot(c, b.at(s * sw, 5, z * .9), new Vec3(0, -.22, 0), .35f, FIRE, 4).life(26).spawn();
            }
            if (c.me.hpFrac() < .5f && t == 10) for (int s = -1; s <= 1; s += 2) for (int y = 0; y < 3; y++)
                dot(c, b.at(s * 3.8, .5 + y * .9, 0), Vec3.ZERO, .4f, FIRE, 4).warn(10).life(80).spawn();
            if (t % 9 == 1 && t < 80) c.sound(SoundEvents.FIRECHARGE_USE, .5f, 1.3f);
        }, (p, t, m) -> {
            torielCast.run(p, t, m);
            float w = (float) Math.sin(t * .09f) * env(t, 8, 100, 12);
            p.r("armL", -1f, 0, .5f * w).r("armR", -1f, 0, -.5f * w);
        });
    }
    /** Thick, non-sweeping streams: too dense to pass through, stay between them. */
    static Attack torielFireHelix() {
        return atk("fire_helix", 100, MERCY_OFF, (c, t) -> {
            Board b = board(c, t);
            if (low(c)) return;
            if (t >= 10 && t < 80 && t % 2 == 0) {
                double tw = .5 * Math.sin(t * .25);
                for (int s = -1; s <= 1; s += 2) for (int z = -2; z <= 2; z++)
                    dot(c, b.at(s * 2.2 + tw * s, 5, z * .9), new Vec3(0, -.24, 0), .6f, FIRE, 4).life(24).spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 8, 100, 12);
            p.r("armL", -2.8f * s, 0, .2f).r("armR", -2.8f * s, 0, -.2f).o("body", 0, .4f * s, 0);
        });
    }
    /** "Faltering": fireballs scatter down but swerve away from you. Used once you are nearly dead. */
    static Attack torielFaltering() {
        return atk("faltering", 80, m -> !MERCY_OFF.test(m), (c, t) -> {
            Board b = board(c, t);
            if (t >= 8 && t < 60 && t % 3 == 0) {
                Vec3 o = c.target.position(); double x = (c.rnd.nextDouble() - .5) * 8, z = (c.rnd.nextDouble() - .5) * 4;
                Vec3 at = b.at(x, 5, z); Vec3 away = new Vec3(at.x - o.x, 0, at.z - o.z);
                if (away.length() < 1.5) at = at.add(away.lengthSqr() < 1e-3 ? b.r().scale(1.5) : away.normalize().scale(1.5));
                dot(c, at, new Vec3(0, -.2, 0), .35f, FIRE, 0).life(26).spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 80, 10);
            p.r("head", .4f * s, 0, 0).r("armL", -.6f * s, 0, .2f).r("armR", -.6f * s, 0, -.2f).o("body", 0, -.5f * s, 0);
        });
    }

    // =================================================================== RUINS MONSTERS
    /** Froggit/Migosp: five flies drop in one after another, hover, then dart at the SOUL. */
    static Attack flySwarm(String id, Predicate<UtMob> when) {
        return atk(id, 80, when, (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 5; i++) {
                int t0 = i * 4; Vec3 hover = b.at((i - 2) * 1.1, 2.2 + .3 * (i % 2), -1);
                int ii = i;
                step(c, t, t0, t0 + 10, 10, k -> k == t0 ? hover.add(0, 2, 0) : hover, p -> p.box(.18f, .18f, .18f).color(0x222222).dmg(0).spawn());
                if (t == t0 + 10) dot(c, hover, Vec3.ZERO, .18f, 0x222222, 0).life(40 - t0 - 10).spawn();
                if (t == 40 + ii * 3) dot(c, hover, c.aim(hover).scale(.32), .18f, 0x222222, 2).life(40).spawn();
            }
            if (t % 6 == 0 && t < 50) c.sound(SoundEvents.BEE_LOOP, .3f, 2f);
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 10);
            p.r("head", -.3f * s, 0, 0).o("body", 0, (float) Math.abs(Math.sin(t * .3f)) * .5f * s, 0);
        });
    }
    /** A little frog sits in the corner, then jumps across with random force. */
    static Attack frogLeap() {
        return atk("frog_leap", 60, null, (c, t) -> {
            Board b = board(c, t);
            Vec3 start = b.at(3, .2, (hr(c.me.getId()) - .5) * 3);
            if (t == 2) dot(c, start, Vec3.ZERO, .45f, 0xf0f0f0, 0).life(14).spawn();
            if (t == 16) {
                double force = .3 + c.rnd.nextDouble() * .2;
                dot(c, start, b.r().scale(-.22).add(0, force, 0), .45f, 0xf0f0f0, 2).gravity(.025f).life(36).spawn();
                c.sound(SoundEvents.FROG_LONG_JUMP, 1, 1.4f);
            }
        }, (p, t, m) -> {
            float crouch = env(t, 10, 16, 2), jump = env(t - 16, 2, 14, 8);
            p.o("body", 0, -1.2f * crouch + 2.5f * jump, 0).r("legL", .8f * jump, 0, 0).r("legR", .8f * jump, 0, 0);
        });
    }
    /** Rare variant: flies drift smoothly down without targeting. */
    static Attack flyDrift() {
        return atk("fly_drift", 70, m -> m.getRandom().nextFloat() < .15f, (c, t) -> {
            Board b = board(c, t);
            if (t >= 4 && t < 44 && t % 4 == 0) dot(c, b.at((c.rnd.nextDouble() - .5) * 6, 4, (c.rnd.nextDouble() - .5) * 3),
                new Vec3(c.rnd.nextGaussian() * .01, -.12, 0), .18f, 0x222222, 2).life(40).spawn();
        }, (p, t, m) -> p.r("head", -.2f * env(t, 6, 70, 10), 0, 0));
    }
    /** Ring of moths circles the board, breathing in and out. */
    static Attack mothRing() {
        return atk("moth_ring", 100, null, (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 8; i++) {
                int ii = i;
                step(c, t, 4, 88, 6, k -> {
                    double a = ii * Math.PI / 4 + k * .05, r = 2.4 + 1.2 * Math.sin(k * .12);
                    return b.at(Math.cos(a) * r, 1 + .3 * Math.sin(k * .3 + ii), Math.sin(a) * r);
                }, p -> p.box(.3f, .15f, .3f).color(0xe8f0e8).dmg(t < 14 ? 0 : 2).spawn());
            }
        }, (p, t, m) -> {
            float f = (float) Math.sin(t * .8f) * .6f;
            p.r("wingL", 0, f, 0).r("wingR", 0, -f, 0).o("body", 0, (float) Math.sin(t * .2f), 0);
        });
    }
    /** Two vertical lines of moths rise from below on either side of you, following your SOUL. */
    static Attack mothColumns() {
        return atk("moth_columns", 90, null, (c, t) -> {
            if (t >= 6 && t < 70 && t % 4 == 2) {
                Vec3 o = c.target.position(), r = c.right();
                for (int s = -1; s <= 1; s += 2) for (int z = -1; z <= 1; z++)
                    dot(c, o.add(r.scale(s * 1.4)).add(c.fwd().scale(z * .6)), new Vec3(0, .14, 0), .2f, 0xe8f0e8, 2).warn(4).life(22).spawn();
            }
        }, (p, t, m) -> {
            float f = (float) Math.sin(t * 1.1f) * .7f, s = env(t, 6, 90, 10);
            p.r("wingL", 0, f, 0).r("wingR", 0, -f, 0).r("head", .3f * s, 0, 0);
        });
    }
    /** Pellets fall from the top in a zigzag. */
    static Attack moldZigzag() {
        return atk("zigzag_pellets", 90, null, (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 6; i++) {
                int t0 = 8 + i * 9; double x0 = (hr(i) - .5) * 5, z0 = (hr(i + 9) - .5) * 3;
                step(c, t, t0, t0 + 42, 6, k -> {
                    double u = k - t0, tri = Math.abs(((u / 12) % 2 + 2) % 2 - 1) * 2 - 1;
                    return b.at(x0 + tri * .9, 5 - u * .12, z0);
                }, p -> p.box(.3f, .3f, .3f).color(0x9bdc5b).dmg(2).spawn());
            }
        }, (p, t, m) -> p.o("body", (float) Math.sin(t * .5f) * .5f, (float) Math.abs(Math.sin(t * .25f)), 0).r("head", 0, (float) Math.sin(t * .3f) * .3f, 0));
    }
    /** Pellets fall a short way, then burst into rings of smaller pellets. */
    static Attack moldBurst() {
        return atk("burst_pellets", 80, null, (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 4; i++) {
                int t0 = 6 + i * 12; Vec3 top = b.at((hr(i + 3) - .5) * 5, 5, (hr(i + 7) - .5) * 3), pop = top.add(0, -3.3, 0);
                if (t == t0) dot(c, top, new Vec3(0, -.22, 0), .4f, 0x9bdc5b, 2).life(15).spawn();
                if (t == t0 + 15) {
                    for (int k = 0; k < 8; k++) { double a = k * Math.PI / 4; dot(c, pop, new Vec3(Math.cos(a) * .14, -.04, Math.sin(a) * .14), .2f, 0xb5ea7c, 2).life(30).spawn(); }
                    c.particle(ParticleTypes.ITEM_SLIME, pop, 6, .2, .05); c.sound(SoundEvents.SLIME_SQUISH, .7f, 1.4f);
                }
            }
        }, (p, t, m) -> { float sq = (float) Math.sin(t * .4f); p.o("body", 0, -.5f * sq, 0).o("head", 0, sq, 0); });
    }
    /** Five spheres per side bounce around the board, speeding up. */
    static Attack looxBouncers() {
        return atk("bouncing_spheres", 100, null, (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 10; i++) {
                int ii = i, from = i < 5 ? -1 : 1, t0 = 6 + (i % 5) * 4; double z = (i % 5 - 2) * .9;
                step(c, t, t0, 92, 4, k -> {
                    double u = k - t0, spd = .06 + u * .0012, x = from * 4.5 - from * u * spd;
                    x = ((x + 4.5) % 18 + 18) % 18; x = x > 9 ? 18 - x : x; // bounce between walls
                    return b.at(x - 4.5, .3 + 1.6 * Math.abs(Math.sin(u * .12 + ii)), z);
                }, p -> p.box(.35f, .35f, .35f).color(0xd22020).dmg(t < t0 + 6 ? 0 : 2).spawn());
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 100, 10);
            p.o("pupil", (float) Math.sin(t * .3f) * 1.2f * s, (float) Math.cos(t * .23f) * .8f * s, 0).o("body", 0, (float) Math.abs(Math.sin(t * .25f)) * s, 0);
        });
    }
    /** Worms of spheres wiggle across from either side and vanish at the far wall. */
    static Attack looxWorms() {
        return atk("sphere_worms", 90, null, (c, t) -> {
            Board b = board(c, t);
            for (int w = 0; w < 4; w++) for (int j = 0; j < 5; j++) { final int j0 = j;
                int from = w % 2 == 0 ? -1 : 1, t0 = 6 + w * 12 + j * 3; double z = (w - 1.5) * 1.1;
                step(c, t, t0, t0 + 54, 6, k -> { double u = k - t0; return b.at(from * (4.5 - u * .17), 1 + .5 * Math.sin(u * .35), z + .4 * Math.cos(u * .35)); },
                    p -> p.box(.3f, .3f, .3f).color(j0 == 0 ? 0xd22020 : 0xdcdce6).dmg(2).spawn());
            }
        }, (p, t, m) -> p.r("body", 0, (float) Math.sin(t * .2f) * .3f, (float) Math.sin(t * .4f) * .1f).o("pupil", (float) Math.sin(t * .2f), 0, 0));
    }
    /** Vegetables fall and bounce off the floor. */
    static Attack veggieBounce() {
        return atk("veggie_bounce", 90, null, (c, t) -> {
            Board b = board(c, t);
            int[] cols = {0xf08a24, 0xd22a2a, 0x7a3fa0, 0x3fae3f};
            for (int i = 0; i < 6; i++) {
                int t0 = 6 + i * 8, col = cols[i % 4]; double x0 = (hr(i * 3) - .5) * 6, z0 = (hr(i * 5) - .5) * 3, dx = (hr(i * 7) - .5) * .08;
                step(c, t, t0, t0 + 54, 4, k -> {
                    double u = k - t0, y = u < 20 ? 5 - 5 * Math.pow(u / 20, 2) : 1.6 * Math.abs(Math.sin((u - 20) * Math.PI / 17));
                    return b.at(x0 + dx * u, y + .2, z0);
                }, p -> p.box(.35f, .35f, .35f).color(col).dmg(2).spawn());
            }
        }, (p, t, m) -> { float s = env(t, 6, 90, 10); p.r("armL", -2.2f * s, 0, .3f).r("armR", -2.2f * s, 0, -.3f).r("head", 0, t * .2f * s, 0); });
    }
    /** Carrots drop straight down onto marked spots. */
    static Attack carrotDrop() {
        return atk("carrot_drop", 80, null, (c, t) -> {
            Board b = board(c, t);
            if (t >= 6 && t < 60 && t % 4 == 2) {
                Vec3 at = b.at((c.rnd.nextDouble() - .5) * 7, 0, (c.rnd.nextDouble() - .5) * 4);
                mark(c, at, .5f, .5f, 0xf08a24, 18);
                c.proj().at(at.add(0, 5, 0)).vel(new Vec3(0, -.27, 0)).box(.2f, .2f, .6f).color(0xf08a24).dmg(2).life(18).spawn();
            }
        }, (p, t, m) -> { float s = env(t, 6, 80, 10); p.o("body", 0, (float) Math.abs(Math.sin(t * .4f)) * s, 0).r("leafL", 0, 0, .4f * s).r("leafR", 0, 0, -.4f * s); });
    }
    /** "Dinner": green vegetables rain down; catch them to heal. */
    static Attack greenDinner() {
        return atk("green_vegetables", 80, null, (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 8; i++) {
                int t0 = 6 + i * 6; Vec3 land = b.at((hr(i + 40) - .5) * 5, .2, (hr(i + 50) - .5) * 3);
                if (t == t0) dot(c, land.add(0, 5, 0), new Vec3(0, -.2, 0), .35f, 0x3fe03f, 0).life(24).spawn();
                if (t == t0 + 24 && c.target.position().distanceTo(land) < 1.1) { c.target.heal(2); c.particle(ParticleTypes.HEART, land.add(0, 1, 0), 3, .2, .01); }
            }
        }, (p, t, m) -> { float s = env(t, 6, 80, 10); p.r("armL", -1.8f * s, 0, .6f).r("armR", -1.8f * s, 0, -.6f); });
    }
    /** Cockroaches fly up from both sides; the middle stays clear. */
    static Attack roachRise() {
        return atk("roach_rise", 90, null, (c, t) -> {
            Board b = board(c, t);
            if (t >= 6 && t < 70 && t % 3 == 0) {
                int s = c.rnd.nextBoolean() ? 1 : -1; double x = s * (1.2 + c.rnd.nextDouble() * 2.6);
                dot(c, b.at(x, 0, (c.rnd.nextDouble() - .5) * 4), new Vec3(0, .16, 0).add(b.r().scale(s * .015)), .25f, 0x3a2a1a, 2).warn(3).life(24).spawn();
            }
        }, (p, t, m) -> { float s = env(t, 6, 90, 10); p.r("armL", -2.5f * s, 0, .4f).r("armR", -2.5f * s, 0, -.4f).r("antL", 0, 0, (float) Math.sin(t) * .3f); });
    }
    /** Alone, Migosp just dances with a tiny cockroach. Harmless. */
    static Attack lonelyDance() {
        return atk("lonely_dance", 70, m -> m.level().getEntitiesOfClass(UtMob.class, m.getBoundingBox().inflate(10), e -> e != m).isEmpty(), (c, t) -> {
            Board b = board(c, t);
            step(c, t, 4, 64, 6, k -> b.at(Math.sin(k * .2) * 2, .15 + .3 * Math.abs(Math.sin(k * .5)), 0), p -> p.box(.2f, .2f, .2f).color(0x3a2a1a).dmg(0).spawn());
            if (t % 10 == 0) c.particle(ParticleTypes.NOTE, c.eye().add(0, .5, 0), 1, .2, 1);
        }, (p, t, m) -> {
            float w = (float) Math.sin(t * .5f);
            p.r("body", 0, 0, .2f * w).r("armL", -2.5f, 0, .5f * w).r("armR", -2.5f, 0, .5f * w).r("legL", .5f * w, 0, 0).r("legR", -.5f * w, 0, 0);
        });
    }
    /** Tears arc out of Napstablook's eyes, then fall straight. */
    static Attack curvedTears() {
        return atk("curved_tears", 90, null, (c, t) -> {
            if (t >= 8 && t < 70 && t % 3 == 0) {
                int s = (t / 3) % 2 == 0 ? 1 : -1; Vec3 eye = c.eye().add(c.right().scale(s * .25)), a = c.aim(eye).multiply(1, 0, 1);
                dot(c, eye, a.scale(.18 + c.rnd.nextDouble() * .08).add(c.right().scale(s * .1)).add(0, .14, 0), .22f, 0x7fd0ff, 3).gravity(.012f).life(70).spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 10, 90, 10);
            p.o("tearL", 0, -2f * s * ((t % 6) / 6f), 0).o("tearR", 0, -2f * s * (((t + 3) % 6) / 6f), 0).o("body", 0, -.5f * s, 0).r("head", .2f * s, 0, 0);
        });
    }
    /** Turn 2 only: "REALLY NOT FEELIN UP TO IT RIGHT NOW. SORRY." Harmless grey text. */
    static Attack notFeelinIt() {
        return atk("not_feelin_up_to_it", 50, m -> !m.getPersistentData().getBoolean("utNotFeelin"), (c, t) -> {
            if (t == 0) c.me.getPersistentData().putBoolean("utNotFeelin", true);
            if (t == 4) {
                say(c, "REALLY NOT FEELIN UP TO IT RIGHT NOW. SORRY.");
                Board b = board(c, t);
                for (int i = 0; i < 14; i++) c.proj().at(b.at(-3 + i * .45, 2.2 + .1 * Math.sin(i), -1.5)).box(.3f, .4f, .05f).color(0x808080).dmg(0).life(40).spawn();
            }
        }, (p, t, m) -> p.o("body", 0, -1.2f * env(t, 10, 50, 10), 0).r("head", .3f * env(t, 10, 50, 10), 0, 0));
    }
    /** Long wiggly tears zigzag across until above your SOUL, then drop. */
    static Attack wigglyTears() {
        return atk("wiggly_tears", 100, null, (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 6; i++) {
                int t0 = 6 + i * 8; double z = (hr(i + 60) - .5) * 3, x0 = (hr(i + 70) - .5) * 4;
                step(c, t, t0, t0 + 46, 4, k -> {
                    double u = k - t0, tri = (Math.abs(((u / 10) % 2 + 2) % 2 - 1) * 2 - 1) * 3;
                    return u < 26 ? b.at(tri, 3.2, z) : b.at((Math.abs(((26 / 10.0) % 2) - 1) * 2 - 1) * 3, 3.2 - (u - 26) * .18, z);
                }, p -> p.box(.18f, .7f, .18f).color(0x7fd0ff).dmg(3).spawn());
            }
        }, (p, t, m) -> { float s = env(t, 10, 100, 10); p.o("body", (float) Math.sin(t * .3f) * .6f * s, 0, 0).r("head", .2f * s, 0, 0); });
    }

    // =================================================================== SNOWDIN MONSTERS
    /** Top hats pop up on the floor and shoot ice chunks that fall back down. */
    static Attack iceHats() {
        return atk("ice_hat_volley", 90, null, (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 4; i++) {
                Vec3 hat = b.at((i - 1.5) * 2, .25, (hr(i + 80) - .5) * 2.5);
                if (t == 4) { c.proj().at(hat).box(.6f, .5f, .6f).color(0x1c2440).dmg(0).life(70).spawn(); }
                int fire = 16 + i * 10;
                if (t == fire || t == fire + 24) {
                    dot(c, hat.add(0, .4, 0), new Vec3(c.rnd.nextGaussian() * .05, .42, c.rnd.nextGaussian() * .05), .35f, 0xbfefff, 3).gravity(.022f).life(46).spawn();
                    c.sound(SoundEvents.SNOW_GOLEM_SHOOT, .7f, 1.4f);
                }
            }
        }, (p, t, m) -> { float s = env(t, 6, 90, 10), b = (float) Math.abs(Math.sin(t * .4f)); p.o("hat1", 0, 1.5f * b * s, 0).r("armL", -2f * s, 0, .3f).r("armR", -2f * s, 0, -.3f); });
    }
    /** Ice walls close in from both sides with a zigzag lane between; the exact centre is always safe. */
    static Attack iceZigzag() {
        return atk("zigzag_ice_walls", 100, null, (c, t) -> {
            Board b = board(c, t);
            if (t >= 8 && t < 80 && t % 4 == 0) {
                double g = .9 * (Math.abs(((t / 20.0) % 2) - 1) * 2 - 1), gl = g - 1.15, gr = g + 1.15;
                Vec3 dir = b.f().scale(-.3);
                c.proj().at(b.at((gl - 5) / 2, .9, 5)).vel(dir).box((float) (gl + 5), 1.8f, .35f).color(0xbfefff).dmg(3).life(40).spawn();
                c.proj().at(b.at((gr + 5) / 2, .9, 5)).vel(dir).box((float) (5 - gr), 1.8f, .35f).color(0xbfefff).dmg(3).life(40).spawn();
            }
        }, (p, t, m) -> { float s = env(t, 6, 100, 10); p.r("body", 0, (float) Math.sin(t * .15f) * .6f * s, 0).r("armL", -1.5f * s, 0, 0).r("armR", -1.5f * s, 0, 0); });
    }
    /** Jerry has no attack of its own; it just makes everything take longer (slowness) and is gross. */
    static Attack jerryAura() {
        return atk("awkward_presence", 60, null, (c, t) -> {
            if (t == 10) { c.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)); say(c, "* Jerry."); }
            if (t % 8 == 0) { c.particle(ParticleTypes.SNEEZE, c.eye(), 4, .3, .02); c.sound(SoundEvents.GENERIC_EAT, .6f, .7f); }
        }, (p, t, m) -> {
            float w = (float) Math.sin(t * .4f);
            p.r("armL", -.5f, 0, .6f * w).r("armR", -.5f, 0, -.6f * w).o("lips", 0, 0, -.5f * Math.abs(w)).r("body", 0, .2f * w, 0);
        });
    }
    /** Crescent icicles fly at you in straight lines. */
    static Attack crescentVolley() {
        return atk("crescent_volley", 80, null, (c, t) -> {
            if (t >= 10 && t < 60 && t % 8 == 2) {
                Vec3 o = c.eye(), a = c.aim(o), side = new Vec3(-a.z, 0, a.x);
                for (int i = -1; i <= 1; i++) c.proj().at(o.add(side.scale(i * .6))).vel(a.scale(.36)).box(.6f, .12f, .15f).color(0xbfefff).dmg(3).warn(3).life(40).spawn();
                c.sound(SoundEvents.SNOW_GOLEM_SHOOT, .7f, 1.6f);
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 8), k = (t - 2) % 8 < 3 ? 1 : 0;
            p.r("armL", -1.6f * s, 0, .8f * k).r("armR", -1.6f * s, 0, -.8f * k).r("head", -.2f * k, 0, 0);
        });
    }
    /** Crescents lobbed in parabolas (also Doggo's 0.5% Snowdrake easter egg). */
    static Attack crescentArcs(String id, Predicate<UtMob> when) {
        return atk(id, 80, when, (c, t) -> {
            if (t >= 10 && t < 60 && t % 6 == 4) {
                Vec3 o = c.eye(), to = c.target.position().add(c.rnd.nextGaussian(), .5, c.rnd.nextGaussian());
                c.proj().at(o).vel(lob(o, to, 28, .03)).box(.6f, .12f, .15f).color(0xbfefff).dmg(3).gravity(.03f).life(40).spawn();
            }
        }, (p, t, m) -> {
            float s = env(t, 6, 80, 8), k = (float) Math.sin(t * .5f);
            p.r("armL", -2.2f * s, 0, .4f * k).r("armR", -2.2f * s, 0, -.4f * k);
        });
    }
    /** Three presents shuffle on the floor, then rise; one is blue. Later they slide while rising. */
    static Attack presentShuffle() {
        return atk("present_shuffle", 90, null, (c, t) -> {
            Board b = board(c, t);
            int blue = Math.floorMod(c.me.getId(), 3); boolean moving = c.me.hpFrac() < .5f;
            for (int i = 0; i < 3; i++) {
                int ii = i;
                IntFunction<Vec3> shuffle = k -> b.at((ii - 1) * 1.8 * Math.cos(k * .25), .45, (ii - 1) * 1.2 * Math.sin(k * .25));
                step(c, t, 4, 34, 5, shuffle, p -> p.box(.9f, .9f, .9f).color(ii == blue ? 0x3fa7ff : 0xd23a3a).dmg(0).spawn());
                if (t == 34) {
                    Vec3 at = shuffle.apply(34); var p = c.proj().at(at).vel(new Vec3(0, .09, 0).add(moving ? b.r().scale((ii - 1) * .05) : Vec3.ZERO))
                        .box(.9f, .9f, .9f).color(0xd23a3a).dmg(3).life(40);
                    if (ii == blue) p.blue();
                    p.spawn();
                }
            }
        }, (p, t, m) -> { float s = env(t, 6, 90, 10); p.r("head", .3f * (float) Math.sin(t * .5f) * s, 0, 0).r("armL", -1.2f * s, 0, .3f).r("armR", -1.2f * s, 0, -.3f); });
    }
    /** Snowflakes fall in wave patterns. */
    static Attack snowflakeWaves() {
        return atk("snowflake_waves", 90, null, (c, t) -> {
            Board b = board(c, t);
            if (t >= 6 && t < 70 && t % 5 == 1) for (int z = -2; z <= 2; z++) {
                double x = 3 * Math.sin(t * .1 + z * .7);
                c.proj().at(b.at(x, 5, z * .9)).vel(new Vec3(0, -.2, 0)).box(.3f, .3f, .05f).color(0xffffff).dmg(3).life(26).spawn();
            }
            if (t % 10 == 0) c.particle(ParticleTypes.SNOWFLAKE, c.eye().add(0, 1, 0), 6, .5, .02);
        }, (p, t, m) -> { float s = env(t, 6, 90, 10); p.r("head", -.3f * s, (float) Math.sin(t * .2f) * .3f * s, 0); });
    }
    /** Doggo: a short blue knife sweeps through the board, back and forth. If you don't move, he can't see you. */
    static Attack doggoSweep() {
        return atk("blue_knife_sweep", 80, null, (c, t) -> {
            Board b = board(c, t);
            for (int k = 0; k < 3; k++) {
                int t0 = 8 + k * 22, dir = k % 2 == 0 ? 1 : -1;
                step(c, t, t0, t0 + 18, 3, u -> b.at(0, .9, dir * (3 - (u - t0) / 3.0)), p -> p.box(5f, .3f, .3f).blue().dmg(4).spawn());
            }
            if (t % 22 == 8) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1.2f);
        }, (p, t, m) -> {
            float u = ((t - 8) % 22) / 18f, s = env(t, 6, 80, 8);
            p.r("armR", -1.5f * s, (-1.2f + 2.4f * Math.min(1, u)) * s, 0).r("armL", -1.5f * s, (1.2f - 2.4f * Math.min(1, u)) * s, 0).r("head", 0, (float) Math.sin(t * .6f) * .3f, 0);
        });
    }
    /** Lesser Dog: an Annoying-Dog lookalike runs on the spot, then leaps at your SOUL. */
    static Attack pupPounce() {
        return atk("pup_pounce", 70, null, (c, t) -> {
            Board b = board(c, t);
            Vec3 at = b.at(3.5, .25, 0);
            step(c, t, 4, 24, 2, k -> at.add(0, .15 * Math.abs(Math.sin(k)), 0), p -> p.box(.5f, .4f, .6f).color(0xffffff).dmg(0).spawn());
            if (t == 24) { c.proj().at(at).vel(lob(at, c.target.position().add(0, .4, 0), 18, .03)).box(.5f, .4f, .6f).color(0xffffff).dmg(3).gravity(.03f).life(30).spawn(); c.sound(SoundEvents.WOLF_AMBIENT, 1, 1.6f); }
        }, (p, t, m) -> { float s = env(t, 6, 70, 10); p.o("head", 0, 2f * s, 0).r("armR", -1f * s, 0, 0).o("tongue", 0, (float) Math.sin(t) * .4f, 0); });
    }
    /** A blue spear crosses right to left, then a white spear stops mid-board and pulls back. */
    static Attack spearPass() {
        return atk("spear_pass", 90, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 8) for (int z = -3; z <= 3; z++) c.proj().at(b.at(7, 1, z * .7)).vel(b.r().scale(-.4)).box(.15f, .15f, 1.6f).blue().dmg(3).life(36).spawn();
            int from = c.me.getId() % 2 == 0 ? 1 : -1;
            for (int z = -3; z <= 3; z++) {
                int zz = z;
                step(c, t, 46, 82, 4, k -> { double u = k - 46, x = u < 12 ? 7 - u * 7 / 12 : u < 20 ? 0 : (u - 20) * 7 / 16; return b.at(from * x, 1, zz * .7); },
                    p -> p.box(.15f, .15f, 1.6f).color(0xffffff).dmg(3).spawn());
            }
        }, (p, t, m) -> { float s = env(t, 6, 90, 10); p.r("armR", -1.4f * s, .6f * s, 0).r("armL", -.8f * s, 0, .3f).o("head", 0, (float) Math.sin(t * .3f) * s, 0); });
    }
    /** Dogamy & Dogaressa: two axes rise, then swoop down in opposite directions tracing a heart. */
    static Attack axeHeart() {
        return atk("axe_heart", 80, null, (c, t) -> {
            Board b = board(c, t);
            for (int s = -1; s <= 1; s += 2) {
                int ss = s;
                step(c, t, 6, 60, 3, k -> {
                    double u = k - 6;
                    if (u < 10) return b.at(ss * .9, 3 + u * .05, 0);
                    double v = Math.min(1, (u - 10) / 40); return b.at(ss * (.9 + 1.6 * Math.sin(Math.PI * v) - .9 * v), 3.5 - 3.3 * v, 0);
                }, p -> p.box(.8f, .8f, .2f).color(0xd8d8e0).dmg(4).spawn());
            }
            if (t == 16) c.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, .8f);
        }, (p, t, m) -> {
            float s = env(t, 8, 80, 10), d = ramp(t, 16, 30);
            p.r("armR", (-2.8f + 2.4f * d) * s, 0, -.2f).r("body", -.2f * d * s, 0, 0);
        });
    }
    /** Dogamy barks a ring of eight hearts (four blue) over to Dogaressa. Needs both. */
    static Attack heartRingBark() {
        return atk("heart_ring_bark", 90, m -> near(m, m.def.id().equals("dogamy") ? "dogaressa" : "dogamy"), (c, t) -> {
            Board b = board(c, t);
            for (int i = 0; i < 8; i++) {
                int ii = i;
                step(c, t, 10, 70, 4, k -> {
                    double u = k - 10, a = ii * Math.PI / 4 + u * .1;
                    return b.at(-4.5 + u * .15 + Math.cos(a) * 1.3, 1 + Math.sin(a) * 1.3 * .6, Math.sin(a) * .8);
                }, p -> { p.box(.35f, .35f, .2f).dmg(3); if (ii % 2 == 0) p.blue(); else p.color(0xffffff); p.spawn(); });
            }
            if (t == 10) c.sound(SoundEvents.WOLF_AMBIENT, 1, .9f);
        }, (p, t, m) -> { float s = env(t, 6, 90, 10), k = t > 8 && t < 16 ? 1 : 0; p.r("head", -.4f * k, 0, 0).o("head", 0, 0, -1f * k).r("armL", -1f * s, 0, .3f); });
    }
    /** Dogamy alone: one feeble heart bark toward the floor. */
    static Attack dogamyLonelyBark() {
        return atk("lonely_bark", 60, m -> !near(m, "dogaressa"), (c, t) -> {
            Board b = board(c, t);
            if (t == 20) { Vec3 at = b.at(-3, 2, 0); c.proj().at(at).vel(lob(at, c.target.position(), 30, .01)).box(.4f, .4f, .2f).color(0xffffff).dmg(3).gravity(.01f).life(40).spawn(); c.sound(SoundEvents.WOLF_WHINE, 1, .8f); }
        }, (p, t, m) -> p.r("head", .4f * env(t, 10, 60, 10), 0, 0).o("body", 0, -.6f * env(t, 10, 60, 10), 0));
    }
    /** Dogaressa alone: two axes tear across the board, rising and dropping fast. */
    static Attack dogaressaRapidAxes() {
        return atk("rapid_axes", 80, m -> !near(m, "dogamy"), (c, t) -> {
            Board b = board(c, t);
            for (int a = 0; a < 2; a++) {
                int from = a == 0 ? -1 : 1; double z = a == 0 ? -.8 : .8;
                step(c, t, 8 + a * 10, 68 + a * 10, 2, k -> { double u = k - 8; return b.at(from * (5 - u * .17), .4 + 1.6 * Math.abs(Math.sin(u * .3)), z); },
                    p -> p.box(.8f, .8f, .2f).color(0xd8d8e0).dmg(4).spawn());
            }
        }, (p, t, m) -> { float w = (float) Math.sin(t * .6f) * env(t, 6, 80, 10); p.r("armR", -2f + w, 0, 0).r("body", 0, .3f * w, 0); });
    }
    /** Greater Dog's dog-faced spear flies across, flickering blue/white. */
    static Attack flickerSpear() {
        return atk("flicker_spear", 90, null, (c, t) -> {
            Board b = board(c, t);
            for (int pass = 0; pass < 2; pass++) {
                int t0 = 8 + pass * 36;
                if (t >= t0 && t < t0 + 32 && (t - t0) % 4 == 0) {
                    boolean blue = ((t - t0) / 4) % 2 == 0; double x = 6 - (t - t0) * .38;
                    for (int z = -3; z <= 3; z++) {
                        var p = c.proj().at(b.at(x, 1, z * .7)).vel(b.r().scale(-.38)).box(.15f, .15f, 2f).dmg(4).life(4);
                        if (blue) p.blue(); else p.color(0xffffff);
                        p.spawn();
                    }
                    c.proj().at(b.at(x - 1.2, 1, 0)).vel(b.r().scale(-.38)).box(.45f, .45f, .45f).color(0xfafafa).dmg(0).life(4).spawn();
                }
            }
        }, (p, t, m) -> { float s = env(t, 6, 90, 10); p.r("armR", -1.4f * s, .5f * (float) Math.sin(t * .2f), 0).o("head", 0, 1f * s, 0); });
    }
    /** A little dog dozes at the bottom; if you move, it wakes up and fires "BARK" at you. */
    static Attack sleepingBark() {
        return atk("sleeping_bark", 100, null, (c, t) -> {
            Board b = board(c, t);
            Vec3 dog = b.at(0, .2, 1.5);
            if (t == 4) c.proj().at(dog).box(.6f, .35f, .8f).color(0xffffff).dmg(0).life(90).spawn();
            if (t % 15 == 0) c.particle(ParticleTypes.POOF, dog.add(0, .6, 0), 1, .05, .01);
            boolean mv = moving(c);
            if (t > 12 && t < 90 && mv && t % 6 == 0) {
                Vec3 from = dog.add(0, .4, 0), a = c.aim(from), side = new Vec3(-a.z, 0, a.x);
                for (int i = 0; i < 4; i++) c.proj().at(from.add(side.scale((i - 1.5) * .35))).vel(a.scale(.3)).box(.28f, .4f, .05f).color(0xffffff).dmg(3).life(30).spawn();
                c.sound(SoundEvents.WOLF_AMBIENT, 1, 1.5f);
            }
        }, (p, t, m) -> { float s = env(t, 6, 100, 10); p.r("head", .3f * s, 0, 0).r("armR", -.4f * s, 0, 0); });
    }

    // =================================================================== PAPYRUS
    static Anim papyPose(float arm) {
        return (p, t, m) -> {
            float s = env(t, 6, 999, 1), w = (float) Math.sin(t * .4f) * s;
            p.r("armR", -arm * s, 0, -.3f * s).r("armL", -.3f * s, 0, .3f + .3f * w).o("body", 0, .3f * Math.abs(w), 0).r("scarfTail", .4f * w, 0, 0);
        };
    }
    /** Turn 1: blue bones scroll by, then "YOU'RE BLUE NOW!" - gravity slam and a small white bone to jump. */
    static Attack papyBlueAttack() {
        return atk("blue_attack", 90, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 6 || t == 14 || t == 22) row(c, b, -1, 1.2f, false, .35, 3, true);
            if (t == 44) { say(c, "YOU'RE BLUE NOW. THAT'S MY ATTACK!"); c.target.setDeltaMovement(0, -1.4, 0); c.target.hurtMarked = true; c.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1, .8f); }
            if (t == 56) row(c, b, -1, .45f, false, .35, 3, false);
        }, (p, t, m) -> { papyPose(1.4f).run(p, t, m); float k = env(t - 38, 3, 14, 4); p.r("armR", -1.2f * k, 0, 0).r("head", -.3f * k, 0, 0); });
    }
    /** Turn 2: a series of small bones, then a medium one. */
    static Attack papySeries() {
        return atk("bone_series", 80, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 8 || t == 18 || t == 28 || t == 38) row(c, b, -1, .45f, false, .35, 3, false);
            if (t == 52) row(c, b, -1, .8f, false, .35, 3, false);
        }, papyPose(1.2f));
    }
    /** Turn 3: one small, three medium, three large. */
    static Attack papyStairs() {
        return atk("bone_stairs", 90, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 8) row(c, b, -1, .45f, false, .35, 3, false);
            if (t == 22 || t == 30 || t == 38) row(c, b, -1, .8f, false, .35, 3, false);
            if (t == 54 || t == 62 || t == 70) row(c, b, -1, 1.05f, false, .35, 3, false);
        }, papyPose(1.5f));
    }
    /** Turn 4: large bone, one dangling from the ceiling, three small, a medium and another dangling large one. */
    static Attack papyDangling() {
        return atk("dangling_bones", 100, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 8) row(c, b, -1, 1.05f, false, .35, 3, false);
            if (t == 22) row(c, b, -1, 1.5f, true, .35, 3, false);
            if (t == 34 || t == 40 || t == 46) row(c, b, -1, .45f, false, .35, 3, false);
            if (t == 58) row(c, b, -1, .8f, false, .35, 3, false);
            if (t == 72) row(c, b, -1, 1.5f, true, .35, 3, false);
        }, papyPose(1.0f));
    }
    /** Turn 5: three large, a blue bone, then a fast small bone from the opposite side. */
    static Attack papyBlueRush() {
        return atk("blue_bone_rush", 90, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 8 || t == 16 || t == 24) row(c, b, -1, 1.05f, false, .35, 3, false);
            if (t == 38) row(c, b, -1, 2.4f, false, .35, 3, true);
            if (t == 56) row(c, b, 1, .45f, false, .6, 3, false);
        }, papyPose(1.6f));
    }
    /** Turn 6: a narrow opening to jump through, a dangling bone from the right, then 8 bones forming a hump. */
    static Attack papyHump() {
        return atk("bone_hump", 110, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 8) { row(c, b, -1, .5f, false, .35, 3, false); row(c, b, -1, .7f, true, .35, 3, false); }
            if (t == 26) row(c, b, 1, 1.5f, true, .35, 3, false);
            float[] hump = {.3f, .5f, .7f, .9f, .9f, .7f, .5f, .3f};
            for (int i = 0; i < 8; i++) if (t == 44 + i * 3) row(c, b, -1, hump[i], false, .35, 3, false);
        }, papyPose(1.3f));
    }
    /** Turn 7: pairs with narrow openings (low then high), a blue bone + large bone from the right, openings from both sides. */
    static Attack papyNarrow() {
        return atk("narrow_openings", 120, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 8 || t == 16) { row(c, b, -1, .5f, false, .35, 3, false); row(c, b, -1, .7f, true, .35, 3, false); }
            if (t == 30 || t == 38) { row(c, b, -1, .8f, false, .35, 3, false); row(c, b, -1, .5f, true, .35, 3, false); }
            if (t == 54) row(c, b, 1, 2.4f, false, .35, 3, true);
            if (t == 64) row(c, b, 1, 1.05f, false, .35, 3, false);
            if (t == 84) for (int s = -1; s <= 1; s += 2) { row(c, b, s, .5f, false, .35, 3, false); row(c, b, s, .7f, true, .35, 3, false); }
        }, papyPose(1.4f));
    }
    /** Turn 8: series after series scrolling along the bottom, the odd blue one. */
    static Attack papyEndless() {
        return atk("endless_bones", 140, null, (c, t) -> {
            Board b = board(c, t);
            if (t >= 8 && t < 124 && t % 7 == 1) { boolean blue = (t / 7) % 5 == 4; row(c, b, -1, blue ? 2.4f : .45f, false, .4, 3, blue); }
        }, papyPose(1.8f));
    }
    /** Turn 9: THE SPECIAL ATTACK!!! ...which the Annoying Dog steals. Just a regular bone, then. */
    static Attack papySpecialAttack() {
        return atk("special_attack", 120, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 4) say(c, "BEHOLD...! MY SPECIAL ATTACK!");
            step(c, t, 30, 70, 4, k -> b.at(6 - (k - 30) * .3, .3 + .15 * Math.abs(Math.sin(k * .8)), -1), p -> p.box(.5f, .4f, .7f).color(0xffffff).dmg(0).spawn());
            step(c, t, 30, 70, 4, k -> b.at(6 - (k - 30) * .3, .7, -1.3), p -> p.box(.15f, .15f, .6f).color(0xf5f5f5).dmg(0).spawn());
            if (t == 40) c.sound(SoundEvents.WOLF_AMBIENT, 1, 1.7f);
            if (t == 74) say(c, "WHERE'S MY SPECIAL ATTACK!? WHATEVER, I'LL JUST USE A REALLY COOL REGULAR ATTACK.");
            if (t == 90) row(c, b, -1, .45f, false, .35, 3, false);
        }, (p, t, m) -> {
            float s = env(t, 6, 30, 4), shock = env(t - 40, 4, 40, 6);
            p.r("armL", -2.9f * s, 0, .4f).r("armR", -2.9f * s, 0, -.4f).o("body", 0, 1f * s, 0)
                .r("armL", 1.5f * shock, 0, .3f * shock).r("armR", 1.5f * shock, 0, -.3f * shock).r("head", 0, .6f * shock * (float) Math.sin(t * .4f), 0);
        });
    }
    static final String[] FONT_C = {"111", "100", "100", "100", "111"}, FONT_O = {"111", "101", "101", "101", "111"},
        FONT_L = {"100", "100", "100", "100", "111"}, FONT_D = {"110", "101", "101", "101", "110"},
        FONT_U = {"101", "101", "101", "101", "111"}, FONT_E = {"111", "100", "111", "100", "111"};
    /** Turn 10: "an absolutely normal attack" - old patterns, the dog, COOL DUDE in bones, a bone on a skateboard, a crowd, one slooow bone. */
    static Attack papyNormalAttack() {
        return atk("absolutely_normal_attack", 220, null, (c, t) -> {
            Board b = board(c, t);
            if (t == 8 || t == 16 || t == 24) row(c, b, -1, .45f, false, .35, 3, false);
            if (t == 34) row(c, b, -1, 1.5f, true, .35, 3, false);
            step(c, t, 46, 80, 4, k -> b.at(6 - (k - 46) * .35, .3, 1), p -> p.box(.5f, .4f, .7f).color(0xffffff).dmg(0).spawn());
            if (t == 84) {
                String[][] word = {FONT_C, FONT_O, FONT_O, FONT_L, null, FONT_D, FONT_U, FONT_D, FONT_E};
                int col = 0;
                for (String[] g : word) {
                    if (g != null) for (int y = 0; y < 5; y++) for (int x = 0; x < 3; x++) if (g[y].charAt(x) == '1')
                        c.proj().at(b.at(6.5 + (col + x) * .3, 3.3 - y * .28, -1.5)).vel(b.r().scale(-.2)).box(.2f, .2f, .2f).color(0xffffff).dmg(3).life(100).spawn();
                    col += 4;
                }
            }
            if (t == 140) {
                c.proj().at(b.at(6.5, .6, 0)).vel(b.r().scale(-.3)).box(.22f, .9f, .22f).color(0xffffff).dmg(3).life(46).spawn();
                c.proj().at(b.at(6.5, .1, 0)).vel(b.r().scale(-.3)).face(b.r()).box(.5f, .1f, 1.0f).color(0x8a3a2a).dmg(0).life(46).spawn();
            }
            if (t >= 156 && t < 180 && t % 3 == 0) row(c, b, (t / 3) % 2 == 0 ? -1 : 1, .3f + (float) c.rnd.nextDouble() * .5f, false, .4, 3, false);
            if (t == 186) row(c, b, -1, .45f, false, .07, 3, false);
            if (t == 200) say(c, "NYEH HEH HEH!");
        }, papyPose(2.2f));
    }
}
