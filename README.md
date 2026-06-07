[download]: https://img.shields.io/github/downloads/Mvndi/RoadSpeedMounts/total
[downloadLink]: https://modrinth.com/plugin/roadspeedmounts
[discord-shield]: https://img.shields.io/discord/728592434577014825?label=discord
[discord-invite]: https://discord.gg/RPNbtRSFqG

[ ![download][] ][downloadLink]
[ ![discord-shield][] ][discord-invite]

[**Discord**](https://discord.gg/RPNbtRSFqG) | [**Hangar**](https://hangar.papermc.io/Hydrolien/RoadSpeedMounts) | [**Modrinth**](https://modrinth.com/plugin/roadspeedmounts) | [**GitHub**](https://github.com/Mvndi/RoadSpeedMounts)

# RoadSpeedMounts
Minecraft plugin to increase mounts (horse, cammel etc.) speed when riding over specific blocks.
**Paper** and **Folia** are supported. Any Paper fork should work.


## Usage

Download the latest version. Start your server. Edit the config to match your expected limitation. Then reload ingame with `/rsm reload`.

Each time you change the configuration, reload or restart your server.

## Config

You can enable or disable the speed modifier on specific game modes with `enabled_gamemode`.

You can configure for each mount and each block under it the speed modifier with `speed_bonus`.
Values need to be > -1.0.
0 = default speed, 0.5 = 50% faster, -0.3 = 30% slower, 1.0 = 100% faster, 3.0 = 300% faster etc.

Negative values will slow down the mount. It can be use to make horses slower over sand for example.

Any entity ridable by player can be affected, as long as it is on the ground.
The player itself can be affected if there is no mounts and PLAYER is set in the config.

## Compatibility

This plugin is compatible with Towny & TownyRoads.
It is a soft dependency, the plugin still works on his own.
If Towny is installed, mounts speed can be set up for inside the towns.
If TownyRoads is installed, mounts speed can be set up for on the roads.

## Statistics
[![bStats Graph Data](https://bstats.org/signatures/bukkit/roadspeedmounts.svg)](https://bstats.org/plugin/bukkit/BoneMealLimiter/25354)

# Build, Test & deploy

Feature requests or pull requests are welcome. Concider creating an issue first to talk about your new feature before sending a pull request.

## Build

Clone the [repo](https://github.com/Mvndi/RoadSpeedMounts) `git clone git@github.com:Mvndi/RoadSpeedMounts.git`

Build with `./gradlew assemble`. The plugin .jar file will be in `build/libs/`.

## Test

You can test the plugin directly in a Minecraft server with `./gradlew runServer`.
You can also try it on an other server by placing the .jar in `plugins/`.
You might want to update the plugin config to fit your needs.

## Deploy

### To Github releases, Hangar & Modrinth

Push tag to git to trigger a github action release that will create a new Github release and publish to Hangar & Modrinth.
```sh
git tag 1.2.3
git push --tags
```

### to Maven central

*Require ossrhUsername & ossrhPassword in ~/.gradle/gradle.properties*

```sh
./gradlew clean publish
./gradlew jreleaserDeploy
```
The published lib will be available [on maven central](https://central.sonatype.com/artifact/net.mvndicraft.roadspeedmounts/roadspeedmounts).