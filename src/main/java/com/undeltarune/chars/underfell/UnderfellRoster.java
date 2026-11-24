package com.undeltarune.chars.underfell;

import com.undeltarune.def.*;
import com.undeltarune.def.CharacterDef.Attitude;

import java.util.ArrayList;
import java.util.List;

/** Underfell versions of the base Undertale monsters: same turns, red/black recolor, tougher, always hostile.
 *  ponytail: attacks are the base game's; Underfell-specific turns can replace them per id later. */
public final class UnderfellRoster {
    static final String[] BASES = {"froggit", "whimsun", "moldsmal", "loox", "vegetoid", "migosp", "ice_cap", "jerry", "snowdrake", "gyftrot",
        "doggo", "lesser_dog", "dogamy", "dogaressa", "greater_dog", "aaron", "woshua", "shyren", "temmie", "mad_dummy", "burgerpants",
        "royal_guard_01", "royal_guard_02", "madjick", "knight_knight", "final_froggit", "astigmatism", "whimsalot", "memoryhead", "endogeny",
        "lemon_bread", "reaper_bird", "snowdrakes_mother", "glyde", "so_sorry", "vulkin", "tsunderplane", "pyrope", "moldbygg", "parsnik",
        "moldessa", "migospel", "asriel_dreemurr", "chara", "gaster_follower_1"};

    /** Call after the base groups are registered. */
    public static void register(List<CharacterDef> out) {
        List<CharacterDef> add = new ArrayList<>();
        for (String id : BASES) {
            CharacterDef b = out.stream().filter(c -> c.id().equals(id)).findFirst().orElse(null);
            if (b == null) continue;
            ModelDef m = new ModelDef();
            for (Part p : b.model().parts) m.parts.add(new Part(p.name(), p.parent(), p.px(), p.py(), p.pz(), p.w(), p.h(), p.d(), tint(p.rgb(), p.glow()), p.hang(), p.glow(), p.alpha()));
            add.add(new CharacterDef("underfell_" + id, "Underfell " + b.name(), b.faction(), Attitude.HOSTILE, b.hp() * 1.5f, b.speed(), b.scale(), b.width(), b.height(),
                b.boss(), b.randomOrder(), Math.max(8, b.rest() * 3 / 4), b.keepDist(), 0xb01818, 0x1a1a1a, m, b.attacks(), b.foes()));
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
