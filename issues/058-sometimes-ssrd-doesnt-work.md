# Issue #58 — sometimes ssrd doesn't work

- **URL**: https://github.com/RanoldStranold/SSRD/issues/58
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-20

## Description

Mod Version: 1.8.5
Other mods being used: Sodium, Distant Horizons, Sable, Create, Create Aeronautics

Reporter tried to identify why SSRD wasn't working in their modpack by removing mods in batches. Even after removing almost everything, the issue with the existing world wasn't fixed, though it doesn't occur in a new world.

Logs attached to original issue (not fetched into this file):
- `latest.log`: https://github.com/user-attachments/files/31280907/latest.log
- `debug.log`: https://github.com/user-attachments/files/31280962/debug.log

## Notes

Reporter's own testing narrows this to **world/save-state corruption or stale persisted config**, not a mod conflict (repro is world-specific, reproduces on a nearly-bare modlist, doesn't happen on a fresh world). Likely candidates given the current codebase: stale `serverMaxTrackingChunks` / `physicsTrackingRange` persisted client-side config, or a Sable sublevel state saved before an SSRD version change. Needs the attached logs actually reviewed before forming a real hypothesis — thin report.
