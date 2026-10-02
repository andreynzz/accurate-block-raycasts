# Development Roadmap

This roadmap is incremental. Later phases must not be implemented prematurely:
each phase should preserve vanilla fallback behavior, reuse shared geometry for
projectiles and vision, and be validated before the next begins.

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
  and skeleton vision. These tests are defined but have not yet been run in
  this workspace because its available JDK is 17 and the project requires
  Java 25.

## Planned sequence

1. Run the full unit and GameTest suites with Java 25, including the new
   trapdoor coverage.
2. Introduce data-driven profiles.
3. Add an external registration API for mod compatibility.
4. Only then investigate resource-pack/model-derived geometry.

## Scope guardrails

Until the preceding steps are complete, do not add broad global raycast hooks,
automatic texture/model scanning, resource-pack synchronization, physical
collision changes, or client-dependent authoritative logic. Unsupported blocks
must continue to use vanilla behavior.
