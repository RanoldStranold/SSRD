# Issue #61 — Server/Client Crash with Bearings in Sublevel

- **URL**: https://github.com/RanoldStranold/SSRD/issues/61
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-29

## Description

Mod Version: 1.8.5
Other mods being used: Voxy-0.2.15-beta, Sodium-0.8.13

Minecraft crashed when assembling a Mechanical Bearing contraption on a Sable sublevel.

Files attached to original issue (not fetched into this file):
- `crash-2026-08-29_12.15.19-server.txt`: https://github.com/user-attachments/files/31592106/crash-2026-08-29_12.15.19-server.txt
- `debug.log`: https://github.com/user-attachments/files/31592130/debug.log
- `latest.log`: https://github.com/user-attachments/files/31592129/latest.log

## Notes

Same crash family as [Issue #52](052-stationary-contraption-crash.md) (Mechanical Bearing on a sublevel crashing the game/server) — very likely a duplicate root cause, possibly the same underlying bug in different SSRD versions (this report is against 1.8.5, same as #52). Should be investigated together; the actual crash stack traces need to be pulled from the linked attachments to confirm whether they match.
