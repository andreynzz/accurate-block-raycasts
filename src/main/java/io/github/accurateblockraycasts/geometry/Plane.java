package io.github.accurateblockraycasts.geometry;

import java.util.Objects;
import java.util.Optional;

/** An infinite plane represented by a point and a non-zero normal vector. */
public record Plane(Vec3 point, Vec3 normal) {
    private static final double PARALLEL_EPSILON = 1.0E-10;

    public Plane {
        Objects.requireNonNull(point, "point");
        Objects.requireNonNull(normal, "normal");
        if (normal.dot(normal) == 0.0) {
            throw new IllegalArgumentException("normal must not be zero");
        }
    }

    /** Returns the intersection of this plane and the finite segment, if one exists. */
    public Optional<RayPlaneIntersection> intersect(Ray ray) {
        Objects.requireNonNull(ray, "ray");
        var direction = ray.direction();
        double denominator = normal.dot(direction);
        if (Math.abs(denominator) < PARALLEL_EPSILON) {
            return Optional.empty();
        }

        double parameter = normal.dot(point.subtract(ray.start())) / denominator;
        if (parameter < 0.0 || parameter > 1.0) {
            return Optional.empty();
        }
        return Optional.of(new RayPlaneIntersection(parameter, ray.pointAt(parameter)));
    }
}
