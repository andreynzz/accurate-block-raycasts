package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.Vec3;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

/**
 * Repeats vanilla block traces past explicitly open, supported block surfaces.
 *
 * <p>Only {@link RayProfileResult#OPEN} changes the normal trace result. A
 * solid, unsupported, or indeterminate hit is returned immediately, retaining
 * vanilla behavior. After an opening, the next trace begins just beyond the
 * hit voxel so the same door cannot be selected again.
 */
public final class PerforatedRaycaster {
    private static final double EXIT_EPSILON = 1.0E-6;

    public static final PerforatedRaycaster INSTANCE = new PerforatedRaycaster(RayProfileRegistry.INSTANCE);

    private final RayProfileRegistry profiles;

    PerforatedRaycaster(RayProfileRegistry profiles) {
        this.profiles = Objects.requireNonNull(profiles, "profiles");
    }

    /** Returns the first blocking vanilla hit after skipping supported openings, or {@code null} on a miss. */
    public @Nullable BlockRaycastHit trace(Ray ray, VanillaBlockTracer vanillaTracer) {
        Objects.requireNonNull(ray, "ray");
        Objects.requireNonNull(vanillaTracer, "vanillaTracer");

        Ray remainingRay = ray;
        while (true) {
            BlockRaycastHit hit = vanillaTracer.trace(remainingRay);
            if (hit == null) {
                return null;
            }

            RayProfile profile = profiles.resolve(hit.state());
            if (profile == null || profile.evaluate(hit.state(), hit.position(), remainingRay) != RayProfileResult.OPEN) {
                return hit;
            }

            Ray nextRay = advancePastBlock(remainingRay, hit.position());
            if (nextRay == null) {
                // A malformed tracer result must not permit an unexpected pass-through.
                return hit;
            }
            remainingRay = nextRay;
        }
    }

    private static @Nullable Ray advancePastBlock(Ray ray, BlockPos position) {
        Vec3 start = ray.start();
        Vec3 direction = ray.direction();
        double exitParameter = Math.min(
            exitParameter(start.x(), direction.x(), position.getX()),
            Math.min(
                exitParameter(start.y(), direction.y(), position.getY()),
                exitParameter(start.z(), direction.z(), position.getZ())
            )
        );
        if (Double.isInfinite(exitParameter) || exitParameter <= 0.0) {
            return null;
        }
        if (exitParameter >= 1.0) {
            return new Ray(ray.end(), ray.end());
        }

        double length = Math.sqrt(direction.dot(direction));
        if (length == 0.0) {
            return null;
        }
        return new Ray(ray.pointAt(Math.min(1.0, exitParameter + EXIT_EPSILON / length)), ray.end());
    }

    private static double exitParameter(double coordinate, double delta, int blockCoordinate) {
        if (delta > 0.0) {
            return (blockCoordinate + 1.0 - coordinate) / delta;
        }
        if (delta < 0.0) {
            return (blockCoordinate - coordinate) / delta;
        }
        return Double.POSITIVE_INFINITY;
    }
}
