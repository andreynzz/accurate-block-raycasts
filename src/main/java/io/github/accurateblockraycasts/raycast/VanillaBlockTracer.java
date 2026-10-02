package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.Ray;
import org.jspecify.annotations.Nullable;

/**
 * Adapter for one vanilla block-trace attempt.
 *
 * <p>Gameplay integrations will adapt the relevant vanilla clip call to this
 * interface. Returning {@code null} represents a vanilla miss.
 */
@FunctionalInterface
public interface VanillaBlockTracer {
    @Nullable BlockRaycastHit trace(Ray ray);
}
