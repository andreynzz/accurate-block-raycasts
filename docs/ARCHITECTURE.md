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

Door and trapdoor masks are manually encoded server-side from a one-time
inspection of the vanilla 26.3 appearance. The implementation contains only
opening geometry, not texture data, and never reads client assets during
gameplay.

`DoorMask` presents those lower and upper `PixelMask` instances as one
continuous 16-by-32 surface, with rows indexed bottom-to-top. It retains the
immutable halves rather than duplicating their packed bits.

`DoorRayProfile` combines this surface with `DoorTransform`. It returns
`OPEN` or `SOLID` only when the finite ray crosses the door mid-plane within
the mask bounds. Exact outer edges and rays with no relevant plane
intersection return `NO_SPECIAL_RESULT`, conservatively preserving vanilla.

`RayProfileRegistry` resolves only manually verified profiles. Door support
includes oak, acacia, bamboo, cherry, jungle, poplar, iron, and all copper
oxidation/wax variants. Trapdoor support includes acacia, bamboo, cherry,
crimson, jungle, mangrove, oak, poplar, warped, iron, and all copper
oxidation/wax variants. Birch, dark oak, pale oak, and spruce trapdoors are
visually opaque and receive no profile, so they and every other unsupported
block remain on the vanilla path.

`PerforatedRaycaster` owns the shared retry loop. It calls the gameplay
adapter's vanilla trace, returns its result unchanged unless the resolved
profile reports `OPEN`, and then starts the next trace just beyond that
supported voxel. Thus one opening cannot discard a later vanilla block
collision.

For arrows, a server-side Mixin redirects only the `Level.clipIncludingBorder`
call in `AbstractArrow.tick`. `ArrowBlockRaycaster` feeds that call through the
shared traversal and returns the later vanilla `BlockHitResult` (or a vanilla
miss). `AbstractArrow.stepMoveAndHit` then retains its normal entity ordering,
damage, deflection, and block-impact behavior. The client retains vanilla
prediction until authoritative server updates arrive.

For common mob vision, a second server-side Mixin redirects the sole
`Level.clip` call in the four-argument `LivingEntity.hasLineOfSight` overload.
`LineOfSightRaycaster` reuses `PerforatedRaycaster` and retains the original
block, fluid, and collision-context settings for each retried trace. Thus the
normal `LivingEntity.hasLineOfSight(Entity)` path and its callers use the same
supported-profile geometry as arrows without changing unrelated level clipping.

`Vec3`, `Ray`, `Plane`, and `RayPlaneIntersection` provide small,
server-safe value types for deterministic ray/plane math. A `Ray` is a finite
segment, and `Plane.intersect` returns no result for parallel or out-of-range
segments.

## Canonical door coordinates

`DoorTransform` maps a vanilla `DoorBlock` state, block position, and
world-space point or ray intersection into one logical two-block door surface.

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

## Canonical trapdoor coordinates

`TrapdoorTransform` maps a vanilla `TrapDoorBlock` state, block position, and
world-space point or ray intersection into one 16-by-16 local surface. It
reads the real `FACING`, `OPEN`, and `HALF` state properties.

For a closed trapdoor, the canonical surface is horizontal: `u` follows world
X and `v` follows world Z. `HALF` selects the representative mid-plane of the
top or bottom 3/16-block slab. For an open trapdoor, the surface is vertical:
its normal faces opposite `FACING`, its width follows `FACING` clockwise, and
`v` follows world Y. The transform therefore stores one mask per block type,
not copies for horizontal facings or open/closed states.

`TrapdoorRayProfile` samples that transform with the same `PixelMask` type as
doors. It returns `OPEN` or `SOLID` only for an in-bounds plane crossing; a
parallel ray, an outer edge, or no relevant crossing conservatively delegates
to vanilla.

## Verified vanilla call paths

The Minecraft 26.3 source was inspected before planning integration:

- `AbstractArrow.tick` performs the initial block trace through
  `Level.clipIncludingBorder` with `ClipContext.Block.COLLIDER`, then handles
  movement and subsequent entity/block hits.
- `LivingEntity.hasLineOfSight` performs `Level.clip` with
  `ClipContext.Block.COLLIDER` and considers a `MISS` visible.

The corresponding narrow gameplay Mixins are implemented and covered by
server GameTests.

## Current limitations

- Door geometry currently uses a representative mid-plane, not full slab
  thickness or texture-derived detail.
- The door transform validates a single door state; pairing/validating the
  neighboring door half is left to the later profile/traversal layer.
- Trapdoor geometry also uses a representative slab mid-plane. Its masks are
  manually maintained static data; resource-pack/model-derived geometry is not
  supported.
