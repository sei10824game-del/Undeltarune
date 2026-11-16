package com.undeltarune.entity;

import com.undeltarune.attack.AttackCtx;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import com.undeltarune.Undeltarune;

import java.util.HashSet;
import java.util.Set;

/** Universal attack object: bone, beam, spear, orb, wall... a colored box that flies, hurts, and fades.
 *  Rendered as a glowing box of size (w,h,l) oriented along its yaw/pitch (l = length along travel). */
public class UtProjectile extends Entity {
    public static final int NORMAL = 0, BLUE = 1, ORANGE = 2;
    static final EntityDataAccessor<Float> W = SynchedEntityData.defineId(UtProjectile.class, EntityDataSerializers.FLOAT);
    static final EntityDataAccessor<Float> H = SynchedEntityData.defineId(UtProjectile.class, EntityDataSerializers.FLOAT);
    static final EntityDataAccessor<Float> L = SynchedEntityData.defineId(UtProjectile.class, EntityDataSerializers.FLOAT);
    static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(UtProjectile.class, EntityDataSerializers.INT);
    static final EntityDataAccessor<Integer> WARN = SynchedEntityData.defineId(UtProjectile.class, EntityDataSerializers.INT);
    public int life = 40, mode, fade; public float dmg = 3, grav, spin; public boolean pierce, glow = true, shrink;
    public int ownerId = -1; private final Set<Integer> hit = new HashSet<>();
    private int age;

    public UtProjectile(EntityType<?> t, Level l) { super(t, l); noPhysics = true; }
    @Override protected void defineSynchedData() {
        entityData.define(W, .3f); entityData.define(H, .3f); entityData.define(L, .3f);
        entityData.define(COLOR, 0xffffff); entityData.define(WARN, 0);
    }
    public float w() { return entityData.get(W); } public float h() { return entityData.get(H); } public float l() { return entityData.get(L); }
    public int color() { return entityData.get(COLOR); } public int warn() { return entityData.get(WARN); }
    public int age() { return age; } public int maxLife() { return life; }
    /** 0..1 over the lifetime, for client fade/scale. */
    public float lifeFrac(float pt) { return Math.min(1f, (age + pt) / Math.max(1, life)); }

    @Override public void tick() {
        super.tick(); age++;
        if (level().isClientSide) { setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z); return; }
        if (age > life) { discard(); return; }
        Vec3 v = getDeltaMovement().add(0, -grav, 0); setDeltaMovement(v); setPos(position().add(v));
        if (v.lengthSqr() > 1e-4) { setYRot((float) (Math.atan2(-v.x, v.z) * 57.29578)); setXRot((float) (-Math.atan2(v.y, Math.hypot(v.x, v.z)) * 57.29578)); }
        if (age <= warn() || !(level() instanceof net.minecraft.server.level.ServerLevel)) return;
        Entity own = ownerId >= 0 ? level().getEntity(ownerId) : null;
        float r = Math.max(w(), Math.max(h(), l())) * .5f + 1;
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(r))) {
            if (e == own || (own instanceof UtMob m && sparesAlly(m, e)) || (!pierce && !hit.isEmpty()) || hit.contains(e.getId())) continue;
            if (!overlaps(e)) continue;
            if (mode == BLUE && !moved(e)) continue;
            if (mode == ORANGE && moved(e)) continue;
            hit.add(e.getId());
            e.hurt(own instanceof LivingEntity o ? damageSources().mobAttack(o) : damageSources().magic(), dmg);
            if (!pierce) { discard(); return; }
        }
    }
    /** Hits everything except the owner, same-faction allies and players the owner is friendly to. */
    private static boolean sparesAlly(UtMob m, LivingEntity e) {
        if (e instanceof UtMob o) return o.def.faction() == m.def.faction() && !m.isFoe(e);
        if (e instanceof net.minecraft.world.entity.player.Player p) return m.def.toPlayer() != com.undeltarune.def.CharacterDef.Attitude.HOSTILE || p.isCreative() || p.isSpectator();
        return false;
    }
    private static boolean moved(LivingEntity e) { return e.getDeltaMovement().horizontalDistanceSqr() > 1e-3 || Math.abs(e.getDeltaMovement().y) > .08; }
    /** OBB test: transform victim center into the projectile's local frame. */
    private boolean overlaps(LivingEntity e) {
        Vec3 d = e.position().add(0, e.getBbHeight() / 2, 0).subtract(position());
        double yr = Math.toRadians(getYRot()), xr = Math.toRadians(getXRot());
        double cy = Math.cos(yr), sy = Math.sin(yr), cx = Math.cos(xr), sx = Math.sin(xr);
        double x1 = d.x * cy + d.z * sy, z1 = -d.x * sy + d.z * cy;           // undo yaw
        double y2 = d.y * cx + z1 * sx, z2 = -d.y * sx + z1 * cx;             // undo pitch
        return Math.abs(x1) <= w() / 2 + e.getBbWidth() / 2 && Math.abs(y2) <= h() / 2 + e.getBbHeight() / 2 && Math.abs(z2) <= l() / 2 + e.getBbWidth() / 2;
    }
    @Override protected void readAdditionalSaveData(CompoundTag t) { discard(); }
    @Override protected void addAdditionalSaveData(CompoundTag t) {}
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

    /** Fluent spawner: c.proj().at(p).vel(v).box(w,h,l).color(0xfff).dmg(4).life(30).warn(10).spawn() */
    public static class B {
        final AttackCtx c; final UtProjectile p;
        public B(AttackCtx c) { this.c = c; p = Undeltarune.PROJECTILE.get().create(c.level); p.ownerId = c.me.getId(); }
        public B at(Vec3 v) { p.setPos(v); return this; }
        public B vel(Vec3 v) { p.setDeltaMovement(v);
            if (v.lengthSqr() > 1e-6) { p.setYRot((float) (Math.atan2(-v.x, v.z) * 57.29578)); p.setXRot((float) (-Math.atan2(v.y, Math.hypot(v.x, v.z)) * 57.29578)); } return this; }
        /** Face along dir without moving (static beams/walls). */
        public B face(Vec3 v) { p.setYRot((float) (Math.atan2(-v.x, v.z) * 57.29578)); p.setXRot((float) (-Math.atan2(v.y, Math.hypot(v.x, v.z)) * 57.29578)); return this; }
        public B box(float w, float h, float l) { p.entityData.set(W, w); p.entityData.set(H, h); p.entityData.set(L, l); return this; }
        public B color(int rgb) { p.entityData.set(COLOR, rgb); return this; }
        public B dmg(float d) { p.dmg = d; return this; }
        public B life(int t) { p.life = t; return this; }
        /** Harmless telegraph for n ticks (drawn as thin translucent outline), then active. */
        public B warn(int t) { p.entityData.set(WARN, t); return this; }
        public B blue() { p.mode = BLUE; return color(0x3fa7ff); }
        public B orange() { p.mode = ORANGE; return color(0xff9a1f); }
        public B gravity(float g) { p.grav = g; return this; }
        public B pierce() { p.pierce = true; return this; }
        public B shrink() { p.shrink = true; return this; }
        public UtProjectile spawn() { c.level.addFreshEntity(p); return p; }
    }
}
