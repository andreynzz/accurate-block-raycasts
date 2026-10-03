# Accurate Block Raycasts

A Fabric mod for Minecraft Java Edition 26.3 that makes intentional visual
openings in supported blocks affect server-authoritative projectile and
line-of-sight raycasts.

An arrow can pass through a verified opening and still collide with a later
block. Mobs use the same geometry for common line-of-sight checks, while solid
parts of the block continue to block both behaviors.

## Status

This is the initial public release (`0.1.1`). It supports the vanilla
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

## Data-driven profiles

Server data packs may add or override a supported door or trapdoor profile.
Place one JSON file per block at
`data/<namespace>/accurateblockraycasts/ray_profiles/<name>.json`. The loader
runs during the normal server data reload; invalid files are ignored and leave
vanilla behavior unchanged for that block unless a built-in profile exists.

Masks contain exactly sixteen strings, listed bottom-to-top. `#` is solid and
`.` is an opening. A door has one mask for each half; a trapdoor has one mask:

```json
{
  "type": "door",
  "block": "example:perforated_door",
  "lower": ["################", "################", "################", "################", "################", "################", "################", "################", "################", "################", "################", "################", "################", "################", "################", "################"],
  "upper": ["################", "################", "################", "################", "###....##....###", "###....##....###", "###....##....###", "################", "################", "###....##....###", "###....##....###", "###....##....###", "################", "################", "################", "################"]
}
```

The declared block must be a registered `DoorBlock` or `TrapDoorBlock` matching
the chosen `type`. Profiles still use the same server-side geometry for arrows
and common mob line of sight; this feature does not inspect client textures or
models.

## Mod registration API

Other mods may register one server-safe `RayProfile` for one block during their
common initialization through `RayProfileRegistry.INSTANCE.register(block,
profile)`. The profile is evaluated by the same shared arrow and mob-vision
paths as built-in profiles. A datapack profile for that block takes precedence;
duplicate external registrations fail explicitly rather than depending on mod
load order.

## Offline door-profile generator

`generateDoorRayProfile` converts two transparent 16-by-16 door PNGs into a
server datapack JSON file. Fully transparent pixels become openings (`.`); all
other pixels, including partially transparent ones, are conservatively solid
(`#`). The tool reads image rows into the required bottom-to-top mask order.

```powershell
.\gradlew.bat generateDoorRayProfile --args="--block example:perforated_door --bottom path\to\bottom.png --top path\to\top.png --output path\to\ray_profile.json"
```

Copy the generated file to
`data/<namespace>/accurateblockraycasts/ray_profiles/<name>.json` in a server
datapack. This is an offline development tool: gameplay never reads client
resource packs or models.

For packs following the vanilla door-model convention, supply `--pack` instead
of the two PNG paths. The generator resolves `block/<door>_bottom_left`, its
parents, and its `bottom`/`top` texture slots inside that directory.

## CI and releases

GitHub Actions validates every push to `develop`, every pull request targeting
`develop`, and manually requested runs. The CI workflow verifies the Gradle
Wrapper, runs unit tests and server GameTests, builds the mod, and uploads the
generated JARs as workflow artifacts.

To publish an approved release commit, create and push a tag in the
`vMAJOR.MINOR.PATCH` format:

```powershell
git tag -a v0.1.1 -m "Release v0.1.1"
git push origin v0.1.1
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
