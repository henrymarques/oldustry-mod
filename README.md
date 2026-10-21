# Oldustry

**Oldustry** is an industrial automation mod for **Minecraft 26.2** and **NeoForge**. Build an EU power network, process resources with machines, and work your way from basic tools to more capable equipment. It draws inspiration from classic IndustrialCraft 2 while adapting its systems and presentation to modern Minecraft.

Oldustry is in active development. Some systems are playable now, while others are still being built or polished.

## What is available

- **EU power:** generators, cables, transformers, batteries, and storage blocks. Cable loss, voltage tiers, and overvoltage matter when designing a network.
- **Machines:** the macerator, extractor, compressor, recycler, electric and induction furnaces, miner, pump, canner, electrolyzer, and magnetizer.
- **Resources and tools:** rubber trees, tin and uranium, bronze equipment, electric drills, powered tools, and wearable power packs.
- **Modern conveniences:** in-game configuration, useful energy tooltips, and optional integrations with JEI, Jade, and Curios.

## Requirements

- Minecraft **26.2**
- NeoForge **26.2.0.67** or newer for Minecraft 26.2
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

**Contributions to every part of the mod are welcome**: gameplay and machine behavior, NeoForge code, textures and other art, sound, translations, documentation, accessibility, compatibility, testing, and bug reports. You do not need to work on a large feature; focused fixes and polish are valuable too.

Open an issue to discuss an idea or report a problem, or send a pull request with a focused change. Please only contribute code and assets that you have the right to share.

## License

Original Oldustry material is available under the [MIT License](LICENSE). Third-party material, including content adapted from IndustrialCraft 2, is excluded from that grant.
