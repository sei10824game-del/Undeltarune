package com.undeltarune.def;

import java.util.EnumSet;
import java.util.Set;

/** Canon allegiances. Add a value + its enemies here when a new group needs one. */
public enum Faction {
    UNDERGROUND, ROYAL_GUARD, ROYAL_SCIENCE, FLOWEY_CHAOS, CHILDREN,
    CARD_KINGDOM, CYBER_CITY, SPAMTON_NET, HEROES, LIGHTNERS, ANGELS, RUNAWAY_SHADOW,
    GENOCIDE_ROUTE, NIGHTMARE_AU, GUARDIAN_AU, ERROR_AU, MERCHANT, NEUTRAL;

    private static final Set<Faction> NONE = EnumSet.noneOf(Faction.class);
    /** Factions this one attacks on sight (symmetric, see {@link #hostile}). */
    public Set<Faction> enemies() {
        return switch (this) {
            case CARD_KINGDOM -> EnumSet.of(HEROES);
            case CYBER_CITY -> EnumSet.of(HEROES, SPAMTON_NET);
            case SPAMTON_NET -> EnumSet.of(CYBER_CITY, HEROES);
            case HEROES -> EnumSet.of(CARD_KINGDOM, CYBER_CITY, SPAMTON_NET, ANGELS, RUNAWAY_SHADOW);
            case ANGELS -> EnumSet.of(HEROES);
            case RUNAWAY_SHADOW -> EnumSet.of(HEROES);
            case ROYAL_GUARD -> EnumSet.of(ROYAL_SCIENCE);
            case ROYAL_SCIENCE -> EnumSet.of(ROYAL_GUARD);
            case GENOCIDE_ROUTE -> EnumSet.of(UNDERGROUND, ROYAL_GUARD, ROYAL_SCIENCE, CHILDREN);
            case NIGHTMARE_AU -> EnumSet.of(GUARDIAN_AU);
            case GUARDIAN_AU -> EnumSet.of(NIGHTMARE_AU, ERROR_AU);
            case ERROR_AU -> EnumSet.of(GUARDIAN_AU);
            default -> NONE;
        };
    }
    public static boolean hostile(Faction a, Faction b) { return a.enemies().contains(b) || b.enemies().contains(a); }
}
