# SSRD — Open GitHub Issues

Snapshot of all **open** issues on https://github.com/RanoldStranold/SSRD/issues, pulled 2026-09-03. One file per issue, formatted with title/labels/date/body plus cross-reference notes where bugs look related. See [`bugs.md`](../bugs.md) in the repo root for the pre-existing ranked/root-caused bug tracker (older, partially stale — several issues below are missing from it and a few it lists as active are now closed on GitHub).

| # | Title | Opened | Notes |
|---|-------|--------|-------|
| [8](008-dh-vanilla-fade-mode-transparency.md) | DH "Vanilla Fade Mode" / transparency sublevel bleed-through | 2026-05-19 | Low severity, low ease per `bugs.md`; maintainer previously "scared to touch" |
| [16](016-conflict-with-no-mans-land.md) | Conflict with No Man's Land (Sundog breaks while walking) | 2026-06-03 | Third-party mod interaction |
| [28](028-shaders-temporal-filter-no-render.md) | Complementary shader + temporal filter (TAA) blocks sublevel render | 2026-06-16 | Also: forceload command doesn't render, only Create: Power Loader does |
| [50](050-not-working-with-shaders-makeup-ultra-fast.md) | SSRD reverts to vanilla render distance with shaders (Makeup Ultra Fast) | 2026-08-04 | Same family as #28; modpack includes Sinytra Connector |
| [51](051-forceload-tickets-bypassed-by-shouldload-shortcircuit.md) | Force-load tickets ignored — `shouldLoad` short-circuit is distance-only | 2026-08-05 | Reporter identified exact root cause + fix in `SubLevelTrackingSystemMixin` |
| [52](052-stationary-contraption-crash.md) | Mechanical Bearing on sublevel crashes game (ticking block entity) | 2026-08-07 | Likely same root cause as #61 |
| [54](054-dh-lods-not-aligned-with-damage-tilt.md) | DH LODs misaligned on damage tilt / death | 2026-08-14 | Same bug family as the fixed view-bobbing issue (#37) |
| [56](056-npe-viewfinder-forceload-serverlevel-init.md) | NPE crash: Vista ViewFinder + forceload during `ServerLevel` init | 2026-08-15 | Ordering bug: SSRD/Sable forceload fires before `ServerLevel.<init>` completes |
| [58](058-sometimes-ssrd-doesnt-work.md) | SSRD intermittently stops working on an existing world | 2026-08-20 | Thin report; world-specific, not mod-conflict |
| [59](059-sublevels-stop-rendering-outside-simulation-distance.md) | Sublevel stops rendering once moved from its assembly chunk + out of sim distance | 2026-08-25 | Reporter on Sable 2.0.5 (newer than the 2.0.1 SSRD is built against) |
| [60](060-dedicated-server-crash-forceloading.md) | Dedicated server crash on `/ssrd forceload`: `NoSuchMethodError` on Sable's `SubLevelLoadingTicket.getType()` | 2026-08-27 | Sable API/ABI version mismatch |
| [61](061-crash-bearings-in-sublevel.md) | Crash assembling Mechanical Bearing contraption on sublevel | 2026-08-29 | Likely same root cause as #52 |

## Likely duplicate/related clusters

1. **Forceload + block-entity-init crashes**: #52, #56, #60, #61 — all triggered by SSRD/Sable force-loading or assembling a sublevel containing a block entity with nontrivial init/tick behavior (Mechanical Bearing, Vista ViewFinder). #60's `NoSuchMethodError` suggests a Sable version mismatch may be the actual root cause behind some of these, not a logic bug in SSRD itself.
2. **Shader interaction blocks sublevel rendering**: #28, #50 — both report sublevels reverting to vanilla render distance / not rendering at all when specific shader packs (with TAA/temporal filtering) are active.
3. **Sable-version drift**: #59 (Sable 2.0.5) and #60 point at SSRD 1.8.5 being built/tested against an older Sable (`claude.md` pins 2.0.1) while users are already on newer Sable releases with API changes.
4. **Matrix-hack family**: #54 is very likely the same class of bug as the already-fixed view-bobbing issue (#37, fixed in v1.8.4) — damage tilt is a similar projection-matrix perturbation that may not be handled by the current `GameRendererProjMixin`/`DHRenderUtilProjMixin` fix.
