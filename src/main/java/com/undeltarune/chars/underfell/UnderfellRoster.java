package com.undeltarune.chars.underfell;

import com.undeltarune.attack.*;
import com.undeltarune.def.*;
import net.minecraft.world.phys.Vec3;
import com.undeltarune.def.CharacterDef.Attitude;

import java.util.ArrayList;
import java.util.List;

/** Underfell versions of the base Undertale monsters: own red/black turns (7 move archetypes mixed per character), tougher, always hostile.
 *  ponytail: archetype moves, not per-character canon; hand-written turns can replace a KIT row. */
public final class UnderfellRoster {
    static final String[] BASES = {"froggit", "whimsun", "moldsmal", "loox", "vegetoid", "migosp", "ice_cap", "jerry", "snowdrake", "gyftrot",
        "doggo", "lesser_dog", "dogamy", "dogaressa", "greater_dog", "aaron", "woshua", "shyren", "temmie", "mad_dummy", "burgerpants",
        "royal_guard_01", "royal_guard_02", "madjick", "knight_knight", "final_froggit", "astigmatism", "whimsalot", "memoryhead", "endogeny",
        "lemon_bread", "reaper_bird", "snowdrakes_mother", "glyde", "so_sorry", "vulkin", "tsunderplane", "pyrope", "moldbygg", "parsnik",
        "moldessa", "migospel", "asriel_dreemurr", "chara", "gaster_follower_1"};

    /** id -> move list. Moves: ring, volley, rain, rise, laser, charge, spiral. */
    static final String[][] KIT = {
        {"froggit", "volley", "rain"}, {"whimsun", "spiral", "ring"}, {"moldsmal", "rise", "charge"}, {"loox", "laser", "volley"},
        {"vegetoid", "rise", "ring", "rain"}, {"migosp", "charge", "spiral"}, {"ice_cap", "rain", "volley"}, {"jerry", "charge", "ring"},
        {"snowdrake", "volley", "spiral"}, {"gyftrot", "charge", "rise"}, {"doggo", "laser", "charge"}, {"lesser_dog", "charge", "volley"},
        {"dogamy", "charge", "ring", "laser"}, {"dogaressa", "charge", "rain", "laser"}, {"greater_dog", "charge", "spiral", "rise"},
        {"aaron", "ring", "charge", "volley"}, {"woshua", "rain", "ring"}, {"shyren", "spiral", "laser"}, {"temmie", "spiral", "charge", "rain"},
        {"mad_dummy", "rain", "ring", "volley", "rise"}, {"burgerpants", "volley", "rain", "charge"}, {"royal_guard_01", "charge", "rise", "volley"},
        {"royal_guard_02", "laser", "volley", "ring"}, {"madjick", "spiral", "ring", "laser"}, {"knight_knight", "rise", "charge", "ring"},
        {"final_froggit", "volley", "rain", "spiral"}, {"astigmatism", "laser", "spiral"}, {"whimsalot", "charge", "volley", "rain"},
        {"memoryhead", "spiral", "ring", "laser"}, {"endogeny", "rise", "ring", "spiral"}, {"lemon_bread", "rain", "charge"},
        {"reaper_bird", "charge", "volley", "laser"}, {"snowdrakes_mother", "ring", "volley", "rise"}, {"glyde", "volley", "spiral", "laser", "rain"},
        {"so_sorry", "rise", "rain", "laser", "ring"}, {"vulkin", "ring", "rain"}, {"tsunderplane", "charge", "laser", "volley"},
        {"pyrope", "spiral", "charge", "rain"}, {"moldbygg", "rise", "ring"}, {"parsnik", "volley", "charge"}, {"moldessa", "rise", "spiral", "ring"},
        {"migospel", "charge", "ring", "spiral"}, {"asriel_dreemurr", "spiral", "laser", "ring", "rain", "rise"},
        {"chara", "charge", "laser", "volley", "ring", "rise"}, {"gaster_follower_1", "laser", "spiral"}};
    static final int[] PAL = {0xff2020, 0xff6a00, 0xb01040, 0xffb000, 0xc00000};

    /** Fell-style move: red/black bullets, faster and heavier than base game. */
    static Attack move(String base, String mv, int col, float dmg, boolean boss, boolean mobile) {
        final String id = "underfell_" + base + "_" + mv;
        final int dur = switch (mv) { case "ring" -> 56; case "volley" -> 50; case "rain" -> 54; case "rise" -> 56; case "laser" -> 66; case "charge" -> 40; default -> 68; };
        AuKit.Tick tk = switch (mv) {
            case "ring" -> (c, t) -> { if (t == 0 || t == 22) for (int k = 0; k < 14; k++) { double a = k * Math.PI / 7 + (t == 22 ? .22 : 0);
                AuKit.shot(c, c.eye(), new Vec3(Math.cos(a) * .36, 0, Math.sin(a) * .36), .38f, col, dmg, 10, 42); } };
            case "volley" -> (c, t) -> { if (t == 10 || t == 20 || t == 30) AuKit.fan(c, c.eye(), 5, 46, .7, .34f, col, dmg, 8, 36); };
            case "rain" -> (c, t) -> { if (t % 3 == 0 && t < 42) { Vec3 p = c.target.position().add((c.rnd.nextDouble() - .5) * 10, 9, (c.rnd.nextDouble() - .5) * 10);
                AuKit.shot(c, p, new Vec3(0, -.55, 0), .42f, col, dmg, 12, 40); } };
            case "rise" -> (c, t) -> { if (t == 0 || t == 14 || t == 28) { Vec3 g = c.target.position(); for (int k = 0; k < (t == 28 ? 3 : 1); k++)
                AuKit.pillar(c, g.add((c.rnd.nextDouble() - .5) * 4 * k, 0, (c.rnd.nextDouble() - .5) * 4 * k), 3.2f, 1f, col, dmg * 1.2f, 20, 26); } };
            case "laser" -> (c, t) -> { if (t == 0) { Vec3 d = c.aim(c.eye()); AuKit.seg(c, c.eye(), c.eye().add(d.scale(32)), .8f, col, dmg * 1.5f, 34, 40); }
                if (t == 28) { Vec3 d = c.aim(c.eye()); AuKit.seg(c, c.eye(), c.eye().add(d.scale(32)), .5f, 0x111111, dmg, 26, 30); } };
            case "charge" -> (c, t) -> { Vec3 d = c.aim(c.pos()); Vec3 h = new Vec3(d.x, 0, d.z).normalize();
                if (t == 0) AuKit.seg(c, c.pos().add(0, .8, 0), c.pos().add(0, .8, 0).add(h.scale(14)), .6f, col, 0, 14, 14);
                if (t == 16 && mobile) c.dash(h.scale(1.15));
                if (t >= 16 && t < 30 && t % 4 == 0) c.aoe(c.pos(), 2.4, dmg * 1.4f, .8); };
            default -> (c, t) -> { if (t % 2 == 0 && t < 60) { double a = t * .5; AuKit.shot(c, c.eye(), new Vec3(Math.cos(a) * .32, 0, Math.sin(a) * .32), .32f, col, dmg, 0, 52);
                AuKit.shot(c, c.eye(), new Vec3(-Math.cos(a) * .32, 0, -Math.sin(a) * .32), .32f, 0x111111, dmg, 0, 52); } };
        };
        AuKit.Anim an = (p, t, m) -> { float e = AuKit.env(t, 10, dur - 14, 10);
            if (mv.equals("charge")) p.r("body", e * .35f, 0, 0).r("armL", -e, 0, 0).r("armR", -e, 0, 0);
            else p.r("armL", -e * 2.4f, 0, 0).r("armR", -e * 2.4f, 0, 0).o("body", 0, e * .4f, 0); };
        AuKit.L a = AuKit.atk(id, dur, tk, an);
        return boss && (mv.equals("rise") || mv.equals("laser")) ? a.when(m -> m.hpFrac() < .6f) : a;
    }

    /** Call after the base groups are registered. */
    public static void register(List<CharacterDef> out) {
        List<CharacterDef> add = new ArrayList<>();
        for (String id : BASES) {
            CharacterDef b = out.stream().filter(c -> c.id().equals(id)).findFirst().orElse(null);
            if (b == null) continue;
            ModelDef m = new ModelDef();
            for (Part p : b.model().parts) m.parts.add(new Part(p.name(), p.parent(), p.px(), p.py(), p.pz(), p.w(), p.h(), p.d(), tint(p.rgb(), p.glow()), p.hang(), p.glow(), p.alpha()));
            List<Attack> atks = new ArrayList<>(); int pal = Math.floorMod(id.hashCode(), PAL.length);
            for (String[] k : KIT) if (k[0].equals(id)) for (int i = 1; i < k.length; i++) atks.add(move(id, k[i], PAL[(pal + i) % PAL.length], 4f + (b.boss() ? 2 : 0), b.boss(), b.speed() > 0));
            add.add(new CharacterDef("underfell_" + id, "Underfell " + b.name(), b.faction(), Attitude.HOSTILE, b.hp() * 1.5f, b.speed() * 1.2f, b.scale(), b.width(), b.height(),
                b.boss(), b.randomOrder(), Math.max(8, b.rest() * 3 / 4), b.keepDist(), 0xb01818, 0x1a1a1a, m, atks.isEmpty() ? b.attacks() : atks, b.foes()));
        }
        out.addAll(add);
    }

    /** Darker, redder; glows (eyes/magic) turn blood red. */
    static int tint(int rgb, boolean glow) {
        if (glow) return 0xff2020;
        int r = rgb >> 16 & 255, g = rgb >> 8 & 255, b = rgb & 255;
        return Math.min(255, (int) (r * .75 + 45)) << 16 | (int) (g * .4) << 8 | (int) (b * .4);
    }
    private UnderfellRoster() {}
}
