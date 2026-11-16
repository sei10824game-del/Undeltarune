package com.undeltarune.attack;

import com.undeltarune.entity.UtProjectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

/** Reusable multi-part visuals shared across characters. */
public final class Fx {
    /** Gaster Blaster: skull appears (warn), charges with a thin telegraph line, then fires a wide beam. */
    public static void blaster(AttackCtx c, Vec3 at, Vec3 dir, int charge, int fire, float dmg, float width) {
        dir = dir.normalize(); double len = 28;
        // skull body (two boxes: cranium + jaw), harmless
        c.proj().at(at).face(dir).box(1.1f, 1.0f, 1.2f).color(0xf2f2f2).dmg(0).life(charge + fire + 6).spawn();
        c.proj().at(at.add(dir.scale(.3)).add(0, -.55, 0)).face(dir).box(.8f, .35f, .9f).color(0xdadada).dmg(0).life(charge + fire + 6).spawn();
        // eyes
        c.proj().at(at.add(dir.scale(.55)).add(0, .15, 0)).face(dir).box(.9f, .2f, .12f).color(0x35e4ff).dmg(0).life(charge + fire + 6).spawn();
        // telegraph + beam in one entity: warn=charge makes it a thin pulsing line, then it becomes the lethal beam
        c.proj().at(at.add(dir.scale(len / 2 + .6))).face(dir).box(width, width, (float) len).color(0xcfefff)
            .dmg(dmg).warn(charge).life(charge + fire).pierce().spawn();
        c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1.2f, 1.6f);
    }
    public static void bone(AttackCtx c, Vec3 at, Vec3 vel, float h, float dmg, int life) {
        c.proj().at(at).vel(vel).box(.22f, h, .22f).color(0xffffff).dmg(dmg).life(life).spawn();
    }
    public static void burst(AttackCtx c, Vec3 at) { c.particle(ParticleTypes.END_ROD, at, 12, .3, .05); }
    private Fx() {}
}
