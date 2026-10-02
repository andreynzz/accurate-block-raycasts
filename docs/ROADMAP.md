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

## Planned sequence

1. Pin Fabric Loom from `1.18-SNAPSHOT` to stable `1.18.2`.
2. Validate an end-to-end skeleton scenario: it sees a player through a hole,
    shoots, and the arrow passes through the same hole.
3. Add remaining vanilla doors.
4. Add trapdoors.
5. Introduce data-driven profiles.
6. Add an external registration API for mod compatibility.
7. Only then investigate resource-pack/model-derived geometry.

## Scope guardrails

Until the preceding steps are complete, do not add broad global raycast hooks,
automatic texture/model scanning, resource-pack synchronization, physical
collision changes, or client-dependent authoritative logic. Unsupported blocks
must continue to use vanilla behavior.
