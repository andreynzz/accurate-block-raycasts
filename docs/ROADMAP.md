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
  transform tests. This feature remains pending integration into `develop`.

## Planned sequence

1. Pin Fabric Loom from `1.18-SNAPSHOT` to stable `1.18.2`.
2. Introduce internal `RayProfile` result semantics such as `OPEN`, `SOLID`,
   and `NO_SPECIAL_RESULT`.
3. Inspect Minecraft 26.3 oak-door textures and encode the real
   transparent/opaque pattern as server-side gameplay data, without copying
   Mojang assets.
4. Support a logical 16-by-32 full-door mask or an equivalent composition.
5. Implement `OakDoorRayProfile` using `DoorTransform` and `PixelMask`.
6. Add a minimal internal `RayProfileRegistry`.
7. Implement reusable ray traversal that can skip an `OPEN` supported surface
   and continue to later vanilla collisions.
8. Integrate that traversal with vanilla arrow collision.
9. Integrate the same profile geometry with common mob line of sight.
10. Validate an end-to-end skeleton scenario: it sees a player through a hole,
    shoots, and the arrow passes through the same hole.
11. Add remaining vanilla doors.
12. Add trapdoors.
13. Introduce data-driven profiles.
14. Add an external registration API for mod compatibility.
15. Only then investigate resource-pack/model-derived geometry.

## Scope guardrails

Until the preceding steps are complete, do not add broad global raycast hooks,
automatic texture/model scanning, resource-pack synchronization, physical
collision changes, or client-dependent authoritative logic. Unsupported blocks
must continue to use vanilla behavior.
