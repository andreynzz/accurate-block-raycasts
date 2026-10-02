# Architecture

## Purpose

Accurate Block Raycasts is a Minecraft Fabric mod for Java Edition 26.3. Its
goal is to let intentional visible openings in supported blocks affect
gameplay raycasts: projectiles may pass through an opening and mobs may see
through that same opening, while solid portions still block both.

The mod does not change physical entity collision, pathfinding, interaction,
or unsupported-block behavior.

## Authority and fallback

Projectile collision and AI visibility are server-authoritative. Therefore,
all gameplay geometry must be available in common/server code and cannot rely
on client rendering, baked models, texture atlases, or player resource packs.

Only explicitly supported blocks receive special handling. Every unsupported
block must retain vanilla raycast behavior; the project will use narrow
integration points rather than replacing global level clipping.

## Shared ray geometry

Projectile collision and mob line of sight must evaluate the same profile
geometry. The intended flow is:

```text
projectile / mob vision
          |
          v
 shared ray traversal
          |
          v
 RayProfileRegistry -> RayProfile -> geometry
```

Gameplay integration and Mixins belong at the edge of this flow. Geometry and
profiles must not know about arrows, mobs, networking, or client rendering.

`RayProfile` is the internal contract for one explicitly supported block type.
It evaluates a block state, its world position, and a finite shared `Ray`, then
returns `OPEN`, `SOLID`, or `NO_SPECIAL_RESULT`. `OPEN` permits a future
traversal to continue past the supported surface; `SOLID` preserves blocking;
and `NO_SPECIAL_RESULT` delegates fully to vanilla. The profile itself does
not traverse the world or select unsupported blocks.

## Geometry foundation

`PixelMask` is an immutable, compact 16-by-16 solid/passable mask backed by
packed bits. It is intended to describe the logical blocking pattern once for
each supported profile, not once per world-facing direction.

For the initial oak-door profile, the masks are manually encoded server-side
from a one-time inspection of the vanilla 26.3 appearance: the lower half is
solid and the upper half has four 4-by-3-pixel windows. The implementation
contains only this opening geometry, not texture data, and never reads client
assets during gameplay.

`Vec3`, `Ray`, `Plane`, and `RayPlaneIntersection` provide small,
server-safe value types for deterministic ray/plane math. A `Ray` is a finite
segment, and `Plane.intersect` returns no result for parallel or out-of-range
segments.

## Canonical oak-door coordinates

The oak-door transform is established on `feature/door-transform` and is
intended to be integrated before a door profile is introduced. `DoorTransform`
maps a vanilla oak-door `BlockState`, block position, and world-space point or
ray intersection into one logical two-block door surface.

Its convention is:

- Canonical front is the closed door face opposite `DoorBlock.FACING`.
- `u = 0` is the left edge and `u = 1` the right edge when viewed from that
  canonical front.
- `v = 0` is the lower edge of the lower block, `v = 0.5` the boundary
  between door halves, and `v = 1` the upper edge of the upper block.

Vanilla `DoorBlock` selects a horizontal shape direction from `FACING`,
`OPEN`, and `HINGE`: closed doors use `FACING`; open doors use clockwise for a
left hinge and counter-clockwise for a right hinge. `HALF` establishes which
block position contributes the lower or upper half of the continuous `v`
coordinate.

Vanilla represents a door half as a 3/16-block-thick slab. The transform uses
the slab's mid-plane deliberately. That creates one deterministic plane for
later center-ray projectile and vision logic instead of choosing a different
surface based on ray direction. `DoorLocalCoordinates` stores `u`/`v`, and
`DoorIntersection` preserves the segment parameter, world point, and mapped
coordinates.

## Verified vanilla call paths

The Minecraft 26.3 source was inspected before planning integration:

- `AbstractArrow.tick` performs the initial block trace through
  `Level.clipIncludingBorder` with `ClipContext.Block.COLLIDER`, then handles
  movement and subsequent entity/block hits.
- `LivingEntity.hasLineOfSight` performs `Level.clip` with
  `ClipContext.Block.COLLIDER` and considers a `MISS` visible.

These paths are recorded for future narrow hooks only; no gameplay Mixins are
implemented yet.

## Current limitations

- No registry, traversal, or gameplay integration exists yet.
- No manually defined oak-door opening mask exists yet.
- No manually defined oak-door opening mask exists yet.
- Door geometry currently uses a representative mid-plane, not full slab
  thickness or texture-derived detail.
- The door transform validates a single oak-door state; pairing/validating the
  neighboring door half is left to the later profile/traversal layer.
- No gameplay behavior has been tested in a running Minecraft instance.
