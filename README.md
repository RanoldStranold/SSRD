### <big>⚠️**WARNING:**</big> [**Distant Horizons**](https://modrinth.com/mod/distanthorizons) or [voxy](https://modrinth.com/mod/voxy)  and [**Sodium**](https://modrinth.com/mod/sodium) MUST be installed on the Client! <big>⚠️</big>

## <big>**Separate Sable Render Distance**</big> 

**SSRD** is a mod that allows players to see Sable physics objects further than the vanilla render distance. If you're building planes, cars, or ships using **Create: Aeronautics** alongside Distant Horizons or voxy, this mod is basically a necessity (especially in multiplayer).

## Configuration
The slider to change the **Sub-Level Render Distance** is located in the Sodium video settings menu, right under the normal **Simulation Distance** slider. The maximum distance allowed dynamically updates to match your LOD distance (voxy/Distant Horizons) or the server's set maximum. The server's maximum distance can be changed with the command, `/gamerule ssrdMaxRenderDistance ` (Default 128 Chunks)

## Force Loading
As of 2.0.1, Sable has its own method of forceloading, `/sable forceload @`, which requires op. SSRD's own forceloading has been overhauled to be a much more server-friendly method of forceloading. `/ssrd forceload` does not require the player to have op, but does adhere to the forceloading cap, which can be changed with `/gamerule ssrdForceloadLimit` (Default 2). `/ssrd help` will provide a list of possible commands, and their current and default values.



---
<details>
<summary>Important Info</summary>

### Modpack Permission:

You are free to include this mod in modpacks hosted on CurseForge & Modrinth. All other redistribution rights remain reserved.

### Bugs/Issues

* Versions 1.8 and up <big>**REQUIRE**</big> sodium 0.8, and if using voxy with that version you need [this](https://github.com/m3t4f1v3/voxy/tree/mc_1211-sodium0.8.12) port


* Certain Iris shader packs cause rendering issues, an example being Sub-levels can be seen through LODs when certain shader packs are active.


</details>

## License
All Rights Reserved. See [LICENSE](LICENSE) for details.
