# Issue #60 — Dedicated Server Crash when using forceloading with SSRD

- **URL**: https://github.com/RanoldStranold/SSRD/issues/60
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-27

## Description

Mod Version: 1.8.5
Other mods being used: Modpack (unspecified)

Upon running the `/ssrd forceload` command on a sublevel, the dedicated server crashes with:

```
Exception in server tick loop
java.lang.NoSuchMethodError: 'dev.ryanhcode.sable.api.sublevel.ticket.SubLevelLoadingTicketType dev.ryanhcode.sable.api.sublevel.ticket.SubLevelLoadingTicket.getType()'
```

Logs: https://mclo.gs/ZlKoWoT

## Notes

`NoSuchMethodError` at runtime (not compile time) is a classic **API/ABI version mismatch** — the server is running against a Sable jar whose `SubLevelLoadingTicket` doesn't have (or has a differently-shaped) `getType()` method compared to the Sable version SSRD 1.8.5 was compiled against (`claude.md` pins Sable 2.0.1+mc1.21.1). Reporter's Sable version isn't stated, but [Issue #59](059-sublevels-stop-rendering-outside-simulation-distance.md) confirms Sable 2.0.5 is in the wild — likely the same root cause: **SSRD needs a Sable-version compatibility check or bump**, since Sable's forceload ticket API appears to have changed between 2.0.1 and 2.0.5. Directly relevant to `SubLevelHoldingChunkMapMixin.java` and wherever SSRD calls into Sable's forceload ticket API (`ServerSubLevelContainer.addForceLoadTicket` per [Issue #51](051-forceload-tickets-bypassed-by-shouldload-shortcircuit.md)).
