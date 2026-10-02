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
- A JDK 25 or newer (the Fabric Loom version used by this project cannot run
  on Java 21 or earlier)
- The Gradle wrapper included in this repository

## Build and verify

Gradle itself must be launched with JDK 25 or newer; configuring a Java
toolchain in `build.gradle` is not enough because Loom is loaded before that
configuration can take effect. Confirm the active Java version first:

```powershell
java -version
```

The output must report version `25` or newer. If it does not, install a JDK 25
distribution and point the current PowerShell session at it before invoking
Gradle (replace the example path with the installed JDK directory):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-25'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

For IntelliJ IDEA, set **Settings | Build, Execution, Deployment | Build
Tools | Gradle | Gradle JVM** to the same JDK 25 installation.

From the repository root on Windows:

```powershell
.\gradlew.bat test
.\gradlew.bat runGameTest
```

The first command runs deterministic unit tests. The second starts Fabric's
dedicated GameTest server and verifies gameplay behavior for supported door and
trapdoor profile families.

## CI and releases

GitHub Actions validates every push to `develop`, every pull request targeting
`develop`, and manually requested runs. The CI workflow verifies the Gradle
Wrapper, runs unit tests and server GameTests, builds the mod, and uploads the
generated JARs as workflow artifacts.

To publish an approved release commit, create and push a tag in the
`vMAJOR.MINOR.PATCH` format:

```powershell
git tag -a v0.1.0 -m "Release v0.1.0"
git push origin v0.1.0
```

The release workflow validates the tag, builds the JAR using its version, then
creates a GitHub Release with generated notes and the production JAR attached.
It currently publishes only to GitHub Releases; Modrinth and CurseForge are
planned for a later release process.

## Documentation

- [Architecture](docs/ARCHITECTURE.md): server authority, shared ray traversal,
  canonical geometry, and integration boundaries.
- [Roadmap](docs/ROADMAP.md): completed work, validation status, and planned
  extensions.

## License

Distributed under the [MIT License](LICENSE).
