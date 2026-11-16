# Handoff (branch `claude/determined-ride-ywipql`)

Read first: `AGENTS_BRIEF.md` (rules) and framework in `src/main/java/com/undeltarune/{def,attack,entity,client}`. Reference character: Sans in `chars/UtRuinsSnowdin.java`; shared FX in `attack/Fx.java`.
State: all groups merged, `./gradlew --no-daemon compileJava -q` passes, ~176 character defs. Not run in-game (try `runClient`).

## Done (complete)
UtWaterfallHotland, UtCoreFinale, UtMisc, AuUnderfellHorror, AuUnderswapSwapfell, DrCh1, AuMultiverse (mostly).

## Gaps - fan out one Opus agent per item (agents must edit only their own file)
1. `DrCh3Ch4.java`: Ch4 enemies missing (register() lines removed): bibliox, jackenstein, missMizzle, hammerOfJustice, winglade, organikk, wicabel, soundOfJustice, titanSpawn, titan, smallMike, catMike, cowboyMike. Style: see `shadowguy()`/`spotlight()` in the same file + `chars/drlate/DrKit.java`. Re-add to register().
2. `DrCh5Plus.java`: empty. Add only web-confirmed Ch5+ content (date 2026-11).
3. `AuDustLastBreath.java`: only 5 chars. Full Dusttale (Dust Sans purple bones etc.) + Last Breath phases/attacks.
4. `UtRuinsSnowdin.java`: audit vs canon (Flowey, Toriel, Froggit, Whimsun, Moldsmal, Loox, Vegetoid, Migosp, Ice Cap, Jerry, Snowdrake, Gyftrot, Doggo, Lesser/Greater Dog, Dogamy, Dogaressa, Papyrus, Dummy, Sans Genocide) - was cut off, add missing.
5. `DrCh2.java`, `AuYellowFangames.java`: cut off; audit (Queen, Berdly, Spamton/NEO, Tasque Manager, Tenna...; UT Yellow: Martlet, Starlo, Dalv, Ceroba, Axis, Chujin, Zenith Martlet...).

## Known issues
- Duplicate ids across files (annoying_dog, disbelief_papyrus, glyde, mad_mew_mew, napstablook, so_sorry): `Characters.init` keeps the first; give distinct ids/remove the duplicates.
- Faction: ROYAL_GUARD vs ROYAL_SCIENCE hostility removed (canon). AU-specific feuds (e.g. Underswap Undyne rebellion) need a per-character enemy override (not built).
- Visuals are procedural box models only (no textures/sounds assets).
- Worktrees under `.claude/` (git-ignored) hold old agent branches `worktree-agent-*`; all merged.
