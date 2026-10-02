package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.DoorLocalCoordinates;
import io.github.accurateblockraycasts.geometry.DoorMask;
import io.github.accurateblockraycasts.geometry.DoorTransform;
import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.Vec3;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Shared planar-door sampling for one block and its manually verified mask. */
final class DoorRayProfile implements RayProfile {
    private final Block supportedBlock;
    private final DoorMask mask;

    DoorRayProfile(Block supportedBlock, DoorMask mask) {
        this.supportedBlock = Objects.requireNonNull(supportedBlock, "supportedBlock");
        this.mask = Objects.requireNonNull(mask, "mask");
    }

    @Override
    public RayProfileResult evaluate(BlockState state, BlockPos position, Ray ray) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(ray, "ray");
        if (!state.is(supportedBlock)) {
            return RayProfileResult.NO_SPECIAL_RESULT;
        }

        DoorTransform transform = DoorTransform.forDoor(state, position);
        return transform.intersect(ray)
            .map(intersection -> sample(intersection.localCoordinates()))
            .orElseGet(() -> isPassableAt(transform, ray.start()) ? RayProfileResult.OPEN : RayProfileResult.NO_SPECIAL_RESULT);
    }

    @Override
    public boolean isPassableAt(BlockState state, BlockPos position, Vec3 worldPoint) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(worldPoint, "worldPoint");
        return state.is(supportedBlock) && isPassableAt(DoorTransform.forDoor(state, position), worldPoint);
    }

    private boolean isPassableAt(DoorTransform transform, Vec3 worldPoint) {
        return sample(transform.toLocal(worldPoint)) == RayProfileResult.OPEN;
    }

    private RayProfileResult sample(DoorLocalCoordinates coordinates) {
        double u = coordinates.u();
        double v = coordinates.v();
        if (u < 0.0 || u >= 1.0 || v < 0.0 || v >= 1.0) {
            return RayProfileResult.NO_SPECIAL_RESULT;
        }

        int column = (int) (u * DoorMask.WIDTH);
        int row = (int) (v * DoorMask.HEIGHT);
        return mask.isSolid(column, row) ? RayProfileResult.SOLID : RayProfileResult.OPEN;
    }
}
