package io.github.accurateblockraycasts.geometry;

import java.util.Objects;

/** A finite ray segment from {@code start} to {@code end}. */
public record Ray(Vec3 start, Vec3 end) {
    public Ray {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
    }

    public Vec3 direction() {
        return end.subtract(start);
    }

    public Vec3 pointAt(double parameter) {
        return start.add(direction().scale(parameter));
    }
}
