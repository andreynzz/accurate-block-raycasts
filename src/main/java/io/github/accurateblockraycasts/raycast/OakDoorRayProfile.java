package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.DoorLocalCoordinates;
import io.github.accurateblockraycasts.geometry.DoorMask;
import io.github.accurateblockraycasts.geometry.DoorTransform;
import io.github.accurateblockraycasts.geometry.Ray;
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

        return DoorTransform.forOakDoor(state, position)
            .intersect(ray)
            .map(intersection -> sample(intersection.localCoordinates()))
            .orElse(RayProfileResult.NO_SPECIAL_RESULT);
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
