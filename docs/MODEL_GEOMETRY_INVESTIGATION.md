# Model-derived geometry investigation

## Finding

Minecraft 26.3 keeps block-model loading and baking on the client. Inspection
of the Loom-provided Minecraft artifacts found `BlockModel`, `ModelBakery`,
`ModelManager`, and baked-model classes in `minecraft-client.jar`; none of
those classes are present in `minecraft-server.jar`.

The logical server therefore cannot read the active client resource pack or
use baked-model geometry to decide projectile collision or mob visibility.
Doing so would make authoritative gameplay depend on client-only state and
would violate the mod's server-authority requirement.

## Decision

Automatic runtime derivation from resource packs is deferred. The supported
authoritative inputs remain:

- built-in, manually verified profiles;
- server datapack JSON profiles; and
- profiles registered by another mod through `RayProfileRegistry`.

## Safe future direction

An offline generator may inspect a known resource pack or model set during
development and emit the existing server datapack profile format. Servers can
then distribute that generated data as normal server-controlled content. A
client-only model inspection tool may also visualize or validate a proposed
mask, but it must never be consulted by gameplay raycasts.
