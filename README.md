# Oldustry

**Oldustry** is an industrial automation mod for **Minecraft 26.1.2** and **NeoForge**. Build an EU power network, process resources with machines, and work your way from basic tools to nuclear power, quantum armor, and crop breeding. It is a modern reading of classic IndustrialCraft 2 — the same EU, cables, machines, and progression feel — adapted to current Minecraft APIs and presentation.

The planned classic feature set is **complete**. Development stays active for quality-of-life improvements, polish, compatibility, and new optional features on top of that base.

## Features

- **EU power:** generators, cables, transformers, batteries, and storage. Cable loss, voltage tiers, and overvoltage matter when designing a network.
- **Machines:** macerator, extractor, compressor, recycler, electric and induction furnaces, miner, pump, canner, electrolyzer, magnetizer, mass fabricator, terraformer, and Tesla coil.
- **Nuclear:** reactor with chambers, fuel cells, heat vents/exchangers, plating, condensators, and related components.
- **Teleportation:** teleporter paired with the frequency transmitter.
- **Resources and world:** rubber trees, tin and uranium, foam, painters, scaffolds, and advanced crafting.
- **Tools and combat:** bronze gear, electric drills and chainsaw, mining laser, nano saber, hazmat, nano and quantum suits, jetpacks, and related equipment.
- **Crops and booze:** crop sticks, crossbreeding, Crop-Matron, Cropnalyzer, barrel fermentation, and mugs.
- **Modern conveniences:** in-game configuration, energy tooltips, and optional integrations with JEI, Jade, and Curios.

## Requirements

- Minecraft **26.1.2**
- NeoForge **26.1.2.127** or newer for Minecraft 26.1.2
- JDK **25** to build from source

JEI, Jade, and Curios are optional.

## Build and run

From this directory:

```bash
./gradlew build
./gradlew runClient
```

On Windows, use `gradlew.bat` instead of `./gradlew`. The built mod JAR is placed in `build/libs/`.

## Contributing

**Contributions to every part of the mod are welcome**: gameplay and machine behavior, quality of life improvements, NeoForge code, textures and other art, sound, translations, documentation, accessibility, compatibility, testing, and bug reports. You do not need to work on a large feature; focused fixes and polish are valuable too.

Open an issue to discuss an idea or report a problem, or send a pull request with a focused change. Please only contribute code and assets that you have the right to share.

## License

Original Oldustry material is available under the [MIT License](LICENSE). Third-party material, including content adapted from IndustrialCraft 2, is excluded from that grant.
