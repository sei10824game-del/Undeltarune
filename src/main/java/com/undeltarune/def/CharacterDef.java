package com.undeltarune.def;

import com.undeltarune.attack.Attack;
import java.util.List;

/** Everything about one character. attacks run in order (or random when randomOrder), one per "turn". */
public record CharacterDef(String id, String name, Faction faction, Attitude toPlayer,
                           float hp, float speed, float scale, float width, float height,
                           boolean boss, boolean randomOrder, int rest, double keepDist,
                           int eggA, int eggB, ModelDef model, List<Attack> attacks, java.util.Set<Faction> foes) {
    public enum Attitude { HOSTILE, NEUTRAL, FRIENDLY }

    public static Builder of(String id, String name, Faction f) { return new Builder(id, name, f); }

    public static class Builder {
        String id, name; Faction f; Attitude att = Attitude.HOSTILE;
        float hp = 40, speed = .25f, scale = 1, w = .6f, h = 1.8f; boolean boss, rnd; int rest = 30;
        double keep = 6; int a = 0x222222, b = 0xffffff; ModelDef m = new ModelDef(); List<Attack> atk = List.of(); java.util.Set<Faction> foes = java.util.Set.of();
        Builder(String id, String name, Faction f) { this.id = id; this.name = name; this.f = f; }
        public Builder att(Attitude v) { att = v; return this; }
        public Builder hp(float v) { hp = v; return this; }
        public Builder speed(float v) { speed = v; return this; }
        public Builder scale(float v) { scale = v; return this; }
        public Builder size(float w, float h) { this.w = w; this.h = h; return this; }
        public Builder boss() { boss = true; return this; }
        public Builder random() { rnd = true; return this; }
        public Builder rest(int v) { rest = v; return this; }
        public Builder keep(double v) { keep = v; return this; }
        public Builder egg(int a, int b) { this.a = a; this.b = b; return this; }
        public Builder model(ModelDef v) { m = v; return this; }
        public Builder attacks(Attack... v) { atk = List.of(v); return this; }
        /** Extra factions this character attacks (AU feuds), one-way. */
        public Builder foes(Faction... v) { foes = java.util.Set.of(v); return this; }
        public CharacterDef build() { return new CharacterDef(id, name, f, att, hp, speed, scale, w, h, boss, rnd, rest, keep, a, b, m, atk, foes); }
    }
}
