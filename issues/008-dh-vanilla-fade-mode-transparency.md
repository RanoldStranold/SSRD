# Issue #8 — Problems with DH "Vanilla Fade Mode"

- **URL**: https://github.com/RanoldStranold/SSRD/issues/8
- **Status**: Open
- **Labels**: (none)
- **Opened**: 2026-05-19
- **Reporter**: Carbonn104

## Description

As mentioned in the known issues on this mod's modrinth page, sub-levels are visible through LODs with certain shader packs. I have observed that this also happens _without_ shaders when DH's "Vanilla Fade Mode" is set to None. It's mentioned in #5 that it's issue with DH fade applying to sub-levels doesn't happen with shaders, I'm guessing that the shader overrides DH's "Vanilla Fade Mode" leading to the same issue as when done manually.

Examples of these problems (screenshots in original issue, not reproduced here):
- Vanilla Fade Mode set to Single Pass/Double Pass vs None
- Entities also render on top of water fog (also seen in #7), not fixed by changing "Vanilla Fade Mode"
- Same issue with block entities
- Also happens with block entities and transparents on sub-levels behind LODs that are behind transparents (water, stained glass, fog etc.)
- When "Vanilla Fade Mode" is set to None this seems to hide certain blocks on the overlapping sub-level (honey blocks and stained glass, not normal glass)

Reporter re-tested on SSRD v0.3 (vs v0.4): all issues reproduced except entities-over-water-fog-without-shaders and sub-levels-over-LODs-with-shaders (those two were new/worse in 0.4).

Log if needed: https://mclo.gs/U9dbsiJ

Modlist: create-1.21.1-6.0.10.jar, create-aeronautics-bundled-1.21.1-1.2.1.jar, create_power_loader-2.0.4-mc1.21.1.jar, DistantHorizons-3.0.3-b-1.21.1-fabric-neoforge.jar, iris-neoforge-1.8.12+mc1.21.1.jar, sable-neoforge-1.21.1-1.2.2.jar, SSRD-0.4-1.21.1.jar, sodium-neoforge-0.6.13+mc1.21.1.jar

## Maintainer notes (from bugs.md, pre-existing)

- **Severity**: Low, **Ease**: Low
- RanoldStranold (2026-05-24): "Scared to touch."
- Carbonn104 (2026-05-24): "lol"
