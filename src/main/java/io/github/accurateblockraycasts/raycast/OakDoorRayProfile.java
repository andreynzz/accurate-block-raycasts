package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.DoorLocalCoordinates;
import io.github.accurateblockraycasts.geometry.DoorMask;
import io.github.accurateblockraycasts.geometry.DoorTransform;
import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.Vec3;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Shared server-side ray geometry for {@code minecraft:oak_door}. */
public final class OakDoorRayProfile implements RayProfile {
    public static final OakDoorRayProfile INSTANCE = new OakDoorRayProfile();

    private OakDoorRayProfile() {
    }

    @Override
    public RayProfileResult evaluate(BlockState state, BlockPos position, Ray ray) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(ray, "ray");
        if (!state.is(Blocks.OAK_DOOR)) {
            return RayProfileResult.NO_SPECIAL_RESULT;
        }

        DoorTransform transform = DoorTransform.forOakDoor(state, position);
        return transform.intersect(ray)
            .map(intersection -> sample(intersection.localCoordinates()))
            .orElseGet(() -> isPassableAt(transform, ray.start()) ? RayProfileResult.OPEN : RayProfileResult.NO_SPECIAL_RESULT);
    }

    /** Returns whether a point projected onto this door's canonical surface is an opening. */
    public boolean isPassableAt(BlockState state, BlockPos position, Vec3 worldPoint) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(worldPoint, "worldPoint");
        return state.is(Blocks.OAK_DOOR) && isPassableAt(DoorTransform.forOakDoor(state, position), worldPoint);
    }

    private static boolean isPassableAt(DoorTransform transform, Vec3 worldPoint) {
        return sample(transform.toLocal(worldPoint)) == RayProfileResult.OPEN;
    }

    private static RayProfileResult sample(DoorLocalCoordinates coordinates) {
        double u = coordinates.u();
        double v = coordinates.v();
        if (u < 0.0 || u >= 1.0 || v < 0.0 || v >= 1.0) {
            // Door edges are not pixels. Preserve vanilla behavior there.
            return RayProfileResult.NO_SPECIAL_RESULT;
        }

        int column = (int) (u * DoorMask.WIDTH);
        int row = (int) (v * DoorMask.HEIGHT);
        return OakDoorMasks.fullDoor().isSolid(column, row)
            ? RayProfileResult.SOLID
            : RayProfileResult.OPEN;
    }
}
