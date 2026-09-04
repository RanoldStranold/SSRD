# Issue #59 — Sable Sublevels stop rendering outside vanilla simulation distance when moved from their assembled chunks

- **URL**: https://github.com/RanoldStranold/SSRD/issues/59
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-25

## Description

Mod Version: v1.8.5
Other mods being used: Create 6.0.10, Create Aeronautics 1.3.1, Distant Horizons 3.2.0-b, Sable 2.0.5, Sodium 0.8.12-alpha.2

Moving a Sable sublevel outside the chunk it was assembled in causes it to stop being rendered by SSRD when moving outside the vanilla simulation distance.

### Steps to reproduce
1. Assemble an Aeronautics vehicle.
2. Move the vehicle outside the chunk it was assembled in (just one is enough).
3. Move outside the vanilla simulation distance.

### Expected result
The sub level remains visible.

### Actual result
The sub level disappears.

## Notes

This looks like the same class of bug as the already-fixed rope/spatial-fallback issue (`bugs.md` "Fixed Solutions": Issue 38, fixed in `ChunkMapMixin` by checking Plot-space coordinates instead of World/overworld coordinates) and the Issues 36/40 tracking-range-extension fix in `ChunkMapTrackedEntityMixin`. Worth checking whether this is a regression against Sable 2.0.5 specifically (reporter's Sable version is newer than the 2.0.1 pinned in `claude.md`) — could be an API/behavior change in Sable's sublevel tracking that SSRD's chunk-tracking mixins no longer account for once the sublevel's origin chunk changes.
