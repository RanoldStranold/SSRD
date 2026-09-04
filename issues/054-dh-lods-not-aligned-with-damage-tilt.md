# Issue #54 — DH LOD's are not aligned with damage tilt with SSRD

- **URL**: https://github.com/RanoldStranold/SSRD/issues/54
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-14

## Description

Mod Version: 1.8.5
Other mods being used: Sodium 0.8.13-beta.2, Distant Horizons 3.2.0-b, Sable 2.0.3

Taking damage with damage tilt on while using SSRD makes Distant Horizons' LODs not align with the camera. Video and logs attached to original issue:

- Video: https://www.youtube.com/watch?v=qAXEsZIcJAM
- Latest Log: https://mclo.gs/a8CQC9W
- Debug Log: https://mclo.gs/1yQFxPj

Edit by reporter: the same thing seems to happen when you die as well.

## Notes

Damage tilt, like view-bobbing, is folded into the projection matrix client-side in modern MC. This is the same family of bug as the already-fixed view-bobbing issue (#37, see `bugs.md` "Fixed Solutions" and the commit `ed122e3`/v1.8.4 changelog in `claude.md`): the fix reformulated `VanillaDispatcherMixin`'s far-plane rewrite as a row operation using pure pre-bob coefficients captured in `GameRendererProjMixin`/`SSRDState.PURE_PROJ_MATRIX`, and `DHRenderUtilProjMixin` copies `SSRDState.LEVEL_PROJ_MATRIX` into DH. Damage tilt is a separate matrix perturbation (screen shake) from view-bob and may not be captured/excluded the same way — needs verification against current `GameRendererProjMixin`/`DHRenderUtilProjMixin` source, per this project's "do not guess" rule.
