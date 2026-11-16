package com.undeltarune.attack;

import com.undeltarune.entity.UtMob;

/** One "turn" of attacks. tick() runs server-side for t=0..duration-1; pose() runs client-side every frame
 *  (t is fractional ticks since start). Keep wind-up/animation in pose() synced to the timings used in tick(). */
public abstract class Attack {
    public final String id; public final int duration;
    protected Attack(String id, int duration) { this.id = id; this.duration = duration; }
    public abstract void tick(AttackCtx c, int t);
    public void pose(Pose p, float t, UtMob m) {}
    /** Gate by phase, e.g. m.hpFrac() < .5f */
    public boolean usable(UtMob m) { return true; }
}
