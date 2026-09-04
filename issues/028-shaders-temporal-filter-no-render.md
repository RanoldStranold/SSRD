# Issue #28 — Shaders such as Complementary with temporal filter enabled doesn't render sublevels

- **URL**: https://github.com/RanoldStranold/SSRD/issues/28
- **Status**: Open
- **Labels**: (none)
- **Opened**: 2026-06-16
- **Reporter**: IssueReportingGuy

## Description

With temporal filter enabled: sublevels do not render (screenshot in original issue).
Without it: sublevels render correctly (screenshot in original issue).

The setting is located in the camera section in Complementary. Also, for some reason using the forceload command doesn't allow rendering of sublevels — could only make them render using Create: Power Loader.

## Maintainer notes (from bugs.md, pre-existing)

- **Severity**: Medium, **Ease**: Low
- RanoldStranold (2026-06-16): Asked for temporal filter function, default state, DH vs Voxy, forceload type used, Sodium version.
- IssueReportingGuy (2026-06-21): Temporal filter is TAA, default on. Using DH, Sodium 0.8.12. Used SSRD/Sable forceload (no render); only Create: Power Loader worked.
- **Fix**: None
