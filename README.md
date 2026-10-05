<!--
SPDX-FileCopyrightText: 2023 Linnea Gräf <nea@nea.moe>

SPDX-License-Identifier: CC0-1.0
-->



<div align="center">

# Firmod

![firmod logo](./docs/firmod_logo_256_nobg.webp)

<hr>

[![Forum Thread](https://img.shields.io/badge/Forum%20Thread-blue?style=flat-square)](https://hypixel.net/threads/firmod-a-skyblock-mod-for-1-20-1.5446366/)
[![Discord](https://img.shields.io/discord/1088154030628417616?style=flat-square&logo=discord)](https://discord.gg/64pFP94AWA)
[![Modrinth](https://img.shields.io/modrinth/dt/IJNUBZ2a?style=flat-square&logo=modrinth)](https://modrinth.com/mod/firmod)
[![Github Releases](https://img.shields.io/github/downloads/nea89o/Firmod/total?style=flat-square&logo=github)](https://github.com/nea89o/firmod/releases)

</div>


## Currently working features

- Item List of all SkyBlock Items
- Recipe Viewer for Crafting Recipes
- Recipe Viewer for Forge Recipes
- ... as well as many more custom recipe types.
- NPC waypoints
- A storage overview as well as a full storage overlay
- A crafting overlay when clicking the "Move Item" plus in a crafting recipe
- Cursor position saver
- Slot locking
- Support for custom texture packs (loads item models from `firmskyblock:<skyblock id>` before the vanilla model gets
  loaded)
- Fairy soul highlighter
- A hud editor powered by [Jarvis](https://github.com/romangraef/jarvis)
- Basic Config Gui (/firm config). Still needs improvement, but for the basics it's enough. You can also
  use `/jarvis options` to search through all config options
- and more (maintaining a feature list properly is a task for the future).

## Installation

Firmod needs the following libraries to work:

- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
 
As well as (for the item list):
- 
- [RoughlyEnoughItems](https://modrinth.com/mod/rei)
- [Architectury](https://modrinth.com/mod/architectury-api)
- [Cloth Config](https://modrinth.com/mod/cloth-config)


You can download Firmod itself on [Modrinth](https://modrinth.com/mod/firmod) or on
[GitHub](https://github.com/romangraef/firmod/releases).

### Usage

Everything is configurable via either `/firm config`, or via `/jarvis options`.

### Recommendations

- [DulkirMod-Fabric](https://github.com/inglettronald/DulkirMod-fabric), a versatile SkyBlock mod.
- [Skyblocker](https://modrinth.com/mod/skyblocker-liap), a very feature rich SkyBlock mod.
- [Sodium](https://modrinth.com/mod/sodium) and [Lithium](https://modrinth.com/mod/lithium), both excellent performance mods.
- [ModMenu](https://modrinth.com/mod/modmenu), just to see which mods you have installed, and to configure some of them.

## Infos about the development

### Licensing and contribution policy

Most of this mod is licensed under a GPL-3.0-or-later license. Some resources may also be licensed using creative
commons licenses. You can use the [reuse](https://github.com/fsfe/reuse-tool) spec to check the concrete licenses for
each file. See the licenses folder for the concrete license terms of each license.

Whenever you add Content to this repository, you license that Content under those terms as specified by reuse, and you
agree that you have the right to license that Content under those terms. If you want your Content to be available under
a different license, or with explicit credit to you, make sure to request so in your pull request, or to provide an
appropriate reuse `.license` file. (Note that an incompatible deviating license might result in your contribution being
rejected.)

Contributions are tentatively welcomed. The structure of the mod is probably not really transparent to newcomers, but if
you are interested, feel free to tackle any [issues](https://github.com/nea89o/Firmod/issues/) or create your own
features. If you need any help contributing feel free to join the [discord].

### Development

Use Java 21.

Running `./gradlew :build` will create a mod jar in `build/libs`

For a more complete development guide check out the [contributing guide](./CONTRIBUTING.md).

### Affiliation to NEU

This codebase was originally labeled as "NotEnoughUpdates 1.19". While the author is a maintainer to NEU, this project
is not affiliated with NEU beyond personal connections. There may still be references to NEU due to old names or
overlapping features and libraries.

[discord]: https://discord.gg/64pFP94AWA

## Minecraft 26.x builds in this fork

Install the jar matching your exact Minecraft version from this organization's GitHub Releases, together with Fabric API and Fabric Language Kotlin. Main tracks 26.3; `minecraft-26.2` and `minecraft-26.1.2` retain the already ported sources for those releases. The three jars are rebuilt from source and collected in one GitHub release. The older 26.1.2 variant retains its historical `firmmod` mod ID; the newer variants use `firmod`.

All branches build on Java 25. Run `./gradlew assemble test runSmoke -PreleaseVersion=26.3.1` on each matching branch. The `runSmoke` task applies every dynamically discovered production mixin, including the installed optional integrations, and verifies the plugin reports all of them applied. Its test mod does not enter the published jar. GitHub Actions uses hosted Ubuntu runners for the three builds.

The 26.3 port updates SDL keyboard/mouse input, render pipeline types, Authlib services, item component patches, registry lookups, container protection hooks, block sounds, and current Jade/Gender integration targets. Saved logical keyboard bindings, mouse bindings and modifier combinations migrate from GLFW to SDL. Existing platform-specific raw scan-code bindings may need rebinding after changing input backends.

26.3's MoulConfig dependency is built from official NotEnoughUpdates/MoulConfig commit `4eaeb73cdaebb083d1d56d310fe1851ded1b7828`; source archive SHA-256 `4b26888b24d5f88223b24a688a67cc39455cacf435ba480e23a9ff35af04523e`. The checked-in Gradle bootstrap verifies that archive and builds the library from source, preserving upstream licenses. Its compatibility patches preserve MoulConfig's Fabric metadata/access widener and separate SDL keyboard codes from mouse binding codes. Install JDK 8 and set `JAVA_HOME_8_X64` for MoulConfig's shared module, with Java 25 selected for Minecraft. Actions installs both toolchains.

The upstream Modrinth project belongs to a different team. This fork does not automatically publish into that project.
