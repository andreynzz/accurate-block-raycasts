package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.PixelMask;
import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.TrapdoorLocalCoordinates;
import io.github.accurateblockraycasts.geometry.TrapdoorTransform;
import io.github.accurateblockraycasts.geometry.Vec3;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Shared planar sampling for one trapdoor block and its verified mask. */
final class TrapdoorRayProfile implements RayProfile {
    private final Block supportedBlock;
    private final PixelMask mask;

    TrapdoorRayProfile(Block supportedBlock, PixelMask mask) {
        this.supportedBlock = Objects.requireNonNull(supportedBlock, "supportedBlock");
        this.mask = Objects.requireNonNull(mask, "mask");
    }

    @Override
    public RayProfileResult evaluate(BlockState state, BlockPos position, Ray ray) {
        if (!state.is(supportedBlock)) {
            return RayProfileResult.NO_SPECIAL_RESULT;
        }
        TrapdoorTransform transform = TrapdoorTransform.forTrapdoor(state, position);
        return transform.intersect(ray)
            .map(this::sample)
            .orElseGet(() -> isPassableAt(transform, ray.start()) ? RayProfileResult.OPEN : RayProfileResult.NO_SPECIAL_RESULT);
    }

    @Override
    public boolean isPassableAt(BlockState state, BlockPos position, Vec3 worldPoint) {
        return state.is(supportedBlock) && isPassableAt(TrapdoorTransform.forTrapdoor(state, position), worldPoint);
    }

    private boolean isPassableAt(TrapdoorTransform transform, Vec3 worldPoint) {
        return sample(transform.toLocal(worldPoint)) == RayProfileResult.OPEN;
    }

    private RayProfileResult sample(TrapdoorLocalCoordinates coordinates) {
        if (coordinates.u() < 0.0 || coordinates.u() >= 1.0 || coordinates.v() < 0.0 || coordinates.v() >= 1.0) {
            return RayProfileResult.NO_SPECIAL_RESULT;
        }
        return mask.isSolid((int) (coordinates.u() * PixelMask.SIZE), (int) (coordinates.v() * PixelMask.SIZE))
            ? RayProfileResult.SOLID : RayProfileResult.OPEN;
    }
}
