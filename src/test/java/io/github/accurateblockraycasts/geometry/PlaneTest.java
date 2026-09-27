package io.github.accurateblockraycasts.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlaneTest {
    private static final Plane DOOR_PLANE = new Plane(new Vec3(0, 0, 0), new Vec3(0, 0, 1));

    @Test
    void findsIntersectionWithinSegment() {
        var intersection = DOOR_PLANE.intersect(new Ray(new Vec3(0.25, 0.5, -1), new Vec3(0.25, 0.5, 1)));

        assertTrue(intersection.isPresent());
        assertEquals(0.5, intersection.orElseThrow().parameter());
        assertEquals(new Vec3(0.25, 0.5, 0), intersection.orElseThrow().point());
    }

    @Test
    void rejectsParallelAndOutOfRangeRays() {
        assertFalse(DOOR_PLANE.intersect(new Ray(new Vec3(0, 0, 1), new Vec3(1, 0, 1))).isPresent());
        assertFalse(DOOR_PLANE.intersect(new Ray(new Vec3(0, 0, 1), new Vec3(0, 0, 2))).isPresent());
    }

    @Test
    void rejectsZeroNormal() {
        assertThrows(IllegalArgumentException.class, () -> new Plane(new Vec3(0, 0, 0), new Vec3(0, 0, 0)));
    }
}
