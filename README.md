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

Download the latest version from [the releases][downloadLink]. Start your server. Edit the config to match your expected limitation. Then reload ingame with `/rsm reload`.

Each time you change the configuration, reload or restart your server.

## Config

You can enable or disable the speed modifier on specific game modes with `enabled_gamemode`.

You can configure for each mount and each block under it the speed modifier with `speed_bonus`.
Values need to be > -1.0.
0 = default speed, 0.5 = 50% faster, -0.3 = 30% slower, 1.0 = 100% faster, 3.0 = 300% faster etc.

Negative values will slow down the mount. It can be use to make horses slower over sand for example.

Any animals ridable by player can be affected, as long as it is on the ground.