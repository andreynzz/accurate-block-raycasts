package io.github.accurateblockraycasts.geometry;

/** A ray intersection with a door's representative mid-plane. */
public record DoorIntersection(double parameter, Vec3 worldPoint, DoorLocalCoordinates localCoordinates) {
}
