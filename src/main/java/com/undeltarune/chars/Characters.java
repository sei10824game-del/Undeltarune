package com.undeltarune.chars;

import com.undeltarune.def.CharacterDef;
import java.util.ArrayList;
import java.util.List;

/** Roster. Each group file owns one section and exposes `static void register(List<CharacterDef>)`.
 *  Only edit your own group file; this list is pre-wired. */
public final class Characters {
    public static final List<CharacterDef> ALL = new ArrayList<>();
    public static void init() {
        UtRuinsSnowdin.register(ALL);
        UtWaterfallHotland.register(ALL);
        UtCoreFinale.register(ALL);
        UtMisc.register(ALL);
        DrCh1.register(ALL);
        DrCh2.register(ALL);
        DrCh3Ch4.register(ALL);
        DrCh5Plus.register(ALL);
        AuUnderswapSwapfell.register(ALL);
        AuUnderfellHorror.register(ALL);
        AuDustLastBreath.register(ALL);
        AuMultiverse.register(ALL);
        AuUnderfellHorror.registerVariants(ALL);
        AuYellowFangames.register(ALL);
        // ponytail: duplicate ids across group files keep the first; split into distinct ids later
        var seen = new java.util.HashSet<String>();
        ALL.removeIf(d -> !seen.add(d.id()));
    }
}
