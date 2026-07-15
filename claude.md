# Seperate Sable Render Distance (SSRD) - Project Context

## Project Overview
Rewrite Sable mod to render physics objects (Aeronautics!) at Distant Horizons distances.
**Target Platform**: NeoForge 21.1.x for Minecraft 1.21.1
**Goal**: Physics visibility beyond vanilla render distance.

## Critical Development Rules
### 1. Verification Before Implementation
- **DO NOT GUESS**: Verify mixin signatures against source code (check `repos/` if needed).
- **Test First**: Test fixes empirically by launching game before declaring done.
- **Source Integrity**: No unrelated refactoring. Surgical changes only.

### 2. Dependency Management
- **Manual Versioning**: Update mod version (0.1 increment) only when all issues fixed and jars requested.
- **Permission Required**: DO NOT PUSH TO GITHUB without explicit permission.

## Dependency Versions
- **NeoForge**: 21.1.228
- **Minecraft**: 1.21.1
- **Sodium**: 0.8.12-SNAPSHOT (API overhauled)
- **Sable**: 2.0.1+mc1.21.1
- **Distant Horizons**: 3.0.3-b-1.21.1
- **Flywheel**: 1.0.6 (API)

## Repository and Port Information (`repos`)
- **Sodium**: https://github.com/CaffeineMC/sodium/tree/1.21.1/stable
- **Voxy**: Voxy has two active ports:
  1. **Fabric port for Sodium 0.8 Beta** (`mc_1211-sodium0.8.12` branch): https://github.com/m3t4f1v3/voxy/tree/mc_1211-sodium0.8.12 (`.jar` in `run/mods`).
  2. **Native NeoForge port for Sodium 0.6.13** (`neoforge-1.21.1` branch): https://github.com/j-shelfwood/voxy-neoforge

*Current Issue Note*: Voxy `.jar` in `run/mods` is Fabric port for Sodium 0.8. Running in NeoForge dev env requires Sinytra Connector, which crashes with `Could not determine clean minecraft artifact path` during `runClient`. Crash cascades to Sodium, aborting config load and throwing misleading `Sodium mod config not found` error.

## Key Files
- `build.gradle` / `gradle.properties`: Build config & versions.
- `src/main/resources/ssrd.mixins.json`: Mixin registration.
- `sable.accesswidener`: Access transformations.
- `src/main/resources/templates/META-INF/neoforge.mods.toml`: Mod metadata template.

## Technical Implementation
### Core Features
- **Long Range Visibility**: Physics objects (SubLevels) visible at DH distances.
- **Sodium Integration**: "Sub-Level Distance" slider in General settings. Slider max is DH or Voxy current set lod distance.
- **Create & Aeronautics Support**: Contraptions (Create trains) and propellers sync and render at physics distances only when part of/tracking Sable sublevel. Normal ground/blocks contraptions restricted to Vanilla Render Distance (VRD) culling and tracking.
- Mod is stable.

### Technical Details (Mixins & Logic)
- `SubLevelTrackingSystem`: Custom server-side sync logic based on `Config.physicsTrackingRange`.
- `RenderSectionManager` & `OcclusionCuller`: Bypass Sodium/Vanilla culling for SubLevels.
- `SodiumConfigBuilderMixin`: UI injection for distance slider into Sodium 0.8.
- `ChunkMap`: Forced contraption chunk tracking.
- `BandedPrimeLimiter`: Bypass Flywheel distance limits for animations.

## Summary of Work
- Decoupled physics rendering from vanilla distance via aggressive Euclidean culling overrides.
- Server-side tracking sync for extreme ranges (bypass vanilla VRD clamps).
- Added manual numeric input `ConfigScreen` (via `/ssrd config`) for precise distance control.
- Disable Sodium slider with stylized tooltips ("Error: No DH Detected") for Voxy compatibility.
- Fixed infinite rendering bug: synchronized client culling with server tracking limits.
- Fixed Create contraption disappearance beyond VRD via spatial chunk tracking fallbacks.
- Refactored UI integration to use reflection for Distant Horizons, preventing crashes when missing.
- Ported SSRD distance slider to Sodium 0.8 `ConfigBuilder` API instead of old `OptionPage` classes.
- Updated `SodiumViewportMixin` to conform to Sodium 0.8 `(III)Z` frustum culling signatures.
- Removed `SSRDForceloadManager` in favor of Sable 2.0.1 native forceloading.
- Added `/gamerule ssrdForceloadLimit` to cap sub-levels non-OP players can forceload, format displays as `(used/max)`.
- Fixed rope disappearing bug on client return by correcting the spatial tracking fallback to check Plot space coordinates instead of World space.
- Fixed stuck/invisible normal Create trains beyond VRD (Issues 36 and 40) by restricting SSRD's entity tracking range extension and client-side unloading blocks to only affect contraptions inside a SubLevel Plot.
- Fixed server tick stall (Issue 42) by removing synchronous debug logging in the hot-path of `SubLevelTrackingSystem.shouldLoad`.

## Planned Features (ASK BEFORE IMPLEMENTING)
- **Fabric Support**: Investigate and implement port for Fabric 1.21.1.

## Installation
- NeoForge 1.21.1
- Required: Sable, Sodium.
- Optional: Distant Horizons.
- Install on Client and Server.

## Claude Instructions (MANDATORY)
- **NEVER PUSH TO GITHUB** without explicit permission.
- **CAVEMAN MODE**: ACTIVE on startup (ULTRA intensity).
- **Windows OS**: System is Windows, not Linux.
- **Validation**: Issue only "fixed" when user says so after game launch.
- **Reference**: Use `repos/` to understand external mod code.
- **Term definitions**: vrd = "Vanilla render distance".
- **External Links**: GitHub Issues at https://github.com/RanoldStranold/SSRD/issues
- **Server/Client Testing**: Sodium 0.8.12 crashes dedicated servers (LWJGL error). To test concurrently: rename `run/mods/sodium-neoforge*.jar` to `.disabled`, run `.\gradlew runServer`, restore `.jar` extension, run `.\gradlew runClient`.
