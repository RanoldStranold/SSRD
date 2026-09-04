# Issue #56 — Server crashes on startup with NullPointerException in ViewFinderBlockEntity when Sable/SSRD force-loads a sublevel containing a ViewFinder

- **URL**: https://github.com/RanoldStranold/SSRD/issues/56
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-15

## Description

Mod Version: 1.8.5
Other mods being used: create-1.21.1-6.0.10, DistantHorizons-3.2.0-b-1.21.1, vista-1.21.1-5.3.7, neoforge-21.1.243

The server crashes during startup with a NullPointerException when Sable (via SSRD's mixin into `SubLevelHoldingChunkMap`) force-loads a sublevel that contains a Vista `ViewFinderBlockEntity`. The crash is 100% reproducible on every server start.

The exception originates in `ViewFinderBlockEntity.setLevel()`, which immediately calls `ensureLinked()` → `BroadcastManager.getInstance()` → moonlight's `WorldSavedDataType.getData()`. At this point in server initialization, `targetLevel` is still null because the `ServerLevel` has not yet fully completed its constructor — Sable injects its sublevel loading into `ServerLevel.<init>` via a mixin, meaning Vista tries to access level data before the level is ready.

Log: https://mclo.gs/NGvaeVA

## Notes

Directly implicates `src/main/java/net/ranold/ssrd/mixin/SubLevelHoldingChunkMapMixin.java` (present in the working tree's current modified files) — the forceload path added for [Issue #47 fix / forceload feature] triggers block entity `setLevel()` calls during `ServerLevel` construction, before third-party mods like Vista can safely resolve their level-scoped singletons. This is a timing/ordering bug in when SSRD/Sable performs its forced chunk/block-entity load relative to `ServerLevel.<init>` completion. Related to forceloading crashes in [Issue #60](060-dedicated-server-crash-forceloading.md) and [Issue #61](061-crash-bearings-in-sublevel.md)/[Issue #52](052-stationary-contraption-crash.md) — all are crashes triggered by SSRD/Sable force-loading or assembling contraptions containing block entities with nontrivial `setLevel()`/tick behavior.
