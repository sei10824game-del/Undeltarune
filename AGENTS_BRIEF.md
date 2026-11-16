# Character authoring brief (read before editing)

Framework: `src/main/java/com/undeltarune/{def,attack,entity,client}`. Reference: `chars/UtRuinsSnowdin.java` (Sans) + `attack/Fx.java`.
Rules from the project owner:
- Add EVERY character of your group, and EVERY attack from canon (wiki). One "turn" of enemy attacks = ONE `Attack`. AU/fan characters with thin canon: invent faithful attacks from all fan works (e.g. Dust Sans purple bones).
- Visuals and animation are first-class: each Attack needs a real `pose()` (wind-up, strike, recovery) and rich projectile choreography (telegraphs via `.warn()`, particles, staggered timings), not a single spawn call.
- Bosses: `.boss()`, phases via `Attack.usable(m)` using `m.hpFrac()`. Faction/Attitude must follow canon (see `def/Faction.java`; add enum values/enemies there only if truly needed — keep edits minimal).
- Model: build a `ModelDef` of boxes that clearly resembles the character (colors, silhouette, eyes/glow parts). Name arm/leg parts armL/armR/legL/legR, head `head`, root `body` for default animation.
- Edit ONLY your own group file in `chars/` (plus new helper classes you create under `chars/<group>/` or `attack/` with unique names). Ids must be globally unique lowercase snake_case, prefix AU ids with the AU (e.g. `dusttale_sans`).
- Verify with `./gradlew --no-daemon compileJava -q`. Commit on your branch with clear messages. No PRs.
