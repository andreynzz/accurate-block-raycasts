package io.github.accurateblockraycasts.geometry;

/**
 * A point on a logical two-block door surface. Values are intentionally not
 * clamped so callers can reject intersections outside the door bounds.
 */
public record DoorLocalCoordinates(double u, double v) {
}
