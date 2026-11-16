package com.undeltarune.chars;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;
import com.undeltarune.entity.UtMob;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public final class UtRuinsSnowdin {
    /** REFERENCE IMPLEMENTATION of a character: model + several multi-phase attacks with matching client animation. */
    public static void register(List<CharacterDef> out) {
        out.add(CharacterDef.of("sans", "sans", Faction.UNDERGROUND).att(Attitude.NEUTRAL).hp(120).boss().keep(7).rest(14).speed(.3f)
            .egg(0x2a6fd6, 0xffffff).size(.7f, 1.1f).scale(.9f)
            .model(new ModelDef()
                .box("body", null, 0, 8, 0, 8, 7, 5, 0x3a6fe0)
                .box("head", "body", 0, 9.5f, 0, 9, 7, 7, 0xf5f5f5)
                .glow("eyeL", "head", -2, .5f, -3.6f, 1.6f, 1.6f, .4f, 0x000000)
                .glow("eyeR", "head", 2, .5f, -3.6f, 1.6f, 1.6f, .4f, 0x000000)
                .box("grin", "head", 0, -2, -3.6f, 5, .7f, .4f, 0x222222)
                .box("hood", "body", 0, 3.4f, 0, 9, 1.5f, 6, 0xffffff)
                .limb("armL", "body", -5, 3, 0, 2.4f, 6, 2.4f, 0x3a6fe0)
                .limb("armR", "body", 5, 3, 0, 2.4f, 6, 2.4f, 0x3a6fe0)
                .limb("legL", null, -2, 5, 0, 3, 5, 3.5f, 0x1a1a1a)
                .limb("legR", null, 2, 5, 0, 3, 5, 3.5f, 0x1a1a1a))
            .attacks(boneSweep(), blasterRing(), gravitySlam(), boneGaps()).build());
    }

    /** Sweeping row of bones rushing toward the target; Sans shrugs one hand out. */
    static Attack boneSweep() {
        return new Attack("bone_sweep", 70) {
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
            }
        };
    }
    /** Blue-soul gravity: target is yanked to the floor, bones rise from below in a line. */
    static Attack gravitySlam() {
        return new Attack("gravity_slam", 80) {
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
            }
        };
    }
    /** Two walls of bones with a gap closing in from either side. */
    static Attack boneGaps() {
        return new Attack("bone_gaps", 76) {
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
}
