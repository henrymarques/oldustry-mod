# Oldustry

**Oldustry** is an industrial automation mod for **Minecraft 26.2** and **NeoForge**. Build an EU power network, process resources with machines, and work your way from basic tools to more capable equipment. It draws inspiration from classic IndustrialCraft 2 while adapting its systems and presentation to modern Minecraft.

Oldustry is in active development. Some systems are playable now, while others are still being built or polished.

## What is available

- **EU power:** generators, cables, transformers, batteries, and storage blocks. Cable loss, voltage tiers, and overvoltage matter when designing a network.
- **Machines:** the macerator, extractor, compressor, recycler, electric and induction furnaces, miner, pump, canner, electrolyzer, and magnetizer.
- **Resources and tools:** rubber trees, tin and uranium, bronze equipment, electric drills, powered tools, and wearable power packs.
- **Modern conveniences:** in-game configuration, useful energy tooltips, and optional integrations with JEI, Jade, and Curios.

See [FEATURES.md](FEATURES.md) for the detailed implementation status and planned work. That document is a development catalog, so it includes unfinished features alongside completed ones.

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

Open an issue to discuss an idea or report a problem, or send a pull request with a focused change. Check [FEATURES.md](FEATURES.md) for current work and design notes. Please only contribute code and assets that you have the right to share.

## License

Oldustry is currently marked **All Rights Reserved** in its project metadata. [TEMPLATE_LICENSE.txt](TEMPLATE_LICENSE.txt) applies to the NeoForge MDK template files, not to the mod as a whole.
