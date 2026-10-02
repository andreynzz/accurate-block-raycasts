package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Server-safe geometry for one explicitly supported block type.
 *
 * <p>Profiles decide only whether their own geometry changes the result for a
 * finite ray segment. They do not perform world traversal or alter vanilla
 * behavior for unsupported blocks; those responsibilities belong to the
 * registry and gameplay integration layers.
 */
@FunctionalInterface
public interface RayProfile {
    /** Evaluates this profile for a block state at one world position. */
    RayProfileResult evaluate(BlockState state, BlockPos position, Ray ray);

    /**
     * Returns whether a point already inside this block occupies a passable
     * part of the profile. Non-planar or unsupported profiles preserve vanilla
     * behavior by returning {@code false}.
     */
    default boolean isPassableAt(BlockState state, BlockPos position, Vec3 worldPoint) {
        return false;
    }
}
