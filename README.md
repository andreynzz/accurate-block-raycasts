# Accurate Block Raycasts

A Fabric mod for Minecraft Java Edition 26.3 that makes intentional visual
openings in supported blocks affect server-authoritative projectile and
line-of-sight raycasts.

An arrow can pass through a verified opening and still collide with a later
block. Mobs use the same geometry for common line-of-sight checks, while solid
parts of the block continue to block both behaviors.

## Status

This is an early development build (`0.1.0-SNAPSHOT`). It supports the vanilla
arrow and the common `LivingEntity` visibility path on the logical server. The
unit suite and all 21 server GameTests currently pass with Java 25.

Supported profiles are manually verified static masks:

- Doors: oak, acacia, bamboo, cherry, jungle, poplar, iron, and every copper
  weathering and wax variant.
- Trapdoors: acacia, bamboo, cherry, crimson, jungle, mangrove, oak, poplar,
  warped, iron, and every copper weathering and wax variant.

Birch, dark oak, pale oak, and spruce trapdoors are deliberately unsupported
because they are visually opaque. They, and every other unsupported block, use
unchanged vanilla raycasting.

## Scope

The mod changes projectile block raycasts and common mob vision only. It does
not alter physical entity collision, pathfinding, player interaction, resource
pack behavior, or block/model texture loading at runtime.

## Development requirements

- Minecraft Java Edition 26.3
- Java 25
- The Gradle wrapper included in this repository

## Build and verify

From the repository root on Windows:

```powershell
.\gradlew.bat test
.\gradlew.bat runGameTest
```

The first command runs deterministic unit tests. The second starts Fabric's
dedicated GameTest server and verifies gameplay behavior for supported door and
trapdoor profile families.

## Documentation

- [Architecture](docs/ARCHITECTURE.md): server authority, shared ray traversal,
  canonical geometry, and integration boundaries.
- [Roadmap](docs/ROADMAP.md): completed work, validation status, and planned
  extensions.

## License

Distributed under the [MIT License](LICENSE).
