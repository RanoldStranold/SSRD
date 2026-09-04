# Issue #50 — SSRD not working with Shaders (Makeup - Ultra Fast)

- **URL**: https://github.com/RanoldStranold/SSRD/issues/50
- **Status**: Open
- **Labels**: bug
- **Opened**: 2026-08-04

## Description

Mod Version: 1.8.5

Same issue occurs with BSL Shaders and Complementary Shaders, currently reproduced with Makeup Ultra Fast shaders.

Mod is on a server and client. With shaders disabled, Sable entities render properly beyond vanilla render distance. With shaders enabled, rendering reverts to vanilla render distance regardless of settings changes.

Saw issue #28 has a similar problem, but in this case reporter disabled every Anti-Aliasing and Ambient Occlusion setting with no difference.

Particle effects for the hot air burner render properly beyond vanilla render distance, but nothing else does.

Using Makeup - Ultra Fast 9.5c, behaves the same on all profiles and advanced options.

### Full mod list (large modded Cobblemon/Create pack)

3D Skin Layers, [EMF] Entity Model Features, [ETF] Entity Texture Features, [Let's Do Addon] EMI Compat, [Let's Do] Bakery/Beachparty/Brewery/Camping/Candlelight/Farm & Charm/Furniture/HerbalBrews/Meadow/Vinery, Accessories, Aeronautics Camera Sync, AI Improvements, AllTheMons x Mega Showdown, AmbientSounds, Amendments, AppleSkin (+ mizunos variant), Architectury API, Athena, Balm, Berry Pouch [Cobblemon], Better Cobblemon Spectation, Better ModList, Better Ping Display, Biolith, Blue's Cobblemon Utilities, Carry On, Catch Indicator, Chat Heads, Chunky, Chunky Border, Climbable Ropes for Create Aeronautics, Cloth Config API, CobbleDollars, CobbleFurnies, Cobbleloots, Cobblemon (+ many Cobblemon addons: Accessories & More, additions, Battle Extras, cafe, Capture XP, Catch Rate Display, Challenge, Counter, Emissive Ores, Fight or Flight Reborn, Firework Capsules, Info for REI/JEI/EMI, Journey Mounts, Occupied Pokeballs, Pokemon Badges, Pokenav, Raid Dens, Rustling Spots, Secret Base Trainer, Spawn Alerts, Stone Statues, Tim Core, Trials Edition, Unchained), Cobblemon: Mega Showdown/PlayerXP/SafePastures, CobbleSafari, CobbleworkersCobbreeding, Concurrent Chunk Management Engine (NeoForge), Continuity, Controlling, Copycats+ aeronautics weight, Create, Create Aeronautics (+ Compatibility), Create Crafts & Additions, Create Deco, Create Deep Seas, Create Propulsion: Simulated, Create Slice & Dice, Create: Aeroworks/Bells & Whistles/Cobblemon Balls Overhaul/Connected/Copycats+/Design n' Decor/Dreams & Desires/Interiors/Power Loader/Threaded Trains, CreativeCore, Cristel Lib, Curios API, Distant Horizons, Distraction Free Recipes, E19 - Cobblemon Minimap Icons, Embers Text API, EMI (+ Addon/Enchanting/Loot/Create Schematics/EMIffect), Entity Culling, Explorer's Compass, Extra Move Animations (Cobblemon), Fabric Language Kotlin, Farmer's Delight, Fast Noise, FastSuite, FerriteCore, Forgified Fabric API, Fresh Waystones Texture, Friends&Foes, Furnies, Fzzy Config, Gardens of the Dead, GeckoLib, Geophilic (+ Backport), Glitch Core, Handcrafted, Hybrid Aquatic, ImmediatelyFast, Iris & Oculus Search, Iris Shaders, Jade, Just Zoom, Konkrete, Kotlin for Forge, Lithium, Lithostitched, Lootr, Lootrmon, MakeUp - Ultra Fast, Mizuno Connected Texture/Emissive Ores/Connected Glass/16 Craft/Modern Mizuno's, ModernFix, Moonlight Lib, More Radical Trainers: SV, Nature's Compass, Navas ZA Megas, No Chat Restrictions, Nullscape, owo-lib, peacefulness, Placebo, Platform, playerAnimator, Pokeblocks, Pokopia Cosmetic Forms, Polymorph, Presence Footsteps, Rad Gyms, Radical Cobblemon Trainer Textures Plus/Trainers/Trainers API, Reese's Sodium Options, ResourcefulConfig, Resourceful Lib, Sable, Searchables, Seasonal Let's Do, SeasonHud, Separate Sable Render Distance, Serene Seasons, Serene Shrubbery, ServerCore, Show Your Identity, SimpleTMs, Sinytra Connector, Sodium (+ Dynamic Lights/Extra/Options API), Sophisticated Backpacks/Core, Sound Physics Remastered, spark, Stardew Fishing, Structure Layout Optimizer, Supplementaries (+ Squared), Tectonic, TerraBlender, Terralith, The Roads More Travelled, threadtweak, Toni's Immersive Lanterns, Towns and Towers, Txni Lib, Vanilla Backport, Vivillon Pride Patterns, Waystones, When Dungeons Arise (+ Seven Seas), William Wythers' Overhauled Overworld, Xaero's Minimap/World Map, YetAnotherConfigLib (YACL)

Note: **Sinytra Connector** is present in this modpack — SSRD's own project notes (see `claude.md`) record that Sinytra Connector + Sodium 0.8 in a NeoForge dev environment crashes with `Could not determine clean minecraft artifact path`, cascading into a misleading "Sodium mod config not found" error. May or may not be related to this report; worth checking whether Sinytra Connector interferes with SSRD's Sodium 0.8 `ConfigBuilder`/slider injection or its Iris/shader interaction path.
