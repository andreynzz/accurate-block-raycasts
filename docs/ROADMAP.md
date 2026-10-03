# Development Roadmap

## MVP release

Version `0.1.1` is the completed MVP release. It establishes server-authoritative
opening geometry, projectile traversal, and the shared common
`LivingEntity.hasLineOfSight` integration. The gameplay tests for that release
validate arrows and skeleton line of sight.

Future work is post-MVP improvement work. Each improvement must preserve
vanilla fallback behavior, reuse shared geometry where applicable, and be
validated before release.

## Completed

- Fabric 26.3 project bootstrap and Gradle wrapper.
- Git Flow branches (`main`, `develop`, and focused feature branches).
- `PixelMask`.
- `Vec3`, `Ray`, and `Plane` geometry primitives.
- Finite ray/plane intersection.
- Vanilla projectile and common line-of-sight call-path investigation.
- `DoorTransform`, `DoorLocalCoordinates`, and `DoorIntersection` on
  `feature/door-transform`, including all-facing, open/hinge, and door-half
  transform tests, integrated into `develop`.
- Internal `RayProfile` contract and explicit `OPEN`, `SOLID`, and
  `NO_SPECIAL_RESULT` result semantics.
- Manually verified server-side opening masks for the vanilla 26.3 oak door:
  an opaque lower half and four upper-half windows.
- Immutable 16-by-32 logical door-mask composition.
- `OakDoorRayProfile`, with all-facing and open/hinge mask-sampling tests.
- Minimal `RayProfileRegistry`, with oak-door-only resolution and vanilla
  fallback for unsupported blocks.
- Shared block traversal that skips an `OPEN` oak-door voxel and continues to
  the next vanilla hit.
- Server-side vanilla-arrow block-trace integration.
- Server-side common living-entity line-of-sight integration.
- Fabric Loom pinned to stable `1.18.2`.
- Server GameTests for an arrow traversing an oak-door opening to a later block
  and skeleton line of sight through that same opening.
- Manual development-client validation: player and skeleton arrows traversed an
  opening, and a skeleton could see a player behind the door.
- Reusable door-profile sampling and registry-based arrow in-ground checks.
- Manually verified profiles for acacia, bamboo, cherry, jungle, poplar,
  iron, and all copper oxidation and wax variants. Visually opaque doors stay
  on the equivalent vanilla-blocking path.
- Twelve server GameTests for representative wood, iron, and copper doors:
  arrow traversal through openings, solid-pixel blocking, and skeleton vision.
- `TrapdoorTransform` and `TrapdoorLocalCoordinates`, covering `FACING`,
  `OPEN`, `HALF`, all horizontal facings, parallel rays, and mask boundaries.
- Shared planar `TrapdoorRayProfile` sampling through `RayProfileRegistry`.
- Manually verified static profiles for acacia, bamboo, cherry, crimson,
  jungle, mangrove, oak, poplar, warped, iron, and every copper oxidation/wax
  variant. Birch, dark oak, pale oak, and spruce trapdoors intentionally retain
  vanilla fallback because they are visually opaque.
- Nine server GameTests for representative wood, iron, and copper trapdoors:
  arrow traversal through an opening to a later block, solid-pixel blocking,
  and skeleton vision. The Java 25 GameTest run completed successfully with
  all 21 required gameplay tests passing.
- Server-data profile loading: a reload listener reads opt-in door and
  trapdoor masks from `data/<namespace>/accurateblockraycasts/ray_profiles`,
  validates their block type and mask shape, and atomically installs valid
  profiles. Data profiles may override built-in profiles; invalid files retain
  the safe vanilla/built-in fallback.
- The Java 25 unit suite passes after the data-profile implementation.
- Direct parser tests cover a valid profile and rejection of invalid block
  types and malformed masks.
- Two server GameTests load a datapack-defined `minecraft:birch_door` profile
  and verify both arrow traversal to a later block and skeleton line of sight;
  the full Java 25 GameTest suite passes with 23 required tests.
- External mod registration through `RayProfileRegistry`, with explicit
  duplicate rejection and datapack precedence over mod-provided profiles.
- Investigation of Minecraft 26.3 model loading confirmed it is client-only;
  automatic runtime resource-pack derivation is deferred to preserve server
  authority. See `MODEL_GEOMETRY_INVESTIGATION.md`.
- Offline PNG-to-datapack generation for 16-by-16 door textures, preserving
  conservative alpha handling and server-owned profile distribution.

## Post-MVP improvements

1. Investigate and extend mob perception paths beyond the skeleton-validated
   common line-of-sight path. Add representative GameTests for each newly
   supported path; do not claim universal mob support until it is verified.
2. Make player block interaction respect supported openings: a click through
   an `OPEN` profile sample must continue to the later target instead of
   activating the door or trapdoor. Keep solid samples and unsupported blocks
   on their vanilla interaction path.
3. Optionally add further offline resource-pack/model resolution to the
   profile generator when its supported model subset is specified.

## Post-MVP branch map

Post-MVP work branches from `develop` and returns there through review. The
following branches are the current intended work streams:

```text
main
 └── release/0.1.1                 completed MVP release

develop
 ├── build/curseforge-jar-prep      prepare a distributable CurseForge JAR
 ├── ci/curseforge-release          publish approved prepared JARs to CurseForge
 ├── feature/mob-vision-paths      extend opening-aware vision beyond skeleton coverage
 ├── feature/interaction-through-openings
 │                                  let player clicks pass through OPEN samples
 ├── perf/raycast-hot-path         measure and optimize shared raycast hot paths
 └── release/0.1.2                 release preparation after selected work is integrated
     ├── main                      reviewed release merge and tag
     └── develop                   merge-back after release
```

Recommended order:

1. `build/curseforge-jar-prep` can proceed independently. It prepares the
   distributable JAR and its release-facing metadata: mod metadata, icon,
   version and artifact naming, dependency declarations, license inclusion,
   and any CurseForge-required packaging details. It must verify the generated
   JAR before a release is published.
2. `ci/curseforge-release` depends on the prepared artifact. It should publish
   only approved release JARs and obtain its CurseForge credentials from CI
   secrets.
3. `feature/mob-vision-paths` must first identify the actual Minecraft 26.3
   perception paths used by failing mobs, then add focused integrations and
   representative GameTests.
4. `feature/interaction-through-openings` must inspect the Minecraft 26.3
   player-interaction raycast path before selecting a narrow integration point.
   It must preserve vanilla interaction for solid samples and unsupported
   blocks.
5. `perf/raycast-hot-path` follows the new gameplay integrations so it can
   optimize measured shared-path costs rather than speculative code paths.
6. Create `release/0.1.2` from `develop` only after the selected improvements
   are validated. Merge it to `main` by PR, tag the release, then merge it back
   to `develop`.

The vision and interaction changes intentionally remain separate branches:
they affect different vanilla call paths and have independent test criteria.

## Scope guardrails

Do not add broad global raycast hooks, automatic runtime texture/model
scanning, resource-pack synchronization, physical collision changes, or
client-dependent authoritative logic. Unsupported blocks must continue to use
vanilla behavior.
