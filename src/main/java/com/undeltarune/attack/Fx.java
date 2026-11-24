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
        Vec3 up = Math.abs(dir.y) > .95 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0), r = dir.cross(up).normalize(), u = r.cross(dir).normalize();
        int life = charge + fire + 8;
        // skull: cranium, long snout, upper teeth, lower jaw that drops open as it fires, horns, dark sockets + glowing eyes
        part(c, at, dir, 1.0f, .9f, .9f, 0xf4f4f4, life);
        part(c, at.add(dir.scale(.85)).add(u.scale(-.05)), dir, .7f, .55f, .8f, 0xe8e8e8, life);
        part(c, at.add(dir.scale(1.2)).add(u.scale(-.28)), dir, .62f, .12f, .5f, 0xffffff, life);                // teeth
        part(c, at.add(dir.scale(.95)).add(u.scale(-.5)), dir, .6f, .22f, .9f, 0xd8d8d8, charge + 4);             // jaw closed
        part(c, at.add(dir.scale(.95)).add(u.scale(-.8)), dir, .6f, .22f, .9f, 0xd8d8d8, fire + 4);               // jaw open (delayed look)
        for (int s2 = -1; s2 <= 1; s2 += 2) {
            part(c, at.add(r.scale(.5 * s2)).add(u.scale(.55)).add(dir.scale(-.15)), dir, .18f, .5f, .18f, 0xffffff, life);          // horns
            part(c, at.add(r.scale(.25 * s2)).add(u.scale(.15)).add(dir.scale(.48)), dir, .3f, .3f, .12f, 0x111111, life);            // sockets
            part(c, at.add(r.scale(.25 * s2)).add(u.scale(.15)).add(dir.scale(.55)), dir, .14f, .14f, .1f, 0x35e4ff, life);           // eyes
        }
        // telegraph + beam: thin pulsing line during charge, then lethal core; a wider harmless halo and white-hot inner beam sell the glow
        Vec3 mid = at.add(dir.scale(len / 2 + 1.4));
        c.proj().at(mid).face(dir).box(width, width, (float) len).color(0xcfefff).dmg(dmg).warn(charge).life(charge + fire).pierce().spawn();
        c.proj().at(mid).face(dir).box(width * 1.7f, width * 1.7f, (float) len).color(0x7fd8ff).dmg(0).warn(charge).life(charge + fire).pierce().shrink().spawn();
        c.particle(ParticleTypes.END_ROD, at.add(dir.scale(1.5)), 10, .25, .06);
        c.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1.2f, 1.6f);
    }
    private static void part(AttackCtx c, Vec3 at, Vec3 dir, float w, float h, float l, int rgb, int life) {
        c.proj().at(at).face(dir).box(w, h, l).color(rgb).dmg(0).life(life).spawn();
    }
    public static void bone(AttackCtx c, Vec3 at, Vec3 vel, float h, float dmg, int life) {
        c.proj().at(at).vel(vel).box(.22f, h, .22f).color(0xffffff).dmg(dmg).life(life).spawn();
    }
    public static void burst(AttackCtx c, Vec3 at) { c.particle(ParticleTypes.END_ROD, at, 12, .3, .05); }
    private Fx() {}
}
