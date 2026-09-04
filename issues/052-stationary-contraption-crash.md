# Issue #52 — Stationary Contraption Crash

- **URL**: https://github.com/RanoldStranold/SSRD/issues/52
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-07

## Description

Mod Version: v1.8.5
Other mods being used: Sodium 0.8.12, Distant Horizons 3.2.0-b

Game crashes from a ticking block entity. An activated Mechanical Bearing mounted to a sublevel causes the crash; reporter hasn't tried with a windmill (or other stationary contraptions).

Crash log attached to the GitHub issue: `crash-2026-08-07_12.55.24-server.txt` (https://github.com/user-attachments/files/30813988/crash-2026-08-07_12.55.24-server.txt) — not fetched into this file, open the issue link to download.

## Notes

Likely related to [Issue #61](061-crash-bearings-in-sublevel.md) (Mechanical Bearing contraption crash) and possibly [Issue #56](056-npe-viewfinder-forceload-serverlevel-init.md) — all three involve block entities on a Sable sublevel crashing during forceload/tick, worth investigating together.
