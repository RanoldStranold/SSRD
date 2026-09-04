# Issue #51 — Force-loaded sub-levels are unloaded because shouldLoad is short-circuited by player distance only

- **URL**: https://github.com/RanoldStranold/SSRD/issues/51
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-05

## Description

Mod Version: 1.8.5
Other mods being used: Sable 2.0.3

### Environment
- Minecraft: 1.21.1 (NeoForge)
- SSRD: 1.8.5
- Sable (Create: Aeronautics): 2.0.3

### Summary
Sub-levels that carry a force-load ticket (added via Sable's official API, `ServerSubLevelContainer.addForceLoadTicket`) are still unloaded and stop rendering once the player moves beyond SSRD's tracking range. Anything that relies on Sable's force-load tickets to keep a sub-level (plane/ship/etc.) loaded is silently broken when SSRD is installed.

### Root cause (as reported)
`SubLevelTrackingSystemMixin` short-circuits Sable's `SubLevelTrackingSystem#shouldLoad(Player, Vector3dc)` at `HEAD` and replaces the entire decision with a player-distance check:

```java
@Inject(method = "shouldLoad", at = @At("HEAD"), cancellable = true)
private void ssd$checkPlayerRequestedRange(Player player, Vector3dc entityPosition,
                                            CallbackInfoReturnable<Boolean> cir) {
    ...
    boolean result = distSq < range * range;
    cir.setReturnValue(result);
}
```

Because this returns before Sable's original body runs, it bypasses Sable's own force-load ticket checks. A force-loaded sub-level should stay loaded regardless of distance, but with SSRD it is reported as `shouldLoad == false` whenever the player is far away — which is exactly what force-load tickets are supposed to prevent.

### Steps to reproduce
1. Install SSRD + Create: Aeronautics.
2. Build a physics body (plane/ship) and force-load its sub-level via `/sable forceload add @`.
3. Fly far away (beyond SSRD's `physicsTrackingRange`).
4. The force-loaded sub-level unloads / stops rendering.
5. Without SSRD it stays loaded as expected.

### Suggested fix (reporter)
Make the `shouldLoad` short-circuit respect Sable's force-load tickets — e.g. if the queried sub-level has an active force-load ticket, return true regardless of distance (check the ticket before applying the distance-only logic). Alternatively, expose an extension point so other mods can influence the result.
