package io.github.accurateblockraycasts.geometry;

/** Immutable three-dimensional vector used by server-safe ray geometry. */
public record Vec3(double x, double y, double z) {
    public Vec3 subtract(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 scale(double factor) {
        return new Vec3(x * factor, y * factor, z * factor);
    }

    public double dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }
}
