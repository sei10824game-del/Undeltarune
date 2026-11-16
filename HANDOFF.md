# Handoff (branch `claude/determined-ride-ywipql`)

Read first: `AGENTS_BRIEF.md` (rules) and framework in `src/main/java/com/undeltarune/{def,attack,entity,client}`. Reference character: Sans in `chars/UtRuinsSnowdin.java`; shared FX in `attack/Fx.java`.
State: all groups merged, `./gradlew --no-daemon compileJava -q` passes, ~176 character defs. Not run in-game (try `runClient`).

## Done (complete)
UtWaterfallHotland, UtCoreFinale, UtMisc, AuUnderfellHorror, AuUnderswapSwapfell, DrCh1, AuMultiverse (mostly).

## Gaps
None open (Ch4, Ch5, Dusttale, Ruins Hard Mode, Ch2, UTY filled/audited). Not run in-game.

## Known issues
- Duplicate character ids fixed (UtMisc *_misc, ruins_napstablook). Attack ids repeat per-character only (fine).
- Faction: per-character one-way enemy override exists: `CharacterDef.Builder.foes(Faction...)` (used by underswap_undyne vs ROYAL_GUARD); add to other AU feuds as needed.
- Visuals are procedural box models only (no textures/sounds assets).
- Worktrees under `.claude/` (git-ignored) hold old agent branches `worktree-agent-*`; all merged.
