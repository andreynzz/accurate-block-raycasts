package io.github.accurateblockraycasts.geometry;

/** The point where a finite ray segment intersects a plane. */
public record RayPlaneIntersection(double parameter, Vec3 point) {
}
